<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  addSpaceUsingPost, getSpaceVoByIdUsingGet, listSpaceLevelUsingGet, updateSpaceUsingPost
} from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'
import { useRoute } from 'vue-router'
import { SPACE_LEVEL_OPTIONS } from '@/constants/space.ts'
import { formatSize } from '../utils'

const space = ref<API.SpaceVO>()
const spaceForm = reactive<API.SpaceAddRequest | API.SpaceEditRequest>({})
const loading = ref(false)

const spaceLevelList = ref<API.SpaceLevel[]>({})

const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('获取空间级别失败' + res.data.message)
  }
}

/**
 * 提交表单
 * @param values
 */
const handlerSubmit = async (values: any) => {
  loading.value = true
  const spaceId = space.value?.id
  try {
    let res
    //有spaceId 就是更新
    if (spaceId) {
      res = await updateSpaceUsingPost({
        id: spaceId,
        ...spaceForm
      })
      if (res.data.code === 0 && res.data.data) {
        message.success('更新成功')
        //跳转首页
        router.push
        ({
          path: '/'
        })
      }
    } else {
      //否则就是创建
      res = await addSpaceUsingPost({
        ...spaceForm
      })
      if (res.data.code === 0 && res.data.data) {
        message.success('创建成功')
        //跳转到空间详情页
        router.push
        ({
          path: `/space/${res.data.data}`
        })
      }
    }
    if (res.data.code !== 0) {
      message.error('操作失败：' + (res.data.message || '未知错误'))
    }
  } catch (error) {
    message.error('系统错误' + (error.message))
  } finally {
    loading.value = false
  }
}


const route = useRoute()
//获取老数据
const getOldSpace = async () => {
  const id = route.query.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      space.value = data
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    }
  }
}
/**
 * 首次进入获取老数据
 */
onMounted(() => {
  getOldSpace()
  fetchSpaceLevelList()
})

</script>

<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">{{ route.query?.id ? '修改空间' : '创建空间' }}</h2>

    <!-- 空间表单-->
    <a-form name="spaceForm" layout="vertical" :model="spaceForm" @finish="handlerSubmit"
            style="margin-bottom: 20px">
      <a-form-item name="name" label="空间名称">
        <a-input v-model:value="spaceForm.spaceName" placeholder="请输入空间名称" allow-clear />
      </a-form-item>
      <a-form-item name="reviewStatus" label="空间级别">
        <a-select
          style="min-width: 180px"
          v-model:value="spaceForm.spaceLevel"
          placeholder="请选择空间级别"
          :options="SPACE_LEVEL_OPTIONS"
          allow-clear
        ></a-select>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" :loading="loading" html-type="submit" style="width: 100%">提交</a-button>
      </a-form-item>
    </a-form>
    <!--    空间级别介绍-->
    <a-card title="空间级别介绍">
      <a-typography-paragraph>
        * 目前仅支持开通普通版本 如需开通更高级版本，请联系管理员
        <a href="https://github.com/FeierYuu" target="_blank">程序员FeierYuu</a>
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList">
        {{ spaceLevel.text }}:大小 {{ formatSize(spaceLevel.maxSize) }}，数量 {{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>
  </div>
</template>

<style scoped>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
