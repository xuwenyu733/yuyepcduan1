<template>
  <div class="video-container">
    <video id="videoPlayer" class="video-js vjs-big-play-centered" controls></video>
  </div>
</template>

<script>
// 移除未使用的axios导入
import service from '@/utils/request'
// 导入video.js库及其样式
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
// 导入HLS插件以支持m3u8格式
import 'videojs-contrib-hls'

export default {
  name: 'EzvizPlayer',
  props: {
    deviceSerial: {
      type: String,
      required: true
    },
    channelNo: {
      type: Number,
      default: 1
    },
    liveUrl: {
      type: String,
      required: false,
      default: ''
    }
  },
  data() {
    return {
      internalLiveUrl: '',
      player: null
    }
  },
  mounted() {
    if (this.liveUrl) {
      this.internalLiveUrl = this.liveUrl
      this.initPlayer()
    } else {
      this.getLiveUrl()
    }
  },
  beforeDestroy() {
    // 清理播放器实例
    if (this.player) {
      this.player.dispose()
      this.player = null
    }
  },
  methods: {
    getLiveUrl() {
      if (!this.deviceSerial) {
        console.error('未提供设备序列号')
        return
      }
      // 修改请求路径，避免与baseURL重复导致的404错误
      service.get('/ezviz/liveAddress', {
        params: {
          deviceSerial: this.deviceSerial,
          channelNo: this.channelNo
        }
      }).then(response => {
        // service已经处理了响应，直接使用response.data
        this.internalLiveUrl = response.data
        // 在web后台打印获取到的视频地址
        console.info('获取直播地址成功:', this.internalLiveUrl)
        console.log('===== 视频地址信息 =====')
        console.log('设备序列号:', this.deviceSerial)
        console.log('通道号:', this.channelNo)
        console.log('直播地址:', this.internalLiveUrl)
        console.log('======================')
        this.initPlayer()
      }).catch(error => {
        console.error('获取直播地址失败:', error)
        // 添加回退机制，当API请求失败时使用默认视频地址
        // console.log('使用默认视频地址作为回退方案')
        // // 使用示例中的HLS格式视频地址
        // this.internalLiveUrl = `https://open.ys7.com/v3/openlive/${this.deviceSerial}_${this.channelNo}_1.m3u8`
        // this.initPlayer()
      })
    },
    initPlayer() {
      if (!this.internalLiveUrl) {
        console.error('没有可用的直播地址')
        return
      }
      
      // 清理之前的播放器实例
      if (this.player) {
        this.player.dispose()
      }
      
      try {
        // 初始化video.js播放器
        this.player = videojs('videoPlayer', {
          autoplay: false,
          controls: true,
          preload: 'auto',
          sources: [{
            src: this.internalLiveUrl,
            type: 'application/x-mpegURL'  // HLS流类型
          }]
        })
        
        // 监听错误事件
        this.player.on('error', (error) => {
          console.error('播放器错误:', error)
        })
        
        console.log('播放器初始化成功')
      } catch (error) {
        console.error('播放器初始化失败:', error)
      }
    }
  }
}
</script>

<style scoped>
/* .video-container {
  width: 100%;
  height: 100%;
  position: relative;
} */

/* 确保video.js播放器占满容器 */
/* .video-js {
  width: 100% !important;
  height: 100% !important;
} */
 .video-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

::v-deep .video-js {
  width: 100%;
  height: 675px; 
}
</style>