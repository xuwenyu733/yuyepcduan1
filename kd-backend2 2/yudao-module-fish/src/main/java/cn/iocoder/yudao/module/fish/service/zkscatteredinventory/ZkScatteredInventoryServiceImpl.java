package cn.iocoder.yudao.module.fish.service.zkscatteredinventory;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkscatteredinventory.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscatteredinventory.ZkScatteredInventoryDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkscatteredinventory.ZkScatteredInventoryMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 零星库存 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkScatteredInventoryServiceImpl implements ZkScatteredInventoryService {

    @Resource
    private ZkScatteredInventoryMapper zkScatteredInventoryMapper;

    @Override
    public Long createZkScatteredInventory(ZkScatteredInventorySaveReqVO createReqVO) {
        // 插入
        ZkScatteredInventoryDO zkScatteredInventory = BeanUtils.toBean(createReqVO, ZkScatteredInventoryDO.class);
        zkScatteredInventoryMapper.insert(zkScatteredInventory);

        // 返回
        return zkScatteredInventory.getId();
    }

    @Override
    public void updateZkScatteredInventory(ZkScatteredInventorySaveReqVO updateReqVO) {
        // 校验存在
        validateZkScatteredInventoryExists(updateReqVO.getId());
        // 更新
        ZkScatteredInventoryDO updateObj = BeanUtils.toBean(updateReqVO, ZkScatteredInventoryDO.class);
        zkScatteredInventoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkScatteredInventory(Long id) {
        // 校验存在
        validateZkScatteredInventoryExists(id);
        // 删除
        zkScatteredInventoryMapper.deleteById(id);
    }

    @Override
        public void deleteZkScatteredInventoryListByIds(List<Long> ids) {
        // 删除
        zkScatteredInventoryMapper.deleteByIds(ids);
        }


    private void validateZkScatteredInventoryExists(Long id) {
        if (zkScatteredInventoryMapper.selectById(id) == null) {
            throw exception(ZK_SCATTERED_INVENTORY_NOT_EXISTS);
        }
    }

    @Override
    public ZkScatteredInventoryDO getZkScatteredInventory(Long id) {
        return zkScatteredInventoryMapper.selectById(id);
    }

    @Override
    public PageResult<ZkScatteredInventoryDO> getZkScatteredInventoryPage(ZkScatteredInventoryPageReqVO pageReqVO) {
        QueryWrapper<ZkScatteredInventoryDO> queryWrapper = new QueryWrapper<>();
        if(pageReqVO.getProductId() != null) queryWrapper.eq("zsi.product_id", pageReqVO.getProductId());
        if(StringUtil.isNotEmptyWith0(pageReqVO.getProductName())) queryWrapper.like("ep.name", pageReqVO.getProductName());
        if(pageReqVO.getCategoryId() != null) queryWrapper.eq("ep.category_id", pageReqVO.getCategoryId());

        Page<ZkScatteredInventoryDO> page = new Page<>(pageReqVO.getPageNo(),pageReqVO.getPageSize());
        IPage<ZkScatteredInventoryDO> ipage = zkScatteredInventoryMapper.customPage(page, queryWrapper);
        return new PageResult<ZkScatteredInventoryDO>(ipage.getRecords(), ipage.getTotal());
    }

    @Override
    public List<ZkScatteredInventoryDO> getByQuery(QueryWrapper<ZkScatteredInventoryDO> zkScatteredInventoryDOQueryWrapper) {

        return zkScatteredInventoryMapper.selectList(zkScatteredInventoryDOQueryWrapper);
    }

}