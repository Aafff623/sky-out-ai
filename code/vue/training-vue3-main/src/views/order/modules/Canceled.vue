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

const emit = defineEmits(["handleAction"]);

const showOrderDetail = (row: OrderPageVO) => {
  emit("handleAction", row);
};

defineOptions({
  name: "CanceledOrder",
});
</script>

<template>
  <div v-if="item.text === 'status'">
    {{ row.cancelReason ?? row.rejectionReason }}
  </div>

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

  <div v-if="item.prop !== undefined">{{ row[item.prop] }}</div>
</template>
