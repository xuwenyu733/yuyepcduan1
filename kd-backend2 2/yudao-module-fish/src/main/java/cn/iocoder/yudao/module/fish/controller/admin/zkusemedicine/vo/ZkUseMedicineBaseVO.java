package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 用药记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkUseMedicineBaseVO {

    private String pondNum; // 系统编号(车间序号存塘序号)
    private String pondScale; // 存塘规模
    private String fishNum; // 存塘鱼数
    private Long pondId; // 存塘Id
    private Long workshopId; // 所属车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号
    private String pondName; // 存塘名称
    @Schema(description = "用药名称", example = "赵六")
    private String medicineName;

    @Schema(description = "用药用量")
    private String midicineDosage;

    @Schema(description = "诊断标题")
    private String controlTitle;

    @Schema(description = "诊断原因")
    private String controlContent;

    @Schema(description = "用药结果")
    private String medicineAfter;

    @Schema(description = "状态(0:申请中;1:已批准;2:已使用;3:已评估;-1:已拒绝;)", example = "2")
    private String status;

    @Schema(description = "附件")
    private String fids;

    @Schema(description = "流程实例", example = "25910")
    private String processInstanceId;

    @Schema(description = "用药人", example = "9993")
    private Long userId;
    private String userName;

    @Schema(description = "所属部门", example = "20081")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "17566")
    @NotNull(message = "文件id不能为空")
    private String fid;

    private List<ZkUseMedicineLogDO> useMedicineLogList;
    private LocalDateTime createTime; // 起始时间
    private LocalDateTime stopMedicationTime; // 停药时间
    private String symptomUrls; // 症状(图像)
    private String symptomVideos; // 症状(视频)
    private String waterQuality; // 水质
    private String systemState; // 系统状态
    private String feedBrand; // 饲料品牌
    private String oneBeforeFeed; // 前一天摄食量
    private String twoBeforeFeed; // 前两天摄食量
    private String threeBeforeFeed; // 前三天摄食量
    private Long numberOfLosses; // 损失数量
    private String specifications; // 规格

    private String expertSolution; // 专家方案
    private String actualSolution; // 实际方案

}
