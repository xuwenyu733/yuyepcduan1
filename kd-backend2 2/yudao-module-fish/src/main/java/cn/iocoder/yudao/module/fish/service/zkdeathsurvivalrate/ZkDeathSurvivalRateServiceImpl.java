package cn.iocoder.yudao.module.fish.service.zkdeathsurvivalrate;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.stats.ZkDeathSurvivalRateStatsRespVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeathsurvivalrate.ZkDeathSurvivalRateDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdeathsurvivalrate.ZkDeathSurvivalRateConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdeathsurvivalrate.ZkDeathSurvivalRateMapper;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 死亡和存活率 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkDeathSurvivalRateServiceImpl implements ZkDeathSurvivalRateService {

    @Resource
    private ZkDeathSurvivalRateMapper zkDeathSurvivalRateMapper;
    @Autowired
    private AdminUserApi adminUserApi;
    @Resource
    private BaseDeptService baseDeptService;
    @Resource
    private DeptScopeSupport deptScopeSupport;

    @Override
    public Long createZkDeathSurvivalRate(ZkDeathSurvivalRateCreateReqVO createReqVO) {
        // 插入
        ZkDeathSurvivalRateDO zkDeathSurvivalRate = BeanUtils.toBean(createReqVO, ZkDeathSurvivalRateDO.class);
        zkDeathSurvivalRateMapper.insert(zkDeathSurvivalRate);
        // 返回
        return zkDeathSurvivalRate.getId();
    }

    @Override
    public void updateZkDeathSurvivalRate(ZkDeathSurvivalRateUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDeathSurvivalRateExists(updateReqVO.getId());
        // 更新
        ZkDeathSurvivalRateDO updateObj = BeanUtils.toBean(updateReqVO, ZkDeathSurvivalRateDO.class);
        zkDeathSurvivalRateMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkDeathSurvivalRate(Long id) {
        // 校验存在
        validateZkDeathSurvivalRateExists(id);
        // 删除
        zkDeathSurvivalRateMapper.deleteById(id);
    }

    private void validateZkDeathSurvivalRateExists(Long id) {
        if (zkDeathSurvivalRateMapper.selectById(id) == null) {
            throw exception(ZK_DEATH_SURVIVAL_RATE_NOT_EXISTS);
        }
    }

    @Override
    public ZkDeathSurvivalRateDO getZkDeathSurvivalRate(Long id) {
        return zkDeathSurvivalRateMapper.selectById(id);
    }

    @Override
    public List<ZkDeathSurvivalRateDO> getZkDeathSurvivalRateList(Collection<Long> ids) {
        return zkDeathSurvivalRateMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDeathSurvivalRateDO> getZkDeathSurvivalRatePage(ZkDeathSurvivalRatePageReqVO pageReqVO) {
        return zkDeathSurvivalRateMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDeathSurvivalRateDO> getZkDeathSurvivalRateList(ZkDeathSurvivalRateExportReqVO exportReqVO) {
        return zkDeathSurvivalRateMapper.selectList(exportReqVO);
    }

    @Override
    public ZkDeathSurvivalRateStatsRespVO getStatistics(Long deptId) {
        List<Long> deptIds = deptScopeSupport.resolveDeptIds(deptId);
        ZkDeathSurvivalRateStatsRespVO respVO = new ZkDeathSurvivalRateStatsRespVO();
        respVO.setTopStats(zkDeathSurvivalRateMapper.getTopStats(deptIds));
        Long sumFishNum = zkDeathSurvivalRateMapper.selectPondFishNum(deptIds);
        if (respVO.getTopStats() != null) {
            respVO.getTopStats().build_avgDeadPerEvent(sumFishNum);
        }
        respVO.setTrendStats(zkDeathSurvivalRateMapper.getTrendStats(deptIds));
        respVO.setPoolStatsList(zkDeathSurvivalRateMapper.getPoolStatsList(deptIds));
        return respVO;
    }

}
