package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifypurchase;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypurchase.PurifyPurchaseDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo.*;

/**
 * 采购记录 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyPurchaseMapper extends BaseMapperX<PurifyPurchaseDO> {

    default PageResult<PurifyPurchaseDO> selectPage(PurifyPurchasePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyPurchaseDO>()
                .eqIfPresent(PurifyPurchaseDO::getPurchaseOrderNo, reqVO.getPurchaseOrderNo())
                .eqIfPresent(PurifyPurchaseDO::getCategory, reqVO.getCategory())
                .eqIfPresent(PurifyPurchaseDO::getSource, reqVO.getSource())
                .eqIfPresent(PurifyPurchaseDO::getDetailedAddress, reqVO.getDetailedAddress())
                .eqIfPresent(PurifyPurchaseDO::getContactPerson, reqVO.getContactPerson())
                .eqIfPresent(PurifyPurchaseDO::getContactPhone, reqVO.getContactPhone())
                .eqIfPresent(PurifyPurchaseDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(PurifyPurchaseDO::getUnitPrice, reqVO.getUnitPrice())
                .eqIfPresent(PurifyPurchaseDO::getTotalAmount, reqVO.getTotalAmount())
                .eqIfPresent(PurifyPurchaseDO::getTransportMethod, reqVO.getTransportMethod())
                .betweenIfPresent(PurifyPurchaseDO::getPurchaseDate, reqVO.getPurchaseDate())
                .eqIfPresent(PurifyPurchaseDO::getPurchaseManager, reqVO.getPurchaseManager())
                .eqIfPresent(PurifyPurchaseDO::getAvgWeight, reqVO.getAvgWeight())
                .eqIfPresent(PurifyPurchaseDO::getNotes, reqVO.getNotes())
                .betweenIfPresent(PurifyPurchaseDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyPurchaseDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyPurchaseDO::getId));
    }

}