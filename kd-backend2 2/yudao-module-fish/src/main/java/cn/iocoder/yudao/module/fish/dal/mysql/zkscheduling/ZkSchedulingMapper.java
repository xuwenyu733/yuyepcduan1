package cn.iocoder.yudao.module.fish.dal.mysql.zkscheduling;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo.*;

/**
 * 排班 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkSchedulingMapper extends BaseMapperX<ZkSchedulingDO> {

    default PageResult<ZkSchedulingDO> selectPage(ZkSchedulingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkSchedulingDO>()
                .likeIfPresent(ZkSchedulingDO::getSchedulingName, reqVO.getSchedulingName())
                .eqIfPresent(ZkSchedulingDO::getSchedulingContent, reqVO.getSchedulingContent())
                .eqIfPresent(ZkSchedulingDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkSchedulingDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkSchedulingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkSchedulingDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkSchedulingDO::getUserIdStr, reqVO.getUserIdStr())
                .betweenIfPresent(ZkSchedulingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkSchedulingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkSchedulingDO::getId));
    }

    default List<ZkSchedulingDO> selectList(ZkSchedulingExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkSchedulingDO>()
                .likeIfPresent(ZkSchedulingDO::getSchedulingName, reqVO.getSchedulingName())
                .eqIfPresent(ZkSchedulingDO::getSchedulingContent, reqVO.getSchedulingContent())
                .eqIfPresent(ZkSchedulingDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkSchedulingDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkSchedulingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkSchedulingDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkSchedulingDO::getUserIdStr, reqVO.getUserIdStr())
                .betweenIfPresent(ZkSchedulingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkSchedulingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkSchedulingDO::getId));
    }

}
