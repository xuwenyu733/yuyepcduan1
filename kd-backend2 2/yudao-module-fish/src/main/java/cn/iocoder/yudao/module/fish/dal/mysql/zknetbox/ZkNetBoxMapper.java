package cn.iocoder.yudao.module.fish.dal.mysql.zknetbox;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zknetbox.ZkNetBoxDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zknetbox.vo.*;

/**
 * 网盘资源 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkNetBoxMapper extends BaseMapperX<ZkNetBoxDO> {

    default PageResult<ZkNetBoxDO> selectPage(ZkNetBoxPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkNetBoxDO>()
                .eqIfPresent(ZkNetBoxDO::getFileType, reqVO.getFileType())
                .likeIfPresent(ZkNetBoxDO::getFileTypeName, reqVO.getFileTypeName())
                .eqIfPresent(ZkNetBoxDO::getPrefixPath, reqVO.getPrefixPath())
                .eqIfPresent(ZkNetBoxDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ZkNetBoxDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkNetBoxDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkNetBoxDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkNetBoxDO::getFid, reqVO.getFid())
                .orderByDesc(ZkNetBoxDO::getId));
    }

    default List<ZkNetBoxDO> selectList(ZkNetBoxExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkNetBoxDO>()
                .eqIfPresent(ZkNetBoxDO::getFileType, reqVO.getFileType())
                .likeIfPresent(ZkNetBoxDO::getFileTypeName, reqVO.getFileTypeName())
                .eqIfPresent(ZkNetBoxDO::getPrefixPath, reqVO.getPrefixPath())
                .eqIfPresent(ZkNetBoxDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ZkNetBoxDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkNetBoxDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkNetBoxDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkNetBoxDO::getFid, reqVO.getFid())
                .orderByDesc(ZkNetBoxDO::getId));
    }

}
