<script lang="ts" setup>
import { resolve } from "path-browserify";
import { useRoute, useRouter, RouteRecordRaw } from "vue-router";
import { translateRouteTitle } from "@/utils/i18n";
import { useTagsViewStore, useSettingsStore, useAppStore } from "@/store";
import { constantRoutes } from "@/router/router.config";

const route = useRoute();
const router = useRouter();

const appStore = useAppStore();
const tagsViewStore = useTagsViewStore();
const settingsStore = useSettingsStore();

const { proxy } = getCurrentInstance()!;
const left = ref(0); // 右键菜单距离左侧的距离
const top = ref(0); // 右键菜单距离顶部的距离
// 已访问页面（视图）标签列表
const { visitedViews } = storeToRefs(tagsViewStore);
// 固定标签列表
const affixTags = ref<TagView[]>([]);
// 当前选择的标签
const selectedTag = reactive<TagView>({
  path: "",
  fullPath: "",
  name: "",
  title: "",
  affix: false,
  keepAlive: false,
});
const contentMenuVisible = ref(false); // 右键菜单是否显示

const layout = computed(() => settingsStore.layout);

onMounted(() => {
  initTags();
});

watch(contentMenuVisible, (value) => {
  if (value) {
    document.body.addEventListener("click", closeContentMenu);
  } else {
    document.body.removeEventListener("click", closeContentMenu);
  }
});

// 监听路由变化，更新标签列表
watch(
  route,
  () => {
    addTags();
    moveToCurrentTag();
  },
  { immediate: true }
);

// 如果是混合模式，更改selectedTag，需要对应高亮的activeTop
watch(
  () => route.name,
  (newVal) => {
    if (newVal) {
      againActiveTop(newVal as string);
    }
  },
  {
    deep: true,
  }
);

// 初始化标签列表
function initTags() {
  const tags: TagView[] = filterAffixTags(constantRoutes);
  affixTags.value = tags;
  for (const tag of tags) {
    // 每个路由项都必须有唯一的 name 属性，才会显示成标签
    if (tag.name) {
      tagsViewStore.addVisitedView(tag);
    }
  }
}

/**
 * 过滤出需要固定的标签
 * @param routes 当前角色对应的路由列表
 */
function filterAffixTags(routes: RouteRecordRaw[], basePath = "/") {
  let tags: TagView[] = [];
  routes.forEach((route) => {
    // 获取标签的路由路径
    const tagPath = resolve(basePath, route.path);
    // 判断当前标签是否为固定标签，是则添加到tags列表中
    if (route.meta?.affix) {
      tags.push({
        path: tagPath,
        fullPath: tagPath,
        name: String(route.name),
        title: route.meta?.title || "no-name",
        affix: route.meta?.affix,
        keepAlive: route.meta?.keepAlive || false,
      });
    }
    // 递归处理子路由
    if (route.children) {
      const tempTags = filterAffixTags(route.children, basePath + route.path);
      if (tempTags.length >= 1) {
        tags = [...tags, ...tempTags]; // 合并父路由固定标签以及子路由固定标签
      }
    }
  });

  return tags;
}

/**
 * 添加标签（如果该路由被标记为 keepAlive 路由，则添加到缓存路由中）
 */
function addTags() {
  if (route.meta.title) {
    tagsViewStore.addView({
      name: route.name as string,
      title: route.meta.title,
      path: route.path,
      fullPath: route.fullPath,
      affix: route.meta.affix || false,
      keepAlive: route.meta.keepAlive || false,
      query: route.query,
    });
  }
}

function moveToCurrentTag() {
  // 使用 nextTick() 的目的是确保在更新 TagsView 组件之前，scrollPaneRef 对象已经滚动到了正确的位置
  nextTick(() => {
    for (const tag of visitedViews.value) {
      if (tag.path === route.path) {
        if (tag.fullPath !== route.fullPath) {
          tagsViewStore.updateVisitedView({
            name: route.name as string,
            title: route.meta.title || "no-title",
            path: route.path,
            fullPath: route.fullPath,
            affix: route.meta.affix || false,
            keepAlive: route.meta.keepAlive || false,
            query: route.query,
          });
        }
      }
    }
  });
}

// 是否为固定标签
function isAffixTag(tag: TagView) {
  return tag.affix;
}

/**
 * 是否为第一个已访问视图标签（因为首页为固定标签，所以下标从 1 开始）
 */
function isFirstView() {
  try {
    return (
      selectedTag.path === "/dashboard" ||
      selectedTag.fullPath === tagsViewStore.visitedViews[1].fullPath
    );
  } catch (error) {
    return false;
  }
}

