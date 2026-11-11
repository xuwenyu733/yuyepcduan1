package cn.iocoder.yudao.module.fish.dal.mysql.zkpaperquestion;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo.*;

/**
 * 试题 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPaperQuestionMapper extends BaseMapperX<ZkPaperQuestionDO> {

    default PageResult<ZkPaperQuestionDO> selectPage(ZkPaperQuestionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPaperQuestionDO>()
                .eqIfPresent(ZkPaperQuestionDO::getPaperId, reqVO.getPaperId())
                .eqIfPresent(ZkPaperQuestionDO::getType, reqVO.getType())
                .likeIfPresent(ZkPaperQuestionDO::getTypeName, reqVO.getTypeName())
                .eqIfPresent(ZkPaperQuestionDO::getQuestion, reqVO.getQuestion())
                .eqIfPresent(ZkPaperQuestionDO::getAnswer, reqVO.getAnswer())
                .eqIfPresent(ZkPaperQuestionDO::getAnalysis, reqVO.getAnalysis())
                .eqIfPresent(ZkPaperQuestionDO::getTag, reqVO.getTag())
                .eqIfPresent(ZkPaperQuestionDO::getScore, reqVO.getScore())
                .eqIfPresent(ZkPaperQuestionDO::getOptions, reqVO.getOptions())
                .betweenIfPresent(ZkPaperQuestionDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPaperQuestionDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPaperQuestionDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPaperQuestionDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPaperQuestionDO::getId));
    }

    default List<ZkPaperQuestionDO> selectList(ZkPaperQuestionExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPaperQuestionDO>()
                .eqIfPresent(ZkPaperQuestionDO::getPaperId, reqVO.getPaperId())
                .eqIfPresent(ZkPaperQuestionDO::getType, reqVO.getType())
                .likeIfPresent(ZkPaperQuestionDO::getTypeName, reqVO.getTypeName())
                .eqIfPresent(ZkPaperQuestionDO::getQuestion, reqVO.getQuestion())
                .eqIfPresent(ZkPaperQuestionDO::getAnswer, reqVO.getAnswer())
                .eqIfPresent(ZkPaperQuestionDO::getAnalysis, reqVO.getAnalysis())
                .eqIfPresent(ZkPaperQuestionDO::getTag, reqVO.getTag())
                .eqIfPresent(ZkPaperQuestionDO::getScore, reqVO.getScore())
                .eqIfPresent(ZkPaperQuestionDO::getOptions, reqVO.getOptions())
                .betweenIfPresent(ZkPaperQuestionDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPaperQuestionDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPaperQuestionDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPaperQuestionDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPaperQuestionDO::getId));
    }

}
