package cn.iocoder.yudao.module.fish.service.zkpurifycostdetail;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifycostdetail.PurifyCostDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifycostdetail.PurifyCostDetailMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 成本明细记录 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyCostDetailServiceImpl implements PurifyCostDetailService {

    @Resource
    private PurifyCostDetailMapper purifyCostDetailMapper;

    @Override
    public Long createPurifyCostDetail(PurifyCostDetailSaveReqVO createReqVO) {
        // 插入
        PurifyCostDetailDO purifyCostDetail = BeanUtils.toBean(createReqVO, PurifyCostDetailDO.class);
        purifyCostDetailMapper.insert(purifyCostDetail);

        // 返回
        return purifyCostDetail.getId();
    }

    @Override
    public void updatePurifyCostDetail(PurifyCostDetailSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyCostDetailExists(updateReqVO.getId());
        // 更新
        PurifyCostDetailDO updateObj = BeanUtils.toBean(updateReqVO, PurifyCostDetailDO.class);
        purifyCostDetailMapper.updateById(updateObj);
    }

    @Override
    public void deletePurifyCostDetail(Long id) {
        // 校验存在
        validatePurifyCostDetailExists(id);
        // 删除
        purifyCostDetailMapper.deleteById(id);
    }

    @Override
        public void deletePurifyCostDetailListByIds(List<Long> ids) {
        // 删除
        purifyCostDetailMapper.deleteByIds(ids);
        }


    private void validatePurifyCostDetailExists(Long id) {
        if (purifyCostDetailMapper.selectById(id) == null) {
            throw exception(PURIFY_COST_DETAIL_NOT_EXISTS);
        }
    }

    @Override
    public PurifyCostDetailDO getPurifyCostDetail(Long id) {
        return purifyCostDetailMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyCostDetailDO> getPurifyCostDetailPage(PurifyCostDetailPageReqVO pageReqVO) {
        return purifyCostDetailMapper.selectPage(pageReqVO);
    }

}