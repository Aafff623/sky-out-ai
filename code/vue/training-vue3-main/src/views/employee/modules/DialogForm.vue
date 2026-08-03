<script lang="ts" setup>
import { ElInput } from "element-plus";
import EmployeeAPI from "@/api/employee";

type CompTypes = [typeof ElInput];
type ModelType = ExtractParam<typeof EmployeeAPI.add>;

const JDialogForm = ref<BaseFormDialogType>();
const model = reactive<ModelType>({});
const fields = ref<Fields<CompTypes, ModelType>>([
  {
    label: "员工姓名：",
    prop: "username",
    style: "width: 100%",
    events: ["username-input"],
    component: markRaw(ElInput),
    props: { clearable: true, placeholder: "请输入员工姓名", tip: "员工姓名不能为空" },
    rules: [
      {
        required: true,
        trigger: "blur",
        validator: (_, __, callback, data: ModelType) => {
          if (!data?.username) {
            return callback(new Error("请输入员工姓名"));
          }
          callback();
        },
      },
    ],
  },
]);

function show() {
  JDialogForm.value?.show();
}

const submit = (data: ModelType) => {
  console.log("提交表单==>", model, data);
};

const onUsernameInput = (value: FormEventPayload<ModelType>) => {
  console.log("用户名输入==>", value);
};

defineExpose({
  show,
});
</script>

<template>
  <div class="employee-dialog-form">
    <BaseFormDialog
      title="新增员工"
      v-model="model"
      :fields="fields"
      ref="JDialogForm"
      @submit="submit"
      @usernameInput="onUsernameInput"
    >
    </BaseFormDialog>
  </div>
</template>

<style lang="scss" scoped></style>
