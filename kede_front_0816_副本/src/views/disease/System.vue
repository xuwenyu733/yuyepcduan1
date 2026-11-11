<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style scoped lang="scss">
:root {
  --primary: #1890ff;
  --primary-hover: #40a9ff;
  --primary-active: #096dd9;
  --primary-light: #e6f7ff;
  --primary-border: #91d5ff;
  --ink: rgba(0, 0, 0, 0.85);
  --text-secondary: rgba(0, 0, 0, 0.45);
  --text-disabled: rgba(0, 0, 0, 0.25);
  --border: #f0f0f0;
  --bg-page: #f0f2f5;
  --bg-card: #ffffff;
  --radius: 8px;
  --radius-lg: 12px;
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px rgba(0, 0, 0, 0.02);
}

* {
  box-sizing: border-box;
}

html,
body,
#app {
  margin: 0;
  padding: 0;
  min-height: 100%;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  background: var(--bg-page);
  color: rgba(0, 0, 0, 0.65);
  font-size: 14px;
  -webkit-font-smoothing: antialiased;
}

:deep(.page-card) {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
}

:deep(.section-title) {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
}

:deep(.section-desc) {
  margin: 0;
  font-size: 13px;
  color: var(--text-secondary);
}

:deep(.page-head) {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

:deep(.empty-box) {
  padding: 48px 16px;
  text-align: center;
  color: var(--text-secondary);
}

:deep(.el-button--primary) {
  background-color: var(--primary);
  border-color: var(--primary);
}

:deep(.el-button--primary:focus),
:deep(.el-button--primary:hover) {
  background: var(--primary-hover);
  border-color: var(--primary-hover);
}

:deep(.btn-dark) {
  background: #1d2129;
  border-color: #1d2129;
  color: #fff;
  border-radius: 10px;
  padding: 10px 18px;
  font-weight: 500;
}

.btn-dark:hover,
.btn-dark:focus {
  background: #4e5969;
  border-color: #4e5969;
  color: #fff;
}

.btn-cancel {
  background: #fff;
  border: 1px solid #e5e6eb;
  color: #1d2129;
  border-radius: 10px;
  padding: 10px 18px;
  font-weight: 500;
}

.btn-cancel:hover,
.btn-cancel:focus {
  background: #f7f8fa;
  border-color: #d0d3d9;
  color: #1d2129;
}

.btn-save {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-save .save-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.btn-save.is-loading .save-icon {
  display: none;
}

.disease-dialog .dialog-footer {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.disease-dialog .el-dialog__footer {
  text-align: right;
  padding: 12px 24px 20px;
}

.config-select-dropdown.el-select-dropdown {
  border: none;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  margin-top: 6px !important;
  padding: 6px;
  overflow: hidden;
}

.config-select-dropdown .el-select-dropdown__list {
  padding: 0;
}

.config-select-dropdown .el-select-dropdown__item {
  height: 40px;
  line-height: 40px;
  padding: 0 14px;
  border-radius: 8px;
  color: #1d2129;
  font-size: 14px;
}

.config-select-dropdown .el-select-dropdown__item.hover,
.config-select-dropdown .el-select-dropdown__item:hover {
  background: #f2f3f5;
}

.config-select-dropdown .el-select-dropdown__item.selected {
  color: #1d2129;
  font-weight: 500;
  background: #f2f3f5;
}

.el-tag--primary {
  background: var(--primary-light);
  border-color: var(--primary-border);
  color: var(--primary);
}

.el-input__inner,
.el-textarea__inner {
  border-radius: var(--radius);
}

.el-table th {
  background: #fafafa !important;
  color: var(--ink);
  font-weight: 600;
}

.disease-dialog {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.disease-dialog .el-dialog__header {
  padding: 16px 24px 8px;
  border-bottom: 1px solid var(--border);
}

.disease-dialog .el-dialog__body {
  padding: 16px 24px;
}

.disease-dialog .el-dialog__footer {
  padding: 12px 24px 16px;
  border-top: 1px solid var(--border);
}
</style>
