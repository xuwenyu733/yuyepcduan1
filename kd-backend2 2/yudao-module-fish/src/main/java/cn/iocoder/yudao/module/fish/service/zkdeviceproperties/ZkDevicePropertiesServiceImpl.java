package cn.iocoder.yudao.module.fish.service.zkdeviceproperties;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceproperties.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceproperties.ZkDevicePropertiesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdeviceproperties.ZkDevicePropertiesConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdeviceproperties.ZkDevicePropertiesMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 设备属性记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkDevicePropertiesServiceImpl implements ZkDevicePropertiesService {

    @Resource
    private ZkDevicePropertiesMapper zkDevicePropertiesMapper;

    @Override
    public Long createZkDeviceProperties(ZkDevicePropertiesCreateReqVO createReqVO) {
        // 插入
        ZkDevicePropertiesDO zkDeviceProperties = BeanUtils.toBean(createReqVO, ZkDevicePropertiesDO.class);
        zkDevicePropertiesMapper.insert(zkDeviceProperties);
        // 返回
        return zkDeviceProperties.getId();
    }

    @Override
    public void updateZkDeviceProperties(ZkDevicePropertiesUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDevicePropertiesExists(updateReqVO.getId());
        // 更新
        ZkDevicePropertiesDO updateObj = BeanUtils.toBean(updateReqVO, ZkDevicePropertiesDO.class);
        zkDevicePropertiesMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkDeviceProperties(Long id) {
        // 校验存在
        validateZkDevicePropertiesExists(id);
        // 删除
        zkDevicePropertiesMapper.deleteById(id);
    }

    private void validateZkDevicePropertiesExists(Long id) {
        if (zkDevicePropertiesMapper.selectById(id) == null) {
            throw exception(ZK_DEVICE_PROPERTIES_NOT_EXISTS);
        }
    }

    @Override
    public ZkDevicePropertiesDO getZkDeviceProperties(Long id) {
        return zkDevicePropertiesMapper.selectById(id);
    }

    @Override
    public List<ZkDevicePropertiesDO> getZkDevicePropertiesList(Collection<Long> ids) {
        return zkDevicePropertiesMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDevicePropertiesDO> getZkDevicePropertiesPage(ZkDevicePropertiesPageReqVO pageReqVO) {
        return zkDevicePropertiesMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDevicePropertiesDO> getZkDevicePropertiesList(ZkDevicePropertiesExportReqVO exportReqVO) {
        return zkDevicePropertiesMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkDevicePropertiesDO> selectNeedDel(int recentDays) {
        return zkDevicePropertiesMapper.selectNeedDel(recentDays);
    }

    @Override
    public void customeDelete(List<ZkDevicePropertiesDO> propertiesDOS) {
        if(EasyListUtil.isEmpty(propertiesDOS)) return;
        Set<Long> collect = propertiesDOS.stream().map(ZkDevicePropertiesDO::getId).collect(Collectors.toSet());
        QueryWrapper<ZkDevicePropertiesDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ZkDevicePropertiesDO::getId, collect);
        zkDevicePropertiesMapper.delete(queryWrapper);
    }

}
