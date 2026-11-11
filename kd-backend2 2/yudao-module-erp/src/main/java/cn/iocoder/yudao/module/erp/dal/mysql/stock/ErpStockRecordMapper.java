package cn.iocoder.yudao.module.erp.dal.mysql.stock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.record.ErpStockRecordPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Objects;

/**
 * ERP 产品库存明细 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ErpStockRecordMapper extends BaseMapperX<ErpStockRecordDO> {

    default PageResult<ErpStockRecordDO> selectPage(ErpStockRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<ErpStockRecordDO> erpStockRecordDOLambdaQueryWrapperX = new LambdaQueryWrapperX<ErpStockRecordDO>()
                .eqIfPresent(ErpStockRecordDO::getProductId, reqVO.getProductId())
                .eqIfPresent(ErpStockRecordDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(ErpStockRecordDO::getBizType, reqVO.getBizType())
                .likeIfPresent(ErpStockRecordDO::getBizNo, reqVO.getBizNo())
                .betweenIfPresent(ErpStockRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpStockRecordDO::getId);
        if(Objects.equals(reqVO.getInType(), 1)) {
            // 入库
            erpStockRecordDOLambdaQueryWrapperX.gt(ErpStockRecordDO::getCount, 0);
        } else if (Objects.equals(reqVO.getInType(), 2)) {
            // 出库
            erpStockRecordDOLambdaQueryWrapperX.lt(ErpStockRecordDO::getCount, 0);
        }
        return selectPage(reqVO, erpStockRecordDOLambdaQueryWrapperX);
    }

}