<template>
  <div class="work-performance-container">
    <!-- 头部区域 -->
    <div class="performance-header">
      <div class="header-title">
        <h2>工作绩效评分</h2>
        <p>部门负责人对成员进行工作绩效评分</p>
      </div>
      <div class="header-actions">
        <span class="month-indicator">当前月份：{{ currentMonth }}</span>
        <el-button type="primary" class="add-rating-btn" @click="showAddRatingDialog">
          <i class="el-icon-plus"></i> 新增评分
        </el-button>
      </div>
    </div>

    <!-- 评分记录列表 -->
    <div class="performance-records-section">
      <div v-loading="loading">
        <el-table 
          :data="performanceList" 
          style="width: 100%"
          stripe>
          <el-table-column prop="ratedUserName" label="被评分人" min-width="120" align="center">
          </el-table-column>
          <el-table-column prop="waterQualityScore" label="水质合格率" min-width="100" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.waterQualityScore !== null && scope.row.waterQualityScore !== undefined" 
                    style="color: #54907a; font-weight: 600;">
                {{ scope.row.waterQualityScore }}分
              </span>
              <span v-else style="color: rgba(255, 255, 255, 0.5);">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="equipmentFailureScore" label="设备故障率" min-width="100" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.equipmentFailureScore !== null && scope.row.equipmentFailureScore !== undefined" 
                    style="color: #54907a; font-weight: 600;">
                {{ scope.row.equipmentFailureScore }}分
              </span>
              <span v-else style="color: rgba(255, 255, 255, 0.5);">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="survivalRateScore" label="存活率" min-width="100" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.survivalRateScore !== null && scope.row.survivalRateScore !== undefined" 
                    style="color: #54907a; font-weight: 600;">
                {{ scope.row.survivalRateScore }}分
              </span>
              <span v-else style="color: rgba(255, 255, 255, 0.5);">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="feedCoefficientScore" label="饵料系数" min-width="100" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.feedCoefficientScore !== null && scope.row.feedCoefficientScore !== undefined" 
                    style="color: #54907a; font-weight: 600;">
                {{ scope.row.feedCoefficientScore }}分
              </span>
              <span v-else style="color: rgba(255, 255, 255, 0.5);">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalScore" label="总分" min-width="100" align="center">
            <template slot-scope="scope">
              <span style="color: #67C23A; font-weight: 600; font-size: 16px;">
                {{ scope.row.totalScore || 0 }}分
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="comment" label="评分说明" min-width="200" show-overflow-tooltip>
            <template slot-scope="scope">
              <span v-if="scope.row.comment">{{ scope.row.comment }}</span>
              <span v-else style="color: rgba(255, 255, 255, 0.5);">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="ratingMonth" label="评分月份" min-width="120" align="center">
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="150" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button 
                type="text" 
                size="small" 
                icon="el-icon-edit"
                @click="handleEditRecord(scope.row)">
                修改
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                icon="el-icon-delete"
                style="color: #F56C6C;"
                @click="handleDeleteRecord(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryParams.pageNo"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="queryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </div>
      </div>
    </div>

    <!-- 打分对话框 -->
    <el-dialog
      :title="ratingDialogTitle"
      :visible.sync="ratingDialogVisible"
      width="700px"
      :close-on-click-modal="false"
      @open="handleDialogOpen"
      @close="handleDialogClose">
      <el-form
        ref="ratingForm"
        :model="ratingForm"
        :rules="ratingRules"
        label-width="120px">
        <el-form-item label="被评分人" prop="ratedUserId">
          <el-select
            v-model="ratingForm.ratedUserId"
            placeholder="请选择被评分人"
            filterable
            style="width: 100%;"
            :disabled="!!ratingForm.id"
            @change="handleUserChange">
            <el-option
              v-for="user in rateableUsers"
              :key="user.id"
              :label="user.name"
              :value="user.id"
              :disabled="user.isRated && !ratingForm.id">
              <span style="float: left">{{ user.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                <el-tag v-if="user.isRated" type="success" size="mini" style="margin-left: 10px;">
                  已评分：{{ user.totalScore }}分
                </el-tag>
                <span v-else style="margin-left: 10px;">{{ user.deptName }}</span>
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="水质合格率" prop="waterQualityScore">
          <el-input-number
            v-model="ratingForm.waterQualityScore"
            :min="0"
            :max="6"
            :precision="1"
            :step="0.1"
            style="width: 100%;">
          </el-input-number>
          <div class="form-tip">满分：6分</div>
        </el-form-item>
        <el-form-item label="设备故障率" prop="equipmentFailureScore">
          <el-input-number
            v-model="ratingForm.equipmentFailureScore"
            :min="0"
            :max="6"
            :precision="1"
            :step="0.1"
            style="width: 100%;">
          </el-input-number>
          <div class="form-tip">满分：6分</div>
        </el-form-item>
        <el-form-item label="存活率" prop="survivalRateScore">
          <el-input-number
            v-model="ratingForm.survivalRateScore"
            :min="0"
            :max="14"
            :precision="1"
            :step="0.1"
            style="width: 100%;">
          </el-input-number>
          <div class="form-tip">满分：14分</div>
        </el-form-item>
        <el-form-item label="饵料系数" prop="feedCoefficientScore">
          <el-input-number
            v-model="ratingForm.feedCoefficientScore"
            :min="0"
            :max="4"
            :precision="1"
            :step="0.1"
            style="width: 100%;">
          </el-input-number>
          <div class="form-tip">满分：4分</div>
        </el-form-item>
        <el-form-item label="总分" v-if="ratingForm.totalScore !== null && ratingForm.totalScore !== undefined">
          <el-input 
            :value="ratingForm.totalScore.toFixed(1) + '分'" 
            disabled
            style="width: 100%;">
          </el-input>
          <div class="form-tip">总分：30分</div>
        </el-form-item>
        <el-form-item label="评分说明" prop="comment">
          <el-input
            v-model="ratingForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入评分说明（非必填）"
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="ratingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRating" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  createOaWorkPerformance, 
  updateOaWorkPerformance, 
  deleteOaWorkPerformance,
  getOaWorkPerformancePage,
  getRateableUsers
} from '@/api/fish/workPerformance'

export default {
  name: 'WorkPerformance',
  data() {
    return {
      loading: false,
      loadingUsers: false,
      submitting: false,
      currentMonth: '',
      rateableUsers: [],
      performanceList: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        ratingMonth: ''
      },
      ratingDialogVisible: false,
      ratingDialogTitle: '新增评分',
      ratingForm: {
        id: null,
        ratedUserId: null,
        ratedUserName: '',
        waterQualityScore: null,
        equipmentFailureScore: null,
        survivalRateScore: null,
        feedCoefficientScore: null,
        totalScore: null,
        comment: ''
      },
      ratingRules: {
        ratedUserId: [
          { required: true, message: '请选择被评分人', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    'ratingForm.waterQualityScore': function() {
      this.calculateTotalScore()
    },
    'ratingForm.equipmentFailureScore': function() {
      this.calculateTotalScore()
    },
    'ratingForm.survivalRateScore': function() {
      this.calculateTotalScore()
    },
    'ratingForm.feedCoefficientScore': function() {
      this.calculateTotalScore()
    }
  },
  mounted() {
    this.initCurrentMonth()
    this.loadPerformanceList()
  },
  methods: {
    initCurrentMonth() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      this.currentMonth = `${year}-${month}`
      this.queryParams.ratingMonth = this.currentMonth
    },
    async loadRateableUsers() {
      this.loadingUsers = true
      try {
        const res = await getRateableUsers()
        if (res.code === 0) {
          this.rateableUsers = res.data || []
        }
      } catch (error) {
        this.$message.error('加载可评分人员失败：' + (error.message || '未知错误'))
      } finally {
        this.loadingUsers = false
      }
    },
    showAddRatingDialog() {
      this.ratingDialogTitle = '新增评分'
      this.ratingForm = {
        id: null,
        ratedUserId: null,
        ratedUserName: '',
        waterQualityScore: null,
        equipmentFailureScore: null,
        survivalRateScore: null,
        feedCoefficientScore: null,
        totalScore: null,
        comment: ''
      }
      this.ratingDialogVisible = true
      this.loadRateableUsers()
    },
    handleDialogOpen() {
      if (!this.ratingForm.id) {
        // 新增时加载可评分人员
        this.loadRateableUsers()
      }
    },
    handleDialogClose() {
      this.$nextTick(() => {
        if (this.$refs.ratingForm) {
          this.$refs.ratingForm.clearValidate()
        }
      })
    },
    handleUserChange(userId) {
      const selectedUser = this.rateableUsers.find(u => u.id === userId)
      if (selectedUser) {
        this.ratingForm.ratedUserName = selectedUser.name
        // 如果已评分，填充已有数据
        if (selectedUser.isRated && selectedUser.ratingId) {
          // 需要获取详细数据
          this.loadPerformanceDetail(selectedUser.ratingId)
        } else {
          this.ratingForm.id = null
          this.ratingForm.waterQualityScore = null
          this.ratingForm.equipmentFailureScore = null
          this.ratingForm.survivalRateScore = null
          this.ratingForm.feedCoefficientScore = null
          this.ratingForm.totalScore = null
          this.ratingForm.comment = ''
          this.ratingDialogTitle = '新增评分'
        }
      }
    },
    async loadPerformanceDetail(id) {
      try {
        const res = await getOaWorkPerformance(id)
        if (res.code === 0 && res.data) {
          const data = res.data
          this.ratingForm.id = data.id
          this.ratingForm.waterQualityScore = data.waterQualityScore ? parseFloat(data.waterQualityScore) : null
          this.ratingForm.equipmentFailureScore = data.equipmentFailureScore ? parseFloat(data.equipmentFailureScore) : null
          this.ratingForm.survivalRateScore = data.survivalRateScore ? parseFloat(data.survivalRateScore) : null
          this.ratingForm.feedCoefficientScore = data.feedCoefficientScore ? parseFloat(data.feedCoefficientScore) : null
          this.ratingForm.totalScore = data.totalScore ? parseFloat(data.totalScore) : null
          this.ratingForm.comment = data.comment || ''
          this.ratingDialogTitle = '修改评分'
        }
      } catch (error) {
        this.$message.error('加载评分详情失败：' + (error.message || '未知错误'))
      }
    },
    calculateTotalScore() {
      let total = 0
      if (this.ratingForm.waterQualityScore !== null && this.ratingForm.waterQualityScore !== undefined) {
        total += parseFloat(this.ratingForm.waterQualityScore)
      }
      if (this.ratingForm.equipmentFailureScore !== null && this.ratingForm.equipmentFailureScore !== undefined) {
        total += parseFloat(this.ratingForm.equipmentFailureScore)
      }
      if (this.ratingForm.survivalRateScore !== null && this.ratingForm.survivalRateScore !== undefined) {
        total += parseFloat(this.ratingForm.survivalRateScore)
      }
      if (this.ratingForm.feedCoefficientScore !== null && this.ratingForm.feedCoefficientScore !== undefined) {
        total += parseFloat(this.ratingForm.feedCoefficientScore)
      }
      this.ratingForm.totalScore = total > 0 ? total : null
    },
    async loadPerformanceList() {
      // 确保始终使用当前月份
      this.queryParams.ratingMonth = this.currentMonth
      this.loading = true
      try {
        const res = await getOaWorkPerformancePage(this.queryParams)
        if (res && (res.code === 0 || res.code === 200)) {
          this.performanceList = (res.data && res.data.list) || []
          this.total = (res.data && res.data.total) || 0
        }
      } catch (error) {
        this.performanceList = []
        this.total = 0
        const msg = (error && error.message) || (typeof error === 'string' ? error : '') || '未知错误'
        if (msg !== 'error') {
          this.$message.error('加载评分记录失败：' + msg)
        }
      } finally {
        this.loading = false
      }
    },
    handleEditRecord(row) {
      this.ratingDialogTitle = '修改评分'
      this.loadRateableUsers()
      this.ratingForm = {
        id: row.id,
        ratedUserId: row.ratedUserId,
        ratedUserName: row.ratedUserName,
        waterQualityScore: row.waterQualityScore ? parseFloat(row.waterQualityScore) : null,
        equipmentFailureScore: row.equipmentFailureScore ? parseFloat(row.equipmentFailureScore) : null,
        survivalRateScore: row.survivalRateScore ? parseFloat(row.survivalRateScore) : null,
        feedCoefficientScore: row.feedCoefficientScore ? parseFloat(row.feedCoefficientScore) : null,
        totalScore: row.totalScore ? parseFloat(row.totalScore) : null,
        comment: row.comment || ''
      }
      this.ratingDialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.ratingForm) {
          this.$refs.ratingForm.clearValidate()
        }
      })
    },
    async handleSubmitRating() {
      this.$refs.ratingForm.validate(async (valid) => {
        if (!valid) {
          return false
        }

        this.submitting = true
        try {
          if (this.ratingForm.id) {
            // 更新
            await updateOaWorkPerformance({
              id: this.ratingForm.id,
              waterQualityScore: this.ratingForm.waterQualityScore,
              equipmentFailureScore: this.ratingForm.equipmentFailureScore,
              survivalRateScore: this.ratingForm.survivalRateScore,
              feedCoefficientScore: this.ratingForm.feedCoefficientScore,
              comment: this.ratingForm.comment
            })
            this.$message.success('修改成功')
          } else {
            // 创建
            await createOaWorkPerformance({
              ratedUserId: this.ratingForm.ratedUserId,
              waterQualityScore: this.ratingForm.waterQualityScore,
              equipmentFailureScore: this.ratingForm.equipmentFailureScore,
              survivalRateScore: this.ratingForm.survivalRateScore,
              feedCoefficientScore: this.ratingForm.feedCoefficientScore,
              comment: this.ratingForm.comment,
              ratingMonth: this.currentMonth
            })
            this.$message.success('评分成功')
          }
          this.ratingDialogVisible = false
          this.loadPerformanceList()
        } catch (error) {
          this.$message.error((error.response && error.response.data && error.response.data.msg) || error.message || '操作失败')
        } finally {
          this.submitting = false
        }
      })
    },
    async handleDeleteRecord(row) {
      this.$confirm('确定要删除这条评分记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteOaWorkPerformance(row.id)
          this.$message.success('删除成功')
          this.loadPerformanceList()
        } catch (error) {
          this.$message.error((error.response && error.response.data && error.response.data.msg) || error.message || '删除失败')
        }
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNo = 1 // 重置到第一页
      this.loadPerformanceList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNo = val
      this.loadPerformanceList()
    },
    formatDate(date) {
      if (!date) return '--'
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }
}
</script>

<style lang="scss" scoped>
.work-performance-container {
  padding: 24px;
  background: transparent;
  min-height: calc(100vh - 100px);

  .performance-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-title {
      h2 {
        color: rgba(255, 255, 255, 0.9);
        font-size: 24px;
        margin: 0 0 8px 0;
      }

      p {
        color: rgba(255, 255, 255, 0.6);
        font-size: 14px;
        margin: 0;
      }
    }

    .header-actions {
      display: flex;
      align-items: center;
      gap: 16px;

      .month-indicator {
        color: rgba(255, 255, 255, 0.6);
        font-size: 14px;
      }

      .add-rating-btn {
        font-weight: 600 !important;
        background: #2f6b5a !important;
        border-color: #54907a !important;
        color: #fff !important;
        &:hover, &:focus {
          background: #3a8570 !important;
          border-color: #6fb59a !important;
        }
      }
    }
  }

  .performance-records-section {
    background: rgba(10, 21, 32, 0.6);
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;

    ::v-deep .el-pagination {
      .btn-prev,
      .btn-next,
      .el-pager li {
        background: #162e46 !important;
        color: rgba(255, 255, 255, 0.85) !important;
        border: 1px solid rgba(84, 144, 122, 0.28) !important;
        border-radius: 4px;
      }
      .el-pager li.active {
        background: #54907a !important;
        border-color: #54907a !important;
        color: #fff !important;
      }
      .el-pagination__total,
      .el-pagination__jump {
        color: rgba(255, 255, 255, 0.75);
      }
    }
  }

  .form-tip {
    color: rgba(255, 255, 255, 0.6);
    font-size: 12px;
    margin-top: 4px;
  }
}

