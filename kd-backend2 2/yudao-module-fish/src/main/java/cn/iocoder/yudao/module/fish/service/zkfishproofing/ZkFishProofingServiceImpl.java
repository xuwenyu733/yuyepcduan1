package cn.iocoder.yudao.module.fish.service.zkfishproofing;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkfishproofing.ZkFishProofingConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfishproofing.ZkFishProofingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 打样记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkFishProofingServiceImpl implements ZkFishProofingService {

    @Resource
    private ZkFishProofingMapper zkFishProofingMapper;
    @Resource
    private ZkPondFishService zkPondFishService;

    @Autowired
    @Lazy
    private ZkPondService zkPondService;

    @Override
    public Long createZkFishProofing(ZkFishProofingCreateReqVO createReqVO) {
        // 插入
        ZkFishProofingDO zkFishProofing = BeanUtils.toBean(createReqVO, ZkFishProofingDO.class);
        zkFishProofingMapper.insert(zkFishProofing);
        // 返回
        return zkFishProofing.getId();
    }

    @Override
    public void updateZkFishProofing(ZkFishProofingUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkFishProofingExists(updateReqVO.getId());
        // 更新
        ZkFishProofingDO updateObj = BeanUtils.toBean(updateReqVO, ZkFishProofingDO.class);
        zkFishProofingMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkFishProofing(Long id) {
        // 校验存在
        validateZkFishProofingExists(id);
        // 删除
        zkFishProofingMapper.deleteById(id);
    }

    private void validateZkFishProofingExists(Long id) {
        if (zkFishProofingMapper.selectById(id) == null) {
            throw exception(ZK_FISH_PROOFING_NOT_EXISTS);
        }
    }

    @Override
    public ZkFishProofingDO getZkFishProofing(Long id) {
        return zkFishProofingMapper.selectById(id);
    }

    @Override
    public List<ZkFishProofingDO> getZkFishProofingList(Collection<Long> ids) {
        return zkFishProofingMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkFishProofingDO> getZkFishProofingPage(ZkFishProofingPageReqVO pageReqVO) {
        return zkFishProofingMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkFishProofingDO> getZkFishProofingList(ZkFishProofingExportReqVO exportReqVO) {
        return zkFishProofingMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkFishProofingDO> getByTaskDetailIds(Set<Long> taskDetailIds) {
        if(CollectionUtils.isAnyEmpty(taskDetailIds)) return new ArrayList<>();
        QueryWrapper<ZkFishProofingDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("task_detail_id", taskDetailIds);
        return zkFishProofingMapper.selectList(queryWrapper);
    }

    @Override
    public void customSaveBatch(List<ZkFishProofingDO> newProofingList) {
        if(EasyListUtil.isEmpty(newProofingList)) return;
        zkFishProofingMapper.insertBatch(newProofingList);
    }

    @Override
    public void customUpdateBatch(List<ZkFishProofingDO> needUpdate) {
        if(EasyListUtil.isEmpty(needUpdate)) return;
        zkFishProofingMapper.updateBatch(needUpdate, needUpdate.size());
    }

    @Override
    public List<ZkFishProofingDO> getCustomList(String statisticsType) {
        QueryWrapper<ZkFishProofingDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("statistics_type", statisticsType);
        return zkFishProofingMapper.getCustomList(queryWrapper);
    }

    @Override
    public ZkFishProofingDO getLastProofing(Long pondId) {
        // 1. 从数据库查询
        QueryWrapper<ZkFishProofingDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_id", pondId).orderByDesc("id").last("limit 1");
        ZkFishProofingDO zkFishProofingDO = zkFishProofingMapper.selectOne(queryWrapper);
        // 2. 如果没有则从 zk_pond_fish 获取最新
        if(zkFishProofingDO == null) {
            ZkPondFishDO byPondId = zkPondFishService.getByPondId(pondId);
            if(byPondId == null) return null;
            zkFishProofingDO = new ZkFishProofingDO().setPondId(pondId).setInitSpecs(byPondId.getInitFishSpec()).setAvgSpecs(byPondId.getCurrFishSpec());
        }
        return zkFishProofingDO;
    }

    @Override
    public List<ZkFishProofingDO> getLastByPondIdSet(Set<Long> pondIdSet) {
        QueryWrapper<ZkFishProofingDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pond_id", pondIdSet).orderByDesc("id");

        return zkFishProofingMapper.selectList(queryWrapper);
    }

    @Override
    public void onlyFillObjByList(List<ZkFishProofingDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        Set<Long> pondIds = list.stream().filter(obj -> obj.getDeptId() == null).map(ZkFishProofingDO::getPondId).collect(Collectors.toSet());

        List<ZkPondDO> zkPondDOS = zkPondService.selectByPondIds(pondIds);
        if(EasyListUtil.isEmpty(zkPondDOS)) return;
        CommonUtil.origins2Targets3(zkPondDOS, "id", List.of("userId", "deptId", "tenantId"), list, "pondId",
                List.of("userId", "deptId", "tenantId"));
    }

}
