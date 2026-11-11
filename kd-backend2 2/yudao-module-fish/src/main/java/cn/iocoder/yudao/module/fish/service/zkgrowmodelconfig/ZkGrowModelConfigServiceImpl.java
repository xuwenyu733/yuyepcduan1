package cn.iocoder.yudao.module.fish.service.zkgrowmodelconfig;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowmodelconfig.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig.ZkGrowModelConfigDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowmodelconfig.ZkGrowModelConfigMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 生长模型配置 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowModelConfigServiceImpl implements ZkGrowModelConfigService {

    @Resource
    private ZkGrowModelConfigMapper zkGrowModelConfigMapper;

    @Override
    public Long createZkGrowModelConfig(ZkGrowModelConfigSaveReqVO createReqVO) {
        // 插入
        ZkGrowModelConfigDO zkGrowModelConfig = BeanUtils.toBean(createReqVO, ZkGrowModelConfigDO.class);
        zkGrowModelConfigMapper.insert(zkGrowModelConfig);

        // 返回
        return zkGrowModelConfig.getId();
    }

    @Override
    public void updateZkGrowModelConfig(ZkGrowModelConfigSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowModelConfigExists(updateReqVO.getId());
        // 更新
        ZkGrowModelConfigDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowModelConfigDO.class);
        zkGrowModelConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowModelConfig(Long id) {
        // 校验存在
        validateZkGrowModelConfigExists(id);
        // 删除
        zkGrowModelConfigMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowModelConfigListByIds(List<Long> ids) {
        // 删除
        zkGrowModelConfigMapper.deleteByIds(ids);
        }


    private void validateZkGrowModelConfigExists(Long id) {
        if (zkGrowModelConfigMapper.selectById(id) == null) {
            throw exception(ZK_GROW_MODEL_CONFIG_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowModelConfigDO getZkGrowModelConfig(Long id) {
        return zkGrowModelConfigMapper.selectById(id);
    }

    @Override
    public ZkGrowModelConfigDO getZkGrowModelConfigBySpeciesId(Long speciesId) {
        return zkGrowModelConfigMapper.selectBySpeciesId(speciesId);
    }

    @Override
    public PageResult<ZkGrowModelConfigDO> getZkGrowModelConfigPage(ZkGrowModelConfigPageReqVO pageReqVO) {
        return zkGrowModelConfigMapper.selectPage(pageReqVO);
    }

}