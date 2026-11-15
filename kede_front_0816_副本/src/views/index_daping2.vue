<template>
  <div class="dashboard-wrapper">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-bar">
        <!-- 左侧：首页按钮 + 导航 -->
        <div class="header-left">
          <button class="nav-btn home" @click="toPage('/index_daping1')" title="返回首页">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          </button>

          <div class="divider"></div>

          <button class="nav-btn btn-cyan" @click="toPage('/index')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
            <span>数字化监控</span>
          </button>
        </div>

        <!-- 中间：标题（粉紫摄像头图标必须紧挨「视频监控系统」，勿放回左侧导航） -->
        <div class="header-center">
          <div class="title-wrap" title="视频监控系统">
            <div class="logo" aria-hidden="true">
              <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m22 8-6 4 6 4V8Z"/><rect width="14" height="12" x="2" y="6" rx="2" ry="2"/></svg>
            </div>
            <h1>视频监控系统</h1>
          </div>
        </div>

        <!-- 右侧：快捷入口 -->
        <div class="header-right">
          <div class="quick-btns">
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>存量分布</span>
            </button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
              <span>智能巡检</span>
            </button>
            <button class="q-btn btn-cyan" @click="toPage('/keyshow/inspectionreport')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
              <span>分析报告</span>
            </button>
            <button class="q-btn btn-pink" @click="toPage('/fish-disease')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              <span>鱼病诊断</span>
            </button>
            <button class="q-btn btn-indigo" @click="toPage('/knowledge/zk-meeting-expert')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              <span>专家会议</span>
            </button>
            <button class="q-btn btn-teal" @click="toPage('/water_quality')">
              <span>水质监测</span>
            </button>
            <button class="q-btn btn-teal" @click="toPage('/device_monitor')">
              <span>设备监控</span>
            </button>
            <button class="q-btn btn-teal" @click="toPage('/alert_monitor')">
              <span>报警监控</span>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-content">
        <span class="filter-label">筛选条件：</span>

        <div class="filter-item">
          <span class="label">车间：</span>
          <select v-model="filterWorkshop" class="select-input">
            <option value="all">全部</option>
            <option v-for="(o, wi) in workshopFilterOptions" :key="'ws-' + o.value + '-' + wi" :value="o.value">{{ o.label }}</option>
          </select>
        </div>

        <div class="filter-item">
          <span class="label">系统：</span>
          <select v-model="filterSystem" class="select-input">
            <option value="all">全部</option>
            <option v-for="(o, si) in systemFilterOptions" :key="'sys-' + o.value + '-' + si" :value="o.value">{{ o.label }}</option>
          </select>
        </div>

        <div class="filter-item">
          <span class="label">池号：</span>
          <select v-model="filterPool" class="select-input">
            <option value="all">全部</option>
            <option value="A">A池</option>
            <option value="B">B池</option>
            <option value="C">C池</option>
            <option value="D">D池</option>
            <option value="E">E池</option>
            <option value="F">F池</option>
          </select>
        </div>

        <div class="filter-item">
          <span class="label">位置：</span>
          <select v-model="filterType" class="select-input">
            <option value="all">全部</option>
            <option value="养殖池水上">养殖池水上</option>
            <option value="养殖池水下">养殖池水下</option>
            <option value="设备区">设备区</option>
            <option value="生化池">生化池</option>
            <option value="通道">通道</option>
            <option value="其他">其他</option>
          </select>
        </div>

        <button @click="resetFilter" class="reset-btn">重置筛选</button>

        <div class="grid-mode-btns">
          <span class="label">宫格：</span>
          <button
            v-for="mode in gridModes"
            :key="mode"
            type="button"
            class="grid-mode-btn"
            :class="{ active: gridMode === mode }"
            @click="gridMode = mode"
          >
            {{ mode }}
          </button>
        </div>

        <div class="filter-meta">
          <div class="status-card online">
            <div class="dot animate-pulse"></div>
            <span>{{ onlineCount }}路在线</span>
          </div>
          <div class="status-card offline">
            <div class="dot"></div>
            <span>{{ offlineCount }}路离线</span>
          </div>
          <div class="status-card maintenance">
            <div class="dot"></div>
            <span>{{ maintenanceCount }}路维护中</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 视频网格 -->
    <div class="video-grid-container">
      <div class="video-grid" :style="{ gridTemplateColumns: `repeat(${gridColumns}, minmax(0, 1fr))` }">
        <div v-for="camera in displayedCameras" :key="camera.id" class="video-card" @click="openFullscreen(camera)">
          <div class="video-wrapper">
            <template v-if="camera.status === 'online'">
              <div class="video-online">
                <div class="video-mock">
                  <div class="grid-overlay"></div>
                  <div class="timestamp">{{ currentTimeStr }}</div>
                  <div class="rec-indicator">
                    <svg class="icon animate-pulse" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="2"/><path d="M4.93 19.07a10 10 0 0 1 0-14.14"/><path d="M7.76 16.24a6 6 0 0 1 0-8.48"/><path d="M16.24 7.76a6 6 0 0 1 0 8.48"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14"/></svg>
                    <span>REC</span>
                  </div>
                  <div class="center-icon">
                    <svg class="icon-xl" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m22 8-6 4 6 4V8Z"/><rect width="14" height="12" x="2" y="6" rx="2" ry="2"/></svg>
                  </div>
                  <div v-if="camera.paused" class="paused-badge">
                    <i class="el-icon-video-pause"></i>
                    <span>已暂停</span>
                  </div>
                </div>
              </div>
            </template>
            <template v-else-if="camera.status === 'offline'">
              <div class="video-offline">
                <div class="video-mock">
                  <div class="grid-overlay"></div>
                  <div class="timestamp">{{ currentTimeStr }}</div>
                  <div class="offline-badge">
                    <i class="el-icon-warning-outline"></i>
                    <span>设备离线</span>
                  </div>
                  <div class="center-icon">
                    <svg class="icon-xl" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m22 8-6 4 6 4V8Z"/><rect width="14" height="12" x="2" y="6" rx="2" ry="2"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="video-maintenance">
                <div class="maintenance-content">
                  <div class="icon-box">
                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m22 8-6 4 6 4V8Z"/><rect width="14" height="12" x="2" y="6" rx="2" ry="2"/></svg>
                  </div>
                  <p>维护中</p>
                </div>
              </div>
            </template>

            <div class="bottom-info">
              <div class="info-content">
                <div class="text-info">
                  <p class="name">{{ camera.name }}</p>
                  <p class="location">{{ camera.location }}</p>
                </div>
                <div class="actions">
                  <button class="action-btn" @click.stop="togglePause(camera)">
                    <i v-if="!camera.paused" class="el-icon-video-pause"></i>
                    <i v-else class="el-icon-video-play"></i>
                  </button>
                  <button class="action-btn" @click.stop="takeScreenshot(camera)">
                    <i class="el-icon-camera"></i>
                  </button>
                  <button class="action-btn" @click.stop="openPlayback(camera)">
                    <i class="el-icon-refresh-right"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 全屏弹窗 -->
    <div v-if="fullscreenCameraId !== null" class="fullscreen-overlay" @click.self="exitFullscreen">
      <div class="fullscreen-container">
        <div class="fs-header">
          <div class="fs-title-row">
            <span class="fs-title">{{ currentFullscreenCamera.name }}</span>
            <span class="fs-status-tag" :class="currentFullscreenCamera.status">
              <span class="dot"></span>
              {{ currentFullscreenCamera.status === 'online' ? '在线' : currentFullscreenCamera.status === 'offline' ? '离线' : '维护中' }}
            </span>
          </div>
          <button class="fs-close" @click="exitFullscreen">
            <i class="el-icon-close"></i>
          </button>
        </div>

        <!-- 视频区：在线用萤石云播放器，离线用全新界面，维护保持不变 -->
        <div class="fs-video-area">
          <template v-if="currentFullscreenCamera.status === 'online'">
            <div ref="ezPlayerMount" class="ez-player-mount"></div>
            <div v-if="currentFullscreenCamera.paused" class="fs-paused-overlay">
              <i class="el-icon-video-pause"></i>
              <span>已暂停</span>
            </div>
            <div class="fs-rec-badge">
              <span class="rec-dot"></span>
              <span>REC</span>
            </div>
            <div class="fs-timestamp">{{ currentTimeStr }}</div>
          </template>
          <template v-else-if="currentFullscreenCamera.status === 'offline'">
            <!-- 新的离线全屏设计 -->
            <div class="fs-offline-container">
              <div class="fs-offline-bg">
                <div class="grid-overlay"></div>
              </div>
              <div class="fs-offline-content">
                <div class="offline-icon-wrap">
                  <svg class="offline-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="m22 8-6 4 6 4V8Z"/><rect width="14" height="12" x="2" y="6" rx="2" ry="2"/><line x1="1" y1="1" x2="23" y2="23"/>
                  </svg>
                </div>
                <div class="offline-text">设备离线</div>
                <div class="offline-sub">无法获取视频流，请检查设备连接</div>
              </div>
              <div class="fs-timestamp">{{ currentTimeStr }}</div>
            </div>
          </template>
          <template v-else>
            <div class="video-maintenance" style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;">
              <div class="maintenance-content">
                <div class="icon-box">
                  <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m22 8-6 4 6 4V8Z"/><rect width="14" height="12" x="2" y="6" rx="2" ry="2"/></svg>
                </div>
                <p>维护中</p>
              </div>
            </div>
          </template>
        </div>

        <div class="fs-controls-bar">
          <div class="fs-btns">
            <button class="fs-btn" :class="{ active: currentFullscreenCamera.paused }" @click="togglePause(currentFullscreenCamera)">
              <i v-if="!currentFullscreenCamera.paused" class="el-icon-video-pause"></i>
              <i v-else class="el-icon-video-play"></i>
              <span>{{ currentFullscreenCamera.paused ? '播放' : '暂停' }}</span>
            </button>
            <button class="fs-btn" @click="takeScreenshot(currentFullscreenCamera)">
              <i class="el-icon-camera"></i>
              <span>抓拍</span>
            </button>
            <button class="fs-btn" :class="{ active: playbackCameraId === currentFullscreenCamera.id }" @click="openPlayback(currentFullscreenCamera)">
              <i class="el-icon-refresh-right"></i>
              <span>回放</span>
            </button>
            <button class="fs-btn" @click="exitFullscreen">
              <i class="el-icon-close"></i>
              <span>退出全屏</span>
            </button>
          </div>
          <div v-if="playbackCameraId === currentFullscreenCamera.id" class="playback-row">
            <input type="datetime-local" v-model="playbackStartTime" class="playback-input">
            <span class="to-label">至</span>
            <input type="datetime-local" v-model="playbackEndTime" class="playback-input">
            <button class="fs-btn btn-primary" @click="confirmPlayback(currentFullscreenCamera)">
              <i class="el-icon-video-play"></i>
              <span>开始回放</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { EZUIKitPlayer } from "ezuikit-js"
