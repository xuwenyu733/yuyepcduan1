package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-08-14
 * @描述
 */
@Data
@NoArgsConstructor
public class KnowLedgeSummaryVo {

    private String bodyInfo; // 部位
    private String symptomInfo; // 症状
    private String illInfo; // 原因+疾病名称

    private String appearanceInfo; // 鱼根表现

    public KnowLedgeSummaryVo(String bodyInfo, String symptomInfo, String illInfo) {
        this.bodyInfo = bodyInfo;
        this.symptomInfo = symptomInfo;
        this.illInfo = illInfo;
    }
    public KnowLedgeSummaryVo build_appearanceInfo(String appearanceInfo) {
        this.appearanceInfo = appearanceInfo;
        return this;
    }
}
