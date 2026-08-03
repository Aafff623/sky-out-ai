<script lang="ts" setup>
import * as EmployeeAPI from "@/api/employee";

const JTable = ref<BaseTableType>();
const tableData = reactive<TableData<EmployeeAPI.UserPageVO[]>>({
  data: [],
  pagination: {},
  columns: [
    {
      label: "用户名",
      slot: "username",
      minWidth: "100px",
    },
    {
      label: "姓名",
      prop: "name",
    },
    {
      label: "手机号",
      prop: "phone",
    },
    {
      label: "性别",
      slot: "sex",
      width: "70px",
    },
    {
      label: "状态",
      slot: "status",
    },
    {
      label: "更新时间",
      prop: "updateTime",
      minWidth: "160px",
    },
    {
      label: "操作",
      slot: "action",
      minWidth: "140px",
      fixed: "right",
    },
  ],
}) as TableData<EmployeeAPI.UserPageVO[]>;

onMounted(() => {
  JTable.value?.updateData({
    requestConfig: { url: EmployeeAPI.baseUrls.getPage },
  });
});
</script>

<template>
  <BaseTable ref="JTable" v-model="tableData"> </BaseTable>
</template>
