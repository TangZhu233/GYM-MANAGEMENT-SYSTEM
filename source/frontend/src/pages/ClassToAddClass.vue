<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加课程</h2>
      <el-button @click="router.push('/class/selClass')">返回</el-button>
    </div>

    <el-card class="form-card class-form-card" shadow="never">
      <el-form label-width="120px">
        <el-form-item label="编号">
          <el-input v-model="form.classId" placeholder="4位编号" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.className" placeholder="课程名称" />
        </el-form-item>
        <el-form-item label="时间">
          <el-input v-model="form.classBegin" placeholder="xxxx年xx月xx日 00:00" />
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-input-number v-model="form.classDuration" :min="10" :max="180" :step="5" />
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
        <el-form-item label="教练">
          <el-select v-model="form.coachId" placeholder="选择教练">
            <el-option v-for="emp in coachList" :key="emp.employeeAccount" :label="emp.employeeName" :value="emp.employeeAccount" />
          </el-select>
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
          <el-button type="primary" @click="submit">添加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postForm } from '../api/client'
import api from '../api/client'

const router = useRouter()
const coachList = ref([])

const form = reactive({
  classId: '',
  className: '',
  classBegin: '',
  classDuration: 60,
  totalSessions: 1,
  classLevel: 1,
  coachId: null,
  price: 200,
  maxCapacity: 30,
  classDescription: ''
})

onMounted(async () => {
  const resp = await api.get('/api/employee/selEmployee')
  coachList.value = resp.data?.employeeList || []
})

async function submit() {
  if (!form.classId) { ElMessage.error('请输入课程编号！'); return }
  if (!form.className) { ElMessage.error('请输入课程名称！'); return }
  if (!form.classBegin) { ElMessage.error('请输入课程时间！'); return }

  await postForm('/api/class/addClass', { ...form, classId: Number(form.classId) })
  router.push('/class/selClass')
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
</style>
