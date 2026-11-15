'use strict'
const path = require('path')
const defaultSettings = require('./src/settings.js')

function resolve(dir) {
  return path.join(__dirname, dir)
}

const CompressionPlugin = require('compression-webpack-plugin')
const TerserPlugin = require('terser-webpack-plugin')

const name = process.env.VUE_APP_TITLE || '养殖智慧平台' // 网页标题

const port = process.env.port || process.env.npm_config_port || 4984 // 端口

module.exports = {
  parallel: process.env.YUYE_BUILD_WORKERS !== '1',
  publicPath: process.env.PUBLIC_PATH ? process.env.PUBLIC_PATH : '/',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: process.env.NODE_ENV === 'development',
  // 极致优化 1：彻底禁用生产环境 SourceMap，显著减少体积
  productionSourceMap: false,
  devServer: {
    host: '0.0.0.0',
    port: port,
    open: true,
    // 固定 HMR/sockjs 走本机，避免曾用过期局域网 IP 打开后一直 ERR_CONNECTION_TIMED_OUT
    public: 'localhost:' + port,
    sockHost: 'localhost',
    sockPort: port,
    proxy: {
      ['/proxy-api']: {
        // 本地后端实际端口 48181（旧默认 48080 会导致代理打空）
        target: `http://localhost:48181`,
        // target: 'http://192.168.5.61:48181',
        changeOrigin: true,
        pathRewrite: {
          '^/proxy-api': ''
        }
      },
      // 方案 B：三模块统一 BFF（鱼病/风味/阈值）
      '/modules-api': {
        target: 'http://localhost:3020',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/modules-api': ''
        }
      },
      // AI 小科 DeepSeek /llm（Vite llm-proxy，端口 5190）
      '/llm': {
        target: 'http://127.0.0.1:5190',
        changeOrigin: true,
      },
      // 3D 数字孪生数据发生器（可选；未启动时孪生页自动本地模拟）
      '/api': {
        target: 'http://127.0.0.1:8787',
        changeOrigin: true
      },
      // 仅 /wiidu-datainf 使用 VUE_APP_PMIS_PROXY_TARGET（值为 .../zx/datainf）；勿与下方 Tomcat 根代理共用
      '/wiidu-datainf': {
        target: process.env.VUE_APP_PMIS_PROXY_TARGET || 'http://127.0.0.1:8088/zx/datainf',
        changeOrigin: true,
        secure: false,
        timeout: 60000,
        proxyTimeout: 60000,
        pathRewrite: {
          '^/wiidu-datainf': ''
        }
      },
      // 以下走 Tomcat 根；可用 VUE_APP_PMIS_ORIGIN（默认 :8088），勿复用 VUE_APP_PMIS_PROXY_TARGET
      'PMIS': {
        target: process.env.VUE_APP_PMIS_ORIGIN || 'http://127.0.0.1:8088',
        changeOrigin: true,
        pathRewrite: {
          ['^/wiidu']: ''
        }

      },
      'list': {
        target: (process.env.VUE_APP_PMIS_ORIGIN || 'http://127.0.0.1:8088') + '/zx',
        changeOrigin: true,
        pathRewrite: {
          ['^/problems']: ''
        }

      },
      'zx': {
        target: process.env.VUE_APP_PMIS_ORIGIN || 'http://127.0.0.1:8088',
        changeOrigin: true,
        pathRewrite: {
          ['^/wiidu']: ''
        }

      }
    },
    disableHostCheck: true,
    overlay: {
      warnings: false,
      errors: true
    }
  },
  css: {
    loaderOptions: {
      sass: {
        sassOptions: { outputStyle: "expanded" }
      }
    }
  },
  configureWebpack: {
    name: name,
    resolve: {
      alias: {
        '@': resolve('src')
      }
    },
    plugins: [
      // 极致优化 2：启用 Gzip 压缩（针对 JS, CSS, HTML, SVG）
      new CompressionPlugin({
        test: /\.(js|css|html|svg)?$/i,
        filename: '[path].gz[query]',
        algorithm: 'gzip',
        threshold: 10240,
        minRatio: 0.8,
        deleteOriginalAssets: false
      })
    ],
    optimization: {
      // 极致优化 3：启用 Terser 混淆加密，移除 Console 和 注释
      minimize: process.env.NODE_ENV === 'production',
      minimizer: [
        new TerserPlugin({
          terserOptions: {
            compress: {
              drop_console: true,
              drop_debugger: true,
              pure_funcs: ['console.log']
            },
            format: {
              comments: false
            }
          },
          extractComments: false,
          parallel: process.env.YUYE_BUILD_WORKERS !== '1'
        })
      ],
    //极致优化 4：重新开启代码分割（禁止使用 splitChunks: false）
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        libs: {
          name: 'chunk-libs',
          test: /[\\/]node_modules[\\/]/,
          priority: 10,
          chunks: 'initial'
        },
        elementUI: {
          name: 'chunk-elementUI',
          priority: 20,
          test: /[\\/]node_modules[\\/]_?element-ui(.*)/
        },
        echarts: {
          name: 'chunk-echarts',
          priority: 20,
          test: /[\\/]node_modules[\\/]_?echarts(.*)/
        },
        commons: {
          name: 'chunk-commons',
          test: resolve('src/components'),
          minChunks: 3,
          priority: 5,
          reuseExistingChunk: true
        }
      }
    }
    }
  },
  chainWebpack(config) {
    if (process.env.YUYE_LOCAL_PREVIEW === '1') config.plugins.delete('optimize-css')
    config.plugins.delete('preload')
    config.plugins.delete('prefetch')

    // 极致优化 5：小图片转 Base64 (10kb 阈值)
    config.module
      .rule('images')
      .use('url-loader')
      .loader('url-loader')
      .tap(options => Object.assign(options, { limit: 10240 }))

    // SVG 配置保持
    config.module
      .rule('svg')
      .exclude.add(resolve('src/assets/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/assets/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()

    config.when(process.env.NODE_ENV !== 'development', config => {
      config
        .plugin('ScriptExtHtmlWebpackPlugin')
        .after('html')
        .use('script-ext-html-webpack-plugin', [{
          inline: /runtime\..*\.js$/
        }])
        .end()

      // 极致优化 6：提取运行时代码
      config.optimization.runtimeChunk('single')
    })
  }
}
