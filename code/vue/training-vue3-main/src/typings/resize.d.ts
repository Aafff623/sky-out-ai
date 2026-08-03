import { type DebounceSettings, type ThrottleSettings } from "lodash-es";

declare global {
  /**  */
  type ResizeCallbackParams = { width: number; height: number };

  /** 尺寸变化后回调函数 */
  type ResizeHandler = (sizes: ResizeCallbackParams) => void;

  type ResizeOptions = {
    /** 节流时间（毫秒），默认200ms */
    throttleTime?: number;
    /** 防抖时间（毫秒），优先级高于节流 */
    debounceTime?: number;
  } & DebounceSettings &
    ThrottleSettings;

  type ResizeDirectiveValue = {
    handler: ResizeHandler;
    options?: ResizeOptions;
  };

  /** 保存到weakmap中值的类型 */
  type ResizeValueType = {
    handler: ResizeHandler;
    cleanup: () => void;
  };
}

export {};
