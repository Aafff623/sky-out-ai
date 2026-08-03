import { CSSProperties } from "vue";
import type { ExtractPropTypes } from "vue";
import * as ElementPlus from "element-plus";

// 全局动态表单组件类型
declare global {
  /** 表单通用数据类型 */
  type FormModelData<T = any> = Record<string, T>;

  /** FormEventPayload 类型：约束事件及其参数 */
  type FormEventPayload<T extends FormModelData = FormModelData> = {
    /** 事件对象，或者包含值的对象 */
    e: Event | { value: any };
    /** 触发事件类型 */
    eventType?: FormEventType;
    /** 表单项prop */
    prop?: keyof T;
    /** 表单数据 */
    model: T;
  };

  /** 表单选项类型 */
  type FormItemOptions<
    TModel extends FormModelData = FormModelData,
    K extends keyof TModel = keyof TModel,
  > = Array<{
    /** 选项键 */
    key?: any;
    /** 选项标签 */
    label?: string;
    /** 选项值 */
    value?: TModel[K];
    /** 是否禁用 */
    disabled?: boolean;
    /** 允许其他未知的配置项 */
    [customKey: string]: any;
  }>;

  /**
   * ElementPlus 所有的组件类型
   */
  type AllElementPlusComponentsType = keyof typeof ElementPlus extends infer K
    ? K extends string
      ? (typeof ElementPlus)[K]
      : never
    : never;

  /**
   * 用户自定义组件类型
   * @example type CustomFormComponentsType = typeof MyCustomComponent | typeof AnotherCustomComponent;
   */
  type CustomFormComponentsType = never;

  /**
   * 动态表单所有的组件联合类型（ElementPlus 和 用户自定义）
   */
  type FormComponentsType =
    | AllElementPlusComponentsType
    | CustomFormComponentsType;

  /**
   * 表单动态组件具有的属性
   */
  type FieldProps<T extends FormComponentsType> = {
    /** 组件的样式 */
    style?: CSSProperties;

    /** 表单项提示信息 */
    tip?: string;

    /** 表单项提示插槽（优先级高于 tip） */
    tipSlot?: boolean;

    /** 动态组件插槽 */
    appendSlot?: boolean;

    /** el-form-item表单项插槽 */
    otherSlot?: boolean;

    /** 允许其他未知的配置项 */
    [customKey: string]: any;
  } & Writable<ExtractPropTypes<InstanceType<T>["$props"]>>;

  // 工具类型：从 Field 中提取 component 的具体类型
  type ExtractComponentType<T> = T extends { component: infer Comp }
    ? Comp
    : never;

  /**
   * 表单配置项
   */
  type Field<
    Comp extends FormComponentsType = FormComponentsType,
    Prop extends FormModelData = FormModelData,
  > = {
    /** 表单项的标签 */
    label?: string;

    /** 表单项绑定的字段名 */
    prop?: keyof Prop;

    /** 表单项的标签宽度 */
    labelWidth?: string;

    /** 常用表单事件注册，[`${field.prop}-input`, ...] */
    events?: Array<FormEventName<Prop>>;

    /** 自定义组件的自定义事件注册，[`${field.prop}-自定义事件名称`, ...] */
    customEvents?: Array<FormEventName<Prop>>;

    /** 是否为手动触发表单校验 （自定义组件表单项需要手动触发校验）*/
    isManualValidate?: boolean;

    /** 用于渲染表单项的组件实例 */
    component?: Comp;

    /** 组件的属性，自动推导根据传入组件的类型 */
    props?: FieldProps<Comp>;

    /** 表单项的校验规则 */
    rules?: Array<ElementPlus.FormItemRule>;

    /** 组件的样式 */
    style?: string | CSSProperties;

    /** 子组件实例 */
    childComponent?: FormComponentsType;

    /** 子组件的属性 */
    childProps?: {
      /** 组件的样式 */
      style?: CSSProperties;

      /** 表单项的选项 */
      options?: FormItemOptions<Prop>;

      /** 允许其他未知的子组件配置项 */
      [customKey: string]: any;
    };
  };

  /**
   * 动态表单类型约束
   */
  type Fields<
    T extends FormComponentsType[] = FormComponentsType[],
    Prop extends FormModelData = FormModelData,
  > = {
    [K in keyof T]: Field<T[K], Prop>;
  };

  /** 表单插槽后缀名称 */
  type FormSlotSuffix = "append" | "other" | "tip";

  /** 表单常用事件类型 */
  type FormEventType = "input" | "change" | "blur" | "focus" | "clear";

  /**
   * 表单事件名称类型（排除id项）
   */
  type FormEventName<T extends FormModelData = FormModelData> =
    `${Exclude<keyof T, "id">}-${FormEventType}`;

  /**
   * 表单常用事件类型（排除id项）
   * @param T 表单数据类型
   * @param ExcludeKeys 排除的键联合类型，生成不包含这些键的表单事件名称类型
   */
  type FormEvents<
    T extends FormModelData = FormModelData,
    ExcludeKeys extends keyof Omit<T, "id"> = never,
  > = Array<FormEventName<Omit<T, "id" | ExcludeKeys>>>;

  /**
   * 深层表单动态事件类型
   */
  type FormListeners = { [K in FormEventName]: Function };

  /**
   * 表单包含字段
   */
  type IncludeKeys<K extends FormModelData = FormModelData> = Array<keyof K>;
}

// 让 TypeScript 识别 .d.ts 文件
export {};
