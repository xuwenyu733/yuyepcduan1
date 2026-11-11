package cn.iocoder.yudao.module.fish.dal.dataobject.zkdayrecord;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 时间字典 DO
 *
 * @author 系统管理员
 */
@TableName("zk_day_record")
@KeySequence("zk_day_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDayRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 时间(格式:yyyy-MM-dd)
     */
    private String simpleFormat;

    private Integer dateYear;
    private Integer dateMonth;
    private Integer dateValue;
    /**
     * 日期名称(只有节假日有意义)
     */
    private String holidayName;
    /**
     * 星期名称
     */
    private String weekName;
    /**
     * 0:工作日;1:周末;2:节假日;
     */
    private Byte dateType;
    /**
     * 备注
     */
    private String remark;

    public ZkDayRecordDO(String simpleFormat, String holidayName, String weekName, Byte dateType) {
        this.simpleFormat = simpleFormat;
        this.holidayName = holidayName;
        this.weekName = weekName;
        this.dateType = dateType;
    }

    public ZkDayRecordDO(String simpleFormat, Integer dateYear, Integer dateMonth, Integer dateValue, String holidayName, String weekName, Byte dateType) {
        this.simpleFormat = simpleFormat;
        this.dateYear = dateYear;
        this.dateMonth = dateMonth;
        this.dateValue = dateValue;
        this.holidayName = holidayName;
        this.weekName = weekName;
        this.dateType = dateType;
    }
}
