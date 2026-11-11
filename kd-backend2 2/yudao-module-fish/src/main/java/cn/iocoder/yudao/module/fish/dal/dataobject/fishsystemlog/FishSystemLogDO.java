package cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;


/**
 * fish系统日志 DO
 *
 * @author 系统管理员
 */
@TableName("zk_fish_system_log")
@KeySequence("zk_fish_system_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FishSystemLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 存塘编号
     */
    private String pondDetailNum;
    /**
     * 日志标题
     */
    private String logTitle;
    /**
     * 日志类型(0:fish;1:其他;)
     */
    private String logType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 原始数据
     */
    private String originData;
    /**
     * 目标数据
     */
    private String targetData;
    /**
     * 原始结果数据
     */
    private String originRes;
    /**
     * 目标结果数据
     */
    private String targetRes;
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

    public FishSystemLogDO(String pondDetailNum, String logTitle, String originData, String targetData, String originRes, String targetRes) {
        this.pondDetailNum = pondDetailNum;
        this.logTitle = logTitle;
        this.originData = originData;
        this.targetData = targetData;
        this.originRes = originRes;
        this.targetRes = targetRes;
    }
}
