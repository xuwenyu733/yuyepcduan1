<template>
  <div class="app-container">
    <!-- 椤堕儴鎼滅储/鎺у埗鏍?-->
    <el-card shadow="hover" class="mb20">
      <el-form :inline="true" size="small" class="m0 flex items-center">
        <el-form-item label="鍏绘畺鎵规" class="mb0">
          <el-select v-model="simParams.batchId" placeholder="璇烽€夋嫨鎵规" @change="handleParamChange" style="width: 200px">
            <el-option v-for="item in batchList" :key="item.id" :label="item.batchNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="姘存俯(鈩?" class="mb0 ml20">
          <el-input-number v-model="simParams.waterTemp" :min="15" :max="35" :step="0.5" @change="handleParamChange"></el-input-number>
        </el-form-item>
        <el-form-item label="鎶曞杺鐜?%)" class="mb0 ml20">
          <el-input-number v-model="simParams.feedRate" :min="0" :max="6" :step="0.1" @change="handleParamChange"></el-input-number>
        </el-form-item>
        <el-form-item label="妯℃嫙澶╂暟" class="mb0 ml20">
          <el-input-number v-model="simParams.maxDays" :min="30" :max="365" @change="handleParamChange"></el-input-number>
        </el-form-item>
        <el-form-item class="mb0 ml20">
          <el-button type="primary" icon="el-icon-video-play" @click="handleParamChange" :loading="loading">鐢熸垚妯℃嫙</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 椤堕儴缁熻姒傝 (5涓嫭绔嬪崱鐗? -->
    <div class="flex justify-between mb20" style="gap: 20px;">
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">鍏绘畺澶╂暟</div>
        <div class="text-3xl font-bold text-blue-600">{{ currentDay }} <span class="text-sm text-gray-400 font-normal">澶?/span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">褰撳墠浣撻噸</div>
        <div class="text-3xl font-bold text-blue-600">{{ currentData.predictedWeight || 0 }} <span class="text-sm text-gray-400 font-normal">g</span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">褰撳墠浣撻暱</div>
        <div class="text-3xl font-bold text-blue-600">{{ currentData.predictedLength || 0 }} <span class="text-sm text-gray-400 font-normal">cm</span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">姘存俯</div>
        <div class="text-3xl font-bold text-blue-600">{{ simParams.waterTemp }} <span class="text-sm text-gray-400 font-normal">鈩?/span></div>
      </el-card>
      <el-card shadow="hover" class="flex-1 text-center" :body-style="{ padding: '20px' }" style="flex: 1;">
        <div class="text-gray-400 text-sm mb10">鎶曢サ鐜?/div>
        <div class="text-3xl font-bold text-blue-600">{{ simParams.feedRate }} <span class="text-sm text-gray-400 font-normal">%</span></div>
      </el-card>
    </div>

    <el-row :gutter="20">
      <!-- 宸︿晶鎺у埗闈㈡澘 -->
      <el-col :span="6">
        <!-- 鍏绘畺鍙傛暟鐩戞祴 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-odometer mr5"></i>鍏绘畺鍙傛暟鐩戞祴</span>
          </div>
          <div class="space-y-4">
            <div class="p15 bg-dark-blue rounded-lg">
              <div class="text-sm text-blue-400 mb5"><i class="el-icon-heavy-rain mr5"></i>浼犳劅鍣ㄦ俯搴?/div>
              <div class="text-2xl font-bold text-blue-600">{{ simParams.waterTemp }} 鈩?/div>
              <div class="text-xs text-blue-400 mt5">鏈€閫傛俯搴︼細23-28鈩?/div>
            </div>
            <div class="p15 bg-dark-green rounded-lg mt15">
              <div class="text-sm text-green-400 mb5"><i class="el-icon-food mr5"></i>鎺ㄨ崘鎶曢サ鐜?/div>
              <div class="text-2xl font-bold text-green-600">{{ currentData.recommendedFeedRate || '3.0' }} %</div>
              <div class="text-xs text-green-400 mt5">鍩轰簬褰撳墠浣撻噸鍜岀敓闀块樁娈佃绠?/div>
            </div>
            <div class="p15 bg-dark-orange rounded-lg mt15">
              <div class="text-sm text-orange-400 mb5"><i class="el-icon-dish-1 mr5"></i>瀹為檯鎶曢サ鐜?/div>
              <div class="text-2xl font-bold text-orange-600">{{ simParams.feedRate }} %</div>
              <div class="text-xs text-orange-400 mt5">鏍规嵁姣忓ぉ鎶曞杺璁板綍缁熻</div>
            </div>
          </div>
        </el-card>

        <!-- 鍏绘畺杩涘害 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header" class="flex justify-between items-center">
            <span class="font-bold"><i class="el-icon-time mr5"></i>鍏绘畺杩涘害</span>
            <el-tag size="mini" type="primary">绗?{{ currentDay }} 澶?/el-tag>
          </div>
          <div class="px10 pb10">
            <el-slider v-model="currentDay" :max="simParams.maxDays" @input="handleDayChange" />
            <div class="flex justify-between text-xs text-gray-400 mt5">
              <span>绗?澶?/span>
              <span>绗瑊{ simParams.maxDays }}澶?/span>
            </div>
          </div>
        </el-card>

        <!-- 鐢熼暱閫熺巼 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-data-line mr5"></i>鐢熼暱閫熺巼缁熻</span>
          </div>
          <div class="rate-info">
            <div class="flex justify-between items-center mb15 pb15 border-b border-gray-800">
              <span class="text-sm text-gray-400">鏃ュ閲?/span>
              <span class="font-bold text-lg">{{ growthRate.weightGainPerDay || 0 }} <span class="text-xs font-normal text-gray-400">g/澶?/span></span>
            </div>
            <div class="flex justify-between items-center mb15 pb15 border-b border-gray-800">
              <span class="text-sm text-gray-400">鏃ュ闀?/span>
              <span class="font-bold text-lg">{{ growthRate.lengthGainPerDay || 0 }} <span class="text-xs font-normal text-gray-400">cm/澶?/span></span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-400">鐗瑰畾鐢熼暱鐜?(SGR)</span>
              <span class="font-bold text-lg text-orange-500">{{ growthRate.sgr || 0 }} <span class="text-xs font-normal text-orange-300">%</span></span>
            </div>
          </div>
        </el-card>

        <!-- 绯荤粺璇存槑 -->
        <el-card shadow="hover" class="mb20 instruction-card">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-info mr5"></i>绯荤粺璇存槑</span>
          </div>
          <ul class="text-sm text-gray-400 list-none p0 m0 space-y-2">
            <li class="mb10">鈥?<i class="el-icon-check text-green-500 mr5"></i>姘磋川绋冲畾锛屾憾姘у厖瓒?/li>
            <li class="mb10">鈥?<i class="el-icon-check text-green-500 mr5"></i>娓╁害鍙帶锛屽叏骞村吇娈?/li>
            <li class="mb10">鈥?<i class="el-icon-check text-green-500 mr5"></i>瀵嗗害閫備腑锛岀敓闀垮潎鍖€</li>
            <li class="mb10">鈥?<i class="el-icon-check text-green-500 mr5"></i>鑷姩鎶曢サ锛岀簿鍑嗙鐞?/li>
          </ul>
        </el-card>
      </el-col>

      <!-- 鍙充晶鍥捐〃鍖哄煙 -->
      <el-col :span="18">
        <el-card shadow="hover" class="mb20">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="浣撻噸鐢熼暱鏇茬嚎" name="weight">
              <div id="weightChart" style="height: 400px; width: 100%;"></div>
            </el-tab-pane>
            <el-tab-pane label="浣撻暱鐢熼暱鏇茬嚎" name="length">
              <div id="lengthChart" style="height: 400px; width: 100%;"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <!-- 棰勬祴鏁版嵁姒傝 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header">
            <span class="font-bold"><i class="el-icon-data-analysis mr5"></i>绗?{{ currentDay }} 澶╅娴嬫暟鎹?/span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">妯″瀷棰勬祴浣撻噸</div>
                <div class="text-xl font-bold text-blue-600">{{ currentData.predictedWeight || 0 }} g</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">瑙嗚璇嗗埆浣撻噸</div>
                <div class="text-xl font-bold text-green-600">{{ currentData.visionWeight || '-' }} g</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">妯″瀷棰勬祴浣撻暱</div>
                <div class="text-xl font-bold text-blue-600">{{ currentData.predictedLength || 0 }} cm</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="p15 bg-dark-blue rounded-lg border border-blue-900">
                <div class="text-xs text-gray-400 mb5">瑙嗚璇嗗埆浣撻暱</div>
                <div class="text-xl font-bold text-green-600">{{ currentData.visionLength || '-' }} cm</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 鐩墠楗叉枡鐨勮惀鍏诲惈閲?-->
        <el-card shadow="hover" class="mb20">
          <div slot="header" class="flex justify-between items-center">
            <span class="font-bold"><i class="el-icon-pie-chart mr5"></i>楗叉枡钀ュ吇鍚噺绠＄悊</span>
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleNutritionContentOpenForm(undefined)"
              v-hasPermi="['fish:zk-grow-bait-nutrition:create']">鏂板</el-button>
          </div>
          <el-table :data="nutritionContentList" size="mini" stripe border>
            <el-table-column prop="pelletSize" label="棰楃矑(mm)" width="70" />
            <el-table-column prop="manufacturer" label="鍘傚鍚嶇О" show-overflow-tooltip />
            <el-table-column prop="crudeProtein" label="绮楄泲鐧?%)" width="80" />
            <el-table-column prop="crudeFat" label="绮楄剛鑲?%)" width="80" />
            <el-table-column prop="moisture" label="姘村垎(%)" width="70" />
            <el-table-column prop="ashContent" label="鐏板垎(%)" width="70" />
            <el-table-column prop="crudeFiber" label="绮楃氦缁?%)" width="80" />
            <el-table-column prop="calcium" label="閽?%)" width="60" />
            <el-table-column prop="phosphorus" label="纾?%)" width="60" />
            <el-table-column prop="lysine" label="璧栨皑閰?%)" width="80" />
            <el-table-column prop="methionine" label="铔嬫皑閰?%)" width="80" />
            <el-table-column label="鎿嶄綔" align="center" width="100" fixed="right">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleNutritionContentOpenForm(scope.row.id)"
                  v-hasPermi="['fish:zk-grow-bait-nutrition:update']">淇敼</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleNutritionContentDelete(scope.row)"
                  v-hasPermi="['fish:zk-grow-bait-nutrition:delete']">鍒犻櫎</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 钀ュ吇闇€姹傝〃 -->
        <el-card shadow="hover" class="mb20">
          <div slot="header" class="flex justify-between items-center">
            <span class="font-bold"><i class="el-icon-notebook-2 mr5"></i>鍚勭敓闀块樁娈佃惀鍏婚渶姹?</span>
            <div>
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleNutritionOpenForm(undefined)"
                v-hasPermi="['fish:zk-grow-nutritional-requirements:create']">鏂板</el-button>
            </div>
          </div>
          <el-table :data="nutritionList" size="mini" stripe border>
            <el-table-column prop="growthStage" label="闃舵" width="80" />
            <el-table-column prop="dayold" label="鏃ラ緞" width="80" />
            <el-table-column prop="weightRange" label="浣撻噸鑼冨洿" width="100" />
            <el-table-column prop="crudeProtein" label="绮楄泲鐧? />
            <el-table-column prop="crudeFat" label="绮楄剛鑲? />
            <el-table-column prop="carbohydrate" label="纰虫按" />
            <el-table-column prop="crudeFiber" label="绾ょ淮" />
            <el-table-column prop="calcium" label="閽? />
            <el-table-column prop="phosphorus" label="纾? />
            <el-table-column label="鎿嶄綔" align="center" width="100">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleNutritionOpenForm(scope.row.id)"
                  v-hasPermi="['fish:zk-grow-nutritional-requirements:update']">淇敼</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleNutritionDelete(scope.row)"
                  v-hasPermi="['fish:zk-grow-nutritional-requirements:delete']">鍒犻櫎</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 楗垫枡鎴愬垎琛?-->
        <el-card shadow="hover" class="mb20">
          <div slot="header" class="flex justify-between items-center">
            <span class="font-bold"><i class="el-icon-dish mr5"></i>甯歌楗垫枡鎴愬垎瀵规瘮琛?/span>
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleBaitOpenForm(undefined)"
              v-hasPermi="['fish:zk-grow-bait-ingredients:create']">鏂板</el-button>
          </div>
          <el-table :data="baitList" size="mini" stripe border>
            <el-table-column prop="baitName" label="楗垫枡鍚嶇О" width="120" />
            <el-table-column prop="feedType" label="绫诲瀷" width="80">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getTypeTag(scope.row.feedType)">{{ scope.row.feedType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="crudeProtein" label="绮楄泲鐧? />
            <el-table-column prop="crudeFat" label="绮楄剛鑲? />
            <el-table-column prop="carbohydrate" label="纰虫按鍖栧悎鐗? />
            <el-table-column prop="crudeFiber" label="绮楃氦缁? />
            <el-table-column prop="moisture" label="姘村垎" />
            <el-table-column prop="ashContent" label="鐏板垎" />
            <el-table-column prop="referencePrice" label="鍙傝€冧环鏍? />
            <el-table-column label="鎿嶄綔" align="center" width="100">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleBaitOpenForm(scope.row.id)"
                  v-hasPermi="['fish:zk-grow-bait-ingredients:update']">淇敼</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleBaitDelete(scope.row)"
                  v-hasPermi="['fish:zk-grow-bait-ingredients:delete']">鍒犻櫎</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

      </el-col>
    </el-row>
    <!-- 瀵硅瘽妗?娣诲姞 / 淇敼) -->
    <ZkGrowNutritionalRequirementsForm ref="nutritionFormRef" @success="refreshNutritionList" />
    <ZkGrowBaitIngredientsForm ref="baitFormRef" @success="refreshBaitList" />
    <ZkGrowBaitNutritionForm ref="nutritionContentFormRef" @success="refreshNutritionContentList" />
  </div>
