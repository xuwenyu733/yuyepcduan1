package cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 聊天日志 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ChatLogExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("消息发送人")
    private String roleName;

    @ExcelProperty("聊天内容")
    private String chatContent;

    @ExcelProperty("对话所属人id")
    private Long userId;

    @ExcelProperty("对话所属人")
    private String userName;

    @ExcelProperty("对话所属人部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("文件id")
    private String fid;
    private BigDecimal askScore; // 评分

}