// 表格样式
::v-deep .el-table {
  background: transparent;

  .el-table__header-wrapper {
    th {
      background: rgba(10, 21, 32, 0.8) !important;
      color: rgba(255, 255, 255, 0.9) !important;
      border-color: rgba(84, 144, 122, 0.2) !important;
    }
  }

  .el-table__body-wrapper {
    tr {
      background: rgba(10, 21, 32, 0.6) !important;
      color: rgba(255, 255, 255, 0.9) !important;
      border-color: rgba(84, 144, 122, 0.2) !important;

      &:hover {
        background: rgba(84, 144, 122, 0.1) !important;
      }

      td {
        background: rgba(10, 21, 32, 0.6) !important;
        color: rgba(255, 255, 255, 0.9) !important;
        border-color: rgba(84, 144, 122, 0.2) !important;
      }
    }

    tr.el-table__row--striped {
      background: rgba(10, 21, 32, 0.4) !important;

      &:hover {
        background: rgba(84, 144, 122, 0.15) !important;
      }
    }
  }
}

// 对话框样式
::v-deep .el-dialog {
  background: rgba(10, 21, 32, 0.95);
  border: 1px solid rgba(84, 144, 122, 0.2);
  border-radius: 8px;

  .el-dialog__header {
    background: rgba(10, 21, 32, 0.8);
    border-bottom: 1px solid rgba(84, 144, 122, 0.2);
    padding: 20px 24px;
    border-radius: 8px 8px 0 0;

    .el-dialog__title {
      color: rgba(255, 255, 255, 0.9);
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__close {
      color: rgba(255, 255, 255, 0.6);
      font-size: 20px;

      &:hover {
        color: rgba(255, 255, 255, 0.9);
      }
    }
  }

  .el-dialog__body {
    background: rgba(10, 21, 32, 0.6);
    padding: 24px;

    .el-form-item {
      margin-bottom: 24px;

      .el-form-item__label {
        color: rgba(255, 255, 255, 0.9);
        font-size: 14px;
        line-height: 40px;
      }

      .el-form-item__content {
        .el-input__inner,
        .el-textarea__inner {
          background: rgba(10, 21, 32, 0.8);
          border-color: rgba(84, 144, 122, 0.2);
          color: rgba(255, 255, 255, 0.9);
          border-radius: 4px;
          transition: all 0.3s;

          &:focus {
            border-color: rgba(84, 144, 122, 0.5);
            box-shadow: 0 0 0 2px rgba(84, 144, 122, 0.1);
          }

          &::placeholder {
            color: rgba(255, 255, 255, 0.4);
          }
        }

        .el-input.is-disabled .el-input__inner {
          background: rgba(10, 21, 32, 0.4);
          border-color: rgba(84, 144, 122, 0.1);
          color: rgba(255, 255, 255, 0.6);
        }

        .el-select {
          .el-input__inner {
            background: rgba(10, 21, 32, 0.8);
            border-color: rgba(84, 144, 122, 0.2);
            color: rgba(255, 255, 255, 0.9);
          }

          .el-input.is-focus .el-input__inner {
            border-color: rgba(84, 144, 122, 0.5);
          }
        }

        .el-input-number {
          width: 100%;

          .el-input__inner {
            text-align: left;
          }
        }
      }
    }
  }

  .el-dialog__footer {
    background: rgba(10, 21, 32, 0.6);
    border-top: 1px solid rgba(84, 144, 122, 0.2);
    padding: 16px 24px;
    border-radius: 0 0 8px 8px;

    .dialog-footer {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }
  }
}

// 下拉选择框样式
::v-deep .el-select-dropdown {
  background: rgba(10, 21, 32, 0.95);
  border: 1px solid rgba(84, 144, 122, 0.2);

  .el-select-dropdown__item {
    background: transparent;
    color: rgba(255, 255, 255, 0.9);
    padding: 12px 20px;

    &:hover {
      background: rgba(84, 144, 122, 0.1);
    }

    &.selected {
      background: rgba(84, 144, 122, 0.2);
      color: rgba(255, 255, 255, 0.9);
      font-weight: 600;
    }

    &.is-disabled {
      color: rgba(255, 255, 255, 0.4);
      cursor: not-allowed;

      &:hover {
        background: transparent;
      }
    }
  }

  .el-scrollbar__bar {
    &.is-vertical {
      .el-scrollbar__thumb {
        background: rgba(84, 144, 122, 0.3);
      }
    }
  }
}

// 分页样式
::v-deep .el-pagination {
  .el-pagination__total,
  .el-pagination__jump,
  .btn-prev,
  .btn-next,
  .number {
    color: rgba(255, 255, 255, 0.9);
  }

  .el-pagination__sizes .el-input__inner {
    background: rgba(10, 21, 32, 0.8);
    border-color: rgba(84, 144, 122, 0.2);
    color: rgba(255, 255, 255, 0.9);
  }
}
</style>



