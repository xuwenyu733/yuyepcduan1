/**
 * 聊天「数据录入」各业务模块的前端逻辑集中在此文件，便于：
 * - 与 PC 原页面（如 zkPondFishDetail、zkFeedingLog）字段/联动保持一致
 * - 多模块复用（投放、投喂、用药等分别 export 配置）
 *
 * 说明：
 * - 后端 Python 仍可下发草稿与字段元数据；**下拉数据、默认值、联动计算以本文件为准**
 * - `fid`：与 PC 一致，为业务侧生成的唯一键（非上传文件 id），创建时用 UUID 去横线
 */

import { listForBatch } from '@/api/erp/purchaseIn'
import { getZkPondPage } from '@/api/fish/zkPond'
import { listSimpleUsersByRole } from '@/api/system/user'
import { listSimpleDepts } from '@/api/system/dept'
import { getProductPage } from '@/api/erp/product'
import { getWarehousePage } from '@/api/erp/warehouse'
import { getStockByProductWarehouse } from '@/api/erp/stock'
import { getZkWorkshopPage } from '@/api/fish/zkWorkshop'
import { getZkPondSystemPage } from '@/api/fish/zkPondSystem'
import { getZkPondFishDetailPage } from '@/api/fish/zkPondFishDetail'
import { getZkFeedingLogPage } from '@/api/fish/zkFeedingLog'
import { KE_DE_ROLES } from '@/utils/constants'
import { getUUID } from '@/utils'

// =========================
// 下拉 options 全局缓存（跨气泡/跨组件）
// - 同一 key（含参数）只请求一次
// - in-flight 去重：并发触发复用同一 Promise
// - TTL 过期才刷新
// =========================
const _optCache = new Map()

function _stableStringify(obj) {
  if (!obj || typeof obj !== 'object') return String(obj)
  const keys = Object.keys(obj).sort()
  const out = {}
  keys.forEach((k) => { out[k] = obj[k] })
  return JSON.stringify(out)
}

async function _cached(key, fetcher, ttlMs = 10 * 60 * 1000) {
  const now = Date.now()
  const hit = _optCache.get(key)
  if (hit) {
    if (hit.data && hit.at && (now - hit.at) < ttlMs) return hit.data
    if (hit.promise) return hit.promise
  }
  const p = Promise.resolve()
    .then(fetcher)
    .then((data) => {
      _optCache.set(key, { data, at: Date.now() })
      return data
    })
    .catch((e) => {
      _optCache.delete(key)
      throw e
    })
  _optCache.set(key, { promise: p, at: now })
  return p
}

function toFiniteNumber(v) {
  if (v === null || v === undefined || v === '') return NaN
  if (typeof v === 'number') return Number.isFinite(v) ? v : NaN
  const n = parseFloat(String(v).replace(/,/g, '').trim())
  return Number.isFinite(n) ? n : NaN
}

/** 从类似 `10g/尾` / `10 g/尾` / `10` 中提取数字 */
export function parseSpecNumber(specStr) {
  const s = String(specStr || '').trim()
  if (!s) return NaN
  const m = s.match(/-?\d+(\.\d+)?/)
  if (!m) return NaN
  const n = parseFloat(m[0])
  return Number.isFinite(n) ? n : NaN
}

/** 投放记录新增：与 PC `zkPondFishDetail/index.vue` 对齐的批次查询参数 */
export function buildPurchaseInListForBatchParams() {
  return {
    pageNo: 1,
    pageSize: 200,
    orderNo: null,
    outKind: '1',
    productName: null,
    userId: null,
    categoryId: '7',
    createTime: [],
    oType: 1
  }
}

export async function loadBatchOptionsForStocking() {
  const params = buildPurchaseInListForBatchParams()
  const key = `stocking:batch:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await listForBatch(params)
    const list = res.data || []
    list.forEach((item) => {
      if (item.remainingInventory === -1) {
        item.remainingInventory = item.productCount
      }
      item.info = `${item.relationOrderNo}(${item.productName}-${item.productBrand}-${item.supplierName}-待投放${item.remainingInventory}条)`
    })
    return list.filter((obj) => obj.remainingInventory > 0)
  })
}

export async function loadPondOptionsForStocking() {
  const params = { pageNo: 1, pageSize: 500 }
  const key = `stocking:pond:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getZkPondPage(params)
    const list = res.data.list || []
    return list.filter((obj) => obj.fishNum > 0)
  })
}

