const roles = [
  {
    key: 'admin',
    label: '管理员',
    description: '完全权限：创建、编辑、删除实验和数据，管理用户'
  },
  {
    key: 'experimenter',
    label: '实验员',
    description: '录入和编辑实验数据，上传多媒体文件'
  },
  {
    key: 'viewer',
    label: '查看者',
    description: '仅可查看实验数据和分析报告'
  }
]

let users = [
  {
    id: 'U-001',
    name: '系统管理员',
    email: 'admin@farmlab.com',
    role: 'admin',
    department: '管理部',
    createdAt: '2024-01-01'
  },
  {
    id: 'U-002',
    name: '刘涛',
    email: 'liutao@farmlab.com',
    role: 'experimenter',
    department: '实验部',
    createdAt: '2024-01-08'
  },
  {
    id: 'U-003',
    name: '陈晓雯',
    email: 'chenxw@farmlab.com',
    role: 'experimenter',
    department: '饲料研究室',
    createdAt: '2024-01-12'
  },
  {
    id: 'U-004',
    name: '王敏',
    email: 'wangmin@farmlab.com',
    role: 'experimenter',
    department: '病害防控组',
    createdAt: '2024-02-03'
  },
  {
    id: 'U-005',
    name: '赵磊',
    email: 'zhaolei@farmlab.com',
    role: 'viewer',
    department: '技术部',
    createdAt: '2024-02-18'
  },
  {
    id: 'U-006',
    name: '孙倩',
    email: 'sunqian@farmlab.com',
    role: 'viewer',
    department: '数据分析室',
    createdAt: '2024-03-01'
  }
]

let logs = [
  {
    id: 'L-001',
    content: "刘涛 创建了新实验 'EXP-2024-001'",
    createdAt: '2024-12-13 14:30:25'
  },
  {
    id: 'L-002',
    content: '刘涛 录入了南美白对虾密度实验第 3 周生长数据',
    createdAt: '2024-12-13 15:12:08'
  },
  {
    id: 'L-003',
    content: '刘涛 上传了 A1 池体表照片 3 张',
    createdAt: '2024-12-13 16:01:44'
  },
  {
    id: 'L-004',
    content: '陈晓雯 完成了罗非鱼饲料配方试验结题归档',
    createdAt: '2024-12-14 09:20:11'
  },
  {
    id: 'L-005',
    content: '王敏 更新了大黄鱼白点病治疗方案实验数据',
    createdAt: '2024-12-14 11:05:33'
  },
  {
    id: 'L-006',
    content: '系统管理员 创建了水质调控实验模板',
    createdAt: '2024-12-15 08:40:02'
  },
  {
    id: 'L-007',
    content: '赵磊 导出了 2024 年第三季度分析报告',
    createdAt: '2024-12-15 16:22:19'
  },
  {
    id: 'L-008',
    content: '刘涛 停止了凡纳滨对虾温控育苗实验',
    createdAt: '2024-12-16 10:11:47'
  }
]

function now() {
  return new Date().toISOString().replace('T', ' ').slice(0, 19)
}

function today() {
  return new Date().toISOString().slice(0, 10)
}

function addLog(content) {
  logs.unshift({
    id: `L-${Date.now()}`,
    content,
    createdAt: now()
  })
}

function getRoles() {
  return roles
}

function listUsers() {
  return users
}

function createUser(payload) {
  const item = {
    id: `U-${Date.now()}`,
    name: payload.name,
    email: payload.email,
    role: payload.role,
    department: payload.department,
    createdAt: today()
  }
  users.unshift(item)
  addLog(`系统管理员 添加了用户 '${item.name}'`)
  return item
}

function updateUser(id, payload) {
  const index = users.findIndex(item => item.id === id)
  if (index === -1) return null
  users[index] = { ...users[index], ...payload, id }
  addLog(`系统管理员 更新了用户 '${users[index].name}'`)
  return users[index]
}

function deleteUser(id) {
  const index = users.findIndex(item => item.id === id)
  if (index === -1) return false
  const name = users[index].name
  users.splice(index, 1)
  addLog(`系统管理员 删除了用户 '${name}'`)
  return true
}

function listLogs() {
  return logs
}

module.exports = {
  getRoles,
  listUsers,
  createUser,
  updateUser,
  deleteUser,
  listLogs
}
