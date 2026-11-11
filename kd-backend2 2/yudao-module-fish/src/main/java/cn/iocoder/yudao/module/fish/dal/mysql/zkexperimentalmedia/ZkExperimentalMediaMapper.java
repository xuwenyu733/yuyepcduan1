package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalmedia;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia.vo.*;

/**
 * 实验多媒体 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkExperimentalMediaMapper extends BaseMapperX<ZkExperimentalMediaDO> {

    default PageResult<ZkExperimentalMediaDO> selectPage(ZkExperimentalMediaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkExperimentalMediaDO>()
                .eqIfPresent(ZkExperimentalMediaDO::getExperimentId, reqVO.getExperimentId())
                .eqIfPresent(ZkExperimentalMediaDO::getExperimentNumber, reqVO.getExperimentNumber())
                .eqIfPresent(ZkExperimentalMediaDO::getFileType, reqVO.getFileType())
                .likeIfPresent(ZkExperimentalMediaDO::getBodyType, reqVO.getBodyType())
                .eqIfPresent(ZkExperimentalMediaDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkExperimentalMediaDO::getFileNames, reqVO.getFileNames())
                .eqIfPresent(ZkExperimentalMediaDO::getFileUrls, reqVO.getFileUrls())
                .betweenIfPresent(ZkExperimentalMediaDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkExperimentalMediaDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkExperimentalMediaDO::getId));
    }

}