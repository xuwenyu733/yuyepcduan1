package cn.iocoder.yudao.module.fish.dal.mysql.zkpondwarn;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn.ZkPondWarnDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.vo.*;

/**
 * 告警 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPondWarnMapper extends BaseMapperX<ZkPondWarnDO> {

    default PageResult<ZkPondWarnDO> selectPage(ZkPondWarnPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPondWarnDO>()
                .likeIfPresent(ZkPondWarnDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondWarnDO::getWorkshopPosition, reqVO.getWorkshopPosition())
                .eqIfPresent(ZkPondWarnDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPondWarnDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPondWarnDO::getPondId, reqVO.getPondId())
                .likeIfPresent(ZkPondWarnDO::getWarnName, reqVO.getWarnName())
                .eqIfPresent(ZkPondWarnDO::getWarnType, reqVO.getWarnType())
                .eqIfPresent(ZkPondWarnDO::getWarnContent, reqVO.getWarnContent())
                .likeIfPresent(ZkPondWarnDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkPondWarnDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkPondWarnDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondWarnDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondWarnDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondWarnDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondWarnDO::getId));
    }

    default List<ZkPondWarnDO> selectList(ZkPondWarnExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPondWarnDO>()
                .likeIfPresent(ZkPondWarnDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondWarnDO::getWorkshopPosition, reqVO.getWorkshopPosition())
                .eqIfPresent(ZkPondWarnDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPondWarnDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPondWarnDO::getPondId, reqVO.getPondId())
                .likeIfPresent(ZkPondWarnDO::getWarnName, reqVO.getWarnName())
                .eqIfPresent(ZkPondWarnDO::getWarnType, reqVO.getWarnType())
                .eqIfPresent(ZkPondWarnDO::getWarnContent, reqVO.getWarnContent())
                .likeIfPresent(ZkPondWarnDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkPondWarnDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkPondWarnDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondWarnDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondWarnDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondWarnDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondWarnDO::getId));
    }

}
