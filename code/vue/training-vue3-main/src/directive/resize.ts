import { Directive, DirectiveBinding } from "vue";
import { throttle, debounce } from "lodash-es";

const map = new WeakMap<HTMLElement, ResizeValueType>();

// 元素尺寸变化观察器
const ob = new ResizeObserver((entries: ResizeObserverEntry[]) => {
  for (const entry of entries) {
    const record = map.get(entry.target as HTMLElement);
    if (!record) continue;

    const size = {
      width: entry.borderBoxSize[0].inlineSize,
      height: entry.borderBoxSize[0].blockSize,
    };

    // 直接调用已包装的函数
    record.cleanup();
    record.cleanup = () => {}; // 防止二次调用
    record.handler(size);
  }
});

/**
 * 监控元素尺寸变化指令
 * @example v-resize="handleResize"
 * @example v-resize="{ handler: handleResize, options: { throttleTime: 500 } }"
 */
export const resize: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { handler, options } = parseBinding(binding);
    const wrappedHandler = getWrappedHandler(handler, options);

    const cleanup = () => {
      options?.debounceTime && wrappedHandler.cancel?.();
    };

    map.set(el, { handler: wrappedHandler, cleanup });
    ob.observe(el);
  },
  unmounted(el: HTMLElement) {
    const record = map.get(el);
    record?.cleanup();
    ob.unobserve(el);
    map.delete(el);
  },
};

// 包装处理函数
function getWrappedHandler(
  handler: ResizeHandler,
  options?: ResizeOptions
): ReturnType<typeof debounce | typeof throttle> {
  if (options?.debounceTime) {
    return debounce(handler, options.debounceTime, {
      leading: options.leading,
      maxWait: options?.maxWait,
      trailing: options.trailing !== false,
      ...options,
    });
  }

  return throttle(handler, options?.throttleTime ?? 200, {
    leading: options?.leading,
    trailing: options?.trailing !== false,
    ...options,
  });
}

// 解析绑定值
function parseBinding(
  binding: DirectiveBinding<ResizeHandler | ResizeDirectiveValue>
) {
  if (typeof binding.value === "function") {
    return { handler: binding.value };
  }

  return binding.value;
}
