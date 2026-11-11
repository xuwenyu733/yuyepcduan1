<template>
  <div class="video-container">
    <!-- 为ezuikit-js播放器提供挂载点 -->
    <div id="ezuikit-player"></div>
    
    <!-- 云台控制按钮组 -->
    <!-- 播放器已内置云台控制，此部分注释掉以备后期使用 -->
    <!-- <div v-if="showPtzControls" class="ptz-controls">
      <button @click="controlPtz('up')" class="ptz-btn">⬆️</button>
      <div class="ptz-row">
        <button @click="controlPtz('left')" class="ptz-btn">⬅️</button>
        <button @click="controlPtz('right')" class="ptz-btn">➡️</button>
      </div>
      <button @click="controlPtz('down')" class="ptz-btn">⬇️</button>
    </div> -->
    
    <!-- 定时截图设置 -->
    <div class="screenshot-controls">
      <el-button @click="toggleScreenshotTimer" :type="screenshotTimer ? 'danger' : 'primary'">
        {{ screenshotTimer ? '停止定时截图' : '开始定时截图' }}
      </el-button>
      <el-input-number v-model="screenshotInterval" :min="5" :max="3600" :step="5" style="width: 150px; margin-left: 10px;">
      </el-input-number>
      <span style="margin-left: 10px;">秒/次</span>
      
      <el-button @click="takeScreenshot" type="success" style="margin-left: 20px;">立即截图</el-button>
      <el-button @click="goToDetect" type="primary" style="margin-left: 20px;">目标检测</el-button>
    </div>
  </div>
</template>

<script>
import service from '@/utils/request'
// 导入ezuikit-js库（项目中已安装）
import { EZUIKitPlayer } from "ezuikit-js"

