import type { ElButton, ElMessageBoxOptions } from "element-plus";

// 按钮组类型定义
declare global {
  /** elementPlus弹窗提示配置项 */
  interface MessageBoxOptions extends ElMessageBoxOptions {}

  /**
   * 按钮组类型定义
   */
  type BaseButtonGroup = {
    /** 按钮文本 */
    label?: string | number;
    /** element-plus弹窗提示配置项 */
    messageBoxProps?: {
      /** 消息弹窗取消事件 */
      onCancel?: (item?: BaseButtonGroup, index?: number) => void;
      /** 消息弹窗确认事件 */
      onConfirm?: (item?: BaseButtonGroup, index?: number) => void;
    } & MessageBoxOptions;
    /** el-button组件属性 */
    props?: Writable<ExtractPropTypes<InstanceType<typeof ElButton>["$props"]>>;
    /** 点击事件 */
    onClick?: (item?: BaseButtonGroup, index?: number) => void;
  };
}

export {};
