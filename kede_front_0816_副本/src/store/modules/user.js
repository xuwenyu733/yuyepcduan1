import {login, logout, getInfo, socialLogin, smsLogin} from '@/api/login'
import {setToken, removeToken} from '@/utils/auth'
import { UserInfoKey } from '@/utils/constants'
import { clearSharedSiteOptionsCache } from '@/utils/siteOptions'
import { clearDashboardLiveCaches } from '@/utils/dashboardLiveBridge'
import { clearDashboardSnapshot } from '@/utils/dashboardCache'
import { clearHealthPreviewCaches } from '@/utils/healthPreviewBridge'
import { clearWaterQualityLiveCaches } from '@/utils/waterQualityLiveBridge'
import { notifyUserScopeChanged } from '@/utils/deptScope'

const MISSION_TASK_CACHE_KEY = 'kede_mission_tasks_cache_v5'

function clearUserScopedCaches() {
  clearSharedSiteOptionsCache()
  try { clearDashboardLiveCaches() } catch (e) { /* ignore */ }
  try { clearDashboardSnapshot() } catch (e) { /* ignore */ }
  try { clearHealthPreviewCaches() } catch (e) { /* ignore */ }
  try { clearWaterQualityLiveCaches() } catch (e) { /* ignore */ }
  try {
    if (typeof sessionStorage !== 'undefined') sessionStorage.removeItem(MISSION_TASK_CACHE_KEY)
  } catch (e) { /* ignore */ }
  notifyUserScopeChanged()
}

const user = {
  state: {
    id: 0, // 用户编号
    name: '',
    nickname: '', // 必须预声明，否则 Vue2 对后加属性不响应（切换用户后昵称不刷新）
    avatar: '',
    deptId: null,
    roles: [],
    permissions: [],
    componentList: [], // 页面组件列表
  },

  mutations: {
    SET_ID: (state, id) => {
      state.id = id
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_NICKNAME: (state, nickname) => {
      state.nickname = nickname
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_DEPT_ID: (state, deptId) => {
      state.deptId = deptId != null ? deptId : null
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
    },
    SET_COMPONENTLIST: (state, componentList) => {
      state.componentList = componentList
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const captchaVerification = userInfo.captchaVerification
      const socialCode = userInfo.socialCode
      const socialState = userInfo.socialState
      const socialType = userInfo.socialType
      return new Promise((resolve, reject) => {
        login(username, password, captchaVerification, socialType, socialCode, socialState).then(res => {
          res = res.data;
          // 设置 token
          setToken(res)
          // 登录成功先清部门相关缓存，避免沿用上一账号数据
          clearUserScopedCaches()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 社交登录
    SocialLogin({ commit }, userInfo) {
      const code = userInfo.code
      const state = userInfo.state
      const type = userInfo.type
      return new Promise((resolve, reject) => {
        socialLogin(type, code, state).then(res => {
          res = res.data;
          // 设置 token
          setToken(res)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 短信登录
    SmsLogin({ commit }, userInfo) {
      const mobile = userInfo.mobile.trim()
      const mobileCode = userInfo.mobileCode
      return new Promise((resolve, reject) => {
        smsLogin(mobile,mobileCode).then(res => {
          res = res.data;
          // 设置 token
          setToken(res)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getInfo().then(res => {
          // console.log(`获取权限数据:${JSON.stringify(res)}`);
          // 没有 data 数据，赋予个默认值
          if (!res) {
            res = {
              data: {
                roles: [],
                user: {
                  id: '',
                  avatar: '',
                  userName: '',
                  nickname: ''
                }
              }
            }
          }
          if(res.other && res.other.componentList) {
            commit('SET_COMPONENTLIST', res.other.componentList);
          }

          res = res.data; // 读取 data 数据
          // data 为空（例如空部门用户被数据权限滤掉自身）时不能继续解包，否则会抛错触发 LogOut
          if (!res || !res.user) {
            reject('获取用户信息失败，请确认账号状态或联系管理员')
            return
          }
          const user = res.user
          const rawAvatar = user && user.avatar
          // 演示站/失效外链（如 test.yudao.iocoder.cn）会 502，回退本地默认头像
          const avatarBroken = !rawAvatar
            || /test\.yudao\.iocoder\.cn/i.test(String(rawAvatar))
          const avatar = avatarBroken ? require("@/assets/images/profile.jpg") : rawAvatar;
          if (res.roles && res.roles.length > 0) { // 验证返回的roles是否是一个非空数组
            commit('SET_ROLES', res.roles)
            commit('SET_PERMISSIONS', res.permissions)
          } else {
            commit('SET_ROLES', ['ROLE_DEFAULT'])
          }
          commit('SET_ID', user.id)
          commit('SET_NAME', user.userName)
          commit('SET_NICKNAME', user.nickname)
          commit('SET_AVATAR', avatar)
          commit('SET_DEPT_ID', user.deptId != null ? user.deptId : null)
          // 同步归属部门/昵称到登录态，供 siteOptions、专家会议等读取
          try {
            const raw = localStorage.getItem(UserInfoKey)
            const info = raw ? JSON.parse(raw) : {}
            const nextDept = user.deptId != null ? user.deptId : null
            const nextUserId = user.id != null ? user.id : null
            const deptChanged = String(info.deptId || '') !== String(nextDept || '')
            const userChanged = String(info.userId || info.id || '') !== String(nextUserId || '')
            info.deptId = nextDept
            if (nextUserId != null) {
              info.userId = nextUserId
              info.id = nextUserId
            }
            if (user.nickname) info.nickname = user.nickname
            if (user.userName) info.userName = user.userName
            if (Array.isArray(res.roles)) {
              info.roles = res.roles.slice()
            }
            localStorage.setItem(UserInfoKey, JSON.stringify(info))
            // 换用户/换部门时清掉总览与任务缓存，避免串数据
            if (deptChanged || userChanged) {
              clearUserScopedCaches()
            }
          } catch (e) { /* ignore */ }
          
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 退出系统（接口失败也清本地登录态，保证能跳转 /login）
    LogOut({ commit, state }) {
      return new Promise((resolve) => {
        const finish = () => {
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          commit('SET_NAME', '')
          commit('SET_NICKNAME', '')
          commit('SET_DEPT_ID', null)
          commit('SET_ID', 0)
          clearUserScopedCaches()
          try { localStorage.removeItem(UserInfoKey) } catch (e) { /* ignore */ }
          removeToken()
          resolve()
        }
        logout().then(finish).catch(finish)
      })
    }
  }
}

export default user
