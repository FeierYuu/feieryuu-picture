import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import userLoginPage from '../pages/user/userLoginPage.vue'
import userRegistPage from '../pages/user/userRegisterPage.vue'
import UserManagerPage from '../pages/admin/UserManagerPage.vue'
import PictureManager from '../pages/admin/PictureManagerPage.vue'
import AddPicturePage from '@/pages/AddPicturePage.vue'
import PictureDetailPage from '@/pages/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/AddPictureBatchPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage
    },
    {
      path: '/user/login',
      name: '用户登入',
      component: userLoginPage
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: userRegistPage
    },
    {
      path: '/admin/userManager',
      name: '用户管理',
      component: UserManagerPage
    },
    {
      path: '/admin/pictureManager',
      name: '图片',
      component: PictureManager
    },
    {
      path: '/add_picture',
      name: '创建图片',
      component: AddPicturePage
    },
    {
      path: '/add_picture/batch',
      name: '批量创建图片',
      component: AddPictureBatchPage
    },
    {
      path: '/picture/:id',
      name: '图片详情',
      component: PictureDetailPage,
      props: true
    }
  ]
})

export default router
