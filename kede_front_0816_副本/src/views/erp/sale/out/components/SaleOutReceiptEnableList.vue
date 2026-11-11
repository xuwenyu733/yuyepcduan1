<template>
    <div>
        <!-- 选择销售出库对话框 -->
        <el-dialog title="选择销售出库（仅展示可收款）" :visible.sync="visible" width="80%" @close="handleClose">
            <!-- 筛选表单 -->
            <el-form :model="searchForm" label-width="100px" v-if="false">
                <el-row>
                    <el-col :span="6">
                        <el-form-item label="出库单号">
                            <el-input v-model="searchForm.outboundNo" placeholder="请输入出库单号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="产品">
                            <el-select v-model="searchForm.productId" placeholder="请选择产品">
                                <el-option v-for="item in productOptions" :key="item.value" :label="item.label"
                                    :value="item.value"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="出库时间">
                            <el-date-picker v-model="searchForm.outboundTime" type="daterange" range-separator="至"
                                start-placeholder="开始日期" end-placeholder="结束日期"
                                value-format="yyyy-MM-dd"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleReset">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>

            <!-- 出库单表格 -->
            <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column prop="no" label="出库单号"></el-table-column>
                <el-table-column prop="customerName" label="客户"></el-table-column>
                <el-table-column prop="productNames" label="产品信息"></el-table-column>
                <el-table-column prop="outTime" label="出库时间"></el-table-column>
                <el-table-column prop="creatorName" label="创建人"></el-table-column>
                <el-table-column prop="totalPrice" label="应收金额"></el-table-column>
                <el-table-column prop="receiptPrice" label="已收金额"></el-table-column>
                <el-table-column prop="unpaidAmount" label="未收金额">
                    <template #default="scope">
                        <span v-if="scope.row.receiptPrice === scope.row.totalPrice">0</span>
                        <el-tag type="danger"v-else>{{ scope.row.totalPrice - scope.row.receiptPrice }}</el-tag>
                    </template>
                </el-table-column>
            </el-table>

            <el-row>
                <div style="width: 100%;height: 15px"></div>
            </el-row>
            <!-- 分页组件 -->
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>

            <!-- 操作按钮 -->
            <div style="text-align: right; margin-top: 20px">
                <el-button type="primary" @click="handleSubmit">确定</el-button>
                <el-button @click="handleClose">取消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { createSaleOut, updateSaleOut, deleteSaleOut, getSaleOut, getSaleOutPage, exportSaleOutExcel, updateSaleOutStatus } from "@/api/erp/saleOut";

export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
    data() {
        return {
            queryParams: {
                pageNo: 1,
                pageSize: 10,
                no: undefined,
                productId: undefined,
                outTime: [],
                receiptEnable: true,
                customerId: undefined
            },
            searchForm: {
                outboundNo: '', // 出库单号
                productId: '', // 产品ID
                outboundTime: [], // 出库时间范围
            },
            productOptions: [
                { value: '1', label: '产品A' },
                { value: '2', label: '产品B' },
                { value: '3', label: '产品C' },
            ],
            tableData: [
                {
                    outboundNo: 'XSCK20251013000001',
                    customer: '科德自用',
                    product: '三文鱼苗',
                    outboundTime: '2025-10-13',
                    creator: 'yangz...',
                    dueAmount: 1.00,
                    paidAmount: 0.00,
                    unpaidAmount: 1.00,
                },
            ],
            currentPage: 1, // 当前页码
            pageSize: 10, // 每页条数
            total: 1, // 总条数
            selectedRows: [], // 选中的行数据
        };
    },
    mounted() {
        this.getList();
    },
    methods: {
        getList() {
            // 执行查询
            getSaleOutPage(this.queryParams).then(response => {
                this.tableData = response.data.list;
                this.total = response.data.total;
                console.log(`============>tableData:${JSON.stringify(this.tableData)}`);
            });
        },
        // 表单提交
        handleSubmit() {
            if (this.selectedRows.length === 0) {
                this.$message.warning('请至少选择一条出库单');
                return;
            }
            // 调用后端接口提交选中的数据
            // console.log('选中的出库单:', this.selectedRows);
            this.$emit('submit', this.selectedRows);
            // this.visible = false;
            this.$emit('son_close');
        },
        // 关闭对话框时重置表单
        handleClose() {
            this.searchForm = {
                outboundNo: '',
                productId: '',
                outboundTime: [],
            };
            this.currentPage = 1;
            this.pageSize = 10;
            this.total = 0;
            this.selectedRows = [];
            this.$emit('son_close');
        },
        // 选中行变化
        handleSelectionChange(rows) {
            this.selectedRows = rows;
            // console.log(`==========>selectedRows:${JSON.stringify(this.selectedRows)}`);
        },
        // 搜索
        handleSearch() {
            // 模拟后端分页查询
            this.currentPage = 1;
            this.fetchData();
        },
        // 重置
        handleReset() {
            this.searchForm = {
                outboundNo: '',
                productId: '',
                outboundTime: [],
            };
        },
        // 每页条数变化
        handleSizeChange(size) {
            this.pageSize = size;
            this.currentPage = 1;
            this.fetchData();
        },
        // 页码变化
        handleCurrentChange(page) {
            this.currentPage = page;
            this.getList();
        },
        // 模拟后端分页查询（实际需替换为真实接口）
        fetchData() {
            // 模拟后端返回数据
            const mockData = [
                {
                    outboundNo: 'XSCK20251013000001',
                    customer: '科德自用',
                    product: '三文鱼苗',
                    outboundTime: '2025-10-13',
                    creator: 'yangz...',
                    dueAmount: 1.00,
                    paidAmount: 0.00,
                    unpaidAmount: 1.00,
                },
                {
                    outboundNo: 'XSCK20251013000002',
                    customer: '客户B',
                    product: '产品B',
                    outboundTime: '2025-10-12',
                    creator: 'admin',
                    dueAmount: 2.00,
                    paidAmount: 1.00,
                    unpaidAmount: 1.00,
                },
            ];
            this.tableData = mockData.slice(
                (this.currentPage - 1) * this.pageSize,
                this.currentPage * this.pageSize
            );
            this.total = mockData.length;
        },
    },
};
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
