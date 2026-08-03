<script lang="ts" setup>
import { PropType } from "vue";

defineProps({
  action: {
    type: Object as PropType<ActionConfig>,
    default: () => {},
  },
  row: {
    type: Object as PropType<any>,
    default: () => {},
  },
});

function checkCondition(value: boolean | ((row: any) => boolean), row: any) {
  return typeof value === "function" ? value(row) : value;
}

const getActionText = (value: ActionConfig["text"], row: any) => {
  return typeof value === "function" ? value(row) : value;
};

defineOptions({
  name: "BaseActionButton",
});
</script>

<template>
  <el-link
    v-bind="action?.linkConfig"
    :underline="action?.linkConfig?.disabled ?? false"
    :disabled="checkCondition(action?.linkConfig?.disabled ?? false, row)"
    class="mr-8px last:mr-0"
  >
    {{ getActionText(action.text, row) }}
  </el-link>
</template>
