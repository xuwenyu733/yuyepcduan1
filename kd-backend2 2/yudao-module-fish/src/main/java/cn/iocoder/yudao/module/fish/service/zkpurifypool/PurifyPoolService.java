package cn.iocoder.yudao.module.fish.service.zkpurifypool;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypool.PurifyPoolDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 鱼池管理 Service 接口
 *
 * @author admin2
 */
public interface PurifyPoolService {

    /**
     * 创建鱼池管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyPool(@Valid PurifyPoolSaveReqVO createReqVO);

    /**
     * 更新鱼池管理
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyPool(@Valid PurifyPoolSaveReqVO updateReqVO);

    /**
     * 删除鱼池管理
     *
     * @param id 编号
     */
    void deletePurifyPool(Long id);

    /**
    * 批量删除鱼池管理
    *
    * @param ids 编号
    */
    void deletePurifyPoolListByIds(List<Long> ids);

    /**
     * 获得鱼池管理
     *
     * @param id 编号
     * @return 鱼池管理
     */
    PurifyPoolDO getPurifyPool(Long id);

    /**
     * 获得鱼池管理分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼池管理分页
     */
    PageResult<PurifyPoolDO> getPurifyPoolPage(PurifyPoolPageReqVO pageReqVO);

}