<template>
  <div class="knowledge-base">
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>
      <!-- 按钮 -->
      <el-row :gutter="20" class="mb-20px">
        <el-col :span="24">
          <el-button type="primary" :icon="Plus" @click="handleAdd"
            >新增知识库</el-button
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
          <el-form-item label="文件名称:">
            <el-input
              clearable
              v-model="queryParams.fileName"
              placeholder="输入文件名称进行搜索"
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

      <!-- 数据列表 -->
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
              <div v-if="item.text === 'action'">
                <el-link
                  type="success"
                  style="margin-right: 8px"
                  :underline="false"
                  @click="handleDownload(row)"
                >
                  下载
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

              <div v-if="item.prop !== undefined">{{ row[item.prop] }}</div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <Pagination
        v-show="total > queryParams.pageSize"
        v-model:total="total"
        v-model:page="queryParams.page"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery"
      />
    </el-card>

    <AddForm @handle-finish="handleQuery" ref="AddFormRef" />
  </div>
</template>

<script setup lang="ts">
import { Delete, Search, Plus } from "@element-plus/icons-vue";
import { useRoute } from "vue-router";
import { getRowClass } from "@/utils/table";
import AddForm from "./modules/AddForm.vue";
import KnowledgeBaseAPI, {
  KnowledgeBase,
  KnowledgeBasePageQueryDTO,
} from "@/api/knowledgeBase";
import { ref, onMounted, reactive } from "vue";
type TableResultData = TableResult<typeof KnowledgeBaseAPI.pageQuery>;

const total = ref(0);
const route = useRoute();
const loading = ref(false);
const isDeleteDisable = ref(true);
const knowledgeBaseIds = ref("");
const knowledgeBaseNames = ref("");
const table = reactive<TableResultData>({
  data: [],
  columns: [
    {
      label: "文件名称",
      prop: "fileName",
    },
    {
      label: "创建时间",
      prop: "createdTime",
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
const AddFormRef = ref();

// 分页参数
const queryParams = ref<KnowledgeBasePageQueryDTO>({
  page: 1,
  pageSize: 10,
  fileName: null,
});

// 分页查询知识库
const handleQuery = async () => {
  const res = await KnowledgeBaseAPI.pageQuery(queryParams.value);
  table.data = res.data.records;
  total.value = res.data.total;
};

// 多选表格，选择项发生变化时触发
const tableSelectionChange = (selection: KnowledgeBase[]) => {
  // 删除按钮变为可选状态
  isDeleteDisable.value = selection.length <= 0;
  knowledgeBaseIds.value = selection
    .map((knowledgeBase) => knowledgeBase.id)
    .join(",");
  knowledgeBaseNames.value = selection
    .map((knowledgeBase) => knowledgeBase.fileName)
    .join(",");
};

// 批量删除知识库
const handleBatchDelete = () => {
  confirmDelete(
    `删除【${knowledgeBaseNames.value}】知识库操作，是否继续?`,
    knowledgeBaseIds.value.split(",").map((id) => Number(id)),
    `删除【${knowledgeBaseNames.value}】成功`
  );
};

// 新增知识库
const handleAdd = () => {
  AddFormRef.value.show(true);
};

const handleDelete = (row: KnowledgeBase) => {
  confirmDelete(
    `删除【${row.fileName}】知识库操作，是否继续?`,
    row.id!,
    `删除【${row.fileName}】成功`
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
    const { code, msg } = await KnowledgeBaseAPI.deleteByIds(
      (Array.isArray(ids) ? ids.join(",") : ids) as number
    );
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

// 下载知识库
const handleDownload = async (row: KnowledgeBase) => {
  ElMessageBox.confirm(`确定要下载${row.fileName}吗？`, "知识库下载", {
    confirmButtonText: "确 定",
    cancelButtonText: "取 消",
    type: "warning",
  }).then(async () => {
    const url = row.url;
    if (!url) {
      ElMessage.error("文件链接不存在");
      return;
    }

    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // 获取文件名（优先使用 row.fileName）
      const fileName = row.fileName || "download";

      // 将响应转换为 blob
      const blob = await response.blob();

      // 创建下载链接
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = fileName;
      link.target = "_blank";

      // 触发下载
      document.body.appendChild(link);
      link.click();

      // 清理
      document.body.removeChild(link);
      URL.revokeObjectURL(link.href);
    } catch (error) {
      console.error("下载失败:", error);
      ElMessage.error("下载失败，请检查网络或文件是否可用");
    }
  });
};

onMounted(() => {
  handleQuery();
});
</script>

<style scoped lang="scss"></style>
