package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo.ZkFishOperationLogRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
// import cn.iocoder.yudao.module.fish.dal.dataobject.outorderproduct.OutOrderProductDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
// import cn.iocoder.yudao.module.fish.service.outorderproduct.OutOrderProductService;
import cn.iocoder.yudao.module.fish.service.custom.OutOrderProductService;
import cn.iocoder.yudao.module.fish.service.zkfishoperationlog.ZkFishOperationLogService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-07-17
 * @描述
 * TODO 框架升级待完成
 */
@Component
public class OperationLogEnhancer {

    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;
    @Resource
    private ZkPondService zkPondService;
    @Resource
    private ZkPondFishService zkPondFishService;
    @Resource
    private ZkFishOperationLogService zkFishOperationLogService;
    @Resource
    private OutOrderProductService outOrderProductService;

    /**
     * 给 统计结果填充 值
     * @param res
     */
    public void enhancer(List<ZkFishOperationLogDO> res) {
        if(EasyListUtil.isEmpty(res)) return;
        // 1. 按照 批次、车间、系统、存塘 进行分组
        Map<String, List<ZkFishOperationLogDO>> type2list = res.stream().collect(Collectors.groupingBy(obj -> obj.getLogType()));
        // 2. 分组后 根据不用类型填充  投放量、存量
        for(String logType : type2list.keySet()) {
            List<ZkFishOperationLogDO> tempList = type2list.get(logType);

            if(StringUtils.equals(FishOperationLogTypeEnum.BATCH.getLogType(), logType)) {
                batchEnhancer(tempList);
            } else if(StringUtils.equals(FishOperationLogTypeEnum.WORKSHOP.getLogType(), logType)) {
                workshopEnhancer(tempList);
            } else if(StringUtils.equals(FishOperationLogTypeEnum.SYSTEM.getLogType(), logType)) {
                systemEnhancer(tempList);
            } else if(StringUtils.equals(FishOperationLogTypeEnum.POND.getLogType(), logType)) {
                pondEnhancer(tempList);
            }
        }
    }

    /**
     * 针对批次 需要 投放量去 erp_purchase_in_items.relation_order_no , 批次存量 就是 投放量-累计死亡量
     * @param list
     */
    public void batchEnhancer(List<ZkFishOperationLogDO> list) {
        // 1. 获取 批次 投放信息
        if(EasyListUtil.isEmpty(list)) return;
        Set<String> batchNumberSet = list.stream().map(obj -> obj.getBatchNumber()).collect(Collectors.toSet());
        List<ErpPurchaseInItemDO> productList = outOrderProductService.getListByRelationOrderNoSet(batchNumberSet);
        if(EasyListUtil.isEmpty(productList)) return;
        productList.forEach(obj -> obj.build_consumeCount());
        // 2. 同步 投放量 到 批次记录中
        for(ErpPurchaseInItemDO product : productList) {
            for(ZkFishOperationLogDO curr : list) {
                if(StringUtils.equals(curr.getBatchNumber(), product.getRelationOrderNo())) {
                    curr.setPushFishNum(product.getConsumeCount().longValue());
                    curr.build_currFishNum();
                }
            }
        }
    }

    /**
     * 针对车间 需要 投放量去 zk_pond_fish_detail , 批次存量 就是 投放量-累计死亡量
     * @param list
     */
    public void workshopEnhancer(List<ZkFishOperationLogDO> list) {
        // 1. 获取 批次 投放信息
        if(EasyListUtil.isEmpty(list)) return;
        Set<Long> batchNumberSet = list.stream().map(obj -> obj.getWorkshopId()).collect(Collectors.toSet());
        List<ZkPondFishDetailDO> detailList = zkPondFishDetailService.countByWorkshopIdSet(PondFishDetailAddTypeEnum.ADD.getAddType(), batchNumberSet);
        if(EasyListUtil.isEmpty(detailList)) return;
        // 2. 同步 投放量 到 批次记录中
        for(ZkPondFishDetailDO remote : detailList) {
            for(ZkFishOperationLogDO curr : list) {
                if(Objects.equals(curr.getWorkshopId(), remote.getWorkshopId())) {
                    curr.setPushFishNum(remote.getFishNum());
                    curr.build_currFishNum();
                }
            }
        }
    }
    /**
     * 针对系统 需要 投放量去 zk_pond_fish_detail , 批次存量 就是 投放量-累计死亡量
     * @param list
     */
    public void systemEnhancer(List<ZkFishOperationLogDO> list) {
        // 1. 获取 批次 投放信息
        if(EasyListUtil.isEmpty(list)) return;
        Set<String> idSet = list.stream().map(obj -> obj.getPondNum()).collect(Collectors.toSet());
        List<ZkPondFishDetailDO> detailList = zkPondFishDetailService.countByPondNumSet(PondFishDetailAddTypeEnum.ADD.getAddType(), idSet);
        if(EasyListUtil.isEmpty(detailList)) return;
        // 2. 同步 投放量 到 批次记录中
        for(ZkPondFishDetailDO remote : detailList) {
            for(ZkFishOperationLogDO curr : list) {
                if(Objects.equals(curr.getPondNum(), remote.getPondNum())) {
                    curr.setPushFishNum(remote.getFishNum());
                    curr.build_currFishNum();
                }
            }
        }
    }
    /**
     * 针对 存塘 需要 投放量去 zk_pond_fish_detail , 批次存量 就是 投放量-累计死亡量
     * @param list
     */
    public void pondEnhancer(List<ZkFishOperationLogDO> list) {
        // 1. 获取 存塘 投放信息
        if(EasyListUtil.isEmpty(list)) return;
        Set<Long> batchNumberSet = list.stream().filter(obj -> obj.getPondId()!=null).map(obj -> obj.getPondId()).collect(Collectors.toSet());
        List<ZkPondFishDetailDO> detailList = zkPondFishDetailService.countByPondIdSet(PondFishDetailAddTypeEnum.ADD.getAddType(), batchNumberSet);
        if(EasyListUtil.isEmpty(detailList)) return;
        detailList = detailList.stream().filter(obj -> obj != null).collect(Collectors.toList());
        if(EasyListUtil.isEmpty(detailList)) return;
        // 2. 同步 投放量 到 批次记录中
        for(ZkFishOperationLogDO curr : list) {
            for(ZkPondFishDetailDO remote : detailList) {
                if((remote != null || curr.getPondId()!=null || remote.getPondId()!=null) && Objects.equals(curr.getPondId(), remote.getPondId())) {
                    curr.setPushFishNum(remote.getFishNum());
                    curr.build_currFishNum();
                }
            }
        }
    }

