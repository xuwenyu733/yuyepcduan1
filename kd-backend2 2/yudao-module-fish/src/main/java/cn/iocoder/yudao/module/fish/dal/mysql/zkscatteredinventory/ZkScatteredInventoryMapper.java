package cn.iocoder.yudao.module.fish.dal.mysql.zkscatteredinventory;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscatteredinventory.ZkScatteredInventoryDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkscatteredinventory.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 零星库存 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkScatteredInventoryMapper extends BaseMapperX<ZkScatteredInventoryDO> {

    default PageResult<ZkScatteredInventoryDO> selectPage(ZkScatteredInventoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkScatteredInventoryDO>()
                .eqIfPresent(ZkScatteredInventoryDO::getProductId, reqVO.getProductId())
                .eqIfPresent(ZkScatteredInventoryDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(ZkScatteredInventoryDO::getUnitId, reqVO.getUnitId())
                .eqIfPresent(ZkScatteredInventoryDO::getRemainingQuantity, reqVO.getRemainingQuantity())
                .eqIfPresent(ZkScatteredInventoryDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ZkScatteredInventoryDO::getBusType, reqVO.getBusType())
                .betweenIfPresent(ZkScatteredInventoryDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkScatteredInventoryDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkScatteredInventoryDO::getId));
    }

    /**
    SELECT
        zsi.*,ep.name AS productName,
        epu.name AS unitName,ew.name AS warehouseName,
        epc.name AS categoryName, epc.id AS categoryId
    FROM zk_scattered_inventory zsi
    LEFT JOIN erp_product ep ON ep.id = zsi.product_id
    LEFT JOIN erp_product_unit epu ON epu.id = zsi.unit_id
    LEFT JOIN erp_warehouse ew ON ew.id = zsi.warehouse_id
    LEFT JOIN erp_product_category epc ON epc.id = ep.category_id

     */
    @Select({"<script>",
            "SELECT \n" +
                    "\tzsi.*,ep.name AS productName, \n" +
                    "\tepu.name AS unitName,ew.name AS warehouseName, \n" +
                    "\tepc.name AS categoryName, epc.id AS categoryId\n" +
                    "FROM zk_scattered_inventory zsi \n" +
                    "LEFT JOIN erp_product ep ON ep.id = zsi.product_id\n" +
                    "LEFT JOIN erp_product_unit epu ON epu.id = zsi.unit_id\n" +
                    "LEFT JOIN erp_warehouse ew ON ew.id = zsi.warehouse_id\n" +
                    "LEFT JOIN erp_product_category epc ON epc.id = ep.category_id",
            "${ew.customSqlSegment} </script>"})
    IPage<ZkScatteredInventoryDO> customPage(Page<ZkScatteredInventoryDO> page, @Param(Constants.WRAPPER) QueryWrapper<ZkScatteredInventoryDO> queryWrapper);

}