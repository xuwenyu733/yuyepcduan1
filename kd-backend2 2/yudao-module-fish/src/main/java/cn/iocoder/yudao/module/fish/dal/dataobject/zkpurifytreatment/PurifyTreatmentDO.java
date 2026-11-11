package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifytreatment;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 处理措施记录 DO
 *
 * @author admin2
 */
@TableName("zk_purify_treatment")
@KeySequence("zk_purify_treatment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyTreatmentDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 关联流程ID
     */
    private Long processId;
    @TableField(exist = false)
    private String recordNumber;
    /**
     * 处理日期
     */
    private LocalDateTime treatmentDate;
    /**
     * 措施类型(用药/水质调节等)
     */
    private String type;
    /**
     * 措施内容
     */
    private String content;
    /**
     * 剂量
     */
    private String dosage;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 备注说明
     */
    private String notes;
    /**
     * 所属部门
     */
    private Long deptId;


}