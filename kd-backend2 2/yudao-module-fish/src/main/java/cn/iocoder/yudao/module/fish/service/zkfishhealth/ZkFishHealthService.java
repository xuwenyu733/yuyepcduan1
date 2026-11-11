package cn.iocoder.yudao.module.fish.service.zkfishhealth;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishhealth.ZkFishHealthDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 鱼类健康评估 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkFishHealthService {

    /**
     * 创建鱼类健康评估
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkFishHealth(@Valid ZkFishHealthCreateReqVO createReqVO);

    /**
     * 更新鱼类健康评估
     *
     * @param updateReqVO 更新信息
     */
    void updateZkFishHealth(@Valid ZkFishHealthUpdateReqVO updateReqVO);

    /**
     * 删除鱼类健康评估
     *
     * @param id 编号
     */
    void deleteZkFishHealth(Long id);

    /**
     * 获得鱼类健康评估
     *
     * @param id 编号
     * @return 鱼类健康评估
     */
    ZkFishHealthDO getZkFishHealth(Long id);

    /**
     * 获得鱼类健康评估列表
     *
     * @param ids 编号
     * @return 鱼类健康评估列表
     */
    List<ZkFishHealthDO> getZkFishHealthList(Collection<Long> ids);

    /**
     * 获得鱼类健康评估分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼类健康评估分页
     */
    PageResult<ZkFishHealthDO> getZkFishHealthPage(ZkFishHealthPageReqVO pageReqVO);

    /**
     * 获得鱼类健康评估列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 鱼类健康评估列表
     */
    List<ZkFishHealthDO> getZkFishHealthList(ZkFishHealthExportReqVO exportReqVO);

}
