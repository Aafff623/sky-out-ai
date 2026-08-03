<script lang="ts" setup>
import router from "@/router";
import { compile } from "path-to-regexp";
import { RouteLocationMatched } from "vue-router";
import { translateRouteTitle } from "@/utils/i18n";
import { ArrowRight } from "@element-plus/icons-vue";

const currentRoute = useRoute();
const breadcrumbs = ref<RouteLocationMatched[]>([]);

onBeforeMount(() => {
  getBreadcrumbs();
});

watch(
  () => currentRoute.path,
  (path) => {
    if (path.startsWith("/redirect/")) {
      return;
    }
    getBreadcrumbs();
  }
);

const pathCompile = (path: string) => {
  const { params } = currentRoute;
  const toPath = compile(path);
  return toPath(params);
};

function getBreadcrumbs() {
  let matched = currentRoute.matched.filter(
    (item) => item.meta && item.meta.title
  );
  const first = matched[0];
  if (!isDashboard(first)) {
    matched = [
      { path: "/", meta: { title: "工作台" } } as any,
    ].concat(matched);
  }

  // 显示在面包屑上面的条件是：存在 title 并且 breadcrumb 不为 false
  breadcrumbs.value = matched.filter((item) => {
    return item.meta && item.meta.title && item.meta.breadcrumb !== false;
  });
}

/**
 * 判断是否为首页
 * @param route 路由对象
 */
function isDashboard(route: RouteLocationMatched) {
  const name = route && route.name;
  if (!name) {
    return false;
  }
  return (
    name.toString().trim().toLocaleLowerCase() ===
    "Dashboard".toLocaleLowerCase()
  );
}

function handleLink(item: any) {
  const { path, redirect } = item;
  if (redirect) {
    router.push(redirect).catch((err) => {
      console.warn(err);
    });
    return;
  }
  router.push(pathCompile(path)).catch((err) => {
    console.warn(err);
  });
}

defineOptions({
  name: "Breadcrumb",
});
</script>

<template>
  <el-breadcrumb class="flex-y-center" :separator-icon="ArrowRight">
    <transition-group
      enter-active-class="animate__animated animate__fadeInRight"
    >
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
        <!-- 设置了不重定向，或者是最后一个，无法点击goTo -->
        <span
          class="color-gray-400"
          v-if="
            item.redirect === 'noredirect' || index === breadcrumbs.length - 1
          "
        >
          {{ translateRouteTitle(item.meta.title) }}
        </span>

        <!-- 正常显示，点击跳转到指定页面 -->
        <a v-else @click.prevent="handleLink(item)">
          {{ translateRouteTitle(item.meta.title) }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<style lang="scss" scoped>
// 覆盖 element-plus 的样式
.el-breadcrumb__inner,
.el-breadcrumb__inner a {
  font-weight: 400 !important;
}
</style>
