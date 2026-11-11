package cn.iocoder.yudao.module.fish.service.zkbiologicalasset;

import java.util.*;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.ZkBiologicalAssetDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 生物资产评估 Service 接口
 *
 * @author 系统管理员
 */

public interface ZkBiologicalAssetService {

    /**
     * 创建生物资产评估
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkBiologicalAsset(@Valid ZkBiologicalAssetCreateReqVO createReqVO);

    /**
     * 更新生物资产评估
     *
     * @param updateReqVO 更新信息
     */
    void updateZkBiologicalAsset(@Valid ZkBiologicalAssetUpdateReqVO updateReqVO);

    /**
     * 根据存塘信息，更新生物资产评估
     *
     * @param zkPondDO 更新信息
     */
    void refreshZkBiologicalAssetByZkPond(ZkPondDO zkPondDO);
    /**
     * 删除生物资产评估
     *
     * @param id 编号
     */
    void deleteZkBiologicalAsset(Long id);

    /**
     * 获得生物资产评估
     *
     * @param id 编号
     * @return 生物资产评估
     */
    ZkBiologicalAssetDO getZkBiologicalAsset(Long id);

    /**
     * 获得生物资产评估列表
     *
     * @param ids 编号
     * @return 生物资产评估列表
     */
    List<ZkBiologicalAssetDO> getZkBiologicalAssetList(Collection<Long> ids);

    /**
     * 获得生物资产评估分页
     *
     * @param pageReqVO 分页查询
     * @return 生物资产评估分页
     */
    PageResult<ZkBiologicalAssetDO> getZkBiologicalAssetPage(ZkBiologicalAssetPageReqVO pageReqVO);

    /**
     * 获得生物资产评估列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 生物资产评估列表
     */
    List<ZkBiologicalAssetDO> getZkBiologicalAssetList(ZkBiologicalAssetExportReqVO exportReqVO);

    /**
     * 获得仪表盘数据
     * @param startDate 统计周期起（yyyy-MM-dd，可空）
     * @param endDate 统计周期止 / 盘点日（yyyy-MM-dd，可空则取今天）
     * @return 仪表盘数据
     */
    ZkBiologicalAssetDashboardRespVO getDashboardData(String startDate, String endDate);
}
