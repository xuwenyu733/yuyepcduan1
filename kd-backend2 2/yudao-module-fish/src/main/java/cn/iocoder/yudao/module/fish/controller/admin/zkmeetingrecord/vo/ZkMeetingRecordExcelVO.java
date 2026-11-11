package cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 会议记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkMeetingRecordExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("会议id")
    private Long meetingId;
    private String meetingName;
    private String meetingNum;
    private String meetDay; // 会议日期

    @ExcelProperty("记录名称")
    private String meetingRecordName;

    @ExcelProperty("记录描述")
    private String meetingRecordContent;

    @ExcelProperty("记录概要")
    private String meetingPoint;

    @ExcelProperty("记录总结")
    private String meetingSummary;
    private String meetToDo; // 会议待办

    @ExcelProperty("记录人id")
    private Long userId;

    @ExcelProperty("记录人")
    private String userName;

    @ExcelProperty("记录人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("状态(0:已记录;1:待入库;2:已入库;3:待评价;3:已评价;-1:已删除;)")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("文件id")
    private String fid;
    private String localVideoUrls; // 会议录屏url
    private String remoteVideoUrls; // 会议录屏阿里云网盘url
    private Integer videoGetCount; // 录屏获取次数

}
