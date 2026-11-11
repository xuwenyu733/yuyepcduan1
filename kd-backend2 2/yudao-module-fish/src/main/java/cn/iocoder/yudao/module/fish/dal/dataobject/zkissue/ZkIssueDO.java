package cn.iocoder.yudao.module.fish.dal.dataobject.zkissue;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 问题总结 DO
 *
 * @author 系统管理员
 */
@TableName("zk_issue")
@KeySequence("zk_issue_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkIssueDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 问题标题
     */
    private String issueTitle;
    /**
     * 问题描述
     */
    private String issueContent;
    /**
     * 总结标题
     */
    private String summaryTitle;
    /**
     * 总结内容
     */
    private String summaryContent;
    /**
     * 状态
     */
    private String status;
    /**
     * 附件
     */
    private String fids;
    /**
     * 流程实例
     */
    private String processInstanceId;
    /**
     * 记录人
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
