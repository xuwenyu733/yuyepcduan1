import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

// 创建巡检智能分析报告
export function createZkInspectionReport(data) {
  return request({
    url: '/fish/zk-inspection-report/create',
    method: 'post',
    data: data
  })
}

// 更新巡检智能分析报告
export function updateZkInspectionReport(data) {
  return request({
    url: '/fish/zk-inspection-report/update',
    method: 'put',
    data: data
  })
}

// 删除巡检智能分析报告
export function deleteZkInspectionReport(id) {
  return request({
    url: '/fish/zk-inspection-report/delete?id=' + id,
    method: 'delete'
  })
}

// 获得巡检智能分析报告
export function getZkInspectionReport(id) {
  return request({
    url: '/fish/zk-inspection-report/get?id=' + id,
    method: 'get'
  })
}

// 获得巡检智能分析报告分页
export function getZkInspectionReportPage(query) {
  return request({
    url: '/fish/zk-inspection-report/page',
    method: 'get',
    params: withUserDeptParams(query || {})
  })
}

// 导出巡检智能分析报告 Excel
export function exportZkInspectionReportExcel(query) {
  return request({
    url: '/fish/zk-inspection-report/export-excel',
    method: 'get',
    params: withUserDeptParams(query || {}),
    responseType: 'blob'
  })
}
