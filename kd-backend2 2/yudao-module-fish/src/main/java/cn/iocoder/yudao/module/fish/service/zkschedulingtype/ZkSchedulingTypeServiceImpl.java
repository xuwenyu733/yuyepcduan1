package cn.iocoder.yudao.module.fish.service.zkschedulingtype;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkschedulingtype.ZkSchedulingTypeConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkschedulingtype.ZkSchedulingTypeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 检测配置 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkSchedulingTypeServiceImpl implements ZkSchedulingTypeService {

    @Resource
    private ZkSchedulingTypeMapper zkSchedulingTypeMapper;

    @Override
    public Long createZkSchedulingType(ZkSchedulingTypeCreateReqVO createReqVO) {
        // 插入
        ZkSchedulingTypeDO zkSchedulingType = BeanUtils.toBean(createReqVO, ZkSchedulingTypeDO.class);
        zkSchedulingTypeMapper.insert(zkSchedulingType);
        // 返回
        return zkSchedulingType.getId();
    }

    @Override
    public void updateZkSchedulingType(ZkSchedulingTypeUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkSchedulingTypeExists(updateReqVO.getId());
        // 更新
        ZkSchedulingTypeDO updateObj = BeanUtils.toBean(updateReqVO, ZkSchedulingTypeDO.class);
        zkSchedulingTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkSchedulingType(Long id) {
        // 校验存在
        validateZkSchedulingTypeExists(id);
        // 删除
        zkSchedulingTypeMapper.deleteById(id);
    }

    private void validateZkSchedulingTypeExists(Long id) {
        if (zkSchedulingTypeMapper.selectById(id) == null) {
            throw exception(ZK_SCHEDULING_TYPE_NOT_EXISTS);
        }
    }

    @Override
    public ZkSchedulingTypeDO getZkSchedulingType(Long id) {
        return zkSchedulingTypeMapper.selectById(id);
    }

    @Override
    public List<ZkSchedulingTypeDO> getZkSchedulingTypeList(Collection<Long> ids) {
        return zkSchedulingTypeMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkSchedulingTypeDO> getZkSchedulingTypePage(ZkSchedulingTypePageReqVO pageReqVO) {
        return zkSchedulingTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkSchedulingTypeDO> getZkSchedulingTypeList(ZkSchedulingTypeExportReqVO exportReqVO) {
        return zkSchedulingTypeMapper.selectList(exportReqVO);
    }

}
