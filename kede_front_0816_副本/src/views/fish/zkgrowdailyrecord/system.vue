<template>
  <div class="app-container">
    <PageLoadingMask
      :visible="loading && !(pools && pools.length) && !(batchList && batchList.length)"
      text="正在加载生长模型数据…"
    />
    <!-- 顶部搜索/控制栏 -->
    <el-card shadow="hover" class="mb20 search-card">
      <el-form :inline="true" size="small" class="m0 flex items-center justify-between flex-wrap">
        <div class="flex items-center flex-wrap">
          <el-form-item label="车间" class="mb0 search-item">
            <el-select v-model="filterWorkshop" placeholder="车间" clearable filterable style="width: 120px" @change="handleWorkshopFilterChange">
              <el-option v-for="(item, idx) in workshopOptions" :key="'w-'+idx+'-'+item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="系统" class="mb0 ml20 search-item">
            <el-select v-model="filterSystem" placeholder="系统" clearable filterable style="width: 120px" @change="handleSystemFilterChange">
              <el-option v-for="(item, idx) in systemOptions" :key="'s-'+idx+'-'+item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="池号" class="mb0 ml20 search-item">
            <el-select v-model="filterPond" placeholder="池号" clearable filterable style="width: 180px" @change="handleFlatFilterChange">
              <el-option v-for="(o, idx) in pondOptions" :key="'p-'+idx+'-'+(o.id || o.value)" :label="o.label" :value="o.id != null ? o.id : o.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="品种" class="mb0 ml20 search-item">
            <el-select v-model="filterSpecies" placeholder="品种" clearable filterable style="width: 120px" @change="handleFlatFilterChange">
              <el-option v-for="item in speciesOptions" :key="'sp_'+item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="批次" class="mb0 ml20 search-item">
            <el-select v-model="filterBatch" placeholder="全部批次" clearable filterable style="width: 150px" @change="handleFlatFilterChange">
              <el-option label="全部批次" value="" />
              <el-option v-for="item in batchOptions" :key="'b_'+item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="规格" class="mb0 ml20 search-item">
            <el-select v-model="filterSpec" placeholder="规格" clearable filterable style="width: 110px" @change="handleFlatFilterChange">
              <el-option v-for="item in specOptions" :key="'spec_'+item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item class="mb0 ml20 search-item">
            <el-button icon="el-icon-refresh" @click="resetFlatFilter">重置</el-button>
          </el-form-item>
          <el-form-item class="mb0 ml12 search-item cross-links">
            <div class="cross-links-col">
              <el-button type="text" size="small" icon="el-icon-share" @click="$router.push('/keyshow/zkFcrModel')">饵料系数</el-button>
              <el-button type="text" size="small" icon="el-icon-share" @click="$router.push('/keyshow/precisionfeeding')">精准投喂</el-button>
            </div>
          </el-form-item>
          <!-- 生长模拟移到搜索维度右侧 -->
          <el-form-item label="生长模拟" class="mb0 ml20 search-item grow-sim-inline">
            <div class="grow-sim-inline-box">
              <el-tag size="mini" type="primary">第{{ currentDay }} 天</el-tag>
              <el-slider v-model="currentDay" :max="simParams.maxDays" @input="handleDayChange" style="width: 160px; margin: 0 8px;" />
              <span class="text-xs text-gray-400">/ {{ simParams.maxDays }}天</span>
            </div>
          </el-form-item>
        </div>

        <el-form-item label="当前鱼种" class="mb0 ml20 search-item">
          <div :class="['fish-species-tag', currentSpecies === 'salmon' ? 'tag-salmon' : 'tag-seabass']">
            <i :class="currentSpecies === 'salmon' ? 'el-icon-s-opportunity' : 'el-icon-dish'" style="margin-right:4px"></i>
            {{ currentSpeciesName || '未知鱼种' }}
          </div>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 顶部统计概览 (5个独立卡片) -->
    <div class="flex justify-between mb20" style="gap: 20px;">
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">养殖天数</div>
        <div class="text-3xl font-bold text-blue-400">{{ realStats.breedingDays || currentDay }} <span class="text-sm text-gray-500 font-normal">天</span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">当前体重</div>
        <div class="text-3xl font-bold text-blue-400">{{ realStats.currentWeight || currentData.predictedWeight || 0 }} <span class="text-sm text-gray-500 font-normal">g</span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">当前体长</div>
        <div class="text-3xl font-bold text-blue-400">{{ realStats.currentLength || currentData.predictedLength || 0 }} <span class="text-sm text-gray-500 font-normal">cm</span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">水温</div>
        <div class="text-3xl font-bold text-blue-400">{{ realStats.waterTemp || simParams.waterTemp }} <span class="text-sm text-gray-500 font-normal">℃</span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">投饵率</div>
        <div class="text-3xl font-bold text-blue-400">{{ realStats.feedRate || simParams.feedRate }} <span class="text-sm text-gray-500 font-normal">%</span></div>
      </el-card>
      <el-card v-if="displayBioAsset != null" shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">生物性资产</div>
        <div class="text-3xl font-bold text-blue-400">{{ displayBioAsset }} <span class="text-sm text-gray-500 font-normal">元</span></div>
      </el-card>
    </div>

    <el-row :gutter="20">
      <!-- 左侧控制面板 -->
      <el-col :span="6">
        <!-- 养殖参数监测 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-odometer mr5"></i>养殖参数监测</span>
          </div>
          <div class="space-y-4">
            <div class="p15 bg-dark-blue rounded-lg">
              <div class="text-sm text-blue-400 mb5"><i class="el-icon-heavy-rain mr5"></i>水质情况</div>
              <div class="text-lg font-bold text-blue-600">水温 {{ realStats.waterTemp || realStats.sensorTemp || simParams.waterTemp }} ℃</div>
              <div class="text-xs text-blue-400 mt5">
                溶氧 {{ realStats.dissolvedOxygen != null ? realStats.dissolvedOxygen : '-' }} /
                氨氮 {{ realStats.ammonia != null ? realStats.ammonia : '-' }} /
                pH {{ realStats.ph != null ? realStats.ph : '-' }}
              </div>
            </div>
            <div class="p15 bg-dark-green rounded-lg mt15">
              <div class="text-sm text-green-400 mb5"><i class="el-icon-food mr5"></i>推荐投饵率</div>
              <div class="text-2xl font-bold text-green-600">{{ realStats.recommendedFeedRate || currentData.recommendedFeedRate || '3.0' }} %</div>
              <div class="text-xs text-green-400 mt5">
                与实际偏差
                <span v-if="realStats.recommendedFeedDiffPercent != null">
                  {{ realStats.recommendedFeedDiffPercent > 0 ? '+' : '' }}{{ realStats.recommendedFeedDiffPercent }}%
                </span>
                <span v-else>-</span>
              </div>
            </div>
            <div class="p15 bg-dark-orange rounded-lg mt15">
              <div class="text-sm text-orange-400 mb5"><i class="el-icon-dish-1 mr5"></i>实际投饵率</div>
              <div class="text-2xl font-bold text-orange-600">{{ realStats.actualFeedRate || simParams.feedRate }} %</div>
              <div class="text-xs text-orange-400 mt5">根据每天投喂记录统计</div>
            </div>
          </div>
        </el-card>

        <!-- 生长模拟已移至顶部搜索栏右侧，此处保留生长速率 -->
        <!-- 生长速率 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-data-line mr5"></i>生长速率统计</span>
          </div>
          <div class="rate-info">
            <div class="flex justify-between items-center mb15 pb15 border-b border-gray-800">
              <span class="text-sm text-gray-400">日增重</span>
              <span class="font-bold text-lg">{{ realStats.weightGainPerDay || growthRate.weightGainPerDay || 0 }} <span class="text-xs font-normal text-gray-400">g/天</span></span>
            </div>
            <div class="flex justify-between items-center mb15 pb15 border-b border-gray-800">
              <span class="text-sm text-gray-400">日增长</span>
              <span class="font-bold text-lg">{{ (realStats.lengthGainPerDay || growthRate.lengthGainPerDay)<0?-(realStats.lengthGainPerDay || growthRate.lengthGainPerDay):(realStats.lengthGainPerDay || growthRate.lengthGainPerDay) || 0 }} <span class="text-xs font-normal text-gray-400">cm/天</span></span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-400">特定生长率(SGR)</span>
              <span class="font-bold text-lg text-orange-500">{{ realStats.sgr || growthRate.sgr || 0 }} <span class="text-xs font-normal text-orange-300">%</span></span>
            </div>
          </div>
        </el-card>

        <!-- AI 智能分析 -->
        <el-card shadow="hover" class="mb20 instruction-card">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-cpu mr5"></i>AI 智能分析</span>
            <el-tag size="mini" :type="realStats.growthStatus === '缓慢' ? 'danger' : (realStats.growthStatus === '发育良好' ? 'success' : 'warning')" style="float:right">
              {{ realStats.growthStatus || '正常' }}
            </el-tag>
          </div>
          <div class="text-sm text-gray-300 mb10">{{ realStats.aiAnalysisSummary || '暂无分析结果' }}</div>
          <div class="text-xs text-gray-400 mb8" v-if="realStats.nutritionRemark">{{ realStats.nutritionRemark }}</div>
          <el-table v-if="realStats.pondAiItems && realStats.pondAiItems.length" :data="realStats.pondAiItems" size="mini" max-height="220">
            <el-table-column prop="pondName" label="鱼池" min-width="90" />
            <el-table-column prop="growthStatus" label="状态" width="80" />
            <el-table-column prop="deviationPercent" label="偏差%" width="70" />
          </el-table>
          <ul class="text-sm text-gray-400 list-none p0 m0 space-y-2 mt10">
            <li class="mb10">● <i class="el-icon-link text-blue-400 mr5"></i>
              <el-button type="text" @click="$router.push('/water_quality')">水质情况</el-button>
              <el-button type="text" @click="$router.push('/keyshow/precisionfeeding')">精准投喂</el-button>
              <el-button type="text" @click="$router.push('/keyshow/zkFcrModel')">饵料系数</el-button>
            </li>
          </ul>
        </el-card>
      </el-col>

      <!-- 右侧图表区域 -->
      <el-col :span="18">
        <el-card shadow="hover" class="mb20">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="体重生长曲线" name="weight">
              <div id="weightChart" style="height: 400px; width: 100%;"></div>
            </el-tab-pane>
            <el-tab-pane label="体长生长曲线" name="length">
              <div id="lengthChart" style="height: 400px; width: 100%;"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <!-- 预测数据概览 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-data-analysis mr5"></i>第{{ currentDay }} 天预测数据</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">模型预测体重</div>
                <div class="text-xl font-bold text-blue-600">{{ currentData.predictedWeight || 0 }} g</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">视觉识别体重</div>
                <div class="text-xl font-bold text-green-600">{{ currentData.visionWeight || '-' }} g</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">模型预测体长</div>
                <div class="text-xl font-bold text-blue-600">{{ currentData.predictedLength || 0 }} cm</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">视觉识别体长</div>
                <div class="text-xl font-bold text-green-600">{{ currentData.visionLength || '-' }} cm</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 模型说明 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-document mr5"></i>模型说明</span>
          </div>
          <div class="text-sm text-gray-400 mb15">
            本模型基于 Von Bertalanffy 生长方程，结合工厂化循环水养殖{{ currentSpecies === 'salmon' ? '大西洋鲑' : '鲈鱼' }}的实际数据建立。
          </div>
          <el-row :gutter="20">
            <el-col :span="12" class="mb15">
              <div class="p15 rounded-lg" style="background-color: rgba(59, 130, 246, 0.1); border: 1px solid rgba(59, 130, 246, 0.2);">
                <div class="font-bold mb5 text-blue-400">最适水温</div>
                <div class="text-xs text-gray-400">
                  {{ currentSpecies === 'salmon' ? '10-14℃，最适生长温度为12℃' : '23-28℃，最适生长温度为25℃' }}
                </div>
              </div>
            </el-col>
            <el-col :span="12" class="mb15">
              <div class="p15 rounded-lg" style="background-color: rgba(16, 185, 129, 0.1); border: 1px solid rgba(16, 185, 129, 0.2);">
                <div class="font-bold mb5 text-green-400">投饵管理</div>
                <div class="text-xs text-gray-400">
                  {{ currentSpecies === 'salmon' ? '推荐投饵率2-3.5%，按鱼体重计算' : '推荐投饵率2-4%，按鱼体重计算' }}
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="p15 rounded-lg" style="background-color: rgba(139, 92, 246, 0.1); border: 1px solid rgba(139, 92, 246, 0.2);">
                <div class="font-bold mb5 text-purple-400">养殖周期</div>
                <div class="text-xs text-gray-400">
                  {{ currentSpecies === 'salmon' ? '18个月达商品规格（4-6kg）' : '180天可达到商品规格（约500g）' }}
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="p15 rounded-lg" style="background-color: rgba(245, 158, 11, 0.1); border: 1px solid rgba(245, 158, 11, 0.2);">
                <div class="font-bold mb5 text-orange-400">生长特点</div>
                <div class="text-xs text-gray-400">
                  {{ currentSpecies === 'salmon' ? '初始体重100g，生长周期较长' : '前期生长快，后期生长速度减缓' }}
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 数据采集说明 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-info mr5"></i>数据采集说明</span>
          </div>
          <div class="space-y-4">
            <div class="flex items-start">
              <div class="w15 h15 rounded mr10 mt2" style="background-color: #3b82f6;"></div>
              <div>
                <div class="text-sm font-bold">模型预测（实线）</div>
                <div class="text-xs text-gray-400">基于生长方程的理论预测值</div>
              </div>
            </div>
            <div class="flex items-start mt10">
              <div class="w15 h15 rounded mr10 mt2" style="background-color: #f59e0b;"></div>
              <div>
                <div class="text-sm font-bold">视觉识别（散点）</div>
                <div class="text-xs text-gray-400">摄像头每5天自动采集，误差约±3-5%</div>
              </div>
            </div>
            <div class="flex items-start mt10">
              <div class="w15 h15 rounded mr10 mt2" style="background-color: #ef4444;"></div>
              <div>
                <div class="text-sm font-bold">手动测量（方块）</div>
                <div class="text-xs text-gray-400">每15天打样测量，误差约±1.5-2%</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 饲料营养含量管理 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header" class="flex justify-between items-center">
            <div>
              <span class="font-bold"><i class="el-icon-pie-chart mr5"></i>饲料营养含量管理</span>
              <div class="text-xs text-gray-400 mt5">管理不同阶段使用的饲料营养成分，确保生长需求</div>
            </div>
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleNutritionContentOpenForm(undefined)"
              v-hasPermi="['fish:zk-grow-bait-nutrition:create']">添加饲料</el-button>
          </div>
          
          <div v-if="nutritionContentList && nutritionContentList.length > 0">
            <div v-for="item in nutritionContentList" :key="item.id" class="p15 mb15 rounded-lg border border-gray-800 bg-dark-blue">
              <div class="flex justify-between items-start mb10">
                <div>
                  <div class="font-bold text-blue-400">{{ item.baitName || '常规饲料' }}</div>
                  <div class="text-xs text-gray-500">{{ item.manufacturer || '未知厂家' }} | 颗粒: {{ item.pelletSize }}mm</div>
                </div>
                <div>
                  <el-button size="mini" type="text" icon="el-icon-edit" @click="handleNutritionContentOpenForm(item.id)">编辑</el-button>
                  <el-button size="mini" type="text" icon="el-icon-delete" class="text-red-400" @click="handleNutritionContentDelete(item)">删除</el-button>
                </div>
              </div>
              <el-row :gutter="10">
                <el-col :span="8" class="mb5">
                  <div class="text-xs text-gray-400">粗蛋白: <span class="text-white">{{ item.crudeProtein }}%</span></div>
                </el-col>
                <el-col :span="8" class="mb5">
                  <div class="text-xs text-gray-400">粗脂肪: <span class="text-white">{{ item.crudeFat }}%</span></div>
                </el-col>
                <el-col :span="8" class="mb5">
                  <div class="text-xs text-gray-400">粗纤维: <span class="text-white">{{ item.crudeFiber }}%</span></div>
                </el-col>
                <el-col :span="8" class="mb5">
                  <div class="text-xs text-gray-400">灰分: <span class="text-white">{{ item.ashContent }}%</span></div>
                </el-col>
                <el-col :span="8" class="mb5">
                  <div class="text-xs text-gray-400">水分: <span class="text-white">{{ item.moisture }}%</span></div>
                </el-col>
                <el-col :span="8" class="mb5">
                  <div class="text-xs text-gray-400">钙: <span class="text-white">{{ item.calcium }}%</span></div>
                </el-col>
                <el-col :span="8">
                  <div class="text-xs text-gray-400">磷: <span class="text-white">{{ item.phosphorus }}%</span></div>
                </el-col>
                <el-col :span="8">
                  <div class="text-xs text-gray-400">赖氨酸: <span class="text-white">{{ item.lysine }}%</span></div>
                </el-col>
                <el-col :span="8">
                  <div class="text-xs text-gray-400">蛋氨酸: <span class="text-white">{{ item.methionine }}%</span></div>
                </el-col>
              </el-row>
            </div>
          </div>
          <el-empty v-else description="暂无饲料营养数据" :image-size="60"></el-empty>
        </el-card>

        <!-- 营养需求表 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header" class="flex justify-between items-center">
            <span class="font-bold"><i class="el-icon-notebook-2 mr5"></i>各生长阶段营养需求</span>
            <div>
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleNutritionOpenForm(undefined)"
                v-hasPermi="['fish:zk-grow-nutritional-requirements:create']">新增</el-button>
            </div>
          </div>
          <el-table :data="nutritionList" size="mini" stripe border>
            <el-table-column prop="growthStage" label="阶段" width="80" />
            <el-table-column prop="dayold" label="日龄" width="80" />
            <el-table-column prop="weightRange" label="体重范围" width="100" />
            <el-table-column prop="crudeProtein" label="粗蛋白" />
            <el-table-column prop="crudeFat" label="粗脂肪" />
            <el-table-column prop="carbohydrate" label="碳水" />
            <el-table-column prop="crudeFiber" label="纤维" />
            <el-table-column prop="calcium" label="钙" />
            <el-table-column prop="phosphorus" label="磷" />
            <el-table-column label="操作" align="center" width="100">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleNutritionOpenForm(scope.row.id)"
                  v-hasPermi="['fish:zk-grow-nutritional-requirements:update']">修改</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleNutritionDelete(scope.row)"
                  v-hasPermi="['fish:zk-grow-nutritional-requirements:delete']">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

      </el-col>
    </el-row>
    <!-- 对话框(添加 / 修改) -->
    <ZkGrowNutritionalRequirementsForm ref="nutritionFormRef" @success="refreshNutritionList" />
    <ZkGrowBaitNutritionForm ref="nutritionContentFormRef" @success="refreshNutritionContentList" />
  </div>
