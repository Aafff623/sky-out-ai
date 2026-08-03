<script lang="ts" setup>
import type { LocationQuery } from "vue-router";
import { isExternal } from "@/utils/index";

type toType = {
  path: string;
  query?: LocationQuery;
};

const props = defineProps({
  // 示例：{ path: "/foo", query: {id: "1"} } -> xxx/foo?id=1
  to: {
    type: Object as PropType<toType>,
    required: true,
  },
});

// 判断传入的 to 对象中的path是否为外部链接
const isExternalLink = computed(() => {
  return isExternal(props.to.path || "");
});

const linkType = computed(() => (isExternalLink.value ? "a" : "router-link"));

// 点击跳转
const linkProps = (to: toType) => {
  if (isExternalLink.value) {
    return {
      href: to.path,
      target: "_blank",
      rel: "noopener noreferrer",
    };
  }

  // 最终效果示例：<router-link :to="{ path: '/foo', query: { id: 1 } }" />
  return { to: to };
};

defineOptions({
  name: "AppLink",
  inheritAttrs: false,
});
</script>

<template>
  <!-- v-bind 是一个指令，用于将对象的所有属性绑定到一个元素或组件上 -->
  <component :is="linkType" v-bind="linkProps(to)">
    <slot></slot>
  </component>
</template>
