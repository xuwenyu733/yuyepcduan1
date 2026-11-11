package cn.iocoder.yudao.module.fish.service.zkgrownutritionalrequirements;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 各阶段营养需求 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowNutritionalRequirementsServiceImpl implements ZkGrowNutritionalRequirementsService {

    @Resource
    private ZkGrowNutritionalRequirementsMapper zkGrowNutritionalRequirementsMapper;

    @Override
    public Long createZkGrowNutritionalRequirements(ZkGrowNutritionalRequirementsSaveReqVO createReqVO) {
        // 插入
        ZkGrowNutritionalRequirementsDO zkGrowNutritionalRequirements = BeanUtils.toBean(createReqVO, ZkGrowNutritionalRequirementsDO.class);
        zkGrowNutritionalRequirementsMapper.insert(zkGrowNutritionalRequirements);

        // 返回
        return zkGrowNutritionalRequirements.getId();
    }

    @Override
    public void updateZkGrowNutritionalRequirements(ZkGrowNutritionalRequirementsSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowNutritionalRequirementsExists(updateReqVO.getId());
        // 更新
        ZkGrowNutritionalRequirementsDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowNutritionalRequirementsDO.class);
        zkGrowNutritionalRequirementsMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowNutritionalRequirements(Long id) {
        // 校验存在
        validateZkGrowNutritionalRequirementsExists(id);
        // 删除
        zkGrowNutritionalRequirementsMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowNutritionalRequirementsListByIds(List<Long> ids) {
        // 删除
        zkGrowNutritionalRequirementsMapper.deleteByIds(ids);
        }


    private void validateZkGrowNutritionalRequirementsExists(Long id) {
        if (zkGrowNutritionalRequirementsMapper.selectById(id) == null) {
            throw exception(ZK_GROW_NUTRITIONAL_REQUIREMENTS_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowNutritionalRequirementsDO getZkGrowNutritionalRequirements(Long id) {
        return zkGrowNutritionalRequirementsMapper.selectById(id);
    }

    @Override
    public PageResult<ZkGrowNutritionalRequirementsDO> getZkGrowNutritionalRequirementsPage(ZkGrowNutritionalRequirementsPageReqVO pageReqVO) {
        return zkGrowNutritionalRequirementsMapper.selectPage(pageReqVO);
    }

}