package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentaldata;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata.vo.*;

/**
 * 实验数据采集 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkExperimentalDataMapper extends BaseMapperX<ZkExperimentalDataDO> {

    default PageResult<ZkExperimentalDataDO> selectPage(ZkExperimentalDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkExperimentalDataDO>()
                .eqIfPresent(ZkExperimentalDataDO::getExperimentId, reqVO.resolveExperimentId())
                .eqIfPresent(ZkExperimentalDataDO::getExperimentNumber, reqVO.getExperimentNumber())
                .eqIfPresent(ZkExperimentalDataDO::getCollectionType, reqVO.getCollectionType())
                .eqIfPresent(ZkExperimentalDataDO::getCollectData, reqVO.getCollectData())
                .betweenIfPresent(ZkExperimentalDataDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkExperimentalDataDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkExperimentalDataDO::getId));
    }

}