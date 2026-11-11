/**
 * 协同诊断：是否可提交诊断结论（待诊断 / 已完成不展示）
 * @param {string} status
 * @returns {boolean}
 */
export function canSubmitDiagnosisConclusion(status) {
  if (!status) return false
  return status !== 'resolved' && status !== 'pending'
}
