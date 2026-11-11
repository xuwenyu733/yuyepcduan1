<template>
  <div class="experiment-list">
    <div class="list-filter-panel">
      <!-- 搜索栏 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" class="list-filter-form" label-width="80px">
        <el-form-item label="实验编号">
          <el-input
            v-model="queryParams.experimentNumber"
            placeholder="请输入实验编号"
            clearable
            @keyup.enter.native="handleQuery"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="实验标题">
          <el-input
            v-model="queryParams.experimentTitle"
            placeholder="请输入实验标题"
            clearable
            @keyup.enter.native="handleQuery"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="实验状态">
          <el-select
            v-model="queryParams.experimentalStatus"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="待开始" :value="0" />
            <el-option label="计划中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已总结" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-btns">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作栏 -->
      <div class="list-action-bar mb8">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">
          新建实验
        </el-button>
        <el-button
          v-if="false"
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="checkedIds.length === 0"
          @click="handleDeleteBatch"
        >
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 实验卡片列表 -->
    <div v-loading="loading" class="experiment-cards">
      <div
        v-for="experiment in filteredList"
        :key="experiment.id"
        class="experiment-card"
        @click="handleCardClick(experiment)"
      >
        <div class="card-header">
          <div class="card-title">
            <i class="el-icon-s-promotion"></i>
            <span class="experiment-number">{{ experiment.experimentNumber }}</span>
          </div>
          <el-tag :type="getStatusType(experiment.experimentalStatus)" size="small">
            {{ getStatusLabel(experiment.experimentalStatus) }}
          </el-tag>
        </div>
        <div class="card-body">
          <h3 class="experiment-title">{{ experiment.experimentTitle }}</h3>
          <div class="experiment-info">
            <div class="info-item">
              <i class="el-icon-date"></i>
              <span>{{ parseTime(experiment.startTime, '{y}-{m}-{d}') }} ~ {{ parseTime(experiment.endTime, '{y}-{m}-{d}') }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-user"></i>
              <!-- <span>{{ experiment.personInCharge }}</span> -->
              <span>
                <userName :user-id="experiment.creator"></userName>
              </span>
            </div>
            <div class="info-item">
              <span>池号:</span>
              <div v-if="experiment.poolNumbers && experiment.poolNumbers.length > 0">
                <el-tag v-for="num in experiment.poolNumbers" :key="num" size="mini" style="margin-right: 5px; margin-bottom: 2px;">{{ num }}</el-tag>
              </div>
              <span v-else>{{ experiment.pondDetailNum }}</span>
            </div>
            <div class="info-item">
              <span>品种:</span>
              <span>{{ experiment.variety }}</span>
            </div>
          </div>
        </div>
        <div class="card-footer">
          <el-button size="mini" type="primary" @click.stop="handleEdit(experiment)">
            {{ (userInfo && (experiment.userId == userInfo.userId || experiment.creator == userInfo.userId)) ? "编辑" : "查看" }}
          </el-button>
          <el-button size="mini" type="danger" @click.stop="handleDelete(experiment)" :disabled="!(userInfo && (experiment.userId == userInfo.userId || experiment.creator == userInfo.userId))">
            删除
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty
        v-if="!loading && filteredList.length === 0"
        description="暂无实验数据"
        :image-size="100"
      >
        <el-button type="primary" @click="handleAdd">创建第一个实验</el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNo"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import * as ZkExperimentalApi from '@/api/fish/zkexperimental'
import { UserInfoKey } from "@/utils/constants";

export default {
  name: 'ExperimentList',
  data() {
    return {
      userInfo: {},
      loading: false,
      list: [],
      filteredList: [],
      total: 0,
      checkedIds: [],
      queryParams: {
        pageNo: 1,
        pageSize: 12,
        experimentNumber: null,
        experimentTitle: null,
        experimentalStatus: null
      }
    }
  },
  created() {
    try {
      const raw = localStorage.getItem(UserInfoKey)
      this.userInfo = raw ? JSON.parse(raw) : {}
    } catch (e) {
      this.userInfo = {}
    }
    this.getList()
  },
  activated() {
    this.getList()
  },
  methods: {
    /** 查询列表 */
    async getList() {
      try {
        this.loading = true
        const res = await ZkExperimentalApi.getZkExperimentalPage(this.queryParams)
        this.list = (res.data && res.data.list) || []
        this.total = (res.data && res.data.total) || 0
        this.filteredList = this.list
      } catch (error) {
        console.error('加载实验列表失败:', error)
        this.list = []
        this.filteredList = []
        this.total = 0
        if (this.$modal && this.$modal.msgError) {
          this.$modal.msgError('加载实验列表失败')
        }
      } finally {
        this.loading = false
      }
    },

    /** 搜索 */
    handleQuery() {
      this.queryParams.pageNo = 1
      this.getList()
    },

    /** 重置 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },

    /** 新建 */
    handleAdd() {
      this.$emit('edit', null)
    },

    /** 编辑 */
    handleEdit(experiment) {
      this.$emit('edit', experiment.id)
    },

    /** 删除 */
    async handleDelete(row) {
      this.$modal.confirm('确定要删除这个实验吗？相关的数据和多媒体文件也会被删除。').then(async() => {
        try {
          await ZkExperimentalApi.deleteZkExperimental(row.id)
          this.$modal.msgSuccess('删除成功')
          this.getList()
          this.$emit('refresh')
        } catch (error) {
          // 错误已在拦截器中处理
        }
      }).catch(() => {

      })
    },

    /** 批量删除 */
    async handleDeleteBatch() {
      if (this.checkedIds.length === 0) {
        this.$modal.msgWarning('请选择要删除的实验')
        return
      }
      await this.$modal.confirm('确定要删除选中的实验吗？')
      try {
        await ZkExperimentalApi.deleteZkExperimentalList(this.checkedIds)
        this.checkedIds = []
        this.$modal.msgSuccess('删除成功')
        this.getList()
        this.$emit('refresh')
      } catch (error) {
        // 错误已在拦截器中处理
      }
    },

    /** 卡片点击 */
    handleCardClick(experiment) {
      // 可以跳转到详情页
      this.handleEdit(experiment);
    },

    /** 获取状态类型 */
    getStatusType(status) {
      const statusMap = {
        0: 'info',      // 待开始
        1: 'warning',   // 计划中
        2: 'success',   // 进行中
        3: '',          // 已完成
        4: 'info'       // 已总结
      }
      return statusMap[status] || 'info'
    },

    /** 获取状态标签 */
    getStatusLabel(status) {
      const statusMap = {
        0: '待开始',
        1: '计划中',
        2: '进行中',
        3: '已完成',
        4: '已总结'
      }
      return statusMap[status] || '未知'
    }
  }
}
</script>

<style scoped lang="scss">
.experiment-list {
  .list-filter-panel > .el-form--inline {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    margin-bottom: 4px;

    .el-form-item {
      margin-right: 18px !important;
      margin-bottom: 10px !important;
    }
  }

  .list-action-bar {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 10px;
    margin-bottom: 14px;
  }

  ::v-deep .el-form {
    .el-form-item__label {
      color: #ffffff !important;
      font-weight: 600 !important;
    }

    .el-input__inner {
      background-color: rgba(255, 255, 255, 0.04) !important;
      border-color: rgba(124, 143, 161, 0.22) !important;
      color: #ffffff !important;

      &::placeholder {
        color: rgba(255, 255, 255, 0.35) !important;
      }
    }

    .el-select .el-input__inner {
      background-color: rgba(255, 255, 255, 0.04) !important;
      border-color: rgba(124, 143, 161, 0.22) !important;
      color: #ffffff !important;
    }

    .el-select .el-input__suffix {
      color: rgba(255, 255, 255, 0.55) !important;
    }
  }

  .experiment-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 14px;
    margin-top: 4px;
  }

  .experiment-card {
    background: #12202d !important;
    border: 1px solid rgba(255, 255, 255, 0.08) !important;
    border-radius: 10px;
    padding: 16px 18px;
    cursor: pointer;
    transition: border-color 0.18s ease, transform 0.18s ease, background 0.18s ease;
    display: flex;
    flex-direction: column;
    box-shadow: none;

    &:hover {
      background: #162738 !important;
      border-color: rgba(84, 144, 122, 0.45) !important;
      transform: translateY(-1px);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.08);

      .card-title {
        display: flex;
        align-items: center;
        gap: 8px;

        i {
          color: #54907a;
          font-size: 16px;
        }

        .experiment-number {
          font-weight: 500;
          font-size: 13px;
          color: #8b9aab;
        }
      }
    }

    .card-body {
      flex: 1;

      .experiment-title {
        font-size: 15px;
        font-weight: 600;
        color: rgba(255, 255, 255, 0.92);
        margin: 0 0 12px 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .experiment-info {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 13px;
          color: rgba(255, 255, 255, 0.68);

          i {
            color: #8b9aab;
          }
        }
      }
    }

    .card-footer {
      display: flex;
      gap: 8px;
      margin-top: 14px;
      padding-top: 12px;
      border-top: 1px solid rgba(255, 255, 255, 0.08);

      .el-button {
        flex: 1;
      }
    }
  }

  ::v-deep .el-empty {
    .el-empty__description {
      color: #8b9aab !important;
    }
  }
}
</style>
