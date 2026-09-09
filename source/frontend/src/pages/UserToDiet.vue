<template>
  <div class="diet-page">
    <div class="page-header">
      <div>
        <h2>饮食推荐</h2>
        <p>按你的 BMI 自动匹配一整天怎么吃（仅供健康参考）</p>
      </div>
      <div class="header-actions">
        <el-button @click="router.push('/user/toHealth')">健康评估</el-button>
        <el-button @click="router.push('/user/toCheckin')">运动打卡</el-button>
        <el-button @click="router.push('/toUserMain')">返回主页</el-button>
      </div>
    </div>

    <template v-if="diet">
      <el-card class="panel goal-panel" shadow="never">
        <div class="goal-main">
          <div>
            <div class="goal-tag">
              <el-tag type="primary" effect="dark" size="large">{{ bmi?.label }} · {{ diet.target }}</el-tag>
            </div>
            <p class="goal-desc">按体重估算的一天参考热量</p>
          </div>
          <div class="kcal-box">
            <b>{{ diet.dailyKcal }}</b>
            <span>千卡 / 天</span>
          </div>
        </div>
        <p class="muted">若想更准可结合运动量在 ±10% 内浮动；减脂建议比参考值低一点、增肌高一点。</p>
      </el-card>

      <el-card class="panel" shadow="never">
        <h3 class="panel-title">一日三餐（含加餐）</h3>
        <div class="meals">
          <div v-for="meal in diet.meals" :key="meal.name" class="meal">
            <div class="meal-name">{{ meal.name }}</div>
            <div class="meal-foods">{{ meal.foods }}</div>
            <el-tag size="small" type="info" effect="plain">{{ meal.kcal }}</el-tag>
          </div>
        </div>
        <div class="sum-line">以上合计约 {{ diet.dailyKcal }} 千卡，供参考</div>
      </el-card>

      <el-card class="panel" shadow="never">
        <div class="tip-block">
          <div class="tip-title">建议忌口</div>
          <div class="tag-row">
            <el-tag v-for="a in diet.avoid" :key="a" type="danger" effect="light" size="small">{{ a }}</el-tag>
          </div>
        </div>
        <el-divider />
        <div class="tip-block">
          <div class="tip-title">健康小贴士</div>
          <ul class="info-list">
            <li v-for="t in diet.tips" :key="t">{{ t }}</li>
          </ul>
          <p class="muted">每天记得喝够 1.5~2L 水；正餐间隔 3~4 小时，别让“饿过头”导致暴食。</p>
        </div>
      </el-card>
    </template>

    <el-card v-else class="panel" shadow="never">
      <div v-if="member" class="fill-hint">
        <p>需要先有身高体重才能算 BMI，才能按你的体型给饮食建议。</p>
        <el-button type="primary" @click="router.push('/user/toUpdateInfo')">去填写身高体重</el-button>
      </div>
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
const diet = ref(null)

async function load() {
  const resp = await api.get('/api/user/health/overview')
  const data = resp.data || {}
  if (data.success === false) {
    ElMessage.error(data.message || '加载失败')
    return
  }
  member.value = data.member || null
  bmi.value = data.bmi || null
  diet.value = data.diet || null
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.diet-page {
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

.goal-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}

.goal-tag {
  margin-bottom: 8px;
}

.goal-desc {
  color: #718095;
  font-size: 13px;
}

.kcal-box {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.kcal-box b {
  color: #0369a1;
  font-size: 44px;
  font-weight: 800;
  line-height: 1;
}

.kcal-box span {
  color: #718095;
  font-size: 13px;
}

.panel-title {
  color: #0d1b2f;
  font-size: 17px;
  font-weight: 800;
  margin-bottom: 14px;
}

.meals {
  display: grid;
  gap: 10px;
}

.meal {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fafc;
}

.meal-name {
  width: 52px;
  color: #0d1b2f;
  font-weight: 700;
  flex: none;
}

.meal-foods {
  flex: 1;
  color: #475569;
  font-size: 14px;
  line-height: 1.6;
}

.sum-line {
  margin-top: 10px;
  color: #94a3b8;
  font-size: 12px;
  text-align: right;
}

.tip-title {
  color: #0d1b2f;
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 10px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.info-list {
  padding-left: 18px;
  color: #64748b;
  font-size: 14px;
  line-height: 2;
  margin-bottom: 10px;
}

.muted {
  color: #94a3b8;
  font-size: 13px;
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
