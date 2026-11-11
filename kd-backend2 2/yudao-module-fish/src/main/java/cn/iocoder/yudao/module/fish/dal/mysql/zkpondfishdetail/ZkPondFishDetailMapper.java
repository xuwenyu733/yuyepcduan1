package cn.iocoder.yudao.module.fish.dal.mysql.zkpondfishdetail;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog.StaticDeadInfoVo3;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.*;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 存塘鱼苗明细 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPondFishDetailMapper extends BaseMapperX<ZkPondFishDetailDO> {

    default PageResult<ZkPondFishDetailDO> selectPage(ZkPondFishDetailPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkPondFishDetailDO> query = new LambdaQueryWrapperX<ZkPondFishDetailDO>()
                .eqIfPresent(ZkPondFishDetailDO::getZkPondFishId, reqVO.getZkPondFishId())
                .eqIfPresent(ZkPondFishDetailDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkPondFishDetailDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkPondFishDetailDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkPondFishDetailDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPondFishDetailDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPondFishDetailDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkPondFishDetailDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkPondFishDetailDO::getAddType, reqVO.getAddType())
                .betweenIfPresent(ZkPondFishDetailDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondFishDetailDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondFishDetailDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondFishDetailDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkPondFishDetailDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkPondFishDetailDO::getParentId, reqVO.getParentId())
                .likeIfPresent(ZkPondFishDetailDO::getSupplier, reqVO.getSupplier())
                .orderByDesc(ZkPondFishDetailDO::getId);
        return selectPage(reqVO, query);
    }

    default List<ZkPondFishDetailDO> selectList(ZkPondFishDetailExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPondFishDetailDO>()
                .eqIfPresent(ZkPondFishDetailDO::getZkPondFishId, reqVO.getZkPondFishId())
                .eqIfPresent(ZkPondFishDetailDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPondFishDetailDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPondFishDetailDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPondFishDetailDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkPondFishDetailDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkPondFishDetailDO::getAddType, reqVO.getAddType())
                .betweenIfPresent(ZkPondFishDetailDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondFishDetailDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondFishDetailDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondFishDetailDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondFishDetailDO::getId));
    }

    @Select({
            "SELECT workshop_id, MIN(workshop_name), COUNT(fish_num) as fish_num FROM zk_pond_fish_detail "+
                    "${ew.customSqlSegment} group by workshop_id"

    })
    List<ZkPondFishDetailDO> countByWorkshopIdSet(@Param("ew") QueryWrapper<ZkPondFishDetailDO> queryWrapper);

    @Select({
            "SELECT pond_num, COUNT(fish_num) as fish_num FROM zk_pond_fish_detail "+
                    "${ew.customSqlSegment} group by pond_num"

    })
    List<ZkPondFishDetailDO> countByPondNumSet(@Param("ew") QueryWrapper<ZkPondFishDetailDO> queryWrapper);

    @Select({
            "SELECT pond_id, COUNT(fish_num) as fish_num FROM zk_pond_fish_detail "+
                    "${ew.customSqlSegment} group by pond_id"

    })
    List<ZkPondFishDetailDO> countByPondIdSet(@Param("ew") QueryWrapper<ZkPondFishDetailDO> queryWrapper);

    @Select({"<script>",
            "SELECT zpfd.* FROM zk_pond zp\n" +
            "LEFT JOIN zk_pond_fish_detail zpfd ON zpfd.pond_id = zp.id\n",
            "${ew.customSqlSegment} " +
            "</script>"
    })
    IPage<ZkPondFishDetailDO> customPage(Page<ZkPondFishDetailDO> page, @Param(Constants.WRAPPER) Wrapper<ZkPondFishDetailDO> wrapper);

    /**

     SELECT  t1.*
         ,IFNULL(t2.day_fish_num, 0) AS day_fish_num
         , zp.fish_num
     FROM (
         SELECT pond_id, accumulate_fish_num FROM (
             SELECT pond_id,SUM(fish_num) AS accumulate_fish_num FROM zk_pond_fish_detail
             WHERE add_type = -2
             GROUP BY pond_id
         ) t11 INNER JOIN zk_pond t12 ON t11.pond_id = t12.id
     ) t1 LEFT JOIN (
         SELECT pond_id, day_fish_num FROM (
             SELECT pond_id, SUM(fish_num) AS day_fish_num
             FROM zk_pond_fish_detail
             WHERE create_time >= DATE_FORMAT(CURDATE(), '%Y-%m-%d 00:00:00')
             AND create_time <= DATE_FORMAT(CURDATE(), '%Y-%m-%d 23:59:59')
             AND add_type = -2
             GROUP BY pond_id
         ) t11 INNER JOIN zk_pond t12 ON t11.pond_id = t12.id
     ) t2 ON t1.pond_id = t2.pond_id
     LEFT JOIN zk_pond zp ON zp.id = t1.pond_id

     * @return 获取死亡 当日和累计死亡信息
     */
    @Select({
            "SELECT  t1.*\n" +
                    "\t,IFNULL(t2.day_fish_num, 0) AS day_fish_num\n" +
                    "\t, zp.fish_num \n" +
                    "FROM (\n" +
                    "\tSELECT pond_id, accumulate_fish_num FROM (\n" +
                    "\t\tSELECT pond_id,SUM(fish_num) AS accumulate_fish_num FROM zk_pond_fish_detail\n" +
                    "\t\tWHERE add_type = -2\n" +
                    "\t\tGROUP BY pond_id\n" +
                    "\t) t11 INNER JOIN zk_pond t12 ON t11.pond_id = t12.id\n" +
                    ") t1 LEFT JOIN (\n" +
                    "\tSELECT pond_id, day_fish_num FROM (\n" +
                    "\t\tSELECT pond_id, SUM(fish_num) AS day_fish_num\n" +
                    "\t\tFROM zk_pond_fish_detail\n" +
                    "\t\tWHERE create_time >= DATE_FORMAT(CURDATE(), '%Y-%m-%d 00:00:00') \n" +
                    "\t\t  AND create_time <= DATE_FORMAT(CURDATE(), '%Y-%m-%d 23:59:59')\n" +
                    "\t\t  AND add_type = -2\n" +
                    "\t\tGROUP BY pond_id\n" +
                    "\t) t11 INNER JOIN zk_pond t12 ON t11.pond_id = t12.id\n" +
                    ") t2 ON t1.pond_id = t2.pond_id\n" +
                    "LEFT JOIN zk_pond zp ON zp.id = t1.pond_id"
    })
    List<Map<String, Object>> staticDeadInfo();


    /**
    SELECT
    all_ponds.pond_id,
            -- 计算7日死亡总量（从加鱼时间开始7天内）
    COALESCE(SUM(CASE
            WHEN d.add_type = '-2'
            AND d.create_time >= all_ponds.create_time
            AND d.create_time <= DATE_ADD(all_ponds.create_time, INTERVAL 7 DAY)
    THEN d.fish_num
    ELSE 0
    END), 0) AS seven_day_death,
    -- 计算总死亡量
    COALESCE(SUM(CASE
                    WHEN d.add_type = '-2'
                    THEN d.fish_num
                    ELSE 0
                    END), 0) AS total_death,
    -- 计算当日死亡量（使用CURDATE()获取当天日期）[12](@ref)[13](@ref)
    COALESCE(SUM(CASE
            WHEN d.add_type = '-2'
            AND DATE(d.create_time) = CURDATE()  -- 使用CURDATE()函数获取当前日期[12](@ref)[13](@ref)
    THEN d.fish_num
    ELSE 0
    END), 0) AS today_death,
    b.fish_num AS pond_fish_num
    FROM (
    -- 获取所有需要计算7日死亡量的池子（包括投放和分池）
                    SELECT
                    pond_id,
    create_time,
    ROW_NUMBER() OVER (PARTITION BY pond_id ORDER BY create_time) AS rn
    FROM (
        -- 投放记录（add_type = '1'）
            SELECT
            pond_id,
        create_time
                FROM zk_pond_fish_detail
                WHERE add_type = '1' AND deleted = 0

                UNION ALL

                -- 分池记录（add_type = '-3'）
                SELECT
                target_pond_id AS pond_id,
        create_time
                FROM zk_pond_fish_detail
                WHERE add_type = '-3' AND deleted = 0
    ) combined_ponds
) all_ponds
    LEFT JOIN zk_pond_fish_detail d ON all_ponds.pond_id = d.pond_id
    AND d.deleted = 0
    AND d.add_type = '-2'  -- 只关联死亡记录
    LEFT JOIN zk_pond b ON all_ponds.pond_id = b.id
    WHERE all_ponds.rn = 1  -- 只取每个池子的第一次加鱼记录（无论是投放还是分池）
    GROUP BY all_ponds.pond_id, b.fish_num
    ORDER BY all_ponds.pond_id;

     -- 已废弃
    select a.*,b.fish_num from (
                WITH ranked_records AS (
                SELECT
                a.target_pond_id AS pond_id,
                a.create_time,
                -- 为每个池子的分池记录编号
                        ROW_NUMBER() OVER (PARTITION BY a.target_pond_id ORDER BY a.create_time) AS rn
        FROM zk_pond_fish_detail a
        WHERE a.add_type = '-3' AND a.deleted = 0
                )
        SELECT
        r.pond_id,
                -- 计算7日死亡量（只关联第一次分池后的死亡记录）
        COALESCE(SUM(CASE
                WHEN d.add_type = '-2'
                AND d.create_time >= r.create_time
                AND d.create_time <= DATE_ADD(r.create_time, INTERVAL 7 DAY)
        THEN d.fish_num
        ELSE 0
        END), 0) AS seven_day_death,
            -- 计算总死亡量
        COALESCE(SUM(CASE
                        WHEN d.add_type = '-2'
                        THEN d.fish_num
                        ELSE 0
                        END), 0) AS total_death
        FROM ranked_records r
        LEFT JOIN zk_pond_fish_detail d ON r.pond_id = d.pond_id
        AND d.deleted = 0
        WHERE r.rn = 1  -- 只取每个池子的第一次分池记录
        GROUP BY r.pond_id
        ORDER BY r.pond_id
    ) a left join zk_pond b on a.pond_id = b.id

     */
    @Select({
            "SELECT \n" +
                    "    all_ponds.pond_id,\n" +
                    "    -- 计算7日死亡总量（从加鱼时间开始7天内）\n" +
                    "    COALESCE(SUM(CASE \n" +
                    "        WHEN d.add_type = '-2' \n" +
                    "        AND d.create_time >= all_ponds.create_time \n" +
                    "        AND d.create_time <= DATE_ADD(all_ponds.create_time, INTERVAL 7 DAY) \n" +
                    "        THEN d.fish_num \n" +
                    "        ELSE 0 \n" +
                    "    END), 0) AS seven_day_death,\n" +
                    "    -- 计算总死亡量\n" +
                    "    COALESCE(SUM(CASE \n" +
                    "        WHEN d.add_type = '-2' \n" +
                    "        THEN d.fish_num \n" +
                    "        ELSE 0 \n" +
                    "    END), 0) AS total_death,\n" +
                    "    -- 计算当日死亡量（使用CURDATE()获取当天日期）[12](@ref)[13](@ref)\n" +
                    "    COALESCE(SUM(CASE \n" +
                    "        WHEN d.add_type = '-2' \n" +
                    "        AND DATE(d.create_time) = CURDATE()  -- 使用CURDATE()函数获取当前日期[12](@ref)[13](@ref)\n" +
                    "        THEN d.fish_num \n" +
                    "        ELSE 0 \n" +
                    "    END), 0) AS today_death,\n" +
                    "    b.fish_num AS pond_fish_num\n" +
                    "FROM (\n" +
                    "    -- 获取所有需要计算7日死亡量的池子（包括投放和分池）\n" +
                    "    SELECT \n" +
                    "        pond_id,\n" +
                    "        create_time,\n" +
                    "        ROW_NUMBER() OVER (PARTITION BY pond_id ORDER BY create_time) AS rn\n" +
                    "    FROM (\n" +
                    "        -- 投放记录（add_type = '1'）\n" +
                    "        SELECT \n" +
                    "            pond_id,\n" +
                    "            create_time\n" +
                    "        FROM zk_pond_fish_detail \n" +
                    "        WHERE add_type = '1' AND deleted = 0\n" +
                    "        \n" +
                    "        UNION ALL\n" +
                    "        \n" +
                    "        -- 分池记录（add_type = '-3'）\n" +
                    "        SELECT \n" +
                    "            target_pond_id AS pond_id,\n" +
                    "            create_time\n" +
                    "        FROM zk_pond_fish_detail \n" +
                    "        WHERE add_type = '-3' AND deleted = 0\n" +
                    "    ) combined_ponds\n" +
                    ") all_ponds\n" +
                    "LEFT JOIN zk_pond_fish_detail d ON all_ponds.pond_id = d.pond_id \n" +
                    "    AND d.deleted = 0\n" +
                    "    AND d.add_type = '-2'  -- 只关联死亡记录\n" +
                    "LEFT JOIN zk_pond b ON all_ponds.pond_id = b.id\n" +
                    "WHERE all_ponds.rn = 1  -- 只取每个池子的第一次加鱼记录（无论是投放还是分池）\n" +
                    "GROUP BY all_ponds.pond_id, b.fish_num\n" +
                    "ORDER BY all_ponds.pond_id;\n"
    })
    @Options(flushCache = Options.FlushCachePolicy.TRUE, useCache = false)
    List<StaticDeadInfoVo3> staticDeadInfo3();

    @Select("SELECT IFNULL(SUM(fish_num), 0) FROM zk_pond_fish_detail " +
            "WHERE pond_id = #{pondId} AND deleted = 0 AND add_type IN ('-2', -2) " +
            "AND create_time >= CURDATE()")
    Integer selectTodayDeathCount(@Param("pondId") Long pondId);

}
