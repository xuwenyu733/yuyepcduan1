package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.tools.FishSystemLogTool;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.ZkFeedingLogController;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.ZkFeedingLogCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.ZkFeedingLogUpdateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.ZkFishOperationLogController;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.ZkPondFishDetailController;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.zkfeedinglog.ZkFeedingLogService;
import cn.iocoder.yudao.module.fish.service.zkfishproofing.ZkFishProofingService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-08-06
 * @描述
 */
@Component
@Slf4j
public class TaskDetailHandler {

    @Autowired
    @Lazy
    private TaskDetailService taskDetailService;

    @Autowired
    @Lazy
    private ZkFishProofingService zkFishProofingService;
    @Autowired
    @Lazy
    private ZkPondFishService zkPondFishService;

    @Autowired
    @Lazy
    private ZkPondService zkPondService;

    @Resource
    private TaskDetail2Feed taskDetail2Feed;

    @Resource
    private ZkFeedingLogService zkFeedingLogService;

    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;

    @Autowired
    @Lazy
    private ZkFeedingLogController zkFeedingLogController;

    @Autowired
    @Lazy
    private ZkFishOperationLogController zkFishOperationLogController;

    @Resource
    private TaskDetail2Dead taskDetail2Dead;

    @Resource
    private ZkPondFishDetailController zkPondFishDetailController;

    /**
     * 巡检数据需要收集统计
     * @param taskDetailDOS 巡检明细
     */
    public void syncData(List<TaskDetailDO> taskDetailDOS) {
        if(EasyListUtil.isEmpty(taskDetailDOS)) return;
        // 一、 同步 处理投喂 信息
        syncData4Feed(taskDetailDOS);
        // 二、 同步 处理死亡 信息
        syncData4Dead(taskDetailDOS);
        // System.out.println(String.format("taskDetailDOS:===============>%s", JSON.toJSONString(taskDetailDOS)));
        // 三、 同步 打样 信息
        // 1. 如果有 打样记录 则需要数据同步 zk_fish_proofing
        List<TaskDetailDO> proofingList = getList(taskDetailDOS, TaskDetailCheckTypeEnum.REGULAR_AREA1_3.getValue());
        List<ZkFishProofingDO> detailDOS = proofingListHandler(proofingList);
        if(EasyListUtil.isEmpty(detailDOS)) return;
        log.warn("【巡检数据需要收集统计 打样】middle,生成的打样信息:{},巡检信息:{}，成功入库", JSON.toJSONString(detailDOS), JSON.toJSONString(taskDetailDOS));
        // 2. 同步数据到 zk_pond_fish
        syncToPondFish(detailDOS);
        // 同步操作日志，方便追踪问题，后续可注释
        String pondDetailNum = taskDetailDOS.get(0).getPondDetailNum();
        FishSystemLogTool.gen(new FishSystemLogDO(pondDetailNum, "巡检数据需要收集统计 打样", JSON.toJSONString(proofingList), null, null, null)
                .setRemark("打样"));

        // 3. 操作完成 以后 同步 统计日志
        zkFishOperationLogController.genLog();
    }

    /**
     * 巡检数据需要收集统计 投喂
     * @param taskDetailDOS 巡检明细
     */
    public void syncData4Feed(List<TaskDetailDO> taskDetailDOS) {
        // 1. 如果有 投喂记录 则需要数据同步 zk_feeding_log
        List<TaskDetailDO> feedDetailList = getList(taskDetailDOS, TaskDetailCheckTypeEnum.DAY_AREA1_FEED_TW.getValue());
        List<ZkFeedingLogDO> detailDOS = feedListHandler(feedDetailList);
        log.warn("【巡检数据需要收集统计 投喂】middle,生成的投喂信息:{},巡检信息:{}，成功入库", JSON.toJSONString(detailDOS), JSON.toJSONString(taskDetailDOS));
    }

    /**
     * 巡检数据需要收集统计 死鱼
     * @param taskDetailDOS 巡检明细
     */
    public void syncData4Dead(List<TaskDetailDO> taskDetailDOS) {
        // 1. 如果有 死亡记录 则需要数据同步 zk_pond_fish_detail
        List<TaskDetailDO> deadDetailList = getList(taskDetailDOS, TaskDetailCheckTypeEnum.DAY_AREA1_DEAD_SW.getValue());
        List<ZkPondFishDetailDO> detailDOS = deadListHandler(deadDetailList);
        log.warn("【巡检数据需要收集统计 死亡】middle,生成的死亡信息:{},巡检信息:{}，成功入库", JSON.toJSONString(detailDOS), JSON.toJSONString(taskDetailDOS));
    }

