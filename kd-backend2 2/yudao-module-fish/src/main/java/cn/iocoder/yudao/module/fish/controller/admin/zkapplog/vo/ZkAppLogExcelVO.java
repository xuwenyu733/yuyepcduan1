package cn.iocoder.yudao.module.fish.controller.admin.zkapplog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * app记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkAppLogExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("app版本")
    private Integer appVersion;
    private String appName;

    @ExcelProperty("app地址")
    private String fileUrl;
    private String fileId;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    private String baseWebUrl;
    private String deviceMac;

}
