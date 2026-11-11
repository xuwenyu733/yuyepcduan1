/**
 *
 */
package cn.iocoder.yudao.module.bpm.service.other;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.controller.admin.model.vo.*;
import jakarta.validation.Valid;
import org.flowable.bpmn.model.BpmnModel;
import org.springframework.transaction.annotation.Transactional;


/**
 * 流程模型设计
 * @author hushouquan
 *
 */
public interface WfModelService {
    /**
     * 获得流程模型分页
     *
     * @param pageVO 分页查询
     * @return 流程模型分页
     */
    PageResult<ModelPageItemRespVO> getModelPage(ModelPageReqVO pageVO);

}
