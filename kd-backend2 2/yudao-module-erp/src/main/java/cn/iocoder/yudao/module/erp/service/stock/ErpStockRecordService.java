package cn.iocoder.yudao.module.erp.service.stock;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.record.ErpStockRecordPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockRecordDO;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * ERP 产品库存明细 Service 接口
 *
 * @author 芋道源码
 */
public interface ErpStockRecordService {

    /**
     * 获得产品库存明细
     *
     * @param id 编号
     * @return 产品库存明细
     */
    ErpStockRecordDO getStockRecord(Long id);

    /**
     * 获得产品库存明细分页
     *
     * @param pageReqVO 分页查询
     * @return 产品库存明细分页
     */
    PageResult<ErpStockRecordDO> getStockRecordPage(ErpStockRecordPageReqVO pageReqVO);

    /**
     * 创建库存明细
     *
     * @param createReqBO 创建库存明细 BO
     */
    ErpStockRecordDO createStockRecord(@Valid ErpStockRecordCreateReqBO createReqBO);

    void updateStockRecord(ErpStockRecordCreateReqBO updateReqVO);

    /**
     * 批量删除 出库单
     * @param ids stock_record.ids
     */
    void deleteByIds(List<Long> ids);

    void removeByBizTypeAndBusId(int bizType, Long busId);
}