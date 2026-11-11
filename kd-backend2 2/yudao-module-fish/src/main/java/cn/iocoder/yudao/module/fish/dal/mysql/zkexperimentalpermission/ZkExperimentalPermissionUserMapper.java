package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpermission;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission.ZkExperimentalPermissionUserDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo.*;

@Mapper
public interface ZkExperimentalPermissionUserMapper extends BaseMapperX<ZkExperimentalPermissionUserDO> {

    default PageResult<ZkExperimentalPermissionUserDO> selectPage(ZkExperimentalPermissionUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkExperimentalPermissionUserDO>()
                .likeIfPresent(ZkExperimentalPermissionUserDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkExperimentalPermissionUserDO::getRoleId, reqVO.getRoleId())
                .eqIfPresent(ZkExperimentalPermissionUserDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkExperimentalPermissionUserDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkExperimentalPermissionUserDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkExperimentalPermissionUserDO::getId));
    }

}
