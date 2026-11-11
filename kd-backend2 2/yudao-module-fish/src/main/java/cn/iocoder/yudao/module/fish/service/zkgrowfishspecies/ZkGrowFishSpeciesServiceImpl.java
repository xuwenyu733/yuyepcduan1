package cn.iocoder.yudao.module.fish.service.zkgrowfishspecies;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies.ZkGrowFishSpeciesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowfishspecies.ZkGrowFishSpeciesMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼类品种 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowFishSpeciesServiceImpl implements ZkGrowFishSpeciesService {

    @Resource
    private ZkGrowFishSpeciesMapper zkGrowFishSpeciesMapper;

    @Override
    public Long createZkGrowFishSpecies(ZkGrowFishSpeciesSaveReqVO createReqVO) {
        // 插入
        ZkGrowFishSpeciesDO zkGrowFishSpecies = BeanUtils.toBean(createReqVO, ZkGrowFishSpeciesDO.class);
        zkGrowFishSpeciesMapper.insert(zkGrowFishSpecies);

        // 返回
        return zkGrowFishSpecies.getId();
    }

    @Override
    public void updateZkGrowFishSpecies(ZkGrowFishSpeciesSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowFishSpeciesExists(updateReqVO.getId());
        // 更新
        ZkGrowFishSpeciesDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowFishSpeciesDO.class);
        zkGrowFishSpeciesMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowFishSpecies(Long id) {
        // 校验存在
        validateZkGrowFishSpeciesExists(id);
        // 删除
        zkGrowFishSpeciesMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowFishSpeciesListByIds(List<Long> ids) {
        // 删除
        zkGrowFishSpeciesMapper.deleteByIds(ids);
        }


    private void validateZkGrowFishSpeciesExists(Long id) {
        if (zkGrowFishSpeciesMapper.selectById(id) == null) {
            throw exception(ZK_GROW_FISH_SPECIES_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowFishSpeciesDO getZkGrowFishSpecies(Long id) {
        return zkGrowFishSpeciesMapper.selectById(id);
    }

    @Override
    public PageResult<ZkGrowFishSpeciesDO> getZkGrowFishSpeciesPage(ZkGrowFishSpeciesPageReqVO pageReqVO) {
        return zkGrowFishSpeciesMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkGrowFishSpeciesDO> getByIds(Set<Long> speciesIds) {
        if(CollectionUtils.isAnyEmpty(speciesIds)) return new ArrayList<>();
        LambdaQueryWrapper<ZkGrowFishSpeciesDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ZkGrowFishSpeciesDO::getId, speciesIds);
        return zkGrowFishSpeciesMapper.selectList(lambdaQueryWrapper);
    }

}