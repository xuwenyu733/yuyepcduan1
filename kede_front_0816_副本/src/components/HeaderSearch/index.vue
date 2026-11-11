<template>
  <div :class="{'show':show}" class="header-search">
    <div class="search-icon-wrapper">
      <i class="el-icon-search search-icon" @click.stop="click"></i>
    </div>
    <el-select
      ref="headerSearchSelect"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="搜索养殖池、设备、数据..."
      class="header-search-select"
      @change="change"
      @focus="show = true"
    >
      <el-option
        v-for="(option, idx) in options"
        :key="(option.item && option.item.path ? option.item.path : 'opt') + '-' + idx"
        :value="option.item"
        :label="option.item.title.join(' > ')"
      />
    </el-select>
  </div>
</template>

<script>
// fuse is a lightweight fuzzy-search module
// make search results more in line with expectations
import Fuse from 'fuse.js/dist/fuse.min.js'
import path from 'path'

export default {
  name: 'HeaderSearch',
  data() {
    return {
      search: '',
      options: [],
      searchPool: [],
      show: false,
      fuse: undefined
    }
  },
  computed: {
    routes() {
      return this.$store.getters.permission_routes
    }
  },
  watch: {
    routes() {
      this.searchPool = this.generateRoutes(this.routes)
    },
    searchPool(list) {
      this.initFuse(list)
    },
    show(value) {
      if (value) {
        document.body.addEventListener('click', this.close)
      } else {
        document.body.removeEventListener('click', this.close)
      }
    }
  },
  mounted() {
    this.searchPool = this.generateRoutes(this.routes)
  },
  methods: {
    click() {
      this.show = !this.show
      if (this.show) {
        this.$refs.headerSearchSelect && this.$refs.headerSearchSelect.focus()
      }
    },
    close() {
      this.$refs.headerSearchSelect && this.$refs.headerSearchSelect.blur()
      this.options = []
      this.show = false
    },
    change(val) {
      if(this.ishttp(val.path)) {
        // http(s):// 路径新窗口打开
        window.open(val.path, "_blank");
      } else {
        this.$router.push(val.path)
      }
      this.search = ''
      this.options = []
      this.$nextTick(() => {
        this.show = false
      })
    },
    initFuse(list) {
      this.fuse = new Fuse(list, {
        shouldSort: true,
        threshold: 0.4,
        location: 0,
        distance: 100,
        minMatchCharLength: 1,
        keys: [{
          name: 'title',
          weight: 0.7
        }, {
          name: 'path',
          weight: 0.3
        }]
      })
    },
    // Filter out the routes that can be displayed in the sidebar
    // And generate the internationalized title
    generateRoutes(routes, basePath = '/', prefixTitle = [], seen = null) {
      let res = []
      const pathSeen = seen || new Set()

      for (const router of routes) {
        const data = {
          path: !this.ishttp(router.path) ? path.resolve(basePath, router.path) : router.path,
          title: [...prefixTitle]
        }

        if (router.meta && router.meta.title) {
          data.title = [...data.title, router.meta.title]
          // 默认跳过 hidden；标记 meta.searchable 的隐藏页仍可被顶部搜索找到（如车间数控）
          const allowSearch = !router.hidden || router.meta.searchable === true
          if (allowSearch && router.redirect !== 'noRedirect' && !pathSeen.has(data.path)) {
            pathSeen.add(data.path)
            res.push(data)
          }
        }

        // 即使父级 hidden（如 /keyshow），也继续遍历子路由，否则业务页全部搜不到
        if (router.children) {
          const tempRoutes = this.generateRoutes(router.children, data.path, data.title, pathSeen)
          if (tempRoutes.length >= 1) {
            res = [...res, ...tempRoutes]
          }
        }
      }
      return res
    },
    querySearch(query) {
      if (query !== '') {
        this.options = this.fuse.search(query)
      } else {
        this.options = []
      }
    },
    ishttp(url) {
      return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1
    }
  }
}
</script>

<style lang="scss" scoped>
.header-search {
  display: flex;
  align-items: center;
  width: 100%;
  height: 36px;
  position: relative;

  .search-icon-wrapper {
    position: absolute;
    left: 12px;
    z-index: 10;
    display: flex;
    align-items: center;
    justify-content: center;
    pointer-events: none;
    
    .search-icon {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.6);
      transition: color 0.3s;
      pointer-events: auto;
      cursor: pointer;
      
      &:hover {
        color: #409EFF;
      }
    }
  }

  .header-search-select {
    width: 100%;
    font-size: 14px;
    transition: all 0.3s;
    display: inline-block;
    vertical-align: middle;

    :deep(.el-input__inner) {
      height: 36px;
      line-height: 36px;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 6px;
      padding-left: 40px;
      padding-right: 12px;
      box-shadow: none !important;
      color: #ffffff;
      font-size: 14px;
      vertical-align: middle;
      
      &::placeholder {
        color: rgba(255, 255, 255, 0.5);
      }
      
      &:focus {
        border-color: rgba(64, 158, 255, 0.5);
        background: rgba(255, 255, 255, 0.08);
      }
    }
    
    :deep(.el-input__prefix) {
      display: none;
    }
    
    :deep(.el-input__suffix) {
      right: 12px;
    }
  }

  &.show {
    .header-search-select {
      width: 100%;
    }
  }
}
</style>
