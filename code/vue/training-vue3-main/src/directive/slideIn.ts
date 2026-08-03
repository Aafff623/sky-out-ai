import { Directive, DirectiveBinding } from "vue";

type AnimationData = {
  animation: Animation;
  once: boolean;
};

const DISTANCE = 150;
const DURATION = 500;
const EASING = "cubic-bezier(0.25, 0.1, 0.25, 1)";
const animationMap = new WeakMap<HTMLElement, AnimationData>();

const observer = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (!entry.isIntersecting) return;

    const el = entry.target as HTMLElement;
    const data = animationMap.get(el);
    if (!data) return;

    // 重置并播放动画
    data.animation.currentTime = 0;
    data.animation.play();

    // 如果配置了只执行一次，取消观察
    if (data.once) {
      observer.unobserve(el);
      animationMap.delete(el);
    }
  });
});

// 判断元素是否在视窗下方
function isBelowViewport(el: HTMLElement) {
  const rect = el.getBoundingClientRect();
  return rect.top - DISTANCE > window.innerHeight;
}

/**
 * 平滑上升动画指令
 * 使用方式：
 * - 默认每次进入视口都触发: v-slide-in
 * - 只触发一次: v-slide-in.once
 */
export const slideIn: Directive<HTMLElement> = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    if (isBelowViewport(el)) return;

    const once = binding.modifiers.once ?? false;

    // 创建动画并暂停
    const animation = el.animate(
      [
        { transform: `translateY(${DISTANCE}px)`, opacity: 0.5 },
        { transform: "translateY(0)", opacity: 1 },
      ],
      {
        duration: DURATION,
        easing: EASING,
        fill: "both",
      }
    );
    animation.pause();
    animationMap.set(el, { animation, once });
    observer.observe(el);
  },

  unmounted(el) {
    const data = animationMap.get(el);
    if (data) {
      data.animation.cancel();
      animationMap.delete(el);
    }
    observer.unobserve(el);
  },
} as const;
