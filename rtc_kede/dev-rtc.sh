#!/usr/bin/env bash
# 启动仓库内专家会议 RTC（方案 1 子项目）
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/rtc_kede"
if [ ! -d node_modules ]; then
  echo "[rtc_kede] installing dependencies..."
  npm install --legacy-peer-deps
fi
echo "[rtc_kede] http://localhost:9391"
npm run dev
