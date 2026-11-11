package cn.iocoder.yudao.module.fish.service.zkintelligentdiagnosis;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 智能诊断 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkIntelligentDiagnosisService {

    /**
     * 创建智能诊断
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkIntelligentDiagnosis(@Valid ZkIntelligentDiagnosisCreateReqVO createReqVO);

    /**
     * 更新智能诊断
     *
     * @param updateReqVO 更新信息
     */
    void updateZkIntelligentDiagnosis(@Valid ZkIntelligentDiagnosisUpdateReqVO updateReqVO);

    /**
     * 删除智能诊断
     *
     * @param id 编号
     */
    void deleteZkIntelligentDiagnosis(Long id);

    /**
     * 获得智能诊断
     *
     * @param id 编号
     * @return 智能诊断
     */
    ZkIntelligentDiagnosisDO getZkIntelligentDiagnosis(Long id);

    /**
     * 获得智能诊断列表
     *
     * @param ids 编号
     * @return 智能诊断列表
     */
    List<ZkIntelligentDiagnosisDO> getZkIntelligentDiagnosisList(Collection<Long> ids);

    /**
     * 获得智能诊断分页
     *
     * @param pageReqVO 分页查询
     * @return 智能诊断分页
     */
    PageResult<ZkIntelligentDiagnosisDO> getZkIntelligentDiagnosisPage(ZkIntelligentDiagnosisPageReqVO pageReqVO);

    /**
     * 获得智能诊断列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 智能诊断列表
     */
    List<ZkIntelligentDiagnosisDO> getZkIntelligentDiagnosisList(ZkIntelligentDiagnosisExportReqVO exportReqVO);


}
