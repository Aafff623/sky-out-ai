<script lang="ts" setup>
import { useAppStore } from "@/store/modules/app";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  width: { type: String, default: "50%" },
  draggable: { type: Boolean, default: true },
  loading: { type: Boolean, default: false },
  btnLoading: { type: Boolean, default: false },
  fullscreen: { type: Boolean, default: false },
  useFooter: { type: Boolean, default: true },
  confirmText: { type: String, default: "确 定" },
  customHeader: { type: Boolean, default: false },
});

const attrs = useAttrs();
const appStore = useAppStore();
const emits = defineEmits<{
  (e: "open"): void;
  (e: "opened"): void;
  (e: "close"): void;
  (e: "closed"): void;
  (e: "confirm"): void;
  (e: "cancel"): void;
  (e: "update:modelValue", value: boolean): void;
}>();

const modelValue = useVModel(props, "modelValue", emits);

const top = computed(() =>
  props.fullscreen || appStore.isMobile ? "0" : (attrs.top as string) || "10vh"
);

const handleCancel = () => {
  modelValue.value = false;
  emits("cancel");
};

defineOptions({
  name: "BaseDialog",
});
</script>

<template>
  <el-dialog
    v-bind="$attrs"
    :top="top"
    v-model="modelValue"
    class="base-dialog"
    :width="width"
    :draggable="draggable"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :fullscreen="fullscreen || appStore.isMobile"
    @open="$emit('open')"
    @opened="$emit('opened')"
    @close="$emit('close')"
    @closed="$emit('closed')"
  >
    <!-- 自定义头部 -->
    <template #header="{ close, titleId, titleClass }" v-if="customHeader">
      <div class="custom-header">
        <slot
          name="header"
          :close="close"
          :titleId="titleId"
          :titleClass="titleClass"
        ></slot>
      </div>
    </template>

    <div v-loading="loading">
      <!-- 默认插槽 -->
      <slot></slot>
    </div>

    <template #footer>
      <div class="footer">
        <el-button @click="handleCancel">取 消</el-button>
        <el-button
          type="primary"
          @click="emits('confirm')"
          :loading="btnLoading"
          >{{ confirmText }}</el-button
        >
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss">
@mixin dialog-content-border() {
  @apply border-#eaeaea dark:border-[var(--el-border-color)];
  border-style: solid;
  border-bottom-width: 1px;
}

.base-dialog {
  // min-width: 600px;
  .el-button {
    padding: 8px 20px;
    border-radius: 2px;
  }

  .el-form {
    padding-right: 50px;

    .el-form-item__content:has(.form-item-help) {
      flex-direction: column;
      align-items: flex-start;
    }
  }

  .el-dialog__header {
    @include dialog-content-border();

    .el-dialog__title {
      font-size: 15px;
    }
  }

  .el-dialog__body {
    padding: 20px 0;
    @include dialog-content-border();
  }

  .el-alert {
    padding: 8px 30px 8px 16px;
  }

  .el-collapse {
    .el-collapse-item:last-child {
      .el-collapse-item__wrap,
      .el-collapse-item__header {
        border-bottom: none;
      }
    }
  }

  /* xl: 大于等于 1200px */
  @media (min-width: 1200px) {
    .el-dialog {
      .el-form {
        padding-right: 30px;
        padding-left: 10px;
      }
    }
  }
}
</style>
