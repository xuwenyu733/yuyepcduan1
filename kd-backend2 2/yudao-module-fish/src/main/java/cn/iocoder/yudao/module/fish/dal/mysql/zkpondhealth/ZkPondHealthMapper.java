package cn.iocoder.yudao.module.fish.dal.mysql.zkpondhealth;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.vo.*;
import cn.iocoder.yudao.module.fish.utils.DeptSqlFragments;
import org.apache.ibatis.annotations.Param;

/**
 * 鱼池健康 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkPondHealthMapper extends BaseMapperX<ZkPondHealthDO> {

    default PageResult<ZkPondHealthDO> selectPage(ZkPondHealthPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPondHealthDO>()
                .eqIfPresent(ZkPondHealthDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPondHealthDO::getEnvironmentalLayerRating, reqVO.getEnvironmentalLayerRating())
                .eqIfPresent(ZkPondHealthDO::getBehavioralLayerRating, reqVO.getBehavioralLayerRating())
                .eqIfPresent(ZkPondHealthDO::getBodyLayerRating, reqVO.getBodyLayerRating())
                .eqIfPresent(ZkPondHealthDO::getOverallRating, reqVO.getOverallRating())
                .eqIfPresent(ZkPondHealthDO::getEnvironmentalLayerInformation, reqVO.getEnvironmentalLayerInformation())
                .eqIfPresent(ZkPondHealthDO::getBehavioralLayerInformation, reqVO.getBehavioralLayerInformation())
                .eqIfPresent(ZkPondHealthDO::getBodyLayerInformation, reqVO.getBodyLayerInformation())
                .eqIfPresent(ZkPondHealthDO::getExceptionMessage, reqVO.getExceptionMessage())
                .eqIfPresent(ZkPondHealthDO::getNumberOfExceptions, reqVO.getNumberOfExceptions())
                .betweenIfPresent(ZkPondHealthDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondHealthDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkPondHealthDO::getId));
    }

    @org.apache.ibatis.annotations.Select("SELECT " +
            "    IFNULL(SUM(CASE WHEN overall_rating >= 80 THEN 1 ELSE 0 END), 0) AS excellentCount, " +
            "    IFNULL(SUM(CASE WHEN overall_rating >= 60 AND overall_rating < 80 THEN 1 ELSE 0 END), 0) AS goodCount, " +
            "    IFNULL(SUM(CASE WHEN overall_rating < 60 THEN 1 ELSE 0 END), 0) AS attentionCount, " +
            "    COUNT(*) AS totalCount " +
            "FROM zk_pond_health " +
            "WHERE deleted = b'0'")
    Map<String, Object> selectHealthStatusDistribution();

    /**

    SELECT zp.pond_detail_num, t1.* FROM (
            SELECT * FROM zk_pond_health WHERE id IN (
            SELECT MAX(id) FROM zk_pond_health GROUP BY pond_id
	) AND deleted = b'0'
            ) t1
    LEFT JOIN zk_pond zp ON zp.id = t1.pond_id
    WHERE zp.pond_detail_num IS NOT NULL

     */
    /**
     * 查询每个鱼池最新一条健康记录（联表补充池号等展示字段）。
     * <p>
     * 注意：2025-09 起增加部门过滤参数 {@code deptIds}。
     * 无参重载仅兼容旧调用，等价于不加部门过滤。
     */
    default List<ZkPondHealthDO> selectLatestPondHealthList() {
        return selectLatestPondHealthList(null);
    }

    @org.apache.ibatis.annotations.Select(
            "<script>" +
            "SELECT zp.pond_detail_num AS pondDetailNum, " +
            "zp.workshop_name AS workshopName, " +
            "zp.pond_system_name AS pondSystemName, " +
            "zp.pond_name AS pondName, " +
            "t1.* FROM (\n" +
            "\tSELECT * FROM zk_pond_health WHERE id IN (\n" +
            "\t    SELECT MAX(id) FROM zk_pond_health GROUP BY pond_id\n" +
            "\t) AND deleted = b'0'\n" +
            ") t1 \n" +
            "LEFT JOIN zk_pond zp ON zp.id = t1.pond_id\n" +
            "WHERE zp.pond_detail_num IS NOT NULL " +
            DeptSqlFragments.AND_ZP_DEPT_ID_IN +
            "</script>"
    )
    List<ZkPondHealthDO> selectLatestPondHealthList(@Param("deptIds") List<Long> deptIds);

}