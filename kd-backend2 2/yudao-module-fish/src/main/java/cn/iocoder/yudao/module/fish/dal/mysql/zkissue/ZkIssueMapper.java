package cn.iocoder.yudao.module.fish.dal.mysql.zkissue;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkissue.ZkIssueDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo.*;

/**
 * 问题总结 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkIssueMapper extends BaseMapperX<ZkIssueDO> {

    default PageResult<ZkIssueDO> selectPage(ZkIssuePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkIssueDO>()
                .eqIfPresent(ZkIssueDO::getIssueTitle, reqVO.getIssueTitle())
                .eqIfPresent(ZkIssueDO::getIssueContent, reqVO.getIssueContent())
                .eqIfPresent(ZkIssueDO::getSummaryTitle, reqVO.getSummaryTitle())
                .eqIfPresent(ZkIssueDO::getSummaryContent, reqVO.getSummaryContent())
                .eqIfPresent(ZkIssueDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkIssueDO::getFids, reqVO.getFids())
                .eqIfPresent(ZkIssueDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .betweenIfPresent(ZkIssueDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkIssueDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkIssueDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkIssueDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkIssueDO::getCreator, reqVO.getCreator())
                .orderByDesc(ZkIssueDO::getId));
    }

    default List<ZkIssueDO> selectList(ZkIssueExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkIssueDO>()
                .eqIfPresent(ZkIssueDO::getIssueTitle, reqVO.getIssueTitle())
                .eqIfPresent(ZkIssueDO::getIssueContent, reqVO.getIssueContent())
                .eqIfPresent(ZkIssueDO::getSummaryTitle, reqVO.getSummaryTitle())
                .eqIfPresent(ZkIssueDO::getSummaryContent, reqVO.getSummaryContent())
                .eqIfPresent(ZkIssueDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkIssueDO::getFids, reqVO.getFids())
                .eqIfPresent(ZkIssueDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .betweenIfPresent(ZkIssueDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkIssueDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkIssueDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkIssueDO::getFid, reqVO.getFid())
                .orderByDesc(ZkIssueDO::getId));
    }

}
