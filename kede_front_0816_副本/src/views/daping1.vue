<template xmlns="">
  <div class="container">
    <!-- <button @click="sendMsg">测试</button>    -->
    <!-- scrolling="no" -->
    <iframe class="iframe" align="center" height="100%" width="100%" :src="iframe_url" frameborder="no" border="0"
      marginwidth="0" marginheight="0"></iframe>
    <!-- 站内信 websocket -->
    <div style="display: none;">
      <notify-message class="right-menu-item hover-effect" />
    </div>
  </div>
</template>

<script>
import { UserInfoKey, AccessTokenKey, RefreshTokenKey } from "@/utils/constants";
import { ADMIN_MANYS } from "@/utils/constants";
import NotifyMessage from '@/layout/components/Message/index2.vue';

export default {
  name: "Daping1",
  components: {
    NotifyMessage
  },
  data() {
    return {
      userInfo: {},
      iframe_url: "",
      token: "",
    };
  },
  created() {
    this.token = localStorage.getItem(AccessTokenKey);
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    console.log(`====================>userInfo:${JSON.stringify(this.userInfo)}`);
    if(ADMIN_MANYS.indexOf(this.userInfo.roleCode) != -1) {
      // 管理员，访问总控平台
      this.iframe_url = `${process.env.VUE_APP_DA_PING_1}?token=${this.token}`;
    } else {
      // 非管理员，直接访问基地页面
      this.toPage("./daping2");
    }
    // `model2/index.html?workshopId=${item.workshopId}&token=${this.token}`
    // this.iframe_url = `http://192.168.1.102:9993/#/`;
    // this.iframe_url = `http://localhost:9090/#/`;
    console.log(`iframe_url=======>${this.iframe_url}`);
    // let refreshToken = localStorage.getItem(RefreshTokenKey);
    // let userInfo = localStorage.getItem(UserInfoKey);
    // console.log(`====>token:${token}`);
    // console.log(`====>userInfo:${JSON.stringify(userInfo)}`);

    this.receiveMsg();
  },
  mounted() {
  },
  methods: {
    toPage(url){
      this.$router.push({
          path: url,
          // path: '/fish/zk-paper-question',
          params: {},
          query: {  },
      });
    },
    sendMsg() {
      console.log(`========>parent click`)
      //获取iframe元素
      var iframe = document.querySelector('iframe');
      //通过iframe的contentWindow给云服务器发送请求（这里省略了云服务器的url）
      // iframe.contentWindow.postMessage("export", "*");
      iframe.contentWindow.postMessage({ type: 'parent_send_to_son', value: 'go_back' }, "*");
    },
    receiveMsg() {
      let that = this;
      //允许任意URL发送消息，不会跨域
      window.postMessage("message", '*')
      //监听传过来的消息
      window.addEventListener('message', function (event) {
        // console.log(`event parent==========>`, event)
        if (event.data.type == 'son_send_to_parent') {
          console.log(`我父页面，收到子组件消息：${JSON.stringify(event.data.value)}`);
          if(event.data.value) 
            that.toPage(event.data.value);
          else that.toPage("/");
        }
      })
    }
  }
};
</script>
<style lang="scss" scoped>
.container {
  // border: 1px solid red;
  height: 100%;
  // position: relative;
  // .iframe {
  //   position: absolute;
  //   top: 100px;
  // }
}
</style>
