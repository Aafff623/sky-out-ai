<script lang="ts" setup>
import { translateRouteTitle } from "@/utils/i18n";

defineProps({
  icon: {
    type: String,
    default: "",
  },
  title: {
    type: String,
    default: "",
  },
});

defineOptions({
  name: "SidebarMenuItem",
});
</script>

<template>
  <!-- 根据 icon 类型决定使用别听类型的图标组件 -->
  <!-- element-plus 图标 -->
  <el-icon v-if="icon && icon.startsWith('el-icon')" class="sub-el-icon">
    <component :is="icon.replace('el-icon-', '')" />
  </el-icon>
  <!-- 自定义图标 -->
  <svg-icon v-else-if="icon" :icon-class="icon" size="18px" />

  <!-- 没有图标使用默认 menu 图标 -->
  <svg-icon v-else icon-class="menu" />

  <!-- 菜单标题 -->
  <span v-if="title" class="ml-1">{{ translateRouteTitle(title) }}</span>
</template>

<style lang="scss" scoped>
.sub-el-icon {
  width: 14px !important;
  margin-right: 0 !important;
  color: currentcolor;
}

.hideSidebar {
  .el-sub-menu,
  .el-menu-item {
    .svg-icon,
    .sub-el-icon {
      // margin-left: 20px;
      margin-left: 0;
    }
  }
}
</style>
