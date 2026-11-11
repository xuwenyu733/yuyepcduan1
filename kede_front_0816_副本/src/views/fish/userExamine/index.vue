<template>
  <div class="examine-page">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="logo-icon">
          <i class="el-icon-trophy"></i>
        </div>
        <div>
          <h1 class="title">考核评分标准</h1>
          <p class="subtitle">技术人员考核评分规范 · 知识库</p>
        </div>
      </div>
    </div>

    <div class="main-content">
      <!-- 一、考核总分及维度占比 -->
      <el-card class="section-card" shadow="never">
        <div slot="header" class="section-header">
          <span class="section-title"><i class="el-icon-data-board"></i> 一、考核总分及维度占比</span>
        </div>
        <p class="section-desc">
          本考核总分100分，由五大维度构成。各项评分独立核算，最终得分=各维度实际得分相加，作为技术人员考核等级评定的核心依据。<br>
          <span class="desc-note">（有数据的直接关联其他模块的数据，如考勤；没有数据的先保留对应评分接口）</span>
        </p>
        <div class="dimension-cards">
          <div v-for="(item, idx) in dimensions" :key="idx" class="dim-card" :style="{ borderTopColor: item.color }">
            <div class="dim-icon" :style="{ background: item.bg }">
              <i :class="item.icon"></i>
            </div>
            <div class="dim-info">
              <div class="dim-name">{{ item.name }}</div>
              <div class="dim-score">{{ item.score }}分</div>
              <div class="dim-percent">{{ item.percent }}</div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 二、各维度具体实施评分标准 -->
      <el-card class="section-card" shadow="never">
        <div slot="header" class="section-header">
          <span class="section-title"><i class="el-icon-notebook-2"></i> 二、各维度具体实施评分标准</span>
        </div>

        <!-- 维度折叠面板 -->
        <el-collapse v-model="activeNames" class="dim-collapse">
          <!-- （一）工作计划完成情况 -->
          <el-collapse-item name="plan">
            <template slot="title">
              <div class="collapse-title">
                <span class="collapse-tag" style="background: rgba(59, 130, 246, 0.2); color: #60a5fa;">25分</span>
                <span>（一）工作计划完成情况</span>
              </div>
            </template>
            <p class="dim-desc">以技术人员提交的工作计划为依据，结合实际完成成果、完成质量、延期原因等进行评分，由直属领导核算得分。</p>
            <div class="grade-list">
              <div class="grade-item grade-excellent">
                <div class="grade-head"><span class="grade-label">优秀</span><span class="grade-range">22-25分</span></div>
                <p>计划内所有任务100%完成，且核心任务（如水质优化方案落地、养殖品种病害防控、设备升级改造等）成果超出预期，经验证为养殖效益提升提供直接支撑；无任何延期任务，任务完成文档（如检测报告、操作记录）完整规范。</p>
              </div>
              <div class="grade-item grade-good">
                <div class="grade-head"><span class="grade-label">良好</span><span class="grade-range">18-21分</span></div>
                <p>计划内任务完成率≥90%，剩余≤10%的非核心任务因客观因素（如外部物资延迟到货、突发设备故障）未完成，且及时报备并提交补救方案；完成的任务质量达标，文档记录基本完整。</p>
              </div>
              <div class="grade-item grade-pass">
                <div class="grade-head"><span class="grade-label">合格</span><span class="grade-range">13-17分</span></div>
                <p>计划内任务完成率≥70%且＜90%，存在部分任务延期情况，其中核心任务延期不超过1项；完成的任务基本符合要求，存在少量文档记录不规范问题，经提醒可及时补充完善。</p>
              </div>
              <div class="grade-item grade-fail">
                <div class="grade-head"><span class="grade-label">不合格</span><span class="grade-range">0-12分</span></div>
                <p>计划内任务完成率＜70%，或核心任务延期超过2项；多项任务质量不达标，需返工整改；未按要求提交任务完成文档，或存在隐瞒任务未完成情况。</p>
              </div>
            </div>
          </el-collapse-item>

          <!-- （二）考勤 -->
          <el-collapse-item name="attendance">
            <template slot="title">
              <div class="collapse-title">
                <span class="collapse-tag" style="background: rgba(16, 185, 129, 0.2); color: #34d399;">10分</span>
                <span>（二）考勤</span>
              </div>
            </template>
            <p class="dim-desc">以厂区考勤系统记录及日常考勤登记为准，核心考核出勤稳定性、纪律性。</p>
            <div class="grade-list">
              <div class="grade-item grade-excellent">
                <div class="grade-head"><span class="grade-label">优秀</span><span class="grade-range">10分</span></div>
                <p>考核周期内全勤，无迟到、早退、旷工记录；因工作需求加班（如突发水质异常、夜间设备抢修）主动响应，无任何考勤纪律问题。</p>
              </div>
              <div class="grade-item grade-good">
                <div class="grade-head"><span class="grade-label">良好</span><span class="grade-range">8分</span></div>
                <p>考核周期内迟到/早退≤2次（单次时长≤20分钟），无旷工记录；加班响应积极，考勤记录完整。</p>
              </div>
              <div class="grade-item grade-pass">
                <div class="grade-head"><span class="grade-label">合格</span><span class="grade-range">6分</span></div>
                <p>考核周期内迟到/早退≤4次（单次时长≤20分钟），无旷工记录；考勤纪律基本符合要求。</p>
              </div>
              <div class="grade-item grade-fail">
                <div class="grade-head"><span class="grade-label">不合格</span><span class="grade-range">4分</span></div>
                <p>考核周期内存在旷工记录（旷工1天扣5分，以此类推，扣完为止）；迟到/早退超过4次，或单次迟到/早退时长＞30分钟；事假未提前报备、未完成工作交接，影响养殖生产进度。</p>
              </div>
            </div>
          </el-collapse-item>

          <!-- （三）考试 -->
          <el-collapse-item name="exam">
            <template slot="title">
              <div class="collapse-title">
                <span class="collapse-tag" style="background: rgba(139, 92, 246, 0.2); color: #a78bfa;">15分</span>
                <span>（三）考试</span>
              </div>
            </template>
            <p class="dim-desc">考试分为理论考试（占10分）和实操考试（占5分）。理论考试与考试系统关联，重点考核循环水养殖技术知识、水质调控原理、设备操作规范、病害防控常识等；实操考试由技术总监评分，重点考核水质检测操作、设备故障排查与基础维修、饵料投喂规范等核心技能。</p>
            <div class="grade-list">
              <div class="grade-item grade-excellent">
                <div class="grade-head"><span class="grade-label">优秀</span><span class="grade-range">13-15分</span></div>
                <p>理论考试得分≥90分，实操考试无失误，操作规范、高效，能快速解决实操过程中出现的基础问题（如检测仪器校准偏差、设备小故障）。</p>
              </div>
              <div class="grade-item grade-good">
                <div class="grade-head"><span class="grade-label">良好</span><span class="grade-range">11-12分</span></div>
                <p>理论考试得分≥80分且＜90分，实操考试基本规范，无重大失误，仅存在少量细节问题（如记录填写不及时），经提醒可立即修正。</p>
              </div>
              <div class="grade-item grade-pass">
                <div class="grade-head"><span class="grade-label">合格</span><span class="grade-range">8-10分</span></div>
                <p>理论考试得分≥60分且＜80分，实操考试能完成核心操作流程，存在1-2处非关键性失误（如操作步骤顺序偏差），经指导后可完成整改，不影响养殖安全。</p>
              </div>
              <div class="grade-item grade-fail">
                <div class="grade-head"><span class="grade-label">不合格</span><span class="grade-range">0-7分</span></div>
                <p>理论考试得分＜60分，或实操考试存在重大失误（如水质检测数据误差过大、设备操作不当可能导致故障），无法独立完成核心实操任务。</p>
              </div>
            </div>
          </el-collapse-item>

          <!-- （四）他人评分 -->
          <el-collapse-item name="rating">
            <template slot="title">
              <div class="collapse-title">
                <span class="collapse-tag" style="background: rgba(236, 72, 153, 0.2); color: #f472b6;">20分</span>
                <span>（四）他人评分（同事+领导）</span>
              </div>
            </template>
            <p class="dim-desc">评分主体分为同事（占8分）和领导（占12分），考核周期结束后由行政部门组织评分，取平均值作为最终得分（同事评分需至少收集3名同岗位/协作岗位同事的评分，领导评分由直属领导及厂区负责人共同评分）。核心考核团队协作、责任心、沟通效率、执行力等。</p>

            <!-- 同事评分 -->
            <div class="sub-section">
              <h4 class="sub-title"><i class="el-icon-user"></i> 1. 同事评分（8分）</h4>
              <div class="grade-list">
                <div class="grade-item grade-excellent">
                  <div class="grade-head"><span class="grade-label">优秀</span><span class="grade-range">7-8分</span></div>
                  <p>团队协作意识强，主动配合同事完成协作任务（如联合开展水质检测、设备维护）；在技术问题上主动分享经验，帮助同事解决难题；沟通高效，反馈问题及时，无协作矛盾。</p>
                </div>
                <div class="grade-item grade-good">
                  <div class="grade-head"><span class="grade-label">良好</span><span class="grade-range">6-7分</span></div>
                  <p>能配合同事完成协作任务，无推诿现象；沟通顺畅，能及时响应同事的需求；团队内关系融洽，无负面评价。</p>
                </div>
                <div class="grade-item grade-pass">
                  <div class="grade-head"><span class="grade-label">合格</span><span class="grade-range">4-5分</span></div>
                  <p>基本能配合完成协作任务，偶尔存在响应不及时情况；沟通基本顺畅，无重大协作矛盾；无明显负面评价。</p>
                </div>
                <div class="grade-item grade-fail">
                  <div class="grade-head"><span class="grade-label">不合格</span><span class="grade-range">0-3分</span></div>
                  <p>拒绝配合协作任务，或因个人原因导致协作任务延期、失误；沟通效率低，存在隐瞒问题、不反馈信息等情况；同事负面评价较多。</p>
                </div>
              </div>
            </div>

            <!-- 领导评分 -->
            <div class="sub-section">
              <h4 class="sub-title"><i class="el-icon-s-custom"></i> 2. 领导评分（12分）</h4>
              <div class="grade-list">
                <div class="grade-item grade-excellent">
                  <div class="grade-head"><span class="grade-label">优秀</span><span class="grade-range">10-12分</span></div>
                  <p>执行力强，能快速落实领导安排的工作任务，且成果达标；主动汇报工作进展，及时反馈问题及解决方案；责任心强，能主动发现养殖过程中的潜在风险（如水质异常、设备隐患）并提前处置。</p>
                </div>
                <div class="grade-item grade-good">
                  <div class="grade-head"><span class="grade-label">良好</span><span class="grade-range">8-9分</span></div>
                  <p>能按要求落实工作任务，执行力较好；工作进展汇报及时，对领导提出的问题能及时反馈；有基本的责任心，能完成本职范围内的风险排查。</p>
                </div>
                <div class="grade-item grade-pass">
                  <div class="grade-head"><span class="grade-label">合格</span><span class="grade-range">6-7分</span></div>
                  <p>经提醒后能落实工作任务，无重大失误；能按要求汇报工作进展；责任心一般，基本能完成本职工作，对潜在风险敏感度不足。</p>
                </div>
                <div class="grade-item grade-fail">
                  <div class="grade-head"><span class="grade-label">不合格</span><span class="grade-range">0-5分</span></div>
                  <p>多次拒绝或拖延落实工作任务，出现重大工作失误；不主动汇报工作，对领导询问的问题回避、隐瞒；责任心缺失，因个人疏忽导致养殖安全风险或生产损失。</p>
                </div>
              </div>
            </div>
          </el-collapse-item>

          <!-- （五）工作绩效 -->
          <el-collapse-item name="performance">
            <template slot="title">
              <div class="collapse-title">
                <span class="collapse-tag" style="background: rgba(245, 158, 11, 0.2); color: #fbbf24;">30分</span>
                <span>（五）工作绩效</span>
              </div>
            </template>
            <p class="dim-desc">核心考核养殖效益相关指标，由技术部门联合财务部门核算，以考核周期内（主要是年度）的实际养殖数据为依据，各项指标权重及评分标准如下（总分30分）：</p>

            <!-- 水质合格率 -->
            <div class="sub-section">
              <h4 class="sub-title"><i class="el-icon-c-scale-to-original"></i> 水质合格率（6分）</h4>
              <p class="sub-desc">以每日水质检测数据（溶解氧、pH值、氨氮、亚硝酸盐等关键指标）为依据，合格率=达标天数/总养殖天数×100%。</p>
              <div class="metric-rules">
                <div class="metric-rule"><span class="rule-tag rule-a">≥98%</span><span>6分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-b">95% ~ 98%</span><span>4-5分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-c">90% ~ 95%</span><span>2-3分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-d">＜90%</span><span>0-1分</span></div>
              </div>
            </div>

            <!-- 设备故障率 -->
            <div class="sub-section">
              <h4 class="sub-title"><i class="el-icon-warning-outline"></i> 设备故障率（6分）</h4>
              <p class="sub-desc">考核循环水系统、增氧设备、投喂设备等核心设备，故障率=故障时长/总运行时长×100%。</p>
              <div class="metric-rules">
                <div class="metric-rule"><span class="rule-tag rule-a">≤1%</span><span>6分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-b">1% ~ 3%</span><span>4-5分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-c">3% ~ 5%</span><span>2-3分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-d">＞5%</span><span>0-1分</span></div>
              </div>
              <p class="sub-note">注：因操作不当导致的故障双倍扣分。</p>
            </div>

            <!-- 存活率 -->
            <div class="sub-section">
              <h4 class="sub-title"><i class="el-icon-fish"></i> 存活率（14分）</h4>
              <p class="sub-desc">存活率=期末养殖生物数量/期初投放数量×100%，按鱼的生长阶段设定不同基准存活率。</p>
              <el-table :data="survivalData" border class="dark-table" size="small">
                <el-table-column prop="stage" label="生长阶段" min-width="160" />
                <el-table-column prop="base" label="基准存活率" width="120" align="center" />
                <el-table-column prop="fullScore" label="满分(3.5分)" min-width="200" />
                <el-table-column prop="midScore" label="中等(2-2.8分)" min-width="180" />
                <el-table-column prop="lowScore" label="较低(1-1.7分)" min-width="180" />
                <el-table-column prop="failScore" label="不合格(0-0.7分)" min-width="160" />
              </el-table>
              <p class="sub-note">最终存活率得分=各生长阶段实际得分相加。</p>
            </div>

            <!-- 饵料系数 -->
            <div class="sub-section">
              <h4 class="sub-title"><i class="el-icon-food"></i> 饵料系数（4分）</h4>
              <p class="sub-desc">饵料系数=总投喂饵料重量/总增重重量，以行业平均饵料系数为基准。</p>
              <div class="metric-rules">
                <div class="metric-rule"><span class="rule-tag rule-a">≤基准值-0.1</span><span>4分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-b">基准值±0.1</span><span>2-3分</span></div>
                <div class="metric-rule"><span class="rule-tag rule-d">＞基准值+0.1</span><span>0-1分</span></div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-card>

      <!-- 三、补充说明 -->
      <el-card class="section-card" shadow="never">
        <div slot="header" class="section-header">
          <span class="section-title"><i class="el-icon-info"></i> 三、补充说明</span>
        </div>
        <div class="note-list">
          <div class="note-item note-cycle">
            <div class="note-icon"><i class="el-icon-date"></i></div>
            <div class="note-content">
              <h4>考核周期</h4>
              <p>可根据厂区生产节奏设定为月度、季度或年度。建议月度考核侧重考勤、工作计划完成情况；季度考核增加考试、他人评分；年度考核重点核算工作绩效。</p>
            </div>
          </div>
          <div class="note-item note-bonus">
            <div class="note-icon"><i class="el-icon-circle-plus"></i></div>
            <div class="note-content">
              <h4>加分项</h4>
              <p>考核周期内，技术人员提出合理化建议（如水质优化、设备改进）并被采纳，为厂区降低成本、提升效益的，可额外加<b>2-5分</b>；在突发养殖事故（如大规模病害、水质崩溃）中表现突出，成功挽回损失的，加<b>3-8分</b>。</p>
            </div>
          </div>
          <div class="note-item note-penalty">
            <div class="note-icon"><i class="el-icon-circle-close"></i></div>
            <div class="note-content">
              <h4>减分项</h4>
              <p>因个人操作失误导致水质恶化、设备损坏、养殖生物死亡等生产损失的，按损失金额的<b>1%-5%</b>折算扣分（扣完对应维度得分为止）；违反厂区安全操作规范、环保规定的，每次扣<b>2-3分</b>。</p>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    <div style="width: 100%; height: 30px;"></div>
  </div>
