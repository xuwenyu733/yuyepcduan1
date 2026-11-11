package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentaltemplate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaltemplate.ZkExperimentalTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo.*;

@Mapper
public interface ZkExperimentalTemplateMapper extends BaseMapperX<ZkExperimentalTemplateDO> {

    default PageResult<ZkExperimentalTemplateDO> selectPage(ZkExperimentalTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkExperimentalTemplateDO>()
                .likeIfPresent(ZkExperimentalTemplateDO::getTemplateName, reqVO.getTemplateName())
                .eqIfPresent(ZkExperimentalTemplateDO::getTemplateType, reqVO.getTemplateType())
                .likeIfPresent(ZkExperimentalTemplateDO::getTemplateCode, reqVO.getTemplateCode())
                .eqIfPresent(ZkExperimentalTemplateDO::getVariety, reqVO.getVariety())
                .eqIfPresent(ZkExperimentalTemplateDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkExperimentalTemplateDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkExperimentalTemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkExperimentalTemplateDO::getId));
    }

}
