<script lang="ts" setup>
import { Search } from "@element-plus/icons-vue";
import { StatusEnum } from "@/enums/StatusEnum";
import EmployeeAPI, {
  type UserPageQuery,
  UserPageVO,
  baseUrls,
} from "@/api/employee";
import { AddForm, EditForm } from "./modules";
// import DialogForm from "./modules/DialogForm.vue";

const route = useRoute();
const loading = ref(false);
const total = ref(0);
const JTable = ref<BaseTableType>();
// const JDialogForm = ref<BaseFormDialogType>();
const EmployeeAddFormRef = ref();
const EmployeeEditFormRef = ref();
const queryParams = reactive<UserPageQuery>({
  name: "",
  page: 1,
  pageSize: 6,
});
const tableData = reactive<TableData<UserPageVO[]>>({
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
}) as TableData<UserPageVO[]>;

onMounted(() => {
  JTable.value?.updateData({ requestConfig: { url: baseUrls.getPage } });
});

function handleQuery() {
  loading.value = true;
  EmployeeAPI.getPage(queryParams)
    .then((res) => {
      total.value = res.data?.total;
      tableData.data = res.data?.records;
      console.log("获取员工列表==>", res);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取员工列表失败");
    })
    .finally(() => (loading.value = false));
}

const handleAdd = () => {
  EmployeeAddFormRef.value.show(true);
  // JDialogForm.value?.show();
};

const handleEdit = (row: UserPageVO) => {
  EmployeeEditFormRef.value.show(true, row);
};

const handleDelete = (row: UserPageVO) => {
  ElMessageBox.confirm(`将要执行删除员工"${row.name}"，是否继续?`, "删除员工", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    const { code, msg } = await EmployeeAPI.deleteById(row.id as number);
    if (code === 200) {
      ElMessage.success("删除成功");
      handleQuery();
    } else {
      ElMessage.error(msg || "删除失败");
    }
  });
};

const handleStartOrStop = async (row: UserPageVO) => {
  const { code } = await EmployeeAPI.startOrStop(
    row.id!,
    row.status! ^ StatusEnum.ENABLE
  );
  if (code.toString().startsWith("2")) {
    ElMessage({
      type: row.status! ^ StatusEnum.ENABLE ? "success" : "error",
      message:
        row.status! ^ StatusEnum.ENABLE
          ? `启用【${row.username}】账号`
          : `禁用【${row.username}】账号`,
      plain: true,
    });
    handleQuery();
    console.log("员工状态修改成功");
  }
};

defineOptions({
  name: "Employee",
});
</script>

<template>
  <div class="employee">
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>

      <el-row :gutter="20" class="mb-20px">
        <el-col :span="12" :xs="{ span: 8 }">
          <el-button type="primary" @click="handleAdd">新增员工</el-button>
        </el-col>

        <el-col :span="12" :xs="{ span: 16 }">
          <el-input
            clearable
            v-model="queryParams.name"
            placeholder="输入员工姓名"
            :suffix-icon="Search"
            @change="handleQuery"
            class="float-right max-w-330px sm:min-w-220px"
          />
        </el-col>
      </el-row>

      <BaseTable ref="JTable" v-model="tableData">
        <template #username="{ row }">
          <span>{{ row.username }}</span>
          <el-tag class="ml-5px" v-if="row.username === 'admin'" type="success"
            >管理员</el-tag
          >
        </template>

        <template #sex="{ row }">
          <svg-icon
            size="2em"
            :color="row.sex == StatusEnum.ENABLE ? '#409eff' : '#ed4d8b'"
            :icon-class="row.sex == StatusEnum.ENABLE ? 'male' : 'female'"
          />
        </template>

        <template #status="{ row }">
          <a @click="handleStartOrStop(row)" title="点击修改状态">
            <el-tag v-if="row.status === StatusEnum.ENABLE" type="success"
              >启用</el-tag
            >
            <el-tag v-else type="info">禁用</el-tag>
          </a>
        </template>

        <template #action="{ row }">
          <el-link
            type="primary"
            style="margin-right: 8px"
            :underline="false"
            @click="handleEdit(row)"
          >
            编辑
          </el-link>
          <el-popconfirm
            :title="
              row.status === StatusEnum.ENABLE ? '禁用该员工？' : '启用该员工？'
            "
            @confirm="handleStartOrStop(row)"
          >
            <template #reference>
              <el-link
                type="warning"
                style="margin-right: 8px"
                :underline="false"
              >
                {{ row.status === StatusEnum.ENABLE ? "禁用" : "启用" }}
              </el-link>
            </template>
          </el-popconfirm>

          <el-link
            type="danger"
            style="margin-right: 8px"
            :underline="false"
            @click="handleDelete(row)"
          >
            删除
          </el-link>
        </template>
      </BaseTable>
    </el-card>

    <AddForm ref="EmployeeAddFormRef" @handle-finish="handleQuery" />
    <EditForm ref="EmployeeEditFormRef" @handle-finish="handleQuery" />
    <!-- <DialogForm ref="JDialogForm" /> -->
  </div>
</template>
