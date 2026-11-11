package cn.iocoder.yudao.module.fish.dal.mysql.zkillcontrol;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkillcontrol.ZkIllControlDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo.*;

/**
 * 疾病诊断与防治记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkIllControlMapper extends BaseMapperX<ZkIllControlDO> {

    default PageResult<ZkIllControlDO> selectPage(ZkIllControlPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkIllControlDO>()
                .eqIfPresent(ZkIllControlDO::getIllTitle, reqVO.getIllTitle())
                .eqIfPresent(ZkIllControlDO::getIllContent, reqVO.getIllContent())
                .eqIfPresent(ZkIllControlDO::getControlTitle, reqVO.getControlTitle())
                .eqIfPresent(ZkIllControlDO::getControlContent, reqVO.getControlContent())
                .eqIfPresent(ZkIllControlDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkIllControlDO::getFids, reqVO.getFids())
                .eqIfPresent(ZkIllControlDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .betweenIfPresent(ZkIllControlDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkIllControlDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkIllControlDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkIllControlDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkIllControlDO::getCreator, reqVO.getCreator())
                .orderByDesc(ZkIllControlDO::getId));
    }

    default List<ZkIllControlDO> selectList(ZkIllControlExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkIllControlDO>()
                .eqIfPresent(ZkIllControlDO::getIllTitle, reqVO.getIllTitle())
                .eqIfPresent(ZkIllControlDO::getIllContent, reqVO.getIllContent())
                .eqIfPresent(ZkIllControlDO::getControlTitle, reqVO.getControlTitle())
                .eqIfPresent(ZkIllControlDO::getControlContent, reqVO.getControlContent())
                .eqIfPresent(ZkIllControlDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkIllControlDO::getFids, reqVO.getFids())
                .eqIfPresent(ZkIllControlDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .betweenIfPresent(ZkIllControlDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkIllControlDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkIllControlDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkIllControlDO::getFid, reqVO.getFid())
                .orderByDesc(ZkIllControlDO::getId));
    }

}
