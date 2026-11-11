package cn.iocoder.yudao.module.fish.dal.mysql.zkpond;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.mybatis.core.dept.DeptDataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 存塘 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPondMapper extends BaseMapperX<ZkPondDO> {

    @DeptDataPermission
    default PageResult<ZkPondDO> selectPage(ZkPondPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkPondDO> zkPondDOLambdaQueryWrapperX = new LambdaQueryWrapperX<ZkPondDO>()
                .likeIfPresent(ZkPondDO::getPondName, reqVO.getPondName())
                .likeIfPresent(ZkPondDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkPondDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(ZkPondDO::getDimension, reqVO.getDimension())
                .likeIfPresent(ZkPondDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkPondDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkPondDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkPondDO::getId, reqVO.getId())
                .orderByDesc(ZkPondDO::getId);
        QueryUtil.applyDeptFilter(zkPondDOLambdaQueryWrapperX, ZkPondDO::getDeptId, reqVO);

        // 存塘类型 1:单一;2:混合;3:全部;
        if("1".equals(reqVO.getPondType())) { // 单一
            zkPondDOLambdaQueryWrapperX.notLike(ZkPondDO::getBatchNumber, "%,%");
        } else if("2".equals(reqVO.getPondType())) { // 混合
            zkPondDOLambdaQueryWrapperX.like(ZkPondDO::getBatchNumber, "%,%");
        }
        // 1:养殖中;2:空闲中;3:全部;
        if("1".equals(reqVO.getFishNumType())) { // 养殖中
            zkPondDOLambdaQueryWrapperX.gt(ZkPondDO::getFishNum, 0);
        } else if("2".equals(reqVO.getFishNumType())) { // 空闲中
            zkPondDOLambdaQueryWrapperX.eq(ZkPondDO::getFishNum, 0);
        }
        return selectPage(reqVO, zkPondDOLambdaQueryWrapperX);
    }

    @DeptDataPermission
    default List<ZkPondDO> selectList(ZkPondExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPondDO>()
                .likeIfPresent(ZkPondDO::getPondName, reqVO.getPondName())
                .likeIfPresent(ZkPondDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkPondDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(ZkPondDO::getDimension, reqVO.getDimension())
                .likeIfPresent(ZkPondDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkPondDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkPondDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondDO::getId));
    }

    @Select({
            "select count(*) as pondCount, sum(fish_num) as fishNum from zk_pond zp"
    })
    PondSummaryVo getSummaryInfo();

    /**
     SELECT zkp.*,t1.* FROM zk_pond zkp
     INNER JOIN (
     SELECT DISTINCT zp.id AS tid, AVG(zsl.`actural_radio`) AS acturalRadio,
     SUM(zsl.`feed_count`) AS accumulateFeedCount, SUM(zsl.curr_feed_count) AS currFeedCount,
     SUM(zbc.`curr_cost`) AS currCost, SUM(zbc.`biology_cost`) AS biologyCost
     FROM zk_pond zp
     LEFT JOIN zk_specs_log zsl ON zsl.`pond_id` = zp.`id`
     LEFT JOIN zk_biology_capital zbc ON zbc.`pond_id` = zp.`id`
     GROUP BY zp.`id`
     ) t1 ON t1.tid = zkp.id
     * @param queryWrapper
     * @return
     */
    @Select({
            "SELECT zkp.*,t1.* FROM zk_pond zkp\n" +
                    "INNER JOIN (\n" +
                    "\tSELECT DISTINCT zp.id AS tid, AVG(zsl.`actural_radio`) AS acturalRadio, AVG(zsl.`predict_radio`) AS predictRadio, \n" +
                    "\t     SUM(zsl.`feed_count`) AS accumulateFeedCount, SUM(zsl.curr_feed_count) AS currFeedCount,\n" +
                    "\t     SUM(zbc.`curr_cost`) AS currCost, SUM(zbc.`biology_cost`) AS biologyCost \n" +
                    "\t     FROM zk_pond zp \n" +
                    "\t    LEFT JOIN zk_specs_log zsl ON zsl.`pond_id` = zp.`id` \n" +
                    "\t    LEFT JOIN zk_biology_capital zbc ON zbc.`pond_id` = zp.`id` \n" +
                    "\t    ${ew.customSqlSegment} " +
                    "\t    GROUP BY zp.`id` \n" +
                    ") t1 ON t1.tid = zkp.id order by zkp.id ASC "
    })
    List<ZkPondDO> selectByPondIds(@Param("ew") QueryWrapper<ZkPondDO> queryWrapper);

    /**

     SELECT zkp.*,t1.* FROM zk_pond zkp
     INNER JOIN (
     SELECT DISTINCT zp.id AS tid, AVG(zsl.`actural_radio`) AS acturalRadio, MAX(zpf.fish_types) AS fishTypes,
     SUM(zsl.`feed_count`) AS accumulateFeedCount, SUM(zsl.curr_feed_count) AS currFeedCount,
     SUM(zbc.`curr_cost`) AS currCost, SUM(zbc.`biology_cost`) AS biologyCost
     FROM zk_pond zp
     LEFT JOIN zk_specs_log zsl ON zsl.`pond_id` = zp.`id`
     LEFT JOIN zk_biology_capital zbc ON zbc.`pond_id` = zp.`id`
     LEFT JOIN zk_fish_operation_log zfol ON zfol.`pond_id` = zp.`id` AND zfol.`log_day`='2025-11-13'
     LEFT JOIN zk_pond_fish zpf ON zpf.`pond_id` = zp.`id`
     WHERE zfol.`id` IS NULL
     GROUP BY zp.`id`
     ) t1 ON t1.tid = zkp.id

     * @return
     */
    @Select({
            "SELECT zkp.*,t1.* FROM zk_pond zkp \n" +
                    "INNER JOIN ( \n" +
                    "\tSELECT DISTINCT zp.id AS tid, AVG(zsl.`actural_radio`) AS acturalRadio, MAX(zpf.fish_types) AS fishTypes, \n" +
                    "                     SUM(zsl.`feed_count`) AS accumulateFeedCount, SUM(zsl.curr_feed_count) AS currFeedCount, \n" +
                    "                     SUM(zbc.`curr_cost`) AS currCost, SUM(zbc.`biology_cost`) AS biologyCost \n" +
                    "                     FROM zk_pond zp \n" +
                    "                    LEFT JOIN zk_specs_log zsl ON zsl.`pond_id` = zp.`id` \n" +
                    "                    LEFT JOIN zk_biology_capital zbc ON zbc.`pond_id` = zp.`id` \n" +
                    "                    LEFT JOIN zk_fish_operation_log zfol ON zfol.`pond_id` = zp.`id` AND zfol.`log_day`='${logDay}' \n" +
                    "                    LEFT JOIN zk_pond_fish zpf ON zpf.`pond_id` = zp.`id` \n" +
                    "                    WHERE zfol.`id` IS NULL \n" +
                    "                    GROUP BY zp.`id` \n" +
                    ") t1 ON t1.tid = zkp.id"
    })
    List<ZkPondDO> getPondListByEmptyOperationLog(@Param("logDay") String logDay);
}