/**
 * 是否为最后一个标签
 */
function isLastView() {
  const lastIndex = tagsViewStore.visitedViews.length - 1;
  try {
    return (
      selectedTag.fullPath === tagsViewStore.visitedViews[lastIndex].fullPath
    );
  } catch (error) {
    return false;
  }
}

/**
 * 刷新当前标签页
 */
function refreshSelectedTag(view: TagView) {
  tagsViewStore.delCachedView(view);
  const { fullPath } = view;
  nextTick(() => {
    // 下一次dom更新后，重定向到指定的 fullPath 路径（带有 /redirect 前缀并且路由配置项中存在 redirect 属性，刚好与 fullPath 匹配，就会重定向到该路由）
    router.replace("/redirect" + fullPath);
  });
}

/**
 * 关闭指定的标签页
 */
function closeSelectedTag(view: TagView) {
  tagsViewStore.delView(view).then(({ visitedViews }) => {
    // 如果关闭的标签是当前激活的标签，则切换到上一个标签
    if (tagsViewStore.isActive(view)) {
      tagsViewStore.toLastView(visitedViews);
    }
  });
}

/**
 * 关闭当前标签页的左侧（非固定标签）标签
 */
function closeLeftTags() {
  tagsViewStore.delLeftViews(selectedTag).then(({ visitedViews }) => {
    if (!visitedViews.find((item) => item.path === route.path)) {
      tagsViewStore.toLastView(visitedViews);
    }
  });
}

/**
 * 关闭当前标签页的右侧（非固定标签）标签
 */
function closeRightTags() {
  tagsViewStore.delRightViews(selectedTag).then(({ visitedViews }) => {
    if (!visitedViews.find((item: any) => item.path === route.path)) {
      tagsViewStore.toLastView(visitedViews);
    }
  });
}

/**
 * 关闭指定标签页之外的其他全部非固定标签页
 */
function closeOtherTags() {
  router.push(selectedTag);
  tagsViewStore.delOtherViews(selectedTag).then(() => {
    moveToCurrentTag();
  });
}

/**
 * 关闭所有非固定的标签页
 */
function closeAllTags(view: TagView) {
  tagsViewStore.delAllViews().then((res: any) => {
    // 关闭所有标签之后跳转到首页
    tagsViewStore.toLastView(res.visitedViews, view);
  });
}

/**
 * 打开右键菜单
 */
function openContentMenu(tag: TagView, e: MouseEvent) {
  const menuMinWidth = 105;

  const offsetLeft = proxy?.$el.getBoundingClientRect().left; // 当前组件距离浏览器（视口）左侧的距离
  const offsetWidth = proxy?.$el.offsetWidth; // 当前组件宽度（offsetWidth的大小取决于元素自身的大小）
  const maxLeft = offsetWidth - menuMinWidth;
  const l = e.clientX - offsetLeft + 15; // 15: margin right

  if (l > maxLeft) {
    left.value = maxLeft;
  } else {
    left.value = l;
  }

  // 混合模式下，需要减去顶部菜单（fixed）的高度
  if (layout.value === "mix") {
    top.value = e.clientY - 50;
  } else {
    top.value = e.clientY;
  }

  contentMenuVisible.value = true;
  Object.assign(selectedTag, tag);
}

/**
 * 关闭右键菜单
 */
function closeContentMenu() {
  contentMenuVisible.value = false;
}

/**
 * 滚动事件
 */
function handleScroll() {
  closeContentMenu();
}

/**
 * 如果是混合模式，则高亮当前标签对应的顶部菜单项
 */
const againActiveTop = (newVal: string) => {
  if (layout.value !== "mix") return;
  const parent = findOutermostParent(constantRoutes, newVal);
  if (appStore.activeTopMenuPath !== parent?.path) {
    appStore.activeTopMenu(parent!.path ?? "/");
  }
};

/**
 * 递归查找最外层父节点
 */
function findOutermostParent(tree: RouteRecordRaw[], findName: string) {
  let parentMap: Record<string, RouteRecordRaw | null> = {};

  function buildParentMap(node: RouteRecordRaw, parent: RouteRecordRaw | null) {
    parentMap[node.name as string] = parent;

    if (node.children) {
      for (let i = 0; i < node.children.length; i++) {
        buildParentMap(node.children[i], node);
      }
    }
  }
  for (let i = 0; i < tree.length; i++) {
    buildParentMap(tree[i], null);
  }

  let currentNode = parentMap[findName];
  while (currentNode) {
    if (!parentMap[currentNode.name as string]) {
      return currentNode;
    }
    currentNode = parentMap[currentNode.name as string];
  }

  return null;
}
const tagRef = ref([]); // 改为数组形式
defineOptions({
  name: "TagsView",
});
</script>

