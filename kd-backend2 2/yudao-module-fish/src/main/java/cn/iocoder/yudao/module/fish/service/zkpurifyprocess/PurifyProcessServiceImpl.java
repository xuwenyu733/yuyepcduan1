package cn.iocoder.yudao.module.fish.service.zkpurifyprocess;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyprocess.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyprocess.PurifyProcessMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 优化流程记录 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyProcessServiceImpl implements PurifyProcessService {

    @Resource
    private PurifyProcessMapper purifyProcessMapper;

    @Override
    public Long createPurifyProcess(PurifyProcessSaveReqVO createReqVO) {
        // 插入
        PurifyProcessDO purifyProcess = BeanUtils.toBean(createReqVO, PurifyProcessDO.class);
        purifyProcessMapper.insert(purifyProcess);

        // 返回
        return purifyProcess.getId();
    }

    @Override
    public void updatePurifyProcess(PurifyProcessSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyProcessExists(updateReqVO.getId());
        // 更新
        PurifyProcessDO updateObj = BeanUtils.toBean(updateReqVO, PurifyProcessDO.class);
        purifyProcessMapper.updateById(updateObj);
    }

    @Override
    public void deletePurifyProcess(Long id) {
        // 校验存在
        validatePurifyProcessExists(id);
        // 删除
        purifyProcessMapper.deleteById(id);
    }

    @Override
        public void deletePurifyProcessListByIds(List<Long> ids) {
        // 删除
        purifyProcessMapper.deleteByIds(ids);
        }


    private void validatePurifyProcessExists(Long id) {
        if (purifyProcessMapper.selectById(id) == null) {
            throw exception(PURIFY_PROCESS_NOT_EXISTS);
        }
    }

    @Override
    public PurifyProcessDO getPurifyProcess(Long id) {
        return purifyProcessMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyProcessDO> getPurifyProcessPage(PurifyProcessPageReqVO pageReqVO) {
        return purifyProcessMapper.selectPage(pageReqVO);
    }

}