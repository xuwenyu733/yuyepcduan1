package cn.iocoder.yudao.module.fish.service.zkgrowthmodel;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowthmodel.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowthmodel.ZkGrowthModelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkgrowthmodel.ZkGrowthModelConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowthmodel.ZkGrowthModelMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 生长模型 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkGrowthModelServiceImpl implements ZkGrowthModelService {

    @Resource
    private ZkGrowthModelMapper zkGrowthModelMapper;

    @Override
    public Long createZkGrowthModel(ZkGrowthModelCreateReqVO createReqVO) {
        // 插入
        ZkGrowthModelDO zkGrowthModel = BeanUtils.toBean(createReqVO, ZkGrowthModelDO.class);
        zkGrowthModelMapper.insert(zkGrowthModel);
        // 返回
        return zkGrowthModel.getId();
    }

    @Override
    public void updateZkGrowthModel(ZkGrowthModelUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkGrowthModelExists(updateReqVO.getId());
        // 更新
        ZkGrowthModelDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowthModelDO.class);
        zkGrowthModelMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowthModel(Long id) {
        // 校验存在
        validateZkGrowthModelExists(id);
        // 删除
        zkGrowthModelMapper.deleteById(id);
    }

    private void validateZkGrowthModelExists(Long id) {
        if (zkGrowthModelMapper.selectById(id) == null) {
            throw exception(ZK_GROWTH_MODEL_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowthModelDO getZkGrowthModel(Long id) {
        return zkGrowthModelMapper.selectById(id);
    }

    @Override
    public List<ZkGrowthModelDO> getZkGrowthModelList(Collection<Long> ids) {
        return zkGrowthModelMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkGrowthModelDO> getZkGrowthModelPage(ZkGrowthModelPageReqVO pageReqVO) {
        return zkGrowthModelMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkGrowthModelDO> getZkGrowthModelList(ZkGrowthModelExportReqVO exportReqVO) {
        return zkGrowthModelMapper.selectList(exportReqVO);
    }

}
