<script lang="ts" setup>
import SetmealAPI from "@/api/setmeal";
import CategoryAPI from "@/api/category";
import * as ElementPlus from "element-plus";
import { StatusEnum } from "@/enums/StatusEnum";

// 搜索表单只承载筛选字段，分页参数由列表页注入，故为 Partial
type ModelType = Partial<ExtractParam<typeof SetmealAPI.getPage>>;
type CompType = [
  typeof ElementPlus.ElInput,
  typeof ElementPlus.ElSelect,
  typeof ElementPlus.ElSelect,
];

const emits = defineEmits<{
  (e: "search", value: FormModelData): void;
}>();

const route = useRoute();
const JForm = ref<BaseFormType>();
const model = reactive<ModelType>({
  status: route.query?.status
    ? (Number(route.query?.status) as StatusEnum)
    : undefined,
});
const fields = ref<Fields<CompType, ModelType>>([
  {
    label: "套餐名称",
    prop: "name",
    style: "width: 300px",
    component: markRaw(ElementPlus.ElInput),
    props: {
      clearable: true,
      placeholder: "请输入套餐名称",
    },
  },
  {
    label: "套餐分类",
    prop: "categoryId",
    style: "width: 300px",
    events: ["categoryId-change"],
    component: markRaw(ElementPlus.ElSelect),
    childComponent: markRaw(ElementPlus.ElOption),
    props: {
      clearable: true,
      placeholder: "请选择套餐分类",
    },
    childProps: {
      options: [],
    },
  },
  {
    label: "售卖状态",
    prop: "status",
    style: "width: 300px",
    events: ["status-change"],
    component: markRaw(ElementPlus.ElSelect),
    childComponent: markRaw(ElementPlus.ElOption),
    props: {
      clearable: true,
      placeholder: "请选择售卖状态",
    },
    childProps: {
      options: [
        { label: "启售", value: 1 },
        { label: "停售", value: 0 },
      ],
    },
  },
]);

onBeforeMount(async () => {
  const res = await CategoryAPI.getPage({
    page: 1,
    pageSize: 1000,
    type: 2, // 只显示套餐分类
  });

  const index = fields.value.findIndex((item) => item.prop === "categoryId");
  fields.value[index].childProps!.options = res.data.records?.map((item) => ({
    label: item.name,
    value: item.id,
  }));
});

// BaseForm 泛型事件负载较宽，用 any 兼容（prop 为 string，无法收窄到 keyof ModelType）
const handleSearch = (value: FormEventPayload<any>) => {
  emits("search", value);
};
</script>

<template>
  <BaseForm
    v-bind="$attrs"
    ref="JForm"
    use-search
    :model="model"
    :fields="fields"
    @search="handleSearch"
    @categoryId-change="handleSearch"
    @status-change="handleSearch"
  />
</template>
