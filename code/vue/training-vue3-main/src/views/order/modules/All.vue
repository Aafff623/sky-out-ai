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

const onConfirm = () => {
  Order.onConfirm(record.value.id!).then(() => {
    emit("handleRefresh");
  });
};

const onCancel = () => {
  emit("handleReason", ReasonEnum.CANCEL, record.value);
};

const onRejection = () => {
  emit("handleReason", ReasonEnum.REJECTTION, record.value);
};

const onDelivery = () => {
  Order.onDelivery(record.value.id!).then(() => {
    emit("handleRefresh");
  });
};

const onComplete = () => {
  Order.onComplete(record.value.id!).then(() => {
    emit("handleRefresh");
  });
};

defineOptions({
  name: "AllOrder",
});
</script>

<template>
  <!-- 订单状态 -->
  <div v-if="item.text === 'status'">
    <span v-if="row.status === OrderStatus.PENDING_PAYMENT">待付款</span>
    <span v-else-if="row.status === OrderStatus.PENDING_ORDER">待接单</span>
    <span v-else-if="row.status === OrderStatus.ORDERS_TAKEN">已接单</span>
    <span v-else-if="row.status === OrderStatus.DELIVERING">派送中</span>
    <span v-else-if="row.status === OrderStatus.COMPLETED">已完成</span>
    <span v-else-if="row.status === OrderStatus.CANCELED">已取消</span>
    <span v-else-if="row.status === OrderStatus.DINE_IN_ORDER">待出餐</span>
    <span v-else>退款</span>
  </div>

  <div v-if="item.text === 'userName'">{{ row.userName ?? "匿名用户" }}</div>

  <div v-if="item.text === 'amount'">￥{{ row.amount }}</div>

  <div v-if="item.text === 'action'">
    <!-- 待接单（2）可以显示：【接单、拒单】 -->
    <span v-if="row.status === OrderStatus.PENDING_ORDER">
      <el-popconfirm title="是否确认接单？" @confirm="onConfirm">
        <template #reference>
          <el-link type="primary" style="margin-right: 8px" :underline="false">
            接单
          </el-link>
        </template>
      </el-popconfirm>

      <el-popconfirm
        title="是否确认拒单？"
        :icon="WarningFilled"
        icon-color="red"
        @confirm="onRejection"
      >
        <template #reference>
          <el-link type="danger" style="margin-right: 8px" :underline="false">
            拒单
          </el-link>
        </template>
      </el-popconfirm>
    </span>

    <!-- 已接单（3）可以显示：【派送、取消】 -->
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

    <!-- 派送中（4）可以显示：【完成、取消】 -->
    <el-popconfirm
      v-if="row.status === OrderStatus.DELIVERING"
      title="完成该订单？"
      @confirm="onComplete"
    >
      <template #reference>
        <el-link type="primary" style="margin-right: 8px" :underline="false">
          完成
        </el-link>
      </template>
    </el-popconfirm>

    <!-- 已完成（5） -->
    <el-popconfirm
      v-if="
        row.status === OrderStatus.ORDERS_TAKEN ||
        row.status === OrderStatus.DELIVERING
      "
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

    <el-popconfirm
      v-if="row.status === OrderStatus.DINE_IN_ORDER"
      title="确认已出餐完成？"
      @confirm="onComplete"
    >
      <template #reference>
        <el-link type="primary" style="margin-right: 8px" :underline="false">
          出餐完成
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
