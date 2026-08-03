<script lang="ts" setup>
import { Search } from "@element-plus/icons-vue";
import { getRowClass } from "@/utils/table";
import { StatusEnum } from "@/enums/StatusEnum";
import CategoryAPI, {
  type CategoryPageQuery,
  CategoryPageVO,
} from "@/api/category";
import { AddForm, EditForm } from "./modules";

const route = useRoute();
const loading = ref(false);
const total = ref(0);
const CategoryAddFormRef = ref();
const CategoryEditFormRef = ref();
const queryParams = reactive<CategoryPageQuery>({
  name: "",
  type: undefined,
  status: undefined,
  page: 1,
  pageSize: 6,
});
const categoryOptions = ref([
  {
    name: "菜品分类",
    value: 1,
  },
  {
    name: "套餐分类",
    value: 2,
  },
]);
const statusOptions = ref([
  {
    name: "启用",
    value: 1,
  },
  {
    name: "禁用",
    value: 0,
  },
]);
const table = reactive<TableData<CategoryPageVO[]>>({
  data: [],
  columns: [
    //prop则是遍历数据源
    //text 则是自定义列 一般用于判断列是否显示
    {
      label: "分类名称",
      prop: "name",
    },
    {
      label: "分类类型",
      text: "type",
    },
    {
      label: "状态",
      text: "status",
    },
    // {
    //   label: "排序",
    //   prop: "sort",
    // },
    {
      label: "更新时间",
      prop: "updateTime",
      minWidth: "160px",
    },
    {
      label: "操作",
      text: "action",
      minWidth: "140px",
      fixed: "right",
    },
  ],
});

onMounted(() => {
  handleQuery();
});

function handleQuery() {
  loading.value = true;
  CategoryAPI.getPage(queryParams)
    .then((res) => {
      total.value = res.data?.total;
      table.data = res.data?.records;
      // console.log("获取分类列表==>", res.data);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取分类列表失败");
    })
    .finally(() => (loading.value = false));
}

const handleAdd = () => {
  CategoryAddFormRef.value.show(true);
};

const handleEdit = (row: CategoryPageVO) => {
  CategoryEditFormRef.value.show(true, row);
};

const handleStartOrStop = async (row: CategoryPageVO) => {
  const { code } = await CategoryAPI.startOrStop(
    row.id!,
    row.status! ^ StatusEnum.ENABLE
  );
  if (code.toString().startsWith("2")) {
    ElMessage({
      type: row.status! ^ StatusEnum.ENABLE ? "success" : "error",
      message:
        row.status! ^ StatusEnum.ENABLE
          ? `启用【${row.name}】分类`
          : `禁用【${row.name}】分类`,
      plain: true,
    });
    handleQuery();
  }
};

const handleDelete = (row: CategoryPageVO) => {
  ElMessageBox.confirm(`删除【${row.name}】分类操作，是否继续?`, "删除分类", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    const { code, msg } = await CategoryAPI.deleteById(row.id as number);
    if (code === 200) {
      ElMessage.success("删除成功");
      handleQuery();
    } else {
      ElMessage.error(msg || "删除失败");
    }
  });
};

defineOptions({
  name: "Category",
});
</script>

<template>
  <div class="category">
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>

      <!-- <el-row class="mb-20px">
        <el-alert
          show-icon
          title="注意：新增的分类时，默认是禁用状态，避免一添加就在小程序端出现，使得顾客浏览时出现`【空分类】`列表。"
          type="warning"
        />
      </el-row> -->

      <el-row :gutter="20" class="mb-20px">
        <el-col :span="24" :xs="{ span: 8 }">
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </el-col>
      </el-row>

      <el-row class="mb-20px">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="分类名称:">
            <el-input
              clearable
              v-model="queryParams.name"
              placeholder="输入分类名称进行搜索"
              @keyup.enter="handleQuery"
              class="sm:w-330px sm:min-w-200px"
              style="width: 220px"
            />
          </el-form-item>

          <el-form-item label="分类类型:">
            <el-select
              v-model="queryParams.type"
              clearable
              placeholder="选择搜索的分类类型"
              class="max-w-330px sm:min-w-200px"
              style="width: 220px"
            >
              <el-option
                v-for="(option, optionIndex) in categoryOptions"
                :key="optionIndex"
                :label="option.name"
                :value="option.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="启用状态:">
            <el-select
              v-model="queryParams.status"
              clearable
              placeholder="分类的启用状态"
              class="max-w-330px sm:min-w-200px"
              style="width: 220px"
            >
              <el-option
                v-for="(option, optionIndex) in statusOptions"
                :key="optionIndex"
                :label="option.name"
                :value="option.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleQuery"
              >搜索</el-button
            >
          </el-form-item>
        </el-form>
      </el-row>

      <div v-stagger class="wh-full">
        <el-table
          stripe
          v-stagger
          class="table-v1"
          v-loading="loading"
          element-loading-text="🍟数据加载中..."
          row-key="id"
          :data="table.data"
          style="width: 100%"
          empty-text="数据都跑空啦~"
          highlight-current-row
          :header-cell-style="getRowClass"
        >
          <el-table-column
            type="index"
            align="center"
            label="序号"
            width="60"
          />
          <el-table-column
            v-for="(item, index) in table.columns"
            align="center"
            :key="index"
            :label="item.label"
            :prop="item.prop"
            :width="item.width"
            :min-width="item.minWidth"
            :fixed="item.fixed"
          >
            <template #default="{ row }">
              <div v-if="item.text === 'type'">
                <el-tag :type="row.type === 1 ? 'primary' : 'warning'">
                  {{ row.type === 1 ? "菜品分类" : "套餐分类" }}
                </el-tag>
              </div>

              <div v-if="item.text === 'status'">
                <a @click="handleStartOrStop(row)" title="点击修改状态">
                  <el-tag v-if="row.status === StatusEnum.ENABLE" type="success"
                    >启用</el-tag
                  >
                  <el-tag v-else type="info">禁用</el-tag>
                </a>
              </div>

              <div v-if="item.text === 'action'">
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
                    row.status == StatusEnum.ENABLE
                      ? '禁用该分类？'
                      : '启用该分类？'
                  "
                  @confirm="handleStartOrStop(row)"
                >
                  <template #reference>
                    <el-link
                      type="warning"
                      style="margin-right: 8px"
                      :underline="false"
                    >
                      {{ row.status == StatusEnum.ENABLE ? "禁用" : "启用" }}
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
              </div>

              <div v-if="item.prop !== undefined">{{ row[item.prop] }}</div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <Pagination
        v-show="total > queryParams.pageSize"
        v-model:total="total"
        v-model:page="queryParams.page"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery"
      />
    </el-card>

    <AddForm ref="CategoryAddFormRef" @handle-finish="handleQuery" />
    <EditForm ref="CategoryEditFormRef" @handle-finish="handleQuery" />
  </div>
</template>
