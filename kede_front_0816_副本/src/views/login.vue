<template xmlns="">
  <div class="container">
    <div class="main-content">
      <!-- 左侧信息区域 -->
      <div class="left-section">
      <div class="logo-section">
        <div class="logo-icon">
          <svg viewBox="0 0 64 64" class="fish-logo">
            <!-- 鱼身体 -->
            <ellipse cx="32" cy="32" rx="18" ry="12" fill="#409EFF" opacity="0.9"/>
            <!-- 鱼头 -->
            <ellipse cx="20" cy="32" rx="8" ry="10" fill="#409EFF" opacity="0.9"/>
            <!-- 鱼眼睛 -->
            <circle cx="18" cy="30" r="2.5" fill="#ffffff"/>
            <circle cx="18" cy="30" r="1.5" fill="#409EFF"/>
            <!-- 鱼尾 -->
            <path d="M 50 32 L 58 24 L 58 28 L 54 32 L 58 36 L 58 40 Z" fill="#409EFF" opacity="0.9"/>
            <!-- 鱼鳍（上） -->
            <ellipse cx="32" cy="22" rx="6" ry="4" fill="#409EFF" opacity="0.8" transform="rotate(-20 32 22)"/>
            <!-- 鱼鳍（下） -->
            <ellipse cx="32" cy="42" rx="6" ry="4" fill="#409EFF" opacity="0.8" transform="rotate(20 32 42)"/>
          </svg>
        </div>
        <div class="logo-text">
          <h1 class="platform-title">智慧渔业</h1>
          <p class="platform-subtitle">Smart Aquaculture Platform</p>
        </div>
      </div>
      
      <div class="main-heading">
        <h2 class="heading-text">科技赋能</h2>
        <h3 class="heading-main">智慧养殖新时代</h3>
      </div>
      
      <p class="slogan">实时监控·数据分析·智能管理</p>
      
      <div class="feature-cards">
        <div class="feature-card">
          <div class="feature-icon wave-icon">
            <svg viewBox="0 0 32 24" class="wave-svg">
              <path d="M0,12 Q8,4 16,12 T32,12" stroke="#409EFF" stroke-width="2" fill="none"/>
              <path d="M0,16 Q8,8 16,16 T32,16" stroke="#409EFF" stroke-width="2" fill="none" opacity="0.7"/>
              <path d="M0,20 Q8,12 16,20 T32,20" stroke="#409EFF" stroke-width="2" fill="none" opacity="0.5"/>
            </svg>
          </div>
          <div class="feature-content">
            <h4 class="feature-title">水质实时监控</h4>
            <p class="feature-desc">24小时不间断监测水质参数</p>
          </div>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <svg viewBox="0 0 32 32" class="fish-icon">
              <ellipse cx="16" cy="16" rx="10" ry="6" fill="#409EFF" opacity="0.9" transform="rotate(-20 16 16)"/>
              <circle cx="14" cy="14" r="1.5" fill="#ffffff"/>
            </svg>
          </div>
          <div class="feature-content">
            <h4 class="feature-title">智能养殖管理</h4>
            <p class="feature-desc">AI驱动的精准养殖决策</p>
          </div>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <i class="el-icon-lock"></i>
          </div>
          <div class="feature-content">
            <h4 class="feature-title">数据安全保障</h4>
            <p class="feature-desc">企业级数据加密与保护</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="right-section">
      <div class="login-form-wrapper">
        <div class="welcome-section">
          <h2 class="welcome-title">欢迎回来</h2>
          <p class="welcome-subtitle">登录到智慧渔业管理系统</p>
        </div>

        <el-tabs v-model="loginForm.loginType" class="login-tabs">
          <el-tab-pane label="账号密码登录" name="uname">
          </el-tab-pane>
          <el-tab-pane label="短信验证码登录" name="sms">
          </el-tab-pane>
        </el-tabs>

        <el-form ref="loginForm" :model="loginForm" :rules="LoginRules" class="login-form" @submit.native.prevent="onEnterLogin">
          <el-form-item prop="tenantName" v-if="tenantEnable">
            <el-input v-model="loginForm.tenantName" type="text" auto-complete="off" placeholder="组织机构"
                      @keydown.enter.native.prevent="onEnterLogin">
              <i slot="prefix" class="el-input__icon el-icon-user"></i>
            </el-input>
          </el-form-item>
          
          <!-- 账号密码登录 -->
          <div v-if="loginForm.loginType === 'uname'">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" type="text" auto-complete="off" placeholder="用户名"
                        @keydown.enter.native.prevent="onEnterLogin">
                <i slot="prefix" class="el-input__icon el-icon-user"></i>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码"
                        show-password
                        @keydown.enter.native.prevent="onEnterLogin">
                <i slot="prefix" class="el-input__icon el-icon-lock"></i>
              </el-input>
            </el-form-item>
            <div class="form-options">
              <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
              <span class="forgot-password" @click.stop>忘记密码?</span>
            </div>
          </div>

          <!-- 短信验证码登录 -->
          <div v-if="loginForm.loginType === 'sms'">
            <el-form-item prop="mobile">
              <el-input v-model="loginForm.mobile" type="text" auto-complete="off" placeholder="请输入手机号"
                        @keydown.enter.native.prevent="onEnterLogin">
                <i slot="prefix" class="el-input__icon el-icon-phone"></i>
              </el-input>
            </el-form-item>
            <el-form-item prop="mobileCode">
              <el-input v-model="loginForm.mobileCode" type="text" auto-complete="off" placeholder="短信验证码"
                        class="sms-login-mobile-code-prefix"
                        @keydown.enter.native.prevent="onEnterLogin">
                <i slot="prefix" class="el-input__icon el-icon-message"></i>
                <template slot="append">
                  <span v-if="mobileCodeTimer <= 0" class="getMobileCode" @click="getSmsCode" style="cursor: pointer;">获取验证码</span>
                  <span v-if="mobileCodeTimer > 0" class="getMobileCode">{{ mobileCodeTimer }}秒后可重新获取</span>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <!-- 登录按钮 -->
          <el-form-item style="width:100%; margin-top: 30px;">
            <el-button :loading="loading" size="medium" type="primary" style="width:100%;"
                native-type="submit"
                @click.native.prevent="getCode">
              <span v-if="!loading">登录 →</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>

          <!-- 下载App链接 -->
          <div class="download-app">
            <span @click.stop="handleDownloadApp" style="cursor: pointer;">下载App</span>
          </div>
          <div class="download-app">
            <span @click.stop="handleDownloadMeetApp" style="cursor: pointer;">下载会议App</span>
          </div>
        </el-form>
      </div>
    </div>
    </div>

    <!-- 图形验证码 -->
    <Verify ref="verify" :captcha-type="'blockPuzzle'" :img-size="{width:'400px',height:'200px'}"
            @success="handleLogin" />

    <!-- footer -->
    <div class="footer">
      Copyright © 2025-2099 Kede All Rights Reserved.
    </div>
  </div>
