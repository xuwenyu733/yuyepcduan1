<template>
  <div class="camera-page">
    <h2>实时监控</h2>
    <p v-if="deviceLabel" class="cam-sub">{{ deviceLabel }}</p>
    <ez-player
      :key="deviceSerial + '-' + channelNo"
      :deviceSerial="deviceSerial"
      :channelNo="channelNo"
    />
  </div>
</template>

<script>
import EzPlayer from '@/components/EzvizPlayer/EzPlayer.vue'

export default {
  name: 'EzvizShow',
  components: {
    EzPlayer
  },
  data() {
    return {
      deviceSerial: 'GC1159385',
      channelNo: 1,
      deviceLabel: ''
    }
  },
  created() {
    this.applyRoute()
  },
  watch: {
    '$route.query': {
      deep: true,
      handler() {
        this.applyRoute()
      }
    }
  },
  methods: {
    applyRoute() {
      const q = (this.$route && this.$route.query) || {}
      const p = (this.$route && this.$route.params) || {}
      this.deviceSerial = q.deviceSerial || q.deviceSn || p.deviceSerial || this.deviceSerial || 'GC1159385'
      this.channelNo = Number(q.channelNo || p.channelNo || 1) || 1
      this.deviceLabel = q.deviceName || p.deviceName || ''
    }
  }
}
</script>

<style scoped>
.camera-page {
  padding: 16px;
}
.cam-sub {
  margin: 0 0 12px;
  color: #909399;
  font-size: 13px;
}
</style>
