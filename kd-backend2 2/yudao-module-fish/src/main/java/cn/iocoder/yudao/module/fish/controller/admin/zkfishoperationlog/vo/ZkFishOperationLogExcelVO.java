package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 鱼苗统计 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkFishOperationLogExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("鱼苗批次")
    private String batchNumber;

    @ExcelProperty("所属车间id")
    private Long workshopId;

    @ExcelProperty("所属系统id")
    private Long pondSystemId;

    @ExcelProperty("存塘系统编号")
    private String pondNum;

    @ExcelProperty("所属存塘id")
    private Long pondId;
    private String pondDetailNum; // 存塘编号(区别于pond_num系统编号)

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("存塘系统名称")
    private String pondSystemName;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("统计时间(yyyy-MM-dd)")
    private String logDay;

    @ExcelProperty("鱼苗编码(pl_product.product_no)")
    private String productNo;

    @ExcelProperty("鱼苗种类")
    private String fishTypes;

    @ExcelProperty("鱼苗规格")
    private String fishSpec;

    @ExcelProperty("操作数量")
    private Long dayFishNum;

    @ExcelProperty("累计操作数量")
    private Long accumulateFishNum;

    @ExcelProperty("投放数量")
    private Long pushFishNum;
    private Long currFishNum = 0L; // 当前存量

    @ExcelProperty("当日操作率")
    private BigDecimal dayRate;

    @ExcelProperty("累计操作率")
    private BigDecimal accumulateRate;

    @ExcelProperty("成活率")
    private BigDecimal survivalRate;

    @ExcelProperty("修正后操作数量")
    private Long newDayFishNum;

    @ExcelProperty("修正后累计操作数量")
    private Long newAccumulateFishNum;

    @ExcelProperty("修正后投放数量")
    private Long newPushFishNum;
    private Long newCurrFishNum = 0L; // 修正后当前存量

    @ExcelProperty("修正后当日操作率")
    private BigDecimal newDayRate;

    @ExcelProperty("修正后累计操作率")
    private BigDecimal newAccumulateRate;

    @ExcelProperty("修正后成活率")
    private BigDecimal newSurvivalRate;

    @ExcelProperty("修正依据")
    private String correctionBasis;

    @ExcelProperty("差值率")
    private BigDecimal differencesRate;

    @ExcelProperty("差异原因")
    private String reasonsForDifferences;

    @ExcelProperty("具体描述")
    private String detailedDescription;

    @ExcelProperty("危险评级(1:正常;2:持续关注;3:立刻介入;)")
    private String dangerRating;

    @ExcelProperty("统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)")
    private String logType;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("评级人id")
    private Long userId;

    @ExcelProperty("评级人")
    private String userName;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

    private BigDecimal currFishWeight; // 当前存量重量(Kg)
    private BigDecimal accumulateFishWeight; // 累计存量重量(Kg)
    private BigDecimal pondScale; // 存塘规模
    private BigDecimal fishDensity; // 密度(Kg/平方米)

    private BigDecimal currFeedCount; // 当日投喂量(Kg)
    private BigDecimal accumulateFeedCount; // 累计投喂量(Kg)
    private BigDecimal acturalRadio; // 实际饵料系数
    private BigDecimal currCost; // 当前成本
    private BigDecimal biologyCost; // 生物资产
    private BigDecimal averageInputCost; // 当前平均成本(元/尾)
    private BigDecimal averageBiologicalAssets; // 平均生物资产(元/尾)

    @TableField(exist = false)
    private List<String> batchNumberList; // 批次编号
    private BigDecimal fishPrice = new BigDecimal(0); // 鱼苗价格

}
