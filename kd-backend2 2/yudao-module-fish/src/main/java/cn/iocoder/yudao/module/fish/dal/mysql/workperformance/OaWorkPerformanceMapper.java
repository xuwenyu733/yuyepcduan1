package cn.iocoder.yudao.module.fish.dal.mysql.workperformance;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.workperformance.OaWorkPerformanceDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.workperformance.vo.*;

/**
 * OA工作绩效评分 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface OaWorkPerformanceMapper extends BaseMapperX<OaWorkPerformanceDO> {

    default PageResult<OaWorkPerformanceDO> selectPage(OaWorkPerformancePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OaWorkPerformanceDO>()
                .eqIfPresent(OaWorkPerformanceDO::getRaterId, reqVO.getRaterId())
                .eqIfPresent(OaWorkPerformanceDO::getRatedUserId, reqVO.getRatedUserId())
                .eqIfPresent(OaWorkPerformanceDO::getRatingMonth, reqVO.getRatingMonth())
                .betweenIfPresent(OaWorkPerformanceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OaWorkPerformanceDO::getCreateTime));
    }

    default List<OaWorkPerformanceDO> selectList(OaWorkPerformanceExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<OaWorkPerformanceDO>()
                .eqIfPresent(OaWorkPerformanceDO::getRaterId, reqVO.getRaterId())
                .eqIfPresent(OaWorkPerformanceDO::getRatedUserId, reqVO.getRatedUserId())
                .eqIfPresent(OaWorkPerformanceDO::getRatingMonth, reqVO.getRatingMonth())
                .betweenIfPresent(OaWorkPerformanceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OaWorkPerformanceDO::getCreateTime));
    }

    default OaWorkPerformanceDO selectByRaterAndRatedAndMonth(Long raterId, Long ratedUserId, String ratingMonth) {
        return selectOne(new LambdaQueryWrapperX<OaWorkPerformanceDO>()
                .eq(OaWorkPerformanceDO::getRaterId, raterId)
                .eq(OaWorkPerformanceDO::getRatedUserId, ratedUserId)
                .eq(OaWorkPerformanceDO::getRatingMonth, ratingMonth));
    }

}



