package cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 打样记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkFishProofingExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("批次编号")
    private String batchNumber;

    @ExcelProperty("车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("系统名称")
    private String pondSystemName;

    @ExcelProperty("系统id")
    private Long pondSystemId;

    @ExcelProperty("系统编号")
    private String pondNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("存塘id")
    private Long pondId;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("初始规格")
    private BigDecimal initSpecs;

    @ExcelProperty("平均打样体长(cm)")
    private BigDecimal avgBodyLength;

    @ExcelProperty("平均打样规格(g)")
    private BigDecimal avgSpecs;

    @ExcelProperty("打样数量(尾)")
    private Long currNum;

    @ExcelProperty("任务明细id")
    private Long taskDetailId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("记录人")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
    private String statisticsType; // 统计类型(0:未统计;1:已统计;)

}
