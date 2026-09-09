<template>
  <div class="health-page">
    <div class="page-header">
      <div>
        <h2>健康评估 · BMI</h2>
        <p>身高体重已自动读取，BMI 实时计算并给出健康建议</p>
      </div>
      <div class="header-actions">
        <el-button @click="router.push('/user/toCheckin')">运动打卡</el-button>
        <el-button @click="router.push('/user/toDiet')">饮食推荐</el-button>
        <el-button @click="router.push('/toUserMain')">返回主页</el-button>
      </div>
    </div>

    <template v-if="bmi">
      <el-card class="panel" shadow="never">
        <div class="bmi-hero">
          <div class="bmi-value">
            <b>{{ bmi.value }}</b>
            <span>BMI</span>
          </div>
          <div class="bmi-meta">
            <el-tag :type="bmiTagType(bmi.gradeCode)" size="large" effect="light">{{ bmi.label }}</el-tag>
            <div class="body-info">
              身高 {{ member?.memberHeight || '-' }} cm · 体重 {{ member?.memberWeight || '-' }} kg
            </div>
          </div>
        </div>
        <p class="advice">{{ bmi.advice }}</p>

        <div class="scale-block">
          <div class="scale-bar">
            <span class="scale-marker" :style="{ left: markerPct(bmi.value) + '%' }"></span>
          </div>
          <div class="scale-labels">
            <span>14</span><span class="t-thin">偏瘦 &lt;18.5</span>
            <span class="t-normal">正常 18.5-24</span>
            <span class="t-over">偏胖 24-28</span>
            <span class="t-fat">肥胖 ≥28</span><span>34</span>
          </div>
        </div>
      </el-card>

      <el-card class="panel" shadow="never">
        <h3 class="panel-title">关于本页</h3>
        <ul class="info-list">
          <li>身高体重来自你的「个人信息」，无需每次填写；修改后回到本页自动重算。</li>
          <li>BMI 仅作参考，运动员 / 肌肉量高人群可能偏高，请结合体脂率判断。</li>
          <li>想调整体型？右侧「饮食推荐」按你的 BMI 给出一日三餐，「运动打卡」帮你养成习惯。</li>
        </ul>
      </el-card>
    </template>

    <el-card v-else class="panel" shadow="never">
      <template v-if="member">
        <div class="fill-hint">
          <p>你还没有填写身高体重，BMI 需要这两个数据才能计算。</p>
          <el-button type="primary" @click="router.push('/user/toUpdateInfo')">去填写身高体重</el-button>
        </div>
      </template>
      <div v-else class="muted">加载中…</div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api/client'

const router = useRouter()
const member = ref(null)
const bmi = ref(null)

function bmiTagType(gradeCode) {
  const map = { normal: 'success', over: 'warning', fat: 'danger', thin: 'info' }
  return map[gradeCode] || 'info'
}

// BMI 刻度范围 14~34 映射到百分比定位（左右各留一档便于阅读）
function markerPct(value) {
  const min = 14
  const max = 34
  const v = Math.min(Math.max(Number(value) || 14, min), max)
  return Math.round(((v - min) / (max - min)) * 100)
}

async function load() {
  const resp = await api.get('/api/user/health/overview')
  const data = resp.data || {}
  if (data.success === false) {
    ElMessage.error(data.message || '加载失败')
    return
  }
  member.value = data.member || null
  bmi.value = data.bmi || null
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.health-page {
  padding: 24px;
  max-width: 880px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 18px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

h2,
h3,
p,
ul {
  margin: 0;
}

h2 {
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
}

.page-header p {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.panel {
  margin-bottom: 18px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.bmi-hero {
  display: flex;
  align-items: center;
  gap: 28px;
  flex-wrap: wrap;
}

.bmi-value {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.bmi-value b {
  color: #0d1b2f;
  font-size: 56px;
  font-weight: 800;
  line-height: 1;
}

.bmi-value span {
  color: #718095;
  font-size: 15px;
}

.bmi-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.body-info {
  color: #64748b;
  font-size: 13px;
}

.advice {
  margin-top: 16px;
  padding: 10px 14px;
  border-radius: 10px;
  background: #f8fafc;
  color: #475569;
  font-size: 14px;
  line-height: 1.7;
}

.scale-block {
  margin-top: 22px;
}

.scale-bar {
  position: relative;
  height: 12px;
  border-radius: 999px;
  background: linear-gradient(90deg,
    #cbd5e1 0 22.5%,
    #22c55e 22.5% 55%,
    #f59e0b 55% 72.5%,
    #ef4444 72.5% 100%);
}

.scale-marker {
  position: absolute;
  top: -6px;
  width: 18px;
  height: 18px;
  margin-left: -9px;
  border: 3px solid #ffffff;
  border-radius: 50%;
  background: #0d1b2f;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
}

.scale-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: #94a3b8;
  font-size: 11px;
  gap: 6px;
  flex-wrap: wrap;
}

.t-thin { color: #94a3b8; }
.t-normal { color: #16a34a; }
.t-over { color: #d97706; }
.t-fat { color: #dc2626; }

.panel-title {
  color: #0d1b2f;
  font-size: 17px;
  font-weight: 800;
  margin-bottom: 12px;
}

.info-list {
  padding-left: 18px;
  color: #64748b;
  font-size: 14px;
  line-height: 2;
}

.muted {
  color: #94a3b8;
}

.fill-hint {
  padding: 8px 0;
}

.fill-hint p {
  color: #718095;
  font-size: 14px;
  margin-bottom: 14px;
}
</style>
