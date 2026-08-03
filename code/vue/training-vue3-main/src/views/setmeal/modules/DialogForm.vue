<script lang="ts" setup>
import SetmealAPI from "@/api/setmeal";
import * as ElementPlus from "element-plus";
import CategoryAPI, { CategoryPageVO } from "@/api/category";
import SelectImage from "@/components/SelectImage/index.vue";
import AddDishDialog from "./AddDishDialog.vue";

type ModelType = ExtractParam<typeof SetmealAPI.add>;
type JTableType = TableData<NonNullable<ModelType["setmealDishes"]>>;
type CompType = [
  typeof ElementPlus.ElInput,
  typeof ElementPlus.ElRadioGroup,
  typeof ElementPlus.ElSelect,
  any,
  typeof SelectImage,
  typeof ElementPlus.ElInputNumber,
  typeof ElementPlus.ElInput,
];

const title = ref("");
const model = ref<ModelType>({
  price: 0,
  status: 0, // 默认停用
  image: "",
});
const JAddDishDialog = ref<InstanceType<typeof AddDishDialog>>();
const JDialogForm = ref<BaseFormDialogType>();
const categoryOptions = ref<CategoryPageVO[]>([]);
const fields = ref<Fields<CompType, ModelType>>([
  {
    label: "套餐名称:",
    prop: "name",
    style: { width: "100%" },
    component: markRaw(ElementPlus.ElInput),
    props: {
      clearable: true,
      placeholder: "请输入套餐名称",
    },
    rules: [{ required: true, message: "请输入套餐名称", trigger: "blur" }],
  },
  {
    label: "套餐状态:",
    prop: "status",
    style: { width: "100%" },
    component: markRaw(ElementPlus.ElRadioGroup),
    childComponent: markRaw(ElementPlus.ElRadioButton),
    props: {
      disabled: true,
      clearable: true,
      placeholder: "请选择套餐状态",
      tip: "默认套餐处于停用状态，首次新增无法修改，避免移动端直接上架",
    },
    childProps: {
      options: [
        { label: "停用", value: 0 },
        { label: "启用", value: 1 },
      ],
    },
  },
  {
    label: "套餐分类:",
    prop: "categoryId",
    style: { width: "100%" },
    component: markRaw(ElementPlus.ElSelect),
    childComponent: markRaw(ElementPlus.ElOption),
    props: {
      clearable: true,
      placeholder: "请选择套餐分类类型",
    },
    childProps: {
      options: [],
    },
    rules: [{ required: true, message: "请输入套餐名称", trigger: ["change"] }],
  },
  {
    label: "套餐菜品:",
    prop: "setmealDishes",
    style: { width: "100%" },
    props: {
      otherSlot: true,
      tip: "该套餐包含有哪些菜品，至少选择两个菜品才能组成一个套餐!",
    },
    isManualValidate: true,
    rules: [
      {
        required: true,
        validator: (_, __, callback) => {
          if (dishTableData.data?.length <= 1) {
            callback(new Error("至少选择两道菜品"));
            return;
          }

          callback();
        },
        trigger: ["change"],
      },
    ],
  },
  {
    label: "套餐封面",
    prop: "image",
    style: { width: "100%" },
    component: markRaw(SelectImage),
    props: {
      otherSlot: true,
      tip: "选择一张该套餐的图片作为封面图",
    },
    isManualValidate: true,
    rules: [
      {
        required: true,
        validator: (_, value, callback) => {
          if (!value) {
            callback(new Error("请选择套餐封面"));
            return;
          }

          callback();
        },
        trigger: ["change"],
      },
    ],
  },
  {
    label: "套餐价格:",
    prop: "price",
    style: { width: "100%" },
    component: markRaw(ElementPlus.ElInputNumber),
    props: {
      precision: 2,
      step: 0.1,
      min: 0,
      placeholder: "套餐价格",
      onInput: (val) => val?.toString().replace(/[^\d]/g, ""),
    },
    rules: [
      {
        required: true,
        validator: (_, value, callback) => {
          if (value === null || value === undefined || value === "") {
            callback(new Error("请输入套餐价格"));
            return;
          }

          if (typeof value !== "number") {
            callback(new Error("请输入数字"));
            return;
          }

          if (value <= 0) {
            callback(new Error("套餐价格必须大于 0"));
            return;
          }

          callback();
        },
        trigger: ["change", "blur"],
      },
    ],
  },
  {
    label: "套餐描述:",
    prop: "description",
    style: { width: "100%" },
    component: markRaw(ElementPlus.ElInput),
    props: {
      type: "textarea",
      autosize: { minRows: 4, maxRows: 6 },
      placeholder: "请输入套餐描述",
      tip: "描述该套餐具有的性价比、包含的菜品、价格优惠等",
    },
  },
]);
const dishTableData = reactive<JTableType>({
  data: [],
  pagination: {
    layout: "sizes, prev, pager, next",
    hidden: (model.value.setmealDishes?.length || 0) <= 10,
  },
  columns: [
    {
      label: "图片",
      prop: "image",
      slot: "slot-image",
      imageConfig: {
        size: 40,
      },
    },
    {
      label: "菜品名称",
      prop: "name",
      minWidth: 150,
    },
    {
      label: "价格",
      prop: "price",
      symbol: {
        prefix: "¥",
      },
    },
    {
      label: "份数",
      prop: "copies",
      symbol: {
        suffix: "份",
      },
    },
    {
      label: "操作",
      slot: "slot-action",
      minWidth: "100px",
      fixed: "right",
      actionConfig: {
        options: [
          {
            text: "删除",
            linkConfig: {
              type: "danger",
            },
            onClick: (row) => {
              dishTableData.data = dishTableData.data?.filter(
                (v) => v.dishId !== row.dishId
              );
            },
          },
        ],
      },
    },
  ],
}) as JTableType;

