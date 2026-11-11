import request from '@/utils/request_jidi'

// 创建排班用户
export function createZkSchedulingUser(data) {
  return request({
    url: '/fish/zk-scheduling-user/create',
    method: 'post',
    data: data
  })
}

// 更新排班用户
export function updateZkSchedulingUser(data) {
  return request({
    url: '/fish/zk-scheduling-user/update',
    method: 'put',
    data: data
  })
}

// 删除排班用户
export function deleteZkSchedulingUser(id) {
  return request({
    url: '/fish/zk-scheduling-user/delete?id=' + id,
    method: 'delete'
  })
}

// 获得排班用户
export function getZkSchedulingUser(id) {
  return request({
    url: '/fish/zk-scheduling-user/get?id=' + id,
    method: 'get'
  })
}

// 获得排班用户分页
export function getZkSchedulingUserPage(query) {
  return request({
    url: '/fish/zk-scheduling-user/page',
    method: 'get',
    params: query
  })
}

// 导出排班用户 Excel
export function exportZkSchedulingUserExcel(query) {
  return request({
    url: '/fish/zk-scheduling-user/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
