<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import {
  clearAllCacheUsingPost,
  deletePictureUsingPost, doPictureReviewUsingPost,
  listPictureByPageUsingPost

} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { PIC_REVIEW_STATUS_ENUM, PIC_REVIEW_STATUS_MAP, PIC_REVIEW_STATUS_OPTIONS } from '../../constants/picture.ts'
import {
  ReloadOutlined
} from '@ant-design/icons-vue'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80
  },
  {
    title: '图片',
    dataIndex: 'url'
  },
  {
    title: '名称',
    dataIndex: 'name'
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    ellipsis: true
  },
  {
    title: '类型',
    dataIndex: 'category'
  },
  {
    title: '标签',
    dataIndex: 'tags'
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo'
  },
  {
    title: '用户id',
    dataIndex: 'userId',
    width: 80
  },
  {
    title: '审核信息',
    dataIndex: 'reviewMessage'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime'
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime'
  },
  {
    title: '操作',
    key: 'action'
  }
]


// 定义数据
const dataList = ref<API.Picture[]>([])
//数据总数

const total = ref(0)
//搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 5,
  sortField: 'createTime',
  sortOrder: 'descend'

})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    //分页显示位置
    position: ['bottomCenter '],
    showTotal: (total) => `共 ${total} 条`
  }
})

// 获取数据
const fetchData = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams,
    nullSpaceId: true
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
}


const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 获取数据
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

//删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    fetchData()
  } else {
    message.error('删除失败' + res.data.message)
  }
}


/**
 * 审核图片
 */
const handlerReview = async (record: API.Picture, reviewStatus: number) => {
  const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员操作通过' : '管理员操作拒绝'
  const res = await doPictureReviewUsingPost({
    id: record.id,
    reviewMessage,
    reviewStatus
  })
  if (res.data.code === 0) {
    message.success('审核成功')
    //重新获取数据
    fetchData()
  } else {
    message.error('审核失败' + res.data.message)
  }
}


const clearing = ref(false)

const handleClearCache = async () => {
  try {
    clearing.value = true
    const { data } = await clearAllCacheUsingPost()
    if (data.data) {
      message.success('缓存清理成功')
      // 可选：重新加载数据
      location.reload()
    }
  } catch (e) {
    message.error(`操作失败: ${e.response?.data?.message || e.message}`)
  } finally {
    clearing.value = false
  }
}


// 页面加载时 请求数据
onMounted(() => {
  fetchData()
})

</script>
<template>
  <div id="pictureManager">
    <a-flex justify="space-between">
      <h2>图片管理</h2>
      <a-space>
        <a-button type="primary" href="/add_picture" target="_blank">
          创建图片
        </a-button>
        <a-button type="primary" href="/add_picture/batch" target="_blank" ghost>批量创建图片</a-button>
        <a-popconfirm
          title="确认要刷新所有缓存吗? 注意:此操作是危险操作!"
          @confirm="handleClearCache"
        >
          <a-button danger :loading="clearing">
            <template #icon>
              <ReloadOutlined />
            </template>
            {{ clearing ? '正在清理...' : '强制刷新缓存' }}
          </a-button>
        </a-popconfirm>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px"></div>
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch" style="margin-bottom: 20px">
      <a-form-item label="关键词">
        <a-input v-model:value="searchParams.searchText" placeholder="从名称和简介搜索" allow-clear />
      </a-form-item>
      <a-form-item label="类型">
        <a-input v-model:value="searchParams.category" placeholder="输入类型" allow-clear />
      </a-form-item>
      <a-form-item label="标签">
        <a-select v-model:value="searchParams.tags" mode="tags" style="min-width: 180px" placeholder="请选择标签"
                  allow-clear>
        </a-select>
      </a-form-item>
      <a-form-item name="reviewStatus" label="审核状态">
        <a-select
          style="min-width: 180px"
          v-model:value="searchParams.reviewStatus"
          placeholder="请选择审核状态"
          :options="PIC_REVIEW_STATUS_OPTIONS"
          allow-clear
        ></a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>

    <!-- 表格 -->
    <a-table :columns="columns" :data-source="dataList" :pagination="pagination" @change="doTableChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="80"></a-image>
        </template>

        <template v-if="column.dataIndex === 'tags'">
          <a-space wrap>
            <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">{{ tag }}</a-tag>
          </a-space>
        </template>

        <template v-if="column.dataIndex === 'picInfo'">
          <div>格式：{{ record.picFormat }}</div>
          <div>宽度：{{ record.picWidth }}</div>
          <div>高度：{{ record.picHeight }}</div>
          <div>宽高比：{{ record.picScale }}</div>
          <div>大小：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
        </template>

        <template v-if="column.dataIndex === 'reviewMessage'">
          <div>审核状态：{{ PIC_REVIEW_STATUS_MAP[record.reviewStatus] }}</div>
          <div>审核信息：{{ record.reviewMessage }}</div>
          <div>审核人：{{ record.reviewerId }}</div>
          <div v-if="record.reviewTime">审核时间：{{ dayjs(record.reviewTime).format('YYYY-MM-DD HH:mm:ss') }}</div>
        </template>

        <template v-else-if="column.dataIndex === 'pictureRole'">
          <div v-if="record.pictureRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通图片</a-tag>
          </div>
        </template>

        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <template v-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button
              type="link"
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"
              target="_blank"
              @click="handlerReview(record,PIC_REVIEW_STATUS_ENUM.PASS)"
            >
              通过
            </a-button>
            <a-button
              type="link"
              danger
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT"
              target="_blank"
              @click="handlerReview(record,PIC_REVIEW_STATUS_ENUM.REJECT)"
            >
              拒绝
            </a-button>

            <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank">编辑</a-button>
            <a-popconfirm title="确认要删除该图片吗？"
                          ok-text="确认"
                          cancel-text="取消"
                          @confirm="doDelete(record.id)"
                          @cancel="message.info('操作已取消')">
              <a-button danger>删除</a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>


