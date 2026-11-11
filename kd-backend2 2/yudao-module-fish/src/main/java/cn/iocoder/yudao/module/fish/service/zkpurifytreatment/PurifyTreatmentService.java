package cn.iocoder.yudao.module.fish.service.zkpurifytreatment;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifytreatment.PurifyTreatmentDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 处理措施记录 Service 接口
 *
 * @author admin2
 */
public interface PurifyTreatmentService {

    /**
     * 创建处理措施记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyTreatment(@Valid PurifyTreatmentSaveReqVO createReqVO);

    /**
     * 更新处理措施记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyTreatment(@Valid PurifyTreatmentSaveReqVO updateReqVO);

    /**
     * 删除处理措施记录
     *
     * @param id 编号
     */
    void deletePurifyTreatment(Long id);

    /**
    * 批量删除处理措施记录
    *
    * @param ids 编号
    */
    void deletePurifyTreatmentListByIds(List<Long> ids);

    /**
     * 获得处理措施记录
     *
     * @param id 编号
     * @return 处理措施记录
     */
    PurifyTreatmentDO getPurifyTreatment(Long id);

    /**
     * 获得处理措施记录分页
     *
     * @param pageReqVO 分页查询
     * @return 处理措施记录分页
     */
    PageResult<PurifyTreatmentDO> getPurifyTreatmentPage(PurifyTreatmentPageReqVO pageReqVO);

}