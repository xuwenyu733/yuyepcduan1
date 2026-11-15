<template>
  <div id="app">
    <router-view />
    <theme-picker />
    <MyChat v-if="assistantReady" :key="assistantIdentity" class="application-assistant" />
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker";
import MyChat from "@/components/MyChat/index";
import { getTenantId } from "@/utils/auth";

export default {
  name: "App",
  components: { ThemePicker, MyChat },
  computed: {
    assistantReady() {
      return Boolean(this.$store.getters.userId) &&
        !['/login', '/sso', '/social-login', '/401', '/404'].includes(this.$route.path);
    },
    assistantIdentity() {
      return `${getTenantId()}:${this.$store.getters.userId}`;
    }
  },
  metaInfo() {
    return {
      title: this.$store.state.settings.dynamicTitle && this.$store.state.settings.title,
      titleTemplate: title => {
        return title ? `${title} - ${process.env.VUE_APP_TITLE}` : process.env.VUE_APP_TITLE
      }
    }
  }
};
</script>
<style scoped>
#app {
  background-color: #0a1520;
  min-height: 100vh;
}

#app .theme-picker {
  display: none;
}
.application-assistant ::v-deep .ai-assistant-text {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
}
</style>
