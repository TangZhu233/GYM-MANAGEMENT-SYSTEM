<template>
  <div style="padding: 24px">
    <div class="page-header">
      <div>
        <h2>会员主页</h2>
        <p>会员工作台，快速访问你的个人信息与课程。</p>
      </div>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </div>

    <template v-if="member">
      <el-card class="home-card" shadow="never">
        <div class="home-head">
          <div class="home-id">
            <h3>{{ member.memberName }} 会员</h3>
            <p>会员卡号：{{ member.memberAccount }}</p>
          </div>
          <div class="remain-box">
            <span>卡内剩余课时</span>
            <b>{{ member.cardNextClass ?? 0 }}</b>
            <em>节</em>
          </div>
        </div>

        <el-divider />

        <div class="stat-row">
          <div class="stat">
            <span>已购买课时</span>
            <b>{{ member.cardClass ?? 0 }}</b>
          </div>
          <div class="stat">
            <span>办卡日期</span>
            <b>{{ member.cardTime || '-' }}</b>
          </div>
        </div>
      </el-card>

      <div class="quick-actions">
        <el-button type="primary" @click="router.push('/user/toApplyClass')">报名选课</el-button>
        <el-button @click="router.push('/user/toUserClass')">我的课程</el-button>
        <el-button @click="router.push('/user/toUserInfo')">个人信息</el-button>
      </div>
    </template>
    <el-card v-else class="home-card" shadow="never">未登录或会话失效。</el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const member = ref(null)

async function load() {
  const resp = await api.get('/api/toUserMain')
  const data = resp.data || {}
  member.value = data.member || null
}

async function logout() {
  await postForm('/api/logout', {})
  router.push('/toUserLogin')
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.home-card {
  max-width: 820px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.home-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.home-id h3 {
  margin: 0;
  color: #0d1b2f;
  font-size: 22px;
  font-weight: 800;
}

.home-id p {
  margin: 6px 0 0;
  color: #718095;
  font-size: 14px;
}

.remain-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 14px 22px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0ea66f, #22c55e);
  color: #ffffff;
}

.remain-box span {
  font-size: 13px;
  opacity: 0.9;
}

.remain-box b {
  font-size: 34px;
  font-weight: 800;
  line-height: 1.1;
}

.remain-box em {
  font-style: normal;
  font-size: 12px;
  opacity: 0.9;
}

.stat-row {
  display: flex;
  gap: 32px;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat span {
  color: #94a3b8;
  font-size: 13px;
}

.stat b {
  color: #0d1b2f;
  font-size: 20px;
  font-weight: 700;
}

.quick-actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
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

