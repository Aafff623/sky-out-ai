<script lang="ts" setup>
import type { FormRules, FormInstance } from "element-plus";
import DishAPI, { type DishForm, type DishFlavor } from "@/api/dish";
import { useAppStore } from "@/store/modules/app";
import { StatusEnum } from "@/enums/StatusEnum";
import { type CategoryPageVO } from "@/api/category";
import { isExternal } from "@/utils";

defineProps({
  dishOptions: {
    type: Array as PropType<CategoryPageVO[]>,
    default: () => [],
  },
});

const appStore = useAppStore();

const btn = reactive({
  // 按钮加载
  loading: false,
  // 按钮是否禁用,默认禁用
  disabled: false,
});

// 对话框是否可见
let visible = ref<boolean>(false);
const DishAddFormRef = ref<FormInstance>();
const form = reactive<DishForm>({
  id: undefined,
  name: "",
  categoryId: undefined,
  price: 0.0,
  image: "",
  description: "",
  flavors: [],
  status: StatusEnum.DISABLE,
});
const checkPrice = (_: any, value: number, callback: any) => {
  if (value <= 0) {
    callback(new Error("请为该菜品设置一个价格"));
  }
  callback();
};

const rules = reactive<FormRules<DishForm>>({
  name: [{ required: true, message: "请输入新增菜品名称", trigger: "blur" }],
  categoryId: [
    { required: true, message: "请选择该菜品所属菜品", trigger: "change" },
  ],
  price: [{ required: true, validator: checkPrice, trigger: "blur" }],
  image: [{ required: true, message: "请选择菜品图片", trigger: "change" }],
  status: [{ required: true, message: "请选择菜品状态", trigger: "change" }],
});
const innerFormRef = ref();
const innerDialogVisible = ref<boolean>(false);
const innerForm = reactive({
  image: "",
  loading: true,
});
const checkLinkImage = (_: any, value: string, callback: any) => {
  if (value && !isExternal(value)) {
    callback(new Error("请输入正确的图片链接地址"));
  } else {
    callback();
  }
};

const innerRules = reactive<FormRules<{ image: string }>>({
  image: [{ required: true, validator: checkLinkImage, trigger: "change" }],
});

// 定义预设口味选项
const presetFlavors = [
  { name: "甜味", value: ["无糖", "少糖", "半糖", "多糖", "全糖"] },
  { name: "温度", value: ["热饮", "常温", "去冰", "少冰", "多冰"] },
  { name: "忌口", value: ["不要葱", "不要蒜", "不要香菜", "不要辣"] },
  { name: "辣度", value: ["不辣", "微辣", "中辣", "重辣"] },
];
const dishFlavorTable = reactive<TableData<DishFlavor[]>>({
  data: [],
  pagination: {
    layout: "sizes, prev, pager, next",
  },
  columns: [
    {
      label: "口味名",
      slot: "name",
      width: 120,
      minWidth: 120,
      fixed: "left",
    },
    {
      label: "口味值",
      slot: "value",
    },
    {
      label: "操作",
      slot: "slot-action",
      width: 80,
      minWidth: 80,
      fixed: "right",
      actionConfig: {
        options: [
          {
            text: "删除",
            linkConfig: {
              type: "danger",
            },
            onClick: (_, $index) => {
              dishFlavorTable.data.splice($index!, 1);
            },
          },
        ],
      },
    },
  ],
}) as TableData<DishFlavor[]>;

const emits = defineEmits<{
  (e: "handleFinish"): void;
}>();

async function show(show: boolean) {
  visible.value = show;
}

// 获取未被选择的口味选项
const availableFlavors = computed(() => {
  const selectedNames = dishFlavorTable.data.map((item) => item.name);
  return presetFlavors.filter((flavor) => !selectedNames.includes(flavor.name));
});
// 当口味名改变时，自动填充对应的口味值
const handleFlavorNameChange = (row: DishFlavor) => {
  const selectedFlavor = presetFlavors.find(
    (flavor) => flavor.name === row.name
  );
  if (selectedFlavor) {
    row.value = [...selectedFlavor.value];
  } else {
    row.value = [];
  }
};
const handleAddFlavor = () => {
  dishFlavorTable.data.push({
    id: undefined,
    dishId: undefined,
    name: "",
    value: [],
  });
};

