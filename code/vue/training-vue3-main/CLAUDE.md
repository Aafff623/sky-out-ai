# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

「锦食坊」外卖**商家管理端**。Vue 3 + TypeScript + Vite 5 + Element Plus + Pinia + ECharts + UnoCSS。工作区整体规范见项目根目录 `CLAUDE.md`。

## 命令
```bash
npm install
npm run dev            # 开发（端口 9527，proxy /dev-api → http://localhost:8848）
npm run build:prod     # 生产构建 + vue-tsc 类型检查
npm run lint:eslint / lint:stylelint / lint:prettier
npm run commit         # git-cz 规范提交（husky + commitlint）
```
Node >= 18。联调需先起后端（localhost:8848）。无单元测试脚本。

## 目录结构
- `src/api/*.ts`：接口层，模式为 `const BASE_URL` + `class XxxAPI { static ... }` + default export，统一走 `utils/request.ts`。
- `src/views/`：页面模块 —— workbench 工作台 / statistics 统计报表（ECharts 4 张 + xlsx 导出）/ dish 菜品 / setmeal 套餐 / order 订单（状态 tab 管理）/ category 分类 / employee 员工 / comment 评论 / knowledge-base 知识库 / customer-service 客服 / sensitive 敏感词 / shop 店铺 / login。
- `src/store/modules`：user（登录态）/ app（设备/语言/营业状态）/ settings（布局/主题）/ tagsView。
- `src/router`：hash 路由，`router.config.ts` 的 constantRoutes + Layout 懒加载，meta 含 title/icon/keepAlive。
- `src/layout`：AppMain / NavBar / Sidebar / TagsView 布局组件。
- `src/plugins`：permission（路由守卫）、index（按序注册 store→router→i18n→权限→echarts→指令）。

## 关键约定
- **请求封装** `utils/request.ts`：baseURL 取 `VITE_APP_BASE_API`，超时 50s；请求头注入 `headers.token`（localStorage `token`）；响应 `code===200` 返回 data，`401` 防抖重置 token 并刷新页面；支持 blob 导出下载（统计报表）。
- **登录**：默认账号 admin/123456；token 存 localStorage key `token`（见 `enums/CacheEnum.ts`）；路由守卫仅按 token 存在与否放行，白名单 `/login`（无细粒度角色校验）。
- **环境**：`.env.development`（PORT 9527、`/dev-api`、`http://localhost:8848`）/ `.env.production`（PORT 3000、`/prod-api`、`http://118.195.133.79`）。
- **代码规范**：Prettier 双引号 + 分号（printWidth 80）；ESLint 已放宽（no-explicit-any、no-unused-vars 关闭）；提交信息走 Conventional Commit。

## 注意
- `views/customer-service/` 客服页 WebSocket 地址**硬编码** `ws://localhost:8848/ws/customer-service/{sessionId}/staff`，且接口里硬编码 `http://localhost:8848`；改后端地址需同步改此处。
- `knowledge-base` 页面：分页管理 + multipart 上传走 `/embed` 接口做向量化入库。
- 主题/布局偏好集中在 `src/settings.ts` 与 `uno.config.ts`。
