<template>
  <div class="attendance-setting-container">
    <div class="header-section">
      <h2>考勤设置</h2>
      <p class="subtitle">设置允许打卡的地址和范围</p>
    </div>

    <div class="content-section">
      <!-- 操作栏 -->
      <div class="action-bar">
        <el-button type="primary" size="small" class="add-btn" icon="el-icon-plus" @click="handleAdd">新增地点</el-button>
        <el-input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索地点名称"
          prefix-icon="el-icon-search"
          clearable
          @input="handleSearch">
        </el-input>
      </div>

      <!-- 列表表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        class="attendance-table">
        <el-table-column prop="name" label="地点名称" min-width="140" align="center"></el-table-column>
        <el-table-column prop="address" label="详细地址" min-width="180" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="经纬度" min-width="170" align="center">
          <template slot-scope="scope">
            <span class="coord-text">{{ scope.row.longitude }}, {{ scope.row.latitude }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="allowRange" label="允许范围（米）" min-width="120" align="center"></el-table-column>
        <el-table-column prop="workStartTime" label="上班时间" min-width="110" align="center">
          <template slot-scope="scope">
            {{ formatTime(scope.row.workStartTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="workEndTime" label="下班时间" min-width="110" align="center">
          <template slot-scope="scope">
            {{ formatTime(scope.row.workEndTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="90" align="center">
          <template slot-scope="scope">
            <span :class="['status-pill', scope.row.status === 1 ? 'is-on' : 'is-off']">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" class="op-edit" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" class="op-delete" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageParams.pageNo"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="900px"
      :close-on-click-modal="false"
      @close="handleDialogClose">
      <el-form
        ref="form"
        :model="formData"
        :rules="formRules"
        label-width="120px">
        <el-form-item label="地点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入地点名称" maxlength="255"></el-input>
        </el-form-item>
        <el-form-item label="选择地点" prop="location">
          <div class="map-selector">
            <el-button type="primary" icon="el-icon-location" @click="openMapPicker">在地图上选择地点</el-button>
            <div v-if="formData.address" class="location-info">
              <p><strong>地址：</strong>{{ formData.address }}</p>
              <p><strong>经纬度：</strong>{{ formData.longitude }}, {{ formData.latitude }}</p>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
            maxlength="500">
          </el-input>
        </el-form-item>
        <el-form-item label="允许打卡范围" prop="allowRange">
          <el-input-number
            v-model="formData.allowRange"
            :min="10"
            :max="5000"
            :step="10"
            style="width: 200px;">
          </el-input-number>
          <span style="margin-left: 10px; color: #909399;">米</span>
        </el-form-item>
        <el-form-item label="上班时间" prop="workStartTime">
          <el-time-picker
            v-model="formData.workStartTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择上班时间"
            style="width: 200px;">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="下班时间" prop="workEndTime">
          <el-time-picker
            v-model="formData.workEndTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择下班时间"
            style="width: 200px;">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 地图选择器对话框 -->
    <el-dialog
      title="选择地点"
      :visible.sync="mapDialogVisible"
      width="900px"
      :close-on-click-modal="false"
      @close="handleMapDialogClose">
      <div class="map-picker-container">
        <!-- 搜索框 -->
        <div class="search-section">
          <el-input
            v-model="mapSearchKeyword"
            placeholder="搜索地点名称，如：北京天安门、中关村"
            prefix-icon="el-icon-search"
            clearable
            @input="handleSearchInput"
            @keyup.enter.native="searchPlace">
            <el-button slot="append" icon="el-icon-search" @click="searchPlace">搜索</el-button>
          </el-input>
        </div>
        
        <!-- 地图和搜索结果 -->
        <div class="map-content-wrapper">
          <!-- 地图容器 -->
          <div id="mapContainer" class="map-container"></div>
          
          <!-- 搜索结果列表 -->
          <div v-if="searchResults.length > 0" class="search-results">
            <div class="search-results-header">
              <span>搜索结果（{{ searchResults.length }}）</span>
            </div>
            <div class="search-results-list">
              <div
                v-for="(item, index) in searchResults"
                :key="index"
                class="search-result-item"
                @click="selectSearchResult(item)">
                <div class="result-name">{{ item.name }}</div>
                <div class="result-address">{{ item.address }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="mapDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmLocation">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getOaAttendanceSettingPage, 
  createOaAttendanceSetting, 
  updateOaAttendanceSetting, 
  deleteOaAttendanceSetting 
} from '@/api/fish/oaAttendanceSetting'

export default {
  name: 'AttendanceSetting',
  data() {
    return {
      loading: false,
      submitting: false,
      searchKeyword: '',
      tableData: [],
      total: 0,
      pageParams: {
        pageNo: 1,
        pageSize: 10
      },
      dialogVisible: false,
      dialogTitle: '新增地点',
      formData: {
        id: null,
        name: '',
        address: '',
        longitude: null,
        latitude: null,
        allowRange: 100,
        workStartTime: '09:30:00',
        workEndTime: '18:00:00',
        status: 1
      },
      formRules: {
        name: [
          { required: true, message: '请输入地点名称', trigger: 'blur' }
        ],
        location: [
          { required: true, message: '请选择地点', trigger: 'change', validator: this.validateLocation }
        ],
        allowRange: [
          { required: true, message: '请输入允许打卡范围', trigger: 'blur' }
        ]
      },
      mapDialogVisible: false,
      map: null,
      marker: null,
      placeSearch: null,
      mapSearchKeyword: '',
      searchResults: [],
      selectedLocation: {
        lng: null,
        lat: null,
        address: '',
        name: ''
      }
    }
  },
  mounted() {
    this.loadData()
    // 加载高德地图
    this.loadAMapScript()
  },
  methods: {
    // 加载高德地图脚本
    loadAMapScript() {
      if (window.AMap) {
        return Promise.resolve()
      }
      return new Promise((resolve, reject) => {
        // 配置安全密钥（如果API Key配置了安全密钥，需要在这里设置）
        // 如果API Key没有配置安全密钥，可以注释掉这部分
        const amapSecurityCode = process.env.VUE_APP_AMAP_SECURITY_CODE
        if (amapSecurityCode) {
          window._AMapSecurityConfig = {
            securityJsCode: amapSecurityCode
          }
        }
        
        const script = document.createElement('script')
        script.type = 'text/javascript'
        script.async = true
        // 从环境变量或配置中获取高德地图key，如果没有配置则使用默认key（需要替换）
        const amapKey = process.env.VUE_APP_AMAP_KEY || 'ef33d29f97d2fb2602def95c2b62560f'
        script.src = `https://webapi.amap.com/maps?v=2.0&key=${amapKey}&callback=initAMap`
        script.onerror = reject
        window.initAMap = () => {
          resolve()
        }
        document.head.appendChild(script)
      })
    },
    
    // 加载数据
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNo: this.pageParams.pageNo,
          pageSize: this.pageParams.pageSize,
          name: this.searchKeyword || undefined
        }
        const res = await getOaAttendanceSettingPage(params)
        if (res.code === 200 || res.code === 0) {
          const list = res.data.list || []
          // 确保时间字段格式正确
          this.tableData = list.map(item => {
            // 处理数组格式的时间 [小时, 分钟, 秒]
            if (item.workStartTime) {
              if (Array.isArray(item.workStartTime)) {
                // 数组格式：[小时, 分钟, 秒（可选）]
                const hours = String(item.workStartTime[0] || 0).padStart(2, '0')
                const minutes = String(item.workStartTime[1] || 0).padStart(2, '0')
                const seconds = String(item.workStartTime[2] || 0).padStart(2, '0')
                item.workStartTime = `${hours}:${minutes}:${seconds}`
              } else if (typeof item.workStartTime === 'object') {
                item.workStartTime = this.convertTimeObjectToString(item.workStartTime)
              } else if (typeof item.workStartTime === 'string' && !item.workStartTime.includes(':')) {
                item.workStartTime = this.formatTime(item.workStartTime)
              }
            }
            if (item.workEndTime) {
              if (Array.isArray(item.workEndTime)) {
                // 数组格式：[小时, 分钟, 秒（可选）]
                const hours = String(item.workEndTime[0] || 0).padStart(2, '0')
                const minutes = String(item.workEndTime[1] || 0).padStart(2, '0')
                const seconds = String(item.workEndTime[2] || 0).padStart(2, '0')
                item.workEndTime = `${hours}:${minutes}:${seconds}`
              } else if (typeof item.workEndTime === 'object') {
                item.workEndTime = this.convertTimeObjectToString(item.workEndTime)
              } else if (typeof item.workEndTime === 'string' && !item.workEndTime.includes(':')) {
                item.workEndTime = this.formatTime(item.workEndTime)
              }
            }
            return item
          })
          this.total = res.data.total || 0
        } else {
          this.$message.error(res.msg || '获取数据失败')
        }
      } catch (error) {
        this.$message.error('获取数据失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },
    
    // 搜索
    handleSearch() {
      this.pageParams.pageNo = 1
      this.loadData()
    },
    
    // 分页大小改变
    handleSizeChange(val) {
      this.pageParams.pageSize = val
      this.pageParams.pageNo = 1
      this.loadData()
    },
    
    // 当前页改变
    handleCurrentChange(val) {
      this.pageParams.pageNo = val
      this.loadData()
    },
    
    // 新增
    handleAdd() {
      this.dialogTitle = '新增地点'
      this.formData = {
        id: null,
        name: '',
        address: '',
        longitude: null,
        latitude: null,
        allowRange: 100,
        workStartTime: '09:30:00',
        workEndTime: '18:00:00',
        status: 1
      }
      this.dialogVisible = true
    },
    
    // 编辑
    handleEdit(row) {
      this.dialogTitle = '编辑地点'
      // 处理时间字段，如果是数组格式则转换
      let workStartTime = row.workStartTime
      if (Array.isArray(workStartTime)) {
        // 数组格式：[小时, 分钟, 秒（可选）]
        const hours = String(workStartTime[0] || 9).padStart(2, '0')
        const minutes = String(workStartTime[1] || 30).padStart(2, '0')
        const seconds = String(workStartTime[2] || 0).padStart(2, '0')
        workStartTime = `${hours}:${minutes}:${seconds}`
      } else if (!workStartTime) {
        workStartTime = '09:30:00'
      }
      
      let workEndTime = row.workEndTime
      if (Array.isArray(workEndTime)) {
        // 数组格式：[小时, 分钟, 秒（可选）]
        const hours = String(workEndTime[0] || 18).padStart(2, '0')
        const minutes = String(workEndTime[1] || 0).padStart(2, '0')
        const seconds = String(workEndTime[2] || 0).padStart(2, '0')
        workEndTime = `${hours}:${minutes}:${seconds}`
      } else if (!workEndTime) {
        workEndTime = '18:00:00'
      }
      
      this.formData = {
        id: row.id,
        name: row.name,
        address: row.address || '',
        longitude: row.longitude,
        latitude: row.latitude,
        allowRange: row.allowRange,
        workStartTime: workStartTime,
        workEndTime: workEndTime,
        status: row.status
      }
      this.dialogVisible = true
    },
    
    // 删除
    handleDelete(row) {
      this.$confirm('确定要删除该地点吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteOaAttendanceSetting(row.id)
          if (res.code === 200 || res.code === 0) {
            this.$message.success('删除成功')
            this.loadData()
          } else {
            this.$message.error(res.msg || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败: ' + (error.message || '未知错误'))
        }
      }).catch(() => {})
    },
    
    // 打开地图选择器
    async openMapPicker() {
      this.mapDialogVisible = true
      this.mapSearchKeyword = ''
      this.searchResults = []
      await this.$nextTick()
      await this.initMap()
    },
    
    // 初始化地图
    async initMap() {
      try {
        await this.loadAMapScript()
        
        // 如果已有地图实例，销毁
        if (this.map) {
          this.map.destroy()
        }
        
        // 创建地图实例
        this.map = new AMap.Map('mapContainer', {
          zoom: 13,
          center: this.formData.longitude && this.formData.latitude 
            ? [this.formData.longitude, this.formData.latitude]
            : [116.397428, 39.90923] // 默认北京
        })
        
        // 加载地点搜索插件
        AMap.plugin('AMap.PlaceSearch', () => {
          try {
            // 初始化地点搜索服务
            this.placeSearch = new AMap.PlaceSearch({
              map: this.map,
              pageSize: 10,
              pageIndex: 1,
              city: '全国' // 设置搜索范围为全国
            })
            console.log('PlaceSearch 插件加载成功')
          } catch (error) {
            console.error('PlaceSearch 初始化失败:', error)
            this.$message.error('搜索服务初始化失败')
          }
        })
        
        // 如果有已有位置，添加标记
        if (this.formData.longitude && this.formData.latitude) {
          this.marker = new AMap.Marker({
            position: [this.formData.longitude, this.formData.latitude],
            map: this.map
          })
          this.map.setCenter([this.formData.longitude, this.formData.latitude])
        }
        
        // 地图点击事件
        this.map.on('click', (e) => {
          const lng = e.lnglat.getLng()
          const lat = e.lnglat.getLat()
          
          // 移除旧标记
          if (this.marker) {
            this.map.remove(this.marker)
          }
          
          // 添加新标记
          this.marker = new AMap.Marker({
            position: [lng, lat],
            map: this.map
          })
          
          // 保存选中的位置
          this.selectedLocation = {
            lng: lng,
            lat: lat,
            address: '',
            name: ''
          }
          
          // 逆地理编码获取地址和地点名称
          this.reverseGeocode(lng, lat)
        })
        
        // 如果已有位置，进行逆地理编码
        if (this.formData.longitude && this.formData.latitude) {
          this.selectedLocation = {
            lng: this.formData.longitude,
            lat: this.formData.latitude,
            address: this.formData.address || '',
            name: this.formData.name || ''
          }
        }
      } catch (error) {
        console.error('初始化地图失败:', error)
        this.$message.error('地图加载失败，请检查网络连接或高德地图配置')
      }
    },
    
    // 搜索输入处理
    handleSearchInput(value) {
      if (!value || value.trim() === '') {
        this.searchResults = []
      }
    },
    
    // 搜索地点
    searchPlace() {
      const keyword = this.mapSearchKeyword.trim()
      if (!keyword) {
        this.$message.warning('请输入搜索关键词')
        return
      }
      
      // 如果插件未加载，先加载插件
      if (!this.placeSearch) {
        AMap.plugin('AMap.PlaceSearch', () => {
          try {
            this.placeSearch = new AMap.PlaceSearch({
              map: this.map,
              pageSize: 10,
              pageIndex: 1,
              city: '全国' // 设置搜索范围为全国
            })
            // 插件加载完成后执行搜索
            this.executeSearch(keyword)
          } catch (error) {
            console.error('PlaceSearch 初始化失败:', error)
            this.$message.error('搜索服务初始化失败')
          }
        })
      } else {
        // 直接执行搜索
        this.executeSearch(keyword)
      }
    },
    
    // 执行搜索
    executeSearch(keyword) {
      if (!this.placeSearch) {
        this.$message.warning('搜索服务未初始化，请稍后再试')
        return
      }
      
      // 执行搜索
      this.placeSearch.search(keyword, (status, result) => {
        console.log('搜索状态:', status)
        console.log('搜索结果:', result)
        
        if (status === 'complete') {
          // 检查结果信息
          if (result && result.info === 'OK' && result.poiList && result.poiList.pois) {
            // 搜索成功
            this.searchResults = result.poiList.pois.map(poi => ({
              name: poi.name || '',
              address: poi.address || '',
              lng: poi.location.lng,
              lat: poi.location.lat,
              fullAddress: (poi.district || '') + (poi.address || '') + (poi.name || '')
            }))
            
            if (this.searchResults.length === 0) {
              this.$message.info('未找到相关地点')
            }
          } else if (result && result.info === 'NO_DATA') {
            // 无数据
            this.$message.info('未找到相关地点')
            this.searchResults = []
          } else {
            // 其他错误
            let errorMsg = '搜索失败，请稍后再试'
            if (result && result.info) {
              if (result.info === 'INVALID_USER_SCODE') {
                errorMsg = 'API Key安全密钥配置错误，请检查环境变量 VUE_APP_AMAP_SECURITY_CODE 或在高德开放平台取消安全密钥配置'
              } else {
                errorMsg = `搜索失败: ${result.info}`
              }
            }
            console.error('搜索错误:', result)
            this.$message.error(errorMsg)
            this.searchResults = []
          }
        } else if (status === 'error') {
          // 请求错误
          let errorMsg = '搜索请求失败，请检查网络连接'
          if (result === 'INVALID_USER_SCODE') {
            errorMsg = 'API Key安全密钥配置错误，请检查环境变量 VUE_APP_AMAP_SECURITY_CODE 或在高德开放平台取消安全密钥配置'
          }
          console.error('搜索请求错误:', result)
          this.$message.error(errorMsg)
          this.searchResults = []
        } else {
          // 其他状态
          console.warn('搜索状态未知:', status, result)
          this.$message.warning('搜索状态异常，请稍后再试')
          this.searchResults = []
        }
      })
    },
    
    // 选择搜索结果
    selectSearchResult(item) {
      // 移除旧标记
      if (this.marker) {
        this.map.remove(this.marker)
      }
      
      // 添加新标记
      this.marker = new AMap.Marker({
        position: [item.lng, item.lat],
        map: this.map
      })
      
      // 设置地图中心
      this.map.setCenter([item.lng, item.lat])
      this.map.setZoom(16)
      
      // 保存选中的位置
      this.selectedLocation = {
        lng: item.lng,
        lat: item.lat,
        address: item.fullAddress || item.address,
        name: item.name || ''
      }
      
      // 清空搜索结果
      this.searchResults = []
      this.mapSearchKeyword = ''
    },
    
    // 逆地理编码
    reverseGeocode(lng, lat) {
      if (!window.AMap) {
        return
      }
      AMap.plugin('AMap.Geocoder', () => {
        const geocoder = new AMap.Geocoder()
        geocoder.getAddress([lng, lat], (status, result) => {
          if (status === 'complete' && result.info === 'OK') {
            const address = result.regeocode.formattedAddress
            this.selectedLocation.address = address
            
            // 尝试获取地点名称（POI名称）
            const pois = result.regeocode.pois
            if (pois && pois.length > 0) {
              // 使用最近的POI名称作为地点名称
              this.selectedLocation.name = pois[0].name || ''
            } else {
              // 如果没有POI，使用地址中的关键信息作为名称
              const addressComponent = result.regeocode.addressComponent
              if (addressComponent) {
                // 组合省市区和街道作为地点名称
                const nameParts = []
                if (addressComponent.province) nameParts.push(addressComponent.province)
                if (addressComponent.city) nameParts.push(addressComponent.city)
                if (addressComponent.district) nameParts.push(addressComponent.district)
                if (addressComponent.street) nameParts.push(addressComponent.street)
                this.selectedLocation.name = nameParts.join('') || address.split('').slice(0, 20).join('')
              }
            }
          }
        })
      })
    },
    
    // 确认选择位置
    confirmLocation() {
      if (!this.selectedLocation.lng || !this.selectedLocation.lat) {
        this.$message.warning('请在地图上点击选择地点')
        return
      }
      
      // 保存经纬度
      this.formData.longitude = this.selectedLocation.lng
      this.formData.latitude = this.selectedLocation.lat
      
      // 保存地址
      if (this.selectedLocation.address) {
        this.formData.address = this.selectedLocation.address
      }
      
      // 回显地点名称（如果地点名称为空或用户未手动输入，则使用选点的名称）
      if (this.selectedLocation.name && (!this.formData.name || this.formData.name.trim() === '')) {
        this.formData.name = this.selectedLocation.name
      }
      
      // 提示用户选点成功
      this.$message.success('地点选择成功，已自动填充地点名称和地址')
      
      this.mapDialogVisible = false
      this.$refs.form.validateField('location')
    },
    
    // 地图对话框关闭
    handleMapDialogClose() {
      if (this.map) {
        this.map.destroy()
        this.map = null
      }
      this.marker = null
      this.placeSearch = null
      this.mapSearchKeyword = ''
      this.searchResults = []
      this.selectedLocation = {
        lng: null,
        lat: null,
        address: '',
        name: ''
      }
    },
    
    // 验证位置
    validateLocation(rule, value, callback) {
      if (!this.formData.longitude || !this.formData.latitude) {
        callback(new Error('请在地图上选择地点'))
      } else {
        callback()
      }
    },
    
    // 提交表单
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        this.submitting = true
        try {
          const data = {
            name: this.formData.name,
            address: this.formData.address,
            longitude: this.formData.longitude,
            latitude: this.formData.latitude,
            allowRange: this.formData.allowRange,
            workStartTime: this.formData.workStartTime,
            workEndTime: this.formData.workEndTime,
            status: this.formData.status
          }
          
          let res
          if (this.formData.id) {
            data.id = this.formData.id
            res = await updateOaAttendanceSetting(data)
          } else {
            res = await createOaAttendanceSetting(data)
          }
          
          if (res.code === 200 || res.code === 0) {
            this.$message.success(this.formData.id ? '更新成功' : '创建成功')
            this.dialogVisible = false
            this.loadData()
          } else {
            this.$message.error(res.msg || '操作失败')
          }
        } catch (error) {
          this.$message.error('操作失败: ' + (error.message || '未知错误'))
        } finally {
          this.submitting = false
        }
      })
    },
    
    // 对话框关闭
    handleDialogClose() {
      this.$refs.form.resetFields()
      this.formData = {
        id: null,
        name: '',
        address: '',
        longitude: null,
        latitude: null,
        allowRange: 100,
        workStartTime: '09:30:00',
        workEndTime: '18:00:00',
        status: 1
      }
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '--'
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    
    // 格式化时间（时分秒）
    formatTime(timeStr) {
      if (!timeStr && timeStr !== 0) return '--'
      
      // 如果是数组格式 [小时, 分钟, 秒]
      if (Array.isArray(timeStr)) {
        const hours = String(timeStr[0] || 0).padStart(2, '0')
        const minutes = String(timeStr[1] || 0).padStart(2, '0')
        const seconds = String(timeStr[2] || 0).padStart(2, '0')
        return `${hours}:${minutes}:${seconds}`
      }
      
      // 如果是字符串格式
      if (typeof timeStr === 'string') {
        // 如果已经是 "HH:mm:ss" 或 "HH:mm" 格式，直接返回
        if (/^\d{2}:\d{2}(:\d{2})?$/.test(timeStr)) {
          // 如果是 "HH:mm" 格式，补全秒数
          if (timeStr.length === 5) {
            return timeStr + ':00'
          }
          return timeStr
        }
        
        // 尝试解析其他格式
        // 处理类似 "9,30" 或 "18,0" 的格式（可能是 LocalTime 序列化后的格式）
        if (timeStr.includes(',')) {
          const commaMatch = timeStr.match(/(\d+),(\d+)(?:,(\d+))?/)
          if (commaMatch) {
            const hours = String(commaMatch[1]).padStart(2, '0')
            const minutes = String(commaMatch[2] || 0).padStart(2, '0')
            const seconds = String(commaMatch[3] || 0).padStart(2, '0')
            return `${hours}:${minutes}:${seconds}`
          }
        }
        
        // 处理其他可能的格式
        return timeStr
      }
      
      // 如果是Date对象
      if (timeStr instanceof Date) {
        const hours = String(timeStr.getHours()).padStart(2, '0')
        const minutes = String(timeStr.getMinutes()).padStart(2, '0')
        const seconds = String(timeStr.getSeconds()).padStart(2, '0')
        return `${hours}:${minutes}:${seconds}`
      }
      
      // 如果是对象，尝试提取时分秒（LocalTime 可能被序列化为对象）
      if (typeof timeStr === 'object' && timeStr !== null) {
        // 尝试多种可能的属性名
        let hours = timeStr.hour !== undefined ? timeStr.hour : 
                   (timeStr.hours !== undefined ? timeStr.hours : 
                   (timeStr.H !== undefined ? timeStr.H : null))
        let minutes = timeStr.minute !== undefined ? timeStr.minute : 
                     (timeStr.minutes !== undefined ? timeStr.minutes : 
                     (timeStr.m !== undefined ? timeStr.m : 
                     (timeStr.M !== undefined ? timeStr.M : null)))
        let seconds = timeStr.second !== undefined ? timeStr.second : 
                     (timeStr.seconds !== undefined ? timeStr.seconds : 
                     (timeStr.s !== undefined ? timeStr.s : 
                     (timeStr.S !== undefined ? timeStr.S : 0)))
        
        if (hours !== null && minutes !== null) {
          hours = String(hours).padStart(2, '0')
          minutes = String(minutes).padStart(2, '0')
          seconds = String(seconds || 0).padStart(2, '0')
          return `${hours}:${minutes}:${seconds}`
        }
      }
      
      // 如果是数字（可能是时间戳或其他格式）
      if (typeof timeStr === 'number') {
        // 如果是秒数（0-86399），转换为时分秒
        if (timeStr >= 0 && timeStr < 86400) {
          const hours = Math.floor(timeStr / 3600)
          const minutes = Math.floor((timeStr % 3600) / 60)
          const seconds = timeStr % 60
          return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
        }
      }
      
      // 如果都不匹配，尝试转换为字符串
      const str = String(timeStr)
      // 如果转换后的字符串看起来像时间格式，返回它
      if (/^\d{1,2}[:,\s]\d{1,2}([:,\s]\d{1,2})?$/.test(str)) {
        return str
      }
      
      return '--'
    },
    
    // 将时间对象转换为字符串格式
    convertTimeObjectToString(timeObj) {
      if (!timeObj || typeof timeObj !== 'object') {
        return timeObj
      }
      
      // 尝试多种可能的属性名
      let hours = timeObj.hour !== undefined ? timeObj.hour : 
                 (timeObj.hours !== undefined ? timeObj.hours : 
                 (timeObj.H !== undefined ? timeObj.H : 0))
      let minutes = timeObj.minute !== undefined ? timeObj.minute : 
                   (timeObj.minutes !== undefined ? timeObj.minutes : 
                   (timeObj.m !== undefined ? timeObj.m : 
                   (timeObj.M !== undefined ? timeObj.M : 0)))
      let seconds = timeObj.second !== undefined ? timeObj.second : 
                   (timeObj.seconds !== undefined ? timeObj.seconds : 
                   (timeObj.s !== undefined ? timeObj.s : 
                   (timeObj.S !== undefined ? timeObj.S : 0)))
      
      return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    }
  }
}
</script>

<style lang="scss" scoped>
$bg-deep: #0a1520;
$bg-panel: #0f1a26;
$teal: #54907a;
$teal-dark: #2f6b5a;
$teal-border: rgba(84, 144, 122, 0.35);
$text: rgba(255, 255, 255, 0.92);
$text-muted: rgba(255, 255, 255, 0.62);

.attendance-setting-container {
  padding: 20px;
  background: $bg-deep;
  min-height: calc(100vh - 84px);

  .header-section {
    background: linear-gradient(180deg, #122033 0%, $bg-panel 100%);
    padding: 18px 22px;
    border-radius: 8px;
    margin-bottom: 16px;
    border: 1px solid $teal-border;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);

    h2 {
      margin: 0 0 6px 0;
      font-size: 20px;
      font-weight: 600;
      color: $text;
      line-height: 1.4;
    }

    .subtitle {
      margin: 0;
      font-size: 13px;
      color: $text-muted;
      line-height: 1.5;
    }
  }

  .content-section {
    background: linear-gradient(180deg, #122033 0%, $bg-panel 100%);
    padding: 18px 20px 16px;
    border-radius: 8px;
    border: 1px solid $teal-border;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);

    .action-bar {
      margin-bottom: 14px;
      padding-bottom: 14px;
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
      /* 避免全局/Element 浅色底边在深色主题下显成「白边」 */
      border: none !important;
      border-bottom: 1px solid rgba(84, 144, 122, 0.22) !important;
      box-shadow: none !important;
      outline: none;

      .add-btn {
        margin: 0;
        height: 32px !important;
        padding: 0 14px !important;
        font-size: 13px !important;
        line-height: 30px !important;
        background: $teal-dark !important;
        border-color: $teal !important;
        color: #fff !important;
        font-weight: 600;
        box-shadow: none !important;

        &:hover,
        &:focus {
          background: #3a8570 !important;
          border-color: lighten($teal, 6%) !important;
        }
      }

      .search-input {
        width: 260px;
        margin: 0;
      }

      ::v-deep .el-input__inner {
        background: rgba(10, 21, 32, 0.75);
        border-color: rgba(84, 144, 122, 0.4);
        color: $text;
        height: 32px;
        line-height: 32px;
        font-size: 13px;
        box-shadow: none !important;

        &::placeholder {
          color: rgba(255, 255, 255, 0.42);
        }

        &:hover,
        &:focus {
          border-color: $teal;
        }
      }

      ::v-deep .el-input__prefix,
      ::v-deep .el-input__suffix {
        color: rgba(255, 255, 255, 0.45);
      }
    }

    .coord-text {
      color: rgba(255, 255, 255, 0.78);
      font-variant-numeric: tabular-nums;
    }

    .status-pill {
      display: inline-block;
      min-width: 48px;
      padding: 2px 10px;
      border-radius: 4px;
      font-size: 12px;
      line-height: 20px;

      &.is-on {
        background: rgba(84, 144, 122, 0.28);
        color: #8fd4b8;
        border: 1px solid rgba(84, 144, 122, 0.5);
      }

      &.is-off {
        background: rgba(255, 255, 255, 0.08);
        color: rgba(255, 255, 255, 0.55);
        border: 1px solid rgba(255, 255, 255, 0.18);
      }
    }

    .op-edit {
      color: $teal !important;
      font-weight: 500;
      &:hover { color: lighten($teal, 12%) !important; }
    }

    .op-delete {
      color: #f87171 !important;
      font-weight: 500;
      &:hover { color: #fca5a5 !important; }
    }

    ::v-deep .attendance-table.el-table {
      background: transparent;
      color: $text;
      border: none !important;
      border-top: none !important;

      /* Element 默认 #EBEEF5 底边/侧边，深色下像白线 */
      &::before,
      &::after {
        display: none !important;
        background-color: transparent !important;
        height: 0 !important;
        width: 0 !important;
      }

      .el-table__header-wrapper,
      .el-table__body-wrapper,
      .el-table__footer-wrapper {
        border: none !important;
      }

      .el-table__header-wrapper th,
      .el-table__fixed-header-wrapper th {
        background-color: #0c1824 !important;
        color: rgba(255, 255, 255, 0.78) !important;
        font-weight: 600;
        border-bottom: 1px solid rgba(84, 144, 122, 0.28) !important;
        border-right: none !important;
        padding: 12px 0;
      }

      .el-table__body-wrapper tr,
      .el-table__fixed-body-wrapper tr {
        background-color: transparent !important;

        td {
          background-color: transparent !important;
          border-bottom: 1px solid rgba(84, 144, 122, 0.16) !important;
          border-right: none !important;
          color: rgba(255, 255, 255, 0.88);
          padding: 12px 0;
        }

        &:hover > td {
          background-color: rgba(84, 144, 122, 0.1) !important;
        }
      }

      .el-table__empty-block {
        background: transparent;
        color: $text-muted;
      }

      .el-table__fixed,
      .el-table__fixed-right {
        background: $bg-panel;
        &::before {
          background-color: rgba(84, 144, 122, 0.2) !important;
        }
      }

      .el-table__fixed-right-patch {
        background: #0c1824 !important;
        border-bottom: 1px solid rgba(84, 144, 122, 0.28) !important;
      }

      /* 横向滚动条：去掉浅色粗条 */
      .el-table__body-wrapper,
      .el-table__header-wrapper,
      .el-table__fixed-body-wrapper {
        &::-webkit-scrollbar {
          height: 8px;
          width: 8px;
        }
        &::-webkit-scrollbar-track {
          background: rgba(10, 21, 32, 0.6);
        }
        &::-webkit-scrollbar-thumb {
          background: rgba(84, 144, 122, 0.45);
          border-radius: 4px;
        }
        &::-webkit-scrollbar-thumb:hover {
          background: rgba(84, 144, 122, 0.65);
        }
      }
    }

    .pagination-container {
      margin-top: 16px;
      padding-top: 14px;
      text-align: right;
      border-top: 1px solid rgba(84, 144, 122, 0.2);

      ::v-deep .el-pagination {
        .el-pagination__total,
        .el-pagination__jump {
          color: rgba(255, 255, 255, 0.75);
        }

        .btn-prev,
        .btn-next,
        .el-pager li {
          background: #162e46 !important;
          color: rgba(255, 255, 255, 0.85) !important;
          border: 1px solid rgba(84, 144, 122, 0.28) !important;
          border-radius: 4px;
          min-width: 30px;
          margin: 0 3px;
        }

        .el-pager li.active {
          background: $teal !important;
          border-color: $teal !important;
          color: #fff !important;
        }

        .el-pager li:hover:not(.active),
        .btn-prev:hover,
        .btn-next:hover {
          color: #8fd4b8 !important;
          border-color: $teal !important;
        }

        .el-select .el-input__inner,
        .el-pagination__editor .el-input__inner {
          background: #162e46 !important;
          border-color: rgba(84, 144, 122, 0.35) !important;
          color: rgba(255, 255, 255, 0.88) !important;
        }
      }
    }
  }

  .map-selector {
    .location-info {
      margin-top: 12px;
      padding: 12px;
      background: rgba(10, 21, 32, 0.65);
      border-radius: 4px;
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
      line-height: 1.8;
      border: 1px solid $teal-border;

      p {
        margin: 4px 0;

        strong {
          color: $text;
          font-weight: 600;
        }
      }
    }
  }
}

.map-picker-container {
  .search-section {
    margin-bottom: 16px;

    ::v-deep .el-input__inner {
      background: rgba(10, 21, 32, 0.7);
      border-color: rgba(255, 255, 255, 0.2);
      color: $text;

      &::placeholder {
        color: rgba(255, 255, 255, 0.45);
      }
    }

    ::v-deep .el-input-group__append {
      background: $teal-dark;
      border-color: $teal;
      color: #fff;
    }
  }

  .map-content-wrapper {
    position: relative;
    display: flex;
    gap: 16px;
  }

  .map-container {
    flex: 1;
    width: 100%;
    height: 500px;
    border: 1px solid $teal-border;
    border-radius: 4px;
  }

  .search-results {
    width: 300px;
    border: 1px solid $teal-border;
    border-radius: 4px;
    background: rgba(10, 21, 32, 0.9);
    display: flex;
    flex-direction: column;
    max-height: 500px;

    .search-results-header {
      padding: 12px 16px;
      border-bottom: 1px solid rgba(84, 144, 122, 0.25);
      font-size: 14px;
      font-weight: 600;
      color: $text;
      background: rgba(10, 21, 32, 0.7);
    }

    .search-results-list {
      flex: 1;
      overflow-y: auto;
      padding: 8px 0;

      .search-result-item {
        padding: 12px 16px;
        cursor: pointer;
        transition: background-color 0.2s;
        border-bottom: 1px solid rgba(84, 144, 122, 0.12);

        &:hover {
          background: rgba(84, 144, 122, 0.12);
        }

        &:last-child {
          border-bottom: none;
        }

        .result-name {
          font-size: 14px;
          font-weight: 500;
          color: $text;
          margin-bottom: 4px;
        }

        .result-address {
          font-size: 12px;
          color: $text-muted;
          line-height: 1.5;
        }
      }
    }
  }
}

::v-deep .el-dialog {
  background: linear-gradient(180deg, #122033 0%, $bg-panel 100%);
  border: 1px solid $teal-border;

  .el-dialog__header {
    border-bottom: 1px solid rgba(84, 144, 122, 0.25);
    padding: 18px 20px 14px;

    .el-dialog__title {
      color: $text;
      font-size: 17px;
      font-weight: 600;
    }

    .el-dialog__close {
      color: rgba(255, 255, 255, 0.65);

      &:hover {
        color: $teal;
      }
    }
  }

  .el-dialog__body {
    padding: 20px;
    color: $text;
    max-height: 70vh;
    overflow-y: auto;
  }

  .el-dialog__footer {
    border-top: 1px solid rgba(84, 144, 122, 0.25);
    padding: 14px 20px;
  }

  .el-form-item__label {
    color: rgba(255, 255, 255, 0.88);
  }

  .el-input__inner,
  .el-textarea__inner {
    background: rgba(10, 21, 32, 0.7);
    border-color: rgba(255, 255, 255, 0.2);
    color: #ffffff;

    &::placeholder {
      color: rgba(255, 255, 255, 0.42);
    }

    &:focus {
      border-color: $teal;
    }
  }

  .el-radio__label {
    color: rgba(255, 255, 255, 0.88);
  }

  .el-radio__input.is-checked .el-radio__inner {
    border-color: $teal;
    background: $teal;
  }

  .el-input-number .el-input__inner {
    background: rgba(10, 21, 32, 0.7);
    border-color: rgba(255, 255, 255, 0.2);
    color: #fff;
  }

  .dialog-footer .el-button--primary {
    background: $teal-dark !important;
    border-color: $teal !important;
  }
}
</style>

