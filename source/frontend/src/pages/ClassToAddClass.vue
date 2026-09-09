<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加课程</h2>
      <el-button @click="router.push('/class/selClass')">返回</el-button>
    </div>

    <el-card class="form-card class-form-card" shadow="never">
      <el-form label-width="120px">
        <el-form-item label="名称">
          <el-input v-model="form.className" placeholder="课程名称" />
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-select v-model="form.classDuration" placeholder="选择时长">
            <el-option v-for="o in DURATION_OPTIONS" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="总课时(节)">
          <el-input-number v-model="form.totalSessions" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="form.classLevel" placeholder="选择等级">
            <el-option label="入门" :value="1" />
            <el-option label="进阶" :value="2" />
            <el-option label="专业" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="任课教练">
          <el-select
            v-model="form.coachIds"
            multiple
            filterable
            clearable
            collapse-tags
            collapse-tags-tooltip
            placeholder="可多选（不选＝待安排）"
          >
            <el-option
              v-for="emp in coachList"
              :key="emp.employeeAccount"
              :label="emp.employeeName"
              :value="emp.employeeAccount"
            />
          </el-select>
          <div class="field-tip">一门课可一次安排多位教练；不选则显示“待安排”（报名需有教练才可选）。</div>
        </el-form-item>
        <el-form-item label="价格(元)">
          <el-input-number v-model="form.price" :min="0" :step="10" :precision="2" />
        </el-form-item>
        <el-form-item label="最大容量">
          <el-input-number v-model="form.maxCapacity" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="form.classDescription" type="textarea" placeholder="课程描述..." />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">添加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'
import { DURATION_OPTIONS } from '../constants/class'

const router = useRouter()
const submitting = ref(false)
const coachList = ref([])

// 编号由数据库主键自增、不手填；开课时间不再录入（新课程无排期时间）
const form = reactive({
  className: '',
  classDuration: 60,
  totalSessions: 1,
  classLevel: 1,
  coachIds: [] as number[],
  price: 200,
  maxCapacity: 30,
  classDescription: ''
})

onMounted(async () => {
  const resp = await api.get('/api/employee/selEmployee')
  coachList.value = (resp.data?.employeeList || []).filter((e) => e.staff === '健身教练')
})

async function submit() {
  if (!form.className.trim()) { ElMessage.error('请输入课程名称！'); return }
  submitting.value = true
  try {
    const resp = await postForm('/api/class/addClass', {
      className: form.className,
      classDuration: form.classDuration,
      totalSessions: form.totalSessions,
      classLevel: form.classLevel,
      coachIds: form.coachIds.join(','),
      price: form.price,
      maxCapacity: form.maxCapacity,
      classDescription: form.classDescription
    })
    const data = resp.data || {}
    if (data.success === false) {
      ElMessage.error(data.message || '添加失败，请重试')
      return
    }
    ElMessage.success('课程添加成功')
    router.push('/class/selClass')
  } catch (e) {
    ElMessage.error('添加失败：网络异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.form-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.class-form-card {
  max-width: 720px;
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
