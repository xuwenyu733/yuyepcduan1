/**
 * TRTC SSO 通道就绪探测：login 返回后偶发仍 SendPacket|not inited
 */
import { useRoomState } from 'tuikit-atomicx-vue3/room';

const INIT_RE = /sdk not ready|not ready|not inited|SendPacket|not init|channel.*init|未初始化/i;

export function isRoomChannelNotReadyError(error: unknown): boolean {
  if (!error) return false;
  const anyErr = error as { message?: string; msg?: string; code?: string | number };
  const msg = String(anyErr.message || anyErr.msg || anyErr.code || error || '');
  return INIT_RE.test(msg);
}

/** 轻量探测：对不存在房间调 getRoomInfo；not inited 则未就绪，房间不存在则通道已通 */
export async function probeRoomChannelReady(): Promise<boolean> {
  try {
    const { getRoomInfo } = useRoomState();
    await getRoomInfo({ roomId: `__kede_probe_${Date.now()}` });
    return true;
  } catch (error: unknown) {
    if (isRoomChannelNotReadyError(error)) return false;
    // 房间不存在 / 其它业务错误：说明通道已能发包
    return true;
  }
}

export async function waitForRoomChannelReady(options?: {
  maxMs?: number;
  intervalMs?: number;
}): Promise<boolean> {
  const maxMs = options?.maxMs ?? 8000;
  const intervalMs = options?.intervalMs ?? 400;
  const start = Date.now();
  // 登录刚完成时给 SSO 一点启动时间
  await new Promise((r) => setTimeout(r, 200));
  while (Date.now() - start < maxMs) {
    if (await probeRoomChannelReady()) return true;
    await new Promise((r) => setTimeout(r, intervalMs));
  }
  return false;
}
