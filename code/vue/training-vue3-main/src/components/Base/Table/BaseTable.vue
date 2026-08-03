<script lang="ts" setup>
import { isEmpty } from "lodash-es";
import request from "@/utils/request";
import { getRowClass } from "@/utils/table";

type TableDataType = TableData["data"];
type TableIItemType = TableData["data"][number];

/** Vue3.5+ 以上版本支持直接响应式解构props */
const props = defineProps({
  maxHeight: { type: [String, Number], default: undefined }, // 表格最大高度
  border: { type: Boolean, default: true }, // 是否显示边框
  stripe: { type: Boolean, default: true }, // 是否显示斑马纹
  isExpandSingle: { type: Boolean, default: false }, // 是否展开单行
  emptyText: { type: String, default: "🐠数据跑空啦~" }, // 空数据时显示的文字
  customRowKey: { type: String, default: () => "id" }, // 自定义rowKey
  highlightCurRow: { type: Boolean, default: true }, // 是否高亮当前行
  staggerOptions: {
    type: Object as PropType<StaggerOptions>,
    default: () => ({}),
  },
  usePagination: {
    type: [Boolean, Function] as PropType<
      boolean | ((data: TableDataType) => boolean)
    >,
    default: true,
  }, // 是否使用分页
  // TODO: 加上类型限制外部使用报错（as PropType<TableData>），目前解决方式既保证类型提示又不报错，双重断言
  modelValue: {
    required: true,
    type: Object as PropType<TableData>,
    default: () => {
      return {
        data: [],
        columns: [],
        pagination: {},
      };
    },
  },
  selectionList: {
    type: Array as PropType<TableDataType>,
    default: () => [],
  },
});
const url = ref("");
const page = ref(1);
const size = ref(10);
const total = ref(0);
const loading = ref(false);
const expands = ref<TableDataType>([]);
const params = ref<FormModelData>({});

const filterColumns = computed(
  () => props.modelValue.columns.filter((col) => col.visible !== false) || []
);
const showPagination = computed(() => {
  if (typeof props.usePagination === "function") {
    return props.usePagination(props.modelValue?.data || []) || false;
  }

  return props.usePagination;
});
const tablePagination = computed<PaginationProps>(() => {
  if (typeof tableData.value?.pagination === "function") {
    return tableData.value?.pagination(props.modelValue.data) || {};
  }

  return tableData.value?.pagination || {};
});

const emits = defineEmits<{
  (e: "init"): void;
  (e: "update:modelValue", data: TableData): void;
  (e: "update:selectionList", data: TableDataType): void;
  (e: "pagination", data: PageQuery): void;
  (e: "get-data", data: TableDataType): void;
  (e: "row-click", data: TableIItemType): void;
  (e: "action-click", data: { row: any; option: ActionConfig }): void;
  (e: "selection-change", data: TableDataType): void;
  (e: "update-request", data: Record<string, any> & PageQuery): void;
  (e: "expand-change", row: TableIItemType, expandedRows: TableDataType): void;
}>();

const selectionList = useVModel(props, "selectionList", emits);
const tableData = useVModel(props, "modelValue", emits);
onMounted(() => emits("init"));

function setConfig<T extends FormModelData = FormModelData>(
  payload?: Partial<PageQuery> & {
    url?: RequestOptions["url"];
    data?: TableData<T[]>["data"] | null;
  }
) {
  if (payload?.data?.length) {
    tableData.value.data = payload.data;
  }

  url.value = payload?.url || "";
  total.value = payload?.total || 0;
  page.value = payload?.page || 1;
  size.value = payload?.pageSize || 10;
}

function getRowKeys(row: TableIItemType) {
  return row[props.customRowKey as keyof TableIItemType];
}

// 点击table行触发
function rowClick(data: TableIItemType) {
  emits("row-click", data);
}

// 勾选数据
function selectionChange(data: TableDataType) {
  selectionList.value = data;
  emits("selection-change", data);
}

// 分页数据变化
function onPaginationChange(data: { page: number; limit: number }) {
  const { page, limit } = data;
  emits("pagination", { page, pageSize: limit });
  updateData();
}

// 展开某一行
function expandChange(row: TableIItemType, expandedRows: TableDataType) {
  // 判断是否只展开一行
  if (props.isExpandSingle) {
    if (expandedRows.length) {
      expands.value = [];
      if (row) {
        expands.value.push(row?.id);
      }
    } else {
      expands.value = [];
    }
  }
  emits("expand-change", row, expandedRows);
}

