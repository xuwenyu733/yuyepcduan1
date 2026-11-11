package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowmodelconfig;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig.ZkGrowModelConfigDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowmodelconfig.vo.*;

/**
 * 生长模型配置 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowModelConfigMapper extends BaseMapperX<ZkGrowModelConfigDO> {

    default PageResult<ZkGrowModelConfigDO> selectPage(ZkGrowModelConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowModelConfigDO>()
                .eqIfPresent(ZkGrowModelConfigDO::getSpeciesId, reqVO.getSpeciesId())
                .likeIfPresent(ZkGrowModelConfigDO::getModelName, reqVO.getModelName())
                .eqIfPresent(ZkGrowModelConfigDO::getLInf, reqVO.getLInf())
                .eqIfPresent(ZkGrowModelConfigDO::getWInf, reqVO.getWInf())
                .eqIfPresent(ZkGrowModelConfigDO::getKLength, reqVO.getKLength())
                .eqIfPresent(ZkGrowModelConfigDO::getKWeight, reqVO.getKWeight())
                .eqIfPresent(ZkGrowModelConfigDO::getInitialWeight, reqVO.getInitialWeight())
                .eqIfPresent(ZkGrowModelConfigDO::getInitialLength, reqVO.getInitialLength())
                .eqIfPresent(ZkGrowModelConfigDO::getOptimalTempMin, reqVO.getOptimalTempMin())
                .eqIfPresent(ZkGrowModelConfigDO::getOptimalTempMax, reqVO.getOptimalTempMax())
                .eqIfPresent(ZkGrowModelConfigDO::getOptimalFeedRate, reqVO.getOptimalFeedRate())
                .betweenIfPresent(ZkGrowModelConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkGrowModelConfigDO::getId));
    }

    default ZkGrowModelConfigDO selectBySpeciesId(Long speciesId) {
        return selectOne(ZkGrowModelConfigDO::getSpeciesId, speciesId);
    }

}