package cn.iocoder.yudao.module.fish.service.zkpondfish;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.dictdata.DictDataService;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfish.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpondfish.ZkPondFishConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondfish.ZkPondFishMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 存塘鱼苗 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkPondFishServiceImpl implements ZkPondFishService {

    @Resource
    private ZkPondFishMapper zkPondFishMapper;
    @Resource
    private DictDataService dictDataService;

    @Resource
    private ZkPondService zkPondService;

    @Override
    public Long createZkPondFish(ZkPondFishCreateReqVO createReqVO) {
        // 插入
        ZkPondFishDO zkPondFish = BeanUtils.toBean(createReqVO, ZkPondFishDO.class);
        zkPondFishMapper.insert(zkPondFish);
        // 返回
        return zkPondFish.getId();
    }

    @Override
    public void updateZkPondFish(ZkPondFishUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPondFishExists(updateReqVO.getId());
        // 更新
        ZkPondFishDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondFishDO.class);
        zkPondFishMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPondFish(Long id) {
        // 校验存在
        validateZkPondFishExists(id);
        // 删除
        zkPondFishMapper.deleteById(id);
    }

    private void validateZkPondFishExists(Long id) {
        if (zkPondFishMapper.selectById(id) == null) {
            throw exception(ZK_POND_FISH_NOT_EXISTS);
        }
    }

    @Override
    public ZkPondFishDO getZkPondFish(Long id) {
        return zkPondFishMapper.selectById(id);
    }

    @Override
    public List<ZkPondFishDO> getZkPondFishList(Collection<Long> ids) {
        return zkPondFishMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPondFishDO> getZkPondFishPage(ZkPondFishPageReqVO pageReqVO) {
        return zkPondFishMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPondFishDO> getZkPondFishList(ZkPondFishExportReqVO exportReqVO) {
        return zkPondFishMapper.selectList(exportReqVO);
    }

    @Override
    public ZkPondFishDO getByPondId(Long pondId) {
        QueryWrapper<ZkPondFishDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_id", pondId).last("limit 1");
        ZkPondFishDO zkPondFishDO = zkPondFishMapper.selectOne(queryWrapper);
        return zkPondFishDO;
    }

    @Override
    public void customInsert(ZkPondFishDO zkPondFishDO) {
        zkPondFishDO.setId(null);
        zkPondFishMapper.insert(zkPondFishDO);
    }

    @Override
    public void customBatchInsert(List<ZkPondFishDO> zkPondFishDOList) {
        if(EasyListUtil.isEmpty(zkPondFishDOList)) return;
        zkPondFishMapper.insertBatch(zkPondFishDOList);
    }

    @Override
    public void customUpdate(ZkPondFishDO zkPondFishDO) {
        zkPondFishMapper.updateById(zkPondFishDO);
    }

    @Override
    public List<ZkPondFishDO> getListByPondIds(Set<Long> pondIds) {
        QueryWrapper<ZkPondFishDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pond_id", pondIds);
        return zkPondFishMapper.selectList(queryWrapper);
    }

    @Override
    public ZkPondFishDO getByBatchNumber(String batchNumber) {
        QueryWrapper<ZkPondFishDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("batch_number", batchNumber).isNull("pond_id").last("limit 1");
        ZkPondFishDO zkPondFishDO = zkPondFishMapper.selectOne(queryWrapper);
        return zkPondFishDO;
    }

    @Override
    public List<ZkPondFishDO> getList() {
        QueryWrapper<ZkPondFishDO> queryWrapper = new QueryWrapper<>();
        List<ZkPondFishDO> zkPondFishDOS = zkPondFishMapper.selectList(queryWrapper);
        enhancer_fishInfo(zkPondFishDOS);
        return zkPondFishDOS;
    }

    @Override
    public List<ZkPondFishDO> getListWithSpec() {
        QueryWrapper<ZkPondFishDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("zpf.pond_id"); // isNull("zpf.batch_number");
        List<ZkPondFishDO> list = zkPondFishMapper.getListWithSpec(queryWrapper);
        return list;
    }

    @Override
    public void updatePush(ZkPondFishDetailDO zkPondFishDetailDO, Long differenceData) {
        // 1. 获取数据
        ZkPondFishDO pondFishDO = getByPondId(zkPondFishDetailDO.getPondId());
        if(pondFishDO == null) return;
        // 2. 执行修改
        Long fishNum = pondFishDO.build_specInfo(zkPondFishDetailDO.getFishSpec()).getFishNum();
        Long newFishNum = fishNum - differenceData;
        pondFishDO.build_specInfo(zkPondFishDetailDO.getFishSpec()).build_pushFishNum(-differenceData).setFishNum(newFishNum);
        zkPondFishMapper.updateById(pondFishDO);
        // 3. 同步数据到 zk_pond
        zkPondService.customUpdateById(new ZkPondDO(zkPondFishDetailDO.getPondId(), newFishNum,
                zkPondFishDetailDO.getBatchNumber()).build_fishSpec(pondFishDO.getInitFishSpec()));
        // 4. 同步 批次信息
        updatePushByBatchNumber(zkPondFishDetailDO.getBatchNumber(), -differenceData, zkPondFishDetailDO);
    }
    public void updatePushByBatchNumber(String batchNumber, Long differenceData, ZkPondFishDetailDO zkPondFishDetailDO) {
        if(StringUtils.isEmpty(batchNumber)) return;
        ZkPondFishDO pondFishDO = getByBatchNumber(batchNumber);
        if(pondFishDO == null) return;
        pondFishDO.build_push(differenceData).build_specInfo(zkPondFishDetailDO.getFishSpec());
        zkPondFishMapper.updateById(pondFishDO);
    }

    @Override
    public void updateDead(ZkPondFishDetailDO zkPondFishDetailDO, Long differenceData) {
        // 1. 获取数据
        ZkPondFishDO pondFishDO = getByPondId(zkPondFishDetailDO.getPondId());
        if(pondFishDO == null) return;
        // 2. 执行修改
        Long fishNum = pondFishDO.getFishNum();
        Long newFishNum = fishNum - differenceData;
        pondFishDO.setFishNum(newFishNum);
        zkPondFishMapper.updateById(pondFishDO);
        // 3. 同步数据到 zk_pond
        // zkPondService.updateFishNum(zkPondFishDetailDO.getPondId(), newFishNum);
        zkPondService.customUpdateById(new ZkPondDO(zkPondFishDetailDO.getPondId(), newFishNum, zkPondFishDetailDO.getBatchNumber()));
    }

    /**
     *
     * @param zkPondFishDetailDO
     * @param differenceData 这个是 源分池数-当前分池数，则源分池数+differenceData，目标分池数-differenceData
     */
    @Override
    public void updateSplit(ZkPondFishDetailDO zkPondFishDetailDO, Long differenceData) {
        // 1. 获取数据
        ZkPondFishDO pondFishDO = getByPondId(zkPondFishDetailDO.getPondId());
        ZkPondFishDO targetPondFishDO = getByPondId(zkPondFishDetailDO.getTargetPondId());
        if(pondFishDO == null) return;
        if(targetPondFishDO == null) return;
        // 2. 执行修改
        pondFishDO.build_specInfo(zkPondFishDetailDO.getFishSpec()).build_splitAdd(differenceData);
        zkPondFishMapper.updateById(pondFishDO);
        targetPondFishDO.build_specInfo(zkPondFishDetailDO.getFishSpec()).build_splitReduce(differenceData);
        zkPondFishMapper.updateById(pondFishDO);
        zkPondFishMapper.updateById(targetPondFishDO);
        // 3. 同步数据到 zk_pond
        // zkPondService.updateFishNum(zkPondFishDetailDO.getPondId(), newFishNum);
        zkPondService.customUpdateById(new ZkPondDO(zkPondFishDetailDO.getPondId(),
                pondFishDO.getFishNum(), zkPondFishDetailDO.getBatchNumber()).build_fishSpec(pondFishDO.getCurrFishSpec()));
        // zkPondService.updateFishNum(zkPondFishDetailDO.getTargetPondId(), targetNewFishNum);
        zkPondService.customUpdateById(new ZkPondDO(zkPondFishDetailDO.getTargetPondId(),
                targetPondFishDO.getFishNum(), zkPondFishDetailDO.getBatchNumber()).build_fishSpec(pondFishDO.getCurrFishSpec()));
        // 4. 同步 批次信息
        updateSplitByBatchNumber(zkPondFishDetailDO.getBatchNumber(), -differenceData, zkPondFishDetailDO);
    }
    public void updateSplitByBatchNumber(String batchNumber, Long differenceData, ZkPondFishDetailDO zkPondFishDetailDO) {
        if(StringUtils.isEmpty(batchNumber)) return;
        ZkPondFishDO pondFishDO = getByBatchNumber(batchNumber);
        if(pondFishDO == null) return;
        pondFishDO.build_split(differenceData).build_specInfo(zkPondFishDetailDO.getFishSpec());
        zkPondFishMapper.updateById(pondFishDO);
    }

    @Override
    public List<ZkPondFishDO> selectByPondIdSet(Set<Long> pondIdSet) {
        if(CollectionUtils.isAnyEmpty(pondIdSet)) return new ArrayList<>();
        QueryWrapper<ZkPondFishDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pond_id", pondIdSet);
        return zkPondFishMapper.selectList(queryWrapper);
    }

    @Override
    public Integer customBatchUpdate(List<ZkPondFishDO> needUpdate) {
        if(EasyListUtil.isEmpty(needUpdate)) return 0;
        zkPondFishMapper.updateBatch(needUpdate, needUpdate.size());
        return needUpdate.size();
    }

    @Override
    public List<ZkPondDO> getByEmptyPondFish() {
        return zkPondFishMapper.getByEmptyPondFish();
    }


    public void enhancer_fishInfo(List<ZkPondFishDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 获取鱼type信息
        List<DictDataVo> dictDataList = dictDataService.getByDictTypes(Arrays.asList("zk_pond_fish_types"));
        // 2. 获取
        for(ZkPondFishDO item : list) {
            for(DictDataVo dict : dictDataList) {
                if(StringUtils.equals(item.getFishTypes(), dict.getValue())) {
                    item.setFishName(dict.getLabel());
                    break;
                }
            }
        }
    }

}
