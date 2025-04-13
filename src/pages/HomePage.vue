<script setup lang="ts">
import { type Component, computed, onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet, listPictureVoByPageUsingPost
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

import 'vue-waterfall-plugin-next/dist/style.css'
import PictureList from '@/components/PictureList.vue'
import { useRoute } from 'vue-router'

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
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    //（强制转换为数字）
    total.value = Number(res.data.data.total) ?? 0

  } else {
    message.error('获取数据失败' + res.data.message)
  }
  loading.value = false
}


// 搜索
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}


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

const route = useRoute()
//保存搜索框里输入的搜索参数
const restoreSearchState = () => {
  try {
    const stateParam = route.query.state
    if (typeof stateParam === 'string') {
      const state = JSON.parse(decodeURIComponent(stateParam))

      // 恢复搜索文本
      if (typeof state.searchText === 'string') {
        searchParams.searchText = state.searchText
      }

      // 恢复分类
      if (typeof state.category === 'string') {
        selectedCategory.value = state.category
      }

      // 恢复标签选中状态（确保标签数据已加载）
      if (Array.isArray(state.tags) && tagList.value.length > 0) {
        selectedTagList.value = tagList.value.map(tag =>
          state.tags.includes(tag.name)
        )
      }
    }
  } catch (e) {
    console.warn('恢复搜索状态失败', e)
    // 重置为默认状态
    searchParams.searchText = ''
    selectedCategory.value = 'all'
    selectedTagList.value = new Array(tagList.value.length).fill(false)
  }
}
// 🟢 新增：获取当前筛选状态
const getCurrentSearchState = () => ({
  searchText: searchParams.searchText,
  category: selectedCategory.value,
  tags: tagList.value
    .filter((_, index) => selectedTagList.value[index])
    .map(tag => tag.name)
})
// 页面加载时获取数据
onMounted(async () => {
  await getTagCategoryOptions() // 先获取标签数据
  await restoreSearchState()          // 再恢复状态
  await fetchData()                  // 最后获取数据
})


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
    <PictureList :data-list="dataList" :loading="loading" :search-state="getCurrentSearchState()"></PictureList>

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

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

</style>