    /**
     * 填充累计死亡量
     * @param res
     */
    public void enhancer_accumulate(List<ZkFishOperationLogDO> res) {
        // if(isFirst) return;
        // 1. 获取上一天的记录
        List<ZkPondFishDO> localList = zkPondFishService.getList();
        if(EasyListUtil.isEmpty(localList)) return;
        // 2. 找到 相同 类型的，然后加上历史值
        for(ZkFishOperationLogDO item : res) {
            ZkPondFishDO local = getByList(localList, item);
            if(local == null) {
                // 没有的可能性小，暂不考虑 ==》 直接抛出异常
                String subject = String.format("【统计FishOperationLog】,middle,填充当前鱼数量通过zk_pond_fish，但是没找到，pondId:%s", item.getPondId());
                String content = String.format("具体zk_fish_operation_log数据：%s,整体数据：%s", JSON.toJSONString(item), JSON.toJSONString(res));
                EmailUtil.send(subject, content);
            } else {
                item.setAccumulateFishNum(local.getDeadFishNum());
                item.setPushFishNum(local.getPushFishNum());
                item.setCurrFishNum(local.getFishNum());
            }
        }
        // 3. 执行 当日死亡率、 累计死亡率
        calculateRate(res);
    }

    public void calculateRate(List<ZkFishOperationLogDO> res) {
        res.forEach(ZkFishOperationLogDO::build_rate);
    }

    public ZkPondFishDO getByList(List<ZkPondFishDO> localList, ZkFishOperationLogDO curr) {
        if(StringUtils.equals(FishOperationLogTypeEnum.BATCH.getLogType(), curr.getLogType())) {
            for(ZkPondFishDO local : localList) {
                if(StringUtils.equals(local.getBatchNumber(), curr.getBatchNumber())) {
                    return local;
                }
            }
        } else if(StringUtils.equals(FishOperationLogTypeEnum.WORKSHOP.getLogType(), curr.getLogType())) {
            Map<Long, ZkPondFishDO> map = groupByWorkshop(localList);
            for(Long workshopId : map.keySet()) {
                if(Objects.equals(workshopId, curr.getWorkshopId())) {
                    return map.get(workshopId);
                }
            }
        } else if(StringUtils.equals(FishOperationLogTypeEnum.SYSTEM.getLogType(), curr.getLogType())) {
            Map<String, ZkPondFishDO> map = groupBySystem(localList);
            for(String pondNum : map.keySet()) {
                if(StringUtils.equals(pondNum, curr.getPondNum())) {
                    return map.get(pondNum);
                }
            }
        } else if(StringUtils.equals(FishOperationLogTypeEnum.POND.getLogType(), curr.getLogType())) {
            for(ZkPondFishDO local : localList) {
                if(Objects.equals(local.getPondId(), curr.getPondId())) {
                    return local;
                }
            }
        }
        return null;
    }
    public Map<Long, ZkPondFishDO> groupByWorkshop(List<ZkPondFishDO> localList) {
        Map<Long, ZkPondFishDO> res = new HashMap<>();
        Map<Long, List<ZkPondFishDO>> collect = localList.stream().filter(obj -> obj.getWorkshopId()!=null).collect(Collectors.groupingBy(obj -> obj.getWorkshopId()));
        for(Long workshopId : collect.keySet()) {
            List<ZkPondFishDO> zkPondFishDOS = collect.get(workshopId);
            ZkPondFishDO tempFish = sumList(zkPondFishDOS);
            res.put(workshopId, tempFish);
        }
        return res;
    }

    public Map<String, ZkPondFishDO> groupBySystem(List<ZkPondFishDO> localList) {
        Map<String, ZkPondFishDO> res = new HashMap<>();
        Map<String, List<ZkPondFishDO>> collect = localList.stream().filter(obj -> obj.getPondNum()!=null).collect(Collectors.groupingBy(obj -> obj.getPondNum()));
        for(String pondNum : collect.keySet()) {
            List<ZkPondFishDO> zkPondFishDOS = collect.get(pondNum);
            ZkPondFishDO tempFish = sumList(zkPondFishDOS);
            res.put(pondNum, tempFish);
        }
        return res;
    }

    public ZkPondFishDO sumList(List<ZkPondFishDO> localList) {
        ZkPondFishDO first = localList.get(0);
        // String workshopName, Long workshopId, String pondNum, String pondSystemName
        ZkPondFishDO zkPondFishDO = new ZkPondFishDO(first.getWorkshopName(), first.getWorkshopId(), first.getPondNum(), first.getPondSystemName());
        for(ZkPondFishDO item : localList) {
            zkPondFishDO.build_count(item.getFishNum(), item.getPushFishNum(), item.getSplitAddNum(), item.getDeadFishNum(), item.getSaleFishNum());
        }
        return zkPondFishDO;
    }
}
