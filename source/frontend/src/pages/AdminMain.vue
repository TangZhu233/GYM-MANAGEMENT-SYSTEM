<template>
  <div class="admin-main">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">管理员主页</h2>
        <p class="page-sub">欢迎回来，以下是健身房今日概览</p>
      </div>
      <div class="header-actions">
        <div class="biz-pill" :class="isClosed ? 'closed' : 'open'">
          <span class="biz-dot"></span>{{ isClosed ? '已打烊' : '营业中' }}
        </div>
        <el-button size="small" class="biz-toggle-btn" :class="isClosed ? 'go-open' : ''" @click="toggleBusiness">
          {{ isClosed ? '恢复营业' : '设为打烊' }}
        </el-button>
        <el-button class="logout-btn" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col v-for="item in statCards" :key="item.label" :xs="24" :sm="12" :lg="6">
        <div class="stat-card" :style="{ '--accent': item.color, '--accent-bg': item.bg }">
          <div class="stat-icon">
            <span class="stat-glyph" :class="item.glyph"></span>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ item.value }}</span>
            <span class="stat-unit">{{ item.unit }}</span>
            <span v-if="item.trend" class="stat-trend" :class="`trend-${item.trend}`" aria-hidden="true">
              <span class="trend-count">{{ item.delta }}</span>
            </span>
          </div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </el-col>
    </el-row>

    <div class="quick-section">
      <div class="section-title">快捷入口</div>
      <div class="quick-grid">
        <div
          v-for="link in quickLinks"
          :key="link.label"
          class="quick-card"
          :style="{ '--q-color': link.color, '--q-bg': link.bg }"
          @click="router.push(link.path)"
        >
          <el-icon :size="22"><component :is="link.icon" /></el-icon>
          <span>{{ link.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Calendar, CreditCard, SuitcaseLine, SwitchButton, Tools, User } from '@element-plus/icons-vue'
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api, { postForm } from '../api/client'
import { businessStatus, refreshBusinessStatus } from '../state/business'

const router = useRouter()
const route = useRoute()
const totals = reactive({
  memberTotal: 0,
  employeeTotal: 0,
  humanTotal: 0,
  equipmentTotal: 0
})
const trendState = reactive({
  memberTotal: '',
  employeeTotal: '',
  humanTotal: '',
  equipmentTotal: ''
})
const trendDelta = reactive({
  memberTotal: 0,
  employeeTotal: 0,
  humanTotal: 0,
  equipmentTotal: 0
})

const totalKeys = ['memberTotal', 'employeeTotal', 'humanTotal', 'equipmentTotal']
const dailyTotalsCacheKey = 'gym-admin-main-daily-totals'

const isClosed = computed(() => businessStatus.value === 'closed')

async function toggleBusiness() {
  const toOpen = isClosed.value
  const action = toOpen ? '恢复营业' : '设为打烊'
  try {
    await ElMessageBox.confirm(`确定要${action}吗？`, '营业状态', {
      type: toOpen ? 'success' : 'warning',
      confirmButtonText: action
    })
  } catch {
    return
  }
  try {
    const resp = await postForm('/api/business/setStatus', { status: toOpen ? 1 : 0 })
    const data = resp.data || {}
    if (data.success === false) {
      ElMessage.error(data.message || '设置失败')
      return
    }
    await refreshBusinessStatus(true)
    ElMessage.success(toOpen ? '已恢复营业' : '已设为打烊')
  } catch (e) {
    ElMessage.error('设置失败：网络异常，请稍后重试')
  }
}

const statCards = computed(() => [
  { label: '健身房总人数', value: totals.humanTotal, unit: '人', glyph: 'user', color: '#3b82f6', bg: '#eff6ff', trend: trendState.humanTotal, delta: trendDelta.humanTotal },
  { label: '员工人数', value: totals.employeeTotal, unit: '人', glyph: 'user', color: '#10b981', bg: '#ecfdf5', trend: trendState.employeeTotal, delta: trendDelta.employeeTotal },
  { label: '会员人数', value: totals.memberTotal, unit: '人', glyph: 'user', color: '#f59e0b', bg: '#fffbeb', trend: trendState.memberTotal, delta: trendDelta.memberTotal },
  { label: '器材数量', value: totals.equipmentTotal, unit: '件', glyph: 'dumbbell', color: '#8b5cf6', bg: '#f5f3ff', trend: trendState.equipmentTotal, delta: trendDelta.equipmentTotal }
])

const quickLinks = [
  { label: '会员卡查询', path: '/member/toSelByCard', icon: CreditCard, color: '#ef4444', bg: '#fff1f2' },
  { label: '会员管理', path: '/member/selMember', icon: User, color: '#3b82f6', bg: '#eff6ff' },
  { label: '课程管理', path: '/class/selClass', icon: Calendar, color: '#f59e0b', bg: '#fffbeb' },
  { label: '员工管理', path: '/employee/selEmployee', icon: SuitcaseLine, color: '#10b981', bg: '#ecfdf5' },
  { label: '器材管理', path: '/equipment/selEquipment', icon: Tools, color: '#8b5cf6', bg: '#f5f3ff' }
]

async function load() {
  const resp = await api.get('/api/toAdminMain')
  const data = resp.data || {}
  const currentTotals = {
    memberTotal: data.memberTotal || 0,
    employeeTotal: data.employeeTotal || 0,
    humanTotal: data.humanTotal || 0,
    equipmentTotal: data.equipmentTotal || 0
  }

  totals.memberTotal = currentTotals.memberTotal
  totals.employeeTotal = currentTotals.employeeTotal
  totals.humanTotal = currentTotals.humanTotal
  totals.equipmentTotal = currentTotals.equipmentTotal
  updateDailyTrends(currentTotals)
}

function dateKey(dayOffset = 0) {
  const date = new Date()
  date.setDate(date.getDate() + dayOffset)
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function updateDailyTrends(currentTotals) {
  if (String(route.query.demoTrend || '') === '1') {
    trendState.humanTotal = 'up'
    trendDelta.humanTotal = 2
    trendState.employeeTotal = 'down'
    trendDelta.employeeTotal = 1
    trendState.memberTotal = 'up'
    trendDelta.memberTotal = 3
    trendState.equipmentTotal = ''
    trendDelta.equipmentTotal = 0
    return
  }

  try {
    const snapshots = JSON.parse(localStorage.getItem(dailyTotalsCacheKey) || '{}')
    const yesterdayTotals = snapshots[dateKey(-1)] || {}

    totalKeys.forEach((key) => {
      const yesterdayValue = Number(yesterdayTotals[key])
      if (!Number.isFinite(yesterdayValue) || yesterdayValue === currentTotals[key]) {
        trendState[key] = ''
        trendDelta[key] = 0
        return
      }
      trendState[key] = currentTotals[key] > yesterdayValue ? 'up' : 'down'
      trendDelta[key] = Math.abs(currentTotals[key] - yesterdayValue)
    })

    snapshots[dateKey()] = currentTotals
    localStorage.setItem(dailyTotalsCacheKey, JSON.stringify(snapshots))
  } catch {
    totalKeys.forEach((key) => {
      trendState[key] = ''
      trendDelta[key] = 0
    })
  }
}

async function logout() {
  await postForm('/api/logout', {})
  router.push('/')
}

onMounted(() => {
  refreshBusinessStatus().catch(() => {})
  load().catch(() => {})
})
</script>

<style scoped>
.admin-main {
  min-height: 100%;
  padding: 28px 32px;
  background:
    linear-gradient(180deg, rgba(16, 185, 129, 0.07), transparent 280px),
    #edf5f0;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  margin: 0;
  color: #111827;
  font-size: 25px;
  font-weight: 600;
}

.page-sub {
  margin: 0;
  color: #9ca3af;
  font-size: 14px;
}

.logout-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-color: #e5e7eb;
  background: #ffffff;
  color: #6b7280;
}

.logout-btn:hover {
  border-color: #ef4444;
  background: #fef2f2;
  color: #ef4444;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.biz-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
}

.biz-pill .biz-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.biz-pill.open {
  background: #dcfce7;
  color: #15803d;
}

.biz-pill.open .biz-dot {
  background: #22c55e;
}

.biz-pill.closed {
  background: #fee2e2;
  color: #b91c1c;
}

.biz-pill.closed .biz-dot {
  background: #ef4444;
}

.biz-toggle-btn {
  border-color: #fca5a5;
  background: #ffffff;
  color: #b91c1c;
}

.biz-toggle-btn:hover {
  border-color: #ef4444;
  background: #fef2f2;
  color: #ef4444;
}

.biz-toggle-btn.go-open {
  border-color: #86efac;
  color: #15803d;
}

.biz-toggle-btn.go-open:hover {
  border-color: #22c55e;
  background: #f0fdf4;
  color: #15803d;
}

.stats-row {
  margin-bottom: 64px;
}

.stat-card {
  min-height: 152px;
  padding: 20px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 14px 34px rgba(30, 50, 77, 0.08);
  cursor: default;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.stat-card:hover {
  box-shadow: 0 16px 36px rgba(30, 50, 77, 0.11);
  transform: translateY(-2px);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  margin-bottom: 14px;
  border-radius: 12px;
  background: var(--accent-bg);
  color: var(--accent);
}

.stat-glyph {
  position: relative;
  display: inline-block;
  width: 20px;
  height: 20px;
}

.stat-glyph.user::before,
.stat-glyph.user::after {
  position: absolute;
  left: 50%;
  content: "";
  transform: translateX(-50%);
}

.stat-glyph.user::before {
  top: 1px;
  width: 8px;
  height: 8px;
  border: 2px solid currentColor;
  border-radius: 50%;
}

.stat-glyph.user::after {
  bottom: 1px;
  width: 17px;
  height: 8px;
  border: 2px solid currentColor;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  border-bottom: 0;
}

.stat-glyph.grid::before {
  position: absolute;
  inset: 2px;
  border: 2px solid currentColor;
  border-radius: 4px;
  content: "";
}

.stat-glyph.grid::after {
  position: absolute;
  left: 5px;
  top: 5px;
  width: 10px;
  height: 10px;
  border-top: 2px solid currentColor;
  border-bottom: 2px solid currentColor;
  content: "";
}

.stat-glyph.dumbbell {
  width: 24px;
  height: 18px;
  transform: rotate(-18deg);
}

.stat-glyph.dumbbell::before {
  position: absolute;
  left: 4px;
  right: 4px;
  top: 8px;
  height: 2px;
  border-radius: 999px;
  background: currentColor;
  content: "";
}

.stat-glyph.dumbbell::after {
  position: absolute;
  inset: 3px 0;
  border-left: 4px solid currentColor;
  border-right: 4px solid currentColor;
  box-shadow:
    4px 0 0 -1px currentColor,
    -4px 0 0 -1px currentColor;
  content: "";
}

.stat-body {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-bottom: 7px;
}

.stat-value {
  color: #0d1b2f;
  font-size: 34px;
  font-weight: 800;
  line-height: 1;
  display: inline-block;
  transition: font-size 0.25s ease, color 0.25s ease;
}

.stat-card:hover .stat-value {
  font-size: 42px;
  color: var(--accent);
}

.stat-unit {
  color: #8795a8;
  font-size: 14px;
}

.stat-trend {
  position: relative;
  display: inline-flex;
  align-items: center;
  min-width: 32px;
  height: 22px;
  margin-left: 12px;
  padding-left: 14px;
  font-size: 14px;
  font-weight: 800;
  line-height: 1;
}

.stat-trend::before {
  position: absolute;
  left: 5px;
  width: 3px;
  height: 15px;
  border-radius: 999px;
  background: currentColor;
  content: "";
}

.stat-trend::after {
  position: absolute;
  left: 1px;
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  content: "";
}

.trend-up {
  color: #17b981;
}

.trend-up::before {
  top: 6px;
}

.trend-up::after {
  top: 1px;
  border-bottom: 8px solid currentColor;
}

.trend-down {
  color: #ef4444;
}

.trend-down::before {
  top: 1px;
}

.trend-down::after {
  bottom: 1px;
  border-top: 8px solid currentColor;
}

.stat-label {
  color: #53657d;
  font-size: 14px;
}

.quick-section {
  padding: 20px 20px 16px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  background: #ffffff;
}

.section-title {
  margin-bottom: 16px;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(5, 132px);
  gap: 14px;
  justify-content: center;
}

.quick-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 7px;
  height: 78px;
  padding: 10px 8px;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  background: #ffffff;
  color: #4b5563;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.2s ease, border-color 0.2s ease, color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.quick-card:hover {
  border-color: var(--q-color);
  background: var(--q-bg);
  color: var(--q-color);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
  transform: translateY(-2px);
}

.quick-card :deep(.el-icon) {
  color: var(--q-color);
}

@media (max-width: 1080px) {
  .quick-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-main {
    padding: 20px;
  }

  .page-header {
    align-items: stretch;
    flex-direction: column;
  }

  .logout-btn {
    width: fit-content;
  }

  .quick-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
