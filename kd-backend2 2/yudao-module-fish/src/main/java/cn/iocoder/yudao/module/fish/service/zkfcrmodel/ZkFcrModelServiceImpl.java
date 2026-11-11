package cn.iocoder.yudao.module.fish.service.zkfcrmodel;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfcrmodel.ZkFcrModelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkfcrmodel.ZkFcrModelConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfcrmodel.ZkFcrModelMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 饵料系数FCR模型 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkFcrModelServiceImpl implements ZkFcrModelService {

    @Resource
    private ZkFcrModelMapper zkFcrModelMapper;

    @Override
    public Long createZkFcrModel(ZkFcrModelCreateReqVO createReqVO) {
        // 插入
        ZkFcrModelDO zkFcrModel = BeanUtils.toBean(createReqVO, ZkFcrModelDO.class);
        zkFcrModelMapper.insert(zkFcrModel);
        // 返回
        return zkFcrModel.getId();
    }

    @Override
    public void updateZkFcrModel(ZkFcrModelUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkFcrModelExists(updateReqVO.getId());
        // 更新
        ZkFcrModelDO updateObj = BeanUtils.toBean(updateReqVO, ZkFcrModelDO.class);
        zkFcrModelMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkFcrModel(Long id) {
        // 校验存在
        validateZkFcrModelExists(id);
        // 删除
        zkFcrModelMapper.deleteById(id);
    }

    private void validateZkFcrModelExists(Long id) {
        if (zkFcrModelMapper.selectById(id) == null) {
            throw exception(ZK_FCR_MODEL_NOT_EXISTS);
        }
    }

    @Override
    public ZkFcrModelDO getZkFcrModel(Long id) {
        return zkFcrModelMapper.selectById(id);
    }

    @Override
    public List<ZkFcrModelDO> getZkFcrModelList(Collection<Long> ids) {
        return zkFcrModelMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkFcrModelDO> getZkFcrModelPage(ZkFcrModelPageReqVO pageReqVO) {
        return zkFcrModelMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkFcrModelDO> getZkFcrModelList(ZkFcrModelExportReqVO exportReqVO) {
        return zkFcrModelMapper.selectList(exportReqVO);
    }

}
