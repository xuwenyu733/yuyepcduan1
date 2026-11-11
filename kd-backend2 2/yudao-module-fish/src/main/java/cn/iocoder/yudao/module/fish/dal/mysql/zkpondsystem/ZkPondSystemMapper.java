package cn.iocoder.yudao.module.fish.dal.mysql.zkpondsystem;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondsystem.ZkPondSystemDO;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo.*;

/**
 * 存塘系统 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPondSystemMapper extends BaseMapperX<ZkPondSystemDO> {

    default PageResult<ZkPondSystemDO> selectPage(ZkPondSystemPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkPondSystemDO> zkPondSystemDOLambdaQueryWrapperX = new LambdaQueryWrapperX<ZkPondSystemDO>()
                .eqIfPresent(ZkPondSystemDO::getSystemNum, reqVO.getSystemNum())
                .likeIfPresent(ZkPondSystemDO::getSystemName, reqVO.getSystemName())
                .likeIfPresent(ZkPondSystemDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondSystemDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPondSystemDO::getUserName, reqVO.getUserName())
                .betweenIfPresent(ZkPondSystemDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondSystemDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondSystemDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondSystemDO::getId);
        QueryUtil.applyDeptFilter(zkPondSystemDOLambdaQueryWrapperX, ZkPondSystemDO::getDeptId, reqVO);
        return selectPage(reqVO, zkPondSystemDOLambdaQueryWrapperX);
    }

    default List<ZkPondSystemDO> selectList(ZkPondSystemExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPondSystemDO>()
                .eqIfPresent(ZkPondSystemDO::getSystemNum, reqVO.getSystemNum())
                .likeIfPresent(ZkPondSystemDO::getSystemName, reqVO.getSystemName())
                .likeIfPresent(ZkPondSystemDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPondSystemDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPondSystemDO::getUserName, reqVO.getUserName())
                .betweenIfPresent(ZkPondSystemDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondSystemDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPondSystemDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPondSystemDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPondSystemDO::getId));
    }

}
