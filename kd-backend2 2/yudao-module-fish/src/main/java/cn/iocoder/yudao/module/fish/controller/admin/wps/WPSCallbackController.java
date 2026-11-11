package cn.iocoder.yudao.module.fish.controller.admin.wps;

import cn.iocoder.yudao.module.fish.controller.admin.wps.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdocument.ZkDocumentDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdocument.ZkDocumentMapper;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.DOCUMENT_NOT_EXISTS;

/**
 * WPS WebOffice SDK 回调接口 Controller
 * 
 * 所有接口路径必须以 /v3/3rd/ 开头，这是WPS SDK的固定要求
 * 
 * 注意：
 * 1. 这些接口会被WPS服务器调用，需要允许跨域访问
 * 2. WPS服务器调用时可能没有认证token，需要使用@PermitAll
 * 3. 接口必须直接返回JSON对象，不使用CommonResult包装
 * 
 * @author 系统管理员
 */
@Tag(name = "WPS回调接口")
@RestController
@RequestMapping("/v3/3rd")
@Slf4j
@PermitAll // 允许WPS服务器调用，无需认证
@CrossOrigin(origins = "*", maxAge = 3600) // 允许跨域访问
public class WPSCallbackController {

  @Resource
  private ZkDocumentMapper documentMapper;

  @Resource
  private AdminUserApi adminUserApi;

  @Resource
  private FileApi fileApi;

  /**
   * 1. 获取文件信息
   * GET /v3/3rd/files/{file_id}
   * 
   * 遵循「文件ID一致性」原则：接口中文件「请求ID」和「返回ID」需保持一致
   * 
   * WPS官方文档示例格式：
   * {
   * "code": 0,
   * "data": {
   * "id": "9",
   * "name": "统计月报.xlsx",
   * "version": 1,
   * "size": 18961,
   * "create_time": 1670218748,
   * "modify_time": 1670328304,
   * "creator_id": "404",
   * "modifier_id": "404"
   * }
   * }
   * 
   * 注意：WPS SDK期望返回 {"code": 0, "data": {...}} 格式
   */
  @GetMapping("/files/{file_id}")
  @Operation(summary = "获取文件信息")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> getFileInfo(
      @Parameter(description = "文档ID", required = true, example = "1") @PathVariable("file_id") String fileId) {
    log.info("WPS回调 - 获取文件信息: fileId={}", fileId);

    // 验证fileId不为空
    if (fileId == null || fileId.trim().isEmpty()) {
      log.error("fileId为空或null: fileId={}", fileId);
      throw new RuntimeException("fileId不能为空");
    }

    // 查询文档
    ZkDocumentDO document = documentMapper.selectById(Long.parseLong(fileId));
    if (document == null) {
      log.error("文档不存在: fileId={}", fileId);
      throw exception(DOCUMENT_NOT_EXISTS);
    }

    // 确保id字段不为空（必须与传入的file_id一致）
    String idValue = fileId.trim();
    if (idValue.isEmpty()) {
      log.error("fileId处理后为空: 原始fileId={}", fileId);
      throw new RuntimeException("fileId不能为空");
    }

    // 构建文件名（WPS要求name必须有后缀名）
    String fileName = document.getTitle() != null && !document.getTitle().trim().isEmpty()
        ? document.getTitle()
        : "未命名文档";

    // 确保文件名包含扩展名
    if (!fileName.contains(".")) {
      // 从文件URL中提取扩展名
      String fileUrl = document.getFileUrl();
      if (fileUrl != null && fileUrl.contains(".")) {
        // 提取扩展名（包括点号）
        int lastDotIndex = fileUrl.lastIndexOf(".");
        int lastSlashIndex = Math.max(fileUrl.lastIndexOf("/"), fileUrl.lastIndexOf("\\"));
        if (lastDotIndex > lastSlashIndex) {
          String extension = fileUrl.substring(lastDotIndex);
          // 移除可能的查询参数
          if (extension.contains("?")) {
            extension = extension.substring(0, extension.indexOf("?"));
          }
          if (extension.contains("#")) {
            extension = extension.substring(0, extension.indexOf("#"));
          }
          fileName = fileName + extension;
          log.info("从文件URL提取扩展名: fileUrl={}, extension={}, fileName={}", fileUrl, extension, fileName);
        }
      }

      // 如果还是没有扩展名，根据officeType推断（但这需要知道文件类型）
      // 这里先尝试从URL路径判断
      if (!fileName.contains(".") && fileUrl != null) {
        String lowerUrl = fileUrl.toLowerCase();
        if (lowerUrl.contains(".doc") || lowerUrl.contains(".docx")) {
          fileName = fileName + ".docx";
        } else if (lowerUrl.contains(".xls") || lowerUrl.contains(".xlsx")) {
          fileName = fileName + ".xlsx";
        } else if (lowerUrl.contains(".ppt") || lowerUrl.contains(".pptx")) {
          fileName = fileName + ".pptx";
        } else {
          // 默认使用.docx
          fileName = fileName + ".docx";
          log.warn("无法确定文件类型，使用默认扩展名.docx: fileId={}, fileUrl={}", fileId, fileUrl);
        }
      }
    }

    // 构建响应数据对象
    Map<String, Object> data = new HashMap<>();
    data.put("id", idValue); // 必须与传入的file_id一致，且不能为空
    data.put("name", fileName); // name必须有后缀名
    data.put("version", 1); // TODO: 实现版本管理，每次保存后递增
    data.put("size", document.getFileSize() != null ? document.getFileSize() : 0L);
    data.put("create_time",
        document.getCreateTime() != null ? document.getCreateTime().toEpochSecond(java.time.ZoneOffset.of("+8"))
            : Instant.now().getEpochSecond());
    data.put("modify_time",
        document.getUpdateTime() != null ? document.getUpdateTime().toEpochSecond(java.time.ZoneOffset.of("+8"))
            : Instant.now().getEpochSecond());
    data.put("creator_id", document.getAuthorId() != null ? document.getAuthorId().toString() : "0");
    data.put("modifier_id", document.getAuthorId() != null ? document.getAuthorId().toString() : "0");

    // 按照WPS官方文档格式返回：{"code": 0, "data": {...}}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", data);

    log.info("WPS回调 - 返回文件信息: id={}, name={}, size={}, response={}", idValue, data.get("name"), data.get("size"),
        result);
    return result;
  }

