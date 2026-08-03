<script lang="ts" setup>
import type { FormRules, FormInstance } from "element-plus";
import EmployeeAPI, {
  type UserForm,
  UserPageVO,
  UserInfo,
} from "@/api/employee";
import avatar from "@/assets/avatar.png";
import { useAppStore } from "@/store/modules/app";
import { useUserStore } from "@/store/modules/user";
import UploadAPI from "@/api/upload";

const appStore = useAppStore();
const userStore = useUserStore();

const userInfo = ref<UserInfo>();

const btn = reactive({
  // 按钮加载
  loading: false,
  // 按钮是否禁用,默认禁用
  disabled: false,
});

// 用户头像地址
const imgUrl = ref(userInfo.value?.avatar);
const formData = new FormData();

// 图片本地预览
const onUploadFile = (uploadFile: any) => {
  // 将imgUrl设置为本地预览图片的值
  imgUrl.value = URL.createObjectURL(uploadFile.raw);
  formData.append("file", uploadFile.raw);
};

// 对话框是否可见
let visible = ref<boolean>(false);
const EmployeeEditFormRef = ref<FormInstance>();
const form = reactive<UserForm>({
  id: undefined,
  name: "",
  username: "",
  phone: "",
  sex: 1,
  avatar: "",
});
const rules = reactive<FormRules<UserForm>>({
  name: [{ required: true, message: "请输入编辑员工姓名", trigger: "blur" }],
  username: [
    { required: true, message: "请输入编辑员工用户名", trigger: "blur" },
  ],
  sex: [{ required: true, message: "请选择编辑员工的性别", trigger: "change" }],
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

async function show(show: boolean = false, record: UserPageVO = {}) {
  visible.value = show;
  setFieldValues(record);
}

function setFieldValues(record: UserPageVO) {
  nextTick(() => {
    Object.assign(form, {
      id: record.id,
      name: record.name,
      username: record.username,
      phone: record.phone,
      sex: record.sex,
    });
  });
}

const handleSubmit = async () => {
  try {
    const valid = await EmployeeEditFormRef.value?.validate();
    if (!valid) return;
    btn.loading = true;
    btn.disabled = true;

    // 检查是否有文件被选择
    if (!formData.has("file")) {
      ElMessage.error("请选择一个图片文件");
      return;
    }

    // 调用上传文件接口
    const uploadRes = await UploadAPI.upload(formData);
    if (!uploadRes.code) {
      ElMessage.error("上传失败，请重试");
      return;
    }

    form.avatar = uploadRes.data;

    const res = await EmployeeAPI.update(form);
    if (res.code === 200) {
      ElMessage.success(`编辑员工【${form.name}】成功`);
      emits("handleFinish");
      handleClose();
    }
    btn.loading = false;
    btn.disabled = false;
  } catch (error) {
    ElMessage.error("上传失败，请重试");
    console.error(error);
  } finally {
    formData.delete("file"); // 清空formData以便下次使用
  }
};

const handleClose = (done?: () => void) => {
  EmployeeEditFormRef.value?.resetFields();
  visible.value = false;
  done?.();
};

defineExpose({
  show,
});

onMounted(async () => {
  userInfo.value = await userStore.getInfo();
  console.log("userInfo==>", userInfo.value);

  if (userInfo.value.avatar && userInfo.value.id === 1) {
    imgUrl.value = userInfo.value.avatar;
  }
});

defineOptions({
  name: "EmployeeEditForm",
});
</script>

<template>
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="10vh"
    width="35%"
    draggable
    title="编辑员工表单"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-form
      ref="EmployeeEditFormRef"
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
          <p>编辑员工的名称，例如：张三</p>
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
          <el-radio value="1">男</el-radio>
          <el-radio value="0">女</el-radio>
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

      <el-form-item
        label="头像"
        prop="avatar"
        v-if="userInfo?.id === 1 && userInfo.username === 'admin'"
      >
        <el-upload
          ref="uploadRef"
          class="avatar-uploader"
          :show-file-list="false"
          :auto-upload="false"
          :on-change="onUploadFile"
          name="file"
        >
          <img v-if="imgUrl" :src="imgUrl" class="avatar" />
          <img v-else :src="avatar" width="278" />
        </el-upload>
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

<style lang="scss" scoped>
.avatar {
  width: 278px;
  height: 278px;
  display: block;
  object-fit: cover;
}
</style>
