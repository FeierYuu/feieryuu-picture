<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { HomeOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { UserOutlined } from '@ant-design/icons-vue'
import { userLogoutUsingPost } from '@/api/userController.ts'

//菜单列表
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页'
  },
  {
    key: '/admin/userManager',
    label: '用户管理',
    title: '用户管理'
  },
  {
    key: '/admin/pictureManager',
    label: '图片管理',
    title: '图片管理'
  },
  {
    key: '/admin/spaceManager',
    label: '空间管理',
    title: '空间管理'
  },
  {
    key: '/add_picture',
    label: '创建图片',
    title: '创建图片'
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/FeierYuu', target: '_blank' }, 'Github'),
    title: '我的GitHub'
  }
]

//过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    //只有管理员 才能看到 /admin 路径下的页面
    if (menu?.key.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole != 'admin') {
        return false
      }
    }
    return true
  })
}

//经过权限过滤后的菜单项
const items = computed<MenuProps['items']>(() => filterMenus(originItems))

const router = useRouter()
// 路由跳转
const doMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}

// 标题高亮
const current = ref<string[]>(['home'])
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()


const doLogOut = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登入'
    })
    message.success('登出成功')
    router.push({
      path: '/user/login',
      replace: true
    })
  } else {
    message.error('退出失败 ' + res.data.message)
  }

}
</script>

<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="https://chat.deepseek.com/favicon.svg" alt="logo" />
            <div class="title">飞飞鱼云图库</div>
          </div>
        </router-link>
      </a-col
      >
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>

              <a-space>
                <a-avatar
                  size="large"
                  class="user-Avatar"
                  :src="loginUserStore.loginUser.userAvatar"
                >
                  <template #icon>
                    <UserOutlined />
                  </template>
                </a-avatar>
                {{ loginUserStore.loginUser.userName }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item>
                    <router-link to="/my_space">
                      <user-outlined />
                      我的空间
                    </router-link>
                  </a-menu-item>
                  <a-menu-item @click="doLogOut">
                    <LogoutOutlined />
                    退出登入
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>

          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登入</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
#globalHeader .title-bar {
  display: flex;
  align-items: center;
}

.logo {
  width: 48px;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 8px;
}

.user-Avatar {
  vertical-align: middle;
  margin-right: 5px;
}
</style>
