package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-08-14
 * @描述
 */
@Getter
@AllArgsConstructor
public enum KnowledgeNodeTypeEnum {

    DESCRIBE("-1", "描述"),
    // 疾病智能体 50 以下
    BODY("1", "部位"),
    SYMPTOM("2", "症状"),
    REASON("3", "原因和疾病"),
    THERAPY("4", "治疗方法"),
    // 常见智能体 >50 <100
    USE_ROOT("51", "常见-基础表象根"),
    USE_FACTOR("52", "常见-关联因素(鱼、水、设备、人)"),
    USE_MAYBE("53", "常见-可能原因"),
    USE_SURE_MAYBE("54", "常见-确诊原因"),
    USE_HANDLE("55", "常见-解决思路"),

    ;

    private final String typeValue;
    private final String typeName;

    public Integer selectType() {
        int intType = Integer.parseInt(this.getTypeValue());
        return intType;
    }
}
