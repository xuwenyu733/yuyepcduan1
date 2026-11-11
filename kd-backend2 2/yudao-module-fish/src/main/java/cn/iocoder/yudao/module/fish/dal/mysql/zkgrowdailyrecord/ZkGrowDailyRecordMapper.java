package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowdailyrecord;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowDailyRecordDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowManualMeasureDO;
import org.apache.ibatis.annotations.Select;

/**
 * 每日生长与环境记录 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowDailyRecordMapper extends BaseMapperX<ZkGrowDailyRecordDO> {

    /**
     * 批次列表：依赖 ERP 采购入库明细（erp_purchase_in_items / erp_product）。
     * 本地未导入 ERP 表结构时会失败，导入后即可正常。
     */
    @Select("SELECT " +
            " epii.relation_order_no, " +
            " ep.fish_types, " +
            " sdd.label, " +
            " epii.standard, " +
            " (epii.`gift_num`+epii.count) AS productCount " +
            "FROM erp_purchase_in_items epii " +
            "LEFT JOIN erp_product ep ON ep.id = epii.product_id " +
            "LEFT JOIN system_dict_data sdd ON sdd.value = ep.fish_types AND sdd.dict_type = 'zk_pond_fish_types' " +
            "WHERE epii.category_id = 7 AND sdd.label IS NOT NULL")
    List<ZkGrowBatchSummaryRespVO> selectBatchSummaryList();

    /**
     * 鱼池摘要：优先用当日 zk_pond_fish_type；无数据时回退 zk_pond_fish。
     */
    @Select({
            "<script>",
            "SELECT ",
            "  x.fish_type AS fishType, ",
            "  sdd.label AS label, ",
            "  x.pond_id AS pondId, ",
            "  zp.pond_num AS pondNum, ",
            "  zp.pond_system_name AS pondSystemName, ",
            "  zp.pond_detail_num AS pondDetailNum, ",
            "  zp.pond_name AS pondName, ",
            "  zp.workshop_name AS workshopName, ",
            "  zp.workshop_id AS workshopId, ",
            "  zp.fish_spec AS fishSpec, ",
            "  zp.batch_number AS relationOrderNo, ",
            "  x.fish_num AS fishNum ",
            "FROM ( ",
            // fish_type(utf8mb4_0900_ai_ci) vs fish_types(utf8mb4_unicode_ci) 在 UNION 下会 Illegal mix of collations
            "  SELECT CONVERT(fish_type USING utf8mb4) COLLATE utf8mb4_unicode_ci AS fish_type, pond_id, MAX(fish_num) AS fish_num ",
            "  FROM zk_pond_fish_type ",
            "  WHERE deleted = 0 ",
            "    AND static_year = YEAR(NOW()) ",
            "    AND static_month = MONTH(NOW()) ",
            "    AND static_day = DAY(NOW()) ",
            "  GROUP BY pond_id, fish_type ",
            "  UNION ALL ",
            "  SELECT CONVERT(fish_types USING utf8mb4) COLLATE utf8mb4_unicode_ci AS fish_type, pond_id, fish_num ",
            "  FROM zk_pond_fish ",
            "  WHERE deleted = 0 ",
            "    AND NOT EXISTS ( ",
            "      SELECT 1 FROM zk_pond_fish_type t ",
            "      WHERE t.deleted = 0 ",
            "        AND t.static_year = YEAR(NOW()) ",
            "        AND t.static_month = MONTH(NOW()) ",
            "        AND t.static_day = DAY(NOW()) ",
            "    ) ",
            ") x ",
            "LEFT JOIN zk_pond zp ON zp.id = x.pond_id AND zp.deleted = 0 ",
            "LEFT JOIN system_dict_data sdd ON sdd.dict_type = 'zk_pond_fish_types' ",
            "  AND CONVERT(sdd.value USING utf8mb4) = CONVERT(x.fish_type USING utf8mb4) ",
            "WHERE zp.pond_detail_num IS NOT NULL",
            "</script>"
    })
    List<ZkGrowPondSummaryRespVO> selectPondSummaryList();

    @Select("<script>" +
            "SELECT " +
            "    a1.pond_id AS pondId, " +
            "    a5.batch_number AS batchNumber, " +
            "    zp.pond_num AS pondNum, " +
            "    zp.pond_detail_num AS pondDetailNum, " +
            "    a1.first_day AS firstDay, " +
            "    a1.breeding_days AS breedingDays, " +
            "    COALESCE(a4.avg_specs, a1.fish_spec) AS fishSpec, " +
            "    a1.fish_spec AS initialWeightSpec, " +
            "    a4.avg_body_length AS avgBodyLength, " +
            "    COALESCE(a2.baitingRate, 3.2) AS baitingRate, " +
            "    COALESCE(a3.metric_value, 25) AS temperature " +
            "FROM ( " +
            "    SELECT " +
            "        pond_id, " +
            "        MAX(first_day) AS first_day, " +
            "        DATEDIFF(NOW(), MAX(first_day)) AS breeding_days, " +
            "        MAX(fish_spec) AS fish_spec " +
            "    FROM ( " +
            "        SELECT " +
            "            pond_id, " +
            "            MAX(create_time) AS first_day, " +
            "            MAX(fish_spec) AS fish_spec " +
            "        FROM zk_pond_fish_detail zpfd " +
            "        WHERE zpfd.add_type IN ('1') " +
            "        GROUP BY pond_id " +
            "        UNION ALL " +
            "        SELECT " +
            "            target_pond_id AS pond_id, " +
            "            MAX(create_time) AS first_day, " +
            "            MAX(fish_spec) AS fish_spec " +
            "        FROM zk_pond_fish_detail zpfd " +
            "        WHERE zpfd.add_type IN ('-3') " +
            "        GROUP BY target_pond_id " +
            "    ) t1 " +
            "    GROUP BY pond_id " +
            ") a1 " +
            "LEFT JOIN ( " +
            "    SELECT " +
            "        zfl.pond_id, " +
            "        zfl.feed_count, " +
            "        COALESCE(zfl.feed_count/zp.total_weight, 3.2) AS baitingRate " +
            "    FROM ( " +
            "        SELECT " +
            "            zfl.pond_id, " +
            "            SUM(zfl.feed_count) AS feed_count " +
            "        FROM zk_feeding_log zfl " +
            "        GROUP BY pond_id " +
            "    ) zfl " +
            "    LEFT JOIN zk_pond zp ON zp.id = zfl.pond_id " +
            ") a2 ON a1.pond_id = a2.pond_id " +
            "LEFT JOIN ( " +
            "    SELECT " +
            "        zd.pond_id, " +
            "        zd.pond_name, " +
            "        zdr.metric_value, " +
            "        zdr.device_id, " +
            "        zdr.metric_id " +
            "    FROM ( " +
            "        SELECT " +
            "            device_id, " +
            "            metric_id, " +
            "            metric_value, " +
            "            record_time, " +
            "            ROW_NUMBER() OVER ( " +
            "                PARTITION BY device_id, metric_id " +
            "                ORDER BY record_time DESC " +
            "            ) AS rn " +
            "        FROM zk_device_record " +
            "        WHERE " +
            "            deleted = 0 " +
            "            AND data_status = 1 " +
            "            AND ( " +
            "                metric_id LIKE '%do_temp%' " +
            "            ) " +
            "    ) zdr " +
            "    INNER JOIN zk_device zd ON " +
            "        zd.device_sn = zdr.device_id " +
            "        AND zd.device_metric_id = zdr.metric_id " +
            "        AND zd.deleted = 0 " +
            "        AND zd.status = '1' " +
            "    WHERE " +
            "        zdr.rn = 1 " +
            "        AND zd.pond_id > 0 " +
            "    GROUP BY " +
            "        zd.pond_id, " +
            "        zd.pond_name, " +
            "        zdr.metric_value, " +
            "        zdr.device_id, " +
            "        zdr.metric_id " +
            "    ORDER BY " +
            "        zd.pond_id " +
            ") a3 ON a1.pond_id = a3.pond_id " +
            "LEFT JOIN ( " +
            "    SELECT " +
            "        pond_id, " +
            "        MAX(avg_specs) AS avg_specs, " +
            "        MAX(avg_body_length) AS avg_body_length " +
            "    FROM zk_fish_proofing " +
            "    GROUP BY pond_id " +
            ") a4 ON a1.pond_id = a4.pond_id " +
            "LEFT JOIN zk_pond zp ON zp.id = a1.pond_id " +
            "LEFT JOIN ( " +
            "    SELECT " +
            "        pond_id, " +
            "        GROUP_CONCAT(DISTINCT batch_number SEPARATOR ',') batch_number " +
            "    FROM zk_pond_fish_type zpft " +
            "    WHERE zpft.static_year = YEAR(NOW()) " +
            "      AND zpft.static_month = MONTH(NOW()) " +
            "      AND zpft.static_day = DAY(NOW()) " +
            "    GROUP BY pond_id " +
            ") a5 ON a5.pond_id = a1.pond_id " +
            "<where> " +
            "    <if test='batchNo != null and batchNo != \"\"'> " +
            "        AND FIND_IN_SET(#{batchNo}, a5.batch_number) > 0 " +
            "    </if> " +
            "    <if test='pondNum != null and pondNum != \"\"'> " +
            "        AND zp.pond_num = #{pondNum} " +
            "    </if> " +
            "    <if test='pondDetailNum != null and pondDetailNum != \"\"'> " +
            "        AND zp.pond_detail_num = #{pondDetailNum} " +
            "    </if> " +
            "</where> " +
            "</script>")
    List<ZkGrowRealStatsDO> selectRealStats(@org.apache.ibatis.annotations.Param("batchNo") String batchNo, 
                                            @org.apache.ibatis.annotations.Param("pondNum") String pondNum, 
                                            @org.apache.ibatis.annotations.Param("pondDetailNum") String pondDetailNum);

    @Select("<script>" +
            "SELECT " +
            "    pond_id AS pondId, " +
            "    avg_body_length AS avgBodyLength, " +
            "    avg_specs AS avgSpecs, " +
            "    create_time AS createTime " +
            "FROM zk_fish_proofing " +
            "WHERE pond_id IN " +
            "<foreach item='id' collection='pondIds' open='(' separator=',' close=')'> " +
            "    #{id} " +
            "</foreach> " +
            "</script>")
    List<ZkGrowManualMeasureDO> selectManualMeasurements(@org.apache.ibatis.annotations.Param("pondIds") List<Long> pondIds);

    default PageResult<ZkGrowDailyRecordDO> selectPage(ZkGrowDailyRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowDailyRecordDO>()
                .eqIfPresent(ZkGrowDailyRecordDO::getBatchId, reqVO.getBatchId())
                .eqIfPresent(ZkGrowDailyRecordDO::getDayIndex, reqVO.getDayIndex())
                .betweenIfPresent(ZkGrowDailyRecordDO::getRecordDate, reqVO.getRecordDate())
                .eqIfPresent(ZkGrowDailyRecordDO::getWaterTemp, reqVO.getWaterTemp())
                .eqIfPresent(ZkGrowDailyRecordDO::getFeedRate, reqVO.getFeedRate())
                .eqIfPresent(ZkGrowDailyRecordDO::getPredictedWeight, reqVO.getPredictedWeight())
                .eqIfPresent(ZkGrowDailyRecordDO::getPredictedLength, reqVO.getPredictedLength())
                .eqIfPresent(ZkGrowDailyRecordDO::getVisionWeight, reqVO.getVisionWeight())
                .eqIfPresent(ZkGrowDailyRecordDO::getVisionLength, reqVO.getVisionLength())
                .eqIfPresent(ZkGrowDailyRecordDO::getManualWeight, reqVO.getManualWeight())
                .eqIfPresent(ZkGrowDailyRecordDO::getManualLength, reqVO.getManualLength())
                .eqIfPresent(ZkGrowDailyRecordDO::getSgr, reqVO.getSgr())
                .betweenIfPresent(ZkGrowDailyRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkGrowDailyRecordDO::getId));
    }

}