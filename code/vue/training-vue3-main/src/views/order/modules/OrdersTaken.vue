<script lang="ts" setup>
import { OrderPageVO } from "@/api/order";
import { WarningFilled } from "@element-plus/icons-vue";
import { OrderEnum as OrderStatus, ReasonEnum } from "@/enums/OrderEnum";
import * as Order from "../hooks";

const props = defineProps({
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

const record = toRef(props.row);
const emit = defineEmits(["handleAction", "handleRefresh", "handleReason"]);

const showOrderDetail = (row: OrderPageVO) => {
  emit("handleAction", row);
};

const onDelivery = () => {
  Order.onDelivery(record.value.id!).then(() => {
    emit("handleRefresh");
  });
};

const onCancel = () => {
  emit("handleReason", ReasonEnum.CANCEL, record.value);
};

defineOptions({
  name: "OrdersTakenOrder",
});
</script>

<template>
  <div v-if="item.text === 'remark'">{{ row.remark ?? "暂无备注" }}</div>

  <div v-if="item.text === 'action'">
    <!-- 只有处于已接单的状态才可以派送订单 -->
    <el-popconfirm
      v-if="row.status === OrderStatus.ORDERS_TAKEN"
      title="派送该订单？"
      @confirm="onDelivery"
    >
      <template #reference>
        <el-link type="primary" style="margin-right: 8px" :underline="false">
          派送
        </el-link>
      </template>
    </el-popconfirm>

    <!-- 已接单的订单可以取消订单 -->
    <el-popconfirm
      v-if="row.status === OrderStatus.ORDERS_TAKEN"
      title="是否取消订单？"
      :icon="WarningFilled"
      icon-color="red"
      @confirm="onCancel"
    >
      <template #reference>
        <el-link type="danger" style="margin-right: 8px" :underline="false">
          取消
        </el-link>
      </template>
    </el-popconfirm>

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
