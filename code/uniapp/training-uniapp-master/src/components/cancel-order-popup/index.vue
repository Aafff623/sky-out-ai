<template>
  <view class="cancel-order-popup">
    <up-popup
      :show="visible"
      closeable
      :round="15"
      :customStyle="popupStyle"
      @close="closePopup"
    >
      <view class="dis-flex flex-x-center flex-y-center f-28 mb-20"
        >取消订单原因</view
      >

      <view class="reason-list">
        <view
          class="reason-item"
          v-for="(item, index) in cancelReasonList"
          :key="index"
          @click="onSelectCancelReason(item.value)"
        >
          <view class="reason-item-label">{{ item.label }}</view>

          <view class="reason-item-radio">
            <up-radio-group
              v-model="reasonRadioVal"
              placement="column"
              activeColor="#a32437"
              @change="onSelectCancelReason"
            >
              <up-radio :name="item.value" />
            </up-radio-group>
          </view>
        </view>

        <view class="other-reason" v-if="reasonRadioVal === 'other'">
          <up-textarea
            count
            :focus="visible"
            :height="80"
            border="none"
            v-model="otherCancelReason"
            :maxlength="50"
            placeholder="请输入订单取消原因，帮助我们改进"
            placeholderStyle="font-size: 26rpx;"
          />
        </view>

        <view class="dis-flex flex-x-center flex-y-center mt-30">
          <up-button
            type="error"
            color="#ca4545"
            shape="circle"
            text="提交"
            @click="submitCancelReason"
          />
        </view>
      </view>
    </up-popup>
  </view>
</template>

<script lang="ts" setup>
import { CSSProperties } from "vue";

defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  // 是否超时
  isTimeout: {
    type: Boolean,
    default: false,
  },
});

const visible = ref(false);
const popupStyle = reactive<CSSProperties>({
  padding: "40rpx",
  minHeight: "400rpx",
  backgroundColor: "#fffff",
});
const reasonRadioVal = ref("");
const otherCancelReason = ref("");
const cancelReasonList = ref([
  { label: "不想要了/临时有时", value: "不想要了/临时有事" },
  { label: "点错了/点多了/点少了", value: "点错了/点多了/点少了" },
  { label: "地址/电话填错了", value: "地址/电话填错了" },
  { label: "等待时间长", value: "等待时间长" },
  { label: "其他原因", value: "other" },
]);

const emits = defineEmits<{
  (e: "update:modelValue", val: string): void;
  (e: "change"): void;
}>();

// 选择取消原因
const onSelectCancelReason = (reason: string) => {
  reasonRadioVal.value = reason;
};

// 提交取消原因
const submitCancelReason = async () => {
  // 如果没有选择原因，则提示
  if (reasonRadioVal.value === "") {
    uni.$u.toast("请选择取消原因");
    return;
  }

  // 如果选择其他原因，则需要输入原因
  if (reasonRadioVal.value === "other" && otherCancelReason.value === "") {
    uni.$u.toast("请输入订单取消原因");
    return;
  }

  emits(
    "update:modelValue",
    reasonRadioVal.value !== "other"
      ? reasonRadioVal.value
      : otherCancelReason.value
  );
  emits("change");
};

function show() {
  visible.value = true;
}

function closePopup() {
  visible.value = false;
  // 重置数据
  reasonRadioVal.value = "";
  otherCancelReason.value = "";
}

defineExpose({
  show,
  closePopup,
});
</script>

<style lang="scss" scoped>
.reason-list {
  padding: 30rpx;
  .reason-item {
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15rpx 0;
    border-bottom: 3rpx solid #f2f2f2;

    &:first-child {
      border-top: 3rpx solid #f2f2f2;
    }

    &-label {
      font-size: 25rpx;
    }
  }
}
</style>
