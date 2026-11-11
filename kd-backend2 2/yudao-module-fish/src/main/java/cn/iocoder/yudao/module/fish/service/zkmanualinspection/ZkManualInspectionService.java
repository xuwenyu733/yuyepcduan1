package cn.iocoder.yudao.module.fish.service.zkmanualinspection;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmanualinspection.ZkManualInspectionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 人工巡检 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkManualInspectionService {

    /**
     * 创建人工巡检
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkManualInspection(@Valid ZkManualInspectionCreateReqVO createReqVO);

    /**
     * 更新人工巡检
     *
     * @param updateReqVO 更新信息
     */
    void updateZkManualInspection(@Valid ZkManualInspectionUpdateReqVO updateReqVO);

    /**
     * 删除人工巡检
     *
     * @param id 编号
     */
    void deleteZkManualInspection(Long id);

    /**
     * 获得人工巡检
     *
     * @param id 编号
     * @return 人工巡检
     */
    ZkManualInspectionDO getZkManualInspection(Long id);

    /**
     * 获得人工巡检列表
     *
     * @param ids 编号
     * @return 人工巡检列表
     */
    List<ZkManualInspectionDO> getZkManualInspectionList(Collection<Long> ids);

    /**
     * 获得人工巡检分页
     *
     * @param pageReqVO 分页查询
     * @return 人工巡检分页
     */
    PageResult<ZkManualInspectionDO> getZkManualInspectionPage(ZkManualInspectionPageReqVO pageReqVO);

    /**
     * 获得人工巡检列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 人工巡检列表
     */
    List<ZkManualInspectionDO> getZkManualInspectionList(ZkManualInspectionExportReqVO exportReqVO);

}
