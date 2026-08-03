<script lang="ts" setup>
import { debounce, cloneDeep } from "lodash-es";
import "element-plus/dist/index.css";
import * as ElementPlus from "element-plus";
import { Search } from "@element-plus/icons-vue";

const props = defineProps({
  fields: {
    required: true,
    type: Array as PropType<Fields<FormComponentsType[]>>,
    default: () => [],
  },
  model: {
    type: Object as () => FormModelData,
    default: () => ({}),
  },
  useSearch: {
    type: Boolean,
    default: false,
  },
  includesKeys: {
    type: Array as PropType<IncludeKeys>,
    default: () => ["id"],
  },
  labelPosition: {
    type: String as PropType<"top" | "left" | "right">,
    default: "right",
  },
  customElementList: {
    type: Array<FormComponentsType>,
    default: () => [],
  },
  formEventTypes: {
    type: Array<FormEventType>,
    default: () => ["input", "change", "blur", "focus", "clear"],
  },
  searchDebounce: {
    type: Number,
    default: 300,
  },
});

const attrs = useAttrs();
const loading = ref(false);
const JForm = ref<ElementPlus.FormInstance>();
const formData = ref<FormModelData>(props.model || {});
const inline = computed(() => (attrs?.inline as boolean) || true);
const labelWidth = computed(() => (attrs?.labelWidth as string) || "105px");

const emits = defineEmits<{
  (e: "init"): void;
  (e: "search", payload: FormEventPayload): void;
  (e: "submit", payload: FormModelData): void;
  (e: "update:modelValue", data: FormModelData): void;
  (e: FormEventName, payload: FormEventPayload): void;
}>();
onMounted(() => emits("init"));

// 表单搜索按钮点击事件（自带防抖300ms）
const handleSearch = debounce(
  () => emits("search", { model: { ...formData.value } } as FormEventPayload),
  props.searchDebounce
);

// 表单组件事件处理
function handleEvent(
  eventName: FormEventType,
  e: any,
  field: Field<FormComponentsType>
) {
  const payload = {
    e,
    eventType: eventName,
    prop: field.prop,
    model: { ...formData.value },
  } as FormEventPayload;
  emits(`${field.prop}-${eventName}` as FormEventName, payload);

  // 如果是用户自定义组件，手动触发验证
  if (field.isManualValidate) {
    field.rules?.forEach((rule) => {
      if (rule.trigger && rule.trigger.indexOf(eventName) > -1) {
        JForm.value?.validateField?.(field.prop);
      }
    });
  }
}

// 处理自定义组件事件
function handleCustomEvent(
  eventName: FormEventType,
  e: any,
  field: Field<FormComponentsType>
) {
  const payload = {
    e,
    model: { ...formData.value },
    field,
    eventName,
  } as FormEventPayload;

  emits(`${field.prop}-${eventName}` as FormEventName, payload);
}

// 动态事件绑定
function getEventHandlers(field: Field<FormComponentsType>) {
  const handlers = {} as Record<string, Function>;
  // 只绑定声明有event字段的事件，并且后缀需要符合xxx-input、xxx-change等格式
  const filterEvents = field?.events?.map(
    (v) => v.split("-")[1]
  ) as FormEventType[];

  if (filterEvents?.some((e) => props.formEventTypes.includes(e))) {
    // 绑定常见表单事件，格式：@input="(e: any) => handleEvent(eventName, e, field)" ...
    props?.formEventTypes?.forEach((eventName) => {
      handlers[eventName] = (e: any) => handleEvent(eventName, e, field);
    });
  }

  // 绑定自定义组件事件
  field?.customEvents?.forEach((eventName) => {
    handlers[eventName] = (e: any) =>
      handleCustomEvent(eventName as FormEventType, e, field);
  });

  return handlers;
}

function submit() {
  JForm.value?.validate((valid) => {
    if (!valid) return;

    const submitData = {} as FormModelData;

    props.fields
      .map((f) => f.prop)
      .forEach((prop) => (submitData[prop!] = formData.value[prop!]));
    props.includesKeys?.forEach((k) => (submitData[k] = formData.value[k]));

    emits("submit", cloneDeep(submitData));
  });
}

function resetFields() {
  JForm.value?.resetFields();
}

function clearValidate() {
  JForm.value?.clearValidate();
}

defineExpose({
  submit,
  resetFields,
  clearValidate,
  getFormData() {
    return cloneDeep(formData.value);
  },
  setFormData(data?: FormModelData) {
    formData.value = cloneDeep(data || {});
  },
});

defineOptions({
  name: "BaseForm",
});
</script>

<template>
  <div class="base-form" v-loading="loading">
    <div class="slot-area">
      <slot name="before"></slot>
    </div>

    <el-form
      ref="JForm"
      v-bind="$attrs"
      :inline="inline"
      :model="formData"
      :label-position="labelPosition"
      :label-width="labelWidth"
    >
      <template v-for="(field, index) in fields" :key="field.prop">
        <el-form-item
          :label="field.label"
          :prop="field.prop"
          :rules="field.rules"
          :style="field.style"
          :label-width="field.labelWidth"
        >
          <component
            :key="`${field.prop}-${index}`"
            :is="field.component"
            v-bind="field.props"
            v-model="formData[field.prop!]"
            v-on="getEventHandlers(field)"
          >
            <template
              v-if="field.childComponent && field.childProps?.options?.length"
            >
              <component
                :is="field.childComponent"
                v-for="(option, index) in field.childProps?.options"
                v-bind="option"
                :value="option.value"
                :key="option.key || index"
              />
            </template>

            <template v-if="field.props?.appendSlot" #append>
              <slot :name="`${field.prop}-append`"></slot>
            </template>
          </component>

          <!-- 表单项插槽 -->
          <template v-if="field.props?.otherSlot">
            <slot :name="`${field.prop}-other`"></slot>
          </template>

          <!-- 表单项帮助信息-->
          <template v-if="field.props?.tip || field.props?.tipSlot">
            <el-row class="form-item-tip w-full">
              <el-col :span="24" class="text">
                <slot
                  v-if="field.props?.tipSlot"
                  :name="`${field.prop}-tip`"
                ></slot>

                <span v-else v-html="field.props?.tip"></span>
              </el-col>
            </el-row>
          </template>
        </el-form-item>
      </template>

      <el-button
        v-show="useSearch"
        class="search-btn"
        type="primary"
        :icon="Search"
        @click="handleSearch"
        >搜索</el-button
      >
    </el-form>

    <div class="slot-area">
      <slot name="default"></slot>
    </div>
  </div>
</template>

<style lang="scss">
.base-form {
  display: flex;
  align-items: flex-start;
  > .el-form {
    width: 100%;
    height: 100%;
    flex: 1;
    .el-form-item {
      margin-right: 0 !important;
    }
  }
  > .slot-area {
    flex: 0 0 auto;
  }

  .search-btn {
    margin: 0 0 18px 10px;
  }

  // 表单提示文字
  .form-item-tip {
    .text {
      @apply text-[var(--el-text-color-secondary)];
      margin: 0;
      font-size: 12.5px;
      line-height: 17px;
      margin-top: 6px;
    }
  }
}
</style>
