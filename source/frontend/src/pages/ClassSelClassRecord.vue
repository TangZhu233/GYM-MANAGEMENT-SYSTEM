<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>课程报名记录</h2>
        <p>
          课程编号: {{ classId || '全部' }}
          <template v-if="className"> | 课程名称: {{ className }}</template>
        </p>
      </div>
    </div>

    <el-table :data="recordList" class="data-table">
      <el-table-column prop="memberId" label="会员ID" width="120" />
      <el-table-column prop="memberName" label="会员姓名" width="140" />
      <el-table-column label="报名时间" width="180">
        <template #default="scope">{{ formatDateTime(scope.row.joinTime) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评分" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.rating" :type="ratingType(scope.row.rating)" size="small">{{ scope.row.rating }}</el-tag>
          <span v-else class="muted">未评</span>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!recordList.length" description="暂无报名记录" />

    <el-card class="insight-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h3>报名记录分析</h3>
            <p>评分、未评价和排行榜查询集中在记录页，和真实后台使用场景更一致。</p>
          </div>
          <div class="filters">
            <el-input-number v-model="lowRating" :min="0" :max="10" :step="0.5" />
            <span>至</span>
            <el-input-number v-model="highRating" :min="0" :max="10" :step="0.5" />
            <el-input-number v-model="topLimit" :min="1" :max="50" />
          </div>
        </div>
      </template>

      <div class="insight-actions">
        <el-button @click="runRecordInsight('ratingRange')">按评分范围查询</el-button>
        <el-button @click="runRecordInsight('unrated')">已报名未评分</el-button>
        <el-button @click="runRecordInsight('topRatings')">评分排行榜 Top N</el-button>
        <el-button @click="runRecordInsight('strengthSecond')">增肌课第二名</el-button>
        <el-button @click="runRecordInsight('passExceptYoga')">除瑜伽外评分达标</el-button>
      </div>

      <el-table v-if="activeInsight.rows.length" :data="activeInsight.rows" class="insight-table" size="small">
        <el-table-column
          v-for="column in activeInsight.columns"
          :key="column.prop"
          :prop="column.prop"
          :label="column.label"
        />
      </el-table>
      <el-empty v-else-if="activeInsight.ran" :description="`${activeInsight.title}暂无结果`" />
    </el-card>

  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api/client'

const route = useRoute()
const classId = ref(route.query.classId || '')
const className = ref('')
const recordList = ref([])
const lowRating = ref(6)
const highRating = ref(8)
const topLimit = ref(10)
const activeInsight = reactive({ title: '', ran: false, rows: [], columns: [] })

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
  const resp = await api.get('/api/class/selClassRecord', { params: { classId: classId.value } })
  const list = resp.data?.classRecordList || []
  recordList.value = list
  if (list.length) {
    className.value = list[0].className || ''
  }
}

async function runRecordInsight(type) {
  const config = {
    ratingRange: {
      title: '评分范围查询',
      url: '/api/experiment/query9b',
      params: { low: lowRating.value, high: highRating.value },
      columns: [
        { prop: 'member_id', label: '会员ID' },
        { prop: 'class_id', label: '课程ID' },
        { prop: 'rating', label: '评分' }
      ]
    },
    unrated: {
      title: '已报名未评分记录',
      url: '/api/experiment/query9d',
      params: {},
      columns: [
        { prop: 'member_id', label: '会员ID' },
        { prop: 'class_id', label: '课程ID' },
        { prop: 'status', label: '状态' }
      ]
    },
    topRatings: {
      title: '评分排行榜',
      url: '/api/experiment/query9f',
      params: { limit: topLimit.value },
      columns: [
        { prop: 'member_name', label: '会员' },
        { prop: 'class_name', label: '课程' },
        { prop: 'rating', label: '评分' }
      ]
    },
    strengthSecond: {
      title: '增肌课第二名',
      url: '/api/experiment/query6',
      params: {},
      columns: [
        { prop: 'member_name', label: '会员姓名' },
        { prop: 'rating', label: '评分' }
      ]
    },
    passExceptYoga: {
      title: '除瑜伽外评分达标会员',
      url: '/api/experiment/query5',
      params: {},
      columns: [
        { prop: 'member_id', label: '会员ID' },
        { prop: 'avg_rating', label: '平均评分' }
      ]
    }
  }[type]

  const resp = await api.get(config.url, { params: config.params })
  activeInsight.title = config.title
  activeInsight.columns = config.columns
  activeInsight.rows = resp.data?.data || []
  activeInsight.ran = true
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.page-header,
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

h2,
h3,
p {
  margin: 0;
}

h2 {
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
}

h3 {
  color: #0d1b2f;
  font-size: 18px;
  font-weight: 800;
}

p,
.muted {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.data-table {
  width: 100%;
}

.insight-card {
  margin-top: 24px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.filters {
  display: flex;
  align-items: center;
  gap: 8px;
}

.insight-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.insight-table {
  width: 100%;
  margin-top: 16px;
}

</style>
