<template>
  <div class="chatContainer" :style="`z-index: 1002;`">
    <div class="chatBtn" @click.stop="openChat">
      <div class="ai-assistant-outer">
        <!-- 外层轨道 -->
        <div class="orbital-path">
          <div class="orbital-dot dot-1"></div>
          <div class="orbital-dot dot-2"></div>
          <div class="orbital-dot dot-3"></div>
          <div class="orbital-dot dot-4"></div>
        </div>
        <!-- 内层方框 -->
        <div class="ai-assistant-inner">
          <i class="el-icon-star-on"></i>
          <!-- 内部装饰元素 -->
          <i class="el-icon-snowflake inner-icon icon-snowflake"></i>
          <i class="el-icon-lightning inner-icon icon-lightning"></i>
          <div class="inner-dot dot-top-left"></div>
          <div class="inner-dot dot-bottom-right"></div>
        </div>
        <!-- 右侧额外圆点 -->
        <div class="extra-dot"></div>
      </div>
      <div class="ai-assistant-text">
        <i class="el-icon-star-on"></i>
        <span>智慧养殖AI助手</span>
      </div>
    </div>
    <!-- 视频通话+语音通话 height: 1px;overflow: hidden; v-if="videoChatFlag"  :targetUserInfo="local_userItem" -->
    <VideoChat ref="videoChat"></VideoChat>
    <AliyunTxt2VoiceVue ref="AliyunTxt2VoiceVue" @son_endPlay="exe_txt2voice_endPlay"
      @son_startPlay="exe_txt2voice_startPlay"></AliyunTxt2VoiceVue>
    <!-- <AliyunVoice2Txt ref="AliyunVoice2TxtRef" @son_send_msg="parentReceiveMsg"></AliyunVoice2Txt>  -->
    <FunAsrVoice2TxtFrame ref="AliyunVoice2TxtRef" funasrKey="pc_agent" @son_send_msg="parentReceiveMsg"
      :isStop="funasrNeedStop"></FunAsrVoice2TxtFrame>
    <div class="chatBox" ref="chatBox" style="display: none" v-if="true">
      <div class="real_content">

        <!-- START: 新增左侧Tab导航栏 -->
        <div class="left-tabs">
          <div v-for="(tab, index) in tabs" :key="index" class="tab-item" :class="{ active: activeTab === tab.name }"
            @click="handleTabChange(tab.name)">
            <i :class="tab.icon"></i>
            <span>{{ tab.label }}</span>
          </div>
        </div>
        <!-- END: Tab导航栏 -->
        <!-- 页面开始 -->
        <div class="water-background">
          <div class="bubbles"></div>
          <div class="bubbles"></div>
          <div class="bubbles"></div>
        </div>
        <div class="mychat_container">
          <div class="character-container">
            <!-- ====== 新增三种视图切换 ====== -->
            <!-- 1. 智能体视图 -->
            <div v-if="activeTab === 'agent'" class="character">
              <div class="number_phone" v-if="number_phone_flag && iframe_url">
                <iframe class="iframe" align="center" height="100%" width="100%" :src="iframe_url" frameborder="no"
                  border="0" marginwidth="0" marginheight="0"></iframe>
              </div>
              <div class="number_phone de-number-phone-missing" v-else-if="number_phone_flag && !iframe_url">
                数字人地址未配置（`VUE_APP_NUMBER_PERSON` 为空）
              </div>
            </div>

            <!-- 2. 单聊视图 -->
            <div v-else-if="activeTab === 'single'" class="contact-list">
              <div v-for="contact in userList1" :key="'s' + contact.currUserId" class="contact-item"
                @click.stop="chatToUser(contact)">
                <img :src="contact.currUserAvatar ? contact.currUserAvatar : 'https://kede-public1.oss-cn-beijing.aliyuncs.com/20251009/back2_1760005115897.jpeg'" class="avatar">
                <div class="info">
                  <div class="name">{{ contact.currUserName }}</div>
                  <div class="last-msg-parent">
                    <div class="last-msg">{{ contact.lastLog }}</div>
                    <div class="last-msg-time">{{ contact.lastLogTime }}</div>
                  </div>
                </div>
                <div v-if="contact.unReadCount > 0" class="unread-badge">
                  {{ contact.unReadCount > 9 ? '9+' : contact.unReadCount }}
                </div>
              </div>
            </div>

            <!-- 3. 群聊视图 -->
            <div v-else-if="activeTab === 'group'" class="contact-list">
              <div v-for="group in groupList1" :key="'g' + group.groupId" class="contact-item"
                @click.stop="chatToGroup(group)">
                <img
                  :src="group.avatar ? group.avatar : 'https://kede-public1.oss-cn-beijing.aliyuncs.com/20251009/back2_1760005115897.jpeg'"
                  class="avatar group-avatar">
                <div class="info">
                  <div class="name">{{ group.groupName }}</div>
                  <div class="last-msg-parent">
                    <div class="last-msg">{{ group.lastLog }}</div>
                    <div class="last-msg-time">{{ group.lastLogTime }}</div>
                  </div>
                </div>
                <div v-if="group.unReadCount > 0" class="unread-badge">
                  {{ group.unReadCount > 9 ? '9+' : group.unReadCount }}
                </div>
              </div>
            </div>

            <div class="character">
            </div>
            <div class="ripple-effect" v-if="activeTab == 'agent'"></div>
          </div>

          <div class="chat-container">
            <div class="chat-header">
              <div class="chat-title">
                <span class="tech-text">{{ chatTitle }}</span>
                <div class="tech-dots" @click.stop="toDetailPage">
                  <div class="dot pulse" style="--delay: 0s"></div>
                  <div class="dot pulse" style="--delay: 0.2s"></div>
                  <div class="dot pulse" style="--delay: 0.4s"></div>
                </div>
              </div>
              <button class="close-btn" @click.stop="closeChat">
                <span class="close-icon"></span>
              </button>
            </div>

            <div class="chat-content" id="chat-content">
              <div class="message-group">
                <div class="message-bubble system" v-if="activeTab == 'agent'">
                  <div class="message-content">
                    <p>欢迎使用渔业科技智能助手！我可以为您提供以下服务：</p>
                    <ul>
                      <li>水质监测数据分析</li>
                      <li>养殖密度优化建议</li>
                      <li>鱼类健康状态评估</li>
                      <li>渔业设备管理指导</li>
                    </ul>
                  </div>
                </div>

                <div class="message-bubble system" v-if="activeTab == 'agent'">
                  <div class="message-content">
                    <p>Hi~，我是小科，可以帮您查设备、查水质。您有什么需要帮助呢？</p>
                  </div>
                </div>
                <template v-for="(item, index) in messageList">
                  <div class="message-bubble user" v-if="isUserMsg(item)" style="float: right;">
                    <div class="message-content" @dblclick.stop="copyMsg($event)" style="">
                      <!-- <p>{{ item.msg }}</p> width: 200px; -->
                      <ChatMsg :data="item" :currPage="``">
                      </ChatMsg>
                    </div>
                  </div>
                  <!-- v-if="item.role == 'ai' && item.msgType != 'first'" -->
                  <div class="message-bubble system" v-else>
                    <div class="message-content">
                      <div class="chat-message">
                        <div class="chat-message-think-title" v-if="item.think"
                          @click.stop="msgThinkShow = !msgThinkShow">
                          <div class="left">
                            {{ item.msg ? "已深度思考" : "深度思考中" }}
                          </div>
                          <div class="right">
                            <i class="el-icon-arrow-up" v-if="msgThinkShow"></i>
                            <i class="el-icon-arrow-down" v-if="!msgThinkShow"></i>
                          </div>
                        </div>
                        <div class="chat-message-think" v-if="msgThinkShow && item.think" v-html="item.think"
                          @dblclick.stop="copyMsg($event)"></div>
                        <!--  -->
                        <!-- <div class="chat-message-msg" v-if="item.msg" v-html="item.msg" @click.stop="copyMsg($event)"></div> -->
                        <div class="chat-message-msg" @dblclick.stop="copyMsg($event)">
                          <ChatMsg :data="item" :currPage="``">
                          </ChatMsg>
                        </div>
                        <div class="chat-message-score" v-if="can_askScore(item) && item.role == 'ai' && item.askScore">
                          <div class="label">评分：</div>
                          <div class="my_rate">
                            <el-rate v-model="item.askScore" show-score @change="changeAskScore($event, item, index)"
                              :colors="['#4C9165', '#4C9165', '#4C9165']" text-color="#4C9165" score-template="{value}">
                            </el-rate>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
                <!-- 思考中：等待 AI 回复时显示，类似 DeepSeek 对话框 -->
                <div class="message-bubble system thinking-bubble" v-if="activeTab === 'agent' && isModelGen">
                  <div class="message-content">
                    <div class="thinking-indicator">
                      <span class="thinking-text">思考中</span>
                      <span class="thinking-dots">
                        <span class="dot"></span>
                        <span class="dot"></span>
                        <span class="dot"></span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="chat-footer">
              <div class="route-hint-bar" v-if="activeTab === 'agent'">
                <span class="route-hint-label">
                  意图选择
                  <el-tooltip placement="top" effect="dark" popper-class="route-hint-tooltip">
                    <div slot="content" class="route-hint-tip-content">
                      <p><strong>查询分析</strong>：查询已有数据、统计分析、对比趋势、因果诊断等（只读，不填表）。</p>
                      <p><strong>录入数据</strong>：新增或补充业务单据，如投放、投喂、死亡、分池、用药等。</p>
                      <p>发送前请先选择意图；若与输入内容不一致，系统会弹窗请您确认。</p>
                    </div>
                    <i class="el-icon-question route-hint-help-icon"></i>
                  </el-tooltip>
                </span>
                <button
                  type="button"
                  class="route-hint-btn"
                  :class="{ active: agentRouteHint === 'query_analysis' }"
                  @click.stop="agentRouteHint = 'query_analysis'"
                >查询分析</button>
                <button
                  type="button"
                  class="route-hint-btn"
                  :class="{ active: agentRouteHint === 'data_entry' }"
                  @click.stop="agentRouteHint = 'data_entry'"
                >录入数据</button>
              </div>
              <div class="input-container">
                <textarea id="query_textarea" placeholder="请输入您的渔业相关问题(发送消息enter，换行ctrl+enter)..."
                  v-model="msg"></textarea>
                <div class="voice-container">
                  <button :class="`send-btn31`" @click.stop="stopVoice">
                    <!-- <i class="el-icon-video-pause" style="color: #fff;text-align: center;"></i> -->
                    <i class="el-icon-refresh-left" style="color: #fff;text-align: center;"></i>
                  </button>
                  <button :class="`send-btn31 ${isMicState ? 'send-btn2-hover' : ''}`" @click.stop="toggleMic">
                    <i class="el-icon-microphone" style="color: #fff;text-align: center;"></i>
                  </button>
                  <button class="send-btn31" @click.stop="chooseFile" v-if="activeTab != 'agent'">
                    <!-- <i class="iconfont icon-fasong" style="color: #fff;transform: rotate(-90deg);text-align: center;"></i> -->
                    <i class="el-icon-folder-add" style="color: #fff;text-align: center;"></i>
                    <!-- <span class="send-icon" style="display: flex;justify-content: center;transform: rotate(-90deg);"></span> -->
                  </button>
                  <!-- 语音通话 -->
                  <button class="send-btn31" @click.stop="phoneWithPartner" v-if="activeTab != 'agent' && false">
                    <!-- <i class="iconfont icon-fasong" style="color: #fff;transform: rotate(-90deg);text-align: center;"></i> -->
                    <i class="el-icon-phone" style="color: #fff;text-align: center;"></i>
                    <!-- <span class="send-icon" style="display: flex;justify-content: center;transform: rotate(-90deg);"></span> -->
                  </button>
                  <!-- 视频通话 -->
                  <button class="send-btn31" @click.stop="videoWithPartner" v-if="activeTab != 'agent'">
                    <!-- <i class="iconfont icon-fasong" style="color: #fff;transform: rotate(-90deg);text-align: center;"></i> -->
                    <i class="el-icon-video-camera" style="color: #fff;text-align: center;"></i>
                    <!-- <span class="send-icon" style="display: flex;justify-content: center;transform: rotate(-90deg);"></span> -->
                  </button>
                  <button class="send-btn31" @click.stop="sendSumMsg">
                    <!-- <i class="iconfont icon-fasong" style="color: #fff;transform: rotate(-90deg);text-align: center;"></i> -->
                    <i class="el-icon-s-promotion" style="color: #fff;text-align: center;"></i>
                    <!-- <span class="send-icon" style="display: flex;justify-content: center;transform: rotate(-90deg);"></span> -->
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 页面结束 -->
      </div>
    </div>
    <div class="fileUploadDiv" style="visibility: hidden;">
      <FileBusUpload ref="uploadRef" :fid="fid" :bus-type="busType" @uploadSuccess="confirmUrl"></FileBusUpload>
    </div>

  </div>
