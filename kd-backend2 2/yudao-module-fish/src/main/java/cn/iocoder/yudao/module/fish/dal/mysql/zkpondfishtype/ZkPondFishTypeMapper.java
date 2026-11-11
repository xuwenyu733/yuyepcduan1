package cn.iocoder.yudao.module.fish.dal.mysql.zkpondfishtype;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishtype.ZkPondFishTypeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype.vo.*;

/**
 * 鱼苗种类 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkPondFishTypeMapper extends BaseMapperX<ZkPondFishTypeDO> {

    default PageResult<ZkPondFishTypeDO> selectPage(ZkPondFishTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPondFishTypeDO>()
                .eqIfPresent(ZkPondFishTypeDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPondFishTypeDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkPondFishTypeDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkPondFishTypeDO::getFishSpec, reqVO.getFishSpec())
                .eqIfPresent(ZkPondFishTypeDO::getStaticYear, reqVO.getStaticYear())
                .eqIfPresent(ZkPondFishTypeDO::getStaticMonth, reqVO.getStaticMonth())
                .eqIfPresent(ZkPondFishTypeDO::getStaticDay, reqVO.getStaticDay())
                .eqIfPresent(ZkPondFishTypeDO::getFishType, reqVO.getFishType())
                .betweenIfPresent(ZkPondFishTypeDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondFishTypeDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkPondFishTypeDO::getId));
    }

}