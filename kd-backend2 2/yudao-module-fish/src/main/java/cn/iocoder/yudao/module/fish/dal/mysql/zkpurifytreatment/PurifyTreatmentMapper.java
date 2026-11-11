package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifytreatment;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifytreatment.PurifyTreatmentDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment.vo.*;

/**
 * 处理措施记录 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyTreatmentMapper extends BaseMapperX<PurifyTreatmentDO> {

    default PageResult<PurifyTreatmentDO> selectPage(PurifyTreatmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyTreatmentDO>()
                .eqIfPresent(PurifyTreatmentDO::getProcessId, reqVO.getProcessId())
                .betweenIfPresent(PurifyTreatmentDO::getTreatmentDate, reqVO.getTreatmentDate())
                .eqIfPresent(PurifyTreatmentDO::getType, reqVO.getType())
                .eqIfPresent(PurifyTreatmentDO::getContent, reqVO.getContent())
                .eqIfPresent(PurifyTreatmentDO::getDosage, reqVO.getDosage())
                .eqIfPresent(PurifyTreatmentDO::getOperator, reqVO.getOperator())
                .eqIfPresent(PurifyTreatmentDO::getNotes, reqVO.getNotes())
                .betweenIfPresent(PurifyTreatmentDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyTreatmentDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyTreatmentDO::getId));
    }

}