<script lang="ts" setup>
import path from "path-browserify";
import type { RouteRecordRaw } from "vue-router";
import { useSettingsStore, useAppStore } from "@/store";
import { isExternal } from "@/utils/index";
import { LayoutEnum } from "@/enums/LayoutEnum";
import variables from "@/styles/variables.module.scss";

type LayoutMode = "horizontal" | "vertical";

const currentRoute = useRoute();
const settingsStore = useSettingsStore();
const appStore = useAppStore();
const props = defineProps({
  menuList: {
    required: true,
    type: Array<RouteRecordRaw>,
    default: () => [],
  },
  basePath: {
    type: String,
    required: true,
  },
});

// 布局模式
const mode = computed<LayoutMode>(() => {
  return settingsStore.layout === LayoutEnum.TOP ? "horizontal" : "vertical";
});

/**
 * 解析路径
 * @param routePath 路由路径
 * @returns 路由路径
 * @example /system/user
 */
function resolvePath(routePath: string) {
  if (isExternal(routePath)) {
    return routePath;
  }
  if (isExternal(props.basePath)) {
    return props.basePath;
  }

  // 完整路径(/system/user) = 父级路径(/system) + 路由路径(user)
  return path.resolve(props.basePath, routePath);
}

defineOptions({
  name: "SidebarMenu",
});
</script>

<template>
  <!-- :hide-timeout="1000000" -->
  <el-menu
    :default-active="currentRoute.path"
    :collapse="!appStore.sidebar.opened"
    :background-color="variables['menu-background']"
    :text-color="variables['menu-text']"
    :active-text-color="variables['menu-active-text']"
    :unique-opened="false"
    :collapse-transition="false"
    :mode="mode"
  >
    <SidebarMenuItem
      v-for="route in menuList"
      :key="route.path"
      :item="route"
      :base-path="resolvePath(route.path)"
    />
  </el-menu>
</template>

<style lang="scss">
@mixin active-left-bar {
  position: relative;

  &::before {
    position: absolute;
    top: 50%;
    left: 0;
    width: 4px;
    height: 20px;
    content: " ";
    background-color: var(--el-color-primary);
    border-radius: 0 5px 5px 0;
    transform: translateY(-50%);
  }
}

.el-menu {
  --el-menu-base-level-padding: 35px;

  background-color: $menu-background;

  .el-menu-item,
  .el-sub-menu__title {
    color: $menu-text;
    height: 56px;
    line-height: 56px;
  }

  .is-active {
    color: var(--el-color-primary) !important;
  }

  .el-sub-menu__title:hover {
    background-color: $menu-hover;
  }

  .el-icon-comment {
    font-size: 18px !important;
    width: 1em;
    height: 1em;
  }
}

.openSidebar {
  .router-link-exact-active {
    .is-active {
      @include active-left-bar;
    }
  }
}

.hideSidebar {
  .el-menu-item,
  .el-sub-menu__title {
    justify-content: center;
  }

  .el-menu-item,
  .el-sub-menu {
    &.is-active {
      @include active-left-bar;
    }
  }
}

// .el-menu--vertical:not(.el-menu--collapse, .el-menu--popup-container)
//   .el-menu-item-group__title,
// .el-menu--vertical:not(.el-menu--collapse, .el-menu--popup-container)
//   .el-sub-menu__title {
//   padding-left: calc(
//     var(--el-menu-base-level-padding) + var(--el-menu-level) *
//       var(--el-menu-level-padding)
//   );
// }
</style>
