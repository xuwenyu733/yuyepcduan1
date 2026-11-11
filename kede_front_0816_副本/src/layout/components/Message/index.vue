<template>
  <div class="notify-message-wrap">
    <el-popover
      placement="bottom"
      width="720"
      trigger="click"
      popper-class="notify-message-popper"
      @show="onPopoverShow"
    >
      <el-badge slot="reference" :value="unreadCount || undefined" :hidden="!unreadCount" :max="99" type="danger">
        <svg-icon icon-class="message" class="notify-bell" @click="getList" />
      </el-badge>

      <div class="notify-panel">
        <div class="notify-panel__head">
          <span class="notify-panel__title">通知</span>
          <span class="notify-panel__tip">按轻重缓急分栏 · 新消息播放提示音</span>
        </div>

        <el-tabs v-model="activeTab" class="notify-tabs" @tab-click="onTabClick">
          <el-tab-pane name="all">
            <span slot="label">全部 <el-badge :value="counts.all || undefined" :hidden="!counts.all" class="tab-badge" /></span>
          </el-tab-pane>
          <el-tab-pane name="urgent">
            <span slot="label"><i class="urgency-dot urgent" />紧急 <el-badge :value="counts.urgent || undefined" :hidden="!counts.urgent" type="danger" class="tab-badge" /></span>
          </el-tab-pane>
          <el-tab-pane name="priority">
            <span slot="label"><i class="urgency-dot priority" />优先 <el-badge :value="counts.priority || undefined" :hidden="!counts.priority" type="warning" class="tab-badge" /></span>
          </el-tab-pane>
          <el-tab-pane name="normal">
            <span slot="label"><i class="urgency-dot normal" />日常 <el-badge :value="counts.normal || undefined" :hidden="!counts.normal" type="info" class="tab-badge" /></span>
          </el-tab-pane>
        </el-tabs>

        <el-table v-loading="loading" :data="filteredList" size="mini" max-height="360" empty-text="暂无未读通知">
          <el-table-column label="缓急" width="70" align="center">
            <template v-slot="scope">
              <el-tag size="mini" :type="getUrgencyTagType(scope.row.urgencyLevel)" effect="dark">
                {{ getUrgencyLabel(scope.row.urgencyLevel) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column width="90" property="templateNickname" label="发送人" show-overflow-tooltip />
          <el-table-column width="150" property="createTime" label="发送时间">
            <template v-slot="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="类型" align="center" prop="templateType" width="80">
            <template v-slot="scope">
              <dict-tag :type="DICT_TYPE.SYSTEM_NOTIFY_TEMPLATE_TYPE" :value="scope.row.templateType" />
            </template>
          </el-table-column>
          <el-table-column property="templateContent" label="内容" min-width="180" show-overflow-tooltip />
        </el-table>

        <div class="notify-panel__foot">
          <el-button type="text" size="mini" @click="previewSound">试听提示音</el-button>
          <el-button type="primary" size="mini" @click="goMyList">查看全部</el-button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { getUnreadNotifyMessageCount, getUnreadNotifyMessageList } from "@/api/system/notify/message";
import { UserInfoKey } from "@/utils/constants";
import Push from "push.js";
import {
  enrichNotifyList,
  filterByUrgency,
  countByUrgency,
  playNotifySound,
  maxUrgency,
  getUrgencyLabel,
  getUrgencyTagType,
} from "@/utils/notifyUrgency";

export default {
  name: 'NotifyMessage',
  data() {
    return {
      userInfo: {},
      loading: false,
      list: [],
      unreadCount: 0,
      lastUnreadCount: 0,
      activeTab: 'all',
      soundEnabled: true,
    }
  },
  computed: {
    socketMsgs() {
      try {
        return this.$socketPublic && this.$socketPublic.state ? this.$socketPublic.state.msg : null
      } catch (e) {
        return null
      }
    },
    filteredList() {
      return filterByUrgency(this.list, this.activeTab)
    },
    counts() {
      return countByUrgency(this.list)
    },
  },
  watch: {
    socketMsgs: {
      handler(msg) {
        if (msg) this.handlerMsg(msg);
      }
    }
  },
  created() {
    try {
      this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey) || '{}') || {}
    } catch (e) {
      this.userInfo = {}
    }
    this.getUnreadCount()
    try { this.initWebSocket(); } catch (e) { console.warn('notify websocket init failed', e) }
    try {
      if (Push && Push.Permission && typeof Push.Permission.request === 'function') {
        // 浏览器拒绝桌面通知时 Push.js 会 reject(undefined)，必须接住 Promise
        Promise.resolve(Push.Permission.request()).catch(() => {})
      }
    } catch (e) {}
    this._pollTimer = setInterval(() => {
      this.getUnreadCount(true)
    }, 1000 * 60 * 2)
  },
  beforeDestroy() {
    if (this._pollTimer) clearInterval(this._pollTimer)
  },
  methods: {
    getUrgencyLabel,
    getUrgencyTagType,
    onPopoverShow() {
      this.getList()
    },
    onTabClick() {
      // tabs 仅前端过滤
    },
    previewSound() {
      const map = { all: 'urgent', urgent: 'urgent', priority: 'priority', normal: 'normal' }
      playNotifySound(map[this.activeTab] || 'normal')
      this.$message.success(`已播放「${getUrgencyLabel(map[this.activeTab] || 'normal')}」提示音`)
    },
    myHandleMsg(msg) {
      if (!msg) return false;
      let param
      try {
        param = typeof msg === 'string' ? JSON.parse(msg) : msg
      } catch (e) {
        return false
      }
      if (!param || typeof param !== 'object') return false
      let flag = false;
      if ("PAGE_TO" == param.operateKey) {
        let t1 = this.my_common.judgeOperateUrl(param.targetPath) ? "无需跳转，当前页面就是目标页面。" : "已经成功跳转到对应页面。";
        this.page_to(param.targetPath);
        flag = true;
        let replyParam = { "uid": this.userInfo.userId, "receiveMsg": t1, "isSyncMsg": true };
        this.$socketPublic.dispatch("sendMsg", JSON.stringify(replyParam));
      } else if (param.targetPageName && param.operateKey && !this.my_common.judgeOperateUrl(param.targetPath) && "ADD_DATA" == param.operateKey) {
        this.$router.push({
          name: `${param.targetPageName}`,
          params: { "socketParam": JSON.stringify(param) },
          query: {},
        });
        flag = true;
      } else if ("SURE_ADD" == param.operateKey && !this.my_common.judgeOperateUrl(param.targetPath)) {
        let replyParam = { "uid": this.userInfo.userId, "receiveMsg": "作为助手你还没有调用工具'add2'。", "isSyncMsg": true };
        this.$socketPublic.dispatch("sendMsg", JSON.stringify(replyParam));
      } else if (param.notify || param.templateContent || param.content) {
        // 实时通知：弹层刷新 + 按缓急播音
        this.onRealtimeNotify(param)
        flag = true
      }
      return flag;
    },
    onRealtimeNotify(param) {
      const row = enrichNotifyList([{
        templateContent: param.templateContent || param.content || param.msg || '',
        templateNickname: param.templateNickname || param.from || '系统',
        templateType: param.templateType,
        urgency: param.urgency || param.priority || param.level,
        createTime: Date.now(),
      }])[0]
      if (this.soundEnabled) playNotifySound(row.urgencyLevel)
      try {
        Push.create(getUrgencyLabel(row.urgencyLevel) + '通知', {
          body: row.templateContent,
          timeout: 5000,
        })
      } catch (e) {}
      this.getUnreadCount(true)
      this.getList(false)
    },
    page_to(targetUrl) {
      if (!targetUrl) return;
      this.$router.push({ path: targetUrl, params: {}, query: {} });
    },
    getList(resetBadge = true) {
      this.loading = true;
      getUnreadNotifyMessageList().then(response => {
        this.list = enrichNotifyList(response.data || []);
        this.loading = false;
        if (resetBadge) this.unreadCount = 0
      }).catch(() => { this.loading = false });
    },
    getUnreadCount(detectIncrease) {
      getUnreadNotifyMessageCount().then(response => {
        const count = Number(response.data) || 0
        if (detectIncrease && count > this.lastUnreadCount && this.lastUnreadCount >= 0) {
          // 未读增加：拉列表后按最高缓急播音
          getUnreadNotifyMessageList().then(res => {
            const list = enrichNotifyList(res.data || [])
            this.list = list
            if (this.soundEnabled && list.length) {
              playNotifySound(maxUrgency(list))
            }
          }).catch(() => {})
        }
        this.unreadCount = count
        this.lastUnreadCount = count
      }).catch(() => {
        // cpolar/Java 超时：不打断当前业务页
      })
    },
    goMyList() {
      this.$router.push({ name: 'MyNotifyMessage' });
    },
    handlerMsg(msg) {
      try {
        let flag = this.myHandleMsg(msg);
        if (flag) return;
      } catch (e) {}
    },
    initWebSocket() {
      if (this.$socketPublic && typeof this.$socketPublic.dispatch === 'function') {
        this.$socketPublic.dispatch('webSocketInit');
      }
    },
  }
}
</script>

