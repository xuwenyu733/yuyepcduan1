package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpermission;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission.ZkExperimentalPermissionLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo.*;

@Mapper
public interface ZkExperimentalPermissionLogMapper extends BaseMapperX<ZkExperimentalPermissionLogDO> {

    default PageResult<ZkExperimentalPermissionLogDO> selectPage(ZkExperimentalPermissionLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkExperimentalPermissionLogDO>()
                .likeIfPresent(ZkExperimentalPermissionLogDO::getUserName, reqVO.getUserName())
                .likeIfPresent(ZkExperimentalPermissionLogDO::getAction, reqVO.getAction())
                .betweenIfPresent(ZkExperimentalPermissionLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkExperimentalPermissionLogDO::getId));
    }

}
