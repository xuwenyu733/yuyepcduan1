package cn.iocoder.yudao.module.erp.dal.mysql.product;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * ERP 产品 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ErpProductMapper extends BaseMapperX<ErpProductDO> {

    default PageResult<ErpProductDO> selectPage(ErpProductPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpProductDO>()
                .likeIfPresent(ErpProductDO::getName, reqVO.getProductName())
                .eqIfPresent(ErpProductDO::getId, reqVO.getProductId())
                .eqIfPresent(ErpProductDO::getCategoryId, reqVO.getCategoryId())
                .betweenIfPresent(ErpProductDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpProductDO::getId));
    }

    default Long selectCountByCategoryId(Long categoryId) {
        return selectCount(ErpProductDO::getCategoryId, categoryId);
    }

    default Long selectCountByUnitId(Long unitId) {
        return selectCount(ErpProductDO::getUnitId, unitId);
    }

    default List<ErpProductDO> selectListByStatus(Integer status) {
        return selectList(ErpProductDO::getStatus, status);
    }

    default ErpProductDO selectByBarCode(Long id, String barCode) {
        ErpProductDO erpProductDO = new ErpProductDO().setBarCode(barCode);
        QueryWrapper<ErpProductDO> queryWrapper = new QueryWrapper<>(erpProductDO);
        if(id != null) queryWrapper.ne("id", id);
        List<ErpProductDO> erpProductDOS = selectList(queryWrapper);
        if(EasyListUtil.isEmpty(erpProductDOS)) return null;
        return erpProductDOS.get(0);
    }

    /**

     select ep.id as productId,GROUP_CONCAT(es.warehouse_id) as warehouseIds,
        GROUP_CONCAT(CONCAT(ew.name,':',round(es.count, 2))) as stockInfo
     from erp_stock es
     left join erp_warehouse ew on ew.id = es.warehouse_id
     left join erp_product ep on ep.id = es.product_id
     group by ep.id

     * @param queryWrapper
     * @return
     */
    @Select({
            "select ep.id as productId,GROUP_CONCAT(es.warehouse_id) as warehouseIds, \n" +
            "        GROUP_CONCAT(CONCAT(ew.name,':',round(es.count, 2))) as stockInfo \n" +
            "from erp_stock es\n" +
            "left join erp_warehouse ew on ew.id = es.warehouse_id \n" +
            "left join erp_product ep on ep.id = es.product_id \n" +
            "${ew.customSqlSegment} " +
            "group by ep.id"
    })
    List<Map<String, Object>> selectStockInfo(@Param("ew") QueryWrapper<ErpProductDO> queryWrapper);

    @Select({
            "SELECT * FROM (\n" +
            "\tSELECT ep.*, COALESCE(es.count, 0) AS `count` FROM erp_product ep\n" +
            "\tLEFT JOIN (\n" +
            "\t\tSELECT product_id, SUM(COUNT) AS `count` FROM erp_stock\n" +
            "\t\tGROUP BY product_id\n" +
            "\t) es ON es.product_id = ep.id\n" +
            ") t1\n" +
            "WHERE warning_threshold > 0 AND warning_threshold > `count`"
    })
    List<ErpProductDO> selectNeedWarningThreshold();

}