<script lang="ts" setup>
import { CSSProperties } from "vue";
import * as ElementPlus from "element-plus";
import { useAppStore } from "@/store/modules/app";
import { SetmealDish } from "@/api/setmeal";
import { CategoryPageVO } from "@/api/category";
import DishAPI, { DishPageVO, DishPageQuery } from "@/api/dish";
import { Search } from "@element-plus/icons-vue";

const props = defineProps({
  modelValue: {
    type: Array as PropType<SetmealDish[]>,
    default: () => [],
  },
  categoryOptions: {
    type: Array as PropType<CategoryPageVO[]>,
    default: () => [],
  },
});

const appStore = useAppStore();
const visible = ref(false);
const loading = ref(false);
const searchKey = ref("");
const dishNameSearchKey = ref("");
const dishList = ref<DishPageVO[]>([]);
const selectedDishTab = ref<CategoryPageVO["id"]>();
const tempSetmealDishes = ref<SetmealDish[]>([]);

const emits = defineEmits(["update:modelValue"]);
// 选择的菜品列表
const selectedDishList = useVModel(props, "modelValue", emits);

const dishListFilter = computed(() => {
  if (selectedDishTab.value === "0" || selectedDishTab.value === undefined) {
    return dishList.value.filter((v) => v.name?.includes(searchKey.value));
  }
  // 先按分类过滤
  const categoryFilter = dishList.value.filter(
    (v) => v.categoryId === selectedDishTab.value
  );

  // 按实际搜索词过滤
  return categoryFilter.filter((v) => v.name?.includes(searchKey.value));
});

// 菜品分类选项
const dishCategoryOptions = computed(() => {
  const newCategoryOptions = props.categoryOptions.filter((v) => v.type === 1);
  return [{ name: "全部", categoryId: null }, ...newCategoryOptions];
});

const addDishBodyStyle = computed<CSSProperties>(() => {
  if (appStore.isMobile) {
    return {
      flexDirection: "column",
    };
  }

  return {
    height: "500px",
    overflow: "hidden",
  };
});

// 确认添加菜品表单提交
const handleConfirmAddDish = async () => {
  if (tempSetmealDishes.value?.length === 0) {
    ElMessage.error("友情提示，您还没有选择任何菜品哦~");
    return;
  }

  selectedDishList.value = [...tempSetmealDishes.value];
  onClosed();
};

// 搜索菜品
const handleSearchDish = () => {
  searchKey.value = dishNameSearchKey.value.trim();

  if (searchKey.value) {
    const matchedDishes = dishList.value.filter((v) =>
      v.name?.includes(searchKey.value)
    );

    if (matchedDishes.length > 0) {
      selectedDishTab.value = matchedDishes[0].categoryId;
    }
  } else {
    // 清空搜索时重置
    searchKey.value = "";
  }
};

// 选择菜品
const handleSelectDish = (dish: DishPageVO) => {
  const index = tempSetmealDishes.value?.findIndex((v) => v.dishId === dish.id);

  if (index === -1) {
    tempSetmealDishes.value?.unshift({
      dishId: dish.id,
      name: dish.name,
      price: dish.price,
      copies: 1,
      image: dish.image,
    });
  }
};

// 移除菜品
const handleRemoveDish = (setmealDish: SetmealDish) => {
  tempSetmealDishes.value = tempSetmealDishes.value?.filter(
    (v) => v.dishId !== setmealDish.dishId
  );
};

// 切换菜品分类
const handleDishTabChange = (categoryId: ElementPlus.TabPaneName) => {
  dishList.value = [];
  getDishList({
    categoryId: (categoryId as number) != 0 ? categoryId : undefined,
  });
};

async function getDishList(
  payload?: Pick<DishPageQuery, "categoryId" | "name">
) {
  loading.value = true;
  const { code, data } = await DishAPI.getPage({
    page: 1,
    pageSize: 1000,
    ...payload,
  });

  if (code === 200) {
    dishList.value = data.records || [];
    console.log(dishList.value);
  }
  loading.value = false;
}

// 弹窗打开
async function onOpened() {
  if (selectedDishList.value.length > 0) {
    tempSetmealDishes.value = [...selectedDishList.value];
  }
  // 获取全部菜品
  await getDishList();
  // 设置默认选中tab
  selectedDishTab.value = dishCategoryOptions.value?.[0].id;
}

// 弹窗关闭
function onClosed() {
  // 每次添加菜品dialog关闭都清空数据，再次打开时重新赋值
  tempSetmealDishes.value = [];
  selectedDishTab.value = undefined; // 重置分类tab
  dishNameSearchKey.value = ""; // 清空搜索词
  loading.value = false; // 重置菜品列表loading
  visible.value = false; // 关闭弹窗
}

