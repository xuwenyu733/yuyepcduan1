<template>
  <div class="app-container bg-dark-main min-h-screen text-gray-200">
    <!-- Header -->
    <div class="header-section bg-dark-card border-b shadow-sm mb-6">
      <div class="container mx-auto px-4 py-6">
        <h1 class="text-3xl mb-4 font-bold text-white">工厂化循环水鱼类精准投喂系统</h1>
        <div class="flex gap-4">
          <el-select v-model="selectedWorkshopId" @change="handleWorkshopChange" placeholder="选择车间" size="medium" class="dark-select">
            <el-option v-for="w in workshopSelectList" :key="w.id" :label="w.name" :value="w.id"></el-option>
          </el-select>
          <el-select v-model="selectedSystemId" @change="handleSystemChange" placeholder="选择系统" size="medium" class="dark-select">
            <el-option label="所有系统" value="all"></el-option>
            <el-option v-for="s in currentSystems" :key="s.id" :label="s.name" :value="s.id"></el-option>
          </el-select>
        </div>
      </div>
    </div>

    <div class="container mx-auto px-4">
      <!-- Stats Cards -->
      <el-row :gutter="16" class="mb-6">
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">鱼池总数</div>
            <div class="text-2xl font-bold text-white">{{ stats.totalPonds }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">总生物量</div>
            <div class="text-2xl font-bold text-white">{{ stats.totalBiomass.toFixed(0) }} kg</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">今日投喂量</div>
            <div class="text-2xl font-bold text-white">{{ stats.totalFeedToday.toFixed(1) }} kg</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">正常</div>
            <div class="text-2xl font-bold text-green-400">{{ stats.normalCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">预警</div>
            <div class="text-2xl font-bold text-yellow-400">{{ stats.warningCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card dark-card">
            <div class="text-sm text-gray-400">警报</div>
            <div class="text-2xl font-bold text-red-400">{{ stats.alertCount }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Search and Filter -->
      <el-card shadow="hover" class="mb-6 dark-card">
        <div class="flex gap-4 items-center flex-wrap">
          <el-input v-model="searchTerm" placeholder="搜索鱼池名称、品种或系统..." prefix-icon="el-icon-search" style="width: 300px;" size="small" class="dark-input"></el-input>
          
          <el-select v-model="filters.status" multiple placeholder="状态筛选" style="width: 200px;" size="small" class="dark-select">
            <el-option label="正常" value="normal"></el-option>
            <el-option label="预警" value="warning"></el-option>
            <el-option label="警报" value="alert"></el-option>
          </el-select>

          <el-select v-model="filters.fishSpecies" multiple placeholder="品种筛选" style="width: 200px;" size="small" class="dark-select">
            <el-option v-for="s in availableSpecies" :key="s" :label="s" :value="s"></el-option>
          </el-select>

          <el-select v-model="filters.growthStage" multiple placeholder="发育阶段筛选" style="width: 200px;" size="small" class="dark-select">
            <el-option v-for="s in availableStages" :key="s" :label="s" :value="s"></el-option>
          </el-select>
        </div>
      </el-card>

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
              <div v-for="pond in filteredPonds" :key="pond.id" class="pond-item" @click="selectPond(pond)">
                <div class="visual-wrapper" :class="{'is-selected': selectedPond && selectedPond.id === pond.id}">
                  <svg width="150" height="150" viewBox="0 0 200 200">
                    <circle cx="100" cy="100" r="95" fill="none" :stroke="getStatusColor(pond.status)" :stroke-width="selectedPond && selectedPond.id === pond.id ? '6' : '4'" />
                    <circle cx="100" cy="100" r="88" fill="#1f2937" stroke="#374151" stroke-width="2" />
                    <defs>
                      <clipPath :id="'waterClip-' + pond.id">
                        <circle cx="100" cy="100" r="86" />
                      </clipPath>
                      <linearGradient :id="'waterGradient-' + pond.id" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" style="stop-color: #3b82f6; stop-opacity: 0.6" />
                        <stop offset="100%" style="stop-color: #1e3a8a; stop-opacity: 0.8" />
                      </linearGradient>
                    </defs>
                    <rect x="14" :y="14 + (172 * 0.15)" width="172" :height="172 * 0.85" :fill="'url(#waterGradient-' + pond.id + ')'" :clip-path="'url(#waterClip-' + pond.id + ')'" />
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
                      <div class="text-sm text-gray-400">所属车间</div>
                      <div class="font-medium">{{ selectedPond.workshopName }}</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">所属系统</div>
                      <div class="font-medium">{{ selectedPond.systemName }}</div>
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
                      <div class="font-medium">{{ selectedPond.averageWeight }} g</div>
                    </el-col>
                    <el-col :span="8" class="mb-4">
                      <div class="text-sm text-gray-400">总生物量</div>
                      <div class="font-medium">{{ selectedPond.totalBiomass }} kg</div>
                    </el-col>
                    <el-col :span="8">
                      <div class="text-sm text-gray-400">养殖天数</div>
                      <div class="font-medium">{{ selectedPond.age }} 天</div>
                    </el-col>
                    <el-col :span="8">
                      <div class="text-sm text-gray-400">发育阶段</div>
                      <el-tag size="small" type="info" effect="dark">{{ selectedPond.growthStage }}</el-tag>
                    </el-col>
                    <el-col :span="8">
                      <div class="text-sm text-gray-400">鱼池容量</div>
                      <div class="font-medium">{{ selectedPond.capacity }} m³</div>
                    </el-col>
                  </el-row>
                </el-card>

                <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="dark-tabs">
                  <!-- 投喂信息 -->
                  <el-tab-pane label="投喂信息" name="feeding">
                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white"><i class="el-icon-s-data text-blue-400"></i> 饵料系数</div>
                      <el-row>
                        <el-col :span="12">
                          <div class="text-sm text-gray-400 mb-1">预估饵料系数</div>
                          <div class="text-3xl font-bold text-blue-400">{{ selectedPond.feedCoefficient.estimated }}</div>
                        </el-col>
                        <el-col :span="12">
                          <div class="text-sm text-gray-400 mb-1">实际饵料系数</div>
                          <div class="text-3xl font-bold text-green-400">{{ selectedPond.feedCoefficient.actual }}</div>
                        </el-col>
                      </el-row>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white"><i class="el-icon-pie-chart text-blue-400"></i> 投喂统计</div>
                      <el-row :gutter="20" class="mb-4">
                        <el-col :span="6">
                          <div class="text-sm text-gray-400">总饲料净重</div>
                          <div class="text-2xl font-bold text-blue-400">{{ selectedPond.feedAmount.totalNet }} kg</div>
                          <div class="text-xs text-gray-500 mt-1">纯饲料重量</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="text-sm text-gray-400">总投喂总重</div>
                          <div class="text-2xl font-bold text-green-400">{{ selectedPond.feedAmount.totalGross }} kg</div>
                          <div class="text-xs text-gray-500 mt-1">含药液重量</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="text-sm text-gray-400">日饲料净重</div>
                          <div class="text-2xl font-bold text-blue-400">{{ selectedPond.feedAmount.dailyNet }} kg</div>
                          <div class="text-xs text-gray-500 mt-1">纯饲料重量</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="text-sm text-gray-400">日投喂总重</div>
                          <div class="text-2xl font-bold text-green-400">{{ selectedPond.feedAmount.dailyGross }} kg</div>
                          <div class="text-xs text-gray-500 mt-1">含药液重量</div>
                        </el-col>
                      </el-row>
                      
                      <el-row :gutter="20" class="mb-4">
                        <el-col :span="12">
                          <div class="p-4 bg-orange-900 bg-opacity-20 border border-orange-800 rounded-lg">
                            <div class="text-sm text-gray-300">今日药液用量</div>
                            <div class="text-2xl font-bold text-orange-400">{{ selectedPond.feedAmount.medicineWeight }} kg</div>
                          </div>
                        </el-col>
                        <el-col :span="12">
                          <div class="p-4 bg-purple-900 bg-opacity-20 border border-purple-800 rounded-lg">
                            <div class="text-sm text-gray-300">投饵率</div>
                            <div class="text-2xl font-bold text-purple-400">{{ selectedPond.feedRate }}%</div>
                          </div>
                        </el-col>
                      </el-row>

                      <div class="mt-4">
                        <h4 class="mb-3 font-bold text-white">今日投喂时间线（详细组成）</h4>
                        <div ref="lineChartRef" style="width: 100%; height: 250px;"></div>
                        
                        <div class="mt-4 p-3 bg-gray-800 rounded-lg border border-gray-700">
                          <h5 class="text-sm font-medium mb-2 text-white">今日投喂明细</h5>
                          <el-table :data="selectedPond.feedAmount.todayFeedings" size="small" border class="dark-table">
                            <el-table-column prop="time" label="时间"></el-table-column>
                            <el-table-column prop="netWeight" label="饲料净重(kg)" align="right"></el-table-column>
                            <el-table-column prop="medicineWeight" label="药液重量(kg)" align="right"></el-table-column>
                            <el-table-column prop="grossWeight" label="总重(kg)" align="right"></el-table-column>
                          </el-table>
                        </div>
                      </div>
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

                    <!-- 停料减料信息 -->
                    <el-card shadow="never" class="mb-4 dark-inner-card" v-if="selectedPond.feedReductionRecords.length > 0">
                      <div slot="header" class="font-bold text-red-400"><i class="el-icon-warning"></i> 停料减料记录</div>
                      <div class="space-y-3">
                        <div v-for="record in selectedPond.feedReductionRecords" :key="record.id" 
                             class="p-4 border rounded-lg"
                             :class="{'bg-yellow-900 bg-opacity-20 border-yellow-700': record.status === 'active', 'bg-gray-800 border-gray-700': record.status === 'completed', 'bg-red-900 bg-opacity-20 border-red-800': record.status === 'cancelled'}">
                          <div class="flex items-start justify-between mb-3">
                            <div class="flex-1">
                              <div class="flex items-center gap-2 mb-1">
                                <el-tag :type="record.status === 'active' ? 'primary' : record.status === 'completed' ? 'info' : 'danger'" size="small" effect="dark">
                                  {{ record.status === 'active' ? '进行中' : record.status === 'completed' ? '已完成' : '已取消' }}
                                </el-tag>
                                <el-tag type="info" size="small" effect="dark">{{ getReductionTypeLabel(record.type) }}</el-tag>
                              </div>
                              <h4 class="font-medium text-lg text-white">{{ record.reason }}</h4>
                            </div>
                            <div class="text-right">
                              <div class="text-2xl font-bold text-orange-400">
                                {{ record.reductionRate === 0 ? '停料' : `${record.reductionRate}%` }}
                              </div>
                              <div class="text-xs text-gray-400">
                                {{ record.reductionRate === 0 ? '完全停止投喂' : '投喂比例' }}
                              </div>
                            </div>
                          </div>
                          <div class="grid grid-cols-3 gap-3 text-sm">
                            <div><span class="text-gray-400">开始日期：</span><span class="font-medium">{{ record.startDate }}</span></div>
                            <div><span class="text-gray-400">{{ record.actualEndDate ? '实际结束' : '预计结束' }}：</span><span class="font-medium">{{ record.actualEndDate || record.endDate }}</span></div>
                            <div><span class="text-gray-400">操作人：</span><span class="font-medium">{{ record.operator }}</span></div>
                          </div>
                          <div v-if="record.remarks" class="mt-3 pt-3 border-t border-gray-700 text-sm text-gray-300">
                            {{ record.remarks }}
                          </div>
                        </div>
                      </div>
                    </el-card>

                  </el-tab-pane>

                  <!-- 水质监测 -->
                  <el-tab-pane label="水质监测" name="water">
                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="flex justify-between items-center font-bold text-white">
                        <span><i class="el-icon-odometer text-blue-400"></i> 水质指标监测</span>
                        <span class="text-sm text-gray-400 font-normal">最后更新: {{ new Date(selectedPond.waterQuality.timestamp).toLocaleString('zh-CN') }}</span>
                      </div>
                      <div class="space-y-4">
                        <div v-for="(item, index) in waterQualityData" :key="index" class="flex items-center justify-between p-4 border border-gray-700 rounded-lg mb-2 bg-gray-800">
                          <div class="flex items-center gap-4 flex-1">
                            <div class="w-24">
                              <div class="font-medium text-gray-200">{{ item.指标 }}</div>
                              <div class="text-xs text-gray-400">{{ item.标准范围 }}</div>
                            </div>
                            <div class="flex-1">
                              <div class="text-2xl font-bold text-white">
                                {{ item.当前值 }} <span class="text-sm font-normal text-gray-400">{{ item.单位 }}</span>
                              </div>
                            </div>
                          </div>
                          <el-tag :type="item.status === 'normal' ? 'success' : item.status === 'warning' ? 'warning' : 'danger'" effect="dark">
                            {{ item.status === 'normal' ? '正常' : item.status === 'warning' ? '预警' : '警报' }}
                          </el-tag>
                        </div>
                      </div>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white"><i class="el-icon-data-line text-blue-400"></i> 关键指标趋势</div>
                      <div ref="waterChartRef" style="width: 100%; height: 300px;"></div>
                    </el-card>
                  </el-tab-pane>

                  <!-- 智能推荐 -->
                  <el-tab-pane label="智能推荐" name="recommendation">
                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white"><i class="el-icon-s-opportunity text-blue-400"></i> 智能投喂推荐</div>
                      <el-row :gutter="20" class="mb-4">
                        <el-col :span="12">
                          <div class="p-4 bg-blue-900 bg-opacity-20 border border-blue-800 rounded-lg">
                            <div class="text-sm text-gray-300 mb-1">推荐日投喂量</div>
                            <div class="text-3xl font-bold text-blue-400">{{ selectedPond.recommendedFeedAmount.toFixed(1) }} kg</div>
                            <div class="text-xs text-gray-400 mt-2">基于当前生物量 {{ selectedPond.totalBiomass }} kg</div>
                          </div>
                        </el-col>
                        <el-col :span="12">
                          <div class="p-4 bg-green-900 bg-opacity-20 border border-green-800 rounded-lg">
                            <div class="text-sm text-gray-300 mb-1">推荐饲料规格</div>
                            <div class="text-3xl font-bold text-green-400">{{ selectedPond.recommendedFeedSpec }}</div>
                            <div class="text-xs text-gray-400 mt-2">适用于当前发育阶段</div>
                          </div>
                        </el-col>
                      </el-row>
                      
                      <div class="mb-4">
                        <h4 class="mb-3 font-bold text-white">当前发育阶段营养需求</h4>
                        <div v-if="currentStage" class="grid grid-cols-4 gap-4">
                          <div class="p-3 border border-gray-700 rounded-lg bg-gray-800">
                            <div class="text-sm text-gray-400">发育阶段</div>
                            <div class="font-medium text-gray-200">{{ currentStage.stage }}</div>
                          </div>
                          <div class="p-3 border border-gray-700 rounded-lg bg-gray-800">
                            <div class="text-sm text-gray-400">天数范围</div>
                            <div class="font-medium text-gray-200">{{ currentStage.daysRange[0] }}-{{ currentStage.daysRange[1] }} 天</div>
                          </div>
                          <div class="p-3 border border-gray-700 rounded-lg bg-gray-800">
                            <div class="text-sm text-gray-400">蛋白质需求</div>
                            <div class="font-medium text-gray-200">{{ currentStage.proteinRequirement }}%</div>
                          </div>
                          <div class="p-3 border border-gray-700 rounded-lg bg-gray-800">
                            <div class="text-sm text-gray-400">脂肪需求</div>
                            <div class="font-medium text-gray-200">{{ currentStage.fatRequirement }}%</div>
                          </div>
                        </div>
                      </div>

                      <div class="p-4 bg-yellow-900 bg-opacity-20 border border-yellow-800 rounded-lg mb-4">
                        <h4 class="font-medium mb-2 text-yellow-400">投喂建议</h4>
                        <ul class="text-sm space-y-1 text-gray-300">
                          <li>• 建议每日投喂 3-4 次，间隔均匀</li>
                          <li>• 根据鱼类摄食情况调整投喂量，避免过度投喂</li>
                          <li>• 当前实际饵料系数({{ selectedPond.feedCoefficient.actual }})略高于预估值({{ selectedPond.feedCoefficient.estimated }})，建议优化投喂策略</li>
                          <li v-if="selectedPond.waterQuality.dissolvedOxygen < 7.5" class="text-orange-400">• 溶氧偏低，建议增加增氧设备运行时间</li>
                          <li v-if="selectedPond.waterQuality.ammonia > 0.2" class="text-orange-400">• 氨氮偏高，建议加强水体循环过滤</li>
                        </ul>
                      </div>
                    </el-card>

                    <el-card shadow="never" class="mb-4 dark-inner-card">
                      <div slot="header" class="font-bold text-white">发育阶段对照表</div>
                      <el-table :data="GROWTH_STAGES" size="small" border class="dark-table">
                        <el-table-column label="发育阶段">
                          <template slot-scope="scope">
                            {{ scope.row.stage }}
                            <el-tag v-if="scope.row.stage === selectedPond.growthStage" size="mini" type="primary" effect="dark" class="ml-2">当前</el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column label="天数范围">
                          <template slot-scope="scope">{{ scope.row.daysRange[0] }}-{{ scope.row.daysRange[1] }} 天</template>
                        </el-table-column>
                        <el-table-column prop="proteinRequirement" label="蛋白质需求(%)"></el-table-column>
                        <el-table-column prop="fatRequirement" label="脂肪需求(%)"></el-table-column>
                        <el-table-column prop="recommendedFeedRate" label="推荐投饵率(%)"></el-table-column>
                      </el-table>
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
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getZkPondPage } from '@/api/fish/zkPond';
import { getZkFeedingLogPage } from '@/api/fish/zkFeedingLog';
import { getZkPondHealthPage } from '@/api/fish/zkpondhealth/index';
import { getPrecisionFeedingPonds } from '@/api/fish/zkBiologyCapital';
import { fetchOverviewMonitor } from '@/utils/dashboardLiveBridge';
import { getFishHealthSnapshot } from '@/api/fish/daPing';
import {
  loadSharedSiteOptions,
  pickWorkshopOptionItems,
  pickSystemOptionItems,
  workshopsEqualLoose,
  systemsEqualLoose
} from '@/utils/siteOptions';
import {
  normalizeSpeciesName,
  speciesNamesEqual,
  collectSpeciesFilterOptions
} from '@/utils/speciesTempBridge';

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

function buildPondFromDb(p, wq, workshopId, workshopName, systemId, systemName) {
  const fishCount = p.fishNum != null ? Number(p.fishNum) : 0;
  const avgWeight = p.fishSpec != null ? Number(p.fishSpec) : 0;
  const biomass = p.totalWeight != null ? Number(p.totalWeight) : (fishCount * avgWeight / 1000);
  return {
    id: String(p.id),
    name: p.pondName || p.pondDetailNum || ('池' + p.id),
    systemId,
    systemName,
    workshopId,
    workshopName,
    fishSpecies: normalizeSpeciesName(p.fishTypeName || p.fishName || p.fishTypes) || '',
    batchNumber: p.batchNumber || '',
    fishCount,
    averageWeight: avgWeight,
    totalBiomass: biomass,
    age: 0,
    growthStage: getGrowthStage(0),
    feedCoefficient: { estimated: 0, actual: 0 },
    feedAmount: {
      totalNet: p.accumulateFeedCount != null ? Number(p.accumulateFeedCount) : 0,
      totalGross: 0,
      dailyNet: p.currFeedCount != null ? Number(p.currFeedCount) : 0,
      dailyGross: 0,
      medicineWeight: 0,
      todayFeedings: []
    },
    feedRate: 0,
    feedBySpec: [],
    feedReductionRecords: [],
    waterQuality: wq,
    recommendedFeedAmount: calculateRecommendedFeed(biomass, 0),
    recommendedFeedSpec: getRecommendedFeedSpec(0),
    capacity: p.pondScale ? parseFloat(p.pondScale) || 0 : 0,
    diameter: 0,
    depth: 0,
    status: 'normal'
  };
}

function enrichPondsWithSpeciesMaster(ponds, masterList) {
  const byId = {};
  (masterList || []).forEach((p) => {
    if (!p || p.id == null) return;
    byId[String(p.id)] = p;
  });
  (ponds || []).forEach((pond) => {
    const m = byId[String(pond.id)];
    if (!m) return;
    const name = normalizeSpeciesName(m.fishTypeName || m.fishName || m.fishTypes);
    if (name) pond.fishSpecies = name;
    if (m.batchNumber && !pond.batchNumber) pond.batchNumber = m.batchNumber;
  });
}

function enrichPondsWithFeedingAndHealth(ponds, feedLogs, healthRows) {
  const feedsByPond = {};
  (feedLogs || []).forEach((f) => {
    const pid = String(f.pondId || '');
    if (!pid) return;
    if (!feedsByPond[pid]) feedsByPond[pid] = [];
    feedsByPond[pid].push(f);
  });
  const healthByPond = {};
  (healthRows || []).forEach((h) => {
    const pid = String(h.pondId || '');
    if (!pid) return;
    // 分页可能非最新优先，保留较大 id
    if (!healthByPond[pid] || (h.id || 0) > (healthByPond[pid].id || 0)) {
      healthByPond[pid] = h;
    }
  });
  ponds.forEach((pond) => {
    const logs = (feedsByPond[String(pond.id)] || []).slice().sort((a, b) => {
      return new Date(b.feedTime || 0) - new Date(a.feedTime || 0);
    });
    const todayStr = new Date().toISOString().slice(0, 10);
    const todayLogs = logs.filter((l) => String(l.feedTime || '').startsWith(todayStr));
    const useLogs = todayLogs.length ? todayLogs : logs.slice(0, 8);
    const pad = (n) => String(n).padStart(2, '0');
    const todayFeedings = useLogs.map((l) => {
      const d = l.feedTime ? new Date(l.feedTime) : null;
      const time = d && !isNaN(d.getTime())
        ? pad(d.getHours()) + ':' + pad(d.getMinutes())
        : '-';
      const net = l.feedCount != null ? Number(l.feedCount) : 0;
      return { time, netWeight: net, medicineWeight: 0, grossWeight: net };
    });
    const dailyNet = todayFeedings.reduce((s, x) => s + (Number(x.netWeight) || 0), 0);
    const totalNet = logs.reduce((s, x) => s + (Number(x.feedCount) || 0), 0);
    const bySpec = {};
    logs.forEach((l) => {
      const spec = l.feedSpec || '未标注';
      bySpec[spec] = (bySpec[spec] || 0) + (Number(l.feedCount) || 0);
    });
    const feedBySpec = Object.keys(bySpec).map((spec) => ({
      spec,
      amount: Math.round(bySpec[spec] * 100) / 100,
      percentage: totalNet > 0 ? Math.round((bySpec[spec] / totalNet) * 1000) / 10 : 0
    }));
    pond.feedAmount = {
      totalNet: totalNet || pond.feedAmount.totalNet || 0,
      totalGross: totalNet || 0,
      dailyNet: dailyNet || pond.feedAmount.dailyNet || 0,
      dailyGross: dailyNet || 0,
      medicineWeight: 0,
      todayFeedings
    };
    pond.feedBySpec = feedBySpec;
    if (!pond.fishSpecies && logs[0]) {
      const fromFeed = normalizeSpeciesName(logs[0].fishTypeName || logs[0].fishName || logs[0].fishTypes);
      if (fromFeed) pond.fishSpecies = fromFeed;
    }
    const h = healthByPond[String(pond.id)];
    if (h) {
      const ex = Number(h.numberOfExceptions || 0);
      const overall = h.overallRating != null ? Number(h.overallRating) : null;
      if (ex > 0) pond.status = 'warning';
      else if (overall != null && overall < 60) pond.status = 'alert';
      else pond.status = 'normal';
    }
  });
}

function buildWorkshopsFromPonds(ponds, wq) {
  const workshopMap = new Map();
  (ponds || []).forEach((p) => {
    const wId = String(p.workshopId || p.workshopName || 'default');
    const wName = p.workshopName || '未分车间';
    if (!workshopMap.has(wId)) {
      workshopMap.set(wId, { id: wId, name: wName, systems: new Map() });
    }
    const ws = workshopMap.get(wId);
    const sId = String(p.pondSystemId || p.pondSystemName || p.pondNum || 'default');
    const sName = p.pondSystemName || p.pondNum || '系统1';
    if (!ws.systems.has(sId)) {
      ws.systems.set(sId, { id: sId, name: sName, workshopId: wId, ponds: [] });
    }
    ws.systems.get(sId).ponds.push(buildPondFromDb(p, wq, wId, wName, sId, sName));
  });
  return Array.from(workshopMap.values()).map((w) => ({
    id: w.id,
    name: w.name,
    systems: Array.from(w.systems.values())
  }));
}

/* ========== 原 mock 假数据已落库 zk_fish_health_snapshot，整段注释保留备查（勿删） ==========
const mockWorkshops_DEPRECATED = [
  {
    id: 'w1', name: '1号养殖车间',
    systems: [
      {
        id: 's1a', name: 'A循环水系统', workshopId: 'w1',
        ponds: [
          {
            id: 'p1a1', name: '1#鱼池', systemId: 's1a', systemName: 'A循环水系统', workshopId: 'w1', workshopName: '1号养殖车间',
            fishSpecies: '大西洋鲑', fishCount: 5000, averageWeight: 350, totalBiomass: 1750, age: 120,
            growthStage: getGrowthStage(120),
            feedCoefficient: { estimated: 1.15, actual: 1.18 },
            feedAmount: {
              totalNet: 2065, totalGross: 2168.25, dailyNet: 52.5, dailyGross: 55.13, medicineWeight: 2.63,
              todayFeedings: [
                { time: '06:00', netWeight: 17.5, medicineWeight: 0.88, grossWeight: 18.38 },
                { time: '12:00', netWeight: 17.5, medicineWeight: 0.88, grossWeight: 18.38 },
                { time: '18:00', netWeight: 17.5, medicineWeight: 0.87, grossWeight: 18.37 },
              ]
            },
            feedRate: 3.0,
            feedBySpec: [ { spec: '1.5mm', amount: 850, percentage: 41.2 }, { spec: '2.0mm', amount: 1215, percentage: 58.8 } ],
            feedReductionRecords: [
              { id: 'fr1', type: 'disease_treatment', reason: '细菌性烂鳃病治疗', startDate: '2024-12-20', endDate: '2024-12-27', actualEndDate: '2024-12-26', reductionRate: 50, status: 'completed', operator: '张医师', remarks: '投喂恢复正常，鱼群摄食情况良好' }
            ],
            waterQuality: { temperature: 14.5, ph: 7.2, dissolvedOxygen: 8.5, ammonia: 0.15, nitrite: 0.08, timestamp: new Date().toISOString() },
            recommendedFeedAmount: calculateRecommendedFeed(1750, 120), recommendedFeedSpec: getRecommendedFeedSpec(120),
            capacity: 100, diameter: 10, depth: 2, status: 'normal'
          },
          {
            id: 'p1a2', name: '2#鱼池', systemId: 's1a', systemName: 'A循环水系统', workshopId: 'w1', workshopName: '1号养殖车间',
            fishSpecies: '大西洋鲑', fishCount: 4800, averageWeight: 280, totalBiomass: 1344, age: 95,
            growthStage: getGrowthStage(95),
            feedCoefficient: { estimated: 1.12, actual: 1.15 },
            feedAmount: {
              totalNet: 1545.6, totalGross: 1622.88, dailyNet: 67.2, dailyGross: 70.56, medicineWeight: 3.36,
              todayFeedings: [
                { time: '06:00', netWeight: 22.4, medicineWeight: 1.12, grossWeight: 23.52 },
                { time: '12:00', netWeight: 22.4, medicineWeight: 1.12, grossWeight: 23.52 },
                { time: '18:00', netWeight: 22.4, medicineWeight: 1.12, grossWeight: 23.52 },
              ]
            },
            feedRate: 5.0,
            feedBySpec: [ { spec: '1.0mm', amount: 618.24, percentage: 40 }, { spec: '1.5mm', amount: 927.36, percentage: 60 } ],
            feedReductionRecords: [
              { id: 'fr2', type: 'health_product', reason: '维生素C强化投喂', startDate: '2024-12-24', endDate: '2024-12-28', reductionRate: 80, status: 'active', operator: '李技术员', remarks: '添加免疫增强剂，减少20%投喂量' }
            ],
            waterQuality: { temperature: 14.8, ph: 7.3, dissolvedOxygen: 8.2, ammonia: 0.18, nitrite: 0.12, timestamp: new Date().toISOString() },
            recommendedFeedAmount: calculateRecommendedFeed(1344, 95), recommendedFeedSpec: getRecommendedFeedSpec(95),
            capacity: 100, diameter: 10, depth: 2, status: 'warning'
          },
          {
            id: 'p1a3', name: '3#鱼池', systemId: 's1a', systemName: 'A循环水系统', workshopId: 'w1', workshopName: '1号养殖车间',
            fishSpecies: '大西洋鲑', fishCount: 6000, averageWeight: 150, totalBiomass: 900, age: 65,
            growthStage: getGrowthStage(65),
            feedCoefficient: { estimated: 1.20, actual: 1.22 },
            feedAmount: {
              totalNet: 1098, totalGross: 1152.9, dailyNet: 45, dailyGross: 47.25, medicineWeight: 2.25,
              todayFeedings: [
                { time: '06:00', netWeight: 15, medicineWeight: 0.75, grossWeight: 15.75 },
                { time: '12:00', netWeight: 15, medicineWeight: 0.75, grossWeight: 15.75 },
                { time: '18:00', netWeight: 15, medicineWeight: 0.75, grossWeight: 15.75 },
              ]
            },
            feedRate: 5.0,
            feedBySpec: [ { spec: '0.5mm', amount: 109.8, percentage: 10 }, { spec: '1.0mm', amount: 988.2, percentage: 90 } ],
            feedReductionRecords: [],
            waterQuality: { temperature: 15.2, ph: 7.1, dissolvedOxygen: 8.8, ammonia: 0.12, nitrite: 0.06, timestamp: new Date().toISOString() },
            recommendedFeedAmount: calculateRecommendedFeed(900, 65), recommendedFeedSpec: getRecommendedFeedSpec(65),
            capacity: 100, diameter: 10, depth: 2, status: 'normal'
          }
        ]
      },
      {
        id: 's1b', name: 'B循环水系统', workshopId: 'w1',
        ponds: [
          {
            id: 'p1b1', name: '4#鱼池', systemId: 's1b', systemName: 'B循环水系统', workshopId: 'w1', workshopName: '1号养殖车间',
            fishSpecies: '虹鳟', fishCount: 7000, averageWeight: 420, totalBiomass: 2940, age: 180,
            growthStage: getGrowthStage(180),
            feedCoefficient: { estimated: 1.08, actual: 1.10 },
            feedAmount: {
              totalNet: 3234, totalGross: 3395.7, dailyNet: 88.2, dailyGross: 92.61, medicineWeight: 4.41,
              todayFeedings: [
                { time: '06:00', netWeight: 29.4, medicineWeight: 1.47, grossWeight: 30.87 },
                { time: '12:00', netWeight: 29.4, medicineWeight: 1.47, grossWeight: 30.87 },
                { time: '18:00', netWeight: 29.4, medicineWeight: 1.47, grossWeight: 30.87 },
              ]
            },
            feedRate: 3.0,
            feedBySpec: [ { spec: '2.0mm', amount: 1617, percentage: 50 }, { spec: '3.0mm', amount: 1617, percentage: 50 } ],
            feedReductionRecords: [
              { id: 'fr4', type: 'equipment_maintenance', reason: '投饵机保养维护', startDate: '2024-12-25', endDate: '2024-12-25', reductionRate: 0, status: 'active', operator: '王维修', remarks: '停料6小时，完成设备例行检查和保养' }
            ],
            waterQuality: { temperature: 13.8, ph: 7.4, dissolvedOxygen: 8.0, ammonia: 0.22, nitrite: 0.15, timestamp: new Date().toISOString() },
            recommendedFeedAmount: calculateRecommendedFeed(2940, 180), recommendedFeedSpec: getRecommendedFeedSpec(180),
            capacity: 120, diameter: 12, depth: 2, status: 'normal'
          },
          {
            id: 'p1b2', name: '5#鱼池', systemId: 's1b', systemName: 'B循环水系统', workshopId: 'w1', workshopName: '1号养殖车间',
            fishSpecies: '虹鳟', fishCount: 8000, averageWeight: 85, totalBiomass: 680, age: 35,
            growthStage: getGrowthStage(35),
            feedCoefficient: { estimated: 1.25, actual: 1.28 },
            feedAmount: {
              totalNet: 870.4, totalGross: 913.92, dailyNet: 34, dailyGross: 35.7, medicineWeight: 1.7,
              todayFeedings: [
                { time: '06:00', netWeight: 11.33, medicineWeight: 0.57, grossWeight: 11.90 },
                { time: '12:00', netWeight: 11.33, medicineWeight: 0.57, grossWeight: 11.90 },
                { time: '18:00', netWeight: 11.34, medicineWeight: 0.56, grossWeight: 11.90 },
              ]
            },
            feedRate: 5.0,
            feedBySpec: [ { spec: '0.5mm', amount: 348.16, percentage: 40 }, { spec: '1.0mm', amount: 522.24, percentage: 60 } ],
            feedReductionRecords: [
              { id: 'fr5', type: 'water_quality', reason: '水质异常处理', startDate: '2024-12-26', endDate: '2024-12-27', reductionRate: 30, status: 'active', operator: '赵主管', remarks: '溶氧偏低，减料70%至水质恢复正常' }
            ],
            waterQuality: { temperature: 14.2, ph: 7.0, dissolvedOxygen: 9.2, ammonia: 0.10, nitrite: 0.05, timestamp: new Date().toISOString() },
            recommendedFeedAmount: calculateRecommendedFeed(680, 35), recommendedFeedSpec: getRecommendedFeedSpec(35),
            capacity: 100, diameter: 10, depth: 2, status: 'alert'
          }
        ]
      }
    ]
  },
  {
    id: 'w2', name: '2号养殖车间',
    systems: [
      {
        id: 's2a', name: 'A循环水系统', workshopId: 'w2',
        ponds: [
          {
            id: 'p2a1', name: '1#鱼池', systemId: 's2a', systemName: 'A循环水系统', workshopId: 'w2', workshopName: '2号养殖车间',
            fishSpecies: '大西洋鲑', fishCount: 5500, averageWeight: 520, totalBiomass: 2860, age: 210,
            growthStage: getGrowthStage(210),
            feedCoefficient: { estimated: 1.05, actual: 1.07 },
            feedAmount: {
              totalNet: 3060.2, totalGross: 3213.21, dailyNet: 57.2, dailyGross: 60.06, medicineWeight: 2.86,
              todayFeedings: [
                { time: '06:00', netWeight: 19.07, medicineWeight: 0.95, grossWeight: 20.02 },
                { time: '12:00', netWeight: 19.07, medicineWeight: 0.95, grossWeight: 20.02 },
                { time: '18:00', netWeight: 19.06, medicineWeight: 0.96, grossWeight: 20.02 },
              ]
            },
            feedRate: 2.0,
            feedBySpec: [ { spec: '3.0mm', amount: 3060.2, percentage: 100 } ],
            feedReductionRecords: [],
            waterQuality: { temperature: 14.0, ph: 7.3, dissolvedOxygen: 8.3, ammonia: 0.20, nitrite: 0.10, timestamp: new Date().toISOString() },
            recommendedFeedAmount: calculateRecommendedFeed(2860, 210), recommendedFeedSpec: getRecommendedFeedSpec(210),
            capacity: 120, diameter: 12, depth: 2, status: 'normal'
          }
        ]
      }
    ]
  }
];

========== mock 注释结束 ========== */

/** 运行时从库加载；初值为空，不再用前端假数据 */
const mockWorkshops = [];

const allPonds = [];

export default {
  name: "ZkFishHealth",
  data() {
    return {
      mockWorkshops,
      allPonds,
      GROWTH_STAGES,
      selectedWorkshopId: '',
      selectedSystemId: 'all',
      selectedPond: null,
      searchTerm: '',
      filters: {
        status: [],
        fishSpecies: [],
        growthStage: []
      },
      activeTab: 'feeding',
      charts: {},
      siteOptions: null
    };
  },
  computed: {
    workshopSelectList() {
      const items = pickWorkshopOptionItems(this.siteOptions);
      if (items.length) {
        return items.map((o) => ({ id: String(o.value), name: o.label }));
      }
      return this.mockWorkshops || [];
    },
    currentSystems() {
      const items = pickSystemOptionItems(this.siteOptions, this.selectedWorkshopId);
      if (items.length || (this.siteOptions && this.siteOptions.systemOptions)) {
        return items.map((o) => ({ id: String(o.value), name: o.label }));
      }
      const w = this.mockWorkshops.find(w => w.id === this.selectedWorkshopId);
      return w ? w.systems : [];
    },
    availableSpecies() {
      return collectSpeciesFilterOptions(this.allPonds);
    },
    availableStages() {
      return Array.from(new Set(this.allPonds.map(p => p.growthStage)));
    },
    filteredPonds() {
      let ponds = this.allPonds;
      if (this.selectedWorkshopId) {
        ponds = ponds.filter(p =>
          workshopsEqualLoose(p.workshopId, this.selectedWorkshopId) ||
          workshopsEqualLoose(p.workshopName, this.selectedWorkshopId)
        );
      }
      if (this.selectedSystemId && this.selectedSystemId !== 'all') {
        ponds = ponds.filter(p =>
          systemsEqualLoose(p.systemId, this.selectedSystemId) ||
          systemsEqualLoose(p.systemName, this.selectedSystemId)
        );
      }
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase();
        ponds = ponds.filter(p => p.name.toLowerCase().includes(term) || p.fishSpecies.toLowerCase().includes(term) || p.systemName.toLowerCase().includes(term));
      }
      if (this.filters.status.length > 0) ponds = ponds.filter(p => this.filters.status.includes(p.status));
      if (this.filters.fishSpecies.length > 0) {
        ponds = ponds.filter(p =>
          this.filters.fishSpecies.some((s) => speciesNamesEqual(p.fishSpecies, s))
        );
      }
      if (this.filters.growthStage.length > 0) ponds = ponds.filter(p => this.filters.growthStage.includes(p.growthStage));
      return ponds;
    },
    stats() {
      const ponds = this.filteredPonds;
      return {
        totalPonds: ponds.length,
        totalBiomass: ponds.reduce((sum, p) => sum + p.totalBiomass, 0),
        totalFeedToday: ponds.reduce((sum, p) => sum + p.feedAmount.dailyGross, 0),
        normalCount: ponds.filter(p => p.status === 'normal').length,
        warningCount: ponds.filter(p => p.status === 'warning').length,
        alertCount: ponds.filter(p => p.status === 'alert').length,
      };
    },
    currentStage() {
      if (!this.selectedPond) return null;
      return this.GROWTH_STAGES.find(s => s.stage === this.selectedPond.growthStage);
    },
    waterQualityData() {
      if (!this.selectedPond) return [];
      const wq = this.selectedPond.waterQuality;
      return [
        { 指标: '水温', 当前值: wq.temperature, 标准范围: '12-16°C', 单位: '°C', status: wq.temperature >= 12 && wq.temperature <= 16 ? 'normal' : 'warning' },
        { 指标: 'PH值', 当前值: wq.ph, 标准范围: '6.5-8.0', 单位: '', status: wq.ph >= 6.5 && wq.ph <= 8.0 ? 'normal' : 'warning' },
        { 指标: '溶氧', 当前值: wq.dissolvedOxygen, 标准范围: '>7.0', 单位: 'mg/L', status: wq.dissolvedOxygen >= 7.0 ? 'normal' : 'alert' },
        { 指标: '氨氮', 当前值: wq.ammonia, 标准范围: '<0.3', 单位: 'mg/L', status: wq.ammonia < 0.3 ? 'normal' : 'warning' },
        { 指标: '亚硝酸盐', 当前值: wq.nitrite, 标准范围: '<0.2', 单位: 'mg/L', status: wq.nitrite < 0.2 ? 'normal' : 'warning' },
      ];
    }
  },
  mounted() {
    this.loadWorkshopsFromDb();
  },
  methods: {
    async loadWorkshopsFromDb() {
      try {
        try {
          this.siteOptions = await loadSharedSiteOptions({ force: false });
        } catch (e) {
          console.warn('[zkFishHealth] site options', e);
        }
        // 筛选项与树结构优先来自存塘/生产记录，快照仅作无池数据时的兜底
        const [pondRes, feedRes, healthRes, mon, masterRes] = await Promise.all([
          getZkPondPage({ pageNo: 1, pageSize: 500 }),
          getZkFeedingLogPage({ pageNo: 1, pageSize: 500 }).catch(() => ({ data: { list: [] } })),
          getZkPondHealthPage({ pageNo: 1, pageSize: 200 }).catch(() => ({ data: { list: [] } })),
          fetchOverviewMonitor(true),
          getPrecisionFeedingPonds().catch(() => ({ data: [] }))
        ]);
        const list = (pondRes && pondRes.data && pondRes.data.list) || [];
        if (list.length) {
          const wm = {};
          (mon.waterRealtime || []).forEach((m) => {
            if (m && m.key) wm[m.key] = m.value;
          });
          const wq = {
            temperature: wm.temp != null ? Number(wm.temp) : null,
            ph: wm.ph != null ? Number(wm.ph) : null,
            dissolvedOxygen: wm.oxygen != null ? Number(wm.oxygen) : null,
            ammonia: wm.ammonia != null ? Number(wm.ammonia) : null,
            nitrite: null,
            timestamp: new Date().toISOString()
          };
          const built = buildWorkshopsFromPonds(list, wq);
          if (built.length) {
            const allPonds = built.flatMap((w) => w.systems.flatMap((s) => s.ponds));
            const masterList = (masterRes && masterRes.data) || [];
            enrichPondsWithSpeciesMaster(allPonds, Array.isArray(masterList) ? masterList : []);
            enrichPondsWithFeedingAndHealth(
              allPonds,
              (feedRes && feedRes.data && feedRes.data.list) || [],
              (healthRes && healthRes.data && healthRes.data.list) || []
            );
            this.mockWorkshops = built;
            this.allPonds = allPonds;
            const first = (this.workshopSelectList && this.workshopSelectList[0]) || built[0];
            this.selectedWorkshopId = first ? first.id : built[0].id;
            return;
          }
        }
        const snap = await getFishHealthSnapshot('default').catch(() => null);
        const snapList = snap && snap.data;
        if (Array.isArray(snapList) && snapList.length) {
          this.mockWorkshops = snapList;
          this.allPonds = snapList.flatMap((w) => (w.systems || []).flatMap((s) => s.ponds || []));
          const first = (this.workshopSelectList && this.workshopSelectList[0]) || snapList[0];
          this.selectedWorkshopId = first ? first.id : snapList[0].id;
        }
      } catch (e) {
        console.warn('[zkFishHealth] loadWorkshopsFromDb', e);
      }
    },
    handleWorkshopChange() {
      this.selectedSystemId = 'all';
      this.selectedPond = null;
    },
    handleSystemChange() {
      this.selectedPond = null;
    },
    selectPond(pond) {
      this.selectedPond = pond;
      this.activeTab = 'feeding';
      this.$nextTick(() => {
        this.renderCharts();
      });
    },
    handleTabClick() {
      this.$nextTick(() => {
        this.renderCharts();
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

      // Destroy old charts
      Object.values(this.charts).forEach(chart => chart && chart.dispose());
      this.charts = {};

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
        if (this.$refs.lineChartRef) {
          const lineChart = echarts.init(this.$refs.lineChartRef);
          lineChart.setOption({
            ...commonOptions,
            tooltip: { ...commonOptions.tooltip, trigger: 'axis' },
            legend: { ...commonOptions.legend, data: ['饲料净重', '药液重量', '总重'] },
            xAxis: { ...commonOptions.xAxis, type: 'category', data: this.selectedPond.feedAmount.todayFeedings.map(f => f.time) },
            yAxis: { ...commonOptions.yAxis, type: 'value' },
            series: [
              { name: '饲料净重', type: 'line', data: this.selectedPond.feedAmount.todayFeedings.map(f => f.netWeight), itemStyle: { color: '#60a5fa' } },
              { name: '药液重量', type: 'line', data: this.selectedPond.feedAmount.todayFeedings.map(f => f.medicineWeight), itemStyle: { color: '#fb923c' } },
              { name: '总重', type: 'line', data: this.selectedPond.feedAmount.todayFeedings.map(f => f.grossWeight), itemStyle: { color: '#4ade80' }, lineStyle: { type: 'dashed' } }
            ]
          });
          this.charts.lineChart = lineChart;
        }

        if (this.$refs.barChartRef) {
          const barChart = echarts.init(this.$refs.barChartRef);
          barChart.setOption({
            ...commonOptions,
            tooltip: { ...commonOptions.tooltip, trigger: 'axis' },
            xAxis: { ...commonOptions.xAxis, type: 'category', data: this.selectedPond.feedBySpec.map(f => f.spec) },
            yAxis: { ...commonOptions.yAxis, type: 'value' },
            series: [{ data: this.selectedPond.feedBySpec.map(f => f.amount), type: 'bar', itemStyle: { color: '#60a5fa' } }]
          });
          this.charts.barChart = barChart;
        }

        if (this.$refs.pieChartRef) {
          const pieChart = echarts.init(this.$refs.pieChartRef);
          pieChart.setOption({
            tooltip: { ...commonOptions.tooltip, trigger: 'item' },
            legend: { ...commonOptions.legend, orient: 'vertical', left: 'left' },
            series: [{
              type: 'pie',
              radius: '50%',
              data: this.selectedPond.feedBySpec.map(f => ({ value: f.amount, name: f.spec })),
              emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
              label: { color: '#d1d5db' }
            }]
          });
          this.charts.pieChart = pieChart;
        }
      } else if (this.activeTab === 'water') {
        if (this.$refs.waterChartRef) {
          const waterChart = echarts.init(this.$refs.waterChartRef);
          waterChart.setOption({
            ...commonOptions,
            tooltip: { ...commonOptions.tooltip, trigger: 'axis' },
            legend: { ...commonOptions.legend, data: ['水温', 'PH', '溶氧'] },
            xAxis: { ...commonOptions.xAxis, type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
            yAxis: { ...commonOptions.yAxis, type: 'value' },
            series: [
              { name: '水温', type: 'line', data: [14.2, 14.0, 14.3, 14.6, 14.8, 14.5], itemStyle: { color: '#f87171' } },
              { name: 'PH', type: 'line', data: [7.1, 7.2, 7.2, 7.3, 7.2, 7.2], itemStyle: { color: '#60a5fa' } },
              { name: '溶氧', type: 'line', data: [8.9, 8.7, 8.6, 8.4, 8.3, 8.5], itemStyle: { color: '#4ade80' } }
            ]
          });
          this.charts.waterChart = waterChart;
        }
      }
    }
  },
  beforeDestroy() {
    Object.values(this.charts).forEach(chart => chart && chart.dispose());
  }
};
</script>

<style scoped>
.app-container {
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
.border-purple-800 { border-color: #6b21a8; }
.border-gray-700 { border-color: #374151; }

/* Layout & Typography */
.min-h-screen { min-height: 100vh; }
.container { max-width: 100%; margin: 0 auto; }
.px-4 { padding-left: 1rem; padding-right: 1rem; }
.py-6 { padding-top: 1.5rem; padding-bottom: 1.5rem; }
.mb-4 { margin-bottom: 1rem; }
.mb-6 { margin-bottom: 1.5rem; }
.text-3xl { font-size: 1.875rem; line-height: 2.25rem; }
.text-2xl { font-size: 1.5rem; line-height: 2rem; }
.text-lg { font-size: 1.125rem; line-height: 1.75rem; }
.text-sm { font-size: 0.875rem; line-height: 1.25rem; }
.text-xs { font-size: 0.75rem; line-height: 1rem; }
.font-bold { font-weight: 700; }
.font-medium { font-weight: 500; }
.flex { display: flex; }
.flex-1 { flex: 1 1 0%; }
.flex-wrap { flex-wrap: wrap; }
.items-center { align-items: center; }
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
</style>