</template>

<script>
import * as ZkGrowDailyRecordApi from '@/api/fish/zkgrowdailyrecord';
import { getZkGrowFarmingBatchPage } from '@/api/fish/zkgrowfarmingbatch';
import * as ZkGrowNutritionalRequirementsApi from '@/api/fish/zkgrownutritionalrequirements';
import * as ZkGrowBaitNutritionApi from '@/api/fish/zkgrowbaitnutrition';
import ZkGrowNutritionalRequirementsForm from '../zkgrownutritionalrequirements/ZkGrowNutritionalRequirementsForm.vue';
import ZkGrowBaitNutritionForm from '../zkgrowbaitnutrition/ZkGrowBaitNutritionForm.vue';
import * as echarts from 'echarts';
import { pushAlarmAndCreateTask } from '@/utils/alarmTaskBridge';
import { getThresholdModule, resolveGrowthThreshold } from '@/utils/thresholdConfig';
import { loadSharedSiteOptions, pickWorkshopOptionLabels, pickSystemOptionLabels, pickPondOptionItems, workshopsEqualLoose, systemsEqualLoose } from '@/utils/siteOptions';
import { collectBatchFilterOptions, rowMatchesBatchFilter } from '@/utils/batchTokens';
import { normalizeSpeciesName, speciesNamesEqual, collectSpeciesFilterOptions } from '@/utils/speciesTempBridge';
import { getPrecisionFeedingPonds } from '@/api/fish/zkBiologyCapital';
import PageLoadingMask from '@/components/PageLoadingMask/index';

