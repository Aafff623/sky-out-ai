<template>
  <view class="user">
    <view class="user-info" :style="[userInfoWrapStyle]">
      <view class="bg-image"></view>

      <!-- 已登录 -->
      <view class="is-login" v-if="isLogin">
        <view class="avatar-name-wrap" @click.stop="onTargetCompleteUserInfo">
          <view class="user-avatar">
            <avatar-image v-if="userInfo.avatar" :url="userInfo.avatar" />
            <up-avatar v-else src="/static/default-avatar.png" size="55" />
          </view>

          <!-- 用户名 -->
          <view class="user-name oneline-hide"> {{ userInfo.name }} </view>
        </view>
      </view>

      <!-- 未登录 -->
      <view v-else class="not-login">
        <view class="user-avatar" @click="handleLogin">
          <up-avatar src="/static/default-avatar.png" size="60" />

          <view class="ml-10 f-28" style="margin-left: 10rpx; font-size: 28rpx">
            点击头像登录</view>
        </view>
      </view>
    </view>
    <!-- 订单区域 -->
    <view class="point">
      <view class="income">
        <view class="dis-flex flex-y-center flex-x-between mb-10">
          <view class="left-line f-30">订单模块</view>

          <view style="color: #929292" class="dis-flex flex-y-center"
            @click="onTargetOrderList(OrderStatus.ALL)">
            <text class="mr-10 f-25">订单中心</text>
            <up-icon name="arrow-right" size="12" color="#929292" />
          </view>
        </view>

        <view style="text-align: center" class="dis-flex flex-x-around">
          <view class="item" @click="onTargetOrderList(OrderStatus.ALL)">
            <text class="iconfont icon-all-order f-50" />
            <view class="f-20 col-92">全部订单</view>
          </view>

          <view class="item"
            @click="onTargetOrderList(OrderStatus.PENDING_PAYMENT)">
            <text class="iconfont icon-wait-pay-order f-50" />
            <view class="f-20 col-92">待付款</view>
          </view>

          <view class="item"
            @click="onTargetOrderList(OrderStatus.DINE_IN_ORDER)">
            <text class="iconfont icon-wait-take-order f-50" />
            <view class="f-20 col-92">堂食订单</view>
          </view>

          <view class="item" @click="onTargetOrderList(OrderStatus.COMPLETED)">
            <text class="iconfont icon-finish-order f-50" />
            <view class="f-20 col-92">已完成</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 单元列表 -->
    <view class="unit-list">
      <view class="cell-item" v-for="(item, index) in cellList" :key="index"
        @click="handleClickCell(item)">
        <view class="dis-flex flex-y-center">
          <text class="iconfont left-icon" :class="[`icon-${item.icon}`]" />
          <text class="ml-10 f-25">{{ item.text }}</text>
        </view>

        <view class="dis-flex flex-y-center flex-x-end">
          <text class="sub-title oneline-hide f-22" style="max-width: 90%">{{
            item.subText
          }}</text>
          <up-icon name="arrow-right" size="12" color="#929292" />
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view v-if="isLogin" class="my-logout">
      <view class="logout-btn" @click="handleLogout()">
        <text>退出登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
  import { onShow, onHide } from "@dcloudio/uni-app";
  import { ComponentInternalInstance } from "vue";
  import { useUserStore } from "@/store";
  import { checkLogin } from "@/core/app";
  import { UserVO } from "@/api/user";
  import AvatarImage from "@/components/avatar-image/index.vue";
  import { OrderStatus } from "@/enums/OrderEnum"

  const userStore = useUserStore();
  const isLogin = ref(false);
  const { proxy } = getCurrentInstance() as ComponentInternalInstance;
  // 是否显示进入页面动画
  const animation = ref(false);
  const userInfo = ref<UserVO>({});
  const cellList = ref([
    {
      text: "地址管理",
      subText: "用户收货地址管理",
      icon: "address-manage",
      link: "pages/address/index",
      query: {},
    },
    {
      text: "设置",
      subText: "设置用户昵称、头像等信息",
      icon: "setting",
      link: "pages/user/complete-user-info",
      query: {
      },
    },
  ]);

  const userInfoWrapStyle = computed(() => {
    let style = {
      transform: animation.value ? "translate(0rpx, 0rpx)" : "",
      transition: "all .3s ease-in-out",
    };
    return style;
  });

  onShow(() => {
    animation.value = true;
    onRefreshPage();
    uni.pageScrollTo({
      duration: 0,
      scrollTop: 0,
    });
  });

  onHide(() => {
    animation.value = false;
  });

  function onRefreshPage() {
    isLogin.value = checkLogin();
    // 获取用户信息
    if (isLogin.value) {
      userStore.getUserInfo().then((res) => {
        userInfo.value = res.data;
      });
    }
  }

  // 跳转到订单中心
  const onTargetOrderList = (tabIndex: OrderStatus) => {
    // console.log("跳转到订单中心", tabIndex);
    proxy?.$navTo(`pages/order/order-list?dataType=${tabIndex}`);
  };

  // 点击单元列表项跳转到某个页面
  const handleClickCell = (item: (typeof cellList.value)[0]) => {
    if (item.text === "设置") {
      proxy?.$navTo("pages/user/complete-user-info", {
        ...userInfo.value,
      });
    }
    if (item.link) {
      proxy?.$navTo(item.link);
    }
  };

  const handleLogout = () => {
    uni.showModal({
      title: "友情提示",
      content: "您确定要退出登录吗?",
      success(res) {
        if (res.confirm) {
          userStore.logout().then(() => onRefreshPage());
        }
      },
    });
  };

  const onTargetCompleteUserInfo = () => {
    proxy?.$navTo("pages/user/complete-user-info", {
      ...userInfo.value,
    });
  };

  const handleLogin = () => {
    !isLogin.value && proxy?.$navTo("pages/login/index");
  };
