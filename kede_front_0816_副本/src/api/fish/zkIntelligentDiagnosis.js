import request from '@/utils/request_jidi'

// 创建智能诊断
export function createZkIntelligentDiagnosis(data) {
  return request({
    url: '/fish/zk-intelligent-diagnosis/create',
    method: 'post',
    data: data
  })
}

// 更新智能诊断
export function updateZkIntelligentDiagnosis(data) {
  return request({
    url: '/fish/zk-intelligent-diagnosis/update',
    method: 'put',
    data: data
  })
}

// 删除智能诊断
export function deleteZkIntelligentDiagnosis(id) {
  return request({
    url: '/fish/zk-intelligent-diagnosis/delete?id=' + id,
    method: 'delete'
  })
}

// 获得智能诊断
export function getZkIntelligentDiagnosis(id) {
  return request({
    url: '/fish/zk-intelligent-diagnosis/get?id=' + id,
    method: 'get'
  })
}

// 获得智能诊断分页
export function getZkIntelligentDiagnosisPage(query) {
  return request({
    url: '/fish/zk-intelligent-diagnosis/page',
    method: 'get',
    params: query
  })
}

// 导出智能诊断 Excel
export function exportZkIntelligentDiagnosisExcel(query) {
  return request({
    url: '/fish/zk-intelligent-diagnosis/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
