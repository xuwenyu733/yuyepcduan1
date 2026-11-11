package cn.iocoder.yudao.module.fish.service.zkinspectionreport;

import java.util.*;
import jakarta.validation.*;

import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkinspectionreport.ZkInspectionReportDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 巡检智能分析报告 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkInspectionReportService {

    /**
     * 创建巡检智能分析报告
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkInspectionReport(@Valid ZkInspectionReportCreateReqVO createReqVO);

    /**
     * 更新巡检智能分析报告
     *
     * @param updateReqVO 更新信息
     */
    void updateZkInspectionReport(@Valid ZkInspectionReportUpdateReqVO updateReqVO);

    /**
     * 删除巡检智能分析报告
     *
     * @param id 编号
     */
    void deleteZkInspectionReport(Long id);

    /**
     * 获得巡检智能分析报告
     *
     * @param id 编号
     * @return 巡检智能分析报告
     */
    ZkInspectionReportDO getZkInspectionReport(Long id);

    /**
     * 获得巡检智能分析报告列表
     *
     * @param ids 编号
     * @return 巡检智能分析报告列表
     */
    List<ZkInspectionReportDO> getZkInspectionReportList(Collection<Long> ids);

    /**
     * 获得巡检智能分析报告分页
     *
     * @param pageReqVO 分页查询
     * @return 巡检智能分析报告分页
     */
    PageResult<ZkInspectionReportDO> getZkInspectionReportPage(ZkInspectionReportPageReqVO pageReqVO);

    /**
     * 获得巡检智能分析报告列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 巡检智能分析报告列表
     */
    List<ZkInspectionReportDO> getZkInspectionReportList(ZkInspectionReportExportReqVO exportReqVO);

    Integer receiveTaskDetailReport(ReceiveOperateVo vo);
    ZkInspectionReportDO selectByTaskDetailId(Long taskDetailId);
}
