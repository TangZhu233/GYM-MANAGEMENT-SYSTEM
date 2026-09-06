<template>
  <div style="padding: 24px">
    <div class="page-header">
      <div>
        <h2>编辑个人信息</h2>
        <p>维护你的姓名、联系方式与身体数据。</p>
      </div>
      <el-button @click="router.push('/user/toUserInfo')">返回</el-button>
    </div>

    <el-card style="max-width: 860px" v-if="member">
      <el-form label-width="140px">
        <el-form-item label="会员卡号">
          <el-input v-model="member.memberAccount" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="member.memberName" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="member.memberPassword" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="member.memberGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="member.memberAge" :min="1" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="member.memberPhone" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="身高（cm）">
          <el-input-number v-model="member.memberHeight" :min="50" :max="250" :step="1" />
        </el-form-item>
        <el-form-item label="体重（kg）">
          <el-input-number v-model="member.memberWeight" :min="20" :max="300" :step="1" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">确认修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-else>加载中...</el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const member = ref(null)

async function load() {
  const resp = await api.get('/api/user/toUpdateInfo')
  member.value = resp.data?.member || null
}

function validatePhone(phone) {
  const reg = /^\d{11}$/
  return reg.test(String(phone || ''))
}

async function submit() {
  if (!member.value) return
  if (!member.value.memberName) { ElMessage.error('姓名不能为空！'); return }
  if (member.value.memberAge == null || member.value.memberAge < 1 || member.value.memberAge > 100) { ElMessage.error('年龄需在1-100之间！'); return }
  if (!validatePhone(member.value.memberPhone)) { ElMessage.error('请输入11位手机号！'); return }
  if (member.value.memberHeight == null || member.value.memberHeight < 50 || member.value.memberHeight > 250) { ElMessage.error('身高需在50-250cm之间！'); return }
  if (member.value.memberWeight == null || member.value.memberWeight < 20 || member.value.memberWeight > 300) { ElMessage.error('体重需在20-300kg之间！'); return }
  await postForm('/api/user/updateInfo', member.value)
  router.push('/user/toUserInfo')
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

