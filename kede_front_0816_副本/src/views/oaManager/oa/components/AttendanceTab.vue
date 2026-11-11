<template>
  <div class="attendance-tab">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 今日打卡卡片 -->
      <div class="punch-card">
        <div class="card-header">
          <i class="el-icon-time"></i>
          <h3>今日打卡</h3>
        </div>
        <p class="punch-tip">请按时进行上下班打卡</p>
        <div class="time-display">
          <div class="current-time">当前时间 {{ currentTime }}</div>
          <div class="current-date">{{ currentDate }}</div>
        </div>
        <div class="punch-buttons">
          <el-button 
            :class="['punch-btn', 'punch-in-btn', { 'punch-in-active': todayAttendance.hasClockIn }]"
            :disabled="todayAttendance.hasClockIn"
            @click="handleClockIn"
            :loading="punchLoading">
            <i class="el-icon-check"></i>
            {{ todayAttendance.hasClockIn ? '已上班打卡' : '上班打卡' }}
          </el-button>
          <el-button 
            :class="['punch-btn', 'punch-out-btn', { 'punch-out-active': todayAttendance.hasClockOut }]"
            :disabled="todayAttendance.hasClockOut || !todayAttendance.hasClockIn"
            @click="handleClockOut"
            :loading="punchLoading">
            <i class="el-icon-close"></i>
            {{ todayAttendance.hasClockOut ? '已下班打卡' : '下班打卡' }}
          </el-button>
        </div>
      </div>

      <!-- 日历卡片 -->
      <div class="calendar-card">
        <div class="calendar-wrapper">
          <el-calendar v-model="calendarDate">
          </el-calendar>
        </div>
      </div>

      <!-- 考勤记录卡片 -->
      <div class="record-card">
        <div class="card-header">
          <h3>考勤记录</h3>
          <p class="subtitle">查看历史考勤记录</p>
        </div>
        <div class="table-container" ref="tableContainer" @scroll="handleScroll">
          <el-table 
            v-loading="loading" 
            :data="attendanceList" 
            style="width: 100%"
            stripe>
            <el-table-column prop="attendanceDate" label="日期" min-width="120" align="center">
              <template slot-scope="scope">
                {{ formatDate(scope.row.attendanceDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="clockInTime" label="上班打卡" min-width="140" align="center">
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.attendanceDate, scope.row.clockInTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="clockOutTime" label="下班打卡" min-width="140" align="center">
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.attendanceDate, scope.row.clockOutTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" min-width="100" align="center">
              <template slot-scope="scope">
                <el-tag 
                  :type="getStatusType(scope.row.status)" 
                  size="small">
                  {{ getStatusLabel(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="location" label="打卡地点" min-width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.location || '--' }}
              </template>
            </el-table-column>
          </el-table>
          <div v-if="loadingMore" class="loading-more">
            <i class="el-icon-loading"></i> 加载中...
          </div>
          <div v-if="!hasMore && attendanceList.length > 0" class="no-more">
            没有更多数据了
          </div>
        </div>
      </div>
    </div>

    <!-- 考勤统计卡片 -->
    <div class="statistics-section">
      <div class="stat-card">
        <div class="stat-icon" style="background: #2f6b5a;">
          <i class="el-icon-date"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.attendanceDays }}天</div>
          <div class="stat-label">应出勤:{{ statistics.shouldAttendanceDays }}天</div>
        </div>
        <div class="stat-title">本月出勤</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #E6A23C;">
          <i class="el-icon-time"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.lateCount }}次</div>
          <div class="stat-label">本月统计</div>
        </div>
        <div class="stat-title">迟到次数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #F56C6C;">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.earlyLeaveCount }}次</div>
          <div class="stat-label">本月统计</div>
        </div>
        <div class="stat-title">早退次数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #F56C6C;">
          <i class="el-icon-close"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.absentCount }}次</div>
          <div class="stat-label">本月统计</div>
        </div>
        <div class="stat-title">缺勤次数</div>
      </div>
    </div>
  </div>
</template>

<script>
import { punch, getTodayAttendance, getAttendancePage, getAttendanceStatistics } from '@/api/oa/attendance'

