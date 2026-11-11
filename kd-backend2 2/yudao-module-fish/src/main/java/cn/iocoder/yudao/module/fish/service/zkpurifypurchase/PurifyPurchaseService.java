package cn.iocoder.yudao.module.fish.service.zkpurifypurchase;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypurchase.PurifyPurchaseDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 采购记录 Service 接口
 *
 * @author admin2
 */
public interface PurifyPurchaseService {

    /**
     * 创建采购记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyPurchase(@Valid PurifyPurchaseSaveReqVO createReqVO);

    /**
     * 更新采购记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyPurchase(@Valid PurifyPurchaseSaveReqVO updateReqVO);

    /**
     * 删除采购记录
     *
     * @param id 编号
     */
    void deletePurifyPurchase(Long id);

    /**
    * 批量删除采购记录
    *
    * @param ids 编号
    */
    void deletePurifyPurchaseListByIds(List<Long> ids);

    /**
     * 获得采购记录
     *
     * @param id 编号
     * @return 采购记录
     */
    PurifyPurchaseDO getPurifyPurchase(Long id);

    /**
     * 获得采购记录分页
     *
     * @param pageReqVO 分页查询
     * @return 采购记录分页
     */
    PageResult<PurifyPurchaseDO> getPurifyPurchasePage(PurifyPurchasePageReqVO pageReqVO);

}