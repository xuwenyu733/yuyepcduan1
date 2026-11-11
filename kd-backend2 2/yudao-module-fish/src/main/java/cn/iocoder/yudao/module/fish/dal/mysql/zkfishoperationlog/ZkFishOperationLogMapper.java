package cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 鱼苗统计 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkFishOperationLogMapper extends BaseMapperX<ZkFishOperationLogDO> {

    default PageResult<ZkFishOperationLogDO> selectPage(ZkFishOperationLogPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkFishOperationLogDO> queryWrapper = new LambdaQueryWrapperX<ZkFishOperationLogDO>()
                .eqIfPresent(ZkFishOperationLogDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkFishOperationLogDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkFishOperationLogDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFishOperationLogDO::getPondNum, reqVO.getPondNum())
                .eqIfPresent(ZkFishOperationLogDO::getPondId, reqVO.getPondId())
                .likeIfPresent(ZkFishOperationLogDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkFishOperationLogDO::getPondSystemName, reqVO.getPondSystemName())
                .likeIfPresent(ZkFishOperationLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFishOperationLogDO::getLogDay, reqVO.getLogDay())
                .eqIfPresent(ZkFishOperationLogDO::getProductNo, reqVO.getProductNo())
                .eqIfPresent(ZkFishOperationLogDO::getFishSpec, reqVO.getFishSpec())
                .eqIfPresent(ZkFishOperationLogDO::getDayFishNum, reqVO.getDayFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getAccumulateFishNum, reqVO.getAccumulateFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getPushFishNum, reqVO.getPushFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getDayRate, reqVO.getDayRate())
                .eqIfPresent(ZkFishOperationLogDO::getAccumulateRate, reqVO.getAccumulateRate())
                .eqIfPresent(ZkFishOperationLogDO::getSurvivalRate, reqVO.getSurvivalRate())
                .eqIfPresent(ZkFishOperationLogDO::getNewDayFishNum, reqVO.getNewDayFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getNewAccumulateFishNum, reqVO.getNewAccumulateFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getNewPushFishNum, reqVO.getNewPushFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getNewDayRate, reqVO.getNewDayRate())
                .eqIfPresent(ZkFishOperationLogDO::getNewAccumulateRate, reqVO.getNewAccumulateRate())
                .eqIfPresent(ZkFishOperationLogDO::getNewSurvivalRate, reqVO.getNewSurvivalRate())
                .eqIfPresent(ZkFishOperationLogDO::getCorrectionBasis, reqVO.getCorrectionBasis())
                .eqIfPresent(ZkFishOperationLogDO::getDifferencesRate, reqVO.getDifferencesRate())
                .eqIfPresent(ZkFishOperationLogDO::getReasonsForDifferences, reqVO.getReasonsForDifferences())
                .eqIfPresent(ZkFishOperationLogDO::getDetailedDescription, reqVO.getDetailedDescription())
                .eqIfPresent(ZkFishOperationLogDO::getDangerRating, reqVO.getDangerRating())
                .eqIfPresent(ZkFishOperationLogDO::getLogType, reqVO.getLogType())
                .betweenIfPresent(ZkFishOperationLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFishOperationLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkFishOperationLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkFishOperationLogDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFishOperationLogDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFishOperationLogDO::getId);
        if(StringUtils.isNotBlank(reqVO.getFishSpecStr())) {
            if(reqVO.getFishSpecStr().contains("-")) {
                String[] gs = reqVO.getFishSpecStr().replace("g", "").split("-");
                queryWrapper.betweenIfPresent(ZkFishOperationLogDO::getFishSpec, gs[0], gs[1]);
            } else if(reqVO.getFishSpecStr().contains("g以上")) {
                String[] gs = reqVO.getFishSpecStr().split("g");
                queryWrapper.geIfPresent(ZkFishOperationLogDO::getFishSpec, gs[0]);
            }
        } else {
            queryWrapper.eqIfPresent(ZkFishOperationLogDO::getFishSpec, reqVO.getFishSpec());
        }
        // 新需求只需要展示 curr_fish_num > 0 的 情況
        queryWrapper.gt(ZkFishOperationLogDO::getCurrFishNum, 0);
        return selectPage(reqVO, queryWrapper);
    }

    default List<ZkFishOperationLogDO> selectList(ZkFishOperationLogExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkFishOperationLogDO>()
                .eqIfPresent(ZkFishOperationLogDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkFishOperationLogDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkFishOperationLogDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFishOperationLogDO::getPondNum, reqVO.getPondNum())
                .eqIfPresent(ZkFishOperationLogDO::getPondId, reqVO.getPondId())
                .likeIfPresent(ZkFishOperationLogDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkFishOperationLogDO::getPondSystemName, reqVO.getPondSystemName())
                .likeIfPresent(ZkFishOperationLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFishOperationLogDO::getLogDay, reqVO.getLogDay())
                .eqIfPresent(ZkFishOperationLogDO::getProductNo, reqVO.getProductNo())
                .eqIfPresent(ZkFishOperationLogDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkFishOperationLogDO::getFishSpec, reqVO.getFishSpec())
                .eqIfPresent(ZkFishOperationLogDO::getDayFishNum, reqVO.getDayFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getAccumulateFishNum, reqVO.getAccumulateFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getPushFishNum, reqVO.getPushFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getDayRate, reqVO.getDayRate())
                .eqIfPresent(ZkFishOperationLogDO::getAccumulateRate, reqVO.getAccumulateRate())
                .eqIfPresent(ZkFishOperationLogDO::getSurvivalRate, reqVO.getSurvivalRate())
                .eqIfPresent(ZkFishOperationLogDO::getNewDayFishNum, reqVO.getNewDayFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getNewAccumulateFishNum, reqVO.getNewAccumulateFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getNewPushFishNum, reqVO.getNewPushFishNum())
                .eqIfPresent(ZkFishOperationLogDO::getNewDayRate, reqVO.getNewDayRate())
                .eqIfPresent(ZkFishOperationLogDO::getNewAccumulateRate, reqVO.getNewAccumulateRate())
                .eqIfPresent(ZkFishOperationLogDO::getNewSurvivalRate, reqVO.getNewSurvivalRate())
                .eqIfPresent(ZkFishOperationLogDO::getCorrectionBasis, reqVO.getCorrectionBasis())
                .eqIfPresent(ZkFishOperationLogDO::getDifferencesRate, reqVO.getDifferencesRate())
                .eqIfPresent(ZkFishOperationLogDO::getReasonsForDifferences, reqVO.getReasonsForDifferences())
                .eqIfPresent(ZkFishOperationLogDO::getDetailedDescription, reqVO.getDetailedDescription())
                .eqIfPresent(ZkFishOperationLogDO::getDangerRating, reqVO.getDangerRating())
                .eqIfPresent(ZkFishOperationLogDO::getLogType, reqVO.getLogType())
                .betweenIfPresent(ZkFishOperationLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFishOperationLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkFishOperationLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkFishOperationLogDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFishOperationLogDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFishOperationLogDO::getId));
    }

    @Select({
            "SELECT batch_number FROM zk_fish_operation_log " +
                    "WHERE batch_number IS NOT NULL " +
                    "GROUP BY batch_number"
    })
    List<ZkFishOperationLogDO> getBatchNumberList();

    @Select({
            "SELECT * FROM zk_fish_operation_log  " +
                    "WHERE id IN (  " +
                    "    SELECT MAX(id) FROM zk_fish_operation_log  " +
                    "    ${ew.customSqlSegment}  " +
                    "    GROUP BY log_type, pond_id  " +
                    ")"
    })
    List<ZkFishOperationLogDO> getByLogType(@Param("ew") QueryWrapper<ZkFishOperationLogDO> queryWrapper);

    // @Select({
    //         "SELECT pond_id, MAX(log_day) AS logDay FROM (  " +
    //                 "    SELECT pond_id, log_day, COUNT(*) AS itemSum FROM zk_fish_operation_log  " +
    //                 "    GROUP BY log_day, pond_id  " +
    //                 "    HAVING itemSum >= 3  " +
    //                 ") t1  " +
    //                 "GROUP BY pond_id  " +
    //                 "ORDER BY logDay DESC LIMIT 1"
    // })
    @Select({
            "SELECT MAX(log_day) AS log_day FROM zk_fish_operation_log"
    })
    ZkFishOperationLogDO selectMaxStatisticsDay();

    @Select({
            "SELECT MAX(log_day) AS log_day FROM zk_fish_operation_log where pond_id = #{pondId}"
    })
    ZkFishOperationLogDO selectMaxStatisticsDay3(@Param("pondId") Long pondId);

    /**

     SELECT
     SUM(fish_num) AS fish_num, pond_id, SUM(fish_cost) AS fish_cost, AVG(fish_price) AS fish_price
     FROM (
     (
     SELECT
     SUM(fish_num) AS fish_num, batch_number, pond_id, (SUM(fish_num)*AVG(ep.purchase_price)) AS fish_cost,
     AVG(ep.purchase_price) AS fish_price
     FROM zk_pond_fish_detail d_add
     LEFT JOIN erp_product ep ON ep.product_no = d_add.product_no
     WHERE add_type = '1'
     GROUP BY pond_id,batch_number
     )
     UNION ALL
     (
     SELECT
     -SUM(fish_num) AS fish_num, batch_number, pond_id, -(SUM(fish_num)*AVG(ep.purchase_price)) AS fish_cost,
     AVG(ep.purchase_price) AS fish_price
     FROM zk_pond_fish_detail d_add
     LEFT JOIN erp_product ep ON ep.product_no = d_add.product_no
     WHERE add_type IN ('-2', '-1', '-3')
     GROUP BY pond_id,batch_number
     )
     UNION ALL
     (
     SELECT
     SUM(fish_num) AS fish_num, batch_number, target_pond_id AS pond_id, (SUM(fish_num)*AVG(ep.purchase_price)) AS fish_cost,
     AVG(ep.purchase_price) AS fish_price
     FROM zk_pond_fish_detail d_add
     LEFT JOIN erp_product ep ON ep.product_no = d_add.product_no
     WHERE add_type IN ('-3')
     GROUP BY target_pond_id,batch_number
     )
     ) t1
     GROUP BY pond_id

     
     * @return
     */
    @Select({
                    "SELECT " +
                    "    SUM(fish_num) AS fish_num, pond_id as pondId, SUM(fish_cost) AS fish_cost, AVG(fish_price) AS fish_price " +
                    "FROM ( " +
                    "  ( " +
                    "    SELECT " +
                    "    SUM(fish_num) AS fish_num, batch_number, pond_id, (SUM(fish_num)*AVG(ep.purchase_price)) AS fish_cost, " +
                    "      AVG(ep.purchase_price) AS fish_price " +
                    "  FROM zk_pond_fish_detail d_add " +
                    "  LEFT JOIN erp_product ep ON ep.product_no = d_add.product_no " +
                    "  WHERE add_type = '1' " +
                    "  GROUP BY pond_id,batch_number " +
                    "  ) " +
                    "  UNION ALL " +
                    "  ( " +
                    "    SELECT " +
                    "      -SUM(fish_num) AS fish_num, batch_number, pond_id, -(SUM(fish_num)*AVG(ep.purchase_price)) AS fish_cost, " +
                    "      AVG(ep.purchase_price) AS fish_price " +
                    "    FROM zk_pond_fish_detail d_add " +
                    "    LEFT JOIN erp_product ep ON ep.product_no = d_add.product_no " +
                    "    WHERE add_type IN ('-1', '-3') " +
                    "    GROUP BY pond_id,batch_number " +
                    "  ) " +
                    "  UNION ALL " +
                    "  ( " +
                    "    SELECT " +
                    "      SUM(fish_num) AS fish_num, batch_number, target_pond_id AS pond_id, (SUM(fish_num)*AVG(ep.purchase_price)) AS fish_cost, " +
                    "      AVG(ep.purchase_price) AS fish_price " +
                    "    FROM zk_pond_fish_detail d_add " +
                    "    LEFT JOIN erp_product ep ON ep.product_no = d_add.product_no " +
                    "    WHERE add_type IN ('-3') " +
                    "    GROUP BY target_pond_id,batch_number " +
                    "  ) " +
                    ") t1 " +
                    "GROUP BY pond_id "
    })
    List<Map<String, Object>> staticCost();

}
