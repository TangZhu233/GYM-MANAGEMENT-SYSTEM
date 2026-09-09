<template>
  <div class="checkin-page">
    <div class="page-header">
      <div>
        <h2>运动打卡</h2>
        <p>到店 / 完成当日运动点一次，养成习惯贵在坚持</p>
      </div>
      <div class="header-actions">
        <el-button @click="router.push('/user/toHealth')">健康评估</el-button>
        <el-button @click="router.push('/user/toDiet')">饮食推荐</el-button>
        <el-button @click="router.push('/toUserMain')">返回主页</el-button>
      </div>
    </div>

    <template v-if="ck">
      <el-card class="panel streak-panel" shadow="never">
        <div class="streak-hero">
          <div class="streak-num">
            <b>{{ ck.streak }}</b>
            <span>连续打卡<br />天数</span>
          </div>
          <div class="streak-side">
            <div class="today-chip">
              <el-tag :type="ck.today ? 'success' : 'info'" effect="light" size="large">
                {{ ck.today ? '今日已打卡 ✓' : '今日还没打卡' }}
              </el-tag>
            </div>
            <div class="stat-line">
              <span>累计打卡 <b>{{ ck.total }}</b> 天</span>
              <span>本月已坚持 <b>{{ monthDone }}</b> 天</span>
            </div>
          </div>
        </div>
        <div class="action-row">
          <el-button v-if="!ck.today" type="primary" size="large" :loading="checking" @click="doCheckin">
            打卡 · 今日到店运动
          </el-button>
          <span v-else class="done-msg">已完成今天的打卡，明天再来～</span>
          <span class="tip">坚持每周 3~5 次，身体会给你答案</span>
        </div>
      </el-card>

      <el-card class="panel" shadow="never">
        <div class="panel-head">
          <h3>{{ monthLabel }} 打卡日历</h3>
          <span class="muted">打勾＝当天已完成</span>
        </div>
        <div class="cal-head">
          <span v-for="d in ['一', '二', '三', '四', '五', '六', '日']" :key="d">{{ d }}</span>
        </div>
        <div class="cal-grid">
          <template v-for="(cell, idx) in cells" :key="idx">
            <span v-if="!cell" class="cal-blank"></span>
            <div v-else class="cal-cell" :class="{ done: cell.checked, today: cell.isToday }">
              <span v-if="cell.checked" class="cal-check">✓</span>
              <span v-else>{{ cell.day }}</span>
            </div>
          </template>
        </div>
      </el-card>

      <el-card class="panel" shadow="never">
        <h3 class="panel-title">打卡小贴士</h3>
        <ul class="info-list">
          <li>一次到店 / 一次居家训练均可打卡，每天限一次，真实记录才有意义。</li>
          <li>断签也别灰心——连续是从“最近一次”算起，随时能重新开始。</li>
          <li>和「我的课程」里的上课打卡是两套记录：这里记你每日运动的习惯。</li>
        </ul>
      </el-card>
    </template>

    <div v-else class="muted loading">加载中…</div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const ck = ref(null)
const checking = ref(false)

const now = new Date()
const year = now.getFullYear()
const month = now.getMonth() + 1
const todayDay = now.getDate()
const monthLabel = `${year}年${month}月`

const monthDone = computed(() => ck.value?.monthDates?.length || 0)

const cells = computed(() => {
  if (!ck.value) return []
  const checked = new Set(ck.value.monthDates || [])
  const firstDow = (new Date(year, month - 1, 1).getDay() + 6) % 7 // 周一=0
  const days = new Date(year, month, 0).getDate()
  const pad = Array.from({ length: firstDow }, () => null)
  const dayCells = Array.from({ length: days }, (_, i) => {
    const day = i + 1
    const key = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
    return { day, checked: checked.has(key), isToday: day === todayDay }
  })
  return [...pad, ...dayCells]
})

async function load() {
  const resp = await api.get('/api/user/health/overview')
  const data = resp.data || {}
  if (data.success === false) {
    ElMessage.error(data.message || '加载失败')
    return
  }
  ck.value = data.checkin || null
}

async function doCheckin() {
  if (checking.value) return
  checking.value = true
  try {
    const resp = await postForm('/api/user/health/checkin', {})
    const data = resp.data || {}
    if (data.success) {
      ElMessage.success('打卡成功，坚持锻炼！')
      await load()
    } else {
      ElMessage.error(data.message || '打卡失败')
    }
  } catch (e) {
    ElMessage.error('打卡失败：网络异常，请稍后重试')
  } finally {
    checking.value = false
  }
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.checkin-page {
  padding: 24px;
  max-width: 880px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 18px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

h2,
h3,
p,
ul {
  margin: 0;
}

h2 {
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
}

.page-header p {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.panel {
  margin-bottom: 18px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.streak-hero {
  display: flex;
  align-items: center;
  gap: 30px;
  flex-wrap: wrap;
}

.streak-num {
  display: flex;
  align-items: center;
  gap: 10px;
}

.streak-num b {
  color: #10b981;
  font-size: 56px;
  font-weight: 800;
  line-height: 1;
}

.streak-num span {
  color: #64748b;
  font-size: 13px;
  line-height: 1.5;
}

.streak-side {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.stat-line {
  display: flex;
  gap: 18px;
  color: #718095;
  font-size: 13px;
}

.stat-line b {
  color: #0d1b2f;
  font-size: 18px;
}

.action-row {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
  margin-top: 18px;
}

.done-msg {
  color: #15803d;
  font-weight: 600;
}

.tip {
  color: #94a3b8;
  font-size: 12px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.panel-head h3,
.panel-title {
  color: #0d1b2f;
  font-size: 17px;
  font-weight: 800;
}

.panel-title {
  margin-bottom: 12px;
}

.muted {
  color: #94a3b8;
  font-size: 13px;
}

.cal-head,
.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}

.cal-head {
  margin-bottom: 6px;
}

.cal-head span {
  text-align: center;
  color: #94a3b8;
  font-size: 12px;
  line-height: 22px;
}

.cal-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 42px;
  border-radius: 10px;
  background: #f8fafc;
  color: #475569;
  font-size: 14px;
}

.cal-cell.done {
  background: #dcfce7;
  color: #15803d;
  font-weight: 700;
}

.cal-cell .cal-check {
  font-weight: 800;
}

.cal-cell.today {
  outline: 2px solid #10b981;
  outline-offset: 1px;
}

.cal-blank {
  height: 42px;
}

.info-list {
  padding-left: 18px;
  color: #64748b;
  font-size: 14px;
  line-height: 2;
}

.loading {
  padding: 20px 0;
}
</style>
