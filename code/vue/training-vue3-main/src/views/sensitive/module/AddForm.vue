<script lang="ts" setup>
import type { FormRules, FormInstance } from "element-plus";
import SensitiveAPI, { type SensitiveForm } from "@/api/sensitive";
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
const sensitiveFormRef = ref<FormInstance>();
const form = reactive<SensitiveForm>({
  sensitive: "",
});

const rules = reactive<FormRules<SensitiveForm>>({
  sensitive: [{ required: true, message: "请输入新增敏感词", trigger: "blur" }],
});

const emits = defineEmits<{
  (e: "handleFinish"): void;
}>();

async function show(show: boolean = false) {
  visible.value = show;
}

const handleSubmit = async () => {
  const valid = await sensitiveFormRef.value?.validate();
  if (!valid) return;
  btn.loading = true;
  btn.disabled = true;

  try {
    const res = await SensitiveAPI.addSensitive(form.sensitive);
    if (res.code === 200) {
      ElMessage.success(`新增敏感词【${form.sensitive}】成功`);
      emits("handleFinish");
      handleClose();
    }
  } finally {
    btn.loading = false;
    btn.disabled = false;
  }
};

const handleClose = (done?: () => void) => {
  sensitiveFormRef.value?.resetFields();
  visible.value = false;
  done?.();
};

defineExpose({
  show,
});

defineOptions({
  name: "SensitiveAddForm",
});
</script>

<template>
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="10vh"
    width="35%"
    draggable
    title="新增敏感词"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-form
      ref="sensitiveFormRef"
      :model="form"
      :rules="rules"
      label-width="120"
    >
      <el-form-item label="敏感词：" prop="sensitive">
        <el-input
          v-model="form.sensitive"
          autocomplete="off"
          placeholder="请输入敏感词"
          clearable
        />
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
