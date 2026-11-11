package cn.iocoder.yudao.module.fish.dal.mysql.zkworkshop;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkshop.ZkWorkshopDO;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo.*;

/**
 * 车间 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkWorkshopMapper extends BaseMapperX<ZkWorkshopDO> {

    @Select("SELECT " +
            "    zw.workshop_name, " +
            "    zw.id AS workshop_id, " +
            "    zp.pond_count " +
            "FROM zk_workshop zw " +
            "INNER JOIN ( " +
            "    SELECT " +
            "        workshop_id, " +
            "        COUNT(*) AS pond_count " +
            "    FROM zk_pond " +
            "    WHERE deleted = 0 " +
            "    GROUP BY workshop_id " +
            ") zp ON zp.workshop_id = zw.id " +
            "WHERE zw.deleted = 0")
    List<ZkWorkshopDashboardRespVO> selectDashboardList();

    default PageResult<ZkWorkshopDO> selectPage(ZkWorkshopPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkWorkshopDO> zkWorkshopDOLambdaQueryWrapperX = new LambdaQueryWrapperX<ZkWorkshopDO>()
                .likeIfPresent(ZkWorkshopDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkWorkshopDO::getWorkshopPosition, reqVO.getWorkshopPosition())
                .eqIfPresent(ZkWorkshopDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(ZkWorkshopDO::getDimension, reqVO.getDimension())
                .likeIfPresent(ZkWorkshopDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkWorkshopDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkWorkshopDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkWorkshopDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkWorkshopDO::getFid, reqVO.getFid())
                .orderByDesc(ZkWorkshopDO::getId);
        QueryUtil.applyDeptFilter(zkWorkshopDOLambdaQueryWrapperX, ZkWorkshopDO::getDeptId, reqVO);
        return selectPage(reqVO, zkWorkshopDOLambdaQueryWrapperX);
    }

    default List<ZkWorkshopDO> selectList(ZkWorkshopExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkWorkshopDO>()
                .likeIfPresent(ZkWorkshopDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkWorkshopDO::getWorkshopPosition, reqVO.getWorkshopPosition())
                .eqIfPresent(ZkWorkshopDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(ZkWorkshopDO::getDimension, reqVO.getDimension())
                .likeIfPresent(ZkWorkshopDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkWorkshopDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkWorkshopDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkWorkshopDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkWorkshopDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkWorkshopDO::getFid, reqVO.getFid())
                .orderByDesc(ZkWorkshopDO::getId));
    }

}