</template>
<script>
import '@/components/MyChat/js/MyChat.js';
/**
该模块配置：
role:   user ai
 */
import { chatPyStream } from "@/api/fish/aiAgent";
import {
  ROUTE_HINT_QUERY,
  detectRouteHintConflict,
  normalizeRouteHint,
} from "@/utils/agentRouteHint";
import {
  UserInfoKey,
  ChatLogKey,
  ChatMsgKey,
  AppChatThink,
  ASK_CHAT_LOG_LIST,
  CHAT_MSG_FOR_USER,
  CHAT_MSG_FOR_GROUP,
  CHAT_MSG_FOR_DELETE,
  UserChatLogKey,
  GroupChatLogKey,
  AppChatMsg,
  VUE_APP_WEBSOCKET_HEARTCHECK,
  KE_DE_ROLES
} from "@/utils/constants";
import {
  createChatLog,
  updateChatLog,
  deleteChatLog,
  getChatLog,
  getChatLogPage,
  exportChatLogExcel,
} from "@/api/fish/chatLog";
import AliyunTxt2VoiceVue from "@/components/aliyun_txt2voice/AliyunTxt2Voice.vue";
import AliyunVoice2Txt from '@/components/aliyun_voice2txt/AliyunVoice2Txt.vue';
import FunAsrVoice2TxtFrame from '@/components/funasr_voice2txt/FunAsrVoice2Txt.vue'; // Frame
import {
  listSimpleUsersByRole
} from "@/api/system/user";
import * as ZkChatGroupApi from '@/api/fish/zkchatgroup';
import * as ZkChatMsgApi from '@/api/fish/zkchatmsg';
import {
  getUUID,
  formatDate
} from "@/utils";
import FileBusUpload from "@/components/FileUpload/index3.vue";
import ChatMsg from '@/components/MyChat/ChatMsg.vue';
import VideoChat from './VideoChat';
import { removeToken } from "@/utils/auth";

