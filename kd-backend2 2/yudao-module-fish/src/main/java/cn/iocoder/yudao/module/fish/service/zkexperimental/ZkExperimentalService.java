package cn.iocoder.yudao.module.fish.service.zkexperimental;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimental.ZkExperimentalDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 实验 Service 接口
 *
 * @author admin2
 */
public interface ZkExperimentalService {

    /**
     * 创建实验
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkExperimental(@Valid ZkExperimentalSaveReqVO createReqVO);

    /**
     * 更新实验
     *
     * @param updateReqVO 更新信息
     */
    void updateZkExperimental(@Valid ZkExperimentalSaveReqVO updateReqVO);

    /**
     * 删除实验
     *
     * @param id 编号
     */
    void deleteZkExperimental(Long id);

    /**
    * 批量删除实验
    *
    * @param ids 编号
    */
    void deleteZkExperimentalListByIds(List<Long> ids);

    /**
     * 获得实验
     *
     * @param id 编号
     * @return 实验
     */
    ZkExperimentalDO getZkExperimental(Long id);

    /**
     * 获得实验分页
     *
     * @param pageReqVO 分页查询
     * @return 实验分页
     */
    PageResult<ZkExperimentalDO> getZkExperimentalPage(ZkExperimentalPageReqVO pageReqVO);

    /**
     * 实验数据分析汇总
     */
    ZkExperimentalAnalysisSummaryRespVO getAnalysisSummary(Long experimentId, String startDate, String endDate);

}