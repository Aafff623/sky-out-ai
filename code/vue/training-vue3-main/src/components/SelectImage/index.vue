<script setup lang="ts">
import * as _ from "lodash-es";
import { ElMessage } from "element-plus";
import type {
  UploadRawFile,
  UploadRequestOptions,
  UploadInstance,
  UploadFile,
} from "element-plus";
import UploadAPI from "@/api/upload";

// el-upload实例
const uploadRef = ref<UploadInstance>();
// 加载
const loading = ref<boolean>(false);
// 允许上传的文件类型
const accept = ref<string>("image/jpeg,image/png,image/gif,image/webp");
// 上传文件大小限制
const uploadSizeLimit = ref<number>(3);
// 本地临时上传图片
const temporaryImg = ref("");

const props = defineProps({
  // 限制文件上传个数
  limit: {
    type: Number,
    default: 1,
  },
  // 默认显示的文件
  defaultList: {
    type: Array,
    default: () => [],
  },
  // 元素的尺寸(宽)
  width: {
    type: Number,
    default: 80,
  },
  // 是否为自动上传
  autoUpload: {
    type: Boolean,
    default: true,
  },
  // 上传的文件字段名
  fileName: {
    type: String,
    default: "file",
  },
  modelValue: {
    type: String,
    default: "",
  },
});

const emits = defineEmits(["update:modelValue", "onSuccess"]);
const imageUrl = useVModel(props, "modelValue", emits);

const isUploadDisable = computed<boolean>(() => {
  return loading.value && temporaryImg.value !== "";
});

/**
 * 限制用户上传文件的格式和大小
 */
function beforeUpload(file: UploadRawFile) {
  // 验证上传的文件大小
  const fileSizeMb = file.size / 1024 / 1024;
  // 上传大小限制2M
  if (fileSizeMb > uploadSizeLimit.value) {
    handleDeleteItem();
    // 显示错误提示
    ElMessage.error(`上传文件大小不能超出${uploadSizeLimit.value}MB`);
    return false;
  }
  return true;
}

// 文件状态改变时的钩子，添加文件、上传成功和上传失败时都会被调用
function onChange(uploadFile: UploadFile) {
  temporaryImg.value = URL.createObjectURL(uploadFile.raw!);
}

// 删除图片
const handleDeleteItem = () => {
  imageUrl.value = "";
  // 清空已上传的文件列表
  clearFiles();
};

// 自定义请求上传
const onUpload = async (options: UploadRequestOptions): Promise<any> => {
  // 构建上传参数
  const formData = new FormData();
  // 设置上传的文件name
  formData.append(props.fileName, options.file);

  // 开始加载
  loading.value = true;

  const res = await UploadAPI.upload(formData);
  if (res.code !== 200) {
    return ElMessage.error(res.msg || "上传失败");
  }

  // 更新图片url
  imageUrl.value = res.data;
  // 通知父组件
  emits("onSuccess");
  ElMessage.success("上传成功");

  loading.value = false;
};

const onError = () => {
  console.log("上传失败");

  loading.value = false;
  setTimeout(clearFiles, 0);
};

/**
 * 清空已上传的文件列表（该方法不支持在 before-upload 中调用）
 */
function clearFiles() {
  // 删除临时图片
  temporaryImg.value = "";
  // 清空文件
  uploadRef.value?.clearFiles?.();
}

defineExpose({
  clearFiles,
});

defineOptions({
  name: "SelectImage",
});
</script>

<template>
  <div class="select-image flex items-center flex-wrap">
    <!-- 显示上传成功的图片 -->
    <div
      v-if="imageUrl"
      class="file-item"
      :style="{ width: `${width}px`, height: `${width}px` }"
    >
      <a :href="imageUrl" target="_blank" title="点击查看图片">
        <el-image
          class="img-cover"
          style="width: 100%; height: 100%"
          :src="imageUrl"
          fit="cover"
        />
      </a>
      <i-ep-circle-close-filled
        class="el-icon-error"
        @click="handleDeleteItem"
      />
    </div>

    <div
      v-else
      class="upload-image"
      :style="{ width: `${width}px`, height: `${width}px` }"
    >
      <el-upload
        ref="uploadRef"
        :name="fileName"
        list-type="picture-card"
        with-credentials
        action="#"
        method="post"
        class="wh-full"
        :headers="{ 'Content-Type': 'multipart/form-data' }"
        :disabled="isUploadDisable"
        :limit="limit"
        :auto-upload="autoUpload"
        :show-file-list="true"
        :accept="accept"
        :before-upload="beforeUpload"
        :http-request="onUpload"
        :on-change="onChange"
        :on-error="onError"
      >
        <div
          v-loading="loading"
          class="selector wh-full"
          :class="{ disabled: loading, selected: !loading }"
          title="点击选择图片"
        >
          <!-- 上传中，预览本地图片 -->
          <a
            v-if="temporaryImg"
            title="预览上传图片"
            target="_blank"
            class="wh-full"
            :href="temporaryImg"
          >
            <el-image
              class="img-cover"
              style="width: 100%; height: 100%"
              :src="temporaryImg"
              :key="temporaryImg"
              fit="cover"
            />
          </a>

          <!-- 未上传 -->
          <i-ep-plus
            v-else
            class="el-icon-plus"
            :style="{ fontSize: `${width * 0.4}px` }"
          />
        </div>
      </el-upload>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 文件元素
.file-item {
  @apply bg-#fff dark:bg-[var(--el-border-color-light)];
  position: relative;
  width: 80px;
  height: 80px;
  padding: 2px;
  border: 1px solid #ddd;
  margin-right: 7px;
  border-radius: 3px;

  .img-cover {
    display: block;
    border-radius: 3px;
    background: no-repeat center center / 100%;
  }

  &:hover {
    border-color: #a7c3de;
  }
}

.file-item,
.temporary-preview {
  .el-icon-error {
    display: none;
    position: absolute;
    top: -6px;
    right: -6px;
    cursor: pointer;
    font-size: 16px;
    color: #c5c5c5;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  &:hover {
    .el-icon-error {
      display: block;
    }
  }
}

// 选择器
.selector {
  @apply flex-center;
  float: left;
  border: 1px dashed #e2e2e2;
  text-align: center;
  color: #dad9d9;
  padding: 2px;
  border-radius: 3px;

  .el-icon-plus {
    font-size: 32px;
  }
}

.selected {
  cursor: pointer;

  &:hover {
    border: 1px dashed var(--el-color-primary);
    color: var(--el-color-primary);
  }
}

// 正在上传时禁止继续上传
.disabled {
  cursor: default;
  pointer-events: none;

  &:hover {
    border: 1px dashed #e2e2e2;
    color: none;
  }
}

// 上传图片样式穿透
:deep(.upload-image) {
  .el-upload--picture-card {
    @apply bg-#eff0f5 dark:bg-[var(--el-bg-color-overlay)];
    width: 80px;
    height: 80px;
    border: none;
    line-height: 0;
    overflow: hidden;
    border-radius: 3px;
  }

  // 隐藏picture-card图片列表
  .el-upload-list--picture-card .el-upload-list__item {
    display: none;
  }
}
</style>