</template>

<script>
export default {
  name: 'UserExamine',
  data() {
    return {
      activeNames: ['plan', 'attendance', 'exam', 'rating', 'performance'],
      dimensions: [
        { name: '工作计划完成情况', score: '25', percent: '25%', icon: 'el-icon-document', color: '#3b82f6', bg: 'rgba(59, 130, 246, 0.15)' },
        { name: '考勤', score: '10', percent: '10%', icon: 'el-icon-time', color: '#10b981', bg: 'rgba(16, 185, 129, 0.15)' },
        { name: '考试', score: '15', percent: '15%', icon: 'el-icon-reading', color: '#8b5cf6', bg: 'rgba(139, 92, 246, 0.15)' },
        { name: '他人评分', score: '20', percent: '20%', icon: 'el-icon-user', color: '#ec4899', bg: 'rgba(236, 72, 153, 0.15)' },
        { name: '工作绩效', score: '30', percent: '30%', icon: 'el-icon-data-line', color: '#f59e0b', bg: 'rgba(245, 158, 11, 0.15)' }
      ],
      survivalData: [
        { stage: '幼苗期（卵~5g）', base: '90%', fullScore: '存活率≥90%', midScore: '89%~92%', lowScore: '87%~89%', failScore: '＜87%' },
        { stage: '生长期一（5g~50g）', base: '95%', fullScore: '存活率≥95%', midScore: '94%~97%', lowScore: '92%~94%', failScore: '＜92%' },
        { stage: '生长期二（50g~500g）', base: '97%', fullScore: '存活率≥97%', midScore: '96%~99%', lowScore: '94%~96%', failScore: '＜94%' },
        { stage: '生长期三（500g~5000g）', base: '98%', fullScore: '存活率≥98%', midScore: '97%~100%', lowScore: '95%~97%', failScore: '＜95%' }
      ]
    };
  }
};
</script>

