package cn.iocoder.yudao.module.fish.dal.mysql.zkbiologycapital;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.ZkBiologyCapitalDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.MainHomeStatDO.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

/**
 * 生物性资产 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkBiologyCapitalMapper extends BaseMapperX<ZkBiologyCapitalDO> {

    default PageResult<ZkBiologyCapitalDO> selectPage(ZkBiologyCapitalPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkBiologyCapitalDO>()
                .eqIfPresent(ZkBiologyCapitalDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkBiologyCapitalDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkBiologyCapitalDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkBiologyCapitalDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkBiologyCapitalDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkBiologyCapitalDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkBiologyCapitalDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkBiologyCapitalDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkBiologyCapitalDO::getProductName, reqVO.getProductName())
                .eqIfPresent(ZkBiologyCapitalDO::getBrand, reqVO.getBrand())
                .eqIfPresent(ZkBiologyCapitalDO::getSpecifications, reqVO.getSpecifications())
                .eqIfPresent(ZkBiologyCapitalDO::getProductClassification, reqVO.getProductClassification())
                .eqIfPresent(ZkBiologyCapitalDO::getCategoryNo, reqVO.getCategoryNo())
                .eqIfPresent(ZkBiologyCapitalDO::getProductNum, reqVO.getProductNum())
                .eqIfPresent(ZkBiologyCapitalDO::getProductWeight, reqVO.getProductWeight())
                .eqIfPresent(ZkBiologyCapitalDO::getProductPrice, reqVO.getProductPrice())
                .eqIfPresent(ZkBiologyCapitalDO::getCapitalTotal, reqVO.getCapitalTotal())
                .eqIfPresent(ZkBiologyCapitalDO::getCurrCost, reqVO.getCurrCost())
                .eqIfPresent(ZkBiologyCapitalDO::getFeedCost, reqVO.getFeedCost())
                .eqIfPresent(ZkBiologyCapitalDO::getMedicineCost, reqVO.getMedicineCost())
                .eqIfPresent(ZkBiologyCapitalDO::getDeviceCost, reqVO.getDeviceCost())
                .eqIfPresent(ZkBiologyCapitalDO::getEnergyCost, reqVO.getEnergyCost())
                .eqIfPresent(ZkBiologyCapitalDO::getExtraCost, reqVO.getExtraCost())
                .eqIfPresent(ZkBiologyCapitalDO::getFixedCost, reqVO.getFixedCost())
                .eqIfPresent(ZkBiologyCapitalDO::getBiologyCost, reqVO.getBiologyCost())
                .eqIfPresent(ZkBiologyCapitalDO::getPondCount, reqVO.getPondCount())
                .eqIfPresent(ZkBiologyCapitalDO::getOverallProfitAndLoss, reqVO.getOverallProfitAndLoss())
                .eqIfPresent(ZkBiologyCapitalDO::getProfitAndLossRatio, reqVO.getProfitAndLossRatio())
                .betweenIfPresent(ZkBiologyCapitalDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkBiologyCapitalDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkBiologyCapitalDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkBiologyCapitalDO::getFid, reqVO.getFid())
                .orderByDesc(ZkBiologyCapitalDO::getId));
    }

    default List<ZkBiologyCapitalDO> selectList(ZkBiologyCapitalExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkBiologyCapitalDO>()
                .eqIfPresent(ZkBiologyCapitalDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkBiologyCapitalDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkBiologyCapitalDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkBiologyCapitalDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkBiologyCapitalDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkBiologyCapitalDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkBiologyCapitalDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkBiologyCapitalDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkBiologyCapitalDO::getProductName, reqVO.getProductName())
                .eqIfPresent(ZkBiologyCapitalDO::getBrand, reqVO.getBrand())
                .eqIfPresent(ZkBiologyCapitalDO::getSpecifications, reqVO.getSpecifications())
                .eqIfPresent(ZkBiologyCapitalDO::getProductClassification, reqVO.getProductClassification())
                .eqIfPresent(ZkBiologyCapitalDO::getCategoryNo, reqVO.getCategoryNo())
                .eqIfPresent(ZkBiologyCapitalDO::getProductNum, reqVO.getProductNum())
                .eqIfPresent(ZkBiologyCapitalDO::getProductWeight, reqVO.getProductWeight())
                .eqIfPresent(ZkBiologyCapitalDO::getProductPrice, reqVO.getProductPrice())
                .eqIfPresent(ZkBiologyCapitalDO::getCapitalTotal, reqVO.getCapitalTotal())
                .eqIfPresent(ZkBiologyCapitalDO::getCurrCost, reqVO.getCurrCost())
                .eqIfPresent(ZkBiologyCapitalDO::getFeedCost, reqVO.getFeedCost())
                .eqIfPresent(ZkBiologyCapitalDO::getMedicineCost, reqVO.getMedicineCost())
                .eqIfPresent(ZkBiologyCapitalDO::getDeviceCost, reqVO.getDeviceCost())
                .eqIfPresent(ZkBiologyCapitalDO::getEnergyCost, reqVO.getEnergyCost())
                .eqIfPresent(ZkBiologyCapitalDO::getExtraCost, reqVO.getExtraCost())
                .eqIfPresent(ZkBiologyCapitalDO::getFixedCost, reqVO.getFixedCost())
                .eqIfPresent(ZkBiologyCapitalDO::getBiologyCost, reqVO.getBiologyCost())
                .eqIfPresent(ZkBiologyCapitalDO::getPondCount, reqVO.getPondCount())
                .eqIfPresent(ZkBiologyCapitalDO::getOverallProfitAndLoss, reqVO.getOverallProfitAndLoss())
                .eqIfPresent(ZkBiologyCapitalDO::getProfitAndLossRatio, reqVO.getProfitAndLossRatio())
                .betweenIfPresent(ZkBiologyCapitalDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkBiologyCapitalDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkBiologyCapitalDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkBiologyCapitalDO::getFid, reqVO.getFid())
                .orderByDesc(ZkBiologyCapitalDO::getId));
    }

    @Select("SELECT \n" +
            "\tpond_detail_num, \n" +
            "\ttotal_weight*50 AS total_mount\n" +
            "FROM zk_pond zp")
    List<BiologyCapital> selectBiologyCapital();

    @Select("SELECT \n" +
            "\tpond_detail_num, \n" +
            "\t1-dead_fish_num/(dead_fish_num+fish_num) AS survival_rate\n" +
            "FROM zk_pond_fish zp\n" +
            "WHERE pond_detail_num IS NOT NULL\n" +
            "AND fish_num>0")
    List<SurvivalRate> selectSurvivalRate();

    @Select("SELECT \n" +
            "    YEAR(feed_time) AS `year`, \n" +
            "    MONTH(feed_time) AS `month`, \n" +
            "    DAY(feed_time) AS `day`, \n" +
            "    SUM(feed_count) AS `total_feed_kg`\n" +
            "FROM zk_feeding_log\n" +
            "WHERE deleted = 0\n" +
            "GROUP BY YEAR(feed_time), MONTH(feed_time), DAY(feed_time)\n" +
            "ORDER BY `year`, `month`, `day` LIMIT 7")
    List<PrecisionFeeding> selectPrecisionFeeding();

    @Select("SELECT \n" +
            "    log_day,\n" +
            "    AVG(fish_spec) as avg_fish_spec\n" +
            "FROM zk_fish_operation_log\n" +
            "WHERE deleted = 0 AND log_type='-2.4'\n" +
            "GROUP BY log_day\n" +
            "ORDER BY log_day\n" +
            "LIMIT 15")
    List<FishGrowth> selectFishGrowth();

    @Select("SELECT \n" +
            "    pond_detail_num,\n" +
            "    CASE \n" +
            "        WHEN avg_bait_coefficient < 0.1 OR avg_bait_coefficient IS NULL \n" +
            "        THEN 1.1 \n" +
            "        ELSE avg_bait_coefficient \n" +
            "    END AS avg_bait_coefficient \n" +
            "FROM zk_specs_log " +
            "where pond_detail_num not like '%号箱%' " +
            "ORDER BY avg_bait_coefficient ASC \n" +
            "LIMIT 7")
    List<BaitCoefficient> selectBaitCoefficient();

    @Select({
            "SELECT \n" +
                    "    SUM(CASE WHEN product_type = '1' THEN product_cost ELSE 0 END) AS feedCost,\n" +
                    "    SUM(CASE WHEN product_type = '2' THEN product_cost ELSE 0 END) AS medicineCost,\n" +
                    "    SUM(CASE WHEN product_type = '3' THEN product_cost ELSE 0 END) AS deviceCost,\n" +
                    "    SUM(CASE WHEN product_type = '4' THEN product_cost ELSE 0 END) AS energyCost,\n" +
                    "    SUM(CASE WHEN product_type = '5' THEN product_cost ELSE 0 END) AS extraCost,\n" +
                    "    SUM(CASE WHEN product_type = '7' THEN product_cost ELSE 0 END) AS biologyCost\n" +
                    "FROM zk_product_cost\n" +
                    "WHERE deleted = b'0'"
    })
    ZkBiologyCapitalDO sqlStatic();

    @Select({
            "select \n" +
                    "    SUM(CASE WHEN category_id = '1' THEN total_price+tax_price ELSE null END) AS feedCost,\n" +
                    "    SUM(CASE WHEN category_id = '2' THEN total_price+tax_price ELSE null END) AS medicineCost,\n" +
                    "    SUM(CASE WHEN category_id = '3' THEN total_price+tax_price ELSE null END) AS deviceCost,\n" +
                    "    SUM(CASE WHEN category_id = '4' THEN total_price+tax_price ELSE null END) AS energyCost,\n" +
                    "    SUM(CASE WHEN category_id = '5' THEN total_price+tax_price ELSE null END) AS extraCost,\n" +
                    "    SUM(CASE WHEN category_id = '7' THEN total_price+tax_price ELSE null END) AS biologyCost\n" +
                    "from erp_purchase_in_items \n" +
                    "where deleted = b'0'"
    })
    ZkBiologyCapitalDO sqlStaticByPurchase();

    @Select("SELECT \n" +
            "\tCOALESCE(pondNum, 0) AS pondNum,\n" +
            "\tCOALESCE(totalWeight, 0) AS totalWeight,\n" +
            "\tCOALESCE(feedCount, 0) AS feedCount,\n" +
            "\tCOALESCE(normalPond, 0) AS normalPond,\n" +
            "\tCOALESCE(preWarnPond, 0) AS preWarnPond, \n" +
            "\tCOALESCE(alarmPond, 0) AS alarmPond\n" +
            "FROM (\n" +
            "\tSELECT (\n" +
            "\t\tSELECT COUNT(*) FROM zk_pond\n" +
            "\t) AS pondNum , \n" +
            "\t(\n" +
            "\t\tSELECT SUM(total_weight) FROM zk_pond\n" +
            "\t) AS totalWeight, \n" +
            "\t(\n" +
            "\t\tSELECT SUM(feed_count) FROM zk_feeding_log\n" +
            "\t\tWHERE DATE(feed_time)=DATE(NOW())\n" +
            "\t) AS feedCount, \n" +
            "\t(\n" +
            "\t\tSELECT COUNT(*) - (\n" +
            "\t\t\tSELECT COUNT(*) FROM (\n" +
            "\t\t\t\tSELECT pond_id, COUNT(*) FROM zk_device_alarm\n" +
            "\t\t\t\tWHERE DATE(create_time) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) \n" +
            "\t\t\t\tAND pond_id IS NOT NULL\n" +
            "\t\t\t\tGROUP BY pond_id\n" +
            "\t\t\t) t1\n" +
            "\t\t)\n" +
            "\t\tFROM zk_pond\n" +
            "\t) AS normalPond,  \n" +
            "\t(\n" +
            "\t\tSELECT 0\n" +
            "\t) AS preWarnPond, \n" +
            "\t(\n" +
            "\t\tSELECT COUNT(*) FROM (\n" +
            "\t\t\tSELECT pond_id, COUNT(*) FROM zk_device_alarm\n" +
            "\t\t\tWHERE DATE(create_time) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) \n" +
            "\t\t\tAND pond_id IS NOT NULL\n" +
            "\t\t\tGROUP BY pond_id\n" +
            "\t\t) t1\n" +
            "\t) AS alarmPond \n" +
            ") t1")
    PrecisionFeedingStatsRespVO selectPrecisionFeedingStats();

    /**
    SELECT
        a1.*,
        CASE
        WHEN a1.age BETWEEN 0 AND 30 THEN '鱼苗期'
        WHEN a1.age BETWEEN 31 AND 90 THEN '幼鱼期'
        WHEN a1.age BETWEEN 91 AND 180 THEN '成鱼期前期'
        WHEN a1.age BETWEEN 181 AND 365 THEN '成鱼期后期'
        ELSE '未知阶段'
        END AS growthStage
        FROM (
                        SELECT
                                t1.*,sdd.label AS fishTypeName,
                        CASE
                                WHEN t1.alarm_id IS NULL THEN '正常'
                                ELSE '警报'
                                END AS pondStatus,
                        t2.age AS age,
                        t3.predict_radio AS predict_radio,
                        t3.actural_radio AS actural_radio,
                        COALESCE(t4.totalCount, 0) AS totalCount,
        COALESCE(t4.dayTotalCount, 0) AS dayTotalCount,
        COALESCE(t4.yesterdayTotalCount, 0) AS yesterdayTotalCount,
        COALESCE(t4.avg_daily_feed, 0) AS avg_daily_feed
        -- 统计 鱼池基本信息，是否有警报
        FROM (
                        SELECT zp.*, zda.id AS alarm_id, zpf.fish_types AS fish_types FROM zk_pond zp
                        LEFT JOIN zk_pond_fish zpf ON zpf.pond_id = zp.id
                        LEFT JOIN zk_device_alarm zda ON zda.pond_id = zp.id AND DATE(zda.create_time) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                ) t1
        -- 统计 养殖品种，使用字典
        LEFT JOIN system_dict_data sdd ON sdd.dict_type = 'zk_pond_fish_types' AND CONVERT(sdd.value USING utf8mb4) = CONVERT(t1.fish_types USING utf8mb4)
        -- 统计 养殖年龄
        LEFT JOIN (
                SELECT pond_id, COUNT(*) AS age FROM zk_fish_operation_log zfol
        WHERE log_type = '-2.4' AND pond_id IS NOT NULL
        GROUP BY pond_id
        ) t2 ON t1.id = t2.pond_id
        -- 统计 预估饵料系数和实际饵料系数
        LEFT JOIN (
                SELECT * FROM zk_specs_log
                WHERE id IN (
                SELECT MAX(id) AS id FROM zk_specs_log
        GROUP BY pond_id
                )
                        ) t3 ON t3.pond_id = t1.id
        -- 统计累计投喂和当日投喂
        LEFT JOIN (
                SELECT
                        t1.pond_id, -- 存塘id
                        t1.totalCount,  -- 累计投喂
                        COALESCE(t2.dayTotalCount, 0) AS dayTotalCount, -- 每日投喂
        COALESCE(t3.yesterdayTotalCount, 0) AS yesterdayTotalCount, -- 昨日投喂
        COALESCE(t4.avg_daily_feed, 0) AS avg_daily_feed -- 前三日投喂
        FROM (
                        SELECT pond_id, SUM(feed_count) AS totalCount FROM zk_feeding_log
        GROUP BY pond_id
          ) t1 LEFT JOIN (
                SELECT pond_id, SUM(COALESCE(feed_count, 0)) AS dayTotalCount FROM zk_feeding_log
        WHERE DATE(feed_time) = DATE(NOW())
        GROUP BY pond_id
          ) t2 ON t1.pond_id = t2.pond_id
        LEFT JOIN (
                SELECT pond_id, SUM(COALESCE(feed_count, 0)) AS yesterdayTotalCount FROM zk_feeding_log
        WHERE DATE(feed_time) = DATE(DATE_SUB(CURDATE(), INTERVAL 1 DAY))
        GROUP BY pond_id
          ) t3 ON t1.pond_id = t3.pond_id
        LEFT JOIN (
            -- 使用递归CTE生成一个动态的、排除了用药日的“前N日”日期列表
                WITH RECURSIVE DateSeries AS (
                -- 锚点：起始日期为当前日期的前1天
                SELECT CURDATE() - INTERVAL 1 DAY AS target_date, 1 AS day_count
        UNION ALL
                -- 递归部分：根据条件生成下一天
                SELECT
        CASE
                  -- 检查新生成的日期（target_date - INTERVAL 1 DAY）是否有用药记录
        WHEN EXISTS (
                SELECT 1
                FROM zk_medicine_log_pond mlp
                INNER JOIN zk_use_medicine_log uml ON mlp.medicine_log_id = uml.id
                -- 关联用药时间，注意用药表字段是`feed_time`
                WHERE DATE(uml.feed_time) = DateSeries.target_date - INTERVAL 1 DAY
        AND mlp.deleted = b'0' -- 考虑逻辑删除
        AND uml.deleted = b'0'
                )
                -- 如果有用药，则多向前推一天（即跳过用药日）
        THEN DateSeries.target_date - INTERVAL 2 DAY
                  -- 如果没有用药，则正常向前推一天
        ELSE DateSeries.target_date - INTERVAL 1 DAY
                END,
        day_count + 1
        FROM DateSeries
                -- 递归终止条件：当我们收集到3个有效的目标日期后停止
        WHERE day_count < 3
                )
                -- 主查询：基于生成的日期列表，按pond_id分组计算每日平均投喂量
                SELECT
        daily_total.pond_id, -- 修正：引用子查询结果集的列，而非原表别名
        AVG(daily_total.feed_sum) AS avg_daily_feed
        FROM (
                SELECT
                        ds.target_date,
                distinct_ponds.pond_id, -- 明确列来源
                        COALESCE(SUM(zfl.feed_count), 0) AS feed_sum
        FROM DateSeries ds
        CROSS JOIN (SELECT DISTINCT pond_id FROM zk_feeding_log WHERE deleted = b'0') AS distinct_ponds
        LEFT JOIN zk_feeding_log zfl
        ON DATE(zfl.feed_time) = ds.target_date
        AND zfl.pond_id = distinct_ponds.pond_id
        AND zfl.deleted = b'0'
        GROUP BY ds.target_date, distinct_ponds.pond_id
            ) AS daily_total
        GROUP BY daily_total.pond_id
        ORDER BY daily_total.pond_id
          ) t4 ON t4.pond_id = t1.pond_id
        ) t4 ON t4.pond_id = t1.id
    ) a1

     * @return
     */
    @InterceptorIgnore(tenantLine = "1")
    @Select("SELECT\n" +
            "    a1.*,\n" +
            "    CASE\n" +
            "    WHEN a1.age BETWEEN 0 AND 30 THEN '鱼苗期'\n" +
            "    WHEN a1.age BETWEEN 31 AND 90 THEN '幼鱼期'\n" +
            "    WHEN a1.age BETWEEN 91 AND 180 THEN '成鱼期前期'\n" +
            "    WHEN a1.age BETWEEN 181 AND 365 THEN '成鱼期后期'\n" +
            "    ELSE '未知阶段'\n" +
            "    END AS growthStage\n" +
            "    FROM (\n" +
            "      SELECT\n" +
            "        t1.*,\n" +
            "        sdd.label AS fishTypeName,\n" +
            "        CASE WHEN t1.alarm_id IS NULL THEN '正常' ELSE '警报' END AS pondStatus,\n" +
            "        t2.age AS age,\n" +
            "        t3.predict_radio AS predict_radio,\n" +
            "        t3.actural_radio AS actural_radio,\n" +
            "        COALESCE(t4.totalCount, 0) AS totalCount,\n" +
            "        COALESCE(t4.dayTotalCount, 0) AS dayTotalCount,\n" +
            "        COALESCE(t4.yesterdayTotalCount, 0) AS yesterdayTotalCount,\n" +
            "        COALESCE(t4.avg_daily_feed, 0) AS avg_daily_feed\n" +
            "      FROM (\n" +
            "        SELECT zp.*,\n" +
            "          (SELECT zda.id FROM zk_device_alarm zda\n" +
            "            WHERE zda.pond_id = zp.id AND zda.deleted = b'0'\n" +
            "              AND zda.create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "            LIMIT 1) AS alarm_id,\n" +
            "          NULLIF(SUBSTRING_INDEX(TRIM(BOTH ',' FROM REPLACE(REPLACE(REPLACE(IFNULL(zpf.fish_types, ''), 'null', ''), ',,', ','), ' ', '')), ',', -1), '') AS fish_types\n" +
            "        FROM zk_pond zp\n" +
            "        LEFT JOIN zk_pond_fish zpf ON zpf.pond_id = zp.id AND zpf.deleted = b'0'\n" +
            "        WHERE zp.deleted = b'0'\n" +
            "      ) t1\n" +
            "      LEFT JOIN system_dict_data sdd ON sdd.dict_type = 'zk_pond_fish_types' AND sdd.deleted = b'0'\n" +
            "        AND CONVERT(sdd.value USING utf8mb4) = CONVERT(t1.fish_types USING utf8mb4)\n" +
            "      LEFT JOIN (\n" +
            "        SELECT pond_id, COUNT(*) AS age FROM zk_fish_operation_log zfol\n" +
            "        WHERE log_type = '-2.4' AND pond_id IS NOT NULL AND deleted = b'0'\n" +
            "        GROUP BY pond_id\n" +
            "      ) t2 ON t1.id = t2.pond_id\n" +
            "      LEFT JOIN (\n" +
            "        SELECT zsl.pond_id, zsl.predict_radio, zsl.actural_radio\n" +
            "        FROM zk_specs_log zsl\n" +
            "        INNER JOIN (\n" +
            "          SELECT pond_id, MAX(id) AS id FROM zk_specs_log WHERE deleted = b'0' GROUP BY pond_id\n" +
            "        ) latest ON latest.id = zsl.id\n" +
            "      ) t3 ON t3.pond_id = t1.id\n" +
            "      LEFT JOIN (\n" +
            "        SELECT pond_id,\n" +
            "          SUM(feed_count) AS totalCount,\n" +
            "          SUM(CASE WHEN feed_time >= CURDATE() AND feed_time < CURDATE() + INTERVAL 1 DAY\n" +
            "                   THEN COALESCE(feed_count, 0) ELSE 0 END) AS dayTotalCount,\n" +
            "          SUM(CASE WHEN feed_time >= CURDATE() - INTERVAL 1 DAY AND feed_time < CURDATE()\n" +
            "                   THEN COALESCE(feed_count, 0) ELSE 0 END) AS yesterdayTotalCount,\n" +
            "          ROUND(SUM(CASE WHEN feed_time >= CURDATE() - INTERVAL 3 DAY AND feed_time < CURDATE()\n" +
            "                         THEN COALESCE(feed_count, 0) ELSE 0 END) / 3, 2) AS avg_daily_feed\n" +
            "        FROM zk_feeding_log\n" +
            "        WHERE deleted = b'0'\n" +
            "        GROUP BY pond_id\n" +
            "      ) t4 ON t4.pond_id = t1.id\n" +
            "    ) a1 ${ew.customSqlSegment}")
    List<PrecisionFeedingPondRespVO> selectPrecisionFeedingPonds(@Param("ew") QueryWrapper<ZkBiologyCapitalDO> queryWrapper);

    /** 今日全部投喂明细（不再按池只取 MAX(id)，否则精准投喂看板漏显） */
    @Select("SELECT * FROM zk_feeding_log\n" +
            "WHERE deleted = b'0'\n" +
            "  AND feed_time >= CURDATE() AND feed_time < CURDATE() + INTERVAL 1 DAY\n" +
            "ORDER BY feed_time DESC, id DESC")
    List<PrecisionFeedingTodayLogRespVO> selectPrecisionFeedingTodayLogs();

    @Select("SELECT pond_id AS pondId, feed_spec AS feedSpec, SUM(feed_count) AS feedCount FROM zk_feeding_log\n" +
            "WHERE deleted = b'0' AND feed_time >= CURDATE() - INTERVAL 7 DAY\n" +
            "GROUP BY pond_id, feed_spec")
    List<cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.PrecisionFeedingSpecStatVO> selectPrecisionFeedingSpecStats();
}
