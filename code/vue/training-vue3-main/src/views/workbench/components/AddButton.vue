<script lang="ts" setup>
import type { RouteLocationRaw } from "vue-router";

defineProps({
  to: {
    type: [String, Object] as PropType<RouteLocationRaw>,
    default: "",
  },
  width: {
    type: String,
    default: "6rem",
  },
  height: {
    type: String,
    default: "6rem",
  },
});

defineEmits(["onClick"]);
</script>

<template>
  <div class="workbench-add-button">
    <router-link v-bind="$attrs" :to="to">
      <button
        ref="button"
        class="relative"
        :style="{ width: width, height: height }"
      >
        <div class="svg-wrapper">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            width="24"
            height="24"
          >
            <path fill="none" d="M0 0h24v24H0z"></path>
            <path
              fill="currentColor"
              d="M1.946 9.315c-.522-.174-.527-.455.01-.634l19.087-6.362c.529-.176.832.12.684.638l-5.454 19.086c-.15.529-.455.547-.679.045L12 14l6-8-8 6-8.054-2.685z"
            ></path>
          </svg>
        </div>

        <div class="btn-content flex-center flex-col">
          <el-icon class="mb-10px" :size="23"><CirclePlusFilled /></el-icon>
          <div class="text-12px w-full">新增菜品</div>
        </div>
      </button>
    </router-link>
  </div>
</template>

<style lang="scss" scoped>
.workbench-add-button {
  $offsetY: 10px;

  button {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    font-family: inherit;
    background: var(--el-color-primary);
    color: var(--el-color-white);
    border: none;
    border-radius: 10px;
    overflow: hidden;
    transition: all 0.2s;
    cursor: pointer;
    transition: all 0.3s ease-in-out;

    .btn-content {
      opacity: 1;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      transition: all 0.3s ease-in-out;
    }

    .svg-wrapper > svg {
      opacity: 0;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-200px, -200px);
      transition: all 0.3s ease-in;
    }

    &:hover {
      transform: translateY(-0.5em) scale(0.98);
    }

    &:hover .svg-wrapper {
      animation: fly 0.6s ease-in-out infinite alternate;
    }

    &:hover .svg-wrapper > svg {
      opacity: 1;
      transform: translate(-50%, -50%) rotate(45deg) scale(1.2);
    }

    &:hover .btn-content {
      opacity: 0;
      transform: translate(5rem, 2rem);
    }
  }

  @keyframes fly {
    from {
      transform: translateY(0.1em);
    }

    to {
      transform: translateY(-0.1em);
    }
  }
}
</style>
