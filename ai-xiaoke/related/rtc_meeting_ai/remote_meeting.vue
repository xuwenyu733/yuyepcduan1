<template>
  <div class="expert-meeting expert-meeting--embed">
    <iframe
      v-if="meetingUrlSafe"
      ref="meetFrame"
      class="meet-iframe"
      :class="{ 'meet-iframe--with-bar': showMeetBar }"
      :src="meetingUrlSafe"
      frameborder="0"
      allow="microphone;camera;display-capture;autoplay;fullscreen"
      allowfullscreen
      @load="onIframeLoad"
    ></iframe>
    <div v-if="!meetingUrlSafe" class="meet-placeholder">
      <div class="meet-placeholder__title">会议地址未配置</div>
      <div class="meet-placeholder__sub">请先执行 bash scripts/build-rtc-static.sh，或设置 VUE_APP_MEETING_PC=/rtc_kede</div>
    </div>
    <!-- 进入时短暂遮罩，大厅就绪后揭开，露出「请选择会议方式」 -->
    <div v-if="meetingUrlSafe && splashVisible" class="meet-placeholder meet-placeholder--soft">
      <div class="meet-placeholder__title">科德专家会议</div>
      <div class="meet-placeholder__sub">正在进入专家会议…</div>
    </div>
    <div v-if="showMeetBar" class="parent-meet-bar" role="toolbar">
      <button type="button" class="pmb-btn" @click="sendRtcCmd('toggle_mic')">麦克风</button>
      <button type="button" class="pmb-btn" @click="sendRtcCmd('toggle_camera')">摄像头</button>
      <button type="button" class="pmb-btn" @click="sendRtcCmd('share_screen')">屏幕共享</button>
      <button type="button" class="pmb-btn" @click="sendRtcCmd('record')">录制</button>
      <button type="button" class="pmb-btn" @click="sendRtcCmd('members')">成员</button>
      <button type="button" class="pmb-btn" @click="sendRtcCmd('ai_subtitle')">AI字幕</button>
      <button type="button" class="pmb-btn" @click="sendRtcCmd('asr_records')">字幕记录</button>
      <button type="button" class="pmb-btn pmb-btn--accent" @click="sendRtcCmd('expert_tools')">专家工具</button>
      <button type="button" class="pmb-btn pmb-btn--warn" @click="sendRtcCmd('leave')">离开</button>
      <button type="button" class="pmb-btn pmb-btn--danger" @click="sendRtcCmd('end')">结束</button>
    </div>

    <!-- 会议结束后在父页填写纪要（独立会议窗关闭后仍可写） -->
    <el-dialog
      title="会议总结"
      :visible.sync="summaryOpen"
      width="640px"
      append-to-body
      :close-on-click-modal="false"
    >
      <p class="summary-meta">会议号：{{ meetingNum || '—' }}</p>
      <el-form label-width="90px">
        <el-form-item label="会议纪要">
          <el-input
            v-model="form.meetingPointAndTodo"
            type="textarea"
            :rows="8"
            placeholder="可粘贴语音转写或补充会议纪要、待办"
          />
        </el-form-item>
        <el-form-item label="个人总结">
          <el-input
            v-model="form.toDoFeedback"
            type="textarea"
            :rows="4"
            placeholder="个人总结 / 待办反馈"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="summaryOpen = false">稍后填写</el-button>
        <el-button type="primary" :loading="summarySaving" @click="submitPostMeetingSummary">提交总结</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkMeetingUser, updateZkMeetingUser, deleteZkMeetingUser, getZkMeetingUser, getZkMeetingUserPage, exportZkMeetingUserExcel } from "@/api/fish/zkMeetingUser";
