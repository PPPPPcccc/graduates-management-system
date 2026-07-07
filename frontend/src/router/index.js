// router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
    {
      path: '/app',
      component: () => import('@/layout/MainLayout.vue'),
      meta: { requiresAuth: true },
      redirect: '/app/graduate',
      children: [
        // 员工 + 管理员 都看这个
        {
          path: 'graduate',
          name: 'graduate',
          component: () => import('@/views/GraduateList.vue'),
          meta: { title: '高校毕业生信息管理', icon: 'TeamOutlined' }
        },
        // 仅管理员
        {
          path: 'account',
          name: 'account',
          component: () => import('@/views/AccountManage.vue'),
          meta: { title: '账号管理', icon: 'UserOutlined', requiresAdmin: true }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('loggedIn') === 'true'
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !isLoggedIn) {
    return next('/login')
  }
  // 仅管理员可访问
  if (to.meta.requiresAdmin && role !== 'admin') {
    return next('/app/graduate')
  }
  next()
})

export default router