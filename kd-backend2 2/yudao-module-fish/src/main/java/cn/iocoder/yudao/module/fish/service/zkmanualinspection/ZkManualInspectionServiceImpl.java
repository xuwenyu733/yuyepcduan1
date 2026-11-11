package cn.iocoder.yudao.module.fish.service.zkmanualinspection;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmanualinspection.ZkManualInspectionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkmanualinspection.ZkManualInspectionConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkmanualinspection.ZkManualInspectionMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 人工巡检 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkManualInspectionServiceImpl implements ZkManualInspectionService {

    @Resource
    private ZkManualInspectionMapper zkManualInspectionMapper;

    @Override
    public Long createZkManualInspection(ZkManualInspectionCreateReqVO createReqVO) {
        // 插入
        ZkManualInspectionDO zkManualInspection = BeanUtils.toBean(createReqVO, ZkManualInspectionDO.class);
        zkManualInspectionMapper.insert(zkManualInspection);
        // 返回
        return zkManualInspection.getId();
    }

    @Override
    public void updateZkManualInspection(ZkManualInspectionUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkManualInspectionExists(updateReqVO.getId());
        // 更新
        ZkManualInspectionDO updateObj = BeanUtils.toBean(updateReqVO, ZkManualInspectionDO.class);
        zkManualInspectionMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkManualInspection(Long id) {
        // 校验存在
        validateZkManualInspectionExists(id);
        // 删除
        zkManualInspectionMapper.deleteById(id);
    }

    private void validateZkManualInspectionExists(Long id) {
        if (zkManualInspectionMapper.selectById(id) == null) {
            throw exception(ZK_MANUAL_INSPECTION_NOT_EXISTS);
        }
    }

    @Override
    public ZkManualInspectionDO getZkManualInspection(Long id) {
        return zkManualInspectionMapper.selectById(id);
    }

    @Override
    public List<ZkManualInspectionDO> getZkManualInspectionList(Collection<Long> ids) {
        return zkManualInspectionMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkManualInspectionDO> getZkManualInspectionPage(ZkManualInspectionPageReqVO pageReqVO) {
        return zkManualInspectionMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkManualInspectionDO> getZkManualInspectionList(ZkManualInspectionExportReqVO exportReqVO) {
        return zkManualInspectionMapper.selectList(exportReqVO);
    }

}