export default {
  name: 'AttendanceTab',
  data() {
    return {
      currentTime: '',
      currentDate: '',
      calendarDate: new Date(),
      todayAttendance: {
        hasClockIn: false,
        hasClockOut: false,
        clockInTime: null,
        clockOutTime: null,
        location: ''
      },
      attendanceList: [],
      loading: false,
      loadingMore: false,
      punchLoading: false,
      total: 0,
      hasMore: true,
      queryParams: {
        pageNo: 1,
        pageSize: 10
      },
      statistics: {
        attendanceDays: 0,
        shouldAttendanceDays: 0,
        lateCount: 0,
        earlyLeaveCount: 0,
        absentCount: 0
      },
      timer: null
    }
  },
  mounted() {
    this.initData()
  },
  activated() {
    // 当组件被激活时（从其他tab切换回来），重新加载数据
    this.initData()
  },
  watch: {
    calendarDate() {
      this.$nextTick(() => {
        this.hideAdjacentMonthDates()
      })
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    initData() {
      this.updateTime()
      if (this.timer) {
        clearInterval(this.timer)
      }
      this.timer = setInterval(() => {
        this.updateTime()
      }, 1000)
      this.getTodayAttendanceInfo()
      this.queryParams.pageNo = 1
      this.attendanceList = []
      this.hasMore = true
      this.getList()
      this.getStatistics()
      this.$nextTick(() => {
        this.hideAdjacentMonthDates()
      })
    },
    updateTime() {
      const now = new Date()
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentTime = `${hours}:${minutes}:${seconds}`
      
      const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const weekday = weekdays[now.getDay()]
      this.currentDate = `${year}年${month}月${day}日${weekday}`
    },
    async getTodayAttendanceInfo() {
      try {
        const res = await getTodayAttendance()
        if (res.code === 200 || res.code === 0) {
          this.todayAttendance = res.data || {
            hasClockIn: false,
            hasClockOut: false,
            clockInTime: null,
            clockOutTime: null,
            location: ''
          }
        } else {
          this.todayAttendance = {
            hasClockIn: false,
            hasClockOut: false,
            clockInTime: null,
            clockOutTime: null,
            location: ''
          }
        }
      } catch (error) {
        console.error('获取今日考勤信息失败:', error)
        this.todayAttendance = {
          hasClockIn: false,
          hasClockOut: false,
          clockInTime: null,
          clockOutTime: null,
          location: ''
        }
      }
    },
    async resolvePunchGeo() {
      if (!navigator.geolocation) {
        throw new Error('当前浏览器不支持定位，请换用 Chrome/Edge 或开启 HTTPS 后再打卡')
      }
      const pos = await new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject, {
          enableHighAccuracy: true,
          timeout: 15000,
          maximumAge: 0
        })
      }).catch((err) => {
        const code = err && err.code
        if (code === 1) throw new Error('定位权限被拒绝，请在浏览器地址栏允许位置访问后重试')
        if (code === 2) throw new Error('无法获取位置信息，请检查设备定位是否开启')
        if (code === 3) throw new Error('定位超时，请到开阔处重试或检查网络')
        throw new Error((err && err.message) || '获取位置失败')
      })
      const latitude = Number(pos.coords.latitude)
      const longitude = Number(pos.coords.longitude)
      if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) {
        throw new Error('定位结果无效，请重试')
      }
      return {
        latitude,
        longitude,
        location: `${latitude.toFixed(6)},${longitude.toFixed(6)}`
      }
    },
    async handleClockIn() {
      if (this.todayAttendance.hasClockIn) {
        this.$message.warning('您已经完成上班打卡，不能重复打卡')
        return
      }
      
      this.punchLoading = true
      try {
        this.$message.info('正在获取定位…')
        const geo = await this.resolvePunchGeo()
        const res = await punch(1, geo)
        if (res.code === 200 || res.code === 0) {
          this.$message.success('上班打卡成功')
          this.todayAttendance.hasClockIn = true
          this.todayAttendance.location = geo.location
          await this.getTodayAttendanceInfo()
          this.queryParams.pageNo = 1
          this.attendanceList = []
          this.hasMore = true
          await this.getList()
          this.getStatistics()
        } else {
          const errorMsg = res.msg || res.message || '上班打卡失败'
          this.$message.error(errorMsg)
        }
      } catch (error) {
        console.error('上班打卡失败:', error)
        let errorMsg = '上班打卡失败，请稍后重试'
        if (error === 'error') {
          errorMsg = '不在打卡范围内，或考勤服务异常'
        } else if (error.response) {
          const errorData = error.response.data
          if (errorData) {
            errorMsg = errorData.msg || errorData.message || `请求失败: ${error.response.status}`
          } else {
            errorMsg = `请求失败: ${error.response.status} ${error.response.statusText}`
          }
        } else if (error.message) {
          errorMsg = error.message
        }
        this.$message.error(errorMsg)
      } finally {
        this.punchLoading = false
      }
    },
    async handleClockOut() {
      if (this.todayAttendance.hasClockOut) {
        this.$message.warning('您已经完成下班打卡，不能重复打卡')
        return
      }
      if (!this.todayAttendance.hasClockIn) {
        this.$message.warning('请先完成上班打卡')
        return
      }
      
      this.punchLoading = true
      try {
        this.$message.info('正在获取定位…')
        const geo = await this.resolvePunchGeo()
        const res = await punch(2, geo)
        if (res.code === 200 || res.code === 0) {
          this.$message.success('下班打卡成功')
          this.todayAttendance.hasClockOut = true
          this.todayAttendance.location = geo.location
          await this.getTodayAttendanceInfo()
          this.queryParams.pageNo = 1
          this.attendanceList = []
          this.hasMore = true
          await this.getList()
          this.getStatistics()
        } else {
          const errorMsg = res.msg || res.message || '下班打卡失败'
          this.$message.error(errorMsg)
        }
      } catch (error) {
        console.error('下班打卡失败:', error)
        let errorMsg = '下班打卡失败，请稍后重试'
        if (error === 'error') {
          errorMsg = '不在打卡范围内，或考勤服务异常'
        } else if (error.response) {
          const errorData = error.response.data
          if (errorData) {
            errorMsg = errorData.msg || errorData.message || `请求失败: ${error.response.status}`
          } else {
            errorMsg = `请求失败: ${error.response.status} ${error.response.statusText}`
          }
        } else if (error.message) {
          errorMsg = error.message
        }
        this.$message.error(errorMsg)
      } finally {
        this.punchLoading = false
      }
    },
    async getList(isLoadMore = false) {
      if (isLoadMore) {
        this.loadingMore = true
      } else {
        this.loading = true
      }
      
      try {
        const res = await getAttendancePage(this.queryParams)
        if (res.code === 200 || res.code === 0) {
          const newList = res.data?.list || []
          this.total = res.data?.total || 0
          
          if (isLoadMore) {
            this.attendanceList = [...this.attendanceList, ...newList]
          } else {
            this.attendanceList = newList
          }
          
          this.hasMore = this.attendanceList.length < this.total
        } else {
          const errorMsg = res.msg || res.message || '获取考勤记录失败'
          this.$message.error(errorMsg)
        }
      } catch (error) {
        console.error('获取考勤记录失败:', error)
        let errorMsg = '获取考勤记录失败'
        if (error.response && error.response.data) {
          const errorData = error.response.data
          errorMsg = errorData.msg || errorData.message || errorMsg
        }
        this.$message.error(errorMsg)
      } finally {
        this.loading = false
        this.loadingMore = false
      }
    },
    handleScroll(event) {
      const container = event.target
      const scrollTop = container.scrollTop
      const scrollHeight = container.scrollHeight
      const clientHeight = container.clientHeight
      
      if (scrollHeight - scrollTop - clientHeight < 20) {
        this.loadMore()
      }
    },
    async loadMore() {
      if (this.loadingMore || !this.hasMore || this.loading) {
        return
      }
      
      this.queryParams.pageNo += 1
      await this.getList(true)
    },
    async getStatistics() {
      try {
        const month = new Date().toISOString().slice(0, 7)
        const res = await getAttendanceStatistics(month)
        if (res.code === 200 || res.code === 0) {
          this.statistics = res.data || {
            attendanceDays: 0,
            shouldAttendanceDays: 0,
            lateCount: 0,
            earlyLeaveCount: 0,
            absentCount: 0
          }
        }
      } catch (error) {
        console.error('获取统计信息失败:', error)
      }
    },
    getStatusType(status) {
      const statusMap = {
        '0': 'success',
        '1': 'warning',
        '2': 'warning',
        '3': 'danger',
        '4': 'warning'
      }
      return statusMap[status] || 'info'
    },
    getStatusLabel(status) {
      const statusMap = {
        '0': '正常',
        '1': '迟到',
        '2': '早退',
        '3': '缺勤',
        '4': '迟到+早退'
      }
      return statusMap[status] || '未知'
    },
    formatDate(date) {
      if (!date) return '--'
      
      if (typeof date === 'number') {
        let timestamp = date
        if (date < 10000000000) {
          timestamp = date * 1000
        }
        const dateObj = new Date(timestamp)
        if (!isNaN(dateObj.getTime())) {
          const year = dateObj.getFullYear()
          const month = String(dateObj.getMonth() + 1).padStart(2, '0')
          const day = String(dateObj.getDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }
      }
      
      if (Array.isArray(date) && date.length >= 3) {
        const year = String(date[0]).padStart(4, '0')
        const month = String(date[1]).padStart(2, '0')
        const day = String(date[2]).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      if (typeof date === 'string') {
        const datePart = date.split('T')[0]
        return datePart
      }
      
      if (date instanceof Date) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      return String(date)
    },
    formatDateTime(date, time) {
      if (!time) return '--'
      
      let dateStr = ''
      if (date) {
        if (Array.isArray(date) && date.length >= 3) {
          const year = String(date[0]).padStart(4, '0')
          const month = String(date[1]).padStart(2, '0')
          const day = String(date[2]).padStart(2, '0')
          dateStr = `${year}-${month}-${day}`
        } else if (typeof date === 'string') {
          dateStr = date.split('T')[0]
        } else if (date instanceof Date) {
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          dateStr = `${year}-${month}-${day}`
        } else {
          dateStr = String(date)
        }
      } else {
        const now = new Date()
        const year = now.getFullYear()
        const month = String(now.getMonth() + 1).padStart(2, '0')
        const day = String(now.getDate()).padStart(2, '0')
        dateStr = `${year}-${month}-${day}`
      }
      
      let timeStr = ''
      if (Array.isArray(time) && time.length >= 2) {
        const hour = String(time[0]).padStart(2, '0')
        const minute = String(time[1]).padStart(2, '0')
        const second = time.length >= 3 ? String(time[2]).padStart(2, '0') : '00'
        timeStr = `${hour}:${minute}:${second}`
      } else if (typeof time === 'string') {
        if (time.includes('T')) {
          const timePart = time.split('T')[1]
          if (timePart) {
            timeStr = timePart.substring(0, 8)
          }
        } else if (time.includes(':')) {
          const parts = time.split(':')
          if (parts.length === 2) {
            timeStr = `${parts[0]}:${parts[1]}:00`
          } else {
            timeStr = time.substring(0, 8)
          }
        } else {
          timeStr = time
        }
      } else if (time instanceof Date) {
        const hours = String(time.getHours()).padStart(2, '0')
        const minutes = String(time.getMinutes()).padStart(2, '0')
        const seconds = String(time.getSeconds()).padStart(2, '0')
        timeStr = `${hours}:${minutes}:${seconds}`
      } else {
        timeStr = String(time)
      }
      
      return `${dateStr} ${timeStr}`
    },
    hideAdjacentMonthDates() {
      this.$nextTick(() => {
        const calendarEl = this.$el.querySelector('.calendar-wrapper')
        if (calendarEl) {
          const notCurrentMonthCells = calendarEl.querySelectorAll('.is-not-current-month, .prev-month, .next-month')
          notCurrentMonthCells.forEach(cell => {
            if (cell.tagName === 'TD') {
              cell.style.visibility = 'hidden'
            } else {
              const td = cell.closest('td')
              if (td) {
                td.style.visibility = 'hidden'
              }
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
$bg-panel: #0f1a26;
$teal: #54907a;
$teal-dark: #2f6b5a;
$teal-border: rgba(84, 144, 122, 0.35);
$text: rgba(255, 255, 255, 0.92);
$text-muted: rgba(255, 255, 255, 0.62);

.attendance-tab {
  .main-content {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 16px;
    margin-bottom: 16px;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  .punch-card,
  .calendar-card,
  .record-card {
    background: linear-gradient(180deg, #122033 0%, $bg-panel 100%);
    border-radius: 8px;
    padding: 22px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);
    border: 1px solid $teal-border;

    &.punch-card {
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      padding: 20px;
    }

    .card-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 0;

      i {
        font-size: 20px;
        color: $teal;
      }

      h3 {
        margin: 0;
        font-size: 18px;
        color: $text;
        font-weight: 600;
      }

      .subtitle {
        margin-left: auto;
        font-size: 13px;
        color: $text-muted;
      }
    }

    .punch-tip {
      color: $text-muted;
      font-size: 13px;
      margin: 8px 0 0;
    }

    .time-display {
      text-align: center;
      margin-top: auto;
      margin-bottom: 16px;

      .current-time {
        font-size: 18px;
        font-weight: 600;
        color: $text;
        margin-bottom: 6px;
      }

      .current-date {
        font-size: 13px;
        color: $text-muted;
      }
    }

    .punch-buttons {
      display: flex;
      gap: 16px;
      justify-content: center;
      margin-top: 0;

      .punch-btn {
        padding: 12px 28px;
        font-size: 14px;
        border-radius: 4px;
        border: none;
        transition: all 0.25s;
        font-weight: 600;

        i {
          margin-right: 6px;
          font-size: 16px;
        }

        &.punch-in-btn {
          background: $teal-dark !important;
          color: #fff !important;
          border: 1px solid $teal !important;
          box-shadow: 0 2px 10px rgba(47, 107, 90, 0.45) !important;

          &:hover:not(:disabled) {
            background: #3a8570 !important;
          }

          &.punch-in-active,
          &:disabled {
            background: $teal-dark !important;
            opacity: 0.85;
            cursor: not-allowed;
          }
        }

        &.punch-out-btn {
          background: rgba(84, 144, 122, 0.15) !important;
          color: rgba(255, 255, 255, 0.9) !important;
          border: 1px solid $teal-border !important;

          &:hover:not(:disabled) {
            background: rgba(84, 144, 122, 0.28) !important;
            border-color: $teal !important;
          }

          &.punch-out-active,
          &:disabled {
            background: $teal-dark !important;
            color: #fff !important;
            border-color: $teal !important;
            opacity: 0.85;
            cursor: not-allowed;
          }
        }

        &:disabled {
          opacity: 0.85;
        }
      }
    }
  }

  .calendar-card {
    padding: 18px;
    overflow: hidden;

    .calendar-wrapper {
      ::v-deep .el-calendar {
        border: none;
        background: transparent;
        color: #fff;
      }

      ::v-deep .el-calendar__header {
        padding: 0 0 12px 0;
        border-bottom: 1px solid rgba(84, 144, 122, 0.28);
        margin-bottom: 12px;
      }

      ::v-deep .el-calendar__title {
        font-size: 15px;
        font-weight: 600;
        color: $text !important;
      }

      ::v-deep .el-calendar__button-group {
        display: flex;
        gap: 8px;
      }

      ::v-deep .el-button {
        padding: 5px 10px;
        font-size: 12px;
        background: rgba(84, 144, 122, 0.12);
        border-color: $teal-border;
        color: rgba(255, 255, 255, 0.82) !important;

        &:hover {
          background: rgba(84, 144, 122, 0.22);
          border-color: $teal;
          color: #fff !important;
        }
      }

      ::v-deep .el-calendar-table {
        font-size: 13px;
        background: transparent;
        color: #fff;
        border-collapse: collapse;
        border: 1px solid rgba(84, 144, 122, 0.18);
      }

      ::v-deep .el-calendar-table thead th {
        padding: 8px 0;
        font-size: 13px;
        color: $text-muted !important;
        border-bottom: 1px solid rgba(84, 144, 122, 0.28);
        border-left: 1px solid rgba(84, 144, 122, 0.16);
        background: transparent;
      }

      ::v-deep .el-calendar-table thead th:first-child {
        border-left: none;
      }

      ::v-deep .el-calendar-table td {
        padding: 6px 0;
        border: 1px solid rgba(84, 144, 122, 0.16);
        background: transparent;
        text-align: center !important;
      }

      ::v-deep .el-calendar-table .el-calendar-day {
        height: 36px;
        line-height: 36px;
        padding: 0;
        color: rgba(255, 255, 255, 0.72) !important;
        background: transparent;
        text-align: center !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;

        &:hover {
          background: rgba(84, 144, 122, 0.14);
          color: #fff !important;
        }
      }

      ::v-deep .el-calendar-day.is-selected {
        background: rgba(84, 144, 122, 0.32) !important;
        color: #fff !important;
      }

      ::v-deep .el-calendar-table td.is-today .el-calendar-day {
        background: rgba(84, 144, 122, 0.22) !important;
        border-radius: 6px !important;
        border: 1px solid rgba(84, 144, 122, 0.55) !important;
        color: #8fd4b8 !important;
        font-weight: 600 !important;
      }

      ::v-deep .el-calendar-day.is-not-current-month,
      ::v-deep .el-calendar-table td.is-not-current-month,
      ::v-deep .el-calendar-table td.is-not-current-month .el-calendar-day,
      ::v-deep td.prev-month,
      ::v-deep td.next-month,
      ::v-deep .el-calendar-table td.prev-month,
      ::v-deep .el-calendar-table td.next-month {
        visibility: hidden !important;
      }
    }
  }

  .record-card {
    grid-column: 1 / -1;

    .table-container {
      max-height: 500px;
      overflow-y: auto;
      position: relative;

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: rgba(10, 21, 32, 0.5);
        border-radius: 3px;
      }

      &::-webkit-scrollbar-thumb {
        background: rgba(84, 144, 122, 0.45);
        border-radius: 3px;

        &:hover {
          background: rgba(84, 144, 122, 0.65);
        }
      }

      .loading-more,
      .no-more {
        text-align: center;
        padding: 15px 0;
        color: $text-muted;
        font-size: 13px;

        i {
          margin-right: 5px;
        }
      }

      .no-more {
        color: rgba(255, 255, 255, 0.4);
      }
    }

    ::v-deep .el-table {
      background: transparent;
      color: #fff;

      &::before {
        background-color: rgba(84, 144, 122, 0.2);
      }

      th {
        background: #0c1824 !important;
        color: rgba(255, 255, 255, 0.78) !important;
        border-bottom: 1px solid rgba(84, 144, 122, 0.28);
        font-weight: 600;
      }

      td {
        background: transparent !important;
        color: rgba(255, 255, 255, 0.85);
        border-bottom: 1px solid rgba(84, 144, 122, 0.14);
      }

      tr {
        background: transparent;

        &:hover > td {
          background: rgba(84, 144, 122, 0.1) !important;
        }
      }

      &.el-table--striped .el-table__body tr.el-table__row--striped td {
        background: rgba(10, 21, 32, 0.35) !important;
      }
    }
  }

  .statistics-section {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }

    .stat-card {
      background: linear-gradient(180deg, #122033 0%, $bg-panel 100%);
      border-radius: 8px;
      padding: 22px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);
      border: 1px solid $teal-border;
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      transition: transform 0.2s ease, border-color 0.2s ease;

      &:hover {
        border-color: rgba(84, 144, 122, 0.55);
        transform: translateY(-2px);
      }

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 14px;

        i {
          font-size: 26px;
          color: white;
        }
      }

      .stat-content {
        margin-bottom: 8px;

        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: $text;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 13px;
          color: $text-muted;
        }
      }

      .stat-title {
        font-size: 15px;
        color: rgba(255, 255, 255, 0.82);
        font-weight: 500;
      }
    }
  }
}
</style>

