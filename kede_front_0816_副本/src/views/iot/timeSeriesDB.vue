<template>
  <div class="iframe-container">
    <h3 class="page-title">时序数据库</h3>
    <div class="iframe-wrapper">
      <iframe 
        :src="iframeUrl" 
        frameborder="0" 
        class="content-iframe"
        title="时序数据库"
      ></iframe>
    </div>
    <div class="credentials-info">
      <p>登录信息：</p>
      <p>用户名：root</p>
      <p>密码：taosdata</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TimeSeriesDB',
  data() {
    return {
      iframeUrl: 'http://39.107.190.68:6060/login'
    }
  }
}
</script>

<style scoped>
.iframe-container {
  width: 100%;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.page-title {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.iframe-wrapper {
  width: 100%;
  height: calc(100% - 100px);
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background-color: #fff;
}

.content-iframe {
  width: 100%;
  height: 100%;
}

.credentials-info {
  margin-top: 15px;
  padding: 15px;
  background-color: #eaf5ff;
  border-radius: 4px;
  color: #333;
  font-size: 14px;
}

.credentials-info p {
  margin: 5px 0;
}
</style>