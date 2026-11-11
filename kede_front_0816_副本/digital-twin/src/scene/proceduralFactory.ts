import * as THREE from 'three'

/** Lightweight stand-in fish plant when factory.glb is not ready yet. */
export function createProceduralFactory(): THREE.Group {
  const group = new THREE.Group()
  group.name = 'ProceduralFactory'

  const wallMat = new THREE.MeshStandardMaterial({
    color: 0xe8eef2,
    roughness: 0.82,
    metalness: 0.05,
  })
  const frameMat = new THREE.MeshStandardMaterial({
    color: 0x5c6b75,
    roughness: 0.55,
    metalness: 0.25,
  })
  const tankMat = new THREE.MeshStandardMaterial({
    color: 0x7eb8c9,
    roughness: 0.35,
    metalness: 0.05,
    transparent: true,
    opacity: 0.72,
  })
  const waterMat = new THREE.MeshStandardMaterial({
    color: 0x3a8fb0,
    roughness: 0.2,
    metalness: 0.1,
    transparent: true,
    opacity: 0.55,
  })
  const deckMat = new THREE.MeshStandardMaterial({ color: 0xd0d6d4, roughness: 0.9 })

  // Hall slab / floor deck
  const deck = new THREE.Mesh(new THREE.BoxGeometry(28, 0.25, 18), deckMat)
  deck.position.set(0, 0.12, 0)
  deck.receiveShadow = true
  group.add(deck)

  // Simple open hall: four corner columns + roof beams
  const colGeo = new THREE.BoxGeometry(0.55, 6, 0.55)
  const corners: [number, number][] = [
    [-13, -8],
    [13, -8],
    [-13, 8],
    [13, 8],
  ]
  for (const [x, z] of corners) {
    const col = new THREE.Mesh(colGeo, frameMat)
    col.position.set(x, 3.1, z)
    col.castShadow = true
    group.add(col)
  }

  const beamX = new THREE.Mesh(new THREE.BoxGeometry(27, 0.35, 0.45), frameMat)
  beamX.position.set(0, 6.1, 0)
  group.add(beamX)
  const beamZ1 = new THREE.Mesh(new THREE.BoxGeometry(0.45, 0.35, 17), frameMat)
  beamZ1.position.set(-13, 6.1, 0)
  group.add(beamZ1)
  const beamZ2 = beamZ1.clone()
  beamZ2.position.x = 13
  group.add(beamZ2)

  // Back wall strip (suggests enclosed plant)
  const backWall = new THREE.Mesh(new THREE.BoxGeometry(28, 4.5, 0.3), wallMat)
  backWall.position.set(0, 2.4, -8.9)
  backWall.castShadow = true
  backWall.receiveShadow = true
  group.add(backWall)

  const tankLayout: { name: string; x: number; z: number; r: number }[] = [
    { name: 'Hotspot_Tank_01', x: -7.5, z: -2.5, r: 2.4 },
    { name: 'Hotspot_Tank_02', x: -2.2, z: -2.5, r: 2.4 },
    { name: 'Hotspot_Tank_03', x: 3.1, z: -2.5, r: 2.4 },
    { name: 'Hotspot_Tank_04', x: 8.4, z: -2.5, r: 2.0 },
  ]

  for (const t of tankLayout) {
    const shell = new THREE.Mesh(new THREE.CylinderGeometry(t.r, t.r, 1.4, 48), tankMat)
    shell.position.set(t.x, 0.95, t.z)
    shell.castShadow = true
    shell.receiveShadow = true
    group.add(shell)

    const water = new THREE.Mesh(new THREE.CylinderGeometry(t.r - 0.12, t.r - 0.12, 0.9, 48), waterMat)
    water.position.set(t.x, 0.85, t.z)
    group.add(water)

    const anchor = new THREE.Object3D()
    anchor.name = t.name
    anchor.position.set(t.x, 2.2, t.z)
    group.add(anchor)
  }

  // Pump block + sensor stand
  const pumpBody = new THREE.Mesh(new THREE.BoxGeometry(1.6, 1.1, 1.2), frameMat)
  pumpBody.position.set(-10.5, 0.8, 4.5)
  pumpBody.castShadow = true
  group.add(pumpBody)
  const pumpMotor = new THREE.Mesh(new THREE.CylinderGeometry(0.35, 0.35, 0.8, 24), wallMat)
  pumpMotor.rotation.z = Math.PI / 2
  pumpMotor.position.set(-10.5, 1.5, 4.5)
  group.add(pumpMotor)

  const pumpAnchor = new THREE.Object3D()
  pumpAnchor.name = 'Hotspot_Pump_01'
  pumpAnchor.position.set(-10.5, 2.3, 4.5)
  group.add(pumpAnchor)

  const sensorPole = new THREE.Mesh(new THREE.CylinderGeometry(0.08, 0.08, 2.2, 12), frameMat)
  sensorPole.position.set(0.4, 1.3, 3.8)
  group.add(sensorPole)
  const sensorHead = new THREE.Mesh(new THREE.SphereGeometry(0.22, 16, 16), wallMat)
  sensorHead.position.set(0.4, 2.5, 3.8)
  group.add(sensorHead)

  const sensorAnchor = new THREE.Object3D()
  sensorAnchor.name = 'Hotspot_Sensor_DO_01'
  sensorAnchor.position.set(0.4, 2.9, 3.8)
  group.add(sensorAnchor)

  // Walkway
  const walk = new THREE.Mesh(new THREE.BoxGeometry(22, 0.12, 2.2), deckMat)
  walk.position.set(0.5, 0.32, 3.8)
  walk.receiveShadow = true
  group.add(walk)

  return group
}
