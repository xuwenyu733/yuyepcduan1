package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowpool;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowpool.ZkGrowPoolDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowpool.vo.*;

/**
 * 生长模型鱼池 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowPoolMapper extends BaseMapperX<ZkGrowPoolDO> {

    default PageResult<ZkGrowPoolDO> selectPage(ZkGrowPoolPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowPoolDO>()
                .eqIfPresent(ZkGrowPoolDO::getPoolNumber, reqVO.getPoolNumber())
                .eqIfPresent(ZkGrowPoolDO::getSize, reqVO.getSize())
                .eqIfPresent(ZkGrowPoolDO::getLocation, reqVO.getLocation())
                .eqIfPresent(ZkGrowPoolDO::getManager, reqVO.getManager())
                .eqIfPresent(ZkGrowPoolDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkGrowPoolDO::getNotes, reqVO.getNotes())
                .betweenIfPresent(ZkGrowPoolDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkGrowPoolDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkGrowPoolDO::getId));
    }

}