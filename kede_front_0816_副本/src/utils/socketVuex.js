import Vue from "vue";
import Vuex from "vuex";
import store from "@/store";
import {
  UserInfoKey,
  ChatLogKey,
  AppChatThink,
  AppChatMsg,
  MeetingJoinMeet,
  MeetingChatMsg,
  VUE_APP_WEBSOCKET_HEARTCHECK,
} from "@/utils/constants";
import MsgHandler from "./chat/socketMsgHandler";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    userId: 0, // 当前链接对应用户
    ws: null, //建立的连接
    url: process.env.VUE_APP_WEBSOCKET_API + "/websocket/message", // /websocket/message
    lockReconnect: false, //是否真正建立连接
    timeout: 15000, //15秒一次心跳
    timeoutObj: null, //心跳心跳倒计时
    serverTimeoutObj: null, //心跳倒计时
    timeoutnum: null, //断开 重连倒计时
    msg: null, //接收到的信息
    agentMsg: null, //接收到的信息
    meetMsg: null, // 接收到的会议模块信息
    productMsg: null, // 商品模块消息
    notifyMsg: null, // 系统提示消息
    chatUserStore: null, // 通讯录用户列表(包含聊天)消息
    chatGroupStore: null, // 群聊列表(包含聊天)消息
    userMsgItem: null, // 通讯录用户当前聊天信息
    groupMsgItem: null, // 群聊当前聊天信息
    my_that: null, // 当前 that, uni
    my_audio: null, // 提示音
    socketState: -1, // socket链接状态,-1:未链接;1:已链接;
  },
  getters: {
    // 获取接收的信息
    socketMsgs: (state) => {
      return state.msg;
    },
    getWs: (state) => {
      return state.ws;
    },
  },
  mutations: {
    //初始化ws 用户登录后调用
    webSocketInit(state) {
      let that = this;
      let userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
      //this 创建一个state.ws对象【发送、接收、关闭socket都由这个对象操作】
      const userId = store.getters.userId;
      state.userId = userId;
      if (!userId) {
        console.log(
          `初始化websocket发现userId为空:${userId},利用浏览器存储userInfo:${JSON.stringify(
            userInfo
          )}`
        );
        state.userId = userInfo.userId;
      }
      if (!state.userId) {
        console.log(
          `初始化websocket发现userId为空:${userId},浏览器存储userInfo也为空，暂停创建链接:${JSON.stringify(
            userInfo
          )}`
        );
        return;
      }
      const wss_url = `${state.url}?deviceType=pc&userId=${state.userId}&accessToken=${userInfo.accessToken}`;
      console.log(`初始化websocket wss_url:${wss_url}`);
      state.ws = new WebSocket(wss_url);
      state.ws.onopen = function (res) {
        // console.log("Connection success...");
        /**
         * 启动心跳检测
         */
        that.commit("start");
      };
      state.ws.onmessage = function (res) {
        // 增强日志：记录完整的消息对象
        // console.log('==========================================');
        // console.log('【WebSocket消息接收】');
        // console.log('消息时间:', new Date().toLocaleString('zh-CN'));
        // console.log('【WebSocket消息接收】:', JSON.stringify(res.data));
        // return
        // console.log('消息内容类型:', typeof res.data);

        // 根据消息类型进行不同的日志记录
        if (typeof res.data === "string") {
          // 字符串类型消息的详细日志
          // console.log('消息内容长度:', res.data.length);
          // 打印前500个字符作为预览，避免日志过长
          // const preview = res.data.length > 500 ? res.data.substring(0, 500) + '...' : res.data;
          // console.log('消息内容预览:', preview);

          // 检查是否包含关键字
          const keyPatterns = [
            "device-alarm",
            "alarm",
            "warning",
            "error",
            "heartCheck",
            "websocket：",
          ];
          keyPatterns.forEach((pattern) => {
            if (res.data.indexOf(pattern) !== -1) {
              // console.log(`消息包含关键字: ${pattern}`);
            }
          });
        } else if (typeof res.data === "object" && res.data !== null) {
          // 对象类型消息的详细日志
          // console.log('对象属性:', Object.keys(res.data).join(', '));
          // console.log('对象内容概览:', JSON.stringify(res.data, null, 2).length > 500 ?
          //            JSON.stringify(res.data, null, 2).substring(0, 500) + '...' :
          //            JSON.stringify(res.data, null, 2));
        }
        // console.log('==========================================');

        // 处理心跳消息
        if (res.data.indexOf(VUE_APP_WEBSOCKET_HEARTCHECK) != -1) {
          // console.log('【心跳处理】收到心跳消息，重置心跳计时器');
          that.commit("reset");
        } else {
          // 设置状态确保所有消息都能被监听到
          // console.log('【状态更新】设置state.msg和state.productMsg');
          state.msg = res.data;
          state.productMsg = {
            data: res.data,
            timestamp: new Date().getTime(),
            originalEvent: res,
          };

          // 进行特殊消息处理
          // console.log('【消息处理】调用initMsg进行进一步处理');
          that.commit("initMsg", res.data);
          // 3. 如果是 通用消息
          MsgHandler.msgHandle(res.data, {
            that,
            state,
          });
        }
      };
      state.ws.onclose = function (res) {
        console.log("Connection closed...");
        //重连
        that.commit("reconnect");
      };
      state.ws.onerror = function (res) {
        console.log("Connection error...", res);
        //重连
        that.commit("reconnect");
      };
    },
    reconnect(state) {
      //重新连接
      let that = this;
      if (state.lockReconnect) {
        return;
      }
      state.lockReconnect = true;
      //没连接上会一直重连,30秒重试请求重连，设置延迟避免请求过多
      state.timeoutnum && clearTimeout(state.timeoutnum);
      state.timeoutnum = setTimeout(() => {
        //新连接
        that.commit("webSocketInit");
        state.lockReconnect = false;
      }, 5000);
    },
    reset(state) {
      //重置心跳
      let that = this;
      //清除时间
      clearTimeout(state.timeoutObj);
      clearTimeout(state.serverTimeoutObj);
      //重启心跳
      that.commit("start");
    },
    start(state) {
      //开启心跳
      var self = this;
      state.timeoutObj && clearTimeout(state.timeoutObj);
      state.serverTimeoutObj && clearTimeout(state.serverTimeoutObj);
      state.timeoutObj = setTimeout(() => {
        //这里发送一个心跳，后端收到后，返回一个心跳消息，
        if (state.ws.readyState === 1) {
          //如果连接正常
          state.ws.send("heartCheck");
        } else {
          //否则重连
          self.commit("reconnect");
        }
        state.serverTimeoutObj = setTimeout(function () {
          //超时关闭
          state.ws.close();
        }, state.timeout);
      }, state.timeout);
    },
    sendMsg(state, msg, sourcePage = "") {
      // console.log(`${sourcePage}开始发送websocket消息：${msg}`)
      state.ws.send(msg);
      // console.log(`${sourcePage}结束发送websocket消息：${msg}`)
    },
    initMsg(state, msg) {
      // 增强的消息接收日志，包含时间戳和完整消息
      const timestamp = new Date().toLocaleString("zh-CN");
      // console.log('==========================================');
      // console.log(`【initMsg】接收到WebSocket消息 - ${timestamp}`);
      // console.log('消息类型:', typeof msg);
      // console.log('原始消息内容:', msg);
      // console.log('==========================================');

      // 处理对象类型的消息（后端直接发送的对象）
      if (typeof msg === "object" && msg !== null) {
        // console.log('【对象消息】收到对象类型消息，执行详细检查');

        // 检查是否是device-alarm类型的告警消息
        if (msg.type === "device-alarm" || msg.alarmType === "device") {
          // console.log('【告警识别】确认收到device-alarm类型对象消息');
          // 为了确保兼容性，同时更新state.msg和state.productMsg
          state.msg = msg;
          state.productMsg = { data: msg };
          return;
        }

        // 检查是否是Java服务端发送的特殊格式消息
        if (
          msg.messageType === "device-alarm" ||
          msg.msgType === "device-alarm"
        ) {
          // console.log('【告警识别】确认收到Java服务端特殊格式device-alarm消息');
          state.msg = msg;
          state.productMsg = { data: msg };
          return;
        }

        // 默认处理：更新状态
        state.msg = msg;
        return;
      }

      // 处理字符串类型的消息
      if (typeof msg === "string") {
        // 心跳消息特殊处理
        if (msg === "heartCheck") {
          // console.log('【心跳消息】收到heartCheck心跳消息');
          return;
        }

        // 连接状态消息处理
        if (msg.startsWith("websocket：")) {
          // console.log('【连接状态】收到WebSocket连接状态消息');
          return;
        }

        // 重点：增强对Java服务端device-alarm类型消息的识别
        // 检查是否包含device-alarm类型标识
        const isDeviceAlarm =
          msg.indexOf('"type":"device-alarm"') !== -1 ||
          msg.indexOf('"alarmType":"device"') !== -1 ||
          msg.indexOf('"messageType":"device-alarm"') !== -1 ||
          msg.indexOf('"msgType":"device-alarm"') !== -1 ||
          // 特殊处理：检查整个消息是否以device-alarm开头的格式
          msg.startsWith("device-alarm");

        if (isDeviceAlarm) {
          // console.log('【告警识别】字符串消息中包含device-alarm类型标识');

          try {
            // 尝试解析JSON格式
            let parsedMsg;

            // 特殊处理：如果消息是以'device-alarm'开头的格式，可能需要特殊解析
            if (msg.startsWith("device-alarm")) {
              console.log("【特殊格式】检测到以device-alarm开头的消息格式");
              // 提取实际的JSON部分（假设格式为 "device-alarm{...}"）
              const jsonPart = msg.substring(msg.indexOf("{"));
              parsedMsg = JSON.parse(jsonPart);
              // 显式设置类型标识
              parsedMsg.type = "device-alarm";
            } else {
              // 常规JSON解析
              parsedMsg = JSON.parse(msg);
            }

            // console.log('【告警处理】成功解析为device-alarm对象:', parsedMsg);
            // 更新状态，确保两种状态都被更新
            state.msg = parsedMsg;
            state.productMsg = { data: parsedMsg };
          } catch (parseError) {
            console.error(
              "【解析错误】无法解析device-alarm消息为JSON:",
              parseError.message
            );
            // 即使解析失败，也尝试通过state.msg传递原始消息
            state.msg = msg;
          }
          return;
        }

        // 尝试解析其他JSON消息
        try {
          const parsedMsg = JSON.parse(msg);
          // console.log('【普通消息】成功解析为JSON对象:', parsedMsg);
          state.msg = parsedMsg;
        } catch (e) {
          // 非JSON格式消息
          // console.log('【普通消息】收到非JSON字符串消息');
          state.msg = msg;
        }
      }

      // 处理聊天相关消息
      if (msg.indexOf(AppChatThink) != -1) {
        this.commit("handle_think", JSON.parse(msg));
        return;
      }
      if (msg.indexOf(AppChatMsg) != -1) {
        this.commit("handle_msg", JSON.parse(msg));
        return;
      }
      if (msg.indexOf(MeetingChatMsg) != -1) {
        this.commit("handle_meet_chat_msg", JSON.parse(msg));
        return;
      }

      // 对于其他可能包含聊天内容的消息，尝试解析和存储
      try {
        // 尝试作为JSON解析
        let parsedMsg = JSON.parse(msg);
        // 如果解析成功且包含聊天相关字段，存储到本地
        if (
          parsedMsg &&
          (parsedMsg.content || parsedMsg.msg || parsedMsg.think)
        ) {
          let msgs = localStorage.getItem(ChatLogKey);
          if (msgs) {
            let messageList = JSON.parse(msgs);
            this.commit("pushMsg", {
              messageList: messageList,
              data: parsedMsg,
            });
          }
        }
      } catch (e) {
        // 非JSON格式的消息不处理，但不阻止其传递
        console.log("非JSON格式消息，跳过本地存储:", e.message);
      }
    },
    pushMsg(state, params) {
      let messageList = params.messageList;
      let data = params.data;
      // console.log(`pushMsg 收到的消息是：`, data);
      let i1 = {
        role: "ai",
        msg: data.content,
        think: data.think,
        key: data.key,
        chatTime: data.chatTime,
        version: `${data.version}`,
        img: "",
      };
      messageList.push(i1);
      // console.log(`从缓存拿到消息是：`, this.messageList);
      localStorage.setItem(ChatLogKey, JSON.stringify(messageList));
    },
    // agent 流式思考 处理
    handle_think(state, msgObj) {
      // console.log(`==============>msgObj:${JSON.stringify(msgObj)}`);
      let msgs = localStorage.getItem(ChatLogKey);
      // console.log(`=====>msgs:`, msgs);
      if (msgs) {
        let messageList = JSON.parse(msgs);
        let temp = null;
        let temp_index = 0;
        messageList.forEach((item, index) => {
          if (item.version == msgObj.version) {
            temp = item;
            temp_index = index;
            return;
          }
        });
        if (temp) {
          let currMsg = temp.msg;
          let currThink = temp.think;
          if (!msgObj.content) {
            // 还在打印思考过程
            currThink += msgObj.think;
          } else {
            // 已经思考结束
            currThink = msgObj.think;
            currMsg += msgObj.content;
          }
          let i1 = {
            role: "ai",
            msg: currMsg,
            think: currThink,
            key: msgObj.key,
            chatTime: msgObj.chatTime,
            version: `${msgObj.version}`,
            img: "",
          };
          messageList.splice(temp_index, 1, i1);
          // console.log(`socketVuex.js======msgObj:${JSON.stringify(msgObj)}=====>${JSON.stringify(messageList)}`);

          localStorage.setItem(ChatLogKey, JSON.stringify(messageList));
        } else {
          // console.log(`messageList===========>${JSON.stringify(messageList)}`);
          this.commit("pushMsg", { messageList: messageList, data: msgObj });
        }
        state.agentMsg = JSON.stringify(msgObj);
      }
    },
    // agent 流式结果 处理
    handle_msg(state, msgObj) {
      // console.log(`==============>msgObj:${JSON.stringify(msgObj)}`);
      let msgs = localStorage.getItem(ChatLogKey);
      // console.log(`=====>msgs:`, msgs);
      if (msgs) {
        let messageList = JSON.parse(msgs);
        let temp = null;
        let temp_index = 0;
        messageList.forEach((item, index) => {
          if (item.version == msgObj.version) {
            temp = item;
            temp_index = index;
            return;
          }
        });
        if (temp) {
          let currMsg = msgObj.content;
          let currThink = msgObj.think;
          let i1 = {
            role: "ai",
            msg: currMsg,
            think: currThink,
            key: msgObj.key,
            chatTime: msgObj.chatTime,
            version: `${msgObj.version}`,
            img: "",
          };
          messageList.splice(temp_index, 1, i1);
          // console.log(`socketVuex.js======msgObj:${JSON.stringify(msgObj)}=====>${JSON.stringify(messageList)}`);

          localStorage.setItem(ChatLogKey, JSON.stringify(messageList));
        } else {
          // console.log(`messageList===========>${JSON.stringify(messageList)}`);
          this.commit("pushMsg", { messageList: messageList, data: msgObj });
        }
        state.agentMsg = JSON.stringify(msgObj);
      }
    },
    // 会议聊天结果 处理
    handle_meet_chat_msg(state, msgObj) {
      state.meetMsg = JSON.stringify(msgObj);
      // console.log(`会议聊天结果==============>msgObj:${JSON.stringify(msgObj)}`);
    },
    playMyAudio(state) {
      if (!state.my_audio) {
        state.my_audio = new Audio("../assets/media/notice.mp3");
      }
      state.my_audio.play();
    },
  },
  actions: {
    webSocketInit({ commit }, url) {
      commit("webSocketInit", url);
    },
    webSocketSend({ commit }, p) {
      commit("webSocketSend", p);
    },
    sendMsg({ commit }, msg, sourcePage = "") {
      commit("sendMsg", msg, sourcePage);
    },
    playMyAudio({ commit }) {
      commit("playMyAudio");
    },
  },
});
