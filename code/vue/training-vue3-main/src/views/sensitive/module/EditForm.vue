<script lang="ts" setup>
import type { FormRules, FormInstance } from "element-plus";
import SensitiveAPI, {
  type SensitiveForm,
  type Sensitive,
} from "@/api/sensitive";
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
const sensitiveEditFormRef = ref<FormInstance>();
const form = reactive<SensitiveForm>({
  id: undefined,
  sensitives: "",
});

const rules = reactive<FormRules<SensitiveForm>>({
  sensitives: [{ required: true, message: "请输入敏感词", trigger: "blur" }],
});

const emits = defineEmits<{
  (e: "handleFinish"): void;
}>();

async function show(show: boolean = false, record: Sensitive) {
  visible.value = show;
  setFieldValues(record);
}

function setFieldValues(record: Sensitive) {
  nextTick(() => {
    Object.assign(form, {
      id: record.id,
      sensitives: record.sensitives,
    });
    console.log(form);
  });
}

const handleSubmit = async () => {
  const valid = await sensitiveEditFormRef.value?.validate();
  if (!valid) return;
  btn.loading = true;
  btn.disabled = true;

  try {
    const res = await SensitiveAPI.updateSensitive(form);
    if (res.code === 200) {
      ElMessage.success(`编辑敏感词【${form.sensitives}】成功`);
      emits("handleFinish");
      handleClose();
    }
  } finally {
    btn.loading = false;
    btn.disabled = false;
  }
};

const handleClose = (done?: () => void) => {
  sensitiveEditFormRef.value?.resetFields();
  visible.value = false;
  done?.();
};

defineExpose({
  show,
});

defineOptions({
  name: "SensitiveEditForm",
});
</script>

<template>
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="10vh"
    width="35%"
    draggable
    title="编辑敏感词"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-form
      ref="sensitiveEditFormRef"
      :model="form"
      :rules="rules"
      label-width="120"
    >
      <el-form-item label="敏感词：" prop="sensitives">
        <el-input
          v-model="form.sensitives"
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
