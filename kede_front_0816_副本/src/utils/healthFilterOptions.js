/**
 * 与「鱼类健康状态」同一套筛选项：
 * 车间 / 系统 / 池号来自 loadSharedSiteOptions；
 * 品种 / 批次来自精准投喂鱼池主档（getPrecisionFeedingPonds）。
 */
import {
  pickWorkshopOptionLabels,
  pickSystemOptionLabels,
  pickPondOptionLabels,
  pickWorkshopOptionItems,
  pickSystemOptionItems,
  pickPondOptionItems
} from '@/utils/siteOptions'
import { collectBatchFilterOptions } from '@/utils/batchTokens'
import { normalizeSpeciesName, collectSpeciesFilterOptions } from '@/utils/speciesTempBridge'

export function healthWorkshopOptions(siteOptions) {
  return pickWorkshopOptionLabels(siteOptions)
}

export function healthSystemOptions(siteOptions, workshopLabel) {
  return pickSystemOptionLabels(siteOptions, workshopLabel || '')
}

export function healthPondOptions(siteOptions, workshopLabel, systemLabel) {
  return pickPondOptionLabels(siteOptions, workshopLabel || '', systemLabel || '')
}

/** 与健康/投喂/生长页同一套品种筛选项 */
export function healthSpeciesOptions(ponds) {
  return collectSpeciesFilterOptions(ponds)
}

/** 与健康页 batchOptions 相同：主档批次拆开、去重 */
export function healthBatchOptions(ponds) {
  return collectBatchFilterOptions(ponds || [], (p) => p.batchNumber || p.batchNo || '')
}

export function mapHealthPondMaster(list) {
  return (list || []).map((p) => ({
    id: p.id,
    pondName: p.pondName,
    pondDetailNum: p.pondDetailNum,
    workshopId: p.workshopId,
    workshopName: p.workshopName,
    pondSystemId: p.pondSystemId,
    pondSystemName: p.pondSystemName || p.pondNum,
    pondNum: p.pondNum,
    fishName: p.fishTypeName || p.fishName,
    fishTypes: p.fishTypes,
    batchNumber: p.batchNumber,
    fishSpec: p.fishSpec
  }))
}

/**
 * 把健康页同款文案筛选项还原成死亡接口使用的 id / 编号。
 */
export function mapHealthFiltersToQuery(siteOptions, ponds, filters) {
  const workshop = (filters && filters.workshop) || ''
  const system = (filters && filters.system) || ''
  const pond = (filters && filters.pond) || ''
  const q = {
    workshopId: null,
    pondSystemId: null,
    pondNum: null,
    pondDetailNum: null,
    pondId: null
  }
  const wOpt = pickWorkshopOptionItems(siteOptions).find(
    (o) => o && (o.label === workshop || o.name === workshop)
  )
  if (wOpt) q.workshopId = wOpt.id != null ? wOpt.id : wOpt.value

  const sOpt = pickSystemOptionItems(siteOptions, workshop).find(
    (o) => o && (o.label === system || o.name === system)
  )
  if (sOpt) {
    q.pondSystemId = sOpt.id != null ? sOpt.id : sOpt.value
    const row = (ponds || []).find(
      (p) =>
        String(p.pondSystemId) === String(q.pondSystemId) ||
        p.pondSystemName === (sOpt.name || sOpt.label)
    )
    if (row && row.pondNum != null && row.pondNum !== '') q.pondNum = row.pondNum
  }

  const pOpt = pickPondOptionItems(siteOptions, workshop, system).find(
    (o) => o && o.label === pond
  )
  if (pOpt) {
    q.pondDetailNum = pOpt.rawCode || pOpt.value || null
    q.pondId = pOpt.id != null ? pOpt.id : null
    if (pOpt.workshopValue) q.workshopId = pOpt.workshopValue
    const pondRow = (ponds || []).find(
      (p) =>
        (pOpt.id != null && String(p.id) === String(pOpt.id)) ||
        (q.pondDetailNum && String(p.pondDetailNum) === String(q.pondDetailNum))
    )
    if (pondRow) {
      if (pondRow.pondNum) q.pondNum = pondRow.pondNum
      if (pondRow.pondSystemId != null) q.pondSystemId = pondRow.pondSystemId
      if (pondRow.workshopId != null) q.workshopId = pondRow.workshopId
    }
  }
  return q
}

export function displayHealthWorkshop(row, siteOptions) {
  if (!row) return '-'
  const items = (siteOptions && siteOptions.workshopOptions) || []
  const hit = items.find(
    (o) =>
      (row.workshopId != null && (String(o.id) === String(row.workshopId) || String(o.value) === String(row.workshopId)))
  )
  return (hit && (hit.label || hit.name)) || row.workshopName || '-'
}

export function displayHealthSystem(row, siteOptions) {
  if (!row) return '-'
  const items = (siteOptions && siteOptions.systemOptions) || []
  const hit = items.find(
    (o) =>
      (row.pondSystemId != null && (String(o.id) === String(row.pondSystemId) || String(o.value) === String(row.pondSystemId))) ||
      (row.pondSystemName && (o.label === row.pondSystemName || o.name === row.pondSystemName))
  )
  return (hit && (hit.name || hit.label)) || row.pondSystemName || '-'
}

export function displayHealthPond(row, siteOptions) {
  if (!row) return '-'
  const items = (siteOptions && siteOptions.pondOptions) || []
  const hit = items.find(
    (o) =>
      (row.pondId != null && String(o.id) === String(row.pondId)) ||
      (row.pondDetailNum && (String(o.rawCode) === String(row.pondDetailNum) || String(o.value) === String(row.pondDetailNum)))
  )
  return (hit && hit.label) || row.pondDetailNum || row.pondName || '-'
}

export function displayHealthSpecies(row, dictLabel) {
  const label = dictLabel || (row && (row.fishName || row.fishTypeName || row.fishTypes)) || ''
  return normalizeSpeciesName(label) || label || '-'
}

export function displayHealthBatch(raw) {
  if (raw == null || raw === '') return '-'
  return String(raw)
}
