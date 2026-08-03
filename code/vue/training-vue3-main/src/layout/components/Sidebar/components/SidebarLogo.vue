<script lang="ts" setup>
import { useSettingsStore, useAppStore } from "@/store";
import defaultSettings from "@/settings";
import variables from "@/styles/variables.module.scss";

const appStore = useAppStore();
const settingsStore = useSettingsStore();

defineProps({
  collapse: {
    type: Boolean,
    default: false,
  },
});

onMounted(() => {
  appStore.getShopStatus();
});

const logo = ref(new URL(`@/assets/logo/logo_3.svg`, import.meta.url).href);

defineOptions({
  name: "SidebarLogo",
});
</script>

<template>
  <div
    class="logo-container"
    :style="{
      height: variables['logo-height'],
    }"
  >
    <!-- 动画过渡依赖 animate.css -->
    <transition enter-active-class="animate__animated animate__fadeInLeft">
      <!-- 折叠侧边栏 -->
      <router-link v-if="collapse" class="wh-full flex-center" to="/">
        <div
          :title="appStore.isOpenShop ? '营业中' : '打烊中'"
          class="shop-status wh-full flex-center"
          :class="{ 'shop-is-open': appStore.isOpenShop }"
        >
          <img
            v-if="settingsStore.sidebarLogo"
            :src="logo"
            class="w-40px h-40px"
          />
        </div>
      </router-link>

      <!-- 展开侧边栏 -->
      <router-link v-else class="wh-full flex-center flex-col" to="/">
        <div
          :title="appStore.isOpenShop ? '营业中' : '打烊中'"
          class="shop-status flex-col flex-center"
          :class="{ 'shop-is-open': appStore.isOpenShop }"
        >
          <img
            v-if="settingsStore.sidebarLogo"
            :src="logo"
            :style="{
              width: variables['logo-image-wh'],
              height: variables['logo-image-wh'],
            }"
          />
          <span class="logo-title light:text-#000 dark:text-#fff">{{
            defaultSettings.title
          }}</span>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<style lang="scss" scoped>
.logo-container {
  width: 100%;
  // background-color: $sidebar-logo-background;
  background-color: $menu-background;
  transition: height 0.3s ease-in-out;

  .logo-title {
    flex-shrink: 0; /* 防止容器在空间不足时缩小 */
    font-family: cursive;
    font-size: 17px;
    font-weight: 300;
  }
}

.shop-status {
  position: relative;
  &::after {
    content: "";
    position: absolute;
    right: 17px;
    bottom: 30px;
    width: 15px;
    height: 15px;
    border-radius: 50%;
    background-color: red;
  }
}
.shop-is-open {
  &::after {
    background-color: rgb(84 255 0);
  }
}

.layout-top,
.layout-mix {
  .logo-container {
    width: $sidebar-width;
  }

  &.hideSidebar {
    .logo-container {
      width: $sidebar-width-collapsed;
    }
  }
}
</style>
