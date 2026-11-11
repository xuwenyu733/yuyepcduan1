package cn.iocoder.yudao.module.fish.controller.admin.daping;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.*;
import cn.iocoder.yudao.module.fish.controller.admin.daping.to.*;
import cn.iocoder.yudao.module.fish.dal.mysql.daping.DaPingMapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.BaseDevice4IOT;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.water.WaterStatus4IOT;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo.ZkPaperRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
// import cn.iocoder.yudao.module.fish.convert.zkpaper.ZkPaperConvert;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper.ZkPaperDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn.ZkPondWarnDO;
import cn.iocoder.yudao.module.fish.service.custom.OutOrderProductService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import cn.iocoder.yudao.module.fish.service.zkpondwarn.ZkPondWarnService;
import cn.iocoder.yudao.module.fish.util.WaterMetricParser;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @创建人 lr
 * @创建时间 2025-03-21
 * @描述
 */
@Tag(name = "大屏模拟")
@RestController
@RequestMapping("/fish/zk-pond-warn")
@Validated
public class DaPingController {

    private static List<String> carNameList = Arrays.asList("基地1", "基地2", "基地3", "基地4", "基地5", "基地6");
    private static List<String> warnNameList = Arrays.asList("PH值过高", "氧气过低", "水温过低", "氨氮过低", "水位过低", "水温过高");
    private static List<String> handleNameList = Arrays.asList("待处理", "已处理", "待处理", "已处理", "待处理", "已处理");
    @Autowired
    private PlusRedisService plusRedisService;
    // 环境指标key
    public static String INDICATOR_KEY = "device:properties:*";
    public static String INDICATOR_LAST_KEY = "history:device:properties:";
    private static final String WATER_QUALITY_LIVE_KEY = "daping:water:quality:live:v2";
    /** 水质轻量接口 Redis 缓存；适当加长以减轻重复进页的 SQL 压力 */
    private static final long WATER_QUALITY_LIVE_CACHE_SEC = 45L;
    private static final String OVERVIEW_MONITOR_CACHE_PREFIX = "daping:overview:monitor:v2:";
    private static final long OVERVIEW_MONITOR_CACHE_SEC = 20L;
    public static String FISH_SHOW_TREE_KEY = "history:FISH_SHOW_TREE_KEY:"; // 后续 这里可以添加一个 区分不同基地(tenantId or deptId)
    @Resource
    private ZkPondWarnService zkPondWarnService;
    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;
    @Resource
    private OutOrderProductService outOrderProductService;
    @Resource
    private FishDetail2Node fishDetail2Node;
    @Resource
    private FishInfoTool fishInfoTool;
    @Resource
    private DaPingMapper daPingMapper;
    @Resource
    private DeptScopeSupport deptScopeSupport;

    @GetMapping("/getOverview3")
    @Operation(summary = "系统总览大屏-渔况智览6模块数据")
    @Parameter(name = "deptId", description = "归属部门（本部门+子部门）；不传则取登录用户部门", required = false)
    public CommonResult<DaPingOverview3RespVO> getOverview3(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        List<Long> deptIds = deptScopeSupport.resolveDeptIds(deptId);
        DaPingOverview3RespVO resp = new DaPingOverview3RespVO();
        List<BiologicalAssetWorthVO> biologicalAssets = daPingMapper.selectBiologicalAssetWorthList(deptIds);
        resp.setBiologicalAssets(biologicalAssets);
        BigDecimal totalWorth = BigDecimal.ZERO;
        if (EasyListUtil.isNotEmpty(biologicalAssets)) {
            for (BiologicalAssetWorthVO item : biologicalAssets) {
                if (item.getWorth() != null) {
                    totalWorth = totalWorth.add(item.getWorth());
                }
            }
        }
        resp.setTotalWorth(totalWorth);
        resp.setHealthList(daPingMapper.selectPondHealthStatusList(deptIds));
        resp.setMortalityStats(daPingMapper.selectMortalityStats(deptIds));
        resp.setPrecisionFeeding(daPingMapper.selectPrecisionFeeding(deptIds));
        resp.setGrowthCurve(daPingMapper.selectGrowthCurveList(deptIds));
        resp.setFeedCoefficient(daPingMapper.selectFeedCoefficient(deptIds));
        return success(resp);
    }

