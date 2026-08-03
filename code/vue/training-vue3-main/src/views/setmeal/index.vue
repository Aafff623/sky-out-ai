<script lang="ts" setup>
import SetmealAPI, { baseUrls, SetmealVO } from "@/api/setmeal";
import { Plus, Delete } from "@element-plus/icons-vue";
import SearchForm from "./modules/SearchForm.vue";
import DataTable from "./modules/DataTable.vue";
import DialogForm from "./modules/DialogForm.vue";

type TableDataType = TableData<SetmealVO[]>["data"];

const route = useRoute();
const selectionList = ref<TableDataType>([]);
const JDialogForm = ref<InstanceType<typeof DialogForm> | null>();
const JTable = ref<InstanceType<typeof DataTable> | null>(null);
const JSearchForm = ref<InstanceType<typeof SearchForm> | null>(null);
const buttons = ref<Array<BaseButtonGroup>>([
  {
    label: "新增套餐",
    props: {
      type: "primary",
      icon: markRaw(Plus),
    },
    onClick() {
      JDialogForm.value?.show({
        url: baseUrls.add,
        method: "post",
      });
    },
  },
  {
    label: "批量删除",
    props: {
      type: "danger",
      icon: markRaw(Delete),
      disabled: true,
    },
    messageBoxProps: {
      title: "确认将选中的套餐删除？",
      message: "删除后将无法恢复，请谨慎操作！",
      async onConfirm() {
        const ids = selectionList.value.map((item) => item.id).join(",");
        const res = await SetmealAPI.batchDelete(ids);

        if (res.code === 200) {
          ElMessage.success("删除成功");
          searchData();
        }
      },
    },
  },
]);

onMounted(() => {
  if (route.query?.type === "create") {
    setTimeout(() => {
      JDialogForm.value?.show({
        url: baseUrls.add,
        method: "post",
      });
    }, 300);
  }
});

async function searchData(data?: any) {
  JTable.value?.updateData(data?.model);
}

function handleEdit(data: TableDataType[number]) {
  JDialogForm.value?.show({
    title: "编辑套餐",
    payload: data,
    url: baseUrls.update,
    method: "put",
    showLoading: true,
  });
}

function selectionChange(data: TableDataType) {
  buttons.value[1].props!.disabled = !data.length;
  selectionList.value = data;
}
defineOptions({
  name: "Setmeal",
});
</script>

<template>
  <BaseCard class="setmeal">
    <BaseButtonGroup :buttons="buttons" />

    <SearchForm ref="JSearchForm" @search="searchData($event)" />

    <DataTable
      ref="JTable"
      @open-edit="handleEdit"
      @selectionChange="selectionChange"
    />

    <DialogForm ref="JDialogForm" @handleRefresh="searchData" />
  </BaseCard>
</template>
