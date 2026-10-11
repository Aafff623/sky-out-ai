<script lang="ts" setup>
import { OrderPageVO } from "@/api/order";

defineProps({
  row: {
    required: true,
    type: Object as PropType<OrderPageVO>,
    default: () => {},
  },
  item: {
    type: Object as PropType<TableColumnData<OrderPageVO>>,
    default: () => {},
  },
});

const emit = defineEmits(["handleAction", "handleRefresh", "handleReason"]);

const showOrderDetail = (row: OrderPageVO) => {
  emit("handleAction", row);
};

defineOptions({
  name: "CompletedOrder",
});
</script>

<template>
  <div v-if="item.text === 'userName'">{{ row.userName ?? "匿名用户" }}</div>

  <div v-if="item.text === 'remark'">{{ row.remark ?? "暂无备注" }}</div>

  <div v-if="item.text === 'amount'">￥{{ row.amount }}</div>

  <div v-if="item.text === 'action'">
    <el-link
      type="primary"
      style="margin-right: 8px"
      :underline="false"
      @click="showOrderDetail(row)"
    >
      查看
    </el-link>
  </div>

  <div v-if="item.prop !== undefined">{{ row[item.prop as keyof OrderPageVO] }}</div>
</template>
