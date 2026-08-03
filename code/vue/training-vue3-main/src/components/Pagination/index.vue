<script lang="ts" setup>
const props = defineProps({
  total: {
    required: true,
    type: Number as PropType<number>,
    default: 0,
  },
  page: {
    type: Number,
    default: 1,
  },
  limit: {
    type: Number,
    default: 20,
  },
  pageSizes: {
    type: Array as PropType<number[]>,
    default() {
      return [10, 20, 30, 40, 50];
    },
  },
  layout: {
    type: String,
    default: "total, sizes, prev, pager, next, jumper",
  },
  background: {
    type: Boolean,
    default: true,
  },
  hidden: {
    type: [Boolean, Function] as PropType<boolean | (() => boolean)>,
    default: false,
  },
});

const emit = defineEmits(["pagination", "update:page", "update:limit"]);

const currentPage = useVModel(props, "page", emit);

const pageSize = useVModel(props, "limit", emit);

// 是否需要每页显示个数选择器的选项设置
const hasPageSizes = computed<boolean>(() => {
  return props.layout
    .split(",")
    .map((item) => item.trim())
    .includes("sizes");
});

function handleSizeChange(val: number) {
  emit("pagination", { page: currentPage, limit: val });
}

function handleCurrentChange(val: number) {
  currentPage.value = val;
  emit("pagination", { page: val, limit: props.limit });
}

defineOptions({
  name: "",
});
</script>

<template>
  <el-scrollbar>
    <div :class="{ hidden: hidden }" class="pagination float-right">
      <el-pagination
        v-bind="$attrs"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :background="background"
        :layout="layout"
        :page-sizes="hasPageSizes ? pageSizes : []"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </el-scrollbar>
</template>

<style lang="scss" scoped>
.pagination {
  padding: 12px;
  padding-right: 0;
  padding-bottom: 0;

  &.hidden {
    display: none;
  }
}
</style>
