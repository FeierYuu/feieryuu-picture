<script setup lang="ts">
import { computed, h, onMounted, ref, watch, watchEffect } from 'vue'
import { getSpaceVoByIdUsingGet } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { listPictureVoByPageUsingPost, searchPictureByColorUsingPost } from '@/api/pictureController.ts'
import { formatSize } from '@/utils'
import PictureList from '@/components/PictureList.vue'
import PictureSearchForm from '@/components/PictureSearchForm.vue'
import { ColorPicker } from 'vue3-colorpicker'
import 'vue3-colorpicker/style.css'
import BatchEditPictureModal from '@/components/BatchEditPictureModal.vue'
import { BarChartOutlined, EditOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { SPACE_PERMISSION_ENUM, SPACE_TYPE_ENUM, SPACE_TYPE_MAP } from '../constants/space.ts'

const props = defineProps<{ id: string | number }>()
const space = ref<API.SpaceVO>({})


// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (space.value.permissionList ?? []).includes(permission)
  })
}

// 定义权限检查
const canManageSpaceUser = createPermissionChecker(SPACE_PERMISSION_ENUM.SPACE_USER_MANAGE)
const canUploadPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_UPLOAD)
const canEditPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDeletePicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)


// 图片数据相关
const dataList = ref<API.PictureVO[]>([]) // 使用这个变量来存储列表数据
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = ref<API.PictureQueryRequest>({
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
  if (dataList.value.length === 0 && searchParams.value.current > 1) {
    searchParams.value.current -= 1
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
      ...searchParams.value
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
  searchParams.value.current = page
  searchParams.value.pageSize = pageSize
  fetchData()
}


//搜索
const onSearch = (newSearchParams: API.PictureQueryRequest) => {
  searchParams.value = {
    ...searchParams.value,
    ...newSearchParams,
    current: 1
  }
  fetchData()

}

const onColorChange = async (color: string) => {
  loading.value = true
  const res = await searchPictureByColorUsingPost({
    picColor: color,
    spaceId: props.id
  })
  if (res.data.code === 0 && res.data.data) {
    const data = res.data.data ?? []
    dataList.value = data
    total.value = data.length
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}


// 批量修改图片信息成功回调
const batchEditPictureModalRef = ref()
const onBatchEditPictureSuccess = () => {
  fetchData()
}

//打开编辑弹窗
const doBatchEdit = () => {
  if (batchEditPictureModalRef.value) {
    batchEditPictureModalRef.value.openModal()
    console.log('打开弹窗')
  }
}

onMounted(async () => {
  await fetchSpaceDetail()
  await fetchData()
})

//空间id改变时 必须重新获取数据
watch(
  () => props.id,
  (newSpaceId) => {
    fetchSpaceDetail()
    fetchData()
  }
)

</script>

<template>
  <div id="spaceDetailPage">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2>{{ space.spaceName }}（{{ SPACE_TYPE_MAP[space.spaceType] }}）</h2>
      <a-space size="middle">
        <a-tooltip :title="`占用空间 ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`">
          <a-progress
            type="circle"
            :percent="((space.totalSize * 100) / space.maxSize).toFixed(1)"
            :size="42"
          />
        </a-tooltip>

        <a-button v-if="canUploadPicture" type="primary" :href="`/add_picture?spaceId=${id}`" target="_blank">
          + 创建图片
        </a-button>
        <a-button
          type="primary"
          ghost
          :icon="h(BarChartOutlined)"
          :href="`/space_analyze?spaceId=${id}`"
          target="_blank"
        >
          空间分析
        </a-button>

        <a-button
          v-if="canManageSpaceUser"
          type="primary"
          ghost
          :icon="h(TeamOutlined)"
          :href="`/spaceUserManage/${id}`"
          target="_blank"
        >
          成员管理
        </a-button>


        <a-button v-if="canEditPicture" :icon="h(EditOutlined)" @click="doBatchEdit" target="_blank">
          批量编辑
        </a-button>

      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px">
    </div>
    <!--搜索表单-->
    <picture-search-form :on-search="onSearch"></picture-search-form>
    <!-- 按颜色搜索 -->
    <a-form-item label="按颜色搜索" style="margin-top: 16px">
      <color-picker format="hex" @pureColorChange="onColorChange" />
    </a-form-item>

    <!-- 图片列表 -->
    <PictureList
      :data-list="dataList"
      :loading="loading"
      @delete-success="handleDeleteSuccess"
      :canEdit="canEditPicture"
      :canDelete="canDeletePicture"
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
    <BatchEditPictureModal
      ref="batchEditPictureModalRef"
      :spaceId="id"
      :pictureList="dataList"
      :onSuccess="onBatchEditPictureSuccess"
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
