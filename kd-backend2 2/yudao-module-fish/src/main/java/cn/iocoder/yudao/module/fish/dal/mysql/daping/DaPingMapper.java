package cn.iocoder.yudao.module.fish.dal.mysql.daping;

import cn.iocoder.yudao.module.fish.controller.admin.daping.to.*;
import cn.iocoder.yudao.module.fish.utils.DeptSqlFragments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DaPingMapper {

    @Select("<script>SELECT " +
            "    t1.fish_type AS fishType, " +
            "    sdd.`label` AS fishName, " +
            "    CAST(sdd.`remark` AS DECIMAL(10,2)) * t1.weight AS worth " +
            "FROM ( " +
            "    SELECT fish_type, SUM(weight) AS weight " +
            "    FROM ( " +
            "        SELECT " +
            "            zpft.fish_type, " +
            "            zpft.fish_num, " +
            "            zp.fish_spec, " +
            "            zp.pond_detail_num, " +
            "            (zp.fish_spec * zpft.fish_num / 1000) AS weight " +
            "        FROM zk_pond_fish_type zpft " +
            "        LEFT JOIN zk_pond zp ON zp.id = zpft.pond_id " +
            "        WHERE zpft.static_year = YEAR(NOW()) " +
            "          AND zpft.static_month = MONTH(NOW()) " +
            "          AND zpft.static_day = DAY(NOW()) " +
            "          AND zp.id IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "        UNION ALL " +
            "        SELECT " +
            "            zpft.fish_type, " +
            "            zpft.fish_num, " +
            "            zp.fish_spec, " +
            "            zp.pond_detail_num, " +
            "            (zp.fish_spec * zpft.fish_num / 1000) AS weight " +
            "        FROM zk_pond_fish_type zpft " +
            "        LEFT JOIN zk_pond zp ON zp.id = zpft.pond_id " +
            "        WHERE NOT EXISTS ( " +
            "            SELECT 1 FROM zk_pond_fish_type t " +
            "            WHERE t.static_year = YEAR(NOW()) " +
            "              AND t.static_month = MONTH(NOW()) " +
            "              AND t.static_day = DAY(NOW()) " +
            "        ) " +
            "          AND zp.id IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "          AND (zpft.static_year, zpft.static_month, zpft.static_day) = ( " +
            "            SELECT static_year, static_month, static_day " +
            "            FROM zk_pond_fish_type " +
            "            WHERE static_year &gt;= 2000 " +
            "            ORDER BY static_year DESC, static_month DESC, static_day DESC " +
            "            LIMIT 1 " +
            "          ) " +
            "    ) AS t1 " +
            "    GROUP BY fish_type " +
            ") t1 " +
            "LEFT JOIN system_dict_data sdd ON sdd.value COLLATE utf8mb4_0900_ai_ci = t1.fish_type " +
            "    AND sdd.`dict_type` = 'zk_pond_fish_types'</script>")
    List<BiologicalAssetWorthVO> selectBiologicalAssetWorthList(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT " +
            "    zph.pond_id AS pondId, " +
            "    CONCAT(zp.workshop_name, '-', zp.pond_system_name, '-', zp.pond_name) AS pondName, " +
            "    CASE " +
            "        WHEN zph.number_of_exceptions = 0 THEN '健康' " +
            "        WHEN zph.number_of_exceptions &gt; 0 THEN '不健康' " +
            "    END AS healthStatus " +
            "FROM zk_pond_health zph " +
            "LEFT JOIN zk_pond zp ON zp.id = zph.pond_id " +
            "WHERE zph.id IN ( " +
            "    SELECT MAX(id) FROM zk_pond_health GROUP BY pond_id " +
            ") AND zp.id IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "</script>")
    List<PondHealthStatusVO> selectPondHealthStatusList(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT " +
            "    t1.dayDead, " +
            "    (t1.dayDead / t2.fishSum) AS dayDeadPer, " +
            "    t1.sumDead, " +
            "    (t1.sumDead / t2.fishSum) AS sumDeadPer, " +
            "    t2.fishSum " +
            "FROM ( " +
            "    SELECT " +
            "        (SELECT 1) AS id, " +
            "        SUM(CASE WHEN DATE(zpfd.create_time) = DATE(NOW()) THEN zpfd.fish_num ELSE 0 END) AS dayDead, " +
            "        SUM(zpfd.fish_num) AS sumDead " +
            "    FROM zk_pond_fish_detail zpfd " +
            "    LEFT JOIN zk_pond zp ON zp.id = zpfd.pond_id " +
            "    WHERE zpfd.add_type = '-2' AND zp.id IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "    GROUP BY zpfd.add_type " +
            ") t1 " +
            "LEFT JOIN ( " +
            "    SELECT (SELECT 1) AS id, SUM(fish_num) AS fishSum FROM zk_pond zp WHERE 1=1 " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            ") t2 ON t1.id = t2.id " +
            "LIMIT 1</script>")
    MortalityStatsVO selectMortalityStats(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT " +
            "    totalWeight, " +
            "    dayFeedCount, " +
            "    CASE WHEN totalWeight IS NULL OR totalWeight = 0 THEN 0 ELSE dayFeedCount / totalWeight END AS feedRate " +
            "FROM ( " +
            "    SELECT ( " +
            "        SELECT COALESCE(SUM(total_weight), 0) FROM zk_pond WHERE 1=1 " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "    ) AS totalWeight, ( " +
            "        SELECT COALESCE( " +
            "            (SELECT SUM(feed_count) FROM zk_feeding_log WHERE DATE(feed_time) = DATE(NOW()) " +
            DeptSqlFragments.AND_DEPT_ID_IN + "), " +
            "            (SELECT SUM(feed_count) FROM zk_feeding_log WHERE DATE(feed_time) = ( " +
            "                SELECT DATE(feed_time) FROM zk_feeding_log WHERE 1=1 " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            " ORDER BY feed_time DESC LIMIT 1 " +
            "            ) " +
            DeptSqlFragments.AND_DEPT_ID_IN + "), " +
            "            0 " +
            "        ) " +
            "    ) AS dayFeedCount " +
            ") t1</script>")
    PrecisionFeedingVO selectPrecisionFeeding(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT " +
            "    static_day AS staticDay, " +
            "    SUM(fish_gain_count) AS fishGainCount " +
            "FROM zk_pond_gain_log WHERE 1=1 " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            " GROUP BY static_day " +
            "ORDER BY static_day ASC</script>")
    List<GrowthCurveItemVO> selectGrowthCurveList(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT " +
            "    (SELECT SUM(feed_count) FROM zk_feeding_log WHERE 1=1 " + DeptSqlFragments.AND_DEPT_ID_IN + ") AS feedCount, " +
            "    t1.accumulated_weight_gain AS accumulatedWeightGain, " +
            "    t2.sumDead, " +
            "    t3.initFishWeight " +
            "FROM ( " +
            "    SELECT (SELECT 1) AS id, SUM(accumulated_weight_gain) AS accumulated_weight_gain " +
            "    FROM zk_fish_operation_log zfol " +
            "    WHERE DATE(NOW()) = log_day " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            ") t1 " +
            "LEFT JOIN ( " +
            "    SELECT (SELECT 1) AS id, t1.sumDead " +
            "    FROM ( " +
            "        SELECT " +
            "            SUM(CASE WHEN DATE(zpfd.create_time) = DATE(NOW()) THEN zpfd.fish_num ELSE 0 END) AS dayDead, " +
            "            SUM(zpfd.fish_num) AS sumDead " +
            "        FROM zk_pond_fish_detail zpfd " +
            "        LEFT JOIN zk_pond zp ON zp.id = zpfd.pond_id " +
            "        WHERE zpfd.add_type = '-2' AND zp.id IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "        GROUP BY zpfd.add_type " +
            "    ) t1 " +
            ") t2 ON t1.id = t2.id " +
            "LEFT JOIN ( " +
            "    SELECT (SELECT 1) AS id, SUM(init_fish_spec * fish_num / 1000) AS initFishWeight " +
            "    FROM zk_pond_fish zpf WHERE 1=1 " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            ") t3 ON t1.id = t3.id " +
            "LIMIT 1</script>")
    FeedCoefficientVO selectFeedCoefficient(@Param("deptIds") List<Long> deptIds);

    /** 水质页关注的固定 metric_id（精确匹配，避免 70 万行 LIKE 全表扫描） */
    String WATER_METRIC_IDS = "'do_temp_1','do_oxygen_1','ph_ph_1','do_nh4_1'," +
            "'ntu_ntu_1','orp_orp_1','TSs_TSs_1'," +
            "'alk_alk_1','hard_hard_1','no3_no3_1','wq_co2_1','tp_tp_1','tn_tn_1'";

    /** 传感器水质最近一条采样时间（按鱼池部门隔离） */
    @Select("<script>SELECT DATE_FORMAT(MAX(zdr.record_time), '%Y-%m-%d %H:%i:%s') FROM zk_device_record zdr "
            + DeptSqlFragments.JOIN_WATER_RECORD_POND
            + "WHERE zdr.deleted = 0 AND zdr.metric_id IN (" + WATER_METRIC_IDS + ") "
            + DeptSqlFragments.AND_WATER_RECORD_DEPT_SCOPE
            + "</script>")
    String selectWaterAsOf(@Param("deptIds") List<Long> deptIds);

    @Select("SELECT DATE_FORMAT(MAX(record_time), '%Y-%m-%d %H:%i:%s') FROM zk_device_record "
            + "WHERE deleted = 0 AND metric_id IN (" + WATER_METRIC_IDS + ") "
            + DeptSqlFragments.AND_DEPT_ID_IS_NULL)
    String selectWaterAsOfLegacyNull();

    /**
     * 各水质指标取部门范围内最新一条。
     * 通过 device→pond 用鱼池 dept_id 过滤，避免 record.dept_id 为空/错标导致无数据。
     */
    @Select("<script>SELECT metric_id AS metricId, metric_value AS metricValue FROM ("
            + "  SELECT zdr.metric_id, zdr.metric_value, "
            + "    ROW_NUMBER() OVER (PARTITION BY zdr.metric_id ORDER BY zdr.record_time DESC) AS rn "
            + "  FROM zk_device_record zdr "
            + DeptSqlFragments.JOIN_WATER_RECORD_POND
            + "  WHERE zdr.deleted = 0 AND zdr.metric_id IN (" + WATER_METRIC_IDS + ") "
            + DeptSqlFragments.AND_WATER_RECORD_DEPT_SCOPE
            + ") t WHERE rn = 1</script>")
    List<DaPingMonitorRowVO.WaterMetricRow> selectLatestWaterMetrics(@Param("deptIds") List<Long> deptIds);

    @Select("SELECT metric_id AS metricId, metric_value AS metricValue FROM ("
            + "  SELECT metric_id, metric_value, "
            + "    ROW_NUMBER() OVER (PARTITION BY metric_id ORDER BY record_time DESC) AS rn "
            + "  FROM zk_device_record "
            + "  WHERE deleted = 0 AND metric_id IN (" + WATER_METRIC_IDS + ") "
            + DeptSqlFragments.AND_DEPT_ID_IS_NULL
            + ") t WHERE rn = 1")
    List<DaPingMonitorRowVO.WaterMetricRow> selectLatestWaterMetricsLegacyNull();

    /** 以库内最新采样日为锚的近 24h 小时趋势（按鱼池部门隔离） */
    @Select("<script>SELECT DATE_FORMAT(zdr.record_time, '%H:00') AS timeBucket, "
            + "  ROUND(AVG(CASE WHEN zdr.metric_id = 'do_temp_1' THEN zdr.metric_value END), 2) AS temp, "
            + "  ROUND(AVG(CASE WHEN zdr.metric_id = 'do_oxygen_1' THEN zdr.metric_value END), 2) AS oxygen, "
            + "  ROUND(AVG(CASE WHEN zdr.metric_id = 'ph_ph_1' THEN zdr.metric_value END), 2) AS ph "
            + "FROM zk_device_record zdr "
            + DeptSqlFragments.JOIN_WATER_RECORD_POND
            + "WHERE zdr.deleted = 0 "
            + "  AND zdr.record_time &gt;= DATE_SUB(("
            + "    SELECT MAX(z2.record_time) FROM zk_device_record z2 "
            + "    LEFT JOIN zk_device zd2 ON zd2.device_sn = z2.device_id "
            + "      AND (zd2.device_metric_id = z2.metric_id OR zd2.device_metric_id IS NULL OR zd2.device_metric_id = '') "
            + "      AND zd2.deleted = 0 "
            + "    LEFT JOIN zk_pond zp2 ON zp2.id = zd2.pond_id AND zp2.deleted = 0 "
            + "    WHERE z2.deleted = 0 AND z2.metric_id IN ('do_temp_1','do_oxygen_1','ph_ph_1') "
            + "    <if test='deptIds != null and deptIds.size() &gt; 0'> AND ("
            + "      zp2.dept_id IN <foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
            + "      OR z2.dept_id IN <foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
            + "    )</if>"
            + "  ), INTERVAL 24 HOUR) "
            + "  AND zdr.metric_id IN ('do_temp_1','do_oxygen_1','ph_ph_1') "
            + DeptSqlFragments.AND_WATER_RECORD_DEPT_SCOPE
            + "GROUP BY DATE_FORMAT(zdr.record_time, '%H:00') "
            + "ORDER BY timeBucket</script>")
    List<DaPingMonitorRowVO.WaterTrendRow> selectWaterTrend24h(@Param("deptIds") List<Long> deptIds);

    @Select("SELECT DATE_FORMAT(record_time, '%H:00') AS timeBucket, " +
            "  ROUND(AVG(CASE WHEN metric_id = 'do_temp_1' THEN metric_value END), 2) AS temp, " +
            "  ROUND(AVG(CASE WHEN metric_id = 'do_oxygen_1' THEN metric_value END), 2) AS oxygen, " +
            "  ROUND(AVG(CASE WHEN metric_id = 'ph_ph_1' THEN metric_value END), 2) AS ph " +
            "FROM zk_device_record " +
            "WHERE deleted = 0 " +
            "  AND record_time >= DATE_SUB((" +
            "    SELECT MAX(record_time) FROM zk_device_record " +
            "    WHERE deleted = 0 AND metric_id IN ('do_temp_1','do_oxygen_1','ph_ph_1') " +
            DeptSqlFragments.AND_DEPT_ID_IS_NULL +
            "  ), INTERVAL 24 HOUR) " +
            "  AND metric_id IN ('do_temp_1','do_oxygen_1','ph_ph_1') " +
            DeptSqlFragments.AND_DEPT_ID_IS_NULL +
            "GROUP BY DATE_FORMAT(record_time, '%H:00') " +
            "ORDER BY timeBucket")
    List<DaPingMonitorRowVO.WaterTrendRow> selectWaterTrend24hLegacyNull();

    @Select("<script>SELECT device_name AS name, status, workshop_name AS workshop, " +
            "  curr_value AS currValue, device_unit AS unit, pond_detail_num AS pondDetailNum " +
            "FROM zk_device WHERE deleted = 0 " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            " ORDER BY id ASC LIMIT 80</script>")
    List<DaPingMonitorRowVO.DeviceStatusRow> selectDeviceStatusList(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT COALESCE(NULLIF(TRIM(warn_device), ''), '未命名设备') AS name, COUNT(*) AS cnt " +
            "FROM zk_device_alarm WHERE deleted = 0 " +
            "  AND create_time &gt;= DATE_SUB(NOW(), INTERVAL 90 DAY) " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "GROUP BY COALESCE(NULLIF(TRIM(warn_device), ''), '未命名设备') " +
            "ORDER BY cnt DESC LIMIT 10</script>")
    List<DaPingMonitorRowVO.NameCountRow> selectFaultTop90d(@Param("deptIds") List<Long> deptIds);

    /**
     * 能耗/费用分项：优先经营成本表；无数据时用净化成本明细（电费等）。
     * value 单位为元（费用），前端 energyNote 会说明。
     */
    @Select("SELECT name, value, color FROM (" +
            "  SELECT '电费' AS name, ROUND(SUM(IFNULL(energy_cost,0)), 2) AS value, '#36b8ff' AS color, 1 AS ord " +
            "  FROM zk_operating_cost WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT '饲料' AS name, ROUND(SUM(IFNULL(feed_cost,0)), 2) AS value, '#2e8b57' AS color, 2 AS ord " +
            "  FROM zk_operating_cost WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT '人工' AS name, ROUND(SUM(IFNULL(labor_cost,0)), 2) AS value, '#e8a020' AS color, 3 AS ord " +
            "  FROM zk_operating_cost WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT '渔需' AS name, ROUND(SUM(IFNULL(fish_supplies_cost,0)), 2) AS value, '#e86aa8' AS color, 4 AS ord " +
            "  FROM zk_operating_cost WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT '设备' AS name, ROUND(SUM(IFNULL(equipment_cost,0)), 2) AS value, '#9b6dff' AS color, 5 AS ord " +
            "  FROM zk_operating_cost WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT '动保' AS name, ROUND(SUM(IFNULL(animal_health_cost,0)), 2) AS value, '#d4783a' AS color, 6 AS ord " +
            "  FROM zk_operating_cost WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT '管理' AS name, ROUND(SUM(IFNULL(management_cost,0)), 2) AS value, '#6fd6a0' AS color, 7 AS ord " +
            "  FROM zk_operating_cost WHERE deleted = 0 " +
            ") t WHERE value IS NOT NULL AND value > 0 ORDER BY ord")
    List<DaPingMonitorRowVO.EnergyRow> selectEnergyFromOperatingCost();

    @Select("SELECT " +
            "  CASE cost_type " +
            "    WHEN 'electricity' THEN '电费' " +
            "    WHEN 'feed' THEN '饲料' " +
            "    WHEN 'labor' THEN '人工' " +
            "    WHEN 'health' THEN '动保' " +
            "    ELSE cost_type END AS name, " +
            "  ROUND(SUM(amount), 2) AS value, " +
            "  CASE cost_type " +
            "    WHEN 'electricity' THEN '#36b8ff' " +
            "    WHEN 'feed' THEN '#2e8b57' " +
            "    WHEN 'labor' THEN '#e8a020' " +
            "    WHEN 'health' THEN '#d4783a' " +
            "    ELSE '#9b6dff' END AS color " +
            "FROM zk_purify_cost_detail WHERE deleted = 0 " +
            "GROUP BY cost_type HAVING SUM(amount) > 0 ORDER BY value DESC")
    List<DaPingMonitorRowVO.EnergyRow> selectEnergyFromPurifyCost();

    /** 近 7 天水质趋势：按 6 小时分桶（以水质指标最新采样时间为锚，避免被其它 metric 的 MAX 带偏） */
    @Select("<script>SELECT CONCAT(DATE_FORMAT(record_time, '%m-%d '), " +
            "  LPAD(FLOOR(HOUR(record_time) / 6) * 6, 2, '0'), 'h') AS timeBucket, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'do_temp%' THEN metric_value END), 2) AS temp, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'do_oxygen%' THEN metric_value END), 2) AS oxygen, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'ph_ph%' THEN metric_value END), 2) AS ph " +
            "FROM zk_device_record " +
            "WHERE deleted = 0 " +
            "  AND record_time &gt;= DATE_SUB((" +
            "    SELECT MAX(record_time) FROM zk_device_record " +
            "    WHERE deleted = 0 AND (metric_id LIKE 'do_temp%' OR metric_id LIKE 'do_oxygen%' OR metric_id LIKE 'ph_ph%') " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "  ), INTERVAL 7 DAY) " +
            "  AND (metric_id LIKE 'do_temp%' OR metric_id LIKE 'do_oxygen%' OR metric_id LIKE 'ph_ph%') " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "GROUP BY CONCAT(DATE_FORMAT(record_time, '%m-%d '), LPAD(FLOOR(HOUR(record_time) / 6) * 6, 2, '0'), 'h') " +
            "ORDER BY MIN(record_time)</script>")
    List<DaPingMonitorRowVO.WaterTrendRow> selectWaterTrend7d(@Param("deptIds") List<Long> deptIds);

    /** 近 30 天水质趋势：按日分桶（锚点同 7d） */
    @Select("<script>SELECT DATE_FORMAT(record_time, '%m-%d') AS timeBucket, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'do_temp%' THEN metric_value END), 2) AS temp, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'do_oxygen%' THEN metric_value END), 2) AS oxygen, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'ph_ph%' THEN metric_value END), 2) AS ph " +
            "FROM zk_device_record " +
            "WHERE deleted = 0 " +
            "  AND record_time &gt;= DATE_SUB((" +
            "    SELECT MAX(record_time) FROM zk_device_record " +
            "    WHERE deleted = 0 AND (metric_id LIKE 'do_temp%' OR metric_id LIKE 'do_oxygen%' OR metric_id LIKE 'ph_ph%') " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "  ), INTERVAL 30 DAY) " +
            "  AND (metric_id LIKE 'do_temp%' OR metric_id LIKE 'do_oxygen%' OR metric_id LIKE 'ph_ph%') " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "GROUP BY DATE_FORMAT(record_time, '%m-%d') " +
            "ORDER BY MIN(record_time)</script>")
    List<DaPingMonitorRowVO.WaterTrendRow> selectWaterTrend30d(@Param("deptIds") List<Long> deptIds);

    /** @deprecated 保留兼容；请使用 selectWaterTrend7d / selectWaterTrend30d */
    @Select("SELECT DATE_FORMAT(record_time, '%m-%d') AS timeBucket, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'do_temp%' THEN metric_value END), 2) AS temp, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'do_oxygen%' THEN metric_value END), 2) AS oxygen, " +
            "  ROUND(AVG(CASE WHEN metric_id LIKE 'ph_ph%' THEN metric_value END), 2) AS ph " +
            "FROM zk_device_record " +
            "WHERE deleted = 0 " +
            "  AND record_time &gt;= DATE_SUB((" +
            "    SELECT MAX(record_time) FROM zk_device_record " +
            "    WHERE deleted = 0 AND (metric_id LIKE 'do_temp%' OR metric_id LIKE 'do_oxygen%' OR metric_id LIKE 'ph_ph%')" +
            "  ), INTERVAL #{days} DAY) " +
            "  AND (metric_id LIKE 'do_temp%' OR metric_id LIKE 'do_oxygen%' OR metric_id LIKE 'ph_ph%') " +
            "GROUP BY DATE_FORMAT(record_time, '%m-%d') " +
            "ORDER BY MIN(record_time)")
    List<DaPingMonitorRowVO.WaterTrendRow> selectWaterTrendByDays(@org.apache.ibatis.annotations.Param("days") int days);

    /** 电度表：按设备类型汇总 delta_kWh（子查询规避 ONLY_FULL_GROUP_BY） */
    @Select("SELECT name, value, " +
            "  CASE name " +
            "    WHEN '水泵' THEN '#36b8ff' WHEN '增氧机' THEN '#2e8b57' WHEN '温控器' THEN '#e8a020' " +
            "    WHEN '投饵机' THEN '#e86aa8' WHEN '微滤机' THEN '#9b6dff' WHEN '风机' THEN '#d4783a' " +
            "    WHEN '臭氧发生器' THEN '#6fd6a0' WHEN '紫外线消毒器' THEN '#d946a8' " +
            "    WHEN '碱度调节系统' THEN '#a8d930' WHEN '反硝化系统' THEN '#5eb8ff' " +
            "    ELSE '#3b82f6' END AS color " +
            "FROM (" +
            "  SELECT COALESCE(NULLIF(TRIM(device_type), ''), '未分类') AS name, " +
            "    ROUND(SUM(IFNULL(delta_kwh, 0)), 2) AS value " +
            "  FROM zk_energy_meter_reading WHERE deleted = 0 " +
            "  GROUP BY COALESCE(NULLIF(TRIM(device_type), ''), '未分类') " +
            "  HAVING SUM(IFNULL(delta_kwh, 0)) > 0" +
            ") t ORDER BY value DESC")
    List<DaPingMonitorRowVO.EnergyRow> selectEnergyFromMeterReading();

    /** 电度按日趋势（近 30 天） */
    @Select("SELECT DATE_FORMAT(reading_time, '%m-%d') AS timeBucket, " +
            "  COALESCE(NULLIF(TRIM(device_type), ''), '未分类') AS deviceType, " +
            "  ROUND(SUM(IFNULL(delta_kwh, 0)), 2) AS value " +
            "FROM zk_energy_meter_reading WHERE deleted = 0 " +
            "  AND reading_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY DATE_FORMAT(reading_time, '%m-%d'), COALESCE(NULLIF(TRIM(device_type), ''), '未分类') " +
            "ORDER BY timeBucket")
    List<DaPingMonitorRowVO.EnergyTrendRow> selectEnergyTrendFromMeter();

    /**
     * 反冲洗/排污类：优先监控面板落库表；无数据再回退设备名模糊匹配。
     */
    @Select("SELECT pool, metric_value AS count, status, device_name AS device, " +
            "  IFNULL(workshop_name,'') AS workshop, IFNULL(system_name,'') AS `system` " +
            "FROM zk_device_monitor_panel WHERE deleted = 0 AND panel_type = 'backwash' " +
            "ORDER BY sort_no ASC, id ASC LIMIT 40")
    List<DaPingMonitorRowVO.BackwashRow> selectBackwashFromPanel();

    @Select("<script>SELECT COALESCE(NULLIF(TRIM(pond_detail_num), ''), device_name) AS pool, " +
            "  ROUND(IFNULL(curr_value, 0), 1) AS count, " +
            "  CASE WHEN status = '1' THEN 'normal' ELSE 'fault' END AS status, " +
            "  device_name AS device, " +
            "  IFNULL(workshop_name, '') AS workshop, " +
            "  IFNULL(pond_detail_num, '') AS `system` " +
            "FROM zk_device WHERE deleted = 0 " +
            "  AND (device_name LIKE '%微滤%' OR device_name LIKE '%反冲%' " +
            "       OR device_name LIKE '%底排%' OR device_name LIKE '%进排%') " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "ORDER BY id ASC LIMIT 40</script>")
    List<DaPingMonitorRowVO.BackwashRow> selectBackwashFromDevices(@Param("deptIds") List<Long> deptIds);

    @Select("SELECT pool, metric_value AS share, IFNULL(workshop_name,'') AS workshop, IFNULL(system_name,'') AS `system` " +
            "FROM zk_device_monitor_panel WHERE deleted = 0 AND panel_type = 'aerator' " +
            "ORDER BY sort_no ASC, id ASC LIMIT 40")
    List<DaPingMonitorRowVO.AeratorRow> selectAeratorFromPanel();

    /** 增氧开启占比：均衡增氧 / 溶氧相关设备 curr_value * 100 */
    @Select("<script>SELECT COALESCE(NULLIF(TRIM(pond_detail_num), ''), device_name) AS pool, " +
            "  ROUND(LEAST(100, GREATEST(0, IFNULL(curr_value, 0) * " +
            "    CASE WHEN IFNULL(curr_value,0) &lt;= 1 THEN 100 ELSE 1 END)), 1) AS share, " +
            "  IFNULL(workshop_name, '') AS workshop, " +
            "  IFNULL(pond_detail_num, '') AS `system` " +
            "FROM zk_device WHERE deleted = 0 " +
            "  AND (device_name LIKE '%增氧%' OR device_name LIKE '%溶氧%') " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "ORDER BY id ASC LIMIT 40</script>")
    List<DaPingMonitorRowVO.AeratorRow> selectAeratorFromDevices(@Param("deptIds") List<Long> deptIds);

    @Select("SELECT device_name AS device, start_time AS startHm, end_time AS endHm, " +
            "  IFNULL(workshop_name,'') AS workshop, IFNULL(system_name,'') AS `system`, IFNULL(module_name,'') AS module " +
            "FROM zk_device_monitor_panel WHERE deleted = 0 AND panel_type = 'co2' " +
            "ORDER BY sort_no ASC, id ASC LIMIT 24")
    List<DaPingMonitorRowVO.Co2SegRow> selectCo2FromPanel();

    /**
     * CO₂ 运行时段：airC_co2 近 24h 按小时平均，超过 600 视为开启段（起止同小时）。
     */
    @Select("SELECT 'CO₂传感器' AS device, " +
            "  DATE_FORMAT(record_time, '%H:00') AS startHm, " +
            "  DATE_FORMAT(DATE_ADD(record_time, INTERVAL 1 HOUR), '%H:00') AS endHm, " +
            "  '' AS workshop, '' AS `system`, 'CO₂吹脱' AS module " +
            "FROM (" +
            "  SELECT DATE_FORMAT(record_time, '%Y-%m-%d %H:00:00') AS hr, " +
            "    AVG(metric_value) AS avgv, MIN(record_time) AS record_time " +
            "  FROM zk_device_record " +
            "  WHERE deleted = 0 AND metric_id LIKE 'airC_co2%' " +
            "    AND record_time >= DATE_SUB((SELECT MAX(record_time) FROM zk_device_record WHERE deleted = 0), INTERVAL 24 HOUR) " +
            "  GROUP BY DATE_FORMAT(record_time, '%Y-%m-%d %H:00:00') " +
            "  HAVING AVG(metric_value) >= 600 " +
            ") t ORDER BY startHm LIMIT 24")
    List<DaPingMonitorRowVO.Co2SegRow> selectCo2Segments();

    @Select("SELECT pool AS name, metric_value AS flow, IFNULL(workshop_name,'') AS workshop, IFNULL(system_name,'') AS `system` " +
            "FROM zk_device_monitor_panel WHERE deleted = 0 AND panel_type = 'water' " +
            "ORDER BY sort_no ASC, id ASC LIMIT 20")
    List<DaPingMonitorRowVO.WaterFlowRow> selectWaterFlowFromPanel();

    /** 水位/浊度等作为「水系统」流量代理展示 */
    @Select("<script>SELECT device_name AS name, ROUND(IFNULL(curr_value, 0), 2) AS flow, " +
            "  IFNULL(workshop_name, '') AS workshop, IFNULL(pond_detail_num, '') AS `system` " +
            "FROM zk_device WHERE deleted = 0 " +
            "  AND (device_name LIKE '%水位%' OR device_name LIKE '%进排%' OR device_name LIKE '%循环%') " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "ORDER BY id ASC LIMIT 20</script>")
    List<DaPingMonitorRowVO.WaterFlowRow> selectWaterFlowFromDevices(@Param("deptIds") List<Long> deptIds);

    @Select("SELECT device_name AS name, ROUND(metric_value, 0) AS cnt " +
            "FROM zk_device_monitor_panel WHERE deleted = 0 AND panel_type = 'fault' " +
            "ORDER BY metric_value DESC, sort_no ASC LIMIT 10")
    List<DaPingMonitorRowVO.NameCountRow> selectFaultFromPanel();

    @Select("SELECT payload FROM zk_fish_health_snapshot WHERE deleted = 0 AND snapshot_key = #{key} LIMIT 1")
    String selectFishHealthSnapshot(@org.apache.ibatis.annotations.Param("key") String key);

    @Select("SELECT payload FROM zk_chart_seed WHERE deleted = 0 AND chart_key = #{key} LIMIT 1")
    String selectChartSeed(@org.apache.ibatis.annotations.Param("key") String key);

    @Select("SELECT category, hour_of_day AS hourOfDay, event_text AS eventText, " +
            "  CASE WHEN completed THEN 1 ELSE 0 END AS completed " +
            "FROM zk_overview_schedule_event WHERE deleted = 0 ORDER BY category, hour_of_day")
    List<DaPingMonitorRowVO.ScheduleEventRow> selectOverviewScheduleEvents();

    @Select("SELECT id, 'fish' AS category, IFNULL(title, alarm_type) AS title, " +
            "  IFNULL(message, '') AS message, IFNULL(suggestion, '') AS suggestion, " +
            "  IFNULL(alarm_level, 'priority') AS level, IFNULL(alarm_status, 'pending') AS status, " +
            "  COALESCE(NULLIF(TRIM(pond_name), ''), pond_detail_num, workshop_name, '') AS location, " +
            "  '' AS owner, DATE_FORMAT(IFNULL(alarm_time, create_time), '%Y-%m-%d %H:%i:%s') AS alarmTime " +
            "FROM zk_fish_vision_alarm WHERE deleted = 0 " +
            "ORDER BY IFNULL(alarm_time, create_time) DESC LIMIT 100")
    List<DaPingMonitorRowVO.ExtendedAlarmRow> selectFishVisionAlarms();

    @Select("SELECT id, 'person' AS category, IFNULL(title, alarm_type) AS title, " +
            "  IFNULL(message, '') AS message, IFNULL(suggestion, '') AS suggestion, " +
            "  IFNULL(alarm_level, 'priority') AS level, IFNULL(alarm_status, 'pending') AS status, " +
            "  IFNULL(location, '') AS location, IFNULL(owner_name, '') AS owner, " +
            "  DATE_FORMAT(IFNULL(alarm_time, create_time), '%Y-%m-%d %H:%i:%s') AS alarmTime " +
            "FROM zk_access_alarm WHERE deleted = 0 " +
            "ORDER BY IFNULL(alarm_time, create_time) DESC LIMIT 100")
    List<DaPingMonitorRowVO.ExtendedAlarmRow> selectAccessAlarms();

    @Update("UPDATE zk_access_alarm SET alarm_status = #{status}, " +
            "owner_name = CASE WHEN #{ownerName} IS NULL OR #{ownerName} = '' THEN owner_name ELSE #{ownerName} END, " +
            "update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateAccessAlarmStatus(@Param("id") Long id, @Param("status") String status, @Param("ownerName") String ownerName);

    @Update("UPDATE zk_fish_vision_alarm SET alarm_status = #{status}, " +
            "update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateFishVisionAlarmStatus(@Param("id") Long id, @Param("status") String status);

}