export async function loadUserOptionsForStocking() {
  const key = `common:userByRole:${_stableStringify({ roles: KE_DE_ROLES })}`
  return _cached(key, async () => {
    const res = await listSimpleUsersByRole(KE_DE_ROLES)
    const list = res.data || []
    list.forEach((obj) => {
      obj.nickname = `${obj.deptName}-${obj.nickname}`
    })
    return list
  })
}

export async function loadDeptOptionsSimple() {
  const key = `common:deptSimple:${_stableStringify({})}`
  return _cached(key, async () => {
    const res = await listSimpleDepts()
    const list = (res && res.data) || []
    return list
  })
}

export async function loadWorkshopOptions() {
  const params = { pageNo: 1, pageSize: 500 }
  const key = `common:workshop:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getZkWorkshopPage(params)
    const list = (res && res.data && res.data.list) ? res.data.list : []
    return list
  })
}

export function applyWorkshopToRow(row, workshopItem) {
  if (!row || !workshopItem) return
  row.workshopId = workshopItem.id
  row.workshopName = workshopItem.workshopName
}

export async function loadPondSystemOptions() {
  const params = { pageNo: 1, pageSize: 500 }
  const key = `common:pondSystem:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getZkPondSystemPage(params)
    const list = (res && res.data && res.data.list) ? res.data.list : []
    return list
  })
}

/** 存塘：对齐 PC `updatePondSystemId` 联动字段 */
export function applyPondSystemToPondRow(row, pondSystemItem) {
  if (!row || !pondSystemItem) return
  // PC 逻辑：选择系统后，存塘编号默认填 `${systemNum}-`
  row.pondDetailNum = `${pondSystemItem.systemNum}-`
  row.workshopName = pondSystemItem.workshopName
  row.workshopId = pondSystemItem.workshopId
  row.pondNum = pondSystemItem.systemNum
  row.pondSystemId = pondSystemItem.id
  row.pondSystemName = pondSystemItem.systemName
}

export async function loadReviseBizOptions(reviseType) {
  const rt = reviseType != null ? String(reviseType) : ''
  if (!rt) return []
  const baseParams = { pageNo: 1, pageSize: 500 }
  const key = `revise:biz:${_stableStringify({ rt })}`
  return _cached(key, async () => {
    // 1:投放 2:死亡 3:分池 -> zkPondFishDetailPage(addType=1/-2/-3)
    if (rt === '1' || rt === '2' || rt === '3') {
      const addType = rt === '1' ? '1' : (rt === '2' ? '-2' : '-3')
      const res = await getZkPondFishDetailPage({ ...baseParams, addType })
      return (res && res.data && res.data.list) ? res.data.list : []
    }
    // 4:投喂 -> zkFeedingLogPage
    if (rt === '4') {
      const res = await getZkFeedingLogPage(baseParams)
      return (res && res.data && res.data.list) ? res.data.list : []
    }
    // 5:存塘 -> zkPondPage
    if (rt === '5') {
      const res = await getZkPondPage(baseParams)
      return (res && res.data && res.data.list) ? res.data.list : []
    }
    return []
  }, 3 * 60 * 1000)
}

export function computeReviseChangeRatio(differenceData, originData) {
  const a = toFiniteNumber(differenceData)
  const b = toFiniteNumber(originData)
  if (!Number.isFinite(a) || !Number.isFinite(b)) return null
  const denom = b === 0 ? 1 : b
  const v = a / denom
  return Math.round(v * 100) / 100
}