</template>

<script>
import {sendSmsCode, socialAuthRedirect} from "@/api/login";
import {getTenantIdByName} from "@/api/system/tenant";
import {SystemUserSocialTypeEnum} from "@/utils/constants";
import {getCaptchaEnable, getTenantEnable} from "@/utils/ruoyi";
import { resetLoginPageChrome } from '@/utils/goLogin'
import {
  getPassword,
  getRememberMe, getTenantName,
  getUsername,
  removePassword, removeRememberMe, removeTenantName,
  removeUsername,
  setPassword, setRememberMe, setTenantId, setTenantName,
  setUsername
} from "@/utils/auth";

import Verify from '@/components/Verifition/Verify';
import {resetUserPwd} from "@/api/system/user";
import { createZkAppLog, updateZkAppLog, deleteZkAppLog, getZkAppLog, getZkAppLogPage, exportZkAppLogExcel } from "@/api/fish/zkAppLog";

export default {
  name: "Login",
  components: {
    Verify
  },
  data() {
    return {
      appFileUrl: '',
      meetAppFileUrl: '',
      codeUrl: "",
      captchaEnable: true,
      tenantEnable: true,
      mobileCodeTimer: 0,
      loginForm: {
        loginType: "uname",
        username: "",
        password: "",
        captchaVerification: "",
        mobile: "",
        mobileCode: "",
        rememberMe: false,
        tenantName: "北京科德数传渔业科技有限公司",
      },
      scene: 21,

      LoginRules: {
        username: [
          {required: true, trigger: "blur", message: "用户名不能为空"}
        ],
        password: [
          {required: true, trigger: "blur", message: "密码不能为空"}
        ],
        mobile: [
          {required: true, trigger: "blur", message: "手机号不能为空"},
          {
            validator: function (rule, value, callback) {
              if (/^(?:(?:\+|00)86)?1(?:3[\d]|4[5-79]|5[0-35-9]|6[5-7]|7[0-8]|8[\d]|9[189])\d{8}$/.test(value) === false) {
                callback(new Error("手机号格式错误"));
              } else {
                callback();
              }
            }, trigger: "blur"
          }
        ],
        tenantName: [
          {required: true, trigger: "blur", message: "租户不能为空"},
          {
            validator: (rule, value, callback) => {
              // debugger
              getTenantIdByName(value).then(res => {
                const tenantId = res.data;
                if (tenantId && tenantId >= 0) {
                  // 设置租户
                  setTenantId(tenantId)
                  callback();
                } else {
                  callback('租户不存在');
                }
              });
            },
            trigger: 'blur'
          }
        ]
      },
      loading: false,
      redirect: undefined,
      // 枚举
      SysUserSocialTypeEnum: SystemUserSocialTypeEnum,
    };
  },
  async created() {
    resetLoginPageChrome()
    // 租户开关
    this.tenantEnable = getTenantEnable();
    if (this.tenantEnable) {
      getTenantIdByName(this.loginForm.tenantName).then(res => { // 设置租户
        const tenantId = res.data;
        if (tenantId && tenantId >= 0) {
          setTenantId(tenantId)
        }
      });
    }
    // 验证码开关
    this.captchaEnable = getCaptchaEnable();
    // 重定向地址
    this.redirect = this.$route.query.redirect ? decodeURIComponent(this.$route.query.redirect) : undefined;
    this.getCookie();
    this.appFileUrl = await this.getApp("base_app3");
    this.meetAppFileUrl = await this.getApp("meet_app");
  },
  mounted() {
    window.addEventListener('keydown', this.onGlobalEnterLogin)
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.onGlobalEnterLogin)
  },
  methods: {
    /** 回车登录：与点击「登录」一致（含验证码流程） */
    onEnterLogin() {
      if (this.loading) return
      // 图形验证码弹层打开时，不重复触发登录
      try {
        const verify = this.$refs.verify
        if (verify && verify.clickShow) return
      } catch (e) { /* ignore */ }
      this.getCode()
    },
    onGlobalEnterLogin(e) {
      if (!e || (e.key !== 'Enter' && e.keyCode !== 13)) return
      if (this.loading) return
      const target = e.target
      if (!target || typeof target.closest !== 'function') return
      // 仅当焦点在本页登录表单内时响应，避免干扰验证码弹层
      if (!target.closest('.login-form')) return
      if (target.closest('.mask') || target.closest('.verifybox')) return
      e.preventDefault()
      this.onEnterLogin()
    },
    download(url = 'http:www.xxx.com/download?name=file.pdf', fileName = '未知文件') {
      const a = document.createElement('a');
      a.style.display = 'none';
      // a.setAttribute('target', '_self');
      /*
       * download的属性是HTML5新增的属性
       * href属性的地址必须是非跨域的地址，如果引用的是第三方的网站或者说是前后端分离的项目(调用后台的接口)，这时download就会不起作用。
       * 此时，如果是下载浏览器无法解析的文件，例如.exe,.xlsx..那么浏览器会自动下载，但是如果使用浏览器可以解析的文件，比如.txt,.png,.pdf....浏览器就会采取预览模式
       * 所以，对于.txt,.png,.pdf等的预览功能我们就可以直接不设置download属性(前提是后端响应头的Content-Type: application/octet-stream，如果为application/pdf浏览器则会判断文件为 pdf ，自动执行预览的策略)
       */
      fileName && a.setAttribute('download', fileName);
      a.href = url;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    },
    handleDownloadApp() {
      this.download(this.appFileUrl, "科德app")
    },
    handleDownloadMeetApp() {
      this.download(this.meetAppFileUrl, "科德会议app")
    },
    getApp(appName) {
      // appFileUrl（本地库 zk_app_log 可能为空，需容错）
      let params = {
        pageNo: 1,
        pageSize: 10,
        appName: appName
      };

      return getZkAppLogPage(params).then(response => {
        const list = (response && response.data && response.data.list) || [];
        return (list[0] && list[0].fileUrl) || '';
      }).catch(() => '');
    },
    getCode() {
      // 情况一，未开启：则直接登录
      if (!this.captchaEnable) {
        this.handleLogin({})
        return;
      }

      // 情况二，已开启：则展示验证码；只有完成验证码的情况，才进行登录
      // 弹出验证码
      this.$refs.verify.show()
    },
    getCookie() {
      const username = getUsername();
      const password = getPassword();
      const rememberMe = getRememberMe();
      const tenantName = getTenantName();
      this.loginForm = {
        ...this.loginForm,
        username: username ? username : this.loginForm.username,
        password: password ? password : this.loginForm.password,
        rememberMe: rememberMe ? getRememberMe() : false,
        tenantName: tenantName ? tenantName : this.loginForm.tenantName,
      };
    },
    async handleLogin(captchaParams) {
      let that = this;
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          // 设置 Cookie
          if (this.loginForm.rememberMe) {
            setUsername(this.loginForm.username)
            setPassword(this.loginForm.password)
            setRememberMe(this.loginForm.rememberMe)
            setTenantName(this.loginForm.tenantName)
          } else {
            removeUsername()
            removePassword()
            removeRememberMe()
            removeTenantName()
          }
          this.loginForm.captchaVerification = captchaParams.captchaVerification
          // 发起登陆
          // console.log("发起登录", this.loginForm);
          this.$store.dispatch(this.loginForm.loginType === "sms" ? "SmsLogin" : "Login", this.loginForm).then(() => {
            that.toPage();
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    async toPage() {
      // this.$router.push({path: this.redirect || "/"}).catch(() => {});
      this.$router.push({path: "/index_daping1"}).catch(() => {});
    },
    async doSocialLogin(socialTypeEnum) {
      // 设置登录中
      this.loading = true;
      let tenant = false;
      if (this.tenantEnable) {
        await this.$prompt('请输入租户名称', "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消"
        }).then(async ({value}) => {
          await getTenantIdByName(value).then(res => {
            const tenantId = res.data;
            tenant = true
            if (tenantId && tenantId >= 0) {
              setTenantId(tenantId)
            }
          });
        }).catch(() => {
          // 取消登录按钮 loading状态
          this.loading = false;

          return false
        });
      } else {
        tenant = true
      }
     if(tenant){
       // 计算 redirectUri
       const redirectUri = location.origin + '/social-login?'
         + encodeURIComponent('type=' + socialTypeEnum.type + '&redirect=' + (this.redirect || "/")); // 重定向不能丢
       // const redirectUri = 'http://127.0.0.1:48080/api/gitee/callback';
       // const redirectUri = 'http://127.0.0.1:48080/api/dingtalk/callback';
       // 进行跳转
       socialAuthRedirect(socialTypeEnum.type, encodeURIComponent(redirectUri)).then((res) => {
         // console.log(res.url);
         window.location.href = res.data;
       });
     }
    },
    /** ========== 以下为升级短信登录 ========== */
    getSmsCode() {
      if (this.mobileCodeTimer > 0) return;
      this.$refs.loginForm.validate(valid => {
        if (!valid) return;
        sendSmsCode(this.loginForm.mobile, this.scene, this.loginForm.uuid, this.loginForm.code).then(res => {
          this.$modal.msgSuccess("获取验证码成功")
          this.mobileCodeTimer = 60;
          let msgTimer = setInterval(() => {
            this.mobileCodeTimer = this.mobileCodeTimer - 1;
            if (this.mobileCodeTimer <= 0) {
              clearInterval(msgTimer);
            }
          }, 1000);
        });
      });
    }
  }
};
</script>
<style lang="scss" scoped>
@import "~@/assets/styles/login.scss";


.oauth-login {
  display: flex;
  align-items: center;
  cursor:pointer;
}
.oauth-login-item {
  display: flex;
  align-items: center;
  margin-right: 10px;
}
.oauth-login-item img {
  height: 25px;
  width: 25px;
}
.oauth-login-item span:hover {
  text-decoration: underline red;
  color: red;
}
.sms-login-mobile-code-prefix {
  :deep(.el-input__prefix) {
    display: flex;
    align-items: center;
    justify-content: center;
    top: 0;
    height: 100%;
    
    .el-input__icon {
      line-height: 1;
      margin-top: 0;
    }
  }
}
</style>
