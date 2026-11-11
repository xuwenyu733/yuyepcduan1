package cn.iocoder.yudao.module.fish.service.zkexperimentalpool;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool.ZkExperimentalPoolDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 实验鱼池 Service 接口
 *
 * @author admin2
 */
public interface ZkExperimentalPoolService {

    /**
     * 创建实验鱼池
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkExperimentalPool(@Valid ZkExperimentalPoolSaveReqVO createReqVO);

    /**
     * 更新实验鱼池
     *
     * @param updateReqVO 更新信息
     */
    void updateZkExperimentalPool(@Valid ZkExperimentalPoolSaveReqVO updateReqVO);

    /**
     * 删除实验鱼池
     *
     * @param id 编号
     */
    void deleteZkExperimentalPool(Long id);

    /**
    * 批量删除实验鱼池
    *
    * @param ids 编号
    */
    void deleteZkExperimentalPoolListByIds(List<Long> ids);

    /**
     * 获得实验鱼池
     *
     * @param id 编号
     * @return 实验鱼池
     */
    ZkExperimentalPoolDO getZkExperimentalPool(Long id);

    /**
     * 获得实验鱼池分页
     *
     * @param pageReqVO 分页查询
     * @return 实验鱼池分页
     */
    PageResult<ZkExperimentalPoolDO> getZkExperimentalPoolPage(ZkExperimentalPoolPageReqVO pageReqVO);

}