<script lang="ts" setup>
import { PropType } from "vue";
import { cloneDeep } from "lodash-es";
import request from "@/utils/request";

const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  model: {
    required: true,
    type: Object as PropType<FormModelData>,
    default: () => ({}),
  },
  fields: {
    required: true,
    type: Array as PropType<Fields<FormComponentsType[]>>,
    default: () => [],
  },
  includesKeys: {
    type: Array as PropType<IncludeKeys>,
    default: () => ["id"],
  },
  reqOptions: {
    type: Object as PropType<RequestOptions>,
    default: () =>
      ({
        method: "post", // 默认post请求
      }) as RequestOptions,
  },
  btnLoading: { type: Boolean, default: false },
  slotSuffixes: {
    type: Array<FormSlotSuffix>,
    default: () => ["append", "other", "tip"],
  },
});

const emits = defineEmits<{
  (e: "open"): void;
  (e: "opened"): void;
  (e: "close"): void;
  (e: "closed"): void;
  (e: "submit", data: FormModelData): void;
  (e: "update:modelValue", data: FormModelData): void;
  (e: FormEventName, payload: { e: any; model: FormModelData }): void;
}>();

const attrs = useAttrs();
const url = ref<RequestOptions["url"]>("");
const methods = ref<RequestOptions["method"]>("");
const visible = ref(false);
const loading = ref(false);
const JDialog = ref<BaseDialogType>();
const JForm = ref<BaseFormType>();
const listeners = reactive<FormListeners>({});
const formData = ref<FormModelData>(props.model || {});
// const formFields = ref<Fields<FormComponentsType[]>>([]);

const title = computed(() => (attrs?.title as string) || props.title);
const labelWidth = computed(() => (attrs?.labelWidth as string) || "80px");
const fieldsSlot = computed(() =>
  props.fields.filter(
    (f) => f?.props?.appendSlot || f?.props?.otherSlot || f?.props?.tipSlot
  )
);
const dynamicSlotNames = computed(() => {
  return fieldsSlot.value?.flatMap((field) =>
    props?.slotSuffixes.map((suffix) => `${field.prop}-${suffix}`)
  );
});
const formFields = computed<Fields<FormComponentsType[]>>(() =>
  props.fields?.map((item) => {
    item.rules?.forEach((rule) => {
      if (rule.validator) {
        const originValidator = rule.validator;
        const modelData = JForm.value?.getFormData?.();

        rule.validator = (rule, value, callback) =>
          originValidator(rule, value, callback, modelData!, {});
      }
    });
    return item;
  })
);

function show(data?: DialogShowParams): Promise<{ closeLoading: () => void }> {
  return new Promise((resolve) => {
    loading.value = data?.showLoading ?? false;
    url.value = data?.url || "";
    methods.value = data?.method || "";
    formData.value = data?.payload
      ? cloneDeep(data.payload)
      : cloneDeep(props.model);

    // 注册表单事件
    registerEvents();
    visible.value = true;
    resolve({
      closeLoading: () => {
        loading.value = false;
      },
    });
  });
}

// 注册表单事件
function registerEvents() {
  // 动态绑定子组件事件并向外暴露事件
  const { fields } = toRefs(props);
  for (const field of fields.value) {
    if (field?.events?.length) {
      for (const eventName of field.events) {
        // 暴露的事件格式：@xxx-input="函数"
        listeners[eventName] = (data: FormEventPayload) =>
          emits(eventName, data);
      }
    }
  }
}

// 提交表单数据请求
async function submitRequest<T = any>(data: T) {
  loading.value = true;
  const res = await request<any, ReqResultType>({
    ...props.reqOptions,
    url: url.value || (attrs.url as RequestOptions["url"]),
    method:
      methods.value ||
      (attrs.method as RequestOptions["method"]) ||
      props.reqOptions.method ||
      "post",
    data: data as FormModelData<T>,
  });
  loading.value = false;

  if (res.code === 200) {
    ElMessage.success(res.msg || "提交成功");
    onClosed();
  } else {
    res.msg && ElMessage.error(res.msg);
  }

  return res;
}

// 弹窗打开（前）
function onOpen() {
  // 设置表单默认值
  JForm.value?.setFormData(formData.value);
  emits("open");
}

// 弹窗打开（打开之后）
function onOpened() {
  emits("opened");
}

// 隐藏弹窗
function onClosed() {
  visible.value = false;
  JForm.value?.resetFields?.();
  formData.value = {};
  emits("closed");
}

defineExpose({
  show,
  onClosed,
  submitRequest,
});

defineOptions({
  name: "BaseDialog",
});
</script>

<template>
  <BaseDialog
    ref="JDialog"
    v-bind="$attrs"
    :title="title"
    v-model="visible"
    :loading="loading"
    :btnLoading="btnLoading"
    @open="onOpen"
    @opened="onOpened"
    @closed="onClosed"
    @confirm="JForm?.submit?.()"
  >
    <div class="base-form-dialog">
      <BaseForm
        ref="JForm"
        v-on="listeners"
        :fields="formFields"
        :label-width="labelWidth"
        :includesKeys="includesKeys"
        @submit="$emit('submit', $event)"
        @init="registerEvents"
      >
        <template
          v-for="slotName in dynamicSlotNames"
          #[slotName]="slotProps"
          :key="slotName"
        >
          <slot :name="`dialog-${slotName}`" v-bind="slotProps"></slot>
        </template>
      </BaseForm>
    </div>
  </BaseDialog>
</template>

<style lang="scss" scoped>
.base-form-dialog {
  width: 100%;
  height: 100%;
  padding: 10px;
  padding-bottom: 0;
  box-sizing: border-box;
}
</style>