export default {
  name: 'EzPlayer',
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
    },
    accessToken: {
      type: String,
      required: false,
      default: '' // 可从父组件传入或从localStorage获取
    },
    showPtzControls: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      internalLiveUrl: '',
      player: null, // ezuikit-js播放器实例
      screenshotTimer: null, // 定时截图定时器
      screenshotInterval: 30, // 默认30秒截图一次
      ptzSpeed: 50, // 云台控制速度（0-100）
      tokenRetryCount: 0, // token重试次数
      maxTokenRetries: 3, // 最大token重试次数
      tokenRetryDelay: 1000 // 初始重试延迟（毫秒）
    }
  },
  mounted() {
    // 首先获取accessToken，然后再处理视频播放逻辑
    this.getAccessToken().then(() => {
      if (this.liveUrl) {
        this.internalLiveUrl = this.liveUrl
        this.initPlayer()
      } else {
        this.getLiveUrl()
      }
    }).catch(() => {
      console.warn('获取accessToken失败，尝试使用已有的liveUrl继续')
      if (this.liveUrl) {
        this.internalLiveUrl = this.liveUrl
        this.initPlayer()
      } else {
        console.error('无法播放视频：没有有效的liveUrl，且获取accessToken失败')
      }
    })
  },
  beforeDestroy() {
    // 清理播放器实例和定时器
    if (this.screenshotTimer) {
      clearInterval(this.screenshotTimer)
      this.screenshotTimer = null
    }
    
    if (this.player) {
      this.player.stop()
      this.player = null
    }
  },
  methods: {
    getAccessToken() {
      // 重置重试计数
      this.tokenRetryCount = 0
      
      // 根据需求，直接通过API获取accessToken，不考虑localStorage中的token
      console.log('开始通过API获取accessToken...')
      
      // 验证token的辅助函数
      const validateToken = (token) => {
        // 简单验证token格式是否有效（非空且有一定长度）
        return token && token.trim().length > 10
      }
      
      // 直接调用API获取accessToken
      return service.get('/ezviz/token')
        .then(response => {
          console.log('API响应数据:', JSON.stringify(response))
          
          // 更健壮的响应数据解析，支持多种可能的响应格式
          let newToken = ''
          
          // 尝试从不同的响应结构中提取token
          if (response && response.accessToken) {
            newToken = response.accessToken
            console.log('从response.accessToken获取token')
          } else if (response && response.data && response.data.accessToken) {
            // 兼容不同的响应格式
            newToken = response.data.accessToken
            console.log('从response.data.accessToken获取token')
          } else if (response && typeof response === 'string') {
            // 处理响应可能是纯字符串的情况
            newToken = response
            console.log('从字符串响应直接获取token')
          } else if (response && response.data && typeof response.data === 'string') {
            newToken = response.data
            console.log('从response.data字符串获取token')
          }
          
          console.log('解析结果 - token是否存在:', !!newToken, '长度:', newToken ? newToken.length : 0)
          
          if (validateToken(newToken)) {
            this.accessToken = newToken
            console.log('✅ 通过API获取accessToken成功')
            // 仍然保存到localStorage以便调试时查看，但主要依赖API获取
            localStorage.setItem('accessToken', newToken)
            return newToken
          } else {
            console.error('❌ API返回的accessToken为空或无效:', newToken)
            throw new Error('获取accessToken失败：返回数据无效')
          }
        })
        .catch(error => {
          console.error('❌ 获取accessToken失败 - 错误详情:', error)
          
          // 提供更详细的错误信息
          let errorMessage = '未知错误'
          if (error.response) {
            errorMessage = `API请求失败: ${error.response.status} ${error.response.statusText}`
            console.error('❌ HTTP错误详情:', JSON.stringify(error.response))
          } else if (error.request) {
            errorMessage = '网络请求失败，无法连接到服务器'
            console.error('❌ 网络请求失败:', error.request)
          } else {
            errorMessage = error.message || '请求处理错误'
          }
          
          const detailedError = new Error(`获取AccessToken失败: ${errorMessage}`)
          detailedError.originalError = error
          
          // 通知父组件
          this.$emit('token-error', detailedError)
          return Promise.reject(detailedError)
        })
    },
    getLiveUrl() {
      if (!this.deviceSerial) {
        console.error('未提供设备序列号')
        return
      }
      
      service.get('/ezviz/liveAddress', {
        params: {
          deviceSerial: this.deviceSerial,
          channelNo: this.channelNo
        }
      }).then(response => {
        this.internalLiveUrl = response.data
        console.info('获取直播地址成功:', this.internalLiveUrl)
        this.initPlayer()
      }).catch(error => {
        console.error('获取直播地址失败:', error)
        // 构建ezopen协议地址作为备用方案
        this.internalLiveUrl = `ezopen://open.ys7.com/${this.deviceSerial}/${this.channelNo}.live`
        this.initPlayer()
      })
    },
    
    initPlayer() {
      if (!this.internalLiveUrl) {
        console.error('没有可用的直播地址')
        return
      }
      
      // 确保accessToken存在
      if (!this.accessToken) {
        console.warn('accessToken不存在，尝试重新获取')
        this.getAccessToken().then(() => {
          this._initializePlayer()
        }).catch(error => {
          console.error('重新获取accessToken失败:', error)
          this.$emit('error', error)
        })
        return
      }
      
      this._initializePlayer()
    },
    
    // 实际的播放器初始化逻辑
    _initializePlayer() {
      // 确保容器存在
      const container = document.getElementById('ezuikit-player')
      if (!container) {
        console.error('播放器容器不存在')
        return
      }
      
      // 清空容器内容
      container.innerHTML = ''
      
      try {
        // 初始化ezuikit-js播放器
        this.player = new EZUIKitPlayer({
          id: "ezuikit-player", // 播放器容器ID
          url: this.internalLiveUrl,
          accessToken: this.accessToken,
          width: container.clientWidth,
          height: 675, // 保持与原样式一致
          handleError: (err) => {
            console.error('播放器错误:', err)
            // 处理AccessToken过期或错误的情况
            if (err.code === '10002' || err.msg && err.msg.includes('AccessToken')) {
              console.warn(`AccessToken无效或过期，尝试重新获取 (${this.tokenRetryCount + 1}/${this.maxTokenRetries})`)
              // 清除旧的token
              localStorage.removeItem('accessToken')
              
              // 使用指数退避策略进行重试
              if (this.tokenRetryCount < this.maxTokenRetries) {
                this.tokenRetryCount++
                const delay = this.tokenRetryDelay * Math.pow(2, this.tokenRetryCount - 1)
                
                console.log(`将在${delay}毫秒后重试获取token...`)
                setTimeout(() => {
                  this.getAccessToken().then(() => {
                    // 重置重试计数
                    this.tokenRetryCount = 0
                    // 重新初始化播放器
                    this._initializePlayer()
                  }).catch(retryError => {
                    console.error('重新获取AccessToken失败:', retryError)
                    // 如果达到最大重试次数，通知父组件
                    if (this.tokenRetryCount >= this.maxTokenRetries) {
                      this.$emit('token-error', {
                        message: '多次尝试获取AccessToken失败，请检查网络连接或重新登录',
                        originalError: retryError
                      })
                    }
                  })
                }, delay)
              } else {
                // 达到最大重试次数
                console.error('已达到最大AccessToken重试次数')
                this.$emit('token-error', {
                  message: '多次尝试获取AccessToken失败，请检查网络连接或重新登录',
                  originalError: err
                })
                // 重置重试计数
                this.tokenRetryCount = 0
              }
            } else {
              // 其他类型的错误
              this.$emit('error', err)
            }
          },
          handleSuccess: () => {
            console.log('播放器初始化成功')
            // 重置token重试计数
            this.tokenRetryCount = 0
            this.$emit('success')
          },
          handleCapturePicture: (data) => {
            console.log('截图成功，图片数据:', data)
            this.$emit('screenshot', data)
          },
          autoplay: true,
          download: true, // 截图自动下载到本地
          decoderType: 'auto', // 自动选择解码方式，更兼容
          // 添加云台控制相关配置
          openSound: false, // 关闭声音
          template: 'pcLive', // PC直播模板，通常包含云台控制功能
          hasPtz: this.showPtzControls // 根据是否显示云台控制按钮来决定是否启用云台功能
        })
        
        // 移除不支持的on方法调用，避免TypeError
        // 在当前版本的ezuikit-js中，事件监听可能通过配置选项而不是on方法
        console.log('播放器初始化完成，事件监听通过配置选项处理')
        
      } catch (error) {
        console.error('播放器初始化失败:', error)
        this.$emit('error', error)
      }
    },
    
    // 云台控制方法
    // controlPtz(direction) {
    //   if (!this.player) {
    //     console.error('播放器实例不存在')
    //     return
    //   }
      
    //   try {
    //     // 根据ezuikit-js 8.1.15版本特性，尝试多种可能的云台控制方法
    //     console.log(`尝试控制云台: ${direction}，速度: ${this.ptzSpeed}`)
        
    //     // 方法1: 尝试使用PTZCommand方法（这是某些版本中支持的方法名）
    //     if (typeof this.player.PTZCommand === 'function') {
    //       this.player.PTZCommand(direction, this.ptzSpeed)
    //       console.log(`使用PTZCommand方法控制云台: ${direction}`)
    //     }
    //     // 方法2: 尝试使用ptzDirection方法
    //     else if (typeof this.player.ptzDirection === 'function') {
    //       this.player.ptzDirection(direction, this.ptzSpeed)
    //       console.log(`使用ptzDirection方法控制云台: ${direction}`)
    //     }
    //     // 方法3: 尝试使用内部player对象
    //     else if (this.player.player) {
    //       if (typeof this.player.player.PTZCommand === 'function') {
    //         this.player.player.PTZCommand(direction, this.ptzSpeed)
    //         console.log(`使用内部player的PTZCommand方法控制云台: ${direction}`)
    //       }
    //       else if (typeof this.player.player.ptzDirection === 'function') {
    //         this.player.player.ptzDirection(direction, this.ptzSpeed)
    //         console.log(`使用内部player的ptzDirection方法控制云台: ${direction}`)
    //       }
    //     }
    //     // 方法4: 尝试通过自定义事件触发
    //     else {
    //       // 如果都没有，尝试通过自定义事件触发
    //       const event = new CustomEvent('ptzDirection', {
    //         detail: { direction, speed: this.ptzSpeed }
    //       })
    //       document.getElementById('ezuikit-player').dispatchEvent(event)
    //       console.log(`通过自定义事件ptzDirection触发云台控制: ${direction}`)
          
    //       // 额外尝试发送PTZ命令到DOM元素
    //       const playerElement = document.getElementById('ezuikit-player')
    //       if (playerElement && playerElement.contentWindow) {
    //         try {
    //           playerElement.contentWindow.postMessage({
    //             type: 'PTZ_COMMAND',
    //             direction: direction,
    //             speed: this.ptzSpeed
    //           }, '*')
    //           console.log(`通过postMessage发送云台控制命令: ${direction}`)
    //         } catch (postError) {
    //           console.warn('通过postMessage发送云台控制命令失败:', postError)
    //         }
    //       }
    //     }
        
    //     // 200ms后停止云台移动（模拟点击一次移动一下的效果）
    //     setTimeout(() => {
    //       if (this.player) {
    //         try {
    //           // 停止云台的多种可能方法
    //           if (typeof this.player.PTZStop === 'function') {
    //             this.player.PTZStop()
    //             console.log('使用PTZStop方法停止云台')
    //           }
    //           else if (typeof this.player.ptzStop === 'function') {
    //             this.player.ptzStop()
    //             console.log('使用ptzStop方法停止云台')
    //           }
    //           else if (this.player.player) {
    //             if (typeof this.player.player.PTZStop === 'function') {
    //               this.player.player.PTZStop()
    //               console.log('使用内部player的PTZStop方法停止云台')
    //             }
    //             else if (typeof this.player.player.ptzStop === 'function') {
    //               this.player.player.ptzStop()
    //               console.log('使用内部player的ptzStop方法停止云台')
    //             }
    //           }
    //           else {
    //             // 触发停止事件
    //             const stopEvent = new CustomEvent('ptzStop')
    //             document.getElementById('ezuikit-player').dispatchEvent(stopEvent)
    //             console.log('通过自定义事件ptzStop停止云台')
                
    //             // 额外尝试通过postMessage发送停止命令
    //             const playerElement = document.getElementById('ezuikit-player')
    //             if (playerElement && playerElement.contentWindow) {
    //               try {
    //                 playerElement.contentWindow.postMessage({
    //                   type: 'PTZ_STOP'
    //                 }, '*')
    //                 console.log('通过postMessage发送停止云台命令')
    //               } catch (stopPostError) {
    //                 console.warn('通过postMessage发送停止云台命令失败:', stopPostError)
    //               }
    //             }
    //           }
    //         } catch (stopError) {
    //           console.warn('停止云台时出错:', stopError)
    //         }
    //       }
    //     }, 200)
        
    //     this.$emit('ptz-control', { direction, status: 'success' })
    //   } catch (error) {
    //     console.error('云台控制失败:', error)
    //     this.$emit('ptz-error', error)
    //   }
    // },
    
    // 立即截图
    takeScreenshot() {
      if (!this.player) {
        console.error('播放器实例不存在')
        return
      }
      
      try {
        this.player.capturePicture()
        console.log('执行截图操作')
      } catch (error) {
        console.error('截图失败:', error)
      }
    },
    
    // 切换定时截图
    toggleScreenshotTimer() {
      if (this.screenshotTimer) {
        // 停止定时截图
        clearInterval(this.screenshotTimer)
        this.screenshotTimer = null
        console.log('定时截图已停止')
      } else {
        // 开始定时截图
        this.screenshotTimer = setInterval(() => {
          this.takeScreenshot()
        }, this.screenshotInterval * 1000)
        console.log(`定时截图已启动，间隔${this.screenshotInterval}秒`)
      }
    },

    // 跳转到目标检测页面
    goToDetect() {
      try {
        // 打开新的路由页面
        this.$router.push({
          path: '/keyshow/ezdetect',
        })
      } catch (error) {
        console.error('跳转到目标检测页面失败:', error)
        // 如果路由跳转失败，尝试使用window.open打开EzDetect.vue页面
        const detectUrl = '/src/components/EzvizPlayer/EzDetect.vue'
        window.open(detectUrl, '_blank')
      }
    }
  }
}
</script>

<style scoped>
.video-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

/* 播放器容器样式 */
#ezuikit-player {
  width: 100%;
  height: 675px;
  background-color: #000;
}

/* 云台控制按钮样式 */
.ptz-controls {
  position: absolute;
  bottom: 20px;
  left: 20px;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 10px;
  border-radius: 5px;
}

.ptz-row {
  display: flex;
  justify-content: center;
  margin: 5px 0;
}

.ptz-btn {
  width: 40px;
  height: 40px;
  margin: 5px;
  border: none;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
}

.ptz-btn:hover {
  background-color: rgba(255, 255, 255, 1);
  transform: scale(1.1);
}

/* 截图控制样式 */
.screenshot-controls {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 5px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
</style>