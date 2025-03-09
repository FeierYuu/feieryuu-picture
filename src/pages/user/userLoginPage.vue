<script lang="ts" setup>
import { reactive } from 'vue'
import { userLoginUsingPost, userLogoutUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

interface FormState {
  username: string
  password: string
  remember: boolean
}

// 用于接收表单输入的值
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: ''
})


const loginUserStore = useLoginUserStore()
/**
 * 提交表单
 * @param values
 */
const handlerSubmit = async (values: any) => {
  try {
    const res = await userLoginUsingPost(values)
    //登入成功 把登入态保存在全局状态中
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登入成功')
      router.push({
        path: '/',
        replace: true
      })
    } else {
      message.error( res.data.message)
    }
  } catch (e) {
    message.error(' 登入失败 '+e.message)
  }


}


</script>

<template>
  <div id="userLoginPage">
    <h2 class="title">飞飞鱼云图库 - 用户登入</h2>
    <div class="desc">企业级智能协同云图库</div>
    <a-form
      :model="formState"
      name="basic"
      @finish="handlerSubmit"

    >
      <a-form-item
        label="用户账号"
        name="userAccount"
        :rules="[{ required: true, message: '请输入账号' }]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <a-form-item
        label="用户密码"
        name="userPassword"
        :rules="[{ required: true, message: '请输入密码' },{min:8,message:'密码不能小于8位'}]"

      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>

      <div class="tips">
        没有账号？
        <router-link to="/user/register">立即注册</router-link>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%;margin-left: 15px">登入</a-button>
      </a-form-item>

    </a-form>
  </div>
</template>

<style scoped>
#userLoginPage {
  max-width: 360px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;

}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  text-align: right;
  font-size: 13px;
  color: #bbb;
  margin-bottom: 16px;
}

</style>