/** 修正记录：选中业务记录后，对齐 PC `updateOther + update_xxx` 的回填字段 */
export function applyReviseBizItemToRow(row, reviseType, bizItem) {
  if (!row || !bizItem) return
  const rt = reviseType != null ? String(reviseType) : ''
  // 清理旧的类型对象，避免切换操作类型后残留导致后端判断异常
  row.fishPush = null
  row.fishDead = null
  row.fishSplit = null
  row.feedingLog = null
  row.pondData = null
  // 统一：busId/pondId 都用该业务记录 id（与 PC 一致）
  row.busId = bizItem.id
  row.pondId = bizItem.id
  // 快照字段（投放/死亡/分池/投喂/存塘基本都具备这些字段）
  row.workshopName = bizItem.workshopName
  row.workshopId = bizItem.workshopId
  row.pondSystemName = bizItem.pondSystemName
  row.pondSystemId = bizItem.pondSystemId
  row.pondNum = bizItem.pondNum
  row.pondName = bizItem.pondName
  row.pondDetailNum = bizItem.pondDetailNum
  // 负责人快照
  row.userName = bizItem.userName
  row.userId = bizItem.userId
  // 存塘规模/鱼数（有的记录会带）
  if (bizItem.pondScale != null) row.pondScale = bizItem.pondScale
  if (bizItem.fishNum != null) row.fishNum = bizItem.fishNum

  // originData：不同类型取值不同
  if (rt === '4') {
    row.originData = bizItem.feedCount
  } else {
    row.originData = bizItem.fishNum
  }

  // 关键：对齐 PC create payload（reviseType 不同需要挂不同对象，否则后端会报“xxx数据为空”）
  // 1:投放 2:死亡 3:分池 4:投喂 5:存塘
  if (rt === '1') row.fishPush = bizItem
  if (rt === '2') row.fishDead = bizItem
  if (rt === '3') row.fishSplit = bizItem
  if (rt === '4') row.feedingLog = bizItem
  if (rt === '5') row.pondData = bizItem

  // 若已有差值，联动变化比例
  const cr = computeReviseChangeRatio(row.differenceData, row.originData)
  if (cr != null) row.changeRatio = cr
}

