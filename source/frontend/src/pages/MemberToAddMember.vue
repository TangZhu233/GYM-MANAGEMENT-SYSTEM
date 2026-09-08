<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加会员</h2>
      <el-button @click="router.push('/member/selMember')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="form.memberName" maxlength="20" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.memberGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.memberAge" :min="1" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.memberPhone" maxlength="11" show-word-limit placeholder="11位手机号" />
        </el-form-item>
        <el-form-item label="身高(cm)（选填）">
          <el-input-number v-model="form.memberHeight" :min="50" :max="250" :step="1" />
        </el-form-item>
        <el-form-item label="体重(kg)（选填）">
          <el-input-number v-model="form.memberWeight" :min="20" :max="300" :step="1" />
        </el-form-item>
        <el-form-item label="购买课时">
          <el-input-number v-model="form.cardClass" :min="1" :max="999" :step="1" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">添加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postForm } from '../api/client'

const router = useRouter()
const msg = ref('')

const form = reactive({
  memberName: '',
  memberGender: '',
  memberAge: null,
  memberPhone: '',
  memberHeight: null,
  memberWeight: null,
  cardClass: null
})

async function submit() {
  msg.value = ''
  if (!form.memberName) { ElMessage.error('请输入姓名！'); return }
  if (!form.memberGender) { ElMessage.error('请选择性别！'); return }
  if (form.memberAge == null || form.memberAge < 1 || form.memberAge > 100) { ElMessage.error('请输入有效年龄(1-100)！'); return }
  if (!form.memberPhone) { ElMessage.error('请输入联系方式！'); return }
  if (!/^\d{11}$/.test(form.memberPhone)) { ElMessage.error('请输入11位手机号！'); return }
  if (form.memberHeight != null && (form.memberHeight < 50 || form.memberHeight > 250)) { ElMessage.error('请输入有效身高(50-250cm)！'); return }
  if (form.memberWeight != null && (form.memberWeight < 20 || form.memberWeight > 300)) { ElMessage.error('请输入有效体重(20-300kg)！'); return }
  if (form.cardClass == null || form.cardClass < 1) { ElMessage.error('请输入有效课时数(>=1)！'); return }

  await postForm('/api/member/addMember', {
    memberName: form.memberName,
    memberGender: form.memberGender,
    memberAge: form.memberAge,
    memberPhone: form.memberPhone,
    memberHeight: form.memberHeight,
    memberWeight: form.memberWeight,
    cardClass: form.cardClass
  })

  router.push('/member/selMember')
}
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
</style>
