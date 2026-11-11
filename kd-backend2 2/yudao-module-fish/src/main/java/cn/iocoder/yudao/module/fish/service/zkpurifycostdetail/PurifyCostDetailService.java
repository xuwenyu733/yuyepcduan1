package cn.iocoder.yudao.module.fish.service.zkpurifycostdetail;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifycostdetail.PurifyCostDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 成本明细记录 Service 接口
 *
 * @author admin2
 */
public interface PurifyCostDetailService {

    /**
     * 创建成本明细记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyCostDetail(@Valid PurifyCostDetailSaveReqVO createReqVO);

    /**
     * 更新成本明细记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyCostDetail(@Valid PurifyCostDetailSaveReqVO updateReqVO);

    /**
     * 删除成本明细记录
     *
     * @param id 编号
     */
    void deletePurifyCostDetail(Long id);

    /**
    * 批量删除成本明细记录
    *
    * @param ids 编号
    */
    void deletePurifyCostDetailListByIds(List<Long> ids);

    /**
     * 获得成本明细记录
     *
     * @param id 编号
     * @return 成本明细记录
     */
    PurifyCostDetailDO getPurifyCostDetail(Long id);

    /**
     * 获得成本明细记录分页
     *
     * @param pageReqVO 分页查询
     * @return 成本明细记录分页
     */
    PageResult<PurifyCostDetailDO> getPurifyCostDetailPage(PurifyCostDetailPageReqVO pageReqVO);

}