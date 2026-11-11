package cn.iocoder.yudao.module.fish.service.zkpurifypurchase;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo.PurifyPurchasePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo.PurifyPurchaseSaveReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypurchase.PurifyPurchaseDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifypurchase.PurifyPurchaseMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 采购记录 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyPurchaseServiceImpl implements PurifyPurchaseService {

    @Resource
    private PurifyPurchaseMapper purifyPurchaseMapper;

    @Override
    public Long createPurifyPurchase(PurifyPurchaseSaveReqVO createReqVO) {
        // 插入
        PurifyPurchaseDO purifyPurchase = BeanUtils.toBean(createReqVO, PurifyPurchaseDO.class);
        
        // 自动生成采购单号: PUR + yyyyMMddHHmmss
        if (StrUtil.isEmpty(purifyPurchase.getPurchaseOrderNo())) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            purifyPurchase.setPurchaseOrderNo("PUR" + timestamp);
        }
        
        purifyPurchaseMapper.insert(purifyPurchase);

        // 返回
        return purifyPurchase.getId();
    }

    @Override
    public void updatePurifyPurchase(PurifyPurchaseSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyPurchaseExists(updateReqVO.getId());
        // 更新
        PurifyPurchaseDO updateObj = BeanUtils.toBean(updateReqVO, PurifyPurchaseDO.class);
        purifyPurchaseMapper.updateById(updateObj);
    }

    @Override
    public void deletePurifyPurchase(Long id) {
        // 校验存在
        validatePurifyPurchaseExists(id);
        // 删除
        purifyPurchaseMapper.deleteById(id);
    }

    @Override
        public void deletePurifyPurchaseListByIds(List<Long> ids) {
        // 删除
        purifyPurchaseMapper.deleteByIds(ids);
        }


    private void validatePurifyPurchaseExists(Long id) {
        if (purifyPurchaseMapper.selectById(id) == null) {
            throw exception(PURIFY_PURCHASE_NOT_EXISTS);
        }
    }

    @Override
    public PurifyPurchaseDO getPurifyPurchase(Long id) {
        return purifyPurchaseMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyPurchaseDO> getPurifyPurchasePage(PurifyPurchasePageReqVO pageReqVO) {
        return purifyPurchaseMapper.selectPage(pageReqVO);
    }

}