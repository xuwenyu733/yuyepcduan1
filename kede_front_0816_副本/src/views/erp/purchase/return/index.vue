<template>
  <div class="app-container">
    <el-empty description="采购退货仅在 Vue3 + element-plus 版本实现，当前 Vue2 环境未开放。请使用「采购订单 / 采购入库」等已上线菜单。">
      <el-button type="primary" size="small" @click="$router.push('/erp/purchase/order')">前往采购订单</el-button>
    </el-empty>
  </div>
</template>
<script>
export default { name: 'ErpPurchaseReturn' }
</script>
