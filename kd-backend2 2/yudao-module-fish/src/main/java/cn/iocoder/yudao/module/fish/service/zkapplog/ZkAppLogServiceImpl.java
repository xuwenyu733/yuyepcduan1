package cn.iocoder.yudao.module.fish.service.zkapplog;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkapplog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkapplog.ZkAppLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkapplog.ZkAppLogConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkapplog.ZkAppLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * app记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkAppLogServiceImpl implements ZkAppLogService {

    @Resource
    private ZkAppLogMapper zkAppLogMapper;

    @Override
    public Long createZkAppLog(ZkAppLogCreateReqVO createReqVO) {
        // 插入
        ZkAppLogDO zkAppLog = BeanUtils.toBean(createReqVO, ZkAppLogDO.class);
        zkAppLogMapper.insert(zkAppLog);
        // 返回
        return zkAppLog.getId();
    }

    @Override
    public void updateZkAppLog(ZkAppLogUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkAppLogExists(updateReqVO.getId());
        // 更新
        ZkAppLogDO updateObj = BeanUtils.toBean(updateReqVO, ZkAppLogDO.class);
        zkAppLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkAppLog(Long id) {
        // 校验存在
        validateZkAppLogExists(id);
        // 删除
        zkAppLogMapper.deleteById(id);
    }

    private void validateZkAppLogExists(Long id) {
        if (zkAppLogMapper.selectById(id) == null) {
            throw exception(ZK_APP_LOG_NOT_EXISTS);
        }
    }

    @Override
    public ZkAppLogDO getZkAppLog(Long id) {
        return zkAppLogMapper.selectById(id);
    }

    @Override
    public List<ZkAppLogDO> getZkAppLogList(Collection<Long> ids) {
        return zkAppLogMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkAppLogDO> getZkAppLogPage(ZkAppLogPageReqVO pageReqVO) {
        return zkAppLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkAppLogDO> getZkAppLogList(ZkAppLogExportReqVO exportReqVO) {
        return zkAppLogMapper.selectList(exportReqVO);
    }

}
