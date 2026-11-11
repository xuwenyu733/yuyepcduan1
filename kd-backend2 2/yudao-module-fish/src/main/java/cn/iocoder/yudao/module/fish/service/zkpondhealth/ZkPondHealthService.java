package cn.iocoder.yudao.module.fish.service.zkpondhealth;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 鱼池健康 Service 接口
 *
 * @author admin2
 */
public interface ZkPondHealthService {

    /**
     * 创建鱼池健康
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPondHealth(@Valid ZkPondHealthSaveReqVO createReqVO);

    /**
     * 更新鱼池健康
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondHealth(@Valid ZkPondHealthSaveReqVO updateReqVO);

    /**
     * 删除鱼池健康
     *
     * @param id 编号
     */
    void deleteZkPondHealth(Long id);

    /**
    * 批量删除鱼池健康
    *
    * @param ids 编号
    */
    void deleteZkPondHealthListByIds(List<Long> ids);

    /**
     * 获得鱼池健康
     *
     * @param id 编号
     * @return 鱼池健康
     */
    ZkPondHealthDO getZkPondHealth(Long id);

    /**
     * 获得鱼池健康分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼池健康分页
     */
    PageResult<ZkPondHealthDO> getZkPondHealthPage(ZkPondHealthPageReqVO pageReqVO);

    /**
     * 获取健康状态分布
     *
     * @return 状态分布统计
     */
    Map<String, Object> getHealthStatusDistribution();

    /**
     * 获取各鱼池最新健康评分
     *
     * @return 鱼池健康列表
     */
    List<ZkPondHealthDO> getLatestPondHealthList();

    void customSave(ZkPondHealthDO res);
}