package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用药记录 Excel 导出 Request VO，参数和 ZkUseMedicinePageReqVO 是一致的")
@Data
public class ZkUseMedicineExportReqVO {

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

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "附件")
    private String fids;

    @Schema(description = "流程实例", example = "25910")
    private String processInstanceId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "用药人", example = "9993")
    private Long userId;
    private String userName;

    @Schema(description = "所属部门", example = "20081")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "17566")
    private String fid;

    private List<ZkUseMedicineLogDO> useMedicineLogList;
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
