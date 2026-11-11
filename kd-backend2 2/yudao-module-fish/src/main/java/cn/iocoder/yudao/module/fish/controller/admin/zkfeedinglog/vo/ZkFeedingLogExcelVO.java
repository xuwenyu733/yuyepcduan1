package cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 投喂记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkFeedingLogExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("系统编号(车间序号_存塘序号)")
    private String pondNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("存塘id")
    private Long pondId;
    private Long workshopId; // 所属车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号

    @ExcelProperty("存塘规模")
    private String pondScale;

    @ExcelProperty("存塘鱼数")
    private Integer fishNum;

    @ExcelProperty("品牌")
    private String feedBrand;
    private String feedName;
    private String productNo; // 商品编号

    @ExcelProperty("规格")
    private String feedSpec;

    @ExcelProperty("数量")
    private BigDecimal feedCount;

    @ExcelProperty("投喂时间")
    private LocalDateTime feedTime;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("类型(1:人工;2:机器;)")
    private String feedType;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("负责人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    private String differenceBefore; // 与前5天对比
    private BigDecimal personScore; // 人对摄食状态评分
    private BigDecimal sumScore; // 综合评分
    private String reviseLog; // 修改值的历史值
    private BigDecimal feedingPrice; // 饲料价格
    private BigDecimal feedingCost; // 投喂成本

    private Long taskDetailId; // 任务明细id
    private Long warehouseId; // 饲料仓库id
    private Long productId; // 饲料商品id
}
