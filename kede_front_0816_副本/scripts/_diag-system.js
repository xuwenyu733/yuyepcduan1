const fs = require('fs')
const path = 'C:/Users/董鉴华/Desktop/测试_副本5/kede_front_0816/src/views/fish/zkDeathSurvivalRate/system.vue'
const s = fs.readFileSync(path, 'utf8')
const script = s.split('<script>')[1].split('</script>')[0]
const imports = script.match(/import[\s\S]*?from\s+['"][^'"]+['"]/g) || []
console.log('=== IMPORTS ===')
imports.forEach(i => console.log(i.replace(/\s+/g, ' ').slice(0, 200)))

const used = [
  'loadWorkshopRegistry', 'resolveWorkshopScope', 'textMatchesWorkshopScope',
  'buildWorkshopOptionLabel', 'buildSystemOptionLabel', 'buildSiteDisplayLabel', 'withTimeout',
  'getThresholdModule', 'resolveHealthGrades', 'resolveHealthWeights', 'recomputeHealthOverall',
  'getZkPondPage', 'getHealthStatusDistribution', 'getLatestPondHealthList',
  'getZkDeathSurvivalRateStatistics', 'siteLabelsEqual', 'normalizeLakeName',
  'handleRefresh', 'bootstrapRouteFilters', 'applyRouteSiteFilters',
  'fishPoolData', 'topStats', 'mortalityData', 'healthWeights',
  'attentionHealthPools', 'updateHealthCharts', 'isHealthOnlyPage',
  'pushingHealthAlarms', 'html2Text', 'pushAlarmAndCreateTask', 'ROUTES',
  'pageTitle', 'activeTab', 'filteredFishPoolData', 'hasMortalityScopeFilter',
  'initCharts', 'updateCharts', 'getPondName', 'formatWorkshop'
]
console.log('\n=== USED ===')
used.forEach(n => console.log((script.includes(n) ? 'Y' : 'N'), n))

// Find undefined method calls that look wrong
const callRe = /this\.([a-zA-Z_][a-zA-Z0-9_]*)\s*\(/g
const methods = new Set()
const mblock = script.match(/methods\s*:\s*\{([\s\S]*)\}\s*,\s*(?:watch|computed|created|mounted|beforeDestroy|activated|components)/)
// simpler: extract method names
const methodNames = [...script.matchAll(/^\s{4}(?:async\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\s*\(/gm)].map(m => m[1])
console.log('\n=== METHOD DEFS sample ===')
console.log(methodNames.slice(0, 80).join(', '))

const dataKeys = []
const dm = script.match(/data\s*\(\s*\)\s*\{\s*return\s*\{([\s\S]*?)\n\s*\}\s*\n\s*\}/)
if (dm) {
  const keys = [...dm[1].matchAll(/^\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*:/gm)].map(x => x[1])
  console.log('\n=== DATA KEYS ===')
  console.log(keys.join(', '))
}