/**
 * 通过dataUrl请求数据
 * @param data.queryParams 查询参数
 * @param data.requestConfig 请求配置
 * @param data.pageQueryParam 分页参数
 */
async function updateData<T extends FormModelData = FormModelData>(
  data?: TableRequestConfig<Partial<T>>
) {
  const submitParams = {} as T;
  const { queryParams, requestConfig, pageQueryParam } = data || {};
  // console.log("updateData", data);

  if (requestConfig?.url) {
    url.value = requestConfig.url;
  }

  if (!url.value) {
    emits("update-request", {
      ...params.value,
      page: page.value,
      pageSize: size.value,
    });
    return void 0;
  }

  loading.value = true;
  if (pageQueryParam?.page) {
    page.value = pageQueryParam?.page;
    size.value = pageQueryParam?.pageSize || 10;
    params.value = {};
  }

  if (queryParams || !isEmpty(params.value)) {
    for (const key in queryParams) {
      params.value[key] = queryParams[key];
    }

    // 过滤空数据
    for (const key in params.value as T) {
      const value = params.value[key];
      value !== "" &&
        value !== null &&
        value !== undefined &&
        (submitParams[key] = value);
    }
  }

  const res = await request<any, ResponseResult<T[]>>({
    url: url.value,
    method: requestConfig?.method || "get",
    params: {
      ...submitParams,
      page: page.value,
      pageSize: size.value,
    },
    ...requestConfig,
  });

  if (res.code === 200) {
    const { records, total: totalNum } = res.data;
    tableData.value.data = records || [];
    total.value = totalNum;
    emits("get-data", records);
  }

  loading.value = false;
}

function getImageSize(col: TableColumnData) {
  const size = getImageConfig(col)?.size;
  return typeof size === "number" ? `${size}px` : size;
}

function getImageConfig(col: TableColumnData) {
  return Object.assign(
    {},
    {
      fit: "cover",
      zoomRate: 1.2,
      maxScale: 7,
      minScale: 0.2,
      showProgress: true,
      initialIndex: 0,
      style: {
        width: "100%",
        height: "100%",
        borderRadius: "4px",
        border: "2px solid rgb(212 212 212)",
      },
    },
    col.imageConfig
  ) as TableColumnData["imageConfig"];
}

// 获取标签配置
function getTagConfig(col: TableColumnData, value: any) {
  const defaultValue = String(value);

  const defaultTag = {
    [defaultValue]: {
      type: "info" as const,
      text: String(value),
    },
  } as TagConfig["options"];
  const mergeOptions = Object.assign(
    {},
    defaultTag,
    col.tagConfig?.options || {}
  );

  return mergeOptions[value] || defaultTag?.[defaultValue];
}

// 获取文本映射
function getTextValue(col: TableColumnData, value: any) {
  const strValue = String(value);

  // 优先匹配原始类型
  if (col.textConfig?.[value] !== undefined) {
    return col.textConfig[value];
  }

  // 其次匹配字符串类型
  if (col.textConfig?.[strValue] !== undefined) {
    return col.textConfig[strValue];
  }

  return value;
}

// 特殊文本处理
function getFormattedText(col: TableColumnData, value: any) {
  if (col.type) {
    console.log(col, value);
    return;
  }

  const text = getTextValue(col, value);
  const symbolConfig = col.symbol || {};
  const prefix = symbolConfig?.prefix || "";
  const suffix = symbolConfig?.suffix || "";

  // 空值不添加符号
  if (text === null || text === undefined || text === "") return "--";

  return `${prefix}${text}${suffix}`.trim();
}

// TODO: 待完善
function defaultHandler<T = TableIItemType>(
  row: T,
  index: number,
  action: ActionConfig
) {
  if (action.type) {
    if (!action.requestUrl) {
      ElMessage.error("未配置请求地址");
      return;
    }

    const map = {
      delete: defaultDeleteHandler,
      startStop: defaultEnableHandler,
    } as Record<
      NonNullable<ActionConfig["type"]>,
      (row: any, action: ActionConfig) => Promise<void>
    >;

    const handler = map[action.type];
    handler && handler(row, action);
    return;
  }

  // 单纯的按钮点击事件
  if (action.onClick) {
    return action.onClick(row, index, action);
  }

  // 确认popupConfirm框点击确认事件
  if (action.onConfirm) {
    return action.onConfirm(row, index, action);
  }
}

