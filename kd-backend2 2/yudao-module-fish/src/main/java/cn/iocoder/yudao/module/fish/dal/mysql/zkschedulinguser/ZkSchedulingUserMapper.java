package cn.iocoder.yudao.module.fish.dal.mysql.zkschedulinguser;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulinguser.ZkSchedulingUserDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo.*;

/**
 * 排班用户 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkSchedulingUserMapper extends BaseMapperX<ZkSchedulingUserDO> {

    default PageResult<ZkSchedulingUserDO> selectPage(ZkSchedulingUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkSchedulingUserDO>()
                .eqIfPresent(ZkSchedulingUserDO::getSchedulingId, reqVO.getSchedulingId())
                .eqIfPresent(ZkSchedulingUserDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkSchedulingUserDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkSchedulingUserDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkSchedulingUserDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkSchedulingUserDO::getFid, reqVO.getFid())
                .orderByDesc(ZkSchedulingUserDO::getId));
    }

    default List<ZkSchedulingUserDO> selectList(ZkSchedulingUserExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkSchedulingUserDO>()
                .eqIfPresent(ZkSchedulingUserDO::getSchedulingId, reqVO.getSchedulingId())
                .eqIfPresent(ZkSchedulingUserDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkSchedulingUserDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkSchedulingUserDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkSchedulingUserDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkSchedulingUserDO::getFid, reqVO.getFid())
                .orderByDesc(ZkSchedulingUserDO::getId));
    }

}
