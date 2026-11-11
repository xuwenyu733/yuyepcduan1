package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifypool;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypool.PurifyPoolDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypool.vo.*;

/**
 * 鱼池管理 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyPoolMapper extends BaseMapperX<PurifyPoolDO> {

    default PageResult<PurifyPoolDO> selectPage(PurifyPoolPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyPoolDO>()
                .eqIfPresent(PurifyPoolDO::getPoolNumber, reqVO.getPoolNumber())
                .eqIfPresent(PurifyPoolDO::getSize, reqVO.getSize())
                .eqIfPresent(PurifyPoolDO::getLocation, reqVO.getLocation())
                .eqIfPresent(PurifyPoolDO::getManager, reqVO.getManager())
                .eqIfPresent(PurifyPoolDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PurifyPoolDO::getNotes, reqVO.getNotes())
                .betweenIfPresent(PurifyPoolDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyPoolDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyPoolDO::getId));
    }

}