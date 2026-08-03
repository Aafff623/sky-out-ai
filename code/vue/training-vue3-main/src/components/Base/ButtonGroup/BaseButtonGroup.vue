<script lang="ts" setup>
defineProps({
  buttons: {
    required: true,
    type: Array as PropType<BaseButtonGroup[]>,
    default: () => [],
  },
});

const openMessageBox = (options: MessageBoxOptions) => {
  return new Promise((resolve, reject) => {
    const title = options.title ?? "提示";
    const type = options.type ?? "warning";
    const content = options.title ?? "将要执行此操作，是否继续？";

    ElMessageBox.confirm(content as string, title, {
      ...options,
      confirmButtonText: options.confirmButtonText ?? "确定",
      cancelButtonText: options.cancelButtonText ?? "取消",
      type,
    })
      .then(resolve)
      .catch(reject);
  });
};

function defaultHandler(item: BaseButtonGroup, index: number) {
  // 消息弹窗提示
  if (item.messageBoxProps) {
    openMessageBox(item.messageBoxProps)
      .then(() => item.messageBoxProps?.onConfirm?.(item, index))
      .catch(() => item.messageBoxProps?.onCancel?.(item, index));
    return;
  }

  if (!item?.onClick || typeof item.onClick !== "function") return;

  item.onClick?.(item, index);
}

defineOptions({
  name: "BaseButtonGroup",
});
</script>

<template>
  <div class="flex flex-wrap gap-2 justify-start mb-20px">
    <el-button
      v-for="(button, index) in buttons"
      :key="index"
      v-bind="button.props"
      @click="defaultHandler(button, index)"
      class="px-4 py-2"
    >
      <template #default>
        <div>{{ button.label }}</div>
      </template>

      <template #loading>
        <slot name="loading"></slot>
      </template>

      <template #icon>
        <slot name="icon"></slot>
      </template>
    </el-button>
  </div>
</template>
