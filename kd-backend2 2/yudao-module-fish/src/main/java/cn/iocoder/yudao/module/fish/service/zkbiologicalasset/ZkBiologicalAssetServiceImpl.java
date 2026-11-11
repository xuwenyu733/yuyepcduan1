package cn.iocoder.yudao.module.fish.service.zkbiologicalasset;

import cn.hutool.http.HtmlUtil;
import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.service.zkbiologicalasset.vo.OutPoolVo;
import cn.iocoder.yudao.module.fish.utils.refreshBioAssetUtile;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.StringUtils;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.ZkBiologicalAssetDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.DashboardStatDO.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.fish.dal.mysql.zkbiologicalasset.ZkBiologicalAssetMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 生物资产评估 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkBiologicalAssetServiceImpl implements ZkBiologicalAssetService {

    @Resource
    private ZkBiologicalAssetMapper zkBiologicalAssetMapper;

    @Autowired
    private AdminUserApi adminUserApi;
    @Resource
    private BaseDeptService baseDeptService;

    @Override
    public Long createZkBiologicalAsset(ZkBiologicalAssetCreateReqVO createReqVO) {
        // 插入
        ZkBiologicalAssetDO zkBiologicalAsset = BeanUtils.toBean(createReqVO, ZkBiologicalAssetDO.class);
        zkBiologicalAssetMapper.insert(zkBiologicalAsset);
        // 返回
        return zkBiologicalAsset.getId();
    }

    @Override
    public void updateZkBiologicalAsset(ZkBiologicalAssetUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkBiologicalAssetExists(updateReqVO.getId());
        // 更新
        ZkBiologicalAssetDO updateObj = BeanUtils.toBean(updateReqVO, ZkBiologicalAssetDO.class);
        zkBiologicalAssetMapper.updateById(updateObj);
    }

    @Override
    public void refreshZkBiologicalAssetByZkPond(ZkPondDO zkPondDO) {
        // 1. 根据 pondId 查找生物资产评估记录
        ZkBiologicalAssetDO zkBiologicalAssetDO = zkBiologicalAssetMapper.selectByPondId(zkPondDO.getId());

        // 如果不存在，则创建新的记录
        if (zkBiologicalAssetDO == null) {
            zkBiologicalAssetDO = new ZkBiologicalAssetDO();
            BeanUtils.copyProperties(zkPondDO, zkBiologicalAssetDO);
            zkBiologicalAssetDO.setId(null);
            zkBiologicalAssetMapper.insert(zkBiologicalAssetDO);
        }

        // 2. 按照字段对应关系更新属性
        // fish_num: zkPondDO.getFishNum() -> zkBiologicalAssetDO.setFishNum()
        if (zkPondDO.getFishNum() != null) {
            zkBiologicalAssetDO.setFishNum(zkPondDO.getFishNum().longValue());
        }

        // fish_spec: zkPondDO.getFishSpec() -> zkBiologicalAssetDO.setFishSpecification()
        if (zkPondDO.getFishSpec() != null) {
            zkBiologicalAssetDO.setFishSpecification(zkPondDO.getFishSpec().intValue());
        }

        // total_weight: zkPondDO.getTotalWeight() -> zkBiologicalAssetDO.setTotalWeight()
        zkBiologicalAssetDO.setTotalWeight(zkPondDO.getTotalWeight());

        // 3. 调用 refreshBioAsset 函数刷新生物资产属性
        zkBiologicalAssetDO = refreshBioAssetUtile.refreshBioAsset(zkBiologicalAssetDO);

        // 4. 更新或插入到数据库
        if (zkBiologicalAssetDO.getId() != null) {
            zkBiologicalAssetMapper.updateById(zkBiologicalAssetDO);
        } else {
            zkBiologicalAssetMapper.insert(zkBiologicalAssetDO);
        }
    }

    @Override
    public void deleteZkBiologicalAsset(Long id) {
        // 校验存在
        validateZkBiologicalAssetExists(id);
        // 删除
        zkBiologicalAssetMapper.deleteById(id);
    }

    private void validateZkBiologicalAssetExists(Long id) {
        if (zkBiologicalAssetMapper.selectById(id) == null) {
            throw exception(ZK_BIOLOGICAL_ASSET_NOT_EXISTS);
        }
    }

    @Override
    public ZkBiologicalAssetDO getZkBiologicalAsset(Long id) {
        return zkBiologicalAssetMapper.selectById(id);
    }

    @Override
    public List<ZkBiologicalAssetDO> getZkBiologicalAssetList(Collection<Long> ids) {
        return zkBiologicalAssetMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkBiologicalAssetDO> getZkBiologicalAssetPage(ZkBiologicalAssetPageReqVO pageReqVO) {
        return zkBiologicalAssetMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkBiologicalAssetDO> getZkBiologicalAssetList(ZkBiologicalAssetExportReqVO exportReqVO) {
        return zkBiologicalAssetMapper.selectList(exportReqVO);
    }

    @Override
    public ZkBiologicalAssetDashboardRespVO getDashboardData(String startDate, String endDate) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        List<Long> subDeptIds = baseDeptService.getSubDeptIds(adminUserRespDTO.getDeptId());
        ZkBiologicalAssetDashboardRespVO respVO = new ZkBiologicalAssetDashboardRespVO();

        LocalDate asOf = parseDateOrToday(endDate);
        LocalDate start = parseDateOrNull(startDate);
        if (start == null) {
            start = asOf;
        }
        respVO.setStartDate(start.toString());
        respVO.setAsOfDate(asOf.toString());

        ZkBiologicalAssetMapper.SnapshotDate snap = zkBiologicalAssetMapper.selectNearestSnapshotDate(
                asOf.getYear(), asOf.getMonthValue(), asOf.getDayOfMonth());
        if (snap == null || snap.getStaticYear() == null) {
            // 无快照时回落最新一日
            snap = zkBiologicalAssetMapper.selectNearestSnapshotDate(null, null, null);
        }
        Integer y;
        Integer m;
        Integer d;
        if (snap != null && snap.getStaticYear() != null) {
            y = snap.getStaticYear();
            m = snap.getStaticMonth();
            d = snap.getStaticDay();
            respVO.setSnapshotDate(String.format("%04d-%02d-%02d", y, m, d));
        } else {
            y = asOf.getYear();
            m = asOf.getMonthValue();
            d = asOf.getDayOfMonth();
            respVO.setSnapshotDate(asOf.toString());
        }

        // 1. Total Assets（按日快照；按部门过滤）
        TotalAssets totalAssetsMap = zkBiologicalAssetMapper.selectTotalAssets(subDeptIds, y, m, d);
        ZkBiologicalAssetDashboardRespVO.TotalAssets totalAssets = new ZkBiologicalAssetDashboardRespVO.TotalAssets();
        if (totalAssetsMap != null) {
            totalAssets.setFish(totalAssetsMap.getFishTotalMount() != null ? totalAssetsMap.getFishTotalMount() : BigDecimal.ZERO);
            // 需求 4.5：总资产仅展示生物性资产（鱼类）
            totalAssets.setTotal(totalAssets.getFish());
            totalAssets.setFishCount(totalAssetsMap.getFishTotalCount() != null ? totalAssetsMap.getFishTotalCount() : BigDecimal.ZERO);
        } else {
            totalAssets.setFish(BigDecimal.ZERO);
            totalAssets.setTotal(BigDecimal.ZERO);
            totalAssets.setFishCount(BigDecimal.ZERO);
        }
        respVO.setTotalAssets(totalAssets);

        // 2. Fish Species Distribution（按日快照）
        List<FishSpecies> fishSpeciesMapList = zkBiologicalAssetMapper.selectFishSpeciesDistribution(subDeptIds, y, m, d);
        List<ZkBiologicalAssetDashboardRespVO.FishSpecies> fishSpeciesList = new ArrayList<>();
        if (fishSpeciesMapList != null) {
            for (FishSpecies map : fishSpeciesMapList) {
                ZkBiologicalAssetDashboardRespVO.FishSpecies species = new ZkBiologicalAssetDashboardRespVO.FishSpecies();
                species.setName(plainText(map.getFishName(), "未知"));
                species.setQuantity(map.getTotalCount() != null ? map.getTotalCount() : BigDecimal.ZERO);
                species.setValue(BigDecimal.ZERO);
                fishSpeciesList.add(species);
            }
        }
        respVO.setFishBySpecies(fishSpeciesList);

        // 3. Pond Productivity（按日快照）
        List<PondProductivity> pondProductivityMapList = zkBiologicalAssetMapper.selectPondProductivity(subDeptIds, y, m, d);
        List<ZkBiologicalAssetDashboardRespVO.PondProductivity> pondProductivityList = new ArrayList<>();
        if (pondProductivityMapList != null) {
            for (PondProductivity map : pondProductivityMapList) {
                ZkBiologicalAssetDashboardRespVO.PondProductivity pond = new ZkBiologicalAssetDashboardRespVO.PondProductivity();
                pond.setName(plainText(map.getPondDetailNum(), "未知"));
                pond.setFish(map.getTotalMount() != null ? map.getTotalMount() : BigDecimal.ZERO);
                pond.setCapacity(map.getTotalWeight() != null ? map.getTotalWeight() : BigDecimal.ZERO);
                String spec = plainText(map.getFishSpec(), null);
                pond.setFishSpec(spec != null ? (spec.contains("g") ? spec : spec + "g/尾") : "未知");
                pond.setFishNum(map.getFishNum());
                pond.setWorkshopName(plainText(map.getWorkshopName(), "未知"));
                pond.setPondSystemName(plainText(map.getPondSystemName(), "未知"));
                pond.setBatchNumber(plainText(map.getBatchNumber(), "未知"));
                pond.setFishTypes(plainText(map.getFishTypes(), "未知"));
                pond.setFishName(plainText(map.getFishName(), pond.getFishTypes()));
                pondProductivityList.add(pond);
            }
        }
        respVO.setPondProductivity(pondProductivityList);

        // 4. Feed Consumption
        List<FeedConsumption> feedConsumptionMapList = zkBiologicalAssetMapper.selectFeedConsumption();
        List<ZkBiologicalAssetDashboardRespVO.FeedConsumption> feedConsumptionList = new ArrayList<>();
        if (feedConsumptionMapList != null) {
            for (FeedConsumption map : feedConsumptionMapList) {
                ZkBiologicalAssetDashboardRespVO.FeedConsumption feed = new ZkBiologicalAssetDashboardRespVO.FeedConsumption();
                String monthStr = map.getMonth() != null ? map.getMonth() + "月" : "";
                feed.setMonth(monthStr);
                feed.setAmount(map.getTotalFeedKg() != null ? map.getTotalFeedKg() : BigDecimal.ZERO);
                feedConsumptionList.add(feed);
            }
        }
        respVO.setFeedConsumption(feedConsumptionList);

        // 5. Key Performance Indicators
        ZkBiologicalAssetDashboardRespVO.KeyPerformanceIndicators kpi = new ZkBiologicalAssetDashboardRespVO.KeyPerformanceIndicators();

        BigDecimal avgLive = zkBiologicalAssetMapper.selectAvgLive();
        kpi.setAvgLive(avgLive != null ? avgLive.multiply(new BigDecimal("100")) : BigDecimal.ZERO);

        BigDecimal fcr = zkBiologicalAssetMapper.selectFeedConversionRatio();
        kpi.setFeedConversionRatio(fcr != null ? fcr : BigDecimal.ZERO);

        kpi.setWaterRecyclingRate(new BigDecimal("98.5"));

        BigDecimal dwg = zkBiologicalAssetMapper.selectDailyWeightGain();
        kpi.setDailyWeightGain(dwg != null ? dwg : BigDecimal.ZERO);
        // 6. 计算出池天数
        List<OutPoolVo> outPoolVos = zkBiologicalAssetMapper.selectRemainOutPoolTime();
        kpi.setEstimatedHarvestTime("45天");
        if (EasyListUtil.isNotEmpty(outPoolVos) && outPoolVos.get(0).getRemainingDays() != null) {
            kpi.setEstimatedHarvestTime(outPoolVos.get(0).getRemainingDays() + "天");
        }

        respVO.setKpi(kpi);

        return respVO;
    }

    private static LocalDate parseDateOrToday(String raw) {
        LocalDate parsed = parseDateOrNull(raw);
        return parsed != null ? parsed : LocalDate.now();
    }

    private static LocalDate parseDateOrNull(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            String s = raw.trim();
            if (s.length() >= 10) {
                s = s.substring(0, 10);
            }
            return LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    /** 去掉富文本 HTML，避免前端图表/筛选项直接露出 &lt;p&gt; 等标签 */
    private static String plainText(String value, String defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        String text = HtmlUtil.cleanHtmlTag(value);
        if (!StringUtils.hasText(text)) {
            return defaultValue;
        }
        return text.replaceAll("\\s+", " ").trim();
    }

}