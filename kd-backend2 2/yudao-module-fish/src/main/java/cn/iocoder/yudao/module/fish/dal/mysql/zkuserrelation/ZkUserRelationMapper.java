package cn.iocoder.yudao.module.fish.dal.mysql.zkuserrelation;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkuserrelation.ZkUserRelationDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation.vo.*;

/**
 * 用户信息关联 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkUserRelationMapper extends BaseMapperX<ZkUserRelationDO> {

    default PageResult<ZkUserRelationDO> selectPage(ZkUserRelationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkUserRelationDO>()
                .eqIfPresent(ZkUserRelationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkUserRelationDO::getRelationType, reqVO.getRelationType())
                .eqIfPresent(ZkUserRelationDO::getRelationValue, reqVO.getRelationValue())
                .eqIfPresent(ZkUserRelationDO::getRelationRemark, reqVO.getRelationRemark())
                .betweenIfPresent(ZkUserRelationDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkUserRelationDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkUserRelationDO::getId));
    }

}