const handleSubmit = async () => {
  const flavorsData = dishFlavorTable.data;
  if (flavorsData.length) {
    if (flavorsData.some((item) => item.name === "")) {
      ElMessage.error("口味名不能为空");
      return;
    }

    if (flavorsData.some((item) => item.value?.length === 0)) {
      ElMessage.error("口味值不能为空");
      return;
    }
  }

  console.log("handleSubmit==>", flavorsData);

  const valid = await DishAddFormRef.value?.validate?.();
  if (!valid) return;
  btn.loading = true;
  btn.disabled = true;
  const res = await DishAPI.add({
    ...form,
    flavors: flavorsData.map((item) => ({
      ...item,
      value: JSON.stringify(item.value),
    })),
  });
  if (res.code === 200) {
    ElMessage.success(`新增菜品【${form.name}】成功`);
    emits("handleFinish");
    handleClose();
  }
  btn.loading = false;
  btn.disabled = false;
};

const handleClose = (done?: () => void) => {
  DishAddFormRef.value?.resetFields();
  visible.value = false;
  dishFlavorTable.data = [];
  done?.();
};

const handleUseLink = () => {
  innerDialogVisible.value = true;
};

const handleUseLinkeSubmit = () => {
  innerDialogVisible.value = false;
  form.image = innerForm.image;
  innerDialogClosed();
};

function linkImageLoad() {
  innerForm.loading = false;
  console.log("使用链接地址图片加载完毕！");
}

function innerDialogClosed() {
  innerFormRef.value?.resetFields();
}

defineExpose({
  show,
});

defineOptions({
  name: "DishAddForm",
});
</script>

