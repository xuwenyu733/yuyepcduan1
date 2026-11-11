<template>
  <div class="wps-editor-container">
    <div v-if="loading" class="wps-loading">
      <i class="el-icon-loading"></i>
      <span>{{ loadingText }}</span>
    </div>
    <div v-if="error" class="wps-error">
      <i class="el-icon-warning"></i>
      <span>{{ error }}</span>
      <el-button 
        v-if="error" 
        type="text" 
        size="small" 
        style="margin-top: 12px;"
        @click="retry">
        重试
      </el-button>
    </div>
    <!-- WPS在线编辑器容器 -->
    <div 
      ref="wpsContainer" 
      id="wps-editor"
      class="wps-editor-wrapper"
      v-show="!loading && !error">
    </div>
  </div>
</template>

<script>
export default {
  name: 'WPSEditor',
  props: {
    // 文档ID
    documentId: {
      type: [Number, String],
      required: true
    },
    // 文件URL
    fileUrl: {
      type: String,
      required: true
    },
    // 编辑模式：view-查看, edit-编辑
    mode: {
      type: String,
      default: 'view',
      validator: (value) => ['view', 'edit'].includes(value)
    },
    // WPS AppID（从环境变量或配置获取）
    appId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: true,
      error: '',
      wpsEditor: null,
      loadingText: '正在加载文档...',
      retryCount: 0,
      maxRetryCount: 3,
      _isDestroying: false, // 标记是否正在销毁
      _iframeObserver: null, // iframe观察器引用
    }
  },
  mounted() {
    // 设置全局错误处理，过滤跨域安全错误
    this.setupGlobalErrorHandler()
    
    // 监控网络请求，检查WPS SDK的接口调用
    this.setupNetworkMonitor()
    this.initWPSEditor()
  },
  beforeDestroy() {
    // 确保在组件销毁前清理编辑器
    if (!this._isDestroying) {
      this.destroyEditor()
    }
  },
  
  destroyed() {
    // 恢复原始错误处理器
    if (this._originalErrorHandler !== undefined) {
      window.onerror = this._originalErrorHandler
      this._originalErrorHandler = undefined
    }
    
    // 组件销毁后的最终清理
    this._isDestroying = false
    this.wpsEditor = null
    this._iframeObserver = null
  },
  watch: {
    fileUrl() {
      this.initWPSEditor()
    },
    mode() {
      this.initWPSEditor()
    }
  },
  methods: {
    // 设置全局错误处理，过滤跨域安全错误
    setupGlobalErrorHandler() {
      // 保存原始的error handler
      const originalErrorHandler = window.onerror
      
      // 重写window.onerror，过滤跨域安全错误
      window.onerror = (message, source, lineno, colno, error) => {
        const errorMsg = (message || '').toString()
        
        // 过滤WPS SDK的跨域安全错误（这些错误不影响功能）
        if (errorMsg.includes('SecurityError') || 
            errorMsg.includes('Blocked a frame') || 
            errorMsg.includes('cross-origin') ||
            errorMsg.includes('_wo_client_api')) {
          // 静默处理跨域错误，不显示在控制台
          console.debug('🔇 已过滤跨域安全错误（不影响功能）:', errorMsg.substring(0, 100))
          return true // 返回true表示错误已处理，不会继续传播
        }
        
        // 其他错误正常处理
        if (originalErrorHandler) {
          return originalErrorHandler.call(window, message, source, lineno, colno, error)
        }
        return false
      }
      
      // 保存引用以便在销毁时恢复
      this._originalErrorHandler = originalErrorHandler
    },
    
    // 设置网络请求监控
    setupNetworkMonitor() {
      // 拦截fetch请求
      const originalFetch = window.fetch
      window.fetch = (...args) => {
        const url = args[0]
        if (typeof url === 'string' && url.includes('/v3/3rd/')) {
          console.log('🔍 WPS SDK网络请求:', url, args[1] || {})
        }
        return originalFetch.apply(this, args)
          .then(response => {
            if (typeof args[0] === 'string' && args[0].includes('/v3/3rd/')) {
              console.log('✅ WPS SDK响应:', args[0], response.status, response.statusText)
              // 克隆响应以便读取内容
              response.clone().json().then(data => {
                console.log('📦 WPS SDK响应数据:', args[0], data)
              }).catch(() => {
                response.clone().text().then(text => {
                  console.log('📦 WPS SDK响应文本:', args[0], text.substring(0, 200))
                })
              })
            }
            return response
          })
          .catch(error => {
            if (typeof args[0] === 'string' && args[0].includes('/v3/3rd/')) {
              console.error('❌ WPS SDK请求失败:', args[0], error)
            }
            throw error
          })
      }
      
      // 拦截XMLHttpRequest
      const originalOpen = XMLHttpRequest.prototype.open
      const originalSend = XMLHttpRequest.prototype.send
      
      XMLHttpRequest.prototype.open = function(method, url, ...rest) {
        if (typeof url === 'string' && url.includes('/v3/3rd/')) {
          console.log('🔍 WPS SDK XHR请求:', method, url)
          this.addEventListener('load', function() {
            console.log('✅ WPS SDK XHR响应:', url, this.status, this.statusText)
            if (this.responseText) {
              try {
                const data = JSON.parse(this.responseText)
                console.log('📦 WPS SDK XHR响应数据:', url, data)
              } catch (e) {
                console.log('📦 WPS SDK XHR响应文本:', url, this.responseText.substring(0, 200))
              }
            }
          })
          this.addEventListener('error', function() {
            console.error('❌ WPS SDK XHR请求失败:', url, this.status, this.statusText)
          })
        }
        return originalOpen.apply(this, [method, url, ...rest])
      }
    },
    
    // 初始化WPS编辑器
    async initWPSEditor() {
      if (!this.fileUrl) {
        this.error = '文件URL不能为空'
        this.loading = false
        return
      }

      // 检查文件类型是否支持
      if (!this.isSupportedFileType()) {
        this.error = '该文件类型不支持在线预览，请下载后使用本地软件打开'
        this.loading = false
        return
      }

      this.loading = true
      this.error = ''
      this.loadingText = this.mode === 'edit' ? '正在加载编辑器...' : '正在加载文档...'

      try {
        this.destroyEditor()
        await this.loadWPSBySDK()
      } catch (error) {
        console.error('初始化WPS WebOffice SDK失败:', error)
        this.error = error.message || 'WPS WebOffice SDK加载失败，请检查网络连接或联系管理员'
        this.loading = false
        this.$emit('error', error)
      }
    },

    // 检查文件类型是否支持
    isSupportedFileType() {
      if (!this.fileUrl) return false
      
      const fileExt = this.fileUrl.split('.').pop().toLowerCase()
      const supportedTypes = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx']
      return supportedTypes.includes(fileExt)
    },

    // 重试加载
    retry() {
      if (this.retryCount >= this.maxRetryCount) {
        this.$message.warning('重试次数已达上限，请刷新页面重试')
        return
      }
      
      this.retryCount++
      this.error = ''
      this.initWPSEditor()
    },


    // 使用WPS WebOffice SDK（企业版正确方式：使用WebOfficeSDK.init()）
    async loadWPSBySDK() {
      const container = this.$refs.wpsContainer
      if (!container) {
        throw new Error('容器元素不存在')
      }

      // 确保容器有明确的尺寸（在弹窗中可能没有明确的高度）
      this.ensureContainerSize(container)

      // 检查是否已加载WebOffice SDK
      if (!window.WebOfficeSDK) {
        // 动态加载WebOffice SDK
        await this.loadWPSSDK()
      }

      if (!window.WebOfficeSDK) {
        throw new Error('WebOffice SDK加载失败，请确保已正确配置SDK地址')
      }

      this.loadingText = '正在初始化WebOffice编辑器...'

      // 根据文件类型确定officeType
      const urlToCheck = this.fileUrl
      if (!urlToCheck || !urlToCheck.includes('.')) {
        throw new Error('文件URL无效，无法确定文件类型: ' + urlToCheck)
      }

      let fileExt = urlToCheck.split('.').pop().toLowerCase()
      if (fileExt.includes('?')) {
        fileExt = fileExt.split('?')[0]
      }
      if (fileExt.includes('#')) {
        fileExt = fileExt.split('#')[0]
      }

      // 确定officeType（根据官方文档，可以使用枚举值或字符串值）
      // OfficeType枚举值：Writer='w', Spreadsheet='s', Presentation='p', Pdf='f', Otl='o', Dbt='d'
      let officeType = null
      if (window.WebOfficeSDK && window.WebOfficeSDK.OfficeType) {
        // 优先使用枚举值（更规范）
        if (['doc', 'docx'].includes(fileExt)) {
          officeType = window.WebOfficeSDK.OfficeType.Writer  // 或 'w'
        } else if (['xls', 'xlsx'].includes(fileExt)) {
          officeType = window.WebOfficeSDK.OfficeType.Spreadsheet  // 或 's'
        } else if (['ppt', 'pptx'].includes(fileExt)) {
          officeType = window.WebOfficeSDK.OfficeType.Presentation  // 或 'p'
        }
      }
      
      // 如果枚举值不可用，使用字符串值
      if (!officeType) {
        if (['doc', 'docx'].includes(fileExt)) {
          officeType = 'w'  // Word文档
        } else if (['xls', 'xlsx'].includes(fileExt)) {
          officeType = 's'  // Spreadsheet表格
        } else if (['ppt', 'pptx'].includes(fileExt)) {
          officeType = 'p'  // Presentation演示文稿
        } else {
          throw new Error(`不支持的文件类型: ${fileExt}，仅支持 doc/docx/xls/xlsx/ppt/pptx`)
        }
      }

      // 验证officeType不为空
      if (!officeType || officeType === '') {
        throw new Error(`officeType确定失败，文件扩展名: ${fileExt}, officeType: ${officeType}`)
      }

      // 获取appId
      const appId = this.appId || process.env.VUE_APP_WPS_APP_ID
      if (!appId) {
        throw new Error('WPS AppID未配置，请在环境变量中设置VUE_APP_WPS_APP_ID')
      }

      try {
        // 根据官方文档，WebOfficeSDK.init()的初始化参数：
        // 必填项：officeType, appId, fileId
        // 可选项：mount, token, refreshToken, endpoint, customArgs, attrAllow, isListenResize等
        // 
        // 注意：严格按照官方文档的"初始化参数"表格，不包含mode（mode属于"初始化配置项"）
        
        // 构建初始化参数对象（严格按照官方文档的"初始化参数"表格）
        const config = {
          // 必填项（官方文档明确要求）
          officeType: officeType,  // 可以使用枚举值或字符串值
          appId: appId,            // WPS AppID
          fileId: this.documentId.toString(),  // 文档ID
          
          // 可选项（官方文档明确提到）
          mount: container,  // 挂载节点，类型：string | HTMLElement，默认：document.body
          
          // 配置iframe的allow属性（允许剪贴板访问等）
          // 默认值：clipboard-read; clipboard-write;
          // 可以根据需要添加其他权限，如：autoplay, camera, geolocation等
          attrAllow: 'clipboard-read; clipboard-write;'
        }
        
        // 验证必填参数
        if (!config.officeType || config.officeType === '' || !config.appId || !config.fileId) {
          console.error('配置验证失败:', {
            officeType: config.officeType,
            appId: config.appId,
            fileId: config.fileId
          })
          throw new Error(`WebOffice SDK初始化参数不完整：officeType=${config.officeType}, appId=${config.appId}, fileId=${config.fileId}`)
        }

        console.log('WebOffice SDK初始化参数（严格按官方文档）:', {
          officeType: config.officeType,
          appId: config.appId,
          fileId: config.fileId,
          hasMount: !!config.mount,
          mountType: config.mount ? (typeof config.mount === 'string' ? 'string' : 'HTMLElement') : '未设置（将使用默认值document.body）'
        })
        
        console.log('⚠️ 注意：')
        console.log('  1. WPS服务器会调用WPS开放平台配置的回调地址')
        console.log('  2. 请确保WPS开放平台配置的回调地址正确（应该包含 /v3/3rd/ 路径）')
        console.log('  3. 回调地址格式：https://your-backend-url/v3/3rd/')
        console.log('  4. 确保后端服务正在运行，且可以访问回调接口')
        console.log('  5. 确保回调接口允许跨域访问（已添加@CrossOrigin注解）')
        
        try {
          // 根据官方文档，调用init()会挂载iframe，初始化数据和事件，并返回JSSDK实例
          console.log('调用WebOfficeSDK.init()...')
          const initResult = window.WebOfficeSDK.init(config)
          
          if (!initResult) {
            throw new Error('WebOffice SDK初始化失败，返回值为空')
          }

          this.wpsEditor = initResult
          console.log('✅ WebOfficeSDK.init()调用成功，已返回JSSDK实例')

          // 根据官方文档，实例可用于事件监听、页面定制、获取Application实例等
          // 先设置事件监听
          this.setupEventListeners()

          // 监听iframe创建并修复尺寸
          this.fixIframeSize()
          
          // 使用MutationObserver监听iframe的创建和尺寸变化
          this.observeIframeChanges()

          // 根据WPS官方文档，使用 await instance.ready() 等待Application对象加载
          if (typeof this.wpsEditor.ready === 'function') {
            console.log('调用ready()方法加载Application对象...')
            console.log('根据WPS文档：使用 await instance.ready() 等待完成')
            
            try {
              // 使用 await 等待ready()完成（符合文档要求）
              await this.wpsEditor.ready()
              
              console.log('✅ ready()方法调用成功，Application对象已加载')
              console.log('现在可以使用 instance.Application 进行API调用了')
              
              // 检查Application对象是否可用
              if (this.wpsEditor.Application) {
                console.log('✅ Application对象已可用:', typeof this.wpsEditor.Application)
              }
              
              if (this.loading) {
                this.loading = false
                this.$emit('loaded')
              }
            } catch (error) {
              console.error('❌ ready()方法调用失败:', error)
              console.error('错误详情:', {
                message: error.message,
                stack: error.stack
              })
              // ready()失败可能是因为回调接口未调用
              console.warn('ready()失败，可能原因：')
              console.warn('  1. WPS服务器无法调用后端回调接口')
              console.warn('  2. 请检查后端日志是否有WPS服务器的请求')
              console.warn('  3. 请检查WPS开放平台配置的回调地址是否正确')
              // 不抛出错误，等待fileOpen事件来检测加载状态
            }
          } else {
            console.warn('⚠️ SDK实例没有ready()方法，仅使用事件监听方式')
          }
          
        } catch (initError) {
          console.error('WebOfficeSDK.init()调用失败:', initError)
          throw initError
        }
      } catch (error) {
        console.error('初始化WebOffice SDK失败:', error)
        throw error
      }
    },

    // 动态加载WebOffice SDK
    loadWPSSDK() {
      return new Promise((resolve, reject) => {
        if (window.WebOfficeSDK) {
          resolve()
          return
        }

        if (window._wpsSDKLoading) {
          const checkInterval = setInterval(() => {
            if (window.WebOfficeSDK) {
              clearInterval(checkInterval)
              window._wpsSDKLoading = false
              resolve()
            }
          }, 100)
          return
        }

        window._wpsSDKLoading = true
        
        const sdkUrl = process.env.VUE_APP_WPS_SDK_URL
        if (!sdkUrl) {
          reject(new Error(
            'WPS WebOffice SDK地址未配置。\n' +
            '请执行以下步骤：\n' +
            '1. 访问 https://solution.wps.cn/ 下载WebOffice SDK\n' +
            '2. 将SDK文件放到项目的public目录（如：public/libs/wps/web-office-sdk-solution.umd.js）\n' +
            '3. 在.env文件中配置：VUE_APP_WPS_SDK_URL=/libs/wps/web-office-sdk-solution.umd.js'
          ))
          return
        }
        
        if (sdkUrl.startsWith('http://') || sdkUrl.startsWith('https://')) {
          reject(new Error('不能使用在线SDK地址，必须下载到本地'))
          return
        }
        
        const script = document.createElement('script')
        script.src = sdkUrl
        
        script.onload = () => {
          window._wpsSDKLoading = false
          setTimeout(() => {
            if (window.WebOfficeSDK) {
              resolve()
            } else {
              reject(new Error('WebOffice SDK加载后未找到SDK对象'))
            }
          }, 1000)
        }
        
        script.onerror = () => {
          window._wpsSDKLoading = false
          reject(new Error('WebOffice SDK加载失败，请检查SDK文件是否存在'))
        }
        
        document.head.appendChild(script)
      })
    },

    // 设置事件监听器
    setupEventListeners() {
      if (!this.wpsEditor) {
        console.warn('wpsEditor为空，无法设置事件监听器')
        return
      }

      console.log('设置WebOffice SDK事件监听器...')
      console.log('检查事件监听方式:', {
        hasOn: typeof this.wpsEditor.on === 'function',
        hasApiEvent: typeof this.wpsEditor.ApiEvent === 'object',
        hasAddApiEventListener: typeof this.wpsEditor.ApiEvent?.AddApiEventListener === 'function'
      })

      // 优先使用ApiEvent方式（新版本推荐）
      if (this.wpsEditor.ApiEvent && typeof this.wpsEditor.ApiEvent.AddApiEventListener === 'function') {
        console.log('使用ApiEvent方式监听事件（推荐）')
        
        // 根据文档，fileOpen事件需要在ready()之前注册
        this.wpsEditor.ApiEvent.AddApiEventListener('fileOpen', (data) => {
          console.log('📄 fileOpen事件触发:', data)
          console.log('📄 fileOpen事件完整数据:', JSON.stringify(data, null, 2))
          
          if (data && data.success) {
            console.log('✅ 文件打开成功:', data.fileInfo)
            console.log('📋 文件信息详情:', {
              id: data.fileInfo?.id,
              name: data.fileInfo?.name,
              officeType: data.fileInfo?.officeType,
              size: data.fileInfo?.size,
              createTime: data.fileInfo?.createTime,
              modifyTime: data.fileInfo?.modifyTime
            })
            
            // 检查officeType是否为空
            if (!data.fileInfo?.officeType || data.fileInfo.officeType === '') {
              console.warn('⚠️ 警告：fileInfo中的officeType为空，可能导致文档无法正确显示')
              console.warn('⚠️ 请检查后端getFileInfo接口是否正确返回了officeType字段')
            }
            
            if (this.loading) {
              this.loading = false
              this.$emit('loaded')
            }
            
            // 文件打开成功后，确保iframe尺寸正确
            setTimeout(() => {
              this.fixIframeSize()
            }, 100)
          } else {
            console.error('❌ 文件打开失败:', data)
            console.error('❌ 失败原因:', data?.msg || data?.result || data?.reason || '未知错误')
            this.error = data?.msg || data?.result || data?.reason || '文件打开失败'
            this.loading = false
            this.$emit('error', data)
          }
        })
        
        // 监听错误事件
        this.wpsEditor.ApiEvent.AddApiEventListener('error', (data) => {
          console.error('❌ error事件触发:', data)
          console.error('❌ 错误详情:', JSON.stringify(data, null, 2))
          console.error('❌ 错误原因:', data?.reason || data?.msg || data?.message || '未知错误')
          
          // 忽略跨域安全错误（这些错误不影响功能，是浏览器安全策略）
          const errorMsg = (data?.reason || data?.msg || data?.message || '').toString()
          if (errorMsg.includes('SecurityError') || 
              errorMsg.includes('Blocked a frame') || 
              errorMsg.includes('cross-origin')) {
            console.warn('⚠️ 检测到跨域安全错误（已忽略，不影响功能）:', errorMsg)
            return // 不处理跨域错误，继续执行
          }
          
          if (this.loading && !this._isDestroying) {
            this.error = data?.reason || data?.msg || data?.message || '编辑器发生错误'
            this.loading = false
            this.$emit('error', data)
          }
        })
        
        // 监听文件加载错误
        this.wpsEditor.ApiEvent.AddApiEventListener('fileLoadError', (data) => {
          console.error('❌ fileLoadError事件触发:', data)
          console.error('❌ 文件加载失败，可能原因：')
          console.error('  1. 文件下载URL无法访问')
          console.error('  2. 文件URL格式不正确（必须是公网可访问的HTTP(S) URL）')
          console.error('  3. 文件内容为空或损坏')
          console.error('  4. 网络连接问题')
          console.error('错误详情:', JSON.stringify(data, null, 2))
          
          this.error = data?.reason || data?.msg || '文件加载失败，请检查文件URL是否可访问'
          this.loading = false
          this.$emit('error', data)
        })
        
        // 监听文件加载进度
        this.wpsEditor.ApiEvent.AddApiEventListener('fileLoadProgress', (data) => {
          console.log('📊 文件加载进度:', data)
        })
        
        // 监听文件状态变化
        this.wpsEditor.ApiEvent.AddApiEventListener('fileStatus', (data) => {
          console.log('📋 文件状态变化:', data)
        })
        
        // 监听文档保存状态
        this.wpsEditor.ApiEvent.AddApiEventListener('DocumentSaveStatus', (data) => {
          console.log('💾 DocumentSaveStatus事件触发:', data)
          if (data && data.status === 1) {
            // 保存成功
            this.$emit('save', {
              documentId: this.documentId,
              fileUrl: this.fileUrl,
              status: data.status
            })
          }
        })
        
      } else if (typeof this.wpsEditor.on === 'function') {
        // 降级使用on()方式（旧版本兼容）
        console.log('使用on()方式监听事件（兼容模式）')
        
        // 设置ready事件监听
        const readyEventHandler = () => {
          console.log('📢 WebOffice编辑器ready事件触发')
          if (this.loading) {
            this.loading = false
            this.$emit('loaded')
          }
        }
        
        this.wpsEditor.on('ready', readyEventHandler)

        this.wpsEditor.on('save', (data) => {
          console.log('WebOffice编辑器save事件触发:', data)
          this.$emit('save', {
            documentId: this.documentId,
            fileUrl: this.fileUrl,
            ...data
          })
        })

        this.wpsEditor.on('error', (error) => {
          console.error('WebOffice编辑器error事件触发:', error)
          this.error = error.message || error.reason || '编辑器加载失败'
          this.loading = false
          this.$emit('error', error)
        })
        
        // 监听fileOpen事件（需要在ready()之前注册）
        this.wpsEditor.on('fileOpen', (data) => {
          console.log('WebOffice编辑器fileOpen事件触发:', data)
          // fileOpen事件表示文件已打开
          if (data && data.success) {
            console.log('✅ 文件打开成功:', data.fileInfo)
            if (this.loading) {
              this.loading = false
              this.$emit('loaded')
            }
          } else {
            console.error('❌ 文件打开失败:', data)
            this.error = data?.msg || data?.result || '文件打开失败'
            this.loading = false
            this.$emit('error', data)
          }
        })
        
        // 监听文件加载错误
        this.wpsEditor.on('fileLoadError', (error) => {
          console.error('❌ WebOffice编辑器文件加载错误:', error)
          this.error = error.message || error.reason || '文件加载失败，请检查文件URL是否可访问'
          this.loading = false
          this.$emit('error', error)
        })
        
        // 监听文件加载进度
        this.wpsEditor.on('fileLoadProgress', (progress) => {
          console.log('📊 WebOffice编辑器文件加载进度:', progress)
        })
        
        // 监听所有可能的错误事件
        this.wpsEditor.on('error', (error) => {
          console.error('❌ WebOffice编辑器error事件:', error)
          if (this.loading) {
            this.error = error.message || error.reason || '编辑器加载失败'
            this.loading = false
            this.$emit('error', error)
          }
        })
        
        // 监听文件打开失败
        this.wpsEditor.on('fileOpenError', (error) => {
          console.error('❌ WebOffice编辑器文件打开错误:', error)
          this.error = error.message || error.reason || '文件打开失败，请检查文件URL和网络连接'
          this.loading = false
          this.$emit('error', error)
        })
        
        // 监听网络错误
        this.wpsEditor.on('networkError', (error) => {
          console.error('❌ WebOffice编辑器网络错误:', error)
          this.error = '网络连接失败，请检查网络或后端接口是否正常'
          this.loading = false
          this.$emit('error', error)
        })
        
        // 监听loadComplete事件（如果存在）
        if (typeof this.wpsEditor.on === 'function') {
          try {
            this.wpsEditor.on('loadComplete', () => {
              console.log('WebOffice编辑器loadComplete事件触发')
              if (this.loading) {
                this.loading = false
                this.$emit('loaded')
              }
            })
          } catch (e) {
            console.warn('loadComplete事件不支持:', e)
          }
        }
        
      } else {
        console.warn('WebOffice SDK实例没有on方法，无法设置事件监听')
      }
    },

    // 确保容器有明确的尺寸（在弹窗中可能没有明确的高度）
    ensureContainerSize(container) {
      if (!container) {
        return
      }

      // 获取容器的父元素
      const parentElement = container.parentElement
      if (!parentElement) {
        return
      }

      // 检查容器是否有明确的高度
      const containerStyle = window.getComputedStyle(container)
      const parentStyle = window.getComputedStyle(parentElement)
      
      console.log('容器尺寸检查:', {
        containerWidth: containerStyle.width,
        containerHeight: containerStyle.height,
        parentWidth: parentStyle.width,
        parentHeight: parentStyle.height,
        containerOffsetWidth: container.offsetWidth,
        containerOffsetHeight: container.offsetHeight
      })

      // 如果容器高度为0或很小，尝试设置一个默认高度
      if (container.offsetHeight < 100) {
        // 尝试从父元素获取高度
        const parentHeight = parentElement.offsetHeight || parentElement.clientHeight
        if (parentHeight > 0) {
          container.style.height = parentHeight + 'px'
          console.log('设置容器高度:', parentHeight + 'px')
        } else {
          // 如果父元素也没有高度，设置一个默认值
          container.style.height = '600px'
          console.log('设置容器默认高度: 600px')
        }
      }

      // 确保容器宽度正确
      if (container.offsetWidth < 100) {
        const parentWidth = parentElement.offsetWidth || parentElement.clientWidth
        if (parentWidth > 0) {
          container.style.width = parentWidth + 'px'
          console.log('设置容器宽度:', parentWidth + 'px')
        } else {
          container.style.width = '100%'
          console.log('设置容器宽度: 100%')
        }
      }
    },

    // 监听并修复iframe尺寸
    fixIframeSize() {
      // 延迟执行，确保iframe已经创建
      this.$nextTick(() => {
        setTimeout(() => {
          const container = this.$refs.wpsContainer
          if (!container) {
            return
          }

          // 查找所有iframe元素
          const iframes = container.querySelectorAll('iframe')
          iframes.forEach((iframe, index) => {
            const iframeStyle = window.getComputedStyle(iframe)
            console.log(`iframe[${index}]尺寸检查:`, {
              width: iframeStyle.width,
              height: iframeStyle.height,
              offsetWidth: iframe.offsetWidth,
              offsetHeight: iframe.offsetHeight,
              inlineStyle: iframe.getAttribute('style')
            })

            // 如果iframe尺寸为0，强制设置尺寸
            if (iframe.offsetWidth === 0 || iframe.offsetHeight === 0) {
              const containerWidth = container.offsetWidth || container.clientWidth
              const containerHeight = container.offsetHeight || container.clientHeight
              
              if (containerWidth > 0 && containerHeight > 0) {
                iframe.style.width = containerWidth + 'px'
                iframe.style.height = containerHeight + 'px'
                console.log(`修复iframe[${index}]尺寸:`, {
                  width: containerWidth + 'px',
                  height: containerHeight + 'px'
                })
              } else {
                // 如果容器也没有尺寸，设置默认值
                iframe.style.width = '100%'
                iframe.style.height = '600px'
                console.log(`设置iframe[${index}]默认尺寸: 100% x 600px`)
              }
            }
          })
        }, 500) // 延迟500ms，确保WPS SDK已经创建iframe
      })
    },

    // 监听iframe的变化并修复尺寸
    observeIframeChanges() {
      const container = this.$refs.wpsContainer
      if (!container) {
        return
      }

      // 创建MutationObserver监听DOM变化
      const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
          if (mutation.type === 'childList') {
            // 检查是否有新的iframe被添加
            mutation.addedNodes.forEach((node) => {
              if (node.nodeName === 'IFRAME') {
                console.log('检测到新的iframe被添加，修复尺寸...')
                this.fixIframeSize()
              } else if (node.nodeType === 1 && node.querySelectorAll) {
                // 检查子元素中是否有iframe
                const iframes = node.querySelectorAll('iframe')
                if (iframes.length > 0) {
                  console.log(`检测到${iframes.length}个iframe，修复尺寸...`)
                  this.fixIframeSize()
                }
              }
            })
          } else if (mutation.type === 'attributes' && mutation.target.nodeName === 'IFRAME') {
            // 如果iframe的属性发生变化（如style），检查尺寸
            if (mutation.attributeName === 'style') {
              const iframe = mutation.target
              if (iframe.offsetWidth === 0 || iframe.offsetHeight === 0) {
                console.log('检测到iframe尺寸为0，修复尺寸...')
                this.fixIframeSize()
              }
            }
          }
        })
      })

      // 开始观察
      observer.observe(container, {
        childList: true,
        subtree: true,
        attributes: true,
        attributeFilter: ['style', 'width', 'height']
      })

      // 保存observer引用，以便在销毁时断开
      this._iframeObserver = observer
    },

    // 销毁编辑器
    destroyEditor() {
      console.log('开始销毁WPS编辑器...')
      
      // 断开iframe观察器
      if (this._iframeObserver) {
        try {
          this._iframeObserver.disconnect()
          this._iframeObserver = null
          console.log('✅ iframe观察器已断开')
        } catch (error) {
          console.warn('断开iframe观察器失败:', error)
        }
      }

      // 先清理SDK编辑器（在清理DOM之前）
      if (this.wpsEditor) {
        try {
          // 移除事件监听器（如果支持）
          if (typeof this.wpsEditor.off === 'function') {
            try {
              this.wpsEditor.off('ready')
              this.wpsEditor.off('save')
              this.wpsEditor.off('error')
              this.wpsEditor.off('fileOpen')
              this.wpsEditor.off('fileStatus')
              this.wpsEditor.off('fileLoadError')
              this.wpsEditor.off('fileLoadProgress')
              this.wpsEditor.off('loadComplete')
            } catch (e) {
              console.warn('移除事件监听器失败:', e)
            }
          }
          
          // 移除ApiEvent监听器（如果使用）
          if (this.wpsEditor.ApiEvent && typeof this.wpsEditor.ApiEvent.RemoveApiEventListener === 'function') {
            try {
              // 注意：这里无法移除匿名函数，但可以尝试清理
              console.log('尝试清理ApiEvent监听器...')
            } catch (e) {
              console.warn('清理ApiEvent监听器失败:', e)
            }
          }
          
          // WebOffice SDK的清理方法
          // 根据官方文档：instance.destroy() 会销毁iframe对应的DOM节点，从而销毁JSSDK实例，关闭WebOffice页面的展示
          // 注意：destroy()会自动处理DOM清理，我们不需要手动清理容器
          if (typeof this.wpsEditor.destroy === 'function') {
            try {
              // 设置一个标志，表示正在销毁
              this._isDestroying = true
              
              // 调用官方文档推荐的destroy()方法
              // 这会自动销毁iframe对应的DOM节点，销毁JSSDK实例，关闭WebOffice页面展示
              this.wpsEditor.destroy()
              
              console.log('✅ SDK destroy()调用成功，iframe DOM节点和JSSDK实例已销毁')
            } catch (error) {
              // 如果destroy失败（可能是跨域问题或DOM已被移除），记录警告但不影响销毁流程
              console.warn('⚠️ destroy()调用时发生错误（已忽略）:', error.message)
              console.warn('⚠️ 错误详情:', error)
              
              // 如果destroy失败，尝试使用close方法（如果存在）
              if (typeof this.wpsEditor.close === 'function') {
                try {
                  this.wpsEditor.close()
                  console.log('✅ SDK close()调用成功（降级方案）')
                } catch (closeError) {
                  console.warn('⚠️ close()也失败（已忽略）:', closeError.message)
                }
              }
            }
          } else if (typeof this.wpsEditor.close === 'function') {
            // 如果SDK没有destroy方法，尝试使用close方法
            try {
              this._isDestroying = true
              this.wpsEditor.close()
              console.log('✅ SDK close()调用成功（降级方案）')
            } catch (error) {
              console.warn('⚠️ close()失败（已忽略）:', error.message)
            }
          } else {
            console.warn('⚠️ SDK实例没有destroy()或close()方法，将手动清理DOM')
          }
        } catch (error) {
          // 捕获所有可能的错误，避免影响组件销毁
          console.warn('关闭WPS编辑器时发生错误（已忽略）:', error.message)
          console.warn('错误详情:', error)
        } finally {
          this.wpsEditor = null
          this._isDestroying = false
        }
      }

      // 根据官方文档，destroy()会自动销毁iframe对应的DOM节点
      // 但为了保险起见，如果destroy()失败或SDK没有destroy方法，我们延迟清理容器DOM
      setTimeout(() => {
        try {
          const container = this.$refs.wpsContainer
          if (container) {
            // 检查是否还有iframe残留（destroy()应该已经清理了）
            const iframes = container.querySelectorAll('iframe')
            if (iframes.length > 0) {
              console.log(`⚠️ 检测到${iframes.length}个残留的iframe，手动清理...`)
              
              // 手动清理残留的iframe
              iframes.forEach((iframe) => {
                try {
                  // 移除src属性，避免继续加载
                  iframe.src = 'about:blank'
                  // 移除iframe
                  if (iframe.parentNode) {
                    iframe.parentNode.removeChild(iframe)
                  }
                } catch (e) {
                  // 如果移除失败（可能是跨域问题），直接清空容器
                  console.warn('移除iframe失败，将清空容器:', e.message)
                }
              })
            }
            
            // 如果容器还有内容，清空它（destroy()应该已经清理了）
            if (container.children.length > 0 || container.innerHTML.trim() !== '') {
              console.log('⚠️ 容器仍有内容，手动清空...')
              container.innerHTML = ''
            } else {
              console.log('✅ 容器已由destroy()自动清理')
            }
          }
        } catch (error) {
          console.warn('清理容器时发生错误（已忽略）:', error.message)
        }
      }, 200) // 延迟200ms，给destroy()足够时间自动清理DOM
    },

  }
}
</script>