// 默认启用/停售处理
const defaultEnableHandler = async (row: any, action: ActionConfig) => {
  const { keyName = "id", valueName = "id" } = action.requestKeyValConfig || {};

  try {
    await request<any, ResponseData>({
      url:
        typeof action.requestUrl === "function"
          ? action.requestUrl(row)
          : action.requestUrl,
      method: action.requestMethod || "put",
      params: { [keyName as string]: row[valueName as string] },
    });
    ElMessage.success("操作成功");
    updateData();
  } catch (error) {
    console.error("操作失败==>", error);
  }
};

// 默认删除处理
const defaultDeleteHandler = async (row: any, action: ActionConfig) => {
  const { keyName = "ids", valueName = "id" } =
    action.requestKeyValConfig || {};

  try {
    await request({
      url:
        typeof action.requestUrl === "function"
          ? action.requestUrl(row)
          : action.requestUrl,
      method: action.requestMethod || "delete",
      params: { [keyName as string]: row[valueName as string] },
    });
    ElMessage.success("删除成功");
    updateData();
  } catch (error) {
    console.error("删除失败==>", error);
  }
};

// 处理操作点击
const handleActionClick = async (
  row: any,
  index: number,
  action: ActionConfig
) => {
  defaultHandler(row, index, action);
};

// 处理确认操作
const handleConfirmAction = async <T = TableIItemType,>(
  row: T,
  index: number,
  action: ActionConfig
) => {
  defaultHandler(row, index, action);
};

const getActionText = (value: ActionConfig["text"], row: any) => {
  return typeof value === "function" ? value(row) : value;
};

defineExpose({
  setConfig,
  updateData,
});

defineOptions({
  name: "BaseTable",
});
</script>

