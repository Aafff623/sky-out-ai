<template>
  <view class="order-remark-input">
    <up-popup
      :show="visible"
      closeable
      :round="15"
      :customStyle="popupStyle"
      @close="closePopup"
    >
      <view class="dis-flex flex-x-center flex-y-center f-28 mb-20"
        >请填写备注</view
      >

      <view class="remark-textarea">
        <up-textarea
          count
          :focus="visible"
          :height="80"
          border="none"
          v-model="remark"
          :maxlength="50"
          placeholder="请输入订单备注内容"
          placeholderStyle="font-size: 26rpx;"
        />
      </view>

      <view class="dis-flex flex-x-center flex-y-center mt-30">
        <up-button
          type="error"
          color="#ca4545"
          shape="circle"
          text="确认备注"
          @click="submitRemark"
        />
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
});

const visible = ref(false);
const popupStyle = reactive<CSSProperties>({
  padding: "40rpx",
  minHeight: "300rpx",
  backgroundColor: "#fffff",
});
const remark = ref("");

const emits = defineEmits<{
  (e: "update:modelValue", val: string): void;
}>();

// 提交备注
const submitRemark = () => {
  emits("update:modelValue", remark.value);
  closePopup();
};

function show() {
  visible.value = true;
}

function closePopup() {
  visible.value = false;
}

defineExpose({
  show,
  closePopup,
});
</script>

<style lang="scss" scoped></style>
