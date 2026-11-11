<template>
  <div class="app-container">
    <el-empty description="库存盘点仅在 Vue3 + element-plus 版本实现，当前 Vue2 环境未开放。请使用「产品库存 / 库存明细」等已上线菜单。">
      <el-button type="primary" size="small" @click="$router.push('/erp/stock/stock')">前往产品库存</el-button>
    </el-empty>
  </div>
</template>
<script>
export default { name: 'ErpStockCheck' }
</script>
