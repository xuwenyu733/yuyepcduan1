package cn.iocoder.yudao.module.fish.service.zkexperimentalmedia;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalmedia.ZkExperimentalMediaMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 实验多媒体 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkExperimentalMediaServiceImpl implements ZkExperimentalMediaService {

    @Resource
    private ZkExperimentalMediaMapper zkExperimentalMediaMapper;

    @Override
    public Long createZkExperimentalMedia(ZkExperimentalMediaSaveReqVO createReqVO) {
        // 插入
        ZkExperimentalMediaDO zkExperimentalMedia = BeanUtils.toBean(createReqVO, ZkExperimentalMediaDO.class);
        zkExperimentalMediaMapper.insert(zkExperimentalMedia);

        // 返回
        return zkExperimentalMedia.getId();
    }

    @Override
    public void updateZkExperimentalMedia(ZkExperimentalMediaSaveReqVO updateReqVO) {
        // 校验存在
        validateZkExperimentalMediaExists(updateReqVO.getId());
        // 更新
        ZkExperimentalMediaDO updateObj = BeanUtils.toBean(updateReqVO, ZkExperimentalMediaDO.class);
        zkExperimentalMediaMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkExperimentalMedia(Long id) {
        // 校验存在
        validateZkExperimentalMediaExists(id);
        // 删除
        zkExperimentalMediaMapper.deleteById(id);
    }

    @Override
        public void deleteZkExperimentalMediaListByIds(List<Long> ids) {
        // 删除
        zkExperimentalMediaMapper.deleteByIds(ids);
        }


    private void validateZkExperimentalMediaExists(Long id) {
        if (zkExperimentalMediaMapper.selectById(id) == null) {
            throw exception(ZK_EXPERIMENTAL_MEDIA_NOT_EXISTS);
        }
    }

    @Override
    public ZkExperimentalMediaDO getZkExperimentalMedia(Long id) {
        return zkExperimentalMediaMapper.selectById(id);
    }

    @Override
    public PageResult<ZkExperimentalMediaDO> getZkExperimentalMediaPage(ZkExperimentalMediaPageReqVO pageReqVO) {
        return zkExperimentalMediaMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkExperimentalMediaDO> getByIds(Set<Long> experimentalIdSet) {
        if(CollectionUtils.isAnyEmpty(experimentalIdSet)) return new ArrayList<>();
        LambdaQueryWrapper<ZkExperimentalMediaDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ZkExperimentalMediaDO::getExperimentId, experimentalIdSet);
        return zkExperimentalMediaMapper.selectList(queryWrapper);
    }

    @Override
    public Map<Long, List<ZkExperimentalMediaDO>> selectMapByIds(Set<Long> experimentalIdSet) {
        Map<Long, List<ZkExperimentalMediaDO>> res = new HashMap<>();
        // 1. 获取列表
        List<ZkExperimentalMediaDO> tList = getByIds(experimentalIdSet);
        if(EasyListUtil.isEmpty(tList)) return res;
        // 2. 封转成对应map
        Map<Long, List<ZkExperimentalMediaDO>> collect = tList.stream().collect(Collectors.groupingBy(ZkExperimentalMediaDO::getExperimentId));
        for(Long experimentId : collect.keySet()) {
            List<ZkExperimentalMediaDO> tempList = collect.get(experimentId);
            res.put(experimentId, tempList);
        }
        return res;
    }

}