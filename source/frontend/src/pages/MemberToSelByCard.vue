<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>会员卡查询</h2>
        <p>输入会员卡号或账号，快速查看会员资料和课时状态。</p>
      </div>
      <el-button @click="router.push('/toAdminMain')">返回主页</el-button>
    </div>

    <el-card class="query-card" shadow="never">
      <el-form class="query-form" label-position="top">
        <el-form-item label="会员卡号">
          <el-input v-model="memberAccount" placeholder="请输入会员卡号" @keyup.enter="submit" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="noMessage" class="empty-card" shadow="never">{{ noMessage }}</el-card>

    <el-table :data="memberList" class="data-table" v-else>
      <el-table-column prop="memberAccount" label="会员卡号" width="160" />
      <el-table-column prop="memberName" label="姓名" />
      <el-table-column prop="cardTime" label="办卡时间" width="140" />
      <el-table-column prop="memberGender" label="性别" width="90" />
      <el-table-column prop="memberAge" label="年龄" width="80" />
      <el-table-column prop="memberPhone" label="联系方式" width="140" />
      <el-table-column prop="memberHeight" label="身高" width="90">
        <template #default="{ row }">{{ row.memberHeight ?? '-' }}</template>
      </el-table-column>
      <el-table-column prop="memberWeight" label="体重" width="90">
        <template #default="{ row }">{{ row.memberWeight ?? '-' }}</template>
      </el-table-column>
      <el-table-column prop="cardClass" label="购买课时" width="100" />
      <el-table-column prop="cardNextClass" label="剩余课时" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" type="info" @click="edit(scope.row.memberAccount)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const memberAccount = ref('')
const memberList = ref([])
const noMessage = ref('')

async function submit() {
  noMessage.value = ''
  memberList.value = []
  const account = String(memberAccount.value || '').trim()
  if (!account) { ElMessage.error('请输入会员卡号！'); return }

  const resp = await postForm('/api/member/selByCard', { memberAccount: account })
  const data = resp.data || {}
  if (data.memberList && data.memberList.length) {
    memberList.value = data.memberList
  } else {
    noMessage.value = data.noMessage || '未查询到数据'
  }
}

function edit(memberAccount) {
  router.push({ path: '/member/toUpdateMember', query: { memberAccount } })
}

onMounted(() => {
  // 不强制加载，用户点击查询后展示结果
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 22px;
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

.query-card,
.empty-card {
  max-width: 860px;
  margin-bottom: 18px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.query-form {
  display: grid;
  grid-template-columns: minmax(260px, 420px) auto;
  gap: 14px;
  align-items: end;
}

.query-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.empty-card {
  color: #ef4444;
}

.data-table {
  width: 100%;
}
</style>
