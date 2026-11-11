package cn.iocoder.yudao.module.fish.service.zkfishhealth;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishhealth.ZkFishHealthDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkfishhealth.ZkFishHealthConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfishhealth.ZkFishHealthMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼类健康评估 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkFishHealthServiceImpl implements ZkFishHealthService {

    @Resource
    private ZkFishHealthMapper zkFishHealthMapper;

    @Override
    public Long createZkFishHealth(ZkFishHealthCreateReqVO createReqVO) {
        // 插入
        ZkFishHealthDO zkFishHealth = BeanUtils.toBean(createReqVO, ZkFishHealthDO.class);
        zkFishHealthMapper.insert(zkFishHealth);
        // 返回
        return zkFishHealth.getId();
    }

    @Override
    public void updateZkFishHealth(ZkFishHealthUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkFishHealthExists(updateReqVO.getId());
        // 更新
        ZkFishHealthDO updateObj = BeanUtils.toBean(updateReqVO, ZkFishHealthDO.class);
        zkFishHealthMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkFishHealth(Long id) {
        // 校验存在
        validateZkFishHealthExists(id);
        // 删除
        zkFishHealthMapper.deleteById(id);
    }

    private void validateZkFishHealthExists(Long id) {
        if (zkFishHealthMapper.selectById(id) == null) {
            throw exception(ZK_FISH_HEALTH_NOT_EXISTS);
        }
    }

    @Override
    public ZkFishHealthDO getZkFishHealth(Long id) {
        return zkFishHealthMapper.selectById(id);
    }

    @Override
    public List<ZkFishHealthDO> getZkFishHealthList(Collection<Long> ids) {
        return zkFishHealthMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkFishHealthDO> getZkFishHealthPage(ZkFishHealthPageReqVO pageReqVO) {
        return zkFishHealthMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkFishHealthDO> getZkFishHealthList(ZkFishHealthExportReqVO exportReqVO) {
        return zkFishHealthMapper.selectList(exportReqVO);
    }

}
