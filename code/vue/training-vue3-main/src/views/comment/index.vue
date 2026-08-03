<script setup lang="ts">
import { Search, Delete } from "@element-plus/icons-vue";
import DishAPI, { DishPageVO } from "@/api/dish";
import SetmealAPI, { SetmealVO } from "@/api/setmeal";
import { getRowClass } from "@/utils/table";
import type { TableInstance } from "element-plus";
import CommentAPI, { CommentVo } from "@/api/comment";
import DetailTree from "@/views/comment/components/DetailTree.vue";
type QueryParams = ExtractParam<typeof CommentAPI.pageQuery>;
type TableResultData = TableResult<typeof CommentAPI.pageQuery>;
type CommentModelExpose = {
  show: (v: boolean, row?: CommentVo) => void;
};

const route = useRoute();
const total = ref(0);
//批量删除按钮状态
const isDeleteDisable = ref(true);
//加载状态
const loading = ref(false);
const DishTableRef = ref<TableInstance>();
//菜品列表
const dishList = ref<DishPageVO[]>([]);
//套餐列表
const setmealList = ref<SetmealVO[]>([]);
//查询参数
const queryParams = reactive<QueryParams>({
  dishId: undefined,
  setmealId: undefined,
  page: 1,
  pageSize: 10,
});
const table = reactive<TableResultData>({
  data: [],
  columns: [
    {
      label: "用户昵称",
      prop: "authorName",
    },
    {
      label: "购买商品",
      text: "dishName",
      minWidth: "80px",
    },
    {
      label: "关联商品",
      text: "moreDishName",
      minWidth: "190px",
    },
    {
      label: "评论内容",
      text: "content",
      minWidth: "190px",
    },
    {
      label: "点赞数",
      prop: "likes",
    },
    {
      label: "回复数",
      prop: "reply",
    },
    {
      label: "创建时间",
      prop: "createdTime",
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

const DetailTreeRef = ref<CommentModelExpose>();

const commentIds = ref<string>();

onMounted(async () => {
  await Promise.all([getDishAll(), handleQuery(), getSetmealAll()]);
});

//获取所有菜品
const getDishAll = () => {
  const params = {
    page: 1,
    pageSize: 9999,
  };
  DishAPI.getPage(params)
    .then((res) => {
      dishList.value = res.data.records;
      // console.log(dishList.value);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取菜品列表失败");
    })
    .finally(() => (loading.value = false));
};

//获取所有套餐
const getSetmealAll = () => {
  const params = {
    page: 1,
    pageSize: 9999,
  };
  SetmealAPI.getPage(params)
    .then((res) => {
      setmealList.value = res.data.records;
      // console.log("获取套餐列表==>", res.data);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取分类列表失败");
    })
    .finally(() => (loading.value = false));
};

async function handleQuery() {
  loading.value = true;
  CommentAPI.pageQuery(queryParams)
    .then((res) => {
      total.value = res.data?.total;
      table.data = res.data?.records;
      // console.log("获取评论列表==>", res.data);
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取评论列表失败");
    })
    .finally(() => (loading.value = false));
}

// 多选表格，选择项发生变化时触发
const tableSelectionChange = (selection: CommentVo[]) => {
  // 删除按钮变为可选状态
  isDeleteDisable.value = selection.length <= 0;
  commentIds.value = selection.map((comment) => comment.id).join(",");
  // console.log("当选择项发生变化时会触发该事件==>", selection);
  // console.log("批量删除的id==>", commentIds.value);
};
//批量删除接口
const handleBatchDelete = () => {
  // console.log("批量删除接口==>", selection);
  confirmDelete(
    `批量删除评论操作，是否继续?`,
    commentIds.value!,
    "批量删除成功"
  );
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
    console.log("ids==>", ids);
    const { code, msg } = await CommentAPI.deleteByIds(
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
//删除接口
const handleDelete = (row: CommentVo) => {
  confirmDelete(`删除评论操作，是否继续?`, row.id, "删除成功");
};
//查看接口
const handleDetail = (row: CommentVo) => {
  DetailTreeRef.value?.show(true, row);
};
</script>
<template>
  <div class="comment">
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>

      <el-row :gutter="20" class="mb-20px">
        <el-col :span="24">
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
            <el-select
              placeholder="选择搜索的菜品名称"
              clearable
              v-model="queryParams.dishId"
              @clear="handleQuery"
              @keyup.enter="handleQuery"
              class="sm:w-330px sm:min-w-200px"
              style="width: 220px"
            >
              <el-option
                v-for="item in dishList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="套餐名称:">
            <el-select
              placeholder="选择搜索的套餐名称"
              clearable
              v-model="queryParams.setmealId"
              @clear="handleQuery"
              @keyup.enter="handleQuery"
              class="sm:w-330px sm:min-w-200px"
              style="width: 220px"
            >
              <el-option
                v-for="item in setmealList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
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
              <!-- 商品名称 -->
              <div v-if="item.text === 'dishName'">
                <!-- 如果 菜品名称不为空则显示菜品名称 否则显示套餐名称 -->
                <span v-if="row.dishName">
                  {{ row.dishName }}
                </span>
                <span v-else>
                  {{ row.setmealName }}
                </span>
              </div>
              <!-- 评论内容过长... -->
              <div v-if="item.text === 'content'">
                <el-tooltip
                  effect="dark"
                  :content="row.content"
                  placement="top"
                  :disabled="!row.content || row.content.length <= 9"
                >
                  <span>{{
                    row.content && row.content.length > 12
                      ? row.content.substring(0, 12) + "..."
                      : row.content
                  }}</span>
                </el-tooltip>
              </div>
              <!-- 关联名称 -->
              <div v-if="item.text === 'moreDishName'">
                <el-tooltip
                  effect="dark"
                  :content="row.moreDishName"
                  placement="top"
                  :disabled="!row.moreDishName || row.content.moreDishName <= 9"
                >
                  <span>{{
                    row.moreDishName && row.moreDishName.length > 12
                      ? row.moreDishName.substring(0, 12) + "..."
                      : row.moreDishName
                  }}</span>
                </el-tooltip>
              </div>
              <!-- 渲染普通内容 -->
              <div v-if="item.prop !== undefined">{{ row[item.prop] }}</div>
              <!-- 渲染按钮 -->
              <div v-if="item.text === 'action'">
                <el-link
                  type="primary"
                  style="margin-right: 8px"
                  :underline="false"
                  @click="handleDetail(row)"
                >
                  查看
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
    <DetailTree @commentQuery="handleQuery" ref="DetailTreeRef" />
  </div>
</template>

<style lang="scss" scoped></style>
