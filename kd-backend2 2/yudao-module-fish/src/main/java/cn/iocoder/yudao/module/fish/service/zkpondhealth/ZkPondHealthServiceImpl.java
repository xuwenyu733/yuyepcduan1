package cn.iocoder.yudao.module.fish.service.zkpondhealth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.stats.ZkDeathSurvivalRateStatsRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to.DeathScoreContext;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to.PondHealthScoreCalculator;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to.PondHealthScoreConstants;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkdeathsurvivalrate.ZkDeathSurvivalRateMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondhealth.ZkPondHealthMapper;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼池健康 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkPondHealthServiceImpl implements ZkPondHealthService {

    @Resource
    private ZkPondHealthMapper zkPondHealthMapper;

    @Resource
    private ZkDeathSurvivalRateMapper zkDeathSurvivalRateMapper;

    @Resource
    private DeptScopeSupport deptScopeSupport;

    /**
     * 单日死亡尾数预警阈值（与健康评分死亡分项、死亡页默认阈值对齐）
     */
    @Value("${yudao.fish.health.death-warn-daily-count:10}")
    private Integer deathWarnDailyCount;

    @Override
    public Long createZkPondHealth(ZkPondHealthSaveReqVO createReqVO) {
        // 插入
        ZkPondHealthDO zkPondHealth = BeanUtils.toBean(createReqVO, ZkPondHealthDO.class);
        zkPondHealthMapper.insert(zkPondHealth);

        // 返回
        return zkPondHealth.getId();
    }

    @Override
    public void updateZkPondHealth(ZkPondHealthSaveReqVO updateReqVO) {
        // 校验存在
        validateZkPondHealthExists(updateReqVO.getId());
        // 更新
        ZkPondHealthDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondHealthDO.class);
        zkPondHealthMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPondHealth(Long id) {
        // 校验存在
        validateZkPondHealthExists(id);
        // 删除
        zkPondHealthMapper.deleteById(id);
    }

    @Override
        public void deleteZkPondHealthListByIds(List<Long> ids) {
        // 删除
        zkPondHealthMapper.deleteByIds(ids);
        }


    private void validateZkPondHealthExists(Long id) {
        if (zkPondHealthMapper.selectById(id) == null) {
            throw exception(ZK_POND_HEALTH_NOT_EXISTS);
        }
    }

    @Override
    public ZkPondHealthDO getZkPondHealth(Long id) {
        return zkPondHealthMapper.selectById(id);
    }

    @Override
    public PageResult<ZkPondHealthDO> getZkPondHealthPage(ZkPondHealthPageReqVO pageReqVO) {
        return zkPondHealthMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<String, Object> getHealthStatusDistribution() {
        // 基于「各池最新一条」按需求等级重算，避免历史全表混算
        List<ZkPondHealthDO> latest = getLatestPondHealthList();
        long excellent = 0;
        long good = 0;
        long attention = 0;
        for (ZkPondHealthDO item : latest) {
            BigDecimal score = item.getOverallRating() == null ? BigDecimal.ZERO : item.getOverallRating();
            if (score.compareTo(PondHealthScoreConstants.GRADE_EXCELLENT_MIN) >= 0) {
                excellent++;
            } else if (score.compareTo(PondHealthScoreConstants.GRADE_GOOD_MIN) >= 0) {
                good++;
            } else {
                attention++;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("excellentCount", excellent);
        map.put("goodCount", good);
        map.put("attentionCount", attention);
        map.put("totalCount", latest.size());
        map.put("deathWarnDailyCount", resolvedDeathWarnThreshold());
        return map;
    }

    @Override
    public List<ZkPondHealthDO> getLatestPondHealthList() {
        List<Long> deptIds = deptScopeSupport.resolveDeptIds(null);
        List<ZkPondHealthDO> list = zkPondHealthMapper.selectLatestPondHealthList(deptIds);
        if (list == null || list.isEmpty()) {
            return list;
        }
        Map<Long, Integer> todayDeathByPond = loadTodayDeathByPond();
        int warn = resolvedDeathWarnThreshold();
        for (ZkPondHealthDO item : list) {
            Integer todayDead = item.getPondId() == null ? null : todayDeathByPond.get(item.getPondId());
            PondHealthScoreCalculator.enrich(item, DeathScoreContext.builder()
                    .deathWarnDailyCount(warn)
                    .todayDeathCount(todayDead != null ? todayDead : 0)
                    .build());
        }
        return list;
    }

    @Override
    public void customSave(ZkPondHealthDO res) {
        if (res == null) {
            return;
        }
        Integer todayDead = null;
        if (res.getPondId() != null) {
            todayDead = loadTodayDeathByPond().getOrDefault(res.getPondId(), 0);
        }
        PondHealthScoreCalculator.enrich(res, DeathScoreContext.builder()
                .deathWarnDailyCount(resolvedDeathWarnThreshold())
                .todayDeathCount(todayDead)
                .build());
        zkPondHealthMapper.insert(res);
    }

    private int resolvedDeathWarnThreshold() {
        if (deathWarnDailyCount == null || deathWarnDailyCount < 0) {
            return PondHealthScoreConstants.DEFAULT_DEATH_WARN_THRESHOLD;
        }
        return deathWarnDailyCount;
    }

    private Map<Long, Integer> loadTodayDeathByPond() {
        Map<Long, Integer> map = new HashMap<>();
        try {
            List<ZkDeathSurvivalRateStatsRespVO.PoolStats> pools =
                    zkDeathSurvivalRateMapper.getPoolStatsList(deptScopeSupport.resolveDeptIds(null));
            if (pools == null) {
                return map;
            }
            for (ZkDeathSurvivalRateStatsRespVO.PoolStats p : pools) {
                if (p.getPondId() == null) {
                    continue;
                }
                map.put(p.getPondId(), p.getDayDead() == null ? 0 : p.getDayDead());
            }
        } catch (Exception ignored) {
            // 死亡统计异常时不阻断健康列表
        }
        return map;
    }

}