const emits = defineEmits(["handleRefresh"]);

// 表单提交
const handleSubmit = async (data: ModelType) => {
  if (dishTableData.data?.length === 0) {
    ElMessage.error("套餐菜品为空，请至少选择两道菜品");
    return;
  }

  const res = await JDialogForm.value?.submitRequest<ModelType>({
    ...data,
    setmealDishes: dishTableData.data,
  });

  res?.code === 200 && emits("handleRefresh");
};

function setStatusDisabled() {
  const statusIndex = fields.value.findIndex((v) => v.prop === "status");
  if (statusIndex !== -1) {
    fields.value[statusIndex].props!.disabled = title.value === "新增套餐";
  }
}

// 设置分类下拉选项
async function setCategoryOptions() {
  const { data } = await CategoryAPI.getPage({
    page: 1,
    pageSize: 1000,
  });

  categoryOptions.value = data.records || [];
  const cateIndex = fields.value.findIndex((v) => v.prop === "categoryId");

  if (cateIndex !== -1) {
    const childProps = fields.value[cateIndex].childProps;
    childProps!.options = categoryOptions.value
      .filter((v) => v.type === 2)
      .map((v) => ({ label: v.name, value: v.id }));
  }
}

async function getSetmealDetail(data?: DialogShowParams<ModelType>) {
  if (!data?.payload?.id) return;

  const { id } = data.payload;
  const res = await SetmealAPI.getById(id);

  if (res.code === 200) {
    model.value = { ...res.data };
    dishTableData.data = res.data.setmealDishes || [];
  }
}

// 关闭弹窗
function onClosed() {
  dishTableData.data = [];
  model.value = {
    name: "",
    status: 0,
    price: 0,
  };
}

defineExpose({
  async show(data?: DialogShowParams<ModelType>) {
    title.value = data?.title || "新增套餐";
    setStatusDisabled();
    const { closeLoading } = await JDialogForm.value!.show(data);
    await Promise.all([getSetmealDetail(data), setCategoryOptions()]);
    closeLoading?.();
  },
});
</script>

<template>
  <div class="setmeal-dialog-form">
    <BaseFormDialog
      ref="JDialogForm"
      v-bind="$attrs"
      :title="title"
      width="55%"
      top="1vh"
      :model="model"
      :fields="fields"
      @closed="onClosed"
      @submit="handleSubmit"
    >
      <template #dialog-setmealDishes-other>
        <div
          class="setmeal-dishes w-full bg-#f7f7f7 dark:bg-[var(--el-bg-color-overlay)] rounded-5px py-20px px-15px"
        >
          <el-button
            type="primary"
            class="mb-10px"
            @click="JAddDishDialog?.show()"
            >添加菜品</el-button
          >

          <div class="max-h-12rem">
            <BaseTable v-model="dishTableData" max-height="12rem" />
          </div>
        </div>
      </template>
    </BaseFormDialog>

    <!-- 添加菜品dialog -->
    <AddDishDialog
      ref="JAddDishDialog"
      v-model="dishTableData.data"
      :categoryOptions="categoryOptions"
    />
  </div>
</template>

<style lang="scss" scoped>
.setmeal-dishes {
  border: 1px solid var(--el-border-color-light);

  :deep(.table-area) {
    flex: none;
  }

  :deep(.page-area) {
    padding: 0;
  }
}
</style>
