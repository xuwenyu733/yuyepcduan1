<template>
  <div class="app-container">
    <el-empty description="销售退货仅在 Vue3 + element-plus 版本实现，当前 Vue2 环境未开放。请使用「销售订单 / 销售出库」等已上线菜单。">
      <el-button type="primary" size="small" @click="$router.push('/erp/sale/order')">前往销售订单</el-button>
    </el-empty>
  </div>
</template>
<script>
export default { name: 'ErpSaleReturn' }
</script>