    @GetMapping("/getOverviewMonitor")
    @Operation(summary = "大屏监控面板：水质/设备/故障/费用（来自库表）")
    @Parameter(name = "scene", description = "场景：all|device|water；device 跳过全部水质查询以加速设备页", example = "device")
    @Parameter(name = "deptId", description = "归属部门（本部门+子部门）；不传则取登录用户部门", required = false)
    public CommonResult<DaPingOverviewMonitorRespVO> getOverviewMonitor(
            @RequestParam(value = "scene", required = false, defaultValue = "all") String scene,
            @RequestParam(value = "deptId", required = false) Long deptId) {
        String sceneKey = StringUtils.isBlank(scene) ? "all" : scene.trim().toLowerCase(Locale.ROOT);
        boolean deviceScene = "device".equals(sceneKey);
        boolean waterScene = "water".equals(sceneKey);
        List<Long> deptIds = deptScopeSupport.resolveDeptIds(deptId);
        String cacheKey = OVERVIEW_MONITOR_CACHE_PREFIX + sceneKey + ":" + deptScopeSupport.cacheDeptSuffix(deptId);
        try {
            DaPingOverviewMonitorRespVO cached = plusRedisService.selectObj(cacheKey, DaPingOverviewMonitorRespVO.class);
            if (cached != null) {
                return success(cached);
            }
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] cache read {}", ex.getMessage());
        }

        DaPingOverviewMonitorRespVO resp = buildOverviewMonitor(deviceScene, waterScene, deptIds);

