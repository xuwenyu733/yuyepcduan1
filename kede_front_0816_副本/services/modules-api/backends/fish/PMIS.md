# 鱼病系统 ↔ PMIS 对接说明

## 配置

`services/modules-api/.env`（从 `.env.example` 复制）

```
PMIS_ENABLED=true
PMIS_BASE_URL=http://127.0.0.1:8088/zx/datainf/mysql
```

本地需先启动桌面 Tomcat（`启动PMIS本地环境.command`）。回切远程可改为  
`https://www.wiidu.com.cn/zx/datainf/mysql` 或 `https://dev.kdaqua.cn/zx/datainf/mysql`。

设为 `PMIS_ENABLED=false` 可回退本地内存 mock。

前端「任务与计划 / 报警」走同源 `/wiidu-datainf/`（开发 vue-devServer、静态包靠 nginx），  
勿直连 `127.0.0.1:8088`（局域网同事机器上会打到对方本机）。  
`VUE_APP_PMIS_PROXY_TARGET` 仅给 `/wiidu-datainf` 用（默认 `http://127.0.0.1:8088/zx/datainf`）；  
`PMIS`/`list`/`zx` 代理用 `VUE_APP_PMIS_ORIGIN`（默认 `http://127.0.0.1:8088`）。

## 既有表（只读，绝不改写）

| 用途 | 表 |
|------|-----|
| 鱼池/区域 | `test_table_aquaculture_location` |
| 实时水质 | `test_table_water_quality_realtime` |
| 阈值 | `test_table_threshold_config` |
| 设备实时 | `test_table_device_realtime`（预留） |

## 新建专用表（可读写）

均以 `test_table_fd_*` 命名，已在 `forminfo` 注册：

- `fd_species` / `fd_disease_types` / `fd_disease_knowledge`
- `fd_diagnosis_records` / `fd_diagnosis_messages`
- `fd_collection_records` / `fd_archived_cases`
- `fd_equipments` / `fd_operation_logs` / `fd_system_settings`

## 跨表 / 跨库关联键

业务关联**统一用 `biz_id`**，场地关联**统一用与 PMIS 同名的 `pool_code`**。

| 关联场景 | 本系统字段 | 对方 |
|----------|------------|------|
| 诊断 ↔ 水质/区域 | `pool_code` | `water_quality_*` / `aquaculture_location.pool_code` |
| 诊断 ↔ 知识库 | `knowledge_biz_id` / `disease_id` | `fd_disease_knowledge.biz_id` |
| 消息 ↔ 诊断 | `diagnosis_id` | `fd_diagnosis_records.biz_id` |
| 采集/诊断 ↔ 品种 | `species_biz_id` / `fish_species` | `fd_species` / `fish_pool_stock.fish_species` |
| 设备台账 ↔ 实时设备 | `device_code` | `device_realtime.device_code` |
| 外部库 | `source_system` + `external_id` | 对方库主键 |

示例：

```sql
select d.case_no, d.pool_code, w.do_mg_l, loc.workshop
from test_table_fd_diagnosis_records d
left join test_table_water_quality_realtime w
  on w.pool_code = d.pool_code and w.isDel = 0
left join test_table_aquaculture_location loc
  on loc.pool_code = d.pool_code and loc.isDel = 0
where d.isDel = 0
```

## 命令

```bash
cd backend
npm run pmis:bootstrap   # 幂等：建缺失表 + 补关联字段 + 种子
npm run dev              # 启动时自动 bootstrap
```

账号仍用本地登录（如 `陈专家` / `123456`）。
