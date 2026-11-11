import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { debounce } from '../utils/timing'

export type TwinScene = {
  scene: THREE.Scene
  camera: THREE.PerspectiveCamera
  renderer: THREE.WebGLRenderer
  controls: OrbitControls
  root: THREE.Group
}

export function createScene(canvas: HTMLCanvasElement): TwinScene {
  const scene = new THREE.Scene()
  scene.background = new THREE.Color(0xd8e4ec)
  scene.fog = new THREE.Fog(0xd8e4ec, 40, 120)

  const camera = new THREE.PerspectiveCamera(50, 1, 0.1, 500)
  camera.position.set(8, 28, 48)

  const renderer = new THREE.WebGLRenderer({ canvas, antialias: true })
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.shadowMap.enabled = true
  renderer.shadowMap.type = THREE.PCFSoftShadowMap
  renderer.outputColorSpace = THREE.SRGBColorSpace

  const controls = new OrbitControls(camera, canvas)
  controls.enableDamping = true
  controls.dampingFactor = 0.06
  controls.maxPolarAngle = Math.PI * 0.49
  controls.minDistance = 3
  controls.maxDistance = 120
  controls.target.set(0, 2, 0)

  const hemi = new THREE.HemisphereLight(0xf5f8ff, 0x8a9a88, 1.05)
  scene.add(hemi)

  const sun = new THREE.DirectionalLight(0xfff4e0, 1.35)
  sun.position.set(20, 28, 12)
  sun.castShadow = true
  sun.shadow.mapSize.set(2048, 2048)
  sun.shadow.camera.near = 1
  sun.shadow.camera.far = 100
  sun.shadow.camera.left = -40
  sun.shadow.camera.right = 40
  sun.shadow.camera.top = 40
  sun.shadow.camera.bottom = -40
  scene.add(sun)

  const fill = new THREE.DirectionalLight(0xc5e0f2, 0.55)
  fill.position.set(-12, 14, -8)
  scene.add(fill)

  const ambient = new THREE.AmbientLight(0xffffff, 0.28)
  scene.add(ambient)

  const ground = new THREE.Mesh(
    new THREE.PlaneGeometry(120, 120),
    new THREE.MeshStandardMaterial({ color: 0xc5d0c2, roughness: 0.95 }),
  )
  ground.rotation.x = -Math.PI / 2
  ground.receiveShadow = true
  scene.add(ground)

  const grid = new THREE.GridHelper(80, 40, 0x9aab9a, 0xb4c2b4)
  grid.position.y = 0.01
  scene.add(grid)

  const root = new THREE.Group()
  root.name = 'FactoryRoot'
  scene.add(root)

  const resizeNow = () => {
    const w = canvas.clientWidth
    const h = canvas.clientHeight
    if (w === 0 || h === 0) return
    camera.aspect = w / h
    camera.updateProjectionMatrix()
    renderer.setSize(w, h, false)
  }
  resizeNow()
  const resize = debounce(resizeNow, 120)
  window.addEventListener('resize', resize)

  return { scene, camera, renderer, controls, root }
}
