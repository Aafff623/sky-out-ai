<script lang="ts" setup>
import { Search } from "@element-plus/icons-vue";
import UserAPI, {
  type WxUserPageQuery,
  WxUserPageVO,
  baseUrls,
} from "@/api/user";
import { IMG_WX_DEFAULT_AVATAR } from "@/constants/minio";

const route = useRoute();
const loading = ref(false);
const total = ref(0);
const JTable = ref<BaseTableType>();
const queryParams = reactive<WxUserPageQuery>({
  name: "",
  phone: "",
  page: 1,
  pageSize: 10,
});
const tableData = reactive<TableData<WxUserPageVO[]>>({
  data: [],
  pagination: {},
  columns: [
    {
      label: "头像",
      slot: "avatar",
      width: "90px",
    },
    {
      label: "昵称",
      prop: "name",
      minWidth: "120px",
    },
    {
      label: "手机号",
      prop: "phone",
      minWidth: "130px",
    },
    {
      label: "注册时间",
      prop: "createTime",
      minWidth: "170px",
    },
  ],
}) as TableData<WxUserPageVO[]>;

onMounted(() => {
  JTable.value?.updateData({ requestConfig: { url: baseUrls.getPage } });
});

function handleQuery() {
  loading.value = true;
  UserAPI.getPage(queryParams)
    .then((res) => {
      total.value = res.data?.total;
      tableData.data = res.data?.records;
    })
    .catch((err) => {
      ElMessage.error(err.msg || "获取用户列表失败");
    })
    .finally(() => (loading.value = false));
}

defineOptions({
  name: "WxUser",
});
</script>

<template>
  <div class="wx-user">
    <el-card>
      <div class="card-title">{{ route.meta.title }}</div>

      <el-row :gutter="20" class="mb-20px">
        <el-col :span="12" :xs="{ span: 24 }">
          <el-input
            clearable
            v-model="queryParams.name"
            placeholder="输入用户昵称"
            :suffix-icon="Search"
            @change="handleQuery"
            class="float-right max-w-330px sm:min-w-220px"
          />
        </el-col>
      </el-row>

      <BaseTable ref="JTable" v-model="tableData">
        <template #avatar="{ row }">
          <el-avatar
            :size="44"
            :src="row.avatar || IMG_WX_DEFAULT_AVATAR"
          />
        </template>
      </BaseTable>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.wx-user {
  .el-avatar {
    vertical-align: middle;
    background-color: var(--el-fill-color);
  }
}
</style>
