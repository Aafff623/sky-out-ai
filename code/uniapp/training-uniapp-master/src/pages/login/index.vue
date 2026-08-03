<template>
  <view class="container">
    <view class="authorize">
      <view class="store-info">
        <!-- logo -->
        <view class="header">
          <up-image
            :src="userStore.getShopRecord?.logo"
            shape="circle"
            :width="95"
            :height="95"
            mode="aspectFill"
          />
        </view>
      </view>
      <view class="auth-title t-c">申请获取以下权限</view>
      <view class="auth-subtitle t-c">获得你的公开信息（昵称、头像等）</view>
      <view class="login-btn">
        <view class="button" @click="handleLogin()">一键登录</view>
      </view>
      <view class="no-login-btn">
        <view class="button" @click="handleCancel()">暂不登录</view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { useUserStore } from "@/store";
import { onShow } from "@dcloudio/uni-app";
import { ComponentInternalInstance } from "vue";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const userStore = useUserStore();

onShow(async () => {
  // 每次页面显示获取一次店铺信息
  await userStore.getShopInfo();
});

const handleLogin = async () => {
  const code = await getCode();
  const res = await userStore.loginMpWx(code);
  if (res.code === 200) {
    proxy?.$toast("登录成功");
    setTimeout(() => onNavigateBack(), 2000);
  } else {
    proxy?.$toast(res.msg || "登录失败");
  }
};

const handleCancel = () => {
  // 跳转回原页面
  onNavigateBack();
};

/**
 * 获取微信授权码
 * @link https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html
 */
function getCode() {
  return new Promise<string>((resolve, reject) => {
    uni.login({
      provider: "weixin",
      success({ code }) {
        resolve(code);
      },
      fail: reject,
    });
  });
}

function onNavigateBack(delta: number = 1) {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack({
      delta: Number(delta || 1),
    });
  } else {
    proxy?.$navTo("pages/order/index");
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 0 60rpx;
  font-size: 32rpx;
  background: #fff;
  min-height: 100vh;
}

.authorize {
  .store-info {
    padding: 80rpx 0 48rpx;
    border-bottom: 1rpx solid #e3e3e3;
    margin-bottom: 70rpx;
    text-align: center;

    .header {
      width: 190rpx;
      height: 190rpx;
      border: 4rpx solid #fff;
      margin: 0 auto 0;
      border-radius: 50%;
      overflow: hidden;
      box-shadow: 2rpx 0 10rpx rgba(50, 50, 50, 0.3);

      .image {
        display: block;
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .auth-title {
    color: #585858;
    font-size: 34rpx;
    margin-bottom: 40rpx;
  }

  .auth-subtitle {
    color: #888;
    margin-bottom: 80rpx;
    font-size: 28rpx;
  }
}

.login-btn {
  margin-bottom: 20rpx;

  .button {
    width: 100%;
    height: 86rpx;
    background: linear-gradient(270deg, #ffaf65 0%, #ff795b 100%);
    color: #fff;
    font-size: 30rpx;
    border-radius: 80rpx;
    letter-spacing: 5rpx;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.no-login-btn {
  .button {
    height: 86rpx;
    background: #dfdfdf;
    color: #fff;
    font-size: 30rpx;
    border-radius: 80rpx;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
</style>
