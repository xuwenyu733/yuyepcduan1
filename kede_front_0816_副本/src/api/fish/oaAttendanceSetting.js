import request from '@/utils/request_jidi'

// 创建OA考勤设置
export function createOaAttendanceSetting (data) {
  return request({
    url: '/fish/oa-attendance-setting/create',
    method: 'post',
    data: data
  })
}

// 更新OA考勤设置
export function updateOaAttendanceSetting (data) {
  return request({
    url: '/fish/oa-attendance-setting/update',
    method: 'put',
    data: data
  })
}

// 删除OA考勤设置
export function deleteOaAttendanceSetting (id) {
  return request({
    url: '/fish/oa-attendance-setting/delete?id=' + id,
    method: 'delete'
  })
}

// 获得OA考勤设置
export function getOaAttendanceSetting (id) {
  return request({
    url: '/fish/oa-attendance-setting/get?id=' + id,
    method: 'get'
  })
}

// 获得OA考勤设置列表
export function getOaAttendanceSettingList (ids) {
  return request({
    url: '/fish/oa-attendance-setting/list?ids=' + ids.join(','),
    method: 'get'
  })
}

// 获得OA考勤设置分页
export function getOaAttendanceSettingPage (params) {
  return request({
    url: '/fish/oa-attendance-setting/page',
    method: 'get',
    params: params
  })
}

// 获得启用的OA考勤设置列表
export function getEnabledOaAttendanceSettingList () {
  return request({
    url: '/fish/oa-attendance-setting/enabled-list',
    method: 'get'
  })
}



