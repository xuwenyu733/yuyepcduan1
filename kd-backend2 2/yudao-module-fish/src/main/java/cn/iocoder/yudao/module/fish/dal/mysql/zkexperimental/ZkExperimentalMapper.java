package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimental;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimental.ZkExperimentalDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo.*;

/**
 * 实验 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkExperimentalMapper extends BaseMapperX<ZkExperimentalDO> {

    default PageResult<ZkExperimentalDO> selectPage(ZkExperimentalPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkExperimentalDO>()
                .eqIfPresent(ZkExperimentalDO::getExperimentNumber, reqVO.getExperimentNumber())
                .likeIfPresent(ZkExperimentalDO::getExperimentTitle, reqVO.getExperimentTitle())
                .eqIfPresent(ZkExperimentalDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkExperimentalDO::getVariety, reqVO.getVariety())
                .eqIfPresent(ZkExperimentalDO::getPersonInCharge, reqVO.getPersonInCharge())
                .eqIfPresent(ZkExperimentalDO::getPersonInChargeId, reqVO.getPersonInChargeId())
                .betweenIfPresent(ZkExperimentalDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ZkExperimentalDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ZkExperimentalDO::getExperimentalStatus, reqVO.getExperimentalStatus())
                .eqIfPresent(ZkExperimentalDO::getExperimentalObjective, reqVO.getExperimentalObjective())
                .eqIfPresent(ZkExperimentalDO::getExperimentalContent, reqVO.getExperimentalContent())
                .eqIfPresent(ZkExperimentalDO::getExperimentalMethod, reqVO.getExperimentalMethod())
                .betweenIfPresent(ZkExperimentalDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkExperimentalDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkExperimentalDO::getId));
    }

}