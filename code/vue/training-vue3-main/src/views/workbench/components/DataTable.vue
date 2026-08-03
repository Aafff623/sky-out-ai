<script lang="ts" setup>
import { OrderEnum } from "@/enums/OrderEnum";
// 其实在项目中尽量少用点这种`* as`全导入，更推荐按需引入，因为可以减少打包体积，我这里懒就不管了
import * as OrderAPI from "@/api/order";

type JTableData = TableData<OrderAPI.OrderPageVO[]>;

const props = defineProps({
  status: {
    type: [String, Number] as PropType<OrderEnum>,
    default: OrderEnum.PENDING_ORDER,
  },
});

const router = useRouter();
// 是否为待接单列
const isPendingOrder = computed(() => props.status === OrderEnum.PENDING_ORDER);
const isDineInOrder = computed(() => props.status === OrderEnum.DINE_IN_ORDER);

const JTable = ref<BaseTableType>();
const tableData = computed<JTableData>(() => {
  return {
    data: [],
    pagination: {
      layout: "total, prev, pager, next",
    },
    columns: [
      {
        type: "index",
        label: "序号",
        align: "center",
        width: 80,
      },
      {
        label: "订单信息",
        prop: "number",
        minWidth: "200px",
        childColumns: [
          {
            label: "订单号",
            prop: "number",
            minWidth: "200px",
          },
          {
            label: "订单菜品",
            prop: "orderDishes",
            minWidth: "200px",
          },
        ],
      },
      {
        label: "取餐码",
        prop: "pickupNumber",
        minWidth: "120px",
        visible: isDineInOrder.value,
      },
      {
        label: "订单类型",
        prop: "orderType",
        minWidth: "120px",
        formatter: (row: OrderAPI.OrderPageVO) =>
          row.orderType === 2 ? "堂食订单" : "外卖订单",
      },
      {
        label: "收货人",
        prop: "consignee",
        minWidth: "140px",
        visible: isPendingOrder.value,
      },
      {
        label: "手机号",
        prop: "phone",
        minWidth: "140px",
        visible: isPendingOrder.value,
      },
      {
        label: "支付方式",
        prop: "payMethod",
        minWidth: "140px",
        visible: isPendingOrder.value,
        formatter: (row: OrderAPI.OrderPageVO) =>
          row.payMethod === 1 ? "微信支付" : "支付宝支付",
      },
      {
        label: "地址",
        prop: "address",
        minWidth: "180px",
        visible: !isDineInOrder.value,
      },
      {
        label: "下单时间",
        prop: "orderTime",
        minWidth: "200px",
        visible: isPendingOrder.value || isDineInOrder.value,
      },
      {
        label: "备注",
        prop: "remark",
        minWidth: "160px",
      },
      {
        label: "餐具数量",
        prop: "tablewareNumber",
        visible: !isPendingOrder.value && !isDineInOrder.value,
      },
      {
        label: "预计送达时间",
        prop: "estimatedDeliveryTime",
        minWidth: "200px",
        visible: !isPendingOrder.value && !isDineInOrder.value,
      },
      {
        label: "操作",
        slot: "slot-action",
        minWidth: "140px",
        fixed: "right",
        actionConfig: {
          options: [
            {
              text: isPendingOrder.value
                ? "前往接单"
                : isDineInOrder.value
                  ? "出餐"
                  : "前往派送",
              linkConfig: {
                type: "primary",
              },
              onClick: (row) => {
                router.push({
                  path: "/order/index",
                  query: {
                    id: row.id,
                    status: props.status,
                  },
                });
              },
            },
          ],
        },
      },
    ],
  };
});

watch(() => props.status, updateData);

function updateData() {
  JTable.value?.updateData<OrderAPI.OrderPageQuery>({
    queryParams: { status: props.status },
    requestConfig: { url: OrderAPI.baseUrls.conditionSearch },
  });
}

onMounted(updateData);
</script>

<template>
  <BaseTable ref="JTable" v-model="tableData" />
</template>