import { createZkMeetingRecord, updateZkMeetingRecord, deleteZkMeetingRecord, getZkMeetingRecord, getZkMeetingRecordPage, exportZkMeetingRecordExcel } from "@/api/fish/zkMeetingRecord";
import { createZkMeeting, summaryPoint, receiveMsg, updateZkMeeting, deleteZkMeeting, getZkMeeting, getZkMeetingPage, exportZkMeetingExcel } from "@/api/fish/zkMeeting";
import Editor from '@/components/Editor';
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkMeetingExpert",
  components: {
    FileBusUpload,
    Editor,
  },
  data() {
    return {
      meeting_url: '/rtc_kede/index.html',
      iframeReady: false,
      splashVisible: true,
      _splashTimer: null,
      meetWin: null,
      inRoomUi: false,
      summaryOpen: false,
      summarySaving: false,
      loading: true,
      getMeetSummary_txt: '获取纪要及待办',
      userInfo: {},
      exportLoading: false,
      showSearch: true,
      total: 0,
      list: [],
      userList1: [],
      title: "",
      open: false,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        meetingName: null,
        meetingContent: null,
        meetingNum: null,
        startTime: [],
        endTime: [],
        status: null,
        createTime: [],
        userId: null,
        userName: null,
        deptId: null,
        fid: null,
      },
      form: {
        meetingRecordContent: '',
        meetingPointAndTodo: '',
        toDoFeedback: '',
      },
      rules: {
        startTime: [{ required: true, message: "开始时间不能为空", trigger: "blur" }],
        endTime: [{ required: true, message: "结束时间不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      },
      meet_iframe: null,
      currUrl: '',
      meetingNum: '',
      meetingId: null,
      meetingPoint: null,
      meetToDo: null,
      currMeetUser: null,
      lastMeetUser: null,
    };
  },
  created() {
    this.refreshUserInfo();
    this.buildMeetingUrl();
    console.info('[专家会议] iframe →', this.meeting_url, 'uid=', this.resolveUserId(), 'nick=', this.resolveDisplayName());
  },
  mounted() {
    this.refreshUserInfo();
    this.meet_iframe = this.$refs.meetFrame || document.querySelector('.meet-iframe');
    this.receiveMsg();
    // 关页/杀进程兜底：仅在真正进过房且已有会议主键时触发，避免大厅误弹「会议id不能为空」
    this._onPageHideAutosync = () => {
      if (this.inRoomUi && this.meetingNum && this.meetingId) {
        this.autosyncMeetingRecord(true);
      }
    };
    window.addEventListener('pagehide', this._onPageHideAutosync);
    this.$nextTick(() => {
      this.refreshUserInfo();
      this.buildMeetingUrl();
      this.splashVisible = true;
      this.pushInitToRtc();
      this._splashTimer = setTimeout(() => {
        if (this.splashVisible) this.splashVisible = false;
        if (!this.iframeReady) this.iframeReady = true;
      }, 8000);
    });
  },
  beforeDestroy() {
    if (this._splashTimer) clearTimeout(this._splashTimer);
    if (this._rtcMsgHandler) {
      window.removeEventListener('message', this._rtcMsgHandler);
      this._rtcMsgHandler = null;
    }
    if (this._onPageHideAutosync) {
      window.removeEventListener('pagehide', this._onPageHideAutosync);
      this._onPageHideAutosync = null;
    }
  },
  computed: {
    // 兜底：HMR 异常时 data 偶发丢失，避免整页「会议地址未配置」
    meetingUrlSafe() {
      const u = this.meeting_url;
      if (u) return u;
      return '/rtc_kede/index.html';
    },
    showMeetBar() {
      return false;
    },
    socketMsg() {
      try {
        return this.$socketPublic && this.$socketPublic.state ? this.$socketPublic.state.meetMsg : null;
      } catch (e) {
        return null;
      }
    },
    isProfessional() {
      const roles = (this.userInfo && (this.userInfo.roles || this.userInfo.roleNames || [])) || [];
      const arr = Array.isArray(roles) ? roles : String(roles).split(',');
      return arr.some((r) => /expert|专业|专家|ke_admin|kede_group|专员/i.test(String(r)));
    },
  },
  watch: {
    socketMsg(newV) {
      if (!newV) return;
      let temp = JSON.parse(newV);
      this.form.meetingRecordContent += temp.content + "\n";
      this.postToRtc({
        type: 'parent_mirror_chat',
        content: temp.content,
        meetingId: this.meetingId,
        toDoFeedback: this.form.toDoFeedback,
      });
    },
  },
  methods: {
    refreshUserInfo() {
      let fromLs = {};
      try {
        fromLs = JSON.parse(localStorage.getItem(UserInfoKey) || '{}') || {};
      } catch (e) {
        fromLs = {};
      }
      const storeUser = (this.$store && this.$store.state && this.$store.state.user) || {};
      this.userInfo = Object.assign({}, fromLs, {
        nickname: fromLs.nickname || storeUser.nickname || fromLs.userName || storeUser.name || '',
        userName: fromLs.userName || storeUser.name || fromLs.nickname || storeUser.nickname || '',
        userId: fromLs.userId || fromLs.id || storeUser.id || '',
        id: fromLs.id || fromLs.userId || storeUser.id || '',
        deptId: fromLs.deptId != null ? fromLs.deptId : storeUser.deptId,
        roles: fromLs.roles || fromLs.roleNames || storeUser.roles || [],
      });
    },
    resolveUserId() {
      const info = this.userInfo || {};
      const storeId = this.$store && this.$store.state && this.$store.state.user
        ? this.$store.state.user.id
        : '';
      return (
        info.userId ||
        info.id ||
        storeId ||
        (info.user && (info.user.userId || info.user.id)) ||
        ''
      );
    },
    /** 可读显示名：昵称优先；禁止把纯数字 userId 当 username 传给会议端 */
    resolveDisplayName() {
      const info = this.userInfo || {};
      const storeUser = (this.$store && this.$store.state && this.$store.state.user) || {};
      const cands = [
        info.nickname,
        info.userName,
        storeUser.nickname,
        storeUser.name,
        info.user && (info.user.nickname || info.user.userName),
      ];
      for (let i = 0; i < cands.length; i++) {
        const s = String(cands[i] == null ? '' : cands[i]).trim();
        if (!s) continue;
        if (/^\d+$/.test(s)) continue;
        return s;
      }
      return '';
    },
    buildMeetingUrl() {
      // 始终使用同域科德静态包，避免环境变量指到线上旧站
      let base = '/rtc_kede';
      try {
        const raw = process.env.VUE_APP_MEETING_PC;
        const cleaned = String(raw == null ? '' : raw).trim().replace(/^['"]|['"]$/g, '').replace(/\/$/, '');
        if (cleaned && cleaned.charAt(0) === '/' && !/^https?:/i.test(cleaned)) {
          base = cleaned;
        }
      } catch (e) { /* ignore */ }
      const uid = this.resolveUserId();
      const nick = this.resolveDisplayName();
      const bust = `v=${Date.now()}&rtc=20260916lobby`;
      const entry = /index\.html$/i.test(base) ? base : `${base}/index.html`;
      if (uid) {
        this.meeting_url = `${entry}?${bust}#/login?userid=${encodeURIComponent(uid)}&username=${encodeURIComponent(nick)}&redirect=${encodeURIComponent('/home')}`;
      } else {
        this.meeting_url = `${entry}?${bust}#/login?redirect=${encodeURIComponent('/home')}`;
      }
      return this.meeting_url;
    },
    reloadRtcHome() {
      this.inRoomUi = false;
      this.iframeReady = false;
      this.splashVisible = true;
      this.buildMeetingUrl();
      if (!this.meetWin || this.meetWin.closed) this.meetWin = null;
    },
    /** 离开/结束后：同步录制 + 打开父页会议总结弹窗 */
    afterLeaveMeeting(payload) {
      const leavePayload = payload || {};
      this.autosyncMeetingRecord();
      // 父页已有总结弹窗：关闭独立会议窗，避免同账号二次登录互踢弹出「被踢下线」
      try {
        if (this.meetWin && !this.meetWin.closed) {
          this.meetWin.close();
        }
      } catch (e) { /* ignore */ }
      this.meetWin = null;
      this.reloadRtcHome();
      if (this.meetingNum) {
        this.hydrateSummaryForm(leavePayload);
        this.summaryOpen = true;
        // AI 字幕已写入纪要时，不再用空的小科结果覆盖
        if (this.meetingId && !String(this.form.meetingPointAndTodo || '').trim()) {
          try { this.getMeetSummary(); } catch (e) { /* ignore */ }
        }
      }
    },
    hydrateSummaryForm(payload) {
      try {
        const fromMsg = String(
          (payload && (payload.summaryText || payload.asrText || payload.meetingPoint)) || ''
        ).trim();
        let text = fromMsg;
        if (!text) {
          const raw = localStorage.getItem('kede-selected-asr-text')
            || sessionStorage.getItem('kede-selected-asr-text')
            || '';
          if (raw) {
            try {
              const parsed = JSON.parse(raw);
              if (parsed && typeof parsed === 'object') {
                const num = String(parsed.meetingNum || '');
                if (!this.meetingNum || !num || num === String(this.meetingNum)) {
                  text = String(parsed.text || '').trim();
                }
              } else {
                text = String(raw).trim();
              }
            } catch (e1) {
              text = String(raw).trim();
            }
          }
        }
        if (!text) {
          try {
            const meta = JSON.parse(localStorage.getItem('kede-meeting-meta') || '{}');
            text = [meta.meetingPoint, meta.meetToDo].filter(Boolean).join('\n\n').trim();
          } catch (e2) { /* ignore */ }
        }
        if (text) {
          this.form.meetingPointAndTodo = text;
        }
      } catch (e) { /* ignore */ }
    },
    submitPostMeetingSummary() {
      const that = this;
      const content = String(that.form.meetingPointAndTodo || '').trim();
      const feedback = String(that.form.toDoFeedback || '').trim();
      if (!content && !feedback) {
        that.$modal && that.$modal.msgWarning('请填写纪要或个人总结后再提交');
        return;
      }
      if (!that.meetingNum) {
        that.$modal && that.$modal.msgWarning('会议号缺失，无法提交');
        return;
      }
      that.summarySaving = true;
      const ensureId = that.meetingId
        ? Promise.resolve(that.meetingId)
        : receiveMsg({
            receiveType: 'son_send_to_parent_meeting_jion_meet',
            meetingNum: that.meetingNum,
            userId: that.userInfo.userId,
            deptId: that.userInfo.deptId,
            tenantId: that.userInfo.tenantId,
            userName: that.userInfo.nickname,
            version: Date.now(),
          }).then((r) => {
            const id = r && r.data && r.data.id;
            if (id != null) that.meetingId = id;
            return id;
          });
      ensureId.then((meetingId) => {
        if (meetingId == null) throw new Error('会议id不能为空');
        return createZkMeetingRecord({
          meetingId,
          meetingName: that.meetingNum,
          meetingNum: that.meetingNum,
          meetingPoint: content,
          meetToDo: feedback,
          meetingRecordContent: content,
          meetingRecordName: '会议总结-' + that.meetingNum,
          userId: that.userInfo.userId,
          deptId: that.userInfo.deptId,
          tenantId: that.userInfo.tenantId,
          userName: that.userInfo.nickname,
          status: '0',
        });
      }).then(() => {
        that.$modal && that.$modal.msgSuccess('会议总结已提交');
        that.summaryOpen = false;
      }).catch((e) => {
        that.$modal && that.$modal.msgError((e && e.message) || '提交失败');
      }).finally(() => {
        that.summarySaving = false;
      });
    },
    /**
     * 创建/加入会议：在独立窗口打开房间页；父页专家会议 iframe 继续留在大厅
     */
    openRoomWindow(payload) {
      const roomId = String((payload && payload.roomId) || '').trim();
      if (!roomId) return;
      const roomType = payload.roomType != null ? String(payload.roomType) : '1';
      const isCreate = !!(payload && payload.isCreate);
      this.refreshUserInfo();
      const uid = this.resolveUserId();
      const nick = this.resolveDisplayName();
      const info = this.userInfo || {};
      const storeUser = (this.$store && this.$store.state && this.$store.state.user) || {};
      this.seedKedeUser({
        userId: uid,
        nickname: nick,
        userName: nick,
        deptId: info.deptId || storeUser.deptId,
        tenantId: info.tenantId,
        accessToken: info.accessToken || (typeof localStorage !== 'undefined' ? localStorage.getItem('ACCESS_TOKEN') : ''),
        roles: info.roles || info.roleNames || storeUser.roles || [],
        isProfessional: this.isProfessional,
      });
      try {
        localStorage.setItem('kede-pending-room', JSON.stringify({
          roomId, roomType, isCreate, at: Date.now(),
        }));
      } catch (e0) { /* ignore */ }
      // 直达房间页：新窗口不再经过大厅「创建/加入」
      const redirect = `/room?roomId=${encodeURIComponent(roomId)}&roomType=${encodeURIComponent(roomType)}&isCreate=${isCreate ? '1' : '0'}`;
      const bust = `v=${Date.now()}&rtc=20260918trtc1`;
      let href = `/rtc_kede/index.html?${bust}#/login?redirect=${encodeURIComponent(redirect)}`;
      if (uid) {
        href = `/rtc_kede/index.html?${bust}#/login?userid=${encodeURIComponent(uid)}&username=${encodeURIComponent(nick)}&redirect=${encodeURIComponent(redirect)}`;
      }
      try {
        href = new URL(href, window.location.origin).href;
      } catch (e) { /* ignore */ }
      // 大厅已先 logout；此处立即开窗直达房间（勿 setTimeout，易被拦截弹窗）
      let win = null;
      try {
        win = window.open(href, 'kede_expert_meeting_room');
      } catch (e) {
        win = null;
      }
      if (!win) {
        this.$message && this.$message.warning('浏览器拦截了会议窗口，请允许弹窗后重试');
        return;
      }
      this.meetWin = win;
      this.inRoomUi = true;
      this.meetingNum = roomId;
      try { win.focus(); } catch (e2) { /* ignore */ }
      const push = () => {
        try {
          if (this.meetWin && !this.meetWin.closed) {
            this.meetWin.postMessage({
              type: 'parent_init_kede',
              userInfo: {
                userId: uid,
                nickname: nick,
                userName: nick,
                deptId: info.deptId || storeUser.deptId,
                tenantId: info.tenantId,
                accessToken: info.accessToken || (typeof localStorage !== 'undefined' ? localStorage.getItem('ACCESS_TOKEN') : ''),
                roles: info.roles || info.roleNames || storeUser.roles || [],
                isProfessional: this.isProfessional,
              },
            }, '*');
          }
        } catch (e3) { /* ignore */ }
      };
      push();
      setTimeout(push, 500);
      setTimeout(push, 1500);
      setTimeout(push, 3000);
    },
    /**
     * 会议结束后自动同步录制：
     * - 先确保 zk_meeting 有主键 meetingId（后端 create 记录强制校验）
     * - 若尚无 zk_meeting_record 行则再创建（含 meetDay），再分页触发 enhancerList 写回 URL
     * - 腾讯云转码有延迟：离开后按 20s/60s/120s/180s 重试
     */
    autosyncMeetingRecord(force) {
      const that = this;
      const meetingNum = that.meetingNum || '';
      if (!meetingNum) return;
      const now = Date.now();
      if (!force && now - (that._autosyncRecordAt || 0) < 30000) return;
      that._autosyncRecordAt = now;

      const meetDay = (() => {
        const d = new Date();
        const p = (n) => (n < 10 ? '0' + n : '' + n);
        return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate());
      })();

      const storeUser = (that.$store && that.$store.state && that.$store.state.user) || {};
      const info = that.userInfo || {};
      const baseUser = {
        userId: info.userId || storeUser.id || storeUser.userId,
        deptId: info.deptId || storeUser.deptId,
        tenantId: info.tenantId || storeUser.tenantId,
        userName: info.nickname || storeUser.nickname || storeUser.username,
      };

      const resolveMeetingId = () => {
        if (that.meetingId) return Promise.resolve(that.meetingId);
        return getZkMeetingPage({ pageNo: 1, pageSize: 10, meetingNum })
          .then((res) => {
            const list = (res && res.data && res.data.list) || [];
            const hit = list.find((m) => String(m.meetingNum) === String(meetingNum));
            if (hit && hit.id != null) {
              that.meetingId = hit.id;
              return hit.id;
            }
            // 尚无会议主表行：用加入语义落一条，拿到 id（与进房 notifyParentJoin 同源）
            return receiveMsg({
              receiveType: 'son_send_to_parent_meeting_jion_meet',
              meetingNum,
              ...baseUser,
              version: Date.now(),
            }).then((r) => {
              const id = r && r.data && r.data.id;
              if (id != null) that.meetingId = id;
              return id;
            });
          });
      };

      const ensureRecordThenPage = (meetingId) => {
        if (meetingId == null || meetingId === '') {
          console.warn('[专家会议] 录制同步跳过：无法解析 meetingId', meetingNum);
          return Promise.resolve(null);
        }
        return getZkMeetingRecordPage({ pageNo: 1, pageSize: 20, meetingNum })
          .then((res) => {
            const list = (res && res.data && res.data.list) || [];
            const rec = list.find((r) => String(r.meetingNum) === String(meetingNum));
            if (rec) return rec;
            const param = {
              meetingId,
              meetingName: meetingNum,
              meetingNum,
              meetDay,
              meetingRecordName: '录制-' + meetingNum + '-' + meetDay,
              meetingRecordContent: '自动同步（离开会议）',
              status: '1',
              videoGetCount: 0,
              ...baseUser,
            };
            return createZkMeetingRecord(param).then(() =>
              getZkMeetingRecordPage({ pageNo: 1, pageSize: 20, meetingNum }).then((res2) => {
                const list2 = (res2 && res2.data && res2.data.list) || [];
                return list2.find((r) => String(r.meetingNum) === String(meetingNum)) || null;
              })
            );
          });
      };

      resolveMeetingId()
        .then((mid) => ensureRecordThenPage(mid))
        .then((rec) => {
          console.info('[专家会议] 录制记录已自动同步', rec && rec.meetingRecordName, rec && rec.localVideoUrls);
          if (rec && rec.localVideoUrls) return;
          const delays = [20000, 60000, 120000, 180000];
          delays.forEach((ms, idx) => {
            setTimeout(() => {
              if (that._autosyncGotUrl) return;
              getZkMeetingRecordPage({ pageNo: 1, pageSize: 20, meetingNum })
                .then((res) => {
                  const list = (res && res.data && res.data.list) || [];
                  const r = list.find((x) => String(x.meetingNum) === String(meetingNum));
                  if (r && r.localVideoUrls) {
                    that._autosyncGotUrl = true;
                    console.info('[专家会议] 录制 URL 延迟回填成功', 'retry#' + (idx + 1), r.localVideoUrls);
                  }
                })
                .catch(() => {});
            }, ms);
          });
        })
        .catch((e) => {
          console.warn('[专家会议] 录制记录同步失败', e && e.message);
        });
    },
    onIframeLoad() {
      this.meet_iframe = this.$refs.meetFrame;
      this.pushInitToRtc();
      setTimeout(() => this.pushInitToRtc(), 400);
      setTimeout(() => this.pushInitToRtc(), 1200);
    },
    seedKedeUser(userInfo) {
      try {
        const raw = JSON.stringify(userInfo || {});
        sessionStorage.setItem('kede-expert-user', raw);
        localStorage.setItem('kede-expert-user', raw);
      } catch (e) { /* ignore */ }
    },
    pushInitToRtc() {
      const info = this.userInfo || {};
      const storeUser = (this.$store && this.$store.state && this.$store.state.user) || {};
      const nick = this.resolveDisplayName();
      const userInfo = {
        userId: this.resolveUserId(),
        nickname: nick,
        userName: nick,
        deptId: info.deptId || storeUser.deptId,
        tenantId: info.tenantId,
        accessToken: info.accessToken || (typeof localStorage !== 'undefined' ? localStorage.getItem('ACCESS_TOKEN') : ''),
        roles: info.roles || info.roleNames || storeUser.roles || [],
        isProfessional: this.isProfessional,
      };
      this.seedKedeUser(userInfo);
      this.postToRtc({
        type: 'parent_init_kede',
        userInfo,
      });
    },
    postToRtc(payload) {
      try {
        if (!this.meet_iframe) this.meet_iframe = this.$refs.meetFrame || document.querySelector('.meet-iframe');
        if (this.meet_iframe && this.meet_iframe.contentWindow) {
          this.meet_iframe.contentWindow.postMessage(payload, '*');
        }
      } catch (e) { /* ignore */ }
      // 房间在独立窗口时，API 回包也要发过去
      try {
        if (this.meetWin && !this.meetWin.closed) {
          this.meetWin.postMessage(payload, '*');
        }
      } catch (e2) { /* ignore */ }
    },
    replyRtcApi(requestId, ok, data, msg) {
      const packet = {
        type: 'rtc_kede_api_result',
        requestId,
        ok: !!ok,
        data,
        msg: msg || '',
      };
      const source = this._rtcReplySources && this._rtcReplySources[requestId];
      if (this._rtcReplySources) delete this._rtcReplySources[requestId];
      try {
        if (source && typeof source.postMessage === 'function') {
          source.postMessage(packet, '*');
        }
      } catch (e) { /* ignore */ }
      this.postToRtc(packet);
    },
    handleRtcApi(event_data) {
      const that = this;
      const action = event_data.action;
      const payload = event_data.payload || {};
      const requestId = event_data.requestId;
      const baseUser = {
        userId: that.userInfo.userId,
        deptId: that.userInfo.deptId,
        tenantId: that.userInfo.tenantId,
        userName: that.userInfo.nickname,
      };
      if (action === 'summaryPoint') {
        const param = {
          meetingNum: payload.meetingNum || that.meetingNum,
          meetingId: payload.meetingId || that.meetingId,
          token: `Bearer ${that.userInfo.accessToken}`,
          ...baseUser,
        };
        if (!param.meetingId || !param.meetingNum) {
          that.replyRtcApi(requestId, false, null, '还没有开始会议');
          return;
        }
        summaryPoint(param).then((response) => {
          if (response.code == 0) {
            that.meetingPoint = response.data.meetingPoint;
            that.meetToDo = response.data.meetToDo;
            that.form.meetingPointAndTodo = `${response.data.meetingPoint} \n\n${response.data.meetToDo}`;
            that.replyRtcApi(requestId, true, response.data);
          } else {
            that.replyRtcApi(requestId, false, null, response.msg || '获取纪要失败');
          }
        }).catch((err) => {
          that.replyRtcApi(requestId, false, null, (err && err.message) || '系统异常');
        });
        return;
      }
      if (action === 'createRecord') {
        const meetingNum = payload.meetingNum || that.meetingNum;
        const ensureId = (payload.meetingId || that.meetingId)
          ? Promise.resolve(payload.meetingId || that.meetingId)
          : receiveMsg({
              receiveType: 'son_send_to_parent_meeting_jion_meet',
              meetingNum,
              ...baseUser,
              version: Date.now(),
            }).then((r) => {
              const id = r && r.data && r.data.id;
              if (id != null) that.meetingId = id;
              return id;
            });
        ensureId.then((meetingId) => {
          if (!meetingNum) {
            that.replyRtcApi(requestId, false, null, '会议号缺失，无法提交');
            return;
          }
          const param = {
            meetingId: meetingId || undefined,
            meetingName: payload.meetingName || meetingNum,
            meetingNum,
            meetingContent: payload.meetingContent || null,
            meetingPoint: payload.meetingPoint || that.meetingPoint,
            meetToDo: payload.meetToDo || that.meetToDo,
            meetingRecordContent: payload.meetingContent || payload.meetingPoint || null,
            meetingRecordName: '会议总结-' + meetingNum,
            status: '0',
            ...baseUser,
          };
          return createZkMeetingRecord(param).then(() => {
            that.replyRtcApi(requestId, true, { ok: true });
          });
        }).catch((err) => {
          const message = (err && err.message) || (typeof err === 'string' ? err : '') || '提交总结失败';
          that.replyRtcApi(requestId, false, null, message);
        });
        return;
      }
      if (action === 'uploadRecording') {
        const fileName = payload.fileName || (`录制-${payload.meetingNum || that.meetingNum || ''}-${Date.now()}`);
        const statusOk = payload.status !== 'failed';
        const meetingNum = payload.meetingNum || that.meetingNum;
        if (!meetingNum) {
          that.replyRtcApi(requestId, false, null, '录制未关联会议，不写入记录');
          return;
        }
        const meetDay = (() => {
          const d = new Date();
          const p = (n) => (n < 10 ? '0' + n : '' + n);
          return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate());
        })();
        const resolveId = () => {
          if (payload.meetingId || that.meetingId) {
            return Promise.resolve(payload.meetingId || that.meetingId);
          }
          return getZkMeetingPage({ pageNo: 1, pageSize: 10, meetingNum })
            .then((res) => {
              const list = (res && res.data && res.data.list) || [];
              const hit = list.find((m) => String(m.meetingNum) === String(meetingNum));
              if (hit && hit.id != null) {
                that.meetingId = hit.id;
                return hit.id;
              }
              return receiveMsg({
                receiveType: 'son_send_to_parent_meeting_jion_meet',
                meetingNum,
                ...baseUser,
                version: Date.now(),
              }).then((r) => {
                const id = r && r.data && r.data.id;
                if (id != null) that.meetingId = id;
                return id;
              });
            });
        };
        resolveId().then((meetingId) => {
          if (meetingId == null) {
            that.replyRtcApi(requestId, false, null, '无法解析会议id，录制记录未写入');
            return;
          }
          const param = {
            meetingId,
            meetingName: payload.meetingName || meetingNum,
            meetingNum,
            meetDay,
            meetingRecordName: fileName,
            meetingRecordContent: statusOk ? '已上传' : '上传失败',
            localVideoUrls: payload.fileUrl || '',
            status: statusOk ? '0' : '-1',
            videoGetCount: 0,
            ...baseUser,
          };
          return createZkMeetingRecord(param).then(() => {
            that.replyRtcApi(requestId, true, { ok: true, status: statusOk ? '已上传' : '上传失败' });
            // 触发 enhancer 拉腾讯云 URL
            that.autosyncMeetingRecord(true);
          });
        }).catch((err) => {
          that.replyRtcApi(requestId, false, null, (err && err.message) || '录制记录写入失败');
        });
        return;
      }
      if (action === 'updateFeedback') {
        if (!that.lastMeetUser || !that.lastMeetUser.id) {
          that.replyRtcApi(requestId, false, null, '暂无历史待办可反馈');
          return;
        }
        const params = {
          id: that.lastMeetUser.id,
          meetingNum: payload.meetingNum || that.meetingNum,
          meetingId: payload.meetingId || that.meetingId,
          toDoFeedback: payload.toDoFeedback,
          ...baseUser,
        };
        updateZkMeetingUser(params).then((response) => {
          if (response.code == 0) that.replyRtcApi(requestId, true, { ok: true });
          else that.replyRtcApi(requestId, false, null, '任务反馈失败');
        }).catch((err) => {
          that.replyRtcApi(requestId, false, null, (err && err.message) || '任务反馈失败');
        });
        return;
      }
      that.replyRtcApi(requestId, false, null, '未知动作');
    },
    getLastMeetUrl() {
      this.currUrl = this.meeting_url || '';
      return this.currUrl;
    },
    sendRtcCmd(cmd) {
      const frame = this.$refs.meetFrame || this.meet_iframe || document.querySelector('.meet-iframe');
      if (!frame || !frame.contentWindow) {
        this.$message && this.$message.warning('会议页尚未就绪');
        return;
      }
      frame.contentWindow.postMessage({ type: 'parent_kede_cmd', cmd }, '*');
    },
    sendMsg() {
      this.postToRtc({ type: 'parent_send_to_son', value: 'go_back' });
    },
    receiveMsg() {
      const that = this;
      if (that._rtcMsgHandler) {
        window.removeEventListener('message', that._rtcMsgHandler);
      }
      that._rtcMsgHandler = function (event) {
        if (!event.data) return;
        if (event.data.type === 'rtc_kede_ready') {
          // 子页已挂载，可推用户；仍保持遮罩直到大厅就绪
          that.pushInitToRtc();
          return;
        }
        if (event.data.type === 'rtc_kede_lobby_ready') {
          that.iframeReady = true;
          that.splashVisible = false;
          return;
        }
        if (event.data.type === 'rtc_kede_open_room') {
          // 大厅点「创建并进入 / 加入」→ 父页开独立会议窗
          that.openRoomWindow(event.data);
          return;
        }
        if (event.data.type === 'rtc_kede_room_ui') {
          that.inRoomUi = true;
          that.iframeReady = true;
          that.splashVisible = false;
          return;
        }
        if (event.data.type === 'rtc_kede_left_room') {
          that.inRoomUi = false;
          that.afterLeaveMeeting(event.data);
          return;
        }
        if (event.data.type === 'rtc_kede_api') {
          if (!that._rtcReplySources) that._rtcReplySources = {};
          if (event.data.requestId && event.source) {
            that._rtcReplySources[event.data.requestId] = event.source;
          }
          that.handleRtcApi(event.data);
          return;
        }
        if (event.data.type == 'son_send_to_parent_meeting') {
          that.handleWindowMsg(event.data);
        }
      };
      window.addEventListener('message', that._rtcMsgHandler);
    },
    handleWindowMsg(event_data) {
      let that = this;
      let params = {};
      const meetingNum = event_data && event_data.meetingNum != null
        ? String(event_data.meetingNum).trim()
        : '';
      if (event_data.value_type == 'son_send_to_parent_meeting_leave_meet') {
        that.inRoomUi = false;
        that.afterLeaveMeeting(event_data);
        return;
      }
      // 子页挂载过早时会发空会议号，直接忽略，避免弹「meetingNum不能为空」
      if (!meetingNum) {
        console.warn('[专家会议] ignore empty meetingNum', event_data && event_data.value_type);
        return;
      }
      that.meetingNum = meetingNum;
      let flag = false;
      // 1.1 son_send_to_parent_meeting_jion_meet  加入会议，没会议创建会议同时会议加入该人
      if(event_data.value_type == 'son_send_to_parent_meeting_jion_meet') {
        that.inRoomUi = true;
        that.iframeReady = true;
        that.splashVisible = false;
        params = {
          receiveType: "son_send_to_parent_meeting_jion_meet",
          meetingNum,
          userId: that.userInfo.userId,
          deptId: that.userInfo.deptId,
          tenantId: that.userInfo.tenantId,
          userName: that.userInfo.nickname,
          version: new Date().getTime(),
        };
        flag = true;
        // console.log(`============>params:${JSON.stringify(params)}`);
        // return;
      }
      // 1.2 son_send_to_parent_meeting_jion_meet  加入会议，没会议创建会议同时会议加入该人
      if(event_data.value_type == 'son_send_to_parent_meeting_chat_msg_middle') {
        // 中间消息
        return;
      }
      // 2. son_send_to_parent_meeting_chat_msg  会议聊天消息（含 AI 字幕上传）
      if(event_data.value_type == 'son_send_to_parent_meeting_chat_msg') {
        const chatMsg = String(event_data.value || '').trim();
        if (chatMsg) {
          that.form.meetingPointAndTodo = that.form.meetingPointAndTodo
            ? `${that.form.meetingPointAndTodo}\n${chatMsg}`
            : chatMsg;
          try {
            localStorage.setItem('kede-selected-asr-text', JSON.stringify({
              meetingNum,
              text: that.form.meetingPointAndTodo,
            }));
          } catch (e0) { /* ignore */ }
        }
        params = {
          receiveType: "son_send_to_parent_meeting_chat_msg",
          meetingNum,
          meetingId: that.meetingId,
          chatMsg: event_data.value,
          userId: that.userInfo.userId,
          deptId: that.userInfo.deptId,
          tenantId: that.userInfo.tenantId,
          userName: that.userInfo.nickname,
          version: new Date().getTime(),
        };
        flag = true;
        // console.log(`============>params:${JSON.stringify(params)}`);
        // return;
      }
      if(!flag) return;
      that.chatMsgToServer(params);
    },
    chatMsgToServer(params) {
      let that = this;
      receiveMsg(params).then(res => {
        that.meetingId = res.data.id;
        let zkMeetingUserDOS = res.data.zkMeetingUserDOS;
        let currMeetUser = null;
        if(zkMeetingUserDOS && zkMeetingUserDOS.length) {
          zkMeetingUserDOS.forEach(item => {
            if(item.userId == that.userInfo.userId) {
              that.currMeetUser = item;
              currMeetUser = item;
              return;
            }
          })
        }
        if(currMeetUser && currMeetUser.lastMeetUser) {
          that.lastMeetUser = currMeetUser.lastMeetUser;
          let toDoFeedback = that.lastMeetUser.toDoFeedback?that.lastMeetUser.toDoFeedback:that.lastMeetUser.meetToDo;
          let pattern = /[\n]\d+[.]/
          let isFormatAfter = pattern.test(toDoFeedback);
          that.form.toDoFeedback = isFormatAfter?toDoFeedback:this.my_common.addSplitPrefix(toDoFeedback, '\n');
        }
        that.postToRtc({
          type: 'parent_mirror_chat',
          meetingId: that.meetingId,
          toDoFeedback: that.form.toDoFeedback,
        });
      })
    },
    update_toDoFeedback() {
      let that = this;
      let params = {
          "id": that.lastMeetUser.id,
          "meetingNum": that.meetingNum,
          "meetingId": that.meetingId,
          "toDoFeedback": that.form.toDoFeedback,
          userId: that.userInfo.userId,
          deptId: that.userInfo.deptId,
          tenantId: that.userInfo.tenantId,
          userName: that.userInfo.nickname,
      }
      updateZkMeetingUser(params).then(response => {
        if(response.code == 0) {
          this.$modal.msgSuccess("任务反馈成功！");
        } else {
          this.$modal.msgWarning("任务反馈失败！"+JSON.stringify(response));
        }
      });
    },
    getMeetSummary() {
      let that = this;
      let param = {
        "meetingNum": that.meetingNum,
        "meetingId": that.meetingId,
        "token": `Bearer ${that.userInfo.accessToken}`,
        userId: that.userInfo.userId,
        deptId: that.userInfo.deptId,
        tenantId: that.userInfo.tenantId,
        userName: that.userInfo.nickname,
      }
      // console.log(`getMeetSummary==================>param:${JSON.stringify(param)}`);
      if(!param.meetingId || !param.meetingNum) {
        this.$modal.msgWarning("还没有开始会议！");
        return;
      }
      that.getMeetSummary_txt = '系统总结中……';
      // return;
      // 添加的提交
      summaryPoint(param).then(response => {
        console.log("获取会议纪要及待办==》"+ JSON.stringify(response.data));
        if(response.code == 0) {
          that.meetingPoint = response.data.meetingPoint;
          that.meetToDo = response.data.meetToDo;
          let meetingPointAndTodo = `${response.data.meetingPoint} \n\n${response.data.meetToDo}`;
          // let meetingPointAndTodo = `会议纪要：${response.data.meetingPoint} 会议待办： ${response.data.meetToDo}`;
          that.form.meetingPointAndTodo = this.my_common.addSplitPrefix(meetingPointAndTodo, '\n');
        } else {
          this.$modal.msgWarning("获取会议纪要及待办失败！"+JSON.stringify(response));
        }
        that.getMeetSummary_txt = '获取纪要及待办';
      });
    },
    addMeetRecord() {
      let that = this;
      let param = {
        "meetingId": that.meetingId,
        "meetingName": that.meetingNum,
        "meetingNum": that.meetingNum,
        "meetingContent": null,
        "meetingPoint": that.meetingPoint,
        "meetToDo": that.meetToDo,
        userId: that.userInfo.userId,
        deptId: that.userInfo.deptId,
        tenantId: that.userInfo.tenantId,
        userName: that.userInfo.nickname
      }
      console.log(`addMeetRecord==================>param:${JSON.stringify(param)}`);
      // return;
      // 添加的提交
      createZkMeetingRecord(param).then(response => {
        this.$modal.msgSuccess("已经成功提交知识库");
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkMeetingPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateUserId(userId) {
      this.pushUserIds(userId);

      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      // console.log("===========>"+JSON.stringify(this.form));
    }, 
    pushUserIds(userId) {
      if(!this.form.userIds) {
        this.form.userIds = userId;
      } else if(!this.form.userIds.includes(userId)) {
        let arr = this.form.userIds.push(userId);
      }
    },
    updateUserIds(userIds) {
      // let item = null;
      // this.userList1.forEach(obj => {
      //   if(obj.id == userId) {
      //     item = obj;
      //     return;
      //   }
      // })
      // // 2. 执行 deptId、userId赋值
      // this.form.deptId = item.deptId;
      // this.form.userName = item.nickname;
      console.log(`==>a:${userIds}`);
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          let tempStr = `${obj.deptName}-${obj.nickname}`;
          obj.nickname = tempStr;
          // console.log("===========>"+JSON.stringify(obj));
        })
      })
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        meetingName: undefined,
        meetingContent: undefined,
        meetingNum: undefined,
        startTime: undefined,
        endTime: undefined,
        status: undefined,
        userId: undefined,
        userName: undefined,
        deptId: undefined,
        fid: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加会议";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkMeeting(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改会议";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        let params = {...this.form};
        
        // 修改的提交
        if (params.id != null) {
          updateZkMeeting(params).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkMeeting(params).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除会议编号为"' + id + '"的数据项?').then(function() {
          return deleteZkMeeting(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有会议数据项?').then(() => {
          this.exportLoading = true;
          return exportZkMeetingExcel(params);
        }).then(response => {
          this.$download.excel(response, '会议.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
<style lang="scss" scoped>
.expert-meeting--embed {
  position: absolute;
  inset: 0;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  min-height: 480px;
  background: #0a1520;
  overflow: hidden;
}
.meet-iframe {
  display: block;
  position: absolute;
  z-index: 1;
  inset: 0;
  width: 100%;
  height: 100%;
  border: 0;
  background: #0a1520;
}
.meet-iframe--with-bar {
  bottom: 68px;
  height: auto;
}
.parent-meet-bar {
  position: absolute;
  z-index: 30;
  left: 0;
  right: 0;
  bottom: 0;
  box-sizing: border-box;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 68px;
  padding: 10px 12px;
  background: #0c1c24;
  border-top: 3px solid #4c9165;
  box-shadow: 0 -8px 24px rgba(0, 0, 0, 0.4);
}
.pmb-btn {
  height: 40px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid rgba(76, 145, 101, 0.55);
  background: #123028;
  color: #d8f3e4;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}
.pmb-btn:hover {
  filter: brightness(1.12);
}
.pmb-btn--accent {
  background: linear-gradient(90deg, #3d8b5c, #5cb87a);
  color: #06110b;
  border-color: #5cb87a;
}
.pmb-btn--warn {
  border-color: rgba(230, 162, 60, 0.7);
  color: #f0d9a8;
}
.pmb-btn--danger {
  border-color: rgba(220, 80, 80, 0.7);
  color: #f0b4b4;
}
.summary-meta {
  margin: 0 0 12px;
  color: #606266;
  font-size: 13px;
}
.meet-placeholder {
  position: absolute;
  z-index: 2;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: #0a1520;
  color: #eef4fc;
  pointer-events: none;
}
.meet-placeholder--soft {
  background: rgba(10, 21, 32, 0.72);
}
.meet-placeholder__title {
  font-size: 20px;
  font-weight: 700;
  color: #7dcea0;
}
.meet-placeholder__sub {
  font-size: 13px;
  color: rgba(238, 244, 252, 0.65);
}
.meet-placeholder__url {
  max-width: 80%;
  font-size: 12px;
  color: rgba(125, 206, 160, 0.85);
  word-break: break-all;
  text-align: center;
}
.meet-placeholder__link {
  pointer-events: auto;
  margin-top: 8px;
  color: #4c9165;
  font-size: 13px;
  text-decoration: underline;
}
.meet-placeholder__btn {
  pointer-events: auto;
  margin-top: 18px;
  min-width: 168px;
  height: 42px;
  padding: 0 20px;
  border: 0;
  border-radius: 8px;
  background: linear-gradient(90deg, #3d8b5c, #5cb87a);
  color: #06110b;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}
.meet-placeholder__btn:hover {
  filter: brightness(1.08);
}
</style>
