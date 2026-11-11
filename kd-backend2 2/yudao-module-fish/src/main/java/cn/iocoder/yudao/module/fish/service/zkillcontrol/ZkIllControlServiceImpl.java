package cn.iocoder.yudao.module.fish.service.zkillcontrol;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkillcontrol.ZkIllControlDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkillcontrol.ZkIllControlConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkillcontrol.ZkIllControlMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 疾病诊断与防治记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkIllControlServiceImpl implements ZkIllControlService {

    @Resource
    private ZkIllControlMapper zkIllControlMapper;

    @Override
    public Long createZkIllControl(ZkIllControlCreateReqVO createReqVO) {
        // 插入
        ZkIllControlDO zkIllControl = BeanUtils.toBean(createReqVO, ZkIllControlDO.class);
        zkIllControlMapper.insert(zkIllControl);
        // 返回
        return zkIllControl.getId();
    }

    @Override
    public void updateZkIllControl(ZkIllControlUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkIllControlExists(updateReqVO.getId());
        // 更新
        ZkIllControlDO updateObj = BeanUtils.toBean(updateReqVO, ZkIllControlDO.class);
        zkIllControlMapper.updateById(updateObj);
    }

    @Override
    public ZkIllControlDO deleteZkIllControl(Long id) {
        // 校验存在
        ZkIllControlDO zkIllControlDO = validateZkIllControlExists(id);
        // 删除
        zkIllControlMapper.deleteById(id);
        return zkIllControlDO;
    }

    private ZkIllControlDO validateZkIllControlExists(Long id) {
        ZkIllControlDO zkIllControlDO = zkIllControlMapper.selectById(id);
        if (zkIllControlDO == null) {
            throw exception(ZK_ILL_CONTROL_NOT_EXISTS);
        }
        return zkIllControlDO;
    }

    @Override
    public ZkIllControlDO getZkIllControl(Long id) {
        return zkIllControlMapper.selectById(id);
    }

    @Override
    public List<ZkIllControlDO> getZkIllControlList(Collection<Long> ids) {
        return zkIllControlMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkIllControlDO> getZkIllControlPage(ZkIllControlPageReqVO pageReqVO) {
        return zkIllControlMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkIllControlDO> getZkIllControlList(ZkIllControlExportReqVO exportReqVO) {
        return zkIllControlMapper.selectList(exportReqVO);
    }

}