    public Integer syncToPondFish(List<ZkFishProofingDO> detailDOS) {
        if(EasyListUtil.isEmpty(detailDOS)) return 0;
        // 1. 获取打样涉及 鱼池
        Set<Long> pondIdSet = detailDOS.stream().map(ZkFishProofingDO::getPondId).collect(Collectors.toSet());
        List<ZkPondFishDO> pondFishList = zkPondFishService.selectByPondIdSet(pondIdSet);
        if(EasyListUtil.isEmpty(pondFishList)) return 0;
        // 2. 开始将 打样的 鱼苗规格 传输到 zk_pond_fish 中
        List<ZkPondFishDO> needUpdate = new ArrayList<>();
        for(ZkFishProofingDO item : detailDOS) {
            ZkPondFishDO currFish = null;
            for(ZkPondFishDO fishItem : pondFishList) {
                if(Objects.equals(item.getPondId(), fishItem.getPondId())) {
                    currFish = fishItem;
                    break;
                }
            }
            if(currFish == null) continue;
            currFish.setCurrFishSpec(item.getAvgSpecs());
            needUpdate.add(currFish);
        }
        if(EasyListUtil.isEmpty(needUpdate)) return 0;
        Integer res = zkPondFishService.customBatchUpdate(needUpdate);
        // 3. 将数据同步到 zk_pond
        List<ZkPondDO> pondDOList = needUpdate.stream().map(obj -> new ZkPondDO().setId(obj.getPondId()).setFishSpec(obj.getCurrFishSpec())).toList();
        zkPondService.customUpdateBatchById(pondDOList);
        return res;
    }

