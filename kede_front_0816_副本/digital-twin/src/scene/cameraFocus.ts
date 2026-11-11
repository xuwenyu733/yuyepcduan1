import * as THREE from 'three'
import type { OrbitControls } from 'three/addons/controls/OrbitControls.js'

type FocusState = {
  active: boolean
  t: number
  duration: number
  fromCam: THREE.Vector3
  toCam: THREE.Vector3
  fromTarget: THREE.Vector3
  toTarget: THREE.Vector3
}

const state: FocusState = {
  active: false,
  t: 0,
  duration: 0.9,
  fromCam: new THREE.Vector3(),
  toCam: new THREE.Vector3(),
  fromTarget: new THREE.Vector3(),
  toTarget: new THREE.Vector3(),
}

function easeInOutCubic(x: number): number {
  return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2
}

export type ViewPose = {
  position: THREE.Vector3Like
  target: THREE.Vector3Like
  duration?: number
}

/** 监控摄像头预置视角（世界坐标，已含厂房偏移） */
export const CAMERA_VIEW_POSES: Record<string, ViewPose> = {
  // 开放厅在 x=-22：俯视圆形养成池
  'cam-oh-01': {
    position: { x: -22, y: 16, z: 14 },
    target: { x: -22, y: 1.2, z: -2.2 },
  },
  // 厂房一号在 x=20：俯视跑道池阵列
  'cam-b1-01': {
    position: { x: 20, y: 18, z: 16 },
    target: { x: 20, y: 1.0, z: -1.5 },
  },
}

export function focusOnObject(
  camera: THREE.PerspectiveCamera,
  controls: OrbitControls,
  object: THREE.Object3D,
  distance = 8,
): void {
  const world = new THREE.Vector3()
  object.getWorldPosition(world)

  const dir = new THREE.Vector3(0.55, 0.45, 0.7).normalize()
  focusOnView(camera, controls, {
    position: {
      x: world.x + dir.x * distance,
      y: Math.max(world.y + 3.5, 8),
      z: world.z + dir.z * distance,
    },
    target: { x: world.x, y: world.y, z: world.z },
  })
}

export function focusOnView(
  camera: THREE.PerspectiveCamera,
  controls: OrbitControls,
  pose: ViewPose,
): void {
  state.fromCam.copy(camera.position)
  state.fromTarget.copy(controls.target)
  state.toCam.set(pose.position.x, pose.position.y, pose.position.z)
  state.toTarget.set(pose.target.x, pose.target.y, pose.target.z)
  state.duration = pose.duration ?? 0.9
  state.t = 0
  state.active = true
}

export function updateCameraFocus(
  camera: THREE.PerspectiveCamera,
  controls: OrbitControls,
  dt: number,
): void {
  if (!state.active) return
  state.t += dt
  const u = easeInOutCubic(Math.min(1, state.t / state.duration))
  camera.position.lerpVectors(state.fromCam, state.toCam, u)
  controls.target.lerpVectors(state.fromTarget, state.toTarget, u)
  controls.update()
  if (u >= 1) state.active = false
}

/** 点击摄像头时：半透明屋顶，露出室内鱼池细节 */
export function setCampusCutaway(root: THREE.Object3D, enabled: boolean): void {
  root.traverse((obj) => {
    if (!obj.name.startsWith('Cutaway_')) return
    const mesh = obj as THREE.Mesh
    if (!mesh.isMesh) return
    const mats = Array.isArray(mesh.material) ? mesh.material : [mesh.material]
    for (const m of mats) {
      const mat = m as THREE.MeshStandardMaterial
      if (!mat) continue
      mat.transparent = true
      mat.opacity = enabled ? 0.12 : (mat.userData.baseOpacity ?? 0.35)
      mat.depthWrite = !enabled
      mat.needsUpdate = true
    }
  })
}