export async function loadProductOptionsForStocking() {
  const params = { pageNo: 1, pageSize: 500, categoryId: 7 }
  const key = `stocking:product:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getProductPage(params)
    const list = res.data.list || []
    list.forEach((obj) => {
      obj.info = `${obj.productName}-${obj.stockInfo ? obj.stockInfo : '无库存'}`
    })
    return list
  })
}

/**
 * 选中批次后：同步 PC `updateBatchNumerId` 核心字段（库存、productNo、规格等）
 * @param {object} row 表格单行草稿
 * @param {object} batchItem erp purchase-in 明细项
 */
export function applyStockingBatchToRow(row, batchItem) {
  if (!batchItem) return
  row.batchNumber = batchItem.relationOrderNo
  row.supplier = batchItem.supplierName
  row.supplierAddress = batchItem.supplierAddress
  row.supplierId = batchItem.supplierId
  row.productNo = batchItem.productNo
  // 对齐 PC：批次带出鱼苗名称/品种（若接口有字段则写入，避免落库为 NULL）
  if (batchItem.productName) row.fishName = batchItem.productName
  if (batchItem.fishTypes) row.fishTypes = batchItem.fishTypes
  row.fishSpec = batchItem.standard || row.fishSpec
  row.pondDetailNum = batchItem.pondDetailNum
  row.pondScale = batchItem.pondScale
  const rem = batchItem.remainingInventory === -1 ? batchItem.productCount : batchItem.remainingInventory
  // remainingInventory 在 PC 提交时会按本次投放数量扣减；这里先记住“原始库存”用于前端实时展示
  const base = rem != null ? Number(rem) : NaN
  if (Number.isFinite(base)) {
    row._remainingInventoryBase = base
    row.remainingInventory = base
  }
}

/**
 * 选中鱼苗种类后：同步 PC `updateProductId` 核心字段（fishName/fishTypes/fishSpec）
 * @param {object} row
 * @param {object} productItem erp product 行
 */
export function applyStockingProductToRow(row, productItem) {
  if (!row || !productItem) return
  row.fishName = productItem.productName
  row.fishTypes = productItem.fishTypes
  row.fishSpec = productItem.standard || row.fishSpec
  row.productNo = productItem.productNo || row.productNo
}

/** 选中投放人后：同步 PC `updateUserId` 核心字段（deptId/userName/tenantId） */
export function applyStockingUserToRow(row, userItem) {
  if (!row || !userItem) return
  row.tenantId = userItem.tenantId
  row.userId = userItem.id
  row.deptId = userItem.deptId
  row.userName = userItem.nickname
}

/**
 * 选中鱼池后：同步 PC `updatePondId` 核心字段
 * @param {object} row
 * @param {object} pondItem zkPond 行
 */
export function applyStockingPondToRow(row, pondItem) {
  if (!pondItem) return
  row.workshopName = pondItem.workshopName
  row.workshopId = pondItem.workshopId
  row.pondName = pondItem.pondName
  row.pondNum = pondItem.pondNum
  row.pondScale = pondItem.pondScale
  // 对齐 PC：选中鱼池后把“历史鱼数”落在 historyFishNum，供 fishNum 输入时实时计算 currFishNum
  row.historyFishNum = pondItem.fishNum
  row.pondId = pondItem.id
  // 若接口返回存塘鱼苗表 id，则一并带上（后端 VO 字段名：zkPondFishId）
  if (pondItem.zkPondFishId) row.zkPondFishId = pondItem.zkPondFishId
  if (pondItem.zkPondFishDO && pondItem.zkPondFishDO.id) row.zkPondFishId = pondItem.zkPondFishDO.id
  row.pondDetailNum = pondItem.pondDetailNum
  row.pondSystemName = pondItem.pondSystemName
  row.pondSystemId = pondItem.pondSystemId

  // 选池后如果已经填了投放数量，立刻补一次“投放后存塘鱼数”，避免界面看起来没联动
  const addNum = toFiniteNumber(row.fishNum)
  const hist = toFiniteNumber(row.historyFishNum)
  if (Number.isFinite(addNum) && Number.isFinite(hist)) {
    row.currFishNum = parseInt(hist, 10) + parseInt(addNum, 10)
  }
}

/**
 * 投放记录：前端实时联动（对齐 PC）
 * - 输入投放数量 fishNum：currFishNum = historyFishNum + fishNum
 * - 若 fishSpec 可解析为 g/尾：pondWeight(kg) = fishNum * spec(g) / 1000
 * - 批次剩余 remainingInventory：= _remainingInventoryBase - fishNum（仅展示用）
 */
export function recomputeStockingDerivedFields(row, changedKey) {
  if (!row) return
  if (changedKey === 'fishNum' || changedKey === 'historyFishNum' || changedKey === 'pondId') {
    const addNum = toFiniteNumber(row.fishNum)
    const hist = toFiniteNumber(row.historyFishNum)
    const addInt = Number.isFinite(addNum) ? parseInt(addNum, 10) : NaN
    const histInt = Number.isFinite(hist) ? parseInt(hist, 10) : NaN
    const addType = String(row.addType || '1')
    if (Number.isFinite(addInt) && Number.isFinite(histInt)) {
      // 对齐 PC：投放(1) 为加；死亡(-2) 为减；其余类型先按加（后续再按 PC 补齐）
      if (addType === '-2') row.currFishNum = histInt - addInt
      else row.currFishNum = histInt + addInt
    } else if (Number.isFinite(histInt) && (row.fishNum === null || row.fishNum === undefined || row.fishNum === '')) {
      // 未填投放数量时，投放后鱼数先展示为历史鱼数，避免空白
      row.currFishNum = histInt
    }
    // 批次剩余数量：展示为扣减后的（与 PC 提交前处理一致）
    const base = toFiniteNumber(row._remainingInventoryBase)
    if (Number.isFinite(base) && Number.isFinite(addNum)) {
      row.remainingInventory = Math.max(0, parseInt(base, 10) - parseInt(addNum, 10))
    } else if (Number.isFinite(base) && (row.fishNum === null || row.fishNum === undefined || row.fishNum === '')) {
      row.remainingInventory = parseInt(base, 10)
    }
    // 规格推重量（kg）
    const specG = parseSpecNumber(row.fishSpec)
    if (Number.isFinite(specG) && Number.isFinite(addNum)) {
      const kg = (specG * addNum) / 1000
      // 保留 3 位小数，避免太长
      row.pondWeight = Math.round(kg * 1000) / 1000
    }
    // 若用户直接填了 pondWeight，但没填 fishSpec，则不覆盖 pondWeight（保持用户输入）
  }

  if (changedKey === 'pondWeight') {
    const kg = toFiniteNumber(row.pondWeight)
    const addNum = toFiniteNumber(row.fishNum)
    if (Number.isFinite(kg) && Number.isFinite(addNum) && addNum > 0) {
      const gPerTail = (kg * 1000) / addNum
      const rounded = Math.round(gPerTail * 1000) / 1000
      // 对齐 PC：fishSpec 提交的是纯数值（如 10.000），不拼接单位；展示时由表头“(g/尾)”表达单位
      row.fishSpec = String(rounded)
    }
  }
}

/**
 * 投放记录草稿默认值：时间=当前（毫秒时间戳）、fid=uuid
 */
export function ensureZkPondFishDetailAddDefaults(row) {
  if (!row.createTime) {
    row.createTime = Date.now()
  }
  if (!row.fid) {
    row.fid = getUUID().replace(/-/g, '')
  }
  if (row.addType === undefined || row.addType === null || row.addType === '') {
    row.addType = '1'
  }
  // Vue2 响应式限制：需要提前把可能联动写入的字段挂到 row 上，否则后续新增属性不触发界面更新
  const reactiveKeys = [
    'historyFishNum',
    'currFishNum',
    'pondWeight',
    'remainingInventory',
    '_remainingInventoryBase'
  ]
  reactiveKeys.forEach((k) => {
    if (!(k in row)) row[k] = null
  })
  // 兼容：如果后端/草稿把历史鱼数放在 fishNum（旧字段），这里尽量不破坏用户输入
  if (row.historyFishNum === undefined || row.historyFishNum === null || row.historyFishNum === '') {
    if (row.fishNum !== undefined && row.fishNum !== null && row.fishNum !== '' && (row.currFishNum === undefined || row.currFishNum === null || row.currFishNum === '')) {
      // 不自动挪字段，避免误伤；只在明确没有 pondId 时才当作历史鱼数
    }
  }
}

/**
 * ============================
 * 投喂记录（zkFeedingLog）
 * 对齐 PC：src/views/fish/zkFeedingLog/index.vue
 * - 选存塘 → 回填 workshop/pond 基础信息
 * - 选饲料(productNo) → 回填 productId/feedName/feedBrand/feedSpec/feedingPrice
 * - 输入 feedCount → feedingCost = feedCount * feedingPrice（若没选饲料则不计算）
 * - 时间默认当前（毫秒时间戳）
 * ============================
 */
export async function loadPondOptionsForFeeding() {
  const params = { pageNo: 1, pageSize: 500 }
  const key = `feeding:pond:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getZkPondPage(params)
    const list = res.data.list || []
    return list.filter((obj) => obj.fishNum > 0)
  })
}

