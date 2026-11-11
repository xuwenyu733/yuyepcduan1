package cn.iocoder.yudao.module.erp.service.product;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ProductSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductCategoryDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductUnitDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.dal.mysql.product.ErpProductMapper;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 产品 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpProductServiceImpl implements ErpProductService {

    @Resource
    private ErpProductMapper productMapper;

    @Resource
    private ErpProductCategoryService productCategoryService;
    @Resource
    private ErpProductUnitService productUnitService;
    @Autowired
    @Lazy
    private ErpStockService erpStockService;

    @Override
    public Long createProduct(ProductSaveReqVO createReqVO) {
        // 校验条码重复
        validateBarCodeUnique(null, createReqVO.getBarCode());
        // 插入
        ErpProductDO product = BeanUtils.toBean(createReqVO, ErpProductDO.class);
        productMapper.insert(product.build_info());
        // 返回
        return product.getId();
    }

    @Override
    public void updateProduct(ProductSaveReqVO updateReqVO) {
        // 校验存在
        validateProductExists(updateReqVO.getId());
        // 校验条码重复
        validateBarCodeUnique(updateReqVO.getId(), updateReqVO.getBarCode());
        // 更新
        ErpProductDO updateObj = BeanUtils.toBean(updateReqVO, ErpProductDO.class);
        productMapper.updateById(updateObj.build_info());
    }

    private void validateBarCodeUnique(Long id, String barCode) {
        if (cn.hutool.core.util.StrUtil.isBlank(barCode)) {
            return;
        }
        ErpProductDO product = productMapper.selectByBarCode(id, barCode);
        if (product == null) {
            return;
        }
        // 如果 id 为空，说明是新增，重复了
        if (id == null) {
            throw exception(PRODUCT_BAR_CODE_DUPLICATE);
        }
        // 如果 id 不为空，说明是更新，如果 id 不同，说明重复了
        if (!product.getId().equals(id)) {
            throw exception(PRODUCT_BAR_CODE_DUPLICATE);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        // 校验存在
        validateProductExists(id);
        // 删除
        productMapper.deleteById(id);
    }

    @Override
    public List<ErpProductDO> validProductList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<ErpProductDO> list = productMapper.selectByIds(ids);
        Map<Long, ErpProductDO> productMap = convertMap(list, ErpProductDO::getId);
        for (Long id : ids) {
            ErpProductDO product = productMap.get(id);
            if (productMap.get(id) == null) {
                throw exception(PRODUCT_NOT_EXISTS);
            }
            if (CommonStatusEnum.isDisable(product.getStatus())) {
                throw exception(PRODUCT_NOT_ENABLE, product.getName());
            }
        }
        return list;
    }

    private void validateProductExists(Long id) {
        if (productMapper.selectById(id) == null) {
            throw exception(PRODUCT_NOT_EXISTS);
        }
    }

    @Override
    public ErpProductDO getProduct(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public List<ErpProductRespVO> getProductVOListByStatus(Integer status) {
        List<ErpProductDO> list = productMapper.selectListByStatus(status);
        return buildProductVOList(list);
    }

    @Override
    public List<ErpProductRespVO> getProductVOList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<ErpProductDO> list = productMapper.selectByIds(ids);
        return buildProductVOList(list);
    }

    @Override
    public PageResult<ErpProductRespVO> getProductVOPage(ErpProductPageReqVO pageReqVO) {
        PageResult<ErpProductDO> pageResult = productMapper.selectPage(pageReqVO);
        if(EasyListUtil.isNotEmpty(pageResult.getList())) {
            pageResult.getList().forEach(obj -> obj.build_info());
        }
        enhancerStockInfo(pageResult.getList());
        enhancer_info(pageResult.getList());
        return new PageResult<>(buildProductVOList(pageResult.getList()), pageResult.getTotal());
    }

    @DataPermission(enable = false)
    public void enhancerStockInfo(List<ErpProductDO> tList) {
        try {
            if(EasyListUtil.isEmpty(tList)) return;
            // 1. 获取 关联 值
            Set<Long> oIds = EasyListUtil.listToIds(tList, ErpProductDO::getId);
            // 2. 利用 关联值 查询
            QueryWrapper<ErpProductDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("es.product_id", oIds);
            List<Map<String, Object>> oList = productMapper.selectStockInfo(queryWrapper);
            // 3. 赋值
            CommonUtil.originList2Targets3(oList, "productId", tList, "id", (Map<String, Object> o, ErpProductDO t) -> {
                t.setStockInfo(o.get("stockInfo")+"");
                t.setWarehouseIds(o.get("warehouseIds")+"");
            });
        } catch (Exception e) {
            String subject = String.format("【产品填充仓库信息-异常】,middle,异常名:%s", e.getMessage());
            String content = String.format("具体数据：%s,具体异常：%s", JSON.toJSONString(tList), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
        }
    }

    public void enhancer_info(List<ErpProductDO> list) {
        try {
            // 1. 获取库存信息
            if(EasyListUtil.isEmpty(list)) return;
            Set<Long> idSet = list.stream().map(obj -> obj.getId()).collect(Collectors.toSet());
            List<ErpStockDO> stockList = erpStockService.getByIds(idSet);
            if(EasyListUtil.isEmpty(stockList)) return;
            // 2. 赋值 信息
            for(ErpProductDO item : list) {
                BigDecimal sum = BigDecimal.ZERO;
                boolean hit = false;
                for(ErpStockDO remote : stockList) {
                    if(Objects.equals(item.getId(), remote.getProductId())) {
                        hit = true;
                        sum = sum.add(remote.getCount() == null ? BigDecimal.ZERO : remote.getCount());
                    }
                }
                if(!hit) continue;
                item.setStockCount(sum);
            }
        } catch (Exception e) {
            String subject = String.format("【产品填充仓库信息-异常】,middle,异常名:%s", e.getMessage());
            String content = String.format("具体数据：%s,具体异常：%s", JSON.toJSONString(list), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
        }
    }

    private List<ErpProductRespVO> buildProductVOList(List<ErpProductDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<Long, ErpProductCategoryDO> categoryMap = productCategoryService.getProductCategoryMap(
                convertSet(list, ErpProductDO::getCategoryId));
        Map<Long, ErpProductUnitDO> unitMap = productUnitService.getProductUnitMap(
                convertSet(list, ErpProductDO::getUnitId));
        return BeanUtils.toBean(list, ErpProductRespVO.class, product -> {
            MapUtils.findAndThen(categoryMap, product.getCategoryId(),
                    category -> product.setCategoryName(category.getName()));
            MapUtils.findAndThen(unitMap, product.getUnitId(),
                    unit -> product.setUnitName(unit.getName()));
        });
    }

    @Override
    public Long getProductCountByCategoryId(Long categoryId) {
        return productMapper.selectCountByCategoryId(categoryId);
    }

    @Override
    public Long getProductCountByUnitId(Long unitId) {
        return productMapper.selectCountByUnitId(unitId);
    }

    @Override
    public List<ErpProductDO> getproductByNos(ArrayList<String> productNos) {
        if(CollectionUtils.isEmpty(productNos))
            return new ArrayList<>();
        QueryWrapper<ErpProductDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_no", productNos);
        List<ErpProductDO> erpProductDOS = productMapper.selectList(queryWrapper);
        if(EasyListUtil.isNotEmpty(erpProductDOS)) {
            enhancer(erpProductDOS);
        }
        return erpProductDOS;
    }

    @Override
    public ErpProductDO getDefaultFeedProduct(Long tenantId, Long deptId) {
        QueryWrapper<ErpProductDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", "1"); // erp_product_category.id = 1 为 饲料
                // .eq("dept_id", deptId);
        List<ErpProductDO> erpProductDOS = productMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(erpProductDOS)) return null;
        return erpProductDOS.get(0);
    }

    @Override
    public ErpProductDO getById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public ErpProductDO getByProductNo(String productNo) {
        LambdaQueryWrapper<ErpProductDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpProductDO::getProductNo, productNo);
        queryWrapper.orderByDesc(ErpProductDO::getId).last("limit 1");
        return productMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ErpProductDO> selectNeedWarningThreshold() {
        return productMapper.selectNeedWarningThreshold();
    }

    public void enhancer(List<ErpProductDO> erpProductDOS) {
        Set<Long> unitIdSet = erpProductDOS.stream().map(obj -> obj.getUnitId()).collect(Collectors.toSet());
        List<ErpProductUnitDO> unitDOS = productUnitService.getByIds(unitIdSet);
        if(EasyListUtil.isEmpty(unitDOS)) return;
        for(ErpProductDO remote : erpProductDOS) {
            ErpProductUnitDO curr = null;
            for(ErpProductUnitDO local : unitDOS) {
                if(Objects.equals(remote.getUnitId(), local.getId())) {
                    curr = local;
                    break;
                }
            }
            if(curr == null) continue;
            remote.setUnit(curr.getName());
        }
    }

}
