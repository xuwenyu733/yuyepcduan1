package cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo;

import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.to.DataReviseTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 数据修正记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkDataReviseBaseVO {

    @Schema(description = "车间id", example = "854")
    private Long workshopId;

    @Schema(description = "车间名称", example = "芋艿")
    private String workshopName;

    @Schema(description = "系统名称", example = "赵六")
    private String pondSystemName;

    @Schema(description = "系统id", example = "21670")
    private Long pondSystemId;

    @Schema(description = "系统编号")
    private String pondNum;

    @Schema(description = "存塘名称", example = "王五")
    private String pondName;

    @Schema(description = "存塘id", example = "8247")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)", required = true, example = "1")
    @NotNull(message = "操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)不能为空")
    private String reviseType;
    private String reviseTypeName; // 操作类型名称

    @Schema(description = "原数据", required = true)
    @NotNull(message = "原数据不能为空")
    private String originData;

    @Schema(description = "差值", required = true)
    @NotNull(message = "差值不能为空")
    private String differenceData;

    @Schema(description = "变化比例", required = true)
    @NotNull(message = "变化比例不能为空")
    private String changeRatio;

    @Schema(description = "原因", required = true, example = "不香")
    @NotNull(message = "原因不能为空")
    private String reason;

    @Schema(description = "技术总监意见", required = true)
    // @NotNull(message = "技术总监意见不能为空")
    private String technicalDirectorsOpinion;

    @Schema(description = "厂长意见", required = true)
    // @NotNull(message = "厂长意见不能为空")
    private String directorsOpinion;

    @Schema(description = "业务id", example = "2197")
    private Long busId;

    @Schema(description = "记录人", example = "9777")
    private Long userId;

    @Schema(description = "所属部门", example = "1813")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "6872")
    // @NotNull(message = "文件id不能为空")
    private String fid;

    private ZkPondFishDetailDO fishPush; // 修正 投放
    private ZkPondFishDetailDO fishDead; // 修正 死亡
    private ZkPondFishDetailDO fishSplit; // 修正 分池
    private ZkFeedingLogDO feedingLog; // 修正 投喂
    private ZkPondDO pondData; // 修正 存塘

    private String status; // 状态(0:已申请;1:已批准;2:技术总监已审批;3:厂长已审批;4:已完成;-1:已拒绝;)
    private String processInstanceId; // 流程实例
}
