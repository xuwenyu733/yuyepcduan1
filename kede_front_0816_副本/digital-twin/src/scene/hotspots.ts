import * as THREE from 'three'
import { ALARM_COLORS, type AlarmLevel, type DeviceTelemetry } from '../data/mockDevices'

export type HotspotHandle = {
  deviceId: string
  hotspotName: string
  mesh: THREE.Mesh
  anchor: THREE.Object3D
}

export function findHotspotAnchors(root: THREE.Object3D): Map<string, THREE.Object3D> {
  const map = new Map<string, THREE.Object3D>()
  root.traverse((obj) => {
    if (obj.name.startsWith('Hotspot_') && !map.has(obj.name)) {
      map.set(obj.name, obj)
    }
  })
  return map
}

export function createHotspotMeshes(
  root: THREE.Object3D,
  devices: DeviceTelemetry[],
): HotspotHandle[] {
  const anchors = findHotspotAnchors(root)
  const handles: HotspotHandle[] = []
  const geo = new THREE.SphereGeometry(0.28, 20, 20)

  for (const device of devices) {
    let anchor = anchors.get(device.hotspot)
    if (!anchor) {
      // Fallback position so UI still works if glTF lacks empties
      anchor = new THREE.Object3D()
      anchor.name = device.hotspot
      const idx = handles.length
      anchor.position.set(-6 + idx * 3.2, 2.4, 0)
      root.add(anchor)
    }

    const mat = new THREE.MeshStandardMaterial({
      color: ALARM_COLORS[device.alarm],
      emissive: ALARM_COLORS[device.alarm],
      emissiveIntensity: 0.35,
      roughness: 0.4,
      metalness: 0.1,
    })
    const mesh = new THREE.Mesh(geo, mat)
    mesh.name = `Marker_${device.id}`
    mesh.userData.deviceId = device.id
    mesh.castShadow = true
    anchor.add(mesh)

    handles.push({
      deviceId: device.id,
      hotspotName: device.hotspot,
      mesh,
      anchor,
    })
  }

  return handles
}

export function updateHotspotColors(handles: HotspotHandle[], devices: DeviceTelemetry[]): void {
  const byId = new Map(devices.map((d) => [d.id, d]))
  for (const h of handles) {
    const d = byId.get(h.deviceId)
    if (!d) continue
    const color = ALARM_COLORS[d.alarm as AlarmLevel]
    const prev = h.mesh.userData.alarmColor as number | undefined
    if (prev === color) continue
    h.mesh.userData.alarmColor = color
    const mat = h.mesh.material as THREE.MeshStandardMaterial
    mat.color.setHex(color)
    mat.emissive.setHex(color)
  }
}

export function pickDeviceId(
  raycaster: THREE.Raycaster,
  pointer: THREE.Vector2,
  camera: THREE.Camera,
  handles: HotspotHandle[],
): string | null {
  raycaster.setFromCamera(pointer, camera)
  const meshes = handles.map((h) => h.mesh)
  const hits = raycaster.intersectObjects(meshes, false)
  if (hits.length === 0) return null
  const id = hits[0].object.userData.deviceId
  return typeof id === 'string' ? id : null
}
