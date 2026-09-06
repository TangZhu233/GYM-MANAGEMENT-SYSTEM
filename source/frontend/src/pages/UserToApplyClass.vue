<template>
  <div style="padding: 24px">
    <div class="page-header">
      <div>
        <h2>报名选课</h2>
        <p>浏览可选课程与余量 / 评分，报名你感兴趣的课程。</p>
      </div>
      <el-button @click="router.push('/user/toUserClass')">返回我的课程</el-button>
    </div>

    <div v-if="member" class="apply-tip" :class="{ warn: !hasRemain }">
      <span>卡内剩余课时：<b>{{ member.cardNextClass ?? 0 }}</b></span>
      <span class="tip-sub">（报名按该课程总课时预扣课时）</span>
      <span v-if="!hasRemain" class="tip-warn">课时不足，暂无法报名新课程</span>
    </div>

    <el-card class="apply-card" shadow="never">
      <el-table :data="classList" class="data-table" style="width: 100%">
        <el-table-column prop="classId" label="编号" width="70" />
        <el-table-column label="课程名称" min-width="150">
          <template #default="scope">
            <span class="course-name">{{ scope.row.className }}</span>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="90">
          <template #default="scope">
            <el-tag :type="levelType(scope.row.classLevel)" size="small">{{ levelText(scope.row.classLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="courseStatusType(scope.row)" size="small">{{ courseStatusText(scope.row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="已报名 / 容量" width="180">
          <template #default="scope">
            <div class="course-capacity">
              <span :class="{ full: isFull(scope.row) }">
                {{ numberValue(scope.row.enrolledCount) }} / {{ capacityText(scope.row.maxCapacity) }}
              </span>
              <el-progress
                :percentage="progressPercent(scope.row.capacityUsage)"
                :color="usageColor(scope.row.capacityUsage)"
                :show-text="false"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="classTime" label="时长(分)" width="90" />
        <el-table-column prop="coach" label="教练" width="110" />
        <el-table-column label="价格" width="90">
          <template #default="scope">
            <span class="price">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="需课时" width="90">
          <template #default="scope">{{ courseSessions(scope.row) }} 节</template>
        </el-table-column>
        <el-table-column label="平均分" width="140">
          <template #default="scope">
            <el-tag
              v-if="ratingInfo(scope.row)"
              :type="ratingType(scope.row.averageRating)"
              size="small"
            >
              {{ Number(scope.row.averageRating).toFixed(1) }} 分 · {{ scope.row.ratingCount }}人评
            </el-tag>
            <span v-else style="color: #999">暂无评分</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-tooltip
              v-if="isFull(scope.row) || !canApply(scope.row)"
              :content="applyHint(scope.row)"
              placement="top"
            >
              <span><el-button size="small" type="primary" disabled>报名</el-button></span>
            </el-tooltip>
            <el-button
              v-else
              size="small"
              type="primary"
              :loading="applyingId === scope.row.classId"
              :disabled="applyingId !== null"
              @click="apply(scope.row)"
            >
              报名
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!classList.length" description="暂无课程，去结课后即可报名" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const classList = ref<any[]>([])
const member = ref<any>(null)
const applyingId = ref<number | null>(null)

const hasRemain = computed(() => Number(member.value?.cardNextClass || 0) > 0)

function courseSessions(row: any) {
  return Math.max(Number(row?.totalSessions || 1), 1)
}

// 该课能否报名：卡内剩余课时要 >= 该课总课时
function canApply(row: any) {
  if (isFull(row)) return false
  if (!hasRemain.value) return false
  return Number(member.value?.cardNextClass || 0) >= courseSessions(row)
}

function applyHint(row: any) {
  if (isFull(row)) return '课程已满，无法报名'
  if (!hasRemain.value) return '课时不足，暂无法报名'
  return `该课需 ${courseSessions(row)} 课时，卡内剩余课时不足`
}

const levelMap: Record<number, string> = { 1: '入门', 2: '进阶', 3: '专业' }
function levelText(l: number) { return levelMap[l] ?? '未设置' }

// 等级用“由低到高”的强度配色（绿→橙→红），与管理员端一致易读
function levelType(l: number) {
  if (l === 1) return 'success'
  if (l === 2) return 'warning'
  if (l === 3) return 'danger'
  return 'info'
}

function numberValue(value: any) { return Number(value || 0) }

function capacityText(value: any) {
  const capacity = Number(value || 0)
  return capacity > 0 ? capacity : '未设置容量'
}

function progressPercent(value: any) {
  const percent = Number(value || 0)
  if (percent < 0) return 0
  if (percent > 100) return 100
  return percent
}

function usageColor(value: any) {
  const percent = Number(value || 0)
  if (percent >= 100) return '#ef4444'
  if (percent >= 90) return '#f59e0b'
  if (percent >= 60) return '#22c55e'
  return '#cbd5e1'
}

function isFull(row: any) {
  const cap = Number(row.maxCapacity || 0)
  if (!(cap > 0)) return false
  return numberValue(row.enrolledCount) >= cap
}

// 状态口径与管理端 ClassSelClass 完全一致
function courseStatusKey(course: any) {
  const enrolled = numberValue(course.enrolledCount)
  const capacity = Number(course.maxCapacity || 0)
  const usage = Number(course.capacityUsage || 0)
  if (enrolled === 0) return 'empty'
  if (!capacity) return 'available'
  if (usage >= 100) return 'full'
  if (usage >= 90) return 'nearlyFull'
  return 'available'
}

function courseStatusText(course: any) {
  const status = courseStatusKey(course)
  if (status === 'empty') return '零报名'
  if (status === 'full') return Number(course.capacityUsage || 0) > 100 ? '超容量' : '已满员'
  if (status === 'nearlyFull') return '接近满员'
  return '余量充足'
}

function courseStatusType(course: any) {
  const status = courseStatusKey(course)
  if (status === 'full') return 'danger'
  if (status === 'nearlyFull') return 'warning'
  if (status === 'empty') return 'info'
  return 'success'
}

function ratingInfo(row: any) {
  return Number(row.ratingCount || 0) > 0 && row.averageRating != null
}

function ratingType(r: any) {
  const value = Number(r)
  if (value >= 8) return 'success'
  if (value >= 6) return ''
  if (value >= 5) return 'warning'
  return 'danger'
}

async function load() {
  const resp = await api.get('/api/user/toApplyClass')
  classList.value = resp.data?.classList || []
  member.value = resp.data?.member || null
}

async function apply(row: any) {
  if (isFull(row)) {
    ElMessage.warning('该课程已满员，暂无法报名')
    return
  }
  if (!canApply(row)) {
    ElMessage.warning(applyHint(row))
    return
  }
  if (applyingId.value !== null) return
  applyingId.value = row.classId
  try {
    const resp = await postForm('/api/user/applyClass', { classId: row.classId })
    const data: any = resp.data || {}
    if (data.success) {
      ElMessage.success('报名成功')
      await load()
      router.push('/user/toUserClass')
    } else {
      ElMessage.error(data.message || '报名失败，请稍后重试')
      await load()
    }
  } catch (e: any) {
    ElMessage.error('报名失败：' + (e?.message || '网络异常，请稍后重试'))
  } finally {
    applyingId.value = null
  }
}

onMounted(() => { load().catch(() => {}) })
</script>

<style scoped>
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

.apply-tip {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  padding: 10px 16px;
  border-radius: 10px;
  background: #f0f7ff;
  border: 1px solid #cfe6ff;
  color: #1f5f9c;
  font-size: 14px;
}

.apply-tip b {
  color: #0b6bcb;
  font-size: 18px;
}

.apply-tip.warn {
  background: #fff7e6;
  border-color: #ffe1a8;
  color: #8a6d1d;
}

.apply-tip .tip-sub {
  color: #6b86a1;
}

.apply-tip .tip-warn {
  color: #d97706;
  font-weight: 600;
}

.apply-card {
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.data-table {
  width: 100%;
}

.data-table :deep(th.el-table__cell) {
  background: #f6f8fa;
  color: #334155;
  font-weight: 600;
}

.course-name {
  color: #111827;
  font-weight: 600;
}

.course-capacity {
  display: grid;
  gap: 6px;
  min-width: 0;
}

.course-capacity span {
  color: #334155;
  font-size: 13px;
  font-weight: 700;
}

.course-capacity span.full {
  color: #ef4444;
}

.price {
  color: #d97706;
  font-weight: 700;
}
</style>
