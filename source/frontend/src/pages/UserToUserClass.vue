<template>
  <div style="padding: 24px">
    <div class="page-header">
      <div>
        <h2>我的课程</h2>
        <p>查看已报名课程，上完课后即可为课程评分。</p>
      </div>
      <el-button @click="router.push('/toUserMain')">返回主页</el-button>
    </div>

    <div class="course-summary">
      <span class="chip balance">卡内剩余课时 <b>{{ Number(member?.cardNextClass || 0) }}</b></span>
      <span v-if="summary.pending > 0" class="chip">待上课 <b>{{ summary.pending }}</b> 节</span>
      <span v-if="summary.attended > 0" class="chip done">已上课 <b>{{ summary.attended }}</b> 节</span>
      <span class="chip-sub">报名按课程总课时预扣；每打卡 1 节，课次进度 +1</span>
    </div>

    <el-card>
      <el-table :data="recordList" style="width: 100%">
        <el-table-column prop="classId" label="编号" width="120" />
        <el-table-column prop="className" label="名称" />
        <el-table-column label="时间" width="180">
          <template #default="scope">{{ formatDateTime(scope.row.classBegin) }}</template>
        </el-table-column>
        <el-table-column label="教练" width="160">
          <template #default="scope">{{ scope.row.coachName || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="课次进度" width="190">
          <template #default="scope">
            <template v-if="scope.row.status !== 2">
              <div class="session-cell">
                <span>
                  已完成 <b class="s-done">{{ sess(scope.row).done }}</b> / {{ sess(scope.row).total }} 节 ·
                  剩 <b class="s-remain">{{ sess(scope.row).remain }}</b> 节
                </span>
                <el-progress
                  :percentage="sessPercent(scope.row)"
                  :show-text="false"
                  :stroke-width="6"
                  :color="sessColor(scope.row)"
                />
              </div>
            </template>
            <span v-else style="color:#ccc">—</span>
          </template>
        </el-table-column>
        <el-table-column label="我的评分" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.rating != null" :type="ratingType(scope.row.rating)" size="small">{{ scope.row.rating }}</el-tag>
            <span v-else style="color:#999">未评</span>
          </template>
        </el-table-column>
        <el-table-column label="课程评分" width="130">
          <template #default="scope">
            <el-button
              v-if="scope.row.status !== 2"
              size="small"
              type="primary"
              @click="rate(scope.row)"
            >
              {{ scope.row.rating != null ? '改评分' : '评分' }}
            </el-button>
            <span v-else style="color:#ccc">已取消</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button
                v-if="sess(scope.row).remain > 0"
                size="small"
                type="primary"
                plain
                @click="checkIn(scope.row)"
              >
                打卡1节
              </el-button>
              <el-button size="small" type="danger" plain @click="del(scope.row.recordId)">退课</el-button>
            </template>
            <el-tag v-else-if="scope.row.status === 1" type="success" size="small">已上完</el-tag>
            <el-tag v-else type="info" size="small">已取消</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!recordList.length" style="color: #666; margin-top: 16px">暂无课程</div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const recordList = ref([])
const member = ref(null)

const summary = computed(() => {
  const list = recordList.value || []
  let attended = 0
  let pending = 0
  list.forEach((r) => {
    if (r.status === 2) return
    const s = sess(r)
    attended += s.done
    pending += s.remain
  })
  return { attended, pending }
})

// 该条报名记录的课次进度：total=总课时, done=已完成, remain=剩余
function sess(r) {
  const total = Math.max(Number(r.totalSessions || 1), 1)
  const done = Math.min(Math.max(Number(r.completedSessions || 0), 0), total)
  return { total, done, remain: total - done }
}

function sessPercent(r) {
  const s = sess(r)
  return Math.round((s.done / s.total) * 100)
}

function sessColor(r) {
  const s = sess(r)
  if (s.remain <= 0) return '#22c55e'
  if (s.done > 0) return '#f59e0b'
  return '#cbd5e1'
}

