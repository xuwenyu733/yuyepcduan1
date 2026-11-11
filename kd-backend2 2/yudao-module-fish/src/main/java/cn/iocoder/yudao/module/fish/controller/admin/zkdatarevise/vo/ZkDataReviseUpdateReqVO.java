package cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo;

import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.to.DataReviseTypeEnum;
import cn.iocoder.yudao.module.workflow.api.dto.TaskParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 数据修正记录更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDataReviseUpdateReqVO extends ZkDataReviseBaseVO {

    @Schema(description = "编号", required = true, example = "10093")
    @NotNull(message = "编号不能为空")
    private Long id;

    private TaskParam taskParam;

    private String action;

    public ZkDataReviseUpdateReqVO build_reviseTypeName() {
        DataReviseTypeEnum[] values = DataReviseTypeEnum.values();
        for(DataReviseTypeEnum item : values) {
            if(StringUtils.equals(item.getReviseType(), this.getReviseType())) {
                this.setReviseTypeName(item.getName());
                break;
            }
        }
        return this;
    }
}
