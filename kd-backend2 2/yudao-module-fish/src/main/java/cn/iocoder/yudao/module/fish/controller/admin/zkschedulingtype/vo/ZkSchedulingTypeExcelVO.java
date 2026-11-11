package cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 检测配置 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkSchedulingTypeExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("检测名称")
    private String checkName;

    @ExcelProperty("类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)")
    private String checkType;

    @ExcelProperty("备注")
    private String checkContent;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    private String fid;
    private String fileUrl;

}