</template>

<script>
import * as ZkGrowDailyRecordApi from '@/api/fish/zkgrowdailyrecord';
import { getZkGrowFarmingBatchPage } from '@/api/fish/zkgrowfarmingbatch';
import * as ZkGrowNutritionalRequirementsApi from '@/api/fish/zkgrownutritionalrequirements';
import * as ZkGrowBaitIngredientsApi from '@/api/fish/zkgrowbaitingredients';
import * as ZkGrowBaitNutritionApi from '@/api/fish/zkgrowbaitnutrition';
import ZkGrowNutritionalRequirementsForm from '../zkgrownutritionalrequirements/ZkGrowNutritionalRequirementsForm.vue';
import ZkGrowBaitIngredientsForm from '../zkgrowbaitingredients/ZkGrowBaitIngredientsForm.vue';
import ZkGrowBaitNutritionForm from '../zkgrowbaitnutrition/ZkGrowBaitNutritionForm.vue';
import * as echarts from 'echarts';

export default {
  name: "ZkGrowDailyRecordSystem",
  components: {
    ZkGrowNutritionalRequirementsForm,
    ZkGrowBaitIngredientsForm,
    ZkGrowBaitNutritionForm
  },
  data() {
    return {
      activeTab: 'weight',
      simParams: {
        batchId: undefined,
        waterTemp: 25,
        feedRate: 3.0,
        maxDays: 180
      },
      currentDay: 90,
      predictionData: [],
      batchList: [],
      nutritionList: [],
      baitList: [],
      nutritionContentList: [],
      weightChart: null,
      lengthChart: null,
      loading: false
    };
  },
  computed: {
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
        const [batchRes] = await Promise.all([
          getZkGrowFarmingBatchPage({ pageNo: 1, pageSize: 100 })
        ]);
        this.batchList = batchRes.data.list;
        
        await Promise.all([
          this.refreshNutritionList(),
          this.refreshBaitList(),
          this.refreshNutritionContentList()
        ]);

        if (this.batchList.length > 0) {
          this.simParams.batchId = this.batchList[0].id;
          await this.fetchPrediction();
        }
      } catch (e) {
        console.error("Failed to load data", e);
      }
    },
    async refreshNutritionList() {
      const res = await ZkGrowNutritionalRequirementsApi.getZkGrowNutritionalRequirementsPage({ pageNo: 1, pageSize: 100 });
      this.nutritionList = res.data.list;
    },
    async refreshBaitList() {
      const res = await ZkGrowBaitIngredientsApi.getZkGrowBaitIngredientsPage({ pageNo: 1, pageSize: 100 });
      this.baitList = res.data.list;
    },
    handleNutritionOpenForm(id) {
      this.$refs["nutritionFormRef"].open(id);
    },
    async handleNutritionDelete(row) {
      const id = row.id;
      await this.$modal.confirm('鏄惁纭鍒犻櫎鍚勯樁娈佃惀鍏婚渶姹傜紪鍙蜂负"' + id + '"鐨勬暟鎹」?')
      try {
        await ZkGrowNutritionalRequirementsApi.deleteZkGrowNutritionalRequirements(id);
        await this.refreshNutritionList();
        this.$modal.msgSuccess("鍒犻櫎鎴愬姛");
      } catch { }
    },
    handleBaitOpenForm(id) {
      this.$refs["baitFormRef"].open(id);
    },
    async handleBaitDelete(row) {
      const id = row.id;
      await this.$modal.confirm('鏄惁纭鍒犻櫎楗垫枡鎴愬垎瀵规瘮缂栧彿涓?' + id + '"鐨勬暟鎹」?')
      try {
        await ZkGrowBaitIngredientsApi.deleteZkGrowBaitIngredients(id);
        await this.refreshBaitList();
        this.$modal.msgSuccess("鍒犻櫎鎴愬姛");
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
      await this.$modal.confirm('鏄惁纭鍒犻櫎鐩墠楗叉枡鐨勮惀鍏诲惈閲忕紪鍙蜂负"' + id + '"鐨勬暟鎹」?')
      try {
        await ZkGrowBaitNutritionApi.deleteZkGrowBaitNutrition(id);
        await this.refreshNutritionContentList();
        this.$modal.msgSuccess("鍒犻櫎鎴愬姛");
      } catch { }
    },
    async fetchPrediction() {
      if (!this.simParams.batchId) return;
      this.loading = true;
      try {
        const res = await ZkGrowDailyRecordApi.predictZkGrowDailyRecord(this.simParams);
        this.predictionData = res.data;
        this.$nextTick(() => {
          this.renderCharts();
        });
      } finally {
        this.loading = false;
      }
    },
    handleParamChange() {
      this.fetchPrediction();
    },
    handleDayChange() {
      // 杩涘害鏉℃嫋鍔ㄦ椂涓嶉渶瑕侀噸鏂拌姹傦紝鍙渶瑕佹洿鏂拌绠楀睘鎬?
    },
    getTypeTag(type) {
      const map = { '鍟嗗搧楗叉枡': 'primary', '椴滄椿楗垫枡': 'success', '鍘熸枡': 'warning' };
      return map[type] || 'info';
    },
    renderCharts() {
      this.renderWeightChart();
      this.renderLengthChart();
    },
    renderWeightChart() {
      const chartDom = document.getElementById('weightChart');
      if (!chartDom) return;
      if (!this.weightChart) this.weightChart = echarts.init(chartDom);
      
      const days = this.predictionData.map(d => `绗?{d.dayIndex}澶ー);
      const predicted = this.predictionData.map(d => d.predictedWeight);
      const vision = this.predictionData.map(d => d.visionWeight);
      const manual = this.predictionData.map(d => d.manualWeight);

      const option = {
        backgroundColor: 'transparent',
        title: { text: '浣撻噸鐢熼暱鏇茬嚎 (g)', left: 'center', textStyle: { color: '#ffffff' } },
        tooltip: { trigger: 'axis', backgroundColor: 'transparent', borderColor: '#2d3b55', textStyle: { color: '#ffffff' } },
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
          { name: '妯″瀷棰勬祴', type: 'line', data: predicted, smooth: true, lineStyle: { width: 3, color: '#3b82f6' }, itemStyle: { color: '#3b82f6' } },
          { name: '瑙嗚璇嗗埆', type: 'scatter', data: vision, itemStyle: { color: '#f59e0b' } },
          { name: '鎵嬪姩娴嬮噺', type: 'scatter', data: manual, itemStyle: { color: '#ef4444' }, symbol: 'rect' }
        ]
      };
      this.weightChart.setOption(option);
    },
    renderLengthChart() {
      const chartDom = document.getElementById('lengthChart');
      if (!chartDom) return;
      if (!this.lengthChart) this.lengthChart = echarts.init(chartDom);
      
      const days = this.predictionData.map(d => `绗?{d.dayIndex}澶ー);
      const predicted = this.predictionData.map(d => d.predictedLength);
      const vision = this.predictionData.map(d => d.visionLength);
      const manual = this.predictionData.map(d => d.manualLength);

      const option = {
        backgroundColor: 'transparent',
        title: { text: '浣撻暱鐢熼暱鏇茬嚎 (cm)', left: 'center', textStyle: { color: '#ffffff' } },
        tooltip: { trigger: 'axis', backgroundColor: 'transparent', borderColor: '#2d3b55', textStyle: { color: '#ffffff' } },
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
          { name: '妯″瀷棰勬祴', type: 'line', data: predicted, smooth: true, lineStyle: { color: '#10b981', width: 3 }, itemStyle: { color: '#10b981' } },
          { name: '瑙嗚璇嗗埆', type: 'scatter', data: vision, itemStyle: { color: '#f59e0b' } },
          { name: '鎵嬪姩娴嬮噺', type: 'scatter', data: manual, itemStyle: { color: '#ef4444' }, symbol: 'rect' }
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
}
.search-item {
  display: flex;
  flex-direction: column;
}
.search-item ::v-deep .el-form-item__label {
  text-align: left;
  line-height: 1.2;
  padding-bottom: 8px;
  font-size: 12px;
  color: #C0C4CC;
}
.fish-species-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  width: 100%;
  background-color: transparent;
  border-radius: 4px;
  font-weight: bold;
  font-size: 14px;
}
.tag-salmon {
  border: 1px solid #06b6d4;
  color: #06b6d4;
  background-color: rgba(6, 182, 212, 0.1);
}
.tag-seabass {
  border: 1px solid #3b82f6;
  color: #3b82f6;
  background-color: rgba(59, 130, 246, 0.1);
}
.el-select .el-input.is-disabled .el-input__inner {
  background-color: #0f172a;
  border-color: #1e293b;
  color: #64748b;
}
</style>
