package cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 任务明细 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class TaskDetailExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("任务id(zk_task)")
    private Long taskId;
    private Long schedulingId; // 排班表id

    @ExcelProperty("检测名称")
    private String checkName;

    @ExcelProperty("检测类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)")
    private String checkType;

    @ExcelProperty("检测config")
    private String checkContent;

    @ExcelProperty("检测res")
    private String checkRes;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private Long workshopId; // 车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 系统名称
    private Long pondSystemId; // 系统id
    private String pondNum; // 系统编号
    private String pondName; // 存塘名称
    private Long pondId; // 存塘id
    private String pondDetailNum; // 存塘编号
    private String batchNumber; // 批次编号

    private Long userId; // 用户id
    private Long deptId; // 部门id
    private Long tenantId; // 租户id
}
