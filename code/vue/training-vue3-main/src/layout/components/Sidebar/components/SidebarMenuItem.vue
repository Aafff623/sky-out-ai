<script lang="ts" setup>
import path from "path-browserify";
import { isExternal } from "@/utils/index";
import type { RouteRecordRaw } from "vue-router";

type CustomRouteRecordRaw = RouteRecordRaw & {
  noShowingChildren?: boolean; // 是否不显示子路由
};

const props = defineProps({
  // 当前路由项对象
  item: {
    type: Object as PropType<RouteRecordRaw>,
    required: true,
  },
  // 父层级完整路由路径
  basePath: {
    type: String,
    required: true,
  },
  // 是否为嵌套路由
  isNest: {
    type: Boolean,
    default: false,
  },
});

const onlyOneChild = ref<CustomRouteRecordRaw>();

/**
 * 判断当前路由是否只有一个显示的子路由
 * @param children 子路由数组
 * @param parent 父路由对象
 * @returns {boolean} true: 只有一个显示的子路由; false: 显示多个子路由
 */
function hasOneShowingChild(
  children: RouteRecordRaw[] = [],
  parent: RouteRecordRaw
): boolean {
  // 筛选出需要显示的子路由
  const showingChildren = children.filter((route) => {
    if (route.meta?.hidden) {
      return false;
    } else {
      route.meta!.hidden = false;
      onlyOneChild.value = route;
      return true;
    }
  });

  // 如果只有一个或者没有显示的子路由
  if (showingChildren.length === 1) {
    return true;
  }

  // 如果没有子路由，则显示父级路由
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: "", noShowingChildren: true };
    return true;
  }
  return false;
}

/**
 * 解析路由路径，将相对路径转换为绝对路径
 * @param routePath 路由路径
 * @returns 解析后的路由绝对路径
 */
function resolvePath(routePath: string) {
  // 外部链接直接返回，不做处理
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
  name: "SidebarMenuItem",
  inheritAttrs: false,
});
</script>

<template>
  <!-- 如果菜单项没有显示定义 hidden: true 则显示 -->
  <div v-if="!item.meta || !item.meta.hidden">
    <!-- 显示只有一个子路由或者没有子路由的菜单项 -->
    <template
      v-if="
        hasOneShowingChild(item.children, item) &&
        (!onlyOneChild?.children || onlyOneChild.noShowingChildren) &&
        !item.meta?.alwaysShow
      "
    >
      <AppLink
        v-if="onlyOneChild?.meta"
        :to="{
          path: resolvePath(onlyOneChild.path),
          query: onlyOneChild.meta.query,
        }"
      >
        <el-menu-item
          :index="resolvePath(onlyOneChild.path)"
          :class="{ 'submenu-title-noDropdown': !isNest }"
        >
          <SidebarMenuItemTitle
            :title="onlyOneChild.meta.title"
            :icon="onlyOneChild.meta.icon || (item.meta && item.meta.icon)"
          />
        </el-menu-item>
      </AppLink>
    </template>

    <!-- 显示具有多个子路由的父菜单项 -->
    <el-sub-menu v-else :index="resolvePath(item.path)" teleported>
      <template #title>
        <SidebarMenuItemTitle
          v-if="item.meta"
          :icon="item.meta && item.meta.icon"
          :title="item.meta.title"
        />
      </template>

      <!-- 递归遍历组件渲染子路由 -->
      <SidebarMenuItem
        v-for="child in item.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </div>
</template>

<style lang="scss">
.hideSidebar {
  .submenu-title-noDropdown {
    position: relative;
    padding: 0 !important;

    .el-tooltip {
      padding: 0 !important;

      .sub-el-icon {
        margin-left: 19px;
      }
    }

    & > span {
      display: inline-block;
      width: 0;
      height: 0;
      overflow: hidden;
      visibility: hidden;
    }
  }

  .el-sub-menu {
    overflow: hidden;

    & > .el-sub-menu__title {
      padding: 0 !important;

      .sub-el-icon {
        margin-left: 19px;
      }

      .el-sub-menu__icon-arrow {
        display: none;
      }
    }
  }

  .el-menu--collapse {
    width: $sidebar-width-collapsed;

    .el-sub-menu {
      & > .el-sub-menu__title {
        & > span {
          display: inline-block;
          width: 0;
          height: 0;
          overflow: hidden;
          visibility: hidden;
        }
      }
    }
  }
}

.el-menu-item:hover {
  @apply dark:bg-#000 light:bg-#fff;
  background-color: $menu-hover !important;
}
</style>
