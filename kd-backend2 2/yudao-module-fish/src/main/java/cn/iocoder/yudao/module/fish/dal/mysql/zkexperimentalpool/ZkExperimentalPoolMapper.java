package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpool;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool.ZkExperimentalPoolDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpool.vo.*;

/**
 * 实验鱼池 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkExperimentalPoolMapper extends BaseMapperX<ZkExperimentalPoolDO> {

    default PageResult<ZkExperimentalPoolDO> selectPage(ZkExperimentalPoolPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkExperimentalPoolDO>()
                .eqIfPresent(ZkExperimentalPoolDO::getPoolNumber, reqVO.getPoolNumber())
                .eqIfPresent(ZkExperimentalPoolDO::getSize, reqVO.getSize())
                .eqIfPresent(ZkExperimentalPoolDO::getLocation, reqVO.getLocation())
                .eqIfPresent(ZkExperimentalPoolDO::getManager, reqVO.getManager())
                .eqIfPresent(ZkExperimentalPoolDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkExperimentalPoolDO::getNotes, reqVO.getNotes())
                .betweenIfPresent(ZkExperimentalPoolDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkExperimentalPoolDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkExperimentalPoolDO::getId));
    }

}