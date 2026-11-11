package cn.iocoder.yudao.module.fish.dal.dataobject.zktrainingknowledge;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 培训知识 DO
 *
 * @author 系统管理员
 */
@TableName("zk_training_knowledge")
@KeySequence("zk_training_knowledge_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingKnowledgeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 知识名称
     */
    private String knowledgeName;
    /**
     * 知识详情
     */
    private String knowledgeContent;
    /**
     * 知识备注
     */
    private String knowledgeRemark;
    /**
     * 状态(0:待入库;1:已入库;-1:已删除;)
     */
    private String status;
    /**
     * 负责人id
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;
    private String agentId;

}
