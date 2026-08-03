<template>
  <view class="pay-success">
    <view class="dis-flex flex-dir-column flex-y-center">
      <text class="iconfont icon-pay-success" style="font-size: 180rpx" />
      <view class="f-w mt-20 mb-20 f-35">下单成功</view>
      <view class="f-25 mb-30" style="color: #a6a2a2"
        >菜品已加急制作中，请耐心等待~</view
      >

      <view class="dis-flex flex-y-center">
        <up-button
          text="返回首页"
          :custom-style="{ 'margin-right': '50rpx' }"
          @click="onBackHome"
        ></up-button>
        <up-button
          type="warning"
          text="查看订单"
          @click="onShowOrderDetail"
        ></up-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { onLoad } from "@dcloudio/uni-app";
import { OrderSubmitVO } from "@/api/order";
import { ComponentInternalInstance } from "@vue/runtime-core";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const orderInfo = reactive<OrderSubmitVO>({});

onLoad((options: any) => {
  Object.assign(orderInfo, options);
});

const onShowOrderDetail = () => {
  uni.showModal({
    title: "功能未开发",
    content: "返回首页？",
    showCancel: true,
    success({ confirm }) {
      if (confirm) {
        onBackHome();
      }
    },
  });
};

const onBackHome = () => {
  proxy?.$navTo("pages/index/index", {}, "redirectTo");
};
</script>

<style lang="scss" scoped>
.pay-success {
  height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
