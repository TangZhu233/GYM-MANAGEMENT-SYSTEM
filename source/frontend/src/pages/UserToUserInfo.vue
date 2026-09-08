<template>
  <div style="padding: 24px">
    <div class="page-header">
      <div>
        <h2>个人信息</h2>
        <p>查看你的会员档案、联系方式与剩余课时。</p>
      </div>
      <el-button @click="router.push('/user/toUpdateInfo')">编辑个人信息</el-button>
    </div>

    <el-card v-if="member" style="max-width: 860px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="会员卡号">{{ member.memberAccount }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ member.memberName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ member.memberGender }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ member.memberAge }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ member.memberPhone }}</el-descriptions-item>
        <el-descriptions-item label="身高">{{ member.memberHeight ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="体重">{{ member.memberWeight ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="办卡时间">{{ member.cardTime }}</el-descriptions-item>
        <el-descriptions-item label="购买课时">{{ member.cardClass }} 课时</el-descriptions-item>
        <el-descriptions-item label="剩余课时">
          <el-tag :type="Number(member.cardNextClass || 0) > 0 ? 'success' : 'danger'" effect="light">
            剩余 {{ member.cardNextClass }} 课时
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-else style="max-width: 860px">未登录或会话失效。</el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/client'

const router = useRouter()
const member = ref(null)

async function load() {
  const resp = await api.get('/api/user/toUserInfo')
  member.value = resp.data?.member || null
}

onMounted(() => {
  load().catch(() => {})
})
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
</style>

