import request from '@/utils/request_jidi'

export function createZkEnergyMeterReading(data) {
  return request({
    url: '/fish/zk-energy-meter-reading/create',
    method: 'post',
    data
  })
}

export function updateZkEnergyMeterReading(data) {
  return request({
    url: '/fish/zk-energy-meter-reading/update',
    method: 'put',
    data
  })
}

export function deleteZkEnergyMeterReading(id) {
  return request({
    url: '/fish/zk-energy-meter-reading/delete?id=' + id,
    method: 'delete'
  })
}

export function getZkEnergyMeterReading(id) {
  return request({
    url: '/fish/zk-energy-meter-reading/get?id=' + id,
    method: 'get'
  })
}

export function getZkEnergyMeterReadingPage(query) {
  return request({
    url: '/fish/zk-energy-meter-reading/page',
    method: 'get',
    params: query
  })
}
