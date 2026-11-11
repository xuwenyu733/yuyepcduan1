package cn.iocoder.yudao.module.fish.dal.mysql.zkspecslog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo.*;
import cn.iocoder.yudao.module.fish.utils.DeptSqlFragments;

/**
 * 规格记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkSpecsLogMapper extends BaseMapperX<ZkSpecsLogDO> {

    @Select("<script>SELECT \n" +
            "\tzp.workshop_name AS workshopName,\n" +
            "\tzp.pond_system_name AS pondSystemName,\n" +
            "\tzp.pond_detail_num AS pondDetailNum,\n" +
            "\tzp.pond_name AS pondName,\n" +
            "\tCOALESCE(zp.fish_num, 0) AS fishNum,\n" +
            "\tCOALESCE(t2.avg_specs, zpl.actural_specs) AS avgSpecs,\n" +
            "\tt2.avg_body_length AS avgBodyLength,\n" +
            "\tCOALESCE(t3.dayTotalCount, 0) AS dayTotalCount,\n" +
            "\tCOALESCE(NULLIF(t3.totalCount, 0), zpl.feed_count, 0) AS totalCount,\n" +
            "\tCOALESCE(zp.total_weight, 0) AS totalWeight,\n" +
            "\tzpl.actural_radio AS acturalRadio,\n" +
            "\tCOALESCE(t1.avgGrowth, 0) AS avgGrowth,\n" +
            "\tCOALESCE(t4.initWeight, 0) AS initWeight,\n" +
            "\tCOALESCE(t4.accumulatedWeightGain, GREATEST(COALESCE(zp.total_weight, 0) - COALESCE(t4.initWeight, 0), 0)) AS accumulatedWeightGain,\n" +
            "\tCASE WHEN EXISTS (\n" +
            "\t\tSELECT 1 FROM zk_feeding_log zfl2\n" +
            "\t\tWHERE zfl2.pond_id = zp.id AND zfl2.deleted = b'0' AND zfl2.feed_type = '2'\n" +
            "\t) THEN 'device' ELSE 'manual' END AS feedSource\n" +
            "FROM zk_pond zp\n" +
            "LEFT JOIN zk_specs_log zpl ON zp.id = zpl.pond_id\n" +
            "LEFT JOIN (\n" +
            "\tSELECT \n" +
            "\t\tzsl.pond_id AS pond_id, \n" +
            "\t\tCOALESCE(actural_accumulated_weight_gain, predict_accumulated_weight_gain)/COALESCE(t1.age, 1) AS avgGrowth\n" +
            "\tFROM zk_specs_log zsl\n" +
            "\tLEFT JOIN (\n" +
            "\t\tSELECT pond_id, COUNT(*) AS age FROM zk_fish_operation_log zfol\n" +
            "\t\tWHERE log_type = '-2.4' AND pond_id IS NOT NULL\n" +
            "\t\tGROUP BY pond_id\n" +
            "\t) t1 ON t1.pond_id = zsl.pond_id\n" +
            ") t1 ON t1.pond_id = zp.id\n" +
            "LEFT JOIN (\n" +
            "\tSELECT pond_id, avg_specs, avg_body_length  \n" +
            "\tFROM zk_fish_proofing\n" +
            "\tWHERE id IN (\n" +
            "\t\tSELECT MAX(id) FROM zk_fish_proofing \n" +
            "\t\tGROUP BY pond_id\n" +
            "\t)\n" +
            ") t2 ON t2.pond_id = zp.id\n" +
            "LEFT JOIN (\n" +
            "\tSELECT s1.pond_id, s1.totalCount, COALESCE(s2.dayTotalCount, 0) AS dayTotalCount FROM (\n" +
            "\t\tSELECT pond_id, SUM(COALESCE(feed_count, 0)) AS totalCount FROM zk_feeding_log\n" +
            "\t\tWHERE deleted = b'0'\n" +
            "\t\tGROUP BY pond_id\n" +
            "\t) s1 LEFT JOIN (\n" +
            "\t\tSELECT pond_id, SUM(COALESCE(feed_count, 0)) AS dayTotalCount FROM zk_feeding_log\n" +
            "\t\tWHERE deleted = b'0' AND DATE(feed_time) = DATE(NOW())\n" +
            "\t\tGROUP BY pond_id\n" +
            "\t) s2 ON s1.pond_id = s2.pond_id\n" +
            ") t3 ON t3.pond_id = zp.id\n" +
            "LEFT JOIN (\n" +
            "    SELECT \n" +
            "        COALESCE(fish_num, 0)*COALESCE(init_fish_spec, 0)/1000 AS initWeight,\n" +
            "        accumulated_weight_gain AS accumulatedWeightGain,\n" +
            "        pond_id\n" +
            "    FROM zk_pond_fish zpf\n" +
            ") t4 ON t4.pond_id = zp.id\n" +
            "WHERE zp.deleted = 0 " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "</script>")
    List<ZkFcrModelRespVO> getFcrModelList(@Param("deptIds") List<Long> deptIds);

    default PageResult<ZkSpecsLogDO> selectPage(ZkSpecsLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkSpecsLogDO>()
                .eqIfPresent(ZkSpecsLogDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkSpecsLogDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkSpecsLogDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkSpecsLogDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkSpecsLogDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkSpecsLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkSpecsLogDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkSpecsLogDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkSpecsLogDO::getInitSpecs, reqVO.getInitSpecs())
                .eqIfPresent(ZkSpecsLogDO::getInitNum, reqVO.getInitNum())
                .eqIfPresent(ZkSpecsLogDO::getFeedCount, reqVO.getFeedCount())
                .eqIfPresent(ZkSpecsLogDO::getCurrNum, reqVO.getCurrNum())
                .eqIfPresent(ZkSpecsLogDO::getPredictRadio, reqVO.getPredictRadio())
                .eqIfPresent(ZkSpecsLogDO::getPredictSpecs, reqVO.getPredictSpecs())
                .eqIfPresent(ZkSpecsLogDO::getActuralSpecs, reqVO.getActuralSpecs())
                .eqIfPresent(ZkSpecsLogDO::getActuralRadio, reqVO.getActuralRadio())
                .betweenIfPresent(ZkSpecsLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkSpecsLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkSpecsLogDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkSpecsLogDO::getFid, reqVO.getFid())
                .orderByDesc(ZkSpecsLogDO::getId));
    }

    default List<ZkSpecsLogDO> selectList(ZkSpecsLogExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkSpecsLogDO>()
                .eqIfPresent(ZkSpecsLogDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkSpecsLogDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkSpecsLogDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkSpecsLogDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkSpecsLogDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkSpecsLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkSpecsLogDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkSpecsLogDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkSpecsLogDO::getInitSpecs, reqVO.getInitSpecs())
                .eqIfPresent(ZkSpecsLogDO::getInitNum, reqVO.getInitNum())
                .eqIfPresent(ZkSpecsLogDO::getFeedCount, reqVO.getFeedCount())
                .eqIfPresent(ZkSpecsLogDO::getCurrNum, reqVO.getCurrNum())
                .eqIfPresent(ZkSpecsLogDO::getPredictRadio, reqVO.getPredictRadio())
                .eqIfPresent(ZkSpecsLogDO::getPredictSpecs, reqVO.getPredictSpecs())
                .eqIfPresent(ZkSpecsLogDO::getActuralSpecs, reqVO.getActuralSpecs())
                .eqIfPresent(ZkSpecsLogDO::getActuralRadio, reqVO.getActuralRadio())
                .betweenIfPresent(ZkSpecsLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkSpecsLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkSpecsLogDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkSpecsLogDO::getFid, reqVO.getFid())
                .orderByDesc(ZkSpecsLogDO::getId));
    }

}
