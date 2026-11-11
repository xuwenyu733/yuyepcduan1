package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

@Data
public class PondHealthStatusVO {

    private Long pondId;
    private String pondName;
    private String healthStatus;

}
