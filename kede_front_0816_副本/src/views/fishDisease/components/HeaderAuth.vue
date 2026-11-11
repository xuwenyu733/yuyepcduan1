<template>
  <div class="header-auth">
    <div class="header-date">{{ todayText }}</div>
  </div>
</template>

<script>
export default {
  name: 'HeaderAuth',
  computed: {
    todayText() {
      const now = new Date()
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日${weekDays[now.getDay()]}`
    }
  }
}
</script>

<style scoped>
.header-auth {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.header-date {
  font-size: 14px;
  color: #7c8fa1;
  white-space: nowrap;
  line-height: 1.4;
}
</style>
