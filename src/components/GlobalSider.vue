<script lang="ts" setup>
import { h, ref } from 'vue'
import { PictureOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'


const loginStore = useLoginUserStore()
//侧边菜单列表
const menuItems = [
  {
    key: '/',
    icon: () => h(PictureOutlined),
    label: '公共图库',
    title: '公共图库'
  },
  {
    key: '/my_space',
    label: '我的空间',
    title: '我的空间',
    icon: () => h(UserOutlined)
  }
]


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

</script>

<template>
  <div id="globalSider">
    <a-layout-sider v-if="loginStore.loginUser.id" class="sider" width="200" breakpoint="lg" collapsed-width="0">
      <a-menu
        mode="inline"
        v-model:selectedKeys="current"
        :items="menuItems"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>

</template>

<style scoped>
#globalSider .title-bar {
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
