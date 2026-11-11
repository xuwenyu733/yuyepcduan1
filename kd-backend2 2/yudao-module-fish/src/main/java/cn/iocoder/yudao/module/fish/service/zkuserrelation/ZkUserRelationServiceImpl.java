package cn.iocoder.yudao.module.fish.service.zkuserrelation;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkuserrelation.ZkUserRelationDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkuserrelation.ZkUserRelationMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 用户信息关联 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkUserRelationServiceImpl implements ZkUserRelationService {

    @Resource
    private ZkUserRelationMapper zkUserRelationMapper;

    @Override
    public synchronized Long createZkUserRelation(ZkUserRelationSaveReqVO createReqVO) {
        // 1. 根据userId 和 relationType 查询
        ZkUserRelationDO zkUserRelationDO = zkUserRelationMapper.selectOne(ZkUserRelationDO::getUserId, createReqVO.getUserId());
        if (zkUserRelationDO != null) {
            zkUserRelationDO.setRelationValue(createReqVO.getRelationValue());
            zkUserRelationMapper.updateById(zkUserRelationDO);
            return zkUserRelationDO.getId();
        }
        // 插入
        ZkUserRelationDO zkUserRelation = BeanUtils.toBean(createReqVO, ZkUserRelationDO.class);
        zkUserRelationMapper.insert(zkUserRelation);

        // 返回
        return zkUserRelation.getId();
    }

    @Override
    public void updateZkUserRelation(ZkUserRelationSaveReqVO updateReqVO) {
        // 校验存在
        validateZkUserRelationExists(updateReqVO.getId());
        // 更新
        ZkUserRelationDO updateObj = BeanUtils.toBean(updateReqVO, ZkUserRelationDO.class);
        zkUserRelationMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkUserRelation(Long id) {
        // 校验存在
        validateZkUserRelationExists(id);
        // 删除
        zkUserRelationMapper.deleteById(id);
    }

    @Override
        public void deleteZkUserRelationListByIds(List<Long> ids) {
        // 删除
        zkUserRelationMapper.deleteByIds(ids);
        }


    private void validateZkUserRelationExists(Long id) {
        if (zkUserRelationMapper.selectById(id) == null) {
            throw exception(ZK_USER_RELATION_NOT_EXISTS);
        }
    }

    @Override
    public ZkUserRelationDO getZkUserRelation(Long id) {
        return zkUserRelationMapper.selectById(id);
    }

    @Override
    public PageResult<ZkUserRelationDO> getZkUserRelationPage(ZkUserRelationPageReqVO pageReqVO) {
        return zkUserRelationMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<String, Object> getUserRelation(Long userId, Integer relationType) {
        ZkUserRelationDO relationDO = new ZkUserRelationDO().setUserId(userId)
                .setRelationType(relationType);
        QueryWrapper<ZkUserRelationDO> queryWrapper = new QueryWrapper<>(relationDO);
        List<ZkUserRelationDO> zkUserRelationDOS = zkUserRelationMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(zkUserRelationDOS)) return Map.of();
        if(zkUserRelationDOS.size() > 1 ) {
            for (int i=zkUserRelationDOS.size()-1;i>=0;i--) zkUserRelationMapper.deleteById(zkUserRelationDOS.get(i).getId());
        }
        return BeanUtil.beanToMap(zkUserRelationDOS.get(zkUserRelationDOS.size()-1));
    }
}