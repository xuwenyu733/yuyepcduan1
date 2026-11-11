<template>
  <div>
    <el-popover placement="bottom" width="600" trigger="click">
      <!-- icon 展示 -->
      <el-badge slot="reference" :is-dot="unreadCount > 0" type="danger">
         <svg-icon icon-class="message" @click="getList"/>
      </el-badge>

      <!-- 弹出列表 -->
      <el-table v-loading="loading" :data="list">
        <el-table-column width="120" property="templateNickname" label="发送人" />
        <el-table-column width="180" property="createTime" label="发送时间">
          <template v-slot="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" align="center" prop="templateType" width="100">
          <template v-slot="scope">
            <dict-tag :type="DICT_TYPE.SYSTEM_NOTIFY_TEMPLATE_TYPE" :value="scope.row.templateType" />
          </template>
        </el-table-column>
        <el-table-column property="templateContent" label="内容" />
      </el-table>

      <!-- 更多 -->
      <div style="text-align: right; margin-top: 10px">
        <el-button type="primary" size="mini" @click="goMyList">查看全部</el-button>
      </div>
    </el-popover>
  </div>
</template>

<script>
import {getUnreadNotifyMessageCount, getUnreadNotifyMessageList} from "@/api/system/notify/message";
import { UserInfoKey } from "@/utils/constants";
import Push from "push.js";

export default {
  name: 'NotifyMessage',
  data() {
    return {
      userInfo: {},
      // 遮罩层
      loading: false,
      // 列表
      list: [],
      // 未读数量,
      unreadCount: 0,
    }
  },
  computed: {
    socketMsgs() {
      return this.$socketPublic.state.msg;
    }
  },
  watch: {
    'socketMsgs': {
      //处理接收到的消息
      handler: function(msg) {
        console.log("@/layout/components/Message/index.vue socketMsgs================>",msg);
        this.handlerMsg(msg);
      }
    }
  },
  created() {
    // 首次加载小红点
    this.getUnreadCount()
    this.initWebSocket();
    try {
      if (Push && Push.Permission && typeof Push.Permission.request === 'function') {
        Promise.resolve(Push.Permission.request()).catch(() => {})
      }
    } catch (e) {}
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    console.log(`index2 ==>layout Message print userInfo:${JSON.stringify(this.userInfo)}`);
    // 轮询刷新小红点
    setInterval(() => {
      this.getUnreadCount()
    },1000 * 60 * 2)
  },
  methods: {
    // 智能体操作通知
    myHandleMsg(msg) {
      if(!msg) return;
      // {targetPage:"操作目标页面", }
      let param = JSON.parse(msg);
      let flag =false;
      if("PAGE_TO" == param.operateKey) {
        console.log(`layout/components/Message/index.vue===========>收到后台发送过来的消息,目的是跳转页面，马上执行跳转：`, msg);
        this.page_to(param.content);
        flag =true;
      } else if (param.targetPageName && param.operateKey && !this.my_common.judgeOperateUrl(param.targetPath)) {
        console.log(`layout/components/Message/index.vue=====判断是否当前页面:${this.my_common.judgeOperateUrl(param.targetPath)}======>收到后台发送过来的消息，目的是弹窗或者其他，现在已经跳过去，执行操作：`, msg);
        // 当消息发送需要页面操作时，而当前页面都还没有打开，则需要一边跳转，然后一边执行操作
        this.$router.push({
          name: `${param.targetPageName}`,
          // path: '/pl/zk-paper-question',
          params: { "socketParam": JSON.stringify(param) },
          query: {  },
        });
        flag = true;
      }
      return flag;
    },
    // 后台发送请求需要跳转页面
    // {"content":"/productmng/product-brand","operateKey":"PAGE_TO"}
    page_to(targetUrl) {
      if(!targetUrl) return;
      this.$router.push({
          path: targetUrl,
          // path: '/pl/zk-paper-question',
          params: {},
          query: {},
      });
    },
    getList: function() {
      this.loading = true;
      getUnreadNotifyMessageList().then(response => {
        this.list = response.data;
        this.loading = false;
        // 强制设置 unreadCount 为 0，避免小红点因为轮询太慢，不消除
        this.unreadCount = 0
      });
    },
    getUnreadCount: function() {
      getUnreadNotifyMessageCount().then(response => {
        this.unreadCount = response.data;
      })
    },
    goMyList: function() {
      this.$router.push({
        name: 'MyNotifyMessage'
      });
    },
    handlerMsg(msg){
      // console.log("websocket receive msg :", msg);
      try{
        let flag = this.myHandleMsg(msg);
        if(flag) return;
        // this.otherHandle(msg);
      }catch (e){}
    },
    initWebSocket() {
      this.$socketPublic.dispatch('webSocketInit');//初始化ws
    },
  }
}
</script>
<style>
.el-badge__content.is-fixed {
  top: 10px; /* 保证徽章的位置 */
}
</style>
