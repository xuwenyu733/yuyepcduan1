package cn.iocoder.yudao.module.fish.utils;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 解析「请求 deptId 或登录用户归属部门」并展开为 本部门 + 子部门。
 * <p>
 * 无归属部门时：仅平台超管返回 {@code null}（不加部门过滤）；
 * 普通用户返回 {@link #DENIED_DEPT_IDS}，SQL 的 {@code dept_id IN (-1)} 结果为空，禁止串看全站。
 * <p>
 * 请求参数 {@code deptId}：平台超管可指定任意部门；普通用户仅当目标部门落在本人部门子树内才放行，
 * 否则强制回落登录部门（防止构造 ?deptId= 越权读他部门）。
 */
@Component
public class DeptScopeSupport {

    /** 不可能存在的部门 id：用于「无部门普通用户」强制过滤为空 */
    public static final Long DENIED_DEPT_ID = -1L;
    /** 不可变哨兵；对外 resolve 一律拷贝为 ArrayList，避免 QueryUtil 按 ArrayList 强转崩溃 */
    public static final List<Long> DENIED_DEPT_IDS = Collections.singletonList(DENIED_DEPT_ID);

    private static final Pattern PLATFORM_ROLE = Pattern.compile(
            "^(admin|super_admin|super_administrator|tenant_admin|ke_admin)$",
            Pattern.CASE_INSENSITIVE);

    @Resource
    private BaseDeptService baseDeptService;
    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 解析最终使用的部门 id（请求参数 vs 登录部门，含越权钳制）。
     */
    public Long resolveRequestOrLoginDeptId(Long requestDeptId) {
        Long loginDeptId = resolveLoginDeptId();
        boolean isAdmin = isLoginUserPlatformUnscopedAdmin();

        if (requestDeptId != null && requestDeptId > 0) {
            // 平台超管：允许指定任意部门做全局排查
            if (isAdmin) {
                return requestDeptId;
            }
            // 普通用户：仅允许本人部门子树内的 deptId（含本部门）
            if (loginDeptId != null && loginDeptId > 0) {
                if (requestDeptId.equals(loginDeptId) || isDeptInSubtree(loginDeptId, requestDeptId)) {
                    return requestDeptId;
                }
                // 越权：忽略外部 deptId，强制本部门
                return loginDeptId;
            }
            // 无部门普通用户：忽略外部 deptId → 后续走 DENIED
            return null;
        }
        return loginDeptId;
    }

    /** 当前登录用户的归属部门（无则 null） */
    public Long resolveLoginDeptId() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return null;
        }
        try {
            AdminUserRespDTO user = adminUserApi.getUser(userId);
            if (user == null || user.getDeptId() == null || user.getDeptId() <= 0) {
                return null;
            }
            return user.getDeptId();
        } catch (Exception ex) {
            return null;
        }
    }

    /** requestDeptId 是否属于 loginDeptId 的本部门+子部门 */
    private boolean isDeptInSubtree(Long loginDeptId, Long requestDeptId) {
        if (loginDeptId == null || requestDeptId == null) {
            return false;
        }
        List<Long> allowed = baseDeptService.getSubDeptIds(loginDeptId);
        return allowed != null && allowed.contains(requestDeptId);
    }

    /**
     * @return 本部门+子部门 id 列表（可变 ArrayList）；
     *         平台超管且无部门时返回 {@code null}（不加过滤）；
     *         普通用户无部门时返回 {@link #DENIED_DEPT_IDS} 的拷贝（查空）
     */
    public List<Long> resolveDeptIds(Long requestDeptId) {
        Long deptId = resolveRequestOrLoginDeptId(requestDeptId);
        if (deptId == null || deptId <= 0) {
            return isLoginUserPlatformUnscopedAdmin() ? null : new ArrayList<>(DENIED_DEPT_IDS);
        }
        List<Long> ids = baseDeptService.getSubDeptIds(deptId);
        if (ids == null || ids.isEmpty()) {
            List<Long> single = new ArrayList<>(1);
            single.add(deptId);
            return single;
        }
        // getSubDeptIds 已返回 ArrayList；再包一层避免调用方改写缓存/共享列表
        return new ArrayList<>(ids);
    }

    /** 是否为「无部门普通用户」被拒绝的范围（应返回空数据，勿走全站面板） */
    public boolean isDeniedScope(List<Long> deptIds) {
        return deptIds != null
                && deptIds.size() == 1
                && DENIED_DEPT_ID.equals(deptIds.get(0));
    }

    /** 是否有明确部门范围（非超管全站、非拒绝哨兵） */
    public boolean hasConcreteDeptScope(List<Long> deptIds) {
        return deptIds != null && !deptIds.isEmpty() && !isDeniedScope(deptIds);
    }

    /** 缓存 key 片段，避免跨部门命中同一 Redis 缓存 */
    public String cacheDeptSuffix(Long requestDeptId) {
        Long deptId = resolveRequestOrLoginDeptId(requestDeptId);
        if (deptId != null && deptId > 0) {
            return "d" + deptId;
        }
        return isLoginUserPlatformUnscopedAdmin() ? "all" : "denied";
    }

    public boolean isLoginUserPlatformUnscopedAdmin() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return false;
        }
        try {
            AdminUserRespDTO user = adminUserApi.getUser(userId);
            return isPlatformUnscopedAdmin(user);
        } catch (Exception ex) {
            return false;
        }
    }

    static boolean isPlatformUnscopedAdmin(AdminUserRespDTO user) {
        if (user == null || !StringUtils.hasText(user.getRoleCodes())) {
            return false;
        }
        String[] parts = user.getRoleCodes().split("[,;\\s]+");
        for (String part : parts) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            String code = part.trim().toLowerCase(Locale.ROOT);
            if (PLATFORM_ROLE.matcher(code).matches()) {
                return true;
            }
        }
        return false;
    }
}