</script>

<style>
  page {
    background-color: #fafafa !important;
  }
</style>

<style lang="scss" scoped>
  $height: 300rpx;

  @mixin flex-y-center {
    display: flex;
    align-items: center;
  }

  .bg-image {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: $height;
    border-radius: 0 0 8% 8%;
    z-index: -99;
    background-image: linear-gradient(90deg, #d81e06, #ff4500);
  }

  .user-info {
    width: 100%;
    height: $height;
    position: relative;
    transform: translate(-400rpx, -400rpx);

    .user-avatar {
      color: #fff;
      padding: 0 10rpx 0 60rpx;
      margin-bottom: 10rpx;
      @include flex-y-center;
    }
  }

  .not-login,
  .is-login {
    height: $height;
    @include flex-y-center;
  }

  .is-login {
    .avatar-name-wrap {
      width: 100%;
      @include flex-y-center;

      .user-name {
        color: #eee;
        font-size: 30rpx;
      }
    }
  }

  .point {
    width: 100%;
    background-color: #fff;

    .income {
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      padding: 20rpx 30rpx;
      height: 200rpx;
      border-radius: 20rpx;
      background-color: #fff;
      box-shadow: 0 0 10rpx 1rpx #dadada;
      margin: 0 50rpx;
      transform: translateY(-20rpx);
      transition: all 0.3s ease-in-out;

      .left-line {
        height: 30rpx;
        line-height: 30rpx;
        padding-left: 10rpx;
        border-left: 8rpx solid rgb(43, 179, 171);
      }
    }

    .point-item {
      .icon {
        font-size: 65rpx;
      }
    }
  }

  // 退出登录
  .my-logout {
    display: flex;
    justify-content: center;
    margin-top: 50rpx;

    .logout-btn {
      width: 75%;
      margin: 0 auto;
      font-size: 28rpx;
      color: #616161;
      border-radius: 30rpx;
      border: 1px solid #dcdcdc;
      padding: 16rpx 0;
      text-align: center;
    }
  }

  .unit-list {
    padding: 0 50rpx;
    background-color: #fff;

    .cell-item {
      height: 60rpx;
      padding: 20rpx 0;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .sub-title {
        color: #929292;
      }

      &:not(:last-child) {
        border-bottom: 3rpx solid #f5f3f3;
      }
    }

    .left-icon {
      font-size: 40rpx;
      color: #f7b418;
    }
  }
</style>
