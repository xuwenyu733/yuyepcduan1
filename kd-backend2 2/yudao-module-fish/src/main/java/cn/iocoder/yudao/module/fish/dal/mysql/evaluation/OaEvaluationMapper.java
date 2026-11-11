package cn.iocoder.yudao.module.fish.dal.mysql.evaluation;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.evaluation.OaEvaluationDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.evaluation.vo.*;

/**
 * OA考核评分 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface OaEvaluationMapper extends BaseMapperX<OaEvaluationDO> {

    default PageResult<OaEvaluationDO> selectPage(OaEvaluationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OaEvaluationDO>()
                .eqIfPresent(OaEvaluationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OaEvaluationDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(OaEvaluationDO::getEvaluationPeriod, reqVO.getEvaluationPeriod())
                .betweenIfPresent(OaEvaluationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OaEvaluationDO::getEvaluationPeriod)
                .orderByDesc(OaEvaluationDO::getTotalScore));
    }

    default List<OaEvaluationDO> selectList(OaEvaluationExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<OaEvaluationDO>()
                .eqIfPresent(OaEvaluationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OaEvaluationDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(OaEvaluationDO::getEvaluationPeriod, reqVO.getEvaluationPeriod())
                .betweenIfPresent(OaEvaluationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OaEvaluationDO::getEvaluationPeriod)
                .orderByDesc(OaEvaluationDO::getTotalScore));
    }

    default OaEvaluationDO selectByUserIdAndPeriod(Long userId, String evaluationPeriod) {
        return selectOne(new LambdaQueryWrapperX<OaEvaluationDO>()
                .eq(OaEvaluationDO::getUserId, userId)
                .eq(OaEvaluationDO::getEvaluationPeriod, evaluationPeriod));
    }

    default List<OaEvaluationDO> selectByPeriod(String evaluationPeriod) {
        return selectList(new LambdaQueryWrapperX<OaEvaluationDO>()
                .eq(OaEvaluationDO::getEvaluationPeriod, evaluationPeriod)
                .orderByDesc(OaEvaluationDO::getTotalScore));
    }

    default List<OaEvaluationDO> selectByUserIdAndPeriods(Long userId, List<String> periods) {
        LambdaQueryWrapperX<OaEvaluationDO> wrapper = new LambdaQueryWrapperX<OaEvaluationDO>()
                .eq(OaEvaluationDO::getUserId, userId);
        // 只有当periods不为空时才添加IN条件，避免SQL语法错误
        if (periods != null && !periods.isEmpty()) {
            wrapper.in(OaEvaluationDO::getEvaluationPeriod, periods);
        } else {
            // 如果periods为空，返回空列表
            return Collections.emptyList();
        }
        return selectList(wrapper.orderByDesc(OaEvaluationDO::getEvaluationPeriod));
    }

}

