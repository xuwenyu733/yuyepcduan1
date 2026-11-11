import * as THREE from 'three'

const metal = () =>
  new THREE.MeshStandardMaterial({ color: 0x5c6b75, roughness: 0.5, metalness: 0.35 })
const plastic = () =>
  new THREE.MeshStandardMaterial({ color: 0xe8eef2, roughness: 0.75, metalness: 0.05 })
const accent = () =>
  new THREE.MeshStandardMaterial({ color: 0x2f6f7e, roughness: 0.45, metalness: 0.15 })

function addAnchor(parent: THREE.Object3D, name: string, y = 0.6): void {
  const a = new THREE.Object3D()
  a.name = name
  a.position.y = y
  parent.add(a)
}

/** 枪机/球机摄像头 */
export function addCameraProp(
  group: THREE.Group,
  name: string,
  x: number,
  y: number,
  z: number,
  yaw = 0,
): void {
  const root = new THREE.Group()
  root.position.set(x, y, z)
  root.rotation.y = yaw

  const arm = new THREE.Mesh(new THREE.BoxGeometry(0.12, 0.12, 0.55), metal())
  arm.position.set(0, 0, 0.15)
  root.add(arm)

  const body = new THREE.Mesh(new THREE.BoxGeometry(0.35, 0.28, 0.45), plastic())
  body.position.set(0, -0.05, 0.45)
  body.castShadow = true
  root.add(body)

  const lens = new THREE.Mesh(new THREE.CylinderGeometry(0.08, 0.1, 0.12, 16), accent())
  lens.rotation.x = Math.PI / 2
  lens.position.set(0, -0.05, 0.7)
  root.add(lens)

  addAnchor(root, name, 0.35)
  group.add(root)
}

/** 壁挂/探针温度计 */
export function addThermometerProp(
  group: THREE.Group,
  name: string,
  x: number,
  y: number,
  z: number,
): void {
  const root = new THREE.Group()
  root.position.set(x, y, z)

  const stem = new THREE.Mesh(new THREE.CylinderGeometry(0.04, 0.04, 0.9, 10), metal())
  stem.position.y = 0.2
  root.add(stem)

  const bulb = new THREE.Mesh(new THREE.SphereGeometry(0.12, 14, 14), accent())
  bulb.position.y = -0.25
  root.add(bulb)

  const head = new THREE.Mesh(new THREE.BoxGeometry(0.28, 0.22, 0.12), plastic())
  head.position.y = 0.7
  root.add(head)

  addAnchor(root, name, 0.95)
  group.add(root)
}

/** 控制开关箱 */
export function addSwitchProp(
  group: THREE.Group,
  name: string,
  x: number,
  y: number,
  z: number,
): void {
  const root = new THREE.Group()
  root.position.set(x, y, z)

  const box = new THREE.Mesh(new THREE.BoxGeometry(0.55, 0.75, 0.25), metal())
  box.castShadow = true
  root.add(box)

  const btn = new THREE.Mesh(new THREE.CylinderGeometry(0.08, 0.08, 0.06, 16), accent())
  btn.rotation.x = Math.PI / 2
  btn.position.set(0, 0.1, 0.16)
  root.add(btn)

  addAnchor(root, name, 0.55)
  group.add(root)
}

/** 投喂机 */
export function addFeederProp(
  group: THREE.Group,
  name: string,
  x: number,
  y: number,
  z: number,
): void {
  const root = new THREE.Group()
  root.position.set(x, y, z)

  const hopper = new THREE.Mesh(new THREE.CylinderGeometry(0.45, 0.55, 0.9, 16), plastic())
  hopper.position.y = 1.1
  hopper.castShadow = true
  root.add(hopper)

  const pole = new THREE.Mesh(new THREE.CylinderGeometry(0.07, 0.07, 1.6, 10), metal())
  pole.position.y = 0.8
  root.add(pole)

  const spout = new THREE.Mesh(new THREE.BoxGeometry(0.2, 0.15, 0.55), metal())
  spout.position.set(0, 0.55, 0.35)
  root.add(spout)

  addAnchor(root, name, 1.7)
  group.add(root)
}

/** 增氧机（叶轮示意） */
export function addAeratorProp(
  group: THREE.Group,
  name: string,
  x: number,
  y: number,
  z: number,
): void {
  const root = new THREE.Group()
  root.position.set(x, y, z)

  const base = new THREE.Mesh(new THREE.CylinderGeometry(0.35, 0.4, 0.25, 16), metal())
  base.position.y = 0.2
  base.castShadow = true
  root.add(base)

  const shaft = new THREE.Mesh(new THREE.CylinderGeometry(0.05, 0.05, 0.8, 10), metal())
  shaft.position.y = 0.7
  root.add(shaft)

  const blade = new THREE.Mesh(new THREE.BoxGeometry(1.1, 0.06, 0.22), accent())
  blade.position.y = 1.05
  root.add(blade)

  addAnchor(root, name, 1.35)
  group.add(root)
}
