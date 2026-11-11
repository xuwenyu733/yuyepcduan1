package cn.iocoder.yudao.module.fish.service.zkpurifytreatment;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifytreatment.PurifyTreatmentDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifytreatment.PurifyTreatmentMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 处理措施记录 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyTreatmentServiceImpl implements PurifyTreatmentService {

    @Resource
    private PurifyTreatmentMapper purifyTreatmentMapper;

    @Override
    public Long createPurifyTreatment(PurifyTreatmentSaveReqVO createReqVO) {
        // 插入
        PurifyTreatmentDO purifyTreatment = BeanUtils.toBean(createReqVO, PurifyTreatmentDO.class);
        purifyTreatmentMapper.insert(purifyTreatment);

        // 返回
        return purifyTreatment.getId();
    }

    @Override
    public void updatePurifyTreatment(PurifyTreatmentSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyTreatmentExists(updateReqVO.getId());
        // 更新
        PurifyTreatmentDO updateObj = BeanUtils.toBean(updateReqVO, PurifyTreatmentDO.class);
        purifyTreatmentMapper.updateById(updateObj);
    }

    @Override
    public void deletePurifyTreatment(Long id) {
        // 校验存在
        validatePurifyTreatmentExists(id);
        // 删除
        purifyTreatmentMapper.deleteById(id);
    }

    @Override
        public void deletePurifyTreatmentListByIds(List<Long> ids) {
        // 删除
        purifyTreatmentMapper.deleteByIds(ids);
        }


    private void validatePurifyTreatmentExists(Long id) {
        if (purifyTreatmentMapper.selectById(id) == null) {
            throw exception(PURIFY_TREATMENT_NOT_EXISTS);
        }
    }

    @Override
    public PurifyTreatmentDO getPurifyTreatment(Long id) {
        return purifyTreatmentMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyTreatmentDO> getPurifyTreatmentPage(PurifyTreatmentPageReqVO pageReqVO) {
        return purifyTreatmentMapper.selectPage(pageReqVO);
    }

}