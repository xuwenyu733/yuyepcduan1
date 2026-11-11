package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验权限操作日志 DO
 */
@TableName("zk_experimental_permission_log")
@KeySequence("zk_experimental_permission_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalPermissionLogDO extends BaseDO {

    @TableId
    private Long id;

    /** 操作用户ID */
    private Long userId;

    /** 操作用户姓名 */
    private String userName;

    /** 操作类型 */
    private String action;

    /** 操作对象 */
    private String target;

    /** 详情 */
    private String detail;

    /** 所属部门 */
    private Long deptId;

}
