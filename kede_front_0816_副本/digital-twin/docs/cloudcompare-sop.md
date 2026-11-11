# CloudCompare 处理 SOP（养鱼工厂点云 Demo）

本机 GUI 操作清单。目标产出：`assets/pointcloud/processed/factory_clean.ply`。

## 0. 准备

### 推荐数据源

1. **主选** [NORA](https://doi.org/10.5281/zenodo.15269128) — 下载 `nora.ply`（已合并工业管廊/设备点云）
2. **备选** [IRIS](https://doi.org/10.5281/zenodo.18671364) — `point_cloud_stations_merged_zone*_density_10%.ply`
3. **兜底** [Warehouse LiDAR](https://github.com/anavsgmbh/lidar-warehouse-dataset) dense 点云

### 目录约定

```
assets/pointcloud/raw/          ← 原始下载文件
assets/pointcloud/processed/    ← CloudCompare 导出
```

将原始文件放入 `raw/`，例如 `assets/pointcloud/raw/nora.ply`。

## 1. 导入

1. 打开 CloudCompare
2. `File > Open` 选择 `raw/` 下的 PLY / LAS / E57
3. 若出现缩放/单位提示，保持默认即可，后续可统一缩放

## 2. 自动去噪

任选其一（建议都试，看哪个更干净）：

- `Tools > Clean > Noise filter`
- 或 `Tools > Clean > SOR filter`（Statistical Outlier Removal）
  - 常见起点：`Nb of neighbors = 6~12`，`Std. ratio = 1.0~1.5`

处理完成后，用新实体继续后续步骤（可隐藏原始层对照）。

## 3. 裁剪无关点

去掉厂房外地面、天空噪点、扫描架、远处杂物：

1. 选中点云 → 工具栏 **Segment**（剪刀）或 `Edit > Segment`
2. 多边形框选保留区域 → `Segment in` / `Segment out`
3. 多视角（顶视 + 侧视）检查，重复裁剪直到主体清晰

## 4. 多站拼接（仅多文件时）

若下载的是分站点云：

1. 先用已知靶球/粗对齐，或 `Tools > Registration > Align`
2. 精配准：`Tools > Registration > Fine registration (ICP)`
3. 全部对齐后：选中各站 → `Edit > Merge`

单文件已合并数据集（如 NORA `nora.ply`）可跳过本步。

## 5. 下采样（强烈建议）

为 Blender / 浏览器可控：

1. `Edit > Subsample`
2. 选 **Space**（体素）模式
3. Demo 建议先降到约 **50 万～150 万点**（视机器性能调整）

## 6. 法线（利于网格化）

1. 选中处理后的点云
2. `Tools > Normals > Compute`
3. 邻域半径按点间距的 2～4 倍试

## 7. 导出

1. 选中最终实体
2. `File > Save` → `PLY` 二进制
3. 保存为：

```
assets/pointcloud/processed/factory_clean.ply
```

### 检查清单

- [ ] 明显飞点/离群点已清除
- [ ] 厂房主体完整，无大块无关背景
- [ ] 点数已下采样到可交互量级
- [ ] 已计算法线并导出到 `processed/`

可选：若 Blender 点云转网格效果差，可在 CloudCompare 做  
`Plugins > PoissonRecon`（或 Mesh 菜单中的 Poisson）生成网格，再导入 Blender 做轻量化与锚点。
