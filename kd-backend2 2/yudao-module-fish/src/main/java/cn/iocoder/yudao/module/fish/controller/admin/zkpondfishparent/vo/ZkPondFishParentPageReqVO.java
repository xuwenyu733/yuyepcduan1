package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 鱼池操作父分页 Request VO")
@Data
public class ZkPondFishParentPageReqVO extends PageParam {

    @Schema(description = "原塘id", example = "32738")
    private Long originPondId;
    private String originPondDetailNum;

    @Schema(description = "鱼苗种类")
    private String fishTypes;

    @Schema(description = "鱼苗编号(pl_product)")
    private String productNo;

    @Schema(description = "鱼苗名称", example = "李四")
    private String fishName;

    @Schema(description = "鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗数量差距(正多负少)")
    private Long fishNumDiff;

    @Schema(description = "投放类型(-3:分池;-2:死亡;-1:销售;1:投放;)", example = "2")
    private String addType;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "17540")
    private Long deptId;
    private String batchNumber; // 批次编号

}