<template>
  <div class="tags-container">
    <!-- 标签列表 -->
    <el-scrollbar
      class="scroll-container"
      :vertical="false"
      @wheel.prevent="handleScroll"
    >
      <router-link
        ref="tagRef"
        v-for="tag in visitedViews"
        :key="tag.fullPath"
        :class="'tags-item ' + (tagsViewStore.isActive(tag) ? 'active' : '')"
        :to="{ path: tag.path, query: tag.query }"
        @click.middle="!isAffixTag(tag) ? closeSelectedTag(tag) : ''"
        @contextmenu.prevent="openContentMenu(tag, $event)"
      >
        <span class="title mr-3px" :title="translateRouteTitle(tag.title)">
          {{ translateRouteTitle(tag.title) }}
        </span>
        <i-ep-close
          class="close-icon"
          size="14px"
          v-if="!isAffixTag(tag)"
          @click.prevent.stop="closeSelectedTag(tag)"
        />
      </router-link>
    </el-scrollbar>

    <!-- 右键菜单 -->
    <ul
      v-show="contentMenuVisible"
      class="contextmenu"
      :style="{ left: left + 'px', top: top + 'px' }"
    >
      <li @click="refreshSelectedTag(selectedTag)">
        <svg-icon icon-class="refresh" />
        刷新
      </li>
      <li
        v-if="!isAffixTag(selectedTag)"
        @click="closeSelectedTag(selectedTag)"
      >
        <svg-icon icon-class="close" />
        关闭
      </li>
      <li @click="closeOtherTags">
        <svg-icon icon-class="close_other" />
        关闭其它
      </li>
      <li v-if="!isFirstView()" @click="closeLeftTags">
        <svg-icon icon-class="close_left" />
        关闭左侧
      </li>
      <li v-if="!isLastView()" @click="closeRightTags">
        <svg-icon icon-class="close_right" />
        关闭右侧
      </li>
      <li @click="closeAllTags(selectedTag)">
        <svg-icon icon-class="close_all" />
        关闭所有
      </li>
    </ul>
  </div>
</template>

<style lang="scss" scoped>
.tags-container {
  width: 100%;
  height: $tags-view-height;
  background-color: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color-light);
  border-bottom: 1px solid var(--el-border-color-light);
  box-shadow: 0 1px 1px var(--el-box-shadow-light);

  .tags-item {
    @apply flex-y-center;

    padding: 4px 6px 4px 8px;
    margin-left: 5px;
    overflow: hidden;
    font-size: 12px;
    color: var(--el-text-color-primary);
    cursor: pointer;
    border-radius: 2px;
    // border: 1px solid var(--el-border-color-light);
    box-shadow: var(--el-box-shadow-light);
    transition: color var(--el-transition-duration);

    .title {
      display: block;
      max-width: 80px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &:hover {
      color: var(--el-color-primary);
    }

    &:first-of-type {
      margin-left: 15px;
    }

    &:last-of-type {
      margin-right: 15px;
    }

    .close-icon {
      padding: 2px;
      border-radius: 50%;

      &:hover {
        color: #fff !important;
        background-color: rgb(161 157 157 / 40%) !important;
      }
    }

    &.active {
      font-weight: 500;
      color: #fff;
      background-color: var(--el-color-primary);

      &::before {
        display: inline-block;
        width: 8px;
        height: 8px;
        margin-right: 5px;
        content: "";
        background: #fff;
        border-radius: 50%;
      }

      .close-icon:hover {
        color: var(--el-color-primary);
        background-color: var(--el-fill-color-light);
      }
    }
  }
}

.contextmenu {
  position: absolute;
  z-index: 99;
  font-size: 12px;
  background: var(--el-bg-color-overlay);
  border-radius: 4px;
  box-shadow: var(--el-box-shadow-light);

  li {
    padding: 8px 16px;
    cursor: pointer;

    &:hover {
      background: var(--el-fill-color-light);
    }
  }
}

:deep(.scroll-container) {
  position: relative;
  width: 100%;
  overflow: hidden;
  white-space: nowrap;

  .el-scrollbar__bar {
    bottom: 0;
  }

  .el-scrollbar__wrap {
    height: $tags-view-height;
  }

  .el-scrollbar__view {
    @apply flex-y-center;

    height: 100%;
    padding-bottom: 2px;
  }
}
</style>
