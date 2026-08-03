import { useRequest } from "./useRequest";

/** 自定义映射配置 */
export interface MapConfig {
  labelKey?: string;
  valueKey?: string;
}

/**
 * 封装根据请求结果更新字段的 options
 * @param requestFn 与 useRequest 一致的请求函数，接收任意参数并返回 Promise
 * @param fields 响应式字段数组（ref 类型），类型为 Fields
 * @param fieldProp 要更新的字段 prop 名称，限制为 fields 数组中存在的 prop
 * @param mapConfig 配置如何从返回记录映射出 options 的 label/value 字段，默认 { labelKey:"name", valueKey:"id" }
 * @param args 请求参数（传递给 requestFn）
 * @example
 * useOptions(
      () =>
        CategoryAPI.getPage({
          page: 1,
          pageSize: 1000,
        }),
      fields,
      "categoryId"
    );
 */
export async function useOptions<T, Args extends any[], F extends Field["prop"]>(
  requestFn: (...args: Args) => Promise<T>,
  fields: Ref<Fields>,
  fieldProp: F,
  mapConfig: MapConfig = { labelKey: "name", valueKey: "id" },
  ...args: Args
): Promise<void> {
  const getIndex = () => fields.value.findIndex((item) => item.prop === fieldProp);

  const getFieldItem = (): Field => {
    const index = getIndex();
    if (index === -1) {
      throw new Error(`useOptions: 未找到 prop 为 '${fieldProp}' 的字段项`);
    }
    return fields.value[index];
  };

  // 重置当前字段的 options，优先处理 childProps，再处理 props
  const resetOptions = (): void => {
    const field = getFieldItem();
    if (field.childProps) {
      field.childProps.options = [];
    } else if (field.props) {
      field.props.options = [];
    }
  };

  // 每次更新前重置 options
  resetOptions();

  try {
    const res = await useRequest<T, Args>(requestFn, "请求失败", ...args);

    const records = (res as any).data?.records || [];
    const options = records.map((item: any) => ({
      label: item[mapConfig.labelKey || "name"],
      value: item[mapConfig.valueKey || "id"],
    }));

    // 更新对应字段的 options，优先 childProps，其次 props
    const field = getFieldItem();
    if (field.childProps) {
      field.childProps.options = options;
    } else if (field.props) {
      field.props.options = options;
    }
  } catch (error) {
    // 此处可添加统一错误处理逻辑
    throw error;
  }
}