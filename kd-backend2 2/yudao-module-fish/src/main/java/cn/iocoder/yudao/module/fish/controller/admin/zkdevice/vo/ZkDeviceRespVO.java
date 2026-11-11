package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Schema(description = "管理后台 - 设备 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceRespVO extends ZkDeviceBaseVO {

    @Schema(description = "编号", required = true, example = "7101")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
    private List<String> dateList;

    private DeviceEnvInfoVo deviceEnvInfoVo; // 设备环境信息

    public ZkDeviceRespVO buildDateList() {
        List<String> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for(int i=12;i>=0;i--) {
            Date temp = EasyDateUtil.plusMinutes(now, -i);
            list.add(EasyDateUtil.format(temp, "HH:mm"));
        }
        list.add(EasyDateUtil.format(now, "HH:mm"));
        this.setDateList(list);
        return this;
    }
}
