<script lang="ts" setup>
import WorkspaceAPI from "@/api/workspace";
import {
  businessDataItems,
  orderDataItems,
  dishDataItems,
  setmealDataItems,
  type ModuleStates,
  type WorkspaceRequestMap,
} from "./config";
import { OrderEnum } from "@/enums/OrderEnum";
import DataTable from "./components/DataTable.vue";

const moduleStates = reactive<ModuleStates>({
  businessData: {
    data: null,
    loading: true,
  },
  orderOverView: {
    data: null,
    loading: true,
  },
  dishOverView: {
    data: null,
    loading: true,
  },
  setmealOverView: {
    data: null,
    loading: true,
  },
});
const orderStatus = ref<OrderEnum>(OrderEnum.PENDING_ORDER);

// 获取各模块数据
const fetchModuleData = async <K extends keyof WorkspaceRequestMap>(
  apiKey: K
) => {
  try {
    moduleStates[apiKey].loading = true;
    const res = await WorkspaceAPI[apiKey]();
    if (res?.code === 200) {
      // console.log(`${apiKey} 数据加载成功==>`, res.data);
      moduleStates[apiKey].data = res.data;
    }
  } catch (err) {
    // console.error(`${apiKey} 数据加载失败==>`, err);
  } finally {
    moduleStates[apiKey].loading = false;
  }
};

onMounted(async () => {
  await Promise.all([
    fetchModuleData("businessData"),
    fetchModuleData("orderOverView"),
    fetchModuleData("dishOverView"),
    fetchModuleData("setmealOverView"),
  ]);
});

defineOptions({
  name: "Workbench",
});
</script>

<template>
  <div class="workbench">
    <!-- 今日数据 -->
    <BaseCard
      title="今日数据"
      v-loading="moduleStates.businessData.loading"
      element-loading-text="今日数据加载中..."
      class="today-data-overview"
      showCurrentTime
      right-nav-text="详细数据"
      right-nav-to="/statistics/index"
    >
      <div class="statistic mt-30px mb-10px">
        <div
          class="statistic-item text-center"
          v-for="(business, businessIndex) in businessDataItems"
          :key="business.key"
          v-stagger:[businessIndex]
        >
          <div
            class="statistic-top flex-center"
            :class="{ 'translate-x-[-5px]': business?.symbol?.prefix }"
          >
            <span v-if="business?.symbol?.prefix">{{
              business.symbol.prefix
            }}</span>
            <el-statistic
              :value="
                typeof business.formatter === 'function'
                  ? business.formatter(
                      moduleStates['businessData'].data?.[business.key]
                    )
                  : moduleStates.businessData.data?.[business.key]
              "
            />
            <span v-if="business?.symbol?.suffix">{{
              business.symbol.suffix
            }}</span>
          </div>
          <div class="statistic-middle flex-center">
            <SvgIcon
              :icon-class="business.icon"
              :size="`${business.iconSize ? business.iconSize : '1.5em'}`"
            />
            <span :class="{ 'ml-6px': !business.iconSize }">{{
              business.label
            }}</span>
          </div>
          <div
            class="statistic-footer w-full truncate-2-lines cursor-pointer"
            :title="business.description"
          >
            {{ business.description }}
          </div>
        </div>
      </div>
    </BaseCard>

    <!-- 订单管理 -->
    <BaseCard
      class="mt-20px"
      title="订单管理"
      v-loading="moduleStates.orderOverView.loading"
      element-loading-text="订单管理加载中..."
      showCurrentTime
      right-nav-text="订单明细"
      right-nav-to="/order/index"
    >
      <div class="order-overview side-sort-card">
        <router-link
          class="side-sort-item"
          v-for="(order, orderIndex) in orderDataItems"
          :key="order.key"
          :to="{
            path: '/order/index',
            query: order.query,
          }"
          v-stagger:[orderIndex]
        >
          <div class="left-icon flex-center">
            <SvgIcon
              :icon-class="order.icon"
              :size="order.iconSize || '2.5em'"
            />
            <span class="ml-6px text-0.875rem">{{ order.label }}</span>
          </div>
          <div class="right-number font-bold text-1.625rem">
            {{ moduleStates.orderOverView.data?.[order.key] || 0 }}
          </div>
        </router-link>
      </div>
    </BaseCard>

    <!-- 菜品、套餐总览 -->
    <el-row class="my-20px" :gutter="10">
      <!-- 菜品总览 -->
      <el-col
        class="sm-mb-20px md-mb-0"
        :xl="12"
        :lg="12"
        :md="12"
        :sm="12"
        :xs="24"
      >
        <BaseCard
          title="菜品总览"
          v-loading="moduleStates.dishOverView.loading"
          element-loading-text="菜品总览加载中..."
          right-nav-text="新增菜品"
          :right-nav-to="{
            path: '/dish/index',
            query: {
              type: 'create',
            },
          }"
        >
          <div class="dish-overview status-tag side-sort-card">
            <router-link
              class="dish-overview-item status-tag-item side-sort-item"
              v-for="(dish, dishIndex) in dishDataItems"
              :key="dish.key"
              :to="{
                path: '/dish/index',
                query: dish.query,
              }"
              v-stagger:[dishIndex]
            >
              <div class="left-icon flex-center">
                <SvgIcon
                  :icon-class="dish.icon"
                  :size="dish.iconSize || '2.5em'"
                />
                <span class="ml-6px text-0.875rem">{{ dish.label }}</span>
              </div>

              <div class="right-number font-bold text-1.625rem">
                {{ moduleStates.dishOverView.data?.[dish.key] || 0 }}
              </div>

              <div
                class="status-tag-type"
                :class="{ active: dish.label === '已启售' }"
              >
                {{ dish.label === "已启售" ? "在售中" : "已停售" }}
              </div>
            </router-link>
          </div>
        </BaseCard>
      </el-col>

      <!-- 套餐总览 -->
      <el-col :xl="12" :lg="12" :md="12" :sm="12" :xs="24">
        <BaseCard
          title="套餐总览"
          v-loading="moduleStates.setmealOverView.loading"
          element-loading-text="套餐总览加载中..."
          right-nav-text="新增套餐"
          :right-nav-to="{
            path: '/setmeal/index',
            query: {
              type: 'create',
            },
          }"
        >
          <div class="setmeal-overview status-tag side-sort-card">
            <router-link
              class="setmeal-overview-item status-tag-item side-sort-item"
              v-for="(setmeal, setmealIndex) in setmealDataItems"
              :key="setmeal.key"
              :to="{
                path: '/setmeal/index',
                query: setmeal.query,
              }"
              v-stagger:[setmealIndex]
            >
              <div class="left-icon flex-center">
                <SvgIcon
                  :icon-class="setmeal.icon"
                  :size="setmeal.iconSize || '2.5em'"
                />
                <span class="ml-6px text-0.875rem">{{ setmeal.label }}</span>
              </div>
              <div class="right-number font-bold text-1.625rem">
                {{ moduleStates.setmealOverView.data?.[setmeal.key] || 0 }}
              </div>

              <div
                class="status-tag-type"
                :class="{ active: setmeal.label === '已启售' }"
              >
                {{ setmeal.label === "已启售" ? "在售中" : "已停售" }}
              </div>
            </router-link>
          </div>
        </BaseCard>
      </el-col>
    </el-row>

    <!-- 订单信息 -->
    <BaseCard title="订单信息" class="order-table-overview">
      <template #right-nav>
        <div class="right-nav-btn">
          <el-radio-group v-model="orderStatus">
            <el-radio-button label="待接单" :value="OrderEnum.PENDING_ORDER" />
            <el-radio-button label="待出餐" :value="OrderEnum.DINE_IN_ORDER" />
            <el-radio-button label="待派送" :value="OrderEnum.ORDERS_TAKEN" />
          </el-radio-group>
        </div>
      </template>

      <DataTable ref="JTable" :status="orderStatus" />
    </BaseCard>
  </div>
