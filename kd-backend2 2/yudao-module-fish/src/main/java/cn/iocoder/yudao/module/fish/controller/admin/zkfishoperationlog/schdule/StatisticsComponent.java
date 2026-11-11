package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.schdule;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.ZkFishOperationLogController;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.FishStatisticsTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.enums.LogRecordConstants;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-26
 * @描述
 */
@Slf4j
@Component
public class StatisticsComponent {
    @Resource
    private ZkPondService zkPondService;
    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;

    public List<ZkPondFishDetailDO> startStatistics(StatisticsParam param) {
        // 1. 获取存塘列表
        List<ZkPondDO> zkPondDOS = selectPondList(param);
        if(EasyListUtil.isEmpty(zkPondDOS)) return null;
        Set<Long> pondIdSet = zkPondDOS.stream().map(ZkPondDO::getId).collect(Collectors.toSet());
        // 2. 获取 zk_pond_fish_detail 数据
        List<ZkPondFishDetailDO> detailList = zkPondFishDetailService.getListByPondIdsAndStatisticsType(pondIdSet, PondFishDetailAddTypeEnum.DEAD.getAddType(),
                FishStatisticsTypeEnum.NOT_STATISTICS.getStatisticsType());
        if(EasyListUtil.isEmpty(detailList)) return null;
        // 3. 如果 有需要统计的 则获取当天所有的信息 进行统计
        Set<String> createStrSet = detailList.stream().map(ZkPondFishDetailDO::build_createStr).map(ZkPondFishDetailDO::getCreateStr).collect(Collectors.toSet());
        List<ZkPondFishDetailDO> allDayList = zkPondFishDetailService.selectByCreateStrSet(createStrSet, PondFishDetailAddTypeEnum.DEAD.getAddType());
        log.warn("【{}】middle 1, 生成的源参数是：{}", LogRecordConstants.GEN_FISH_OPERATION_LOG, JSON.toJSONString(allDayList));
        return allDayList;
    }

    /**
     * 先获取存塘列表，方便后续传入 deptId或者tenantId，区分不同基地
     * 参数先不填，后续 区分只需要改这一个方法，后续方法全部不需要动
     * @return
     */
    public List<ZkPondDO> selectPondList(StatisticsParam param) {
        List<ZkPondDO> list = zkPondService.selectList();
        return list;
    }


}
