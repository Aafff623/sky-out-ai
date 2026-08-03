<template>
  <view class="select-table-ware">
    <up-popup
      :show="visible"
      closeable
      :round="15"
      :customStyle="popupStyle"
      @close="closePopup"
    >
      <view class="tableware-content">
        <view class="address">
          <text class="iconfont icon-location location-icon" />
          <view
            class="address-info"
            v-if="defaultAddress && !_.isEmpty(defaultAddress)"
          >
            <view class="detail oneline-hide">{{ detailAddress }}</view>
            <view class="consignee oneline-hide">{{ consigneeInfo }}</view>
          </view>

          <view class="address-info f-25" v-else>地址暂未选择</view>
        </view>

        <view class="tableware-card">
          <view class="tableware-body">
            <view class="tip-title">当前地址下单，是否需要餐具？</view>

            <!-- 是否需要餐具选择 -->
            <view class="dis-flex flex-y-center flex-x-center mb-30">
              <view
                class="tableware-item"
                :class="{ active: needTableware }"
                @click="onSelectNeedTableware"
              >
                <view class="tableware-item-title">需要餐具</view>
              </view>

              <view
                class="tableware-item"
                :class="{
                  active: !needTableware && needTableware !== undefined,
                }"
                @click="onSelectNoTableware"
              >
                <view class="tableware-item-title mb-10">无需餐具</view>
                <view class="dis-flex flex-x-center flex-y-center">
                  <text class="iconfont icon-environment environment-icon" />
                  <text class="tableware-item-desc">助力环保，减碳38g</text>
                </view>
              </view>
            </view>

            <!-- 需要餐具，选择餐具数量 -->
            <block v-if="needTableware && needTableware !== undefined">
              <view class="tip-title mb-20">选择餐具数量</view>

              <view class="select-tableware">
                <!-- 按量提供 -->
                <view
                  class="by-size"
                  :class="{ selected: isBySize }"
                  @click="onSelectBySize"
                  >商家按餐具数量提供</view
                >

                <!-- 选择具体数量 -->
                <view
                  class="by-number"
                  :class="{ selected: !isBySize }"
                  @click="onSelectByNumber"
                >
                  <up-number-box v-model="tablewareNum" :min="1" :max="15">
                    <template #minus>
                      <view class="minus">
                        <up-icon name="minus" color="#000" bold size="12" />
                      </view>
                    </template>
                    <template #input>
                      <text class="tableware-number">{{ tablewareNum }}份</text>
                    </template>
                    <template #plus>
                      <view class="plus">
                        <up-icon name="plus" color="#000" bold size="12" />
                      </view>
                    </template>
                  </up-number-box>
                </view>
              </view>
            </block>
          </view>
        </view>

        <!-- 保存按钮 -->
        <view class="save-btn">
          <up-button
            text="保存"
            :disabled="needTableware === undefined"
            shape="circle"
            color="#cb4544"
            @click="handleCutlerySelectionConfirm"
          />
        </view>
      </view>
    </up-popup>
  </view>
</template>

<script lang="ts" setup>
import * as _ from "lodash";
import { CSSProperties } from "vue";
import { AddressBook } from "@/api/address";
import { SexEnum } from "@/enums/AddressEnum";
import { TablewareStatusEnum } from "@/enums/OrderEnum";

export type DefaultValue = {
  is_need?: boolean;
  cutlery_num?: number;
  tableware_status?: TablewareStatusEnum;
};

const props = defineProps({
  modelValue: {
    type: [Number, String] as PropType<TablewareStatusEnum>,
    default: TablewareStatusEnum.BY_SIZE,
  },
  // 默认地址
  defaultAddress: {
    type: Object as PropType<AddressBook>,
    default: () => {},
  },
  // 默认选中
  defaultValue: {
    type: Object as PropType<DefaultValue>,
    default: () => {},
  },
});

const visible = ref(false);
const popupStyle = reactive<CSSProperties>({
  padding: "40rpx",
  minHeight: "400rpx",
  backgroundColor: "#f3f3f3",
});
// 是否需要餐具
const needTableware = ref<boolean | undefined>();
const tablewareNum = ref(1);

// 餐具数量变化，更新modelValue为选择餐具数量
watch(
  () => tablewareNum.value,
  (val) => {
    if (val === TablewareStatusEnum.BY_SIZE) return;
    emits("update:modelValue", TablewareStatusEnum.BY_NUMBER);
  }
);

const emits = defineEmits<{
  (e: "update:modelValue", val: TablewareStatusEnum): void;
  (e: "updateCutleryQuantity", val: number | string): void;
  (e: "selectCompelete", val: DefaultValue): void;
}>();

const detailAddress = computed(() => {
  const { provinceName, cityName, districtName, detail } = props.defaultAddress;
  return `${provinceName} ${cityName} ${districtName} ${detail}`;
});

