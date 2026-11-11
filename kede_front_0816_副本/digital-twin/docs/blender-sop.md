# Blender 白模与 glTF 导出 SOP

输入：`assets/pointcloud/processed/factory_clean.ply`  
输出：
- `public/models/factory.glb`（开放厅，可选）
- `public/models/factory1.glb`（厂房一号，可选）

缺省时前端用程序化模型拼成厂区。

## 1. 导入点云

1. 安装扩展 **Point Cloud I/O**（Blender Extensions：支持 PLY/LAS/PCD 等到原生 PointCloud）
2. `File > Import` 选择处理后的 `factory_clean.ply`
3. 若点数过大导致卡顿，回到 CloudCompare 再降采样一次

> 若只用内置 Stanford PLY 导入，会变成顶点网格，体积大时更吃内存，优先用点云插件。

## 2. 点云转网格（白模）

任选可行路径：

### A. Blender 内「点云转网格」

1. 对点云对象添加 **Geometry Nodes**
2. 使用插件/节点组完成「点云 → 网格」（或 Points to Volume → Volume to Mesh）
3. 末端加 **Realize Instances**（导出 glTF 必需）
4. `Ctrl/Cmd + A` → Visual Geometry to Mesh，得到可编辑网格

### B. CloudCompare Poisson 后进 Blender（效果通常更稳）

1. 在 CloudCompare 对 `factory_clean.ply` 做 Poisson Surface Reconstruction
2. 导出网格（PLY/OBJ）
3. Blender `File > Import` 该网格，再做清理与轻量化

## 3. 清理与轻量化

目标 Demo：`factory.glb` **约 5～20MB**，三角面尽量 **&lt; 50 万**。

1. Edit Mode 删除明显多余面（外围地面碎屑、扫描噪声壳）
2. Modifier：**Decimate**（Collapse / Planar 按形状选择）
3. 合并重叠点：`Mesh > Merge by Distance`
4. 赋简单灰色材质即可（白模 Demo，不必上复杂贴图）

## 4. 交互锚点（必须按此命名）

前端通过空物体名称绑定模拟设备。在对应鱼池/水泵/传感器位置添加 **Empty**，命名：

| 空物体名 | 对应设备 |
| --- | --- |
| `Hotspot_Tank_01` … `Hotspot_Tank_04` | 开放厅鱼池 |
| `Hotspot_Pump_01`、`Hotspot_Sensor_DO_01` | 开放厅泵 / 探头 |
| `Hotspot_B1_Tank_01` … `Hotspot_B1_Tank_06` | 厂房一号跑道池 |
| `Hotspot_B1_Pump_01`、`Hotspot_B1_Filter_01`、`Hotspot_B1_Sensor_DO_01` | 厂房一号泵 / 过滤 / 探头 |

建议：Empty 放在设备上方约 0.5～1.5m，便于相机聚焦与色点显示。

导出时把这些 Empty **与网格一起选中**。

## 5. 导出 glTF

1. 选中厂房网格 + 全部 `Hotspot_*` Empty
2. `File > Export > glTF 2.0 (.glb)`
3. 勾选：
   - Format: **glTF Binary (.glb)**
   - Include: Selected Objects
   - Transform: **+Y Up**
   - Geometry: **Apply Modifiers**
4. 保存到：

```
public/models/factory.glb   # 开放厅
public/models/factory1.glb  # 厂房一号
```

也可先存一份备份到 `assets/models/`。

## 6. 验证

```bash
npm run dev
```

顶部状态应为「已加载 factory.glb」。  
点击侧栏设备或场景色点，相机会聚焦到对应锚点。

若仍提示程序化厂房：检查文件路径是否为 `public/models/factory.glb`，并硬刷新浏览器。
