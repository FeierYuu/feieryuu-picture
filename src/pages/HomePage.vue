<script setup lang="ts">
import { type Component, computed, onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
  listPictureVoByPageWithCacheUsingPost
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { Waterfall } from 'vue-waterfall-plugin-next' //使用瀑布流组件
import 'vue-waterfall-plugin-next/dist/style.css'

// 定义数据
const dataList = ref<API.Picture[]>([])
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend'
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 12,
    total: total.value,
    onChange: (page: number, pageSize: number) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    }
  }
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  const params = {
    ...searchParams,
    tags: [] as string[]
  }
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  selectedTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags?.push(tagList.value[index].name)
    }
  })
  const res = await listPictureVoByPageWithCacheUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0

  } else {
    message.error('获取数据失败' + res.data.message)
  }
  loading.value = false
}

// 加载更多数据
const loadMore = () => {
  if (dataList.value.length < total.value) {
    searchParams.current++
    fetchData()
  }
}

// 搜索
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})

// 获取标签和分类列表
const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')

interface TagItem {
  name: string;
  icon: Component;
}

const tagList = ref<TagItem[]>([])
const selectedTagList = ref<boolean[]>([])

const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    categoryList.value = res.data.data.categoryList ?? []
    tagList.value = (res.data.data.tagList ?? []).map((tag, index) => {
      const icons = [
        'src/assets/icon/hot.png',
        'src/assets/icon/smile.png',
        'src/assets/icon/life.png',
        'src/assets/icon/HD.png',
        'src/assets/icon/art.png',
        'src/assets/icon/school.png',
        'src/assets/icon/background.png',
        'src/assets/icon/resume.png',
        'src/assets/icon/creativity.png'
      ]
      return {
        name: tag,
        icon: icons[index % icons.length]
      }
    })
    selectedTagList.value = new Array(tagList.value.length).fill(false)
  } else {
    message.error('获取标签和分类列表失败' + res.data.message)
  }
}

// 首次进入页面时获取标签和分类选项
onMounted(() => {
  // 从URL参数恢复搜索状态
  if (route.query.fromSearch) {
    try {
      const savedSearch = JSON.parse(decodeURIComponent(route.query.fromSearch as string))
      searchParams.searchText = savedSearch.searchText
    } catch (e) {
      console.error('参数解析失败', e)
    }
  }
  fetchData() // 确保触发数据加载
  getTagCategoryOptions()
})

const router = useRouter()
const route = useRoute()

// 跳转详情页方法
const doClickPicture = (picture: API.PictureVO) => {
  // 携带当前搜索条件
  router.push({
    path: `/picture/${picture.id}`,
    query: {
      fromSearch: encodeURIComponent(JSON.stringify({
        searchText: searchParams.searchText
      }))
    }
  })
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
  <div id="homePage">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        placeholder="从海量图片中搜索"
        v-model:value="searchParams.searchText"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />
    </div>

    <!-- 分类和标签筛选 -->
    <a-tabs v-model:active-key="selectedCategory" @change="doSearch" size="large">
      <a-tab-pane key="all" tab="全部"></a-tab-pane>
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category"></a-tab-pane>
    </a-tabs>

    <div class="tag-bar">
      <span style="margin-right: 8px">标签:</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag.name"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          <img :src="tag.icon" alt="icon" class="tag-icon" />
          {{ tag.name }}
        </a-checkable-tag>
      </a-space>
    </div>

    <!-- 图片列表 - 使用瀑布流组件 -->
    <template v-if="dataList.length > 0">
      <Waterfall
        :list="dataList"
        :width="250"
        :gutter="20"
        :lazyload="true"
        align="center"
        @scrollReachBottom="loadMore"
      >
        <template #item="{ item: picture }">
          <div class="waterfall-item" @click="doClickPicture(picture)">
            <a-card hoverable>
              <template #cover>
                <img :alt="picture.name" :src="picture.url" style="width: 100%; object-fit: cover" />
              </template>
              <a-card-meta :title="picture.name">
                <template #description>
                  <a-flex :wrap="true" gap="small" class="tags-container">
                    <a-tag color="green">{{ picture.category ?? '默认' }}</a-tag>
                    <a-tag v-for="tag in formatTags(picture.tags)" :key="tag" class="flex-tag">
                      {{ tag }}
                    </a-tag>
                  </a-flex>
                </template>
              </a-card-meta>
            </a-card>
          </div>
        </template>
      </Waterfall>
    </template>
    <template v-else>
      <a-empty description="暂无图片数据" />
    </template>

    <!-- 分页 -->
    <div class="pagination-container">
      <a-pagination
        v-model:current="searchParams.current"
        v-model:pageSize="searchParams.pageSize"
        :total="total"
        @change="pagination.onChange"
      />
    </div>
  </div>
</template>

<style scoped>
#homePage {
  margin-bottom: 16px;
}

#homePage .search-bar {
  max-width: 580px;
  margin: 0 auto 16px;
}

#homePage .tag-bar {
  margin-bottom: 16px;
}

.ant-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 瀑布流样式 */
.waterfall-item {
  margin-bottom: 16px;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 16px;
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
</style>
