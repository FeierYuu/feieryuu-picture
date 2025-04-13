<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { listPictureTagCategoryUsingGet } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'


interface Props {
  onSearch?: (searchParams: API.PictureQueryRequest) => void
}

const props = defineProps<Props>()

//搜索条件
const searchParams = reactive<API.PictureQueryRequest>({})


// 获取数据
const doSearch = () => {
  props.onSearch?.(searchParams)
}

const dateRange = ref<[]>([])

/**
 * 日期范围更改时触发
 * @param dates
 * @param dateStrings
 */
const onRangeChange = (dates: any[], dateStrings: string[]) => {
  if (dates?.length >= 2) {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
  } else {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  }

}

// 时间范围预设
const rangePresets = ref([
  { label: '过去 7 天', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '过去 14 天', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '过去 30 天', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '过去 90 天', value: [dayjs().add(-90, 'd'), dayjs()] }
])


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

// 清除输入条件
const doClear = () => {
  Object.keys(searchParams).forEach(key => {
    searchParams[key] = undefined
  })
  // 日期时间单独设置
  dateRange.value = []

  // 清空后重新搜索
  props.onSearch?.(searchParams)
}





/**
 * 首次进入页面时获取标签和分类选项
 */
onMounted(() => {
  getTagCategoryOptions()
})

</script>
<template>
  <div id="PictureSearchForm">
    <!-- 搜索表单 -->

    <a-form layout="inline" :model="searchParams" @finish="doSearch" style="margin-bottom: 20px">
      <a-form-item label="关键词">
        <a-input v-model:value="searchParams.searchText" placeholder="从名称和简介搜索" allow-clear />
      </a-form-item>
      <a-form-item label="分类">
        <a-select :options="categoryOptions" v-model:value="searchParams.category" placeholder="输入类型"
                  style="min-width: 180px" allow-clear />
      </a-form-item>
      <a-form-item label="标签">
        <a-select :options="tagOptions" v-model:value="searchParams.tags" mode="tags" style="min-width: 180px"
                  placeholder="请选择标签"
                  allow-clear>
        </a-select>
      </a-form-item>

      <a-form-item label="日期" name="dateRange">
        <a-range-picker
          style="width: 400px"
          show-time
          format="YYYY/MM/DD HH:mm:ss"
          :placeholder="['开始时间','结束时间']"
          v-model:value="dateRange"
          :presets="rangePresets"
          @change="onRangeChange"
        />
      </a-form-item>

      <a-form-item label="名称" name="name">
        <a-input v-model:value="searchParams.name" placeholder="请输入图片名称" allow-clear style="width: 150px" />
      </a-form-item>

      <a-form-item label="宽度" name="picWidth">
        <a-input-number v-model:value="searchParams.picWidth" placeholder="请输入宽度" style="width: 150px" />
      </a-form-item>
      <a-form-item label="高度" name="picHeight">
        <a-input-number v-model:value="searchParams.picHeight" placeholder="请输入高度" style="width: 150px" />
      </a-form-item>

      <a-form-item label="简介" name="introduction">
        <a-input v-model:value="searchParams.introduction" placeholder="请输入简介" allow-clear style="width: 150px" />
      </a-form-item>

      <a-form-item label="格式" name="picFormat">
        <a-input v-model:value="searchParams.picFormat" placeholder="请输入图片格式" allow-clear style="width: 150px" />
      </a-form-item>


      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit" style="width: 96px">搜索</a-button>
          <a-button html-type="reset" @click="doClear">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>

  </div>
</template>
<style scoped>
#PictureSearchForm .ant-form-item {
  margin-top: 12px;
}
</style>
