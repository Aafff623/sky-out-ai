<script lang="ts" setup>
import { WarningFilled } from "@element-plus/icons-vue";
import { OrderPageVO } from "@/api/order";
import { OrderEnum as OrderStatus, ReasonEnum } from "@/enums/OrderEnum";
import { PayMethodEnum } from "@/enums/PayEnum";
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

const payType = computed(() => {
  return props.row.payMethod === PayMethodEnum.WECHAT ? "wechat_pay" : "alipay";
});

const showOrderDetail = (row: OrderPageVO) => {
  emit("handleAction", row);
};

const onConfirm = () => {
  Order.onConfirm(record.value.id!).then(() => {
    emit("handleRefresh");
  });
};

const onRejection = () => {
  emit("handleReason", ReasonEnum.REJECTTION, record.value);
};

defineOptions({
  name: "PendingOrder",
});
</script>

<template>
  <div v-if="item.text === 'userName'">{{ row.userName ?? "匿名用户" }}</div>

  <div v-if="item.text === 'remark'">{{ row.remark ?? "暂无备注" }}</div>

  <div v-if="item.text === 'payMethod'">
    <svg-icon
      :icon-class="payType"
      size="1.5em"
      :color="row.payMethod === PayMethodEnum.WECHAT ? '#07c160' : '#02a9f1'"
    />
  </div>

  <div v-if="item.text === 'action'">
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
