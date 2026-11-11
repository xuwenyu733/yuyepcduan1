# 科德主仓库 · 专家会议（rtc_kede）集成说明

本目录由桌面 `rtc_kede` 迁入主仓库，与 `kede_front_0816` / `kd-backend2` 并列。

## 目录关系

```
测试_副本6 2/
  kede_front_0816/   # 渔业主前端（Vue2），iframe 嵌入会议
  rtc_kede/          # 腾讯 TRTC / TUIRoomKit（Vue3 + Vite）
  kd-backend2/       # 后端
```

专家会议页：`kede_front_0816/src/views/fish/zkMeeting/remote_meeting.vue`  
iframe 地址由环境变量 `VUE_APP_MEETING_PC` 控制。

## 推荐：静态编译（避免 npm run dev）

腾讯 RTC 可 `vite build` 成静态 HTML。日常联调请用编译版，由渔业前端同域托管，不占 :9391：

```bash
# 仓库根目录
bash scripts/build-rtc-static.sh
# → 输出到 kede_front_0816/public/rtc_kede/
```

`kede_front_0816/.env.local`：

```bash
VUE_APP_MEETING_PC = '/rtc_kede'
```

然后只开渔业前端：

```bash
cd kede_front_0816 && npm run local
# iframe → http://localhost:4984/rtc_kede/index.html#/...
```

改完 `rtc_kede` 源码后重新执行一次 `build-rtc-static.sh` 即可。

## 可选：Vite 热更新

仅在改会议 UI 需要 HMR 时使用：

```bash
bash scripts/dev-rtc.sh   # → http://localhost:9391
# 临时把 VUE_APP_MEETING_PC 改成 http://localhost:9391
```

## 线上

可将静态包挂到会议域名，或继续同域 `/rtc_kede`：

```bash
VUE_APP_MEETING_PC = 'https://t4.kedeyuye.online'
# 或
VUE_APP_MEETING_PC = '/rtc_kede'
```

## 说明

- 不修改桌面原 `~/Desktop/rtc_kede`；以仓库内副本为准继续开发。
- `node_modules` 已在根 `.gitignore` 中忽略，克隆后需在本目录执行 `npm install`。
- `public/rtc_kede` 为构建产物；未生成时专家会议 iframe 会 404，请先跑 build 脚本。
