package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent.vo;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailCreateReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 鱼池操作父新增/修改 Request VO")
@Data
public class ZkPondFishParentSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2250")
    private Long id;

    @Schema(description = "原塘id", example = "32738")
    private Long originPondId;
    private String originPondDetailNum;

    @Schema(description = "鱼苗种类")
    private String fishTypes;

    @Schema(description = "鱼苗编号(pl_product)")
    private String productNo;

    @Schema(description = "鱼苗名称", example = "李四")
    private String fishName;

    @Schema(description = "鱼苗数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "鱼苗数量不能为空")
    private Long fishNum;

    @Schema(description = "鱼苗数量差距(正多负少)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "鱼苗数量差距(正多负少)不能为空")
    private Long fishNumDiff;

    @Schema(description = "投放类型(-3:分池;-2:死亡;-1:销售;1:投放;)", example = "2")
    private String addType;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "所属部门", example = "17540")
    private Long deptId;
    private String batchNumber; // 批次编号

    @Schema(description = "分池明细列表")
    private List<ZkPondFishDetailCreateReqVO> details;

}