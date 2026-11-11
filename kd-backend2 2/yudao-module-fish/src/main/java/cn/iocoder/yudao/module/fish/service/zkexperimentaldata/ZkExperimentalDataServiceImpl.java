package cn.iocoder.yudao.module.fish.service.zkexperimentaldata;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool.ZkExperimentalPoolDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpool.ZkExperimentalPoolMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentaldata.ZkExperimentalDataMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 实验数据采集 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkExperimentalDataServiceImpl implements ZkExperimentalDataService {

    @Resource
    private ZkExperimentalDataMapper zkExperimentalDataMapper;

    @Resource
    private ZkExperimentalPoolMapper zkExperimentalPoolMapper;

    @Override
    public Long createZkExperimentalData(ZkExperimentalDataSaveReqVO createReqVO) {
        // 插入
        ZkExperimentalDataDO zkExperimentalData = BeanUtils.toBean(createReqVO, ZkExperimentalDataDO.class);
        if (zkExperimentalData.getExperimentId() == null) {
            zkExperimentalData.setExperimentId(createReqVO.resolveExperimentId());
        }

        // 如果没有传入池号但传入了池子ID，补充池号
        if (StringUtil.isEmpty(zkExperimentalData.getPoolNumber()) && zkExperimentalData.getPoolId() != null) {
            ZkExperimentalPoolDO pool = zkExperimentalPoolMapper.selectById(zkExperimentalData.getPoolId());
            if (pool != null) {
                zkExperimentalData.setPoolNumber(pool.getPoolNumber());
            }
        }
        
        zkExperimentalDataMapper.insert(zkExperimentalData);

        // 返回
        return zkExperimentalData.getId();
    }

    @Override
    public void updateZkExperimentalData(ZkExperimentalDataSaveReqVO updateReqVO) {
        // 校验存在
        validateZkExperimentalDataExists(updateReqVO.getId());
        // 更新
        ZkExperimentalDataDO updateObj = BeanUtils.toBean(updateReqVO, ZkExperimentalDataDO.class);
        if (updateObj.getExperimentId() == null) {
            updateObj.setExperimentId(updateReqVO.resolveExperimentId());
        }

        // 如果没有传入池号但传入了池子ID，补充池号
        if (StringUtil.isEmpty(updateObj.getPoolNumber()) && updateObj.getPoolId() != null) {
            ZkExperimentalPoolDO pool = zkExperimentalPoolMapper.selectById(updateObj.getPoolId());
            if (pool != null) {
                updateObj.setPoolNumber(pool.getPoolNumber());
            }
        }
        
        zkExperimentalDataMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkExperimentalData(Long id) {
        // 校验存在
        validateZkExperimentalDataExists(id);
        // 删除
        zkExperimentalDataMapper.deleteById(id);
    }

    @Override
        public void deleteZkExperimentalDataListByIds(List<Long> ids) {
        // 删除
        zkExperimentalDataMapper.deleteByIds(ids);
        }


    private void validateZkExperimentalDataExists(Long id) {
        if (zkExperimentalDataMapper.selectById(id) == null) {
            throw exception(ZK_EXPERIMENTAL_DATA_NOT_EXISTS);
        }
    }

    @Override
    public ZkExperimentalDataDO getZkExperimentalData(Long id) {
        return zkExperimentalDataMapper.selectById(id);
    }

    @Override
    public PageResult<ZkExperimentalDataDO> getZkExperimentalDataPage(ZkExperimentalDataPageReqVO pageReqVO) {
        return zkExperimentalDataMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkExperimentalDataDO> getByIds(Set<Long> experimentalIdSet) {
        if(CollectionUtils.isAnyEmpty(experimentalIdSet)) return new ArrayList<>();
        LambdaQueryWrapper<ZkExperimentalDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ZkExperimentalDataDO::getExperimentId, experimentalIdSet);
        return zkExperimentalDataMapper.selectList(queryWrapper);
    }

    @Override
    public Map<Long, Map<String, ZkExperimentalDataDO>> selectMapByIds(Set<Long> experimentalIdSet) {
        Map<Long, Map<String, ZkExperimentalDataDO>> res = new HashMap<>();
        // 1. 获取列表
        List<ZkExperimentalDataDO> tList = getByIds(experimentalIdSet);
        if(EasyListUtil.isEmpty(tList)) return res;
        // 2. 封转成对应map
        Map<Long, List<ZkExperimentalDataDO>> collect = tList.stream().collect(Collectors.groupingBy(ZkExperimentalDataDO::getExperimentId));
        for(Long experimentId : collect.keySet()) {
            List<ZkExperimentalDataDO> tempList = collect.get(experimentId);
            Map<String, ZkExperimentalDataDO> map = new HashMap<>();
            for(ZkExperimentalDataDO item : tempList) {
                map.put(item.getCollectionType(), item);
            }
            res.put(experimentId, map);
        }
        return res;
    }

}