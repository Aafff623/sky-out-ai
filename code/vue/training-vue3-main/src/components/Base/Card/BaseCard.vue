<script lang="ts" setup>
import type { RouteLocationRaw } from "vue-router";
import { formatDate } from "@/utils/index";

const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  headerClass: {
    type: String,
    default: "",
  },
  hideHeader: {
    type: Boolean,
    default: false,
  },
  hideTitle: {
    type: Boolean,
    default: false,
  },
  // 是否显示标题文本左侧的装饰线
  showDecoration: {
    type: Boolean,
    default: true,
  },
  // 副标题文本
  subTitle: {
    type: String,
    default: "",
  },
  // 右侧导航文本
  rightNavText: {
    type: String,
    default: "",
  },
  staggerOptions: {
    type: Object as PropType<StaggerOptions>,
    default: () => ({}),
  },
  // 右侧导航路由路径
  rightNavTo: {
    type: [String, Object] as PropType<
      RouteLocationRaw | { [key: string]: any }
    >,
    default: "",
    validator: (value: RouteLocationRaw) => {
      return (
        typeof value === "string" ||
        (typeof value === "object" && ("path" in value || "name" in value))
      );
    },
  },
  // 副标题显示当前时间
  showCurrentTime: {
    type: Boolean,
    default: false,
  },
  subTitleFormat: {
    type: String as PropType<CommonDateFormat>,
    default: "YYYY-MM-DD",
  },
});

const route = useRoute();
const slots = useSlots();
const currentTime = ref("");
let timer: number | NodeJS.Timeout | null = null;

const curTitle = computed(() => {
  return props.title || route.meta.title;
});

const hasCustomTitle = computed(() => !!slots.title);
const shouldShowTitle = computed(() => {
  if (hasCustomTitle.value) return false;

  return !props.hideTitle;
});

const hasRightNav = computed(
  () => props.rightNavText || useSlots()["right-nav"]
);

// 组合后的副标题内容
const combinedSubTitle = computed(() => {
  return props.showCurrentTime ? currentTime.value : props.subTitle;
});

onMounted(() => {
  if (props.showCurrentTime) {
    updateTime();
    // 定时更新时间（根据需要）
    // timer = setInterval(updateTime, 1000);
  }
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});

const updateTime = () => {
  currentTime.value = formatDate(new Date(), props.subTitleFormat);
};

defineOptions({
  name: "BaseCard",
});
</script>

<template>
  <div class="base-card" v-stagger="staggerOptions">
    <el-card v-bind="$attrs">
      <div v-if="!hideHeader" class="base-card-title" :class="headerClass">
        <el-row :gutter="10" justify="space-between" class="w-full">
          <el-col class="flex-y-center!" :span="12">
            <div
              v-if="shouldShowTitle"
              class="title-content flex-y-center justify-start"
              :class="{ 'hide-decoration': showDecoration && !shouldShowTitle }"
            >
              <slot name="title">
                <div class="title-wrapper">
                  <span class="default-title">{{ curTitle }}</span>
                  <span v-if="combinedSubTitle" class="sub-title">
                    {{ combinedSubTitle }}
                  </span>
                </div>
              </slot>
            </div>
          </el-col>

          <el-col class="flex-y-center!" :span="12">
            <div
              v-if="hasRightNav"
              class="right-nav w-full flex-y-center justify-end"
            >
              <slot name="right-nav">
                <router-link
                  v-if="rightNavTo"
                  :to="rightNavTo"
                  class="nav-link"
                >
                  <span>{{ rightNavText }}</span>
                  <el-icon :size="14"><arrow-right /></el-icon>
                </router-link>

                <div v-else class="nav-link">
                  <span>{{ rightNavText }}</span>
                  <el-icon :size="14"><arrow-right /></el-icon>
                </div>
              </slot>
            </div>
          </el-col>
        </el-row>
      </div>

      <slot></slot>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.base-card {
  .base-card-title {
    margin-bottom: 15px;

    .title-content {
      display: flex;
      align-items: baseline;
      position: relative;
      padding-left: 10px;

      &::before {
        content: "";
        position: absolute;
        top: 50%;
        left: 0;
        width: 5px;
        height: 70%;
        display: block;
        border-radius: 2px;
        transform: translateY(-50%);
        background-color: var(--el-color-primary);
      }

      .title-wrapper {
        display: flex;
        align-items: center;
        gap: 12px;

        // 默认标题样式
        .default-title {
          font-size: 1rem;
          color: var(--el-text-color-primary);
        }

        .sub-title {
          font-size: 0.875rem;
          color: var(--el-text-color-secondary);
          line-height: 1;
        }
      }
    }

    .hide-decoration::before {
      display: none;
    }

    .right-nav {
      .nav-link {
        display: inline-flex;
        align-items: center;
        gap: 3px;
        cursor: pointer;
        color: var(--el-text-color-regular);
        transition: opacity 0.2s;

        &:hover {
          opacity: 0.8;
          color: var(--el-color-primary);
        }

        span {
          font-size: 14px;
        }

        .el-icon {
          margin-top: 1px;
        }
      }
    }
  }
}

@media (max-width: 469px) {
  .base-card {
    .title-wrapper {
      gap: 3px !important;
      flex-direction: column;
      align-items: flex-start !important;
      margin-left: 4px;
    }
  }
}
</style>
