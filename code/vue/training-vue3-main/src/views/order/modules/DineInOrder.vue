<script lang="ts" setup>
import * as Order from "../hooks";
import { OrderPageVO } from "@/api/order";
import { OrderEnum as OrderStatus } from "@/enums/OrderEnum";

const props = defineProps({
  row: {
    type: Object as PropType<OrderPageVO>,
    required: true,
  },
  item: {
    type: Object as PropType<TableColumnData<OrderPageVO>>,
    default: () => ({}),
  },
});

const record = toRef(props.row);
const emit = defineEmits(["handleAction", "handleRefresh", "handleReason"]);

const onCompleteToFinished = () => {
  Order.onComplete(record.value.id!).then(() => emit("handleRefresh"));
};

defineOptions({
  name: "DineInOrder",
});
</script>

<template>
  <template v-if="item.prop === 'orderType'">
    {{ row.orderType === 2 ? "堂食订单" : "--" }}
  </template>

  <template v-else-if="item.prop === 'pickupNumber'">
    {{ row.pickupNumber || "--" }}
  </template>

  <template v-else-if="item.text === 'action'">
    <template v-if="row.status === OrderStatus.DINE_IN_ORDER">
      <el-popconfirm title="确认已出餐完成？" @confirm="onCompleteToFinished">
        <template #reference>
          <el-link type="primary" style="margin-right: 8px" :underline="false">
            出餐完成
          </el-link>
        </template>
      </el-popconfirm>
    </template>
  </template>

  <template v-else>
    {{ row[item.prop!] }}
  </template>
</template>
