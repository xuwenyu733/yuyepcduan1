/**
 *
 */
package cn.iocoder.yudao.module.bpm.service.other;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.controller.admin.model.vo.ModelPageItemRespVO;
import cn.iocoder.yudao.module.bpm.controller.admin.model.vo.ModelPageReqVO;
import jakarta.annotation.Resource;
import org.flowable.engine.RepositoryService;
import org.springframework.stereotype.Service;

/**
 * @author hushouquan
 *
 */
@Service
public class WfModelServiceImpl implements WfModelService {

    @Resource
    private RepositoryService repositoryService;

    @Override
    public PageResult<ModelPageItemRespVO> getModelPage(ModelPageReqVO pageVO) {
        return null;
    }

}
