package cn.iocoder.yudao.module.fish.dal.mysql.zkpaperanswer;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo.*;

/**
 * 答题记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPaperAnswerMapper extends BaseMapperX<ZkPaperAnswerDO> {

    default PageResult<ZkPaperAnswerDO> selectPage(ZkPaperAnswerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPaperAnswerDO>()
                .eqIfPresent(ZkPaperAnswerDO::getPaperId, reqVO.getPaperId())
                .eqIfPresent(ZkPaperAnswerDO::getAnswer, reqVO.getAnswer())
                .betweenIfPresent(ZkPaperAnswerDO::getSubmitTime, reqVO.getSubmitTime())
                .eqIfPresent(ZkPaperAnswerDO::getScore, reqVO.getScore())
                .eqIfPresent(ZkPaperAnswerDO::getPassed, reqVO.getPassed())
                .betweenIfPresent(ZkPaperAnswerDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPaperAnswerDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPaperAnswerDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPaperAnswerDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPaperAnswerDO::getId));
    }

    default List<ZkPaperAnswerDO> selectList(ZkPaperAnswerExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPaperAnswerDO>()
                .eqIfPresent(ZkPaperAnswerDO::getPaperId, reqVO.getPaperId())
                .eqIfPresent(ZkPaperAnswerDO::getAnswer, reqVO.getAnswer())
                .betweenIfPresent(ZkPaperAnswerDO::getSubmitTime, reqVO.getSubmitTime())
                .eqIfPresent(ZkPaperAnswerDO::getScore, reqVO.getScore())
                .eqIfPresent(ZkPaperAnswerDO::getPassed, reqVO.getPassed())
                .betweenIfPresent(ZkPaperAnswerDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPaperAnswerDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPaperAnswerDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPaperAnswerDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPaperAnswerDO::getId));
    }

}