        // 仅缓存有有效切片的结果，避免空包占住；设备场景缓存更长以加速重复进页
        boolean cacheable = (resp.getDevices() != null && !resp.getDevices().isEmpty())
                || (resp.getEnergy() != null && !resp.getEnergy().isEmpty())
                || (resp.getBackwash() != null && !resp.getBackwash().isEmpty());
        if (cacheable) {
            long cacheSec = deviceScene ? 45L : OVERVIEW_MONITOR_CACHE_SEC;
            try {
                plusRedisService.insertStr(cacheKey, JSON.toJSONString(resp), cacheSec);
            } catch (Exception ex) {
                org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] cache write {}", ex.getMessage());
            }
        }
        return success(resp);
    }

    @GetMapping("/getDeviceMonitorLive")
    @Operation(summary = "设备监控轻量接口：设备/能耗/面板（跳过水质查询以加速）")
    @Parameter(name = "deptId", description = "归属部门（本部门+子部门）；不传则取登录用户部门", required = false)
    public CommonResult<DaPingOverviewMonitorRespVO> getDeviceMonitorLive(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        return getOverviewMonitor("device", deptId);
    }

    /**
     * 按场景组装监控数据。
     * device：跳过全部水质块（实时+趋势），专供设备监控页加速。
     * water：仅强调水质（当前仍带设备块以兼容旧前端）。
     */
    private DaPingOverviewMonitorRespVO buildOverviewMonitor(boolean deviceScene, boolean waterScene,
                                                             List<Long> deptIds) {
        DaPingOverviewMonitorRespVO resp = new DaPingOverviewMonitorRespVO();
        // 无归属部门的普通用户：直接空结果，禁止走未带 dept 的 panel/能耗全站查询
        if (deptScopeSupport.isDeniedScope(deptIds)) {
            resp.setDevices(Collections.emptyList());
            resp.setFaultMonth(Collections.emptyList());
            resp.setEnergy(Collections.emptyList());
            resp.setEnergyNote("当前账号未设置归属部门，无法查看设备监控数据");
            resp.setEnergyTrend(Collections.emptyList());
            resp.setBackwash(Collections.emptyList());
            resp.setAerator(Collections.emptyList());
            resp.setCo2(Collections.emptyList());
            resp.setWaterFlow(Collections.emptyList());
            if (!deviceScene) {
                resp.setWaterRealtime(Collections.emptyList());
                resp.setWaterTrend7d(Collections.emptyList());
                resp.setWaterTrend30d(Collections.emptyList());
            }
            return resp;
        }
        boolean concreteDept = deptScopeSupport.hasConcreteDeptScope(deptIds);
        // 分块加载：单块失败不拖垮整页
        if (!deviceScene) {
            try {
                fillWaterLive(resp, deptIds);
                // 7d 用 6h 分桶、30d 按日，均以水质指标时间为锚
                resp.setWaterTrend7d(buildWaterTrend(daPingMapper.selectWaterTrend7d(deptIds)));
                resp.setWaterTrend30d(buildWaterTrend(daPingMapper.selectWaterTrend30d(deptIds)));
            } catch (Exception ex) {
                org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] water {}", ex.getMessage());
            }
        }
        // 设备状态：各场景均需要（water 场景暂不裁剪，保持页面效果）
        try {
            resp.setDevices(buildDeviceStatus(daPingMapper.selectDeviceStatusList(deptIds)));
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] devices {}", ex.getMessage());
        }
        // waterScene 预留：后续可跳过面板块；当前为兼容前端保持加载
        if (waterScene) {
            // no-op: keep device/panels for existing pages
        }
        try {
            // 有部门用户：只用带 deptIds 的设备推导/故障统计，禁止未过滤的 panel 全站种子
            List<DaPingMonitorRowVO.NameCountRow> panelFault = concreteDept
                    ? Collections.emptyList()
                    : daPingMapper.selectFaultFromPanel();
            if (panelFault != null && !panelFault.isEmpty()) {
                resp.setFaultMonth(buildFaultTop(panelFault));
            } else if (!deviceScene || concreteDept) {
                resp.setFaultMonth(buildFaultTop(daPingMapper.selectFaultTop90d(deptIds)));
            }
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] fault {}", ex.getMessage());
        }
        try {
            if (concreteDept) {
                // 电度/成本表无可靠部门字段：有部门用户不返回全站能耗，避免串部门
                resp.setEnergy(Collections.emptyList());
                resp.setEnergyNote("本部门暂无分部门能耗数据");
                resp.setEnergyTrend(Collections.emptyList());
            } else {
                List<DaPingMonitorRowVO.EnergyRow> energyRows = daPingMapper.selectEnergyFromMeterReading();
                String note;
                if (energyRows != null && !energyRows.isEmpty()) {
                    note = "来自电度表 zk_energy_meter_reading（与设备页同口径展示）";
                } else if (!deviceScene) {
                    energyRows = daPingMapper.selectEnergyFromOperatingCost();
                    note = "来自经营成本（元）；电度表暂无数据";
                    if (energyRows == null || energyRows.isEmpty()) {
                        energyRows = daPingMapper.selectEnergyFromPurifyCost();
                        note = "来自净化成本明细（元）；电度表暂无数据";
                    }
                } else {
                    note = "电度表暂无数据";
                }
                resp.setEnergy(buildEnergy(energyRows == null ? Collections.emptyList() : energyRows));
                resp.setEnergyNote(resp.getEnergy().isEmpty()
                        ? "暂无能耗数据（电度表/经营成本/净化成本均为空）"
                        : note);
                resp.setEnergyTrend(buildEnergyTrend(daPingMapper.selectEnergyTrendFromMeter()));
            }
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] energy {}", ex.getMessage());
            resp.setEnergy(Collections.emptyList());
            resp.setEnergyNote("能耗查询失败");
        }
        try {
            List<DaPingMonitorRowVO.BackwashRow> bw = concreteDept
                    ? daPingMapper.selectBackwashFromDevices(deptIds)
                    : daPingMapper.selectBackwashFromPanel();
            if (!concreteDept && (bw == null || bw.isEmpty())) {
                bw = daPingMapper.selectBackwashFromDevices(deptIds);
            }
            resp.setBackwash(buildBackwash(bw));
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] backwash {}", ex.getMessage());
        }
        try {
            List<DaPingMonitorRowVO.AeratorRow> ae = concreteDept
                    ? daPingMapper.selectAeratorFromDevices(deptIds)
                    : daPingMapper.selectAeratorFromPanel();
            if (!concreteDept && (ae == null || ae.isEmpty())) {
                ae = daPingMapper.selectAeratorFromDevices(deptIds);
            }
            resp.setAerator(buildAerator(ae));
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] aerator {}", ex.getMessage());
        }
        try {
            // CO₂ panel/时段表无 dept：部门用户暂不展示全站时段，避免串部门
            List<DaPingMonitorRowVO.Co2SegRow> co2 = concreteDept
                    ? Collections.emptyList()
                    : daPingMapper.selectCo2FromPanel();
            if (!concreteDept && (co2 == null || co2.isEmpty())) {
                co2 = daPingMapper.selectCo2Segments();
            }
            resp.setCo2(buildCo2(co2));
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] co2 {}", ex.getMessage());
        }
        try {
            List<DaPingMonitorRowVO.WaterFlowRow> wf = concreteDept
                    ? daPingMapper.selectWaterFlowFromDevices(deptIds)
                    : daPingMapper.selectWaterFlowFromPanel();
            if (!concreteDept && (wf == null || wf.isEmpty())) {
                wf = daPingMapper.selectWaterFlowFromDevices(deptIds);
            }
            resp.setWaterFlow(buildWaterFlow(wf));
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewMonitor] waterFlow {}", ex.getMessage());
        }
        return resp;
    }

    private List<DaPingOverviewMonitorRespVO.WaterMetricItem> buildWaterRealtime(
            List<DaPingMonitorRowVO.WaterMetricRow> rows) {
        Map<String, BigDecimal> latest = WaterMetricParser.parseLatest(rows);
        List<DaPingOverviewMonitorRespVO.WaterMetricItem> list = new ArrayList<>();
        list.add(waterItem("水温", "°C", latest.get("temp"), "10-30", "temp"));
        list.add(waterItem("pH值", "", latest.get("ph"), "6.5-8.5", "ph"));
        list.add(waterItem("溶解氧", "mg/L", latest.get("oxygen"), "≥5.0", "oxygen"));
        list.add(waterItem("氨氮", "mg/L", latest.get("ammonia"), "<0.5", "ammonia"));
        list.add(waterItem("浊度", "NTU", latest.get("turbidity"), "0-5", "turbidity"));
        list.add(waterItem("ORP", "mV", latest.get("orp"), "200-600", "orp"));
        list.add(waterItem("碱度", "mg/L", latest.get("alkalinity"), "80-250", "alkalinity"));
        list.add(waterItem("硬度", "mg/L", latest.get("hardness"), "100-300", "hardness"));
        list.add(waterItem("硝酸盐", "mg/L", latest.get("nitrate"), "0-35", "nitrate"));
        list.add(waterItem("二氧化碳", "mg/L", latest.get("co2"), "0-15", "co2"));
        list.add(waterItem("TSS", "mg/L", latest.get("tss"), "0-20", "tss"));
        list.add(waterItem("总磷", "mg/L", latest.get("tp"), "0-1", "tp"));
        list.add(waterItem("总氮", "mg/L", latest.get("tn"), "0-10", "tn"));
        if (latest.containsKey("nitrite")) {
            list.add(waterItem("亚硝酸盐", "mg/L", latest.get("nitrite"), "<0.1", "nitrite"));
        }
        return list;
    }

    /** 本部门有标注水质则用标注；否则回退历史 dept_id 为空的传感器数据（不串其它已标注部门） */
    private boolean waterMetricsHasValue(List<DaPingMonitorRowVO.WaterMetricRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return false;
        }
        for (DaPingMonitorRowVO.WaterMetricRow row : rows) {
            if (row != null && row.getMetricValue() != null) {
                return true;
            }
        }
        return false;
    }

    private void fillWaterLive(DaPingOverviewMonitorRespVO resp, List<Long> deptIds) {
        // 主路径：按鱼池/记录部门范围取数（见 DaPingMapper 联表）
        List<DaPingMonitorRowVO.WaterMetricRow> metrics = daPingMapper.selectLatestWaterMetrics(deptIds);
        // 仅当无部门限制（平台超管）且仍无数据时，才回退历史 dept_id IS NULL；
        // 有部门用户不再回退全站未标注数据，避免串部门 / 空结果被错误兜底掩盖。
        boolean useLegacy = (deptIds == null || deptIds.isEmpty()) && !waterMetricsHasValue(metrics);
        if (useLegacy) {
            resp.setWaterAsOf(daPingMapper.selectWaterAsOfLegacyNull());
            resp.setWaterRealtime(buildWaterRealtime(daPingMapper.selectLatestWaterMetricsLegacyNull()));
            resp.setWaterTrend(buildWaterTrend(daPingMapper.selectWaterTrend24hLegacyNull()));
        } else {
            resp.setWaterAsOf(daPingMapper.selectWaterAsOf(deptIds));
            resp.setWaterRealtime(buildWaterRealtime(metrics));
            resp.setWaterTrend(buildWaterTrend(daPingMapper.selectWaterTrend24h(deptIds)));
        }
    }

    @GetMapping("/getWaterQualityLive")
    @Operation(summary = "水质专题轻量接口：仅最新指标+24h趋势（加快水质页加载）")
    @Parameter(name = "deptId", description = "归属部门（本部门+子部门）；不传则取登录用户部门", required = false)
    public CommonResult<DaPingOverviewMonitorRespVO> getWaterQualityLive(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        List<Long> deptIds = deptScopeSupport.resolveDeptIds(deptId);
        String cacheKey = WATER_QUALITY_LIVE_KEY + ":" + deptScopeSupport.cacheDeptSuffix(deptId);
        try {
            DaPingOverviewMonitorRespVO cached = plusRedisService.selectObj(
                    cacheKey, DaPingOverviewMonitorRespVO.class);
            if (cached != null && cached.getWaterRealtime() != null && !cached.getWaterRealtime().isEmpty()) {
                return success(cached);
            }
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getWaterQualityLive] cache read {}", ex.getMessage());
        }
        DaPingOverviewMonitorRespVO resp = new DaPingOverviewMonitorRespVO();
        try {
            fillWaterLive(resp, deptIds);
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getWaterQualityLive] water {}", ex.getMessage());
        }
        try {
            if (resp.getWaterRealtime() != null && !resp.getWaterRealtime().isEmpty()) {
                plusRedisService.insertStr(cacheKey, JSON.toJSONString(resp), WATER_QUALITY_LIVE_CACHE_SEC);
            }
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getWaterQualityLive] cache write {}", ex.getMessage());
        }
        return success(resp);
    }

    private DaPingOverviewMonitorRespVO.WaterMetricItem waterItem(String name, String unit, BigDecimal value,
                                                                   String range, String key) {
        DaPingOverviewMonitorRespVO.WaterMetricItem item = new DaPingOverviewMonitorRespVO.WaterMetricItem();
        item.setName(name);
        item.setUnit(unit);
        item.setValue(value);
        item.setRange(range);
        item.setKey(key);
        item.setStatus(value == null ? "unknown" : "normal");
        return item;
    }

    private List<DaPingOverviewMonitorRespVO.WaterTrendItem> buildWaterTrend(
            List<DaPingMonitorRowVO.WaterTrendRow> rows) {
        List<DaPingOverviewMonitorRespVO.WaterTrendItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.WaterTrendRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.WaterTrendItem item = new DaPingOverviewMonitorRespVO.WaterTrendItem();
            item.setTime(row.getTimeBucket() == null ? "-" : row.getTimeBucket());
            item.setTemp(row.getTemp());
            item.setOxygen(row.getOxygen());
            item.setPh(row.getPh());
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.DeviceStatusItem> buildDeviceStatus(
            List<DaPingMonitorRowVO.DeviceStatusRow> rows) {
        List<DaPingOverviewMonitorRespVO.DeviceStatusItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.DeviceStatusRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.DeviceStatusItem item = new DaPingOverviewMonitorRespVO.DeviceStatusItem();
            item.setName(row.getName() == null ? "-" : row.getName());
            String statusRaw = row.getStatus() == null ? "" : row.getStatus();
            if ("1".equals(statusRaw)) item.setStatus("running");
            else if ("-1".equals(statusRaw)) item.setStatus("maintenance");
            else item.setStatus("stopped");
            item.setWorkshop(row.getWorkshop() == null ? "" : row.getWorkshop());
            item.setCurrValue(row.getCurrValue());
            item.setUnit(row.getUnit() == null ? "" : row.getUnit());
            item.setPondDetailNum(row.getPondDetailNum() == null ? "" : row.getPondDetailNum());
            item.setVoltage(null);
            item.setCurrent(item.getCurrValue());
            item.setFrequency(null);
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.NameCountItem> buildFaultTop(
            List<DaPingMonitorRowVO.NameCountRow> rows) {
        List<DaPingOverviewMonitorRespVO.NameCountItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.NameCountRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.NameCountItem item = new DaPingOverviewMonitorRespVO.NameCountItem();
            item.setName(row.getName() == null ? "-" : row.getName());
            item.setCount(row.getCnt() == null ? 0L : row.getCnt());
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.EnergyItem> buildEnergy(List<DaPingMonitorRowVO.EnergyRow> rows) {
        List<DaPingOverviewMonitorRespVO.EnergyItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.EnergyRow row : rows) {
            if (row == null || row.getValue() == null) continue;
            DaPingOverviewMonitorRespVO.EnergyItem item = new DaPingOverviewMonitorRespVO.EnergyItem();
            item.setName(row.getName());
            item.setValue(row.getValue());
            item.setColor(row.getColor() == null ? "#3b82f6" : row.getColor());
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.EnergyTrendItem> buildEnergyTrend(
            List<DaPingMonitorRowVO.EnergyTrendRow> rows) {
        List<DaPingOverviewMonitorRespVO.EnergyTrendItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.EnergyTrendRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.EnergyTrendItem item = new DaPingOverviewMonitorRespVO.EnergyTrendItem();
            item.setTime(row.getTimeBucket() == null ? "-" : row.getTimeBucket());
            item.setDeviceType(row.getDeviceType() == null ? "未分类" : row.getDeviceType());
            item.setValue(row.getValue());
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.BackwashItem> buildBackwash(
            List<DaPingMonitorRowVO.BackwashRow> rows) {
        List<DaPingOverviewMonitorRespVO.BackwashItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.BackwashRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.BackwashItem item = new DaPingOverviewMonitorRespVO.BackwashItem();
            item.setPool(row.getPool());
            item.setCount(row.getCount());
            item.setStatus(row.getStatus());
            item.setDevice(row.getDevice());
            item.setWorkshop(row.getWorkshop());
            item.setSystem(row.getSystem());
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.AeratorItem> buildAerator(
            List<DaPingMonitorRowVO.AeratorRow> rows) {
        List<DaPingOverviewMonitorRespVO.AeratorItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.AeratorRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.AeratorItem item = new DaPingOverviewMonitorRespVO.AeratorItem();
            item.setPool(row.getPool());
            item.setShare(row.getShare());
            item.setWorkshop(row.getWorkshop());
            item.setSystem(row.getSystem());
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.Co2SegItem> buildCo2(List<DaPingMonitorRowVO.Co2SegRow> rows) {
        List<DaPingOverviewMonitorRespVO.Co2SegItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.Co2SegRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.Co2SegItem item = new DaPingOverviewMonitorRespVO.Co2SegItem();
            item.setDevice(row.getDevice());
            item.setStart(row.getStartHm());
            item.setEnd(row.getEndHm());
            item.setWorkshop(row.getWorkshop());
            item.setSystem(row.getSystem());
            item.setModule(row.getModule());
            list.add(item);
        }
        return list;
    }

    private List<DaPingOverviewMonitorRespVO.WaterFlowItem> buildWaterFlow(
            List<DaPingMonitorRowVO.WaterFlowRow> rows) {
        List<DaPingOverviewMonitorRespVO.WaterFlowItem> list = new ArrayList<>();
        if (rows == null) return list;
        for (DaPingMonitorRowVO.WaterFlowRow row : rows) {
            if (row == null) continue;
            DaPingOverviewMonitorRespVO.WaterFlowItem item = new DaPingOverviewMonitorRespVO.WaterFlowItem();
            item.setName(row.getName());
            item.setFlow(row.getFlow());
            item.setWorkshop(row.getWorkshop());
            item.setSystem(row.getSystem());
            list.add(item);
        }
        return list;
    }

    @GetMapping("/getWarn")
    @Operation(summary = "获取系统警告")
    @Parameter(name = "workshopId", description = "基地id", required = false, example = "1024")
    public CommonResult<List<WarnVo>> getWarn(@RequestParam(value = "workshopId", required = false) String workshopId) {
        List<ZkPondWarnDO> dataList = zkPondWarnService.selectRecentInfo(7);
        List<WarnVo> list = WarnVo.build_byData(dataList);
        if (StringUtils.isNotBlank(workshopId)) {
            try {
                Long wid = Long.valueOf(workshopId);
                list = list.stream().filter(w -> wid.equals(w.getWorkshopId())).collect(Collectors.toList());
            } catch (NumberFormatException ignored) {
                // 非数字 workshopId 则返回全部
            }
        }
        return success(list);
    }

    @GetMapping("/getDeviceInfo")
    @Operation(summary = "获取基地设备信息")
    @Parameter(name = "workshopId", description = "基地id", required = true, example = "1024")
    public CommonResult<DeviceInfo> getDeviceInfo(@RequestParam("workshopId") Long workshopId) {
        String cacheLastKey = String.format("%s%s", INDICATOR_LAST_KEY, workshopId != null ? workshopId : "default");
        DeviceInfo lastInfo = plusRedisService.selectObj(cacheLastKey, DeviceInfo.class);
        DeviceInfo info = buildDeviceInfoFromMetrics(workshopId);
        info.build_upOrDown(lastInfo);
        plusRedisService.insertStr(cacheLastKey, JSON.toJSONString(info), 60 * 24L);
        return success(info);
    }

    @GetMapping("/getExtendedAlarms")
    @Operation(summary = "鱼类视觉/人员门禁扩展告警（预留表，无上游数据时为空）")
    @Parameter(name = "deptId", description = "归属部门；不传则取登录用户部门", required = false)
    public CommonResult<Map<String, Object>> getExtendedAlarms(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Long> deptIds = deptScopeSupport.resolveDeptIds(deptId);
        // 扩展告警表暂无可靠部门字段：无部门普通用户 / 有部门用户均不返回全站扩展告警，避免串部门
        if (deptScopeSupport.isDeniedScope(deptIds) || deptScopeSupport.hasConcreteDeptScope(deptIds)) {
            data.put("fish", Collections.emptyList());
            data.put("person", Collections.emptyList());
            return success(data);
        }
        try {
            data.put("fish", daPingMapper.selectFishVisionAlarms());
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getExtendedAlarms] fish {}", ex.getMessage());
            data.put("fish", Collections.emptyList());
        }
        try {
            data.put("person", daPingMapper.selectAccessAlarms());
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getExtendedAlarms] person {}", ex.getMessage());
            data.put("person", Collections.emptyList());
        }
        return success(data);
    }

    @PutMapping("/updateExtendedAlarm")
    @Operation(summary = "更新人员门禁/鱼类视觉扩展告警状态")
    public CommonResult<Boolean> updateExtendedAlarm(@RequestBody Map<String, Object> body) {
        if (body == null || body.get("id") == null) {
            return CommonResult.error(400, "缺少告警编号");
        }
        Long id;
        try {
            id = Long.valueOf(String.valueOf(body.get("id")));
        } catch (Exception ex) {
            return CommonResult.error(400, "告警编号格式错误");
        }
        String category = String.valueOf(body.getOrDefault("category", "person")).trim().toLowerCase(Locale.ROOT);
        String status = String.valueOf(body.getOrDefault("status", "pending")).trim().toLowerCase(Locale.ROOT);
        if ("done".equals(status) || "handled".equals(status)) {
            status = "completed";
        } else if ("exec".equals(status)) {
            status = "executing";
        }
        String ownerName = body.get("ownerName") == null ? "" : String.valueOf(body.get("ownerName"));
        int updated;
        try {
            if ("fish".equals(category)) {
                updated = daPingMapper.updateFishVisionAlarmStatus(id, status);
            } else {
                updated = daPingMapper.updateAccessAlarmStatus(id, status, ownerName);
            }
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[updateExtendedAlarm] {}", ex.getMessage());
            return CommonResult.error(500, "扩展告警表更新失败，请确认表结构已就绪");
        }
        if (updated <= 0) {
            return CommonResult.error(404, "未找到对应扩展告警");
        }
        return success(Boolean.TRUE);
    }

    @GetMapping("/getFishHealthSnapshot")
    @Operation(summary = "鱼健康页树形快照（库表 zk_fish_health_snapshot）")
    public CommonResult<Object> getFishHealthSnapshot(
            @RequestParam(value = "key", required = false, defaultValue = "default") String key) {
        String payload = daPingMapper.selectFishHealthSnapshot(key);
        if (StringUtils.isBlank(payload)) {
            return success(Collections.emptyList());
        }
        try {
            return success(JSON.parse(payload));
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getFishHealthSnapshot] {}", ex.getMessage());
            return success(Collections.emptyList());
        }
    }

    @GetMapping("/getChartSeed")
    @Operation(summary = "图表/考评种子数据（库表 zk_chart_seed）")
    public CommonResult<Object> getChartSeed(@RequestParam("key") String key) {
        String payload = daPingMapper.selectChartSeed(key);
        if (StringUtils.isBlank(payload)) {
            return success(null);
        }
        try {
            return success(JSON.parse(payload));
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getChartSeed] {}", ex.getMessage());
            return success(null);
        }
    }

    @GetMapping("/getOverviewSchedule")
    @Operation(summary = "总览日程事件（库表 zk_overview_schedule_event）")
    public CommonResult<List<DaPingMonitorRowVO.ScheduleEventRow>> getOverviewSchedule() {
        try {
            return success(daPingMapper.selectOverviewScheduleEvents());
        } catch (Exception ex) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("[getOverviewSchedule] {}", ex.getMessage());
            return success(Collections.emptyList());
        }
    }

    private DeviceInfo buildDeviceInfoFromMetrics(Long workshopId) {
        Map<String, BigDecimal> latest = WaterMetricParser.parseLatest(
                daPingMapper.selectLatestWaterMetrics(deptScopeSupport.resolveDeptIds(null)));
        DeviceInfo info = new DeviceInfo(workshopId);
        DeviceInfo.ItemVo itemVo1 = new DeviceInfo.ItemVo("水位",
                WaterMetricParser.formatPlain(latest.get("level")), "m", "up");
        itemVo1.setKey("shuiWei");
        DeviceInfo.ItemVo itemVo2 = new DeviceInfo.ItemVo("水温",
                WaterMetricParser.formatPlain(latest.get("temp")), "℃", "up");
        itemVo2.setKey("shuiWen");
        DeviceInfo.ItemVo itemVo3 = new DeviceInfo.ItemVo("酸碱度",
                WaterMetricParser.formatPlain(latest.get("ph")), "PH", "up");
        itemVo3.setKey("suanJianDu");
        DeviceInfo.ItemVo itemVo4 = new DeviceInfo.ItemVo("溶氧浓度",
                WaterMetricParser.formatPlain(latest.get("oxygen")), "mg/l", "up");
        itemVo4.setKey("rongYangNongDu");
        DeviceInfo.ItemVo itemVo5 = new DeviceInfo.ItemVo("氨氮",
                WaterMetricParser.formatPlain(latest.get("ammonia")), "mg/l", "up");
        itemVo5.setKey("anDan");
        info.setItems(Arrays.asList(itemVo1, itemVo2, itemVo3, itemVo4, itemVo5));
        return info;
    }

    /**
     * 对接IOT传输上来的值
     * @return
     */
    public WaterStatus4IOT selectWaterInfo() {
        // 1. 从redis中获取指标值
        List<String> childrenByParent = plusRedisService.getChildrenByParent(INDICATOR_KEY);
        List<WaterStatus4IOT> res = new ArrayList<>();
        for(String item : childrenByParent) {
            if(StringUtils.isEmpty(item)) continue;
            try{
                // System.out.println(String.format("item 1==========>%s", item));
                JSONObject jsonObject = JSON.parseObject(item);
                if(jsonObject == null) continue;
                BaseDevice4IOT baseDevice4IOT = BaseDevice4IOT.buildByJson(jsonObject);
                List<WaterStatus4IOT> waterStatus4IOTList = WaterStatus4IOT.buildByJson4List(jsonObject);
                if(EasyListUtil.isNotEmpty(waterStatus4IOTList)) {
                    waterStatus4IOTList.forEach(obj -> obj.build_baseDevice4IOT(baseDevice4IOT));
                }
                res.addAll(waterStatus4IOTList);
                // System.out.println(String.format("item 2==========>%s", JSON.toJSONString(waterStatus4IOTList)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 2. 正常应该和我们的池子进行对应，然后前台根据池子来输出，这里选择一个值不为0的返回
        WaterStatus4IOT finalRes = null;
        if(EasyListUtil.isEmpty(res)) return finalRes;
        for(WaterStatus4IOT item : res) {
            if(EasyBigDecimalUtil.gt0(new BigDecimal(item.getDoOxygen()))) {
                finalRes = item;
                break;
            }
        }
        return finalRes;
    }

    /**
     * 存量分布大屏：对普通员工开放查看（关闭部门数据权限过滤）。
     * 空数据返回空列表，避免前端误判为 403 加载失败。
     */
    @GetMapping("/getFishBatch")
    @Operation(summary = "获取鱼苗批次信息")
    @Parameter(name = "deptId", description = "基地id", required = false, example = "1024")
    @DataPermission(enable = false)
    public CommonResult<List<FishTree>> getFishBatch(@RequestParam(value = "deptId", required = false) String deptId) {
        System.out.println("基地id=============>"+deptId);
        try {
            // 1. 获取投放记录 ==> tree2 origin data
            List<ZkPondFishDetailDO> recentDayList = zkPondFishDetailService.getRecentDayList(PondFishDetailAddTypeEnum.ADD.getAddType());
            if (EasyListUtil.isEmpty(recentDayList)) {
                return success(Collections.emptyList());
            }
            Map<String, List<ZkPondFishDetailDO>> collect = recentDayList.stream().filter(obj -> StringUtils.isNotBlank(obj.getBatchNumber()))
                    .collect(Collectors.groupingBy(ZkPondFishDetailDO::getBatchNumber));
            Set<String> batchNumberSet = collect.keySet();
            // 2. 找 父节点 信息 ==> tree1 data add
            List<ErpPurchaseInItemDO> outOrderProductList = outOrderProductService.getListByRelationOrderNoSet(batchNumberSet);
            if (EasyListUtil.isEmpty(outOrderProductList)) {
                return success(Collections.emptyList());
            }
            List<FishTree> roots = new ArrayList<>();
            for (ErpPurchaseInItemDO item : outOrderProductList) {
                if (item == null || item.getErpProductDO() == null) {
                    continue;
                }
                FishNode fishNode = new FishNode(item.getRelationOrderNo(), item.getRemainingInventory(), item.getErpProductDO().getStandard()).build_Purchase(item);
                // String transferTime, String transferQuantity, String transferFishSpec
                FishNodeDetail fishNodeDetail = new FishNodeDetail(EasyDateUtil.format(item.getCreateTime(), "yyyy.MM.dd"), item.getCount()+"", item.getErpProductDO().getStandard());
                fishNode.build_fishNodeDetailList(fishNodeDetail);
                FishTree fishTree = new FishTree().addRoot(fishNode);
                roots.add(fishTree);
                fishInfoTool.enhancerRoot(fishNode, collect);
            }
            // 3. 添加二级节点  ==> tree2 data add
            for (FishTree fishTree : roots) {
                String originBatchNumber = fishTree.getRoot().getOriginBatchNumber();
                List<ZkPondFishDetailDO> detailDOS = collect.get(originBatchNumber);
                fishDetail2Node.selectByFishDetail(detailDOS, fishTree);
            }
            // 4. 处理分池记录和分池节点指标信息填充 ==> tree3+ data add
            fishDetail2Node.handleSplitByTrees(3, roots);
            System.out.println(String.format("getFishBatch 9==================>roots:%s", JSON.toJSONString(roots)));
            fishDetail2Node.normalEnhancer4TreeNodes(roots); // 节点信息汇总，son node向root汇总
            // 5. 指标上升or下降（缓存失败不影响主流程）
            try {
                handleUpOrDown(roots);
            } catch (Exception e) {
                System.out.println("getFishBatch handleUpOrDown skip: " + e.getMessage());
            }
            roots = fishDetail2Node.treeNodesRemoveEmpty(roots);
            return success(roots == null ? Collections.emptyList() : roots);
        } catch (Exception e) {
            e.printStackTrace();
            // 不向普通用户抛权限/内部异常，保证页面可打开
            return success(Collections.emptyList());
        }
    }

    public void handleUpOrDown(List<FishTree> roots) {
        try {
            // 1. 从缓存获取数据
            LocalDateTime yesterday = EasyDateUtil.plusDay(LocalDateTime.now(), -1);
            String yesterdayStr = EasyDateUtil.format(yesterday, "yyyy-MM-dd");
            String yesterdayKey = String.format("%s%s", FISH_SHOW_TREE_KEY, yesterdayStr);
            List<FishTree> historyRoots = plusRedisService.selectArr(yesterdayKey, FishTree.class);
            if(EasyListUtil.isNotEmpty(historyRoots)) {
                // 2. 处理树
                fishDetail2Node.handleUpOrDown(historyRoots, roots);
            }
            // 3. 缓存进历史
            String dayStr = EasyDateUtil.format(LocalDateTime.now(), "yyyy-MM-dd");
            String dayKey = String.format("%s%s", FISH_SHOW_TREE_KEY, dayStr);
            plusRedisService.insertStr(dayKey, JSON.toJSONString(roots), 60*24*2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
