package cn.iocoder.yudao.module.fish.dal.mysql.zkpaper;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper.ZkPaperDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo.*;

/**
 * 试卷 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPaperMapper extends BaseMapperX<ZkPaperDO> {

    default PageResult<ZkPaperDO> selectPage(ZkPaperPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPaperDO>()
                .likeIfPresent(ZkPaperDO::getName, reqVO.getName())
                .eqIfPresent(ZkPaperDO::getContent, reqVO.getContent())
                .betweenIfPresent(ZkPaperDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ZkPaperDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ZkPaperDO::getDuration, reqVO.getDuration())
                .eqIfPresent(ZkPaperDO::getLookAnswer, reqVO.getLookAnswer())
                .eqIfPresent(ZkPaperDO::getUpset, reqVO.getUpset())
                .eqIfPresent(ZkPaperDO::getPassScore, reqVO.getPassScore())
                .eqIfPresent(ZkPaperDO::getTotalScore, reqVO.getTotalScore())
                .eqIfPresent(ZkPaperDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkPaperDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPaperDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPaperDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPaperDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPaperDO::getId));
    }

    default List<ZkPaperDO> selectList(ZkPaperExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPaperDO>()
                .likeIfPresent(ZkPaperDO::getName, reqVO.getName())
                .eqIfPresent(ZkPaperDO::getContent, reqVO.getContent())
                .betweenIfPresent(ZkPaperDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ZkPaperDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ZkPaperDO::getDuration, reqVO.getDuration())
                .eqIfPresent(ZkPaperDO::getLookAnswer, reqVO.getLookAnswer())
                .eqIfPresent(ZkPaperDO::getUpset, reqVO.getUpset())
                .eqIfPresent(ZkPaperDO::getPassScore, reqVO.getPassScore())
                .eqIfPresent(ZkPaperDO::getTotalScore, reqVO.getTotalScore())
                .eqIfPresent(ZkPaperDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkPaperDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPaperDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPaperDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPaperDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPaperDO::getId));
    }

}
