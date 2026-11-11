package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "饲料消耗汇总")
@Data
public class PrecisionFeedConsumptionRespVO {

    private BigDecimal totalFeedKg;
    private Integer pondCount;
    private List<Item> items;

    @Data
    public static class Item {
        private Long workshopId;
        private String workshopName;
        private Long pondSystemId;
        private String pondSystemName;
        private Long pondId;
        private String pondName;
        private String feedName;
        private String feedSpec;
        /** 与 feedName 一致，供前端 feed-consumption 表格字段 feedType */
        private String feedType;
        /** 与 feedSpec 一致，供前端字段 feedModel */
        private String feedModel;
        private BigDecimal totalKg;
    }
}
