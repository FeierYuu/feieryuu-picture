<script lang="ts" setup>
import { reactive } from 'vue'
import { userLoginUsingPost, userLogoutUsingPost, userRegisterUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

interface FormState {
  username: string
  password: string
  remember: boolean
}

// 用于接收表单输入的值
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
})


const loginUserStore = useLoginUserStore()
/**
 * 提交表单
 * @param values
 */
const handlerSubmit = async (values: any) => {
  //校验两次输入密码是否一致
  if (formState.userPassword !== formState.checkPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  const res = await userRegisterUsingPost(values)
  //注册成功 跳转到登入页面
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功')
    router.push({
      path: '/user/login',
      replace: true
    })
  } else {
    message.error( "注册失败 "+res.data.message)
  }

}


</script>

<template>
  <div id="userRegisterPage">
    <h2 class="title">飞飞鱼云图库 - 用户注册</h2>
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


      <a-form-item
        label="请确认密码"
        name="checkPassword"
        :rules="[{ required: true, message: '请确认密码' },{min:8,message:'确认密码不能小于8位'}]"

      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" />
      </a-form-item>

      <div class="tips">
        已有账号？
        <router-link to="/user/login">去登入</router-link>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%;margin-left: 15px">注册</a-button>
      </a-form-item>

    </a-form>
  </div>
</template>

<style scoped>
#userRegisterPage {
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
