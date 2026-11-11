<template>
  <div class="video-container">
    <div class="prev-dialog-content">
        <div class="iframe-wrapper">
          <iframe id="video-frame" class="video-frame" ref="videoFrame" src="http://101.200.231.83:9006/video" 
              style="background-color: #000; overflow: hidden;" frameborder="0" allowfullscreen></iframe>
        </div>
        <div class="fullscreen-control">
            <button @click="toggleFullscreen" class="fullscreen-btn" title="全屏">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
                </svg>
            </button>
        </div>
    </div>
  </div>
</template>
<script>
// import IframeScaler from '@/utils/IframeScaler.js'
export default {
  name: 'EzDetect',
  data() {
    return {
      isFullscreen: false,
      originalStyles: null
    }
  },

  mounted() {
    // 初始化IframeScaler
    // const scaler = new IframeScaler('video-frame', {
    //     scale: 1, // 根据需要调整缩放比例
    //     maxWidth: '100%'
    // }).init();
    
    // 监听全屏状态变化
    document.addEventListener('fullscreenchange', this.handleFullscreenChange);
    document.addEventListener('webkitfullscreenchange', this.handleFullscreenChange);
    document.addEventListener('mozfullscreenchange', this.handleFullscreenChange);
    
    // 监听窗口大小变化，确保内容适配
    window.addEventListener('resize', this.handleResize);
    
    // 初始化时调整尺寸
    this.handleResize();
  },
  
  beforeDestroy() {
    // 移除全屏状态变化的监听
    document.removeEventListener('fullscreenchange', this.handleFullscreenChange);
    document.removeEventListener('webkitfullscreenchange', this.handleFullscreenChange);
    document.removeEventListener('mozfullscreenchange', this.handleFullscreenChange);
    
    // 移除窗口大小变化的监听
    window.removeEventListener('resize', this.handleResize);
  },
  
  methods: {
    handleResize() {
      if (this.isFullscreen) return;
      
      const container = document.querySelector('.video-container');
      const wrapper = document.querySelector('.iframe-wrapper');
      const iframe = document.getElementById('video-frame');
      
      if (container && wrapper && iframe) {
        // 计算容器尺寸
        const containerWidth = container.clientWidth;
        const containerHeight = container.clientHeight;
        
        // 设置wrapper和iframe尺寸
        wrapper.style.width = `${containerWidth}px`;
        wrapper.style.height = `${containerHeight}px`;
        iframe.style.width = '100%';
        iframe.style.height = '100%';
      }
    },
    
    toggleFullscreen() {
      const iframe = document.getElementById('video-frame');
      const prevDialogContent = document.querySelector('.prev-dialog-content');
      const fullscreenBtn = document.querySelector('.fullscreen-btn');
      
      // 检查当前是否全屏
      const isFullscreen = document.fullscreenElement || document.webkitFullscreenElement || document.mozFullScreenElement;
      
      if (!isFullscreen) {
        // 尝试直接请求iframe全屏
        try {
          if (iframe.requestFullscreen) {
            iframe.requestFullscreen();
          } else if (iframe.webkitRequestFullscreen) {
            iframe.webkitRequestFullscreen();
          } else if (iframe.mozRequestFullScreen) {
            iframe.mozRequestFullScreen();
          } else {
            // 如果iframe不能全屏，尝试请求容器全屏
            if (prevDialogContent.requestFullscreen) {
              prevDialogContent.requestFullscreen();
            } else if (prevDialogContent.webkitRequestFullscreen) {
              prevDialogContent.webkitRequestFullscreen();
            } else if (prevDialogContent.mozRequestFullScreen) {
              prevDialogContent.mozRequestFullScreen();
            }
          }
        } catch (err) {
          console.error(`全屏请求失败: ${err.message}`);
          // 降级方案 - 直接调整样式
          this.forceFullscreen();
        }
      } else {
        // 退出全屏
        try {
          if (document.exitFullscreen) {
            document.exitFullscreen();
          } else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
          } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
          }
        } catch (err) {
          console.error(`退出全屏失败: ${err.message}`);
          // 降级方案 - 恢复样式
          this.exitForceFullscreen();
        }
      }
    },
    
    // 处理全屏状态变化
    handleFullscreenChange() {
      const isFullscreen = document.fullscreenElement || document.webkitFullscreenElement || document.mozFullScreenElement;
      const fullscreenBtn = document.querySelector('.fullscreen-btn');
      
      this.isFullscreen = !!isFullscreen;
      
      // 更新按钮图标
      if (this.isFullscreen) {
        fullscreenBtn.innerHTML = `
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"/>
          </svg>
        `;
      } else {
        fullscreenBtn.innerHTML = `
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
          </svg>
        `;
      }
    },
    
    // 降级方案 - 强制全屏样式
    forceFullscreen() {
      const videoContainer = document.querySelector('.video-container');
      const prevDialogContent = document.querySelector('.prev-dialog-content');
      const wrapper = document.querySelector('.iframe-wrapper');
      const iframe = document.getElementById('video-frame');
      
      // 保存原始样式
      this.originalStyles = {
        videoContainer: {
          position: videoContainer.style.position,
          top: videoContainer.style.top,
          left: videoContainer.style.left,
          width: videoContainer.style.width,
          height: videoContainer.style.height,
          zIndex: videoContainer.style.zIndex
        },
        prevDialogContent: {
          position: prevDialogContent.style.position,
          top: prevDialogContent.style.top,
          left: prevDialogContent.style.left,
          width: prevDialogContent.style.width,
          height: prevDialogContent.style.height
        },
        wrapper: {
          width: wrapper.style.width,
          height: wrapper.style.height,
          position: wrapper.style.position,
          top: wrapper.style.top,
          left: wrapper.style.left
        },
        iframe: {
          width: iframe.style.width,
          height: iframe.style.height
        }
      };
      
      // 应用全屏样式
      videoContainer.style.position = 'fixed';
      videoContainer.style.top = '0';
      videoContainer.style.left = '0';
      videoContainer.style.width = '100vw';
      videoContainer.style.height = '100vh';
      videoContainer.style.zIndex = '9999';
      videoContainer.style.backgroundColor = '#000';
      
      prevDialogContent.style.position = 'absolute';
      prevDialogContent.style.top = '0';
      prevDialogContent.style.left = '0';
      prevDialogContent.style.width = '100%';
      prevDialogContent.style.height = '100%';
      
      wrapper.style.position = 'absolute';
      wrapper.style.top = '0';
      wrapper.style.left = '0';
      wrapper.style.width = '100%';
      wrapper.style.height = '100%';
      
      iframe.style.width = '100%';
      iframe.style.height = '100%';
      
      this.isFullscreen = true;
      
      // 阻止页面滚动
      document.body.style.overflow = 'hidden';
      
      // 更新按钮图标
      const fullscreenBtn = document.querySelector('.fullscreen-btn');
      fullscreenBtn.innerHTML = `
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"/>
        </svg>
      `;
    },
    
    // 退出强制全屏样式
    exitForceFullscreen() {
      const videoContainer = document.querySelector('.video-container');
      const prevDialogContent = document.querySelector('.prev-dialog-content');
      const wrapper = document.querySelector('.iframe-wrapper');
      const iframe = document.getElementById('video-frame');
      
      // 恢复原始样式
      if (this.originalStyles) {
        Object.assign(videoContainer.style, this.originalStyles.videoContainer);
        Object.assign(prevDialogContent.style, this.originalStyles.prevDialogContent);
        Object.assign(wrapper.style, this.originalStyles.wrapper);
        Object.assign(iframe.style, this.originalStyles.iframe);
        
        // 恢复页面滚动
        document.body.style.overflow = '';
        
        this.isFullscreen = false;
        
        // 更新按钮图标
        const fullscreenBtn = document.querySelector('.fullscreen-btn');
        fullscreenBtn.innerHTML = `
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
          </svg>
        `;
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
  /* 确保容器高度 */
  height: 675px;
}
.prev-dialog-content {
  width: 100%;
  height: 100%;
  position: relative;
}
.iframe-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}
.video-frame {
  width: 100%;
  height: 100%;
  background-color: #000;
  display: block;
  /* 确保内容能够完整显示 */
  object-fit: contain;
}