    /**
     * 打样同步数据 到  表: zk_fish_proofing
     * @param list 巡检明细
     */
    public List<ZkFishProofingDO> proofingListHandler(List<TaskDetailDO> list) {
        if(EasyListUtil.isEmpty(list)) return null;
        // 1. 得到 需要 同步的 数据
        List<ZkFishProofingDO> newProofingList = list.stream().map(obj -> {
            ZkFishProofingDO zkFishProofingDO = TaskDetail2FishProofing.transformer(obj);
            return zkFishProofingDO;
        }).collect(Collectors.toList());
        if(EasyListUtil.isEmpty(newProofingList)) return null;
        newProofingList = newProofingList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(EasyListUtil.isEmpty(newProofingList)) return null;
        // 2. 执行数据同步
        Set<Long> taskDetailIds = newProofingList.stream().map(ZkFishProofingDO::getTaskDetailId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<ZkFishProofingDO> localList = zkFishProofingService.getByTaskDetailIds(taskDetailIds);
        List<ZkFishProofingDO> needUpdate = new ArrayList<>();
        List<ZkFishProofingDO> needAdd = new ArrayList<>();
        if(EasyListUtil.isEmpty(localList)) {
            // 直接添加
            needAdd = newProofingList;
        } else {
            // 需要找到添加的和更新的
            for(ZkFishProofingDO newItem : newProofingList) {
                ZkFishProofingDO history = null;
                for(ZkFishProofingDO has : localList) {
                    if(Objects.equals(newItem.getTaskDetailId(), has.getTaskDetailId())) {
                        history = has;
                        break;
                    }
                }
                if(history != null) {
                    CommonUtil.copyPropertiesIgnoreNull(newItem, history);
                    needUpdate.add(history);
                } else {
                    needAdd.add(newItem);
                }
            }
        }
        zkFishProofingService.onlyFillObjByList(EasyListUtil.merge(needAdd, needUpdate));
        zkFishProofingService.customSaveBatch(needAdd);
        zkFishProofingService.customUpdateBatch(needUpdate);
        needUpdate.addAll(needAdd);
        return needUpdate;
    }

    /**
     * 投喂同步数据 到  表: zk_feeding_log
     * @param list 巡检明细
     */
    public List<ZkFeedingLogDO> feedListHandler(List<TaskDetailDO> list) {
        if(EasyListUtil.isEmpty(list)) return null;
        // 1. 得到 需要 同步的 数据
        List<ZkFeedingLogDO> needSyncList = new ArrayList<>();
        for(TaskDetailDO obj : list) {
            List<ZkFeedingLogDO> feedingLogDOS = taskDetail2Feed.transformer(obj);
            if(EasyListUtil.isEmpty(feedingLogDOS)) continue;
            needSyncList.addAll(feedingLogDOS);
        }
        if(EasyListUtil.isEmpty(needSyncList)) return null;
        needSyncList = needSyncList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(EasyListUtil.isEmpty(needSyncList)) return null;
        // 2. 执行数据同步
        Set<Long> taskDetailIds = needSyncList.stream().map(ZkFeedingLogDO::getTaskDetailId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<ZkFeedingLogDO> localList = zkFeedingLogService.getByTaskDetailIds(taskDetailIds);
        List<ZkFeedingLogDO> needUpdate = new ArrayList<>();
        List<ZkFeedingLogDO> needAdd = new ArrayList<>();
        if(EasyListUtil.isEmpty(localList)) {
            // 直接添加
            needAdd = needSyncList;
        } else {
            // 需要找到添加的和更新的
            for(ZkFeedingLogDO newItem : needSyncList) {
                ZkFeedingLogDO history = null;
                for(ZkFeedingLogDO has : localList) {
                    if(Objects.equals(newItem.getTaskDetailId(), has.getTaskDetailId())) {
                        history = has;
                        break;
                    }
                }
                if(history != null) {
                    CommonUtil.copyPropertiesIgnoreNull(newItem, history);
                    needUpdate.add(history);
                } else {
                    needAdd.add(newItem);
                }
            }
        }
        // 虽然这里 写的是循环添加，但是实际 只会有一条数据，写成循环方便后续 升级 代码复用
        if(EasyListUtil.isNotEmpty(needAdd)) {
            for(ZkFeedingLogDO i : needAdd) {
                ZkFeedingLogCreateReqVO createReqVO = new ZkFeedingLogCreateReqVO();
                CommonUtil.copyPropertiesIgnoreNull(i, createReqVO);
                zkFeedingLogController.createZkFeedingLog(createReqVO);
            }
        }
        if(EasyListUtil.isNotEmpty(needUpdate)) {
            for(ZkFeedingLogDO i : needUpdate) {
                ZkFeedingLogUpdateReqVO updateReqVO = new ZkFeedingLogUpdateReqVO();
                CommonUtil.copyPropertiesIgnoreNull(i, updateReqVO);
                zkFeedingLogController.updateZkFeedingLog(updateReqVO);
            }
        }
        needUpdate.addAll(needAdd);
        return needUpdate;
    }


    /**
     * 死亡同步数据 到  表: zk_pond_fish_detail
     * @param list 巡检明细
     */
    public List<ZkPondFishDetailDO> deadListHandler(List<TaskDetailDO> list) {
        if(EasyListUtil.isEmpty(list)) return null;
        // 1. 得到 需要 同步的 数据
        List<ZkPondFishDetailDO> needSyncList = new ArrayList<>();
        for(TaskDetailDO obj : list) {
            List<ZkPondFishDetailDO> detailDOS = taskDetail2Dead.transformer(obj);
            if(EasyListUtil.isEmpty(detailDOS)) continue;
            needSyncList.addAll(detailDOS);
        }
        if(EasyListUtil.isEmpty(needSyncList)) return null;
        needSyncList = needSyncList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(EasyListUtil.isEmpty(needSyncList)) return null;
        // 2. 执行数据同步
        Set<Long> taskDetailIds = needSyncList.stream().map(ZkPondFishDetailDO::getTaskDetailId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<ZkPondFishDetailDO> localList = zkPondFishDetailService.getByTaskDetailIds(taskDetailIds);
        List<ZkPondFishDetailDO> needUpdate = new ArrayList<>();
        List<ZkPondFishDetailDO> needAdd = new ArrayList<>();
        if(EasyListUtil.isEmpty(localList)) {
            // 直接添加
            needAdd = needSyncList;
        } else {
            // 需要找到添加的和更新的
            for(ZkPondFishDetailDO newItem : needSyncList) {
                ZkPondFishDetailDO history = null;
                for(ZkPondFishDetailDO has : localList) {
                    if(Objects.equals(newItem.getTaskDetailId(), has.getTaskDetailId())) {
                        history = has;
                        break;
                    }
                }
                if(history != null) {
                    CommonUtil.copyPropertiesIgnoreNull(newItem, history);
                    needUpdate.add(history);
                } else {
                    needAdd.add(newItem);
                }
            }
        }
        // 虽然这里 写的是循环添加，但是实际 只会有一条数据，写成循环方便后续 升级 代码复用
        if(EasyListUtil.isNotEmpty(needAdd)) {
            for(ZkPondFishDetailDO i : needAdd) {
                ZkPondFishDetailCreateReqVO createReqVO = new ZkPondFishDetailCreateReqVO();
                CommonUtil.copyPropertiesIgnoreNull(i, createReqVO);
                zkPondFishDetailController.createZkPondFishDetail(createReqVO);
            }
        }
        if(EasyListUtil.isNotEmpty(needUpdate)) {
            for(ZkPondFishDetailDO i : needUpdate) {
                ZkPondFishDetailUpdateReqVO updateReqVO = new ZkPondFishDetailUpdateReqVO();
                CommonUtil.copyPropertiesIgnoreNull(i, updateReqVO);
                zkPondFishDetailController.updateZkPondFishDetail(updateReqVO);
            }
        }
        needUpdate.addAll(needAdd);
        return needUpdate;
    }

    public List<TaskDetailDO> getList(List<TaskDetailDO> taskDetailDOS, String checkType) {
        List<TaskDetailDO> collect = taskDetailDOS.stream().filter(obj -> StringUtils.equals(obj.getCheckType(), checkType)).collect(Collectors.toList());
        return collect;
    }
}
