package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifydictdata;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydictdata.PurifyDictDataDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydictdata.vo.*;

/**
 * 业务字典明细 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyDictDataMapper extends BaseMapperX<PurifyDictDataDO> {

    default PageResult<PurifyDictDataDO> selectPage(PurifyDictDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyDictDataDO>()
                .eqIfPresent(PurifyDictDataDO::getDictType, reqVO.getDictType())
                .eqIfPresent(PurifyDictDataDO::getDictLabel, reqVO.getDictLabel())
                .eqIfPresent(PurifyDictDataDO::getDictValue, reqVO.getDictValue())
                .eqIfPresent(PurifyDictDataDO::getSort, reqVO.getSort())
                .eqIfPresent(PurifyDictDataDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(PurifyDictDataDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyDictDataDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyDictDataDO::getId));
    }

}