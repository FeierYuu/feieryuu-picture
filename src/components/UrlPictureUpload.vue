<template>
  <div class="url-Picture-upload">
    <a-input-group compact>
      <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="请输入图片地址" />
      <a-button type="primary" style="width: 120px" :loading="loading" @click="handleUpload">Submit</a-button>
    </a-input-group>
    <div class="img-wrapper">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />

    </div>
  </div>


</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { UploadChangeParam, UploadProps } from 'ant-design-vue'
import { uploadPictureByUrlUsingPost, uploadPictureUsingPost } from '@/api/pictureController.ts'


interface Props {
  picture?: API.PictureVO;
  onSuccess?: (picture: API.PictureVO) => void;
  spaceId?: number;
}

const props = defineProps<Props>()
const loading = ref<boolean>(false)

const fileUrl = ref<string>()
/**
 * 上传文件
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    params.spaceId = props.spaceId
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('上传成功')
      //把上传成功的信息 传递给父组件
      props.onSuccess?.(res.data.data)
    } else {
      message.error('上传失败' + res.data.message)
    }

  } catch (e) {
    console.log('图片上传失败' + e.message)
  }
  loading.value = false
}
</script>
<style scoped>
.url-Picture-upload :deep(.ant-upload) {
  margin-bottom: 16px;
}

.url-Picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

.url-Picture-upload .img-wrapper {
  text-align: center;
  margin-top: 16px;
}

</style>
