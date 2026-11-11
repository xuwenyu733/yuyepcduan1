<template>
  <span class="dept-info">
    {{name}}
  </span>
</template>
<script>
import { getDeptById } from '@/api/system/dept'

export default {
  data() {
    return {
      name: null,
    }
  },
  methods: {
    getDeptName() {
      if(this.deptId == null || this.deptId == ""){
        this.name = null;
        return;
      }
      getDeptById(this.deptId).then(response => {
        // console.log(`==============>getDeptById:${JSON.stringify(response)}`);
        if(response.data){
          this.name = response.data;
        }else{
          this.name = null;
        }
      });
    }
  },
  watch: {
    deptId(){
      this.$nextTick(() => this.getDeptName())
    }
  },
  mounted: function () {
    this.getDeptName();
  },
  props: {
    deptId: [String, Number],
    required: true
  }
}
</script>
<style>
</style>