<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>编辑会员</h2>
      <el-button @click="router.push('/member/selMember')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never" v-if="member">
      <el-form label-width="140px">
        <el-form-item label="会员卡号">
          <el-input v-model="member.memberAccount" disabled />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="member.memberPassword" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="member.memberName" />
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
        <el-form-item label="身高(cm)（选填）">
          <el-input-number v-model="member.memberHeight" :min="50" :max="250" :step="1" />
        </el-form-item>
        <el-form-item label="体重(kg)（选填）">
          <el-input-number v-model="member.memberWeight" :min="20" :max="300" :step="1" />
        </el-form-item>
        <el-form-item label="购买课时">
          <el-input-number v-model="member.cardClass" :min="1" :max="999" :step="1" />
          <span class="field-tip">调大＝加课充值，差额会自动补到「剩余课时」</span>
        </el-form-item>
        <el-form-item label="剩余课时">
          <el-input-number v-model="member.cardNextClass" :min="0" :max="999" :step="1" />
          <span v-if="remainOver" class="remain-warn">剩余课时不能大于购买课时</span>
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

const route = useRoute()
const router = useRouter()
const member = ref(null)

const remainOver = computed(() => {
  const m = member.value
  if (!m) return false
  return m.cardClass != null && m.cardNextClass != null && m.cardNextClass > m.cardClass
})

async function load() {
  const memberAccount = String(route.query.memberAccount || '')
  const resp = await api.get('/api/member/toUpdateMember', { params: { memberAccount } })
  const list = resp.data?.memberList || []
  if (list.length) {
    member.value = list[0]
  }
}

// 购买课时调大＝充值加课：差额自动补到剩余课时（剩余仍可单独手改）
watch(
  () => member.value?.cardClass,
  (nv, ov) => {
    const m = member.value
    if (m && nv != null && ov != null && nv > ov) {
      m.cardNextClass = (m.cardNextClass ?? 0) + (nv - ov)
    }
  }
)

async function submit() {
  const m = member.value
  if (!m) return
  if (!m.memberName) { ElMessage.error('姓名不能为空！'); return }
  if (m.memberAge == null || m.memberAge < 1 || m.memberAge > 100) { ElMessage.error('年龄需在1-100之间！'); return }
  if (!/^\d{11}$/.test(String(m.memberPhone || ''))) { ElMessage.error('请输入11位手机号！'); return }
  if (m.memberHeight != null && (m.memberHeight < 50 || m.memberHeight > 250)) { ElMessage.error('身高需在50-250cm之间！'); return }
  if (m.memberWeight != null && (m.memberWeight < 20 || m.memberWeight > 300)) { ElMessage.error('体重需在20-300kg之间！'); return }
  if (m.cardClass == null || m.cardClass < 1) { ElMessage.error('购买课时需大于等于 1！'); return }
  if (m.cardNextClass == null || m.cardNextClass < 0) { ElMessage.error('剩余课时不能为负数！'); return }
  if (m.cardNextClass > m.cardClass) { ElMessage.error('剩余课时不能大于购买课时，请先核对课时数！'); return }

  const resp = await postForm('/api/member/updateMember', member.value)
  const data = resp.data || {}
  if (data.success === false) {
    ElMessage.error(data.message || '保存失败，请重试')
    return
  }
  ElMessage.success('已保存')
  router.push('/member/selMember')
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
  margin-left: 12px;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.6;
}

.remain-warn {
  margin-left: 12px;
  font-size: 12px;
  color: #ef4444;
  line-height: 1.6;
}
</style>
