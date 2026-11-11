package cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 鱼类健康评估 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkFishHealthExcelVO {

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

    @ExcelProperty("呼吸频率")
    private Integer breathingRate;

    @ExcelProperty("摄食状态")
    private Integer feedingStatus;

    @ExcelProperty("健康数量")
    private Integer healthyCount;

    @ExcelProperty("亚健康数量")
    private Integer subhealthyCount;

    @ExcelProperty("发病数量")
    private Integer diseasedCount;

    @ExcelProperty("健康评分")
    private Integer healthScore;

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
