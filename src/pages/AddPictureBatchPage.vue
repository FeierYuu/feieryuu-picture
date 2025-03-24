<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  uploadPictureByBatchUsingPost
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10
})
const loading = ref(false)


/**
 * 提交表单
 * @param values
 */
const handlerSubmit = async (values: any) => {
  loading.value = true
  try {
    const res = await uploadPictureByBatchUsingPost({
      ...formData
    })
    //上传成功
    if (res.data.code === 0 && res.data.data) {
      message.success(`创建成功,共 ${res.data.data} 条`)
      //跳转到主页
      router.push
      ({
        path: '/'
      })
    } else {
      message.error('创建失败' + res.data.message)
    }
    loading.value = false
  } catch (e) {
    message.error('网络环境异常,抓取停止')
  } finally {
    loading.value = false
  }

}


const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

/**
 * 获取标签和分类选项
 *
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data
      }
    })
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data
      }
    })
  } else {
    message.error('获取标签和分类选项失败' + res.data.message)
  }
}
/**
 * 首次进入页面时获取标签和分类选项
 */
onMounted(() => {
  getTagCategoryOptions()
})


</script>

<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px">
      批量创建
    </h2>


    <!-- 图片表单-->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handlerSubmit"
            style="margin-bottom: 20px">
      <a-form-item name="searchText" label="关键词">
        <a-input v-model:value="formData.searchText" placeholder="请输入关键词" allow-clear />
      </a-form-item>
      <a-form-item name="count" label="抓取数量">
        <a-input-number v-model:value="formData.count" placeholder="请输入要抓取的数量" style="min-width: 180px"
                        :min="1"
                        :max="30" allow-clear />
      </a-form-item>
      <a-form-item name="namePrefix" label="名称前缀">
        <a-auto-complete v-model:value="formData.namePrefix" placeholder="请输入名称前缀,会自动补充序号"
                         allow-clear></a-auto-complete>
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-auto-complete :options="categoryOptions" v-model:value="formData.category" placeholder="请输入标签分类"
                         allow-clear></a-auto-complete>
      </a-form-item>


      <a-form-item name="tags" label="标签">
        <a-select :options="tagOptions" v-model:value="formData.tags" mode="tags" placeholder="请输入标签"
                  allow-clear></a-select>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">执行抓取任务</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
