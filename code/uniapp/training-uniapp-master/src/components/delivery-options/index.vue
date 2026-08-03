<template>
  <view class="delivery-options dis-flex flex-dir-column w-full">
    <!-- 只有在营业时间内并且营业中才能选择立即配送（1） v-if="isOperating && isInOperatingTime" -->
    <view
      class="delivery-item mb-10"
      :class="{ active: activeClass }"
      @click="handleImmediatelyDelivery"
    >
      <view class="text">立即配送</view>
      <view class="time">预计{{ immediateDeliveryTime }}送达</view>

      <text
        class="iconfont icon-delivery-checked icon"
        :class="{ 'icon-checked': activeClass }"
      />
    </view>

    <!-- 预约配送（0） -->
    <view
      class="delivery-item"
      @click="selectReservationDelivery"
      :class="{ active: !activeClass }"
    >
      <view class="text">预约配送</view>
      <view class="time dis-flex flex-x-center">
        <text class="mr-10">{{ reservationTime }}</text>
        <up-icon
          name="arrow-right"
          :color="!activeClass ? '#ca4545' : '#767575'"
          size="10"
        />
      </view>

      <text
        class="iconfont icon-delivery-checked icon"
        :class="{ 'icon-checked': !activeClass }"
      />
    </view>

    <!-- 选择预约送达时间 -->
    <up-popup
      closeable
      :round="15"
      :customStyle="{
        padding: '40rpx 0',
        minHeight: '400rpx',
        maxHeight: '450rpx',
      }"
      :show="showReservationPopup"
      @open="openReservationPopup"
      @close="closeReservationPopup"
    >
      <view class="popup-content">
        <view class="title">选择预计送达时间</view>

        <view class="menu-wrap">
          <!-- 左侧菜单 -->
          <scroll-view scroll-y class="tab-view">
            <view
              v-for="(item, index) in dayOptions"
              :key="index"
              class="tab-item"
              @tap.stop="onSelectMenu(item)"
              :class="[currentDayType == item.type ? 'tab-item-active' : '']"
            >
              <text class="tab-item-text">{{ item.name }}</text>
            </view>
          </scroll-view>

          <!-- 选择进入预计送达时间列表 -->
          <scroll-view
            v-if="currentDayType === DeliveryDateTypeEnum.TODAY"
            scroll-y
            :scroll-top="scrollTop"
            class="right-box today-list"
            @scroll="onScroll"
          >
            <view
              v-for="(today, ti) in todayTime"
              :key="ti"
              class="time-item"
              @click="handleReservationDelivery(today)"
              :class="[currentTimeId === today.id ? 'time-item-active' : '']"
            >
              <view class="time-item-text">{{ today.text }}</view>
              <up-icon
                v-if="currentTimeId === today.id"
                name="checkbox-mark"
                color="#ca4545"
                size="20"
              />
            </view>
          </scroll-view>

          <!-- 选择明天预计送达时间列表 -->
          <scroll-view
            v-else
            scroll-y
            :scroll-top="scrollTop"
            class="right-box tomorrow-list"
          >
            <view
              v-for="(tomorrow, ti) in tomorrowTime"
              :key="ti"
              class="time-item"
              @click="handleReservationDelivery(tomorrow)"
              :class="[currentTimeId === tomorrow.id ? 'time-item-active' : '']"
            >
              <view class="time-item-text">{{ tomorrow.text }}</view>
              <up-icon
                v-if="currentTimeId === tomorrow.id"
                name="checkbox-mark"
                color="#ca4545"
                size="20"
              />
            </view>
          </scroll-view>
        </view>
      </view>
    </up-popup>
  </view>
</template>

<script lang="ts" setup>
// import { useShopStore } from "@/store";
import { DeliveryStatusEnum, DeliveryDateTypeEnum } from "@/enums/OrderEnum";
import {
  getDeliveryTimeRange,
  getTodayWeekday,
  getTomorrowWeekday,
  calculateTimeIntervals,
} from "@/utils";
import { CustomEvent } from "@uni-helper/uni-app-types/index";

type DateItem = {
  id: string;
  text: string;
};

const props = defineProps({
  modelValue: {
    type: Number as PropType<DeliveryStatusEnum>,
    default: DeliveryStatusEnum.IMMEDIATELY,
  },
});