// 动画进入
function enterAnimation(el: any) {
  const target = el as HTMLElement;
  const index = parseInt(target.dataset.index || "0");
  const isEven = index % 2 === 0;
  const direction = isEven ? 1.875 : -1.875; // 偶数向左，奇数向右

  // 设置初始状态
  target.style.opacity = "0";
  target.style.transform = `translateX(${direction}rem)`;

  target.offsetHeight; // 触发重绘

  // 执行动画
  target.style.transition = "all 0.3s ease";
  target.style.opacity = "1";
  target.style.transform = "translateX(0)";
}

// 动画离开
function leaveAnimation(el: any, done: Function) {
  const target = el as HTMLElement;
  const parent = target.parentElement!;
  const itemHeight = target.offsetHeight + 10; // 元素高度 + marginTop-10px
  const index = parseInt(target.dataset.index || "0");
  const isEven = index % 2 === 0;
  const direction = isEven ? 1.875 : -1.875;

  target.style.transition = "all 0.3s ease";
  target.style.opacity = "0";
  target.style.transform = `translateX(${direction}rem)`;

  // 2. 同时移动父容器
  if (index === 0) {
    parent.style.transition = "transform 0.3s ease";
    parent.style.transform = `translateY(-${itemHeight}px)`;
  }

  setTimeout(() => {
    if (index === 0) {
      parent.style.transition = "none";
      parent.style.transform = "translateY(0)";

      // 强制重绘确保复位生效
      parent.offsetHeight;
      parent.style.transition = "transform 0.3s ease";
    }

    done();
  }, 300);
}

defineExpose({
  show() {
    visible.value = true;
  },
});

defineOptions({
  name: "AddDishDialog",
});
</script>

<template>
  <BaseDialog
    top="5vh"
    width="60%"
    v-model="visible"
    append-to-body
    custom-header
    :show-close="false"
    @opened="onOpened"
    @closed="onClosed"
    @confirm="handleConfirmAddDish"
  >
    <template #header="{ titleId, titleClass }">
      <div class="add-dish-header flex justify-between items-center">
        <div :id="titleId" :class="titleClass">添加菜品</div>

        <el-input
          v-model="dishNameSearchKey"
          clearable
          style="max-width: 280px"
          :prefix-icon="Search"
          placeholder="请输入菜品名称进行搜索"
          @change="handleSearchDish"
        />
      </div>
    </template>

    <div
      class="add-dish-body flex"
      :style="addDishBodyStyle"
      :class="appStore.isMobile ? 'mobile-size' : ''"
    >
      <div class="category-left flex-1">
        <el-tabs
          :tab-position="appStore.isMobile ? 'top' : 'left'"
          class="category-tabs"
          v-model="selectedDishTab"
          @tab-change="handleDishTabChange"
        >
          <el-tab-pane
            v-for="c in dishCategoryOptions"
            :key="c.id"
            :label="c.name"
            :name="c.id"
          >
            <div
              v-loading="loading"
              element-loading-text="加载菜品中..."
              :class="{ 'justify-start!': appStore.isMobile }"
              class="dish-list flex flex-wrap gap-10px pt-3px"
            >
              <div
                v-for="(d, dIndex) in dishListFilter"
                :key="d.id"
                v-stagger:[dIndex]
                @click="handleSelectDish(d)"
                :class="{
                  'selected-dish-item': tempSetmealDishes?.some(
                    (v) => v.dishId === d.id
                  ),
                }"
                class="dish-item bg-white dark:bg-[var(--el-bg-color-overlay)] hover:-translate-y-2px"
              >
                <div class="w-full h-15 rounded-5px overflow-hidden">
                  <el-image
                    :src="d.image"
                    :title="d.description"
                    fit="cover"
                    class="w-full h-full transition-transform duration-300"
                  />
                </div>

                <div class="p-5px dish-item-content">
                  <div class="truncate">
                    <span class="text-12px font-500" :title="d.name">{{
                      d.name
                    }}</span>
                  </div>

                  <div class="flex justify-center items-center">
                    <span
                      class="text-14px text-primary font-600 whitespace-nowrap"
                    >
                      ¥{{ d.price }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 添加空状态提示 -->
              <BaseEmptyTip
                emptyText="没有找到相关菜品"
                v-if="dishListFilter.length === 0"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div
        v-loading.once="loading"
        element-loading-text="加载菜品中..."
        :class="{ 'w-30%': !appStore.isMobile }"
        class="selected-dish-right relative min-w-228px h-full overflow-y-auto overflow-x-hidden"
      >
        <div class="title mb-10px pl-20px bg-#fff dark:bg-#141414">
          已选菜品({{ tempSetmealDishes?.length || 0 }})
        </div>

        <div
          class="selected-dish-list w-full px-20px"
          :style="{
            transform: `translateX(${tempSetmealDishes?.length ? 0 : 500}px)`,
          }"
        >
          <transition-group
            tag="div"
            mode="out-in"
            name="selected-dish-item"
            @enter="enterAnimation"
            @leave="leaveAnimation"
          >
            <div
              v-for="(d, index) in tempSetmealDishes"
              :key="d.dishId"
              class="selected-dish-item w-full flex items-start py-10px px-15px bg-white dark:bg-[var(--el-bg-color-overlay)] rounded-5px mb-10px"
              :data-index="index"
            >
              <div
                class="w-3rem h-3rem rounded-5px overflow-hidden mr-10px border-2px border-solid border-#eee dark:border-#949494"
              >
                <el-image :src="d.image" fit="cover" class="w-full h-full" />
              </div>

              <div class="flex flex-col mr-20px max-w-[calc(100%-3rem-30px)]">
                <div class="dish-name truncate cursor-default" :title="d.name">
                  {{ d.name }}
                </div>

                <div class="dish-price text-[var(--el-color-primary)]">
                  ￥{{ d.price }}
                </div>
              </div>

              <div
                class="remove-icon cursor-pointer flex items-center justify-center"
              >
                <el-icon @click="handleRemoveDish(d)">
                  <Close />
                </el-icon>
              </div>
            </div>
          </transition-group>
        </div>

        <!-- 添加空状态提示 -->
        <div
          v-if="!tempSetmealDishes?.length"
          :style="{
            overflow: 'hidden',
            height: tempSetmealDishes?.length ? '100%' : 'calc(100% - 100px)',
          }"
          class="w-full absolute top-50% left-50% translate-x-[-50%] translate-y-[-50%]"
        >
          <BaseEmptyTip emptyText="还没有选任何菜品哦" />
        </div>
      </div>

      <el-backtop target=".selected-dish-right">
        <svg-icon icon-class="backtop" size="24px" />
      </el-backtop>
    </div>
  </BaseDialog>
