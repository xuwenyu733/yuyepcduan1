<!-- 注册与登录功能已关闭：路由 /login 已重定向到 /config，本页暂不挂载 -->

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-brand">
        <div class="brand-title">循环水养殖</div>
        <div class="brand-sub">病害诊断系统</div>
      </div>

      <div class="mode-tabs">
        <button
          type="button"
          class="mode-tab"
          :class="{ active: mode === 'login' }"
          @click="switchMode('login')"
        >
          登录
        </button>
        <button
          type="button"
          class="mode-tab"
          :class="{ active: mode === 'register' }"
          @click="switchMode('register')"
        >
          注册
        </button>
      </div>

      <p class="login-desc">
        {{
          mode === "login"
            ? "请使用系统账号登录后继续操作"
            : "注册新账号，加入协同诊断系统"
        }}
      </p>

      <!-- 登录 -->
      <el-form
        v-if="mode === 'login'"
        ref="loginRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.native.prevent="handleLogin"
      >
        <el-form-item prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="用户名或邮箱"
            prefix-icon="el-icon-user"
            clearable
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
            show-password
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? "登录中…" : "登录" }}
        </button>
      </el-form>

      <!-- 注册 -->
      <el-form
        v-else
        ref="registerRef"
        :model="registerForm"
        :rules="registerRules"
        class="login-form"
        @submit.native.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
            prefix-icon="el-icon-user"
            clearable
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱"
            prefix-icon="el-icon-message"
            clearable
          />
        </el-form-item>
        <el-form-item prop="role">
          <el-select
            v-model="registerForm.role"
            placeholder="选择角色"
            style="width: 100%"
            popper-class="config-select-dropdown"
          >
            <el-option label="工人" value="worker" />
            <el-option label="专家" value="expert" />
          </el-select>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码（至少 6 位）"
            prefix-icon="el-icon-lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            prefix-icon="el-icon-lock"
            show-password
            @keyup.enter.native="handleRegister"
          />
        </el-form-item>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? "注册中…" : "注册并登录" }}
        </button>
      </el-form>

      <div v-if="mode === 'login'" class="login-hint">
        <div>演示账号（密码均为 123456）</div>
        <div>admin@system.com · 陈专家 · 张工</div>
      </div>
    </div>
  </div>
</template>

<script>
import { login, register } from "../api";
import { isLoggedIn, setAuth } from "@/utils/disease_auth";

export default {
  name: "Login",
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    return {
      loading: false,
      mode: "login",
      loginForm: {
        account: "admin@system.com",
        password: "123456",
      },
      registerForm: {
        username: "",
        email: "",
        role: "worker",
        password: "",
        confirmPassword: "",
      },
      loginRules: {
        account: [{ required: true, message: "请输入账号", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      },
      registerRules: {
        username: [
          { required: true, message: "请填写用户名", trigger: "blur" },
        ],
        email: [
          { required: true, message: "请填写邮箱", trigger: "blur" },
          {
            type: "email",
            message: "邮箱格式不正确",
            trigger: ["blur", "change"],
          },
        ],
        role: [{ required: true, message: "请选择角色", trigger: "change" }],
        password: [
          { required: true, message: "请填写密码", trigger: "blur" },
          { min: 6, message: "密码至少 6 位", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请确认密码", trigger: "blur" },
          { validator: validateConfirm, trigger: "blur" },
        ],
      },
    };
  },
  watch: {
    "$route.query.mode": {
      immediate: true,
      handler(val) {
        this.mode = val === "register" ? "register" : "login";
      },
    },
  },
  created() {
    if (isLoggedIn() && this.$route.query.mode !== "register") {
      const redirect = this.$route.query.redirect || "/config";
      this.$router.replace(redirect);
    }
  },
  methods: {
    switchMode(mode) {
      this.mode = mode;
      this.$router
        .replace({
          path: "/login",
          query: {
            ...this.$route.query,
            mode,
          },
        })
        .catch(() => {});
    },
    applyAuth(data) {
      setAuth({
        accessToken: data.accessToken || data.token,
        refreshToken: data.refreshToken,
        user: data.user,
      });
    },
    handleLogin() {
      this.$refs.loginRef.validate(async (valid) => {
        if (!valid) return;
        this.loading = true;
        try {
          const res = await login({
            account: this.loginForm.account.trim(),
            password: this.loginForm.password,
          });
          this.applyAuth(res.data);
          this.$message.success("登录成功");
          this.$router.replace(this.resolveHome(res.data.user));
        } catch (e) {
          // toasted
        } finally {
          this.loading = false;
        }
      });
    },
    resolveHome(user) {
      const redirect = this.$route.query.redirect;
      if (redirect && redirect !== "/login") return redirect;
      if (user && user.role === "worker") return "/diagnosis";
      if (user && user.role === "admin") return "/config";
      return "/diagnosis";
    },
    handleRegister() {
      this.$refs.registerRef.validate(async (valid) => {
        if (!valid) return;
        this.loading = true;
        try {
          const res = await register({
            username: this.registerForm.username.trim(),
            email: this.registerForm.email.trim(),
            password: this.registerForm.password,
            role: this.registerForm.role,
          });
          this.applyAuth(res.data);
          this.$message.success("注册成功，已自动登录");
          this.$router.replace(this.resolveHome(res.data.user));
        } catch (e) {
          // toasted
        } finally {
          this.loading = false;
        }
      });
    },
  },
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: radial-gradient(
      circle at top right,
      rgba(24, 144, 255, 0.12),
      transparent 40%
    ),
    radial-gradient(
      circle at bottom left,
      rgba(24, 144, 255, 0.08),
      transparent 36%
    ),
    #f7f8fa;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border: 1px solid #eef0f3;
  border-radius: 16px;
  padding: 36px 32px 28px;
  box-shadow: 0 12px 40px rgba(15, 23, 42, 0.06);
}

.login-brand {
  margin-bottom: 24px;
}

.brand-title {
  font-size: 22px;
  font-weight: 700;
  color: #1890ff;
  line-height: 1.3;
}

.brand-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #86909c;
}

.mode-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 12px;
  background: #f2f3f5;
  border-radius: 10px;
  padding: 4px;
}

.mode-tab {
  flex: 1;
  height: 36px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #86909c;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}

.mode-tab.active {
  background: #fff;
  color: #1d2129;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.login-desc {
  margin: 0 0 20px;
  font-size: 14px;
  color: #86909c;
}

.login-form >>> .el-form-item {
  margin-bottom: 16px;
}

.login-form >>> .el-input__inner,
.login-form >>> .el-select .el-input__inner {
  height: 44px;
  line-height: 44px;
  background: #f2f3f5;
  border: none;
  border-radius: 10px;
}

.login-form >>> .el-input__inner:focus,
.login-form >>> .el-select .el-input.is-focus .el-input__inner {
  background: #eef0f3;
  box-shadow: none;
}

.login-btn {
  width: 100%;
  height: 44px;
  margin-top: 4px;
  border: none;
  border-radius: 10px;
  background: #1d2129;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}

.login-btn:hover:not(:disabled) {
  background: #4e5969;
}

.login-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.login-hint {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #eef0f3;
  font-size: 12px;
  color: #86909c;
  line-height: 1.7;
}
</style>
