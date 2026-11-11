package cn.iocoder.yudao.module.fish.dal.dataobject.zkuserrelation;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户信息关联 DO
 *
 * @author admin2
 */
@TableName("zk_user_relation")
@KeySequence("zk_user_relation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkUserRelationDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 关联数据类型(1:个推;)
     */
    private Integer relationType;
    /**
     * 关联值
     */
    private String relationValue;
    /**
     * 备注(部分信息可能需要json)
     */
    private String relationRemark;
    /**
     * 所属部门
     */
    private Long deptId;


}