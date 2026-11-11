package cn.iocoder.yudao.module.fish.controller.admin.zktask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 任务 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkTaskExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("业务id")
    private Long busId;
    private Long schedulingId;

    @ExcelProperty("任务名称")
    private String taskName;

    @ExcelProperty("任务描述")
    private String taskContent;

    @ExcelProperty("任务反馈")
    private String taskPoint;

    @ExcelProperty("任务结果")
    private String taskSummary;

    @ExcelProperty("执行人id")
    private Long userId;

    @ExcelProperty("执行人")
    private String userName;

    @ExcelProperty("执行人所属部门")
    private Long deptId;
    private Long tenantId;
    /**
     * 任务类型 参考字典配置：   zk_scheduling_type
     */
    private String taskType;

    @ExcelProperty("状态(0:已下发;1:已通知;2:待完成;3:已完成;4:已评估;-1:已删除;)")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("文件id")
    private String fid;
    private String fileUrl; // 详情url

    @ExcelProperty("流程实例")
    private String processInstanceId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime finishTime; // 任务完成时间
    private String taskConfigIds; // 巡检配置ids
    private String checkTypes; // 巡检类型(多个英文,号分隔)
}
