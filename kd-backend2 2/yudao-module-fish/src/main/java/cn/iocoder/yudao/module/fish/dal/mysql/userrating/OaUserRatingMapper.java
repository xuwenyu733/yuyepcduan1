package cn.iocoder.yudao.module.fish.dal.mysql.userrating;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.userrating.OaUserRatingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.userrating.vo.*;

/**
 * OA他人打分 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface OaUserRatingMapper extends BaseMapperX<OaUserRatingDO> {

  default PageResult<OaUserRatingDO> selectPage(OaUserRatingPageReqVO reqVO) {
    return selectPage(reqVO, new LambdaQueryWrapperX<OaUserRatingDO>()
        .eqIfPresent(OaUserRatingDO::getRaterId, reqVO.getRaterId())
        .eqIfPresent(OaUserRatingDO::getRatedUserId, reqVO.getRatedUserId())
        .eqIfPresent(OaUserRatingDO::getRatingMonth, reqVO.getRatingMonth())
        .betweenIfPresent(OaUserRatingDO::getCreateTime, reqVO.getCreateTime())
        .orderByDesc(OaUserRatingDO::getCreateTime));
  }

  default List<OaUserRatingDO> selectList(OaUserRatingExportReqVO reqVO) {
    return selectList(new LambdaQueryWrapperX<OaUserRatingDO>()
        .eqIfPresent(OaUserRatingDO::getRaterId, reqVO.getRaterId())
        .eqIfPresent(OaUserRatingDO::getRatedUserId, reqVO.getRatedUserId())
        .eqIfPresent(OaUserRatingDO::getRatingMonth, reqVO.getRatingMonth())
        .betweenIfPresent(OaUserRatingDO::getCreateTime, reqVO.getCreateTime())
        .orderByDesc(OaUserRatingDO::getCreateTime));
  }

  default OaUserRatingDO selectByRaterAndRatedAndMonth(Long raterId, Long ratedUserId, String ratingMonth) {
    return selectOne(new LambdaQueryWrapperX<OaUserRatingDO>()
        .eq(OaUserRatingDO::getRaterId, raterId)
        .eq(OaUserRatingDO::getRatedUserId, ratedUserId)
        .eq(OaUserRatingDO::getRatingMonth, ratingMonth));
  }

}


