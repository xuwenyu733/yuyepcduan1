package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验权限角色 DO
 */
@TableName("zk_experimental_permission_role")
@KeySequence("zk_experimental_permission_role_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalPermissionRoleDO extends BaseDO {

    @TableId
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

    /** 权限标识，逗号分隔 */
    private String permissions;

    /** 描述 */
    private String description;

    /** 状态 0启用 1禁用 */
    private Integer status;

    /** 所属部门 */
    private Long deptId;

}
