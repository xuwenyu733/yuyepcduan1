package cn.iocoder.yudao.module.fish.dal.mysql.zkpondfishparent;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishparent.ZkPondFishParentDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent.vo.*;

/**
 * 鱼池操作父 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkPondFishParentMapper extends BaseMapperX<ZkPondFishParentDO> {

    default PageResult<ZkPondFishParentDO> selectPage(ZkPondFishParentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPondFishParentDO>()
                .eqIfPresent(ZkPondFishParentDO::getOriginPondId, reqVO.getOriginPondId())
                .eqIfPresent(ZkPondFishParentDO::getFishTypes, reqVO.getFishTypes())
                .eqIfPresent(ZkPondFishParentDO::getProductNo, reqVO.getProductNo())
                .likeIfPresent(ZkPondFishParentDO::getFishName, reqVO.getFishName())
                .eqIfPresent(ZkPondFishParentDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkPondFishParentDO::getFishNumDiff, reqVO.getFishNumDiff())
                .eqIfPresent(ZkPondFishParentDO::getAddType, reqVO.getAddType())
                .eqIfPresent(ZkPondFishParentDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ZkPondFishParentDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondFishParentDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkPondFishParentDO::getId));
    }

}