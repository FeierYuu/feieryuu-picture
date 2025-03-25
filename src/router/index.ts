import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import userLoginPage from '../pages/user/userLoginPage.vue'
import userRegistPage from '../pages/user/userRegisterPage.vue'
import UserManagerPage from '../pages/admin/UserManagerPage.vue'
import PictureManager from '../pages/admin/PictureManagePage.vue'
import AddPicturePage from '@/pages/AddPicturePage.vue'
import PictureDetailPage from '@/pages/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/AddPictureBatchPage.vue'
import spaceManagerPage from '@/pages/admin/SpaceManagePage.vue'
import spaceManagePage from '@/pages/admin/SpaceManagePage.vue'
import AddSpacePage from '@/pages/AddSpacePage.vue'
import MySpacePage from '@/pages/MySpacePage.vue'
import SpaceDetailPage from '@/pages/SpaceDetailPage.vue'

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
      path: '/admin/spaceManager',
      name: '空间管理',
      component: spaceManagePage
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
      path: '/add_space',
      name: '创建空间',
      component: AddSpacePage
    },
    {
      path: '/my_space',
      name: '我的空间',
      component: MySpacePage
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
    },
    {
      path: '/space/:id',
      name: '空间详情',
      component: SpaceDetailPage,
      props: true
    }
  ]
})

export default router