// const shopStore = useShopStore();
// const { isOperating, isInOperatingTime } = storeToRefs(shopStore);
const showReservationPopup = ref(false);
const immediateDeliveryTime = ref(getDeliveryTimeRange().join("-"));
const dayOptions = ref([
  {
    name: `今天(${getTodayWeekday()})`,
    type: DeliveryDateTypeEnum.TODAY,
  },
  {
    name: `明天(${getTomorrowWeekday()})`,
    type: DeliveryDateTypeEnum.TOMORROW,
  },
]);
const scrollTop = ref(0);
const savedScrollTop = ref(0); // 保存当前滚动位置
const currentTimeId = ref<DateItem["id"]>();
const currentDayType = ref<DeliveryDateTypeEnum>(DeliveryDateTypeEnum.TODAY);
const todayTime = ref<DateItem[]>([]);
const tomorrowTime = ref<DateItem[]>([]);
const reservationTime = ref<string>("选择预约配送时间");

const activeClass = computed(
  () => props.modelValue === DeliveryStatusEnum.IMMEDIATELY
);

const emits = defineEmits<{
  (e: "update:modelValue", val: DeliveryStatusEnum): void;
}>();

onMounted(() => {
  calcInitTime();
});

// 每次切换配送方式时，更新配送时间
watch(
  () => props.modelValue,
  (val) => {
    // 每次重新切换回立即配送时，更新配送时间
    if (val === DeliveryStatusEnum.IMMEDIATELY) {
      immediateDeliveryTime.value = getDeliveryTimeRange().join("-");
    }
  }
);

function calcInitTime() {
  // 计算`今天`的送达时间区间
  todayTime.value = calcTodyTimeIntervals();
  // console.log("今天的送达时间区间==>", todayTime.value);

  // 计算`明天`的送达时间区间
  const tomorrow = calculateTimeIntervals();
  tomorrowTime.value = formatTimeIntervals(
    tomorrow,
    DeliveryDateTypeEnum.TOMORROW
  );
}

// 立即配送
const handleImmediatelyDelivery = () => {
  emits("update:modelValue", DeliveryStatusEnum.IMMEDIATELY);
  // 重置预约配送popup数据
  resetData();
};

// 选择预约配送时间
const handleReservationDelivery = (date: DateItem) => {
  if (currentTimeId.value === date.id) {
    return;
  }
  if (date.id === todayTime.value[0].id) {
    currentTimeId.value = date.id;
    emits("update:modelValue", DeliveryStatusEnum.IMMEDIATELY);
    closeReservationPopup();
    return;
  }

  currentTimeId.value = date.id;
  const item = dayOptions.value.find((v) => v.type === currentDayType.value);
  // 记录选择的预约配送时间
  reservationTime.value = `${item?.name}${date.text}送达`;
  emits("update:modelValue", DeliveryStatusEnum.RESERVATION);
  // 关闭弹窗
  closeReservationPopup();
};

const selectReservationDelivery = () => {
  showReservationPopup.value = true;
};

// 点击左侧菜单
const onSelectMenu = (item: { name: string; type: DeliveryDateTypeEnum }) => {
  currentDayType.value = item.type;
};

// 打开预约配送弹窗
function openReservationPopup() {
  // 每次打开弹窗时，重新计算`今天`和`明天`的送达时间区间
  calcInitTime();

  // 如果有已选择的 timeId，判断是今天还是明天
  if (currentTimeId.value) {
    const isToday = todayTime.value.some((v) => v.id === currentTimeId.value);
    currentDayType.value = isToday
      ? DeliveryDateTypeEnum.TODAY
      : DeliveryDateTypeEnum.TOMORROW;

    // 获取选中项在列表中的索引
    const list = isToday ? todayTime.value : tomorrowTime.value;
    const selectedIndex = list.findIndex(
      (item) => item.id === currentTimeId.value
    );
    // 如果找到选中的项，则计算 scrollTop
    if (selectedIndex !== -1) {
      scrollTop.value = savedScrollTop.value; // 恢复滚动位置
      // 组件属性设置不生效解决办法：https://uniapp.dcloud.net.cn/tutorial/vue-api.html#componentsolutions
      nextTick(() => {
        getElRect("time-item").then(() => {});
        // 重新滚动
        const itemHeight = 45; // 动态获取 .time-item 的高度，但是获取到的始终为null，暂时用固定值代替
        scrollTop.value = selectedIndex * itemHeight;
      });
    }
    return;
  }

  // 如果是立即配送，默认选择今天的第一个时间
  if (props.modelValue === DeliveryStatusEnum.IMMEDIATELY) {
    currentDayType.value = DeliveryDateTypeEnum.TODAY;
    currentTimeId.value = todayTime.value[0]?.id; // 默认选择今天第一个时间
  }
}

