<template>
  <div class="app-container erp-home">
    <el-alert
      title="本系统为 Vue2 版本。下方为当前可用的 ERP 功能入口；部分菜单（退货/盘点/调拨等）仅在 Vue3 版实现，点开会提示占位。"
      type="info"
      :closable="false"
      show-icon
      class="erp-home-alert"
    />

    <div v-for="group in groups" :key="group.title" class="erp-home-group">
      <div class="erp-home-group__title">{{ group.title }}</div>
      <el-row :gutter="16">
        <el-col
          v-for="item in group.items"
          :key="item.path"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <div class="erp-home-card" @click="go(item.path)">
            <div class="erp-home-card__name">{{ item.name }}</div>
            <div class="erp-home-card__desc">{{ item.desc }}</div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ErpHome',
  data() {
    return {
      groups: [
        {
          title: '采购管理',
          items: [
            { name: '采购订单', path: '/erp/purchase/order', desc: '采购下单、审批与查询' },
            { name: '采购入库', path: '/erp/purchase/in', desc: '采购到货入库' },
            { name: '供应商信息', path: '/erp/purchase/supplier', desc: '供应商档案维护' }
          ]
        },
        {
          title: '销售管理',
          items: [
            { name: '销售订单', path: '/erp/sale/order', desc: '销售下单与查询' },
            { name: '销售出库', path: '/erp/sale/out', desc: '销售发货出库' },
            { name: '客户信息', path: '/erp/sale/customer', desc: '客户档案维护' }
          ]
        },
        {
          title: '库存与产品',
          items: [
            { name: '产品库存', path: '/erp/stock/stock', desc: '当前库存查询' },
            { name: '其它入库', path: '/erp/stock/in', desc: '非采购入库' },
            { name: '其它出库', path: '/erp/stock/out', desc: '非销售出库' },
            { name: '库存明细', path: '/erp/stock/record', desc: '出入库流水' },
            { name: '仓库信息', path: '/erp/stock/warehouse', desc: '仓库档案' },
            { name: '产品信息', path: '/erp/product/product', desc: '产品档案' },
            { name: '产品分类', path: '/erp/product/category', desc: '产品分类' },
            { name: '产品单位', path: '/erp/product/unit', desc: '计量单位' }
          ]
        },
        {
          title: '财务管理',
          items: [
            { name: '结算账户', path: '/erp/finance/account', desc: '收付款账户' },
            { name: '付款单', path: '/erp/finance/payment', desc: '采购付款' },
            { name: '收款单', path: '/erp/finance/receipt', desc: '销售收款' }
          ]
        }
      ]
    }
  },
  methods: {
    go(path) {
      this.$router.push(path).catch(() => {})
    }
  }
}
</script>

<style scoped>
.erp-home {
  --erp-panel: #152536;
  --erp-panel-hover: #1a3148;
  --erp-border: rgba(100, 160, 210, 0.18);
  --erp-text: #e8eef8;
  --erp-text-2: #8b9bb0;
  --erp-accent: #3aa0ff;
}

.erp-home-alert {
  margin-bottom: 20px;
  background: rgba(58, 160, 255, 0.1) !important;
  border: 1px solid rgba(58, 160, 255, 0.28) !important;
  color: var(--erp-text-2) !important;
}
.erp-home-alert >>> .el-alert__title,
.erp-home-alert >>> .el-alert__description {
  color: var(--erp-text-2);
}
.erp-home-alert >>> .el-alert__icon {
  color: var(--erp-accent);
}

.erp-home-group {
  margin-bottom: 24px;
}
.erp-home-group__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--erp-text);
  margin-bottom: 12px;
}
.erp-home-card {
  border: 1px solid var(--erp-border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  cursor: pointer;
  background: var(--erp-panel);
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}
.erp-home-card:hover {
  border-color: rgba(58, 160, 255, 0.55);
  background: var(--erp-panel-hover);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.28);
}
.erp-home-card__name {
  font-size: 15px;
  font-weight: 600;
  color: var(--erp-text);
  margin-bottom: 6px;
}
.erp-home-card__desc {
  font-size: 13px;
  color: var(--erp-text-2);
  line-height: 1.4;
}
</style>
