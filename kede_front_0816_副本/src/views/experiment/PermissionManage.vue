<template>
  <div class="permission-page">
    <div class="page-card">
      <h2 class="section-title">角色权限说明</h2>
      <div class="role-grid">
        <div v-for="role in roles" :key="role.key" class="role-card">
          <el-tag :type="roleTagType(role.key)" size="small" effect="plain">{{ role.label }}</el-tag>
          <p>{{ role.description }}</p>
        </div>
      </div>
    </div>

    <div class="page-card user-card">
      <div class="list-header">
        <h2 class="section-title" style="margin: 0">用户列表 ({{ users.length }})</h2>
        <el-button type="primary" icon="el-icon-user-solid" @click="openCreate">添加用户</el-button>
      </div>

      <div v-if="formVisible" ref="formPanel" class="user-form-panel">
        <div class="form-panel-title">{{ editingId ? '编辑用户' : '添加用户' }}</div>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="user-form"
        >
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12">
              <el-form-item label="角色" prop="role">
                <el-select v-model="form.role" style="width: 100%">
                  <el-option
                    v-for="role in roles"
                    :key="role.key"
                    :label="role.label"
                    :value="role.key"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="部门" prop="department">
                <el-input v-model="form.department" placeholder="请输入部门" />
              </el-form-item>
            </el-col>
          </el-row>
          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="submitForm">
              {{ editingId ? '保存修改' : '创建用户' }}
            </el-button>
            <el-button @click="cancelForm">取消</el-button>
          </div>
        </el-form>
      </div>

      <el-table :data="users" border size="small">
        <el-table-column label="姓名" min-width="140">
          <template slot-scope="{ row }">
            <span class="user-cell"><i class="el-icon-user" />{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="角色" width="110">
          <template slot-scope="{ row }">
            <el-tag :type="roleTagType(row.role)" size="mini" effect="plain">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="120" />
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" @click.stop="openEdit(scope.row)">
              <i class="el-icon-edit" />
            </el-button>
            <el-button type="text" style="color: #f56c6c" @click.stop="handleDelete(scope.row)">
              <i class="el-icon-delete" />
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="page-card log-card">
      <h2 class="section-title">操作日志</h2>
      <div class="log-list">
        <div v-for="item in logs" :key="item.id" class="log-item">
          <i class="el-icon-success log-icon" />
          <div>
            <div class="log-text">{{ item.content }}</div>
            <div class="log-time">{{ item.createdAt }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  fetchRoles,
  fetchUsers,
  createUser,
  updateUser,
  deleteUser,
  fetchOperationLogs
} from '@/api/experiment'

function emptyForm() {
  return {
    name: '',
    email: '',
    role: 'viewer',
    department: ''
  }
}

export default {
  name: 'PermissionManage',
  data() {
    return {
      roles: [],
      users: [],
      logs: [],
      formVisible: false,
      submitting: false,
      editingId: '',
      form: emptyForm(),
      rules: {
        name: [{ required: true, message: '请填写此字段', trigger: 'blur' }],
        email: [
          { required: true, message: '请填写此字段', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }],
        department: [{ required: true, message: '请填写此字段', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const [roleRes, userRes, logRes] = await Promise.all([
        fetchRoles(),
        fetchUsers(),
        fetchOperationLogs()
      ])
      this.roles = roleRes.data
      this.users = userRes.data
      this.logs = logRes.data
    },
    roleLabel(key) {
      const found = this.roles.find(item => item.key === key)
      return found ? found.label : key
    },
    roleTagType(key) {
      return {
        admin: 'danger',
        experimenter: '',
        viewer: 'info'
      }[key] || 'info'
    },
    openCreate() {
      this.editingId = ''
      this.form = emptyForm()
      this.formVisible = true
      this.$nextTick(() => {
        if (this.$refs.formRef) this.$refs.formRef.clearValidate()
        this.scrollToForm()
      })
    },
    openEdit(row) {
      this.editingId = row.id
      this.form = {
        name: row.name,
        email: row.email,
        role: row.role,
        department: row.department
      }
      this.formVisible = true
      this.$nextTick(() => {
        if (this.$refs.formRef) this.$refs.formRef.clearValidate()
        this.scrollToForm()
      })
    },
    scrollToForm() {
      const panel = this.$refs.formPanel
      if (panel && panel.scrollIntoView) {
        panel.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
      }
    },
    cancelForm() {
      this.formVisible = false
      this.editingId = ''
      this.form = emptyForm()
    },
    submitForm() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.submitting = true
        try {
          if (this.editingId) {
            await updateUser(this.editingId, this.form)
            this.$message.success('更新成功')
          } else {
            await createUser(this.form)
            this.$message.success('创建成功')
          }
          this.cancelForm()
          const [userRes, logRes] = await Promise.all([fetchUsers(), fetchOperationLogs()])
          this.users = userRes.data
          this.logs = logRes.data
        } finally {
          this.submitting = false
        }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定删除用户「${row.name}」吗？`, '提示', { type: 'warning' })
        .then(async () => {
          await deleteUser(row.id)
          this.$message.success('删除成功')
          const [userRes, logRes] = await Promise.all([fetchUsers(), fetchOperationLogs()])
          this.users = userRes.data
          this.logs = logRes.data
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.permission-page {
  color: rgba(255, 255, 255, 0.88);
}

.page-card {
  background: #0f1a26;
  border: 1px solid rgba(124, 143, 161, 0.18);
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: none;
}

.section-title {
  margin: 0 0 14px;
  font-size: 15px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.role-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.role-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(124, 143, 161, 0.16);
  border-radius: 10px;
  padding: 14px;
}

.role-card p {
  margin: 10px 0 0;
  font-size: 13px;
  color: #7c8fa1;
  line-height: 1.55;
}

.user-card,
.log-card {
  margin-top: 14px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  gap: 12px;
}

.user-form-panel {
  margin-bottom: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(124, 143, 161, 0.16);
  border-radius: 10px;
}

.form-panel-title {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.user-form >>> .el-form-item {
  margin-bottom: 14px;
}

.form-actions {
  display: flex;
  gap: 10px;
  padding-top: 4px;
}

.user-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.85);
}

.log-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.log-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(124, 143, 161, 0.14);
  border-radius: 8px;
  padding: 12px 14px;
}

.log-icon {
  color: #67c23a;
  margin-top: 2px;
}

.log-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

.log-time {
  margin-top: 4px;
  font-size: 12px;
  color: #7c8fa1;
}

@media (max-width: 900px) {
  .role-grid {
    grid-template-columns: 1fr;
  }
}
</style>
