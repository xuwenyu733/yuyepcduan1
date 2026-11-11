import request from "@/utils/request";

function normalizeModelPage(res, query) {
  const data = res && res.data
  if (data && Array.isArray(data.list)) {
    return res
  }
  const list = Array.isArray(data) ? data : []
  const pageNo = Number((query && query.pageNo) || 1)
  const pageSize = Number((query && query.pageSize) || 10)
  const start = Math.max(0, (pageNo - 1) * pageSize)
  res.data = {
    list: list.slice(start, start + pageSize),
    total: list.length
  }
  return res
}

export function getModelPage(query) {
  const q = Object.assign({}, query || {})
  const pageNo = q.pageNo
  const pageSize = q.pageSize
  delete q.pageNo
  delete q.pageSize
  return request({
    url: "/bpm/model/list",
    method: "get",
    params: q,
  }).then((res) => normalizeModelPage(res, { pageNo, pageSize }))
}

export function getModel(id) {
  return request({
    url: "/bpm/model/get?id=" + id,
    method: "get",
  });
}

export function updateModel(data) {
  return request({
    url: "/bpm/model/update",
    method: "PUT",
    data: data,
  });
}

// 任务状态修改
export function updateModelState(id, state) {
  return request({
    url: "/bpm/model/update-state",
    method: "put",
    data: {
      id,
      state,
    },
  });
}

export function createModel(data) {
  return request({
    url: "/bpm/model/create",
    method: "POST",
    data: data,
  });
}

export function deleteModel(id) {
  return request({
    url: "/bpm/model/delete?id=" + id,
    method: "DELETE",
  });
}

export function deployModel(id) {
  return request({
    url: "/bpm/model/deploy?id=" + id,
    method: "POST",
  });
}
