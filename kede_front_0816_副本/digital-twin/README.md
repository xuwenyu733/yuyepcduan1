# 养鱼工厂点云数字孪生 Demo

本目录已并入主前端 `kede_front(3)`：生产构建输出到 `../public/digital-twin/`，系统总览「3D数字孪生」按钮全页打开（含入口预取）。

公开工业点云 → CloudCompare 清洗 → Blender 白模 → glTF → Three.js 加载，并绑定模拟设备遥测与侧栏交互。

## 快速开始

独立开发：

```bash
npm install
npm run web
```

在主前端仓库根目录：

```bash
npm run twin:install
npm run twin:build   # 写入 public/digital-twin
npm run twin:api     # 可选数据发生器 :8787
```

主站开发时访问：`http://localhost:4984/digital-twin/`（未起 API 时自动本地模拟）。

会同时启动：
- **数据发生器 API**（`http://127.0.0.1:8787`）— 每 2 秒向本地 SQLite `INSERT` 全设备遥测
- **前端**（Vite）— 代理 `/api` 到发生器，侧栏动态刷新

只跑前端：`npm run web`（无 API 时自动本地模拟）  
只跑发生器：`npm run api`

浏览器打开终端提示的本地地址。未放置 glTF 时，会自动使用**程序化厂区**（开放厅 + **厂房一号**），交互与数据绑定可先演示。

### 设备与面板

- 类型筛选：鱼池 / 水泵 / 传感器 / 摄像头 / 温度计 / 开关 / 投喂 / 增氧 / 过滤
- 点击设备后 Tab：**详情**（实时指标+快捷操作）、**选项**（参数表）、**场景**（位置说明/摄像头预览）、**动态**（入库历史）

### 本地数据库

- 文件：`data/twin_telemetry.sqlite`
- 表：`device_telemetry`（字段对齐养殖业务：`device_code` / `pool_code` / `workshop` 等）
- 健康检查：`GET /api/health`
- 最新快照：`GET /api/devices/latest`
- 历史：`GET /api/devices/:code/history`
- 操作：`POST /api/devices/:id/action` `{ "action": "toggle_power" | ... }`

## 流水线

1. 下载公开点云到 `assets/pointcloud/raw/`（见下方数据源）
2. 按 [docs/cloudcompare-sop.md](docs/cloudcompare-sop.md) 去噪、裁剪、拼接、下采样
3. 按 [docs/blender-sop.md](docs/blender-sop.md) 转网格、轻量化、放置 `Hotspot_*`、导出 glTF
4. 将模型放到 `public/models/`（`factory.glb` / `factory1.glb`），刷新即可切换到真实白模

```
点云(raw) → CloudCompare → processed/*.ply → Blender → public/models/factory.glb → Three.js
```

## 点云数据源

| 优先级 | 数据集 | 说明 |
| --- | --- | --- |
| 主选 | [NORA](https://doi.org/10.5281/zenodo.15269128) `nora.ply` | 工业设备 mock-up，已合并 |
| 备选 | [IRIS](https://doi.org/10.5281/zenodo.18671364) merged zone 10% | 工业房间，密度已降 |
| 兜底 | [Warehouse LiDAR](https://github.com/anavsgmbh/lidar-warehouse-dataset) | 仓储工业场景 |

## 前端能力

- OrbitControls 漫游
- 按 `Hotspot_*` 锚点挂载可点击色点
- 模拟水温 / 溶氧 / pH / 液位 / 投喂 / 报警，约 1.5s 刷新
- 侧栏列表与详情；点击聚焦相机

## 目录结构

```
public/models/factory.glb     # Blender 导出（你来放）
assets/pointcloud/raw/        # 原始点云
assets/pointcloud/processed/  # CloudCompare 产出
assets/models/                # 模型备份（可选）
docs/cloudcompare-sop.md
docs/blender-sop.md
src/
  main.ts
  data/mockDevices.ts
  scene/…                     # 场景、加载、热点、程序化回退
  ui/…                        # 侧栏与样式
```

## 脚本

| 命令 | 说明 |
| --- | --- |
| `npm run dev` | 本地开发 |
| `npm run build` | 类型检查 + 生产构建 |
| `npm run preview` | 预览构建结果 |

本期为 Demo：**不使用 Revit/BIM**，设备数据为前端模拟，未对接真实 PMIS。
