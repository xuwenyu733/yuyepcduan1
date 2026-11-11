package cn.iocoder.yudao.module.fish.dal.mysql.zkbiologicalasset;

import java.math.BigDecimal;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.ZkBiologicalAssetDO;
import cn.iocoder.yudao.module.fish.service.zkbiologicalasset.vo.OutPoolVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.DashboardStatDO.*;

/**
 * 生物资产评估 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkBiologicalAssetMapper extends BaseMapperX<ZkBiologicalAssetDO> {

    default PageResult<ZkBiologicalAssetDO> selectPage(ZkBiologicalAssetPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkBiologicalAssetDO>()
                .eqIfPresent(ZkBiologicalAssetDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkBiologicalAssetDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkBiologicalAssetDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkBiologicalAssetDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkBiologicalAssetDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkBiologicalAssetDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkBiologicalAssetDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkBiologicalAssetDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkBiologicalAssetDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkBiologicalAssetDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkBiologicalAssetDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkBiologicalAssetDO::getFishSpecification, reqVO.getFishSpecification())
                .eqIfPresent(ZkBiologicalAssetDO::getTotalWeight, reqVO.getTotalWeight())
                .eqIfPresent(ZkBiologicalAssetDO::getFishPrice, reqVO.getFishPrice())
                .eqIfPresent(ZkBiologicalAssetDO::getBiologicalAsset, reqVO.getBiologicalAsset())
                .betweenIfPresent(ZkBiologicalAssetDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkBiologicalAssetDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkBiologicalAssetDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkBiologicalAssetDO::getFid, reqVO.getFid())
                .orderByDesc(ZkBiologicalAssetDO::getId));
    }

    default List<ZkBiologicalAssetDO> selectList(ZkBiologicalAssetExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkBiologicalAssetDO>()
                .eqIfPresent(ZkBiologicalAssetDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkBiologicalAssetDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkBiologicalAssetDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkBiologicalAssetDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkBiologicalAssetDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkBiologicalAssetDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkBiologicalAssetDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkBiologicalAssetDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkBiologicalAssetDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkBiologicalAssetDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkBiologicalAssetDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkBiologicalAssetDO::getFishSpecification, reqVO.getFishSpecification())
                .eqIfPresent(ZkBiologicalAssetDO::getTotalWeight, reqVO.getTotalWeight())
                .eqIfPresent(ZkBiologicalAssetDO::getFishPrice, reqVO.getFishPrice())
                .eqIfPresent(ZkBiologicalAssetDO::getBiologicalAsset, reqVO.getBiologicalAsset())
                .betweenIfPresent(ZkBiologicalAssetDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkBiologicalAssetDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkBiologicalAssetDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkBiologicalAssetDO::getFid, reqVO.getFid())
                .orderByDesc(ZkBiologicalAssetDO::getId));
    }

    /**
     * 根据 pondId 查询生物资产评估记录
     * @param pondId 存塘ID
     * @return 生物资产评估记录
     */
    @Select("SELECT * FROM zk_biological_asset WHERE pond_id = #{pondId} LIMIT 1")
    ZkBiologicalAssetDO selectByPondId(Long pondId);

    /**
     * 取 ≤ 目标日 的最近一条日快照日期（年/月/日），用于往期资产回溯。
     */
    @Select("<script>" +
            "SELECT static_year AS staticYear, static_month AS staticMonth, static_day AS staticDay " +
            "FROM zk_pond_fish_type " +
            "WHERE deleted = b'0' " +
            "<if test='asOfYear != null'>" +
            "  AND (static_year &lt; #{asOfYear} " +
            "    OR (static_year = #{asOfYear} AND static_month &lt; #{asOfMonth}) " +
            "    OR (static_year = #{asOfYear} AND static_month = #{asOfMonth} AND static_day &lt;= #{asOfDay})) " +
            "</if>" +
            "ORDER BY static_year DESC, static_month DESC, static_day DESC " +
            "LIMIT 1" +
            "</script>")
    SnapshotDate selectNearestSnapshotDate(@Param("asOfYear") Integer asOfYear,
                                           @Param("asOfMonth") Integer asOfMonth,
                                           @Param("asOfDay") Integer asOfDay);

    // 按日快照统计总资产（重量=数量×规格g/1000；单价取字典 remark，缺省 50）
    @Select("<script>" +
            "SELECT \n" +
            "    COALESCE(SUM(snap.row_weight), 0) AS fishTotalWeight,\n" +
            "    COALESCE(SUM(snap.row_weight * COALESCE(snap.unit_price, 50)), 0) AS fishTotalMount,\n" +
            "    COALESCE(SUM(snap.fish_num), 0) AS fishTotalCount\n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        zpft.pond_id,\n" +
            "        zpft.fish_num,\n" +
            "        (COALESCE(zpft.fish_num, 0) * COALESCE(\n" +
            "            CAST(NULLIF(REPLACE(REPLACE(REPLACE(IFNULL(zpft.fish_spec,''), 'g/尾', ''), 'g', ''), ' ', ''), '') AS DECIMAL(20,4)),\n" +
            "            0) / 1000) AS row_weight,\n" +
            "        CAST(sdd.remark AS DECIMAL(10,2)) AS unit_price\n" +
            "    FROM zk_pond_fish_type zpft\n" +
            "    INNER JOIN zk_pond zp ON zp.id = zpft.pond_id\n" +
            "    LEFT JOIN system_dict_data sdd ON CONVERT(sdd.value USING utf8mb4) = CONVERT(zpft.fish_type USING utf8mb4)\n" +
            "        AND sdd.dict_type = 'zk_pond_fish_types' AND sdd.deleted = b'0'\n" +
            "    WHERE zpft.deleted = b'0'\n" +
            "      AND zpft.static_year = #{staticYear}\n" +
            "      AND zpft.static_month = #{staticMonth}\n" +
            "      AND zpft.static_day = #{staticDay}\n" +
            "      AND zp.dept_id IN \n" +
            "      <foreach collection='deptIds' item='deptId' open='(' separator=',' close=')'>\n" +
            "          #{deptId}\n" +
            "      </foreach>\n" +
            ") snap" +
            "</script>")
    TotalAssets selectTotalAssets(@Param("deptIds") List<Long> deptIds,
                                  @Param("staticYear") Integer staticYear,
                                  @Param("staticMonth") Integer staticMonth,
                                  @Param("staticDay") Integer staticDay);

    @Select("<script>" +
            "SELECT t1.fish_type, t1.fish_num AS total_count, COALESCE(sdd.label, CAST(t1.fish_type AS CHAR)) AS fish_name " +
            "FROM (" +
            "    SELECT zpft.fish_type, SUM(zpft.fish_num) AS fish_num " +
            "    FROM zk_pond_fish_type zpft " +
            "    LEFT JOIN zk_pond zp ON zp.id = zpft.pond_id " +
            "    WHERE zpft.deleted = b'0' " +
            "      AND zpft.static_year = #{staticYear} " +
            "      AND zpft.static_month = #{staticMonth} " +
            "      AND zpft.static_day = #{staticDay} " +
            "      AND zp.dept_id IN " +
            "      <foreach collection='deptIds' item='deptId' open='(' separator=',' close=')'>" +
            "        #{deptId}" +
            "      </foreach>" +
            "    GROUP BY zpft.fish_type" +
            ") t1 " +
            "LEFT JOIN system_dict_data sdd " +
            "    ON CONVERT(t1.`fish_type` USING utf8mb4) = CONVERT(sdd.`value` USING utf8mb4) " +
            "   AND sdd.`dict_type` = 'zk_pond_fish_types'" +
            "</script>")
    List<FishSpecies> selectFishSpeciesDistribution(@Param("deptIds") List<Long> deptIds,
                                                    @Param("staticYear") Integer staticYear,
                                                    @Param("staticMonth") Integer staticMonth,
                                                    @Param("staticDay") Integer staticDay);

    /**
     * 各鱼池资产：以日快照为主（数量/规格/批次/品种），车间系统名来自存塘主档。
     */
    @Select("<script>" +
            "SELECT \n" +
            "    zp.pond_detail_num,\n" +
            "    snap.total_weight,\n" +
            "    snap.fish_spec,\n" +
            "    snap.fish_num,\n" +
            "    zp.workshop_name,\n" +
            "    zp.pond_system_name,\n" +
            "    COALESCE(snap.batch_number, zp.batch_number) AS batch_number,\n" +
            "    snap.fish_types,\n" +
            "    sdd.label AS fish_name,\n" +
            "    snap.total_mount\n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        zpft.pond_id,\n" +
            "        SUM(zpft.fish_num) AS fish_num,\n" +
            "        SUBSTRING_INDEX(GROUP_CONCAT(zpft.fish_spec ORDER BY zpft.fish_num DESC SEPARATOR ','), ',', 1) AS fish_spec,\n" +
            "        SUBSTRING_INDEX(GROUP_CONCAT(zpft.fish_type ORDER BY zpft.fish_num DESC SEPARATOR ','), ',', 1) AS fish_types,\n" +
            "        SUBSTRING_INDEX(GROUP_CONCAT(zpft.batch_number ORDER BY zpft.fish_num DESC SEPARATOR ','), ',', 1) AS batch_number,\n" +
            "        SUM(COALESCE(zpft.fish_num, 0) * COALESCE(\n" +
            "            CAST(NULLIF(REPLACE(REPLACE(REPLACE(IFNULL(zpft.fish_spec,''), 'g/尾', ''), 'g', ''), ' ', ''), '') AS DECIMAL(20,4)),\n" +
            "            0) / 1000) AS total_weight,\n" +
            "        SUM(COALESCE(zpft.fish_num, 0) * COALESCE(\n" +
            "            CAST(NULLIF(REPLACE(REPLACE(REPLACE(IFNULL(zpft.fish_spec,''), 'g/尾', ''), 'g', ''), ' ', ''), '') AS DECIMAL(20,4)),\n" +
            "            0) / 1000 * COALESCE(CAST(price.remark AS DECIMAL(10,2)), 50)) AS total_mount\n" +
            "    FROM zk_pond_fish_type zpft\n" +
            "    LEFT JOIN system_dict_data price ON CONVERT(price.value USING utf8mb4) = CONVERT(zpft.fish_type USING utf8mb4)\n" +
            "        AND price.dict_type = 'zk_pond_fish_types' AND price.deleted = b'0'\n" +
            "    WHERE zpft.deleted = b'0'\n" +
            "      AND zpft.static_year = #{staticYear}\n" +
            "      AND zpft.static_month = #{staticMonth}\n" +
            "      AND zpft.static_day = #{staticDay}\n" +
            "    GROUP BY zpft.pond_id\n" +
            ") snap\n" +
            "INNER JOIN zk_pond zp ON zp.id = snap.pond_id\n" +
            "LEFT JOIN system_dict_data sdd ON CONVERT(snap.fish_types USING utf8mb4) = CONVERT(sdd.`value` USING utf8mb4)\n" +
            "    AND sdd.`dict_type` = 'zk_pond_fish_types' AND sdd.deleted = b'0'\n" +
            "WHERE zp.dept_id IN \n" +
            "<foreach collection='deptIds' item='deptId' open='(' separator=',' close=')'>\n" +
            "    #{deptId}\n" +
            "</foreach>" +
            "</script>")
    List<PondProductivity> selectPondProductivity(@Param("deptIds") List<Long> deptIds,
                                                  @Param("staticYear") Integer staticYear,
                                                  @Param("staticMonth") Integer staticMonth,
                                                  @Param("staticDay") Integer staticDay);

    @Select("SELECT \n" +
            "    YEAR(feed_time) AS `year`, \n" +
            "    MONTH(feed_time) AS `month`, \n" +
            "    SUM(feed_count) AS `total_feed_kg`\n" +
            "FROM zk_feeding_log\n" +
            "WHERE deleted = 0\n" +
            "GROUP BY YEAR(feed_time), MONTH(feed_time)\n" +
            "ORDER BY `year`, `month`")
    List<FeedConsumption> selectFeedConsumption();

    @Select({
            "SELECT \n" +
                    "    pond_id, \n" +
                    "    MAX(create_time) AS push_time, \n" +
                    "    DATEDIFF(CURDATE(), MAX(create_time)) AS release_days, -- 已投放天数\n" +
                    "    GREATEST(365 - DATEDIFF(CURDATE(), MAX(create_time)), 0) AS remaining_days -- 剩余出池天数\n" +
                    "FROM zk_pond_fish_detail zpfd \n" +
                    "WHERE add_type = '1' \n" +
                    "GROUP BY pond_id \n" +
                    "ORDER BY pond_id"
    })
    List<OutPoolVo> selectRemainOutPoolTime();

    @Select("SELECT 1-COALESCE(SUM(dead_fish_num), 0)/(COALESCE(SUM(fish_num), 1)+COALESCE(SUM(dead_fish_num), 0)) AS avg_live FROM zk_pond_fish zpf")
    BigDecimal selectAvgLive();

    @Select("SELECT AVG(avg_bait_coefficient) FROM zk_specs_log")
    BigDecimal selectFeedConversionRatio();

    @Select("SELECT today_total_feed_kg*1000/avg_bait_coefficient FROM (\n" +
            "\tSELECT \n" +
            "\t\t1 AS dummy_key,\n" +
            "\t\tCOALESCE(SUM(feed_count), 0) AS `today_total_feed_kg`\n" +
            "\tFROM zk_feeding_log\n" +
            "\tWHERE deleted = 0 \n" +
            "\t  AND DATE(feed_time) = CURDATE()\n" +
            ") t1 LEFT JOIN (\n" +
            "\tSELECT 1 AS dummy_key, COALESCE(AVG(avg_bait_coefficient), 1.1) AS avg_bait_coefficient FROM zk_specs_log\n" +
            ") t2 ON t1.dummy_key = t2.dummy_key")
    BigDecimal selectDailyWeightGain();

    /**
     * 日快照日期投影
     */
    class SnapshotDate {
        private Integer staticYear;
        private Integer staticMonth;
        private Integer staticDay;

        public Integer getStaticYear() {
            return staticYear;
        }

        public void setStaticYear(Integer staticYear) {
            this.staticYear = staticYear;
        }

        public Integer getStaticMonth() {
            return staticMonth;
        }

        public void setStaticMonth(Integer staticMonth) {
            this.staticMonth = staticMonth;
        }

        public Integer getStaticDay() {
            return staticDay;
        }

        public void setStaticDay(Integer staticDay) {
            this.staticDay = staticDay;
        }
    }

}
