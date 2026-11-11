# 风味优化 ↔ PMIS 对接说明

与鱼病系统同一套守则：既有表只读；缺失则新建 `fo_*`；结构带跨库关联键。

## 配置

`backend/.env`：`PMIS_ENABLED=true`

## 既有表（只读）

- `test_table_aquaculture_location`
- `test_table_water_quality_realtime`
- `test_table_threshold_config`

## 新建表（可读写）

| 表 | 用途 |
|----|------|
| `test_table_fo_sys_config` | 单位等配置 |
| `test_table_fo_dict` | 品类/来源/规格/运输字典 |
| `test_table_fo_pond` | 鱼池档案（含 `pool_code`） |
| `test_table_fo_purchase` | 采购单 |
| `test_table_fo_entry` | 入池/出池批次 |
| `test_table_fo_opt_log` | 优化监测日志 |

关联键：`biz_id`、`pool_code`、`purchase_biz_id`、`entry_biz_id`、`source_system`、`external_id`

## 命令

```bash
cd backend
npm run pmis:bootstrap
npm run dev
```
