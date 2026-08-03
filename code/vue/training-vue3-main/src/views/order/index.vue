<script lang="ts" setup>
import { Search } from "@element-plus/icons-vue";
import { type FormInstance, TabPaneName } from "element-plus";
import OrderAPI, { type OrderPageQuery, OrderPageVO } from "@/api/order";
import { useAppStore } from "@/store";
import { OrderEnum, ReasonEnum } from "@/enums/OrderEnum";
import OrderTable from "./table/OrderTable.vue";
import * as orderColumns from "./table/columns";
import * as orderModules from "./modules";
import { OrderModel, ReasonModel } from "./model";

type KeyWordSelectType = "number" | "userId" | "phone";
type KeyWordOptions = Array<{
  label: string;
  value: KeyWordSelectType;
}>;
type TabListType = Array<{
  label: string;
  name: OrderEnum;
  value: string;
}>;
type ComponentType =
  | OrderEnum.ALL
  | OrderEnum.CANCELED
  | OrderEnum.COMPLETED
  | OrderEnum.DELIVERING
  | OrderEnum.PENDING_ORDER
  | OrderEnum.ORDERS_TAKEN
  | OrderEnum.DINE_IN_ORDER;

type OrderModelExpose = {
  show: (v: boolean, row?: OrderPageVO) => void;
};
type ReasonModelExpose = {
  show: (reason: ReasonEnum, row: OrderPageVO) => void;
};

const route = useRoute();
const appStore = useAppStore();
const loading = ref(false);
const total = ref(0);
const OrderModelRef = ref<OrderModelExpose>();
const ReasonModelRef = ref<ReasonModelExpose>();
const OrderQueryFormRef = ref<FormInstance>();
const queryParams = reactive<OrderPageQuery>({
  number: "",
  userId: "",
  status: undefined,
  phone: "",
  beginTime: "",
  endTime: "",
  page: 1,
  pageSize: 6,
});
const keyword = computed(() => {
  return Object.keys(queryParams).find(
    (key) => key === keywordSelect.value
  ) as KeyWordSelectType;
});
const keywordSelect = ref<KeyWordSelectType>("number");
const keywordOptions = ref<KeyWordOptions>([
  {
    label: "订单号",
    value: "number",
  },
  {
    label: "手机号",
    value: "phone",
  },
  // {
  //   label: "用户id",
  //   value: "userId",
  // },
]);
const orderTime = ref<string[]>([]);
const activeOrderType = ref(OrderEnum.ALL);
const tabList = ref<TabListType>([
  { label: "全部订单", name: OrderEnum.ALL, value: "全部订单" },
  {
    label: `待接单`,
    name: OrderEnum.PENDING_ORDER,
    value: "待接单",
  },
  {
    label: "待出餐",
    name: OrderEnum.DINE_IN_ORDER,
    value: "待出餐",
  },
  {
    label: `待派送`,
    name: OrderEnum.ORDERS_TAKEN,
    value: "待派送",
  },
  {
    label: `派送中`,
    name: OrderEnum.DELIVERING,
    value: "派送中",
  },
  {
    label: `已完成`,
    name: OrderEnum.COMPLETED,
    value: "已完成",
  },
  {
    label: `已取消`,
    name: OrderEnum.CANCELED,
    value: "已取消",
  },
]);
const table = reactive<TableData<OrderPageVO[]>>({
  data: [],
  columns: orderColumns.ALL,
}) as TableData<OrderPageVO[]>;
const compType = reactive<Record<ComponentType, Component>>({
  // 使用markRaw 防止 Vue 过度响应 动态组件
  [OrderEnum.ALL]: markRaw(orderModules.All), // 全部订单
  [OrderEnum.CANCELED]: markRaw(orderModules.Canceled), // 已取消
  [OrderEnum.COMPLETED]: markRaw(orderModules.Completed), // 已完成
  [OrderEnum.DELIVERING]: markRaw(orderModules.Delivering), // 派送中
  [OrderEnum.PENDING_ORDER]: markRaw(orderModules.PendingOrder), // 待接单
  [OrderEnum.ORDERS_TAKEN]: markRaw(orderModules.OrdersTaken), // 待派送
  [OrderEnum.DINE_IN_ORDER]: markRaw(orderModules.DineInOrder),
});
// 当前订单table组件实例
const curOrderTable = ref<ComponentType>(OrderEnum.ALL);
const componentId = computed({
  get() {
    return compType[curOrderTable.value];
  },
  set(val: ComponentType) {
    curOrderTable.value = val;
    // tab切换更新table列字段
    switch (val) {
      case OrderEnum.ALL: // 全部订单表字段
        table.columns = orderColumns.ALL as any;
        break;
      case OrderEnum.CANCELED: // 已取消订单表字段
        table.columns = orderColumns.CANCELED as any;
        break;
      case OrderEnum.COMPLETED: // 已完成订单表字段
        table.columns = orderColumns.COMPLETED as any;
        break;
      case OrderEnum.DELIVERING: // 派送中订单表字段
        table.columns = orderColumns.DELIVERING as any;
        break;
      case OrderEnum.PENDING_ORDER: // 待接单订单表字段
        table.columns = orderColumns.PENDING_ORDER as any;
        break;
      case OrderEnum.ORDERS_TAKEN: // 待派送订单表字段
        table.columns = orderColumns.ORDERS_TAKEN as any;
        break;
      case OrderEnum.DINE_IN_ORDER:
        table.columns = orderColumns.DINE_IN_ORDER as any;
        break;
      default:
        table.columns = orderColumns.ALL as any;
        break;
    }
  },
});
// 子组件动态条件是否需要emit刷新事件
// const hasHandleRefresh = computed(() => {
//   return ![OrderEnum.CANCELED].includes(activeOrderType.value);
// });
const needEmit = computed(() => {
  return ![OrderEnum.CANCELED].includes(activeOrderType.value);
});
// 动态多事件绑定
const combinedListeners = computed(() => {
  let listeners = {};

  if (needEmit.value) {
    listeners = { ...listeners, handleReason, handleRefresh: handleQuery };
  }
  // if (hasHandleRefresh.value) {
  //   listeners = { ...listeners, handleRefresh: handleQuery };
  // }
  return listeners;
});