<template>
  <div
    v-stagger="staggerOptions"
    ref="JBaseTable"
    class="base-table"
    v-loading="loading"
    element-loading-text="数据加载中..."
  >
    <div class="table-area" ref="JTableArea">
      <el-table
        ref="JTable"
        v-bind="$attrs"
        :border="border"
        :stripe="stripe"
        :max-height="maxHeight"
        :row-key="getRowKeys"
        :empty-text="emptyText"
        :expand-row-keys="expands"
        :data="tableData?.data || []"
        :header-cell-style="getRowClass"
        :highlight-current-row="highlightCurRow"
        @row-click="rowClick"
        @expand-change="expandChange"
        @selection-change="selectionChange"
      >
        <template v-for="col in filterColumns">
          <el-table-column
            v-if="!col.slot"
            :key="col.prop"
            :align="col.align || 'center'"
            :resizable="col.resizable || true"
            v-bind="col"
          >
            <template #default="{ row, column, $index }" v-if="!col.type">
              <!-- 兼容自定义格式化内容 -->
              <span>
                {{
                  col.formatter
                    ? col.formatter(row, column, row[col.prop!], $index)
                    : getFormattedText(col, row[col.prop!])
                }}
              </span>

              <template
                v-if="col.childColumns && Array.isArray(col.childColumns)"
              >
                <el-table-column
                  v-for="child in col?.childColumns"
                  :key="`${child.prop}-childColumns`"
                  :align="child.align || 'center'"
                  :resizable="child.resizable || true"
                  v-bind="child"
                />
              </template>
            </template>
          </el-table-column>

          <!-- 表格图片 -->
          <el-table-column
            v-else-if="col.slot === 'slot-image'"
            v-bind="col"
            :label="col.label"
            :resizable="col.resizable || true"
            :align="col.align || 'center'"
            :key="`${col.prop}-${col.slot}-image`"
            :type="col.isExpand ? 'expand' : ''"
          >
            <template #default="{ row }">
              <div
                v-if="Array.isArray(row[col.prop!])"
                class="multi-image-container"
                :style="{ display: 'flex', gap: '8px', flexWrap: 'wrap' }"
              >
                <a
                  v-for="(img, index) in row[col.prop!]"
                  :key="index"
                  :href="img"
                  target="_blank"
                  rel="noopener noreferrer"
                  title="查看图片"
                  :style="{
                    display: 'inline-flex',
                    width: getImageSize(col),
                    height: getImageSize(col),
                  }"
                >
                  <el-image :src="img" v-bind="getImageConfig(col)" />
                </a>
              </div>

              <a
                v-else-if="row[col.prop!]"
                :href="row[col.prop!]"
                target="_blank"
                rel="noopener noreferrer"
                title="查看图片"
                :style="{
                  display: 'inline-flex',
                  width: getImageSize(col),
                  height: getImageSize(col),
                }"
              >
                <el-image :src="row[col.prop!]" v-bind="getImageConfig(col)" />
              </a>

              <span v-else>--</span>
            </template>
          </el-table-column>

          <!-- 表格tag -->
          <el-table-column
            v-else-if="col.slot === 'slot-tag'"
            v-bind="col"
            :label="col.label"
            :resizable="col.resizable || true"
            :align="col.align || 'center'"
            :key="`${col.prop}-${col.slot}-tag`"
          >
            <template #default="{ row }">
              <el-tag
                v-if="row[col.prop!] !== undefined"
                v-bind="getTagConfig(col, row[col.prop!])"
              >
                <i
                  v-if="getTagConfig(col, row[col.prop!])?.icon"
                  :class="getTagConfig(col, row[col.prop!])?.icon"
                ></i>
                {{ getTagConfig(col, row[col.prop!])!.text }}
              </el-tag>

              <span v-else>--</span>
            </template>
          </el-table-column>

          <!-- 表格文本 -->
          <el-table-column
            v-else-if="col.slot === 'slot-text'"
            v-bind="col"
            :label="col.label"
            :resizable="col.resizable || true"
            :align="col.align || 'center'"
            :key="`${col.prop}-${col.slot}-text`"
          >
            <template #default="{ row }">
              <span v-if="row[col.prop!] !== undefined">
                {{ getFormattedText(col, row[col.prop!]) }}
              </span>
              <span v-else>--</span>
            </template>
          </el-table-column>

          <!-- 表格操作 -->
          <el-table-column
            v-else-if="col.slot === 'slot-action'"
            v-bind="col"
            :label="col.label"
            :resizable="col.resizable || true"
            :align="col.align || 'center'"
            :key="`${col.prop}-${col.slot}-action`"
          >
            <template
              #default="{ row, $index }"
              v-if="col.actionConfig?.options?.length"
            >
              <template
                v-for="(action, actionIndex) in col.actionConfig?.options"
              >
                <!-- 带确认的操作 -->
                <el-popconfirm
                  v-if="action.popupConfirmConfig"
                  v-bind="action.popupConfirmConfig"
                  :key="actionIndex"
                  :title="getActionText(action.popupConfirmConfig.title, row)"
                  @confirm="handleConfirmAction(row, $index, action)"
                >
                  <template #reference>
                    <BaseActionButton :action="action" :row="row" />
                  </template>
                </el-popconfirm>

                <!-- 普通操作 -->
                <BaseActionButton
                  v-else
                  :row="row"
                  :action="action"
                  @click="handleActionClick(row, $index, action)"
                />
              </template>
            </template>
          </el-table-column>

          <!-- 表格插槽 -->
          <el-table-column
            v-else
            :key="col.slot"
            :label="col.label"
            :resizable="col.resizable || true"
            v-bind="{
              ...col,
              width: col.width,
              minWidth: col.minWidth,
              fixed: col.fixed,
              align: col.align || 'center',
            }"
            :type="col.isExpand ? 'expand' : ''"
          >
            <template #default="{ row, $index }">
              <slot :name="col.slot" :row="row" :index="$index"></slot>
            </template>
          </el-table-column>
        </template>
      </el-table>
    </div>

    <div class="page-area" v-if="showPagination">
      <Pagination
        v-model:total="total"
        v-model:page="page"
        v-model:limit="size"
        v-bind="tablePagination"
        @pagination="onPaginationChange"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.base-table {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  overflow: hidden;
  > .table-area {
    flex: 1;
    overflow: hidden;

    :deep(.current-row td.el-table__cell) {
      @apply dark:bg-[var(--el-border-color-dark)];
    }
  }
  > .page-area {
    padding: 20px 0;
    flex: 0 0 auto;
    display: flex;
    justify-content: flex-end;
  }
}

.sub-table {
  height: auto;
  > .table-area {
    flex: none;
  }
}
</style>
