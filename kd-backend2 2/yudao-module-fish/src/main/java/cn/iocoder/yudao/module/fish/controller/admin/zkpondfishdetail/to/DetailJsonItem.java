package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-03-10
 * @描述
 */
@Data
@NoArgsConstructor
public class DetailJsonItem {

    private String fishType;
    private String fishName;
    private Long fishNum;
    private String productNo; // 鱼苗编号
    private String fishSpec; // 规格

    public DetailJsonItem(String fishType, String fishName, Long fishNum) {
        this.fishType = fishType;
        this.fishName = fishName;
        this.fishNum = fishNum;
    }

    public DetailJsonItem(String fishType, String fishName, Long fishNum, String productNo) {
        this.fishType = fishType;
        this.fishName = fishName;
        this.fishNum = fishNum;
        this.productNo = productNo;
    }

    public DetailJsonItem(String fishType, String fishName, Long fishNum, String productNo, String fishSpec) {
        this.fishType = fishType;
        this.fishName = fishName;
        this.fishNum = fishNum;
        this.productNo = productNo;
        this.fishSpec = fishSpec;
    }
}
