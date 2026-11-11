package cn.iocoder.yudao.module.fish.dal.mysql.zkmedicinelogpond;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond.ZkMedicineLogPondDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond.vo.*;

/**
 * 用药存塘关联 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkMedicineLogPondMapper extends BaseMapperX<ZkMedicineLogPondDO> {

    default PageResult<ZkMedicineLogPondDO> selectPage(ZkMedicineLogPondPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkMedicineLogPondDO>()
                .eqIfPresent(ZkMedicineLogPondDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkMedicineLogPondDO::getMedicineLogId, reqVO.getMedicineLogId())
                .orderByDesc(ZkMedicineLogPondDO::getId));
    }

}