<style lang="scss" scoped>
.wps-editor-container {
  width: 100%;
  height: 100%;
  position: relative;
  background: rgba(10, 21, 32, 0.6);
  border-radius: 8px;
  overflow: hidden;

  .wps-loading,
  .wps-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: rgba(255, 255, 255, 0.9);
    font-size: 16px;

    i {
      font-size: 48px;
      margin-bottom: 16px;
      color: #409EFF;
    }

    span {
      margin-top: 8px;
    }
  }

  .wps-error {
    i {
      color: #F56C6C;
    }

    .el-button {
      margin-top: 12px;
      color: #409EFF;
      
      &:hover {
        color: #66b1ff;
      }
    }
  }

  .wps-editor-wrapper {
    width: 100%;
    height: 100%;
    min-height: 600px;
    position: relative;
    
    // 确保WPS SDK创建的iframe能够正确显示
    :deep(iframe) {
      width: 100% !important;
      height: 100% !important;
      min-width: 100% !important;
      min-height: 100% !important;
      border: none;
      display: block;
    }
    
    // 确保WPS SDK创建的容器能够正确显示
    :deep(.web-office-iframe) {
      width: 100% !important;
      height: 100% !important;
      min-width: 100% !important;
      min-height: 100% !important;
    }
  }
}
</style>