export default {
  name: "ZkGrowDailyRecordSystem",
  components: {
    ZkGrowNutritionalRequirementsForm,
    ZkGrowBaitNutritionForm,
    PageLoadingMask
  },
  data() {
    return {
      activeTab: 'weight',
      realStats: {},
      simParams: {
        batchId: undefined,
        waterTemp: 25,
        feedRate: 3.0,
        maxDays: 180
      },
      searchType: 'batch',
      systemId: undefined,
      poolId: undefined,
      filterWorkshop: '',
      filterSystem: '',
      filterPond: '',
      filterSpecies: '',
      filterBatch: '',
      filterSpec: '',
      siteOptions: null,
      currentSpecies: 'seabass',
      currentSpeciesId: undefined,
      currentSpeciesName: '',
      systems: [],
      pools: [],
      speciesMasterPonds: [],
      currentDay: 90,
      predictionData: [],
      batchList: [],
      nutritionList: [],
      nutritionContentList: [],
      weightChart: null,
      lengthChart: null,
      loading: false,
      growthThreshold: { modelDevMax: 15, dailyGainMin: 0.5, sgrMin: 0.8 },
      pushingGrowthAlarms: false
    };
  },
  computed: {
    workshopOptions() {
      return pickWorkshopOptionLabels(this.siteOptions);
    },
    systemOptions() {
      return pickSystemOptionLabels(this.siteOptions, this.filterWorkshop);
    },
    pondOptions() {
      // 下拉展示统一标签；value 仍用存塘 id，便于原有预测接口
      return pickPondOptionItems(this.siteOptions, this.filterWorkshop, this.filterSystem);
    },
    speciesOptions() {
      return collectSpeciesFilterOptions([
        ...(this.speciesMasterPonds || []),
        ...(this.pools || []),
        ...(this.batchList || [])
      ]);
    },
    batchOptions() {
      // 与精准投喂/生物资产/健康页统一：来自鱼池 zk_pond.batch_number，并按逗号拆开
      let list = this.pools || [];
      if (this.filterWorkshop) {
        list = list.filter(p => workshopsEqualLoose(p.workshopName || p.workshop, this.filterWorkshop));
      }
      if (this.filterSystem) {
        list = list.filter(p =>
          systemsEqualLoose(p.systemName, this.filterSystem) ||
          systemsEqualLoose(p.systemId, this.filterSystem)
        );
      }
      if (this.filterPond) {
        list = list.filter(p =>
          String(p.id) === String(this.filterPond) ||
          this.pondMatchesFilter(p, this.filterPond)
        );
      }
      if (this.filterSpecies) {
        list = list.filter(p =>
          this.speciesNameMatch(p.speciesName, this.filterSpecies) ||
          p.speciesName === this.filterSpecies
        );
      }
      return collectBatchFilterOptions(list, (p) => p.batchNumber || p.batchNo || '');
    },
    specOptions() {
      let list = this.pools;
      if (this.filterWorkshop) {
        list = list.filter(p => workshopsEqualLoose(p.workshopName || p.workshop, this.filterWorkshop));
      }
      if (this.filterSystem) {
        list = list.filter(p =>
          systemsEqualLoose(p.systemName, this.filterSystem) ||
          systemsEqualLoose(p.systemId, this.filterSystem)
        );
      }
      if (this.filterPond) {
        list = list.filter(p =>
          String(p.id) === String(this.filterPond) ||
          this.pondMatchesFilter(p, this.filterPond)
        );
      }
      return [...new Set(list.map(p => p.fishSpec).filter(Boolean))];
    },
    displayBioAsset() {
      const value = this.realStats.biologyAsset
        ?? this.realStats.bioAssetValue
        ?? this.realStats.bioAsset
        ?? this.realStats.totalAssetValue
        ?? this.realStats.totalAssets
        ?? this.realStats.totalWorth;
      if (value == null || value === '') return null;
      const num = Number(value);
      if (Number.isNaN(num)) return null;
      return num.toLocaleString();
    },
    currentData() {
      if (this.predictionData.length === 0) return {};
      return this.predictionData[this.currentDay] || this.predictionData[this.predictionData.length - 1];
    },
    growthRate() {
      if (this.predictionData.length < 30) return {};
      const startDay = Math.max(0, this.currentDay - 30);
      const start = this.predictionData[startDay];
      const end = this.currentData;
      const days = this.currentDay - startDay || 1;
      
      return {
        weightGainPerDay: ((end.predictedWeight - start.predictedWeight) / days).toFixed(2),
        lengthGainPerDay: ((end.predictedLength - start.predictedLength) / days).toFixed(2),
        sgr: end.sgr ? end.sgr.toFixed(2) : 0
      };
    }
  },
  watch: {
    activeTab(val) {
      this.$nextTick(() => {
        if (val === 'weight' && this.weightChart) this.weightChart.resize();
        if (val === 'length' && this.lengthChart) this.lengthChart.resize();
      });
    }
  },
  async created() {
    await this.initData();
  },
  mounted() {
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    if (this.weightChart) this.weightChart.dispose();
    if (this.lengthChart) this.lengthChart.dispose();
  },
  methods: {
    async initData() {
      try {
        this.loading = true;
        let batchRes = { data: [] };
        let pondRes = { data: [] };
        try {
          batchRes = await ZkGrowDailyRecordApi.getBatchSummaryList();
        } catch (e) {
          console.warn('批次列表加载失败，已降级为空列表', e);
          batchRes = { data: [] };
        }
        try {
          pondRes = await ZkGrowDailyRecordApi.getPondSummaryList();
        } catch (e) {
          console.warn('鱼池列表加载失败，已降级为空列表', e);
          pondRes = { data: [] };
        }
        try {
          this.siteOptions = await loadSharedSiteOptions({ force: false });
        } catch (e) {
          console.warn('[zkgrowdailyrecord] site options', e);
        }
        // 品种筛选项与精准投喂 / 健康状态共用主档，避免各接口 label 不一致
        try {
          const pf = await getPrecisionFeedingPonds();
          this.speciesMasterPonds = Array.isArray(pf && pf.data) ? pf.data : [];
        } catch (e) {
          this.speciesMasterPonds = [];
        }
        
        // 1. 处理批次数据
        this.batchList = (batchRes.data || []).map(item => ({
          id: item.relationOrderNo,
          batchNo: item.relationOrderNo,
          species: this.mapSpeciesType(item.label),
          speciesId: item.fishTypes,
          speciesName: normalizeSpeciesName(item.label) || item.label || ''
        }));

        // 2. 处理鱼池数据
        const poolData = pondRes.data || [];
        this.pools = poolData.map(item => ({
          id: item.pondDetailNum,
          name: item.pondDetailNum || item.pondName,
          pondDetailNum: item.pondDetailNum,
          systemId: item.pondNum,
          systemName: item.pondSystemName || item.pondNum,
          workshopName: item.workshopName || item.workshop || '',
          workshopId: item.workshopId,
          batchNumber: item.relationOrderNo || item.batchNo || '',
          fishSpec: item.fishSpec || item.avgSpecs || '',
          species: this.mapSpeciesType(item.label),
          speciesId: item.fishType,
          speciesName: normalizeSpeciesName(item.label) || item.label || ''
        }));

        // 3. 从鱼池数据中抽取去重后的系统列表
        const systemMap = new Map();
        this.pools.forEach(p => {
          if (!systemMap.has(p.systemId)) {
            // 系统也关联其下第一个鱼池的鱼种信息
            systemMap.set(p.systemId, { 
              id: p.systemId, // 系统编号作为 value   pondNum
              name: p.systemName, // 系统名称作为 label   pondSystemName
              species: p.species,
              speciesId: p.speciesId,
              speciesName: p.speciesName
            });
          }
        });
        this.systems = Array.from(systemMap.values());

        await Promise.all([
          this.refreshNutritionList(),
          this.refreshNutritionContentList(),
          this.loadGrowthThresholds()
        ]);

        // 进入默认「全部批次」，不预选单一批次
        this.filterBatch = '';
        this.filterSpecies = this.filterSpecies || '';
        this.searchType = 'system';
        this.handleFlatFilterChange();
      } catch (e) {
        console.error("Failed to load data", e);
      } finally {
        this.loading = false;
      }
    },
    // 根据 label 映射前端 UI 鱼种类型
    mapSpeciesType(label) {
      if (!label) return 'seabass';
      const text = String(label);
      if (text.includes('鲑') || text.includes('三文鱼') || text.includes('salmon') || text.includes('三叉鱼')) {
        return 'salmon';
      }
      return 'seabass';
    },
    /** 统一别名：鲈鱼苗=加州鲈，三文鱼=大西洋鲑 */
    speciesNameMatch(a, b) {
      return speciesNamesEqual(a, b);
    },
    findBatchBySpecies(speciesName) {
      if (!speciesName) return null;
      return this.batchList.find(b => b.speciesName === speciesName)
        || this.batchList.find(b => this.speciesNameMatch(b.speciesName, speciesName))
        || null;
    },
    findPondBySpecies(speciesName) {
      if (!speciesName) return null;
      return this.pools.find(p => p.speciesName === speciesName)
        || this.pools.find(p => this.speciesNameMatch(p.speciesName, speciesName))
        || null;
    },
    syncCurrentSpeciesFromLabel(label, speciesId) {
      if (!label) return;
      this.currentSpecies = this.mapSpeciesType(label);
      // 展示名以用户筛选为准，避免选「大西洋鲑」却显示「三文鱼」
      this.currentSpeciesName = this.filterSpecies || label;
      if (speciesId != null && speciesId !== '') this.currentSpeciesId = speciesId;
      this.updateParamsBySpecies();
    },
    async fetchPrediction() {
      let searchId = undefined;
      if (this.searchType === 'batch') searchId = this.simParams.batchId;
      if (this.searchType === 'system') searchId = this.systemId;
      if (this.searchType === 'pool') searchId = this.poolId;

      if (!searchId) {
        if (this.searchType === 'batch' && this.batchList.length > 0) searchId = this.batchList[0].id;
        else if (this.searchType === 'system' && this.systems.length > 0) searchId = this.systems[0].id;
        else if (this.searchType === 'pool' && this.pools.length > 0) searchId = this.pools[0].id;
      }
      if (!searchId) {
        this.loading = false;
        return;
      }

      const reqSeq = (this._predictSeq = (this._predictSeq || 0) + 1);
      this.loading = true;
      try {
        const res = await ZkGrowDailyRecordApi.getDashboardData({
          searchType: this.searchType,
          searchId: searchId,
          speciesId: this.currentSpeciesId,
          maxDays: this.simParams.maxDays
        });
        if (reqSeq !== this._predictSeq) return;
        const data = res.data;
        this.realStats = data || {};
        
        if (data.breedingDays) {
          this.currentDay = data.breedingDays;
        }

        this.predictionData = data.predictions || [];
        this.$nextTick(() => {
          this.renderCharts();
          this.autoPushGrowthAlarms();
        });
      } finally {
        if (reqSeq === this._predictSeq) this.loading = false;
      }
    },
    async loadGrowthThresholds() {
      try {
        const feeding = await getThresholdModule('feeding', true);
        this.growthThreshold = resolveGrowthThreshold(feeding);
      } catch (e) {
        console.warn('[growth] 阈值加载失败，使用默认偏差阈值', e && e.message);
      }
    },
    async autoPushGrowthAlarms() {
      if (this.pushingGrowthAlarms) return;
      const devLimit = Number((this.growthThreshold && this.growthThreshold.modelDevMax) || 15);
      const items = (this.realStats && this.realStats.pondAiItems) || [];
      const actualGain = Number(this.realStats.weightGainPerDay);
      const predictedGain = Number(this.growthRate.weightGainPerDay);
      const actualSgr = Number(this.realStats.sgr);

      const slowPonds = items.filter((p) => {
        const dev = Number(p.deviationPercent);
        return p.growthStatus === '缓慢' || (Number.isFinite(dev) && dev <= -devLimit);
      });

      const aggregateSlow = !slowPonds.length && predictedGain > 0 && actualGain > 0
        && ((actualGain - predictedGain) / predictedGain) * 100 <= -devLimit;

      const sgrSlow = !slowPonds.length && !aggregateSlow
        && Number.isFinite(actualSgr)
        && actualSgr < Number((this.growthThreshold && this.growthThreshold.sgrMin) || 0.8);

      if (!slowPonds.length && !aggregateSlow && !sgrSlow) return;

      this.pushingGrowthAlarms = true;
      try {
        if (slowPonds.length) {
          for (const p of slowPonds) {
            const pondNo = p.pondDetailNum || p.pondName || '';
            if (!pondNo) continue;
            const dev = Number(p.deviationPercent) || 0;
            await pushAlarmAndCreateTask({
              source: 'growth',
              category: 'fish',
              pondDetailNum: pondNo,
              pondName: p.pondName || pondNo,
              title: `生长滞后 ${pondNo} 偏差${dev.toFixed(1)}%`,
              content: [
                `实测体重 ${p.currentWeight || '-'}g，模型预测 ${p.predictedWeight || '-'}g`,
                `日增重 ${actualGain || '-'}g/天，模型预测 ${predictedGain || '-'}g/天`,
                p.remark || ''
              ].filter(Boolean).join('；'),
              taskTitle: `巡检核查：${pondNo} 生长速率低于模型预测`,
              level: dev <= -(devLimit + 10) ? 'urgent' : 'priority',
              period: 'day'
            });
          }
          return;
        }

        const title = aggregateSlow
          ? '生长模型：日增重显著低于预测'
          : '生长模型：特定生长率 SGR 偏低';
        const content = aggregateSlow
          ? `实测日增重 ${actualGain.toFixed(2)}g/天，模型预测 ${predictedGain.toFixed(2)}g/天，偏差 ${(((actualGain - predictedGain) / predictedGain) * 100).toFixed(1)}%`
          : `当前 SGR ${actualSgr.toFixed(2)}%，低于阈值 ${(this.growthThreshold.sgrMin || 0.8)}%`;

        await pushAlarmAndCreateTask({
          source: 'growth',
          category: 'fish',
          title,
          content,
          taskTitle: '巡检核查：生长速率低于模型预测',
          level: 'priority',
          period: 'day'
        });
      } catch (e) {
        console.warn('[growth] auto push alarm failed', e);
      } finally {
        this.pushingGrowthAlarms = false;
      }
    },
    handleParamChange() {
      this.fetchPrediction();
    },
    handleWorkshopFilterChange() {
      this.filterSystem = '';
      this.filterPond = '';
      this.handleFlatFilterChange();
    },
    handleSystemFilterChange() {
      this.filterPond = '';
      this.handleFlatFilterChange();
    },
    pondMatchesFilter(p, filterPond) {
      if (!p || filterPond == null || filterPond === '') return false;
      if (String(p.id) === String(filterPond)) return true;
      const opt = (this.pondOptions || []).find(
        (o) => String(o.id) === String(filterPond) || String(o.value) === String(filterPond)
      );
      if (!opt) return false;
      return (
        String(p.id) === String(opt.id) ||
        String(p.pondDetailNum || '') === String(opt.rawCode || '') ||
        String(p.pondDetailNum || '') === String(opt.value || '')
      );
    },
    resolvePoolIdFromFilter(filterPond) {
      if (filterPond == null || filterPond === '') return null;
      const byId = this.pools.find((p) => String(p.id) === String(filterPond));
      if (byId) return byId.id;
      const opt = (this.pondOptions || []).find(
        (o) => String(o.id) === String(filterPond) || String(o.value) === String(filterPond) || String(o.label) === String(filterPond)
      );
      if (opt && opt.id != null) {
        const hit = this.pools.find((p) => String(p.id) === String(opt.id));
        if (hit) return hit.id;
        return opt.id;
      }
      return filterPond;
    },
    handleFlatFilterChange() {
      // 立即给出加载反馈，避免用户误以为未响应
      this.loading = true;
      // 品种筛选优先同步「当前鱼种」，避免大西洋鲑 / 三文鱼显示不一致
      if (this.filterSpecies) {
        const matched = this.findBatchBySpecies(this.filterSpecies)
          || this.findPondBySpecies(this.filterSpecies);
        this.syncCurrentSpeciesFromLabel(
          this.filterSpecies,
          matched ? matched.speciesId : this.currentSpeciesId
        );
      }
      if (this.filterPond) {
        const poolId = this.resolvePoolIdFromFilter(this.filterPond);
        this.searchType = 'pool';
        this.poolId = poolId;
        this.handlePoolChange(poolId);
        return;
      }
      if (this.filterSystem) {
        const sys = this.systems.find(s =>
          systemsEqualLoose(s.name, this.filterSystem) ||
          systemsEqualLoose(s.id, this.filterSystem)
        );
        this.searchType = 'system';
        this.systemId = sys ? sys.id : this.filterSystem;
        this.handleSystemChange(this.systemId);
        return;
      }
      if (this.filterWorkshop) {
        let ponds = this.pools.filter(p => workshopsEqualLoose(p.workshopName || p.workshop, this.filterWorkshop));
        if (this.filterSpecies) {
          ponds = ponds.filter(p => this.speciesNameMatch(p.speciesName, this.filterSpecies) || p.speciesName === this.filterSpecies);
        }
        if (ponds.length) {
          this.searchType = 'pool';
          this.poolId = ponds[0].id;
          this.filterPond = ponds[0].id;
          this.handlePoolChange(ponds[0].id);
          return;
        }
      }
      if (this.filterBatch) {
        // 预测接口仍用采购批次摘要；找不到则回落到含该批次的鱼池
        const batch = this.batchList.find(b => b.batchNo === this.filterBatch)
          || this.batchList.find(b => rowMatchesBatchFilter({ batchNumber: b.batchNo }, this.filterBatch));
        if (batch) {
          this.searchType = 'batch';
          this.simParams.batchId = batch.id;
          this.handleBatchChange(batch.id);
          return;
        }
        let ponds = (this.pools || []).filter(p => rowMatchesBatchFilter(p, this.filterBatch));
        if (this.filterSpecies) {
          ponds = ponds.filter(p =>
            this.speciesNameMatch(p.speciesName, this.filterSpecies) ||
            p.speciesName === this.filterSpecies
          );
        }
        if (ponds.length) {
          this.searchType = 'pool';
          this.poolId = ponds[0].id;
          this.filterPond = ponds[0].id;
          this.handlePoolChange(ponds[0].id);
          return;
        }
      }
      // 仅选品种：按该品种批次展示，但不改动「全部批次」下拉（除非用户已选手动批次）
      if (this.filterSpecies) {
        const batch = this.findBatchBySpecies(this.filterSpecies);
        if (batch) {
          this.searchType = 'batch';
          this.simParams.batchId = batch.id;
          this.handleBatchChange(batch.id);
          return;
        }
        const pond = this.findPondBySpecies(this.filterSpecies);
        if (pond) {
          this.searchType = 'pool';
          this.poolId = pond.id;
          this.filterPond = pond.id;
          this.handlePoolChange(pond.id);
          return;
        }
      }
      // 全部批次：优先按首个系统展示全厂趋势，避免默认锁死单一批次；下拉保持「全部批次」
      this.filterBatch = '';
      if (this.systems.length > 0) {
        this.searchType = 'system';
        this.systemId = this.systems[0].id;
        this.handleSystemChange(this.systemId);
        return;
      }
      if (this.batchList.length > 0) {
        // 无系统维度时仍可预测，但不回填 filterBatch，界面保持「全部批次」
        this.searchType = 'batch';
        this.simParams.batchId = this.batchList[0].id;
        this.handleBatchChange(this.simParams.batchId);
        return;
      }
      this.loading = false;
    },
    resetFlatFilter() {
      this.filterWorkshop = '';
      this.filterSystem = '';
      this.filterPond = '';
      this.filterSpecies = '';
      this.filterBatch = '';
      this.filterSpec = '';
      this.handleFlatFilterChange();
    },
    handleBatchChange(val) {
      const batch = this.batchList.find(b => b.id === val);
      if (batch) {
        // 若用户已选手动品种筛选，展示以筛选为准
        const label = this.filterSpecies || batch.speciesName;
        this.syncCurrentSpeciesFromLabel(label, batch.speciesId);
        this.fetchPrediction();
      } else {
        this.loading = false;
      }
    },
    handleSystemChange(val) {
      const system = this.systems.find(s => s.id === val);
      if (system) {
        const label = this.filterSpecies || system.speciesName;
        this.syncCurrentSpeciesFromLabel(label, system.speciesId);
        this.fetchPrediction();
      } else {
        this.loading = false;
      }
    },
    handlePoolChange(val) {
      const pool = this.pools.find(p => p.id === val);
      if (pool) {
        const label = this.filterSpecies || pool.speciesName;
        this.syncCurrentSpeciesFromLabel(label, pool.speciesId);
        this.fetchPrediction();
      } else {
        this.loading = false;
      }
    },
    async refreshNutritionList() {
      const res = await ZkGrowNutritionalRequirementsApi.getZkGrowNutritionalRequirementsPage({ pageNo: 1, pageSize: 100 });
      this.nutritionList = res.data.list;
    },
    handleNutritionOpenForm(id) {
      this.$refs["nutritionFormRef"].open(id);
    },
    async handleNutritionDelete(row) {
      const id = row.id;
      await this.$modal.confirm('是否确认删除各阶段营养需求编号为"' + id + '"的数据项?')
      try {
        await ZkGrowNutritionalRequirementsApi.deleteZkGrowNutritionalRequirements(id);
        await this.refreshNutritionList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    async refreshNutritionContentList() {
      const res = await ZkGrowBaitNutritionApi.getZkGrowBaitNutritionPage({ pageNo: 1, pageSize: 100 });
      this.nutritionContentList = res.data.list;
    },
    handleNutritionContentOpenForm(id) {
      this.$refs["nutritionContentFormRef"].open(id);
    },
    async handleNutritionContentDelete(row) {
      const id = row.id;
      await this.$modal.confirm('是否确认删除目前饲料的营养含量编号为"' + id + '"的数据项?')
      try {
        await ZkGrowBaitNutritionApi.deleteZkGrowBaitNutrition(id);
        await this.refreshNutritionContentList();
        this.$modal.msgSuccess("删除成功");
      } catch { }
    },
    updateParamsBySpecies() {
      // 根据鱼种调整默认参数
      if (this.currentSpecies === 'salmon') {
        this.simParams.waterTemp = 12;
        this.simParams.feedRate = 2.7;
        this.simParams.maxDays = 540;
        this.currentDay = 270;
      } else {
        this.simParams.waterTemp = 25;
        this.simParams.feedRate = 3.2;
        this.simParams.maxDays = 180;
        this.currentDay = 90;
      }
    },
    handleDayChange() {
      // 进度条拖动时不需要重新请求，只需要更新计算属性
    },
    renderCharts() {
      this.renderWeightChart();
      this.renderLengthChart();
    },
    renderWeightChart() {
      const chartDom = document.getElementById('weightChart');
      if (!chartDom) return;
      if (!this.weightChart) this.weightChart = echarts.init(chartDom);
      
      const days = this.predictionData.map(d => `第${d.dayIndex}天`);
      const predicted = this.predictionData.map(d => d.predictedWeight);
      const vision = this.predictionData.map(d => d.visionWeight);
      const manual = this.predictionData.map(d => d.manualWeight);

      const option = {
        backgroundColor: 'transparent',
        title: { text: '体重生长曲线 (g)', left: 'center', textStyle: { color: '#ffffff' } },
        tooltip: { 
          trigger: 'axis', 
          backgroundColor: 'rgba(26, 34, 52, 0.9)', 
          borderColor: '#2d3b55', 
          textStyle: { color: '#ffffff' },
          borderWidth: 1
        },
        legend: { bottom: 0, textStyle: { color: '#ccc' } },
        grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true, borderColor: '#2d3b55' },
        xAxis: { 
          type: 'category', 
          boundaryGap: false, 
          data: days,
          axisLabel: { color: '#888' },
          axisLine: { lineStyle: { color: '#2d3b55' } }
        },
        yAxis: { 
          type: 'value',
          splitLine: { lineStyle: { color: '#2d3b55' } },
          axisLabel: { color: '#888' },
          axisLine: { show: false }
        },
        series: [
          { name: '模型预测', type: 'line', data: predicted, smooth: true, lineStyle: { width: 3, color: '#3b82f6' }, itemStyle: { color: '#3b82f6' } },
          { name: '视觉识别', type: 'scatter', data: vision, itemStyle: { color: '#f59e0b' } },
          { name: '手动测量', type: 'scatter', data: manual, itemStyle: { color: '#ef4444' }, symbol: 'rect' }
        ]
      };
      this.weightChart.setOption(option);
    },
    renderLengthChart() {
      const chartDom = document.getElementById('lengthChart');
      if (!chartDom) return;
      if (!this.lengthChart) this.lengthChart = echarts.init(chartDom);
      
      const days = this.predictionData.map(d => `第${d.dayIndex}天`);
      const predicted = this.predictionData.map(d => d.predictedLength);
      const vision = this.predictionData.map(d => d.visionLength);
      const manual = this.predictionData.map(d => d.manualLength);

      const option = {
        backgroundColor: 'transparent',
        title: { text: '体长生长曲线 (cm)', left: 'center', textStyle: { color: '#ffffff' } },
        tooltip: { 
          trigger: 'axis', 
          backgroundColor: 'rgba(26, 34, 52, 0.9)', 
          borderColor: '#2d3b55', 
          textStyle: { color: '#ffffff' },
          borderWidth: 1
        },
        legend: { bottom: 0, textStyle: { color: '#ccc' } },
        grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true, borderColor: '#2d3b55' },
        xAxis: { 
          type: 'category', 
          boundaryGap: false, 
          data: days,
          axisLabel: { color: '#888' },
          axisLine: { lineStyle: { color: '#2d3b55' } }
        },
        yAxis: { 
          type: 'value',
          splitLine: { lineStyle: { color: '#2d3b55' } },
          axisLabel: { color: '#888' },
          axisLine: { show: false }
        },
        series: [
          { name: '模型预测', type: 'line', data: predicted, smooth: true, lineStyle: { color: '#10b981', width: 3 }, itemStyle: { color: '#10b981' } },
          { name: '视觉识别', type: 'scatter', data: vision, itemStyle: { color: '#f59e0b' } },
          { name: '手动测量', type: 'scatter', data: manual, itemStyle: { color: '#ef4444' }, symbol: 'rect' }
        ]
      };
      this.lengthChart.setOption(option);
    },
    handleResize() {
      if (this.weightChart) this.weightChart.resize();
      if (this.lengthChart) this.lengthChart.resize();
    }
  }
};
</script>

