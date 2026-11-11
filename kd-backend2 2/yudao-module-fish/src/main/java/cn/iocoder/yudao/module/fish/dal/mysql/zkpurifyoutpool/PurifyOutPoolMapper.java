package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyoutpool;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool.PurifyOutPoolDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo.*;

/**
 * 出池登记 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyOutPoolMapper extends BaseMapperX<PurifyOutPoolDO> {

    default PageResult<PurifyOutPoolDO> selectPage(PurifyOutPoolPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyOutPoolDO>()
                .eqIfPresent(PurifyOutPoolDO::getProcessId, reqVO.getProcessId())
                .betweenIfPresent(PurifyOutPoolDO::getOutPoolTime, reqVO.getOutPoolTime())
                .eqIfPresent(PurifyOutPoolDO::getOutPoolWeight, reqVO.getOutPoolWeight())
                .eqIfPresent(PurifyOutPoolDO::getOutPoolCount, reqVO.getOutPoolCount())
                .eqIfPresent(PurifyOutPoolDO::getSpecification, reqVO.getSpecification())
                .likeIfPresent(PurifyOutPoolDO::getCustomerName, reqVO.getCustomerName())
                .eqIfPresent(PurifyOutPoolDO::getSalesPrice, reqVO.getSalesPrice())
                .eqIfPresent(PurifyOutPoolDO::getTotalAmount, reqVO.getTotalAmount())
                .eqIfPresent(PurifyOutPoolDO::getFishQuality, reqVO.getFishQuality())
                .eqIfPresent(PurifyOutPoolDO::getTestOperator, reqVO.getTestOperator())
                .betweenIfPresent(PurifyOutPoolDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyOutPoolDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyOutPoolDO::getId));
    }

}