  /**
   * 2. 获取文件下载地址
   * GET /v3/3rd/files/{file_id}/download
   * 
   * 根据WPS官方文档，此接口必须返回url字段（文档下载地址）
   * 文档下载地址需确保外网可访问且排除访问时防火墙的限制
   * 
   * WPS官方文档示例格式：
   * {
   * "code": 0,
   * "data": {
   * "url": "https://foo.bar.com/files/9/180"
   * }
   * }
   * 
   * 注意：
   * 1. WPS SDK期望返回 {"code": 0, "data": {...}} 格式
   * 2. url字段是必须的
   * 3. 如果下载地址设置了Referer防盗链，需要在headers中返回自定义Referer请求头
   */
  @GetMapping("/files/{file_id}/download")
  @Operation(summary = "获取文件下载地址")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> getFileDownloadUrl(
      @Parameter(description = "文档ID", required = true, example = "1") @PathVariable("file_id") String fileId) {
    log.info("WPS回调 - 获取文件下载地址: fileId={}", fileId);

    // 查询文档
    ZkDocumentDO document = documentMapper.selectById(Long.parseLong(fileId));
    if (document == null) {
      log.error("文档不存在: fileId={}", fileId);
      throw exception(DOCUMENT_NOT_EXISTS);
    }

    // 获取文件下载URL
    String fileUrl = document.getFileUrl();
    if (fileUrl == null || fileUrl.trim().isEmpty()) {
      log.error("文档文件URL为空: fileId={}", fileId);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("code", 400);
      Map<String, Object> errorData = new HashMap<>();
      errorData.put("error", "文档文件URL不能为空");
      errorResult.put("data", errorData);
      return errorResult;
    }

    log.info("原始文件URL: fileId={}, fileUrl={}", fileId, fileUrl);

    // 生成文件下载URL
    // 如果文件存储在OSS等需要签名的存储中，需要生成预签名URL
    // 预签名URL有效期设置为1小时（3600秒），WPS SDK会立即下载文件
    String downloadUrl;
    try {
      // 尝试生成预签名URL（如果文件存储支持）
      downloadUrl = fileApi.presignGetUrl(fileUrl, 3600);
      log.info("✅ 生成预签名下载URL成功: fileId={}, originalUrl={}, presignedUrl={}",
          fileId, fileUrl, downloadUrl);

      // 验证URL格式（必须是http://或https://开头）
      if (!downloadUrl.startsWith("http://") && !downloadUrl.startsWith("https://")) {
        log.error("❌ 预签名URL格式不正确，不是有效的HTTP(S) URL: fileId={}, url={}", fileId, downloadUrl);
        // 如果预签名URL格式不正确，尝试使用原始URL
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
          downloadUrl = fileUrl;
          log.warn("使用原始URL: fileId={}, url={}", fileId, downloadUrl);
        } else {
          throw new RuntimeException("文件URL格式不正确，必须是公网可访问的HTTP(S) URL");
        }
      }
    } catch (Exception e) {
      // 如果生成预签名URL失败，检查原始URL是否可用
      log.warn("⚠️ 生成预签名URL失败: fileId={}, fileUrl={}, error={}",
          fileId, fileUrl, e.getMessage());

      // 检查原始URL是否是公网可访问的HTTP(S) URL
      if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
        downloadUrl = fileUrl;
        log.info("使用原始URL: fileId={}, url={}", fileId, downloadUrl);
      } else {
        log.error("❌ 文件URL不是公网可访问的HTTP(S) URL: fileId={}, fileUrl={}", fileId, fileUrl);
        log.error("⚠️ WPS服务器无法访问内网或相对路径的文件URL，请确保文件URL是公网可访问的");
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("code", 400);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("error", "文件URL必须是公网可访问的HTTP(S) URL");
        errorResult.put("data", errorData);
        return errorResult;
      }
    }

    // 最终验证：确保URL是公网可访问的
    if (!downloadUrl.startsWith("http://") && !downloadUrl.startsWith("https://")) {
      log.error("❌ 最终文件URL格式不正确: fileId={}, url={}", fileId, downloadUrl);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("code", 400);
      Map<String, Object> errorData = new HashMap<>();
      errorData.put("error", "文件URL格式不正确，必须是公网可访问的HTTP(S) URL");
      errorResult.put("data", errorData);
      return errorResult;
    }

    // 构建响应数据对象
    Map<String, Object> data = new HashMap<>();
    data.put("url", downloadUrl); // url字段是必须的

    // 如果文件URL需要特殊的请求头（如Referer防盗链），可以在这里设置
    // 例如：如果OSS设置了Referer防盗链，需要返回Referer头
    // Map<String, String> headers = new HashMap<>();
    // headers.put("Referer", "https://your-domain.com");
    // data.put("headers", headers);

    // 按照WPS官方文档格式返回：{"code": 0, "data": {...}}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", data);

    log.info("返回文件下载地址: fileId={}, url={}, response={}", fileId, downloadUrl, result);
    return result;
  }

  /**
   * 3. 获取文档用户权限
   * GET /v3/3rd/files/{file_id}/permission
   * 
   * 注意：
   * - 此接口只接受文档ID，用户ID从文档的authorId获取
   * - 当update=1或print=1时，必须返回当前用户的user_id
   * - 需要实现相应的回调接口才能启用某些功能
   * - WPS SDK期望返回 {"code": 0, "data": {...}} 格式
   */
  @GetMapping("/files/{file_id}/permission")
  @Operation(summary = "获取文档用户权限")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> getFilePermission(
      @Parameter(description = "文档ID", required = true, example = "1") @PathVariable("file_id") String fileId) {
    log.info("WPS回调 - 获取文档权限: fileId={}", fileId);

    // 查询文档
    ZkDocumentDO document = documentMapper.selectById(Long.parseLong(fileId));
    if (document == null) {
      log.error("文档不存在: fileId={}", fileId);
      // 返回错误格式，而不是抛出异常
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("code", 400);
      Map<String, Object> errorData = new HashMap<>();
      errorData.put("error", "文档不存在");
      errorResult.put("data", errorData);
      return errorResult;
    }

    // 获取当前登录用户ID（如果WPS请求中带有token，可以获取到）
    Long currentUserId = null;
    try {
      currentUserId = SecurityFrameworkUtils.getLoginUserId();
      log.info("从SecurityFrameworkUtils获取当前用户ID: userId={}", currentUserId);
    } catch (Exception e) {
      // WPS服务器调用时可能没有token，无法获取当前用户
      log.debug("无法获取当前登录用户ID（WPS服务器调用可能没有token）: {}", e.getMessage());
    }

    // 从文档中获取用户ID（文档的作者ID）
    Long documentAuthorId = document.getAuthorId();
    String finalUserId;
    if (documentAuthorId != null) {
      finalUserId = documentAuthorId.toString();
    } else {
      log.warn("文档没有authorId，使用默认值0: fileId={}", fileId);
      documentAuthorId = 0L;
      finalUserId = "0";
    }

    // 如果无法获取当前用户ID，使用文档作者ID作为默认值（用于WPS SDK返回）
    if (currentUserId == null) {
      currentUserId = 0L;
      log.info("使用文档作者ID作为当前用户ID: fileId={}, authorId={}", fileId, documentAuthorId);
    }

    log.info("文档权限判断: fileId={}, documentAuthorId={}, currentUserId={}, status={}",
        fileId, documentAuthorId, currentUserId, document.getStatus());

    // 判断权限
    // 1. 读权限：所有人都可读
    boolean canRead = true;

    // 2. 编辑权限：文档是共享的 或者 当前用户是文档所有者
    boolean canEdit = false;
    if ("shared".equals(document.getStatus())) {
      // 共享文档：所有人都可以编辑
      canEdit = true;
      log.info("文档是共享的，允许编辑: fileId={}", fileId);
    } else if (currentUserId != null && currentUserId.equals(documentAuthorId)) {
      // 私有文档：只有文档所有者可以编辑
      canEdit = true;
      log.info("当前用户是文档所有者，允许编辑: fileId={}, userId={}", fileId, currentUserId);
    } else {
      // 私有文档且不是所有者：只能查看
      canEdit = false;
      log.info("私有文档且当前用户不是所有者，不允许编辑: fileId={}, currentUserId={}, authorId={}",
          fileId, currentUserId, documentAuthorId);
    }

    // 3. 下载权限：有读权限就有下载权限
    boolean canDownload = canRead;

    // 构建响应数据对象
    Map<String, Object> data = new HashMap<>();
    // 返回当前用户ID（优先使用实际登录用户ID，如果没有则使用文档作者ID）
    data.put("user_id", currentUserId != null ? currentUserId.toString() : finalUserId);
    data.put("read", canRead ? 1 : 0);
    data.put("update", canEdit ? 1 : 0);
    data.put("download", canDownload ? 1 : 0);
    data.put("rename", canEdit ? 1 : 0); // 有编辑权限就可以重命名
    data.put("history", 0); // 历史版本功能暂不实现
    data.put("copy", canRead ? 1 : 0); // 有读权限就可以复制
    data.put("print", canRead ? 1 : 0); // 有读权限就可以打印
    data.put("saveas", 0); // 暂不支持
    data.put("comment", canRead ? 1 : 0); // 有读权限就可以评论

    // 按照WPS官方文档格式返回：{"code": 0, "data": {...}}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", data);

    log.info("返回文档权限: fileId={}, userId={}, read={}, update={}", fileId, finalUserId, canRead, canEdit);
    return result;
  }

  /**
   * 文档重命名
   * PUT /v3/3rd/files/{file_id}/name
   * 
   * 该接口用于在 WebOffice 侧重命名文档
   * 
   * 请求参数：
   * - file_id: 文档ID（Path参数，必填）
   * - name: 新文档名称（Body参数，必填，包含扩展名）
   * 
   * 返回值：
   * {
   * "code": 0,
   * "data": {}
   * }
   * 
   * 注意：WPS SDK期望返回 {"code": 0, "data": {}} 格式
   */
  @PutMapping("/files/{file_id}/name")
  @Operation(summary = "文档重命名")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> renameFile(
      @Parameter(description = "文档ID", required = true, example = "1") @PathVariable("file_id") String fileId,
      @RequestBody WPSRenameFileReqVO request) {
    log.info("WPS回调 - 文档重命名: fileId={}, newName={}", fileId, request.getName());

    // 验证fileId不为空
    if (fileId == null || fileId.trim().isEmpty()) {
      log.error("fileId为空: fileId={}", fileId);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("code", 400);
      Map<String, Object> errorData = new HashMap<>();
      errorData.put("error", "fileId不能为空");
      errorResult.put("data", errorData);
      return errorResult;
    }

    // 验证name不为空
    if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
      log.error("name为空: fileId={}", fileId);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("code", 400);
      Map<String, Object> errorData = new HashMap<>();
      errorData.put("error", "name不能为空");
      errorResult.put("data", errorData);
      return errorResult;
    }

    // 查询文档
    ZkDocumentDO document = documentMapper.selectById(Long.parseLong(fileId));
    if (document == null) {
      log.error("文档不存在: fileId={}", fileId);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("code", 400);
      Map<String, Object> errorData = new HashMap<>();
      errorData.put("error", "文档不存在");
      errorResult.put("data", errorData);
      return errorResult;
    }

    // 提取文件名（去掉扩展名，因为title字段不包含扩展名）
    String newName = request.getName().trim();
    String title = newName;

    // 如果包含扩展名，去掉扩展名
    if (newName.contains(".")) {
      int lastDotIndex = newName.lastIndexOf(".");
      // 确保不是隐藏文件（以.开头）
      if (lastDotIndex > 0) {
        title = newName.substring(0, lastDotIndex);
      }
    }

    // 更新文档标题
    ZkDocumentDO updateObj = new ZkDocumentDO();
    updateObj.setId(Long.parseLong(fileId));
    updateObj.setTitle(title);

    int updateCount = documentMapper.updateById(updateObj);

    if (updateCount > 0) {
      log.info("文档重命名成功: fileId={}, oldTitle={}, newTitle={}, newName={}",
          fileId, document.getTitle(), title, newName);
    } else {
      log.warn("文档重命名失败，更新行数为0: fileId={}", fileId);
    }

    // 按照WPS官方文档格式返回：{"code": 0, "data": {}}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", new HashMap<>());

    return result;
  }

  /**
   * 4. 事件通知
   * POST /v3/3rd/notify
   * 
   * 支持的事件类型：
   * - session_open: 文件首次打开
   * - session_quit: 文件关闭
   * - user_join: 用户加入会话
   * - user_quit: 用户退出会话
   * - operate_record_export: 导出操作记录
   * 
   * 注意：WPS SDK期望返回JSON格式：{"code": 0}
   */
  @PostMapping("/notify")
  @Operation(summary = "事件通知")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> notify(@RequestBody WPSNotifyReqVO data) {
    log.info("WPS回调 - 收到事件通知: fileId={}, type={}", data.getFile_id(), data.getType());

    String eventType = data.getType();
    String fileId = data.getFile_id();

    // 处理各种事件
    switch (eventType) {
      case "session_open":
        log.info("文件打开事件: fileId={}, sessionId={}, readonly={}",
            fileId, data.getSession_id(), data.getReadonly());
        // TODO: 记录文件打开日志
        break;
      case "session_quit":
        log.info("文件关闭事件: fileId={}", fileId);
        // TODO: 记录文件关闭日志
        break;
      case "user_join":
        log.info("用户加入会话: fileId={}", fileId);
        // TODO: 记录用户加入日志
        break;
      case "user_quit":
        log.info("用户退出会话: fileId={}", fileId);
        // TODO: 记录用户退出日志
        break;
      case "operate_record_export":
        log.info("导出操作记录: fileId={}", fileId);
        // TODO: 处理操作记录导出
        break;
      default:
        log.warn("未知事件类型: type={}", eventType);
    }

    // 按照WPS官方文档格式返回：{"code": 0}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    return result;
  }

  /**
   * 5. 三阶段保存-准备上传
   * GET /v3/3rd/files/{file_id}/upload/prepare
   * 
   * 主要用于WebOffice与接入方进行参数协商，目前主要协商摘要算法
   * 
   * 注意：WPS SDK期望返回 {"code": 0, "data": {...}} 格式
   */
  @GetMapping("/files/{file_id}/upload/prepare")
  @Operation(summary = "准备上传")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> prepareUpload(
      @Parameter(description = "文档ID", required = true, example = "1") @PathVariable("file_id") String fileId) {
    log.info("WPS回调 - 准备上传: fileId={}", fileId);

    // 查询文档
    ZkDocumentDO document = documentMapper.selectById(Long.parseLong(fileId));
    if (document == null) {
      throw exception(DOCUMENT_NOT_EXISTS);
    }

    // 构建响应数据对象
    Map<String, Object> data = new HashMap<>();
    data.put("digest_types", Arrays.asList("sha1")); // 支持sha1算法

    // 按照WPS官方文档格式返回：{"code": 0, "data": {...}}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", data);

    return result;
  }

  /**
   * 6. 三阶段保存-获取上传地址
   * POST /v3/3rd/files/{file_id}/upload/address
   * 
   * 获取文件上传地址，WebOffice会将保存的文档上传到此地址
   * 
   * 注意：WPS SDK期望返回 {"code": 0, "data": {...}} 格式
   */
  @PostMapping("/files/{file_id}/upload/address")
  @Operation(summary = "获取上传地址")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> getUploadAddress(
      @Parameter(description = "文档ID", required = true, example = "1") @PathVariable("file_id") String fileId,
      @RequestBody WPSUploadAddressReqVO request) {
    log.info("WPS回调 - 获取上传地址: fileId={}, name={}, size={}, isManual={}",
        fileId, request.getName(), request.getSize(), request.getIs_manual());

    // 查询文档
    ZkDocumentDO document = documentMapper.selectById(Long.parseLong(fileId));
    if (document == null) {
      throw exception(DOCUMENT_NOT_EXISTS);
    }

    // 生成上传地址（参考协同办公中的文件上传实现）
    // 使用FileService生成预签名URL
    String fileName = request.getName();
    if (fileName == null || fileName.isEmpty()) {
      // 如果没有文件名，使用文档标题
      fileName = document.getTitle();
      if (fileName == null || fileName.isEmpty()) {
        fileName = "document_" + fileId;
      }
      // 确保有文件扩展名
      if (!fileName.contains(".")) {
        // 从原始文件URL中提取扩展名
        String originalUrl = document.getFileUrl();
        if (originalUrl != null && originalUrl.contains(".")) {
          String ext = originalUrl.substring(originalUrl.lastIndexOf("."));
          fileName = fileName + ext;
        } else {
          fileName = fileName + ".docx"; // 默认扩展名
        }
      }
    }

    // 生成预签名上传URL（参考FileController的实现）
    // 使用oa/document目录，与协同办公保持一致
    FilePresignedUrlRespVO presignedUrl = fileApi.presignPutUrl(fileName, "oa/document");

    // 构建响应，将文件访问URL放入send_back_params，以便上传完成后使用
    Map<String, String> sendBackParams = new HashMap<>();
    sendBackParams.put("file_url", presignedUrl.getUrl()); // 文件访问URL
    sendBackParams.put("file_path", presignedUrl.getPath()); // 文件路径
    sendBackParams.put("config_id", presignedUrl.getConfigId().toString()); // 配置ID

    // 构建响应数据对象
    Map<String, Object> data = new HashMap<>();
    data.put("url", presignedUrl.getUploadUrl()); // 预签名上传URL
    data.put("method", "PUT"); // 目前只支持PUT方法
    data.put("headers", new HashMap<>()); // 如果需要额外的请求头，可以在这里设置
    data.put("params", new HashMap<>()); // 如果需要额外的查询参数，可以在这里设置
    data.put("send_back_params", sendBackParams); // 需要原样带回的参数（包含文件URL）

    // 按照WPS官方文档格式返回：{"code": 0, "data": {...}}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", data);

    log.info("生成上传地址成功: fileId={}, uploadUrl={}, fileUrl={}",
        fileId, presignedUrl.getUploadUrl(), presignedUrl.getUrl());

    return result;
  }

  /**
   * 7. 三阶段保存-上传完成回调
   * POST /v3/3rd/files/{file_id}/upload/complete
   * 
   * WebOffice在将新版本文件上传到指定地址后，会回调通知接入方
   * 遵循「文件ID一致性」原则：接口中文件「请求ID」和「返回ID」需保持一致
   * 
   * 注意：WPS SDK期望返回 {"code": 0, "data": {...}} 格式
   */
  @PostMapping("/files/{file_id}/upload/complete")
  @Operation(summary = "上传完成回调")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> uploadComplete(
      @Parameter(description = "文档ID", required = true, example = "1") @PathVariable("file_id") String fileId,
      @RequestBody WPSUploadCompleteReqVO data) {
    log.info("WPS回调 - 上传完成: fileId={}, statusCode={}",
        fileId, data.getResponse() != null ? data.getResponse().getStatus_code() : null);

    // 查询文档
    ZkDocumentDO document = documentMapper.selectById(Long.parseLong(fileId));
    if (document == null) {
      throw exception(DOCUMENT_NOT_EXISTS);
    }

    // 检查上传是否成功
    String newFileUrl = document.getFileUrl(); // 默认使用原URL
    if (data.getResponse() != null && data.getResponse().getStatus_code() != null) {
      int statusCode = data.getResponse().getStatus_code();
      if (statusCode >= 200 && statusCode < 300) {
        // 上传成功
        log.info("文件上传成功: fileId={}, statusCode={}", fileId, statusCode);

        // 从send_back_params中提取文件URL（在获取上传地址时设置的）
        if (data.getSend_back_params() != null) {
          String fileUrl = data.getSend_back_params().get("file_url");
          String filePath = data.getSend_back_params().get("file_path");

          if (fileUrl != null && !fileUrl.isEmpty()) {
            newFileUrl = fileUrl;
            log.info("从send_back_params获取文件URL: fileId={}, fileUrl={}", fileId, fileUrl);
          } else if (filePath != null && !filePath.isEmpty()) {
            // 如果没有URL，但有路径，可以尝试构建URL
            // 这里需要根据实际的文件存储方式调整
            log.info("获取到文件路径: fileId={}, filePath={}", fileId, filePath);
          }
        }

        // 更新文件信息
        ZkDocumentDO updateObj = new ZkDocumentDO();
        updateObj.setId(Long.parseLong(fileId));

        // 更新文件URL（如果上传到了新地址）
        if (newFileUrl != null && !newFileUrl.equals(document.getFileUrl())) {
          updateObj.setFileUrl(newFileUrl);
          log.info("更新文件URL: fileId={}, oldUrl={}, newUrl={}",
              fileId, document.getFileUrl(), newFileUrl);
        }

        // 更新文件大小
        if (data.getRequest() != null && data.getRequest().getSize() != null) {
          updateObj.setFileSize(data.getRequest().getSize());
          log.info("更新文件大小: fileId={}, size={}", fileId, data.getRequest().getSize());
        }

        // 执行更新
        if (updateObj.getFileUrl() != null || updateObj.getFileSize() != null) {
          documentMapper.updateById(updateObj);
          log.info("已更新文件信息: fileId={}", fileId);
        }
      } else {
        log.error("文件上传失败: fileId={}, statusCode={}", fileId, statusCode);
      }
    } else {
      log.warn("上传响应中没有状态码: fileId={}", fileId);
    }

    // 构建响应数据对象
    Map<String, Object> responseData = new HashMap<>();
    responseData.put("file_id", fileId); // 必须与传入的file_id一致
    responseData.put("download_url", newFileUrl); // 使用更新后的URL

    // 按照WPS官方文档格式返回：{"code": 0, "data": {...}}
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", responseData);

    return result;
  }

  /**
   * 8. 批量获取用户信息
   * GET /v3/3rd/users
   * 
   * 用于获取指定用户的名称和头像，在协作场景中很重要
   * 例如：查看历史变更、在线协作用户头像等场景需要获取用户信息
   * 
   * WPS官方文档示例格式：
   * {
   * "code": 0,
   * "data": [
   * {
   * "id": "1",
   * "name": "user name1"
   * },
   * {
   * "id": "2",
   * "name": "user name2"
   * }
   * ]
   * }
   * 
   * 重要提示：
   * 1. 必须能够接收多个user_ids参数（可重复），例如：/v3/3rd/users?user_ids=1&user_ids=2&user_ids=3
   * 2. 返回值应该是与传入的user_ids对应的用户信息数组
   * 3. data字段直接是用户数组，格式：{"code": 0, "data": [...]}
   * 4. 每个用户对象必须包含：id（必须）、name（必须）、avatar_url（可选，需要HTTPS链接）
   * 5. 不要硬编码返回值，否则可能导致某些功能异常
   * 
   * 常见功能异常场景（如果遇到以下问题，优先排查此接口实现）：
   * - 用户打开文档，但当前用户找不到，导致文档无法打开
   * - 用户插入图片，但当前用户找不到，导致图片插入失败
   * - 评论用户信息无法显示
   * - 回复评论无法正常显示回复者
   * - 协作记录对应的用户信息缺失，导致无法回滚
   * - 对应的用户版本信息异常等
   */
  @GetMapping("/users")
  @Operation(summary = "批量获取用户信息")
  @PermitAll // 允许WPS服务器调用，无需认证
  @ResponseBody
  public Map<String, Object> getUsers(
      @Parameter(description = "用户ID列表，可重复", required = true, example = "1,2,3") @RequestParam("user_ids") List<String> userIds) {
    log.info("WPS回调 - 批量获取用户信息: userIds={}", userIds);

    // 去重
    Set<String> uniqueUserIds = new HashSet<>(userIds);

    // 查询用户信息
    List<Map<String, Object>> users = new ArrayList<>();
    for (String userId : uniqueUserIds) {
      try {
        Long userIdLong = Long.parseLong(userId);
        AdminUserRespDTO user = adminUserApi.getUser(userIdLong);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userId);
        if (user != null) {
          userInfo.put("name", user.getNickname() != null ? user.getNickname() : user.getUsername());
          if (user.getAvatar() != null) {
            userInfo.put("avatar_url", user.getAvatar());
          }
        } else {
          log.warn("用户不存在: userId={}", userId);
          // 即使用户不存在，也返回基本信息，避免影响功能
          userInfo.put("name", "未知用户");
        }
        users.add(userInfo);
      } catch (Exception e) {
        log.error("获取用户信息失败: userId={}", userId, e);
        // 即使出错，也返回基本信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userId);
        userInfo.put("name", "未知用户");
        users.add(userInfo);
      }
    }

    // 按照WPS官方文档格式返回：{"code": 0, "data": [...]}
    // 注意：data字段直接是用户数组，而不是包含users字段的对象
    Map<String, Object> result = new HashMap<>();
    result.put("code", 0);
    result.put("data", users); // data直接是用户数组

    return result;
  }

  // ========== 私有方法 ==========

  /**
   * 判断用户是否有读权限
   */
  private boolean canReadDocument(ZkDocumentDO document, Long userId) {
    // 共享文档：所有人可读
    if ("shared".equals(document.getStatus())) {
      return true;
    }
    // 私有文档：只有创建者可读
    return document.getAuthorId() != null && document.getAuthorId().equals(userId);
  }

  /**
   * 判断用户是否有编辑权限
   */
  private boolean canEditDocument(ZkDocumentDO document, Long userId) {
    // 共享文档：所有人可编辑
    if ("shared".equals(document.getStatus())) {
      return true;
    }
    // 私有文档：只有创建者可编辑
    return document.getAuthorId() != null && document.getAuthorId().equals(userId);
  }

}
