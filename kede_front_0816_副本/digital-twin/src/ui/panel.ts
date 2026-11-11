import {
  ALARM_LABELS,
  TYPE_FILTERS,
  TYPE_LABELS,
  deviceSummaryLine,
  type DeviceTelemetry,
  type DeviceType,
} from '../data/mockDevices'

export type PanelCallbacks = {
  onSelectDevice: (deviceId: string) => void
  onAction: (deviceId: string, action: string) => void
  /** 懒加载：切到「动态」页签时再拉历史 */
  onNeedHistory?: (deviceId: string) => void
}

type TabKey = 'detail' | 'options' | 'scene' | 'history'

export function mountPanel(el: HTMLElement, callbacks: PanelCallbacks): {
  render: (
    devices: DeviceTelemetry[],
    selectedId: string | null,
    history?: { t: string; summary: string }[],
    opts?: { soft?: boolean },
  ) => void
  getFilter: () => DeviceType | 'all'
  getTab: () => TabKey
} {
  let filter: DeviceType | 'all' = 'all'
  let tab: TabKey = 'detail'
  let lastDevices: DeviceTelemetry[] = []
  let lastSelected: string | null = null
  let lastHistory: { t: string; summary: string }[] = []

  el.innerHTML = `
    <div class="panel-header">
      <h2>养殖设备监控</h2>
      <p class="panel-hint">筛选类型 · 点击色点或列表查看详情 / 选项 / 场景</p>
    </div>
    <div class="type-filters" id="type-filters"></div>
    <ul class="device-list" id="device-list"></ul>
    <section class="detail" id="device-detail">
      <p class="detail-empty">未选择设备</p>
    </section>
  `

  const filtersEl = el.querySelector('#type-filters') as HTMLElement
  const listEl = el.querySelector('#device-list') as HTMLUListElement
  const detailEl = el.querySelector('#device-detail') as HTMLElement

  filtersEl.innerHTML = TYPE_FILTERS.map(
    (f) =>
      `<button type="button" class="filter-chip ${f.key === 'all' ? 'is-active' : ''}" data-filter="${f.key}">${f.label}</button>`,
  ).join('')

  filtersEl.addEventListener('click', (e) => {
    const btn = (e.target as HTMLElement).closest<HTMLButtonElement>('[data-filter]')
    if (!btn?.dataset.filter) return
    filter = btn.dataset.filter as DeviceType | 'all'
    filtersEl.querySelectorAll('.filter-chip').forEach((c) => c.classList.remove('is-active'))
    btn.classList.add('is-active')
    paint()
  })

  listEl.addEventListener('click', (e) => {
    const target = (e.target as HTMLElement).closest<HTMLElement>('[data-device-id]')
    if (!target?.dataset.deviceId) return
    callbacks.onSelectDevice(target.dataset.deviceId)
  })

  detailEl.addEventListener('click', (e) => {
    const tabBtn = (e.target as HTMLElement).closest<HTMLButtonElement>('[data-tab]')
    if (tabBtn?.dataset.tab) {
      tab = tabBtn.dataset.tab as TabKey
      paintDetail()
      if (tab === 'history' && lastSelected) {
        callbacks.onNeedHistory?.(lastSelected)
      }
      return
    }
    const actionBtn = (e.target as HTMLElement).closest<HTMLButtonElement>('[data-action]')
    if (actionBtn?.dataset.action && lastSelected) {
      callbacks.onAction(lastSelected, actionBtn.dataset.action)
    }
  })

  function paintDetail(): void {
    const device = lastDevices.find((d) => d.id === lastSelected) ?? null
    if (!device) {
      detailEl.innerHTML = `<p class="detail-empty">未选择设备</p>`
      return
    }

    const tabs = `
      <div class="detail-tabs">
        <button type="button" class="tab ${tab === 'detail' ? 'is-active' : ''}" data-tab="detail">详情</button>
        <button type="button" class="tab ${tab === 'options' ? 'is-active' : ''}" data-tab="options">选项</button>
        <button type="button" class="tab ${tab === 'scene' ? 'is-active' : ''}" data-tab="scene">场景</button>
        <button type="button" class="tab ${tab === 'history' ? 'is-active' : ''}" data-tab="history">动态</button>
      </div>`

    const title = `
      <div class="detail-title">
        <span class="alarm-dot alarm-${device.alarm}"></span>
        <div>
          <h3>${device.name}</h3>
          <p>${TYPE_LABELS[device.type]} · ${ALARM_LABELS[device.alarm]} · ${device.deviceCode}</p>
        </div>
      </div>`

    let body = ''
    if (tab === 'detail') {
      body = renderMetrics(device) + renderQuickActions(device)
    } else if (tab === 'options') {
      body = `<dl class="options-list">${device.options
        .map(
          (o) => `<div><dt>${o.label}${o.editable ? ' · 可调' : ''}</dt><dd>${o.value}${o.unit ? ` ${o.unit}` : ''}</dd></div>`,
        )
        .join('')}</dl>`
    } else if (tab === 'scene') {
      body = `
        <div class="scene-card">
          <p class="scene-title">场景说明</p>
          <p>${device.sceneNote}</p>
          <dl class="metrics compact">
            <div><dt>车间</dt><dd>${device.workshop}</dd></div>
            <div><dt>鱼池</dt><dd>${device.poolCode}</dd></div>
            <div><dt>锚点</dt><dd class="mono">${device.hotspot}</dd></div>
            <div><dt>画面/视角</dt><dd>${device.type === 'camera' ? device.cameraScene : '聚焦设备本体'}</dd></div>
          </dl>
          ${
            device.type === 'camera'
              ? `<div class="camera-preview"><div class="camera-preview-inner">${device.cameraOnline ? '● LIVE 已切入 3D 监控视角' : '○ 离线（仍可看预置俯视）'}</div><p>${device.cameraScene} — 主画面为车间鱼池俯视，非镜头近景</p></div>`
              : ''
          }
        </div>`
    } else {
      body =
        lastHistory.length === 0
          ? `<p class="detail-empty">暂无入库动态（请启动数据发生器）</p>`
          : `<ul class="history-list">${lastHistory
              .map((h) => `<li><time>${h.t}</time><span>${h.summary}</span></li>`)
              .join('')}</ul>`
    }

    detailEl.innerHTML = title + tabs + body
  }

  function renderMetrics(device: DeviceTelemetry): string {
    const rows: [string, string][] = []
    if (['tank', 'sensor', 'thermometer'].includes(device.type)) {
      rows.push(['水温', `${device.tempC.toFixed(1)} °C`])
    }
    if (['tank', 'sensor'].includes(device.type)) {
      rows.push(['溶氧', `${device.dissolvedOxygen.toFixed(2)} mg/L`])
      rows.push(['pH', device.ph.toFixed(2)])
      rows.push(['液位', `${device.levelPct.toFixed(0)} %`])
    }
    if (device.type === 'thermometer') {
      rows.push(['湿度', `${device.humidity.toFixed(0)} %RH`])
    }
    if (device.type === 'camera') {
      rows.push(['在线', device.cameraOnline ? '在线' : '离线'])
      rows.push(['画面', device.cameraScene])
    }
    if (device.type === 'switch') {
      rows.push(['开关', device.switchState])
      rows.push(['带电', device.powerOn ? '是' : '否'])
    }
    if (device.type === 'pump' || device.type === 'aerator') {
      rows.push(['运行', device.powerOn ? '运行' : '停止'])
      rows.push(['转速', `${Math.round(device.rpm)} rpm`])
    }
    if (device.type === 'pump') {
      rows.push(['流量', `${device.flowRate.toFixed(1)} m³/h`])
    }
    if (device.type === 'feeder') {
      rows.push(['投喂', device.feeding ? '进行中' : '待机'])
    }
    if (device.type === 'filter') {
      rows.push(['运行', device.powerOn ? '运行' : '停止'])
      rows.push(['滤材负荷', `${device.levelPct.toFixed(0)} %`])
    }
    rows.push(['更新', new Date(device.updatedAt).toLocaleTimeString()])

    return `<dl class="metrics">${rows.map(([k, v]) => `<div><dt>${k}</dt><dd>${v}</dd></div>`).join('')}</dl>`
  }

  function renderQuickActions(device: DeviceTelemetry): string {
    const actions: { action: string; label: string }[] = []
    if (device.type === 'switch') {
      actions.push({ action: 'toggle_switch', label: '切换开关' })
      actions.push({ action: 'cycle_switch_mode', label: '切换模式' })
    }
    if (device.type === 'pump' || device.type === 'aerator' || device.type === 'filter') {
      actions.push({ action: 'toggle_power', label: device.powerOn ? '停机' : '启动' })
    }
    if (device.type === 'feeder') {
      actions.push({ action: 'toggle_feeding', label: device.feeding ? '停止投喂' : '开始投喂' })
    }
    if (actions.length === 0) return ''
    return `<div class="action-row">${actions
      .map((a) => `<button type="button" class="action-btn" data-action="${a.action}">${a.label}</button>`)
      .join('')}</div>`
  }

  function paintListSoft(): boolean {
    const filtered =
      filter === 'all' ? lastDevices : lastDevices.filter((d) => d.type === filter)
    const items = listEl.querySelectorAll<HTMLElement>('.device-item')
    if (items.length !== filtered.length) return false

    for (let i = 0; i < filtered.length; i++) {
      const d = filtered[i]
      const li = items[i]
      if (li.dataset.deviceId !== d.id) return false
      li.classList.toggle('is-selected', d.id === lastSelected)
      const dot = li.querySelector('.alarm-dot')
      const tag = li.querySelector('.alarm-tag')
      const summary = li.querySelector('.device-meta span')
      if (dot) dot.className = `alarm-dot alarm-${d.alarm}`
      if (tag) {
        tag.className = `alarm-tag alarm-${d.alarm}`
        tag.textContent = ALARM_LABELS[d.alarm]
      }
      if (summary) summary.textContent = `${TYPE_LABELS[d.type]} · ${deviceSummaryLine(d)}`
    }
    return true
  }

  function paint(): void {
    const filtered =
      filter === 'all' ? lastDevices : lastDevices.filter((d) => d.type === filter)

    listEl.innerHTML = filtered
      .map(
        (d) => `
      <li class="device-item ${d.id === lastSelected ? 'is-selected' : ''}" data-device-id="${d.id}">
        <span class="alarm-dot alarm-${d.alarm}"></span>
        <div class="device-meta">
          <strong>${d.name}</strong>
          <span>${TYPE_LABELS[d.type]} · ${deviceSummaryLine(d)}</span>
        </div>
        <span class="alarm-tag alarm-${d.alarm}">${ALARM_LABELS[d.alarm]}</span>
      </li>`,
      )
      .join('')

    paintDetail()
  }

  function render(
    devices: DeviceTelemetry[],
    selectedId: string | null,
    history: { t: string; summary: string }[] = [],
    opts: { soft?: boolean } = {},
  ): void {
    const selectionChanged = selectedId !== lastSelected
    lastDevices = devices
    lastSelected = selectedId
    lastHistory = history

    if (opts.soft && !selectionChanged && paintListSoft()) {
      // Live telemetry: refresh detail numbers without rebuilding whole list DOM
      paintDetail()
      return
    }
    paint()
  }

  return {
    render,
    getFilter: () => filter,
    getTab: () => tab,
  }
}

export function setStatusBadge(text: string, tone: 'ok' | 'warn' | 'info' = 'info'): void {
  const el = document.getElementById('status-badge')
  if (!el) return
  el.textContent = text
  el.dataset.tone = tone
}
