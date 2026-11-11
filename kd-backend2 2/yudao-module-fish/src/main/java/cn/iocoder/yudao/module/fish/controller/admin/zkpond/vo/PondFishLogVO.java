package cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJson;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJsonItem;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Data
public class PondFishLogVO {
    private String summary; // 总的描述信息
    private List<String> fishTypeNums; // 鱼品种及数量
    private List<String> fishLogs; // 鱼投放记录
    private List<DetailJsonItem> detailJson; // 鱼苗详细信息

    public PondFishLogVO build(ZkPondDO zkPond, ZkPondFishDO byPondId, List<ZkPondFishDetailDO> detailDOS) {
        // 1. 统计摘要
        summary = String.format("当前存塘(%s-%s)总鱼数: %s尾，存塘规格: %sm³", zkPond.getPondDetailNum(), zkPond.getPondName(), zkPond.getFishNum(), zkPond.getPondScale());
        // 2. 统计 鱼品种及数量
        build_fishTypeNums(byPondId);
        // 3. 统计 投放历史
        build_fishLogs(detailDOS);
        return this;
    }

    public void build_fishLogs(List<ZkPondFishDetailDO> detailDOS) {
        if(EasyListUtil.isEmpty(detailDOS)) return;
        List<String> collect = detailDOS.stream().map(obj -> {
            return obj.build_operationInfo();
        }).collect(Collectors.toList());
        fishLogs = collect;
    }

    public void build_fishTypeNums(ZkPondFishDO byPondId) {
        if(byPondId != null) {
            ZkPondFishDO zkPondFishDO = byPondId.build_flushInfo();
            String flushInfo = zkPondFishDO.getFlushInfo();
            if(StringUtils.isBlank(flushInfo)) return;
            String[] split = flushInfo.split(";");
            fishTypeNums = Arrays.asList(split);
            DetailJson detailJson = JSON.parseObject(byPondId.getDetailJson(), DetailJson.class);
            this.setDetailJson(detailJson.getDetailJson());
        }
    }
}
