<template>
  <section class="class-page">
    <div class="page-header">
      <div>
        <h2>课程管理</h2>
        <p>维护课程信息，查看报名记录。</p>
      </div>
      <el-button type="primary" @click="router.push('/class/toAddClass')">添加课程信息</el-button>
    </div>

    <el-radio-group v-model="activeTab" class="view-switch">
      <el-radio-button value="list">课程列表</el-radio-button>
      <el-radio-button value="records">报名记录</el-radio-button>
    </el-radio-group>

    <section v-if="activeTab === 'list'" class="panel">
      <el-card class="class-filter-card" shadow="never">
        <el-form class="class-filter-form" :model="classFilters" label-position="top">
          <el-form-item label="课程 / 教练">
            <el-input v-model="classFilters.keyword" clearable placeholder="课程名或教练" />
          </el-form-item>
          <el-form-item label="教练">
            <el-select v-model="classFilters.coach" clearable placeholder="全部教练">
              <el-option label="待安排（无教练）" value="__none__" />
              <el-option
                v-for="c in coachOptions"
                :key="c.employeeAccount"
                :label="c.employeeName"
                :value="String(c.employeeAccount)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="报名状态">
            <el-select v-model="classFilters.capacityStatus" clearable placeholder="全部">
              <el-option label="余量充足" value="available" />
              <el-option label="接近满员" value="nearlyFull" />
              <el-option label="满员/超容量" value="full" />
              <el-option label="零报名" value="empty" />
            </el-select>
          </el-form-item>
          <el-form-item label="等级">
            <el-select v-model="classFilters.level" clearable placeholder="全部">
              <el-option label="入门" value="1" />
              <el-option label="进阶" value="2" />
              <el-option label="专业" value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="时长">
            <el-select v-model="classFilters.duration" clearable placeholder="全部">
              <el-option label="45分钟" value="45" />
              <el-option label="60分钟" value="60" />
              <el-option label="90分钟" value="90" />
            </el-select>
          </el-form-item>
          <el-form-item class="class-filter-actions">
            <el-button type="primary" @click="applyClassFilters">查询</el-button>
            <el-button :disabled="!hasClassFilters" @click="resetClassFilters">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-table :data="pagedClassList" class="data-table">
        <el-table-column prop="classId" label="编号" width="70" />
        <el-table-column label="名称" min-width="140">
          <template #default="scope">
            <el-link class="course-name-link" @click="showDetail(scope.row.classId)">{{ scope.row.className }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="已报名 / 容量" width="180">
          <template #default="scope">
            <div class="course-capacity">
              <span>{{ numberValue(scope.row.enrolledCount) }} / {{ capacityText(scope.row.maxCapacity) }}</span>
              <el-progress
                :percentage="progressPercent(scope.row.capacityUsage)"
                :color="usageColor(scope.row.capacityUsage)"
                :show-text="false"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="courseStatusType(scope.row)" size="small">{{ courseStatusText(scope.row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="80">
          <template #default="scope">{{ levelText(scope.row.classLevel) }}</template>
        </el-table-column>
        <el-table-column label="开课时间" width="180">
          <template #default="scope">{{ formatDateTime(scope.row.classBegin) }}</template>
        </el-table-column>
        <el-table-column label="教练" width="150">
          <template #default="scope">
            <template v-if="scope.row.coach">
              <el-tag
                v-for="nm in String(scope.row.coach).split(',')"
                :key="nm"
                class="coach-tag"
                size="small"
                effect="plain"
              >
                {{ nm }}
              </el-tag>
            </template>
            <el-tag v-else type="info" size="small" effect="light">待安排</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时长" width="90">
          <template #default="scope">{{ scope.row.classTime || scope.row.classDuration }}分钟</template>
        </el-table-column>
        <el-table-column label="价格" width="90">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column label="操作" width="320">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="openRecords(scope.row.classId)">查看报名</el-button>
            <el-button size="small" @click="openCoachEditor(scope.row)">改教练</el-button>
            <el-button size="small" type="warning" plain @click="setClassSessions(scope.row)">节数</el-button>
            <el-button size="small" type="danger" @click="delClass(scope.row.classId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!classList.length" description="暂无课程数据" />
      <el-empty v-else-if="!filteredClassList.length" description="没有符合条件的课程" />

      <div v-if="classList.length" class="table-footer">
        <span class="table-count">共 {{ filteredClassList.length }} 门课程</span>
        <el-pagination
          v-model:current-page="classPage"
          v-model:page-size="classPageSize"
          :page-sizes="[10, 15, 20]"
          :total="filteredClassList.length"
          background
          layout="sizes, prev, pager, next"
        />
      </div>
    </section>

    <section v-if="activeTab === 'records'" class="panel">
      <el-card class="record-filter-card" shadow="never">
        <el-form class="record-form" :model="recordFilters" label-position="top">
          <el-form-item label="课程编号">
            <el-input v-model="recordFilters.classId" clearable placeholder="如 1" />
          </el-form-item>
          <el-form-item label="课程名称">
            <el-input v-model="recordFilters.className" clearable placeholder="如 动感单车" />
          </el-form-item>
          <el-form-item label="会员">
            <el-input v-model="recordFilters.memberKeyword" clearable placeholder="会员ID或姓名" />
          </el-form-item>
          <el-form-item label="教练">
            <el-select
              v-model="recordFilters.coachId"
              clearable
              placeholder="全部教练"
              @change="searchRecords"
            >
              <el-option
                v-for="c in coachOptions"
                :key="c.employeeAccount"
                :label="c.employeeName"
                :value="String(c.employeeAccount)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="报名状态">
            <el-select v-model="recordFilters.status" clearable placeholder="全部">
              <el-option label="已报名" value="0" />
              <el-option label="已完成" value="1" />
              <el-option label="已取消" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="评分状态">
            <el-select v-model="recordFilters.ratingStatus" clearable placeholder="全部" @change="handleRatingStatusChange">
              <el-option label="已评分" value="rated" />
              <el-option label="未评分" value="unrated" />
            </el-select>
          </el-form-item>
          <el-form-item class="record-actions">
            <el-button type="primary" :loading="recordsLoading" @click="searchRecords">查询</el-button>
            <el-button :disabled="recordsLoading" @click="resetRecordFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div v-if="!recordsTouched" class="quiet-state">设置条件后点击查询，或从课程列表进入某门课的报名记录。</div>

      <el-table v-else v-loading="recordsLoading" :data="recordList" class="data-table">
        <el-table-column prop="recordId" label="记录ID" width="90" />
        <el-table-column prop="className" label="课程" min-width="140" />
        <el-table-column label="教练" min-width="100">
          <template #default="scope">{{ scope.row.coachName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="memberId" label="会员ID" min-width="130" />
        <el-table-column prop="memberName" label="会员姓名" min-width="120" />
        <el-table-column label="报名时间" min-width="160">
          <template #default="scope">{{ formatDateTime(scope.row.joinTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
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

      <el-empty v-if="recordsTouched && !recordsLoading && !recordList.length" description="暂无报名记录" />

      <div v-if="recordsTouched" class="table-footer">
        <span class="table-count">共 {{ recordTotal }} 条报名记录</span>
        <el-pagination
          v-model:current-page="recordPage"
          v-model:page-size="recordPageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="recordTotal"
          background
          layout="sizes, prev, pager, next"
        />
      </div>
    </section>

    <el-dialog v-model="coachEditVisible" :title="`设置「${coachEditName}」的任课教练`" width="520px">
      <el-select
        v-model="coachEditIds"
        multiple
        filterable
        placeholder="可多选；清空＝该课程暂待安排（无教练）"
        style="width: 100%"
      >
        <el-option
          v-for="c in coachOptions"
          :key="c.employeeAccount"
          :label="c.employeeName"
          :value="String(c.employeeAccount)"
        />
      </el-select>
      <template #footer>
        <el-button @click="coachEditVisible = false">取消</el-button>
        <el-button type="primary" :loading="coachEditSaving" @click="saveCoachEditor">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" :title="detail.cur?.className || '课程详情'" width="620px">
      <template v-if="detail.cur">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="编号">{{ detail.cur.classId }}</el-descriptions-item>
          <el-descriptions-item label="等级">
            <el-tag>{{ levelText(detail.cur.classLevel) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="时长">{{ detail.cur.classTime || detail.cur.classDuration }}分钟</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ detail.cur.price }}</el-descriptions-item>
          <el-descriptions-item label="教练">{{ detail.cur.coach }}</el-descriptions-item>
          <el-descriptions-item label="容量">{{ detail.enrolled }} / {{ detail.cur.maxCapacity || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="开课时间" :span="2">{{ formatDateTime(detail.cur.classBegin) }}</el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">{{ detail.cur.classDescription || '暂无描述' }}</el-descriptions-item>
          <el-descriptions-item label="使用器械" :span="2">
            <template v-if="detail.equipList?.length">
              <el-tag v-for="eq in detail.equipList" :key="eq.equipmentId" class="tag" size="small">
                {{ eq.equipmentName }}
              </el-tag>
            </template>
            <span v-else class="muted">未关联器械</span>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onActivated, onBeforeUnmount, onDeactivated, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const route = useRoute()
const activeTab = ref('list')
const classList = ref([])
const coachOptions = ref([])
const classPage = ref(1)
const classPageSize = ref(10)
const classFilters = reactive({ keyword: '', coach: '', capacityStatus: '', level: '', duration: '' })
const recordList = ref([])
const recordsLoading = ref(false)
const recordsTouched = ref(false)
const recordPage = ref(1)
const recordPageSize = ref(20)
const recordTotal = ref(0)
let pageActive = true
let classRequestController = null
let recordRequestController = null
let detailRequestController = null

const recordFilters = reactive({
  classId: '',
  className: '',
  memberKeyword: '',
  coachId: '',
  status: '',
  ratingStatus: '',
  lowRating: '',
  highRating: ''
})

const detailVisible = ref(false)
const detail = reactive({ cur: null, equipList: [], enrolled: 0 })

const coachEditVisible = ref(false)
const coachEditSaving = ref(false)
const coachEditName = ref('')
const coachEditClassId = ref(null)
const coachEditIds = ref([])

const filteredClassList = computed(() => {
  const keyword = classFilters.keyword.trim().toLowerCase()
  return classList.value.filter((item) => {
    const matchesKeyword = !keyword
      || String(item.className || '').toLowerCase().includes(keyword)
      || String(item.coach || '').toLowerCase().includes(keyword)
    const coachVal = classFilters.coach
    const coachIdList = String(item.coachIds || '').split(',').filter(Boolean)
    const matchesCoach = !coachVal
      || (coachVal === '__none__' ? coachIdList.length === 0 : coachIdList.includes(coachVal))
    const matchesStatus = !classFilters.capacityStatus || courseStatusKey(item) === classFilters.capacityStatus
    const matchesLevel = !classFilters.level || String(item.classLevel || '') === classFilters.level
    const duration = Number(item.classDuration || item.classTime || 0)
    const matchesDuration = !classFilters.duration || duration === Number(classFilters.duration)
    return matchesKeyword && matchesCoach && matchesStatus && matchesLevel && matchesDuration
  })
})

const hasClassFilters = computed(() =>
  Boolean(classFilters.keyword || classFilters.coach || classFilters.capacityStatus || classFilters.level || classFilters.duration)
)

const pagedClassList = computed(() => {
  const start = (classPage.value - 1) * classPageSize.value
  return filteredClassList.value.slice(start, start + classPageSize.value)
})

const levelMap = { 1: '入门', 2: '进阶', 3: '专业' }
const statusMap = { 0: '已报名', 1: '已完成', 2: '已取消' }

function levelText(level) {
  return levelMap[level] || '未设置'
}

function statusText(status) {
  return statusMap[status] || status
}

function statusType(status) {
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
}

function ratingType(rating) {
  const value = Number(rating)
  if (value >= 8) return 'success'
  if (value >= 6) return ''
  if (value >= 5) return 'warning'
  return 'danger'
}

function nullableNumber(value) {
  if (value === '' || value === null || value === undefined) return undefined
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : undefined
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

function numberValue(value) {
  return Number(value || 0)
}

function capacityText(value) {
  const capacity = Number(value || 0)
  return capacity > 0 ? capacity : '未设置容量'
}

function progressPercent(value) {
  const percent = Number(value || 0)
  if (percent < 0) return 0
  if (percent > 100) return 100
  return percent
}

function usageColor(value) {
  const percent = Number(value || 0)
  if (percent >= 100) return '#ef4444'
  if (percent >= 90) return '#f59e0b'
  if (percent >= 60) return '#22c55e'
  return '#cbd5e1'
}

function courseStatusKey(course) {
  const enrolled = numberValue(course.enrolledCount)
  const capacity = Number(course.maxCapacity || 0)
  const usage = Number(course.capacityUsage || 0)
  if (enrolled === 0) return 'empty'
  if (!capacity) return 'available'
  if (usage >= 100) return 'full'
  if (usage >= 90) return 'nearlyFull'
  return 'available'
}

function courseStatusText(course) {
  const status = courseStatusKey(course)
  if (status === 'empty') return '零报名'
  if (status === 'full') return Number(course.capacityUsage || 0) > 100 ? '超容量' : '已满员'
  if (status === 'nearlyFull') return '接近满员'
  return '余量充足'
}

function courseStatusType(course) {
  const status = courseStatusKey(course)
  if (status === 'full') return 'danger'
  if (status === 'nearlyFull') return 'warning'
  if (status === 'empty') return 'info'
  return 'success'
}

function resetClassFilters() {
  classFilters.keyword = ''
  classFilters.coach = ''
  classFilters.capacityStatus = ''
  classFilters.level = ''
  classFilters.duration = ''
  classPage.value = 1
}

function applyClassFilters() {
  classPage.value = 1
}

function handleRatingStatusChange() {
  if (recordFilters.ratingStatus === 'unrated' || recordFilters.ratingStatus === 'rated') {
    recordFilters.lowRating = ''
    recordFilters.highRating = ''
  }
}

function isCanceled(error) {
  return error?.code === 'ERR_CANCELED' || error?.name === 'CanceledError'
}

async function loadClasses() {
  classRequestController?.abort()
  const controller = new AbortController()
  classRequestController = controller
  try {
    const resp = await api.get('/api/class/selClass', { signal: controller.signal })
    if (!pageActive || controller.signal.aborted) return
    classList.value = resp.data?.classList || []
    classPage.value = 1
  } catch (error) {
    if (!isCanceled(error)) throw error
  } finally {
    if (classRequestController === controller) classRequestController = null
  }
}

async function loadCoaches() {
  const resp = await api.get('/api/employee/selEmployee')
  coachOptions.value = (resp.data?.employeeList || []).filter((e) => e.staff === '健身教练')
}

async function loadRecords({ includeTotal = true } = {}) {
  recordRequestController?.abort()
  const controller = new AbortController()
  recordRequestController = controller
  recordsTouched.value = true
  recordsLoading.value = true
  try {
    const params = {
      classId: nullableNumber(recordFilters.classId),
      className: recordFilters.className || undefined,
      memberKeyword: recordFilters.memberKeyword || undefined,
      coachId: recordFilters.coachId ? Number(recordFilters.coachId) : undefined,
      status: nullableNumber(recordFilters.status),
      ratingStatus: recordFilters.ratingStatus || undefined,
      lowRating: recordFilters.ratingStatus ? undefined : nullableNumber(recordFilters.lowRating),
      highRating: recordFilters.ratingStatus ? undefined : nullableNumber(recordFilters.highRating),
      page: recordPage.value,
      pageSize: recordPageSize.value,
      includeTotal
    }
    const resp = await api.get('/api/class/record/search', { params, signal: controller.signal })
    if (!pageActive || controller.signal.aborted) return
    recordList.value = resp.data?.classRecordList || []
    if (includeTotal) recordTotal.value = Number(resp.data?.total || 0)
  } catch (error) {
    if (!isCanceled(error)) throw error
  } finally {
    if (recordRequestController === controller) {
      recordRequestController = null
      if (pageActive) recordsLoading.value = false
    }
  }
}

function searchRecords() {
  if (recordPage.value !== 1) {
    recordPage.value = 1
  }
  loadRecords().catch(() => {})
}

async function showDetail(classId) {
  detailRequestController?.abort()
  const controller = new AbortController()
  detailRequestController = controller
  detailVisible.value = true
  try {
    const resp = await api.get('/api/class/classDetail', { params: { classId }, signal: controller.signal })
    if (!pageActive || controller.signal.aborted) return
    detail.cur = resp.data?.course
    detail.equipList = resp.data?.equipmentList || []
    detail.enrolled = resp.data?.enrolledCount || 0
  } catch (error) {
    if (!isCanceled(error)) throw error
  } finally {
    if (detailRequestController === controller) detailRequestController = null
  }
}

function openRecords(classId) {
  recordFilters.classId = String(classId)
  recordPage.value = 1
  activeTab.value = 'records'
  loadRecords().catch(() => {})
}

function applyRecordQuery() {
  if (route.query.tab === 'list') {
    activeTab.value = 'list'
    return
  }
  if (route.query.tab !== 'records') return
  activeTab.value = 'records'
  recordFilters.classId = route.query.classId ? String(route.query.classId) : ''
  recordFilters.className = route.query.className ? String(route.query.className) : ''
  recordFilters.memberKeyword = route.query.memberKeyword ? String(route.query.memberKeyword) : ''
  recordFilters.status = route.query.status ? String(route.query.status) : ''
  recordFilters.ratingStatus = ['rated', 'unrated'].includes(route.query.ratingStatus) ? route.query.ratingStatus : ''
  recordPage.value = route.query.page ? Math.max(Number(route.query.page) || 1, 1) : 1
  handleRatingStatusChange()
  loadRecords().catch(() => {})
}

function resetRecordFilters() {
  recordFilters.classId = ''
  recordFilters.className = ''
  recordFilters.memberKeyword = ''
  recordFilters.coachId = ''
  recordFilters.status = ''
  recordFilters.ratingStatus = ''
  recordFilters.lowRating = ''
  recordFilters.highRating = ''
  recordPage.value = 1
  loadRecords().catch(() => {})
}

function openCoachEditor(row) {
  coachEditClassId.value = row.classId
  coachEditName.value = row.className || ''
  coachEditIds.value = String(row.coachIds || '').split(',').filter(Boolean)
  coachEditVisible.value = true
}

async function saveCoachEditor() {
  if (coachEditSaving.value) return
  coachEditSaving.value = true
  try {
    const resp = await postForm('/api/class/updateClassCoaches', {
      classId: coachEditClassId.value,
      coachIds: coachEditIds.value.join(',')
    })
    const data = resp.data || {}
    if (data.success === false) {
      ElMessage.error(data.message || '保存失败')
      return
    }
    ElMessage.success('已更新任课教练')
    coachEditVisible.value = false
    await loadClasses()
  } catch (e) {
    ElMessage.error('保存失败：网络异常，请稍后重试')
  } finally {
    coachEditSaving.value = false
  }
}

async function delClass(classId) {
  try { await ElMessageBox.confirm('确定要删除该课程吗？', '删除确认', { type: 'warning' }) } catch { return }
  await postForm('/api/class/delClass', { classId })
  await loadClasses()
}

async function setClassSessions(row) {
  const current = Number(row.totalSessions || 1)
  let value
  try {
    const res = await ElMessageBox.prompt(
      `「${row.className}」这门课共几节？`,
      '设置课程总课时',
      {
        inputType: 'number',
        inputValue: current,
        inputValidator: (v) => {
          const n = Number(v)
          if (!Number.isFinite(n) || n < 1) return '请输入不小于 1 的整数'
          return true
        }
      }
    )
    value = res.value
  } catch { return }

  const total = Number(value)
  if (!Number.isFinite(total) || total < 1) return
  try {
    const resp = await postForm('/api/class/updateClassSessions', { classId: row.classId, totalSessions: total })
    const data = resp.data || {}
    if (data.success) {
      ElMessage.success('已更新该课程总课时')
      await loadClasses()
    } else {
      ElMessage.error(data.message || '更新失败')
    }
  } catch (e) {
    ElMessage.error('更新失败：网络异常，请稍后重试')
  }
}

onMounted(() => {
  loadClasses().catch(() => {})
  loadCoaches().catch(() => {})
  applyRecordQuery()
})

watch(
  () => [classFilters.keyword, classFilters.coach, classFilters.capacityStatus, classFilters.level, classFilters.duration, classPageSize.value],
  () => {
    classPage.value = 1
  }
)

watch(
  () => route.fullPath,
  () => {
    if (route.path === '/class/selClass') applyRecordQuery()
  }
)

watch(activeTab, (tab) => {
  if (tab === 'records' && !recordsTouched.value) {
    recordPage.value = 1
    loadRecords().catch(() => {})
  }
})

watch(recordPage, () => {
  if (activeTab.value === 'records' && recordsTouched.value) {
    loadRecords({ includeTotal: false }).catch(() => {})
  }
})

watch(recordPageSize, () => {
  if (activeTab.value !== 'records' || !recordsTouched.value) return
  if (recordPage.value === 1) {
    loadRecords().catch(() => {})
    return
  }
  recordPage.value = 1
})

onActivated(() => {
  pageActive = true
})

onDeactivated(() => {
  pageActive = false
  recordsLoading.value = false
  classRequestController?.abort()
  recordRequestController?.abort()
  detailRequestController?.abort()
})

onBeforeUnmount(() => {
  pageActive = false
  classRequestController?.abort()
  recordRequestController?.abort()
  detailRequestController?.abort()
})
</script>

<style scoped>
.class-page {
  min-width: 0;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

h2,
p {
  margin: 0;
}

h2 {
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
}

p,
.muted {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.view-switch {
  margin-bottom: 18px;
}

.panel {
  min-width: 0;
}

.class-filter-card {
  margin-bottom: 14px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.class-filter-form {
  display: grid;
  grid-template-columns: minmax(180px, 1.2fr) 150px 150px 110px 100px auto;
  gap: 12px;
  align-items: end;
}

.class-filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.class-filter-form :deep(.el-select) {
  width: 100%;
}

.class-filter-actions {
  min-width: 92px;
}

.record-filter-card {
  margin-bottom: 18px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.record-form {
  display: grid;
  grid-template-columns: 120px minmax(130px, 1fr) minmax(130px, 1fr) 150px 130px 130px auto;
  gap: 12px;
  align-items: end;
}

.record-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.record-form :deep(.el-form-item__label) {
  padding-bottom: 5px;
  color: #53657d;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.2;
}

.record-form :deep(.el-select) {
  width: 100%;
}

.rating-range {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 8px;
  align-items: center;
}

.rating-range span {
  color: #718095;
}

.record-actions {
  min-width: 132px;
}

.quiet-state {
  padding: 38px 20px;
  border: 1px dashed #c9d5e5;
  border-radius: 14px;
  color: #718095;
  text-align: center;
  background: #f8fbff;
}

.data-table {
  width: 100%;
}

.course-name-link {
  color: #111827;
  font-weight: 600;
}

.course-name-link:hover {
  color: #0ea66f;
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

.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 14px;
}

.table-count {
  color: #718095;
  font-size: 13px;
}

.tag,
.coach-tag {
  margin-right: 6px;
}

@media (max-width: 1200px) {
  .record-form {
    grid-template-columns: repeat(2, minmax(160px, 1fr));
  }

  .class-filter-form {
    grid-template-columns: repeat(2, minmax(160px, 1fr));
  }
}
</style>
