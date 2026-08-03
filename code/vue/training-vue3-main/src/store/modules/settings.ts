import { store } from "@/store";
import defaultSettings from "@/settings";
import { ThemeEnum } from "@/enums/ThemeEnum";
import Color from "color";

type SettingsValue = boolean | string;
interface StartViewTransition extends Document {
  startViewTransition: (arg: any) => any;
}

export const useSettingsStore = defineStore("setting", () => {
  // 是否显示设置
  const settingsVisible = ref(false);
  const clickEvent = ref<MouseEvent | null>(null);
  // 是否显示标签视图
  const tagsView = useStorage<boolean>("tagsView", defaultSettings.tagsView);
  // 是否显示侧边栏logo
  const sidebarLogo = useStorage<boolean>(
    "sidebarLogo",
    defaultSettings.sidebarLogo
  );
  // 是否固定头部
  const fixedHeader = useStorage<boolean>(
    "fixedHeader",
    defaultSettings.fixedHeader
  );
  // 布局模式：left-左侧模式(默认) top-顶部模式 mix-混合模式
  const layout = useStorage<string>("layout", defaultSettings.layout);
  // 主题颜色
  const themeColor = useStorage<string>(
    "themeColor",
    defaultSettings.themeColor
  );
  // 主题：light-亮色(默认) dark-暗色
  const theme = useStorage<string>("theme", defaultSettings.theme);
  const themeAnimation = useStorage<boolean>(
    "themeAnimation",
    defaultSettings.themeAnimation
  );
  // 是否开启水印
  const watermarkEnabled = useStorage<boolean>(
    "watermarkEnabled",
    defaultSettings.watermarkEnabled
  );
  // 切换动画类型
  const transitionType = useStorage<TransitionType>(
    "transitionType",
    defaultSettings.transitionType
  );

  const isDark = computed(() => theme.value === ThemeEnum.DARK);

  // 监听主题颜色改变、浏览器亮暗色切换
  watch(
    [theme, themeColor],
    ([newTheme, newThemeColor], [oldTheme, oldThemeColor]) => {
      if (newTheme !== oldTheme) {
        setThemeAnimation(newTheme);
      }

      if (newThemeColor !== oldThemeColor) {
        const rootStyle = document.documentElement.style;
        rootStyle.setProperty(`--el-color-primary`, newThemeColor);
        rootStyle.setProperty(`--el-color-primary-dark-2`, newThemeColor);

        for (let i = 1; i < 10; i++) {
          rootStyle.setProperty(
            `--el-color-primary-light-${i}`,
            `${Color(newThemeColor).alpha(1 - i * 0.1)}`
          );
        }
      }
    },
    { immediate: true } // 立即执行，确保在侦听器创建时执行一次
  );

  const settingsMap: Record<string, Ref<SettingsValue>> = {
    fixedHeader,
    tagsView,
    sidebarLogo,
    layout,
    settingsVisible,
    transitionType,
    watermarkEnabled,
    themeAnimation,
  };

  // 改变设置，比如：侧边栏布局、主题颜色等等
  function changeSetting({
    key,
    value,
  }: {
    key: string;
    value: SettingsValue;
  }) {
    const setting = settingsMap[key];
    if (setting) {
      setting.value = value;
    }
  }

  function setThemeAnimation(val: string) {
    if (!themeAnimation.value) {
      setTheme(val);
      return;
    }

    const transition = (document as StartViewTransition).startViewTransition(
      () => setTheme(val)
    );

    if (!clickEvent.value) return;

    transition.ready.then(() => {
      const { clientX, clientY } = clickEvent.value!;
      const radius = Math.hypot(
        Math.max(clientX, innerWidth - clientX),
        Math.max(clientY, innerHeight - clientY)
      );

      const clipPath = [
        `circle(0 at ${clientX}px ${clientY}px)`,
        `circle(${radius}px at ${clientX}px ${clientY}px)`,
      ];

      document.documentElement.animate(
        { clipPath: isDark.value ? clipPath.reverse() : clipPath },
        {
          duration: 400,
          pseudoElement: isDark.value
            ? "::view-transition-old(root)"
            : "::view-transition-new(root)",
        }
      );
    });
  }

  function setClickEvent(e: MouseEvent) {
    clickEvent.value = e;
  }

  function setTheme(val: string) {
    if (val === ThemeEnum.DARK) {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
  }

  /**
   * 切换亮暗模式
   * @param val 亮暗模式：light-亮色 dark-暗色
   */
  function changeTheme(val: string) {
    theme.value = val;
  }

  /**
   * 切换主题颜色
   * @param color 主题颜色
   *
   */
  function changeThemeColor(color: string) {
    themeColor.value = color;
  }

  /**
   * 切换布局
   * @param val 布局模式：left-左侧模式 top-顶部模式 mix-混合模式
   */
  function changeLayout(val: string) {
    layout.value = val;
  }

  return {
    settingsVisible,
    tagsView,
    fixedHeader,
    sidebarLogo,
    layout,
    themeColor,
    isDark,
    theme,
    transitionType,
    watermarkEnabled,
    themeAnimation,
    setClickEvent,
    changeSetting,
    changeTheme,
    changeThemeColor,
    changeLayout,
  };
});

export function useSettingsStoreHook() {
  return useSettingsStore(store);
}
