import { createRouter, createWebHashHistory } from 'vue-router'
import api from '../api/client'

const routes = [
  { path: '/', component: () => import('../pages/AdminLogin.vue') },
  { path: '/toUserLogin', component: () => import('../pages/UserLogin.vue') },

  { path: '/toAdminMain', component: () => import('../pages/AdminMain.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/toUserMain', component: () => import('../pages/UserMain.vue'), meta: { requiresAuth: true, role: 'user' } },

  { path: '/member/selMember', component: () => import('../pages/MemberSelMember.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/toAddMember', component: () => import('../pages/MemberToAddMember.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/toUpdateMember', component: () => import('../pages/MemberToUpdateMember.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/toSelByCard', component: () => import('../pages/MemberToSelByCard.vue'), meta: { requiresAuth: true, role: 'admin' } },

  { path: '/employee/selEmployee', component: () => import('../pages/EmployeeSelEmployee.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/employee/toAddEmployee', component: () => import('../pages/EmployeeToAddEmployee.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/employee/toUpdateEmployee', component: () => import('../pages/EmployeeToUpdateEmployee.vue'), meta: { requiresAuth: true, role: 'admin' } },

  { path: '/equipment/selEquipment', component: () => import('../pages/EquipmentSelEquipment.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/equipment/toAddEquipment', component: () => import('../pages/EquipmentToAddEquipment.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/equipment/toUpdateEquipment', component: () => import('../pages/EquipmentToUpdateEquipment.vue'), meta: { requiresAuth: true, role: 'admin' } },

  { path: '/class/selClass', component: () => import('../pages/ClassSelClass.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/class/toAddClass', component: () => import('../pages/ClassToAddClass.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/class/selClassRecord', component: () => import('../pages/ClassSelClassRecord.vue'), meta: { requiresAuth: true, role: 'admin' } },
  { path: '/class/analysis', component: () => import('../pages/ClassAnalysis.vue'), meta: { requiresAuth: true, role: 'admin' } },

  { path: '/user/toUserInfo', component: () => import('../pages/UserToUserInfo.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toUpdateInfo', component: () => import('../pages/UserToUpdateInfo.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toUserClass', component: () => import('../pages/UserToUserClass.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toApplyClass', component: () => import('../pages/UserToApplyClass.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toHealth', component: () => import('../pages/UserToHealth.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toCheckin', component: () => import('../pages/UserToCheckin.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toDiet', component: () => import('../pages/UserToDiet.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toChat', component: () => import('../pages/UserToChat.vue'), meta: { requiresAuth: true, role: 'user' } },

  { path: '/:pathMatch(.*)*', component: () => import('../pages/NotImplemented.vue') }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

let verifiedRole: string | null = null

router.beforeEach(async (to) => {
  const meta = to.meta as any
  if (!meta?.requiresAuth) {
    verifiedRole = null
    return true
  }

  const role = meta.role as string | undefined
  if (verifiedRole === role) return true
  try {
    if (role === 'user') {
      await api.get('/api/toUserMain')
    } else {
      await api.get('/api/toAdminMain')
    }
    verifiedRole = role || null
    return true
  } catch (e) {
    verifiedRole = null
    // 未登录/会话失效：按角色回登录页
    return { path: role === 'user' ? '/toUserLogin' : '/' }
  }
})

export default router
