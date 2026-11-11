<template>
  <button type="button" class="menu-toggle" aria-label="打开导航菜单" @click="openNav">
    <span class="menu-toggle-bar" />
    <span class="menu-toggle-bar" />
    <span class="menu-toggle-bar" />
  </button>
</template>

<script>
export default {
  name: 'MenuToggle',
  inject: {
    openNav: {
      default: () => () => {}
    }
  }
}
</script>

<style scoped>
.menu-toggle {
  display: inline-flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  width: 36px;
  height: 36px;
  padding: 8px;
  margin: 0;
  border: none;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 0.15s;
}

.menu-toggle:hover {
  background: rgba(64, 158, 255, 0.1);
}

.menu-toggle-bar {
  display: block;
  width: 18px;
  height: 2px;
  border-radius: 1px;
  background: #e8edf2;
}
</style>
