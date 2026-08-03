<template>
  <el-popconfirm
    v-bind="$attrs"
    :confirm-button-text="($attrs['confirm-button-text'] as string) || '确认'"
    :cancel-button-text="($attrs['cancel-button-text'] as string) || '取消'"
    @confirm="$emit('confirm')"
    @cancel="$emit('cancel')"
  >
    <template #reference>
      <slot />
    </template>
  </el-popconfirm>
</template>
