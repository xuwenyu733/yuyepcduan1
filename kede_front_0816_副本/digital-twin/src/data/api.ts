import {
  DEVICE_SEEDS,
  createInitialDevices,
  refreshDeviceView,
  type DeviceTelemetry,
} from './mockDevices'

const API_BASE = '/api'
const BASE_DEVICES = createInitialDevices()

export type HistoryItem = { t: string; summary: string }

function hydrateFromApi(raw: Record<string, unknown>): DeviceTelemetry | null {
  const deviceCode = String(raw.deviceCode ?? raw.device_code ?? '')
  const seed =
    DEVICE_SEEDS.find((s) => s.id === raw.id) ||
    DEVICE_SEEDS.find((s) => s.deviceCode === deviceCode)
  if (!seed) return null

  const base = BASE_DEVICES.find((d) => d.id === seed.id)
  if (!base) return null

  return refreshDeviceView({
    ...base,
    tempC: Number(raw.tempC ?? base.tempC),
    dissolvedOxygen: Number(raw.dissolvedOxygen ?? base.dissolvedOxygen),
    ph: Number(raw.ph ?? base.ph),
    levelPct: Number(raw.levelPct ?? base.levelPct),
    humidity: Number(raw.humidity ?? base.humidity),
    powerOn: Boolean(raw.powerOn ?? base.powerOn),
    switchState: (raw.switchState as DeviceTelemetry['switchState']) ?? base.switchState,
    cameraOnline: Boolean(raw.cameraOnline ?? base.cameraOnline),
    cameraScene: String(raw.cameraScene ?? base.cameraScene),
    rpm: Number(raw.rpm ?? base.rpm),
    flowRate: Number(raw.flowRate ?? base.flowRate),
    feeding: Boolean(raw.feeding ?? base.feeding),
    alarm: (raw.alarm as DeviceTelemetry['alarm']) ?? base.alarm,
    updatedAt: String(raw.updatedAt ?? new Date().toISOString()),
  })
}

export async function fetchLatestDevices(): Promise<{
  devices: DeviceTelemetry[]
  online: boolean
}> {
  try {
    const res = await fetch(`${API_BASE}/devices/latest`)
    if (!res.ok) throw new Error(String(res.status))
    const data = (await res.json()) as { devices: Record<string, unknown>[] }
    const devices = data.devices
      .map((raw) => hydrateFromApi(raw))
      .filter((d): d is DeviceTelemetry => Boolean(d))
    return { devices, online: true }
  } catch {
    return { devices: [], online: false }
  }
}

export async function fetchDeviceHistory(deviceCode: string): Promise<HistoryItem[]> {
  try {
    const res = await fetch(
      `${API_BASE}/devices/${encodeURIComponent(deviceCode)}/history?limit=15`,
    )
    if (!res.ok) return []
    const data = (await res.json()) as { history: HistoryItem[] }
    return data.history ?? []
  } catch {
    return []
  }
}

export async function postDeviceAction(deviceId: string, action: string): Promise<boolean> {
  try {
    const res = await fetch(`${API_BASE}/devices/${encodeURIComponent(deviceId)}/action`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action }),
    })
    return res.ok
  } catch {
    return false
  }
}

export async function fetchHealth(): Promise<{ ok: boolean; totalRows?: number }> {
  try {
    const res = await fetch(`${API_BASE}/health`)
    if (!res.ok) return { ok: false }
    return (await res.json()) as { ok: boolean; totalRows?: number }
  } catch {
    return { ok: false }
  }
}
