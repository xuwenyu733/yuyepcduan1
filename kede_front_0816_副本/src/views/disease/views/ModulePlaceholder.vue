<template>
  <div class="placeholder-page">
    <h2 class="placeholder-title">{{ title }}</h2>
    <p class="placeholder-desc">该模块正在建设中，敬请期待。</p>
  </div>
</template>

<script>
export default {
  name: 'ModulePlaceholder',
  computed: {
    title() {
      return this.$route.meta.title || '功能模块'
    }
  }
}
</script>

<style scoped>
.placeholder-page {
  min-height: 360px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
}

.placeholder-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1d2129;
}

.placeholder-desc {
  margin: 0;
  font-size: 14px;
  color: #86909c;
}
</style>