watch(
  activeOrderType,
  (newStatus, oldStatus) => {
    if (newStatus !== oldStatus) {
      updateStatus(newStatus);
    }
  },
  { immediate: true }
);

watch(keywordSelect, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    // 每次选项发生变化，清空上一次的搜索内容
    queryParams[oldVal] = "";
  }
});

onMounted(async () => {
  await initData();
  await handleQuery();
});

function initData() {
  return new Promise((resolve) => {
    if (Object.keys(route.query).length > 0) {
      for (const key in route.query) {
        if (key === "status") {
          const status =
            (Number(route.query[key]) as OrderEnum) || OrderEnum.ALL;
          activeOrderType.value = status;
          updateStatus(status);
        } else {
          (queryParams as any)[key] = route.query[key];
        }
      }
    }

    resolve(null);
  });
}

async function handleQuery() {
  loading.value = true;
  OrderAPI.conditionSearch(queryParams)
    .then((res) => {
      total.value = res.data?.total;
      table.data = res.data?.records;
      console.log("获取订单列表==>", res.data);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取订单列表失败");
    })
    .finally(() => {
      loading.value = false;
      OrderModelRef.value?.show(false);
    });
}

const handleOrderTimeChange = (val: string[]) => {
  queryParams.beginTime = val?.length ? val[0] : "";
  queryParams.endTime = val?.length ? val[1] : "";
};

const handleTabChange = (name: TabPaneName) => {
  const status = name as ComponentType;
  const orderTypeName = tabList.value.find((item) => item.name === name)?.value;
  console.log(`tab项发生变化，【${orderTypeName}】==>`, name);
  updateStatus(status);
  handleQuery();
};

const handleAction = (row: OrderPageVO) => {
  console.log("订单操作", row);

  OrderModelRef.value?.show(true, row);
};

const handleReason = (reason: ReasonEnum, row: OrderPageVO) => {
  ReasonModelRef.value?.show(reason, row);
};

const resetForm = () => {
  Object.assign(queryParams, {
    number: "",
    userId: "",
    status: undefined,
    phone: "",
    beginTime: "",
    endTime: "",
    page: 1,
    pageSize: 6,
  });
  orderTime.value = [];
};

function updateStatus(s: OrderEnum) {
  componentId.value = s as ComponentType;
  queryParams.status = s;
}

defineOptions({
  name: "Order",
});
</script>

<template>
  <div class="">
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>

      <el-row :gutter="20">
        <el-form ref="OrderQueryFormRef" :inline="true" :model="queryParams">
          <el-form-item label="关键词:">
            <el-input
              clearable
              v-model="queryParams[keyword]"
              class="sm:w-330px sm:min-w-200px"
              placeholder="请输入关键词"
              style="width: 280px"
              @clear="handleQuery"
              @keyup.enter="handleQuery"
            >
              <template #prepend>
                <el-select
                  v-model="keywordSelect"
                  placeholder="关键词"
                  style="width: 100px"
                >
                  <el-option
                    v-for="(k, i) in keywordOptions"
                    :key="i"
                    :label="k.label"
                    :value="k.value"
                  />
                </el-select>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="下单时间:" v-if="!appStore.isMobile">
            <el-date-picker
              v-model="orderTime"
              type="datetimerange"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD hh:mm:ss"
              date-format="YYYY/MM/DD ddd"
              time-format="A hh:mm:ss"
              class="sm:w-330px sm:min-w-200px"
              @change="handleOrderTimeChange"
            />
          </el-form-item>

          <template v-else>
            <el-form-item label="开始时间:">
              <el-date-picker
                v-model="queryParams.beginTime"
                type="datetime"
                value-format="YYYY-MM-DD hh:mm:ss"
                placeholder="开始时间"
                style="width: 280px"
                @clear="handleQuery"
              />
            </el-form-item>

            <el-form-item label="结束时间:">
              <el-date-picker
                v-model="queryParams.endTime"
                type="datetime"
                value-format="YYYY-MM-DD hh:mm:ss"
                placeholder="结束时间"
                style="width: 280px"
                @clear="handleQuery"
              />
            </el-form-item>
          </template>

          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleQuery"
              >搜索</el-button
            >
            <el-button type="primary" plain @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-row>

      <el-tabs
        v-model="activeOrderType"
        tab-position="top"
        @tab-change="handleTabChange"
      >
        <el-tab-pane
          v-for="tab in tabList"
          :key="tab.name"
          :label="tab.label"
          :name="tab.name"
        >
          <OrderTable
            ref="OrderTableRef"
            :loading="loading"
            :activeOrderType="activeOrderType"
            v-model:total="total"
            v-model:table="table"
            v-model:query-params="queryParams"
            @pagination="handleQuery"
          >
            <template #order="{ row, item }">
              <component
                :is="componentId"
                :row="row"
                :item="item"
                v-bind="$attrs"
                @handle-action="handleAction"
                v-on="combinedListeners"
              />
            </template>
          </OrderTable>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <OrderModel
      ref="OrderModelRef"
      :status-list="tabList"
      @handle-reason="handleReason"
      @handle-refresh="handleQuery"
    />
    <ReasonModel ref="ReasonModelRef" @handle-refresh="handleQuery" />
  </div>
</template>
