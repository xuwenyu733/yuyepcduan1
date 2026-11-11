package cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJsonItem;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 存塘更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPondUpdateReqVO extends ZkPondBaseVO {

    @Schema(description = "编号", required = true, example = "26613")
    @NotNull(message = "编号不能为空")
    private Long id;

    // zk_pond_fish 数量信息
    private List<DetailJsonItem> detailJson;
    private String deviceType = "pc";

    private Long originData; // 原数据
    private Long differenceData; // 差值
    private BigDecimal changeRatio; // 变化比例
    private String reason; // 原因

    public List<ZkPondFishDetailDO> selectDetails() {
        List<ZkPondFishDetailDO> list = new ArrayList<>();
        for(DetailJsonItem item : detailJson) {
            // Long zkPondFishId, Long workshopId, String workshopName, String pondName, String pondNum, Long pondId, String fishTypes,
            // String productNo, String fishName, Long fishNum, String addType, Long userId, String userName, Long deptId, String pondScale
            ZkPondFishDetailDO detailDO = new ZkPondFishDetailDO(0l, getWorkshopId(), getWorkshopName(), getPondName(), getPondNum(), getId(), item.getFishType(),
                    item.getProductNo(), item.getFishName(), item.getFishNum(), "1", getUserId(), getUserName(), getDeptId(), getPondScale());
            list.add(detailDO);
        }
        return list;
    }

}
