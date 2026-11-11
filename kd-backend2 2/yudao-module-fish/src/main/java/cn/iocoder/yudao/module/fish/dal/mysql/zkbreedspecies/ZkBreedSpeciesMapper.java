package cn.iocoder.yudao.module.fish.dal.mysql.zkbreedspecies;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbreedspecies.ZkBreedSpeciesDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies.vo.*;

/**
 * 养殖品种 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkBreedSpeciesMapper extends BaseMapperX<ZkBreedSpeciesDO> {

    default PageResult<ZkBreedSpeciesDO> selectPage(ZkBreedSpeciesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkBreedSpeciesDO>()
                .likeIfPresent(ZkBreedSpeciesDO::getSpeciesName, reqVO.getSpeciesName())
                .eqIfPresent(ZkBreedSpeciesDO::getQualityGrade, reqVO.getQualityGrade())
                .likeIfPresent(ZkBreedSpeciesDO::getScientificName, reqVO.getScientificName())
                .eqIfPresent(ZkBreedSpeciesDO::getSuitableTemp, reqVO.getSuitableTemp())
                .eqIfPresent(ZkBreedSpeciesDO::getBreedingCycle, reqVO.getBreedingCycle())
                .betweenIfPresent(ZkBreedSpeciesDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkBreedSpeciesDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkBreedSpeciesDO::getId));
    }

}