export default {
  name: "MyChat",
  props: {
    show_MyChat: {
      type: Boolean,
      default: true
    },
    show_VideoChat: { // {"notShow": true}
      type: Object,
      default() {
        return { "notShow": false };
      }
    },
    // "videoUserInfo": {
    // 	type: Object,
    // 	default () {
    // 		return {};
    // 	}
    // }
  },
  data() {
    return {
      fid: '', // 文件id
      busType: 'UserChatFile',
      chatTitle: '渔业科技智能助手',
      // iframe_url: 'http://localhost:9391',
      // iframe_url: 'http://localhost:9876/16',
      // 数字人页面：兼容 .env 中带引号/空格导致 iframe src 失效
      iframe_url: String(process.env.VUE_APP_NUMBER_PERSON || "")
        .trim()
        .replace(/^['"]+/, "")
        .replace(/['"]+$/, ""),
      number_phone_flag: false,
      msg: "",
      agentRouteHint: ROUTE_HINT_QUERY,
      z_index: 0,
      testMsg: "",
      messageList: [],
      userImg:
        "https://img1.baidu.com/it/u=2368124407,3658744752&fm=253&fmt=auto&app=120&f=JPEG?w=800&h=800",
      aiImg: "@/assets/MyChat/img/ai-head.png",
      userInfo: {},
      uid: "",
      agentModel: {
        isStart_txt2voice: false,
        modelName: "deepseek-v3", // 模型名称 deepseek-v3 、 deepseek-r1
        agentType: "ill", // 开启知识库 ill other
        searchType: "false", // 开启联网 true 、 false
        dialogType: 1, // 1:对话模式;2:机器人模式;
      },
      msgThinkShow: true,
      interval: null, // 播放循环器
      isMicState: false, // 是否mic
      isMicState_: false, // 是否mic
      isModelGen: false, // 是否模型生成
      micState: {
        count: 0,
        txt: ''
      },
      funasrNeedStop: false, // 语音识别是否需要停止识别，eg：智能体说话期间
      mic_timer: null, // mic连续说话缓冲定时器
      full_msg: '', // 当前这一轮语音识别的增量文本
      asr_prefix: '', // 开始本轮语音识别时，输入框中已有的文本（保留用户手动输入）
      dialogName: '机器人模式', // 机器人模式   对话模式
      // START: 新增Tab数据
      tabs: [
        { name: "agent", label: "智能体", icon: "el-icon-cpu" },
        { name: "single", label: "单聊", icon: "el-icon-user" },
        { name: "group", label: "群聊", icon: "el-icon-s-custom" }
      ],
      activeTab: "agent",
      // END: Tab数据
      // 新增联系人列表数据
      contactLists: {
        single: [ // 单聊联系人
          { id: 1, name: '张渔师', avatar: 'https://kede-public1.oss-cn-beijing.aliyuncs.com/20251009/back2_1760005115897.jpeg', lastMsg: '最近水质如何？', unread: 2 },
          { id: 2, name: '李养殖', avatar: 'https://kede-public1.oss-cn-beijing.aliyuncs.com/20251009/back2_1760005115897.jpeg', lastMsg: '设备维护完成', unread: 0 }
        ],
        group: [ // 群聊联系人
          { id: 101, name: '南海养殖交流群', avatar: 'https://kede-public1.oss-cn-beijing.aliyuncs.com/20251009/back2_1760005115897.jpeg', lastMsg: '小王：新鱼苗到了', unread: 5 },
          { id: 102, name: '智能设备管理组', avatar: 'https://kede-public1.oss-cn-beijing.aliyuncs.com/20251009/back2_1760005115897.jpeg', lastMsg: '系统升级通知', unread: 1 }
        ],
      },
      formData: {},
      local_chatUserStore: {
        userItemList: []
      }, // 单聊聊天信息
      local_chatGroupStore: {
        groupItemList: []
      }, // 群聊聊天信息
      group_unReadCount: 0, // 群聊未读数量
      group_cacheKey: undefined, // 缓存消息key
      user_cacheKey: undefined, // 缓存消息key
      userList1: [], // 单聊联系人列表
      groupList1: [], // 群聊联系列表
      currentGroup: {}, // 当前聊天的群组
      currentUser: {}, // 当前聊天联系人
      local_userItem: {}, // 对方opposite单聊联系人(chat_detail的时候才有效)
      videoChatFlag: false,
      dialogSingle1V1Visible: false, //1v1视频聊天对话框
      videoUserInfo: {},
    };
  },
  components: {
    AliyunTxt2VoiceVue,
    AliyunVoice2Txt,
    FunAsrVoice2TxtFrame,
    FileBusUpload,
    ChatMsg,
    VideoChat,
  },
  computed: {
    show() {
      return this.$store.getters.showFlag_MyChat;
    },
    socketMsg() {
      // return this.$socketPublic.state.msg;
      return this.$socketPublic.state.agentMsg;
    },
    chatUserStore() {
      return this.$socketPublic.state.chatUserStore;
    },
    chatGroupStore() {
      return this.$socketPublic.state.chatGroupStore;
    }
  },
  watch: {
    show(nv) {
      // console.log(`MyChat/index.vue============>watch.show:${nv}`);
      this.showFlag = nv;
    },
    socketMsg(newV, oldV) {
      console.log("(MyChat/index.vue)收到服务器内容：" + newV);
      // 1. 开启语音
      try {
        if (newV.indexOf(AppChatThink) != -1) this.myStart_txt2voice(JSON.parse(newV));
        else if ((!this.agentModel.isStart_txt2voice || this.agentModel.modelName == 'deepseek-v3') && newV.indexOf(AppChatMsg) != -1) {
          this.myStart_txt2voice(JSON.parse(newV));
        }
      } catch (error) {
        console.log(`socketMsg==================>error:`, error);
      }
      // 2. 同步消息
      if (newV && (newV.indexOf(ChatMsgKey) != -1 || newV.indexOf(AppChatThink) != -1)) {
        this.initMessages();
      } else if (newV && newV.indexOf(AppChatMsg) != -1) {
        this.isModelGen = false;
        this.initMessages();
      }
    },
    "agentModel.isStart_txt2voice": {
      handler(nv, olv) {
        // let obj = {"content":"我是循环水养殖系统AI智能顾问","deviceType":"app","operateKey":"CHAT_MSG","version":"1754719983805"};
        // this.myStart_txt2voice(obj);
        // console.log(`isStart_txt2voice:${nv}`);
        // return;
        if (!nv) {
          this.myEnd_txt2voice();
        }
      },
    },
    // watch 负责 接受最新的消息数据，更新lastLog、lastLogTime
    chatGroupStore(nv) {
      // console.log(`chatGroupStore============>watch.nv:${JSON.stringify(nv)}`);
      if (!(nv && nv.groupItemList && nv.groupItemList.length > 0)) {
        return;
      }

      // 1. 记录本地 groupItemList
      let groupItemList = this.local_chatGroupStore.groupItemList;
      // this.$set(this.local_chatGroupStore, 'groupItemList', []);
      groupItemList.forEach((localItem, index) => {
        if (!localItem.msgItemList) localItem.msgItemList = [];
        if (!localItem.lastLogVersion) localItem.lastLogVersion = 0;
        let remoteCurr = null;
        // 2. 遍历远端的 groupItemList
        nv.groupItemList.forEach(ii => {
          if (localItem.groupId == ii.groupId) {
            remoteCurr = ii;
            return;
          }
        })
        // 3. 如果 远端的userItem不为空，需要赋值
        if (remoteCurr) {
          if (!remoteCurr.id) remoteCurr.id = remoteCurr.groupId;
          localItem.lastLog = remoteCurr.lastLog;
          localItem.lastLogTime = remoteCurr.lastLogTime;
          localItem.lastLogVersion = remoteCurr.lastLogVersion;
          // localItem.onLineStatus = remoteCurr.onLineStatus;
          // localItem.onLineInfo = remoteCurr.onLineInfo;
          localItem.msgItemList = [...remoteCurr.msgItemList]; // ...localItem.msgItemList,
          this.$set(groupItemList, index, localItem);
        }
        if (localItem.msgItemList && localItem.msgItemList.length > 0) {
          // 消息去重
          localItem.msgItemList = this.my_common.listToSet(localItem.msgItemList, "chatMsgReceiveId");
          // 筛选 不是当前用户发送的消息， 即 currUserId != 当前用户的id
          let receiveMsgList = localItem.msgItemList; // .filter(obj => obj.currUserId != this.userInfo.userId)
          let temp = receiveMsgList[receiveMsgList.length - 1];
          if (temp) {
            localItem.lastLogTime = temp.chatTime;
            localItem.lastLogVersion = new Date(temp.chatTime).getTime();
            localItem.lastLog = temp.fid ? temp.msg.split(",")[0] : temp.msg;
          }
          localItem.unReadCount = localItem.msgItemList.filter(obj => !obj.hadRead).length;
        }
        if (this.currentGroup && localItem.groupId == this.currentGroup.groupId) {
          this.messageList = localItem.msgItemList;
          localItem.msgItemList.forEach(obj => obj.hadRead = 1);
          localItem.unReadCount = 0;
        }
        // console.log(`address_book.vue============>watch.chatGroupStore:${JSON.stringify(localItem)}`);
      })
      this.$set(this.local_chatGroupStore, 'groupItemList', groupItemList);
      this.storeGroupList1();
      // console.log(`chat-group.vue============>watch.local_chatGroupStore:${JSON.stringify(this.local_chatGroupStore)}`);
    },
    chatUserStore(nv) {
      // console.log(`chat chatUserStore============>watch.nv:${JSON.stringify(nv)}`);
      // this.local_chatUserStore = nv;
      if (!(nv && nv.userItemList && nv.userItemList.length > 0)) {
        return;
      }
      // 1. 记录本地 userItemList
      let userItemList = this.local_chatUserStore.userItemList;
      // this.$set(this.local_chatUserStore, 'userItemList', []);
      userItemList.forEach((localItem, index) => {
        if (!localItem.msgItemList) localItem.msgItemList = [];
        if (!localItem.lastLogVersion) localItem.lastLogVersion = 0;
        let remoteCurr = null;
        // 2. 遍历远端的 userItemList
        nv.userItemList.forEach(ii => {
          if (localItem.currUserId == ii.currUserId) {
            remoteCurr = ii;
            return;
          }
        })
        if (localItem.currUserId == this.userInfo.userId) {
          localItem.userName = "文件传输助手";
        }
        // 3. 如果 远端的userItem不为空，需要赋值
        if (remoteCurr) {
          localItem.lastLog = remoteCurr.lastLog;
          localItem.lastLogTime = remoteCurr.lastLogTime;
          localItem.lastLogVersion = remoteCurr.lastLogVersion;
          localItem.onLineStatus = remoteCurr.onLineStatus;
          localItem.onLineInfo = remoteCurr.onLineInfo;
          localItem.msgItemList =
            this.my_common.listToSet([...localItem.msgItemList, ...remoteCurr.msgItemList], "chatMsgReceiveId"); //
          this.$set(userItemList, index, localItem);
        }
        if (localItem.msgItemList && localItem.msgItemList.length > 0) {
          // 筛选 不是当前用户发送的消息， 即 currUserId != 当前用户的id
          let receiveMsgList = localItem.msgItemList; // .filter(obj => obj.currUserId != this.userInfo.userId)
          let temp = receiveMsgList[receiveMsgList.length - 1];
          if (temp) {
            localItem.lastLogTime = this.my_common.timeKeepM(temp.chatTime);
            localItem.lastLogVersion = new Date(temp.chatTime).getTime();
            localItem.lastLog = temp.fid ? temp.msg.split(",")[0] : temp.msg;
          }
          localItem.unReadCount = localItem.msgItemList.filter(obj => !obj.hadRead).length;
        }
        if (this.currentUser && localItem.currUserId == this.currentUser.currUserId) {
          this.messageList = localItem.msgItemList;
          localItem.msgItemList.forEach(obj => obj.hadRead = 1);
          localItem.unReadCount = 0;
        }
        // console.log(`address_book.vue============>watch.chatUserStore:${JSON.stringify(localItem)}`);
      })

      this.$set(this.local_chatUserStore, 'userItemList', userItemList);
      this.storeUserList1();
      // console.log(`chat chatUserStore============>watch.chatUserStore:${JSON.stringify(this.local_chatUserStore)}`);
    },
  },
  methods: {
    parentReceiveMsg(msgObj) {
      let that = this;
      if (!msgObj || typeof msgObj !== 'object') return;
      if (msgObj.type == 'mic_start_fail') {
        this.$modal && this.$modal.msgWarning && this.$modal.msgWarning('语音识别暂不可用');
        return;
      }
      if (msgObj.type == 'mic_start_ok') {
        // this.myStart_txt2voice('你好!请说出您的渔业相关问题。');
        if (that.agentModel.dialogType == 2) { // 机器人模式
          this.exe_txt2voice('你好！我是小科，有什么我可以帮助你的吗？');
        } else {
          this.isMicState = this.isMicState_;
          this.$modal.msgSuccess('成功开启麦克风')
        }
        // 记录当前输入框内容为本轮识别的前缀，保留用户此前的手动输入与历史文本
        that.asr_prefix = that.msg || "";
        // 清空本轮识别的增量缓冲
        that.full_msg = "";
      } else if (msgObj.type == 'finished_msg') {
        if (!that.full_msg && msgObj.tempValue) {
          msgObj.tempValue = this.my_common.myReplaceMany(msgObj.tempValue, ["，", "？", "。", "："])
        }
        // 最终结果：以开启识别时的文本为前缀 + 本轮完整识别内容
        that.full_msg += msgObj.tempValue;
        that.msg = that.asr_prefix + that.full_msg;
        if (this.mic_timer) clearTimeout(this.mic_timer);
        this.mic_timer = setTimeout(() => {
          if (that.agentModel.dialogType == 2) {
            this.try_sendMsg(that.msg);
            that.full_msg = "";
          }
        }, 3000);
      } else if (msgObj.type == 'changed_msg') {
        // 实时中间结果：更新本轮增量缓冲，并实时刷新输入框，保证能看到识别文字
        that.full_msg = msgObj.value || "";
        that.msg = that.asr_prefix + that.full_msg;
      }
      // console.log(`===================>msg:${JSON.stringify(msgObj)}`);
    },

    toggleMic() {
      this.isMicState_ = !this.isMicState;
      // 1. 关闭麦克
      if (!this.isMicState_) {
        this.$refs.AliyunVoice2TxtRef.stopRecording();
        this.isMicState = this.isMicState_;
        return;
      }
      // 2. 开启麦克
      let flag = this.$refs.AliyunVoice2TxtRef.judgeIsStart();
      this.isMicState = this.isMicState_;
      if (flag) { // 2.1 已开启
      } else { // 2.2 需要开启
        // start mic
        this.$refs.AliyunVoice2TxtRef.prepareRecording();
      }
      // 3. 关闭机器人模式
      this.agentModel.dialogType = 1;
    },
    stopVoice() {
      console.log(`===========================>stopVoice:`, this.$refs.AliyunTxt2VoiceVue);
      // return;
      this.full_msg = "";
      this.msg = "";
      this.$refs.AliyunTxt2VoiceVue.myEnd();
    },
    myStart_txt2voice(msgObj) {
      // debugger
      if (!msgObj || !msgObj.content) return;
      this.exe_txt2voice(msgObj.content);
    },
    exe_txt2voice(text) {
      let that = this;
      if (!text) return;
      that.agentModel.isStart_txt2voice = true;
      let currMsg =
        this.my_common.myReplaceMany(text, [
          "<br>",
          "#",
          " ",
          "	",
          "\t",
          "：",
          "\n",
          "*",
          "？",
        ]) + "。"; // , "."
      // console.log(`txt_play_enhancer================>${currMsg}`);
      // debugger
      let canStart = currMsg && this.$refs.AliyunTxt2VoiceVue;
      // console.log(`myStart_txt2voice：======条件：${canStart}========>msgObj:${JSON.stringify(currMsg)}`);
      if (canStart) {
        this.$refs.AliyunTxt2VoiceVue.myStart(currMsg);
      } else {
        console.log(`条件：${currMsg},ref:${this.$refs.AliyunTxt2VoiceVue}, 不用语音`);
      }
    },
    // 通知 数字人开始说话
    exe_txt2voice_startPlay() {
      this.funasrNeedStop = true;
      this.agentModel.isStart_txt2voice = true;
      this.iframe_sendMsg("mouthFn");
      // console.log(`exe_txt2voice_startPlay================>开始播放`);
    },
    // 通知 数字人结束说话
    exe_txt2voice_endPlay() {
      this.agentModel.isStart_txt2voice = false;
      this.iframe_sendMsg("mouthStopFn");
      this.funasrNeedStop = false;
      // console.log(`exe_txt2voice_endPlay================>结束播放`);
    },
    myEnd_txt2voice() {
      this.$refs.AliyunTxt2VoiceVue.myEnd();
    },
    // 新一轮对话开始前，打断并清空上一轮语音播报
    interruptCurrentSpeech(reason = "new_message") {
      try {
        this.myEnd_txt2voice();
      } catch (e) {
        console.warn("interruptCurrentSpeech myEnd failed:", e);
      }
      this.agentModel.isStart_txt2voice = false;
      this.funasrNeedStop = false;
      try {
        this.iframe_sendMsg("mouthStopFn");
      } catch (e) {
        // iframe 不可用时忽略，不影响主流程
      }
      console.log(`interruptCurrentSpeech: ${reason}`);
    },
    // 开始播放： mouthFn , 结束播放： mouthStopFn
    iframe_sendMsg(msg) {
      // console.log(`========>parent click`);
      //获取iframe元素
      var iframe = document.querySelector("iframe");
      //通过iframe的contentWindow给云服务器发送请求（这里省略了云服务器的url）
      // iframe.contentWindow.postMessage("export", "*");
      iframe.contentWindow.postMessage(
        { type: "parent_send_to_son", value: `${msg}` },
        "*"
      );
    },
    iframe_receiveMsg() {
      let that = this;
      //允许任意URL发送消息，不会跨域
      window.postMessage("message", "*");
      //监听传过来的消息
      window.addEventListener("message", function (event) {
        // console.log(`event parent==========>`, event)
        if (event.data.type == "son_send_to_parent") {
          console.log(`我父页面，收到子组件消息：${JSON.stringify(event.data)}`);
          that.toPage("/");
        }
      });
    },
    can_askScore(item) {
      if (!this.userInfo.roleCode) return false;
      let flag = this.userInfo.roleCode.indexOf("ke_admin") != -1;
      return flag;
    },
    async copyMsg(currDoc) {
      try {
        // 获取要复制的HTML内容
        const content = this.my_common.decode(currDoc.target.innerHTML);
        // 使用 navigator.clipboard.writeText() 复制内容到剪贴板
        await navigator.clipboard.writeText(content);
        // console.log('内容已复制到剪贴板！', content);
        this.$modal.msgSuccess("内容已复制到剪贴板！");
      } catch (err) {
        console.error("复制失败:", err);
        this.$modal.msgError(`复制失败!`, err);
      }
    },
    changeAskScore(score, item, index) {
      let that = this;
      let param = {
        fid: item.key,
        userId: that.userInfo.userId,
        askScore: score,
      };
      item.askScore = score;
      let messageList = this.messageList;
      messageList.splice(index, 1, item);

      this.messageList = messageList;
      localStorage.setItem(ChatLogKey, JSON.stringify(this.messageList));
      // console.log(`score:${score} 修改评分：${JSON.stringify(this.messageList)}`);
      // return;
      updateChatLog(param).then((response) => {
        console.log("评分成功");
      });
    },
    // 时间戳：1637244864707
    /*
        时间戳转换为时间
        type: 1:yyyy-MM-dd HH:mm:ss;   2:yyyy-MM-dd;
    */
    timestampToTime(timestamp, type) {
      timestamp = timestamp ? timestamp : null;
      let date = new Date(timestamp); //时间戳为10位需*1000，时间戳为13位的话不需乘1000
      let Y = date.getFullYear() + "-";
      let M =
        (date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1) +
        "-";
      let D = (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + " ";
      let h = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":";
      let m =
        (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) + ":";
      let s = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

      if (type == 1) return Y + M + D + h + m + s;
      else if (type == 2) return Y + M + D;
    },
    parentReceive(obj = {}) {
      // console.log("子组件传过来的=========" + JSON.stringify(obj));
    },
    // 关闭聊天窗口
    closeChat() {
      let that = this;
      // 关闭窗口时立即停止当前语音播报与数字人口型
      this.interruptCurrentSpeech("close_chat");
      // 若当前仍在流式返回，关闭窗口时一并中止，避免后台继续追加语音/文本
      if (this._chatStreamAbort) {
        try {
          this._chatStreamAbort.abort();
        } catch (e) {
          /* ignore */
        }
        this._chatStreamAbort = null;
      }
      this.isModelGen = false;
      that.$refs.AliyunVoice2TxtRef.stopRecording();
      that.agentModel.dialogType = 1;
      that.isMicState = false;
      this.jq(".chatBox").toggle(10);
      // 关闭时强制隐藏数字人（避免 toggle 导致状态错乱）
      that.number_phone_flag = false;
      // console.log(`================>number_phone_flag:${that.number_phone_flag}`);
    },
    // 打开聊天窗口
    openChat() {
      let that = this;
      let jq = this.jq;
      this.jq(".chatBox").toggle(10);
      this.$nextTick(function () {
        that.jq(document).ready(function () {
          // 打开时强制显示数字人（无论 tab/窗口宽度）
          that.number_phone_flag = true;
          // 还原：打开聊天时默认切回“智能体”页，确保数字人区域可见
          that.activeTab = "agent";
          // console.log(`================>number_phone_flag:${that.number_phone_flag}`);
          that.scrollToBottom();
          setTimeout(() => {
            // console.log("=========>执行了点击聊天窗口。");
          }, 1000);
        });
      });
    },
    scrollToBottom() {
      this.$nextTick(function () {
        const chatContainer = document.getElementById('chat-content');
        chatContainer.scrollTop = chatContainer.scrollHeight + 555;
        chatContainer.scrollTo(0, chatContainer.scrollHeight + 555);
        console.log(`=================>scrollToBottom:${chatContainer.scrollHeight}`);
      });
    },
    // 对话模式发送消息
    async try_sendMsg(paramMsg) {
      try {
        if (this.isModelGen) {
          this.$modal.msgWarning("小科思考中……");
          return;
        }
        await this.sendMsg(paramMsg);
        if (this.isModelGen) {
          this.exe_txt2voice('已收到你的问题，小科这边已经在思考中。');
        }
      } catch (err) {
        this.isModelGen = false;
        console.log(`===========>sendMsg error ! `, err);
        this.exe_txt2voice('这边暂时出现异常了，请联系我的管理员呢!');
      }
    },
    // 发送消息 汇总方案
    async sendSumMsg() {
      const paramMsg = this.msg;
      console.log(`chat sendSumMsg==========>activeTab:${this.activeTab},msg:${this.msg}`)
      // 发送消息 类型需要判断
      switch (this.activeTab) {
        case "agent":
          // this.loadAgentContent();
          await this.sendMsg(paramMsg);
          break;
        case "single":
          // this.loadSingleChat();
          this.sendUserMsg(paramMsg);
          break;
        case "group":
          // this.loadGroupChat();
          this.sendGroupMsg(paramMsg);
          break;
      }
    },
    async resolveAgentRouteHint(textContent) {
      const conflict = detectRouteHintConflict(this.agentRouteHint, textContent);
      if (!conflict) {
        return this.agentRouteHint;
      }
      try {
        await this.$confirm(conflict.message, "意图确认", {
          confirmButtonText: conflict.confirmText,
          cancelButtonText: conflict.cancelText,
          type: "warning",
          distinguishCancelAndClose: true,
        });
        return conflict.confirmHint;
      } catch (action) {
        if (action === "cancel") {
          return conflict.cancelHint;
        }
        return null;
      }
    },
    // 发送消息给智能体
    async sendMsg(paramMsg) {
      let that = this;
      // console.log(`sendMsg=====msg:${this.msg}======>full_msg:${that.full_msg}`);
      // this.msg = "";
      // that.full_msg = "";
      // return;
      var textContent = (paramMsg && typeof paramMsg == 'string') ? paramMsg : that.msg;
      // console.log(`sendMsg=====paramMsg:${paramMsg}======>textContent:${textContent}`);
      // return;
      if (textContent != "") {
        if (this.isModelGen) {
          return;
        }
        let routeHint = this.agentRouteHint;
        if (this.activeTab === "agent") {
          const resolved = await this.resolveAgentRouteHint(textContent);
          if (resolved === null) {
            return;
          }
          routeHint = resolved;
          this.agentRouteHint = resolved;
        }
        this.pushMessages("user", textContent);
        this.scrollToBottom();
        //发送后清空输入框
        that.msg = "";
        that.full_msg = "";
        // 显示「思考中」气泡，等待回复后隐藏
        this.isModelGen = true;
        // 发送聊天信息（SSE 流式：HTTP 调用后端 /fish/zk-ai-agent/chatPyStream，由 Java 中转到 Python /api/chat/stream）
        const req = {
          user_id: String(that.userInfo.userId || ""),
          conversation_id: that.uid || "",
          message: textContent,
          route_hint: normalizeRouteHint(routeHint),
        };
        // 1) 懒创建 AI 气泡：拿到首个可见内容再创建，避免“空白气泡”
        let aiIndex = -1;
        const ensureAiBubble = (initialText = "") => {
          if (aiIndex >= 0 && this.messageList[aiIndex]) return aiIndex;
          aiIndex = this.pushMessages("ai", initialText || "");
          return aiIndex;
        };
        // 数据录入：同一 session_id 的表格“迁移到最新回复下方”，保持对话顺序自然
        const moveDataEntryTableToLatest = (viz, currentIdx) => {
          try {
            if (!viz || viz.type !== "data_entry") return currentIdx;
            const sid = viz.session_id || "";
            if (!sid) return currentIdx;
            const cleanLegacyTip = (idx) => {
              try {
                const m = this.messageList[idx];
                if (!m) return;
                const tip = "已更新上方表格，请在上方查看最新数据。";
                const s = String(m.msg || "");
                if (!s) return;
                if (s.indexOf(tip) >= 0) {
                  const cleaned = s
                    .split("\n")
                    .filter((line) => String(line || "").trim() && String(line || "").trim() !== tip)
                    .join("\n");
                  m.msg = cleaned;
                }
              } catch (e) {
                /* ignore */
              }
            };
            // 从后往前找“上一条 data_entry 且同 session_id”的 AI 气泡
            for (let i = this.messageList.length - 1; i >= 0; i--) {
              if (i === currentIdx) continue;
              const m = this.messageList[i];
              if (!m || m.role !== "ai" || !m.viz) continue;
              if (m.viz.type === "data_entry" && m.viz.session_id === sid) {
                // 把旧表格清掉，把新表格挂到当前气泡下面（最新一条）
                try {
                  if (this.messageList[i]) this.$set(this.messageList[i], "viz", null);
                } catch (e) {
                  /* ignore */
                }
                if (currentIdx >= 0 && this.messageList[currentIdx]) {
                  this.$set(this.messageList[currentIdx], "viz", viz);
                }
                // 清理历史遗留的“已更新上方表格”提示（该方案已弃用）
                cleanLegacyTip(i);
                cleanLegacyTip(currentIdx);
                return currentIdx;
              }
            }
          } catch (e) {
            /* ignore */
          }
          return currentIdx;
        };
        let lastProgressText = "";
        // SSE 语音播报：按句触发，避免等整段结束才发声
        let ttsBuffer = "";
        let ttsHasSpokenInStream = false;
        const speakChunk = (txt) => {
          const s = String(txt || "").trim();
          if (!s) return;
          ttsHasSpokenInStream = true;
          this.exe_txt2voice(s);
        };
        const flushTtsByPunctuation = () => {
          // 命中句末标点则播报前半句，保留尾巴继续拼接
          const m = ttsBuffer.match(/([\s\S]*?[。！？!?；;：:\n])/);
          if (!m || !m[1]) return;
          const sentence = m[1];
          ttsBuffer = ttsBuffer.slice(sentence.length);
          speakChunk(sentence);
        };
        let lastFlushAt = 0;
        const flush = () => {
          const now = Date.now();
          if (now - lastFlushAt > 300) {
            lastFlushAt = now;
            localStorage.setItem(ChatLogKey, JSON.stringify(this.messageList));
          }
        };

        // 2) 启动流式
        const controller = new AbortController();
        this._chatStreamAbort = controller;
        // 新一轮回复开始到达时才打断上一轮语音（只打断一次）
        let interruptedForThisTurn = false;
        const interruptOnce = (why) => {
          if (interruptedForThisTurn) return;
          interruptedForThisTurn = true;
          this.interruptCurrentSpeech(why || "new_reply_start");
        };

        chatPyStream(req, {
          signal: controller.signal,
          onEvent: (evt) => {
            try {
              const e = evt || {};
              if (e.type === "meta") {
                interruptOnce("sse_meta");
                // meta 可能早于 delta（尤其追问场景无开场 progress），须先占位气泡再写 viz/conversation_id
                const idx = ensureAiBubble("");
                const conversationId = e.conversation_id || e.conversationId || "";
                if (conversationId) {
                  this.uid = conversationId;
                  if (this.messageList[idx]) {
                    this.messageList[idx].conversation_id = conversationId;
                  }
                }
                if (e.viz && this.messageList[idx]) {
                  // A: 历史表格不加载下拉。仅“最新未提交”一张表可编辑、可请求下拉。
                  try {
                    const isDataEntry = e.viz && e.viz.type === "data_entry";
                    if (isDataEntry) {
                      // 先把已有 data_entry 表格全部置为不可编辑，避免回显时请求风暴
                      for (let i = 0; i < this.messageList.length; i++) {
                        const m = this.messageList[i];
                        if (m && m.viz && m.viz.type === "data_entry") {
                          this.$set(m.viz, "_editable", false);
                        }
                      }
                      // 当前最新表格：未提交才允许编辑/加载下拉
                      const submitted = !!e.viz._dataEntrySubmitted;
                      this.$set(e.viz, "_editable", !submitted);
                    }
                  } catch (e2) {
                    /* ignore */
                  }
                  this.$set(this.messageList[idx], "viz", e.viz);
                  // 同 session_id：把表格移动到最新一条回复下面
                  aiIndex = moveDataEntryTableToLatest(e.viz, idx);
                }
              } else if (e.type === "progress") {
                interruptOnce("sse_progress");
                // 让“思考过程”可见：将进度消息也作为增量展示，但做去重避免刷屏
                const text = String(e.content || "").trim();
                if (!text || text === lastProgressText) return;
                lastProgressText = text;
                const idx = ensureAiBubble("");
                if (!this.messageList[idx]) return;
                const prev = this.messageList[idx].msg || "";
                const joiner = prev.endsWith("\n") || !prev ? "" : "\n";
                this.messageList[idx].msg = `${prev}${joiner}${text}\n`;
              } else if (e.type === "delta") {
                interruptOnce("sse_delta");
                const part = String(e.content || "");
                if (!part) return;
                const idx = ensureAiBubble("");
                if (!this.messageList[idx]) return;
                this.messageList[idx].msg = (this.messageList[idx].msg || "") + part;
                // 流式增量到达时按句播报
                ttsBuffer += part;
                flushTtsByPunctuation();
              } else if (e.type === "final") {
                interruptOnce("sse_final");
                const finalText = String(e.content || "");
                if (finalText) {
                  const idx = ensureAiBubble("");
                  if (this.messageList[idx]) this.messageList[idx].msg = finalText;
                  // 若前面未触发过语音，final 时整段播报；否则仅播报残留尾句
                  if (!ttsHasSpokenInStream) {
                    this.exe_txt2voice(finalText);
                  } else if (ttsBuffer.trim()) {
                    speakChunk(ttsBuffer);
                    ttsBuffer = "";
                  }
                } else if (aiIndex < 0) {
                  // 兜底：服务端 final 为空且前面没有 delta 时，给一个可见回复，避免一直“思考中”
                  ensureAiBubble("暂时没有可展示的回复，请稍后重试。");
                }
                this.isModelGen = false;
                // 最终答案必须立即落本地缓存，避免仅依赖 flush 节流导致刷新后仍停留在「思考步骤」
                localStorage.setItem(ChatLogKey, JSON.stringify(this.messageList));
                flush();
              } else {
                // 兜底：当作增量文本
                if (e.content) {
                  const idx = ensureAiBubble("");
                  if (this.messageList[idx]) {
                    this.messageList[idx].msg = (this.messageList[idx].msg || "") + String(e.content || "");
                  }
                }
              }
              this.scrollToBottom();
              flush();
            } catch (err) {
              console.error("SSE事件处理失败:", err);
            }
          },
        })
          .catch((err) => {
            console.error("调用智能体流式接口失败:", err);
            const idx = ensureAiBubble("不好意思呢！服务器异常，请稍后再试。");
            if (this.messageList[idx]) this.messageList[idx].msg = this.messageList[idx].msg || "不好意思呢！服务器异常，请稍后再试。";
            localStorage.setItem(ChatLogKey, JSON.stringify(this.messageList));
          })
          .finally(() => {
            this.isModelGen = false;
            this._chatStreamAbort = null;
            try {
              localStorage.setItem(ChatLogKey, JSON.stringify(this.messageList));
            } catch (e) {
              /* ignore */
            }
          });
      }
    },
    msg_enhancer(text) {
      // ChatMsg 会先 _escapeHtml 再按行渲染，<br> 会变成可见字符；历史记录里也可能含后端转的 <br>
      let t = String(text || "");
      t = t.replace(/<br\s*\/?>/gi, "\n");
      // 清理历史遗留提示（旧交互方案遗留到本地缓存里）
      const legacyTip = "已更新上方表格，请在上方查看最新数据。";
      if (t.indexOf(legacyTip) >= 0) {
        t = t
          .split(/\r?\n/)
          .filter((line) => String(line || "").trim() && String(line || "").trim() !== legacyTip)
          .join("\n");
      }
      return t.replace(/\n{3,}/g, "\n\n");
    },
    // 初始化消息
    initMessages() {
      let that = this;
      let msgs = localStorage.getItem(ChatLogKey);
      // console.log(`=====>msgs:`, msgs);
      if (msgs) {
        let messageList = JSON.parse(msgs);
        // console.log(`最新msg内容：`, messageList); // != ""
        messageList = messageList
          .filter((item) => {
            // console.log(`===============>item:`, item)；
            return item && (item.msg && typeof item.msg == 'string' && item.msg.indexOf('502 Bad Gateway') == -1) || (item.think && typeof item.think == 'string');
          })
          .map((obj) => {
            if (!obj.askScore) obj.askScore = 1;
            if (obj.msg) obj.msg = this.msg_enhancer(obj.msg);
            if (obj.think) obj.think = this.msg_enhancer(obj.think);
            return obj;
          });
        this.messageList = messageList;
        if (messageList.length > 0) {
          for (let i = messageList.length - 1; i >= 0; i--) {
            if (messageList[i].conversation_id) {
              this.uid = messageList[i].conversation_id;
              break;
            }
          }
        }
        setTimeout(() => {
          that.$nextTick(() => {
            that.scrollToBottom();
          });
        }, 1500);
        // console.log(`从缓存拿到消息是：`, this.messageList);
      } else {
        let i1 = {
          role: "ai",
          msg: "你好，我是科德智能体，有什么能够帮助到您",
          chatTime: "2025-03-02 14:26:58",
          img: "",
          msgType: "first",
        };
        // let i2 = {"role": "user","msg": "帮我跳转到系统主页。", "chatTime": "2025-03-02 14:27:58", "img": ""};
        // let i3 = {"role": "ai","msg": "好的，已为您跳转系统主页。", "chatTime": "2025-03-02 14:28:09", "img": ""};
        // this.messageList.push(i1);
        // this.messageList.push(i2);
        // this.messageList.push(i3);
      }
    },
    pushMessages(role, msg, conversationId) {
      if (msg === undefined || msg === null) return -1;
      if (msg === "" && role !== "ai") return -1;
      const chatTime = this.timestampToTime(new Date().getTime(), 1);
      let i1 = { role: role, msg: msg, chatTime: chatTime };
      if (conversationId && (role === "ai" || !role)) i1.conversation_id = conversationId;
      this.messageList.push(i1);
      localStorage.setItem(ChatLogKey, JSON.stringify(this.messageList));
      return this.messageList.length - 1;
    },
    dongtai_func() {
      let that = this;
      let jq = that.jq;
      // 创建鱼类装饰元素
      createFishDecorations();

      // 发送按钮点击事件
      // const sendBtn = document.querySelector('.send-btn');
      const textarea = document.getElementById('query_textarea');
      // console.log(`keydown==================>0 `, textarea);

      textarea.addEventListener('keydown', function (e) {
        // console.log(`keydown=key:${e.key}==ctrlKey:${e.ctrlKey}===============>1`)
        if (e.key == 'Enter' && e.ctrlKey) {
          // console.log(`keydown==================> ctrl + enter`);
          e.preventDefault();
          that.msg += "\n";
          return;
        } else if (e.key === 'Enter' && !e.shiftKey) {
          // console.log(`keydown==================>enter`);
          e.preventDefault();
          that.sendSumMsg(that.msg);
          return;
        }
      });

      // 水波纹效果
      setInterval(createRipple, 3000);

      // 消息气泡悬停效果
      const messageBubbles = document.querySelectorAll('.message-bubble');
      messageBubbles.forEach(bubble => {
        bubble.addEventListener('mouseenter', function () {
          this.style.transform = 'translateY(-5px)';
          this.style.boxShadow = '0 5px 15px rgba(0, 0, 0, 0.2)';
        });

        bubble.addEventListener('mouseleave', function () {
          this.style.transform = 'translateY(0)';
          this.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
        });
      });
    },
    // START: 新增Tab切换方法
    handleTabChange(tabName) {
      this.activeTab = tabName;
      // 根据TAB加载不同内容
      switch (tabName) {
        case "agent":
          // this.loadAgentContent();
          this.initMessages();
          this.currentUser = undefined;
          this.currentGroup = undefined;
          this.chatTitle = '渔业科技智能助手';
          break;
        case "single":
          // this.loadSingleChat();
          this.chatTitle = '单聊';
          this.currentGroup = undefined;
          this.userList1 = this.local_chatUserStore.userItemList;
          this.messageList = [];
          break;
        case "group":
          // this.loadGroupChat();
          this.chatTitle = '群聊';
          this.groupList1 = this.local_chatGroupStore.groupItemList;
          this.messageList = [];
          this.currentUser = undefined;
          break;
      }
    },
    // END: Tab切换方法
    // init 操作负责去重、未读、已读 更新
    initChatGroupStore() {
      let chatGroupStoreStr = localStorage.getItem(this.group_cacheKey);
      if (!chatGroupStoreStr) return;
      let param = JSON.parse(chatGroupStoreStr);
      if (!(param && param.groupItemList && param.groupItemList.length > 0)) return;
      param.groupItemList.filter(obj => !this.my_common.listIsEmpty(obj.msgItemList))
        .forEach(obj => obj.unReadCount = obj.msgItemList.filter(m => !m.hadRead).length);
      localStorage.setItem(`${this.group_cacheKey}`, JSON.stringify(param));
      param.groupItemList.filter(obj => obj.unReadCount).forEach(obj => this.group_unReadCount += obj.unReadCount);
      this.local_chatGroupStore = param;
      // console.log(`initChatGroupStore===========>${JSON.stringify(this.local_chatGroupStore)}`);
    },
    initChatUserStore() {
      let chatUserStoreStr = localStorage.getItem(`${UserChatLogKey}_${this.userInfo.userId}`);
      if (!chatUserStoreStr) return;
      let param = JSON.parse(chatUserStoreStr);
      if (!(param && param.userItemList && param.userItemList.length > 0)) return;
      // console.log(`chat initChatUserStore 0=================>param:${JSON.stringify(param)}`);
      param.userItemList.forEach(l => l.msgItemList = this.my_common.listToSet(l.msgItemList, "chatMsgReceiveId"));
      param.userItemList.filter(obj => !this.my_common.listIsEmpty(obj.msgItemList))
        .forEach(obj => obj.unReadCount = obj.msgItemList.filter(m => !m.hadRead).length);
      param.userItemList.filter(obj => this.my_common.listIsEmpty(obj.msgItemList)).forEach(obj => obj.unReadCount = 0);
      // console.log(`initChatUserStore 1=================>param:${JSON.stringify(param)}`);
      // localStorage.setItem(`${this.user_cacheKey}`, JSON.stringify(param));
      this.local_chatUserStore = param;
      // console.log(`chat initChatUserStore 2=================>param:${JSON.stringify(param)}`);
    },
    // list列表获取 初始化对应 local_store
    getUserList() {
      // 1. 从缓存中获取
      this.initChatUserStore();
      // 2. 更新用户列表，并合并聊天列表
      this.userList1 = []
      return listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data);
        // console.log(`chat getUserList=========>${JSON.stringify(this.userList1)}`);
        this.userList1.forEach(item => {
          item.currUserName = item.id == this.userInfo.userId ? "文件传输助手" : `${item.nickname}`;
          item.currUserId = `${item.id}`;
          item.currUserAvatar = `${item.avatar}`;
          item.lastLogVersion = 0;
          let localItem = null;
          // console.log(`chat getUserList 1 =========>local_chatUserStore:${JSON.stringify(this.local_chatUserStore)}`);
          this.local_chatUserStore.userItemList.forEach(ii => {
            if (ii.currUserId == item.currUserId) {
              localItem = ii;
              return;
            }
          })
          if (localItem) {
            localItem.currUserName = item.currUserName;
            localItem.currUserAvatar = item.avatar;
          } else {
            this.local_chatUserStore.userItemList.push({
              "currUserId": item.currUserId,
              "currUserName": item.currUserName,
              "currUserAvatar": item.avatar,
              "lastLog": "",
              "lastLogTime": "",
              "onLineStatus": "0",
              "onLineInfo": "不在线",
              "msgItemList": [],
            });
          }
        })
        // console.log(`getUserList====user_cacheKey:${this.user_cacheKey}=====>${JSON.stringify(this.local_chatUserStore)}`);
        localStorage.setItem(this.user_cacheKey, JSON.stringify(this.local_chatUserStore));

        // console.log(`chat getUserList 2=========>${JSON.stringify(this.local_chatUserStore)}`);
        let userIdStr = this.userList1.map(obj => obj.id).join(",");
        let version = new Date().getTime();
        let params = {
          "currUserId": this.userInfo.userId,
          "oppositeUserId": this.userInfo.userId,
          "chatTime": formatDate(version),
          "msg": userIdStr,
          "operateKey": ASK_CHAT_LOG_LIST,
          "version": version,
        }

        // this.$socketPublic.dispatch('sendMsg', JSON.stringify(params),
        //   "通讯录： /pages/address_book/address_book"); // 发送WebSocket消息
      })
    },
    /** 查询列表 */
    async getGroupList() {
      this.initChatGroupStore();
      const queryParams = {
        pageNo: 1,
        pageSize: 500
      }
      const res = await ZkChatGroupApi.getZkChatGroupPage(queryParams);
      res.data.list.forEach(l => {
        l.msgItemList = [];
        l.groupId = l.id;
        l.lastLogVersion = 0;
      })
      if (this.my_common.listIsEmpty(this.local_chatGroupStore.groupItemList)) {
        this.local_chatGroupStore.groupItemList = res.data.list;
      }

      this.groupList1 = [];
      this.my_common.list2listHandle(this.local_chatGroupStore.groupItemList, "groupId", res.data.list, "groupId", (o, t) => {
        this.groupList1.push({ ...t, ...o });
        // console.log(`o:${JSON.stringify(o)}========>t:${JSON.stringify(t)}`);
      })
      // console.log(`========>list:${JSON.stringify(this.groupList1)}`);
      this.local_chatGroupStore.groupItemList = this.groupList1;
      this.local_chatGroupStore.groupItemList.forEach(l => l.msgItemList = this.my_common.listToSet(l.msgItemList, "chatMsgReceiveId"));
      localStorage.setItem(this.group_cacheKey, JSON.stringify(this.local_chatGroupStore));
      // console.log(`chat getGroupList==========>local_chatGroupStore.groupItemList:${JSON.stringify(this.local_chatGroupStore)}`);
    },
    sendGroupMsg4File(msg, fid) {
      let that = this;
      this.formData = {
        groupId: this.currentGroup.id,
        targetUserId: undefined,
        msg: msg,
        fid: fid,
        msgType: "0",
        userId: this.userInfo.userId,
        deptId: this.userInfo.deptId,
        currUserId: this.userInfo.userId,
        isSelf: true,
        deviceType: "pc,app",
        chatTime: formatDate(new Date()),
      }
      // console.log(`sendGroupMsg4File=============>formData:${JSON.stringify((this.formData))}`)
      this.sendMsgAsk(this.formData);
    },
    sendGroupMsg(inputText) {
      if (!inputText.trim()) return

      this.formData = {
        groupId: this.currentGroup.id,
        targetUserId: undefined,
        msg: inputText,
        fid: undefined,
        msgType: "0",
        userId: this.userInfo.userId,
        deptId: this.userInfo.deptId,
        currUserId: this.userInfo.userId,
        isSelf: true,
        deviceType: "pc,app",
        chatTime: formatDate(new Date()),
      }
      this.sendMsgAsk(this.formData);
    },
    sendUserMsg4File(msg, fid) {
      let that = this;
      this.formData = {
        groupId: undefined,
        targetUserId: undefined,
        msg: msg,
        fid: fid,
        msgType: "0",
        userId: this.userInfo.userId,
        deptId: this.userInfo.deptId,
        currUserId: this.userInfo.userId,
        isSelf: true,
        deviceType: "pc,app",
        chatTime: formatDate(new Date()),
      }
      if (this.activeTab == 'group') {
        this.formData = { ...this.formData, groupId: this.currentGroup.id };
      } else if (this.activeTab == 'agent') {
        return;
      } else if (this.activeTab == 'single') {
        this.formData = { ...this.formData, targetUserId: this.currentUser.currUserId };
      }
      console.log(`sendUserMsg4File======currentUser:${JSON.stringify(this.currentUser)}===activeTab:${this.activeTab}====>formData:${JSON.stringify((this.formData))}`)
      // return;
      this.sendMsgAsk(this.formData);
    },
    sendUserMsg(textContent) {
      let that = this;
      console.log(`chat sendUserMsg=========>currentUser:${JSON.stringify(this.currentUser)}`);
      this.formData = {
        groupId: undefined,
        targetUserId: this.currentUser.currUserId,
        msg: textContent,
        fid: undefined,
        msgType: "0",
        userId: this.userInfo.userId,
        deptId: this.userInfo.deptId,
        currUserId: this.userInfo.userId,
        isSelf: true,
        deviceType: "pc,app",
        chatTime: formatDate(new Date()),
      }

      this.sendMsgAsk(this.formData);
    },
    sendMsgAsk(formData) {
      let that = this;
      ZkChatMsgApi.createZkChatMsg(formData).then(res => {
        // this.pushMessages(this.formData);
        that.msg = ''
        that.scrollToBottom();
      })
    },
    // 点击单聊
    chatToUser(contact) {
      // console.log(`chat chatToUser===========>${JSON.stringify(contact)}`);
      this.currentUser = contact;
      this.activeTab = "single";
      // 更改 title
      this.chatTitle = contact.currUserName;
      // 更改 聊天内容
      // console.log(`chat singleChatToUser this.local_chatUserStore============>${JSON.stringify(this.local_chatUserStore)}`);
      this.my_common.listFindOne(this.local_chatUserStore.userItemList, "currUserId", contact.currUserId, (localItem) => {
        // console.log(`chat singleChatToUser===========>${JSON.stringify(obj)}`);
        this.messageList = localItem.msgItemList;
        localItem.msgItemList.forEach(obj => obj.hadRead = 1);
        localItem.unReadCount = 0;
        this.local_userItem = localItem;
      })
      localStorage.setItem(`${this.user_cacheKey}`, JSON.stringify(this.local_chatUserStore));
      this.userList1 = this.local_chatUserStore.userItemList;
    },
    // 点击群聊
    chatToGroup(contact) {
      this.currentGroup = contact;
      this.activeTab = "group";
      // 更改 title
      this.chatTitle = contact.groupName;
      // 更改 聊天内容
      // console.log(`chat chatToGroup============>contact:${JSON.stringify(contact)}`);
      this.my_common.listFindOne(this.local_chatGroupStore.groupItemList, "groupId", contact.groupId, (localItem) => {
        // console.log(`chat chatToGroup===========>${JSON.stringify(obj)}`);
        this.messageList = localItem.msgItemList;
        localItem.msgItemList.forEach(obj => obj.hadRead = 1);
        localItem.unReadCount = 0;
      })
      localStorage.setItem(`${this.group_cacheKey}`, JSON.stringify(this.local_chatGroupStore));
      this.groupList1 = this.local_chatGroupStore.groupItemList;
    },
    isUserMsg(msg) {
      let flag = false;
      if (this.activeTab == 'agent' && msg.role != 'ai') {
        flag = true;
      } else if (this.activeTab == 'single' && msg.isSelf) {
        flag = true;
      } else if (this.activeTab == 'group' && msg.isSelf) {
        flag = true;
      }
      // console.log(`activeTab:${this.activeTab}=======flag:${flag}=======>msg:${JSON.stringify(msg)}`)
      return flag;
    },
    // 存储 聊天记录并排序
    storeUserList1() {
      let tempList = this.userList1;
      let userList1 = [];
      this.my_common.list2listHandle(this.local_chatUserStore.userItemList, "currUserId", tempList, "currUserId", (o, t) => {
        userList1.push({ ...t, ...o });
        // console.log(`o:${JSON.stringify(o)}========>t:${JSON.stringify(t)}`);
      })
      this.userList1 = this.my_common.smartSortOptimized(userList1, "lastLogVersion", "desc");
      this.local_chatUserStore.userItemList = this.userList1;
      localStorage.setItem(`${this.user_cacheKey}`, JSON.stringify(this.local_chatUserStore));
    },
    storeGroupList1() {
      let tempList = this.groupList1;
      let groupList1 = [];
      this.my_common.list2listHandle(this.local_chatGroupStore.groupItemList, "groupId", tempList, "groupId", (o, t) => {
        groupList1.push({ ...t, ...o });
        // console.log(`o:${JSON.stringify(o)}========>t:${JSON.stringify(t)}`);
      })
      this.groupList1 = this.my_common.smartSortOptimized(groupList1, "lastLogVersion", "desc");
      this.local_chatGroupStore.groupItemList = this.groupList1;
      localStorage.setItem(`${this.group_cacheKey}`, JSON.stringify(this.local_chatGroupStore));
    },
    toDetailPage() {
      if (this.activeTab == 'group') {
        this.$router.push({ path: '/hr/zk-chat-group', query: { groupId: this.currentGroup.groupId } });
      }
    },
    chooseFile() {
      this.fid = getUUID();
      // console.log(`chooseFile===========>fid:${this.fid}：`);
      this.$refs.uploadRef.startUpload();
    },
    confirmUrl({ name, url }) {
      // this.form.confirmProcurementInformationUrl = url;
      this.sendUserMsg4File(`${name},${url}`, this.fid);
      console.log(`confirmUrl=====>name:${name}=======>url:${url}`);
    },
    videoWithPartner() {
      if(this.activeTab != 'single') {
        this.$message.error('请选择单聊模式！');
        return;
      }
      if(!this.local_userItem) {
        this.$message.error('请选择单聊用户！');
        return;
      }
      // this.local_userItem = {
      //   currUserName: "yangzhileader2",
      //   type: 1,
      //   chatName: 'yangzhileader2',
      //   currUserId: 145,
      // }
      // console.log(`GlobalComponent===================>videoWithPartner 开始视频通话`);
      this.$refs.videoChat.videoWithPartner(this.local_userItem);
    },
    phoneWithPartner() {
      this.$refs.videoChat.phoneWithPartner();
    },
  },
  mounted() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.group_cacheKey = `${GroupChatLogKey}_${this.userInfo.userId}`;
    this.user_cacheKey = `${UserChatLogKey}_${this.userInfo.userId}`;
    // 初始化消息 单聊、群聊
    this.getUserList(); // 获取用户列表
    this.getGroupList(); // 获取群组列表
    if (this.videoUserInfo.local_userItem) {
      this.local_userItem = this.videoUserInfo.local_userItem;
    }
    this.videoChatFlag = this.show_VideoChat.notShow ? false : true;
    console.log(`mounted===============>videoChatFlag:${this.videoChatFlag}`);

    // console.log(`mounted===============>userInfo:${JSON.stringify(this.userInfo)}`);
    this.uid = `AiAgent:ChatLog:${this.userInfo.username}_${this.userInfo.nickname}_${this.userInfo.userId}`;
    this.dongtai_func();

    this.initMessages();
    let that = this;
    screenFuc();
    function screenFuc() {
      var topHeight = that.jq(".chatBox-head").innerHeight(); //聊天头部高度
      //屏幕小于768px时候,布局change
      var winWidth = that.jq(window).innerWidth();
      if (winWidth <= 768) {
        var totalHeight = that.jq(window).height(); //页面整体高度
        that.jq(".chatBox-info").css("height", totalHeight - topHeight);
        var infoHeight = that.jq(".chatBox-info").innerHeight(); //聊天头部以下高度
        //中间内容高度
        that.jq(".chatBox-content").css("height", infoHeight - 46);
        that.jq(".chatBox-content-demo").css("height", infoHeight - 46);

        that.jq(".chatBox-list").css("height", totalHeight - topHeight);
        that.jq(".chatBox-kuang").css("height", totalHeight - topHeight);
        that.jq(".div-textarea").css("width", winWidth); //  - 106
      } else {
        that.jq(".chatBox-info").css("height", 495);
        that.jq(".chatBox-content").css("height", 448);
        that.jq(".chatBox-content-demo").css("height", 448);
        that.jq(".chatBox-list").css("height", 495);
        that.jq(".chatBox-kuang").css("height", 495);
        that.jq(".div-textarea").css("width", "100%");
      }
    }
    (window.onresize = function () {
      screenFuc();
    })();
    that.jq(".message-num").each(function () {
      var wdNum = that.jq(this).html();
      if (wdNum == "") {
        that.jq(this).css("padding", 0);
      }
    });

    //进聊天页面
    that.jq(".chat-list-people").each(function () {
      that.jq(this).click(function () {
        var n = that.jq(this).index();
        that.jq(".chatBox-head-one").toggle();
        that.jq(".chatBox-head-two").toggle();
        that.jq(".chatBox-list").fadeToggle();
        that.jq(".chatBox-kuang").fadeToggle();

        //传名字
        that
          .jq(".ChatInfoName")
          .text(that.jq(this).children(".chat-name").children("p").eq(0).html());

        //传头像
        that
          .jq(".ChatInfoHead>img")
          .attr("src", that.jq(this).children().eq(0).children("img").attr("src"));

        //聊天框默认最底部
        that.jq(document).ready(function () {
          that
            .jq("#chatBox-content-demo")
            .scrollTop(that.jq("#chatBox-content-demo")[0].scrollHeight);
        });
      });
    });
  },
};
</script>

<style lang="scss" scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap");
/* @import url("./css/MyChat.css"); */
@import url("./css/chat1.css");

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.chatBox {
  font-family: "Noto Sans SC", sans-serif;
  background: linear-gradient(135deg, #0a192f, #1a3658);
  color: #e6f1ff;
  overflow: hidden;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.water-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg,
      rgba(10, 25, 47, 0.8) 0%,
      rgba(27, 65, 112, 0.6) 100%);
  z-index: -1;
  overflow: hidden;
}

.bubbles {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml;utf8,<svg xmlns=" http://www.w3.org/2000/svg " viewBox="0 0 1440 320"><path fill="rgba(64, 158, 255, 0.1)" d="M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,250.7C672,235,768,181,864,181.3C960,181,1056,235,1152,234.7C1248,235,1344,181,1392,154.7L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') no-repeat;
  background-size: cover;
  animation: wave 15s linear infinite;
}

.bubbles:nth-child(2) {
  opacity: 0.5;
  animation: wave 20s linear infinite reverse;
}

.bubbles:nth-child(3) {
  opacity: 0.3;
  animation: wave 25s linear infinite;
}

@keyframes wave {
  0% {
    background-position-x: 0;
  }

  100% {
    background-position-x: 1440px;
  }
}

.mychat_container {
  display: flex;
  width: 90%;
  height: 80vh;
  background: rgba(16, 42, 67, 0.6);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(64, 158, 255, 0.2);
  position: relative;
  min-width: 0; /* 关键：允许子项正确收缩/溢出计算，避免把左侧挤没 */
  /* border: 1px solid red; */
}

.character-container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(10, 25, 47, 0.7), rgba(27, 65, 112, 0.5));
  /* 关键：左侧固定宽度，不随右侧内容变宽而被挤压到不可见 */
  flex: 0 0 300px;
  width: 300px;
  min-width: 300px;
  max-width: 340px;
  flex-shrink: 0;
}

