<script lang="ts" setup>
const props = defineProps({
  emptyText: {
    type: String,
    default: "无数据",
  },
  width: {
    type: [String, Number],
    default: "100%",
  },
  height: {
    type: [String, Number],
    default: "100%",
  },
  iconClass: {
    type: String,
    default: "empty_tip",
  },
  size: {
    type: String,
    default: "3.5em",
  },
  useElementPlusIcon: {
    type: Boolean,
    default: false,
  },
});

const emptyTipSizes = computed(() => {
  return {
    width: typeof props.width === "number" ? `${props.width}px` : props.width,
    height:
      typeof props.height === "number" ? `${props.height}px` : props.height,
  };
});

defineOptions({
  name: "BaseEmptyTip",
});
</script>

<template>
  <div class="base-empty-tip" :style="emptyTipSizes">
    <div
      class="wh-full flex items-center justify-center flex-col text-gray-400 py-20px"
    >
      <template v-if="!useElementPlusIcon">
        <SvgIcon :icon-class="iconClass" v-bind="$attrs" :size="size" />
      </template>

      <template v-else>
        <el-icon v-bind="$attrs" />
      </template>

      <span class="text-#9c9c9c text-1em mt-10px">{{ emptyText }}</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.base-empty-tip {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>
