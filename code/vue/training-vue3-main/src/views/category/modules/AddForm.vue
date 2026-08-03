<script lang="ts" setup>
import type { FormRules, FormInstance } from "element-plus";
import CategoryAPI, { type CategoryForm } from "@/api/category";
import { useAppStore } from "@/store/modules/app";

const appStore = useAppStore();

const btn = reactive({
  // 按钮加载
  loading: false,
  // 按钮是否禁用,默认禁用
  disabled: false,
});

// 对话框是否可见
let visible = ref<boolean>(false);
const CategoryAddFormRef = ref<FormInstance>();
const form = reactive<CategoryForm>({
  name: "",
  type: undefined,
  sort: 100,
});
const rules = reactive<FormRules<CategoryForm>>({
  name: [{ required: true, message: "请输入新增分类姓名", trigger: "blur" }],
  type: [{ required: true, message: "请选择新增分类类型", trigger: "change" }],
  sort: [
    { required: true, message: "请为该分类选择一个排序序号", trigger: "blur" },
  ],
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

const emits = defineEmits<{
  (e: "handleFinish"): void;
}>();

async function show(show: boolean = false) {
  visible.value = show;
}

const handleSubmit = async () => {
  const valid = await CategoryAddFormRef.value?.validate();
  if (!valid) return;
  btn.loading = true;
  btn.disabled = true;

  const res = await CategoryAPI.add(form);
  if (res.code === 200) {
    ElMessage.success(`新增分类【${form.name}】成功`);
    emits("handleFinish");
    handleClose();
  }
  btn.loading = false;
  btn.disabled = false;
};

const handleClose = (done?: () => void) => {
  CategoryAddFormRef.value?.resetFields();
  visible.value = false;
  done?.();
};

defineExpose({
  show,
});

defineOptions({
  name: "CategoryAddForm",
});
</script>

<template>
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="10vh"
    width="35%"
    draggable
    title="新增分类表单"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <div class="dialog-alert-tip">
      <el-alert
        show-icon
        title="注意：新增的分类【默认是禁用】的，需要手动启用"
        type="warning"
      />
    </div>
    <el-form
      ref="CategoryAddFormRef"
      :model="form"
      :rules="rules"
      label-width="120"
    >
      <el-form-item label="分类名称：" prop="name">
        <el-input
          v-model="form.name"
          autocomplete="off"
          placeholder="请输入分类的名称"
          clearable
        />
        <div class="form-item-help">
          <p>新增分类的名称，例如：饮料</p>
        </div>
      </el-form-item>

      <el-form-item label="分类类型：" prop="type">
        <el-select v-model="form.type" clearable placeholder="选择分类类型">
          <el-option
            v-for="(option, optionIndex) in categoryOptions"
            :key="optionIndex"
            :label="option.name"
            :value="option.value"
          />
        </el-select>
        <div class="form-item-help">
          <p>新增分类的类型</p>
        </div>
      </el-form-item>

      <el-form-item label="排序：" prop="sort">
        <el-input-number v-model="form.sort" :min="0" />
        <div class="form-item-help">
          <p>排序，数字越小越靠前</p>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose()">取消</el-button>
      <el-button
        type="primary"
        :loading="btn.loading"
        :disabled="btn.disabled"
        @click="handleSubmit"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
