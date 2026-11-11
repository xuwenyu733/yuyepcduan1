package cn.iocoder.yudao.module.fish.dal.mysql.zkkonwledgetree;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree.ZkKonwledgeTreeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo.*;

/**
 * 知识树 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkKonwledgeTreeMapper extends BaseMapperX<ZkKonwledgeTreeDO> {

    default PageResult<ZkKonwledgeTreeDO> selectPage(ZkKonwledgeTreePageReqVO reqVO) {
        LambdaQueryWrapperX<ZkKonwledgeTreeDO> query = new LambdaQueryWrapperX<ZkKonwledgeTreeDO>()
                .eqIfPresent(ZkKonwledgeTreeDO::getParentId, reqVO.getParentId())
                .likeIfPresent(ZkKonwledgeTreeDO::getNodeTitle, reqVO.getNodeTitle())
                .likeIfPresent(ZkKonwledgeTreeDO::getNodeTag, reqVO.getNodeTag())
                .eqIfPresent(ZkKonwledgeTreeDO::getNodeType, reqVO.getNodeType())
                .betweenIfPresent(ZkKonwledgeTreeDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkKonwledgeTreeDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkKonwledgeTreeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkKonwledgeTreeDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkKonwledgeTreeDO::getFileUrl, reqVO.getFileUrl());
                // .orderByDesc(ZkKonwledgeTreeDO::getId);
        query.orderByAsc(ZkKonwledgeTreeDO::getSort);
        return selectPage(reqVO, query);
    }

    default List<ZkKonwledgeTreeDO> selectList(ZkKonwledgeTreeExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkKonwledgeTreeDO>()
                .eqIfPresent(ZkKonwledgeTreeDO::getParentId, reqVO.getParentId())
                .eqIfPresent(ZkKonwledgeTreeDO::getNodeTitle, reqVO.getNodeTitle())
                .eqIfPresent(ZkKonwledgeTreeDO::getNodeTag, reqVO.getNodeTag())
                .eqIfPresent(ZkKonwledgeTreeDO::getNodeType, reqVO.getNodeType())
                .betweenIfPresent(ZkKonwledgeTreeDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkKonwledgeTreeDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkKonwledgeTreeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkKonwledgeTreeDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkKonwledgeTreeDO::getFileUrl, reqVO.getFileUrl())
                .orderByDesc(ZkKonwledgeTreeDO::getId));
    }

}