<template>
  <!-- TODO: 在dialog内部的form无法获取ref导致的警告问题 -->
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="2vh"
    width="50%"
    draggable
    title="新增菜品表单"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-form
      ref="DishAddFormRef"
      :model="form"
      :rules="rules"
      label-width="120"
    >
      <el-form-item label="菜品名称：" prop="name">
        <el-input
          v-model="form.name"
          autocomplete="off"
          placeholder="请输入菜品的名称"
          clearable
        />
        <div class="form-item-help">
          <p>新增菜品的名称，例如：螺蛳鸭脚煲</p>
        </div>
      </el-form-item>

      <el-form-item label="所属分类：" prop="categoryId">
        <el-select
          v-model="form.categoryId"
          clearable
          placeholder="选择这个菜品属于哪个分类"
        >
          <el-option
            v-for="dish in dishOptions"
            :key="dish.id"
            :label="dish.name"
            :value="dish.id!"
          />
        </el-select>
        <div class="form-item-help">
          <p>新增菜品所属分类，例如：主食</p>
        </div>
      </el-form-item>

      <el-form-item label="口味做法配置：" prop="flavors">
        <div
          class="setmeal-dishes w-full bg-#f7f7f7 dark:bg-[var(--el-bg-color-overlay)] rounded-5px py-20px px-15px"
        >
          <el-button type="primary" class="mb-10px" @click="handleAddFlavor"
            >添加口味</el-button
          >

          <div class="max-h-12rem">
            <BaseTable
              v-model="dishFlavorTable"
              max-height="12rem"
              :use-pagination="(data) => data?.length > 10"
            >
              <!-- 口味名 -->
              <template #name="{ row }">
                <el-select
                  v-model="row.name"
                  clearable
                  placeholder="请选择口味名"
                  @change="() => handleFlavorNameChange(row)"
                >
                  <el-option
                    v-for="flavor in availableFlavors"
                    :key="flavor.name"
                    :label="flavor.name"
                    :value="flavor.name"
                  />
                </el-select>
              </template>

              <!-- 口味值 -->
              <template #value="{ row }">
                <el-select
                  v-model="row.value"
                  multiple
                  filterable
                  clearable
                  allow-create
                  default-first-option
                  :reserve-keyword="false"
                  placeholder="口味值"
                >
                  <!-- 合并预设值和已选择的值，避免选项丢失 -->
                  <el-option
                    v-for="(item, index) in Array.from(
                      new Set([
                        ...(presetFlavors.find((f) => f.name === row.name)
                          ?.value || []),
                        ...(row.value || []),
                      ])
                    )"
                    :key="index"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </template>
            </BaseTable>
          </div>
        </div>

        <div class="form-item-help">
          <p>
            选择该菜品的口味数据，可以多选，支持自定义【新增口味值时回车确认】
          </p>
        </div>
      </el-form-item>

      <el-form-item label="菜品封面：" prop="image">
        <SelectImage v-model="form.image" />
        <div class="form-item-help">
          <p>
            菜品的封面图片，建议尺寸：100px*100px
            <el-link :underline="false" type="primary" @click="handleUseLink"
              >【使用链接】</el-link
            >
          </p>
        </div>
      </el-form-item>

      <el-form-item label="菜品状态：" prop="status">
        <el-radio-group v-model="form.status" disabled>
          <el-radio-button label="起售" :value="StatusEnum.ENABLE" />
          <el-radio-button label="停售" :value="StatusEnum.DISABLE" />
        </el-radio-group>
        <div class="form-item-help">
          <p>菜品的状态，可以选择起售、停售【新增时默认停售】</p>
        </div>
      </el-form-item>

      <el-form-item label="菜品价格：" prop="price">
        <el-input-number
          v-model="form.price as number"
          :min="0"
          :precision="2"
          :step="1.0"
        />
        <div class="form-item-help">
          <p>为该菜品设置价格，单位：元</p>
        </div>
      </el-form-item>

      <el-form-item label="菜品描述：" prop="description">
        <el-input
          v-model="form.description"
          show-word-limit
          maxlength="500"
          type="textarea"
          placeholder="可以适当对该菜品进行一个简单的描述"
          :autosize="{ minRows: 3, maxRows: 4 }"
        />
        <div class="form-item-help">
          <p>
            对菜品的描述内容，可以是配料、烹饪方法等等，例如：螺蛳鸭脚煲，营养价值高，适合做早餐
          </p>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose()">取消</el-button>
      <el-button
        type="primary"
        :loading="btn.loading"
        :disabled="btn.disabled"
        @click="handleSubmit"
      >
        确定
      </el-button>
    </template>

    <el-dialog
      class="dialog-v1"
      v-model="innerDialogVisible"
      width="45%"
      title="🚢使用图片链接"
      append-to-body
      :fullscreen="appStore.isMobile"
      @closed="innerDialogClosed"
    >
      <el-form
        ref="innerFormRef"
        :model="innerForm"
        :rules="innerRules"
        label-width="120"
      >
        <el-form-item label="链接地址：" prop="image">
          <el-input
            v-model="innerForm.image"
            autocomplete="off"
            placeholder="请输入图片链接地址"
            clearable
          />
          <div class="form-item-help">
            <p>使用已经上传好了的图片链接地址</p>
          </div>
        </el-form-item>

        <!-- 预览图片 -->
        <el-form-item v-if="isExternal(innerForm.image)">
          <a
            :href="innerForm.image"
            class="w-100px h-100px inner-preview-image"
            target="_blank"
            title="点击预览图片"
          >
            <el-image
              v-loading="innerForm.loading"
              element-loading-text="加载中..."
              :src="innerForm.image"
              fit="cover"
              class="p-2px wh-full"
              style="border: 1px dashed #eee"
              @load="linkImageLoad"
            />
          </a>
          <div class="form-item-help">
            <p>预览链接图片</p>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button type="primary" @click="handleUseLinkeSubmit">
          🚇确定
        </el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>
