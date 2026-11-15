package cn.iocoder.yudao.module.fish.service.smartentry;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.ZkPondPageReqVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmartEntryService {
    private final JdbcTemplate jdbc;
    private final ZkPondService pondService;
    private static final int MAX_FILE = 5 * 1024 * 1024;
    private static final int MAX_TOTAL = 20 * 1024 * 1024;
    private static final Set<String> STOP_WORDS = Set.of("查询", "查看", "一下", "帮我", "请问", "什么", "怎么", "如何", "哪些", "相关", "资料", "数据", "记录", "录入", "最新", "最近", "内容", "数据库", "里面", "里的", "一下子", "分析", "总结");

    public SmartEntryService(DataSource source, ZkPondService pondService) {
        this.jdbc = new JdbcTemplate(source); this.pondService = pondService;
    }

    private long tenant() {
        var user = SecurityFrameworkUtils.getLoginUser();
        Long tenantId = TenantContextHolder.getTenantId();
        if (user == null || user.getId() == null) throw new ServiceException(401, "请先登录");
        if (tenantId == null || !Objects.equals(user.getTenantId(), tenantId)) {
            throw new ServiceException(403, "不能访问其他组织的录入资料");
        }
        return tenantId;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> create(String title, String content, String requestId, List<MultipartFile> files) throws IOException {
        long tenantId = tenant();
        title = title == null ? "" : title.strip();
        content = content == null ? "" : content.strip();
        if (title.isEmpty() || title.length() > 160) throw new ServiceException(400, "标题需为 1 至 160 个字符");
        if (content.length() > 20000) throw new ServiceException(400, "文字最多 20000 个字符");
        try { UUID.fromString(requestId); } catch (Exception e) { throw new ServiceException(400, "录入标识无效，请重新打开录入窗口"); }
        List<MultipartFile> uploads = files == null ? List.of() : files;
        if (uploads.size() > 6) throw new ServiceException(400, "一次最多上传 6 张图片");
        if (content.isEmpty() && uploads.isEmpty()) throw new ServiceException(400, "请填写文字或选择图片");
        List<ImageUpload> images = new ArrayList<>();
        long total = 0;
        for (MultipartFile file : uploads) {
            total += file.getSize();
            if (file.isEmpty() || file.getSize() > MAX_FILE || total > MAX_TOTAL) {
                throw new ServiceException(400, "单张图片需小于 5 MB，合计不超过 20 MB");
            }
            byte[] bytes = file.getBytes();
            String mime = detectImageType(bytes);
            String name = Optional.ofNullable(file.getOriginalFilename()).orElse("image").replace('\\', '/');
            name = name.substring(name.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "_");
            if (name.isBlank()) name = "image";
            if (name.length() > 255) name = name.substring(name.length() - 255);
            images.add(new ImageUpload(name, mime, bytes));
        }
        List<Long> previous = jdbc.queryForList("SELECT id FROM zk_smart_entry WHERE tenant_id=? AND request_id=?", Long.class, tenantId, requestId);
        if (!previous.isEmpty()) return detail(previous.get(0));
        var key = new GeneratedKeyHolder();
        final String finalTitle = title;
        final String finalContent = content;
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO zk_smart_entry (tenant_id,request_id,title,content,creator) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, tenantId); ps.setString(2, requestId); ps.setString(3, finalTitle);
                ps.setString(4, finalContent); ps.setLong(5, SecurityFrameworkUtils.getLoginUserId());
                return ps;
            }, key);
        } catch (DuplicateKeyException e) {
            return detail(jdbc.queryForObject("SELECT id FROM zk_smart_entry WHERE tenant_id=? AND request_id=?", Long.class, tenantId, requestId));
        }
        long entryId = Objects.requireNonNull(key.getKey()).longValue();
        for (ImageUpload image : images) {
            jdbc.update("INSERT INTO zk_smart_entry_image (entry_id,tenant_id,file_name,mime_type,byte_size,sha256,image_data) VALUES (?,?,?,?,?,?,?)",
                    entryId, tenantId, image.name(), image.mime(), image.bytes().length, sha256(image.bytes()), image.bytes());
        }
        return detail(entryId);
    }

    public Map<String, Object> list(int pageNo, int pageSize) {
        long tenantId = tenant();
        int size = Math.max(1, Math.min(20, pageSize));
        int page = Math.max(1, Math.min(100000, pageNo));
        List<Map<String, Object>> rows = jdbc.query("SELECT id,title,content,creator,create_time FROM zk_smart_entry WHERE tenant_id=? ORDER BY id DESC LIMIT ? OFFSET ?", this::entryRow, tenantId, size, (page - 1) * size);
        addImages(rows, tenantId);
        return Map.of("list", rows, "total", Objects.requireNonNull(jdbc.queryForObject("SELECT COUNT(*) FROM zk_smart_entry WHERE tenant_id=?", Long.class, tenantId)));
    }

    public Map<String, Object> detail(long id) {
        long tenantId = tenant();
        List<Map<String, Object>> rows = jdbc.query("SELECT id,title,content,creator,create_time FROM zk_smart_entry WHERE tenant_id=? AND id=?", this::entryRow, tenantId, id);
        if (rows.isEmpty()) throw new ServiceException(404, "记录不存在或无权访问");
        addImages(rows, tenantId);
        return rows.get(0);
    }

    public ImageUpload image(long id) {
        long tenantId = tenant();
        List<ImageUpload> images = jdbc.query("SELECT file_name,mime_type,image_data FROM zk_smart_entry_image WHERE tenant_id=? AND id=?",
                (rs, row) -> new ImageUpload(rs.getString(1), rs.getString(2), rs.getBytes(3)), tenantId, id);
        if (images.isEmpty()) throw new ServiceException(404, "图片不存在或无权访问");
        return images.get(0);
    }

    public Map<String, Object> search(String question) {
        long tenantId = tenant();
        String q = question == null ? "" : question.strip();
        if (q.isEmpty() || q.length() > 500) throw new ServiceException(400, "查询问题需为 1 至 500 个字符");
        List<String> terms = searchTerms(q);
        boolean browseRecent = q.replaceAll("[\\s，。！？!?]", "").matches("^(?:请|帮我)?(?:查|查询|查看|列出)?(?:最近|最新|全部|所有)(?:的)?(?:录入)?(?:的)?(?:资料|记录|内容|数据)$");
        if (terms.isEmpty() && !browseRecent) {
            return Map.of("query", q, "hits", List.of(), "notice", "请提供具体关键词；如需浏览资料，可查询“最近录入的资料”。", "searchedAt", java.time.OffsetDateTime.now().toString());
        }
        List<Object> args = new ArrayList<>(); args.add(tenantId);
        String filter = browseRecent ? "" : likeFilter("CONCAT(title,' ',content)", terms, args);
        List<Map<String, Object>> entries = jdbc.query("SELECT id,title,LEFT(content,5000) AS content,creator,create_time FROM zk_smart_entry WHERE tenant_id=?" + filter + " ORDER BY id DESC LIMIT 8", this::entryRow, args.toArray());
        addImages(entries, tenantId);
        List<Map<String, Object>> hits = new ArrayList<>(entries);
        // Existing business knowledge is also read from MySQL, scoped to this tenant.
        List<Object> knowledgeArgs = new ArrayList<>(); knowledgeArgs.add(tenantId);
        String knowledgeFilter = likeFilter("CONCAT(COALESCE(node_title,''),' ',COALESCE(node_describe,''),' ',COALESCE(node_tag,''))", terms, knowledgeArgs);
        if (!terms.isEmpty() && !browseRecent) {
            hits.addAll(jdbc.query("SELECT id,node_title,LEFT(node_describe,4000) AS node_describe,node_tag,create_time FROM zk_konwledge_tree WHERE tenant_id=? AND deleted=0" + knowledgeFilter + " ORDER BY id DESC LIMIT 4", (rs, row) -> {
                Map<String, Object> hit = new LinkedHashMap<>();
                hit.put("id", "knowledge-" + rs.getLong("id")); hit.put("title", rs.getString("node_title"));
                hit.put("content", Objects.toString(rs.getString("node_describe"), "") + "\n标签：" + Objects.toString(rs.getString("node_tag"), ""));
                hit.put("source", "知识库 · zk_konwledge_tree"); hit.put("kind", "knowledge"); hit.put("images", List.of());
                return hit;
            }, knowledgeArgs.toArray()));
        }
        if (Pattern.compile("鱼池|养殖池|网箱|pond", Pattern.CASE_INSENSITIVE).matcher(q).find()) {
            // Reuse the existing department permission chain, including the filtered total.
            var page = new ZkPondPageReqVO(); page.setPageNo(1); page.setPageSize(10);
            var ponds = pondService.getZkPondPage(page);
            StringBuilder body = new StringBuilder("当前可见鱼池总数：" + ponds.getTotal() + "。以下列出当前可见的前 10 条鱼池记录：\n");
            for (var pond : ponds.getList()) body.append(pond.getPondName()).append("；车间：").append(pond.getWorkshopName()).append("；系统：").append(pond.getPondSystemName()).append("；鱼数量：").append(pond.getFishNum()).append("；总重量：").append(pond.getTotalWeight()).append("\n");
            hits.add(Map.of("id", "pond-summary", "title", "鱼池数据概览", "content", body.toString(), "source", "业务数据库 · zk_pond", "kind", "business", "images", List.of()));
        }
        return Map.of("query", q, "hits", hits, "entryTotal", Objects.requireNonNull(jdbc.queryForObject("SELECT COUNT(*) FROM zk_smart_entry WHERE tenant_id=?", Long.class, tenantId)), "searchedAt", java.time.OffsetDateTime.now().toString());
    }

    private Map<String, Object> entryRow(ResultSet rs, int row) throws SQLException {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", rs.getLong("id")); item.put("title", rs.getString("title")); item.put("content", rs.getString("content"));
        item.put("creator", rs.getLong("creator")); item.put("createdAt", rs.getTimestamp("create_time").toLocalDateTime().toString());
        item.put("source", "智能录入 · zk_smart_entry"); item.put("kind", "entry");
        return item;
    }

    private void addImages(List<Map<String, Object>> rows, long tenantId) {
        for (var entry : rows) {
            var images = jdbc.query("SELECT id,file_name,mime_type,byte_size,sha256 FROM zk_smart_entry_image WHERE tenant_id=? AND entry_id=? ORDER BY id", (rs, row) -> Map.of(
                    "id", rs.getLong("id"), "name", rs.getString("file_name"), "mime", rs.getString("mime_type"), "size", rs.getInt("byte_size"), "sha256", rs.getString("sha256")), tenantId, entry.get("id"));
            entry.put("images", images);
        }
    }

    static List<String> searchTerms(String q) {
        LinkedHashSet<String> terms = new LinkedHashSet<>();
        for (String stop : STOP_WORDS.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList()) q = q.replace(stop, " ");
        Matcher matcher = Pattern.compile("[\\p{IsHan}]+|[a-zA-Z0-9_-]{2,}").matcher(q);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            if (STOP_WORDS.contains(word)) continue;
            if (word.matches("[\\p{IsHan}]{3,}")) {
                if (word.length() <= 24) terms.add(word);
                for (int i = 0; i < word.length() - 1; i++) {
                    String pair = word.substring(i, i + 2);
                    if (!STOP_WORDS.contains(pair)) terms.add(pair);
                }
            } else if (word.length() > 1 || word.matches("[\\p{IsHan}]")) terms.add(word);
        }
        return terms.stream().limit(16).toList();
    }

    private String likeFilter(String expression, List<String> terms, List<Object> args) {
        if (terms.isEmpty()) return "";
        List<String> clauses = new ArrayList<>();
        for (String term : terms) {
            clauses.add(expression + " LIKE ? ESCAPE '!'");
            args.add("%" + term.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%");
        }
        return " AND (" + String.join(" OR ", clauses) + ")";
    }

    static String detectImageType(byte[] b) {
        if (b.length >= 24 && b[0] == (byte) 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G' && b[4] == 13 && b[5] == 10 && b[6] == 26 && b[7] == 10) return "image/png";
        if (b.length >= 4 && b[0] == (byte) 0xff && b[1] == (byte) 0xd8 && b[2] == (byte) 0xff) return "image/jpeg";
        if (b.length >= 13 && (new String(b, 0, 6, StandardCharsets.US_ASCII).equals("GIF87a") || new String(b, 0, 6, StandardCharsets.US_ASCII).equals("GIF89a"))) return "image/gif";
        if (b.length >= 16 && new String(b, 0, 4, StandardCharsets.US_ASCII).equals("RIFF") && new String(b, 8, 4, StandardCharsets.US_ASCII).equals("WEBP")) return "image/webp";
        throw new ServiceException(400, "请上传 PNG、JPEG、GIF 或 WebP 图片，不支持其他文件类型");
    }

    private String sha256(byte[] bytes) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    public record ImageUpload(String name, String mime, byte[] bytes) {}
}
