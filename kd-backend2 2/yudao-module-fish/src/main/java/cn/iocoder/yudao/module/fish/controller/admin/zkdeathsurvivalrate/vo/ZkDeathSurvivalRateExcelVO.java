package cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 死亡和存活率 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkDeathSurvivalRateExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("所属车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("所属系统id")
    private Long pondSystemId;

    @ExcelProperty("存塘系统编号")
    private String pondNum;

    @ExcelProperty("存塘系统名称")
    private String pondSystemName;

    @ExcelProperty("所属存塘id")
    private Long pondId;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("批次编号")
    private String batchNumber;

    @ExcelProperty("鱼苗数量")
    private Integer fryQuantity;

    @ExcelProperty("死亡数量")
    private Integer deathQuantity;

    @ExcelProperty("销售数量")
    private Integer salesQuantity;

    @ExcelProperty("分塘数量")
    private Integer pondTransferQuantity;

    @ExcelProperty("存塘数量")
    private Integer stockedQuantity;

    @ExcelProperty("存活率")
    private Object survivalRate;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}
