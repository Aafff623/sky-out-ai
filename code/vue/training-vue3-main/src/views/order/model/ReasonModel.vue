<script lang="ts" setup>
import OrderAPI, {
  OrderPageVO,
  OrdersRejectionDTO,
  OrdersCancelDTO,
} from "@/api/order";
import { ReasonEnum } from "@/enums/OrderEnum";
import { useAppStore } from "@/store";
import type { FormRules, FormInstance } from "element-plus";

type CombinedOrderForm = {
  /** 订单ID */
  id?: number;
} & Omit<OrdersRejectionDTO, "id"> &
  Omit<OrdersCancelDTO, "id">;

const appStore = useAppStore();

let visible = ref<boolean>(false);
const ReasonFormRef = ref<FormInstance>();
const btn = reactive({
  // 按钮加载
  loading: false,
  // 按钮是否禁用,默认禁用
  disabled: false,
});
const form = reactive<CombinedOrderForm>({
  id: undefined,
  cancelReason: "",
  rejectionReason: "",
});
const checkCancelReason = (_: any, value: string, callback: any) => {
  if (value === "" && reason.value === ReasonEnum.CANCEL) {
    callback(new Error("取消订单的原因不能为空"));
  } else {
    callback();
  }
};
const checkRejectionReason = (_: any, value: string, callback: any) => {
  if (value === "" && reason.value === ReasonEnum.REJECTTION) {
    callback(new Error("拒绝订单的原因不能为空"));
  } else {
    callback();
  }
};
const rules = reactive<FormRules<CombinedOrderForm>>({
  cancelReason: [
    { required: true, validator: checkCancelReason, trigger: "blur" },
  ],
  rejectionReason: [
    { required: true, validator: checkRejectionReason, trigger: "blur" },
  ],
});
const reason = ref<ReasonEnum>();
const record = ref<OrderPageVO>();

const sizeDrawer = computed(() => {
  return appStore.isMobile ? "100%" : "30%";
});

const emit = defineEmits(["handleRefresh"]);

function show(r: ReasonEnum, row: OrderPageVO) {
  visible.value = true;
  record.value = row;
  reason.value = r;
  console.log("填写原因==>", r, row);
}

const cancelClick = () => {
  handleClose();
};

const confirmClick = () => {
  ReasonFormRef.value?.validate((valid: boolean) => {
    if (!valid) return;
    btn.loading = true;
    btn.disabled = true;
    reason.value === ReasonEnum.CANCEL ? cancelOrder() : rejectOrder();
    btn.loading = false;
    btn.disabled = false;
  });
};

const cancelOrder = async () => {
  const res = await OrderAPI.cancel({
    id: record.value?.id,
    cancelReason: form.cancelReason,
  });
  if (res.code === 200) {
    handleClose();
    emit("handleRefresh");
    ElMessage.success("订单取消成功");
  }
};

const rejectOrder = async () => {
  const res = await OrderAPI.rejection({
    id: record.value?.id,
    rejectionReason: form.rejectionReason,
  });
  if (res.code === 200) {
    handleClose();
    emit("handleRefresh");
    ElMessage.success("拒单成功");
  }
};

const drawerOpen = () => {
  ReasonFormRef.value?.resetFields();
};

const handleClose = (done?: () => void) => {
  visible.value = false;
  done?.();
};

defineExpose({
  show,
});

defineOptions({
  name: "ReasonModel",
});
</script>

<template>
  <div class="reason-drawer">
    <el-drawer
      v-model="visible"
      direction="rtl"
      :size="sizeDrawer"
      @open="drawerOpen"
    >
      <template #header>
        <h4>填写【{{ reason }}】原因</h4>
      </template>
      <template #default>
        <el-form
          ref="ReasonFormRef"
          :model="form"
          :rules="rules"
          label-width="120"
        >
          <el-form-item
            v-if="reason === ReasonEnum.CANCEL"
            label="取消订单原因："
            prop="cancelReason"
            label-position="top"
          >
            <el-input
              v-model="form.cancelReason"
              show-word-limit
              maxlength="500"
              type="textarea"
              placeholder="请输入取消订单原因"
              :autosize="{ minRows: 4, maxRows: 5 }"
            />
            <div class="form-item-help">
              <p>商家对取消订单原因的说明</p>
            </div>
          </el-form-item>

          <el-form-item
            v-else
            label="拒单原因："
            prop="rejectionReason"
            label-position="top"
          >
            <el-input
              v-model="form.rejectionReason"
              show-word-limit
              maxlength="500"
              type="textarea"
              placeholder="请输入拒绝订单原因"
              :autosize="{ minRows: 4, maxRows: 5 }"
            />
            <div class="form-item-help">
              <p>商家对拒绝订单原因的说明</p>
            </div>
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <div style="flex: auto">
          <el-button @click="cancelClick">取消</el-button>
          <el-button
            type="primary"
            :loading="btn.loading"
            :disabled="btn.loading"
            @click="confirmClick"
            >确认</el-button
          >
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<style lang="scss">
.reason-drawer {
  .el-drawer {
    .el-drawer__header {
      margin-bottom: 0;
    }
    .el-drawer__footer {
      padding-bottom: 35px;
    }
  }
}
</style>
