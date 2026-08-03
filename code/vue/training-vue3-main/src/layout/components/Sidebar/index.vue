<script lang="ts" setup>
import { useSettingsStore, useAppStore } from "@/store";
import { LayoutEnum } from "@/enums/LayoutEnum";
import { constantRoutes } from "@/router/router.config";

const appStore = useAppStore();
const settingsStore = useSettingsStore();

const sidebarLogo = computed(() => settingsStore.sidebarLogo);
const layout = computed(() => settingsStore.layout);

defineOptions({
  name: "Sidebar",
});
</script>

<template>
  <div :class="{ 'has-logo': sidebarLogo }">
    <!-- 左侧菜单栏布局 -->
    <template v-if="layout === LayoutEnum.LEFT">
      <!-- 侧边栏logo -->
      <SidebarLogo v-if="sidebarLogo" :collapse="!appStore.sidebar.opened" />

      <!-- 侧边栏菜单区域 -->
      <el-scrollbar>
        <SidebarMenu :menu-list="constantRoutes" base-path="/" />
      </el-scrollbar>
    </template>
  </div>
</template>

<style lang="scss" scoped>
.el-scrollbar {
  height: calc(100vh - $logo-height);
  padding: 40px 0;
}
</style>
