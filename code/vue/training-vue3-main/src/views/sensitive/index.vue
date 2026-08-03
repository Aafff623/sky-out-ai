<script setup lang="ts">
import { Search, Delete, Plus } from "@element-plus/icons-vue";
import { ref, onMounted, reactive } from "vue";
import { getRowClass } from "@/utils/table";
type QueryParams = ExtractParam<typeof SensitiveAPI.page>;
type TableResultData = TableResult<typeof SensitiveAPI.page>;
import AddForm from "./module/AddForm.vue";
import EditForm from "./module/EditForm.vue";
import SensitiveAPI, { Sensitive } from "@/api/sensitive";
const route = useRoute();
//绑定添加弹窗
const SensitiveAddFormRef = ref();
const SensitiveEditFormRef = ref();
//批量删除按钮状态
const isDeleteDisable = ref(true);
//加载状态
const loading = ref(false);
const total = ref(0);
const table = reactive<TableResultData>({
  data: [],
  columns: [
    {
      label: "敏感词",
      prop: "sensitives",
      minWidth: "160px",
    },
    // {
    //   label: "创建时间",
    //   prop: "createdTime",
    //   minWidth: "160px",
    // },
    {
      label: "更新时间",
      prop: "updatedTime",
      minWidth: "160px",
    },
    {
      label: "操作",
      text: "action",
      minWidth: "90px",
      fixed: "right",
    },
  ],
});
const sensitiveIds = ref<number[]>();
//查询参数
const queryParams = reactive<QueryParams>({
  page: 1,
  pageSize: 10,
  sensitive: undefined,
});

onMounted(async () => {
  await Promise.all([handleQuery()]);
});

//获取敏感词
async function handleQuery() {
  loading.value = true;
  SensitiveAPI.page(queryParams)
    .then((res) => {
      total.value = res.data?.total;
      table.data = res.data?.records;
      console.log("获取敏感词列表列表==>", res.data);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取敏感词列表失败");
    })
    .finally(() => (loading.value = false));
}
// 多选表格，选择项发生变化时触发
const tableSelectionChange = (selection: Sensitive[]) => {
  // 删除按钮变为可选状态
  isDeleteDisable.value = selection.length <= 0;
  sensitiveIds.value = selection.map((sensitive) => sensitive.id).join(",");
  // console.log("当选择项发生变化时会触发该事件==>", selection);
  console.log("批量删除的id==>", sensitiveIds.value);
};
//删除接口
const handleDelete = (row: Sensitive) => {
  confirmDelete(
    `删除敏感词【${row.sensitives}】操作，是否继续?`,
    row.id,
    "删除成功"
  );
};
//批量删除接口
const handleBatchDelete = () => {
  // console.log("批量删除接口==>", selection);
  confirmDelete(
    `批量删除敏感词操作，是否继续?`,
    sensitiveIds.value,
    "批量删除成功"
  );
};
const confirmDelete = async (
  message: string,
  ids: number | number[],
  successMessage: string
) => {
  try {
    await ElMessageBox.confirm(message, "删除确认", {
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      type: "warning",
    });
    console.log("ids==>", ids);
    const { code, msg } = await SensitiveAPI.delete(ids as number[]);
    if (code === 200) {
      ElMessage.success(successMessage);
      handleQuery();
    } else {
      ElMessage.error(msg || "删除失败");
    }
  } catch (error) {
    console.error("删除操作已取消或出错", error);
  }
};

//编辑
const handleEdit = (row: Sensitive) => {
  console.log("row==>", row);
  SensitiveEditFormRef.value.show(true, row);
};
const handleAdd = () => {
  SensitiveAddFormRef.value.show(true);
};
</script>
<template>
  <div>
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>

      <el-row :gutter="20" class="mb-20px">
        <el-col :span="24">
          <el-button type="primary" :icon="Plus" @click="handleAdd"
            >新增敏感词</el-button
          >
          <el-button
            :disabled="isDeleteDisable"
            type="danger"
            :icon="Delete"
            @click="handleBatchDelete()"
            >批量删除(多个)</el-button
          >
        </el-col>
      </el-row>

      <el-row class="mb-20px">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="敏感词名称:">
            <el-input
              placeholder="请输入搜索的敏感词名称"
              clearable
              v-model="queryParams.sensitive"
              @clear="handleQuery"
              @keyup.enter="handleQuery"
              class="sm:w-330px sm:min-w-200px"
              style="width: 220px"
            />
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
              <!-- 渲染普通内容 -->
              <div v-if="item.prop !== undefined">{{ row[item.prop] }}</div>
              <!-- 渲染按钮 -->
              <div v-if="item.text === 'action'">
                <el-link
                  type="primary"
                  style="margin-right: 8px"
                  :underline="false"
                  @click="handleEdit(row)"
                >
                  编辑
                </el-link>
                <el-link
                  type="danger"
                  style="margin-right: 8px"
                  :underline="false"
                  @click="handleDelete(row)"
                >
                  删除
                </el-link>
              </div>
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
    <AddForm ref="SensitiveAddFormRef" @handle-finish="handleQuery" />
    <EditForm ref="SensitiveEditFormRef" @handle-finish="handleQuery" />
  </div>
</template>

<style lang="scss" scoped></style>