<style scoped lang="scss">
.app-container {
  position: relative;
  background-color: #0b0f19;
  min-height: calc(100vh - 84px);
  color: #ffffff;
  padding: 20px;
}

::v-deep {
  .el-card {
    background-color: #1a2234;
    border: 1px solid #2d3b55;
    color: #ffffff;

    .el-card__header {
      border-bottom: 1px solid #2d3b55;
      color: #ffffff;
    }
  }

  .el-form-item__label {
    color: #909399;
  }

  .el-input__inner,
  .el-textarea__inner {
    background-color: #0b0f19;
    border-color: #2d3b55;
    color: #ffffff;
  }

  .el-input-number__increase,
  .el-input-number__decrease {
    background-color: #2d3b55;
    border-color: #2d3b55;
    color: #ffffff;
  }

  .el-table {
    background-color: #1a2234;
    color: #ffffff;

    th,
    tr {
      background-color: #1a2234;
      color: #ffffff;
    }

    td,
    th.is-leaf {
      border-bottom: 1px solid #2d3b55;
    }

    &.el-table--border,
    &.el-table--group {
      border: 1px solid #2d3b55;
    }

    .el-table__header-wrapper th {
      background-color: #242f47;
    }

    &.el-table--striped .el-table__row--striped td {
      background-color: #242f47;
    }
    
    .el-table__row:hover > td {
      background-color: #2d3b55 !important;
    }

    .el-table__empty-block {
      background-color: #1a2234;
    }

    .el-table__empty-text {
      color: #909399;
    }
  }

  .el-tabs__item {
    color: #909399;
    &.is-active {
      color: #409EFF;
    }
  }

  .el-slider__runway {
    background-color: #2d3b55;
  }

  .el-slider__button {
    border-color: #409EFF;
    background-color: #ffffff;
  }

  .el-tabs__nav-wrap::after {
    background-color: #2d3b55;
  }

  .el-select .el-input.is-focus .el-input__inner {
    border-color: #409EFF;
  }

  .el-select-dropdown {
    background-color: #1a2234;
    border-color: #2d3b55;
    .el-select-dropdown__item {
      color: #ffffff;
      &.hover, &:hover {
        background-color: #2d3b55;
      }
      &.selected {
        color: #409EFF;
      }
    }
  }
}

