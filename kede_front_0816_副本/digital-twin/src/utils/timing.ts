/** 防抖：连续触发时只在安静 wait ms 后执行最后一次 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function debounce<T extends (...args: any[]) => void>(
  fn: T,
  waitMs: number,
): ((...args: Parameters<T>) => void) & { cancel: () => void } {
  let timer: ReturnType<typeof setTimeout> | null = null
  const wrapped = (...args: Parameters<T>) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      timer = null
      fn(...args)
    }, waitMs)
  }
  return Object.assign(wrapped, {
    cancel: () => {
      if (timer) clearTimeout(timer)
      timer = null
    },
  })
}

/** 节流：wait ms 内最多执行一次（leading + trailing） */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function throttle<T extends (...args: any[]) => void>(
  fn: T,
  waitMs: number,
): ((...args: Parameters<T>) => void) & { cancel: () => void } {
  let last = 0
  let timer: ReturnType<typeof setTimeout> | null = null
  let pendingArgs: Parameters<T> | null = null

  const wrapped = (...args: Parameters<T>) => {
    const now = Date.now()
    const remain = waitMs - (now - last)
    if (remain <= 0) {
      if (timer) {
        clearTimeout(timer)
        timer = null
      }
      last = now
      fn(...args)
      return
    }
    pendingArgs = args
    if (!timer) {
      timer = setTimeout(() => {
        last = Date.now()
        timer = null
        if (pendingArgs) {
          const a = pendingArgs
          pendingArgs = null
          fn(...a)
        }
      }, remain)
    }
  }

  return Object.assign(wrapped, {
    cancel: () => {
      if (timer) clearTimeout(timer)
      timer = null
      pendingArgs = null
    },
  })
}

/** rAF 节流：同一帧内多次调用合并为一次（适合 DOM / 材质更新） */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function rafThrottle<T extends (...args: any[]) => void>(
  fn: T,
): ((...args: Parameters<T>) => void) & { cancel: () => void } {
  let raf = 0
  let latestArgs: Parameters<T> | null = null
  const wrapped = (...args: Parameters<T>) => {
    latestArgs = args
    if (raf) return
    raf = requestAnimationFrame(() => {
      raf = 0
      const a = latestArgs
      latestArgs = null
      if (a) fn(...a)
    })
  }
  return Object.assign(wrapped, {
    cancel: () => {
      if (raf) cancelAnimationFrame(raf)
      raf = 0
      latestArgs = null
    },
  })
}
