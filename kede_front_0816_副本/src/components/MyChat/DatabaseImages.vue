<template>
  <div v-if="images.length" class="database-images">
    <button type="button" class="image-toggle" @click="toggle">{{ expanded ? '收起图片' : `查看 ${images.length} 张已存图片` }}</button>
    <div v-if="expanded" class="image-grid" v-loading="loading">
      <div v-for="item in loaded" :key="item.id" class="stored-image">
        <el-image :src="item.url" :preview-src-list="previewUrls" :z-index="6000" fit="cover" :alt="item.name" />
        <span>{{ item.name }}</span>
      </div>
      <p v-if="error" role="alert">{{ error }} <button type="button" @click="load">重试</button></p>
    </div>
  </div>
</template>
<script>
import { loadSmartImage } from '@/api/fish/smartEntry'
export default {
  name: 'DatabaseImages',
  props: { images: { type: Array, default: () => [] }, autoOpen: Boolean },
  data: () => ({ expanded: false, loading: false, loaded: [], error: '', controller: null }),
  computed: { previewUrls() { return this.loaded.map(item => item.url) } },
  mounted() { if (this.autoOpen) { this.expanded = true; this.load() } },
  beforeDestroy() { this.release() },
  methods: {
    release() {
      if (this.controller) this.controller.abort()
      this.loaded.forEach(item => URL.revokeObjectURL(item.url))
      this.loaded = []
    },
    toggle() { this.expanded = !this.expanded; if (this.expanded) this.load(); else this.release() },
    async load() {
      this.release(); this.loading = true; this.error = ''
      const controller = new AbortController(); this.controller = controller
      try {
        // Original images are fetched only after the user expands this record.
        for (const image of this.images) {
          const blob = await loadSmartImage(image.id, controller.signal)
          if (controller.signal.aborted) return
          this.loaded.push({ ...image, url: URL.createObjectURL(blob) })
        }
      } catch (e) { if (e.name !== 'AbortError') this.error = e.message }
      finally { if (this.controller === controller) this.loading = false }
    }
  }
}
</script>
<style scoped>
.database-images { margin: 10px 0; }
.image-toggle { color: #21799e; border: 1px solid #abd5e3; background: #eefaff; border-radius: 6px; padding: 7px 12px; cursor: pointer; }
.image-grid { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 12px; min-height: 40px; }
.stored-image { width: 115px; }
.stored-image .el-image { width: 115px; height: 90px; border-radius: 6px; background: #e8eef4; }
.stored-image span { display: block; font-size: 12px; overflow-wrap: anywhere; color: #66798d; }
.image-grid p { color: #bc4e3e; }
</style>
