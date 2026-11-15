# 智能图文录入与查询

## 使用方式

登录后，点击右下角“智慧养殖 AI 助手”。在助手底部切换“查询分析”和“录入数据”。

录入区与查询区共用同一个助手窗口，使用相同的深蓝色样式。填写资料标题后，可以保存文字、图片或二者组合。支持选择、拖入、粘贴 PNG、JPEG、GIF、WebP 图片，最多 6 张，单张不超过 5 MB，合计不超过 20 MB。标题最多 160 字符，文字说明最多 20,000 字符。

在当前页面切换查询或收起助手，未保存的录入草稿会保留。刷新页面或退出账号会清除未保存草稿。保存成功后，在“已存资料”查看文字与原图；原图通过带登录认证的接口按需加载。

查询结果包含：

1. 数据库已有内容：图文资料、知识库内容，以及按现有部门权限过滤的鱼池概览。
2. 联网查询相关内容：链接、来源与检索服务实际返回的摘要。
3. 数据库与网络综合总结：模型可用时综合两类证据；未配置或调用失败时明确标注为摘录汇总。

## 数据库迁移

在 Java 后端连接的现有业务数据库中执行：

`kd-backend2 2/yudao-module-fish/src/main/resources/db/smart-entry.sql`

脚本使用 `CREATE TABLE IF NOT EXISTS` 新增以下两张表，不包含业务数据或旧表重建操作：

| 表 | 内容 |
| --- | --- |
| `zk_smart_entry` | 标题、文字、所属组织、录入人、时间、用于重试去重的请求编号 |
| `zk_smart_entry_image` | 图片原始字节、文件名、类型、大小、SHA-256、所属记录与组织 |

迁移需要手动执行；应用启动不会自动创建这些表。查询已有知识时还会读取原项目的 `zk_konwledge_tree`，查询鱼池时复用原项目的鱼池服务。

## 服务配置

本次本地运行使用以下地址。部署到其他环境时，需要统一调整代理目标和服务端口：

| 配置或服务 | 本地值 | 用途 |
| --- | --- | --- |
| Java 后端 | `http://127.0.0.1:48181` | 登录认证、资料保存和数据库查询 |
| AI 小科 Vite 服务 | `http://127.0.0.1:5190` | `/llm/database/chat/stream`，组织数据库检索、联网检索与总结 |
| 前端编译预览 | `http://127.0.0.1:4985` | `scripts/serve-local.cjs` 提供静态资源与代理 |
| 前端 `VUE_APP_BASE_API` | `/proxy-api` | 前端调用 Java 接口的代理前缀 |
| 小科 `SMART_ENTRY_BACKEND` | `http://127.0.0.1:48181` | 小科调用 Java 查询接口，默认值也是此地址 |

前端必须把 `/llm` 转发到运行 `llmProxyPlugin` 的小科服务，把 `/proxy-api` 转发到 Java 并移除该前缀。仅托管小科的静态构建文件不会启动其服务端中间件。

本地私有配置放入各应用的 `.env.local` 或外置后端配置，保持这些文件不入 Git。前端需要正常的组织与登录配置；数据库账号、密码、模型密钥由部署环境提供。

Java 的 multipart 限制需要允许本功能的上传上限，例如：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 22MB
```

模型总结由小科服务的 `LLM_API_KEY`、`LLM_BASE_URL`、`LLM_MODEL` 配置控制。未配置有效密钥时，仍可显示数据库结果与可用的网络结果，第三块显示资料摘录汇总。联网检索需要服务端能访问所用检索服务。

## 构建与验证

本次本地前端使用 Node.js 20.20.2 构建，Java 使用 JDK 17。安装各应用自己的依赖；前端、小科以及模块服务的依赖目录相互独立。

在 `kede_front_0816_副本` 中，已有本地环境配置时可执行：

```powershell
$env:NODE_ENV = 'production'
$env:NODE_OPTIONS = '--openssl-legacy-provider --max-old-space-size=4096'
$env:YUYE_BUILD_WORKERS = '1'
$env:YUYE_LOCAL_PREVIEW = '1'
npx vue-cli-service build --mode local
```

`YUYE_BUILD_WORKERS=1` 限制构建并行度；`YUYE_LOCAL_PREVIEW=1` 关闭本地预览构建中的 CSS 优化插件。编译预览脚本为 `kede_front_0816_副本/scripts/serve-local.cjs`，它还会使用 `services/modules-api` 中安装的 Express 依赖，相关服务须按脚本中的目标地址启动。

在仓库根目录执行联网结果解析测试：

```powershell
node --test ai-xiaoke/app/knowledge/webSearch.test.js
```

在 `kd-backend2 2` 中执行本功能相关的 Java 单元测试：

```powershell
mvn -pl yudao-server -am '-Dtest=SmartEntryServiceTest,LoggingMultipartTest' '-DskipTests=false' '-Dsurefire.failIfNoSpecifiedTests=false' test
```

验证范围包括关键词处理、组织隔离、图片类型检查、鱼池权限服务复用和 multipart 日志处理。这些单元测试使用模拟依赖，不需要真实业务数据库。

2026-11-12 的内嵌录入界面验收使用隔离浏览器和模拟接口，检查了原位切换、草稿保留、三种窗口宽度、失败重试、保存结果与图片显示、三块查询结果和刷新后的列表读取。该轮界面验收没有重新写入真实业务数据库。

## 当前范围

- 图片保存原件，不包含 OCR 或图片内容自动识别；检索依赖标题和文字说明。
- 数据库查询目前覆盖上述图文资料、知识库与鱼池概览，不覆盖所有业务表，也不等同于任意 SQL 分析。
- 查询采用关键词匹配和数量限制，长文本在结果与总结中会被截取；完整资料可在“已存资料”查看。
- 语音识别与数字人使用原项目的独立配置和外部服务，本次图文录入改动不包含这些服务的修复。
