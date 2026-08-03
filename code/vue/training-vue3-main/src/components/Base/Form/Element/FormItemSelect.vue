<script lang="ts" setup>
defineProps({
  value: {
    type: [String, Number, Array],
    default: () => "",
  },
  options: {
    type: Array as PropType<FormItemOptions>,
    default: () => [],
  },
});

const emit = defineEmits(["input", "blur"]);

function change(val: any) {
  emit("input", val);
}

defineOptions({
  name: "FormElementSelect",
});
</script>

<template>
  <el-select
    v-bind="$attrs"
    :value="value"
    @change="change"
    @blur="$emit('blur', $event)"
  >
    <template v-for="item in options" :key="item.key || item.value">
      <el-option
        :label="item.label"
        :value="item.value"
        :disabled="item.disabled"
      />
    </template>
  </el-select>
</template>
