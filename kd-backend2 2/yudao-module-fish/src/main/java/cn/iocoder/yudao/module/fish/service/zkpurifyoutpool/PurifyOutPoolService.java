package cn.iocoder.yudao.module.fish.service.zkpurifyoutpool;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool.PurifyOutPoolDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 出池登记 Service 接口
 *
 * @author admin2
 */
public interface PurifyOutPoolService {

    /**
     * 创建出池登记
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyOutPool(@Valid PurifyOutPoolSaveReqVO createReqVO);

    /**
     * 更新出池登记
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyOutPool(@Valid PurifyOutPoolSaveReqVO updateReqVO);

    /**
     * 删除出池登记
     *
     * @param id 编号
     */
    void deletePurifyOutPool(Long id);

    /**
    * 批量删除出池登记
    *
    * @param ids 编号
    */
    void deletePurifyOutPoolListByIds(List<Long> ids);

    /**
     * 获得出池登记
     *
     * @param id 编号
     * @return 出池登记
     */
    PurifyOutPoolDO getPurifyOutPool(Long id);

    /**
     * 获得出池登记分页
     *
     * @param pageReqVO 分页查询
     * @return 出池登记分页
     */
    PageResult<PurifyOutPoolDO> getPurifyOutPoolPage(PurifyOutPoolPageReqVO pageReqVO);

}