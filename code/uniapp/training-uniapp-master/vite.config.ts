import { ConfigEnv, UserConfig, loadEnv } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import path from "path";

import AutoImport from "unplugin-auto-import/vite";
// TODO: css原子化
import UnoCSS from "unocss/vite";

// 获取src目录的绝对路径
const pathSrc = path.resolve(__dirname, "src");

// https://vitejs.dev/config/
export default async ({ mode }: ConfigEnv): Promise<UserConfig> => {
  // 读取.env文件中的环境变量，process.cwd()获取当前文件路径
  const env = loadEnv(mode, process.cwd());
  // const UnoCSS = (await import('unocss/vite')).default;

  return {
    // 路径别名配置
    resolve: {
      alias: {
        "@": pathSrc,
      },
    },
    server: {
      // 允许IP访问
      host: "0.0.0.0",
      // 应用端口 (默认:3000)
      port: Number(env.VITE_APP_PORT),
      // 运行是否自动打开浏览器
      open: false,
      proxy: {
        /**
         * 反向代理解决跨域配置
         * http://localhost:3000/dev-api/users (F12可见请求路径) => http://localhost:8989/users (实际请求后端 API 路径)
         * env.VITE_APP_BASE_API: /dev-api
         * env.VITE_APP_API_URL: http://localhost:8989
         */
        [env.VITE_APP_BASE_API]: {
          changeOrigin: true,
          target: env.VITE_APP_API_URL,
          rewrite: (path) =>
            path.replace(new RegExp("^" + env.VITE_APP_BASE_API), ""),
        },
      },
    },
    plugins: [
      UnoCSS(),
      uni(),
      AutoImport({
        imports: ["vue", "pinia"],
        // 是否在 vue 模板中自动导入
        vueTemplate: true,
        // 如有用到eslint记得加上写段，没有用到可以忽略
        eslintrc: {
          enabled: true,
          filepath: "./.eslintrc-auto-import.json",
          globalsPropValue: true,
        },
        dts: "src/types/auto-import.d.ts",
      }),
    ],
  };
};
