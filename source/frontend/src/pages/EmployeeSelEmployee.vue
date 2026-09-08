<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>员工管理</h2>
        <p>按工号、姓名、性别和职务查询员工信息。</p>
      </div>
      <el-button type="primary" @click="router.push('/employee/toAddEmployee')">添加员工信息</el-button>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form class="filter-form" :model="filters" label-position="top">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="工号 / 姓名" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="filters.gender" clearable placeholder="全部">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="filters.staff" clearable placeholder="全部" class="staff-select">
            <el-option label="健身教练" value="健身教练" />
            <el-option label="保洁员" value="保洁员" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" :loading="loading" @click="search">查询</el-button>
          <el-button :disabled="loading" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table v-loading="loading" :data="employeeList" class="data-table">
      <el-table-column prop="employeeAccount" label="工号" width="120" />
      <el-table-column prop="employeeName" label="姓名" />
      <el-table-column prop="employeeGender" label="性别" width="90" />
      <el-table-column prop="employeeAge" label="年龄" width="80" />
      <el-table-column prop="entryTime" label="入职时间" width="160" />
      <el-table-column prop="staff" label="职务" />
      <el-table-column prop="employeeMessage" label="备注信息" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" type="info" @click="edit(scope.row.employeeAccount)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.employeeAccount)">解雇</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-footer">
      <span>共 {{ total }} 条</span>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="sizes, prev, pager, next"
        :total="total"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </section>
</template>

<script setup>
import { onActivated, onBeforeUnmount, onDeactivated, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const employeeList = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const filters = reactive({ keyword: '', gender: '', staff: '' })
let pageActive = true
let requestController = null

function isCanceled(error) {
  return error?.code === 'ERR_CANCELED' || error?.name === 'CanceledError'
}

async function load(params = {}) {
  requestController?.abort()
  const controller = new AbortController()
  requestController = controller
  loading.value = true
  try {
    const resp = await api.get('/api/employee/search', { params, signal: controller.signal })
    if (!pageActive || controller.signal.aborted) return
    employeeList.value = resp.data?.employeeList || []
    total.value = resp.data?.total || 0
  } catch (error) {
    if (!isCanceled(error)) throw error
  } finally {
    if (requestController === controller) {
      requestController = null
      if (pageActive) loading.value = false
    }
  }
}

function buildParams() {
  return {
    keyword: filters.keyword || undefined,
    gender: filters.gender || undefined,
    staff: filters.staff || undefined,
    page: page.value,
    pageSize: pageSize.value
  }
}

function search() {
  page.value = 1
  return load(buildParams())
}

function handlePageChange(p) {
  page.value = p
  return load(buildParams())
}

function handleSizeChange(s) {
  pageSize.value = s
  page.value = 1
  return load(buildParams())
}

function reset() {
  filters.keyword = ''
  filters.gender = ''
  filters.staff = ''
  page.value = 1
  return load(buildParams())
}

function edit(employeeAccount) {
  router.push({ path: '/employee/toUpdateEmployee', query: { employeeAccount } })
}

async function del(employeeAccount) {
  try { await ElMessageBox.confirm('确定要解雇该员工吗？', '解雇确认', { type: 'warning' }) } catch { return }
  try {
    const resp = await postForm('/api/employee/delEmployee', { employeeAccount })
    const data = resp.data || {}
    if (data.success === false) {
      ElMessage.error(data.message || '解雇失败')
      return
    }
    ElMessage.success('已解雇')
    await search()
  } catch (e) {
    ElMessage.error('解雇失败：网络异常，请稍后重试')
  }
}

onMounted(() => {
  load(buildParams()).catch(() => {})
})

onActivated(() => {
  pageActive = true
})

onDeactivated(() => {
  pageActive = false
  loading.value = false
  requestController?.abort()
})

onBeforeUnmount(() => {
  pageActive = false
  requestController?.abort()
})
</script>

<style scoped>
.page-header,
.filter-form {
  display: flex;
  align-items: end;
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

p {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 18px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.filter-form {
  display: grid;
  grid-template-columns: minmax(260px, 360px) 160px 180px auto;
  justify-content: start;
  align-items: end;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-form :deep(.el-select) {
  width: 100%;
}

.filter-actions {
  min-width: 168px;
}

@media (max-width: 1100px) {
  .filter-form {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }
}

.data-table {
  width: 100%;
}

.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
}

.table-footer span {
  color: #718095;
}
</style>
