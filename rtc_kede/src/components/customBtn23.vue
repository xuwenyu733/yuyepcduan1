<script setup lang="ts">
defineProps<{ togglePanel?: () => void }>();
</script>

<template>
  <div @click="togglePanel?.()">提交至知识库</div>
</template>

<style scoped lang="scss">

</style>
