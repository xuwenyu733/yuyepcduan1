package cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo;

import lombok.Data;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;

@Data
public class ZkEnergyMeterReadingPageReqVO extends PageParam {
    private String deviceId;
    private String deviceType;
    private String workshopName;
    private LocalDateTime[] readingTime;
}
