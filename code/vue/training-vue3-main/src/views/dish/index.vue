<script lang="ts" setup>
import type { TableInstance } from "element-plus";
import { Search, Delete, Plus } from "@element-plus/icons-vue";
import { getRowClass } from "@/utils/table";
import { StatusEnum } from "@/enums/StatusEnum";
import DishAPI, { type DishPageVO } from "@/api/dish";
import CategoryAPI, { type CategoryPageVO } from "@/api/category";
import { AddForm, EditForm } from "./modules";

type QueryParams = ExtractParam<typeof DishAPI.getPage>;
type TableResultData = TableResult<typeof DishAPI.getPage>;
const route = useRoute();
const loading = ref(false);
const total = ref(0);
const isDeleteDisable = ref(true);
const dishIds = ref<string>();
const DishTableRef = ref<TableInstance>();
const DishAddFormRef = ref();
const DishEditFormRef = ref();
const queryParams = reactive<QueryParams>({
  name: "",
  categoryId: undefined,
  status: undefined,
  page: 1,
  pageSize: 6,
});
const dishOptions = ref<Array<CategoryPageVO>>();
const statusOptions = ref([
  {
    name: "起售状态",
    value: 1,
  },
  {
    name: "停售状态",
    value: 0,
  },
]);
const table = reactive<TableResultData>({
  data: [],
  columns: [
    {
      label: "菜品名称",
      prop: "name",
    },
    {
      label: "菜品封面",
      text: "image",
    },
    {
      label: "菜品分类",
      prop: "categoryName",
    },
    {
      label: "价格",
      prop: "price",
    },
    {
      label: "售卖状态",
      text: "status",
    },
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

onMounted(async () => {
  await initData();
  await Promise.all([handleQuery(), getCategorys()]);

  // 打开新增菜品弹窗
  if (route.query?.type === "create") {
    setTimeout(() => {
      DishAddFormRef.value.show(true);
    }, 300);
  }
});

function initData() {
  return new Promise((resolve) => {
    if (Object.keys(route.query).length > 0) {
      for (const key in route.query) {
        if (key === "status") {
          const status = Number(route.query[key]) as StatusEnum;
          queryParams.status = status;
        } else {
          (queryParams as any)[key] = route.query[key];
        }
      }
    }

    resolve(null);
  });
}

async function handleQuery() {
  loading.value = true;
  DishAPI.getPage(queryParams)
    .then((res) => {
      total.value = res.data?.total;
      table.data = res.data?.records;
      console.log("获取菜品列表==>", res.data);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取菜品列表失败");
    })
    .finally(() => (loading.value = false));
}

async function getCategorys() {
  CategoryAPI.getPage({
    status: StatusEnum.ENABLE,
    page: 1,
    pageSize: 999999999,
  })
    .then((res) => {
      dishOptions.value = res.data?.records || [];
      console.log("菜品菜品列表==>", res);
    })
    .catch((err) => {
      console.log("获取菜品菜品列表失败==>", err);
    });
}

const handleAdd = () => {
  DishAddFormRef.value.show(true);
};

const handleEdit = (row: DishPageVO) => {
  DishEditFormRef.value.show(true, row);
};

const handleDelete = (row: DishPageVO) => {
  confirmDelete(
    `删除【${row.name}】菜品操作，是否继续?`,
    row.id!.toString(),
    `删除【${row.name}】成功`
  );
};

const handleBatchDelete = () => {
  confirmDelete(`批量删除菜品操作，是否继续?`, dishIds.value!, "批量删除成功");
};

const confirmDelete = async (
  message: string,
  ids: string | string[],
  successMessage: string
) => {
  try {
    await ElMessageBox.confirm(message, "删除确认", {
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      type: "warning",
    });

    const { code, msg } = await DishAPI.batchDelete(
      Array.isArray(ids) ? ids.join(",") : ids
    );
    if (code === 200) {
      ElMessage.success(successMessage);
      handleQuery();
      DishTableRef.value?.clearSelection?.();
    } else {
      ElMessage.error(msg || "删除失败");
    }
  } catch (error) {
    console.error("删除操作已取消或出错", error);
  }
};

// 多选表格，选择项发生变化时触发
const tableSelectionChange = (selection: DishPageVO[]) => {
  // 删除按钮变为可选状态
  isDeleteDisable.value = selection.length <= 0;
  dishIds.value = selection.map((dish) => dish.id).join(",");
  console.log("当选择项发生变化时会触发该事件==>", selection);
};

const handleStartOrStop = async (row: CategoryPageVO) => {
  const { code } = await DishAPI.startOrStop(
    row.id!,
    row.status! ^ StatusEnum.ENABLE
  );
  if (code.toString().startsWith("2")) {
    ElMessage({
      type: row.status! ^ StatusEnum.ENABLE ? "success" : "error",
      message:
        row.status! ^ StatusEnum.ENABLE
          ? `起售【${row.name}】菜品`
          : `停售【${row.name}】菜品`,
      plain: true,
    });
    handleQuery();
  }
};

defineOptions({
  name: "Dish",
});
</script>

<template>
  <div class="dish">
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>

      <el-row :gutter="20" class="mb-20px">
        <el-col :span="24">
          <el-button type="primary" :icon="Plus" @click="handleAdd"
            >新增菜品</el-button
          >
          <el-button
            :disabled="isDeleteDisable"
            type="danger"
            :icon="Delete"
            @click="handleBatchDelete"
            >批量删除(多个)</el-button
          >
        </el-col>
      </el-row>

      <el-row class="mb-20px">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="菜品名称:">
            <el-input
              clearable
              v-model="queryParams.name"
              placeholder="输入菜品名称进行搜索"
              @clear="handleQuery"
              @keyup.enter="handleQuery"
              class="sm:w-330px sm:min-w-200px"
              style="width: 220px"
            />
          </el-form-item>

          <el-form-item label="菜品分类:">
            <el-select
              v-model="queryParams.categoryId"
              clearable
              placeholder="选择搜索的菜品分类"
              class="max-w-330px sm:min-w-200px"
              style="width: 220px"
            >
              <el-option
                v-for="(dish, dishIndex) in dishOptions"
                :key="dishIndex"
                :label="dish.name"
                :value="dish.id!"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="启用状态:">
            <el-select
              v-model="queryParams.status"
              clearable
              placeholder="售卖状态"
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
              >查询</el-button
            >
          </el-form-item>
        </el-form>
      </el-row>

      <div v-stagger class="wh-full">
        <el-table
          stripe
          class="table-v1"
          ref="DishTableRef"
          v-loading="loading"
          element-loading-text="🍟数据加载中..."
          row-key="id"
          :data="table.data"
          style="width: 100%"
          empty-text="数据都跑空啦~"
          highlight-current-row
          :header-cell-style="getRowClass"
          @selection-change="tableSelectionChange"
        >
          <el-table-column
            reserve-selection
            type="selection"
            align="center"
            width="30"
          />
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
              <div v-if="item.text === 'image'">
                <a
                  :href="row.image"
                  target="_blank"
                  rel="noopener noreferrer"
                  title="查看菜品"
                >
                  <el-image
                    style="border: 1px solid #eee"
                    :src="row.image"
                    fit="cover"
                    class="w-40px h-40px rounded-2px"
                  />
                </a>
              </div>

              <div v-if="item.text === 'status'">
                <a title="点击修改状态" @click="handleStartOrStop(row)">
                  <el-tag v-if="row.status === StatusEnum.ENABLE" type="success"
                    >起售</el-tag
                  >
                  <el-tag v-else type="info">停售</el-tag>
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
                      ? '停售该菜品？'
                      : '起售该菜品？'
                  "
                  @confirm="handleStartOrStop(row)"
                >
                  <template #reference>
                    <el-link
                      type="warning"
                      style="margin-right: 8px"
                      :underline="false"
                    >
                      {{ row.status == StatusEnum.ENABLE ? "停售" : "起售" }}
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

    <AddForm
      ref="DishAddFormRef"
      :dishOptions="dishOptions"
      @handle-finish="handleQuery"
    />
    <EditForm
      ref="DishEditFormRef"
      :dishOptions="dishOptions"
      @handle-finish="handleQuery"
    />
  </div>
</template>
