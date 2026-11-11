package cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo;

import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.to.DataReviseTypeEnum;
import cn.iocoder.yudao.module.workflow.api.dto.TaskParam;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 数据修正记录创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDataReviseCreateReqVO extends ZkDataReviseBaseVO {

    TaskParam taskParam;


    public ZkDataReviseCreateReqVO build_reviseTypeName() {
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
