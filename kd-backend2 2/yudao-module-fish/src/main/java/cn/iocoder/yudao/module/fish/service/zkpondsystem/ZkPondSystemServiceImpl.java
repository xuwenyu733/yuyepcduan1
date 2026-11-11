package cn.iocoder.yudao.module.fish.service.zkpondsystem;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondsystem.ZkPondSystemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpondsystem.ZkPondSystemConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondsystem.ZkPondSystemMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 存塘系统 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkPondSystemServiceImpl implements ZkPondSystemService {

    @Resource
    private ZkPondSystemMapper zkPondSystemMapper;

    @Resource
    private BaseDeptService baseDeptService;

    @Override
    public Long createZkPondSystem(ZkPondSystemCreateReqVO createReqVO) {
        // 插入
        ZkPondSystemDO zkPondSystem = BeanUtils.toBean(createReqVO, ZkPondSystemDO.class);
        zkPondSystemMapper.insert(zkPondSystem);
        // 返回
        return zkPondSystem.getId();
    }

    @Override
    public void updateZkPondSystem(ZkPondSystemUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPondSystemExists(updateReqVO.getId());
        // 更新
        ZkPondSystemDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondSystemDO.class);
        zkPondSystemMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPondSystem(Long id) {
        // 校验存在
        validateZkPondSystemExists(id);
        // 删除
        zkPondSystemMapper.deleteById(id);
    }

    private void validateZkPondSystemExists(Long id) {
        if (zkPondSystemMapper.selectById(id) == null) {
            throw exception(ZK_POND_SYSTEM_NOT_EXISTS);
        }
    }

    @Override
    public ZkPondSystemDO getZkPondSystem(Long id) {
        return zkPondSystemMapper.selectById(id);
    }

    @Override
    public List<ZkPondSystemDO> getZkPondSystemList(Collection<Long> ids) {
        return zkPondSystemMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPondSystemDO> getZkPondSystemPage(ZkPondSystemPageReqVO pageReqVO) {
        baseDeptService.enhancerDeptIds(pageReqVO, pageReqVO.getDeptId(), "deptIds");
        return zkPondSystemMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPondSystemDO> getZkPondSystemList(ZkPondSystemExportReqVO exportReqVO) {
        return zkPondSystemMapper.selectList(exportReqVO);
    }

}
