package cn.iocoder.yudao.module.fish.dal.mysql.zkschedulingtype;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype.vo.*;

/**
 * 检测配置 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkSchedulingTypeMapper extends BaseMapperX<ZkSchedulingTypeDO> {

    default PageResult<ZkSchedulingTypeDO> selectPage(ZkSchedulingTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkSchedulingTypeDO>()
                .likeIfPresent(ZkSchedulingTypeDO::getCheckName, reqVO.getCheckName())
                .eqIfPresent(ZkSchedulingTypeDO::getCheckType, reqVO.getCheckType())
                .eqIfPresent(ZkSchedulingTypeDO::getCheckContent, reqVO.getCheckContent())
                .betweenIfPresent(ZkSchedulingTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkSchedulingTypeDO::getId));
    }

    default List<ZkSchedulingTypeDO> selectList(ZkSchedulingTypeExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkSchedulingTypeDO>()
                .likeIfPresent(ZkSchedulingTypeDO::getCheckName, reqVO.getCheckName())
                .eqIfPresent(ZkSchedulingTypeDO::getCheckType, reqVO.getCheckType())
                .eqIfPresent(ZkSchedulingTypeDO::getCheckContent, reqVO.getCheckContent())
                .betweenIfPresent(ZkSchedulingTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkSchedulingTypeDO::getId));
    }

}
