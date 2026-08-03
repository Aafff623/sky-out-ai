import type { AxiosRequestConfig } from "axios";
import { ElTableColumn, ElPagination } from "element-plus";
import Pagination from "@/components/Pagination/index.vue";
import echarts from "@/plugins/echarts";

// 全局类型声明
declare global {
  /**
   * 平台的名称、版本、运行所需的`node`版本、依赖、构建时间的类型提示
   */
  declare const __APP_INFO__: {
    pkg: {
      name: string;
      version: string;
      engines: {
        node: string;
      };
      dependencies: Record<string, string>;
      devDependencies: Record<string, string>;
    };
    buildTimestamp: number;
  };

  /**
   * 页签对象
   */
  interface TagView {
    /** 页签名称 */
    name: string;
    /** 页签标题 */
    title: string;
    /** 页签路由路径 */
    path: string;
    /** 页签路由完整路径 */
    fullPath: string;
    /** 页签图标 */
    icon?: string;
    /** 是否固定页签 */
    affix?: boolean;
    /** 是否开启缓存 */
    keepAlive?: boolean;
    /** 路由查询参数 */
    query?: any;
  }

  /**
   * 系统设置
   */
  interface AppSettings {
    /** 系统标题 */
    title: string;
    /** 系统版本 */
    version: string;
    /** 是否显示设置 */
    showSettings: boolean;
    /** 是否固定头部 */
    fixedHeader: boolean;
    /** 是否显示多标签导航 */
    tagsView: boolean;
    /** 是否显示侧边栏Logo */
    sidebarLogo: boolean;
    /** 导航栏布局(left|top|mix) */
    layout: string;
    /** 主题颜色 */
    themeColor: string;
    /** 主题模式(dark|light) */
    theme: string;
    /** 亮暗色主题切换动画效果 */
    themeAnimation: boolean;
    /** 布局大小(default |large |small) */
    size: string;
    /** 语言( zh-cn| en) */
    language: string;
    /** 是否开启水印 */
    watermarkEnabled: boolean;
    /** 水印内容 */
    watermarkContent: string;
    /** 切换页面时的动画类型 */
    transitionType: TransitionType;
  }

  /** 切换页面时的动画类型 */
  type TransitionType = "fade" | "fade-slide" | "fade-scale" | "fade-rotate";

  /** 公共日期格式类型 */
  type CommonDateFormat =
    | "YYYY-MM-DD HH:mm:ss"
    | "YYYY-MM-DD"
    | "HH:mm:ss"
    | "MM-DD HH:mm"
    | "YYYY年MM月DD日";

  /** 将readonly类型转换为可写类型 */
  type Writable<T> = {
    -readonly [P in keyof T]: T[P];
  };

  /**
   * 提取函数参数元组中指定下标位置的类型
   */
  type ExtractParam<
    T extends (...args: any[]) => any,
    N extends number = 0,
  > = Parameters<T>[N];

  /** 提取数组类型中的元素类型 */
  type ExtractArrayItem<T extends any[] = any[]> =
    T extends Array<infer Item> ? Item : never;

  /**
   * 分页查询参数
   */
  interface PageQuery {
    /** 总条数 */
    total?: number;
    /** 当前页码 */
    page: number;
    /** 每页条数 */
    pageSize: number;
  }

  /**
   * 分页响应对象
   */
  interface PageResult<T extends any[] = any[]> {
    data: {
      /** 数据列表 */
      records: T;
      /** 总条数 */
      total: number;
    };
  }

  /**
   * 响应结果类型（code、data、msg）
   */
  interface ReqResultType<T = any> {
    /** 响应状态码 */
    code: number;
    /** 响应数据 */
    data: T;
    /** 响应消息 */
    msg: string;
  }

  /**
   * 响应数据V2（records、total），用于分页查询结合ReqResultType类型使用
   * @example
   *  ReqResultType<ResponseDataV2<XxxVO>>   records：对象数据，适用于单条数据查询
   *  ReqResultType<ResponseDataV2<XxxVO[]>>  records：数组数据，适用于分页
   */
  interface ResponseDataV2<T = any> {
    /** 数据列表 */
    records: T;
    /** 总条数 */
    total: number;
  }

  /**
   * 响应结果类型（code、data: { records: T; total: number }、msg）
   */
  type ResponseResult<T = any> = ReqResultType<ResponseDataV2<T>>;

  /**
   * 响应数据
   */
  interface ResponseData<T = any> {
    /** 响应状态码 */
    code: number;
    /** 响应数据 */
    data: {
      /** 数据列表 */
      records: T;
      /** 总条数 */
      total: number;
    };
    /** 响应消息 */
    msg: string;
  }

  /**
   * 响应数据公共字段
   */
  interface ResponseVOFields {
    /** 创建时间 */
    createTime?: Date;
    /** 更新时间 */
    updateTime?: Date;
    /** 创建用户id */
    createUser?: number;
    /** 更新用户id */
    updateUser?: number;
  }

  /**
   * 请求配置项
   */
  interface RequestOptions<T = any> extends AxiosRequestConfig<T> {}

  /**
   * dialog基础配置
   */
  interface DialogShowParams<T extends FormModelData = FormModelData> {
    /** 标题 */
    title?: string;
    /** 是否需要loading显示 */
    showLoading?: boolean;
    /** 请求url */
    url?: RequestOptions["url"];
    /** 请求方法 */
    method?: RequestOptions["method"];
    /** 携带数据 */
    payload?: T;
    /** 其他请求配置 */
    otherReqOptions?: RequestOptions;
  }

  /**  全局 echarts options 类型 */
  interface EChartsCoreOption extends echarts.EChartsCoreOption {}

  /**
   * 交错动画指令选择类型数据类型
   */
  interface StaggerOptions {
    /** 是否有动画效果 */
    isAnimate?: boolean;
    /** 动画类型 */
    type?: "fade" | "slide" | "scale";
    /** 延迟步长（秒） */
    delayStep?: number;
    /** 动画时长（秒） */
    duration?: number;
    /** 是否启用持久化(默认true) */
    persistent?: boolean;
  }
}

// 让 TypeScript 识别 .d.ts 文件
export {};
