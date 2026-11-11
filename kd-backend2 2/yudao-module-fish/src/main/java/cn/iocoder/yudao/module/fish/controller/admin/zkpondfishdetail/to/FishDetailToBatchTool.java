package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * zk_pond_fish_detail 投放、分池、死亡、销售 信息 需要同步 到 zk_pond_fish 中批次类型
 * @创建人 lr
 * @创建时间 2025-07-18
 * @描述
 */
@Component
public class FishDetailToBatchTool {
    @Resource
    private ZkPondFishService zkPondFishService;

    public void syncDataToBatchNumber(ZkPondFishDetailDO zkPondFishDetail) {
        // 1. 获取当前批次信息
        String batchNumber = zkPondFishDetail.getBatchNumber();
        ZkPondFishDO zkPondFishDO = zkPondFishService.getByBatchNumber(batchNumber);
        if(zkPondFishDO == null) {
            // 为空则需要添加
            zkPondFishDO = getDoForAdd(zkPondFishDetail);
            zkPondFishService.customInsert(zkPondFishDO);
        } else {
            // 不为空，直接更新就好了
             getDoForUpdate(zkPondFishDetail, zkPondFishDO);
            zkPondFishService.customUpdate(zkPondFishDO);
        }

    }
    public ZkPondFishDO getDoForUpdate(ZkPondFishDetailDO zkPondFishDetail, ZkPondFishDO zkPondFishDO) {
        baseSet(zkPondFishDetail, zkPondFishDO);
        return zkPondFishDO;
    }

    public ZkPondFishDO getDoForAdd(ZkPondFishDetailDO zkPondFishDetail) {
        ZkPondFishDO zkPondFishDO = new ZkPondFishDO(0l, 0l, zkPondFishDetail.getBatchNumber());
        baseSet(zkPondFishDetail, zkPondFishDO);
        return zkPondFishDO;
    }

    public void baseSet(ZkPondFishDetailDO zkPondFishDetail, ZkPondFishDO zkPondFishDO) {
        if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), zkPondFishDetail.getAddType())) {
            zkPondFishDO.build_pushFishNum(zkPondFishDetail.getFishNum());
            zkPondFishDO.build_fishNum(zkPondFishDetail.getFishNum());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), zkPondFishDetail.getAddType())) {
            zkPondFishDO.build_deadFishNum(zkPondFishDetail.getFishNum());
            zkPondFishDO.build_fishNum(-zkPondFishDetail.getFishNum());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), zkPondFishDetail.getAddType())) {
            zkPondFishDO.build_saleFishNum(zkPondFishDetail.getFishNum());
            zkPondFishDO.build_fishNum(-zkPondFishDetail.getFishNum());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), zkPondFishDetail.getAddType())) {
            zkPondFishDO.build_splitReduceNum(zkPondFishDetail.getFishNum());
            zkPondFishDO.setSplitReduceNum(zkPondFishDetail.getFishNum());
        }
        zkPondFishDO.build_specInfo(zkPondFishDetail.getFishSpec());
    }

}
