import { getLoginUserUsingGet } from '@/api/userController'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.UserLoginVo>({
    userName: '未登录',
  })

  //远程获取登录用户信息
  async function fetchLoginUser() {
    // todo 由于后端还没提供接口，暂时注释
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
    //测试 三秒后自动登入
    // setTimeout(() => {
    //   loginUser.value = {
    //     id: 1,
    //     userName: '测试用户',
    //   }
    // }, 3000)
  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, fetchLoginUser }
})
