<script setup lang="ts">
import { Waterfall } from 'vue-waterfall-plugin-next'
import { DeleteOutlined, EditOutlined, SearchOutlined } from '@ant-design/icons-vue'
import router from '@/router'
import { deletePictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  searchState?: {
    searchText: string
    category: string
    tags: string[]
  },
  showOp?: boolean,
  onReload?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  searchState: () => ({
    searchText: '',
    category: 'all',
    tags: []
  }),
  showOp: false

})


// 修改后的图片点击方法
const doClickPicture = (picture: API.PictureVO, event: Event) => {
  const target = event.target as HTMLElement
  // 阻止按钮区域的点击触发图片跳转
  if (target.closest('.action-buttons')) return

  try {
    const state = encodeURIComponent(JSON.stringify(props.searchState))
    router.push({
      path: `/picture/${picture.id}`,
      query: { state }
    })
  } catch (e) {
    console.error('跳转失败', e)
    router.push(`/picture/${picture.id}`)
  }
}

// 编辑处理
const handleEdit = (picture: API.PictureVO, e: Event) => {
  e.preventDefault()
  e.stopPropagation()
  router.push({
    path: '/add_picture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId
    }
  })
}

// 删除处理
const handleDelete = async (picture: API.PictureVO, e: Event) => {
  e.preventDefault()
  e.stopPropagation()
  const id = picture.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    props.onReload?.()
  } else {
    message.error('删除失败')
  }
  console.log('删除图片', picture.id)
}

// 以图搜图搜索
const doSearch = (picture, e) => {
  e.stopPropagation()
  window.open(`/search_picture?pictureId=${picture.id}`)
}


/**
 * Tags处理
 * @param tags
 */
const formatTags = (tags: any) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags

  try {
    if (typeof tags === 'string' && tags.startsWith('[')) {
      return JSON.parse(tags)
    }
    return tags.split(/,|、/)
  } catch {
    return []
  }
}
</script>

<template>
  <div class="picture-list">
    <template v-if="dataList.length > 0">
      <Waterfall
        :list="dataList"
        :width="250"
        :gutter="25"
        :lazyload="true"
        align="center"
      >
        <template #item="{ item: picture }">
          <div class="waterfall-item">
            <a-card hoverable @click="doClickPicture(picture, $event)">
              <template #cover>
                <div class="image-overlay">
                  <!-- 操作按钮容器 -->
                  <div class="action-buttons" @click.stop v-if="showOp">
                    <a-tooltip title="寻找相似图片" placement="top">
                      <a-button
                        type="primary"
                        shape="circle"
                        @click="doSearch(picture, $event)"
                        class="edit-btn"
                      >
                        <template #icon>
                          <SearchOutlined />
                        </template>
                      </a-button>
                    </a-tooltip>
                    <a-tooltip title="编辑图片" placement="top">

                      <a-button
                        type="primary"
                        shape="circle"
                        @click="handleEdit(picture, $event)"
                        class="edit-btn"
                      >
                        <template #icon>
                          <EditOutlined />
                        </template>
                      </a-button>
                    </a-tooltip>
                    <a-tooltip title="删除图片" placement="top">

                      <a-button
                        danger
                        type="primary"
                        shape="circle"
                        @click="handleDelete(picture, $event)"
                        class="delete-btn"
                      >
                        <template #icon>
                          <DeleteOutlined />
                        </template>
                      </a-button>
                    </a-tooltip>

                  </div>

                  <!-- 标签区域 -->
                  <a-flex wrap="warp" gap="small" class="tags-container overlay-tags">
                    <a-tag color="green">{{ picture.category || '默认' }}</a-tag>
                    <a-tag v-for="tag in formatTags(picture.tags)" :key="tag" class="flex-tag">
                      {{ tag }}
                    </a-tag>
                  </a-flex>

                  <img :alt="picture.name" :src="picture.url ?? picture.thumbnailUrl" />
                </div>
              </template>
            </a-card>
          </div>
        </template>
      </Waterfall>
    </template>
    <template v-else>
      <a-empty description="暂无图片数据" />
    </template>
  </div>
</template>

<style scoped>
/* 基础样式 */
.waterfall-item {
  margin-bottom: 16px;
  position: relative;
}

/* 图片覆盖层 */
.image-overlay {
  position: relative;
  overflow: hidden;
  border-radius: 12px;
}

.image-overlay img {
  display: block;
  width: 100%;
  height: auto;
  transition: transform 0.3s ease;
}

/* 操作按钮 */
.action-buttons {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 12px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.5));
  opacity: 0;
  transform: translateY(100%);
  transition: all 0.3s ease;
  z-index: 2;
  pointer-events: auto; /* 关键修复 */
}

.waterfall-item:hover .action-buttons {
  opacity: 1;
  transform: translateY(0);
}

.edit-btn, .delete-btn {
  width: 36px;
  height: 36px;
  backdrop-filter: blur(6px);
  background: rgba(255, 255, 255, 0.2) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-btn:hover, .delete-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

/* 标签样式 */
.tags-container {
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  z-index: 1;
  pointer-events: none;
}

.overlay-tags :deep(.ant-tag) {
  backdrop-filter: blur(6px);
  background: rgb(151 148 148 / 20%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 1);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  pointer-events: auto;
  margin: 0;
}

.overlay-tags :deep(.ant-tag-green) {
  background: rgb(83 233 15 / 49%) !important;
  border-color: rgba(47, 115, 9, 0.3) !important;
}

.flex-tag {
  white-space: normal;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-flex;
}

/* 暗色模式适配 */
@media (prefers-color-scheme: dark) {
  .action-buttons {
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  }

  .edit-btn, .delete-btn {
    background: rgba(0, 0, 0, 0.3) !important;
    border-color: rgba(255, 255, 255, 0.2) !important;
  }

  .overlay-tags :deep(.ant-tag) {
    background: rgba(0, 0, 0, 0.3);
    color: rgba(255, 255, 255, 0.9);
  }
}

/* 动画效果 */
.waterfall-item :deep(.ant-card):hover .image-overlay img {
  transform: scale(1.05);
}

/* 移除卡片默认样式 */
.waterfall-item :deep(.ant-card) {
  background: transparent;
  border: none;
  box-shadow: none;
}

.waterfall-item :deep(.ant-card-body) {
  padding: 0 !important;
}

/* 新增关键样式 */
.waterfall-item :deep(.ant-card) {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.waterfall-item :deep(.ant-card):hover {
  transform: translateY(-3px);
}


</style>
