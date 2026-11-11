package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验权限用户 DO
 */
@TableName("zk_experimental_permission_user")
@KeySequence("zk_experimental_permission_user_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalPermissionUserDO extends BaseDO {

    @TableId
    private Long id;

    /** 系统用户ID */
    private Long userId;

    /** 用户姓名 */
    private String userName;

    /** 角色ID */
    private Long roleId;

    /** 角色名称 */
    private String roleName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态 0启用 1禁用 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 所属部门 */
    private Long deptId;

}
