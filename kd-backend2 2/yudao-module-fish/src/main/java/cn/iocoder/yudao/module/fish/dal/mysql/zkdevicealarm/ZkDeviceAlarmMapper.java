package cn.iocoder.yudao.module.fish.dal.mysql.zkdevicealarm;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm.ZkDeviceAlarmDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo.*;
import cn.iocoder.yudao.module.fish.utils.DeptSqlFragments;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;

/**
 * 设备报警 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDeviceAlarmMapper extends BaseMapperX<ZkDeviceAlarmDO> {

    @Select("<script>SELECT " +
            "    id, " +
            "    warn_device_type AS warnDeviceType, " +
            "    warn_type AS warnType, " +
            "    status, " +
            "    warn_name AS warnName, " +
            "    warn_content AS warnContent, " +
            "    handle_info AS handleInfo, " +
            "    workshop_name AS workshopName, " +
            "    pond_system_name AS pondSystemName, " +
            "    pond_detail_num AS pondDetailNum, " +
            "    pond_name AS pondName, " +
            "    user_id AS userId, " +
            "    user_name AS userName, " +
            "    dept_id AS deptId, " +
            "    create_time AS createTime, " +
            "    CASE warn_device_type " +
            "        WHEN 0 THEN 'equipment' " +
            "        WHEN 1 THEN 'water' " +
            "        WHEN 2 THEN 'fish' " +
            "        WHEN 3 THEN 'human' " +
            "    END AS category, " +
            "    CONCAT('【', " +
            "        CASE warn_device_type " +
            "            WHEN 0 THEN '设备' " +
            "            WHEN 1 THEN '水' " +
            "            WHEN 2 THEN '鱼' " +
            "            WHEN 3 THEN '人' " +
            "        END, '】', warn_name) AS title, " +
            "    warn_content AS message, " +
            "    '立即检修' AS suggestion, " +
            "    CASE " +
            "        WHEN TIMESTAMPDIFF(MINUTE, create_time, NOW()) &lt;= 30 " +
            "            THEN CONCAT(TIMESTAMPDIFF(MINUTE, create_time, NOW()), '分钟前') " +
            "        WHEN TIMESTAMPDIFF(MINUTE, create_time, NOW()) BETWEEN 60 AND 1440 " +
            "            THEN CONCAT(ROUND(TIMESTAMPDIFF(MINUTE, create_time, NOW()) / 60, 1), '小时前') " +
            "        WHEN TIMESTAMPDIFF(MINUTE, create_time, NOW()) &gt; 1440 " +
            "            THEN CONCAT(ROUND(TIMESTAMPDIFF(MINUTE, create_time, NOW()) / (60 * 24), 1), '天前') " +
            "        ELSE '刚刚' " +
            "    END AS time " +
            "FROM zk_device_alarm " +
            "WHERE deleted = 0 " +
            DeptSqlFragments.AND_DEPT_ID_IN +
            "ORDER BY create_time DESC " +
            "LIMIT 100</script>")
    List<ZkDeviceAlarmDashboardRespVO> selectDashboardList(@Param("deptIds") List<Long> deptIds);

    default PageResult<ZkDeviceAlarmDO> selectPage(ZkDeviceAlarmPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkDeviceAlarmDO> wrapper = new LambdaQueryWrapperX<ZkDeviceAlarmDO>()
                .eqIfPresent(ZkDeviceAlarmDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkDeviceAlarmDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkDeviceAlarmDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkDeviceAlarmDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkDeviceAlarmDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkDeviceAlarmDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDeviceAlarmDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkDeviceAlarmDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDeviceAlarmDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnDeviceId, reqVO.getWarnDeviceId())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnDevice, reqVO.getWarnDevice())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnDeviceType, reqVO.getWarnDeviceType())
                .likeIfPresent(ZkDeviceAlarmDO::getWarnName, reqVO.getWarnName())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnType, reqVO.getWarnType())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnContent, reqVO.getWarnContent())
                .likeIfPresent(ZkDeviceAlarmDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkDeviceAlarmDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkDeviceAlarmDO::getHandleInfo, reqVO.getHandleInfo())
                .betweenIfPresent(ZkDeviceAlarmDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceAlarmDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceAlarmDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceAlarmDO::getId);
        QueryUtil.applyDeptFilter(wrapper, ZkDeviceAlarmDO::getDeptId, reqVO);
        return selectPage(reqVO, wrapper);
    }

    default List<ZkDeviceAlarmDO> selectList(ZkDeviceAlarmExportReqVO reqVO) {
        LambdaQueryWrapperX<ZkDeviceAlarmDO> wrapper = new LambdaQueryWrapperX<ZkDeviceAlarmDO>()
                .eqIfPresent(ZkDeviceAlarmDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkDeviceAlarmDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkDeviceAlarmDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkDeviceAlarmDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkDeviceAlarmDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkDeviceAlarmDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDeviceAlarmDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkDeviceAlarmDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDeviceAlarmDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnDeviceId, reqVO.getWarnDeviceId())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnDevice, reqVO.getWarnDevice())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnDeviceType, reqVO.getWarnDeviceType())
                .likeIfPresent(ZkDeviceAlarmDO::getWarnName, reqVO.getWarnName())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnType, reqVO.getWarnType())
                .eqIfPresent(ZkDeviceAlarmDO::getWarnContent, reqVO.getWarnContent())
                .likeIfPresent(ZkDeviceAlarmDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkDeviceAlarmDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkDeviceAlarmDO::getHandleInfo, reqVO.getHandleInfo())
                .betweenIfPresent(ZkDeviceAlarmDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceAlarmDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceAlarmDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceAlarmDO::getId);
        QueryUtil.applyDeptFilter(wrapper, ZkDeviceAlarmDO::getDeptId, reqVO);
        return selectList(wrapper);
    }

}
