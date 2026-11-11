import './ui/styles.css'
import * as THREE from 'three'
import {
  applyDeviceAction,
  createInitialDevices,
  tickDevices,
} from './data/mockDevices'
import {
  fetchDeviceHistory,
  fetchHealth,
  fetchLatestDevices,
  postDeviceAction,
  type HistoryItem,
} from './data/api'
import { createScene } from './scene/createScene'
import { loadFactory } from './scene/loadFactory'
import {
  createHotspotMeshes,
  pickDeviceId,
  updateHotspotColors,
  type HotspotHandle,
} from './scene/hotspots'
import {
  focusOnObject,
  focusOnView,
  CAMERA_VIEW_POSES,
  setCampusCutaway,
  updateCameraFocus,
} from './scene/cameraFocus'
import { mountPanel, setStatusBadge } from './ui/panel'
import { debounce, rafThrottle, throttle } from './utils/timing'

const canvas = document.getElementById('twin-canvas') as HTMLCanvasElement
const panelEl = document.getElementById('side-panel') as HTMLElement

const { scene, camera, renderer, controls, root } = createScene(canvas)
let devices = createInitialDevices()
let selectedId: string | null = devices[0]?.id ?? null
let handles: HotspotHandle[] = []
let history: HistoryItem[] = []
let apiOnline = false
let factoryMessage = ''
let refreshInFlight = false
let pollCount = 0
let lastRowHint = '—'
let pageVisible = document.visibilityState === 'visible'

const POLL_MS = 4000
const HISTORY_EVERY_N = 5
const HEALTH_EVERY_N = 4

const applySoftUi = rafThrottle(() => {
  updateHotspotColors(handles, devices)
  panel.render(devices, selectedId, history, { soft: true })
})

const setStatusDebounced = debounce((text: string, tone: 'ok' | 'warn' | 'info') => {
  setStatusBadge(text, tone)
}, 200)

const loadHistoryDebounced = debounce((deviceId: string) => {
  void (async () => {
    if (!apiOnline) return
    const device = devices.find((d) => d.id === deviceId)
    if (!device) return
    history = await fetchDeviceHistory(device.deviceCode)
    panel.render(devices, selectedId, history, { soft: true })
  })()
}, 250)

const panel = mountPanel(panelEl, {
  onSelectDevice: (id) => {
    void selectDevice(id, true)
  },
  onAction: (id, action) => {
    void handleAction(id, action)
  },
  onNeedHistory: (id) => {
    loadHistoryDebounced(id)
  },
})

const pointer = new THREE.Vector2()
const raycaster = new THREE.Raycaster()
const clock = new THREE.Clock()

async function selectDevice(id: string, animateCamera: boolean): Promise<void> {
  selectedId = id
  const device = devices.find((d) => d.id === id)
  // 历史懒加载：仅当前已在「动态」页签时才立即拉，否则等用户点开再拉
  if (device && apiOnline && panel.getTab() === 'history') {
    history = await fetchDeviceHistory(device.deviceCode)
  } else if (panel.getTab() !== 'history') {
    history = []
  }
  panel.render(devices, selectedId, history)
  if (!animateCamera || !device) return

  if (device.type === 'camera') {
    setCampusCutaway(root, true)
    const pose = CAMERA_VIEW_POSES[device.id]
    if (pose) {
      focusOnView(camera, controls, pose)
      return
    }
  } else {
    setCampusCutaway(root, false)
  }

  const handle = handles.find((h) => h.deviceId === id)
  if (handle) focusOnObject(camera, controls, handle.anchor, 10)
}

async function handleAction(id: string, action: string): Promise<void> {
  if (apiOnline) {
    await postDeviceAction(id, action)
    await refreshFromApiOrLocal(false, false)
  } else {
    devices = applyDeviceAction(devices, id, action)
    updateHotspotColors(handles, devices)
    panel.render(devices, selectedId, history)
  }
}

const onPointerDown = throttle((event: PointerEvent) => {
  const rect = canvas.getBoundingClientRect()
  pointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
  pointer.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
  const id = pickDeviceId(raycaster, pointer, camera, handles)
  if (id) void selectDevice(id, true)
}, 80)

canvas.addEventListener('pointerdown', onPointerDown)

async function refreshFromApiOrLocal(
  allowLocalTick: boolean,
  softUi: boolean,
): Promise<void> {
  if (refreshInFlight) return
  if (!pageVisible && softUi) return
  refreshInFlight = true
  pollCount += 1
  try {
    const { devices: remote, online } = await fetchLatestDevices()
    apiOnline = online
    if (online && remote.length > 0) {
      devices = remote
    } else if (allowLocalTick) {
      devices = tickDevices(devices)
    }

    const wantHistory =
      softUi &&
      apiOnline &&
      selectedId &&
      panel.getTab() === 'history' &&
      pollCount % HISTORY_EVERY_N === 0

    if (!softUi && selectedId && apiOnline && panel.getTab() === 'history') {
      const device = devices.find((d) => d.id === selectedId)
      if (device) history = await fetchDeviceHistory(device.deviceCode)
    } else if (wantHistory) {
      const device = devices.find((d) => d.id === selectedId)
      if (device) history = await fetchDeviceHistory(device.deviceCode)
    }

    if (softUi) {
      applySoftUi()
    } else {
      updateHotspotColors(handles, devices)
      panel.render(devices, selectedId, history)
    }

    if (!softUi || pollCount % HEALTH_EVERY_N === 0) {
      if (apiOnline) {
        const health = await fetchHealth()
        lastRowHint = String(health.totalRows ?? lastRowHint)
        setStatusDebounced(
          `${factoryMessage} · 数据发生器在线 · 已入库 ${lastRowHint} 条`,
          'ok',
        )
      } else {
        setStatusDebounced(`${factoryMessage} · 本地模拟（未连 API）`, 'warn')
      }
    }
  } finally {
    refreshInFlight = false
  }
}

async function boot(): Promise<void> {
  setStatusBadge('正在加载厂房模型…', 'info')

  // 渐进加载：开放厅先上屏，厂房一号懒加载完成后重建热点
  const factory = await loadFactory((partial) => {
    root.clear()
    root.add(partial.root)
    factoryMessage = partial.message
    setStatusBadge(partial.message, 'info')
    handles = createHotspotMeshes(partial.root, devices)
    panel.render(devices, selectedId, history)
  })

  root.clear()
  root.add(factory.root)
  factoryMessage = factory.message
  handles = createHotspotMeshes(factory.root, devices)

  await refreshFromApiOrLocal(false, false)
  if (selectedId) await selectDevice(selectedId, true)
}

boot().catch((err) => {
  console.error(err)
  setStatusBadge('场景初始化失败', 'warn')
})

document.addEventListener('visibilitychange', () => {
  pageVisible = document.visibilityState === 'visible'
  if (pageVisible) void refreshFromApiOrLocal(true, true)
})

window.setInterval(() => {
  if (!pageVisible) return
  void refreshFromApiOrLocal(true, true)
}, POLL_MS)

function animate(): void {
  requestAnimationFrame(animate)
  const dt = clock.getDelta()
  updateCameraFocus(camera, controls, dt)
  controls.update()
  renderer.render(scene, camera)
}

animate()
