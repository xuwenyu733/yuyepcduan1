package cn.iocoder.yudao.module.fish.dal.mysql.zkdeathsurvivalrate;

import java.math.BigDecimal;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeathsurvivalrate.ZkDeathSurvivalRateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.stats.ZkDeathSurvivalRateStatsRespVO;
import cn.iocoder.yudao.module.fish.utils.DeptSqlFragments;

/**
 * 死亡和存活率 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDeathSurvivalRateMapper extends BaseMapperX<ZkDeathSurvivalRateDO> {

    default PageResult<ZkDeathSurvivalRateDO> selectPage(ZkDeathSurvivalRatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkDeathSurvivalRateDO>()
                .eqIfPresent(ZkDeathSurvivalRateDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkDeathSurvivalRateDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkDeathSurvivalRateDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkDeathSurvivalRateDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDeathSurvivalRateDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkDeathSurvivalRateDO::getFryQuantity, reqVO.getFryQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getDeathQuantity, reqVO.getDeathQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getSalesQuantity, reqVO.getSalesQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondTransferQuantity, reqVO.getPondTransferQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getStockedQuantity, reqVO.getStockedQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getSurvivalRate, reqVO.getSurvivalRate())
                .betweenIfPresent(ZkDeathSurvivalRateDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeathSurvivalRateDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeathSurvivalRateDO::getId));
    }

    default List<ZkDeathSurvivalRateDO> selectList(ZkDeathSurvivalRateExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDeathSurvivalRateDO>()
                .eqIfPresent(ZkDeathSurvivalRateDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkDeathSurvivalRateDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkDeathSurvivalRateDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkDeathSurvivalRateDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDeathSurvivalRateDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkDeathSurvivalRateDO::getFryQuantity, reqVO.getFryQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getDeathQuantity, reqVO.getDeathQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getSalesQuantity, reqVO.getSalesQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getPondTransferQuantity, reqVO.getPondTransferQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getStockedQuantity, reqVO.getStockedQuantity())
                .eqIfPresent(ZkDeathSurvivalRateDO::getSurvivalRate, reqVO.getSurvivalRate())
                .betweenIfPresent(ZkDeathSurvivalRateDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeathSurvivalRateDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeathSurvivalRateDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeathSurvivalRateDO::getId));
    }

    @Select("<script>SELECT " +
            "    SUM(CASE WHEN DATE(zpfi.create_time) = DATE(NOW()) THEN zpfi.fish_num ELSE 0 END) AS dayDead, " +
            "    SUM(CASE WHEN YEARWEEK(zpfi.create_time, 1) = YEARWEEK(CURDATE(), 1) THEN zpfi.fish_num ELSE 0 END) AS weekDead, " +
            "    SUM(CASE WHEN YEAR(zpfi.create_time) = YEAR(CURDATE()) AND MONTH(zpfi.create_time) = MONTH(CURDATE()) THEN zpfi.fish_num ELSE 0 END) AS monthDead, " +
            "    SUM((CASE WHEN add_type = '-2' THEN zpfi.fish_num ELSE 0 END)) AS avgDeadPerEvent " +
            "FROM zk_pond_fish_detail zpfi " +
            "LEFT JOIN zk_pond zp ON zp.id = zpfi.pond_id " +
            "WHERE add_type = '-2' AND zp.id IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "</script>")
    ZkDeathSurvivalRateStatsRespVO.TopStats getTopStats(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT SUM(zp.fish_num) FROM zk_pond zp WHERE 1=1 " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "</script>")
    Long selectPondFishNum(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT " +
            "    IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 1 DAY AND zpfi.create_time &lt; CURDATE() AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) AS death_count_yesterday, " +
            "    ROUND(IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 1 DAY AND zpfi.create_time &lt; CURDATE() AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) * 100.0 / " +
            "          NULLIF(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 1 DAY AND zpfi.create_time &lt; CURDATE() THEN zpfi.fish_num ELSE 0 END), 0), 2) AS mortality_rate_yesterday, " +
            "    IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 2 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 1 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) AS death_count_two_days_ago, " +
            "    ROUND(IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 2 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 1 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) * 100.0 / " +
            "          NULLIF(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 2 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 1 DAY THEN zpfi.fish_num ELSE 0 END), 0), 2) AS mortality_rate_two_days_ago, " +
            "    IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 3 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 2 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) AS death_count_three_days_ago, " +
            "    ROUND(IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 3 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 2 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) * 100.0 / " +
            "          NULLIF(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 3 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 2 DAY THEN zpfi.fish_num ELSE 0 END), 0), 2) AS mortality_rate_three_days_ago, " +
            "    IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 4 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 3 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) AS death_count_four_days_ago, " +
            "    ROUND(IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 4 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 3 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) * 100.0 / " +
            "          NULLIF(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 4 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 3 DAY THEN zpfi.fish_num ELSE 0 END), 0), 2) AS mortality_rate_four_days_ago, " +
            "    IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 5 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 4 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) AS death_count_five_days_ago, " +
            "    ROUND(IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 5 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 4 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) * 100.0 / " +
            "          NULLIF(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 5 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 4 DAY THEN zpfi.fish_num ELSE 0 END), 0), 2) AS mortality_rate_five_days_ago, " +
            "    IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 6 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 5 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) AS death_count_six_days_ago, " +
            "    ROUND(IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 6 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 5 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) * 100.0 / " +
            "          NULLIF(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 6 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 5 DAY THEN zpfi.fish_num ELSE 0 END), 0), 2) AS mortality_rate_six_days_ago, " +
            "    IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 7 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 6 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) AS death_count_seven_days_ago, " +
            "    ROUND(IFNULL(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 7 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 6 DAY AND zpfi.add_type = '-2' THEN zpfi.fish_num ELSE 0 END), 0) * 100.0 / " +
            "          NULLIF(SUM(CASE WHEN zpfi.create_time &gt;= CURDATE() - INTERVAL 7 DAY AND zpfi.create_time &lt; CURDATE() - INTERVAL 6 DAY THEN zpfi.fish_num ELSE 0 END), 0), 2) AS mortality_rate_seven_days_ago " +
            "FROM zk_pond_fish_detail zpfi " +
            "LEFT JOIN zk_pond zp ON zp.id = zpfi.pond_id " +
            "WHERE zpfi.create_time &gt;= CURDATE() - INTERVAL 7 DAY AND zp.id IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "</script>")
    ZkDeathSurvivalRateStatsRespVO.TrendStats getTrendStats(@Param("deptIds") List<Long> deptIds);

    @Select("<script>SELECT " +
            "    zp.pond_name, " +
            "    zp.id AS pond_id, " +
            "    COALESCE(fd.dayDead, 0) AS dayDead, " +
            "    COALESCE(fd.weekDead, 0) AS weekDead, " +
            "    COALESCE(fd.monthDead, 0) AS monthDead, " +
            "    COALESCE(dsr.death_quantity, 0) AS deathQuantity, " +
            "    dsr.survival_rate AS survivalRate " +
            "FROM zk_pond zp " +
            "LEFT JOIN ( " +
            "    SELECT pond_id, " +
            "        SUM(CASE WHEN DATE(create_time) = DATE(NOW()) THEN fish_num ELSE 0 END) AS dayDead, " +
            "        SUM(CASE WHEN YEARWEEK(create_time, 1) = YEARWEEK(CURDATE(), 1) THEN fish_num ELSE 0 END) AS weekDead, " +
            "        SUM(CASE WHEN YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE()) THEN fish_num ELSE 0 END) AS monthDead " +
            "    FROM zk_pond_fish_detail " +
            "    WHERE add_type = '-2' AND deleted = 0 " +
            "    GROUP BY pond_id " +
            ") fd ON fd.pond_id = zp.id " +
            "LEFT JOIN ( " +
            "    SELECT d.pond_id, d.death_quantity, d.survival_rate " +
            "    FROM zk_death_survival_rate d " +
            "    INNER JOIN ( " +
            "        SELECT pond_id, MAX(id) AS max_id " +
            "        FROM zk_death_survival_rate " +
            "        WHERE deleted = 0 AND pond_id IS NOT NULL " +
            "        GROUP BY pond_id " +
            "    ) latest ON latest.max_id = d.id " +
            ") dsr ON dsr.pond_id = zp.id " +
            "WHERE zp.deleted = 0 " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "  AND (COALESCE(fd.dayDead, 0) &gt; 0 OR COALESCE(fd.weekDead, 0) &gt; 0 OR COALESCE(fd.monthDead, 0) &gt; 0 OR COALESCE(dsr.death_quantity, 0) &gt; 0) " +
            "ORDER BY zp.id</script>")
    List<ZkDeathSurvivalRateStatsRespVO.PoolStats> getPoolStatsList(@Param("deptIds") List<Long> deptIds);

}
