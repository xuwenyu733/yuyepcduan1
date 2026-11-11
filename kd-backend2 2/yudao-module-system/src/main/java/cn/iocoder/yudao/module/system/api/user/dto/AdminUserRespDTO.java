package cn.iocoder.yudao.module.system.api.user.dto;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Admin 用户 Response DTO
 *
 * @author 芋道源码
 */
@Data
public class AdminUserRespDTO {

    /**
     * 用户ID
     */
    private Long id;
    private String username;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 帐号状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;
    private List<Long> deptIds;
    /**
     * 部门
     */
    private DeptRespDTO dept;
    /**
     * 岗位编号数组
     */
    private Set<Long> postIds;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 用户头像
     */
    private String avatar;

    private String roleCodes;

    public Boolean userDeptIdsSearch() {
        if(StringUtil.isEmpty(roleCodes))
            return false;
        if(this.getRoleCodes().contains("fzr"))
            return true;
        if(this.getRoleCodes().contains("admin") || this.getRoleCodes().contains("负责人") ||
                this.getRoleCodes().contains("财务")) {
            return true;
        }
        return false;
    }

}
