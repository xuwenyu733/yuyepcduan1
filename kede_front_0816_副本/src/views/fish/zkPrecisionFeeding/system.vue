<template>
  <div class="app-container bg-dark-main min-h-screen text-gray-200">
    <PageLoadingMask
      :visible="pageLoading && !(allPonds && allPonds.length)"
      text="正在加载精准投喂数据…"
    />
    <!-- Header -->
    <div class="header-section bg-dark-card border-b shadow-sm mb-6">
      <div class="container mx-auto px-4 py-6">
        <h1 class="text-3xl mb-4 font-bold text-white">工厂化循环水鱼类精准投喂系统</h1>

      </div>
    </div>

    <div class="container mx-auto px-4">
      <!-- Search and Filter -->
      <el-card shadow="hover" class="mb-6 dark-card">
        <div class="flex gap-4 items-center flex-wrap">
          <el-input v-model="searchTerm" placeholder="搜索鱼池名称、品种或系统..." prefix-icon="el-icon-search" style="width: 300px;" size="small" class="dark-input"></el-input>
          <el-select v-model="selectedWorkshopId" @change="handleWorkshopChange" placeholder="选择车间" style="width: 120px;" size="small" class="dark-select">
            <el-option label="所有车间" value="all"></el-option>
            <el-option v-for="w in workshops" :key="'ws_'+w.id" :label="w.name" :value="w.id"></el-option>
          </el-select>
          <el-select v-model="selectedSystemId" @change="handleSystemChange" placeholder="选择系统" style="width: 120px;" size="small" class="dark-select">
            <el-option label="所有系统" value="all"></el-option>
            <el-option v-for="s in currentSystems" :key="'sys_'+s.id+'_'+(s.name||'')" :label="s.name" :value="s.id"></el-option>
          </el-select>
          <el-select v-model="filterPond" clearable placeholder="池号" style="width: 120px;" size="small" class="dark-select" @change="handlePondFilterChange">
            <el-option v-for="p in pondFilterOptions" :key="'pond_'+p" :label="p" :value="p"></el-option>
          </el-select>
          <el-select v-model="filterBatch" clearable placeholder="批次" style="width: 140px;" size="small" class="dark-select">
            <el-option v-for="b in batchFilterOptions" :key="'batch_'+b" :label="b" :value="b"></el-option>
          </el-select>
          <el-select v-model="filterSpecies" clearable placeholder="品种" style="width: 120px;" size="small" class="dark-select">
            <el-option v-for="s in speciesFilterOptions" :key="'sp_'+s" :label="s" :value="s"></el-option>
          </el-select>
          <el-select v-model="filterSpec" clearable placeholder="规格" style="width: 110px;" size="small" class="dark-select">
            <el-option v-for="s in specFilterOptions" :key="'spec_'+s" :label="s" :value="s"></el-option>
          </el-select>

          <el-select v-model="filters.status" multiple placeholder="状态筛选" style="width: 120px;" size="small" class="dark-select">
            <el-option label="良好" value="normal"></el-option>
            <el-option label="关注" value="warning"></el-option>
            <el-option label="介入" value="alert"></el-option>
          </el-select>

          <el-select v-model="filters.growthStage" multiple placeholder="发育阶段筛选" style="width: 160px;" size="small" class="dark-select">
            <el-option v-for="s in availableStages" :key="s" :label="s" :value="s"></el-option>
          </el-select>
        </div>
      </el-card>
      <!-- Stats Cards -->
      <el-row :gutter="12" class="mb-6">
        <el-col :span="3">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">鱼池总数</div>
            <div class="text-2xl font-bold text-white">{{ stats.totalPonds }}</div>
          </el-card>
        </el-col>
        <el-col :span="3">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">总生物量</div>
            <div class="text-2xl font-bold text-white">{{ stats.totalBiomass.toFixed(0) }} kg</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">今日投喂量</div>
            <div class="flex justify-between items-end ">
              <div class="text-2xl font-bold text-white">{{ stats.totalFeedToday.toFixed(1) }} kg</div>
              <div class="text-xs text-right" :class="(apiStats.yesterdayDiffPercent || 0) >= 0 ? 'text-green-400' : 'text-red-400'">
                <div>昨日 {{ Number(apiStats.yesterdayFeedCount || 0).toFixed(1) }} kg</div>
                <div v-if="apiStats.yesterdayDiffPercent != null">
                  ({{ apiStats.yesterdayDiffPercent > 0 ? '+' : '' }}{{ apiStats.yesterdayDiffPercent }}%)
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">平均规格 / 密度</div>
            <div class="flex justify-between items-end mt-1">
              <div class="text-lg font-bold text-white">{{ Number(apiStats.avgSpec || 0).toFixed(1) }} g</div>
              <div class="text-xs text-gray-400 ">{{ Number(apiStats.avgDensity || 0).toFixed(1) }} 尾/m³</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="3">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">良好</div>
            <div class="text-2xl font-bold text-green-400">{{ stats.normalCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="3">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">关注</div>
            <div class="text-2xl font-bold text-yellow-400">{{ stats.warningCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">介入</div>
            <div class="text-2xl font-bold text-red-400">{{ stats.alertCount }}</div>
          </el-card>
        </el-col>
      </el-row>



      <!-- Main Content -->
      <el-row :gutter="24">
        <!-- Pond List -->
        <el-col :span="8">
          <el-card shadow="hover" class="pond-list-card dark-card">
            <div slot="header" class="flex justify-between items-center text-white">
              <span class="font-bold">鱼池列表</span>
              <el-tag type="info" effect="dark" size="small">{{ filteredPonds.length }} 个</el-tag>
            </div>
            <div class="pond-grid" style="height: calc(100vh - 350px); overflow-y: auto;">
              <div v-for="(pond, pondIdx) in filteredPonds" :key="'pond_'+pondIdx+'_'+pond.id+'_'+(pond.pondDetailNum||pond.name||'')" class="pond-item" @click="selectPond(pond)">
                <div class="visual-wrapper" :class="{'is-selected': selectedPond && selectedPond.id === pond.id}">
                  <div style="position: relative; width: 150px; height: 150px;">
                    <svg width="150" height="150" viewBox="0 0 200 200">
                      <circle cx="100" cy="100" r="95" fill="none" :stroke="getStatusColor(pond.status)" :stroke-width="selectedPond && selectedPond.id === pond.id ? '6' : '4'" />
                      <circle cx="100" cy="100" r="88" fill="#1f2937" stroke="#374151" stroke-width="2" />
                      <defs>
                        <clipPath :id="'waterClip-' + pondIdx + '-' + pond.id">
                          <circle cx="100" cy="100" r="86" />
                        </clipPath>
                        <linearGradient :id="'waterGradient-' + pondIdx + '-' + pond.id" x1="0%" y1="0%" x2="0%" y2="100%">
                          <stop offset="0%" style="stop-color: #3b82f6; stop-opacity: 0.6" />
                          <stop offset="100%" style="stop-color: #1e3a8a; stop-opacity: 0.8" />
                        </linearGradient>
                      </defs>
                      <rect x="14" :y="14 + (172 * 0.15)" width="172" :height="172 * 0.85" :fill="'url(#waterGradient-' + pondIdx + '-' + pond.id + ')'" :clip-path="'url(#waterClip-' + pondIdx + '-' + pond.id + ')'" />
                      <ellipse cx="100" :cy="14 + (172 * 0.15)" rx="86" ry="8" fill="#60a5fa" opacity="0.3" />
                      <g opacity="0.6">
                        <ellipse cx="70" cy="100" rx="15" ry="8" fill="#fff" opacity="0.8" />
                        <path d="M 55 100 L 50 95 L 50 105 Z" fill="#fff" opacity="0.8" />
                        <ellipse cx="130" cy="120" rx="12" ry="6" fill="#fff" opacity="0.7" />
                        <path d="M 142 120 L 147 116 L 147 124 Z" fill="#fff" opacity="0.7" />
                        <ellipse cx="90" cy="140" rx="13" ry="7" fill="#fff" opacity="0.7" />
                        <path d="M 77 140 L 72 136 L 72 144 Z" fill="#fff" opacity="0.7" />
                      </g>
                      <circle cx="60" cy="150" r="3" fill="#fff" opacity="0.4" />
                      <circle cx="140" cy="160" r="2" fill="#fff" opacity="0.4" />
                      <circle cx="110" cy="135" r="2.5" fill="#fff" opacity="0.4" />
                    </svg>

                    <!-- 投喂信息 Overlay -->
                    <div style="position: absolute; top: 0; left: 0; width: 150px; height: 150px; display: flex; flex-direction: column; align-items: center; justify-content: center; pointer-events: none;">
                      <div style="background-color: rgba(17, 24, 39, 0.7); border-radius: 14px; padding: 6px; display: flex; flex-direction: column; align-items: center; width: 88px; border: 1px solid rgba(107, 114, 128, 0.5); backdrop-filter: blur(2px);">
                        <!-- 今日投喂 -->
                        <div style="color: #d1d5db; font-size: 11px; margin-bottom: 2px;">今日投喂</div>
                        <div style="color: #ffffff; font-weight: bold; font-size: 16px; margin-bottom: 4px;">
                          {{ pond.feedAmount.dailyGross || 0 }} <span style="font-weight: normal; color: #d1d5db; font-size: 10px;">kg</span>
                        </div>

                        <!-- 分割线 -->
                        <div style="width: 100%; height: 1px; background-color: rgba(107, 114, 128, 0.5); margin-bottom: 4px;"></div>

                        <!-- 对比 -->
                        <div style="width: 100%; display: flex; flex-direction: column; gap: 4px;">
                          <div style="display: flex; justify-content: space-between; align-items: center; font-size: 11px;">
                            <span style="color: #9ca3af;">昨日</span>
                            <div style="display: flex; align-items: center;">
                              <span style="color: #f3f4f6;">{{ pond.feedAmount.yesterdayTotalCount || 0 }}</span>
                              <span v-if="Number(pond.feedAmount.dailyGross || 0) > Number(pond.feedAmount.yesterdayTotalCount || 0)" style="color: #f87171; font-weight: bold; margin-left: 4px;">↑</span>
                              <span v-else-if="Number(pond.feedAmount.dailyGross || 0) < Number(pond.feedAmount.yesterdayTotalCount || 0)" style="color: #4ade80; font-weight: bold; margin-left: 4px;">↓</span>
                              <span v-else style="color: #6b7280; margin-left: 4px;">-</span>
                            </div>
                          </div>
                          <div style="display: flex; justify-content: space-between; align-items: center; font-size: 11px;">
                            <span style="color: #9ca3af;">前3日均</span>
                            <div style="display: flex; align-items: center;">
                              <span style="color: #f3f4f6;">{{ pond.feedAmount.avgDailyFeed || 0 }}</span>
                              <span v-if="Number(pond.feedAmount.dailyGross || 0) > Number(pond.feedAmount.avgDailyFeed || 0)" style="color: #f87171; font-weight: bold; margin-left: 4px;">↑</span>
                              <span v-else-if="Number(pond.feedAmount.dailyGross || 0) < Number(pond.feedAmount.avgDailyFeed || 0)" style="color: #4ade80; font-weight: bold; margin-left: 4px;">↓</span>
                              <span v-else style="color: #6b7280; margin-left: 4px;">-</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="mt-2 text-center">
                    <div class="font-medium text-gray-200">{{ pond.name }}</div>
                    <div class="text-sm text-gray-400">{{ pond.fishSpecies }}</div>
                    <div class="flex items-center justify-center gap-2 mt-1">
                      <div class="w-2 h-2 rounded-full" :style="{ backgroundColor: getStatusColor(pond.status) }"></div>
                      <span class="text-xs text-gray-400">{{ getStatusText(pond.status) }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="filteredPonds.length === 0" class="text-center py-8 text-gray-500">没有找到符合条件的鱼池</div>
            </div>
          </el-card>
        </el-col>

        <!-- Details Panel -->
        <el-col :span="16">
          <el-card shadow="hover" class="dark-card">
            <div slot="header" class="font-bold text-white">
              <span>{{ selectedPond ? `${selectedPond.name} - 详细信息` : '请选择一个鱼池查看详情' }}</span>
            </div>
            <div v-if="selectedPond" style="height: calc(100vh - 350px); overflow-y: auto; padding-right: 10px;" class="custom-scrollbar">
              <div class="space-y-4">
                <!-- 基本信息 -->
                <el-card shadow="never" class="mb-4 dark-inner-card">
                  <div slot="header" class="flex items-center gap-2 font-bold text-white">
                    <i class="el-icon-info text-blue-400"></i> 鱼池基本信息
                  </div>
                  <el-row :gutter="20">
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">池号</div>
                      <div class="font-medium">{{ selectedPond.pondDetailNum || selectedPond.name }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">所属车间</div>
                      <div class="font-medium">{{ selectedPond.workshopName }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">所属系统</div>
                      <div class="font-medium">{{ selectedPond.systemName }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">养殖批次</div>
                      <div class="font-medium">{{ selectedPond.batchNumber || '-' }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">规格</div>
                      <div class="font-medium">{{ formatFishSpec(selectedPond.averageWeight) }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">养殖水体</div>
                      <div class="font-medium">{{ selectedPond.capacity }} m³</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">鱼类品种</div>
                      <div class="font-medium">{{ selectedPond.fishSpecies }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">鱼数量</div>
                      <div class="font-medium">{{ selectedPond.fishCount.toLocaleString() }} 尾</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">平均体重</div>
                      <div class="font-medium">{{ formatFishSpec(selectedPond.averageWeight) }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">总生物量</div>
                      <div class="font-medium">{{ selectedPond.totalBiomass }} kg</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">养殖天数</div>
                      <div class="font-medium">{{ selectedPond.age }} 天</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">平均密度</div>
                      <div class="font-medium">{{ selectedPond.avgDensity != null ? selectedPond.avgDensity : '-' }} 尾/m³</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">健康状态</div>
                      <div class="font-medium">{{ selectedPond.pondStatus }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">今日死亡</div>
                      <div class="font-medium">{{ selectedPond.todayDeathCount != null ? selectedPond.todayDeathCount : 0 }} 尾</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">水质情况</div>
                      <div class="font-medium text-xs">
                        水温 {{ (selectedPond.waterQuality && selectedPond.waterQuality.temperature) != null ? selectedPond.waterQuality.temperature : '-' }}℃ /
                        溶氧 {{ (selectedPond.waterQuality && selectedPond.waterQuality.dissolvedOxygen) != null ? selectedPond.waterQuality.dissolvedOxygen : '-' }} /
                        氨氮 {{ (selectedPond.waterQuality && selectedPond.waterQuality.ammonia) != null ? selectedPond.waterQuality.ammonia : '-' }} /
                        pH {{ (selectedPond.waterQuality && selectedPond.waterQuality.ph) != null ? selectedPond.waterQuality.ph : '-' }}
                      </div>
                    </el-col>

                  </el-row>
                </el-card>

                <el-card shadow="never" class="mb-4 dark-inner-card" v-if="affectingTasks.length">
                  <div slot="header" class="font-bold text-white">影响投喂的任务</div>
                  <el-table :data="affectingTasks" size="mini" class="dark-table">
                    <el-table-column label="任务" min-width="120" show-overflow-tooltip>
                      <template slot-scope="scope">{{ scope.row.taskName || scope.row.name || scope.row.title || '-' }}</template>
                    </el-table-column>
                    <el-table-column label="类型" width="100">
                      <template slot-scope="scope">{{ formatAffectTaskType(scope.row.taskType != null ? scope.row.taskType : scope.row.type) }}</template>
                    </el-table-column>
                    <el-table-column label="状态" width="90">
                      <template slot-scope="scope">{{ formatAffectTaskStatus(scope.row.status) }}</template>
                    </el-table-column>
                    <el-table-column label="说明" min-width="160" show-overflow-tooltip>
                      <template slot-scope="scope">{{ scope.row.remark || scope.row.description || scope.row.content || '-' }}</template>
                    </el-table-column>
                  </el-table>
                </el-card>

                <el-alert type="warning" title="核心注意事项" :closable="false" class="mb-4 alert-dark alert-warning-dark" show-icon>
                  <div class="text-sm space-y-1">
                    <div v-for="(line, idx) in corePrecautionLines" :key="'cp_'+idx">• {{ line }}</div>
                  </div>
                </el-alert>
                <el-alert type="info" title="日投喂量计算说明" :closable="false" class="mb-4 alert-dark alert-info-dark" show-icon>
                  <div class="text-sm">
                    日投喂量(kg) = 生物量 × 基础投饵率 × 水温系数 × 溶氧系数 × 水质系数 × 摄食行为系数；智能投喂量可在下方手动微调。
                  </div>
                </el-alert>

                <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="dark-tabs">
                  <el-tab-pane label="投喂信息" name="feeding">
                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="flex justify-between items-center text-white">
                        <span class="font-bold">各生长阶段营养需求</span>
                        <div>
                          <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleNutritionOpenForm(undefined)"
                                     v-hasPermi="['fish:zk-grow-nutritional-requirements:create']">新增</el-button>
                        </div>
                      </div>
                      <el-table :data="nutritionList" size="small" border class="dark-table">
                        <el-table-column prop="growthStage" label="阶段" width="100">
                          <template slot-scope="scope">
                            {{ scope.row.growthStage }}
                            <el-tag v-if="scope.row.growthStage === selectedPond.growthStage" size="mini" type="primary" effect="dark" class="ml-2">当前</el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column prop="dayold" label="日龄" width="80" />
                        <el-table-column prop="weightRange" label="体重范围" width="100" />
                        <el-table-column prop="crudeProtein" label="粗蛋白" />
                        <el-table-column prop="crudeFat" label="粗脂肪" />
                        <el-table-column prop="carbohydrate" label="碳水" />
                        <el-table-column prop="crudeFiber" label="纤维" />
                        <el-table-column prop="calcium" label="钙" />
                        <el-table-column prop="phosphorus" label="磷" />
                        <el-table-column label="操作" width="130" class-name="nutrition-action-col">
                          <template slot-scope="scope">
                            <div class="nutrition-action-btns">
                              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleNutritionOpenForm(scope.row.id)"
                                         v-hasPermi="['fish:zk-grow-nutritional-requirements:update']">修改</el-button>
                              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleNutritionDelete(scope.row)"
                                         v-hasPermi="['fish:zk-grow-nutritional-requirements:delete']">删除</el-button>
                            </div>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card feed-plan-card">
                      <div slot="header" class="font-bold text-white">智能 / 实际投喂计划</div>
                      <el-row :gutter="24" class="mb-4 feed-plan-row">
                        <el-col :span="12">
                          <div class="feed-plan-col">
                            <div class="text-sm text-gray-400 mb-2">智能投喂计划</div>
                            <div class="text-sm text-gray-300  mb-4 mt-2 ">
                              总量 {{ feedPlanSmart.totalAmountKg || smartFeedAmount }} kg /
                              {{ feedPlanSmart.frequency || '-' }} 次 /
                              单次 {{ feedPlanSmart.singleAmountKg || '-' }} kg /
                              {{ feedPlanSmart.feedType || '-' }}
                            </div>
                            <el-button size="mini" type="primary" plain @click="saveSmartPlan">保存智能计划</el-button>
                          </div>
                        </el-col>
                        <el-col :span="12">
                          <div class="feed-plan-col">
                            <div class="text-sm text-gray-400 mb-2 ">实际投喂计划</div>
                            <div class="mb-3 flex items-center flex-wrap gap-2">
                              <el-input-number v-model="actualPlanTotal" :precision="2" :min="0" :step="0.1" size="mini" style="width:110px" />
                              <span class="text-xs text-gray-400">kg</span>
                              <el-input-number v-model="actualPlanFreq" :min="1" :max="8" size="mini" style="width:80px" />
                              <span class="text-xs text-gray-400">次</span>
                            </div>
                            <el-input v-model="actualPlanFeedType" size="mini" placeholder="饲料种类" style="width:160px; float:right;" class="mb-3" />
                            <div class="flex gap-2 mt-4">
                              <el-button size="mini" @click="saveActualPlan">保存实际计划</el-button>
                              <el-button size="mini" type="success" @click="confirmActualPlan">确定实施</el-button>
                            </div>
                          </div>
                        </el-col>
                      </el-row>
                      <div class="p-4 bg-blue-900 bg-opacity-20 border border-blue-800 rounded-lg feed-plan-recommend">
                        <div class="flex items-center flex-wrap gap-4">
                          <div class="flex-1" style="min-width: 200px;">
                            <div class="text-sm text-gray-400 mb-2">智能投喂量 (kg)</div>
                            <el-input-number
                              v-model="smartFeedAmount"
                              :precision="2"
                              :min="0"
                              :step="0.1"
                              size="small"
                              style="width: 160px;"
                            />
                            <el-button size="mini" type="text" class="ml-2 mb-2 px-4" @click="resetSmartFeedAmount">恢复推荐值</el-button>
                          </div>
                          <div v-if="feedRecommendation" class="text-sm text-gray-400 pl-4">
                            <div class="mb-1">系统推荐：{{ Number(feedRecommendation.dailyFeedAmount).toFixed(2) }} kg</div>
                            <div class="mb-1">今日实际：{{ selectedPond.feedAmount.dailyGross || 0 }} kg</div>
                            <div v-if="smartFeedDiffPercent != null" :class="Math.abs(smartFeedDiffPercent) > 15 ? 'text-yellow-400' : 'text-green-400'">
                              与推荐差异：{{ smartFeedDiffPercent > 0 ? '+' : '' }}{{ smartFeedDiffPercent.toFixed(1) }}%
                            </div>
                          </div>
                        </div>
                      </div>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card" v-if="aiAnalysis">
                      <div slot="header" class="font-bold text-white">AI 智能分析</div>
                      <div class="text-sm text-gray-300 mb-2">{{ aiAnalysis.summary }}</div>
                      <div class="text-xs text-yellow-400 mb-1" v-for="(r, i) in (aiAnalysis.deviationReasons || [])" :key="'r'+i">· {{ r }}</div>
                      <div class="text-xs text-green-400" v-for="(s, i) in (aiAnalysis.suggestions || [])" :key="'s'+i">· {{ s }}</div>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card feed-consumption-card">
                      <div slot="header" class="flex justify-between items-center text-white">
                        <span class="font-bold">饲料消耗情况</span>
                        <el-button size="small" icon="el-icon-refresh" @click="loadFeedConsumption">刷新</el-button>
                      </div>
                      <div class="text-sm text-gray-400 mb-2">合计 {{ Number(feedConsumption.totalFeedKg || 0).toFixed(2) }} kg / {{ feedConsumption.pondCount || 0 }} 池</div>
                      <el-table :data="feedConsumption.items || []" size="mini" max-height="220" class="dark-table">
                        <el-table-column prop="pondName" label="鱼池" min-width="100" />
                        <el-table-column prop="feedName" label="饲料" min-width="100" />
                        <el-table-column prop="feedSpec" label="型号" width="90" />
                        <el-table-column prop="totalKg" label="消耗(kg)" width="90" />
                      </el-table>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white">投喂统计</div>
                      <el-row :gutter="20" class="mb-4">
                        <el-col :span="12">
                          <div class="text-sm text-gray-400">累计投喂量</div>
                          <div class="text-2xl font-bold text-blue-400">{{ selectedPond.feedAmount.totalGross || 0 }} kg</div>

                          <div v-if="selectedPond.feedSpecStats && selectedPond.feedSpecStats.length > 0" class="mt-2 text-xs">
                            <div class="text-gray-400 mb-2">不同规格累计投喂:</div>
                            <div v-for="(stat, index) in selectedPond.feedSpecStats" :key="index" class="flex font-size-12 justify-between text-gray-300 mb-1 bg-gray-800 p-1 rounded border-radius-4 padding-left-2">
                              <span>{{ stat.feedSpec || '未知规格' }}</span>
                              <span class="text-blue-300">{{ stat.feedCount || 0 }} kg</span>
                            </div>
                          </div>

                        </el-col>
                        <el-col :span="12">
                          <div class="text-sm text-gray-400">日投喂量</div>
                          <div class="text-2xl font-bold text-green-400">{{ selectedPond.feedAmount.dailyGross || 0 }} kg</div>
                        </el-col>
                      </el-row>

                      <div class="p-4 bg-purple-900 bg-opacity-20 border border-purple-800 rounded-lg mb-4">
                        <div class="text-sm text-gray-300">投饵率</div>
                        <div class="text-2xl font-bold text-purple-400">{{ selectedPond.feedRate || 0 }}%</div>
                      </div>

                      <div class="p-4 border border-gray-700 rounded-lg bg-gray-800">
                        <div class="text-sm font-medium text-white mb-3">昨日投喂量对比</div>
                        <div class="space-y-2">
                          <div class="flex items-center justify-between text-sm">
                            <span class="text-gray-400">今日投喂</span>
                            <span class="font-medium text-green-400">{{ selectedPond.feedAmount.dailyGross || 0 }} kg</span>
                          </div>
                          <div class="flex items-center justify-between text-sm">
                            <span class="text-gray-400">昨日投喂</span>
                            <span class="font-medium text-blue-400">{{ selectedPond.feedAmount.yesterdayTotalCount || 0 }} kg</span>
                          </div>
                          <div class="flex items-center justify-between text-sm">
                            <span class="text-gray-400">前3日均</span>
                            <span class="font-medium text-blue-400">{{ selectedPond.feedAmount.avgDailyFeed || 0 }} kg</span>
                          </div>
                        </div>
                      </div>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white">投喂任务列表</div>
                      <el-table :data="selectedPond.feedAmount.todayFeedings" size="small" border class="dark-table" empty-text="今日暂无投喂记录">
                        <el-table-column prop="time" label="时间" width="90" />
                        <el-table-column prop="feedName" label="饲料名称" />
                        <el-table-column prop="feedSpec" label="规格" width="100" />
                        <el-table-column prop="grossWeight" label="投喂量(kg)"  width="110">
                          <template slot-scope="scope">
                            <span class="text-blue-400 font-medium">{{ scope.row.grossWeight || 0 }}</span>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white">不同规格饲料投喂量统计</div>
                      <el-row>
                        <el-col :span="12">
                          <div ref="barChartRef" style="width: 100%; height: 250px;"></div>
                        </el-col>
                        <el-col :span="12">
                          <div ref="pieChartRef" style="width: 100%; height: 250px;"></div>
                        </el-col>
                      </el-row>
                    </el-card>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </div>
            <div v-else class="flex items-center justify-center h-64 text-gray-500">
              <div class="text-center">
                <div class="text-6xl mb-4">🐟</div>
                <p>点击左侧鱼池查看详细信息</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <!-- 对话框 -->
    <ZkGrowNutritionalRequirementsForm ref="nutritionFormRef" @success="refreshNutritionList" />
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getPrecisionFeedingStats, getPrecisionFeedingPonds, getPrecisionFeedingTodayLogs } from '@/api/fish/zkBiologyCapital';
import {
  getPrecisionFeedRecommend,
  getPrecisionFeedPlans,
  savePrecisionFeedPlan,
  confirmPrecisionFeedPlan,
  getPrecisionFeedConsumption,
  getPrecisionFeedAiAnalysis,
  getPrecisionFeedAffectingTasks
} from '@/api/fish/zkPrecisionFeeding';
import * as ZkGrowNutritionalRequirementsApi from '@/api/fish/zkgrownutritionalrequirements';
import ZkGrowNutritionalRequirementsForm from '../zkgrownutritionalrequirements/ZkGrowNutritionalRequirementsForm.vue';
import { getThresholdModule, resolveFeedingDev, resolveFeedingFcr } from '@/utils/thresholdConfig';
import { evaluateAssociationAndPush } from '@/utils/associationBridge';
import { silentCatch } from '@/utils/reportSilentError';
import {
  loadSharedSiteOptions,
  pickWorkshopOptionItems,
  pickSystemOptionItems,
  pickPondOptionLabels,
  rowMatchesSiteFilters,
  workshopsEqualLoose,
  systemsEqualLoose,
  isRowInUserSiteScope,
  SITE_OPTIONS_CHANGED_EVENT
} from '@/utils/siteOptions';
import { onProductionRecordChanged } from '@/utils/productionRecordBridge';
import { collectBatchFilterOptions, rowMatchesBatchFilter } from '@/utils/batchTokens';
import { normalizeSpeciesName, speciesNamesEqual, collectSpeciesFilterOptions } from '@/utils/speciesTempBridge';
import PageLoadingMask from '@/components/PageLoadingMask/index';

const GROWTH_STAGES = [
  { stage: '鱼苗期', daysRange: [0, 30], proteinRequirement: 45, fatRequirement: 12, recommendedFeedRate: 8 },
  { stage: '幼鱼期', daysRange: [31, 90], proteinRequirement: 42, fatRequirement: 10, recommendedFeedRate: 5 },
  { stage: '成鱼期前期', daysRange: [91, 180], proteinRequirement: 38, fatRequirement: 8, recommendedFeedRate: 3 },
  { stage: '成鱼期后期', daysRange: [181, 365], proteinRequirement: 35, fatRequirement: 7, recommendedFeedRate: 2 },
];

function getGrowthStage(age) {
  const stage = GROWTH_STAGES.find(s => age >= s.daysRange[0] && age <= s.daysRange[1]);
  return stage?.stage || '成鱼期后期';
}

function calculateRecommendedFeed(biomass, age) {
  const stage = GROWTH_STAGES.find(s => age >= s.daysRange[0] && age <= s.daysRange[1]);
  if (stage) return biomass * (stage.recommendedFeedRate / 100);
  return biomass * 0.02;
}

function getRecommendedFeedSpec(age) {
  if (age <= 30) return '0.5mm';
  if (age <= 60) return '1.0mm';
  if (age <= 120) return '1.5mm';
  if (age <= 180) return '2.0mm';
  return '3.0mm';
}

function parseWeightG(value) {
  if (value == null || value === '') return 0
  if (typeof value === 'number') return Number.isFinite(value) ? value : 0
  const n = parseFloat(String(value).replace(/[^\d.-]/g, ''))
  return Number.isFinite(n) ? n : 0
}

function calculateTotalBiomass(effectiveCount, averageWeight) {
  return (Number(effectiveCount) || 0) * parseWeightG(averageWeight) / 1000
}

function calculateBaseFeedRate(averageWeight) {
  averageWeight = parseWeightG(averageWeight)
  if (averageWeight < 2.5) {
    return 6.0
  } else if (averageWeight <= 3.33) {
    return 9.0
  } else if (averageWeight <= 5.0) {
    return 10.0
  } else if (averageWeight <= 6.67) {
    return 9.0
  } else if (averageWeight <= 10.0) {
    return 7.5
  } else if (averageWeight <= 14.29) {
    return 6.0
  } else if (averageWeight <= 25.0) {
    return 5.0
  } else if (averageWeight <= 50.0) {
    return 3.0
  } else if (averageWeight <= 100.0) {
    return 2.7
  } else if (averageWeight <= 150.0) {
    return 2.5
  } else if (averageWeight <= 200.0) {
    return 2.0
  } else if (averageWeight <= 250.0) {
    return 1.6
  } else if (averageWeight <= 294.12) {
    return 1.3
  } else if (averageWeight <= 357.14) {
    return 1.1
  } else if (averageWeight <= 384.62) {
    return 1.0
  } else if (averageWeight <= 454.55) {
    return 0.9
  }
  return 0.8
}

function calculateTemperatureCoefficient(temperature) {
  if (temperature < 12) {
    return 0;
  } else if (temperature < 16) {
    return 0.4; // 0.3-0.5
  } else if (temperature < 22) {
    return 0.8; // 0.7-0.9
  } else if (temperature <= 28) {
    return 1.0;
  } else if (temperature <= 32) {
    return 0.85; // 0.8-0.9
  } else {
    return 0.25; // 0-0.5
  }
}

function calculateDoCoefficient(dissolvedOxygen) {
  if (dissolvedOxygen >= 5.0) {
    return 1.0;
  } else if (dissolvedOxygen >= 4.0) {
    return 0.8;
  } else if (dissolvedOxygen >= 3.0) {
    return 0.5;
  } else {
    return 0;
  }
}

function calculateWaterQualityCoefficient(ammonia, nitrite) {
  const ammoniaSafe = ammonia <= 0.2;
  const nitriteSafe = nitrite <= 0.2;
  const ammoniaWarning = ammonia > 0.2 && ammonia <= 0.5;
  const nitriteWarning = nitrite > 0.2 && nitrite <= 0.5;
  const ammoniaDanger = ammonia > 0.5;
  const nitriteDanger = nitrite > 0.5;

  if (ammoniaSafe && nitriteSafe) return 1.0;
  if ((ammoniaWarning && nitriteSafe) || (ammoniaSafe && nitriteWarning)) return 0.7;
  if (ammoniaDanger || nitriteDanger || (ammoniaWarning && nitriteWarning)) return 0.4;
  return 0;
}

function calculateFeedingBehaviorCoefficient(feedingBehavior) {
  if (feedingBehavior >= 85) return 1.0;
  if (feedingBehavior >= 60) return 0.8;
  if (feedingBehavior >= 30) return 0.5;
  return 0;
}

function calculateFeedRecommendation(params) {
  const warnings = [];

  const totalBiomass = calculateTotalBiomass(params.effectiveCount, params.averageWeight);
  const baseFeedRate = calculateBaseFeedRate(params.averageWeight);
  const temperatureCoefficient = calculateTemperatureCoefficient(params.temperature);
  const doCoefficient = calculateDoCoefficient(params.dissolvedOxygen);
  const waterQualityCoefficient = calculateWaterQualityCoefficient(params.ammonia, params.nitrite);
  const feedingBehaviorCoefficient = calculateFeedingBehaviorCoefficient(params.feedingBehavior);

  const dailyFeedAmount = totalBiomass *
    (baseFeedRate / 100) *
    temperatureCoefficient *
    doCoefficient *
    waterQualityCoefficient *
    feedingBehaviorCoefficient;

  const singleFeedAmount = dailyFeedAmount / params.dailyFrequency;

  let recommendation = '';

  if (temperatureCoefficient < 0.5) {
    warnings.push('水温过低或过高，严重影响摄食');
    recommendation += '建议调整水温至适宜范围(22-28°C)。';
  } else if (temperatureCoefficient < 0.8) {
    warnings.push('水温偏离最佳范围');
    recommendation += '水温偏低，酌情减少投喂。';
  }

  if (doCoefficient < 1.0) {
    if (doCoefficient === 0) {
      warnings.push('溶氧严重不足，应停止投喂');
      recommendation += '紧急增氧！停止投喂直至溶氧恢复。';
    } else if (doCoefficient === 0.5) {
      warnings.push('溶氧不足');
      recommendation += '加强增氧，减少投喂。';
    } else {
      warnings.push('溶氧偏低');
      recommendation += '注意增氧设备运行。';
    }
  }

  if (waterQualityCoefficient < 1.0) {
    if (waterQualityCoefficient === 0) {
      warnings.push('水质严重超标');
      recommendation += '立即停料并处理水质问题。';
    } else if (waterQualityCoefficient < 0.5) {
      warnings.push('水质指标危险');
      recommendation += '大幅减料，紧急改善水质。';
    } else {
      warnings.push('水质指标警戒');
      recommendation += '减少投喂，加强水质监控。';
    }
  }

  if (feedingBehaviorCoefficient < 1.0) {
    if (feedingBehaviorCoefficient === 0) {
      warnings.push('鱼群完全不摄食');
      recommendation += '停止投喂，检查鱼群健康状况。';
    } else if (feedingBehaviorCoefficient === 0.5) {
      warnings.push('摄食欲望差');
      recommendation += '减少投喂量，观察鱼群状态。';
    } else {
      warnings.push('摄食速度偏慢');
      recommendation += '适当减少投喂，避免残饵。';
    }
  }

  if (warnings.length === 0) {
    recommendation = '各项指标正常，按推荐量投喂。严控残饵，宁少勿多。';
  }

  if (params.averageWeight > 200) {
    recommendation += ' 成鱼阶段，注意上市前1-2天停食。';
  }

  return {
    totalBiomass,
    baseFeedRate,
    temperatureCoefficient,
    doCoefficient,
    waterQualityCoefficient,
    feedingBehaviorCoefficient,
    dailyFeedAmount,
    singleFeedAmount,
    recommendation,
    warnings,
  };
}

export default {
  name: "ZkPrecisionFeeding",
  components: {
    ZkGrowNutritionalRequirementsForm,
    PageLoadingMask
  },
  data() {
    return {
      workshops: [],
      allPonds: [],
      GROWTH_STAGES,
      selectedWorkshopId: 'all',
      selectedSystemId: 'all',
      filterPond: '',
      filterBatch: '',
      filterSpec: '',
      filterSpecies: '',
      selectedPond: null,
      searchTerm: '',
      smartFeedAmount: 0,
      filters: {
        status: [],
        growthStage: []
      },
      activeTab: 'feeding',
      charts: {},
      apiStats: {
        pondNum: 0,
        totalWeight: 0,
        feedCount: 0,
        normalPond: 0,
        preWarnPond: 0,
        alarmPond: 0,
        yesterdayFeedCount: 0,
        yesterdayDiffPercent: null,
        avgSpec: 0,
        avgDensity: 0
      },
      nutritionList: [],
      backendRecommend: null,
      feedingThreshold: {
        feedDevMax: 100,
        feedDevEnabled: true,
        fcrMin: 1.2,
        fcrMax: 1.5
      },
      feedPlanSmart: {},
      actualPlanTotal: 0,
      actualPlanFreq: 3,
      actualPlanFeedType: '',
      aiAnalysis: null,
      affectingTasks: [],
      feedConsumption: { totalFeedKg: 0, pondCount: 0, items: [] },
      pageLoading: false,
      siteOptions: null
    };
  },
  created() {
    // 阈值不挡首屏；鱼池与筛选项并行
    this.applyFeedingThresholds();
    this.fetchData();
    this.loadSiteFilterOptions(false);
    this._onSiteOptionsChanged = () => {
      this.loadSiteFilterOptions(false);
    };
    if (typeof window !== 'undefined') {
      window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    this._offProductionRecord = onProductionRecordChanged((detail) => {
      const t = detail && detail.type
      if (!t || t === 'feeding' || t === 'stocking' || t === 'death' || t === 'proofing') {
        this.fetchData()
      }
    })
  },
  activated() {
    this.loadSiteFilterOptions(false);
    this.fetchData();
  },
  computed: {
    currentSystems() {
      if (this.siteOptions) {
        const wid = this.selectedWorkshopId === 'all' ? '' : this.selectedWorkshopId;
        return pickSystemOptionItems(this.siteOptions, wid).map((o) => ({
          id: String(o.value),
          name: o.label
        }));
      }
      if (this.selectedWorkshopId === 'all') {
        const systems = [];
        const systemIds = new Set();
        this.allPonds.forEach(p => {
          if (p.systemId && !systemIds.has(p.systemId)) {
            systemIds.add(p.systemId);
            systems.push({ id: p.systemId, name: p.systemName });
          }
        });
        return systems;
      }
      const w = this.workshops.find(w => w.id === this.selectedWorkshopId);
      return w ? w.systems : [];
    },
    availableSpecies() {
      return collectSpeciesFilterOptions(this.allPonds);
    },
    availableStages() {
      return Array.from(new Set(this.allPonds.map(p => p.growthStage)));
    },
    speciesFilterOptions() {
      let ponds = this.allPonds;
      if (this.selectedWorkshopId && this.selectedWorkshopId !== 'all') {
        ponds = ponds.filter(p => workshopsEqualLoose(p.workshopId, this.selectedWorkshopId) || workshopsEqualLoose(p.workshopName, this.selectedWorkshopId));
      }
      if (this.selectedSystemId && this.selectedSystemId !== 'all') {
        ponds = ponds.filter(p => systemsEqualLoose(p.systemId, this.selectedSystemId) || systemsEqualLoose(p.systemName, this.selectedSystemId));
      }
      if (this.filterPond) {
        ponds = ponds.filter(p => rowMatchesSiteFilters(p, { pond: this.filterPond }, this.siteOptions) || String(p.pondDetailNum || p.name) === String(this.filterPond));
      }
      return collectSpeciesFilterOptions(ponds);
    },
    pondFilterOptions() {
      const wid = this.selectedWorkshopId === 'all' ? '' : this.selectedWorkshopId;
      const sid = this.selectedSystemId === 'all' ? '' : this.selectedSystemId;
      const fromSite = pickPondOptionLabels(this.siteOptions, wid, sid);
      if (fromSite.length) return fromSite;
      let ponds = this.allPonds;
      if (wid) ponds = ponds.filter(p => workshopsEqualLoose(p.workshopId, wid) || workshopsEqualLoose(p.workshopName, wid));
      if (sid) ponds = ponds.filter(p => systemsEqualLoose(p.systemId, sid) || systemsEqualLoose(p.systemName, sid));
      return [...new Set(ponds.map(p => p.pondDetailNum || p.name).filter(Boolean))];
    },
    batchFilterOptions() {
      let ponds = this.allPonds;
      if (this.selectedWorkshopId && this.selectedWorkshopId !== 'all') {
        ponds = ponds.filter(p => workshopsEqualLoose(p.workshopId, this.selectedWorkshopId) || workshopsEqualLoose(p.workshopName, this.selectedWorkshopId));
      }
      if (this.selectedSystemId && this.selectedSystemId !== 'all') {
        ponds = ponds.filter(p => systemsEqualLoose(p.systemId, this.selectedSystemId) || systemsEqualLoose(p.systemName, this.selectedSystemId));
      }
      if (this.filterPond) {
        ponds = ponds.filter(p => rowMatchesSiteFilters(p, { pond: this.filterPond }, this.siteOptions) || String(p.pondDetailNum || p.name) === String(this.filterPond));
      }
      return collectBatchFilterOptions(ponds);
    },
    specFilterOptions() {
      let ponds = this.allPonds;
      if (this.selectedWorkshopId && this.selectedWorkshopId !== 'all') {
        ponds = ponds.filter(p => workshopsEqualLoose(p.workshopId, this.selectedWorkshopId) || workshopsEqualLoose(p.workshopName, this.selectedWorkshopId));
      }
      if (this.selectedSystemId && this.selectedSystemId !== 'all') {
        ponds = ponds.filter(p => systemsEqualLoose(p.systemId, this.selectedSystemId) || systemsEqualLoose(p.systemName, this.selectedSystemId));
      }
      if (this.filterPond) {
        ponds = ponds.filter(p => rowMatchesSiteFilters(p, { pond: this.filterPond }, this.siteOptions) || String(p.pondDetailNum || p.name) === String(this.filterPond));
      }
      return [...new Set(ponds.map(p => this.formatFishSpec(p.averageWeight)).filter(Boolean))];
    },
    filteredPonds() {
      let ponds = this.allPonds || [];
      if (this.selectedWorkshopId && this.selectedWorkshopId !== 'all') {
        ponds = ponds.filter(p => workshopsEqualLoose(p.workshopId, this.selectedWorkshopId) || workshopsEqualLoose(p.workshopName, this.selectedWorkshopId));
      }
      if (this.selectedSystemId && this.selectedSystemId !== 'all') {
        ponds = ponds.filter(p => systemsEqualLoose(p.systemId, this.selectedSystemId) || systemsEqualLoose(p.systemName, this.selectedSystemId));
      }
      if (this.filterPond) {
        ponds = ponds.filter(p => rowMatchesSiteFilters(p, { pond: this.filterPond }, this.siteOptions) || String(p.pondDetailNum || p.name) === String(this.filterPond));
      }
      if (this.filterBatch) {
        ponds = ponds.filter(p => rowMatchesBatchFilter(p, this.filterBatch));
      }
      if (this.filterSpecies) {
        ponds = ponds.filter(p => speciesNamesEqual(p.fishSpecies, this.filterSpecies) || String(p.fishSpecies || '') === String(this.filterSpecies));
      }
      if (this.filterSpec) {
        ponds = ponds.filter(p => this.formatFishSpec(p.averageWeight) === this.filterSpec);
      }
      if (this.searchTerm) {
        const term = String(this.searchTerm).toLowerCase();
        ponds = ponds.filter(p => {
          const name = String(p.name || '').toLowerCase();
          const species = String(p.fishSpecies || '').toLowerCase();
          const system = String(p.systemName || '').toLowerCase();
          return name.includes(term) || species.includes(term) || system.includes(term);
        });
      }
      if (this.filters.status.length > 0) ponds = ponds.filter(p => this.filters.status.includes(p.status));
      if (this.filters.growthStage.length > 0) ponds = ponds.filter(p => this.filters.growthStage.includes(p.growthStage));
      return ponds;
    },
    stats() {
      const apiTotal = Number(this.apiStats.pondNum) || 0;
      const listTotal = (this.allPonds || []).length;
      return {
        // 列表已加载时以列表为准，避免「统计有数、列表为空」观感
        totalPonds: listTotal > 0 ? listTotal : apiTotal,
        totalBiomass: Number(this.apiStats.totalWeight) || 0,
        totalFeedToday: Number(this.apiStats.feedCount) || 0,
        normalCount: Number(this.apiStats.normalPond) || 0,
        warningCount: Number(this.apiStats.preWarnPond) || 0,
        alertCount: Number(this.apiStats.alarmPond) || 0,
      };
    },
    currentStage() {
      if (!this.selectedPond) return null;
      return this.GROWTH_STAGES.find(s => s.stage === this.selectedPond.growthStage);
    },
    feedRecommendation() {
      if (this.backendRecommend) {
        return {
          dailyFeedAmount: Number(this.backendRecommend.dailyFeedAmountKg || 0),
          singleFeedAmount: Number(this.backendRecommend.singleFeedAmountKg || 0),
          baseFeedRate: Number(this.backendRecommend.baseFeedRate || 0),
          recommendation: this.backendRecommend.recommendation,
          warnings: this.backendRecommend.warnings || [],
          dailyFrequency: this.backendRecommend.dailyFrequency,
          feedType: this.backendRecommend.feedType
        };
      }
      if (!this.selectedPond) return null;
      return this.getFeedRecommendationFor(this.selectedPond);
    },
    smartFeedDiffPercent() {
      if (!this.feedRecommendation || !this.feedRecommendation.dailyFeedAmount) return null;
      return ((this.smartFeedAmount - this.feedRecommendation.dailyFeedAmount) / this.feedRecommendation.dailyFeedAmount) * 100;
    },
    /** 按当前选中池品种切换核心注意事项 */
    corePrecautionLines() {
      const species = String((this.selectedPond && (this.selectedPond.fishSpecies || this.selectedPond.fishName || this.selectedPond.fishTypes)) || '').trim();
      const s = species.toLowerCase();
      // 大西洋鲑 / 三文鱼：冷水鱼
      if (/鲑|salmon|三文/i.test(species) || s.indexOf('salmon') >= 0) {
        return [
          '溶氧≥7 mg/L、氨氮≤0.1 mg/L、亚硝酸盐≤0.05 mg/L 方可正常投喂（鲑科对水质更敏感）',
          '水温 8–16℃ 为最佳摄食区间，＞18℃ 或＜6℃ 时应减喂或停喂',
          '严控残饵与脂肪沉积，宁少勿多；摄食减弱或游动异常时优先停料并排查溶氧',
        ];
      }
      // 鲈鱼苗 / 加州鲈等
      if (/鲈|bass|perch/i.test(species)) {
        return [
          '溶氧≥5 mg/L、氨氮≤0.2 mg/L、亚硝酸盐≤0.2 mg/L 方可正常投喂',
          '水温 22–28℃ 为最佳摄食区间，偏离时应酌情减喂或停喂',
          '严控残饵，宁少勿多；水质异常或鱼群摄食欲望差时优先停料',
        ];
      }
      // 罗非 / 草鱼等暖水鱼默认
      if (/罗非|草鱼|鲤|鲫|tilapia/i.test(species)) {
        return [
          '溶氧≥4 mg/L、氨氮≤0.5 mg/L、亚硝酸盐≤0.3 mg/L 方可正常投喂',
          '水温 24–30℃ 为最佳摄食区间，低温期应明显减喂',
          '严控残饵，宁少勿多；阴雨闷热或浮头前兆时优先停料增氧',
        ];
      }
      return [
        '溶氧≥5 mg/L、氨氮≤0.2 mg/L、亚硝酸盐≤0.2 mg/L 方可正常投喂',
        '按当前品种适宜水温区间投喂，偏离时应酌情减喂或停喂',
        '严控残饵，宁少勿多；水质异常或鱼群摄食欲望差时优先停料',
      ];
    },
  },
  methods: {
    async applyFeedingThresholds() {
      try {
        // 不用 force，避免阈值 BFF 短暂不可用时弹「系统异常」打断投喂页
        const feeding = await getThresholdModule('feeding', false)
        const dev = resolveFeedingDev(feeding)
        const fcr = resolveFeedingFcr(feeding)
        this.feedingThreshold = {
          feedDevMax: dev.value,
          feedDevEnabled: dev.enabled,
          fcrMin: fcr.min,
          fcrMax: fcr.max
        }
      } catch (e) {
        console.warn('[精准投喂] 阈值加载失败，使用默认投喂偏差/FCR', e && e.message)
      }
    },
    applyFeedDevStatus(pond) {
      if (!pond || !this.feedingThreshold.feedDevEnabled) return pond
      const recommended = Number(pond.recommendedFeedAmount) || 0
      const actual = Number(pond.feedAmount && pond.feedAmount.dailyGross) || 0
      if (recommended <= 0) return pond
      const pct = Math.abs((actual - recommended) / recommended) * 100
      pond.feedDevPct = Number(pct.toFixed(1))
      if (pct > this.feedingThreshold.feedDevMax) {
        if (pond.status === 'normal') pond.status = 'warning'
        if (pct > this.feedingThreshold.feedDevMax * 1.5) pond.status = 'alert'
      }
      const fcrActual = Number(pond.feedCoefficient && pond.feedCoefficient.actual)
      if (Number.isFinite(fcrActual) && fcrActual > this.feedingThreshold.fcrMax) {
        pond.status = pond.status === 'alert' ? 'alert' : 'warning'
      }
      return pond
    },
    formatFishSpec(value) {
      if (value == null || value === '') return '-';
      const text = String(value).trim();
      return /g$/i.test(text) ? text : `${text} g`;
    },
    mapHealthStatus(level) {
      if (level === '良好' || level === '优秀' || level === '正常') return 'normal';
      if (level === '关注' || level === '预警') return 'warning';
      if (level === '介入' || level === '警报' || level === '异常') return 'alert';
      if (level === 'normal' || level === 'warning' || level === 'alert') return level;
      return 'warning';
    },
    formatAffectTaskType(type) {
      if (type == null || type === '') return '-';
      const key = String(type);
      // 与任务模块一致：优先字典 zk_scheduling_type
      try {
        const dicts = (this.getDictDatas && this.getDictDatas(this.DICT_TYPE.ZK_SCHEDULING_TYPE)) || [];
        const hit = dicts.find(d => String(d.value) === key);
        if (hit && hit.label) return hit.label;
      } catch (e) { /* ignore */ }
      const map = {
        1: '上班', 2: '下班', 3: '巡查', 4: '自创建',
        split: '分池', disinfect: '消毒', medicine: '用药', maintain: '维护',
        stopFeed: '停食', sale: '销售', feed: '投喂', inspect: '巡检'
      };
      return map[type] || map[key] || key;
    },
    formatAffectTaskStatus(status) {
      if (status == null || status === '') return '-';
      const key = String(status);
      try {
        const dicts = (this.getDictDatas && this.getDictDatas(this.DICT_TYPE.ZK_TASK_STATUS)) || [];
        const hit = dicts.find(d => String(d.value) === key);
        if (hit && hit.label) return hit.label;
      } catch (e) { /* ignore */ }
      // 对齐 TaskStatusEnum
      const map = {
        1: '待接收', 2: '已接收待完成', 3: '已完成待评价', 4: '已评价',
        0: '未接收',
        draft: '草稿', pending: '待接收', running: '执行中', doing: '执行中',
        done: '已完成', completed: '已完成', overdue: '已逾期', cancelled: '已取消'
      };
      return map[status] || map[key] || key;
    },
    getFeedRecommendationFor(pond) {
      if (!pond) return null;
      const params = {
        effectiveCount: pond.fishCount || 0,
        averageWeight: parseWeightG(pond.averageWeight),
        temperature: (pond.waterQuality && pond.waterQuality.temperature) || 20,
        dissolvedOxygen: (pond.waterQuality && pond.waterQuality.dissolvedOxygen) || 8,
        ammonia: (pond.waterQuality && pond.waterQuality.ammonia) || 0.1,
        nitrite: (pond.waterQuality && pond.waterQuality.nitrite) || 0.1,
        feedingBehavior: 90,
        dailyFrequency: 2,
      };
      return calculateFeedRecommendation(params);
    },
    initSmartFeedAmount(pond) {
      const recommendation = this.feedRecommendation || this.getFeedRecommendationFor(pond);
      this.smartFeedAmount = recommendation
        ? Number(Number(recommendation.dailyFeedAmount).toFixed(2))
        : Number((pond && pond.feedAmount && pond.feedAmount.dailyGross) || 0);
      this.actualPlanTotal = this.smartFeedAmount;
      this.actualPlanFreq = (recommendation && recommendation.dailyFrequency) || 3;
      this.actualPlanFeedType = (recommendation && recommendation.feedType) || '';
    },
    resetSmartFeedAmount() {
      if (!this.selectedPond) return;
      this.initSmartFeedAmount(this.selectedPond);
    },
    async loadPondBiz(pondId) {
      if (pondId == null || pondId === '') return;
      const silent = { silent: true, hideErrorMessage: true };
      try {
        const results = await Promise.allSettled([
          getPrecisionFeedRecommend(pondId, silent),
          getPrecisionFeedPlans(pondId, undefined, silent),
          getPrecisionFeedAiAnalysis(pondId, silent),
          getPrecisionFeedAffectingTasks(pondId, silent)
        ]);
        const recRes = results[0].status === 'fulfilled' ? results[0].value : null;
        const planRes = results[1].status === 'fulfilled' ? results[1].value : null;
        const aiRes = results[2].status === 'fulfilled' ? results[2].value : null;
        const taskRes = results[3].status === 'fulfilled' ? results[3].value : null;

        this.backendRecommend = recRes && recRes.data ? recRes.data : null;
        const plans = (planRes && planRes.data) || [];
        this.feedPlanSmart = plans.find(p => p.planType === 'smart') || {};
        const actual = plans.find(p => p.planType === 'actual');
        if (actual) {
          this.actualPlanTotal = Number(actual.totalAmountKg || 0);
          this.actualPlanFreq = actual.frequency || 3;
          this.actualPlanFeedType = actual.feedType || '';
        }
        this.aiAnalysis = aiRes && aiRes.data ? aiRes.data : null;
        this.affectingTasks = (taskRes && taskRes.data) || [];
        this.initSmartFeedAmount(this.selectedPond);
        if (results.some(r => r.status === 'rejected')) {
          console.warn('[精准投喂] 部分详情接口失败，已降级本地推荐');
        }
      } catch (e) {
        console.error('loadPondBiz failed', e);
        this.backendRecommend = null;
        this.initSmartFeedAmount(this.selectedPond);
      }
      this.loadFeedConsumption();
    },
    async saveSmartPlan() {
      if (!this.selectedPond) return;
      try {
        await savePrecisionFeedPlan({
          pondId: this.selectedPond.id,
          planType: 'smart',
          totalAmountKg: this.smartFeedAmount,
          frequency: (this.feedRecommendation && this.feedRecommendation.dailyFrequency) || 3,
          feedType: this.feedRecommendation && this.feedRecommendation.feedType,
          status: 'draft'
        });
        this.$modal.msgSuccess('智能计划已保存');
        this.loadPondBiz(this.selectedPond.id);
      } catch (e) {
        this.$modal.msgError((e && e.message) || '保存智能计划失败');
      }
    },
    async saveActualPlan() {
      if (!this.selectedPond) return;
      try {
        await savePrecisionFeedPlan({
          pondId: this.selectedPond.id,
          planType: 'actual',
          totalAmountKg: this.actualPlanTotal,
          frequency: this.actualPlanFreq,
          singleAmountKg: this.actualPlanFreq ? Number((this.actualPlanTotal / this.actualPlanFreq).toFixed(3)) : this.actualPlanTotal,
          feedType: this.actualPlanFeedType,
          status: 'draft'
        });
        this.$modal.msgSuccess('实际计划已保存');
        this.loadPondBiz(this.selectedPond.id);
      } catch (e) {
        this.$modal.msgError((e && e.message) || '保存实际计划失败');
      }
    },
    async confirmActualPlan() {
      if (!this.selectedPond) return;
      try {
        await savePrecisionFeedPlan({
          pondId: this.selectedPond.id,
          planType: 'actual',
          totalAmountKg: this.actualPlanTotal,
          frequency: this.actualPlanFreq,
          singleAmountKg: this.actualPlanFreq ? Number((this.actualPlanTotal / this.actualPlanFreq).toFixed(3)) : this.actualPlanTotal,
          feedType: this.actualPlanFeedType,
          status: 'draft'
        });
        await confirmPrecisionFeedPlan(this.selectedPond.id, 'actual');
        this.$modal.msgSuccess('实际计划已确认实施');
        this.fetchData();
        this.loadPondBiz(this.selectedPond.id);
      } catch (e) {
        this.$modal.msgError((e && e.message) || '确认实施失败，请稍后重试');
      }
    },
    async loadFeedConsumption() {
      try {
        const params = {};
        if (this.selectedWorkshopId && this.selectedWorkshopId !== 'all') params.workshopId = this.selectedWorkshopId;
        if (this.selectedSystemId && this.selectedSystemId !== 'all') params.pondSystemId = this.selectedSystemId;
        if (this.selectedPond) params.pondId = this.selectedPond.id;
        const res = await getPrecisionFeedConsumption(params, { silent: true, hideErrorMessage: true });
        this.feedConsumption = (res && res.data) || { totalFeedKg: 0, pondCount: 0, items: [] };
      } catch (e) {
        console.error('loadFeedConsumption failed', e);
        this.feedConsumption = { totalFeedKg: 0, pondCount: 0, items: [] };
      }
    },
    async refreshNutritionList() {
      try {
        const res = await ZkGrowNutritionalRequirementsApi.getZkGrowNutritionalRequirementsPage({ pageNo: 1, pageSize: 100 });
        this.nutritionList = (res && res.data && res.data.list) || [];
      } catch (e) {
        console.error('Failed to fetch nutrition list', e);
        this.nutritionList = [];
      }
    },
    handleNutritionOpenForm(id) {
      const form = this.$refs.nutritionFormRef;
      if (form && typeof form.open === 'function') {
        form.open(id);
      } else {
        this.$modal.msgWarning('营养需求表单未就绪，请刷新后重试');
      }
    },
    async handleNutritionDelete(row) {
      const id = row.id;
      try {
        await this.$modal.confirm('是否确认删除各阶段营养需求编号为"' + id + '"的数据项?')
        await ZkGrowNutritionalRequirementsApi.deleteZkGrowNutritionalRequirements(id);
        await this.refreshNutritionList();
        this.$modal.msgSuccess("删除成功");
      } catch (e) { /* 取消或失败忽略 */ }
    },
    async loadSiteFilterOptions(force = false) {
      try {
        const siteOpts = await loadSharedSiteOptions({ includePonds: true, force: !!force });
        this.siteOptions = siteOpts;
        const items = pickWorkshopOptionItems(siteOpts);
        if (items.length) {
          this.workshops = items.map((o) => ({
            id: String(o.value),
            name: o.label,
            systems: pickSystemOptionItems(siteOpts, o.value).map((s) => ({
              id: String(s.value),
              name: s.label
            }))
          }));
        } else if (!this.workshops || !this.workshops.length) {
          // 生产记录暂无车间时，保留/回退到鱼池数据推导，避免下拉只剩「所有车间」
          this.rebuildWorkshopsFromPonds();
        }
        if (items.length && Array.isArray(this.allPonds) && this.allPonds.length) {
          this.allPonds = this.allPonds.filter((p) => isRowInUserSiteScope(p, this.siteOptions));
        }
        this.sanitizeSiteFilters();
      } catch (e) {
        console.warn('[precisionFeeding] site options', e);
        if (!this.workshops || !this.workshops.length) this.rebuildWorkshopsFromPonds();
      }
    },
    rebuildWorkshopsFromPonds() {
      const workshopMap = new Map();
      (this.allPonds || []).forEach((p) => {
        const wid = p.workshopId != null ? String(p.workshopId) : (p.workshopName || '');
        if (!wid) return;
        const key = String(wid);
        const label = p.workshopName || key;
        if (!workshopMap.has(key)) {
          workshopMap.set(key, { id: key, name: label, systems: [] });
        }
        const w = workshopMap.get(key);
        const sid = p.systemId != null ? String(p.systemId) : '';
        if (sid && !w.systems.find((s) => String(s.id) === sid)) {
          w.systems.push({ id: sid, name: p.systemName || sid });
        }
      });
      this.workshops = Array.from(workshopMap.values()).sort((a, b) =>
        String(a.name).localeCompare(String(b.name), 'zh-CN')
      );
    },
    sanitizeSiteFilters() {
      const wsIds = new Set((this.workshops || []).map((w) => String(w.id)));
      if (this.selectedWorkshopId && this.selectedWorkshopId !== 'all' && !wsIds.has(String(this.selectedWorkshopId))) {
        const byName = (this.workshops || []).find((w) => String(w.name) === String(this.selectedWorkshopId));
        if (byName) {
          this.selectedWorkshopId = String(byName.id);
        } else {
          this.selectedWorkshopId = 'all';
          this.selectedSystemId = 'all';
          this.filterPond = '';
          return;
        }
      }
      if (this.selectedSystemId && this.selectedSystemId !== 'all') {
        const sysOk = (this.currentSystems || []).some(
          (s) => String(s.id) === String(this.selectedSystemId) || String(s.name) === String(this.selectedSystemId)
        );
        if (!sysOk) {
          this.selectedSystemId = 'all';
          this.filterPond = '';
        }
      }
    },
    async fetchData() {
      if (this._fetchInflight) return this._fetchInflight;
      this.pageLoading = true;
      const run = this._fetchDataInner();
      this._fetchInflight = run;
      try {
        await run;
      } finally {
        if (this._fetchInflight === run) this._fetchInflight = null;
      }
    },
    async _fetchDataInner() {
      // 鱼池主档与筛选项并行；不再串行等 force 站点选项
      const sitePromise = this.loadSiteFilterOptions(false).catch(() => {});
      try {
        const pondsRes = await getPrecisionFeedingPonds(false).catch(e => {
          console.warn('ponds failed', e);
          return null;
        });

        let todayLogs = [];
        const applyPonds = (logs) => {
          if (!pondsRes || !pondsRes.data) return;
          // 投喂 SQL 可能因 alarm/品种 JOIN 产生重复行，按池 ID 去重后与健康等模块对齐
          const uniquePonds = [];
          const seen = new Set();
          pondsRes.data.forEach((p) => {
            const key = p && p.id != null ? String(p.id) : '';
            if (!key || seen.has(key)) return;
            seen.add(key);
            uniquePonds.push(p);
          });
          const ponds = uniquePonds.map(p => {
            const age = p.age || 120;
            const biomass = p.totalWeight || 0;
            const pondKey = p.id != null ? p.id : (p.pondDetailNum || p.pondName);

            const pondLogs = (logs || []).filter(log =>
              String(log.pondId) === String(p.id) ||
              String(log.pondDetailNum || '') === String(p.pondDetailNum || '')
            );
            const todayFeedings = pondLogs.map(log => {
              const date = new Date(log.feedTime);
              const timeStr = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
              return {
                time: timeStr,
                grossWeight: log.feedCount || 0,
                feedName: log.feedName,
                feedSpec: log.feedSpec
              };
            });

            const feedBySpecMap = {};
            todayFeedings.forEach(f => {
              const spec = f.feedSpec || '未知规格';
              if (!feedBySpecMap[spec]) feedBySpecMap[spec] = 0;
              feedBySpecMap[spec] += f.grossWeight;
            });
            const feedBySpec = Object.keys(feedBySpecMap).map(spec => ({
              spec,
              amount: feedBySpecMap[spec]
            }));
            if (feedBySpec.length === 0) {
              feedBySpec.push({ spec: '暂无数据', amount: 0 });
            }

            return {
              id: pondKey,
              name: p.pondName || p.pondDetailNum || String(pondKey || ''),
              pondDetailNum: p.pondDetailNum || p.pondName || String(pondKey || ''),
              batchNumber: p.batchNumber || p.relationOrderNo || p.batchNo || '',
              fishSpecLabel: p.avgSpecs || p.fishSpec || '',
              systemId: p.pondSystemId != null ? String(p.pondSystemId) : '',
              systemName: p.pondSystemName || '',
              workshopId: p.workshopId != null ? String(p.workshopId) : (p.workshopName || ''),
              workshopName: p.workshopName || '',
              fishSpecies: normalizeSpeciesName(p.fishTypeName || p.fishName || p.fishTypes) || '',
              fishCount: p.fishNum || 0,
              averageWeight: p.avgSpecs || p.fishSpec || 0,
              totalBiomass: biomass,
              age: age,
              growthStage: p.growthStage || getGrowthStage(age),
              feedCoefficient: {
                estimated: p.predictRadio || 1.1,
                actual: p.acturalRadio || 1.18
              },
              feedAmount: {
                totalGross: p.totalCount || 0,
                dailyGross: p.dayTotalCount || 0,
                yesterdayTotalCount: p.yesterdayTotalCount || 0,
                avgDailyFeed: p.avgDailyFeed ? Number(p.avgDailyFeed).toFixed(1) : 0,
                todayFeedings: todayFeedings
              },
              feedRate: biomass > 0 ? (((p.dayTotalCount || 0) / biomass) * 100).toFixed(2) : 0,
              feedBySpec: feedBySpec,
              feedSpecStats: p.feedSpecStats || [],
              feedReductionRecords: [],
              waterQuality: {
                temperature: p.temperature != null ? p.temperature : 25,
                ph: p.ph != null ? p.ph : 7.5,
                dissolvedOxygen: p.dissolvedOxygen != null ? p.dissolvedOxygen : 6,
                ammonia: p.ammonia != null ? p.ammonia : 0.05,
                nitrite: p.nitrite != null ? p.nitrite : 0.05,
                timestamp: new Date().toISOString()
              },
              recommendedFeedAmount: p.recommendFeedKg != null ? p.recommendFeedKg : calculateRecommendedFeed(biomass, age),
              recommendedFeedSpec: getRecommendedFeedSpec(age),
              capacity: p.waterVolume != null ? p.waterVolume : (p.pondScale || 100),
              avgDensity: p.avgDensity,
              todayDeathCount: p.todayDeathCount || 0,
              healthScore: p.healthScore,
              status: this.mapHealthStatus(p.healthLevel || p.pondStatus),
              pondStatus: p.healthLevel || (p.pondStatus == '正常' ? '良好' : p.pondStatus == '预警' ? '关注' : (p.pondStatus || '关注'))
            };
          }).map(p => this.applyFeedDevStatus(p));

          const seenPondIds = new Set();
          const hasSiteWs = this.siteOptions && pickWorkshopOptionItems(this.siteOptions).length > 0;
          this.allPonds = ponds.filter(p => {
            const hasFeedToday =
              (p.feedAmount && Number(p.feedAmount.dailyGross) > 0) ||
              (p.feedAmount && Array.isArray(p.feedAmount.todayFeedings) && p.feedAmount.todayFeedings.length > 0);
            if (hasSiteWs && !hasFeedToday && !isRowInUserSiteScope(p, this.siteOptions)) return false;
            const key = String(p.id != null && p.id !== '' ? p.id : (p.pondDetailNum || p.name || Math.random()));
            if (seenPondIds.has(key)) return false;
            seenPondIds.add(key);
            return true;
          });
          // 投喂偏差超阈值 → 关联规则推送 AI 报警 / 巡检任务
          this.allPonds.filter(p => p.feedDevPct != null && p.feedDevPct > this.feedingThreshold.feedDevMax).slice(0, 8).forEach(p => {
            evaluateAssociationAndPush({
              source: 'feeding',
              category: 'fish',
              pondDetailNum: p.pondDetailNum || p.name,
              pondName: p.name,
              workshopName: p.workshopName,
              pondSystemName: p.systemName,
              feedDevPct: p.feedDevPct,
              feedDevMax: this.feedingThreshold.feedDevMax,
              fcr: p.feedCoefficient && p.feedCoefficient.actual,
              title: `投喂偏差超阈值 ${p.pondDetailNum || p.name}`
            }).catch(silentCatch('精准投喂-关联报警'))
          });

          this.rebuildWorkshopsFromPonds();
          // 有生产记录时覆盖为同源筛选项（与任务/报警一致）
          if (this.siteOptions && pickWorkshopOptionItems(this.siteOptions).length) {
            this.workshops = pickWorkshopOptionItems(this.siteOptions).map((o) => ({
              id: String(o.value),
              name: o.label,
              systems: pickSystemOptionItems(this.siteOptions, o.value).map((s) => ({
                id: String(s.value),
                name: s.label
              }))
            }));
          }
          if (this.selectedWorkshopId && this.selectedWorkshopId !== 'all') {
            this.selectedWorkshopId = String(this.selectedWorkshopId);
          }
          if (this.selectedSystemId && this.selectedSystemId !== 'all') {
            this.selectedSystemId = String(this.selectedSystemId);
          }
          this.sanitizeSiteFilters();
          if (!this.selectedPond && this.filteredPonds.length) {
            this.selectPond(this.filteredPonds[0]);
          }
        };

        applyPonds(todayLogs);
        // 列表已可展示，结束全屏 loading
        this.pageLoading = false;

        // 筛选项晚到时再收窄一次（不挡首屏）
        sitePromise.then(() => {
          if (pondsRes && pondsRes.data) applyPonds(todayLogs);
        });

        getPrecisionFeedingStats().then(statsRes => {
          if (statsRes && statsRes.data) this.apiStats = statsRes.data;
        }).catch(e => console.warn('stats failed', e));

        getPrecisionFeedingTodayLogs().then(logsRes => {
          if (logsRes && logsRes.data) {
            todayLogs = logsRes.data;
            applyPonds(todayLogs);
          }
        }).catch(e => console.warn('logs failed', e));

        // 营养需求列表不挡首屏
        this.refreshNutritionList().catch(() => {});
      } catch (error) {
        console.error('Failed to fetch precision feeding data:', error);
        this.$message && this.$message.warning('精准投喂数据加载较慢或失败，请刷新重试');
        this.pageLoading = false;
      }
    },
    handleWorkshopChange() {
      this.selectedSystemId = 'all';
      this.filterPond = '';
      this.selectedPond = null;
    },
    handleSystemChange() {
      this.filterPond = '';
      this.selectedPond = null;
    },
    handlePondFilterChange() {
      this.selectedPond = null;
    },
    selectPond(pond) {
      this.selectedPond = pond;
      this.activeTab = 'feeding';
      this.loadPondBiz(pond.id);
      this.$nextTick(() => {
        this.renderCharts();
      });
    },
    handleTabClick() {
      this.$nextTick(() => {
        if (this.activeTab === 'feeding') {
          this.renderCharts();
        }
      });
    },
    getStatusColor(status) {
      return status === 'normal' ? '#4ade80' : status === 'warning' ? '#facc15' : '#f87171';
    },
    getStatusText(status) {
      return status === 'normal' ? '正常' : status === 'warning' ? '预警' : '警报';
    },
    getReductionTypeLabel(type) {
      const labels = { 'disease_treatment': '病害防治', 'health_product': '保健品投放', 'equipment_maintenance': '设备维修维护', 'water_quality': '水质异常处理', 'other': '其他' };
      return labels[type] || type;
    },
    renderCharts() {
      if (!this.selectedPond) return;
      try {
        Object.values(this.charts || {}).forEach(chart => chart && chart.dispose());
      } catch (e) { /* ignore */ }
      this.charts = {};

      const feedBySpec = (this.selectedPond.feedBySpec && this.selectedPond.feedBySpec.length)
        ? this.selectedPond.feedBySpec
        : [];
      const isEmptyFeedSpec = !feedBySpec.length || feedBySpec.every(f => !(Number(f.amount) > 0));
      const pieFeedBySpec = isEmptyFeedSpec
        ? [{ spec: '暂无数据', amount: 1 }] // 占位扇区，仅用于画出空态圆，不展示百分比
        : feedBySpec;
      const barFeedBySpec = isEmptyFeedSpec
        ? [{ spec: '暂无数据', amount: 0 }]
        : feedBySpec;

      const commonOptions = {
        textStyle: { color: '#9ca3af' },
        tooltip: {
          backgroundColor: '#1f2937',
          borderColor: '#374151',
          textStyle: { color: '#e5e7eb' }
        },
        legend: { textStyle: { color: '#d1d5db' } },
        xAxis: {
          axisLine: { lineStyle: { color: '#374151' } },
          axisLabel: { color: '#9ca3af' }
        },
        yAxis: {
          splitLine: { lineStyle: { color: '#374151', type: 'dashed' } },
          axisLabel: { color: '#9ca3af' }
        }
      };

      if (this.activeTab === 'feeding') {
        if (this.$refs.barChartRef) {
          const barChart = echarts.init(this.$refs.barChartRef);
          barChart.setOption({
            ...commonOptions,
            tooltip: { ...commonOptions.tooltip, trigger: 'axis' },
            legend: { show: true, bottom: 0, data: ['投喂量'] },
            xAxis: { ...commonOptions.xAxis, type: 'category', data: barFeedBySpec.map(f => f.spec) },
            yAxis: { ...commonOptions.yAxis, type: 'value' },
            series: [{ name: '投喂量', data: barFeedBySpec.map(f => f.amount), type: 'bar', itemStyle: { color: '#60a5fa' }, barWidth: '40%' }]
          });
          this.charts.barChart = barChart;
        }

        if (this.$refs.pieChartRef) {
          const pieChart = echarts.init(this.$refs.pieChartRef);
          const pieLabelFormatter = (params) => {
            if (isEmptyFeedSpec || params.name === '暂无数据') return '暂无数据';
            const pct = params.percent;
            if (pct == null || Number.isNaN(Number(pct))) return params.name;
            return `${params.name}: ${pct}%`;
          };
          pieChart.setOption({
            tooltip: {
              ...commonOptions.tooltip,
              trigger: 'item',
              formatter: pieLabelFormatter
            },
            series: [{
              type: 'pie',
              radius: '70%',
              data: pieFeedBySpec.map((f, index) => ({
                value: f.amount,
                name: f.spec,
                itemStyle: {
                  color: isEmptyFeedSpec
                    ? '#374151'
                    : (index === 0 ? '#60a5fa' : '#4ade80')
                }
              })),
              emphasis: isEmptyFeedSpec
                ? { disabled: true }
                : { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
              label: {
                color: isEmptyFeedSpec ? '#9ca3af' : '#60a5fa',
                formatter: pieLabelFormatter
              }
            }]
          });
          this.charts.pieChart = pieChart;
        }
      }
    }
  },
  beforeDestroy() {
    if (typeof this._offProductionRecord === 'function') {
      this._offProductionRecord();
      this._offProductionRecord = null;
    }
    if (typeof window !== 'undefined' && this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    Object.values(this.charts).forEach(chart => chart && chart.dispose());
  }
};
</script>

<style scoped>
.app-container {
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* Dark Theme Colors */
.bg-dark-main { background-color: #0b1120; }
.bg-dark-card { background-color: #111827; }
.dark-inner-card { background-color: #1f2937; border-color: #374151; }
.border-b { border-bottom: 1px solid #374151; }
.shadow-sm { box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.5); }
.text-white { color: #ffffff; }
.text-gray-200 { color: #e5e7eb; }
.text-gray-300 { color: #d1d5db; }
.text-gray-400 { color: #9ca3af; }
.text-gray-500 { color: #6b7280; }
.text-blue-400 { color: #60a5fa; }
.text-green-400 { color: #4ade80; }
.text-yellow-400 { color: #facc15; }
.text-red-400 { color: #f87171; }
.text-orange-400 { color: #fb923c; }
.text-purple-400 { color: #c084fc; }

/* Background Opacities for Alerts/Cards */
.bg-opacity-20 { background-opacity: 0.2; }
.bg-blue-900 { background-color: rgba(30, 58, 138, 0.2); }
.bg-green-900 { background-color: rgba(20, 83, 45, 0.2); }
.bg-yellow-900 { background-color: rgba(113, 63, 18, 0.2); }
.bg-orange-900 { background-color: rgba(124, 45, 18, 0.2); }
.bg-red-900 { background-color: rgba(127, 29, 29, 0.2); }
.bg-purple-900 { background-color: rgba(88, 28, 135, 0.2); }
.bg-gray-800 { background-color: #1f2937; }

/* Border Colors */
.border { border: 1px solid #374151; }
.border-t { border-top: 1px solid #374151; }
.border-blue-800 { border-color: #1e40af; }
.border-green-800 { border-color: #166534; }
.border-yellow-800 { border-color: #854d0e; }
.border-orange-800 { border-color: #9a3412; }
.border-red-800 { border-color: #991b1b; }
.border-radius-4 { border-radius: 0.5rem; }
.border-purple-800 { border-color: #6b21a8; }
.border-gray-700 { border-color: #374151; }

/* Layout & Typography */
.min-h-screen { min-height: 100vh; }
.container { max-width: 100%; margin: 0 auto; }
.py-2 { padding-top: 0.5rem; padding-bottom: 0.5rem; }
.px-2 { padding-left: 0.5rem; padding-right: 0.5rem; }
.px-4 { padding-left: 1rem; padding-right: 1rem; }
.py-4 { padding-top: 1rem; padding-bottom: 1rem; }
.py-6 { padding-top: 1.5rem; padding-bottom: 1.5rem; }
.mb-2 { margin-bottom: 0.5rem; }
.mb-4 { margin-bottom: 1rem; }
.mb-6 { margin-bottom: 1.5rem; }
.text-3xl { font-size: 1.875rem; line-height: 2.25rem; }
.text-2xl { font-size: 1.5rem; line-height: 2rem; }
.text-lg { font-size: 1.125rem; line-height: 1.75rem; }
.text-sm { font-size: 0.875rem; line-height: 1.25rem; }
.text-xs { font-size: 0.75rem; line-height: 1rem; }
.font-bold { font-weight: 700; }
.font-medium { font-weight: 500; }
.font-size-12 { font-size: 0.75rem; }
.font-size-14 { font-size: 0.875rem; }
.flex { display: flex; }
.flex-1 { flex: 1 1 0%; }
.flex-wrap { flex-wrap: wrap; }
.items-center { align-items: center; }
.items-end { align-items: flex-end; }
.items-start { align-items: flex-start; }
.justify-between { justify-content: space-between; }
.justify-center { justify-content: center; }
.gap-2 { gap: 0.5rem; }
.gap-3 { gap: 0.75rem; }
.gap-4 { gap: 1rem; }
.grid { display: grid; }
.grid-cols-3 { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.grid-cols-4 { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.p-3 { padding: 0.75rem; }
.p-4 { padding: 1rem; }
.pt-3 { padding-top: 0.75rem; }
.mt-1 { margin-top: 0.25rem; }
.mt-2 { margin-top: 0.5rem; }
.mt-3 { margin-top: 0.75rem; }
.mt-4 { margin-top: 1rem; }
.ml-2 { margin-left: 0.5rem; }
.mx-auto { margin: 0 auto; }
.rounded-lg { border-radius: 0.5rem; }
.rounded-full { border-radius: 9999px; }
.w-2 { width: 0.5rem; }
.h-2 { height: 0.5rem; }
.w-24 { width: 6rem; }
.space-y-1 > * + * { margin-top: 0.25rem; }
.space-y-3 > * + * { margin-top: 0.75rem; }
.space-y-4 > * + * { margin-top: 1rem; }
.text-center { text-align: center; }
.text-right { text-align: right; }
.py-8 { padding-top: 2rem; padding-bottom: 2rem; }
.text-6xl { font-size: 3.75rem; line-height: 1; }
.padding-left-2 { padding: 0 0.5rem; }

/* Element UI Overrides for Dark Theme */
::v-deep .dark-card {
  background-color: #111827;
  border-color: #374151;
  color: #e5e7eb;
}
::v-deep .dark-card .el-card__header {
  border-bottom-color: #374151;
}
::v-deep .dark-inner-card {
  background-color: #1f2937;
  border-color: #374151;
  color: #e5e7eb;
}
::v-deep .dark-inner-card .el-card__header {
  border-bottom-color: #374151;
}

::v-deep .dark-input .el-input__inner,
::v-deep .dark-select .el-input__inner {
  background-color: #1f2937;
  border-color: #374151;
  color: #e5e7eb;
}
::v-deep .dark-input .el-input__inner:focus,
::v-deep .dark-select .el-input__inner:focus {
  border-color: #60a5fa;
}

::v-deep .dark-table {
  background-color: transparent !important;
  color: #e5e7eb;
}
::v-deep .dark-table th,
::v-deep .dark-table tr {
  background-color: #1f2937 !important;
  color: #d1d5db;
  border-color: #374151 !important;
}
::v-deep .dark-table td,
::v-deep .dark-table th.is-leaf {
  border-bottom-color: #374151 !important;
}
::v-deep .dark-table--border,
::v-deep .dark-table--group {
  border-color: #374151 !important;
}
::v-deep .dark-table::before,
::v-deep .dark-table::after {
  background-color: #374151 !important;
}
::v-deep .dark-table .el-table__empty-block {
  background-color: #1f2937;
}
::v-deep .dark-table .el-table__empty-text {
  color: #9ca3af;
}
::v-deep .el-table--enable-row-hover .el-table__body tr:hover>td {
  background-color: #374151 !important;
}

::v-deep .dark-tabs .el-tabs__item {
  color: #9ca3af;
}
::v-deep .dark-tabs .el-tabs__item.is-active {
  color: #60a5fa;
}
::v-deep .dark-tabs .el-tabs__nav-wrap::after {
  background-color: #374151;
}

/* Custom Scrollbar for dark theme */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #111827;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #374151;
  border-radius: 3px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #4b5563;
}

/* Fish Pond Grid */
.pond-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}
.pond-grid::-webkit-scrollbar {
  width: 6px;
}
.pond-grid::-webkit-scrollbar-track {
  background: #111827;
}
.pond-grid::-webkit-scrollbar-thumb {
  background: #374151;
  border-radius: 3px;
}
.pond-item {
  cursor: pointer;
  transition: transform 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.pond-item:hover {
  transform: scale(1.05);
}
.visual-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.visual-wrapper svg {
  border-radius: 50%;
}
.visual-wrapper.is-selected svg {
  box-shadow: 0 0 0 4px #60a5fa;
}

/* 修改二: Alert 暗色主题适配 */
::v-deep .alert-dark.el-alert {
  background-color: #1f2937;
  border: 1px solid #374151;
  color: #e5e7eb;
}
::v-deep .alert-dark.el-alert .el-alert__title {
  color: #e5e7eb;
}
::v-deep .alert-dark.el-alert .el-alert__description,
::v-deep .alert-dark.el-alert .el-alert__content {
  color: #d1d5db;
}
::v-deep .alert-warning-dark.el-alert {
  background-color: rgba(120, 80, 20, 0.25);
  border-color: #854d0e;
}
::v-deep .alert-warning-dark.el-alert .el-alert__icon,
::v-deep .alert-warning-dark.el-alert .el-alert__title {
  color: #fbbf24;
}
::v-deep .alert-info-dark.el-alert {
  background-color: rgba(30, 64, 175, 0.25);
  border-color: #2563eb;
}
::v-deep .alert-info-dark.el-alert .el-alert__icon,
::v-deep .alert-info-dark.el-alert .el-alert__title {
  color: #60a5fa;
}

/* 修改四: 表格操作按钮对齐 */
::v-deep .nutrition-action-col .cell {
  display: flex;
  justify-content: center;
  align-items: center;
}
.nutrition-action-btns {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  width: 100%;
}
.nutrition-action-btns .el-button--mini {
  padding: 4px 8px;
  font-size: 12px;
  min-width: 48px;
}

/* 修改五: 对话框分割线间距 */
::v-deep .el-dialog__header {
  margin-right: 0;
  padding: 16px 20px 12px;
}
::v-deep .el-dialog__body {
  padding: 18px 20px 10px;
}
::v-deep .el-dialog__headerbtn {
  top: 14px;
  right: 18px;
}

/* 修改六: 投喂计划框间距 */
.feed-plan-card {
  padding: 4px 0;
}
::v-deep .feed-plan-card .el-card__body {
  padding: 18px 20px;
}
.feed-plan-row .feed-plan-col {
  padding: 4px 8px;
}
.feed-plan-recommend {
  margin-top: 12px;
}

/* 修改七: 饲料消耗卡片刷新按钮 */
::v-deep .feed-consumption-card .el-button--small {
  padding: 8px 14px;
  font-size: 13px;
}
</style>