export async function loadUserOptionsForFeeding() {
  // 与投放复用：同一套角色用户列表
  return loadUserOptionsForStocking()
}

export async function loadProductOptionsForFeeding() {
  const params = { pageNo: 1, pageSize: 500, categoryId: 1 }
  const key = `feeding:product:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getProductPage(params)
    const list = res.data.list || []
    list.forEach((obj) => {
      obj.info = `${obj.productName}-${obj.stockInfo ? obj.stockInfo : '无库存'}`
    })
    return list
  })
}

export async function loadProductOptionsForMedicine() {
  const params = { pageNo: 1, pageSize: 500, categoryId: 2 }
  const key = `medicine:product:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getProductPage(params)
    const list = res.data.list || []
    list.forEach((obj) => {
      obj.info = `${obj.productName}-${obj.productBrand}`
    })
    return list
  })
}

export function applyMedicineProductToLogRow(logRow, productItem) {
  if (!logRow || !productItem) return
  logRow.medicineName = productItem.productName
  logRow.medicineBrand = productItem.productBrand
  logRow.medicineSpec = productItem.productSpec
  logRow.medicinePrice = productItem.purchasePrice
  // medicineCost 由前端输入用量后计算（与 PC 一致）
}

export async function loadWarehouseOptionsForFeeding() {
  const params = { pageNo: 1, pageSize: 200 }
  const key = `feeding:warehouse:${_stableStringify(params)}`
  return _cached(key, async () => {
    const res = await getWarehousePage(params)
    const list = res.data.list || []
    return list
  })
}

export function ensureZkPondFishTypeAddDefaults(row) {
  if (!row) return
  // 统计年月日：默认今天
  const d = new Date()
  if (!row.staticYear) row.staticYear = d.getFullYear()
  if (!row.staticMonth) row.staticMonth = d.getMonth() + 1
  if (!row.staticDay) row.staticDay = d.getDate()
}

