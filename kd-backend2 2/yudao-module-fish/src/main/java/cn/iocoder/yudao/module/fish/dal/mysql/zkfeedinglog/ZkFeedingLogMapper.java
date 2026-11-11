package cn.iocoder.yudao.module.fish.dal.mysql.zkfeedinglog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.*;
import org.apache.ibatis.annotations.Select;

/**
 * 投喂记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkFeedingLogMapper extends BaseMapperX<ZkFeedingLogDO> {

    default PageResult<ZkFeedingLogDO> selectPage(ZkFeedingLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkFeedingLogDO>()
                .eqIfPresent(ZkFeedingLogDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFeedingLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFeedingLogDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFeedingLogDO::getPondScale, reqVO.getPondScale())
                .eqIfPresent(ZkFeedingLogDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkFeedingLogDO::getFeedBrand, reqVO.getFeedBrand())
                .eqIfPresent(ZkFeedingLogDO::getFeedSpec, reqVO.getFeedSpec())
                .eqIfPresent(ZkFeedingLogDO::getFeedCount, reqVO.getFeedCount())
                .betweenIfPresent(ZkFeedingLogDO::getFeedTime, reqVO.getFeedTime())
                .eqIfPresent(ZkFeedingLogDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ZkFeedingLogDO::getFeedType, reqVO.getFeedType())
                .eqIfPresent(ZkFeedingLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkFeedingLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkFeedingLogDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkFeedingLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkFeedingLogDO::getId));
    }

    default List<ZkFeedingLogDO> selectList(ZkFeedingLogExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkFeedingLogDO>()
                .eqIfPresent(ZkFeedingLogDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFeedingLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFeedingLogDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFeedingLogDO::getPondScale, reqVO.getPondScale())
                .eqIfPresent(ZkFeedingLogDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkFeedingLogDO::getFeedBrand, reqVO.getFeedBrand())
                .eqIfPresent(ZkFeedingLogDO::getFeedSpec, reqVO.getFeedSpec())
                .eqIfPresent(ZkFeedingLogDO::getFeedCount, reqVO.getFeedCount())
                .betweenIfPresent(ZkFeedingLogDO::getFeedTime, reqVO.getFeedTime())
                .eqIfPresent(ZkFeedingLogDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ZkFeedingLogDO::getFeedType, reqVO.getFeedType())
                .eqIfPresent(ZkFeedingLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkFeedingLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkFeedingLogDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkFeedingLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkFeedingLogDO::getId));
    }

    /**

     select  t1.*
     , IFNULL(t2.curr_feed_count, 0) AS curr_feed_count
     , zp.pond_detail_num
     from (
     SELECT pond_id,SUM(feed_count) AS accumulate_feed_count
     FROM zk_feeding_log
     GROUP BY pond_id
     ) t1 left join (
     SELECT pond_id, SUM(feed_count) AS curr_feed_count
     FROM zk_feeding_log
     WHERE feed_time >= DATE_FORMAT(CURDATE(), '%Y-%m-%d 00:00:00')
     AND feed_time <= DATE_FORMAT(CURDATE(), '%Y-%m-%d 23:59:59')
     GROUP BY pond_id
     ) t2 on t1.pond_id = t2.pond_id
     left join zk_pond zp on zp.id = t1.pond_id

     * @return
     */
    @Select({
            "SELECT  t1.*\n" +
                    "\t, IFNULL(t2.curr_feed_count, 0) AS curr_feed_count\n" +
                    // "\t, zp.pond_detail_num \n" +
                    "FROM (\n" +
                    "\tSELECT pond_id,SUM(feed_count) AS accumulate_feed_count\n" +
                    "\tFROM zk_feeding_log\n" +
                    "\tGROUP BY pond_id\n" +
                    ") t1 LEFT JOIN (\n" +
                    "\tSELECT pond_id, SUM(feed_count) AS curr_feed_count\n" +
                    "\tFROM zk_feeding_log\n" +
                    "\tWHERE feed_time >= DATE_FORMAT(CURDATE(), '%Y-%m-%d 00:00:00') \n" +
                    "\t  AND feed_time <= DATE_FORMAT(CURDATE(), '%Y-%m-%d 23:59:59')\n" +
                    "\tGROUP BY pond_id\n" +
                    ") t2 ON t1.pond_id = t2.pond_id\n" +
                    "LEFT JOIN zk_pond zp ON zp.id = t1.pond_id"
    })
    List<Map<String, Object>> staticFeedInfo();

    @Select("SELECT \n" +
            "\ta1.*,\n" +
            "\tzp.workshop_name, \n" +
            "\tzp.pond_num, \n" +
            "\tzp.pond_detail_num \n" +
            "FROM (\n" +
            "\tSELECT \n" +
            "\t    t.first_day, \n" +
            "\t    t.pond_id, \n" +
            "\t    t.fish_spec, \n" +
            "\t    COALESCE(SUM(CASE \n" +
            "\t\tWHEN DATEDIFF(f.feed_time, t.first_day) BETWEEN 0 AND 30 \n" +
            "\t\tTHEN f.feeding_cost \n" +
            "\t\tELSE 0 \n" +
            "\t    END), 0) AS seedling_stage_cost,\n" +
            "\t    COALESCE(SUM(CASE \n" +
            "\t\tWHEN DATEDIFF(f.feed_time, t.first_day) BETWEEN 31 AND 90 \n" +
            "\t\tTHEN f.feeding_cost \n" +
            "\t\tELSE 0 \n" +
            "\t    END), 0) AS juvenile_stage_cost,\n" +
            "\t    COALESCE(SUM(CASE \n" +
            "\t\tWHEN DATEDIFF(f.feed_time, t.first_day) BETWEEN 91 AND 180 \n" +
            "\t\tTHEN f.feeding_cost \n" +
            "\t\tELSE 0 \n" +
            "\t    END), 0) AS early_adult_stage_cost,\n" +
            "\t    COALESCE(SUM(CASE \n" +
            "\t\tWHEN DATEDIFF(f.feed_time, t.first_day) BETWEEN 181 AND 365 \n" +
            "\t\tTHEN f.feeding_cost \n" +
            "\t\tELSE 0 \n" +
            "\t    END), 0) AS late_adult_stage_cost,\n" +
            "\t    COALESCE(SUM(f.feeding_cost), 0) AS total_feeding_cost,\n" +
            "\t    COALESCE(SUM(f.feeding_cost)/COALESCE(MAX(zpf.`accumulated_weight_gain`), 1), 0) AS cost_per_kg\n" +
            "\tFROM (\n" +
            "\t    SELECT \n" +
            "\t\tpond_id, \n" +
            "\t\tMAX(create_time) AS first_day,\n" +
            "\t\tMAX(fish_spec) AS fish_spec \n" +
            "\t    FROM zk_pond_fish_detail zpfd\n" +
            "\t    WHERE zpfd.add_type IN ('1') AND zpfd.deleted = 0 \n" +
            "\t    GROUP BY pond_id \n" +
            "\t) t\n" +
            "\tLEFT JOIN zk_feeding_log f ON t.pond_id = f.pond_id AND f.deleted = 0 \n" +
            "\tLEFT JOIN zk_pond_fish zpf ON zpf.pond_id = t.pond_id AND zpf.deleted = 0 \n" +
            "\tGROUP BY t.pond_id, t.first_day, t.fish_spec\n" +
            ") a1 \n" +
            "LEFT JOIN zk_pond zp ON zp.id = a1.pond_id AND zp.deleted = 0 \n" +
            "WHERE zp.pond_detail_num IS NOT NULL \n" +
            "ORDER BY a1.pond_id")
    List<VariousFryStagesCostRespVO> getVariousFryStagesCost();

}
