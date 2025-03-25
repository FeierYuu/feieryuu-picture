<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getSpaceVoByIdUsingGet } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { listPictureVoByPageUsingPost } from '@/api/pictureController.ts'
import { formatSize } from '@/utils'
import PictureList from '@/components/PictureList.vue'

const props = defineProps<{ id: string | number }>()
const space = ref<API.SpaceVO>({})

// 图片数据相关
const dataList = ref<API.PictureVO[]>([]) // 使用这个变量来存储列表数据
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend'
})

// 处理删除成功事件
const handleDeleteSuccess = (deletedId: string) => {
  // 方式1：立即过滤本地数据（快速响应）
  dataList.value = dataList.value.filter(item => item.id !== deletedId)
  total.value -= 1 // 总数减1

  // 方式2：重新加载数据（保证一致性）
  // fetchData()

  // 如果当前页数据为空且不是第一页，自动返回上一页
  if (dataList.value.length === 0 && searchParams.current > 1) {
    searchParams.current -= 1
    fetchData()
  }
}

// 获取空间详情
const fetchSpaceDetail = async () => {
  try {
    const res = await getSpaceVoByIdUsingGet({ id: props.id })
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    } else {
      message.error('获取空间详情失败：' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取空间详情失败：' + e.message)
  }
}

// 获取图片数据
const fetchData = async () => {
  try {
    loading.value = true
    const params = {
      spaceId: props.id,
      ...searchParams
    }
    const res = await listPictureVoByPageUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records ?? []
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取数据失败：' + res.data.message)
    }

  } catch (e: any) {
    message.error('数据加载失败：' + e.message)
  } finally {
    loading.value = false
  }
}

// 分页变化处理
const onPageChange = (page: number, pageSize: number) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  fetchData()
}

onMounted(async () => {
  await fetchSpaceDetail()
  await fetchData()
})
</script>

<template>
  <div id="spaceDetailPage">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2>{{ space.spaceName }}（私有空间）</h2>
      <a-space size="middle">
        <a-tooltip :title="`占用空间 ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`">
          <a-progress
            type="circle"
            :percent="((space.totalSize * 100) / space.maxSize).toFixed(1)"
            :size="42"
          />
        </a-tooltip>

        <a-button type="primary" :href="`/add_picture?spaceId=${id}`" target="_blank">
          + 创建图片
        </a-button>

      </a-space>
    </a-flex>

    <!-- 图片列表 -->
    <PictureList
      :data-list="dataList"
      :loading="loading"
      @delete-success="handleDeleteSuccess"
      :showOp="true"
      :onReload="fetchData"
    />

    <!-- 分页器 -->
    <a-pagination
      style="text-align: center;"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      :show-total="(total: number) => `图片总数 ${total} / ${space.maxCount}`"
      @change="onPageChange"
      show-size-changer
    />
  </div>
</template>

<style scoped>
#spaceDetailPage {

}

h2 {
  margin-bottom: 20px;
  color: #1f1f1f;
}
</style>
