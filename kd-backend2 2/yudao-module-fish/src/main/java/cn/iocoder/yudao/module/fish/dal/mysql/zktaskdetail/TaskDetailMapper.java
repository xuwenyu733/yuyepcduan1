package cn.iocoder.yudao.module.fish.dal.mysql.zktaskdetail;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 任务明细 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface TaskDetailMapper extends BaseMapperX<TaskDetailDO> {

    default PageResult<TaskDetailDO> selectPage(TaskDetailPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TaskDetailDO>()
                .eqIfPresent(TaskDetailDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(TaskDetailDO::getCheckName, reqVO.getCheckName())
                .eqIfPresent(TaskDetailDO::getCheckType, reqVO.getCheckType())
                .eqIfPresent(TaskDetailDO::getCheckContent, reqVO.getCheckContent())
                .eqIfPresent(TaskDetailDO::getCheckRes, reqVO.getCheckRes())
                .betweenIfPresent(TaskDetailDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TaskDetailDO::getId));
    }

    default List<TaskDetailDO> selectList(TaskDetailExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<TaskDetailDO>()
                .eqIfPresent(TaskDetailDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(TaskDetailDO::getCheckName, reqVO.getCheckName())
                .eqIfPresent(TaskDetailDO::getCheckType, reqVO.getCheckType())
                .eqIfPresent(TaskDetailDO::getCheckContent, reqVO.getCheckContent())
                .eqIfPresent(TaskDetailDO::getCheckRes, reqVO.getCheckRes())
                .betweenIfPresent(TaskDetailDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TaskDetailDO::getId));
    }

    @Select({
            "SELECT zt.* FROM zk_task zt " +
                    "LEFT JOIN zk_task_detail ztd ON zt.`id` = ztd.`task_id` " +
                    "${ew.customSqlSegment}"
    })
    List<ZkTaskDO> getByTypesAndPondId(@Param("ew") QueryWrapper<ZkTaskDO> queryWrapper);

    @InterceptorIgnore(tenantLine = "true", dataPermission = "true")
    @Select("SELECT GROUP_CONCAT(CONCAT(CASE WHEN zdr.metric_id LIKE '%do_oxygen%' THEN '溶氧' WHEN zdr.metric_id LIKE '%do_temp%' THEN '温度' WHEN zdr.metric_id LIKE '%do_oxyper%' THEN '饱和度' WHEN zdr.metric_id LIKE '%ph_ph%' THEN 'PH' WHEN zdr.metric_id LIKE '%orp_orp%' THEN 'ORP' WHEN zdr.metric_id LIKE '%ntu_ntu%' THEN '浊度' WHEN zdr.metric_id LIKE '%lel_level%' THEN '水位' ELSE '其他' END, ': ', ROUND(zdr.metric_value, 2), ' (', DATE_FORMAT(zdr.record_time, '%Y-%m-%d %H:%i'), ')') ORDER BY CASE WHEN zdr.metric_id LIKE '%do_oxygen%' THEN 1 WHEN zdr.metric_id LIKE '%do_temp%' THEN 2 WHEN zdr.metric_id LIKE '%do_oxyper%' THEN 3 WHEN zdr.metric_id LIKE '%ph_ph%' THEN 4 WHEN zdr.metric_id LIKE '%orp_orp%' THEN 5 WHEN zdr.metric_id LIKE '%ntu_ntu%' THEN 6 WHEN zdr.metric_id LIKE '%lel_level%' THEN 7 ELSE 8 END SEPARATOR ' | ') AS water_quality_info FROM (SELECT device_id, metric_id, metric_value, record_time, ROW_NUMBER() OVER (PARTITION BY device_id, metric_id ORDER BY record_time DESC) AS rn FROM zk_device_record WHERE deleted = 0 AND data_status = 1 AND (metric_id LIKE '%do_oxygen%' OR metric_id LIKE '%do_temp%' OR metric_id LIKE '%do_oxyper%' OR metric_id LIKE '%ph_ph%' OR metric_id LIKE '%orp_orp%' OR metric_id LIKE '%ntu_ntu%' OR metric_id LIKE '%lel_level%')) zdr INNER JOIN zk_device zd ON zd.device_sn = zdr.device_id AND zd.device_metric_id = zdr.metric_id AND zd.deleted = 0 AND zd.status = '1' WHERE zdr.rn = 1 AND zd.pond_id = #{pondId} GROUP BY zd.pond_id, zd.pond_name")
    String getWaterQualityInfo(@Param("pondId") Long pondId);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true", illegalSql = "true", dataPermission = "true")
    @Select("WITH RECURSIVE DateSeries AS (SELECT CURDATE() - INTERVAL 1 DAY AS target_date, 1 AS day_count UNION ALL SELECT CASE WHEN EXISTS (SELECT 1 FROM zk_medicine_log_pond mlp INNER JOIN zk_use_medicine_log uml ON mlp.medicine_log_id = uml.id WHERE DATE(uml.feed_time) = DateSeries.target_date - INTERVAL 1 DAY AND mlp.deleted = 0 AND uml.deleted = 0) THEN DateSeries.target_date - INTERVAL 2 DAY ELSE DateSeries.target_date - INTERVAL 1 DAY END, day_count + 1 FROM DateSeries WHERE day_count < 3) SELECT AVG(daily_total.feed_sum) AS avg_daily_feed FROM (SELECT ds.target_date, #{pondId} AS pond_id, COALESCE(SUM(zfl.feed_count), 0) AS feed_sum FROM DateSeries ds LEFT JOIN zk_feeding_log zfl ON DATE(zfl.feed_time) = ds.target_date AND zfl.pond_id = #{pondId} AND zfl.deleted = 0 GROUP BY ds.target_date) AS daily_total GROUP BY daily_total.pond_id")
    Double getAvgDailyFeed(@Param("pondId") Long pondId);

    @InterceptorIgnore(tenantLine = "true", dataPermission = "true")
    @Select("SELECT GROUP_CONCAT(CONCAT(DATE_FORMAT(create_time, '%Y-%m-%d %H:%i'), ': ', warn_name, ' (', warn_content, ')') ORDER BY create_time DESC SEPARATOR ' | ') AS recent_alarms FROM zk_device_alarm WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND create_time <= NOW() AND deleted = 0 AND STATUS IN (0, 1, 2) AND pond_id = #{pondId} GROUP BY pond_id, pond_name")
    String getRecentAlarms(@Param("pondId") Long pondId);
}
