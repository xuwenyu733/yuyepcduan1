import request from '@/utils/request_jidi'

// 创建车间
export function createZkWorkshop(data) {
  return request({
    url: '/fish/zk-workshop/create',
    method: 'post',
    data: data
  })
}

// 更新车间
export function updateZkWorkshop(data) {
  return request({
    url: '/fish/zk-workshop/update',
    method: 'put',
    data: data
  })
}

// 删除车间
export function deleteZkWorkshop(id) {
  return request({
    url: '/fish/zk-workshop/delete?id=' + id,
    method: 'delete'
  })
}

// 获得车间
export function getZkWorkshop(id) {
  return request({
    url: '/fish/zk-workshop/get?id=' + id,
    method: 'get'
  })
}

// 获得车间分页
export function getZkWorkshopPage(query) {
  return request({
    url: '/fish/zk-workshop/page',
    method: 'get',
    params: query
  })
}

// 获得车间列表，用于首页大屏（本地缺表时不弹「系统异常」）
export function getZkWorkshopDashboardList() {
  return request({
    url: '/fish/zk-workshop/list-for-dashboard',
    method: 'get',
    silent: true,
    hideErrorMessage: true
  })
}

// 导出车间 Excel
export function exportZkWorkshopExcel(query) {
  return request({
    url: '/fish/zk-workshop/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
