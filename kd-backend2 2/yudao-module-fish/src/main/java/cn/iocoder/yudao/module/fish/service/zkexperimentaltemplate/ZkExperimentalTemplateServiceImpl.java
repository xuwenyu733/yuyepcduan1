package cn.iocoder.yudao.module.fish.service.zkexperimentaltemplate;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaltemplate.ZkExperimentalTemplateDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentaltemplate.ZkExperimentalTemplateMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

@Service
@Validated
public class ZkExperimentalTemplateServiceImpl implements ZkExperimentalTemplateService {

    private static final List<ZkExperimentalTemplateTypeRespVO> DEFAULT_TYPES = Arrays.asList(
            new ZkExperimentalTemplateTypeRespVO("feed", "饲料试验"),
            new ZkExperimentalTemplateTypeRespVO("water", "水质试验"),
            new ZkExperimentalTemplateTypeRespVO("medicine", "用药试验"),
            new ZkExperimentalTemplateTypeRespVO("breed", "育种试验"),
            new ZkExperimentalTemplateTypeRespVO("other", "其他")
    );

    @Resource
    private ZkExperimentalTemplateMapper zkExperimentalTemplateMapper;

    @Override
    public Long createZkExperimentalTemplate(ZkExperimentalTemplateSaveReqVO createReqVO) {
        ZkExperimentalTemplateDO template = BeanUtils.toBean(createReqVO, ZkExperimentalTemplateDO.class);
        if (template.getStatus() == null) {
            template.setStatus(0);
        }
        if (StrUtil.isBlank(template.getTemplateCode())) {
            template.setTemplateCode("TPL-" + System.currentTimeMillis());
        }
        zkExperimentalTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updateZkExperimentalTemplate(ZkExperimentalTemplateSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        ZkExperimentalTemplateDO updateObj = BeanUtils.toBean(updateReqVO, ZkExperimentalTemplateDO.class);
        zkExperimentalTemplateMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkExperimentalTemplate(Long id) {
        validateExists(id);
        zkExperimentalTemplateMapper.deleteById(id);
    }

    @Override
    public void deleteZkExperimentalTemplateListByIds(List<Long> ids) {
        zkExperimentalTemplateMapper.deleteByIds(ids);
    }

    @Override
    public ZkExperimentalTemplateDO getZkExperimentalTemplate(Long id) {
        return zkExperimentalTemplateMapper.selectById(id);
    }

    @Override
    public PageResult<ZkExperimentalTemplateDO> getZkExperimentalTemplatePage(ZkExperimentalTemplatePageReqVO pageReqVO) {
        return zkExperimentalTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public Long copyZkExperimentalTemplate(Long id) {
        ZkExperimentalTemplateDO source = zkExperimentalTemplateMapper.selectById(id);
        if (source == null) {
            throw exception(ZK_EXPERIMENTAL_TEMPLATE_NOT_EXISTS);
        }
        ZkExperimentalTemplateDO copy = BeanUtils.toBean(source, ZkExperimentalTemplateDO.class);
        copy.setId(null);
        copy.setTemplateName(source.getTemplateName() + " (副本)");
        copy.setTemplateCode("TPL-" + System.currentTimeMillis());
        zkExperimentalTemplateMapper.insert(copy);
        return copy.getId();
    }

    @Override
    public List<ZkExperimentalTemplateTypeRespVO> getTemplateTypes() {
        return DEFAULT_TYPES;
    }

    private void validateExists(Long id) {
        if (zkExperimentalTemplateMapper.selectById(id) == null) {
            throw exception(ZK_EXPERIMENTAL_TEMPLATE_NOT_EXISTS);
        }
    }

}
