// 全局类型声明
declare global {
  /**
   * 分页查询参数
   */
  interface PageQuery {
    page: number;
    pageSize: number;
  }

  /**
   * 分页响应对象
   */
  interface PageResult<T> {
    /** 数据列表 */
    records: Array<T>;
    /** 总条数 */
    total: number;
  }

  /**
   * 响应数据
   */
  interface ResponseData<T = any> {
    /** 响应状态码 */
    code: number;
    /** 响应数据 */
    data: T;
    /** 响应消息 */
    msg: string;
  }

  /**
   * 响应数据公共字段
   */
  interface ResponseVOCommonFields {
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
  interface ReqOptionsConfig {
    /** 是否自动提示错误 */
    isPrompt?: boolean;
    /** 是否显示加载动画 */
    load?: boolean;
    /** 是否使用数据工厂 */
    isFactory?: boolean;
    /** 重新请求次数 */
    resend?: number;
  }

  /**
   * 地址解析数据
   */
  interface AddressParseData {
    /** 收货人姓名 */
    name?: string;
    /** 手机号码 */
    mobile?: string;
    /** 省份 */
    province?: string;
    /** 城市 */
    city?: string;
    /** 地区 */
    area?: string;
    /** 详细地址 */
    details?: string;
  }

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