export function ensureZkUseMedicineAddDefaults(row) {
  if (!row) return
  if (!row.createTime) row.createTime = Date.now()
  if (!row.stopMedicationTime) row.stopMedicationTime = Date.now()
  // Vue2 响应式：提前挂载联动字段
  const reactiveKeys = [
    'pondId',
    'pondDetailNum',
    'pondName',
    'pondNum',
    'pondScale',
    'fishNum',
    'workshopId',
    'workshopName',
    'pondSystemName',
    'userId',
    'userName',
    'deptId',
    'tenantId',
    'stopMedicationTime',
    'numberOfLosses',
    'specifications',
    'useMedicineLogList',
    'taskParam',
  ]
  reactiveKeys.forEach((k) => {
    if (!(k in row)) row[k] = null
  })
  if (!Array.isArray(row.useMedicineLogList)) row.useMedicineLogList = []
  // Java 侧会直接读取 taskParam.processKey 发起流程，必须兜底
  if (!row.taskParam || typeof row.taskParam !== 'object') {
    row.taskParam = {
      processKey: 'apply_for_medication',
      submitToPrev: 'y',
      finishProcess: 'n',
      variables: {},
    }
  } else if (!row.taskParam.processKey) {
    row.taskParam.processKey = 'apply_for_medication'
  }
}

export async function getFeedingStockSnapshot(productId, warehouseId) {
  const pid = productId != null ? String(productId) : ''
  const wid = warehouseId != null ? String(warehouseId) : ''
  if (!pid || !wid) return null
  const key = `feeding:stock:${pid}:${wid}`
  // 库存变化快：TTL 取短一点，避免误判（仍支持 in-flight 去重）
  return _cached(
    key,
    async () => {
      const res = await getStockByProductWarehouse(Number(pid), Number(wid))
      return (res && res.data) || null
    },
    30 * 1000
  )
}

export function applyFeedingPondToRow(row, pondItem) {
  if (!row || !pondItem) return
  row.workshopName = pondItem.workshopName
  row.workshopId = pondItem.workshopId
  row.pondName = pondItem.pondName
  row.pondNum = pondItem.pondNum
  row.pondScale = pondItem.pondScale
  row.fishNum = pondItem.fishNum
  row.pondId = pondItem.id
  row.pondDetailNum = pondItem.pondDetailNum
  row.pondSystemName = pondItem.pondSystemName
  row.pondSystemId = pondItem.pondSystemId
  // PC 里还会回填默认负责人；聊天里让用户选 userId 即可
}

export function applyFeedingProductToRow(row, productItem) {
  if (!row || !productItem) return
  row.feedName = productItem.productName
  row.feedBrand = productItem.productBrand
  row.feedSpec = productItem.standard
  row.productNo = productItem.productNo
  row.productId = productItem.id
  row.feedingPrice = productItem.purchasePrice
  recomputeFeedingDerivedFields(row, 'feedCount')
}

export function applyFeedingUserToRow(row, userItem) {
  if (!row || !userItem) return
  row.deptId = userItem.deptId
  row.tenantId = userItem.tenantId
  row.userName = userItem.nickname
  row.userId = userItem.id
}

export function recomputeFeedingDerivedFields(row, changedKey) {
  if (!row) return
  if (changedKey === 'feedCount' || changedKey === 'feedingPrice') {
    const c = toFiniteNumber(row.feedCount)
    const p = toFiniteNumber(row.feedingPrice)
    if (Number.isFinite(c) && Number.isFinite(p)) {
      const cost = c * p
      row.feedingCost = Math.round(cost * 10000) / 10000
    }
  }
}

export function ensureZkFeedingLogAddDefaults(row) {
  if (!row.feedTime) {
    row.feedTime = Date.now()
  }
  // Vue2 响应式限制：提前挂载可能被联动写入的字段
  const reactiveKeys = [
    'workshopId',
    'workshopName',
    'pondSystemId',
    'pondSystemName',
    'pondDetailNum',
    'pondName',
    'pondNum',
    'pondScale',
    'fishNum',
    'productNo',
    'productId',
    'feedName',
    'feedBrand',
    'feedSpec',
    'feedingPrice',
    'feedingCost',
    'warehouseId',
    'userId',
    'userName',
    'deptId',
    'tenantId',
  ]
  reactiveKeys.forEach((k) => {
    if (!(k in row)) row[k] = null
  })
}
