<script lang="ts" setup>
import { DeviceEnum } from "@/enums/DeviceEnum";
import { useAppStore } from "@/store";

const appStore = useAppStore();

const isMobile = computed(() => appStore.device === DeviceEnum.MOBILE);

function toggleSideBar() {
  appStore.toggleSidebar();
}

defineOptions({
  name: "NavBar",
});
</script>

<template>
  <div class="navbar-container">
    <!-- 导航栏面包屑 -->
    <div class="flex">
      <hamburger
        :is-active="appStore.sidebar.opened"
        @toggle-click="toggleSideBar"
      />
      <breadcrumb v-if="!isMobile" />
    </div>
    <!-- 导航栏右侧 -->
    <NavbarAction />
  </div>
</template>

<style lang="scss" scoped>
.navbar-container {
  @apply flex-x-between;

  height: $navbar-height;
  background: var(--el-bg-color);
}
</style>
