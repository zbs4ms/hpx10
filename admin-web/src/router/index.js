import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)

/* 各模块路由配置项 */
// banner
import bannerRouters from '@/components/banner/router'
// 医生信息
import doctorRouters from '@/components/doctor/router'
// 权限管理
import authRouters from '@/components/auth/router'
// 日记管理
import logRouters from '@/components/log/router'

// 登陆
const Login = resolve => require(['@/components/login/Login'], resolve)
// 404
const NotFound = resolve => require(['@/components/notFound/NotFound'], resolve)

const router = new Router({
  routes: [
    {
      path: '/',
      name: 'Root',
      redirect: '/banner'
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    ...bannerRouters,
    ...doctorRouters,
    ...authRouters,
    ...logRouters,
    {
      path: '*',
      name: 'NotFound',
      component: NotFound
    }
  ]
})

// 全局路由验证登陆状态
import { Cookie } from '@/utils/index'
router.beforeEach((to, from, next) => {
  if (to.name !== 'Login' && !Cookie.get('login')) {
    next({ name: 'Login' })
  } else if (to.name === 'Login' && Cookie.get('login') === 'yes') {
    next(from.path || '/')
  } else {
    next()
  }
})

export default router