.character {
  position: relative;
  /* width: 220px; */
  /* height: 400px; */
  /* height: 100%; */
  z-index: 2;
  /* border: 1px solid red; */
}

.number_phone {
  width: 260px;
  height: 400px;
}
.de-number-phone-missing{
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  border-radius: 12px;
  border: 1px dashed rgba(255, 193, 7, 0.55);
  background: rgba(255, 193, 7, 0.12);
  color: rgba(255, 255, 255, 0.9);
  font-size: 13px;
  line-height: 1.4;
  text-align: center;
}

.character-head {
  position: absolute;
  width: 80px;
  height: 90px;
  background: #5a3d8a;
  border-radius: 40px 40px 30px 30px;
  top: 20px;
  left: 50px;
  z-index: 3;
  overflow: hidden;
}

.character-hair {
  position: absolute;
  width: 100%;
  height: 60px;
  background: #8a6dbb;
  border-radius: 40px 40px 0 0;
  top: -10px;
}

.character-face {
  position: absolute;
  width: 60px;
  height: 60px;
  background: #f5d6d6;
  border-radius: 50%;
  top: 20px;
  left: 10px;
}

.character-eyes {
  position: absolute;
  width: 40px;
  height: 10px;
  background: #333;
  border-radius: 5px;
  top: 25px;
  left: 10px;
}

