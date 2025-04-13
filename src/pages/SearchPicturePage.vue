<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getPictureVoByIdUsingGet, searchPictureByPictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { DownloadOutlined } from '@ant-design/icons-vue'
import { downloadImage } from '@/utils'

const route = useRoute()
const loading = ref(true)

// 图片 id
const pictureId = computed(() => {
  return route.query?.pictureId
})

const picture = ref<API.PictureVO>({})

// 获取原图数据
const getOldPicture = async () => {
  const id = route.query?.pictureId
  if (id) {
    const res = await getPictureVoByIdUsingGet({ id })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    }
  }
}

const dataList = ref<API.ImageSearchResult[]>([])

// 获取搜索结果
const fetchData = async () => {
  try {
    loading.value = true
    const res = await searchPictureByPictureUsingPost({
      pictureId: pictureId.value
    })
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data ?? []
    } else {
      message.error('获取数据失败：' + (res.data.message || '未知错误'))
      dataList.value = []
    }
  } catch (e) {
    message.error('请求失败：' + (e as Error).message)
    dataList.value = []
  } finally {
    loading.value = false
  }
}

// 下载处理
const handleDownload = (item: API.ImageSearchResult, event: MouseEvent) => {
  event.preventDefault()
  event.stopPropagation()

  try {
    // 优先级：originUrl > thumbUrl
    const imageUrl = item.originUrl || item.thumbUrl
    const fileName = item.title
      ? `${item.title}.${imageUrl.split('.').pop()}` // 保留原后缀
      : `image_${Date.now()}_${Math.random().toString(36).slice(2, 7)}` // 随机文件名

    if (!imageUrl) {
      message.warning('该图片暂无下载地址')
      return
    }

    downloadImage(imageUrl, fileName)
  } catch (e) {
    message.error('下载失败：' + (e as Error).message)
    console.error('下载错误详情：', e)
  }
}

// 生命周期
onMounted(async () => {
  try {
    await getOldPicture()
    await fetchData()
  } catch (e) {
    loading.value = false
  }
})
</script>

<template>
  <div id="searchPicturePage">
    <h2 style="margin-bottom: 16px">寻找相似图片</h2>

    <h3 style="margin: 16px 0">原图</h3>
    <a-card style="width: 240px">
      <template #cover>
        <a-image
          style="height: 180px; object-fit: cover"
          :alt="picture.name"
          :src="picture.thumbnailUrl ?? picture.url"
        />
      </template>
    </a-card>

    <h3 style="margin: 16px 0">识图结果</h3>

    <a-spin :spinning="loading" tip="正在搜索相似图片...">
      <a-list
        :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
        :data-source="dataList"
      >
        <template #renderItem="{ item }">
          <a-list-item style="padding: 0">
            <div class="image-card-container">
              <a :href="item.fromUrl" target="_blank">
                <a-card>
                  <template #cover>
                    <div class="image-wrapper">
                      <div class="download-overlay">
                        <a-tooltip title="下载图片" placement="top">
                          <a-button
                            type="primary"
                            shape="circle"
                            @click.stop="handleDownload(item, $event)"
                            class="download-btn"
                          >
                            <template #icon>
                              <DownloadOutlined />
                            </template>
                          </a-button>
                        </a-tooltip>
                      </div>
                      <img
                        style="height: 180px; object-fit: cover"
                        :src="item.thumbUrl"
                        :alt="item.title"
                      >
                    </div>
                  </template>
                </a-card>
              </a>
            </div>
          </a-list-item>
        </template>

        <template v-if="!loading && dataList.length === 0">
          <a-empty description="未找到相似图片" />
        </template>
      </a-list>
    </a-spin>
  </div>
</template>

<style scoped>
/* 加载动画样式 */
:deep(.ant-spin-nested-loading) {
  min-height: 200px;
}

:deep(.ant-spin-blur) {
  opacity: 0.5;
}

/* 图片卡片容器 */
.image-card-container {
  position: relative;
  transition: transform 0.2s ease;
  margin: 8px;
}

.image-card-container:hover {
  transform: translateY(-4px);
}

/* 图片包装器 */
.image-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
}

/* 下载覆盖层 */
.download-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 2;
}

.image-wrapper:hover .download-overlay {
  opacity: 1;
}

/* 下载按钮样式 */
.download-btn {
  width: 40px;
  height: 40px;
  background: rgb(57 54 54 / 90%) !important;
  border: none !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.2s ease;
}

.download-btn:hover {
  background: rgb(57 54 54 / 90%) !important;
  transform: scale(1.1);
}

/* 暗色模式适配 */
@media (prefers-color-scheme: dark) {
  .download-overlay {
    background: rgba(0, 0, 0, 0.6);
  }

  .download-btn {
    background: rgba(0, 0, 0, 0.7) !important;
    color: white !important;
  }

  .download-btn:hover {
    background: rgba(0, 0, 0, 0.9) !important;
  }
}

/* 卡片样式 */
:deep(.ant-card) {
  border-radius: 8px !important;
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

:deep(.ant-card-body) {
  padding: 0 !important;
}

/* 空状态样式 */
:deep(.ant-empty) {
  margin: 40px 0;
}
</style>