import service from '@/utils/request'
import { formatWorkshop, formatSystem, workshopsEqual, systemsEqual } from '@/utils/siteNaming'
import {
  loadSharedSiteOptions as fetchSiteOptions,
  peekSharedSiteOptions,
  pickWorkshopOptionLabels,
  pickSystemOptionLabels,
  isRowInUserSiteScope,
  resolveCurrentUserDeptId,
  SITE_OPTIONS_CHANGED_EVENT
} from '@/utils/siteOptions'
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope'

/** 演示摄像头字段（车间/系统用 1/none）转成与站点白名单可比的行 */
function cameraToScopeRow(camera) {
  if (!camera) return null
  const wsRaw = camera.workshop
  const sysRaw = camera.system
  const wsEmpty = !wsRaw || wsRaw === 'none' || wsRaw === 'all'
  const sysEmpty = !sysRaw || sysRaw === 'none' || sysRaw === 'all'
  const workshop = wsEmpty ? '' : (formatWorkshop(wsRaw) || String(wsRaw))
  const system = sysEmpty ? '' : (formatSystem(sysRaw) || String(sysRaw))
  return {
    workshop,
    workshopName: workshop,
    system,
    systemName: system,
    pondSystemName: system
  }
}

export default {
  name: "IndexDaping2",
  data() {
    return {
      filterWorkshop: 'all',
      filterSystem: 'all',
      filterPool: 'all',
      filterType: 'all',
      gridMode: 16,
      gridModes: [1, 4, 9, 16],
      currentTime: new Date(),
      timer: null,
      playbackCameraId: null,
      playbackStartTime: '',
      playbackEndTime: '',
      fullscreenCameraId: null,
      ezPlayer: null,
      ezAccessToken: '',
      sharedSiteOptions: peekSharedSiteOptions() || {
        workshops: [], systems: [], ponds: [],
        workshopOptions: [], systemOptions: [], pondOptions: []
      },
      allCameras: [
        { id: 1, name: '1号车间-1号系统-A池-水上', location: '1号养殖车间', workshop: '1', system: '1', pool: 'A', type: '养殖池水上', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 1 },
        { id: 2, name: '1号车间-1号系统-A池-水下', location: '1号养殖车间', workshop: '1', system: '1', pool: 'A', type: '养殖池水下', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 2 },
        { id: 3, name: '1号车间-1号系统-B池-水上', location: '1号养殖车间', workshop: '1', system: '1', pool: 'B', type: '养殖池水上', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 3 },
        { id: 4, name: '1号车间-2号系统-C池-水下', location: '1号养殖车间', workshop: '1', system: '2', pool: 'C', type: '养殖池水下', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 4 },
        { id: 5, name: '1号车间-设备区', location: '1号养殖车间', workshop: '1', system: 'none', pool: 'none', type: '设备区', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 5 },
        { id: 6, name: '1号车间-通道', location: '1号养殖车间', workshop: '1', system: 'none', pool: 'none', type: '通道', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 6 },
        { id: 7, name: '2号车间-1号系统-A池-水上', location: '2号养殖车间', workshop: '2', system: '1', pool: 'A', type: '养殖池水上', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 7 },
        { id: 8, name: '2号车间-1号系统-D池-水下', location: '2号养殖车间', workshop: '2', system: '1', pool: 'D', type: '养殖池水下', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 8 },
        { id: 9, name: '2号车间-2号系统-E池-水上', location: '2号养殖车间', workshop: '2', system: '2', pool: 'E', type: '养殖池水上', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 9 },
        { id: 10, name: '2号车间-设备区', location: '2号养殖车间', workshop: '2', system: 'none', pool: 'none', type: '设备区', status: 'offline', paused: false, deviceSerial: '', channelNo: 1 },
        { id: 11, name: '2号车间-通道', location: '2号养殖车间', workshop: '2', system: 'none', pool: 'none', type: '通道', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 10 },
        { id: 12, name: '3号车间-1号系统-A池-水上', location: '3号养殖车间', workshop: '3', system: '1', pool: 'A', type: '养殖池水上', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 11 },
        { id: 13, name: '3号车间-3号系统-F池-水下', location: '3号养殖车间', workshop: '3', system: '3', pool: 'F', type: '养殖池水下', status: 'offline', paused: false, deviceSerial: '', channelNo: 1 },
        { id: 14, name: '3号车间-设备区', location: '3号养殖车间', workshop: '3', system: 'none', pool: 'none', type: '设备区', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 12 },
        { id: 15, name: '3号车间-通道', location: '3号养殖车间', workshop: '3', system: 'none', pool: 'none', type: '通道', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 13 },
        { id: 16, name: '生化池-1号', location: '循环水处理系统', workshop: 'none', system: 'none', pool: 'none', type: '生化池', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 14 },
        { id: 17, name: '生化池-2号', location: '循环水处理系统', workshop: 'none', system: 'none', pool: 'none', type: '生化池', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 15 },
        { id: 18, name: '生化池-3号', location: '循环水处理系统', workshop: 'none', system: 'none', pool: 'none', type: '生化池', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 16 },
        { id: 19, name: '厂区大门', location: '厂区入口', workshop: 'none', system: 'none', pool: 'none', type: '其他', status: 'online', paused: false, deviceSerial: 'GC1159385', channelNo: 1 },
        { id: 20, name: '设备机房', location: '设备控制中心', workshop: 'none', system: 'none', pool: 'none', type: '其他', status: 'maintenance', paused: false, deviceSerial: '', channelNo: 1 }
      ]
    };
  },
  computed: {
    /** 仅本部门（及子部门）可见摄像头 */
    cameras() {
      const self = this
      return (this.allCameras || []).filter(function (camera) {
        return self.cameraInUserDeptScope(camera)
      })
    },
    workshopFilterOptions() {
      const labels = pickWorkshopOptionLabels(this.sharedSiteOptions) || []
      if (labels.length) {
        return labels.map(function (label) {
          const m = String(label).match(/(\d+)/)
          return { value: m ? m[1] : label, label: label }
        })
      }
      // 无白名单时：从已可见摄像头推导（超管可见全量）
      const seen = {}
      const opts = []
      ;(this.cameras || []).forEach(function (c) {
        if (!c.workshop || c.workshop === 'none') return
        if (seen[c.workshop]) return
        seen[c.workshop] = true
        opts.push({ value: String(c.workshop), label: formatWorkshop(c.workshop) || (c.workshop + '号车间') })
      })
      return opts
    },
    systemFilterOptions() {
      const ws = this.filterWorkshop === 'all' ? '' : this.filterWorkshop
      const labels = pickSystemOptionLabels(this.sharedSiteOptions, ws ? formatWorkshop(ws) : '') || []
      if (labels.length) {
        return labels.map(function (label) {
          const m = String(label).match(/(\d+)/)
          return { value: m ? m[1] : label, label: label }
        })
      }
      const seen = {}
      const opts = []
      ;(this.cameras || []).forEach(function (c) {
        if (ws && !workshopsEqual(c.workshop, ws)) return
        if (!c.system || c.system === 'none') return
        if (seen[c.system]) return
        seen[c.system] = true
        opts.push({ value: String(c.system), label: formatSystem(c.system) || (c.system + '号系统') })
      })
      return opts
    },
    filteredCameras() {
      return this.cameras.filter(camera => {
        if (this.filterWorkshop !== 'all' && !workshopsEqual(camera.workshop, this.filterWorkshop)) return false;
        if (this.filterSystem !== 'all' && camera.system !== 'none' && !systemsEqual(camera.system, this.filterSystem)) return false;
        if (this.filterSystem !== 'all' && camera.system === 'none') return false;
        if (this.filterPool !== 'all' && camera.pool !== this.filterPool) return false;
        if (this.filterType !== 'all' && camera.type !== this.filterType) return false;
        return true;
      });
    },
    gridColumns() {
      return Math.sqrt(this.gridMode) || 1;
    },
    displayedCameras() {
      return this.filteredCameras.slice(0, this.gridMode);
    },
    currentTimeStr() {
      const year = this.currentTime.getFullYear();
      const month = String(this.currentTime.getMonth() + 1).padStart(2, '0');
      const day = String(this.currentTime.getDate()).padStart(2, '0');
      const hours = String(this.currentTime.getHours()).padStart(2, '0');
      const minutes = String(this.currentTime.getMinutes()).padStart(2, '0');
      const seconds = String(this.currentTime.getSeconds()).padStart(2, '0');
      return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
    },
    onlineCount() {
      return this.cameras.filter(c => c.status === 'online').length;
    },
    maintenanceCount() {
      return this.cameras.filter(c => c.status === 'maintenance').length;
    },
    offlineCount() {
      return this.cameras.filter(c => c.status === 'offline').length;
    },
    currentFullscreenCamera() {
      return this.cameras.find(c => c.id === this.fullscreenCameraId) || {};
    }
  },
  mounted() {
    var self = this
    this.timer = setInterval(() => {
      this.currentTime = new Date();
    }, 1000);
    this.loadSharedSiteOptions(true);
    this._onSiteOptionsChanged = function () { self.loadSharedSiteOptions(true); };
    this._onUserScopeChanged = function () {
      self.filterWorkshop = 'all';
      self.filterSystem = 'all';
      self.filterPool = 'all';
      self.filterType = 'all';
      self.fullscreenCameraId = null;
      self.loadSharedSiteOptions(true);
    };
    window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
  },
  activated() {
    this.loadSharedSiteOptions(true);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    this.destroyPlayer();
    if (this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    if (this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    }
  },
  methods: {
    cameraInUserDeptScope(camera) {
      const deptId = resolveCurrentUserDeptId()
      const opts = this.sharedSiteOptions
      const hasScope = !!(opts && (
        (opts.workshopOptions || []).length ||
        (opts.systemOptions || []).length ||
        (opts.pondOptions || []).length
      ))
      // 部门用户白名单未就绪：不展示，避免闪现全厂摄像头
      if (deptId != null && !hasScope) return false
      const row = cameraToScopeRow(camera)
      return isRowInUserSiteScope(row, opts)
    },
    loadSharedSiteOptions(force) {
      var self = this
      return fetchSiteOptions({ includePonds: true, force: !!force }).then(function (opts) {
        self.sharedSiteOptions = opts || self.sharedSiteOptions
        self.sanitizeVideoFilters()
      }).catch(function (e) {
        console.warn('[index_daping2] site options', e && e.message)
      })
    },
    sanitizeVideoFilters() {
      var self = this
      const wsOpts = this.workshopFilterOptions || []
      if (this.filterWorkshop !== 'all' && !wsOpts.some(function (o) {
        return workshopsEqual(o.value, self.filterWorkshop) || o.value === self.filterWorkshop
      })) {
        this.filterWorkshop = 'all'
        this.filterSystem = 'all'
        this.filterPool = 'all'
      }
      const sysOpts = this.systemFilterOptions || []
      if (this.filterSystem !== 'all' && !sysOpts.some(function (o) {
        return systemsEqual(o.value, self.filterSystem) || o.value === self.filterSystem
      })) {
        this.filterSystem = 'all'
        this.filterPool = 'all'
      }
    },
    toPage(path) {
      this.$router.push({ path: path }).catch(() => {});
    },
    resetFilter() {
      this.filterWorkshop = 'all';
      this.filterSystem = 'all';
      this.filterPool = 'all';
      this.filterType = 'all';
    },
    async getEzvizToken() {
      try {
        const res = await service.get('/ezviz/token');
        // 兼容多种代理返回结构
        const token =
          (res && res.accessToken) ||
          (res && res.data && res.data.accessToken) ||
          (res && res.data && res.data.data && res.data.data.accessToken) ||
          (typeof res === 'string' ? res : '') ||
          (res && typeof res.data === 'string' ? res.data : '');
        if (token) {
          this.ezAccessToken = token;
          return;
        }
        if (res && (res.code === 0 || res.code === 200) && res.data) {
          this.ezAccessToken = res.data.accessToken || res.data;
          return;
        }
        throw new Error((res && (res.message || res.msg)) || 'Token 获取失败');
      } catch (e) {
        this.$message.error('获取播放凭证失败');
        throw e;
      }
    },
    initPlayer(camera) {
      if (!camera.deviceSerial || !camera.channelNo) {
        this.$message.warning('缺少设备信息');
        return;
      }
      this.destroyPlayer();
      const mount = this.$refs.ezPlayerMount;
      if (!mount) return;
      mount.innerHTML = '';
      try {
        this.ezPlayer = new EZUIKitPlayer({
          id: mount,
          accessToken: this.ezAccessToken,
          url: `ezopen://open.ys7.com/${camera.deviceSerial}/${camera.channelNo}.hd.live`,
          template: 'simple',
          autoplay: true,
          width: mount.clientWidth,
          height: mount.clientHeight
        });
      } catch (error) {
        console.error('播放器初始化失败', error);
        this.$message.error('视频流加载失败');
      }
    },
    destroyPlayer() {
      if (this.ezPlayer) {
        this.ezPlayer.stop();
        this.ezPlayer = null;
      }
    },
    async openFullscreen(camera) {
      this.fullscreenCameraId = camera.id;
      if (camera.status === 'online') {
        try {
          if (!this.ezAccessToken) {
            await this.getEzvizToken();
          }
          this.$nextTick(() => {
            this.initPlayer(camera);
          });
        } catch (e) {
          console.error('全屏初始化失败', e);
        }
      }
    },
    exitFullscreen() {
      const cameraId = this.fullscreenCameraId;
      this.destroyPlayer();
      this.fullscreenCameraId = null;
      this.playbackCameraId = null;
      const cam = this.cameras.find(c => c.id === cameraId);
      if (cam) cam.paused = false;
    },
    togglePause(camera) {
      const cam = this.cameras.find(c => c.id === camera.id);
      if (cam) {
        cam.paused = !cam.paused;
        if (this.fullscreenCameraId === camera.id && this.ezPlayer) {
          if (cam.paused) {
            this.ezPlayer.pause();
          } else {
            this.ezPlayer.play();
          }
        }
      }
    },
    takeScreenshot(camera) {
      const mount = this.$refs.ezPlayerMount;
      const video = mount?.querySelector('video');
      if (!video || video.readyState < 2) {
        this.$message.warning('视频未就绪，请稍后再试');
        return;
      }
      try {
        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth || 1280;
        canvas.height = video.videoHeight || 720;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        ctx.fillStyle = 'rgba(255, 255, 255, 0.8)';
        ctx.font = '20px Arial';
        ctx.fillText(`${camera.name} - ${this.currentTimeStr}`, 20, 30);
        try {
          canvas.toBlob((blob) => {
            if (!blob) {
              this.fallbackScreenshot(canvas, camera);
              return;
            }
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `截图_${camera.name}_${Date.now()}.png`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
            this.$message.success(`截图已保存：${camera.name}`);
          }, 'image/png');
        } catch (blobErr) {
          this.fallbackScreenshot(canvas, camera);
        }
      } catch (e) {
        console.error('截图失败:', e);
        this.$message.error('截图失败：' + e.message);
      }
    },
    fallbackScreenshot(canvas, camera) {
      try {
        const dataUrl = canvas.toDataURL('image/png');
        const a = document.createElement('a');
        a.href = dataUrl;
        a.download = `截图_${camera.name}_${Date.now()}.png`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        this.$message.success(`截图已保存：${camera.name}`);
      } catch (e) {
        console.error('截图备用方案也失败:', e);
        this.$message.error('截图失败（跨域限制）');
      }
    },
    openPlayback(camera) {
      if (this.playbackCameraId === camera.id) {
        this.playbackCameraId = null;
      } else {
        this.playbackCameraId = camera.id;
        const now = new Date();
        const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
        const pad = (n) => String(n).padStart(2, '0');
        const fmt = (d) => `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
        this.playbackStartTime = fmt(oneHourAgo);
        this.playbackEndTime = fmt(now);
      }
    },
    confirmPlayback(camera) {
      if (!this.playbackStartTime || !this.playbackEndTime) {
        this.$message.warning('请选择回放时间范围');
        return;
      }
      const video = this.$refs.ezPlayerMount?.querySelector('video');
      if (video) {
        video.currentTime = 0;
        video.play();
      }
      const cam = this.cameras.find(c => c.id === camera.id);
      if (cam) cam.paused = false;
      this.$message.success(`回放中：${this.playbackStartTime} 至 ${this.playbackEndTime}`);
      this.playbackCameraId = null;
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-wrapper {
  width: 100%;
  height: 100vh;
  background: linear-gradient(to bottom right, #020617, #172554, #0f172a);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  color: white;
  font-size: 15px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;

  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  svg {
    flex-shrink: 0;
  }

  .header {
    background: linear-gradient(to right, rgba(15, 23, 42, 0.8), rgba(88, 28, 135, 0.6), rgba(15, 23, 42, 0.8));
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(168, 85, 247, 0.3);
    padding: 0.65rem 1.25rem;
    flex-shrink: 0;

    .header-bar {
      display: grid;
      grid-template-columns: 1fr auto 1fr;
      align-items: center;
      gap: 1rem;
      min-width: 0;
    }

    .header-left {
      justify-self: start;
      display: flex;
      align-items: center;
      gap: 0.6rem;
      min-width: 0;

      .nav-btn {
        display: flex;
        align-items: center;
        gap: 0.45rem;
        padding: 0 0.9rem;
        border-radius: 0.5rem;
        border: none;
        cursor: pointer;
        transition: all 0.3s;
        color: white;
        font-weight: 600;
        font-size: 0.94rem;
        height: 2.5rem;
        white-space: nowrap;
        flex-shrink: 0;

        &.home {
          background: linear-gradient(to bottom right, #2563eb, #06b6d4);
          justify-content: center;
          padding: 0;
          width: 2.5rem;
          box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);

          .icon {
            width: 1.1rem;
            height: 1.1rem;
          }
        }

        &.btn-blue {
          background: linear-gradient(to right, #2563eb, #3b82f6);
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        }

        &.btn-cyan {
          background: linear-gradient(to right, #0891b2, #06b6d4);
          box-shadow: 0 4px 12px rgba(6, 182, 212, 0.3);
        }

        &:hover {
          filter: brightness(1.08);
          transform: translateY(-1px);
        }

        .icon {
          width: 0.9rem;
          height: 0.9rem;
        }
      }

      .divider {
        width: 1px;
        height: 1.75rem;
        background: rgba(168, 85, 247, 0.35);
        flex-shrink: 0;
      }
    }

    .header-center {
      justify-self: center;
      display: flex;
      align-items: center;
      min-width: 0;

      .title-wrap {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        text-align: left;

        .logo {
          position: static;
          width: 2.75rem;
          height: 2.75rem;
          background: linear-gradient(to bottom right, #a855f7, #f472b6);
          border-radius: 0.5rem;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 4px 15px rgba(168, 85, 247, 0.45);
          flex-shrink: 0;

          .icon-lg {
            width: 1.6rem;
            height: 1.6rem;
            color: white;
          }
        }

        h1 {
          font-size: 1.75rem;
          font-weight: 800;
          line-height: 1.2;
          white-space: nowrap;
          color: white;
          margin: 0;
        }

        p {
          font-size: 0.94rem;
          color: #d8b4fe;
          margin-top: 0.1rem;
          white-space: nowrap;
        }
      }
    }

    .header-right {
      justify-self: end;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      min-width: 0;

      .quick-btns {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-end;
        gap: 0.6rem;

        .q-btn {
          height: 2.5rem;
          padding: 0 0.9rem;
          border-radius: 0.5rem;
          display: flex;
          align-items: center;
          gap: 0.45rem;
          color: white;
          font-size: 0.94rem;
          font-weight: 600;
          border: none;
          cursor: pointer;
          transition: all 0.3s;
          white-space: nowrap;
          flex-shrink: 0;

          .icon {
            width: 0.9rem;
            height: 0.9rem;
          }

          &.btn-emerald {
            background: linear-gradient(to right, #059669, #10b981);
            box-shadow: 0 4px 10px rgba(16, 185, 129, 0.25);
          }

          &.btn-orange {
            background: linear-gradient(to right, #ea580c, #f97316);
            box-shadow: 0 4px 10px rgba(249, 115, 22, 0.25);
          }

          &.btn-cyan {
            background: linear-gradient(to right, #0891b2, #06b6d4);
            box-shadow: 0 4px 10px rgba(6, 182, 212, 0.25);
          }

          &.btn-pink {
            background: linear-gradient(to right, #db2777, #ec4899);
            box-shadow: 0 4px 10px rgba(236, 72, 153, 0.25);
          }

          &.btn-indigo {
            background: linear-gradient(to right, #4f46e5, #6366f1);
            box-shadow: 0 4px 10px rgba(99, 102, 241, 0.25);
          }

          &.btn-teal {
            background: linear-gradient(to right, #0d9488, #14b8a6);
            box-shadow: 0 4px 10px rgba(20, 184, 166, 0.25);
          }

          &:hover {
            filter: brightness(1.08);
            transform: translateY(-1px);
          }
        }
      }
    }
  }

  .status-card {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.45rem 0.85rem;
    border-radius: 0.5rem;
    border: 1px solid;
    white-space: nowrap;

    .dot {
      width: 0.55rem;
      height: 0.55rem;
      border-radius: 50%;
      flex-shrink: 0;
    }

    span {
      font-size: 0.94rem;
      font-weight: 600;
    }

    &.online {
      background: rgba(6, 78, 59, 0.3);
      border-color: rgba(16, 185, 129, 0.4);

      .dot {
        background: #34d399;
      }

      span {
        color: #6ee7b7;
      }
    }

    &.offline {
      background: rgba(127, 29, 29, 0.3);
      border-color: rgba(248, 113, 113, 0.4);

      .dot {
        background: #f87171;
      }

      span {
        color: #fca5a5;
      }
    }

    &.maintenance {
      background: rgba(113, 63, 18, 0.3);
      border-color: rgba(234, 179, 8, 0.4);

      .dot {
        background: #facc15;
      }

      span {
        color: #fde047;
      }
    }
  }

  .grid-mode-btns {
    display: flex;
    align-items: center;
    gap: 0.35rem;

    .label {
      color: #cbd5e1;
      font-size: 0.94rem;
      white-space: nowrap;
    }

    .grid-mode-btn {
      min-width: 2.25rem;
      min-height: 2.25rem;
      padding: 0 0.55rem;
      border-radius: 0.35rem;
      border: 1px solid rgba(168, 85, 247, 0.35);
      background: rgba(30, 41, 59, 0.8);
      color: #e2e8f0;
      font-size: 0.9rem;
      font-weight: 600;
      cursor: pointer;

      &.active {
        background: rgba(147, 51, 234, 0.85);
        border-color: rgba(168, 85, 247, 0.6);
        color: #fff;
      }
    }
  }

  .filter-bar {
    padding: 0.75rem 1.25rem;
    background: linear-gradient(to right, rgba(15, 23, 42, 0.6), rgba(88, 28, 135, 0.4), rgba(15, 23, 42, 0.6));
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(168, 85, 247, 0.2);
    flex-shrink: 0;

    .filter-content {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex-wrap: wrap;

      .filter-label {
        color: #e9d5ff;
        font-size: 0.94rem;
        font-weight: 600;
        white-space: nowrap;
      }

      .filter-item {
        display: flex;
        align-items: center;
        gap: 0.45rem;

        .label {
          color: #cbd5e1;
          font-size: 0.94rem;
          white-space: nowrap;
        }

        .select-input {
          background: rgba(30, 41, 59, 0.8);
          border: 1px solid rgba(168, 85, 247, 0.3);
          border-radius: 0.35rem;
          padding: 0.45rem 0.75rem;
          color: white;
          font-size: 0.94rem;
          outline: none;
          cursor: pointer;
          min-height: 2.25rem;

          &:focus {
            border-color: rgba(168, 85, 247, 0.5);
            box-shadow: 0 0 0 2px rgba(168, 85, 247, 0.25);
          }

          option {
            background: #1e293b;
            color: white;
          }
        }
      }

      .reset-btn {
        padding: 0.45rem 1rem;
        min-height: 2.25rem;
        background: rgba(147, 51, 234, 0.8);
        color: white;
        font-size: 0.94rem;
        font-weight: 600;
        border-radius: 0.35rem;
        border: none;
        cursor: pointer;
        transition: background 0.3s;
        white-space: nowrap;

        &:hover {
          background: #a855f7;
        }
      }

      .filter-meta {
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        flex-wrap: wrap;
        justify-content: flex-end;
      }
    }
  }

  .video-grid-container {
    flex: 1;
    min-height: 0;
    padding: 1rem 1.25rem 1.25rem;
    overflow-y: auto;

    .video-grid {
      display: grid;
      gap: 0.85rem;

      .video-card {
        aspect-ratio: 16 / 9;
        background: linear-gradient(to bottom right, rgba(30, 41, 59, 0.6), rgba(15, 23, 42, 0.6));
        backdrop-filter: blur(4px);
        border-radius: 0.75rem;
        border: 1px solid rgba(168, 85, 247, 0.2);
        overflow: hidden;
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
        transition: all 0.3s;

        &:hover {
          border-color: rgba(168, 85, 247, 0.5);

          .bottom-info .actions {
            opacity: 1;
          }
        }

        .video-wrapper {
          position: relative;
          height: 100%;

          .video-online {
            position: absolute;
            inset: 0;
            background: linear-gradient(to bottom right, #0f172a, #1e293b, #1e3a8a);
            display: flex;
            align-items: center;
            justify-content: center;

            .video-mock {
              position: relative;
              width: 100%;
              height: 100%;
              background: rgba(2, 6, 23, 0.5);

              .center-icon {
                position: absolute;
                inset: 0;
                background-image: linear-gradient(rgba(59, 130, 246, 0.1) 1px, transparent 1px),
                linear-gradient(90deg, rgba(59, 130, 246, 0.1) 1px, transparent 1px);
                background-size: 20px 20px;
              }

              .timestamp {
                position: absolute;
                top: 0.5rem;
                left: 0.5rem;
                padding: 0.3rem 0.55rem;
                background: rgba(0, 0, 0, 0.6);
                border-radius: 0.25rem;
                color: white;
                z-index: 5;
                font-size: 0.85rem;
                font-family: monospace;
              }

              .rec-indicator {
                position: absolute;
                top: 0.5rem;
                right: 0.5rem;
                display: flex;
                align-items: center;
                gap: 0.25rem;
                padding: 0.3rem 0.55rem;
                background: rgba(127, 29, 29, 0.6);
                border-radius: 0.25rem;
                z-index: 5;

                .icon {
                  width: 0.85rem;
                  height: 0.85rem;
                  color: #f87171;
                }

                span {
                  color: #fca5a5;
                  font-size: 0.85rem;
                  font-weight: 600;
                }
              }

              .center-icon {
                position: absolute;
                inset: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                opacity: 0.3;

                .icon-xl {
                  width: 2rem;
                  height: 2rem;
                  color: #60a5fa;
                }
              }

              .paused-badge {
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                z-index: 5;
                display: flex;
                align-items: center;
                gap: 0.35rem;
                padding: 0.35rem 0.75rem;
                background: rgba(168, 85, 247, 0.85);
                border-radius: 9999px;
                color: white;
                font-size: 0.8rem;
                font-weight: 600;
                backdrop-filter: blur(4px);

                i {
                  font-size: 0.85rem;
                }
              }
            }
          }

          .video-maintenance {
            position: absolute;
            inset: 0;
            background: linear-gradient(to bottom right, rgba(113, 63, 18, 0.2), rgba(124, 45, 18, 0.2));
            display: flex;
            align-items: center;
            justify-content: center;

            &.large {
              background: linear-gradient(to bottom right, rgba(113, 63, 18, 0.3), rgba(124, 45, 18, 0.3));
            }

            .maintenance-content {
              text-align: center;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;

              .icon-box {
                width: 2rem;
                height: 2rem;
                background: rgba(234, 179, 8, 0.2);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 0.35rem;

                .icon {
                  width: 1rem;
                  height: 1rem;
                  color: #facc15;
                }
              }

              p {
                color: #fde047;
                font-size: 0.8rem;
                font-weight: 600;
              }
            }
          }

          .video-offline {
            position: absolute;
            inset: 0;
            background: linear-gradient(to bottom right, rgba(127, 29, 29, 0.3), rgba(153, 27, 27, 0.25));
            display: flex;
            align-items: center;
            justify-content: center;

            .video-mock {
              position: relative;
              width: 100%;
              height: 100%;
              background: rgba(2, 6, 23, 0.5);

              .center-icon {
                position: absolute;
                inset: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                opacity: 0.5;

                .icon-xl {
                  width: 2rem;
                  height: 2rem;
                  color: #f87171;
                  stroke-width: 2.5;
                }
              }

              .offline-badge {
                position: absolute;
                top: 0.5rem;
                right: 0.5rem;
                display: flex;
                align-items: center;
                gap: 0.25rem;
                padding: 0.3rem 0.55rem;
                background: rgba(127, 29, 29, 0.6);
                border-radius: 0.25rem;
                z-index: 5;

                i {
                  font-size: 0.85rem;
                  color: #fca5a5;
                }

                span {
                  color: #fca5a5;
                  font-size: 0.85rem;
                  font-weight: 600;
                }
              }

              .timestamp {
                position: absolute;
                top: 0.5rem;
                left: 0.5rem;
                padding: 0.3rem 0.55rem;
                background: rgba(0, 0, 0, 0.6);
                border-radius: 0.25rem;
                color: white;
                z-index: 5;
                font-size: 0.85rem;
                font-family: monospace;
              }

              .grid-overlay {
                position: absolute;
                inset: 0;
                background-image: linear-gradient(rgba(248, 113, 113, 0.08) 1px, transparent 1px),
                linear-gradient(90deg, rgba(248, 113, 113, 0.08) 1px, transparent 1px);
                background-size: 20px 20px;
              }
            }

            .offline-content {
              text-align: center;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;

              .icon-box {
                width: 2rem;
                height: 2rem;
                background: rgba(248, 113, 113, 0.2);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 0.35rem;

                .icon {
                  width: 1rem;
                  height: 1rem;
                  color: #f87171;
                }
              }

              p {
                color: #fca5a5;
                font-size: 0.8rem;
                font-weight: 600;
              }
            }
          }

          .bottom-info {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
            padding: 0.65rem 0.75rem;

            .info-content {
              display: flex;
              align-items: center;
              justify-content: space-between;
              gap: 0.5rem;

              .text-info {
                min-width: 0;

                .name {
                  color: white;
                  font-size: 0.94rem;
                  font-weight: 600;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }

                .location {
                  color: #cbd5e1;
                  font-size: 0.85rem;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }
              }

              .actions {
                display: flex;
                gap: 0.25rem;
                opacity: 0;
                transition: opacity 0.3s;
                flex-shrink: 0;

                .action-btn {
                  width: 2rem;
                  height: 2rem;
                  background: rgba(51, 65, 85, 0.8);
                  border-radius: 0.25rem;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  border: none;
                  cursor: pointer;
                  transition: background 0.3s;

                  &:hover {
                    background: rgba(71, 85, 105, 1);
                  }

                  i {
                    font-size: 1rem;
                    color: white;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  .fs-btn {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.45rem 0.9rem;
    background: rgba(51, 65, 85, 0.8);
    border: 1px solid rgba(100, 116, 139, 0.3);
    border-radius: 0.5rem;
    color: #e2e8f0;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;

    i {
      font-size: 1rem;
    }

    &:hover {
      background: rgba(71, 85, 105, 0.9);
      border-color: rgba(168, 85, 247, 0.5);
    }

    &.active {
      background: rgba(147, 51, 234, 0.7);
      border-color: rgba(168, 85, 247, 0.6);
      color: white;
    }

    &.btn-primary {
      background: rgba(59, 130, 246, 0.7);
      border-color: rgba(59, 130, 246, 0.8);

      &:hover {
        background: rgba(59, 130, 246, 0.9);
      }
    }
  }

  /* 全屏弹窗 */
  .fullscreen-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.9);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
  }

  .fullscreen-container {
    width: 100vw;
    height: 100vh;
    background: #000;
    display: flex;
    flex-direction: column;
  }

  .fs-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0.75rem 1.5rem;
    background: rgba(15, 23, 42, 0.95);

    .fs-title-row {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .fs-title {
      color: white;
      font-size: 1.1rem;
      font-weight: 700;
    }

    .fs-status-tag {
      display: flex;
      align-items: center;
      gap: 0.35rem;
      padding: 0.25rem 0.65rem;
      border-radius: 0.4rem;
      font-size: 0.85rem;
      font-weight: 600;

      .dot {
        width: 0.5rem;
        height: 0.5rem;
        border-radius: 50%;
      }

      &.online {
        background: rgba(6, 78, 59, 0.4);
        .dot { background: #34d399; }
        color: #6ee7b7;
      }

      &.offline {
        background: rgba(127, 29, 29, 0.4);
        .dot { background: #f87171; }
        color: #fca5a5;
      }

      &.maintenance {
        background: rgba(113, 63, 18, 0.4);
        .dot { background: #facc15; }
        color: #fde047;
      }
    }

    .fs-close {
      width: 2rem;
      height: 2rem;
      background: rgba(71, 85, 105, 0.6);
      border: none;
      border-radius: 0.4rem;
      color: white;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: background 0.3s;

      &:hover {
        background: rgba(239, 68, 68, 0.8);
      }

      i {
        font-size: 1.1rem;
      }
    }
  }

  .fs-video-area {
    flex: 1;
    min-height: 0;
    display: flex;
    align-items: stretch;
    justify-content: center;
    position: relative;
    background: #000;

    .fs-video {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }

    .ez-player-mount {
      width: 100%;
      height: 100%;
      background: #000;
    }

    .video-online,
    .video-offline,
    .video-maintenance {
      width: 100%;
      height: 100%;
    }

    .center-icon .icon-xl {
      width: 6rem;
      height: 6rem;
    }

    .timestamp,
    .offline-badge {
      font-size: 1.1rem;
      padding: 0.5rem 1rem;
    }

    .rec-indicator {
      font-size: 1.1rem;
      padding: 0.5rem 1rem;
    }

    .offline-badge i {
      font-size: 1.2rem;
    }
  }

  .fs-paused-overlay {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    color: rgba(168, 85, 247, 0.9);
    z-index: 10;

    i {
      font-size: 4rem;
    }

    span {
      font-size: 1.2rem;
      font-weight: 600;
    }
  }

  .fs-rec-badge {
    position: absolute;
    top: 1rem;
    right: 1rem;
    display: flex;
    align-items: center;
    gap: 0.35rem;
    padding: 0.3rem 0.75rem;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 0.35rem;
    color: #ef4444;
    font-size: 0.9rem;
    font-weight: 700;
    z-index: 5;

    .rec-dot {
      width: 0.6rem;
      height: 0.6rem;
      background: #ef4444;
      border-radius: 50%;
      animation: blink 1s infinite;
    }
  }

  .fs-timestamp {
    position: absolute;
    top: 1rem;
    left: 1rem;
    padding: 0.3rem 0.75rem;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 0.35rem;
    color: #e2e8f0;
    font-size: 0.9rem;
    font-family: monospace;
    z-index: 5;
  }

  /* 离线全屏新样式 */
  .fs-offline-container {
    position: relative;
    width: 100%;
    height: 100%;
    background: radial-gradient(ellipse at center, rgba(127, 29, 29, 0.35) 0%, rgba(0, 0, 0, 0.95) 70%);
    display: flex;
    align-items: center;
    justify-content: center;

    .fs-offline-bg {
      position: absolute;
      inset: 0;
      .grid-overlay {
        position: absolute;
        inset: 0;
        background-image:
          linear-gradient(rgba(248, 113, 113, 0.08) 1px, transparent 1px),
          linear-gradient(90deg, rgba(248, 113, 113, 0.08) 1px, transparent 1px);
        background-size: 20px 20px;
      }
    }

    .fs-offline-content {
      position: relative;
      z-index: 2;
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
    }

    .offline-icon-wrap {
      width: 6rem;
      height: 6rem;
      border-radius: 50%;
      background: rgba(248, 113, 113, 0.12);
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        inset: -0.5rem;
        border-radius: 50%;
        border: 2px solid rgba(248, 113, 113, 0.25);
        animation: pulse-ring 2s cubic-bezier(0.25, 0.46, 0.45, 0.94) infinite;
      }

      .offline-icon {
        width: 3rem;
        height: 3rem;
        color: #f87171;
        animation: blink 1.5s infinite;
      }
    }

    .offline-text {
      font-size: 2rem;
      font-weight: 700;
      color: #fca5a5;
      text-shadow: 0 0 20px rgba(248, 113, 113, 0.5);
    }

    .offline-sub {
      font-size: 1rem;
      color: #94a3b8;
    }
  }

  @keyframes pulse-ring {
    0% { transform: scale(0.9); opacity: 1; }
    100% { transform: scale(1.4); opacity: 0; }
  }

  @keyframes blink {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.3; }
  }

  .fs-controls-bar {
    padding: 0.75rem 1.5rem;
    background: rgba(15, 23, 42, 0.95);
    border-top: 1px solid rgba(168, 85, 247, 0.2);

    .fs-btns {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      flex-wrap: wrap;
    }

    .playback-row {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin-top: 0.5rem;
      padding: 0.5rem;
      background: rgba(30, 41, 59, 0.6);
      border-radius: 0.4rem;
    }

    .playback-input {
      padding: 0.35rem 0.5rem;
      background: rgba(15, 23, 42, 0.9);
      border: 1px solid rgba(100, 116, 139, 0.4);
      border-radius: 0.25rem;
      color: white;
      font-size: 0.85rem;
      color-scheme: dark;
    }

    .to-label {
      color: #94a3b8;
      font-size: 0.85rem;
    }
  }

  .fs-btn {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.5rem 1rem;
    background: rgba(51, 65, 85, 0.8);
    border: 1px solid rgba(100, 116, 139, 0.3);
    border-radius: 0.5rem;
    color: #e2e8f0;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;

    i {
      font-size: 1rem;
    }

    &:hover {
      background: rgba(71, 85, 105, 0.9);
      border-color: rgba(168, 85, 247, 0.5);
    }

    &.active {
      background: rgba(147, 51, 234, 0.7);
      border-color: rgba(168, 85, 247, 0.6);
      color: white;
    }

    &.btn-primary {
      background: rgba(59, 130, 246, 0.7);
      border-color: rgba(59, 130, 246, 0.8);

      &:hover {
        background: rgba(59, 130, 246, 0.9);
      }
    }
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: .5; }
  }

  /* 隐藏滚动条 */
  ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  ::-webkit-scrollbar-track {
    background: transparent;
  }
  ::-webkit-scrollbar-thumb {
    background: rgba(168, 85, 247, 0.3);
    border-radius: 3px;
  }
  ::-webkit-scrollbar-thumb:hover {
    background: rgba(168, 85, 247, 0.5);
  }
}
</style>
