package cn.iocoder.yudao.module.fish.service.zkworkplan;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkplan.ZkWorkPlanDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkworkplan.ZkWorkPlanConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkworkplan.ZkWorkPlanMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 工作目标与计划 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkWorkPlanServiceImpl implements ZkWorkPlanService {

    @Resource
    private ZkWorkPlanMapper zkWorkPlanMapper;

    @Override
    public Long createZkWorkPlan(ZkWorkPlanCreateReqVO createReqVO) {
        // 插入
        ZkWorkPlanDO zkWorkPlan = BeanUtils.toBean(createReqVO, ZkWorkPlanDO.class);
        zkWorkPlanMapper.insert(zkWorkPlan);
        // 返回
        return zkWorkPlan.getId();
    }

    @Override
    public void updateZkWorkPlan(ZkWorkPlanUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkWorkPlanExists(updateReqVO.getId());
        // 更新
        ZkWorkPlanDO updateObj = BeanUtils.toBean(updateReqVO, ZkWorkPlanDO.class);
        zkWorkPlanMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkWorkPlan(Long id) {
        // 校验存在
        validateZkWorkPlanExists(id);
        // 删除
        zkWorkPlanMapper.deleteById(id);
    }

    private void validateZkWorkPlanExists(Long id) {
        if (zkWorkPlanMapper.selectById(id) == null) {
            throw exception(ZK_WORK_PLAN_NOT_EXISTS);
        }
    }

    @Override
    public ZkWorkPlanDO getZkWorkPlan(Long id) {
        return zkWorkPlanMapper.selectById(id);
    }

    @Override
    public List<ZkWorkPlanDO> getZkWorkPlanList(Collection<Long> ids) {
        return zkWorkPlanMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkWorkPlanDO> getZkWorkPlanPage(ZkWorkPlanPageReqVO pageReqVO) {
        return zkWorkPlanMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkWorkPlanDO> getZkWorkPlanList(ZkWorkPlanExportReqVO exportReqVO) {
        return zkWorkPlanMapper.selectList(exportReqVO);
    }

}