<style scoped lang="scss">
.examine-page {
  min-height: calc(100vh - 84px);
  background: #020617;
  color: #e2e8f0;
  padding-bottom: 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* 头部 */
.page-header {
  background-color: #0f172a;
  border-bottom: 1px solid #1e293b;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.5);
  padding: 24px 32px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(to bottom right, #f59e0b, #ef4444);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  box-shadow: 0 0 15px rgba(245, 158, 11, 0.5);
}

.title {
  margin: 0;
  font-size: 24px;
  color: #f8fafc;
  font-weight: 600;
  letter-spacing: 1px;
}

.subtitle {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #94a3b8;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Element UI 深色覆盖 */
::v-deep .el-card {
  background-color: #0f172a;
  border: 1px solid #1e293b;
  color: #e2e8f0;
}

::v-deep .el-card__header {
  border-bottom: 1px solid #1e293b;
}

/* 区块卡片 */
.section-card {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  align-items: center;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #f8fafc;
  display: flex;
  align-items: center;
  gap: 8px;

  i {
    color: #f59e0b;
  }
}

.section-desc {
  font-size: 14px;
  color: #cbd5e1;
  line-height: 1.7;
  margin: 0 0 16px 0;
}

.desc-note {
  color: #94a3b8;
  font-size: 13px;
}

/* 维度占比卡片 */
.dimension-cards {
  margin-top: 8px;
  display: flex;
  gap: 16px;
}

.dim-card {
  flex: 1;
  min-width: 0;
  background: linear-gradient(145deg, #0f172a, #1e293b);
  border: 1px solid #1e293b;
  border-top: 3px solid #3b82f6;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
  }
}

.dim-icon {
  width: 42px;
  height: 42px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.dim-info {
  flex: 1;
  min-width: 0;
}

.dim-name {
  font-size: 13px;
  color: #94a3b8;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dim-score {
  font-size: 22px;
  font-weight: bold;
  color: #f8fafc;
}

.dim-percent {
  font-size: 12px;
  color: #64748b;
}

/* 折叠面板 */
::v-deep .el-collapse {
  border: none;
}

::v-deep .el-collapse-item__header {
  background-color: #0f172a;
  border-bottom: 1px solid #1e293b;
  color: #e2e8f0;
  font-size: 15px;
  padding: 0 12px;
  height: 56px;
  line-height: 56px;

  &:hover {
    background-color: #1e293b;
  }
}

::v-deep .el-collapse-item__wrap {
  background-color: #0f172a;
  border-bottom: 1px solid #1e293b;
}

::v-deep .el-collapse-item__content {
  padding: 16px 8px 20px 8px;
  color: #e2e8f0;
}

::v-deep .el-collapse-item__header i.el-collapse-item__arrow {
  color: #94a3b8;
}

.collapse-title {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-right: 20px;
}

.collapse-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
  line-height: 1;
}

.dim-desc {
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.7;
  margin: 0 0 14px 0;
  padding: 10px 14px;
  background: rgba(15, 23, 42, 0.6);
  border-left: 3px solid #3b82f6;
  border-radius: 0 6px 6px 0;
}

/* 子区块 */
.sub-section {
  margin-top: 20px;

  &:first-child {
    margin-top: 0;
  }
}

.sub-title {
  font-size: 15px;
  color: #f8fafc;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 6px;

  i {
    color: #60a5fa;
  }
}

.sub-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0 0 10px 0;
  line-height: 1.6;
}

.sub-note {
  font-size: 12px;
  color: #fbbf24;
  margin: 8px 0 0 0;
}

/* 等级列表 */
.grade-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.grade-item {
  border-radius: 8px;
  padding: 12px 14px;
  border: 1px solid #1e293b;

  p {
    margin: 8px 0 0 0;
    font-size: 13px;
    line-height: 1.7;
    color: #cbd5e1;
  }
}

.grade-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.grade-label {
  font-size: 14px;
  font-weight: 600;
}

.grade-range {
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
}

.grade-excellent {
  background: rgba(16, 185, 129, 0.08);
  border-color: rgba(16, 185, 129, 0.3);
  .grade-label { color: #34d399; }
}

.grade-good {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.3);
  .grade-label { color: #60a5fa; }
}

.grade-pass {
  background: rgba(245, 158, 11, 0.08);
  border-color: rgba(245, 158, 11, 0.3);
  .grade-label { color: #fbbf24; }
}

.grade-fail {
  background: rgba(239, 68, 68, 0.08);
  border-color: rgba(239, 68, 68, 0.3);
  .grade-label { color: #f87171; }
}

/* 指标规则 */
.metric-rules {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}

.metric-rule {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid #1e293b;
  border-radius: 6px;
  padding: 8px 14px;
  font-size: 13px;
  color: #e2e8f0;
}

.rule-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 4px;
  font-weight: 600;
  font-size: 12px;
}

.rule-a { background: rgba(16, 185, 129, 0.2); color: #34d399; }
.rule-b { background: rgba(59, 130, 246, 0.2); color: #60a5fa; }
.rule-c { background: rgba(245, 158, 11, 0.2); color: #fbbf24; }
.rule-d { background: rgba(239, 68, 68, 0.2); color: #f87171; }

/* 深色表格 */
::v-deep .dark-table {
  background: transparent;

  th, td {
    background-color: #0f172a !important;
    border-color: #1e293b !important;
    color: #e2e8f0 !important;
  }

  th {
    color: #94a3b8 !important;
    font-weight: 600;
  }

  tr:hover td {
    background-color: #1e293b !important;
  }
}

/* 补充说明 */
.note-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.note-item {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #1e293b;
}

.note-cycle {
  background: rgba(59, 130, 246, 0.06);
  border-color: rgba(59, 130, 246, 0.25);
}

.note-bonus {
  background: rgba(16, 185, 129, 0.06);
  border-color: rgba(16, 185, 129, 0.25);
}

.note-penalty {
  background: rgba(239, 68, 68, 0.06);
  border-color: rgba(239, 68, 68, 0.25);
}

.note-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.note-cycle .note-icon { background: rgba(59, 130, 246, 0.2); color: #60a5fa; }
.note-bonus .note-icon { background: rgba(16, 185, 129, 0.2); color: #34d399; }
.note-penalty .note-icon { background: rgba(239, 68, 68, 0.2); color: #f87171; }

.note-content {
  h4 {
    margin: 0 0 6px 0;
    font-size: 15px;
    color: #f8fafc;
  }

  p {
    margin: 0;
    font-size: 13px;
    color: #cbd5e1;
    line-height: 1.7;
  }

  b {
    color: #f8fafc;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .dimension-cards {
    flex-wrap: wrap;
  }
  .dim-card {
    flex: 1 1 45%;
  }
  .grade-list {
    grid-template-columns: 1fr;
  }
}
</style>
