import BaseForm from "@/components/Base/Form/BaseForm.vue";
import BaseFormDialog from "@/components/Base/Form/BaseFormDialog.vue";
import BaseDialog from "@/components/Base/Dialog/BaseDialog.vue";
import BaseTable from "@/components/Base/Table/BaseTable.vue";
import BaseEmptyTip from "@/components/Base/Empty/BaseEmptyTip.vue";
import BaseEcharts from "@/components/Base/Echarts/BaseEcharts.vue";
import BaseCard from "@/components/Base/Card/BaseCard.vue";

declare global {
  /**  基础表单组件类型 */
  type BaseFormType = InstanceType<typeof BaseForm>;

  /**  Dialog 组件类型 */
  type BaseDialogType = InstanceType<typeof BaseDialog>;

  /**  Dialog+表单 组件类型 */
  type BaseFormDialogType = InstanceType<typeof BaseFormDialog>;

  /**  Table 组件类型 */
  type BaseTableType = InstanceType<typeof BaseTable>;

  /**  空提示组件类型 */
  type BaseEmptyTipType = InstanceType<typeof BaseEmptyTip>;

  /**  Echarts 组件类型 */
  type BaseEchartsType = InstanceType<typeof BaseEcharts>;

  /**  Card 组件类型 */
  type BaseCardType = InstanceType<typeof BaseCard>;
}

// 让 TypeScript 识别 .d.ts 文件
export {};