.mb20 { margin-bottom: 20px; }
.mb15 { margin-bottom: 15px; }
.mb10 { margin-bottom: 10px; }
.mb5 { margin-bottom: 5px; }
.mb0 { margin-bottom: 0 !important; }
.mr15 { margin-right: 15px; }
.mr5 { margin-right: 5px; }
.ml20 { margin-left: 20px; }
.p15 { padding: 15px; }
.px10 { padding-left: 10px; padding-right: 10px; }
.pb10 { padding-bottom: 10px; }
.pb15 { padding-bottom: 15px; }
.mt5 { margin-top: 5px; }
.mt15 { margin-top: 15px; }
.m0 { margin: 0; }
.flex { display: flex; }
.items-center { align-items: center; }
.justify-between { justify-content: space-between; }
.justify-around { justify-content: space-around; }
.font-bold { font-weight: bold; }
.font-normal { font-weight: normal; }
.text-3xl { font-size: 1.875rem; }
.text-2xl { font-size: 1.5rem; }
.text-xl { font-size: 1.25rem; }
.text-lg { font-size: 1.125rem; }
.text-sm { font-size: 0.875rem; }
.text-xs { font-size: 0.75rem; }
.text-gray-400 { color: #C0C4CC; }
.text-blue-600 { color: #409EFF; }
.text-blue-400 { color: #60a5fa; }
.text-green-600 { color: #67C23A; }
.text-green-500 { color: #22c55e; }
.text-green-400 { color: #4ade80; }
.text-orange-600 { color: #ea580c; }
.text-orange-500 { color: #E6A23C; }
.text-orange-400 { color: #fb923c; }
.text-orange-300 { color: #fdba74; }
.bg-dark-blue { background-color: #1e293b; }
.bg-dark-green { background-color: #064e3b; }
.bg-dark-orange { background-color: #431407; }
.rounded-lg { border-radius: 8px; }
.border { border: 1px solid; }
.border-b { border-bottom-width: 1px; }
.border-blue-900 { border-color: #1e3a8a; }
.border-gray-800 { border-color: #2d3b55; }
.list-none { list-style: none; }
.p0 { padding: 0; }
.text-center { text-align: center; }
.flex-1 { flex: 1; }

.instruction-card ul li {
  line-height: 1.5;
}

.search-card {
  border-radius: 8px;
  background-color: #1a2234 !important;
  overflow-x: auto;
}
.search-card ::v-deep .el-form {
  flex-wrap: wrap;
  row-gap: 8px;
}
.search-item {
  display: flex;
  flex-direction: column;
  margin-right: 12px !important;
  margin-bottom: 4px !important;
}
.search-item ::v-deep .el-form-item__label {
  text-align: left;
  line-height: 1.5;
  padding-bottom: 4px;
  font-size: 13px;
  color: #ffffff;
  font-weight: bold;
}
.search-item ::v-deep .el-form-item__content {
  line-height: 1;
}
/* 上下排布，节省横向宽度，不挤占生长模拟 */
.cross-links {
  justify-content: flex-end;
  min-width: 0;
  margin-right: 4px !important;
}
.cross-links-col {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 6px;
  line-height: 1;
}
.cross-links-col .el-button {
  margin: 0 !important;
  padding: 6px 10px !important;
  height: auto !important;
  white-space: nowrap;
  font-size: 13px;
}
.cross-links-col .el-button + .el-button {
  margin-left: 0 !important;
}
.cross-links-col .el-button [class^="el-icon-"] {
  font-size: 14px;
}
.ml12 { margin-left: 12px; }
.fish-species-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  min-width: 100px;
  padding: 0 15px;
  background-color: rgba(59, 130, 246, 0.1);
  border: 1px solid #3b82f6;
  border-radius: 6px;
  font-weight: bold;
  font-size: 14px;
  color: #3b82f6;
}
.grow-sim-inline-box {
  display: inline-flex;
  align-items: center;
  min-width: 200px;
  max-width: 100%;
  flex-wrap: wrap;
}
@media (max-width: 1400px) {
  .search-item {
    margin-right: 8px !important;
  }
  .search-item ::v-deep .el-select {
    width: 100px !important;
  }
  .grow-sim-inline-box {
    min-width: 180px;
  }
  .grow-sim-inline-box .el-slider {
    width: 120px !important;
  }
}
@media (max-width: 1100px) {
  .search-card ::v-deep .el-form > div.flex {
    width: 100%;
  }
  .search-item {
    margin-right: 6px !important;
  }
}
.tag-salmon {
  border-color: #06b6d4;
  color: #06b6d4;
  background-color: rgba(6, 182, 212, 0.1);
}
.tag-seabass {
  border-color: #3b82f6;
  color: #3b82f6;
  background-color: rgba(59, 130, 246, 0.1);
}
.el-select ::v-deep .el-input__inner {
  background-color: #0b0f19 !important;
  border-color: #2d3b55 !important;
  color: #ffffff !important;
}
.el-select ::v-deep .el-input.is-disabled .el-input__inner {
  background-color: #1a2234 !important;
  border-color: #2d3b55 !important;
  color: #4b5563 !important;
}
.text-red-400 { color: #f87171; }
.w15 { width: 15px; }
.h15 { height: 15px; }
.rounded { border-radius: 4px; }
.mt2 { margin-top: 2px; }
.mr10 { margin-right: 10px; }
</style>
