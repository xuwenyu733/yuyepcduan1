package cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 试卷 DO
 *
 * @author 系统管理员
 */
@TableName("zk_paper")
@KeySequence("zk_paper_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPaperDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 试卷名称
     */
    private String name;
    /**
     * 考试说明
     */
    private String content;
    /**
     * 开始考试时间
     */
    private LocalDateTime startTime;
    /**
     * 结束考试时间
     */
    private LocalDateTime endTime;
    /**
     * 时长（分钟）
     */
    private Integer duration;
    /**
     * 是否允许查看答案 1是 0否
     */
    private String lookAnswer;
    /**
     * 是否打乱试题 1是 0否
     */
    private String upset;
    /**
     * 及格分数
     */
    private Double passScore;
    /**
     * 试卷总分
     */
    private Double totalScore;
    /**
     * 状态 1正常 0删除
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

}
