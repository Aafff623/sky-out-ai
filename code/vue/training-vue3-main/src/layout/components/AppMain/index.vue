<script lang="ts" setup>
import { useSettingsStore, useTagsViewStore } from "@/store";
import variables from "@/styles/variables.module.scss";

const settingsStore = useSettingsStore();
const cachedViews = computed(() => useTagsViewStore().cachedViews);
const minHeight = computed(() => {
  // 开启标签页
  if (settingsStore.tagsView) {
    // 100vh - navbar height - tags view height
    return `calc(100vh - ${variables["navbar-height"]} - ${variables["tags-view-height"]})`;
  } else {
    // 100vh - navbar height
    return `calc(100vh - ${variables["navbar-height"]})`;
  }
});

defineOptions({
  name: "AppMain",
});
</script>

<template>
  <section class="app-main" :style="{ minHeight: minHeight }">
    <router-view>
      <template #default="{ Component, route }">
        <!-- 启用自定义进场动画：name="fade-transform（或者其他）" 或者使用animate.css库 enter-active-class="animate__animated animate__fadeIn" -->
        <transition :name="settingsStore.transitionType" mode="out-in">
          <keep-alive :include="cachedViews">
            <component :is="Component" :key="route.path" />
          </keep-alive>
        </transition>
      </template>
    </router-view>
  </section>
</template>

<style lang="scss" scoped>
.app-main {
  position: relative;
  padding: 20px;
  background-color: var(--app-main-bg);
}
</style>
