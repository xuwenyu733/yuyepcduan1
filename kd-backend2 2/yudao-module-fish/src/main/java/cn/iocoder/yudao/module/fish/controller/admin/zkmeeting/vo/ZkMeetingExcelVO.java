package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 会议 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkMeetingExcelVO {

    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("会议名称")
    private String meetingName;

    @ExcelProperty("会议备注")
    private String meetingContent;

    @ExcelProperty("会议编号")
    private String meetingNum;
    private String meetDay; // 会议日期

    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @ExcelProperty("状态(0:已预定;1:待开始;2:会议中;3:已结束;-1:已撤销;)")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("主持人id")
    private Long userId;
    private List<Long> userIds;
    private String userNameStr;

    @ExcelProperty("主持人")
    private String userName;

    @ExcelProperty("主持人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}