/* 全屏控制按钮样式 */
.fullscreen-control {
  position: absolute;
  bottom: 10px;
  right: 10px;
  z-index: 10;
}

.fullscreen-btn {
  width: 36px;
  height: 36px;
  border: none;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.fullscreen-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.fullscreen-btn:focus {
  outline: none;
}
</style>

/* 全局全屏样式 - 独立的style块 */
<style>
/* 全屏容器样式 */
.prev-dialog-content:fullscreen,
.prev-dialog-content:-webkit-full-screen,
.prev-dialog-content:-moz-full-screen {
  display: flex !important;
  width: 100% !important;
  height: 100% !important;
  background-color: #000 !important;
  margin: 0 !important;
  padding: 0 !important;
  overflow: hidden !important;
  justify-content: center;
  align-items: center;
}

/* iframe全屏样式 */
.video-frame:fullscreen,
.video-frame:-webkit-full-screen,
.video-frame:-moz-full-screen {
  display: block !important;
  width: 100% !important;
  height: 100% !important;
  max-width: 100% !important;
  max-height: 100% !important;
  background-color: #000 !important;
  object-fit: contain;
}

/* 容器全屏嵌套样式 */
:fullscreen .video-container,
:-webkit-full-screen .video-container,
:-moz-full-screen .video-container {
  display: block !important;
  width: 100% !important;
  height: 100% !important;
  max-width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
  background-color: #000 !important;
}

:fullscreen .prev-dialog-content,
:-webkit-full-screen .prev-dialog-content,
:-moz-full-screen .prev-dialog-content {
  display: flex !important;
  width: 100% !important;
  height: 100% !important;
  background-color: #000 !important;
  margin: 0 !important;
  padding: 0 !important;
  justify-content: center;
  align-items: center;
}

:fullscreen .iframe-wrapper,
:-webkit-full-screen .iframe-wrapper,
:-moz-full-screen .iframe-wrapper {
  display: flex !important;
  width: 100% !important;
  height: 100% !important;
  justify-content: center;
  align-items: center;
}

:fullscreen .video-frame,
:-webkit-full-screen .video-frame,
:-moz-full-screen .video-frame {
  display: block !important;
  width: auto !important;
  height: auto !important;
  max-width: 100% !important;
  max-height: 100% !important;
  background-color: #000 !important;
  object-fit: contain;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .video-container {
    height: auto !important;
    min-height: 300px;
    padding-top: 56.25%; /* 16:9 宽高比 */
  }
  
  .prev-dialog-content {
    position: absolute !important;
    top: 0 !important;
    left: 0 !important;
    width: 100% !important;
    height: 100% !important;
  }
  
  .iframe-wrapper {
    position: absolute !important;
    top: 0 !important;
    left: 0 !important;
    width: 100% !important;
    height: 100% !important;
  }
}
</style>