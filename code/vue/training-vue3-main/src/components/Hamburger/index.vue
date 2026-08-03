<script setup lang="ts">
defineProps({
  isActive: {
    required: true,
    type: Boolean,
    default: false,
  },
  size: {
    type: String,
    default: "1em",
  },
});

const emit = defineEmits(["toggleClick"]);

function toggleClick() {
  emit("toggleClick");
}
</script>

<template>
  <div
    class="px-15px flex-center color-[var(--el-text-color-regular)]"
    @click="toggleClick"
  >
    <svg-icon
      :size="size"
      class="hamburger"
      :class="{ 'is-active': isActive }"
      icon-class="collapse"
    />
  </div>
</template>

<style scoped lang="scss">
.hamburger {
  vertical-align: middle;
  cursor: pointer;
  transform: scaleX(-1);
}

.hamburger.is-active {
  transform: scaleX(1);
}
</style>
