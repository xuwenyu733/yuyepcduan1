import request from '@/utils/request'
import { uploadFile } from '@/api/infra/file'

// 获得文档分页
export function getDocumentPage (query) {
  return request({
    url: '/fish/zk-document/page',
    method: 'get',
    params: query
  })
}

// 上传文档
export function uploadDocument (data) {
  return request({
    url: '/fish/zk-document/upload',
    method: 'post',
    data
  })
}

// 上传文件（使用现有的文件上传接口）
export function uploadDocumentFile (file, directory = 'oa/document') {
  return uploadFile(file, directory)
}

// 获得文档详情
export function getDocumentDetail (id) {
  return request({
    url: `/fish/zk-document/${id}`,
    method: 'get'
  })
}

// 删除文档
export function deleteDocument (id) {
  return request({
    url: `/fish/zk-document/${id}`,
    method: 'delete'
  })
}

// 更新文档状态（共享/私有）
export function updateDocumentStatus (id, status) {
  return request({
    url: `/fish/zk-document/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获得文档统计
export function getDocumentStatistics () {
  return request({
    url: '/fish/zk-document/statistics',
    method: 'get'
  })
}

// 获得WPS编辑链接
export function getWPSEditLink (documentId) {
  return request({
    url: `/fish/zk-document/${documentId}/wps-link`,
    method: 'post',
    params: {
      mode: 'edit' // 编辑模式
    }
  })
}

// 获得WPS查看链接
export function getWPSViewLink (documentId) {
  return request({
    url: `/fish/zk-document/${documentId}/wps-link`,
    method: 'post',
    params: {
      mode: 'view' // 查看模式
    }
  })
}

