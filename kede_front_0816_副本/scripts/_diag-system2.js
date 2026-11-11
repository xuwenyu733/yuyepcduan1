const fs = require('fs')
const path = 'C:/Users/董鉴华/Desktop/测试_副本5/kede_front_0816/src/views/fish/zkDeathSurvivalRate/system.vue'
const s = fs.readFileSync(path, 'utf8')
const script = s.split('<script>')[1].split('</script>')[0]
const tmpl = s.split('<template>')[1].split('</template>')[0]

const issues = []
function need(cond, msg) { if (!cond) issues.push(msg) }

// Template refs that must exist
;[
  'pageTitle','pageSubtitle','lastUpdateTime','handleRefresh','pageLoading',
  'filterWorkshop','workshopOptions','onFilterWorkshopChange',
  'filterSystem','systemOptions','onFilterSystemChange',
  'filterPond','pondOptions','onFilterPondChange','onFilterPondClear',
  'filterSpecies','speciesOptions','filterBatch','batchOptions','filterSpec','specOptions',
  'applyFilter','resetFilter','goThresholdPage','activeTab','isHealthOnlyPage',
  'attentionHealthPools','pushingHealthAlarms','pushAttentionHealthAlarms',
  'healthGrade','filteredFishPoolData','healthWeights',
  'hasMortalityScopeFilter','filteredTodayDeaths','filteredWeekDeaths','filteredMonthDeaths','filteredAvgMortality',
  'filteredMortalityData','deathWarnThreshold','plainText'
].forEach(name => {
  need(tmpl.includes(name), 'template missing ref: ' + name)
  need(script.includes(name), 'script missing symbol: ' + name)
})

// Broken aliases
need(!script.includes('buildWorkshopOptionLabel(pool)') || script.includes('function buildWorkshopOptionLabel') || script.includes('buildWorkshopOptionLabel }'), 'ok')
need(script.includes('getHealthStatusDistribution'), 'uses getHealthStatusDistribution')
need(script.includes("url: '/fish/zk-pond-health/health-status-distribution'") || fs.readFileSync('C:/Users/董鉴华/Desktop/测试_副本5/kede_front_0816/src/api/fish/zkPondHealth.js','utf8').includes('health-status-distribution'), 'api ok')

// Check applyHealthThresholdWeights field names vs template
need(tmpl.includes('environmentalLayerRating'), 'tmpl env rating')
need(script.includes('envScaled') || script.includes('environmentalLayerRating'), 'script sets env rating')

// pageTitle computed?
need(/pageTitle\s*\(/.test(script) || /pageTitle\s*:/.test(script), 'pageTitle defined')
need(/pageSubtitle\s*\(/.test(script) || /pageSubtitle\s*:/.test(script), 'pageSubtitle defined')
need(/lastUpdateTime\s*\(/.test(script) || /lastUpdateTime\s*:/.test(script), 'lastUpdateTime defined')
need(/healthWeights\s*\(/.test(script) || /healthWeights\s*:/.test(script), 'healthWeights defined')

console.log('ISSUES ('+issues.length+'):')
issues.forEach(i => console.log(' -', i))
