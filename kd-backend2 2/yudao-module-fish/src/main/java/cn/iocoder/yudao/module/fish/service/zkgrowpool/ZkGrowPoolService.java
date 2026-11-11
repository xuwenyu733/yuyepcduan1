package cn.iocoder.yudao.module.fish.service.zkgrowpool;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowpool.ZkGrowPoolDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 生长模型鱼池 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowPoolService {

    /**
     * 创建生长模型鱼池
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowPool(@Valid ZkGrowPoolSaveReqVO createReqVO);

    /**
     * 更新生长模型鱼池
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowPool(@Valid ZkGrowPoolSaveReqVO updateReqVO);

    /**
     * 删除生长模型鱼池
     *
     * @param id 编号
     */
    void deleteZkGrowPool(Long id);

    /**
    * 批量删除生长模型鱼池
    *
    * @param ids 编号
    */
    void deleteZkGrowPoolListByIds(List<Long> ids);

    /**
     * 获得生长模型鱼池
     *
     * @param id 编号
     * @return 生长模型鱼池
     */
    ZkGrowPoolDO getZkGrowPool(Long id);

    /**
     * 获得生长模型鱼池分页
     *
     * @param pageReqVO 分页查询
     * @return 生长模型鱼池分页
     */
    PageResult<ZkGrowPoolDO> getZkGrowPoolPage(ZkGrowPoolPageReqVO pageReqVO);

}