const consigneeInfo = computed(() => {
  const { consignee, sex, phone } = props.defaultAddress;
  return `${consignee} ${sex === SexEnum.MALE ? "先生" : "女士"} ${phone}`;
});

const isBySize = computed(() => {
  return props.modelValue === TablewareStatusEnum.BY_SIZE;
});

// 需要餐具
const onSelectNeedTableware = () => {
  needTableware.value = true;
};

// 无需餐具
const onSelectNoTableware = () => {
  needTableware.value = false;
};

// 选择商家按量提供
const onSelectBySize = () => {
  emits("update:modelValue", TablewareStatusEnum.BY_SIZE);
};

// 选择具体数量
const onSelectByNumber = () => {
  emits("update:modelValue", TablewareStatusEnum.BY_NUMBER);
};

const handleCutlerySelectionConfirm = () => {
  // 用户选择了`无需餐具`（状态为：0选择具体数量，餐具数量为0）
  if (!needTableware.value && needTableware.value !== undefined) {
    emits("updateCutleryQuantity", "no_cutlery");
    emits("selectCompelete", {
      is_need: false,
      cutlery_num: 0,
      tableware_status: TablewareStatusEnum.BY_NUMBER,
    });
    closePopup();
    return;
  }

  // 选择具体餐具数量
  if (props.modelValue === TablewareStatusEnum.BY_NUMBER) {
    emits("updateCutleryQuantity", tablewareNum.value);
    emits("selectCompelete", {
      is_need: true,
      cutlery_num: tablewareNum.value,
      tableware_status: TablewareStatusEnum.BY_NUMBER,
    });
    closePopup();
    return;
  }

  // 按量提供
  emits("updateCutleryQuantity", "");

  // 选择完毕
  emits("selectCompelete", {
    is_need: true,
    cutlery_num: undefined,
    tableware_status: TablewareStatusEnum.BY_SIZE,
  });

  // 关闭弹窗
  closePopup();
};

// 设置默认选中
function setDefaultValue(val: DefaultValue) {
  nextTick(() => {
    needTableware.value = val.is_need;
    tablewareNum.value = val.cutlery_num || 1;
    emits(
      "update:modelValue",
      val.tableware_status ?? TablewareStatusEnum.BY_SIZE
    );
  });
}

function show(record?: DefaultValue) {
  visible.value = true;
  record && !_.isEmpty(record) && setDefaultValue(record);
}

function closePopup() {
  visible.value = false;
}

// 重置数据
function reset() {
  tablewareNum.value = 1;
  needTableware.value = undefined;
}

defineExpose({
  show,
  reset,
  setDefaultValue,
  closePopup,
});
</script>

<style lang="scss">
:deep(.select-table-ware) {
  .u-card {
    .u-card__body {
      padding: 20rpx !important;
    }
  }
}
</style>

<style lang="scss" scoped>
.address {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 20rpx;

  .location-icon {
    font-size: 40rpx;
    transform: translateY(-4rpx);
  }

  .address-info {
    color: #5a5a5a;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: flex-start;

    .detail {
      font-size: 22rpx;
      color: #5a5a5a;
      margin-bottom: 10rpx;
    }

    .consignee {
      font-size: 20rpx;
      color: #848484;
    }
  }
}

.tableware-card {
  background-color: #fff;
  padding: 20rpx;
  border-radius: 15rpx;

  .tableware-body {
    .tip-title {
      font-size: 26rpx;
      font-weight: bold;
      margin-bottom: 20rpx;
    }

    .tableware-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background-color: #fafafa;
      border: 2rpx solid rgb(220, 220, 220, 0.7);
      border-radius: 10rpx;
      padding: 25rpx 20rpx;
      height: 50rpx;

      &-title {
        font-size: 26rpx;
        color: #616161;
        font-weight: bold;
      }

      &-desc {
        font-size: 20rpx;
        color: #b0aeae;
      }

      .environment-icon {
        font-size: 26rpx;
        margin-right: 7rpx;
      }

      &:first-child {
        margin-right: 18rpx;
      }
    }

    .active {
      border-color: #ca4545;
      background-color: rgba(202, 69, 69, 0.1);
      .tableware-item-title,
      text {
        color: #ca4545;
      }
    }
  }
}

.select-tableware {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .by-size,
  .by-number {
    height: 30rpx;
    flex: 1;
    border: 2rpx solid rgb(220, 220, 220, 0.7);
    font-size: 22rpx;
    display: flex;
    align-items: center;
    border-radius: 10rpx;
  }

  .by-size {
    padding: 15rpx;
    margin-right: 15rpx;
    justify-content: center;
  }

  .by-number {
    padding: 15rpx 30rpx;
    justify-content: space-between;

    .tableware-number {
      width: 100rpx;
      text-align: center;
    }
  }

  .selected {
    color: #ca4545;
    border-color: #ca4545;
    background-color: rgba(202, 69, 69, 0.1);

    .tableware-number {
      color: black;
    }
  }
}

.save-btn {
  margin-top: 30rpx;
}
</style>
