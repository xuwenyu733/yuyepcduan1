package cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 请假 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkAskforLeaveExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("请假名称")
    private String leaveName;

    @ExcelProperty("请假备注")
    private String leaveContent;

    @ExcelProperty("请假天数")
    private Object leaveDay;

    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @ExcelProperty("状态(0:待审批;1:已审批;2:已销假;-1:待申请;)")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("请假人id")
    private Long userId;
    private String userName;

    @ExcelProperty("请假人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
    private String processInstanceId;
    private String askforType; // 请假类型(1:事假;2:调休;3:病假;4:产假;5:陪产假;6:婚嫁;7:其他;)
    private Long approveUserId; // 上级审批人
    private Long noticeUserId; // 抄送人

}
