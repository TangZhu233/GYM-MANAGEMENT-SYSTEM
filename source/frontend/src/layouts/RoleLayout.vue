<template>
  <el-container class="role-layout">
    <el-aside width="240px" class="role-aside">
      <div class="role-brand">
        <div class="role-title">健身房管理系统</div>
        <div class="role-subtitle" :style="{ color: subtitle.color }">
          <span class="biz-dot" :style="{ background: subtitle.color }"></span>{{ subtitle.text }}
        </div>
      </div>

      <el-menu
        :router="true"
        :default-active="activePath"
        class="role-menu"
        background-color="transparent"
        text-color="#c8d3e2"
        active-text-color="#ffffff"
      >
        <template v-if="role === 'admin'">
          <el-menu-item index="/toAdminMain">管理员主页</el-menu-item>
          <el-menu-item index="/member/toSelByCard">会员卡查询</el-menu-item>
          <el-menu-item index="/member/selMember">会员管理</el-menu-item>
          <el-menu-item index="/employee/selEmployee">员工管理</el-menu-item>
          <el-menu-item index="/equipment/selEquipment">器材管理</el-menu-item>
          <el-menu-item index="/class/selClass">课程管理</el-menu-item>
          <el-menu-item index="/class/analysis">课程运营分析</el-menu-item>
        </template>

        <template v-else>
          <el-menu-item index="/toUserMain">会员主页</el-menu-item>
          <el-menu-item index="/user/toUserInfo">个人信息</el-menu-item>

          <el-sub-menu index="user-health">
            <template #title>健康管理</template>
            <el-menu-item index="/user/toHealth">健康评估（BMI）</el-menu-item>
            <el-menu-item index="/user/toCheckin">运动打卡</el-menu-item>
            <el-menu-item index="/user/toDiet">饮食推荐</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/user/toChat">健康助手（AI）</el-menu-item>

          <el-sub-menu index="user-course">
            <template #title>课程管理</template>
            <el-menu-item index="/user/toApplyClass">报名选课</el-menu-item>
            <el-menu-item index="/user/toUserClass">我的课程</el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>

      <div class="role-footer">
        <div class="role-footer-title">{{ role === 'admin' ? '管理员' : '会员' }}</div>
        <div class="role-footer-text">{{ role === 'admin' ? '今日运营数据已更新' : memberWelcome }}</div>
      </div>
    </el-aside>

    <el-main class="role-main">
      <slot />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api/client'
import { businessStatus, refreshBusinessStatus } from '../state/business'

type Role = 'admin' | 'user'

const props = defineProps<{
  role: Role
}>()

const route = useRoute()
const activePath = computed(() => route.path)

const memberName = ref('')

// 营业状态：绿=营业中 / 红=已打烊（红绿语义同 el-tag success/danger）
const subtitle = computed(() => {
  const st = businessStatus.value
  if (st === 'closed') return { text: '已打烊', color: '#f87171' }
  if (st === 'open') return { text: '营业中', color: '#4ade80' }
  return { text: '营业状态…', color: '#91a3b7' }
})

const memberWelcome = computed(() =>
  memberName.value ? `${memberName.value}，欢迎回来` : '欢迎回来'
)

async function loadStatus() {
  await refreshBusinessStatus()
}

async function loadMemberName() {
  if (props.role !== 'user') {
    memberName.value = ''
    return
  }
  try {
    const resp = await api.get('/api/toUserMain')
    memberName.value = resp.data?.member?.memberName || ''
  } catch (e) {
    memberName.value = ''
  }
}

watch(() => props.role, () => {
  memberName.value = ''
  loadStatus()
  loadMemberName()
})

onMounted(() => {
  loadStatus()
  loadMemberName()
})
</script>

<style scoped>
.role-layout {
  min-height: 100svh;
  width: 100%;
  min-width: 0;
  align-items: flex-start;
  background:
    linear-gradient(180deg, rgba(16, 185, 129, 0.07), transparent 280px),
    #edf5f0;
}

.role-aside {
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  flex: 0 0 240px;
  box-sizing: border-box;
  padding: 24px 14px 16px;
  background:
    radial-gradient(circle at 20% 0%, rgba(30, 180, 120, 0.18), transparent 34%),
    linear-gradient(180deg, #0b2318, #061410);
  color: #c8d3e2;
  box-shadow: 8px 0 24px rgba(31, 45, 64, 0.14);
}

.role-brand {
  padding: 0 10px 22px;
}

.role-title {
  font-size: 21px;
  font-weight: 800;
  line-height: 1.25;
  color: #ffffff;
}

.role-subtitle {
  margin-top: 8px;
  color: #91a3b7;
  font-size: 12px;
}

.biz-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  margin-right: 6px;
  border-radius: 50%;
  vertical-align: middle;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.08);
}

.role-menu {
  border-right: none;
  flex: 1;
  padding: 0 12px;
  background: transparent;
}

.role-menu .el-menu-item {
  height: 42px;
  margin-bottom: 6px;
  padding-left: 13px !important;
  border-radius: 9px;
  font-size: 15px;
  transition: background 0.2s ease, color 0.2s ease;
}
.role-menu .el-sub-menu .el-sub-menu__title {
  height: 42px;
  padding-left: 13px !important;
  border-radius: 9px;
  font-size: 15px;
}

.role-menu :deep(.el-menu-item:hover),
.role-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.role-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.12);
  box-shadow: inset 3px 0 0 #10b981;
  color: #ffffff;
  font-weight: 700;
}

.role-footer {
  margin-top: auto;
  padding: 14px 13px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.07);
  color: #ffffff;
}

.role-footer-title {
  font-size: 14px;
  font-weight: 700;
}

.role-footer-text {
  margin-top: 6px;
  color: #9fb0c5;
  font-size: 13px;
}

.role-main {
  min-width: 0;
  padding: 24px 32px;
  overflow: auto;
  background:
    linear-gradient(180deg, rgba(16, 185, 129, 0.07), transparent 280px),
    #edf5f0;
}
</style>
