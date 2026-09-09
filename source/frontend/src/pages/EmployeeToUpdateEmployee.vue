<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>编辑员工</h2>
      <el-button @click="router.push('/employee/selEmployee')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never" v-if="employee">
      <el-form label-width="120px">
        <el-form-item label="工号">
          <el-input v-model="employee.employeeAccount" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="employee.employeeName" maxlength="20" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="employee.employeeGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="employee.employeeAge" :min="15" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="employee.staff" placeholder="请选择职务" filterable allow-create default-first-option>
            <el-option v-for="o in STAFF_OPTIONS" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isCoach" label="所教课程">
          <el-select
            v-model="taughtCourseIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="选择该教练教的课程（可多选）"
          >
            <el-option
              v-for="c in courseOptions"
              :key="c.classId"
              :value="String(c.classId)"
              :label="courseLabel(c)"
            />
          </el-select>
          <div class="field-tip">勾选＝把该教练加入这门课的任教名单（一门课可有多位教练）；取消勾选＝该教练不再教这门课。</div>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="employee.employeeMessage" maxlength="100" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">确认修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="form-card" shadow="never" v-else>加载中...</el-card>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'
import { STAFF_OPTIONS } from '../constants/staff'

const route = useRoute()
const router = useRouter()
const employee = ref(null)
const courseOptions = ref([])
const taughtCourseIds = ref([])

const isCoach = computed(() => employee.value?.staff === '健身教练')

function courseLabel(c) {
  const owner = c.coach ? `（现有教练：${c.coach}）` : '（待安排）'
  return `${c.className} ${owner}`
}

async function loadCourses() {
  const resp = await api.get('/api/class/selClass')
  courseOptions.value = resp.data?.classList || []
}

function applyTaughtSelection() {
  const e = employee.value
  if (!e) return
  if (e.staff !== '健身教练') {
    taughtCourseIds.value = []
    return
  }
  taughtCourseIds.value = courseOptions.value
    .filter((c) => String(c.coachIds || '').split(',').includes(String(e.employeeAccount)))
    .map((c) => String(c.classId))
}

async function load() {
  const employeeAccount = String(route.query.employeeAccount || '')
  const resp = await api.get('/api/employee/toUpdateEmployee', { params: { employeeAccount } })
  const list = resp.data?.employeeList || []
  employee.value = list[0] || null
  await loadCourses()
  applyTaughtSelection()
}

// 改职务（离开教练）时清空勾选，避免误派课程
watch(isCoach, (coach) => {
  if (!coach) taughtCourseIds.value = []
})

async function submit() {
  const e = employee.value
  if (!e) return
  if (!e.employeeName) { ElMessage.error('姓名不能为空！'); return }
  if (e.employeeAge == null || e.employeeAge < 15 || e.employeeAge > 100) { ElMessage.error('年龄需在15-100之间！'); return }
  if (!e.staff) { ElMessage.error('请选择职务！'); return }

  const payload = {
    ...e,
    courseIds: isCoach.value ? taughtCourseIds.value.join(',') : ''
  }
  await postForm('/api/employee/updateEmployee', payload)
  router.push('/employee/selEmployee')
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.form-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

h2 {
  margin: 0;
}

.field-tip {
  width: 100%;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.6;
}
</style>
