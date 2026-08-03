<template>
  <el-dialog
    class="dialog-v1"
    v-model="visible"
    top="10vh"
    width="35%"
    draggable
    title="新增知识库"
    destroy-on-close
    :fullscreen="appStore.isMobile"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-upload
      class="upload-demo"
      drag
      :limit="1"
      accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.md"
      :auto-upload="false"
      :on-change="onUploadFile"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">拖拽上传或 <em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip hint">
          支持上传txt、pdf、doc、md等格式的单个文件，单个文件不超过10MB
        </div>
        <div class="dialog-footer">
          <el-button type="info" @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleAdd">确定</el-button>
        </div>
      </template>
    </el-upload>
  </el-dialog>
</template>

<script setup lang="ts">
import { useAppStore } from "@/store/modules/app";
import KnowledgeBaseAPI from "@/api/knowledgeBase";
import { ElLoading } from "element-plus";
import type { FormInstance, UploadFile } from "element-plus";

const appStore = useAppStore();

// 对话框是否可见
let visible = ref<boolean>(false);
const knowledgeBaseFormRef = ref<FormInstance>();
const formData = new FormData();
const loading = ref(false);
const handleClose = (done?: () => void) => {
  knowledgeBaseFormRef.value?.resetFields();
  visible.value = false;
  //清空表单数据
  formData.delete("file");
  loading.value = false;
  done?.();
};

const emits = defineEmits<{
  (e: "handleFinish"): void;
}>();

async function show(show: boolean) {
  visible.value = show;
}

const handleAdd = async () => {
  // 检查是否有文件被选择
  if (!formData.has("file")) {
    ElMessage.error("请先选择一个文件");
    return;
  }
  const loadingInstance = ElLoading.service({
    fullscreen: true,
    text: "正在上传...",
  });
  try {
    const res = await KnowledgeBaseAPI.addKnowledgeBase(formData);

    loading.value = true;
    if (res.code === 200) {
      ElMessage.success("添加成功");
      emits("handleFinish");
      loadingInstance.close();
      handleClose();
    } else {
      ElMessage.error(res.msg);
      loading.value = false;
    }
    //清空表单数据
    formData.delete("file");
  } catch (error) {
    ElMessage.error("添加知识库失败！");
    loading.value = false;
    loadingInstance.close();
    handleClose();
  }
};
const handleCancel = () => {
  handleClose();
};

const onUploadFile = (uploadFile: UploadFile) => {
  formData.append("file", uploadFile.raw as Blob);
};

defineExpose({ show });
</script>

<style scoped lang="scss">
.hint {
  text-align: center;
}
.dialog-footer {
  margin-top: 2%;
  text-align: right;
}
</style>
