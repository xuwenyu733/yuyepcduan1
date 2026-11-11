package cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 试题 DO
 *
 * @author 系统管理员
 */
@TableName("zk_paper_question")
@KeySequence("zk_paper_question_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPaperQuestionDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 试卷ID
     */
    private Long paperId;
    /**
     * 试题类型 1单选题 2多选题 3判断题 
     */
    private String type;
    /**
     * 试题类型
     */
    private String typeName;
    /**
     * 题干
     */
    private String question;
    /**
     * 答案
     */
    private String answer;
    /**
     * 解析
     */
    private String analysis;
    /**
     * 标签
     */
    private String tag;
    /**
     * 分数
     */
    private Double score;
    /**
     * 选项
     */
    private String options;
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

    @TableField(exist = false)
    private List<String> myAnswer2 = new ArrayList<>();

}