</template>

<style lang="scss" scoped>
$dialog-height: 500px;

.add-dish-header {
  padding-top: 15px;
}

.add-dish-body {
  .category-left {
    :deep(.el-tabs__content) {
      padding-right: 10px;
      min-width: 115px;
      height: $dialog-height;
      overflow: auto;
    }

    :deep(.el-tabs__header-vertical) {
      height: $dialog-height;
    }

    .dish-list {
      .dish-item {
        @apply border-#dadada dark:border-[var(--el-border-color-light)];
        cursor: pointer;
        border-radius: 5px;
        min-width: 7.4rem;
        max-width: 7.4rem;
        padding: 10px 10px 0 10px;
        // overflow: hidden;
        transition: all 0.3s ease-in-out;
        border-width: 1px;
        border-style: solid;
        box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.12);

        &:hover {
          border: 1px solid var(--el-color-primary);
        }
      }

      .selected-dish-item {
        border: 1px solid var(--el-color-primary);
      }
    }
  }

  .selected-dish-right {
    @apply border-#eee dark:border-#414243;
    border-style: solid;
    border-left-width: 2px;
    .title {
      position: sticky;
      top: 0px;
      left: 0;
      height: 30px;
      width: 100%;
      z-index: 99;
    }

    .selected-dish-list {
      transition: all 0.3s ease-in-out;

      .selected-dish-item {
        position: relative;
        box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.12);

        .remove-icon {
          position: absolute;
          top: 50%;
          right: 10px;
          transform: translateY(-50%);
          font-size: 16px;

          &:hover {
            color: var(--el-color-primary);
          }
        }
      }
    }
  }
}

.mobile-size {
  padding: 0 20px;

  .category-left {
    margin-bottom: 20px;

    :deep(.el-tabs__content) {
      margin-right: 0;
      min-width: 100%;
      height: 300px;
    }
  }

  .selected-dish-right {
    height: 17.5rem;
    max-height: 17.5rem;
    max-width: 100% !important;
    border-left: none;
    border-top: 2px solid #eee;

    .title {
      height: 50px;
      @apply flex items-center pl-0px;
    }

    .selected-dish-list {
      @apply px-10px;
    }
  }
}

.selected-dish-item-enter-active,
.selected-dish-item-leave-active {
  transition: all 0.3s ease;
  position: absolute;
  width: 100%;
}

.selected-dish-item-enter-from {
  opacity: 0;
  transform: translateX(-1.875rem);
}

.selected-dish-item-leave-to {
  opacity: 0;
  transform: translateX(1.875rem);
}

.selected-dish-item-leave-active {
  position: absolute;
}

.selected-dish-item-move {
  position: relative;
  transition: all 0.3s ease;
  // 启用硬件加速
  will-change: transform;
}

@media (max-width: 1020px) {
  .category-left {
    .dish-list {
      justify-content: center;
    }
  }
}
</style>
