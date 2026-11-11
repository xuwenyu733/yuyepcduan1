package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 用药记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkUseMedicineExcelVO {

    private String pondNum; // 系统编号(车间序号存塘序号)
    private String pondScale; // 存塘规模
    private String fishNum; // 存塘鱼数
    private Long pondId; // 存塘Id
    private Long workshopId; // 所属车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号
    private String pondName; // 存塘名称
    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("用药名称")
    private String medicineName;

    @ExcelProperty("用药用量")
    private String midicineDosage;

    @ExcelProperty("诊断标题")
    private String controlTitle;

    @ExcelProperty("诊断原因")
    private String controlContent;

    @ExcelProperty("用药结果")
    private String medicineAfter;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("附件")
    private String fids;

    @ExcelProperty("流程实例")
    private String processInstanceId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("用药人")
    private Long userId;
    private String userName;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
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
