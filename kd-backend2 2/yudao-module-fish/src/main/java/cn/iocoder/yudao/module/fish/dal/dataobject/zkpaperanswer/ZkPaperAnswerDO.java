package cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 答题记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_paper_answer")
@KeySequence("zk_paper_answer_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPaperAnswerDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 试卷ID
     */
    private Long paperId;
    @TableField(exist = false)
    private String paperName;
    /**
     * 答案JSON数组 [{questionId:1,answer:'A'}]
     */
    private String answer;
    /**
     * 交卷时间
     */
    private LocalDateTime submitTime;
    /**
     * 总得分
     */
    private Double score;
    /**
     * 是否及格 1是 0否
     */
    private String passed;
    /**
     * 考试人id
     */
    private Long userId;
    private String userName;
    /**
     * 考试人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

}