.character-body {
  position: absolute;
  width: 100px;
  height: 150px;
  background: #333;
  top: 110px;
  left: 40px;
  z-index: 2;
  border-radius: 20px 20px 0 0;
}

.character-uniform {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #444;
  border-radius: 20px 20px 0 0;
}

.character-collar {
  position: absolute;
  width: 80px;
  height: 20px;
  background: #fff;
  top: 0;
  left: 10px;
  border-radius: 10px 10px 0 0;
}

.character-arms {
  position: absolute;
  width: 120px;
  height: 40px;
  background: #444;
  top: 120px;
  left: 30px;
  z-index: 1;
  border-radius: 20px;
  animation: armsMove 3s ease-in-out infinite;
}

.character-skirt {
  position: absolute;
  width: 100px;
  height: 60px;
  background: #444;
  top: 260px;
  left: 40px;
  z-index: 2;
  border-radius: 0 0 20px 20px;
}

.character-legs {
  position: absolute;
  width: 80px;
  height: 120px;
  background: #222;
  top: 320px;
  left: 50px;
  z-index: 1;
  border-radius: 0 0 20px 20px;
}

@keyframes armsMove {

  0%,
  100% {
    transform: translateX(0);
  }

  50% {
    transform: translateX(5px);
  }
}

.ripple-effect {
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.1);
  transform: scale(0);
  animation: ripple 6s linear infinite;
  z-index: 1;
}

