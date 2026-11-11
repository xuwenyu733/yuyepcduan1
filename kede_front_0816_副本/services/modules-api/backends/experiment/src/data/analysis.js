const { listExperiments } = require('./experiments')
const { listRecords } = require('./collection')

function getSummary({ experimentId, startDate, endDate } = {}) {
  let experiments = listExperiments({})
  if (experimentId) {
    experiments = experiments.filter(item => item.id === experimentId)
  }

  let records = listRecords({ experimentId })
  if (startDate) {
    records = records.filter(item => item.createdAt.slice(0, 10) >= startDate)
  }
  if (endDate) {
    records = records.filter(item => item.createdAt.slice(0, 10) <= endDate)
  }

  // Demo metrics: derived from experiment count and collection records
  const base = experiments.length
  const recordCount = records.length
  const avgGrowthRate = base ? Math.min(100, 12 + recordCount * 3.5) : 0
  const avgSurvivalRate = base ? Math.min(100, 88 + recordCount * 1.2) : 0
  const avgFcr = base ? Number((1.2 + recordCount * 0.05).toFixed(2)) : 0
  const totalMortality = base ? recordCount * 2 : 0

  const trend = records.slice(0, 6).map((item, index) => ({
    date: item.createdAt.slice(0, 10),
    growthRate: Math.min(100, 10 + (6 - index) * 8)
  }))

  return {
    avgGrowthRate,
    avgSurvivalRate,
    avgFcr,
    totalMortality,
    trend
  }
}

module.exports = {
  getSummary
}
