package cn.iocoder.yudao.module.fish.service.zkgrowbaitingredients;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitingredients.ZkGrowBaitIngredientsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowbaitingredients.ZkGrowBaitIngredientsMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 饵料成分对比 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowBaitIngredientsServiceImpl implements ZkGrowBaitIngredientsService {

    @Resource
    private ZkGrowBaitIngredientsMapper zkGrowBaitIngredientsMapper;

    @Override
    public Long createZkGrowBaitIngredients(ZkGrowBaitIngredientsSaveReqVO createReqVO) {
        // 插入
        ZkGrowBaitIngredientsDO zkGrowBaitIngredients = BeanUtils.toBean(createReqVO, ZkGrowBaitIngredientsDO.class);
        zkGrowBaitIngredientsMapper.insert(zkGrowBaitIngredients);

        // 返回
        return zkGrowBaitIngredients.getId();
    }

    @Override
    public void updateZkGrowBaitIngredients(ZkGrowBaitIngredientsSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowBaitIngredientsExists(updateReqVO.getId());
        // 更新
        ZkGrowBaitIngredientsDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowBaitIngredientsDO.class);
        zkGrowBaitIngredientsMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowBaitIngredients(Long id) {
        // 校验存在
        validateZkGrowBaitIngredientsExists(id);
        // 删除
        zkGrowBaitIngredientsMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowBaitIngredientsListByIds(List<Long> ids) {
        // 删除
        zkGrowBaitIngredientsMapper.deleteByIds(ids);
        }


    private void validateZkGrowBaitIngredientsExists(Long id) {
        if (zkGrowBaitIngredientsMapper.selectById(id) == null) {
            throw exception(ZK_GROW_BAIT_INGREDIENTS_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowBaitIngredientsDO getZkGrowBaitIngredients(Long id) {
        return zkGrowBaitIngredientsMapper.selectById(id);
    }

    @Override
    public PageResult<ZkGrowBaitIngredientsDO> getZkGrowBaitIngredientsPage(ZkGrowBaitIngredientsPageReqVO pageReqVO) {
        return zkGrowBaitIngredientsMapper.selectPage(pageReqVO);
    }

}