@keyframes ripple {
  0% {
    transform: scale(0);
    opacity: 1;
  }

  100% {
    transform: scale(1);
    opacity: 0;
  }
}

.chat-container {
  /* 右侧允许收缩，配合 min-width:0 避免把左侧顶没 */
  flex: 1 1 auto;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: rgba(22, 51, 85, 0.7);
  border-left: 1px solid rgba(64, 158, 255, 0.2);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: linear-gradient(90deg, rgba(16, 42, 67, 0.8), rgba(27, 65, 112, 0.6));
  border-bottom: 1px solid rgba(64, 158, 255, 0.2);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tech-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(90deg, #64a8ff, #4fd1ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 10px rgba(100, 168, 255, 0.3);
}

.tech-dots {
  display: flex;
  gap: 5px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4fd1ff;
  box-shadow: 0 0 5px #4fd1ff;
}

.pulse {
  animation: pulse 1.5s ease-in-out infinite;
  animation-delay: var(--delay);
}

@keyframes pulse {

  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }

  50% {
    transform: scale(1.2);
    opacity: 0.7;
  }
}

.close-btn {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 71, 87, 0.2);
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.close-btn:hover {
  background: rgba(255, 71, 87, 0.4);
  transform: rotate(90deg);
}

.close-icon {
  width: 12px;
  height: 12px;
  position: relative;
}

