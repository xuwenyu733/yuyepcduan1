import request from '@/utils/request_jidi'

// 创建他人打分
export function createOaUserRating (data) {
  return request({
    url: '/fish/oa-user-rating/create',
    method: 'post',
    data
  })
}

// 更新他人打分
export function updateOaUserRating (data) {
  return request({
    url: '/fish/oa-user-rating/update',
    method: 'put',
    data
  })
}

// 删除他人打分
export function deleteOaUserRating (id) {
  return request({
    url: '/fish/oa-user-rating/delete',
    method: 'delete',
    params: { id }
  })
}

// 获取他人打分详情
export function getOaUserRating (id) {
  return request({
    url: '/fish/oa-user-rating/get',
    method: 'get',
    params: { id }
  })
}

// 获取他人打分分页
export function getOaUserRatingPage (params) {
  return request({
    url: '/fish/oa-user-rating/page',
    method: 'get',
    params
  })
}

// 获取可评分人员列表
export function getRateableUsers () {
  return request({
    url: '/fish/oa-user-rating/rateable-users',
    method: 'get'
  })
}



