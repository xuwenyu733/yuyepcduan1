package cn.iocoder.yudao.module.fish.service.zkgrowthmodel;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowthmodel.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowthmodel.ZkGrowthModelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 生长模型 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkGrowthModelService {

    /**
     * 创建生长模型
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowthModel(@Valid ZkGrowthModelCreateReqVO createReqVO);

    /**
     * 更新生长模型
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowthModel(@Valid ZkGrowthModelUpdateReqVO updateReqVO);

    /**
     * 删除生长模型
     *
     * @param id 编号
     */
    void deleteZkGrowthModel(Long id);

    /**
     * 获得生长模型
     *
     * @param id 编号
     * @return 生长模型
     */
    ZkGrowthModelDO getZkGrowthModel(Long id);

    /**
     * 获得生长模型列表
     *
     * @param ids 编号
     * @return 生长模型列表
     */
    List<ZkGrowthModelDO> getZkGrowthModelList(Collection<Long> ids);

    /**
     * 获得生长模型分页
     *
     * @param pageReqVO 分页查询
     * @return 生长模型分页
     */
    PageResult<ZkGrowthModelDO> getZkGrowthModelPage(ZkGrowthModelPageReqVO pageReqVO);

    /**
     * 获得生长模型列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 生长模型列表
     */
    List<ZkGrowthModelDO> getZkGrowthModelList(ZkGrowthModelExportReqVO exportReqVO);

}
