<script lang="ts" setup>
import { getRowClass } from "@/utils/table";
import { OrderPageVO, OrderPageQuery } from "@/api/order";
import { OrderEnum } from "@/enums/OrderEnum";

const props = defineProps({
  // 订单列表数据
  table: {
    required: true,
    type: Object as PropType<TableData<OrderPageVO[], OrderPageVO>>,
    default: () => {
      return {
        data: [] as OrderPageVO[],
        columns: [] as Array<TableColumnData<OrderPageVO>>,
      };
    },
  },
  activeOrderType: {
    type: [Number, String] as PropType<OrderEnum>,
    default: OrderEnum.ALL,
  },
  queryParams: {
    required: true,
    type: Object as PropType<OrderPageQuery>,
    default: () => {
      return {
        page: 1,
        pageSize: 6,
      };
    },
  },
  loading: {
    type: Boolean,
    default: false,
  },
  rowKey: {
    type: String,
    default: "id",
  },
  total: {
    required: true,
    type: Number,
    default: 0,
  },
});

const route = useRoute();
const OrderTableRef = ref();
const emit = defineEmits([
  "init",
  "pagination",
  "update:queryParams",
  "update:total",
  "update:table",
]);

const orderTotal = useVModel(props, "total", emit);
const tableData = useVModel(props, "table", emit);
const orderQueryParams = useVModel(props, "queryParams", emit);

const { data, columns } = toRefs(tableData.value);
const { page, pageSize } = toRefs(orderQueryParams.value);

// 设置当前行选中
watch(
  () => tableData.value,
  (newVal) => {
    if (!newVal.data?.length) {
      return;
    }

    const targetId = route.query?.id ? Number(route.query.id) : undefined;
    const index = newVal.data.findIndex((item) => item.id === targetId);
    if (index > -1) {
      OrderTableRef.value.setCurrentRow(newVal.data[index]);
    }
  },
  { deep: true }
);

function setRowClassName(data: { row: any; rowIndex: number }) {
  const targetId = route.query?.id ? Number(route.query.id) : undefined;
  return data.row.id === targetId ? "highlight-row" : "";
}

const handlePagination = (val: { page: number; limit: number }) => {
  emit("pagination", val);
};

defineOptions({
  name: "OrderTable",
});

onMounted(() => {
  // console.log("tableData", tableData.value);
});
</script>

<template>
  <div v-stagger class="order-table wh-full">
    <el-table
      stripe
      v-bind="$attrs"
      class="table-v1"
      ref="OrderTableRef"
      v-loading="loading"
      element-loading-text="🐠数据加载中..."
      :row-key="rowKey"
      :data="data"
      :row-class-name="setRowClassName"
      style="width: 100%; height: 100%; margin-bottom: 10px"
      empty-text="数据都跑空啦~"
      highlight-current-row
      :header-cell-style="getRowClass"
    >
      <el-table-column type="index" align="center" label="序号" width="60" />
      <el-table-column
        v-for="(item, index) in columns"
        align="center"
        :key="index"
        :label="item.label"
        :prop="item.prop"
        :width="item.width"
        :min-width="item.minWidth"
        :fixed="item.fixed"
      >
        <template #default="{ row }" v-if="item.prop !== 'orderType'">
          <div v-if="item.text === 'address'">
            <el-tooltip
              :content="row.address"
              placement="top"
              :disabled="!row.address || row.address.length <= 9"
            >
              <span>{{
                row.address && row.address.length > 9
                  ? row.address.substring(0, 9) + "..."
                  : row.address
              }}</span>
            </el-tooltip>
          </div>
          <div v-if="item.text === 'orderTypeName'">
            <el-tag :type="row.orderType === 1 ? 'primary' : 'success'">
              {{ row.orderTypeName }}
            </el-tag>
          </div>
          <slot name="order" :row="row" :item="item"></slot>
        </template>
      </el-table-column>
    </el-table>

    <Pagination
      v-if="orderTotal > pageSize"
      v-model:total="orderTotal"
      v-model:page="page"
      v-model:limit="pageSize"
      @pagination="handlePagination"
    />
  </div>
</template>

<style lang="scss" scoped>
:deep(.highlight-row) {
  animation: rowHighlight 0.6s ease-in-out 2;
}

@keyframes rowHighlight {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}
</style>
