<script lang="ts" setup>
import * as SetmealAPI from "@/api/setmeal";
import { StatusEnum } from "@/enums/StatusEnum";

type JTableData = TableData<SetmealAPI.SetmealVO[]>;

const route = useRoute();
const JTable = ref<BaseTableType>();
const tableData = reactive<JTableData>({
  data: [],
  pagination: {},
  columns: [
    {
      width: 50,
      type: "selection",
    },
    {
      label: "套餐名称",
      prop: "name",
      minWidth: "220px",
    },
    {
      label: "图片",
      prop: "image",
      slot: "slot-image",
      minWidth: "150px",
      imageConfig: {
        size: 50,
      },
    },
    {
      label: "套餐分类",
      prop: "categoryName",
      minWidth: "100px",
    },
    {
      label: "套餐价格",
      prop: "price",
      minWidth: "100px",
      symbol: {
        prefix: "¥",
      },
    },
    {
      label: "售卖状态",
      slot: "slot-tag",
      prop: "status",
      minWidth: "100px",
      tagConfig: {
        options: {
          [StatusEnum.DISABLE]: { text: "停售", type: "info" },
          [StatusEnum.ENABLE]: { text: "启售", type: "success" },
        },
      },
    },
    {
      label: "更新时间",
      prop: "updateTime",
      minWidth: "200px",
    },
    {
      label: "操作",
      slot: "slot-action",
      minWidth: "140px",
      fixed: "right",
      actionConfig: {
        options: [
          {
            text: "编辑",
            linkConfig: {
              type: "primary",
            },
            onClick: (row) => emits("openEdit", row),
          },
          {
            type: "startStop",
            text: (row) => (row.status === StatusEnum.ENABLE ? "停售" : "启售"),
            requestUrl: (row) =>
              `${SetmealAPI.baseUrls.startOrStop}/${row.status! ^ StatusEnum.ENABLE}`,
            requestMethod: "post",
            linkConfig: {
              type: "warning",
            },
            popupConfirmConfig: {
              title: (row) =>
                row.status === StatusEnum.ENABLE
                  ? "确认停售该菜品？"
                  : "确认启售该菜品？",
            },
          },
          {
            type: "delete",
            text: "删除",
            linkConfig: {
              type: "danger",
            },
            requestUrl: SetmealAPI.baseUrls.batchDelete,
            popupConfirmConfig: {
              title: "确认删除选中菜品？",
            },
          },
        ],
      },
    },
  ],
}) as JTableData;

const emits = defineEmits<{
  (e: "openEdit", row: SetmealAPI.SetmealVO): void;
  (e: "selectionChange", selection: SetmealAPI.SetmealVO[]): void;
}>();

async function updateData(data?: SetmealAPI.SetmealPageQueryDTO) {
  // console.log("DataTable", data);
  JTable.value?.updateData<SetmealAPI.SetmealPageQueryDTO>({
    queryParams: {
      ...data,
      //TODO: 此处进行了注释，不注释会导致套餐管理售卖状态查询有问题
      // status:
      //   (data?.status ?? route.query?.status)
      //     ? Number(route.query?.status)
      //     : undefined,
    },
    requestConfig: { url: SetmealAPI.baseUrls.getPage },
  });
}

onMounted(updateData);

defineExpose({
  updateData,
});
</script>

<template>
  <BaseTable
    ref="JTable"
    v-model="tableData"
    @selection-change="$emit('selectionChange', $event)"
  />
</template>
