package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailRespVO;

@Schema(description = "管理后台 - 鱼池操作父 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkPondFishParentRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2250")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "原塘id", example = "32738")
    @ExcelProperty("原塘id")
    private Long originPondId;
    private String originPondDetailNum;

    @Schema(description = "鱼苗种类")
    @ExcelProperty("鱼苗种类")
    private String fishTypes;

    @Schema(description = "鱼苗编号(pl_product)")
    @ExcelProperty("鱼苗编号(pl_product)")
    private String productNo;

    @Schema(description = "鱼苗名称", example = "李四")
    @ExcelProperty("鱼苗名称")
    private String fishName;

    @Schema(description = "鱼苗数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗数量差距(正多负少)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("鱼苗数量差距(正多负少)")
    private Long fishNumDiff;

    @Schema(description = "投放类型(-3:分池;-2:死亡;-1:销售;1:投放;)", example = "2")
    @ExcelProperty("投放类型(-3:分池;-2:死亡;-1:销售;1:投放;)")
    private String addType;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "17540")
    @ExcelProperty("所属部门")
    private Long deptId;
    private String batchNumber; // 批次编号

    @Schema(description = "分池明细列表")
    private List<ZkPondFishDetailRespVO> details;

}