<style>
.el-badge__content.is-fixed {
  top: 10px;
}

/* 弹层挂到 body，需非 scoped；暗色主题下把字色提亮 */
.notify-message-popper {
  color: #e8eef8;
}
.notify-message-popper .notify-panel__title {
  color: #f5f8ff;
}
.notify-message-popper .notify-panel__tip {
  color: #b8c4d8;
  opacity: 1;
}
.notify-message-popper .notify-tabs .el-tabs__item {
  color: #c5d0e2;
}
.notify-message-popper .notify-tabs .el-tabs__item:hover {
  color: #eef3fb;
}
.notify-message-popper .notify-tabs .el-tabs__item.is-active {
  color: #ffffff;
}
.notify-message-popper .el-table,
.notify-message-popper .el-table th,
.notify-message-popper .el-table td,
.notify-message-popper .el-table .cell {
  color: #e6edf7;
}
.notify-message-popper .el-table th > .cell {
  color: #d2dbeb;
  font-weight: 600;
}
.notify-message-popper .el-table--enable-row-hover .el-table__body tr:hover > td {
  color: #ffffff;
}
.notify-message-popper .notify-panel__foot .el-button--text {
  color: #dce5f4;
}
.notify-message-popper .notify-panel__foot .el-button--text:hover {
  color: #ffffff;
}
.notify-message-popper .el-table__empty-text {
  color: #aeb9cc;
}
</style>

<style scoped>
.notify-bell {
  font-size: 20px;
  cursor: pointer;
  vertical-align: middle;
}
.notify-panel__head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 4px;
}
.notify-panel__title {
  font-size: 15px;
  font-weight: 600;
  color: #f5f8ff;
}
.notify-panel__tip {
  font-size: 12px;
  color: #b8c4d8;
  opacity: 1;
}
.notify-panel__foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.urgency-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 4px;
  vertical-align: middle;
}
.urgency-dot.urgent { background: #f56c6c; }
.urgency-dot.priority { background: #e6a23c; }
.urgency-dot.normal { background: #909399; }
.tab-badge {
  margin-left: 4px;
}
.tab-badge >>> .el-badge__content {
  position: static;
  transform: none;
}
</style>