.close-icon::before,
.close-icon::after {
  content: "";
  position: absolute;
  width: 100%;
  height: 2px;
  background: #ff4757;
  top: 50%;
  left: 0;
}

.close-icon::before {
  transform: translateY(-50%) rotate(45deg);
}

.close-icon::after {
  transform: translateY(-50%) rotate(-45deg);
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(64, 158, 255, 0.5) transparent;
}

.chat-content::-webkit-scrollbar {
  width: 6px;
}

.chat-content::-webkit-scrollbar-track {
  background: transparent;
}

.chat-content::-webkit-scrollbar-thumb {
  background-color: rgba(64, 158, 255, 0.5);
  border-radius: 3px;
}

.message-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-bubble {
  max-width: 90%;
  padding: 15px;
  border-radius: 15px;
  position: relative;
  animation: fadeIn 0.5s ease-out;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.message-bubble.system {
  align-self: flex-start;
  background: rgba(30, 70, 120, 0.7);
  border: 1px solid rgba(64, 158, 255, 0.3);
  border-top-left-radius: 5px;
}

.message-bubble.user {
  align-self: flex-end;
  background: rgba(30, 70, 120, 0.9);
  /* border: 1px solid rgba(64, 158, 255, 0.3); */
  /* border-left: 5px; */
}

.message-content {
  font-size: 14px;
  line-height: 1.6;
}

.message-content p {
  margin-bottom: 8px;
}

.message-content p:last-child {
  margin-bottom: 0;
}

.message-content ol,
.message-content ul {
  padding-left: 20px;
  margin: 10px 0;
}

.message-content li {
  margin-bottom: 5px;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 10px;
}

.data-item {
  background: rgba(40, 90, 150, 0.3);
  border-radius: 8px;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.data-label {
  font-size: 12px;
  color: rgba(230, 241, 255, 0.8);
}

.data-value {
  font-weight: 500;
}

.data-status {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.data-status.good {
  background: #4caf50;
  box-shadow: 0 0 5px #4caf50;
}

.data-status.normal {
  background: #ff9800;
  box-shadow: 0 0 5px #ff9800;
}

.data-status.bad {
  background: #f44336;
  box-shadow: 0 0 5px #f44336;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 思考中气泡：类似 DeepSeek 的等待状态 */
.thinking-bubble {
  min-height: 44px;
}

.thinking-indicator {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: rgba(230, 241, 255, 0.95);
  font-size: 14px;
}

.thinking-dots {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  margin-left: 2px;
}

.thinking-dots .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(230, 241, 255, 0.9);
  animation: thinkingBounce 1.4s ease-in-out infinite both;
}

.thinking-dots .dot:nth-child(1) {
  animation-delay: 0s;
}

.thinking-dots .dot:nth-child(2) {
  animation-delay: 0.2s;
}

.thinking-dots .dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes thinkingBounce {
  0%,
  80%,
  100% {
    transform: scale(0.6);
    opacity: 0.6;
  }

  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-footer {
  padding: 15px 20px;
  border-top: 1px solid rgba(64, 158, 255, 0.2);
  background: linear-gradient(90deg, rgba(16, 42, 67, 0.8), rgba(27, 65, 112, 0.6));
}

.route-hint-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.route-hint-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
  color: #9ec5ef;
  font-size: 12px;
  line-height: 1;
  white-space: nowrap;
}

.route-hint-help-icon {
  font-size: 14px;
  color: rgba(158, 197, 239, 0.85);
  cursor: help;
  transition: color 0.2s ease;
}

.route-hint-help-icon:hover {
  color: #64a8ff;
}

.route-hint-btn {
  flex: 0 0 auto;
  width: auto;
  min-width: 0;
  padding: 5px 14px;
  border-radius: 6px;
  border: 1px solid rgba(64, 158, 255, 0.35);
  background: rgba(30, 70, 120, 0.35);
  color: #b8d4f0;
  font-size: 12px;
  line-height: 1.4;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.2s ease;
}

.route-hint-btn:hover {
  border-color: rgba(100, 168, 255, 0.6);
  color: #e6f1ff;
}

.route-hint-btn.active {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.55), rgba(100, 168, 255, 0.35));
  border-color: #64a8ff;
  color: #fff;
  box-shadow: 0 0 12px rgba(64, 158, 255, 0.25);
}

.input-container {
  position: relative;
  margin-bottom: 10px;
}

.input-container textarea {
  width: 100%;
  height: 80px;
  background: rgba(30, 70, 120, 0.5);
  border: 1px solid rgba(64, 158, 255, 0.3);
  border-radius: 10px;
  padding: 10px 40px 10px 15px;
  color: #e6f1ff;
  font-family: inherit;
  resize: none;
  outline: none;
  transition: all 0.3s ease;
}

.input-container textarea:focus {
  border-color: #64a8ff;
  box-shadow: 0 0 10px rgba(100, 168, 255, 0.3);
}

.send-btn {
  position: absolute;
  right: 10px;
  bottom: 10px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.3);
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-btn:hover {
  background: rgba(64, 158, 255, 0.5);
  transform: scale(1.1);
}


.send-btn2 {
  position: absolute;
  right: 50px;
  bottom: 10px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.3);
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-btn2-hover {
  background: rgba(64, 158, 255, 0.9);
  transform: scale(1.3);
}

.send-btn3 {
  position: absolute;
  right: 90px;
  bottom: 10px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.3);
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-btn3-hover {
  background: rgba(64, 158, 255, 0.5);
  transform: scale(1.1);
}

.voice-container {
  position: absolute;
  right: 10px;
  bottom: 10px;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: flex-end;

  .send-btn31 {
    border: none;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 21px;
    border-radius: 50%;
    background: rgba(64, 158, 255, 0.3);
    margin-inline-start: 15px;
  }

  .send-btn31:hover {
    background: rgba(64, 158, 255, 0.5);
    transform: scale(1.1);
  }
}

.send-icon {
  width: 16px;
  height: 16px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23e6f1ff"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>');
  background-size: contain;
}


/* 鱼类元素装饰 */
.fish-decoration {
  position: absolute;
  width: 40px;
  height: 20px;
  background: rgba(100, 168, 255, 0.3);
  border-radius: 50%;
  animation: swim 15s linear infinite;
  z-index: 9002;
}

.fish-decoration::before {
  content: "";
  position: absolute;
  width: 10px;
  height: 10px;
  background: rgba(100, 168, 255, 0.5);
  border-radius: 50%;
  top: 5px;
  left: 5px;
}

.fish-decoration::after {
  content: "";
  position: absolute;
  width: 15px;
  height: 10px;
  background: rgba(100, 168, 255, 0.3);
  border-radius: 50% 0 0 50%;
  top: 5px;
  right: -5px;
  transform: rotate(45deg);
}

@keyframes swim {
  0% {
    transform: translateX(-100px) translateY(0) rotateY(0);
  }

  50% {
    transform: translateX(calc(100vw + 100px)) translateY(100px) rotateY(0);
  }

  51% {
    transform: translateX(calc(100vw + 100px)) translateY(100px) rotateY(180deg);
  }

  100% {
    transform: translateX(-100px) translateY(200px) rotateY(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .mychat_container {
    flex-direction: column;
    height: 90vh;
  }

  .character-container {
    display: flex;
    width: 100%;
    flex: 0 0 auto;
    min-width: 0;
    min-height: 200px;
    justify-content: center;
    align-items: center;
  }

  .chat-container {
    flex: 1;
  }
}

/* START: 新增左侧Tab样式 */
.left-tabs {
  position: fixed;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 80px;
  height: auto;
  background: rgba(22, 51, 85, 0.9);
  border-radius: 0 12px 12px 0;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
  z-index: 3100;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 0;

  .tab-item {
    width: 100%;
    padding: 18px 0;
    text-align: center;
    cursor: pointer;
    color: rgba(255, 255, 255, 0.7);
    transition: all 0.4s ease;
    border-left: 3px solid transparent;

    i {
      font-size: 22px;
      display: block;
      margin-bottom: 6px;
    }

    span {
      font-size: 14px;
      font-weight: 300;
    }

    &:hover {
      background: rgba(64, 158, 255, 0.15);
      color: #fff;
    }

    &.active {
      background: rgba(64, 158, 255, 0.25);
      color: #64a8ff;
      border-left: 3px solid #64a8ff;
      position: relative;

      &::after {
        content: "";
        position: absolute;
        right: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 30px;
        background: #64a8ff;
        border-radius: 3px 0 0 3px;
      }
    }
  }
}

/* END: 左侧Tab样式 */
/* 保留其他原有样式 */
/* ====== 新增联系人列表样式 ====== */
.contact-list {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 10px;
}

.contact-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 12px 15px;
  margin-bottom: 8px;
  background: rgba(30, 70, 120, 0.4);
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.contact-item:hover {
  background: rgba(64, 158, 255, 0.3);
  transform: translateY(-2px);
}

.avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 15px;
  border: 2px solid rgba(64, 158, 255, 0.5);
}

.group-avatar {
  border-radius: 8px;
}

.info {
  flex: 1;
}

.name {
  font-weight: 600;
  color: #e6f1ff;
  margin-bottom: 3px;
}

.last-msg-parent {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.last-msg {
  font-size: 13px;
  color: rgba(230, 241, 255, 0.7);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
}

.last-msg-time {
  color: #666;
}

.unread-badge {
  position: absolute;
  top: 10px;
  right: 15px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  box-shadow: 0 2px 4px rgba(255, 71, 87, 0.3);
}

/* 智慧养殖AI助手样式 - 应用到对话按钮 */
/* 确保图标可见 */
.chatBtn .ai-assistant-outer {
  position: relative !important;
  width: 60px !important;
  height: 60px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  animation: rotateClockwise 8s linear infinite !important;
  z-index: 1 !important;
  visibility: visible !important;
  opacity: 1 !important;
}

/* 外层轨道路径 */
.chatBtn .orbital-path {
  position: absolute;
  width: 70px;
  height: 70px;
  border: 1.5px solid rgba(64, 158, 255, 0.6);
  border-radius: 12px;
  animation: rotateCounterClockwise 10s linear infinite;
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.4);
  top: 50%;
  left: 50%;
  margin-top: -35px;
  margin-left: -35px;
  transform: rotate(15deg);
  z-index: 0;
}

/* 轨道上的小圆点 */
.chatBtn .orbital-dot {
  position: absolute;
  width: 6px;
  height: 6px;
  background: rgba(64, 158, 255, 0.9);
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(64, 158, 255, 0.8);
}

.chatBtn .orbital-dot.dot-1 {
  top: -3px;
  left: 50%;
  transform: translateX(-50%);
}

.chatBtn .orbital-dot.dot-2 {
  right: -3px;
  top: 50%;
  transform: translateY(-50%);
}

.chatBtn .orbital-dot.dot-3 {
  bottom: -3px;
  left: 50%;
  transform: translateX(-50%);
}

.chatBtn .orbital-dot.dot-4 {
  left: -3px;
  top: 50%;
  transform: translateY(-50%);
}

/* 右侧额外圆点 */
.chatBtn .extra-dot {
  position: absolute;
  right: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  background: rgba(64, 158, 255, 0.9);
  border-radius: 50%;
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.8);
  z-index: 3;
}

.chatBtn .ai-assistant-inner {
  width: 50px !important;
  height: 50px !important;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.8) 0%, rgba(138, 43, 226, 0.8) 100%) !important;
  border-radius: 8px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: 0 0 20px rgba(64, 158, 255, 0.6), inset 0 0 20px rgba(255, 255, 255, 0.2) !important;
  border: 2px solid rgba(64, 158, 255, 0.5) !important;
  animation: rotateCounterClockwise 6s linear infinite !important;
  position: absolute !important;
  top: 50% !important;
  left: 50% !important;
  margin-top: -25px !important;
  margin-left: -25px !important;
  overflow: visible !important;
  visibility: visible !important;
  opacity: 1 !important;
  z-index: 2 !important;
}

