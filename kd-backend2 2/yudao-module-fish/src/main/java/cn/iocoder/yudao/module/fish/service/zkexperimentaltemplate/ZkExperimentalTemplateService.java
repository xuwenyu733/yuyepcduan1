package cn.iocoder.yudao.module.fish.service.zkexperimentaltemplate;

import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaltemplate.ZkExperimentalTemplateDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import java.util.List;

public interface ZkExperimentalTemplateService {

    Long createZkExperimentalTemplate(@Valid ZkExperimentalTemplateSaveReqVO createReqVO);

    void updateZkExperimentalTemplate(@Valid ZkExperimentalTemplateSaveReqVO updateReqVO);

    void deleteZkExperimentalTemplate(Long id);

    void deleteZkExperimentalTemplateListByIds(List<Long> ids);

    ZkExperimentalTemplateDO getZkExperimentalTemplate(Long id);

    PageResult<ZkExperimentalTemplateDO> getZkExperimentalTemplatePage(ZkExperimentalTemplatePageReqVO pageReqVO);

    Long copyZkExperimentalTemplate(Long id);

    List<ZkExperimentalTemplateTypeRespVO> getTemplateTypes();

}