</template>

<style lang="scss" scoped>
@mixin num-count-style() {
  font-size: 1.625rem;
  font-weight: bold;
}

@mixin grid-auto-fit() {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
}

@mixin box-common-style() {
  padding: 20px;
  border-radius: 10px;
  box-shadow: var(--el-box-shadow-light);
  background-color: var(--el-bg-color-overlay);
}

@mixin fade-in() {
  animation: fadeIn 0.6s ease-in-out 1;
}

// 左右排序卡片
.side-sort-card {
  @include grid-auto-fit();
  @apply mt-30px mb-10px;
  cursor: pointer;

  .side-sort-item {
    @include box-common-style();
    @apply flex items-center justify-between;
    padding: 1.5625rem;
    border: 1px solid var(--el-bg-color-overlay);
    transition: transform 0.3s ease-in-out;

    &:hover {
      transform: scale(0.98);
      opacity: 0.9;
      border-color: var(--el-color-primary);
    }
  }
}

.status-tag {
  .status-tag-item {
    position: relative;
    overflow: hidden;

    .status-tag-type {
      position: absolute;
      top: 0;
      left: 0;
      background: var(--el-color-info);
      font-size: 12px;
      text-align: center;
      color: var(--el-color-white);
      transform: rotate(45deg) scale(0.9);
      transform-origin: 2.8125rem 2.8125rem;
      writing-mode: vertical-rl;
      letter-spacing: 2px;
      height: 100%;
      padding: 0 1px;
    }

    .active {
      background-color: var(--el-color-primary);
    }
  }
}

.workbench {
  // 今日数据
  .today-data-overview {
    .statistic {
      @include grid-auto-fit();

      .statistic-item {
        @include box-common-style();

        :deep(.el-statistic) {
          .el-statistic__number {
            @include num-count-style();
          }
        }

        .statistic-top {
          @include num-count-style();
        }

        .statistic-middle {
          font-size: 0.8438rem;
        }

        .statistic-footer {
          font-size: 12px;
          color: var(--el-text-color-secondary);
          margin-top: 10px;
        }
      }
    }
  }

  // 菜品总览
  .dish-overview {
    &-item {
      padding-left: 25px;
    }
  }
}

.truncate-2-lines {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.5);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 639px) {
  .dish-setmeal-overview {
    margin-bottom: 20px;
  }
}
</style>