// 监听 scroll 事件，保存滚动位置
const onScroll = (event: CustomEvent) => {
  savedScrollTop.value = event.detail.scrollTop; // 保存当前滚动位置
};

// 获取一个目标元素的高度
const count = ref(0); // 循环计数器，最多尝试10次
function getElRect(elClass: string) {
  return new Promise<UniApp.NodeInfo | UniApp.NodeInfo[] | null>((resolve) => {
    const query = uni.createSelectorQuery();
    query
      .select("." + elClass)
      .boundingClientRect((res) => {
        // 如果节点尚未生成，res值为null，循环调用执行
        if (!res) {
          count.value++;
          if (count.value > 10) {
            console.error(`获取元素高度失败，类名【${elClass}】`);
            resolve(null);
            return;
          }
          setTimeout(() => {
            getElRect(elClass);
          }, 10);
          return;
        }
        resolve(res);
      })
      .exec();
  });
}

// 关闭预约配送弹窗
function closeReservationPopup() {
  showReservationPopup.value = false;
  savedScrollTop.value = 0; // 关闭时重置滚动位置
}

// 计算`今天`的送达时间区间
function calcTodyTimeIntervals() {
  const startTime = new Date();
  // 推迟30分钟
  startTime.setMinutes(startTime.getMinutes() + 30);
  const todayTime = calculateTimeIntervals(startTime);
  todayTime.unshift("立即送出");
  return formatTimeIntervals(todayTime, DeliveryDateTypeEnum.TODAY);
}

// 格式化时间区间
function formatTimeIntervals(
  intervals: string[],
  type?: DeliveryDateTypeEnum
): DateItem[] {
  const timeList =
    type === DeliveryDateTypeEnum.TODAY ? todayTime : tomorrowTime;

  // 如果时间列表已有值，直接更新它们的 text 值
  if (timeList.value.length !== 0) {
    return (timeList.value as DateItem[]).map((v, i) => ({
      ...v,
      text: intervals[i],
    }));
  }

  return intervals.map((v) => ({
    id: generateSecureRandomId(8),
    text: v,
  }));
}

// 生成随机id
function generateSecureRandomId(length = 16) {
  return [...Array(length)]
    .map(() => Math.floor(Math.random() * 36).toString(36))
    .join("");
}

// 重置数据
function resetData() {
  currentTimeId.value = undefined;
  currentDayType.value = DeliveryDateTypeEnum.TODAY;
}
</script>

<style lang="scss" scoped>
.delivery-options {
  .delivery-item {
    display: flex;
    align-items: center;
    border-radius: 20rpx;
    border: 3rpx solid #ececec;
    padding: 0 20rpx;
    min-height: 80rpx;
    position: relative;

    .text,
    .time {
      font-size: 25rpx;
      font-weight: bold;
    }

    .text {
      color: black;
      margin-right: 30rpx;
    }

    .time {
      color: #767575;
    }
    .icon {
      display: none;
    }
  }

  // 选中时样式
  .active {
    border: 3rpx solid #e6b1b1;
    .text,
    .time {
      color: #ca4545;
    }

    .icon-checked {
      display: block;
      position: absolute;
      right: -24rpx;
      top: -10rpx;
      font-size: 40rpx;
    }
  }
}

.popup-content {
  height: 100%;
  .title {
    color: black;
    text-align: center;
    font-weight: bold;
    font-size: 28rpx;
    margin-bottom: 30rpx;
  }

  .menu-wrap {
    display: flex;
    height: calc(100% - 66rpx);
  }

  // 左侧菜单
  .tab-view {
    flex: 1.5;
    background-color: #f6f6f6;

    .tab-item {
      height: 80rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      color: black;
      font-size: 25rpx;
    }

    .tab-item-active {
      background-color: #fff;
    }
  }

  // 右侧时间选择列表
  .right-box {
    flex: 3;
    padding-left: 35rpx;

    .time-item {
      height: 90rpx;
      display: flex;
      padding: 0 20rpx 0 5rpx;
      align-items: center;
      justify-content: space-between;
      color: black;
      font-size: 25rpx;
      border-bottom: 2rpx solid #eee;
    }

    .time-item-active {
      color: #ca4545;
    }
  }
}
</style>
