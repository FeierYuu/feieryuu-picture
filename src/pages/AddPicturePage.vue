<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'
import { useRoute } from 'vue-router'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'

const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditRequest>({})
const uploadType = ref<'file' | 'url'>('file')

/**
 * 图片上传成功 回填图片信息
 * @param newPicture
 */
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}


/**
 * 提交表单
 * @param values
 */
const handlerSubmit = async (values: any) => {
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    ...values
  })
  //上传成功
  if (res.data.code === 0 && res.data.data) {
    message.success('创建成功')
    //跳转到图片详情页
    router.push
    ({
      path: `/picture/${pictureId}`
    })
  } else {
    message.error('创建失败' + res.data.message)
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


const route = useRoute()
//获取老数据
const getOldPicture = async () => {
  const id = route.query.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.tags = data.tags
      pictureForm.category = data.category
      pictureForm.introduction = data.introduction

    }
  }
}
/**
 * 首次进入获取老数据
 */
onMounted(() => {
  getOldPicture()
})

</script>

<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">{{ route.query?.id ? '修改图片' : '创建图片' }}</h2>
    <!--选择上传方式-->
    <a-tabs v-model:activeKey="uploadType">
      <a-tab-pane key="file" tab="文件上传">
        <!--    图片上传组件-->
        <picture-upload :picture="picture" :on-success="onSuccess"></picture-upload>
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL上传" force-render>
        <!--    url 图片上传组件-->
        <UrlPictureUpload :picture="picture" :on-success="onSuccess"></UrlPictureUpload>
      </a-tab-pane>
    </a-tabs>


    <!-- 图片表单-->
    <a-form v-if="picture" name="pictureForm" layout="vertical" :model="pictureForm" @finish="handlerSubmit"
            style="margin-bottom: 20px">
      <a-form-item name="name" label="名称">
        <a-input v-model:value="pictureForm.name" placeholder="请输入图片名称" allow-clear />
      </a-form-item>
      <a-form-item name="introduction" label="简介">
        <a-textarea v-model:value="pictureForm.introduction" placeholder="请输入图片简介"
                    :auto-size="{minRows:2,maxRows:4}" allow-clear />
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-auto-complete :options="categoryOptions" v-model:value="pictureForm.category" placeholder="请输入标签分类"
                         allow-clear></a-auto-complete>
      </a-form-item>


      <a-form-item name="tags" label="标签">
        <a-select :options="tagOptions" v-model:value="pictureForm.tags" mode="tags" placeholder="请输入标签"
                  allow-clear></a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">创建</a-button>
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
