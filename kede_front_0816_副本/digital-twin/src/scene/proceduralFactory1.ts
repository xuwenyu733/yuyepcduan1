import * as THREE from 'three'
import {
  addAeratorProp,
  addCameraProp,
  addFeederProp,
  addSwitchProp,
  addThermometerProp,
} from './equipProps'

/**
 * 厂房一号：封闭式养成车间（程序化白模）
 * — 双坡屋顶、四面围墙、跑道式鱼池、水处理角、卷帘门
 */
export function createProceduralFactory1(): THREE.Group {
  const group = new THREE.Group()
  group.name = 'ProceduralFactory1'

  const wallMat = new THREE.MeshStandardMaterial({
    color: 0xeef3f6,
    roughness: 0.88,
    metalness: 0.04,
  })
  const trimMat = new THREE.MeshStandardMaterial({
    color: 0x3d5a66,
    roughness: 0.5,
    metalness: 0.2,
  })
  const roofMat = new THREE.MeshStandardMaterial({
    color: 0x6a8490,
    roughness: 0.7,
    metalness: 0.15,
    transparent: true,
    opacity: 0.32,
    depthWrite: false,
  })
  roofMat.userData.baseOpacity = 0.32
  const floorMat = new THREE.MeshStandardMaterial({
    color: 0xc8d0cc,
    roughness: 0.92,
  })
  const tankMat = new THREE.MeshStandardMaterial({
    color: 0x8ec4d4,
    roughness: 0.32,
    metalness: 0.06,
    transparent: true,
    opacity: 0.78,
  })
  const waterMat = new THREE.MeshStandardMaterial({
    color: 0x2f7fa0,
    roughness: 0.18,
    metalness: 0.08,
    transparent: true,
    opacity: 0.58,
  })
  const equipMat = new THREE.MeshStandardMaterial({
    color: 0x5c6b75,
    roughness: 0.55,
    metalness: 0.3,
  })
  const glassMat = new THREE.MeshStandardMaterial({
    color: 0xa8c8d8,
    roughness: 0.15,
    metalness: 0.05,
    transparent: true,
    opacity: 0.35,
  })

  // Interior fill lights — avoid “黑蒙蒙” closed hall
  const lampCenter = new THREE.PointLight(0xfff5e6, 80, 28, 2)
  lampCenter.position.set(0, 4.2, 0)
  group.add(lampCenter)
  const lampA = new THREE.PointLight(0xe8f4ff, 45, 18, 2)
  lampA.position.set(-8, 3.8, -3)
  group.add(lampA)
  const lampB = new THREE.PointLight(0xe8f4ff, 45, 18, 2)
  lampB.position.set(8, 3.8, 2)
  group.add(lampB)

  const W = 32
  const D = 20
  const wallH = 5.2
  const halfW = W / 2
  const halfD = D / 2

  // Floor slab
  const floor = new THREE.Mesh(new THREE.BoxGeometry(W + 1.2, 0.28, D + 1.2), floorMat)
  floor.position.set(0, 0.14, 0)
  floor.receiveShadow = true
  group.add(floor)

  // Exterior base skirt
  const skirt = new THREE.Mesh(new THREE.BoxGeometry(W + 1.4, 0.45, D + 1.4), trimMat)
  skirt.position.set(0, 0.22, 0)
  group.add(skirt)

  // Walls (north solid, south with door, east/west with windows)
  const wallThickness = 0.28

  const north = new THREE.Mesh(new THREE.BoxGeometry(W, wallH, wallThickness), wallMat)
  north.position.set(0, wallH / 2 + 0.28, -halfD)
  north.castShadow = true
  north.receiveShadow = true
  group.add(north)

  // South wall split around roller door
  const doorW = 5.5
  const southLeftW = (W - doorW) / 2
  const southLeft = new THREE.Mesh(new THREE.BoxGeometry(southLeftW, wallH, wallThickness), wallMat)
  southLeft.position.set(-(doorW / 2 + southLeftW / 2), wallH / 2 + 0.28, halfD)
  southLeft.castShadow = true
  group.add(southLeft)
  const southRight = southLeft.clone()
  southRight.position.x = doorW / 2 + southLeftW / 2
  group.add(southRight)

  // Roller door panel (slightly inset)
  const door = new THREE.Mesh(new THREE.BoxGeometry(doorW, wallH - 0.4, 0.12), trimMat)
  door.position.set(0, (wallH - 0.4) / 2 + 0.28, halfD - 0.05)
  group.add(door)
  // Door ribs
  for (let i = 0; i < 8; i++) {
    const rib = new THREE.Mesh(new THREE.BoxGeometry(doorW - 0.2, 0.06, 0.04), equipMat)
    rib.position.set(0, 0.6 + i * 0.55, halfD + 0.02)
    group.add(rib)
  }

  const west = new THREE.Mesh(new THREE.BoxGeometry(wallThickness, wallH, D), wallMat)
  west.position.set(-halfW, wallH / 2 + 0.28, 0)
  west.castShadow = true
  group.add(west)
  const east = west.clone()
  east.position.x = halfW
  group.add(east)

  // Make upper wall band lighter / open so interior reads clearly
  north.scale.y = 0.72
  north.position.y = (wallH * 0.72) / 2 + 0.28
  southLeft.scale.y = 0.72
  southLeft.position.y = (wallH * 0.72) / 2 + 0.28
  southRight.scale.y = 0.72
  southRight.position.y = (wallH * 0.72) / 2 + 0.28
  west.scale.y = 0.72
  west.position.y = (wallH * 0.72) / 2 + 0.28
  east.scale.y = 0.72
  east.position.y = (wallH * 0.72) / 2 + 0.28
  door.scale.y = 0.85
  door.position.y = ((wallH - 0.4) * 0.85) / 2 + 0.28

  // Windows on east/west
  const winPositions = [-5, 0, 5]
  for (const z of winPositions) {
    for (const x of [-halfW + 0.02, halfW - 0.02]) {
      const frame = new THREE.Mesh(new THREE.BoxGeometry(0.1, 1.8, 2.4), trimMat)
      frame.position.set(x, 2.2, z)
      group.add(frame)
      const glass = new THREE.Mesh(new THREE.BoxGeometry(0.06, 1.5, 2.1), glassMat)
      glass.position.set(x > 0 ? x - 0.04 : x + 0.04, 2.2, z)
      group.add(glass)
    }
  }

  // Gable roof (two slabs)
  const roofLen = W + 1.6
  const roofDepth = D / 2 + 0.8
  const roofThick = 0.18
  const pitch = 0.22

  const roofL = new THREE.Mesh(new THREE.BoxGeometry(roofLen, roofThick, roofDepth + 1), roofMat)
  roofL.name = 'Cutaway_B1_Roof'
  roofL.position.set(0, wallH + 0.9, -roofDepth / 2 + 0.2)
  roofL.rotation.x = pitch
  roofL.castShadow = false
  group.add(roofL)

  const roofR = new THREE.Mesh(new THREE.BoxGeometry(roofLen, roofThick, roofDepth + 1), roofMat.clone())
  ;(roofR.material as THREE.MeshStandardMaterial).userData.baseOpacity = 0.32
  roofR.name = 'Cutaway_B1_Roof'
  roofR.position.set(0, wallH + 0.9, roofDepth / 2 - 0.2)
  roofR.rotation.x = -pitch
  roofR.castShadow = false
  group.add(roofR)

  // Ridge beam
  const ridge = new THREE.Mesh(new THREE.BoxGeometry(roofLen, 0.25, 0.35), trimMat)
  ridge.name = 'Cutaway_B1_Roof'
  ridge.position.set(0, wallH + 1.55, 0)
  group.add(ridge)

  // Interior columns
  const colGeo = new THREE.BoxGeometry(0.4, wallH - 0.2, 0.4)
  for (const x of [-10, 0, 10]) {
    for (const z of [-6, 6]) {
      const col = new THREE.Mesh(colGeo, equipMat)
      col.position.set(x, wallH / 2 + 0.2, z)
      col.castShadow = true
      group.add(col)
    }
  }

  // Raceway tanks (矩形跑道池) — 6 pools in 2 rows
  const raceways: { name: string; x: number; z: number; w: number; d: number }[] = [
    { name: 'Hotspot_B1_Tank_01', x: -10, z: -4.5, w: 7.5, d: 3.2 },
    { name: 'Hotspot_B1_Tank_02', x: -1.5, z: -4.5, w: 7.5, d: 3.2 },
    { name: 'Hotspot_B1_Tank_03', x: 7, z: -4.5, w: 7.5, d: 3.2 },
    { name: 'Hotspot_B1_Tank_04', x: -10, z: 1.2, w: 7.5, d: 3.2 },
    { name: 'Hotspot_B1_Tank_05', x: -1.5, z: 1.2, w: 7.5, d: 3.2 },
    { name: 'Hotspot_B1_Tank_06', x: 7, z: 1.2, w: 7.5, d: 3.2 },
  ]

  for (const t of raceways) {
    const shell = new THREE.Mesh(new THREE.BoxGeometry(t.w, 1.35, t.d), tankMat)
    shell.position.set(t.x, 0.95, t.z)
    shell.castShadow = true
    shell.receiveShadow = true
    group.add(shell)

    const water = new THREE.Mesh(
      new THREE.BoxGeometry(t.w - 0.25, 0.85, t.d - 0.25),
      waterMat,
    )
    water.position.set(t.x, 0.85, t.z)
    group.add(water)

    // Rim
    const rim = new THREE.Mesh(
      new THREE.BoxGeometry(t.w + 0.15, 0.12, t.d + 0.15),
      trimMat,
    )
    rim.position.set(t.x, 1.55, t.z)
    group.add(rim)

    const anchor = new THREE.Object3D()
    anchor.name = t.name
    anchor.position.set(t.x, 2.35, t.z)
    group.add(anchor)
  }

  // Walkway between rows
  const walk = new THREE.Mesh(new THREE.BoxGeometry(26, 0.1, 1.6), floorMat)
  walk.position.set(-1.5, 0.35, -1.65)
  walk.receiveShadow = true
  group.add(walk)

  // RAS / filter corner (NW)
  const filterBase = new THREE.Mesh(new THREE.BoxGeometry(4.2, 1.8, 3.5), equipMat)
  filterBase.position.set(-12.5, 1.15, 6.5)
  filterBase.castShadow = true
  group.add(filterBase)
  const filterTank = new THREE.Mesh(new THREE.CylinderGeometry(1.1, 1.1, 2.2, 24), tankMat)
  filterTank.position.set(-12.5, 2.2, 6.5)
  group.add(filterTank)
  const filterAnchor = new THREE.Object3D()
  filterAnchor.name = 'Hotspot_B1_Filter_01'
  filterAnchor.position.set(-12.5, 3.6, 6.5)
  group.add(filterAnchor)

  // Pump set
  const pumpBody = new THREE.Mesh(new THREE.BoxGeometry(1.8, 1.2, 1.4), equipMat)
  pumpBody.position.set(-8.5, 0.85, 6.8)
  pumpBody.castShadow = true
  group.add(pumpBody)
  const pumpMotor = new THREE.Mesh(new THREE.CylinderGeometry(0.4, 0.4, 0.9, 20), wallMat)
  pumpMotor.rotation.z = Math.PI / 2
  pumpMotor.position.set(-8.5, 1.6, 6.8)
  group.add(pumpMotor)
  const pumpAnchor = new THREE.Object3D()
  pumpAnchor.name = 'Hotspot_B1_Pump_01'
  pumpAnchor.position.set(-8.5, 2.4, 6.8)
  group.add(pumpAnchor)

  // Pipe run (simple)
  const pipe = new THREE.Mesh(new THREE.CylinderGeometry(0.12, 0.12, 8, 12), trimMat)
  pipe.rotation.z = Math.PI / 2
  pipe.position.set(-4.5, 1.8, 6.8)
  group.add(pipe)

  // DO sensor pole near center aisle
  const pole = new THREE.Mesh(new THREE.CylinderGeometry(0.07, 0.07, 2.4, 10), equipMat)
  pole.position.set(2.5, 1.4, -1.65)
  group.add(pole)
  const head = new THREE.Mesh(new THREE.SphereGeometry(0.2, 14, 14), wallMat)
  head.position.set(2.5, 2.7, -1.65)
  group.add(head)
  const sensorAnchor = new THREE.Object3D()
  sensorAnchor.name = 'Hotspot_B1_Sensor_DO_01'
  sensorAnchor.position.set(2.5, 3.1, -1.65)
  group.add(sensorAnchor)

  // Exterior signboard (厂房一号)
  const sign = new THREE.Mesh(new THREE.BoxGeometry(6, 1.1, 0.15), trimMat)
  sign.position.set(0, 4.2, halfD + 0.2)
  group.add(sign)

  const label = new THREE.Object3D()
  label.name = 'Hotspot_B1_Building'
  label.position.set(0, 4, halfD + 2)
  group.add(label)

  addCameraProp(group, 'Hotspot_Cam_B1_01', 0, 4.6, halfD - 0.4, Math.PI)
  addThermometerProp(group, 'Hotspot_Thermo_B1_01', -10, 1.5, -2.6)
  addSwitchProp(group, 'Hotspot_Switch_B1_01', 12, 1.3, halfD - 1.2)
  addFeederProp(group, 'Hotspot_Feeder_B1_01', -1.5, 0.2, -2.4)
  addAeratorProp(group, 'Hotspot_Aerator_B1_01', -10, 0.2, 1.2)

  return group
}