.chatBtn .ai-assistant-inner::before {
  content: '' !important;
  position: absolute !important;
  top: -2px !important;
  left: -2px !important;
  right: -2px !important;
  bottom: -2px !important;
  border-radius: 10px !important;
  background: linear-gradient(90deg,
    rgba(64, 158, 255, 0) 0%,
    rgba(64, 158, 255, 0.6) 25%,
    rgba(64, 158, 255, 1) 50%,
    rgba(64, 158, 255, 0.6) 75%,
    rgba(64, 158, 255, 0) 100%
  ) !important;
  background-size: 200% 100% !important;
  animation: borderGlow 3s linear infinite !important;
  z-index: -1 !important;
}

.chatBtn .ai-assistant-inner i.el-icon-star-on {
  font-size: 28px !important;
  color: #ffffff !important;
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.8) !important;
  filter: drop-shadow(0 0 10px rgba(64, 158, 255, 0.8)) !important;
  position: relative !important;
  z-index: 3 !important;
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
  width: auto !important;
  height: auto !important;
  font-family: element-icons !important;
  font-style: normal !important;
  font-weight: normal !important;
  font-variant: normal !important;
  text-transform: none !important;
  line-height: 1 !important;
  -webkit-font-smoothing: antialiased !important;
  -moz-osx-font-smoothing: grayscale !important;
}

/* 内部装饰图标 */
.chatBtn .ai-assistant-inner .inner-icon {
  position: absolute !important;
  font-size: 14px !important;
  z-index: 2 !important;
  display: block !important;
  visibility: visible !important;
  opacity: 0.8 !important;
}

.chatBtn .ai-assistant-inner .icon-snowflake {
  bottom: 8px !important;
  left: 8px !important;
  color: rgba(64, 158, 255, 0.9) !important;
  text-shadow: 0 0 8px rgba(64, 158, 255, 0.6) !important;
}

.chatBtn .ai-assistant-inner .icon-lightning {
  top: 8px !important;
  right: 8px !important;
  color: rgba(255, 193, 7, 0.9) !important;
  text-shadow: 0 0 8px rgba(255, 193, 7, 0.6) !important;
}

/* 内部小圆点 */
.chatBtn .ai-assistant-inner .inner-dot {
  position: absolute !important;
  width: 4px !important;
  height: 4px !important;
  border-radius: 50% !important;
  z-index: 2 !important;
  display: block !important;
  visibility: visible !important;
  opacity: 0.9 !important;
}

.chatBtn .ai-assistant-inner .dot-top-left {
  top: 8px !important;
  left: 8px !important;
  background: rgba(64, 158, 255, 0.9) !important;
  box-shadow: 0 0 6px rgba(64, 158, 255, 0.6) !important;
}

.chatBtn .ai-assistant-inner .dot-bottom-right {
  bottom: 8px !important;
  right: 8px !important;
  background: rgba(255, 193, 7, 0.9) !important;
  box-shadow: 0 0 6px rgba(255, 193, 7, 0.6) !important;
}

.chatBtn .ai-assistant-text {
  position: absolute;
  right: 100%;
  bottom: 50%;
  margin-right: 15px;
  transform: translateY(50%);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(15, 26, 38, 0.95) 0%, rgba(20, 35, 50, 0.95) 100%);
  border-radius: 8px;
  border: 1px solid rgba(64, 158, 255, 0.5);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(64, 158, 255, 0.3);
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  pointer-events: none;
  z-index: 1004;
}

.ai-assistant-text i {
  font-size: 18px;
  color: rgba(64, 158, 255, 1);
  text-shadow: 0 0 10px rgba(64, 158, 255, 0.8);
  filter: drop-shadow(0 0 8px rgba(64, 158, 255, 0.6));
}

.ai-assistant-text span {
  font-size: 14px;
  color: #ffffff;
  text-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
  font-weight: 500;
}

.chatBtn:hover .ai-assistant-text {
  opacity: 1;
  visibility: visible;
  transform: translateY(50%) translateX(-5px);
}

/* 旋转动画 */
@keyframes rotateClockwise {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes rotateCounterClockwise {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(-360deg);
  }
}

@keyframes borderGlow {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 对话按钮样式调整 - 覆盖外部CSS */
.chatContainer .chatBtn,
.chatBtn {
  position: absolute !important;
  bottom: 0 !important;
  right: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  background: transparent !important;
  box-shadow: none !important;
  overflow: visible !important;
  width: 60px !important;
  height: 60px !important;
  min-width: 60px !important;
  min-height: 60px !important;
  border-radius: 0 !important;
  line-height: normal !important;
  visibility: visible !important;
  opacity: 1 !important;
  z-index: 1000 !important;
  cursor: pointer !important;
  /* 确保按钮可见 */
  pointer-events: auto !important;
  transform: none !important;
}

.chatBtn > i {
  display: none !important;
}

/* 修复 chatContainer 高度问题 */
.chatContainer {
  // height: auto !important;
  // min-height: 60px !important;
  max-height: 0px;
  overflow: visible !important;
}
</style>

<style lang="scss">
.route-hint-tooltip {
  max-width: 320px;
  line-height: 1.5;
  font-size: 12px;
}

.route-hint-tooltip .route-hint-tip-content p {
  margin: 0 0 6px;
}

.route-hint-tooltip .route-hint-tip-content p:last-child {
  margin-bottom: 0;
  opacity: 0.9;
}
</style>
