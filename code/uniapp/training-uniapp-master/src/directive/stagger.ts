import type { Directive, DirectiveBinding } from "vue";

const animationMap = new WeakMap<HTMLElement, boolean>();

/**
 * stagger 指令
 * @example 使用方式
 * v-stagger:[index]="{ 
      type: 'slide',  // 动画类型
      delayStep: 0.3, // 延迟步长（秒）
      duration: 0.8,  // 动画时长（秒）
      persistent: true,  // 是否启用持久化
    }"
*/
export const stagger: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<number | StaggerOptions>) {
    const options: StaggerOptions =
      typeof binding.value === "number"
        ? { delayStep: binding.value }
        : binding.value || {};

    const {
      isAnimate = true,
      type = "slide",
      delayStep = 0.2,
      duration = 0.6,
      persistent = true,
    } = options;

    // 禁用动画效果
    if (!isAnimate) return;

    const index = binding.arg ? parseInt(binding.arg) : 0;
    const delay = index * delayStep;

    // 持久化检查
    if (persistent && animationMap.has(el)) return;

    // 设置CSS变量
    el.style.setProperty("--stagger-delay", `${delay}s`);
    el.style.setProperty("--stagger-duration", `${duration}s`);

    // 添加动画类
    el.classList.add(`stagger-${type}`, "stagger-item");

    // 标记动画状态
    const onAnimationEnd = () => {
      if (persistent) animationMap.set(el, true);
      el.classList.remove(`stagger-${type}`, "stagger-item");
      el.removeEventListener("animationend", onAnimationEnd);
    };

    el.addEventListener("animationend", onAnimationEnd);
  },
  unmounted(el: HTMLElement) {
    el.classList.remove(
      "stagger-item",
      "stagger-fade",
      "stagger-slide",
      "stagger-scale"
    );
  },
};
