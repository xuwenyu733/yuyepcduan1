<template>
  <div class="user-rating-container">
    <!-- 头部区域 -->
    <div class="rating-header">
      <div class="header-title">
        <h2>他人打分</h2>
        <p>对部门成员进行评分</p>
      </div>
      <div class="header-actions">
        <span class="month-indicator">当前月份：{{ currentMonth }}</span>
        <el-button type="primary" class="add-rating-btn" @click="showAddRatingDialog">
          <i class="el-icon-plus"></i> 新增评分
        </el-button>
      </div>
    </div>

    <!-- 评分记录列表 -->
    <div class="rating-records-section">
      <div v-loading="loading">
        <el-table 
          :data="ratingList" 
          style="width: 100%"
          stripe>
          <el-table-column prop="ratedUserName" label="被评分人" min-width="120" align="center">
          </el-table-column>
          <el-table-column prop="score" label="评分" min-width="100" align="center">
            <template slot-scope="scope">
              <span style="color: #54907a; font-weight: 600;">{{ scope.row.score }}分</span>
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
      width="600px"
      :close-on-click-modal="false"
      @open="handleDialogOpen"
      @close="handleDialogClose">
      <el-form
        ref="ratingForm"
        :model="ratingForm"
        :rules="ratingRules"
        label-width="100px">
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
                  已评分：{{ user.score }}分
                </el-tag>
                <span v-else style="margin-left: 10px;">{{ user.deptName }}</span>
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="评分" prop="score">
          <el-input-number
            v-model="ratingForm.score"
            :min="0"
            :max="ratingForm.maxScore"
            :precision="1"
            :step="0.1"
            style="width: 100%;">
          </el-input-number>
          <div class="form-tip">满分：{{ ratingForm.maxScore }}分</div>
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
  createOaUserRating, 
  updateOaUserRating, 
  deleteOaUserRating,
  getOaUserRatingPage,
  getRateableUsers
} from '@/api/fish/userRating'

export default {
  name: 'UserRating',
  data() {
    return {
      loading: false,
      loadingUsers: false,
      submitting: false,
      currentMonth: '',
      rateableUsers: [],
      ratingList: [],
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
        score: null,
        comment: '',
        maxScore: 8
      },
      ratingRules: {
        ratedUserId: [
          { required: true, message: '请选择被评分人', trigger: 'change' }
        ],
        score: [
          { required: true, message: '请输入评分', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.initCurrentMonth()
    this.loadRatingList()
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
        score: null,
        comment: '',
        maxScore: 8
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
        this.ratingForm.maxScore = selectedUser.maxScore
        // 如果已评分，填充已有数据
        if (selectedUser.isRated && selectedUser.ratingId) {
          this.ratingForm.id = selectedUser.ratingId
          this.ratingForm.score = selectedUser.score
          this.ratingForm.comment = selectedUser.comment || ''
          this.ratingDialogTitle = '修改评分'
        } else {
          this.ratingForm.id = null
          this.ratingForm.score = null
          this.ratingForm.comment = ''
          this.ratingDialogTitle = '新增评分'
        }
      }
    },
    async loadRatingList() {
      // 确保始终使用当前月份
      this.queryParams.ratingMonth = this.currentMonth
      this.loading = true
      try {
        const res = await getOaUserRatingPage(this.queryParams)
        if (res.code === 0) {
          this.ratingList = res.data.list || []
          this.total = res.data.total || 0
        }
      } catch (error) {
        this.$message.error('加载评分记录失败：' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },
    handleRateUser(user) {
      this.ratingDialogTitle = '打分'
      this.ratingForm = {
        id: null,
        ratedUserId: user.id,
        ratedUserName: user.name,
        score: null,
        comment: '',
        maxScore: user.maxScore
      }
      this.ratingDialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.ratingForm) {
          this.$refs.ratingForm.clearValidate()
        }
      })
    },
    handleEditRecord(row) {
      this.ratingDialogTitle = '修改评分'
      this.loadRateableUsers()
      this.ratingForm = {
        id: row.id,
        ratedUserId: row.ratedUserId,
        ratedUserName: row.ratedUserName,
        score: parseFloat(row.score),
        comment: row.comment || '',
        maxScore: parseFloat(row.score) <= 8 ? 8 : 12
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
            await updateOaUserRating({
              id: this.ratingForm.id,
              score: this.ratingForm.score,
              comment: this.ratingForm.comment
            })
            this.$message.success('修改成功')
          } else {
            // 创建
            await createOaUserRating({
              ratedUserId: this.ratingForm.ratedUserId,
              score: this.ratingForm.score,
              comment: this.ratingForm.comment,
              ratingMonth: this.currentMonth
            })
            this.$message.success('打分成功')
          }
          this.ratingDialogVisible = false
          this.loadRatingList()
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
          await deleteOaUserRating(row.id)
          this.$message.success('删除成功')
          this.loadRatingList()
        } catch (error) {
          this.$message.error((error.response && error.response.data && error.response.data.msg) || error.message || '删除失败')
        }
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNo = 1 // 重置到第一页
      this.loadRatingList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNo = val
      this.loadRatingList()
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
.user-rating-container {
  padding: 24px;
  background: transparent;
  min-height: calc(100vh - 100px);

  .rating-header {
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

  .rating-records-section {
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

