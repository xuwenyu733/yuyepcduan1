import * as THREE from 'three'

export type FactoryLoadResult = {
  root: THREE.Object3D
  source: 'gltf' | 'procedural' | 'mixed'
  message: string
}

const GLTF_URL = '/models/factory.glb'
const GLTF_B1_URL = '/models/factory1.glb'

function fitAndCenter(object: THREE.Object3D, targetSize = 24): void {
  const box = new THREE.Box3().setFromObject(object)
  if (box.isEmpty()) return
  const size = box.getSize(new THREE.Vector3())
  const maxDim = Math.max(size.x, size.y, size.z, 0.001)
  const scale = targetSize / maxDim
  object.scale.multiplyScalar(scale)
  object.updateMatrixWorld(true)
  const box2 = new THREE.Box3().setFromObject(object)
  const center2 = box2.getCenter(new THREE.Vector3())
  const minY = box2.min.y
  object.position.x -= center2.x
  object.position.z -= center2.z
  object.position.y -= minY
}

function prepareMeshes(root: THREE.Object3D): void {
  root.traverse((obj) => {
    const mesh = obj as THREE.Mesh
    if (!mesh.isMesh) return
    mesh.castShadow = true
    mesh.receiveShadow = true
    const mats = Array.isArray(mesh.material) ? mesh.material : [mesh.material]
    for (const m of mats) {
      const std = m as THREE.MeshStandardMaterial
      if (std && 'roughness' in std) {
        std.roughness = Math.max(std.roughness ?? 0.7, 0.55)
      }
    }
  })
}

async function tryLoadGltf(url: string, name: string): Promise<THREE.Object3D | null> {
  let buffer: ArrayBuffer
  try {
    const res = await fetch(url)
    if (!res.ok) return null
    const ct = res.headers.get('content-type') ?? ''
    if (ct.includes('text/html')) return null
    buffer = await res.arrayBuffer()
    if (buffer.byteLength < 12) return null
  } catch {
    return null
  }

  // Lazy-load GLTFLoader only when a real asset exists
  const { GLTFLoader } = await import('three/addons/loaders/GLTFLoader.js')
  return new Promise((resolve) => {
    const loader = new GLTFLoader()
    loader.parse(
      buffer,
      '',
      (gltf) => {
        const model = gltf.scene
        model.name = name
        prepareMeshes(model)
        fitAndCenter(model)
        resolve(model)
      },
      () => resolve(null),
    )
  })
}

async function loadPlant0(): Promise<{ node: THREE.Object3D; fromGltf: boolean }> {
  const gltf = await tryLoadGltf(GLTF_URL, 'FactoryGLTF')
  if (gltf) return { node: gltf, fromGltf: true }
  const { createProceduralFactory } = await import('./proceduralFactory')
  return { node: createProceduralFactory(), fromGltf: false }
}

async function loadPlant1(): Promise<{ node: THREE.Object3D; fromGltf: boolean }> {
  const gltf = await tryLoadGltf(GLTF_B1_URL, 'Factory1GLTF')
  if (gltf) return { node: gltf, fromGltf: true }
  const { createProceduralFactory1 } = await import('./proceduralFactory1')
  return { node: createProceduralFactory1(), fromGltf: false }
}

/**
 * 厂区加载：
 * 1) 先出开放厅（首屏可交互）
 * 2) 再懒加载厂房一号，避免首包卡顿
 */
export async function loadFactory(
  onProgress?: (partial: FactoryLoadResult) => void,
): Promise<FactoryLoadResult> {
  const campus = new THREE.Group()
  campus.name = 'Campus'

  const plant0 = await loadPlant0()
  plant0.node.position.set(-22, 0, 0)
  campus.add(plant0.node)

  const path = new THREE.Mesh(
    new THREE.BoxGeometry(18, 0.08, 4),
    new THREE.MeshStandardMaterial({ color: 0xb8c2b8, roughness: 0.95 }),
  )
  path.position.set(-1, 0.04, 8)
  path.receiveShadow = true
  campus.add(path)

  onProgress?.({
    root: campus,
    source: plant0.fromGltf ? 'mixed' : 'procedural',
    message: '已加载开放厅，厂房一号懒加载中…',
  })

  // Yield a frame so first paint happens before building #1
  await new Promise<void>((r) => requestAnimationFrame(() => r()))

  const plant1 = await loadPlant1()
  plant1.node.position.set(20, 0, 0)
  campus.add(plant1.node)

  const bothGltf = plant0.fromGltf && plant1.fromGltf
  const bothProc = !plant0.fromGltf && !plant1.fromGltf

  let message: string
  let source: FactoryLoadResult['source']
  if (bothGltf) {
    message = '已加载 factory.glb + factory1.glb'
    source = 'gltf'
  } else if (bothProc) {
    message = '程序化厂区：开放厅 + 厂房一号'
    source = 'procedural'
  } else {
    message = plant1.fromGltf
      ? '厂房一号已加载 glTF，开放厅为程序化'
      : '开放厅已加载 glTF，厂房一号为程序化'
    source = 'mixed'
  }

  return { root: campus, source, message }
}
