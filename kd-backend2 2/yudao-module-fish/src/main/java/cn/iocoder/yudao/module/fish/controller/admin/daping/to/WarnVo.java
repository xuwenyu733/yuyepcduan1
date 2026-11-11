package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn.ZkPondWarnDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-03-21
 * @描述
 */
@Data
@NoArgsConstructor
public class WarnVo {

    /**
     * 车间id
     */
    private Long workshopId;
    private String carName;
    private String warnName;
    private String handleName;

    public WarnVo(Long workshopId, String carName, String warnName, String handleName) {
        this.workshopId = workshopId;
        this.carName = carName;
        this.warnName = warnName;
        this.handleName = handleName;
    }

    public static List<WarnVo> build_byData(List<ZkPondWarnDO> dataList) {
        if(EasyListUtil.isEmpty(dataList)) return new ArrayList<>();
        List<WarnVo> collect = dataList.stream().map(obj -> new WarnVo(obj.getWorkshopId(), obj.getPondName(), obj.getWarnContent(), obj.getHandleInfo())).collect(Collectors.toList());
        return collect;
    }
}
