/**
 * Express 4 不会自动把 async 路由的 reject 交给 error middleware。
 * 在注册路由之前 require 本文件即可全局兜住。
 */
const Layer = require('express/lib/router/layer')

Object.defineProperty(Layer.prototype, 'handle', {
  enumerable: true,
  get() {
    return this.__handle
  },
  set(fn) {
    // 四参 error middleware 不包装
    if (!fn || fn.length === 4) {
      this.__handle = fn
      return
    }
    this.__handle = function asyncAware(req, res, next) {
      try {
        const ret = fn(req, res, next)
        if (ret && typeof ret.catch === 'function') {
          ret.catch(err => next(err))
        }
      } catch (err) {
        next(err)
      }
    }
  }
})
