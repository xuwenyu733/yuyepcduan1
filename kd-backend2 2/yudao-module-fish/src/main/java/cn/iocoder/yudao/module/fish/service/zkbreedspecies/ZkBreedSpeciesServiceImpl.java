package cn.iocoder.yudao.module.fish.service.zkbreedspecies;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbreedspecies.ZkBreedSpeciesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkbreedspecies.ZkBreedSpeciesMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 养殖品种 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkBreedSpeciesServiceImpl implements ZkBreedSpeciesService {

    @Resource
    private ZkBreedSpeciesMapper zkBreedSpeciesMapper;

    @Override
    public Long createZkBreedSpecies(ZkBreedSpeciesSaveReqVO createReqVO) {
        // 插入
        ZkBreedSpeciesDO zkBreedSpecies = BeanUtils.toBean(createReqVO, ZkBreedSpeciesDO.class);
        zkBreedSpeciesMapper.insert(zkBreedSpecies);

        // 返回
        return zkBreedSpecies.getId();
    }

    @Override
    public void updateZkBreedSpecies(ZkBreedSpeciesSaveReqVO updateReqVO) {
        // 校验存在
        validateZkBreedSpeciesExists(updateReqVO.getId());
        // 更新
        ZkBreedSpeciesDO updateObj = BeanUtils.toBean(updateReqVO, ZkBreedSpeciesDO.class);
        zkBreedSpeciesMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkBreedSpecies(Long id) {
        // 校验存在
        validateZkBreedSpeciesExists(id);
        // 删除
        zkBreedSpeciesMapper.deleteById(id);
    }

    @Override
        public void deleteZkBreedSpeciesListByIds(List<Long> ids) {
        // 删除
        zkBreedSpeciesMapper.deleteByIds(ids);
        }


    private void validateZkBreedSpeciesExists(Long id) {
        if (zkBreedSpeciesMapper.selectById(id) == null) {
            throw exception(ZK_BREED_SPECIES_NOT_EXISTS);
        }
    }

    @Override
    public ZkBreedSpeciesDO getZkBreedSpecies(Long id) {
        return zkBreedSpeciesMapper.selectById(id);
    }

    @Override
    public PageResult<ZkBreedSpeciesDO> getZkBreedSpeciesPage(ZkBreedSpeciesPageReqVO pageReqVO) {
        return zkBreedSpeciesMapper.selectPage(pageReqVO);
    }

}