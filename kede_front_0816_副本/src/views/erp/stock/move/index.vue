<template>
  <div class="app-container">
    <el-empty description="库存调拨仅在 Vue3 + element-plus 版本实现，当前 Vue2 环境未开放。请使用「产品库存 / 其它出入库」等已上线菜单。">
      <el-button type="primary" size="small" @click="$router.push('/erp/stock/stock')">前往产品库存</el-button>
    </el-empty>
  </div>
</template>
<script>
export default { name: 'ErpStockMove' }
</script>
