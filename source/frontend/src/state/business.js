// 全站营业状态共享（无 Pinia，用模块级 ref）：
// 侧栏副标题 / 会员主页提示 / 管理端开关 共用，切状态后自动同步。
// businessStatus: 'open' | 'closed' | null（未加载）
import { ref } from 'vue'
import api from '../api/client'

export const businessStatus = ref(null)

export async function refreshBusinessStatus(force = false) {
  if (businessStatus.value !== null && !force) return businessStatus.value
  try {
    const resp = await api.get('/api/business/status')
    const data = resp.data || {}
    businessStatus.value = Number(data.status) === 0 ? 'closed' : 'open'
  } catch (e) {
    // 会话失效/网络异常时不打断页面，保持原值
  }
  return businessStatus.value
}
