import { CSSProperties } from "vue";
import * as ElementPlus from "element-plus";
import Pagination from "@/components/Pagination/index.vue";

/**
   * 表格请求操作参数配置
   */
type RequestKeyValConfig<T = any> = {
  /** 请求参数键名 */
  keyName?: string | ((row: T) => string);
  /** 请求参数值名 */
  valueName?: string | ((row: T) => string);
}

// 全局table类型声明
declare global {
  /** 表格请求类型 */
  type TableRequestType = "post" | "get" | "put" | "delete";

  /** 表格tag配置 */
  type TagConfig = {
    /** tag类型map映射 */
    options: Record<
      string | number,
      {
        /** 标签文本 */
        text?: string | number;
        /** 自定义标签 \<i class="el-icon-xxx"></i> */
        icon?: string;
      } & InstanceType<typeof ElementPlus.ElTag>["$props"]
    >;
  };

  /** 表格action配置 */
  type ActionConfig<T = any> = {
    /** 按钮类型 */
    type?: "delete" | "startStop";  
    /** 按钮文本 */
    text?: string | ((row: T) => string);
    /** 请求地址（用于默认操作） */
    requestUrl?: string | ((row: T) => string);
    /** 请求方法（用于默认操作） */
    requestMethod?: TableRequestType = "post";
    /** 请求key-value参数配置 */
    requestKeyValConfig?: RequestKeyValConfig<T>;
    /** el-popconfirm配置 */
    popupConfirmConfig?: {
      /** el-popconfirm弹出框标题 */
      title: string | ((row: T) => string);
    }
    | InstanceType<typeof ElementPlus.ElPopconfirm>["$props"];
    /** el-link配置 */
    linkConfig?: {
      /** el-link是否可用 */
      disabled?: boolean | ((row: T) => boolean);
    } & InstanceType<typeof ElementPlus.ElLink>["$props"];
    /** 点击事件回调 */
    onClick?: (row: T, $index?: number, action?: ActionConfig) => void;
    /** el-popconfirm onConfirm回调 */
    onConfirm?: (row: T, $index?: number, action?: ActionConfig) => void;
  };

  /**
   * 表格table头部字段类型
   */
  type TableColumnData<T = any> = Partial<
    {
      /** 关联字段 */
      prop?: keyof T; // 提取指定类型，prop只能是string | undefined

      /** @deprecated 已废弃，判断条件（兼容之前的写法，待定删除）请使用slot字段 */
      text?: keyof T | "action";

      /**  表格插槽名称 */
      slot:
      | keyof T
      | "slot-image"
      | "slot-tag"
      | "slot-text"
      | "slot-action"
      | "action";

      /** 组件的样式 */
      style?: CSSProperties; 

      /** 是否显示列 */
      visible?: boolean; 

      /**  是否展开子项 */
      isExpand?: boolean;

      /** 特殊符号配置 */
      symbol?: {
        /**
         * 文本前缀符号
         * @example prefix: '￥'
         */
        prefix?: string;
        /**
         * 文本后缀符号
         * @example suffix: '元'
         */
        suffix?: string;
      };

      /**  表格图片配置 */
      imageConfig?: {
        /** 图片宽度 */
        size?: number | string;
      } & InstanceType<typeof ElementPlus.ElImage>["$props"];

      /**
       * 标签列配置
       * @example 配置示例
       *  tagConfig: {
       *     0: { type: 'danger', text: '已下架', icon: 'ep-remove' },
       *     1: { type: 'success', text: '售卖中', icon: 'ep-check' },
       *     2: { type: 'warning', text: '库存预警', effect: 'dark' }
       *   }
       */
      tagConfig?: TagConfig;

      /**
       * 文本映射配置
       * @example 配置示例
       * textConfig: {
       *   [StatusEnum.ENABLE]: '启用',
       *   [StatusEnum.DISABLE]: '禁用'
       * }
       */
      textConfig?: Record<string | number, string | number>;

      actionConfig?: {
        /** 表格操作列选项配置 */
        options?: ActionConfig<T>[];
      };

      /** 子项 column 列配置（注意此处不能使用字段名叫`children`，控制台会打印警告，无语😅） */
      childColumns?: TableColumnData<T>[];
    } & InstanceType<typeof ElementPlus.ElTableColumn>
  >;

  /**
   * 表格table数据类型
   */
  interface TableData<T extends any[] = any[], U = ExtractArrayItem<T>> {
    /** 分页结果响应数据 */
    data: T;
    /** 表格列配置 */
    columns: Array<TableColumnData<U>>;
    /**
     * 组件Pagination和ElPagination属性的并集
     * @description 注意每页条数请使用`limit`字段，当前页面使用`page`
    */
    pagination?: PaginationProps | ((data: T) => PaginationProps);
  }

  /** 分页属性配置类型 */
  type PaginationProps = Partial<
    InstanceType<typeof Pagination>["$props"] &
    Omit<InstanceType<typeof ElementPlus.ElPagination>["$props"], "currentPage" | "pageSize">
  >;

  type ComponentProps<C extends Component> = C extends new (...args: any[]) => infer InstanceType
  ? InstanceType extends { $props: infer PropsType }
    ? PropsType
      : never
  : never;

  type TableColumn<C extends Component> = {
    component: C;
    props: ComponentProps<C>;
  };

  /**
   * 分页结果响应的table表格数据类型
   */
  type TableResult<T> =
    ReturnType<T> extends Promise<PageResult<infer R>>
    ? R extends Array<infer Item>
    ? TableData<R, Item> // 自动推断table columns列的类型
    : TableData<R>
    : never;

    /**
     * 表格请求配置类型
     */
    type TableRequestConfig<T = any> = {
      /** 查询参数 */
      queryParams?: T,

      /** 请求配置 */
      requestConfig?: RequestOptions<T>;

      /** 页码参数 */
      pageQueryParam?: PageQuery;
    }
}

export { }; 
