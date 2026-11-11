<template>
  <span class="user-info">
    {{name}}
  </span>
</template>
<script>
import { getUserById } from '@/api/system/user'

export default {
  data() {
    return {
      name: null,
    }
  },
  methods: {
    getUserName() {
      if(this.userId == null || this.userId == ""){
        this.name = null;
        return;
      }
      getUserById(this.userId).then(response => {
        // console.log(`==============>getUserById:${JSON.stringify(response)}`);
        if(response.data){
          this.name = response.data.nickname;
        }else{
          this.name = null;
        }
      });
    }
  },
  watch: {
    userId(){
      this.$nextTick(() => this.getUserName())
    }
  },
  mounted: function () {
    this.getUserName();
  },
  props: {
    userId: [String, Number],
    required: true
  }
}
</script>
<style>
</style>