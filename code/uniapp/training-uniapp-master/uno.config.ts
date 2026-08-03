// uno.config.ts
import {
  defineConfig,
  presetAttributify,
  presetIcons,
  presetTypography,
  presetUno,
  presetWebFonts,
  transformerDirectives,
  transformerVariantGroup,
} from "unocss";
import { presetUni } from "@uni-helper/unocss-preset-uni";
import presetWeapp from "unocss-preset-weapp";
import {
  transformerAttributify,
  transformerClass,
} from "unocss-preset-weapp/transformer";

const include = [/\.wxml$/];

export default defineConfig({
  content: {
    pipeline: {
      include,
    },
  },
  shortcuts: {
    "flex-center": "flex justify-center items-center",
    "flex-x-center": "flex justify-center",
    "flex-y-center": "flex items-center",
    "wh-full": "w-full h-full",
    "flex-x-between": "flex items-center justify-between",
    "flex-x-end": "flex items-center justify-end",
    "absolute-lt": "absolute left-0 top-0",
    "absolute-rt": "absolute right-0 top-0 ",
    "fixed-lt": "fixed left-0 top-0",
  },
  theme: {
    colors: {
      primary: "var(--el-color-primary)",
      primary_dark: "var(--el-color-primary-light-5)",
    },
  },
  presets: [
    presetUni(),
    presetUno(),
    presetAttributify(),
    presetIcons(),
    presetTypography(),
    presetWebFonts({
      fonts: {
        // ...
      },
    }),
    // presetWeapp({
    //   // prefix: "li-", // 可选前缀
    //   transform: true, // 启用转换
    //   whRpx: false, // 关闭宽度高度自动转换 rpx（建议手动处理）
    //   platform: "uniapp", // 指定平台
    // }),
  ],
  transformers: [
    transformerDirectives(),
    transformerVariantGroup(),
  ],
});
