package cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 会议用户 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkMeetingUserExcelVO {

    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("会议id")
    private Long meetingId;
    private String meetingNum;
    private String meetToDo; // 会议待办
    private String toDoFeedback; // 待办反馈

    @ExcelProperty("参会人id")
    private Long userId;

    @ExcelProperty("参会人")
    private String userName;

    @ExcelProperty("参会人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("用户类型(0:参会人;1:记录人;2:主持人;3:列席人员;3:特邀人员;-1:旁听人员;)")
    private String userType;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("文件id")
    private String fid;

}
