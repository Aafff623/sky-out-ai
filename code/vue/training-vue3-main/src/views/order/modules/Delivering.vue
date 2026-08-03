<script lang="ts" setup>
import { OrderPageVO } from "@/api/order";
import { OrderEnum as OrderStatus, ReasonEnum } from "@/enums/OrderEnum";
import { PayMethodEnum } from "@/enums/PayEnum";
import * as Order from "../hooks";
import { WarningFilled } from "@element-plus/icons-vue";

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

const payType = computed(() => {
  return props.row.payMethod === PayMethodEnum.WECHAT ? "wechat_pay" : "alipay";
});

const showOrderDetail = (row: OrderPageVO) => {
  emit("handleAction", row);
};

const onComplete = () => {
  Order.onComplete(record.value.id!).then(() => {
    emit("handleRefresh");
  });
};

const onCancel = () => {
  emit("handleReason", ReasonEnum.CANCEL, record.value);
};

defineOptions({
  name: "Delivering",
});
</script>

<template>
  <div v-if="item.text === 'userName'">{{ row.userName ?? "匿名用户" }}</div>

  <div v-if="item.text === 'amount'">￥{{ row.amount }}</div>

  <div v-if="item.text === 'payMethod'">
    <svg-icon
      :icon-class="payType"
      size="1.5em"
      :color="row.payMethod === PayMethodEnum.WECHAT ? '#07c160' : '#02a9f1'"
    />
  </div>

  <div v-if="item.text === 'action'">
    <!-- 处于派送中的订单 -->
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

    <el-popconfirm
      v-if="row.status === OrderStatus.DELIVERING"
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
