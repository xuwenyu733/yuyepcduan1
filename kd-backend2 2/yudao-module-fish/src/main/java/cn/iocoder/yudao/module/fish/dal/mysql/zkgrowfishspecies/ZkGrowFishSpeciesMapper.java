package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowfishspecies;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies.ZkGrowFishSpeciesDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies.vo.*;

/**
 * 鱼类品种 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowFishSpeciesMapper extends BaseMapperX<ZkGrowFishSpeciesDO> {

    default PageResult<ZkGrowFishSpeciesDO> selectPage(ZkGrowFishSpeciesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowFishSpeciesDO>()
                .likeIfPresent(ZkGrowFishSpeciesDO::getName, reqVO.getName())
                .likeIfPresent(ZkGrowFishSpeciesDO::getCommonName, reqVO.getCommonName())
                .likeIfPresent(ZkGrowFishSpeciesDO::getScientificName, reqVO.getScientificName())
                .eqIfPresent(ZkGrowFishSpeciesDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(ZkGrowFishSpeciesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkGrowFishSpeciesDO::getId));
    }

}