<script lang="ts" setup>
import type { FormRules, FormInstance } from "element-plus";
import EmployeeAPI, { type UserForm } from "@/api/employee";
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
const EmployeeAddFormRef = ref<FormInstance>();
const form = reactive<UserForm>({
  name: "",
  username: "",
  phone: "",
  sex: 1,
});
const rules = reactive<FormRules<UserForm>>({
  name: [{ required: true, message: "请输入新增员工姓名", trigger: "blur" }],
  username: [
    { required: true, message: "请输入新增员工用户名", trigger: "blur" },
  ],
  sex: [{ required: true, message: "请选择新增员工的性别", trigger: "change" }],
  phone: [
    {
      required: true,
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
});

const emits = defineEmits<{
  (e: "handleFinish"): void;
}>();

async function show(show: boolean = false) {
  visible.value = show;
}

const handleSubmit = async () => {
  const valid = await EmployeeAddFormRef.value?.validate();
  if (!valid) return;
  btn.loading = true;
  btn.disabled = true;

  const res = await EmployeeAPI.add(form);
  if (res.code === 200) {
    ElMessage.success(`新增员工【${form.name}】成功`);
    emits("handleFinish");
    handleClose();
  }
  btn.loading = false;
  btn.disabled = false;
};

const handleClose = (done?: () => void) => {
  EmployeeAddFormRef.value?.resetFields();
  visible.value = false;
  done?.();
};

defineExpose({
  show,
});

defineOptions({
  name: "EmployeeAddForm",
});
</script>

<template>
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="10vh"
    width="35%"
    draggable
    title="新增员工表单"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-form
      ref="EmployeeAddFormRef"
      :model="form"
      :rules="rules"
      label-width="120"
    >
      <el-form-item label="员工姓名：" prop="name">
        <el-input
          v-model="form.name"
          autocomplete="off"
          placeholder="请输入员工的名称"
          clearable
        />
        <div class="form-item-help">
          <p>新增员工的名称，例如：张三</p>
        </div>
      </el-form-item>

      <el-form-item label="员工用户名：" prop="username">
        <el-input
          v-model="form.username"
          autocomplete="off"
          placeholder="请输入员工用户名"
          clearable
        />
        <div class="form-item-help">
          <p>用于登录后台的账号，例如：zhangsan</p>
        </div>
      </el-form-item>

      <el-form-item label="员工性别：" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio :value="1">男</el-radio>
          <el-radio :value="0">女</el-radio>
        </el-radio-group>
        <div class="form-item-help">
          <p>员工的性别</p>
        </div>
      </el-form-item>

      <el-form-item label="员工手机号：" prop="phone">
        <el-input
          v-model="form.phone"
          autocomplete="off"
          placeholder="请输入员工手机号"
        />
        <div class="form-item-help">
          <p>该员工的手机号</p>
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
