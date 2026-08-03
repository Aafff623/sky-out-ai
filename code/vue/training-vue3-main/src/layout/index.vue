<script lang="ts" setup>
import { useAppStore, useSettingsStore } from "@/store";
import defaultSettings from "@/settings";
import { DeviceEnum } from "@/enums/DeviceEnum";
import { LayoutEnum } from "@/enums/LayoutEnum";

const route = useRoute();
const appStore = useAppStore();
const settingsStore = useSettingsStore();

const width = useWindowSize().width; // 获取当前浏览器窗口宽度
const WIDTH_DESKTOP = 992; // 响应式布局容器固定宽度 大屏（>=1200px）中屏（>=992px）小屏（>=768px）
const isMobile = computed(() => appStore.device === DeviceEnum.MOBILE); // 手机端、h5
const isOpenSidebar = computed(() => appStore.sidebar.opened);
const fixedHeader = computed(() => settingsStore.fixedHeader); // 是否固定顶部导航
const showTagsView = computed(() => settingsStore.tagsView); // 是否显示多标签页
const layout = computed(() => settingsStore.layout); // 布局模式 left、top、mix
// const activeTopMenuPath = computed(() => appStore.activeTopMenuPath); // 当前激活的顶部菜单路径

const classObj = computed(() => {
  return {
    hideSidebar: !appStore.sidebar.opened,
    openSidebar: appStore.sidebar.opened,
    mobile: appStore.device === DeviceEnum.MOBILE,
    [`layout-${settingsStore.layout}`]: true,
  };
});

// 监听路由变化，每次切换页面时都判断：当前浏览器视口宽度小于固定宽度，并且侧边栏是打开状态，自动关闭侧边栏
watch(route, () => {
  if (isMobile.value && isOpenSidebar.value) {
    appStore.closeSideBar();
  }
});

// 监听浏览器窗口大小变化，自动切换设备类型和侧边栏状态
watchEffect(() => {
  appStore.toggleDevice(
    width.value < WIDTH_DESKTOP ? DeviceEnum.MOBILE : DeviceEnum.DESKTOP
  );

  // 当前浏览器窗口比固定宽度大时自动打开侧边栏，否则自动关闭
  if (width.value >= WIDTH_DESKTOP) {
    appStore.openSideBar();
  } else {
    appStore.closeSideBar();
  }
});

// 手动关闭侧边栏
function handleOutsideClick() {
  appStore.closeSideBar();
}

// 手动打开侧边栏
// function toggleSideBar() {
//   appStore.toggleSidebar();
// }

defineOptions({
  name: "LayoutIndex",
});
</script>

<template>
  <div class="wh-full layout-container" :class="classObj">
    <!-- 遮罩层（仅在浏览器宽度很小时并且侧边栏是打开状态时显示） -->
    <div
      v-if="isMobile && isOpenSidebar"
      class="wh-full fixed-lt z-999 bg-black bg-opacity-30"
      @click="handleOutsideClick"
    ></div>

    <!-- 公用侧边栏 -->
    <Sidebar class="sidebar-container" />

    <!-- TODO: 混合布局 -->

    <!-- 左侧和顶部布局 -->
    <div :class="{ hasTagsView: showTagsView }" class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <NavBar v-if="layout === LayoutEnum.LEFT" />
        <TagsView v-if="showTagsView && !isMobile" />
      </div>
      <AppMain />
      <Settings v-if="defaultSettings.showSettings" />
      <!-- 返回顶部 -->
      <el-backtop target=".main-container">
        <svg-icon icon-class="backtop" size="24px" />
      </el-backtop>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.el-icon-comment {
  font-size: 18px !important;
}
.sidebar-container {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 999;
  width: $sidebar-width;
  background-color: $menu-background;
  border-right: 2px solid rgb(219 216 216 / 30%);
  transition: width 0.28s;

  :deep(.el-menu) {
    border: none;
  }
}

.main-container {
  position: relative;
  height: 100%;
  margin-left: $sidebar-width;
  overflow-y: auto;
  transition: margin-left 0.28s;

  .fixed-header {
    position: sticky;
    top: 0;
    z-index: 9;
    transition: width 0.28s;
  }
}

.layout-top {
  .sidebar-container {
    position: sticky;
    z-index: 999;
    display: flex;
    width: 100% !important;
    height: $navbar-height;

    :deep(.el-scrollbar) {
      flex: 1;
      height: $navbar-height;
    }

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title),
    :deep(.el-menu--horizontal) {
      height: $navbar-height;
      line-height: $navbar-height;
    }

    :deep(.el-menu--collapse) {
      width: 100%;
    }
  }

  .main-container {
    height: calc(100vh - $navbar-height);
    margin-left: 0;
  }
}

.layout-mix {
  .sidebar-container {
    width: 100% !important;
    height: $navbar-height;

    :deep(.el-scrollbar) {
      flex: 1;
      height: $navbar-height;
    }

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title),
    :deep(.el-menu--horizontal) {
      height: $navbar-height;
      line-height: $navbar-height;
    }

    :deep(.el-menu--horizontal.el-menu) {
      border: none;
    }
  }

  .mix-container {
    display: flex;
    height: 100%;
    padding-top: $navbar-height;

    .mix-container__left {
      position: relative;
      width: $sidebar-width;
      height: 100%;

      :deep(.el-menu) {
        height: 100%;
        border: none;
      }

      .sidebar-toggle {
        position: absolute;
        bottom: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 50px;
        line-height: 50px;
        box-shadow: 0 0 6px -2px var(--el-color-primary);

        div:hover {
          background-color: var(--menu-background);
        }

        :deep(svg) {
          color: var(--el-color-primary) !important;
        }
      }
    }

    .main-container {
      flex: 1;
      min-width: 0;
      margin-left: 0;
    }
  }
}

.hideSidebar {
  .main-container {
    margin-left: $sidebar-width-collapsed;
  }

  &.layout-top {
    .main-container {
      margin-left: 0;
    }
  }

  &.layout-mix {
    .sidebar-container {
      width: 100% !important;
    }

    .mix-container {
      .mix-container__left {
        width: $sidebar-width-collapsed;
      }
    }
  }
}

.layout-left.hideSidebar {
  .sidebar-container {
    width: $sidebar-width-collapsed !important;
  }

  .main-container {
    margin-left: $sidebar-width-collapsed;
  }

  &.mobile {
    .sidebar-container {
      pointer-events: none;
      transition-duration: 0.3s;
      transform: translate3d(-210px, 0, 0);
    }

    .main-container {
      margin-left: 0;
    }
  }
}

.mobile {
  .main-container {
    margin-left: 0;
  }

  .navbar-container {
    border: 1px solid var(--el-border-color-light);
  }

  &.layout-top {
    // 顶部模式全局变量修改
    --el-menu-item-height: $navbar-height;
  }
}
</style>
