<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="车间" prop="workshopId">
        <el-select style="width:100%;" v-model="queryParams.workshopId" placeholder="请选择车间" filterable clearable @change="onSearchWorkshopChange">
          <el-option v-for="(item, index) in workshopList" :key="'ws_'+item.workshopId+'_'+index" :label="item.workshopName"
            :value="item.workshopId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="系统" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable @change="onSearchSystemChange">
          <el-option v-for="(item, index) in filteredSystemList" :key="'sys_'+item.pondNum+'_'+index" :label="item.pondNum"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="池号" prop="pondDetailNum">
        <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable @change="onSearchPondChange">
          <el-option v-for="(item, index) in filteredPondList" :key="'pond_'+item.id+'_'+index" :label="item.pondDetailNum"
            :value="item.pondDetailNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间" prop="feedTime">
        <el-date-picker
          v-model="queryParams.feedTime"
          style="width: 360px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="至"
          start-placeholder="起始时间"
          end-placeholder="截止时间"
          :default-time="['00:00:00', '23:59:59']"
        />
      </el-form-item>
      <!-- <el-form-item label="系统编号(车间序号_存塘序号)" prop="pondNum">
        <el-input v-model="queryParams.pondNum" placeholder="请输入系统编号(车间序号_存塘序号)" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘名称" prop="pondName">
        <el-input v-model="queryParams.pondName" placeholder="请输入存塘名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘id" prop="pondId">
        <el-input v-model="queryParams.pondId" placeholder="请输入存塘id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘规模" prop="pondScale">
        <el-input v-model="queryParams.pondScale" placeholder="请输入存塘规模" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="存塘鱼数" prop="fishNum">
        <el-input v-model="queryParams.fishNum" placeholder="请输入存塘鱼数" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="品牌" prop="feedBrand">
        <el-input v-model="queryParams.feedBrand" placeholder="请输入品牌" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="规格" prop="feedSpec">
        <el-input v-model="queryParams.feedSpec" placeholder="请输入规格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="数量" prop="feedCount">
        <el-input v-model="queryParams.feedCount" placeholder="请输入数量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="投喂时间" prop="feedTime">
        <el-date-picker v-model="queryParams.feedTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="类型(1:人工;2:机器;)" prop="feedType">
        <el-select v-model="queryParams.feedType" placeholder="请选择类型(1:人工;2:机器;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入负责人所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['pl:zk-feeding-log:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-s-grid" size="mini" @click="handleBatchAdd"
                   v-hasPermi="['pl:zk-feeding-log:create']">批量新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-feeding-log:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <el-table-column label="车间" align="center" prop="workshopName" />
      <!-- <el-table-column label="系统" align="center" prop="pondSystemName" /> -->
      <el-table-column label="系统" align="center" prop="pondNum" />
      <el-table-column label="池号" align="center" prop="pondDetailNum" />
      <el-table-column label="时间" align="center" prop="feedTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.feedTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单次投喂量(Kg)" align="center" prop="feedCount"  width="120" />
      <el-table-column label="规格(几号料)" align="center" prop="feedSpec" min-width="140" show-overflow-tooltip>
        <template v-slot="scope">
          <span>{{ displayFeedSpec(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="与前5天对比" align="center" prop="differenceBefore" width="160">
        <template v-slot="scope">
          <span>{{ formatDifferenceBefore(scope.row.differenceBefore) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="人对摄食状态评分" align="center" prop="personScore" width="130" />
      <el-table-column label="综合评分" align="center" prop="sumScore" />
      <!-- <el-table-column label="存塘名称" align="center" prop="pondName" /> -->
      <!-- <el-table-column label="存塘id" align="center" prop="pondId" /> -->
      <!-- 
      <el-table-column label="存塘规模" align="center" prop="pondScale" />
      <el-table-column label="存塘鱼数" align="center" prop="fishNum" />
      <el-table-column label="品牌" align="center" prop="feedBrand" /> -->
      <!-- <el-table-column label="投喂时间" align="center" prop="feedTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.feedTime) }}</span>
        </template>
      </el-table-column> -->
      <!-- <el-table-column label="备注" align="center" prop="remark" /> -->
      <!-- <el-table-column label="类型(1:人工;2:机器;)" align="center" prop="feedType" /> -->
      <!-- <el-table-column label="负责人id" align="center" prop="userId" /> -->
      <!-- <el-table-column label="执行人" align="center" prop="userName" show-overflow-tooltip/> -->
      <el-table-column label="执行人" align="center" prop="userId" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.userName">{{ scope.row.userName }}</span>
          <userName v-else :user-id="scope.row.userId"></userName>
        </template>
      </el-table-column>
      <!-- <el-table-column label="负责人所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['pl:zk-feeding-log:update']">修正</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['pl:zk-feeding-log:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修正) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <div v-if="editType == 1" class="reuse-last-bar">
        <el-button type="success" plain size="small" icon="el-icon-document-copy" :loading="reuseLoading"
          @click="reuseLastRecord">复用上次新增记录</el-button>
        <span class="reuse-last-tip">回填上次提交的全部字段（投喂量除外，需重新录入）</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <el-form-item label="所在存塘" prop="pondId">
          <el-select style="width:100%;" v-model="form.pondId" placeholder="请选择所在存塘" filterable clearable  @change="updatePondId">
            <el-option v-for="(item, index) in pondList3" :key="item.id+'__'+index" :label="`${item.pondDetailNum}(${item.pondName}:${item.pondScale}m³:${item.fishNum}条)`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="系统编号(车间序号_存塘序号)" prop="pondNum">
          <el-input v-model="form.pondNum" placeholder="请输入系统编号(车间序号_存塘序号)" />
        </el-form-item>
        <el-form-item label="存塘名称" prop="pondName">
          <el-input v-model="form.pondName" placeholder="请输入存塘名称" />
        </el-form-item>
        <el-form-item label="存塘id" prop="pondId">
          <el-input v-model="form.pondId" placeholder="请输入存塘id" />
        </el-form-item>
        <el-form-item label="存塘规模" prop="pondScale">
          <el-input v-model="form.pondScale" placeholder="请输入存塘规模" />
        </el-form-item>
        <el-form-item label="存塘鱼数" prop="fishNum">
          <el-input v-model="form.fishNum" placeholder="请输入存塘鱼数" />
        </el-form-item> -->
        <el-form-item label="饲料仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" style="width:100%" placeholder="请先选择饲料仓库" filterable clearable @change="onWarehouseChange">
            <el-option v-for="w in warehouses" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="投放饲料" prop="productNo">
          <el-select style="width:100%;" v-model="form.productNo" :placeholder="form.warehouseId ? '仅展示该仓库有库存的饲料' : '请先选择饲料仓库'" filterable clearable :disabled="!form.warehouseId" @change="updateProductId">
            <el-option v-for="(item, index) in productsForWarehouse" :key="item.id+'__'+index" :label="`${item.info}`"
              :value="item.productNo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规格(几号料)" prop="feedSpec">
          <el-input v-model="form.feedSpec" placeholder="选择饲料后自动带出产品名称（如海大15号料）" disabled />
        </el-form-item>
        <el-form-item label="投喂方式" prop="feedType">
          <el-select v-model="form.feedType" placeholder="请选择投喂方式" style="width:100%" @change="onFeedTypeChange">
            <el-option label="人工投喂" :value="1" />
            <el-option label="自动投喂机" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="单次投喂量(Kg)" prop="feedCount">
          <el-input v-model="form.feedCount" placeholder="请输入单次投喂量(Kg)"
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <el-form-item label="投喂时间" prop="feedTime">
          <el-date-picker clearable v-model="form.feedTime" style="width: 100%;" type="datetime" value-format="timestamp"
            :placeholder="form.feedType == 2 ? '自动投喂机启动时间(自动导入)' : '录入投喂时间(自动导入当前时间)'" />
        </el-form-item>
        <el-form-item label="人对摄食状态评分" prop="personScore">
          <div class="person-score-row">
            <el-input v-model="form.personScore" placeholder="人工录入 1-100，或语音录入数字" @blur="personScoreBlur"
              oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
            <el-button type="primary" plain size="mini" icon="el-icon-microphone" :loading="voiceListening"
              @click="startVoiceScore">{{ voiceListening ? '聆听中…' : '语音录入' }}</el-button>
          </div>
        </el-form-item>
        <el-form-item label="与前5天对比" prop="differenceBefore">
          <el-input :value="formatDifferenceBefore(form.differenceBefore)" placeholder="根据同池同餐次近5天自动计算" disabled />
        </el-form-item>
        <el-form-item label="执行人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="根据登录账号自动导入" filterable disabled>
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="综合评分" prop="sumScore">
          <el-input v-model="form.sumScore" placeholder="由前5天对比与人工评分自动计算(暂行)" disabled />
        </el-form-item>
        <el-form-item label="原数据" prop="originData" v-if="editType == 2">
          <el-input v-model="form.originData" placeholder="请输入原数据" />
        </el-form-item>
        <el-form-item label="差值" prop="differenceData" v-if="editType == 2">
          <el-input v-model="form.differenceData" placeholder="请输入差值" />
        </el-form-item>
        <el-form-item label="变化比例" prop="changeRatio" v-if="editType == 2">
          <el-input v-model="form.changeRatio" placeholder="请输入变化比例" />
        </el-form-item>
        <el-form-item label="原因" prop="reason" v-if="editType == 2">
          <el-input v-model="form.reason" placeholder="请输入原因" />
        </el-form-item>
        <!-- <el-form-item label="负责人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入负责人id" />
        </el-form-item>
        <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="负责人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入负责人所属部门" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :loading="operationLoading">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量新增：同一系统多口塘 -->
    <el-dialog title="批量新增投喂记录" :visible.sync="batchOpen" width="1100px" v-dialogDrag append-to-body>
      <div class="batch-tip">选择车间与系统后自动带出该系统下全部存塘；填好默认饲料/投喂量后点「应用到全部行」，再按塘微调。提交前每行投喂量必填且须大于 0。</div>
      <el-form :inline="true" size="small" label-width="72px" class="batch-shared-form">
        <el-form-item label="车间">
          <el-select v-model="batchShared.workshopId" placeholder="请选择车间" filterable clearable style="width:160px"
            @change="onBatchWorkshopChange">
            <el-option v-for="(item, index) in workshopList" :key="'bws_'+item.workshopId+'_'+index"
              :label="item.workshopName" :value="item.workshopId" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统">
          <el-select v-model="batchShared.pondNum" placeholder="请选择系统" filterable clearable style="width:140px"
            @change="onBatchSystemChange">
            <el-option v-for="(item, index) in batchSystemOptions" :key="'bsys_'+item.pondNum+'_'+index"
              :label="item.pondNum" :value="item.pondNum" />
          </el-select>
        </el-form-item>
        <el-form-item label="投喂时间">
          <el-date-picker v-model="batchShared.feedTime" type="datetime" value-format="timestamp"
            style="width:190px" placeholder="共用时间" />
        </el-form-item>
        <el-form-item label="投喂方式">
          <el-select v-model="batchShared.feedType" style="width:120px">
            <el-option label="人工投喂" :value="1" />
            <el-option label="自动投喂机" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="饲料仓库">
          <el-select v-model="batchShared.warehouseId" placeholder="先选仓库" filterable clearable style="width:180px"
            @change="onBatchDefaultWarehouseChange">
            <el-option v-for="w in warehouses" :key="'bwh_'+w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认饲料">
          <el-select v-model="batchShared.productNo" placeholder="该仓库有库存的饲料" filterable clearable style="width:220px"
            :disabled="!batchShared.warehouseId" @change="onBatchDefaultProductChange">
            <el-option v-for="(item, index) in productsInWarehouse(batchShared.warehouseId)" :key="'bp_'+item.id+'_'+index"
              :label="item.info" :value="item.productNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认投喂量">
          <el-input v-model="batchShared.feedCount" placeholder="Kg" style="width:100px"
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain size="mini" @click="applyBatchDefaultsToRows">应用到全部行</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="batchRows" border size="mini" max-height="420" empty-text="请先选择车间与系统">
        <el-table-column label="池号" prop="pondDetailNum" width="110" show-overflow-tooltip />
        <el-table-column label="存塘" min-width="140" show-overflow-tooltip>
          <template v-slot="scope">
            <span>{{ scope.row.pondName }}（{{ scope.row.fishNum || 0 }}条）</span>
          </template>
        </el-table-column>
        <el-table-column label="仓库" width="130">
          <template v-slot="scope">
            <el-select v-model="scope.row.warehouseId" size="mini" style="width:100%" placeholder="仓库"
              @change="(v) => onBatchRowWarehouseChange(scope.row, v)">
              <el-option v-for="w in warehouses" :key="'brw_'+scope.$index+'_'+w.id"
                :label="w.name" :value="w.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="投放饲料" min-width="200">
          <template v-slot="scope">
            <el-select v-model="scope.row.productNo" filterable clearable size="mini" style="width:100%"
              placeholder="选择饲料" :disabled="!scope.row.warehouseId" @change="(v) => onBatchRowProductChange(scope.row, v)">
              <el-option v-for="(item, index) in productsInWarehouse(scope.row.warehouseId)" :key="'brp_'+scope.$index+'_'+item.id+'_'+index"
                :label="item.info" :value="item.productNo" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="规格(几号料)" min-width="150" show-overflow-tooltip>
          <template v-slot="scope">
            <el-input v-model="scope.row.feedSpec" size="mini" disabled placeholder="产品名称" />
          </template>
        </el-table-column>
        <el-table-column label="投喂量(Kg)" width="110">
          <template v-slot="scope">
            <el-input v-model="scope.row.feedCount" size="mini" placeholder="必填"
              oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
          </template>
        </el-table-column>
        <el-table-column label="方式" width="120">
          <template v-slot="scope">
            <el-select v-model="scope.row.feedType" size="mini" style="width:100%">
              <el-option label="人工" :value="1" />
              <el-option label="机器" :value="2" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="90">
          <template v-slot="scope">
            <el-input v-model="scope.row.personScore" size="mini" placeholder="可选"
              oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
          </template>
        </el-table-column>
        <el-table-column label="时间" width="180">
          <template v-slot="scope">
            <el-date-picker v-model="scope.row.feedTime" type="datetime" value-format="timestamp"
              size="mini" style="width:100%" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70" fixed="right">
          <template v-slot="scope">
            <el-button type="text" size="mini" @click="removeBatchRow(scope.$index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <span class="batch-footer-tip">共 {{ batchRows.length }} 口塘</span>
        <el-button type="primary" @click="submitBatchForm" :loading="batchSubmitting">提交全部</el-button>
        <el-button @click="batchOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkFeedingLog, updateZkFeedingLog, deleteZkFeedingLog, getZkFeedingLog, getZkFeedingLogPage, exportZkFeedingLogExcel } from "@/api/fish/zkFeedingLog";
import { createZkFishProofing, updateZkFishProofing, deleteZkFishProofing, getZkFishProofing, getZkFishProofingPage, exportZkFishProofingExcel, getLastProofing } from "@/api/fish/zkFishProofing";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { loadDeptScopedPondList } from "@/utils/siteOptions";
import { notifyProductionRecordChanged } from "@/utils/productionRecordBridge";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import {
  getProductPage
} from "@/api/erp/product";
import { createWarehouse, updateWarehouse, deleteWarehouse, getWarehouse, getWarehousePage, exportWarehouseExcel } from "@/api/erp/warehouse";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkFeedingLog",
  components: {
      FileBusUpload,
  },
  data() {
    return {
      userInfo: {},
      // 遮罩层
      loading: true,
      operationLoading: false,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 投喂记录列表
      list: [],
      pondList: [],
      pondList3: [], // 去掉为存塘数0 的
      productList: [],
      currProduct: {}, // 当前选择的饲料
      workshopList: [],
      warehouses: [],
      systemList: [],
      userList1: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        pondNum: null,
        pondName: null,
        pondId: null,
        pondScale: null,
        fishNum: null,
        feedBrand: null,
        feedSpec: null,
        feedCount: null,
        feedTime: [],
        remark: null,
        feedType: null,
        userId: null,
        userName: null,
        deptId: null,
        createTime: [],

        logType: '',
        batchNumber: '',
        workshopId: '',
        pondNum: '',
        pondDetailNum: '',
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        pondId: [{ required: true, message: "存塘id不能为空", trigger: "blur" }],
        feedTime: [{ required: true, message: "投喂时间不能为空", trigger: "blur" }],
        productNo: [{ required: true, message: "投喂饲料不能为空", trigger: "blur" }],
        feedCount: [{ required: true, message: "投喂量不能为空", trigger: "blur" }],
        warehouseId: [{ required: true, message: "饲料仓库不能为空", trigger: "blur" }],
      },
      editType: 1, // 1:添加;2:修正;
      searchType: '', //
      voiceListening: false,
      speechRecognition: null,
      calcDiffTimer: null,
      filteredAllList: [],
      selectedWorkshopName: '',
      reuseLoading: false,
      batchOpen: false,
      batchSubmitting: false,
      batchRows: [],
      batchShared: {
        workshopId: '',
        pondNum: '',
        feedTime: null,
        feedType: 1,
        productNo: '',
        warehouseId: undefined,
        personScore: undefined,
        feedCount: undefined,
      },
    };
  },
  computed: {
    // 系统选项：从存塘列表按车间过滤后按 pondNum 去重
    filteredSystemList() {
      const wid = this.queryParams.workshopId
      let list = this.pondList || []
      if (wid !== null && wid !== undefined && wid !== '') {
        list = list.filter(p => String(p.workshopId) === String(wid))
      }
      const seen = Object.create(null)
      const result = []
      list.forEach(p => {
        const key = p.pondNum == null ? '' : String(p.pondNum)
        if (!key || seen[key]) return
        seen[key] = true
        result.push(p)
      })
      return result
    },
    // 池号选项：按车间 + 系统过滤
    filteredPondList() {
      const wid = this.queryParams.workshopId
      const pondNum = this.queryParams.pondNum
      let list = this.pondList || []
      if (wid !== null && wid !== undefined && wid !== '') {
        list = list.filter(p => String(p.workshopId) === String(wid))
      }
      if (pondNum !== null && pondNum !== undefined && pondNum !== '') {
        list = list.filter(p => String(p.pondNum) === String(pondNum))
      }
      return list
    },
    /** 所选仓库有库存的饲料；未选仓库时为空 */
    productsForWarehouse() {
      return this.productsInWarehouse(this.form.warehouseId)
    },
    /** 当前饲料有库存的仓库；无关联信息时回退全量仓库 */
    availableWarehouses() {
      const ids = this.resolveProductWarehouseIds(this.currProduct || this.findProductByNo(this.form.productNo))
      if (!ids.length) return this.warehouses || []
      const filtered = (this.warehouses || []).filter(w => ids.includes(String(w.id)))
      return filtered.length ? filtered : (this.warehouses || [])
    },
    /** 批量新增：按所选车间过滤系统 */
    batchSystemOptions() {
      const wid = this.batchShared.workshopId
      let list = this.pondList3 && this.pondList3.length ? this.pondList3 : (this.pondList || [])
      if (wid !== null && wid !== undefined && wid !== '') {
        list = list.filter(p => String(p.workshopId) === String(wid))
      }
      const seen = Object.create(null)
      const result = []
      list.forEach(p => {
        const key = p.pondNum == null ? '' : String(p.pondNum)
        if (!key || seen[key]) return
        seen[key] = true
        result.push(p)
      })
      return result
    },
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getList();
    this.flushChoose();
  },
  watch: {
    'form.feedCount': {
      handler(newV, oldV) {
        if(!newV) {
          this.scheduleCalcDifferenceBefore()
          return;
        }
        if(!this.form.feedingPrice) {
          this.$modal.msgWarning("请先选择饲料！");
          this.form.feedCount = 0;
          return;
        }
        if (newV) {
          this.form.feedingCost = this.my_common.computer(newV, this.form.feedingPrice, 2, 4);
        }
        if (newV && this.editType == 2) {
          this.form.differenceData = newV - this.form.originData; // 计算差值
          this.form.changeRatio = this.my_common.computer(this.form.differenceData, this.form.originData, 3, 2);
        }
        this.scheduleCalcDifferenceBefore()
      }
    },
    'form.feedTime'() {
      this.scheduleCalcDifferenceBefore()
    },
    'form.pondId'() {
      this.scheduleCalcDifferenceBefore()
    },
    'form.personScore'() {
      this.recomputeSumScore()
    },
    'form.differenceBefore'() {
      this.recomputeSumScore()
    },
  },
  beforeDestroy() {
    this.stopVoiceScore()
    if (this.calcDiffTimer) clearTimeout(this.calcDiffTimer)
  },
  methods: {
    formatDifferenceBefore(val) {
      if (val === null || val === undefined || val === '') return '-'
      if (typeof val === 'string' && (val.indexOf('对比') !== -1 || val.indexOf('暂无') !== -1 || val.indexOf('喂') !== -1)) return val
      const num = Number(val)
      if (!Number.isFinite(num)) return String(val)
      const abs = Math.abs(num).toFixed(2)
      // 与标注一致：当前投喂量 - 前5天同餐次平均投喂量（kg）
      if (num > 0) return `与前五天对比多喂 ${abs}`
      if (num < 0) return `与前五天对比少喂 ${abs}`
      return '与前五天对比少喂 0.00'
    },
    /** 餐次：按投喂小时划分，用于“同一餐”对比 */
    getMealSlot(ts) {
      const h = new Date(Number(ts)).getHours()
      if (h >= 5 && h < 11) return 'morning'
      if (h >= 11 && h < 15) return 'noon'
      if (h >= 15 && h < 21) return 'evening'
      return 'night'
    },
    scheduleCalcDifferenceBefore() {
      if (this.calcDiffTimer) clearTimeout(this.calcDiffTimer)
      this.calcDiffTimer = setTimeout(() => {
        this.calcDifferenceBefore()
      }, 350)
    },
    async calcDifferenceBefore() {
      const pondId = this.form.pondId
      const feedTime = this.form.feedTime
      const feedCount = Number(this.form.feedCount)
      if (!pondId || !feedTime || !Number.isFinite(feedCount) || feedCount <= 0) {
        return
      }
      const end = new Date(Number(feedTime))
      const start = new Date(end.getTime() - 5 * 24 * 60 * 60 * 1000)
      const pad = n => (n < 10 ? '0' + n : '' + n)
      const fmt = d => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
      try {
        const res = await getZkFeedingLogPage({
          pageNo: 1,
          pageSize: 200,
          pondId,
          feedTime: [fmt(start), fmt(end)],
        })
        const meal = this.getMealSlot(feedTime)
        const rows = (res.data && res.data.list) || []
        const history = rows.filter(row => {
          if (this.form.id && row.id === this.form.id) return false
          if (!row.feedTime) return false
          const t = Number(row.feedTime)
          if (!Number.isFinite(t) || t >= Number(feedTime)) return false
          return this.getMealSlot(t) === meal && Number(row.feedCount) > 0
        })
        if (!history.length) {
          this.$set(this.form, 'differenceBefore', '暂无同餐次近5天数据')
          this.recomputeSumScore()
          return
        }
        const avg = history.reduce((s, r) => s + Number(r.feedCount), 0) / history.length
        if (!avg) {
          this.$set(this.form, 'differenceBefore', '暂无同餐次近5天数据')
          this.recomputeSumScore()
          return
        }
        // 绝对差(kg)：当前投喂量 - 前5天同餐次平均投喂量
        const diff = feedCount - avg
        this.$set(this.form, 'differenceBefore', Number(diff.toFixed(2)))
        this._diffAvgForScore = avg
        this.recomputeSumScore()
      } catch (e) {
        console.warn('calcDifferenceBefore failed', e)
      }
    },
    /**
     * 综合评分（暂行标准，待《评分标准》文档替换）
     * = 0.5 * 人工评分归一化 + 0.5 * (1 - min(|对比比率|, 1))
     */
    recomputeSumScore() {
      const person = Number(this.form.personScore)
      let personNorm = Number.isFinite(person) ? Math.min(100, Math.max(0, person)) / 100 : null
      let diffRatio = null
      const raw = this.form.differenceBefore
      const absDiff = (typeof raw === 'number')
        ? raw
        : (raw != null && raw !== '' && !Number.isNaN(Number(raw)) ? Number(raw) : null)
      if (absDiff != null) {
        const avg = Number(this._diffAvgForScore)
        const feedCount = Number(this.form.feedCount)
        if (Number.isFinite(avg) && avg > 0) {
          diffRatio = absDiff / avg
        } else if (Number.isFinite(feedCount) && feedCount > 0) {
          // 回退：用 当前量-差值 还原均量
          const restoredAvg = feedCount - absDiff
          diffRatio = restoredAvg ? absDiff / restoredAvg : 0
        } else {
          diffRatio = 0
        }
      }
      if (personNorm == null && diffRatio == null) return
      if (personNorm == null) personNorm = 0.5
      if (diffRatio == null) diffRatio = 0
      const diffScore = 1 - Math.min(Math.abs(diffRatio), 1)
      const sum = 0.5 * personNorm + 0.5 * diffScore
      this.$set(this.form, 'sumScore', Number(sum.toFixed(2)))
    },
    onFeedTypeChange(type) {
      // 人工：录入投喂时间默认当前；机器：按启动时间自动导入（暂无设备接口时用当前时间）
      if (this.editType === 1) {
        this.form.feedTime = new Date().getTime()
        if (type == 2) {
          this.$modal.msgSuccess('已按自动投喂机启动时间自动导入（当前为设备时间占位）')
        }
      }
    },
    applyLoginUserAsExecutor() {
      const uid = this.userInfo && (this.userInfo.userId || this.userInfo.id)
      if (!uid) return
      const hit = this.userList1.find(u => u.id == uid)
      if (hit) {
        this.updateUserId(hit.id)
      } else {
        this.form.userId = uid
        this.form.userName = this.userInfo.nickname || this.userInfo.username || ''
        this.form.deptId = this.userInfo.deptId
      }
    },
    lastFeedCacheKey() {
      const uid = this.userInfo && (this.userInfo.userId || this.userInfo.id)
      return 'kede_feeding_log_last_create:' + (uid != null ? String(uid) : 'anon')
    },
    saveLastCreatedRecord(record) {
      if (!record) return
      try {
        const slim = {
          pondId: record.pondId,
          pondNum: record.pondNum,
          pondName: record.pondName,
          pondScale: record.pondScale,
          fishNum: record.fishNum,
          workshopId: record.workshopId,
          workshopName: record.workshopName,
          pondDetailNum: record.pondDetailNum,
          pondSystemName: record.pondSystemName,
          pondSystemId: record.pondSystemId,
          feedBrand: record.feedBrand,
          feedSpec: record.feedSpec,
          feedName: record.feedName,
          productNo: record.productNo,
          productId: record.productId,
          feedingPrice: record.feedingPrice,
          warehouseId: record.warehouseId,
          feedType: record.feedType,
          remark: record.remark,
          personScore: record.personScore,
          feedTime: record.feedTime
        }
        localStorage.setItem(this.lastFeedCacheKey(), JSON.stringify(slim))
      } catch (e) { /* ignore */ }
    },
    readLastCreatedCache() {
      try {
        const raw = localStorage.getItem(this.lastFeedCacheKey())
        if (!raw) return null
        const parsed = JSON.parse(raw)
        return parsed && typeof parsed === 'object' ? parsed : null
      } catch (e) {
        return null
      }
    },
    normalizeFormFeedTime(val) {
      if (val == null || val === '') return new Date().getTime()
      if (typeof val === 'number' && Number.isFinite(val)) return val
      const n = Number(val)
      if (Number.isFinite(n) && n > 0) return n
      const d = new Date(String(val).replace(/-/g, '/'))
      const t = d.getTime()
      return Number.isFinite(t) ? t : new Date().getTime()
    },
    applyReuseRecord(row) {
      if (!row) return false
      const pondId = row.pondId
      const productNo = row.productNo
      this.form = {
        ...this.form,
        id: undefined,
        pondId: pondId,
        pondNum: row.pondNum,
        pondName: row.pondName,
        pondScale: row.pondScale,
        fishNum: row.fishNum,
        workshopId: row.workshopId,
        workshopName: row.workshopName,
        pondDetailNum: row.pondDetailNum,
        pondSystemName: row.pondSystemName,
        pondSystemId: row.pondSystemId,
        feedBrand: row.feedBrand,
        feedSpec: row.feedSpec,
        feedName: row.feedName,
        productNo: productNo,
        productId: row.productId,
        feedingPrice: row.feedingPrice,
        warehouseId: row.warehouseId,
        feedType: row.feedType != null && row.feedType !== '' ? Number(row.feedType) || row.feedType : 1,
        remark: row.remark,
        personScore: row.personScore,
        feedTime: this.normalizeFormFeedTime(row.feedTime),
        // 投喂量必须人工重录
        feedCount: undefined,
        differenceBefore: undefined,
        sumScore: undefined,
        originData: undefined,
        differenceData: undefined,
        changeRatio: undefined,
        reason: undefined,
        fid: getUUID().replaceAll('-', '')
      }
      if (pondId) this.updatePondId(pondId)
      if (productNo) this.updateProductId(productNo)
      // 复用后仍保持当前登录人为执行人
      this.applyLoginUserAsExecutor()
      this.$nextTick(() => {
        if (this.$refs.form) this.$refs.form.clearValidate(['feedCount'])
        this.scheduleCalcDifferenceBefore()
        this.recomputeSumScore()
      })
      return true
    },
    /** 复用当前账号上次新增的投喂记录（不含投喂量） */
    async reuseLastRecord() {
      if (this.editType !== 1 || this.reuseLoading) return
      this.reuseLoading = true
      try {
        let last = null
        const uid = this.userInfo && (this.userInfo.userId || this.userInfo.id)
        if (uid) {
          try {
            const res = await getZkFeedingLogPage({
              pageNo: 1,
              pageSize: 1,
              userId: uid
            })
            const list = (res && res.data && res.data.list) || []
            if (list.length) last = list[0]
          } catch (e) {
            console.warn('reuseLastRecord page', e)
          }
        }
        if (!last) last = this.readLastCreatedCache()
        if (!last) {
          this.$modal.msgWarning('暂无本账号上次新增的投喂记录可复用')
          return
        }
        this.applyReuseRecord(last)
        this.$modal.msgSuccess('已回填上次记录，请重新录入投喂量后提交')
      } finally {
        this.reuseLoading = false
      }
    },
    /** 新增时按搜索栏选中的车间/系统/池号预填存塘 */
    applySearchPondToForm() {
      const { workshopId, pondNum, pondDetailNum } = this.queryParams
      if (!pondDetailNum && !pondNum && !workshopId) return
      let candidate = null
      if (pondDetailNum) {
        candidate = this.pondList3.find(p => p.pondDetailNum == pondDetailNum)
      } else if (pondNum) {
        candidate = this.pondList3.find(p => p.pondNum == pondNum && (!workshopId || p.workshopId == workshopId))
      } else if (workshopId) {
        candidate = this.pondList3.find(p => p.workshopId == workshopId)
      }
      if (candidate) {
        this.form.pondId = candidate.id
        this.updatePondId(candidate.id)
      }
    },
    startVoiceScore() {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
      if (!SpeechRecognition) {
        this.$modal.msgWarning('当前浏览器不支持语音录入，请手动输入评分')
        return
      }
      this.stopVoiceScore()
      const recognition = new SpeechRecognition()
      recognition.lang = 'zh-CN'
      recognition.interimResults = false
      recognition.maxAlternatives = 1
      this.speechRecognition = recognition
      this.voiceListening = true
      recognition.onresult = (event) => {
        const transcript = (event.results[0] && event.results[0][0] && event.results[0][0].transcript) || ''
        const match = String(transcript).match(/(\d+(\.\d+)?)/)
        if (match) {
          this.form.personScore = match[1]
          this.personScoreBlur()
          this.recomputeSumScore()
          this.$modal.msgSuccess(`语音识别评分：${match[1]}`)
        } else {
          this.$modal.msgWarning(`未识别到数字评分，原文：${transcript || '空'}`)
        }
      }
      recognition.onerror = () => {
        this.voiceListening = false
        this.$modal.msgWarning('语音识别失败，请重试或手动输入')
      }
      recognition.onend = () => {
        this.voiceListening = false
      }
      recognition.start()
    },
    stopVoiceScore() {
      if (this.speechRecognition) {
        try { this.speechRecognition.stop() } catch (e) {}
        this.speechRecognition = null
      }
      this.voiceListening = false
    },
    personScoreBlur() {
      if(this.form.personScore<1) {
        this.form.personScore = 1;
        this.$modal.msgWarning(`人对摄食状态评分不能小于1`);
        return false;
      }
      if(this.form.personScore>100) {
        this.form.personScore = 100;
        this.$modal.msgWarning(`人对摄食状态评分不能大于100`);
        return false;
      }
      this.recomputeSumScore()
      return true;
    },
    sumScoreBlur() {
      if(this.form.sumScore<0) {
        this.form.sumScore = 0;
        this.$modal.msgWarning(`综合评分不能小于0`);
        return false;
      }
      if(this.form.sumScore>1) {
        this.form.sumScore = 1;
        this.$modal.msgWarning(`综合评分不能大于1`);
        return false;
      }
      return true;
    },
    update_warehouseId(currId) {
      let item = null;
      this.warehouses.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      if (!item) return
      // 2. 执行 赋值
      this.form.warehouseName = item.name || item.warehouseName;
      this.form.warehouseId = item.id;
    },
    onWarehouseChange(currId) {
      this.update_warehouseId(currId)
      const allowed = this.productsInWarehouse(currId)
      const nos = allowed.map(p => String(p.productNo))
      if (this.form.productNo && nos.length && !nos.includes(String(this.form.productNo))) {
        this.form.productNo = undefined
        this.form.feedSpec = undefined
        this.form.feedName = undefined
        this.form.productId = undefined
        this.currProduct = {}
      }
    },
    productsInWarehouse(warehouseId) {
      const all = this.productList || []
      if (warehouseId == null || warehouseId === '') return []
      return all.filter(p => this.resolveProductWarehouseIds(p).includes(String(warehouseId)))
    },
    findProductByNo(productNo) {
      if (productNo == null || productNo === '') return null
      return (this.productList || []).find(obj => String(obj.productNo) === String(productNo)) || null
    },
    /** 业务「几号料」：优先产品名称，不写系统条码/包装规格 */
    resolveFeedSpecLabel(product) {
      if (!product) return ''
      const name = String(product.productName || product.name || '').trim()
      if (name) return name
      // 兜底：名称缺失时才用条码，避免整栏空白
      return String(product.productNo || product.barCode || '').trim()
    },
    /** 列表展示：历史若存了 P 条码或包装规格，尽量回退到产品名 */
    displayFeedSpec(row) {
      if (!row) return '-'
      const spec = String(row.feedSpec || '').trim()
      const name = String(row.feedName || '').trim()
      if (!spec) return name || '-'
      // 系统条码形态
      if (/^P\d{10,}$/i.test(spec) && name) return name
      // 明显是包装规格（含 kg/袋、mm）且有产品名时，优先产品名
      if (name && (/kg\s*\/\s*袋/i.test(spec) || /\d+(\.\d+)?\s*mm/i.test(spec)) && spec !== name) {
        return name
      }
      return spec
    },
    resolveProductWarehouseIds(item) {
      if (!item) return []
      return String(item.warehouseIds || '')
        .split(',')
        .map(s => String(s).trim())
        .filter(Boolean)
    },
    /** 选择饲料后：若尚未选仓则落到有库存的仓；已选手动仓库则保留 */
    syncWarehouseForProduct(item) {
      if (!item) return
      const ids = this.resolveProductWarehouseIds(item)
      if (!ids.length) return
      const current = this.form.warehouseId
      const matched = current != null && ids.includes(String(current))
      if (matched) return
      if (current != null && current !== '') return
      const firstId = ids[0]
      const hit = (this.warehouses || []).find(w => String(w.id) === String(firstId))
      this.form.warehouseId = hit ? hit.id : (Number(firstId) || firstId)
      this.form.warehouseName = hit ? (hit.name || hit.warehouseName) : ''
    },
    get_warehouseList() {
      this.warehouses = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      let promise = new Promise((resolve, reject) => {
        getWarehousePage(params).then(response => {
          this.warehouses.push(...(response.data && response.data.list || []));
          resolve(this.warehouses);
        }).catch(reject)
      });
      return promise;
    },
    async flushChoose(force) {
      // 单项失败/超时不阻断其它下拉；总超时避免新增弹窗一直不出现
      const needPond = force || !(this.pondList && this.pondList.length)
      const needUser = force || !(this.userList1 && this.userList1.length)
      const needProduct = force || !(this.productList && this.productList.length)
      const needWh = force || !(this.warehouses && this.warehouses.length)
      if (!needPond && !needUser && !needProduct && !needWh) return
      if (this._flushChooseInflight) return this._flushChooseInflight
      const withTimeout = (p, ms, label) => Promise.race([
        Promise.resolve(p).catch(e => { console.warn(label, e); return null }),
        new Promise(resolve => setTimeout(() => { console.warn(label + ' timeout'); resolve(null) }, ms)),
      ])
      const tasks = []
      if (needPond) tasks.push(withTimeout(this.getPondList(), 8000, 'getPondList'))
      if (needUser) tasks.push(withTimeout(this.getUserList(), 8000, 'getUserList'))
      if (needProduct) tasks.push(withTimeout(this.getProductList(), 8000, 'getProductList'))
      if (needWh) tasks.push(withTimeout(this.get_warehouseList(), 8000, 'get_warehouseList'))
      this._flushChooseInflight = Promise.all(tasks).finally(() => {
        this._flushChooseInflight = null
      })
      await this._flushChooseInflight
    },
    onSearchWorkshopChange(workshopId) {
      this.queryParams.workshopId = (workshopId === null || workshopId === undefined) ? '' : workshopId
      this.queryParams.pondNum = ''
      this.queryParams.pondDetailNum = ''
      this.queryParams.pageNo = 1
      const ws = this.workshopList.find(w => String(w.workshopId) === String(workshopId))
      this.selectedWorkshopName = ws ? (ws.workshopName || '') : ''
      this.queryParams.logType = this.queryParams.workshopId !== '' ? -2.2 : ''
      this.getList()
    },
    onSearchSystemChange(pondNum) {
      this.queryParams.pondNum = pondNum || ''
      this.queryParams.pondDetailNum = ''
      this.queryParams.pageNo = 1
      if (pondNum) {
        const hit = (this.pondList || []).find(p => {
          const sameSystem = String(p.pondNum) === String(pondNum)
          if (!sameSystem) return false
          if (this.queryParams.workshopId === '' || this.queryParams.workshopId == null) return true
          return String(p.workshopId) === String(this.queryParams.workshopId)
        })
        if (hit) {
          this.queryParams.workshopId = hit.workshopId
          this.selectedWorkshopName = hit.workshopName || this.selectedWorkshopName
        }
        this.queryParams.logType = -2.3
      } else if (this.queryParams.workshopId !== '' && this.queryParams.workshopId != null) {
        this.queryParams.logType = -2.2
      } else {
        this.queryParams.logType = ''
      }
      this.getList()
    },
    onSearchPondChange(pondDetailNum) {
      this.queryParams.pondDetailNum = pondDetailNum || ''
      this.queryParams.pageNo = 1
      if (pondDetailNum) {
        const hit = (this.pondList || []).find(p => String(p.pondDetailNum) === String(pondDetailNum))
        if (hit) {
          this.queryParams.workshopId = hit.workshopId
          this.queryParams.pondNum = hit.pondNum
          this.selectedWorkshopName = hit.workshopName || this.selectedWorkshopName
        }
        this.queryParams.logType = -2.4
      } else if (this.queryParams.pondNum) {
        this.queryParams.logType = -2.3
      } else if (this.queryParams.workshopId !== '' && this.queryParams.workshopId != null) {
        this.queryParams.logType = -2.2
      } else {
        this.queryParams.logType = ''
      }
      this.getList()
    },
    querySearch(item, searchType) {
      if (searchType === -2.2) return this.onSearchWorkshopChange(item)
      if (searchType === -2.3) return this.onSearchSystemChange(item)
      if (searchType === -2.4) return this.onSearchPondChange(item)
      this.getList()
    },
    getPondInfo(list, workshopId, pondNum, pondDetailNum) {
      if(!(list && list.length)) return;
      let item = null;
      list.forEach((curr) => {
        if(workshopId != null && workshopId !== '' && String(workshopId) === String(curr.workshopId)) {
          item = curr;
          return;
        }
        if(pondNum != null && pondNum !== '' && String(pondNum) === String(curr.pondNum)) {
          item = curr;
          return;
        }
        if(pondDetailNum != null && pondDetailNum !== '' && String(pondDetailNum) === String(curr.pondDetailNum)) {
          item = curr;
          return;
        }
      })
      return item;
    },
    hasSearchFilter() {
      const q = this.queryParams
      return (q.workshopId !== '' && q.workshopId != null)
        || !!q.pondNum
        || !!q.pondDetailNum
        || (q.feedTime && q.feedTime.length === 2)
    },
    /** 只传有效筛选字段，避免空字符串干扰后端 */
    buildQueryParams(forRemote = true) {
      const q = this.queryParams
      const params = {
        pageNo: forRemote && this.hasSearchFilter() ? 1 : q.pageNo,
        pageSize: forRemote && this.hasSearchFilter() ? 500 : q.pageSize,
      }
      if (q.feedTime && q.feedTime.length === 2) {
        params.feedTime = q.feedTime
      }
      if (q.pondDetailNum) {
        params.logType = -2.4
        params.pondDetailNum = q.pondDetailNum
        if (q.pondNum) params.pondNum = q.pondNum
        if (q.workshopId !== '' && q.workshopId != null) params.workshopId = q.workshopId
        if (this.selectedWorkshopName) params.workshopName = this.selectedWorkshopName
      } else if (q.pondNum) {
        params.logType = -2.3
        params.pondNum = q.pondNum
        if (q.workshopId !== '' && q.workshopId != null) params.workshopId = q.workshopId
        if (this.selectedWorkshopName) params.workshopName = this.selectedWorkshopName
      } else if (q.workshopId !== '' && q.workshopId != null) {
        params.logType = -2.2
        params.workshopId = q.workshopId
        if (this.selectedWorkshopName) params.workshopName = this.selectedWorkshopName
      }
      return params
    },
    /** 前端兜底过滤：后端未按车间筛时仍保证列表正确 */
    applyLocalFilter(list) {
      const q = this.queryParams
      let result = Array.isArray(list) ? list.slice() : []
      const wid = q.workshopId
      const pondNum = q.pondNum
      const detail = q.pondDetailNum
      const wsName = this.selectedWorkshopName

      if (detail) {
        result = result.filter(r => String(r.pondDetailNum) === String(detail))
      } else if (pondNum) {
        result = result.filter(r => {
          if (String(r.pondNum) !== String(pondNum)) return false
          if (wid === '' || wid == null) return true
          if (r.workshopId != null && r.workshopId !== '') {
            return String(r.workshopId) === String(wid)
          }
          return !wsName || r.workshopName === wsName
        })
      } else if (wid !== '' && wid != null) {
        result = result.filter(r => {
          if (r.workshopId != null && r.workshopId !== '') {
            return String(r.workshopId) === String(wid)
          }
          // 部分历史数据只有车间名
          return !!wsName && r.workshopName === wsName
        })
      }

      if (q.feedTime && q.feedTime.length === 2) {
        const start = new Date(q.feedTime[0]).getTime()
        const end = new Date(q.feedTime[1]).getTime()
        result = result.filter(r => {
          const t = Number(r.feedTime)
          if (!Number.isFinite(t)) return true
          return t >= start && t <= end
        })
      }
      return result
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      const params = this.buildQueryParams(true)
      getZkFeedingLogPage(params).then(response => {
        let list = (response.data && response.data.list) || []
        list = this.applyLocalFilter(list)
        if (this.hasSearchFilter()) {
          this.filteredAllList = list
          this.total = list.length
          const size = this.queryParams.pageSize || 10
          const page = this.queryParams.pageNo || 1
          const start = (page - 1) * size
          this.list = list.slice(start, start + size)
        } else {
          this.filteredAllList = []
          this.list = list
          this.total = response.data.total
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      if (!item) return
      this.form.deptId = item.deptId;
      this.form.tenantId = item.tenantId;
      this.form.userName = item.nickname;
      this.form.userId = item.id;
    },
    getUserList() {
      this.userList1 = []
      let promise = new Promise((resolve, reject) => {
        listSimpleUsersByRole(KE_DE_ROLES).then(response => {
          this.userList1.push(...(response.data || []))
          this.userList1.forEach(obj => {
            let tempStr = `${obj.deptName}-${obj.nickname}`;
            obj.nickname = tempStr;
          })
          if(this.userInfo && this.userInfo.userId) this.updateUserId(this.userInfo.userId);
          resolve(this.userList1);
        }).catch(reject)
      });
      return promise;
    },
    updateProductId(currId){
      let item = null;
      this.productList.forEach(obj => {
        if(obj.productNo == currId) {
          item = obj;
          return;
        }
      })
      if (!item) {
        this.currProduct = {};
        return
      }
      // 规格栏存业务几号料（产品名称），不写系统条码、也不写 20kg/袋 包装规格
      this.form.feedName = item.productName || item.name;
      this.form.feedBrand = item.productBrand;
      this.form.feedSpec = this.resolveFeedSpecLabel(item);
      this.form.productNo = item.productNo;
      this.form.productId = item.id;
      this.form.feedingPrice = item.purchasePrice;
      this.currProduct = item;
      // 自动对齐有库存的仓库（避免默认仓与饲料库存仓不一致）
      this.syncWarehouseForProduct(item);
    },
    getProductList() {
      this.productList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
				categoryId: 1,
      }
      let promise = new Promise((resolve, reject) => {
        getProductPage(params).then(response => {
          this.productList.push(...(response.data && response.data.list || []))
          this.productList.forEach(obj => {
            const name = obj.productName || obj.name || ''
            const pack = obj.standard || obj.productSpec || obj.spec || ''
            let tempStr = `${name || '未命名饲料'}${pack ? '(' + pack + ')' : ''}-${obj.stockInfo?obj.stockInfo:'无库存'}`;
            obj.info = tempStr;
          })
          resolve(this.productList);
        }).catch(reject)
      });
      return promise;
    },
    updateLastFishSpec(pondId) {
      getLastProofing(pondId).then(response => {
        console.log("getLastProofing===========>"+JSON.stringify(response.data));
      })
    },
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      if(!item) return;
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.fishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.pondDetailNum = item.pondDetailNum;
      this.form.pondSystemName = item.pondSystemName;
      this.form.pondSystemId = item.pondSystemId;
      // 执行人：新增时固定当前登录账号，不覆盖为存塘负责人
      if (this.editType !== 1) {
        this.form.userName = item.userName;
        this.form.userId = item.userId;
        this.form.deptId = item.deptId;
        this.form.tenantId = item.tenantId;
      } else {
        this.applyLoginUserAsExecutor()
      }
      this.scheduleCalcDifferenceBefore()
    },
    getPondList() {
      this.pondList = []
      this.pondList3 = [];
      let promise = new Promise((resolve, reject) => {
        loadDeptScopedPondList().then(list => {
          const rows = Array.isArray(list) ? list.slice() : []
          this.pondList = rows
          this.pondList3 = rows.slice().sort((a, b) => (Number(b.fishNum) || 0) - (Number(a.fishNum) || 0))
          this.getWorkshopList();
          resolve(this.pondList3);
        }).catch(reject)
      });
      return promise;
    },
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.workshopList = [];
      this.systemList = [];
      this.pondList.forEach((item, index) => {
        if(!shopTemp.includes(item.workshopId)) {
          shopTemp.push(item.workshopId);
          this.workshopList.push(item);
        }
        if(!systemTemp.includes(item.pondNum)) {
          systemTemp.push(item.pondNum);
          this.systemList.push(item);
        }
      })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        pondNum: undefined,
        pondName: undefined,
        pondId: undefined,
        pondScale: undefined,
        fishNum: undefined,
        feedBrand: undefined,
        feedSpec: undefined,
        feedName: undefined,
        feedCount: undefined,
        feedTime: new Date().getTime(),
        remark: undefined,
        feedType: 1,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        warehouseId: undefined,
        productNo: undefined,
        productId: undefined,
        personScore: undefined,
        differenceBefore: undefined,
        sumScore: undefined,
      };
      let warehouse = this.warehouses.find(i => i.defaultStatus)
      if(warehouse) this.form = {...this.form, warehouseId: warehouse.id};
      this.applyLoginUserAsExecutor()
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.selectedWorkshopName = ''
      this.filteredAllList = []
      this.queryParams.logType = ''
      this.queryParams.workshopId = ''
      this.queryParams.pondNum = ''
      this.queryParams.pondDetailNum = ''
      this.resetForm("queryForm");
      this.handleQuery();
    },
    warehousesForProduct(productNo) {
      const item = this.findProductByNo(productNo)
      const ids = this.resolveProductWarehouseIds(item)
      if (!ids.length) return this.warehouses || []
      const filtered = (this.warehouses || []).filter(w => ids.includes(String(w.id)))
      return filtered.length ? filtered : (this.warehouses || [])
    },
    fillProductFieldsOnRow(row, product) {
      if (!row || !product) return
      row.productNo = product.productNo
      row.productId = product.id
      row.feedName = product.productName || product.name
      row.feedBrand = product.productBrand
      row.feedSpec = this.resolveFeedSpecLabel(product)
      row.feedingPrice = product.purchasePrice
      const ids = this.resolveProductWarehouseIds(product)
      if (ids.length && (row.warehouseId == null || row.warehouseId === '')) {
        const firstId = ids[0]
        const hit = (this.warehouses || []).find(w => String(w.id) === String(firstId))
        row.warehouseId = hit ? hit.id : (Number(firstId) || firstId)
      }
    },
    buildBatchRowFromPond(pond) {
      const product = this.findProductByNo(this.batchShared.productNo)
      const row = {
        pondId: pond.id,
        pondDetailNum: pond.pondDetailNum,
        pondName: pond.pondName,
        pondNum: pond.pondNum,
        pondScale: pond.pondScale,
        fishNum: pond.fishNum,
        workshopId: pond.workshopId,
        workshopName: pond.workshopName,
        pondSystemName: pond.pondSystemName,
        pondSystemId: pond.pondSystemId,
        productNo: '',
        productId: undefined,
        feedName: '',
        feedBrand: '',
        feedSpec: '',
        feedingPrice: undefined,
        warehouseId: this.batchShared.warehouseId,
        feedCount: undefined,
        feedType: this.batchShared.feedType != null ? this.batchShared.feedType : 1,
        personScore: this.batchShared.personScore,
        feedTime: this.batchShared.feedTime || new Date().getTime(),
      }
      if (product) this.fillProductFieldsOnRow(row, product)
      return row
    },
    rebuildBatchRows() {
      const wid = this.batchShared.workshopId
      const pondNum = this.batchShared.pondNum
      if (!pondNum) {
        this.batchRows = []
        return
      }
      let list = this.pondList3 && this.pondList3.length ? this.pondList3 : (this.pondList || [])
      list = list.filter(p => String(p.pondNum) === String(pondNum))
      if (wid !== null && wid !== undefined && wid !== '') {
        list = list.filter(p => String(p.workshopId) === String(wid))
      }
      this.batchRows = list.map(p => this.buildBatchRowFromPond(p))
    },
    onBatchWorkshopChange() {
      this.batchShared.pondNum = ''
      this.batchRows = []
    },
    onBatchSystemChange() {
      this.rebuildBatchRows()
    },
    onBatchDefaultWarehouseChange(warehouseId) {
      const allowed = this.productsInWarehouse(warehouseId).map(p => String(p.productNo))
      if (this.batchShared.productNo && allowed.length && !allowed.includes(String(this.batchShared.productNo))) {
        this.batchShared.productNo = ''
      }
      this.batchRows.forEach(row => {
        row.warehouseId = warehouseId
        const rowAllowed = this.productsInWarehouse(warehouseId).map(p => String(p.productNo))
        if (row.productNo && !rowAllowed.includes(String(row.productNo))) {
          row.productNo = ''
          row.feedSpec = ''
        }
      })
    },
    onBatchDefaultProductChange(productNo) {
      const product = this.findProductByNo(productNo)
      if (!product) return
      if (this.batchShared.warehouseId) return
      const ids = this.resolveProductWarehouseIds(product)
      if (ids.length) {
        const hit = (this.warehouses || []).find(w => String(w.id) === String(ids[0]))
        this.batchShared.warehouseId = hit ? hit.id : (Number(ids[0]) || ids[0])
      }
    },
    applyBatchDefaultsToRows() {
      if (!this.batchRows.length) {
        this.$modal.msgWarning('请先选择系统生成存塘行')
        return
      }
      const product = this.findProductByNo(this.batchShared.productNo)
      const sharedCount = this.batchShared.feedCount
      this.batchRows.forEach(row => {
        row.feedTime = this.batchShared.feedTime || row.feedTime || new Date().getTime()
        row.feedType = this.batchShared.feedType != null ? this.batchShared.feedType : row.feedType
        if (this.batchShared.personScore != null && this.batchShared.personScore !== '') {
          row.personScore = this.batchShared.personScore
        }
        if (sharedCount != null && sharedCount !== '' && Number(sharedCount) > 0) {
          row.feedCount = sharedCount
        }
        if (product) this.fillProductFieldsOnRow(row, product)
        else if (this.batchShared.warehouseId != null) row.warehouseId = this.batchShared.warehouseId
      })
      this.$modal.msgSuccess('已将共用默认值应用到全部行（可再逐行修改投喂量/饲料）')
    },
    onBatchRowProductChange(row, productNo) {
      const product = this.findProductByNo(productNo)
      if (!product) {
        row.productId = undefined
        row.feedSpec = ''
        row.feedName = ''
        return
      }
      this.fillProductFieldsOnRow(row, product)
    },
    onBatchRowWarehouseChange(row, warehouseId) {
      row.warehouseId = warehouseId
      const allowed = this.productsInWarehouse(warehouseId).map(p => String(p.productNo))
      if (row.productNo && allowed.length && !allowed.includes(String(row.productNo))) {
        row.productNo = ''
        row.feedSpec = ''
        row.productId = undefined
      }
    },
    removeBatchRow(index) {
      this.batchRows.splice(index, 1)
    },
    /** 批量新增入口：立刻弹窗，下拉后台补齐 */
    handleBatchAdd() {
      this.batchOpen = true
      this.batchShared = {
        workshopId: this.queryParams.workshopId || '',
        pondNum: this.queryParams.pondNum || '',
        feedTime: new Date().getTime(),
        feedType: 1,
        productNo: '',
        warehouseId: undefined,
        personScore: undefined,
        feedCount: undefined,
      }
      this.batchRows = []
      this.flushChoose(false).then(() => {
        if (!this.batchOpen) return
        if (!this.batchShared.warehouseId) {
          const warehouse = (this.warehouses || []).find(i => i.defaultStatus)
          if (warehouse) this.batchShared.warehouseId = warehouse.id
        }
        if (this.batchShared.pondNum) this.rebuildBatchRows()
      }).catch(() => {})
    },
    buildBatchPayload(row) {
      const uid = this.userInfo && (this.userInfo.userId || this.userInfo.id)
      let userName = ''
      let deptId = this.userInfo && this.userInfo.deptId
      if (uid) {
        const hit = (this.userList1 || []).find(u => String(u.id) === String(uid))
        if (hit) {
          userName = hit.nickname
          deptId = hit.deptId != null ? hit.deptId : deptId
        } else {
          userName = (this.userInfo && (this.userInfo.nickname || this.userInfo.username)) || ''
        }
      }
      return {
        pondId: row.pondId,
        pondDetailNum: row.pondDetailNum,
        pondName: row.pondName,
        pondNum: row.pondNum,
        pondScale: row.pondScale,
        fishNum: row.fishNum,
        workshopId: row.workshopId,
        workshopName: row.workshopName,
        pondSystemName: row.pondSystemName,
        pondSystemId: row.pondSystemId,
        productNo: row.productNo,
        productId: row.productId,
        feedName: row.feedName,
        feedBrand: row.feedBrand,
        feedSpec: row.feedSpec,
        feedingPrice: row.feedingPrice,
        warehouseId: row.warehouseId,
        feedCount: row.feedCount,
        feedType: row.feedType,
        personScore: row.personScore,
        feedTime: row.feedTime,
        userId: uid,
        userName,
        deptId,
        fid: getUUID().replaceAll('-', ''),
      }
    },
    async submitBatchForm() {
      if (this.batchSubmitting) return
      if (!this.batchRows.length) {
        this.$modal.msgWarning('没有可提交的存塘行')
        return
      }
      const invalid = []
      this.batchRows.forEach((row, idx) => {
        if (!row.pondId) invalid.push(`第${idx + 1}行缺少存塘`)
        else if (!row.productNo || !row.productId) invalid.push(`${row.pondDetailNum || ('第' + (idx + 1) + '行')}未选饲料`)
        else if (!row.warehouseId) invalid.push(`${row.pondDetailNum || ('第' + (idx + 1) + '行')}未选仓库`)
        else if (row.feedCount == null || row.feedCount === '' || !(Number(row.feedCount) > 0)) {
          invalid.push(`${row.pondDetailNum || ('第' + (idx + 1) + '行')}请填写投喂量(Kg)且须大于0`)
        } else if (!row.feedTime) invalid.push(`${row.pondDetailNum || ('第' + (idx + 1) + '行')}缺少时间`)
        else {
          const product = this.findProductByNo(row.productNo)
          const ids = this.resolveProductWarehouseIds(product)
          if (ids.length && !ids.includes(String(row.warehouseId))) {
            invalid.push(`${row.pondDetailNum}仓库与饲料库存不匹配`)
          }
        }
      })
      if (invalid.length) {
        this.$modal.msgWarning(invalid.slice(0, 3).join('；') + (invalid.length > 3 ? '…' : ''))
        return
      }
      this.batchSubmitting = true
      let ok = 0
      const errors = []
      try {
        for (let i = 0; i < this.batchRows.length; i++) {
          const row = this.batchRows[i]
          const payload = this.buildBatchPayload(row)
          try {
            await createZkFeedingLog(payload)
            ok += 1
            if (i === this.batchRows.length - 1) this.saveLastCreatedRecord(payload)
          } catch (e) {
            const msg = (e && (e.message || e.msg)) || '提交失败'
            errors.push(`${row.pondDetailNum || ('行' + (i + 1))}：${msg}`)
          }
        }
        if (ok > 0) {
          this.getList()
          notifyProductionRecordChanged({ type: 'feeding', source: 'zkFeedingLog-batch' })
        }
        if (!errors.length) {
          this.$modal.msgSuccess(`批量新增成功，共 ${ok} 条`)
          this.batchOpen = false
        } else if (ok > 0) {
          this.$modal.msgWarning(`成功 ${ok} 条，失败 ${errors.length} 条。${errors[0]}`)
        } else {
          this.$modal.msgError(errors[0] || '批量新增失败')
        }
      } finally {
        this.batchSubmitting = false
      }
    },
    /** 新增按钮操作：先立刻弹出，下拉数据后台补齐（不再等接口） */
    handleAdd() {
      this.editType = 1;
      this.reset();
      this.applySearchPondToForm();
      this.applyLoginUserAsExecutor();
      this.open = true;
      this.title = "添加投喂记录";
      this.form.fid = getUUID().replaceAll('-', '');
      // 后台补齐下拉；补齐后再尝试按搜索栏预填存塘 / 默认仓
      this.flushChoose(false).then(() => {
        if (!this.open || this.editType !== 1) return
        if (!this.form.pondId) this.applySearchPondToForm()
        if (!this.form.warehouseId) {
          const warehouse = (this.warehouses || []).find(i => i.defaultStatus)
          if (warehouse) this.form.warehouseId = warehouse.id
        }
        this.applyLoginUserAsExecutor()
      }).catch((e) => {
        console.warn('flushChoose background', e)
      })
    },
    /** 修正按钮操作 */
    handleUpdate(row) {
      this.editType = 2;
      this.reset();
      const id = row.id;
      // 先开弹窗再拉详情，避免「点修正没反应」的等待感
      this.open = true;
      this.title = "修正投喂记录";
      this.flushChoose(false);
      getZkFeedingLog(id).then(response => {
        this.form = response.data;
        this.form.originData = this.form.feedCount; // 原数据
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
        this.scheduleCalcDifferenceBefore()
        this.recomputeSumScore()
      }).catch(() => {
        this.open = false
        this.$modal.msgError('加载投喂记录失败')
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        if(this.editType == 2 && !this.form.reason && this.form.differenceData) {
          this.$modal.msgWarning(`修正原因不能为空`);
          return;
        }
        if (!this.form.productId || !this.form.warehouseId) {
          this.$modal.msgWarning('请选择投放饲料和饲料仓库');
          return;
        }
        // 提交前再对齐一次仓库，防止默认仓与库存仓不一致
        const product = this.currProduct && this.currProduct.id
          ? this.currProduct
          : this.findProductByNo(this.form.productNo)
        if (product) {
          this.syncWarehouseForProduct(product)
          const ids = this.resolveProductWarehouseIds(product)
          if (ids.length && !ids.includes(String(this.form.warehouseId))) {
            this.$modal.msgWarning('当前仓库没有该饲料库存，请改选库存所在仓库（见饲料名称后的仓库提示）');
            return;
          }
        }
        const stockCount = Number(
          (this.currProduct && (this.currProduct.stockCount != null ? this.currProduct.stockCount : this.currProduct.count))
        )
        if (Number.isFinite(stockCount) && stockCount <= 0) {
          this.$modal.msgWarning(`当前饲料没有库存`);
          return;
        }
        // console.log(`提交的数据为：${JSON.stringify(this.form)}`);
        // return;
        if(this.operationLoading) {
          this.$modal.msgWarning("不能重复请求！");
          return;
        }
        this.operationLoading = true;
        // 保存前：对比结果写成可读文案（与列表展示一致）
        const payload = { ...this.form }
        if (typeof payload.differenceBefore === 'number') {
          payload.differenceBefore = this.formatDifferenceBefore(payload.differenceBefore)
        }
        // 修正的提交
        if (this.form.id != null) {
          updateZkFeedingLog(payload).then(response => {
            this.$modal.msgSuccess("修正成功");
            this.open = false;
            setTimeout(()=>{
              this.operationLoading = false;
            }, 1000);
            this.getList();
          }).catch(() => { this.operationLoading = false });
          return;
        }
        // 添加的提交
        createZkFeedingLog(payload).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.saveLastCreatedRecord(payload);
          this.open = false;
          setTimeout(()=>{
            this.operationLoading = false;
          }, 1000);
          this.getList();
          notifyProductionRecordChanged({ type: 'feeding', pondId: payload.pondId, source: 'zkFeedingLog' });
        }).catch(() => { this.operationLoading = false });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除投喂记录编号为"' + id + '"的数据项?').then(function() {
          return deleteZkFeedingLog(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有投喂记录数据项?').then(() => {
          this.exportLoading = true;
          return exportZkFeedingLogExcel(params);
        }).then(response => {
          this.$download.excel(response, '投喂记录.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.reuse-last-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 16px;
  padding: 10px 12px;
  border-radius: 6px;
  background: rgba(103, 194, 58, 0.08);
  border: 1px solid rgba(103, 194, 58, 0.35);
}
.reuse-last-tip {
  font-size: 12px;
  color: #909399;
}
.batch-tip {
  margin: 0 0 12px;
  padding: 8px 10px;
  font-size: 12px;
  color: #909399;
  border-radius: 6px;
  background: rgba(64, 158, 255, 0.08);
  border: 1px solid rgba(64, 158, 255, 0.25);
}
.batch-shared-form {
  margin-bottom: 8px;
}
.batch-footer-tip {
  float: left;
  line-height: 32px;
  font-size: 12px;
  color: #909399;
}
.person-score-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.person-score-row .el-input {
  flex: 1;
}
</style>
