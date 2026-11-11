package cn.iocoder.yudao.module.fish.dal.mysql.zkdevicerecord;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord.vo.*;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 设备数据记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDeviceRecordMapper extends BaseMapperX<ZkDeviceRecordDO> {

    default PageResult<ZkDeviceRecordDO> selectPage(ZkDeviceRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkDeviceRecordDO> wrapper = new LambdaQueryWrapperX<ZkDeviceRecordDO>()
                .eqIfPresent(ZkDeviceRecordDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(ZkDeviceRecordDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .betweenIfPresent(ZkDeviceRecordDO::getRecordTime, reqVO.getRecordTime())
                .eqIfPresent(ZkDeviceRecordDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkDeviceRecordDO::getMetricValue, reqVO.getMetricValue())
                .eqIfPresent(ZkDeviceRecordDO::getDataStatus, reqVO.getDataStatus())
                .eqIfPresent(ZkDeviceRecordDO::getRawData, reqVO.getRawData())
                .eqIfPresent(ZkDeviceRecordDO::getCollectionMethod, reqVO.getCollectionMethod())
                .betweenIfPresent(ZkDeviceRecordDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceRecordDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceRecordDO::getId);
        QueryUtil.applyDeptFilter(wrapper, ZkDeviceRecordDO::getDeptId, reqVO);
        return selectPage(reqVO, wrapper);
    }

    default List<ZkDeviceRecordDO> selectList(ZkDeviceRecordExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDeviceRecordDO>()
                .eqIfPresent(ZkDeviceRecordDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(ZkDeviceRecordDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .betweenIfPresent(ZkDeviceRecordDO::getRecordTime, reqVO.getRecordTime())
                .eqIfPresent(ZkDeviceRecordDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkDeviceRecordDO::getMetricValue, reqVO.getMetricValue())
                .eqIfPresent(ZkDeviceRecordDO::getDataStatus, reqVO.getDataStatus())
                .eqIfPresent(ZkDeviceRecordDO::getRawData, reqVO.getRawData())
                .eqIfPresent(ZkDeviceRecordDO::getCollectionMethod, reqVO.getCollectionMethod())
                .betweenIfPresent(ZkDeviceRecordDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceRecordDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeviceRecordDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceRecordDO::getId));
    }


    @Select({
            "SELECT * FROM zk_device_record " +
                    "WHERE create_time < ( " +
                    " SELECT MAX(create_time)-#{recentDays} FROM zk_device_record " +
                    ") order by zk_device_record.id asc limit 500"
    })
    List<ZkDeviceRecordDO> selectNeedDel(@Param("recentDays") int recentDays);

    /**
        SELECT zd.pond_id, zp.pond_detail_num, t1.* FROM (
                SELECT * FROM zk_device_record zdr
                WHERE zdr.id IN (
                SELECT MAX(id) FROM zk_device_record
        WHERE DATE(create_time) = DATE(NOW())
        GROUP BY device_id,metric_id
        )
                ) t1 LEFT JOIN zk_device zd ON zd.device_sn = t1.device_id AND zd.device_metric_id = t1.metric_id
        LEFT JOIN zk_pond zp ON zp.id = zd.pond_id
        WHERE zd.pond_id IS NOT NULL
     */
    @Select({
            "SELECT zd.pond_id, zp.pond_detail_num, t1.* FROM (\n" +
                    "\tSELECT * FROM zk_device_record zdr\n" +
                    "\tWHERE zdr.id IN (\n" +
                    "\t\tSELECT MAX(id) FROM zk_device_record\n" +
                    "\t\tWHERE DATE(create_time) = DATE(NOW())\n" +
                    "\t\tGROUP BY device_id,metric_id\n" +
                    "\t)\n" +
                    ") t1 LEFT JOIN zk_device zd ON zd.device_sn = t1.device_id AND zd.device_metric_id = t1.metric_id\n" +
                    "LEFT JOIN zk_pond zp ON zp.id = zd.pond_id\n" +
                    "WHERE zd.pond_id IS NOT NULL"
    })
    List<ZkDeviceRecordDO> selectRecent();


    @Select({
            "SELECT zd.pond_id, zp.pond_detail_num, t1.* FROM (\n" +
                    "\tSELECT * FROM zk_device_record zdr\n" +
                    "\tWHERE zdr.id IN (\n" +
                    "\t\tSELECT MAX(id) FROM zk_device_record\n" +
                    "\t\tWHERE DATE(create_time) = DATE(NOW())\n" +
                    "\t\tGROUP BY device_id,metric_id\n" +
                    "\t)\n" +
                    ") t1 LEFT JOIN zk_device zd ON zd.device_sn = t1.device_id AND zd.device_metric_id = t1.metric_id\n" +
                    "LEFT JOIN zk_pond zp ON zp.id = zd.pond_id\n" +
                    "${ew.customSqlSegment}"
    })
    List<ZkDeviceRecordDO> selectRecentByQuery(@Param("ew") QueryWrapper<ZkDeviceRecordDO> queryWrapper);
}