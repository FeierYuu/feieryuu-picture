<script setup lang="ts">
import { Waterfall } from 'vue-waterfall-plugin-next'
import router from '@/router'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  searchState: {
    searchText: string
    category: string
    tags: string[]
  }
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false
})


// 跳转详情页方法
const doClickPicture = (picture: API.PictureVO) => {
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


/**
 * Tags处理 防止后端传来的tags格式错误 导致前端展示错误
 * @param tags
 */
const formatTags = (tags: any) => {
  // 处理空值情况
  if (!tags) return []

  // 处理数组类型
  if (Array.isArray(tags)) return tags

  try {
    // 处理JSON字符串格式
    if (typeof tags === 'string' && tags.startsWith('[')) {
      return JSON.parse(tags)
    }

    // 处理普通字符串格式
    return tags.split(/,|、/) // 同时处理中文顿号和英文逗号
  } catch {
    // 所有异常情况返回空数组
    return []
  }
}
</script>

<template>
  <div class="picture-list">
    <!-- 图片列表 - 使用瀑布流组件 -->
    <template v-if="dataList.length > 0">
      <Waterfall
        :list="dataList"
        :width="250"
        :gutter="20"
        :lazyload="true"
        align="center"
      >
        <template #item="{ item: picture }">
          <div class="waterfall-item" @click="doClickPicture(picture)">
            <a-card hoverable>
              <template #cover>
                <!-- 添加标签容器到封面区域 -->
                <div class="image-overlay">
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
.ant-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 瀑布流样式 */
.waterfall-item {
  margin-bottom: 16px;
}


/* 新增样式 */
.tags-container {
  flex-wrap: wrap;
  gap: 4px 8px; /* 行间距4px 列间距8px */
  align-items: center;
}

.flex-tag {
  white-space: normal; /* 允许标签内文字换行 */
  max-width: 200px; /* 设置最大宽度防止单个标签过长 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-flex; /* 保持标签对齐 */
}

/* 瀑布流卡片内调整 */
.waterfall-item :deep(.ant-card-body) {
  padding: 12px;
}

.waterfall-item :deep(.ant-card-meta-description) {
  min-height: 60px; /* 保持卡片高度一致 */
}


/* 新增玻璃效果样式 */
.waterfall-item :deep(.ant-card) {
  background: transparent;
  border: none;
  box-shadow: none;
}

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

.overlay-tags {
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
}

.overlay-tags :deep(.ant-tag-green) {
  background: rgb(83 233 15 / 49%) !important;
  border-color: rgba(47, 115, 9, 0.3) !important;
}

/* 悬停效果 */
.waterfall-item :deep(.ant-card):hover .image-overlay img {
  transform: scale(1.05);
}

/* 移除原卡片内容区域样式 */
.waterfall-item :deep(.ant-card-body) {
  padding: 0 !important;
}

/* 深色背景适配 */
@media (prefers-color-scheme: dark) {
  .overlay-tags :deep(.ant-tag) {
    background: rgba(0, 0, 0, 0.3);
    color: rgba(255, 255, 255, 0.9);
  }
}
</style>
