<script lang="ts" setup>
import { useAppStore } from "@/store/modules/app";
import OrderAPI, { OrderPageVO } from "@/api/order";
import { OrderEnum, ReasonEnum } from "@/enums/OrderEnum";
import { PayMethodEnum } from "@/enums/PayEnum";
import { Iphone, Location, Tickets, User } from "@element-plus/icons-vue";

type DescItem = {
  label: string;
  span: number;
  value: any;
  icon?: Component;
};

type CollapseData = {
  title: string;
  name: string;
  column: number;
  descriptions: Array<DescItem>;
};

const props = defineProps({
  statusList: {
    type: Array as PropType<
      Array<{
        label: string;
        name: OrderEnum;
        value: string;
      }>
    >,
    default: () => [],
  },
});

const emit = defineEmits(["handleRefresh", "handleReason"]);

const appStore = useAppStore();

const btn = reactive({
  // 按钮加载
  loading: false,
  // 按钮是否禁用,默认禁用
  disabled: false,
});

// 对话框是否可见
let visible = ref<boolean>(false);
const record = ref<OrderPageVO>();
const collapse = ref<string[]>(["user_info", "dish_info", "cost_info"]);

const statusName = computed(() => {
  if (record.value?.status === OrderEnum.PENDING_PAYMENT) return "待付款";
  return props.statusList.find((item) => item.name === record.value?.status)
    ?.value;
});
const collapseData = ref<Array<CollapseData>>();

function show(show: boolean = false, row?: OrderPageVO) {
  visible.value = show;
  record.value = row;
  setFieldsValue(row);
}

const handleRejection = () => {
  emit("handleReason", ReasonEnum.CANCEL, record.value);
};

const handleConfirm = async () => {
  const res = await OrderAPI.confirm(record.value?.id as number);
  if (res.code === 200) {
    emit("handleRefresh");
    ElMessage.success("商家接单成功");
  } else {
    ElMessage.error(res.msg || "商家接单失败");
  }
};

const handleClose = (done?: () => void) => {
  visible.value = false;
  done?.();
};

const setFieldsValue = (row?: OrderPageVO) => {
  nextTick(() => {
    const userInfo: Array<DescItem> = [
      {
        label: "用户名",
        span: 1,
        value: row?.userName ?? "匿名用户",
        icon: markRaw(User),
      },
      {
        label: "手机号",
        span: 1,
        value: row?.phone ?? "未知电话",
        icon: markRaw(Iphone),
      },
      {
        label: "预计送达时间",
        span: 1,
        value: row?.estimatedDeliveryTime,
        icon: markRaw(Tickets),
      },
      {
        label: "下单时间",
        span: 1,
        value: row?.orderTime,
        icon: markRaw(Tickets),
      },
      {
        label: "收货地址",
        span: 1,
        value: row?.address,
        icon: markRaw(Location),
      },
    ];

    const dishInfo: Array<DescItem> = [
      {
        label: "菜品",
        span: 2,
        value: row?.orderDishes,
      },
      {
        label: "菜品小计",
        span: 1,
        value: "￥" + row?.amount,
      },
      {
        label: "备注",
        span: 2,
        value: row?.remark,
      },
    ];

    const costInfo: Array<DescItem> = [
      {
        label: "打包费",
        span: 1,
        value: "￥" + row?.packAmount,
      },
      {
        label: "派送费",
        span: 1,
        value: "￥6.00",
      },
      {
        label: "合计",
        span: 1,
        value: "￥" + row?.amount,
      },
      {
        label: "支付方式",
        span: 1,
        value:
          row?.payMethod === PayMethodEnum.WECHAT ? "微信支付" : "支付宝支付",
      },
      {
        label: "支付时间",
        span: 1,
        value: row?.checkoutTime,
      },
    ];

    collapseData.value = [
      {
        title: "🤵用户信息",
        name: "user_info",
        column: 2,
        descriptions: userInfo,
      },
      {
        title: "🍝菜品信息",
        name: "dish_info",
        column: 2,
        descriptions: dishInfo,
      },
      {
        title: "🧾费用信息",
        name: "cost_info",
        column: 2,
        descriptions: costInfo,
      },
    ] as Array<CollapseData>;
  });
};

defineExpose({
  show,
});

defineOptions({
  name: "OrderModel",
});
</script>

<template>
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="5vh"
    width="55%"
    draggable
    title="订单信息"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <div class="px-30px order-info">
      <el-row class="pb-18px" :gutter="20">
        <el-col :span="24">
          <span class="mr-8px"
            >订单号：<span>{{ record?.number }}</span></span
          >
          <el-tag type="info" effect="dark" round>{{ statusName }}</el-tag>
        </el-col>
      </el-row>

      <el-scrollbar max-height="600px">
        <el-collapse v-model="collapse">
          <el-collapse-item
            v-for="(item, index) in collapseData"
            :key="index"
            :title="item.title"
            :name="item.name"
          >
            <el-descriptions class="margin-top" :column="item.column" border>
              <el-descriptions-item
                v-for="(desc, di) in item.descriptions"
                :key="di"
                :span="desc.span"
              >
                <template #label>
                  <div class="cell-item flex-y-center">
                    <el-icon v-if="desc.icon">
                      <component :is="desc.icon" />
                    </el-icon>
                    <span>{{ desc.label }}</span>
                  </div>
                </template>
                <span>{{ desc.value }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-collapse-item>
        </el-collapse>
      </el-scrollbar>
    </div>
    <!-- 只有用户已经下单并且付完款才可以进行接单、拒单操作 状态为 待接单（2）  -->
    <template #footer v-if="record?.status === OrderEnum.PENDING_ORDER">
      <el-button plain @click="handleRejection">🥡拒绝接单</el-button>
      <el-button
        type="primary"
        :loading="btn.loading"
        :disabled="btn.disabled"
        @click="handleConfirm"
      >
        🥕确认接单
      </el-button>
    </template>
  </el-dialog>
</template>
