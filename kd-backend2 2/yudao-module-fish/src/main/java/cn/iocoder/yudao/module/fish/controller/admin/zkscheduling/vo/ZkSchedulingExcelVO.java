package cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 排班 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkSchedulingExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("排班名称")
    private String schedulingName;

    @ExcelProperty("排班描述")
    private String schedulingContent;

    @ExcelProperty("排班人id")
    private Long userId;
    private List<Long> userIds;

    @ExcelProperty("排班人")
    private String userName;

    @ExcelProperty("排班人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)")
    private String status;
    /**
     * 任务类型(1:打卡;2:巡查;3:其他;)
     */
    private String schedulingType;

    @ExcelProperty("排班人id")
    private String userIdStr;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("文件id")
    private String fid;
    private String schedulingTime;
    private List<String> selectDays;
    private Integer selectDayType;
    private String taskConfigIds; // 巡检配置ids
    private String pondIds; // 存塘ids(多个英文逗号隔开)

}