const statusMap = { 0: '已报名', 1: '已完成', 2: '已取消' }
function statusText(s) { return statusMap[s] ?? s }
function statusType(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'danger'
  return 'info'
}
function ratingType(r) {
  if (r >= 8.0) return 'success'
  if (r >= 6.0) return ''
  if (r >= 5.0) return 'warning'
  return 'danger'
}

function padTime(value) {
  return String(value).padStart(2, '0')
}

function formatDateTime(value) {
  if (!value) return '-'
  if (typeof value === 'string') {
    const normalized = value.trim().replace('T', ' ')
    const matched = normalized.match(/^(\d{4}-\d{2}-\d{2})\s+(\d{2}:\d{2})/)
    if (matched) return `${matched[1]} ${matched[2]}`
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)

  return [
    date.getFullYear(),
    padTime(date.getMonth() + 1),
    padTime(date.getDate())
  ].join('-') + ` ${padTime(date.getHours())}:${padTime(date.getMinutes())}`
}

async function load() {
  const resp = await api.get('/api/user/toUserClass')
  recordList.value = resp.data?.classRecordList || []
  member.value = resp.data?.member || null
}

async function del(recordId) {
  if (!confirm('确定要退课吗？')) return
  await postForm('/api/user/delUserClass', { recordId })
  await load()
}

async function checkIn(row) {
  try {
    const resp = await postForm('/api/user/checkInClass', { recordId: row.recordId })
    const data = resp.data || {}
    if (data.success) {
      const remain = sess(row).remain - 1
      ElMessage.success(remain <= 0 ? '已完成本课程全部课时' : `打卡成功，本课还剩 ${remain} 节`)
      await load()
    } else {
      ElMessage.error(data.message || '打卡失败，请稍后重试')
    }
  } catch (e) {
    ElMessage.error('打卡失败：网络异常，请稍后重试')
  }
}

async function rate(row) {
  const current = row.rating != null ? Number(row.rating) : undefined
  let value
  try {
    const result = await ElMessageBox.prompt(
      '请为该课程打分（0 ~ 10 分）',
      current != null ? '修改评分' : '课程评分',
      {
        inputType: 'number',
        inputValue: current,
        inputValidator: (v) => {
          if (v === null || v === undefined || v === '') return '请输入评分'
          const n = Number(v)
          if (!Number.isFinite(n) || n < 0 || n > 10) return '评分需在 0 到 10 之间'
          return true
        }
      }
    )
    value = result.value
  } catch (e) {
    return // 用户取消
  }
  if (value === null || value === undefined || value === '') return

  const rating = Number(value)
  if (!Number.isFinite(rating) || rating < 0 || rating > 10) {
    ElMessage.warning('评分需在 0 到 10 之间')
    return
  }
  try {
    const resp = await postForm('/api/user/rateClass', { recordId: row.recordId, rating })
    const data = resp.data || {}
    if (data.success) {
      ElMessage.success('评分成功')
      await load()
    } else {
      ElMessage.error(data.message || '评分失败，请稍后重试')
    }
  } catch (e) {
    ElMessage.error('评分失败：网络异常，请稍后重试')
  }
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.course-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 999px;
  background: #eef2f7;
  color: #475569;
  font-size: 13px;
}

.chip b {
  color: #0d1b2f;
  font-size: 16px;
}

.chip.balance {
  background: #e0f2fe;
  color: #075985;
}

.chip.balance b {
  color: #0369a1;
}

.chip.done {
  background: #dcfce7;
  color: #166534;
}

.chip.done b {
  color: #15803d;
}

.chip-sub {
  color: #94a3b8;
  font-size: 12px;
}

.session-cell {
  display: grid;
  gap: 5px;
  min-width: 0;
  font-size: 13px;
  color: #475569;
}

.session-cell .s-done {
  color: #15803d;
}

.session-cell .s-remain {
  color: #d97706;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.page-header h2 {
  margin: 0;
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
  line-height: 1.2;
}

.page-header p {
  margin: 6px 0 0;
  color: #718095;
  font-size: 14px;
}
</style>
