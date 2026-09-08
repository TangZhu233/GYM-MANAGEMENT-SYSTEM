<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加员工</h2>
      <el-button @click="router.push('/employee/selEmployee')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="form.employeeName" maxlength="20" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.employeeGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.employeeAge" :min="15" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="form.staff" placeholder="请选择职务" filterable allow-create>
            <el-option label="健身教练" value="健身教练" />
            <el-option label="保洁员" value="保洁员" />
            <el-option label="前台" value="前台" />
            <el-option label="经理" value="经理" />
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
          <div class="field-tip">勾选＝把该教练加入这门课的任教名单（一门课可有多位教练）；全不勾＝该教练暂不任教。</div>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="form.employeeMessage" maxlength="100" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">添加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const msg = ref('')
const form = reactive({
  employeeName: '',
  employeeGender: '',
  employeeAge: null,
  staff: '',
  employeeMessage: ''
})

const isCoach = computed(() => form.staff === '健身教练')
const courseOptions = ref([])
const taughtCourseIds = ref([])

function courseLabel(c) {
  const owner = c.coach ? `（现有教练：${c.coach}）` : '（待安排）'
  return `${c.className} ${owner}`
}

async function loadCourses() {
  const resp = await api.get('/api/class/selClass')
  courseOptions.value = resp.data?.classList || []
}

async function submit() {
  msg.value = ''
  if (!form.employeeName) { ElMessage.error('请输入姓名！'); return }
  if (!form.employeeGender) { ElMessage.error('请选择性别！'); return }
  if (form.employeeAge == null || form.employeeAge < 15 || form.employeeAge > 100) { ElMessage.error('请输入有效年龄(15-100)！'); return }
  if (!form.staff) { ElMessage.error('请选择职务！'); return }

  await postForm('/api/employee/addEmployee', {
    employeeName: form.employeeName,
    employeeGender: form.employeeGender,
    employeeAge: form.employeeAge,
    staff: form.staff,
    employeeMessage: form.employeeMessage,
    courseIds: isCoach.value ? taughtCourseIds.value.join(',') : ''
  })

  router.push('/employee/selEmployee')
}

onMounted(() => {
  loadCourses().catch(() => {})
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
