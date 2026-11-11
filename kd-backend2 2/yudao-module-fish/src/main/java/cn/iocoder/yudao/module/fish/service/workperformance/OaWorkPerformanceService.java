package cn.iocoder.yudao.module.fish.service.workperformance;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.workperformance.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.workperformance.OaWorkPerformanceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * OA工作绩效评分 Service 接口
 *
 * @author 系统管理员
 */
public interface OaWorkPerformanceService {

    /**
     * 创建OA工作绩效评分
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOaWorkPerformance(@Valid OaWorkPerformanceCreateReqVO createReqVO);

    /**
     * 更新OA工作绩效评分
     *
     * @param updateReqVO 更新信息
     */
    void updateOaWorkPerformance(@Valid OaWorkPerformanceUpdateReqVO updateReqVO);

    /**
     * 删除OA工作绩效评分
     *
     * @param id 编号
     */
    void deleteOaWorkPerformance(Long id);

    /**
     * 获得OA工作绩效评分
     *
     * @param id 编号
     * @return OA工作绩效评分
     */
    OaWorkPerformanceDO getOaWorkPerformance(Long id);

    /**
     * 获得OA工作绩效评分分页（带权限控制）
     *
     * @param pageReqVO 分页查询
     * @return OA工作绩效评分分页
     */
    PageResult<OaWorkPerformanceDO> getOaWorkPerformancePage(OaWorkPerformancePageReqVO pageReqVO);

    /**
     * 获得OA工作绩效评分列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return OA工作绩效评分列表
     */
    List<OaWorkPerformanceDO> getOaWorkPerformanceList(OaWorkPerformanceExportReqVO exportReqVO);

    /**
     * 获取可评分人员列表（仅部门成员）
     *
     * @return 可评分人员列表
     */
    List<Map<String, Object>> getRateableUsers();

}



