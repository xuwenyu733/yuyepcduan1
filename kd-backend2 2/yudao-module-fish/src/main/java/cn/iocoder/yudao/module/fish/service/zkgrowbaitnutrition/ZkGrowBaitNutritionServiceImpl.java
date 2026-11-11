package cn.iocoder.yudao.module.fish.service.zkgrowbaitnutrition;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition.ZkGrowBaitNutritionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowbaitnutrition.ZkGrowBaitNutritionMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 饵料营养成分 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowBaitNutritionServiceImpl implements ZkGrowBaitNutritionService {

    @Resource
    private ZkGrowBaitNutritionMapper zkGrowBaitNutritionMapper;

    @Override
    public Long createZkGrowBaitNutrition(ZkGrowBaitNutritionSaveReqVO createReqVO) {
        // 插入
        ZkGrowBaitNutritionDO zkGrowBaitNutrition = BeanUtils.toBean(createReqVO, ZkGrowBaitNutritionDO.class);
        zkGrowBaitNutritionMapper.insert(zkGrowBaitNutrition);

        // 返回
        return zkGrowBaitNutrition.getId();
    }

    @Override
    public void updateZkGrowBaitNutrition(ZkGrowBaitNutritionSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowBaitNutritionExists(updateReqVO.getId());
        // 更新
        ZkGrowBaitNutritionDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowBaitNutritionDO.class);
        zkGrowBaitNutritionMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowBaitNutrition(Long id) {
        // 校验存在
        validateZkGrowBaitNutritionExists(id);
        // 删除
        zkGrowBaitNutritionMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowBaitNutritionListByIds(List<Long> ids) {
        // 删除
        zkGrowBaitNutritionMapper.deleteByIds(ids);
        }


    private void validateZkGrowBaitNutritionExists(Long id) {
        if (zkGrowBaitNutritionMapper.selectById(id) == null) {
            throw exception(ZK_GROW_BAIT_NUTRITION_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowBaitNutritionDO getZkGrowBaitNutrition(Long id) {
        return zkGrowBaitNutritionMapper.selectById(id);
    }

    @Override
    public PageResult<ZkGrowBaitNutritionDO> getZkGrowBaitNutritionPage(ZkGrowBaitNutritionPageReqVO pageReqVO) {
        return zkGrowBaitNutritionMapper.selectPage(pageReqVO);
    }

}