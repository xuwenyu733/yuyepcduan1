package cn.iocoder.yudao.module.fish.service.zknetbox;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zknetbox.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zknetbox.ZkNetBoxDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zknetbox.ZkNetBoxConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zknetbox.ZkNetBoxMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 网盘资源 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkNetBoxServiceImpl implements ZkNetBoxService {

    @Resource
    private ZkNetBoxMapper zkNetBoxMapper;

    @Override
    public Long createZkNetBox(ZkNetBoxCreateReqVO createReqVO) {
        // 插入
        ZkNetBoxDO zkNetBox = BeanUtils.toBean(createReqVO, ZkNetBoxDO.class);
        zkNetBoxMapper.insert(zkNetBox);
        // 返回
        return zkNetBox.getId();
    }

    @Override
    public void updateZkNetBox(ZkNetBoxUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkNetBoxExists(updateReqVO.getId());
        // 更新
        ZkNetBoxDO updateObj = BeanUtils.toBean(updateReqVO, ZkNetBoxDO.class);
        zkNetBoxMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkNetBox(Long id) {
        // 校验存在
        validateZkNetBoxExists(id);
        // 删除
        zkNetBoxMapper.deleteById(id);
    }

    private void validateZkNetBoxExists(Long id) {
        if (zkNetBoxMapper.selectById(id) == null) {
            throw exception(ZK_NET_BOX_NOT_EXISTS);
        }
    }

    @Override
    public ZkNetBoxDO getZkNetBox(Long id) {
        return zkNetBoxMapper.selectById(id);
    }

    @Override
    public List<ZkNetBoxDO> getZkNetBoxList(Collection<Long> ids) {
        return zkNetBoxMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkNetBoxDO> getZkNetBoxPage(ZkNetBoxPageReqVO pageReqVO) {
        return zkNetBoxMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkNetBoxDO> getZkNetBoxList(ZkNetBoxExportReqVO exportReqVO) {
        return zkNetBoxMapper.selectList(exportReqVO);
    }

}
