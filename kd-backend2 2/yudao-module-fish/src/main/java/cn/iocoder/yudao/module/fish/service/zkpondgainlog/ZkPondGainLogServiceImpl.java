package cn.iocoder.yudao.module.fish.service.zkpondgainlog;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondgainlog.ZkPondGainLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpondgainlog.ZkPondGainLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼生长 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkPondGainLogServiceImpl implements ZkPondGainLogService {

    @Resource
    private ZkPondGainLogMapper zkPondGainLogMapper;

    @Override
    public Long createZkPondGainLog(ZkPondGainLogSaveReqVO createReqVO) {
        // 插入
        ZkPondGainLogDO zkPondGainLog = BeanUtils.toBean(createReqVO, ZkPondGainLogDO.class);
        zkPondGainLogMapper.insert(zkPondGainLog);

        // 返回
        return zkPondGainLog.getId();
    }

    @Override
    public void updateZkPondGainLog(ZkPondGainLogSaveReqVO updateReqVO) {
        // 校验存在
        validateZkPondGainLogExists(updateReqVO.getId());
        // 更新
        ZkPondGainLogDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondGainLogDO.class);
        zkPondGainLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPondGainLog(Long id) {
        // 校验存在
        validateZkPondGainLogExists(id);
        // 删除
        zkPondGainLogMapper.deleteById(id);
    }

    @Override
        public void deleteZkPondGainLogListByIds(List<Long> ids) {
        // 删除
        zkPondGainLogMapper.deleteByIds(ids);
        }


    private void validateZkPondGainLogExists(Long id) {
        if (zkPondGainLogMapper.selectById(id) == null) {
            throw exception(ZK_POND_GAIN_LOG_NOT_EXISTS);
        }
    }

    @Override
    public ZkPondGainLogDO getZkPondGainLog(Long id) {
        return zkPondGainLogMapper.selectById(id);
    }

    @Override
    public PageResult<ZkPondGainLogDO> getZkPondGainLogPage(ZkPondGainLogPageReqVO pageReqVO) {
        return zkPondGainLogMapper.selectPage(pageReqVO);
    }

}