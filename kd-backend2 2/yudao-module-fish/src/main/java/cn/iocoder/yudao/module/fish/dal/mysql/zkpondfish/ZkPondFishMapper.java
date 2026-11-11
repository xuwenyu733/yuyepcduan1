package cn.iocoder.yudao.module.fish.dal.mysql.zkpondfish;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfish.vo.ZkPondFishExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfish.vo.ZkPondFishPageReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 存塘鱼苗 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPondFishMapper extends BaseMapperX<ZkPondFishDO> {

    default PageResult<ZkPondFishDO> selectPage(ZkPondFishPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPondFishDO>()
                .likeIfPresent(ZkPondFishDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondFishDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPondFishDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPondFishDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPondFishDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkPondFishDO::getFishNum, reqVO.getFishNum())
                .betweenIfPresent(ZkPondFishDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondFishDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondFishDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondFishDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondFishDO::getId));
    }

    default List<ZkPondFishDO> selectList(ZkPondFishExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPondFishDO>()
                .likeIfPresent(ZkPondFishDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondFishDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPondFishDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPondFishDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPondFishDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkPondFishDO::getFishNum, reqVO.getFishNum())
                .betweenIfPresent(ZkPondFishDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondFishDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondFishDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondFishDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondFishDO::getId));
    }

    /**
     SELECT zpf.*, zp.`fish_spec` AS fishSpec, zbc.`feed_count` AS feedCount FROM zk_pond_fish zpf
         LEFT JOIN zk_pond zp ON zp.`id` = zpf.`pond_id`
         LEFT JOIN zk_biology_capital zbc ON zbc.`pond_id` = zpf.`pond_id`
     * @param queryWrapper
     * @return
     */
    @Select({
            "SELECT zpf.*, zp.`fish_spec` as fishSpec, zbc.`feed_count` as feedCount FROM zk_pond_fish zpf " +
                    "LEFT JOIN zk_pond zp ON zp.`id` = zpf.`pond_id` " +
                    "LEFT JOIN zk_biology_capital zbc ON zbc.`pond_id` = zpf.`pond_id` " +
                    "${ew.customSqlSegment}"
    })
    List<ZkPondFishDO> getListWithSpec(@Param("ew") QueryWrapper<ZkPondFishDO> queryWrapper);

    @Select({
            "SELECT * FROM zk_pond zp \n" +
                    "LEFT JOIN zk_pond_fish zpf ON zpf.pond_id=zp.id \n" +
                    "WHERE zpf.id IS NULL"
    })
    List<ZkPondDO> getByEmptyPondFish();

}
