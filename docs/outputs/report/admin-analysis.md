# 「锦食坊」商家管理端（training-vue3-main）深度源码分析

> 分析范围：`D:\OneDrive\Desktop\sky\sky-ai\code\vue\training-vue3-main`（已排除 `node_modules` / `dist`）
> 技术栈核实：Vue 3.4.21 + TypeScript 5.2 + Vite 5.2 + Element Plus 2.7.3 + Pinia 2.1.7 + ECharts 5.5.1 + UnoCSS + vue-i18n
> 说明：本文为只读静态分析，未修改任何源文件。

## 0. 与需求速览的两处关键修正

1. **ECharts 图表不在「工作台」，而在「数据统计」页**。工作台 `views/workbench/index.vue` 用的是 `el-statistic` 卡片 + `BaseTable`，**不含任何 ECharts**；4 张 ECharts（营业额 / 新增用户 / 订单 / 销量 TOP10）全部在 `views/statistics/index.vue` + `hook/useStatisticsConfig.ts`。
2. **订单管理无「导出」功能**。订单页只有流转（tab 状态）+ 分页 + 搜索；`xlsx` 导出（`StatisticsAPI.exportReport`）在「数据统计」页。

---

## 1. 工程结构

### 1.1 package.json（`package.json`）

- **名称**：`锦食坊`，`version: 1.0.0`，`type: module`，`engines.node >= 18`。
- **脚本**：

  | 脚本 | 内容 |
  |---|---|
  | `dev` | `vite serve --mode development`（端口 9527） |
  | `build:prod` | `vite build --mode production && vue-tsc --noEmit` |
  | `preview` | `vite preview` |
  | `lint:eslint` / `lint:stylelint` / `lint:prettier` | 三件套，均带 `--fix` |
  | `lint:lint-staged` | `lint-staged` |
  | `prepare` | `husky install` |
  | `commit` | `git-cz`（配置 `cz-git`） |

- **核心依赖**：`vue 3.4.21`、`vue-router 4.0.13`（**较旧**）、`pinia 2.1.7`、`element-plus 2.7.3`、`@element-plus/icons-vue 2.3.1`、`echarts 5.5.1`、`axios 1.7.1`、`vue-i18n 9.13.1`、`@vueuse/core 10.9.0`、`qs`、`nprogress`、`color`、`animate.css`、`path-browserify`、`path-to-regexp`、`websocket 1.0.35`（**声明为依赖但未被实际使用**）。
  - ⚠️ `lint-staged` 被放在 `dependencies` 而非 `devDependencies`（`package.json:51`）。
- **开发依赖**：`vite 5.2`、`typescript 5.2`、`vue-tsc 2.0.6`、`eslint 8.57`、`stylelint 16.8`、`prettier 3.2.5`、`unocss 0.60`、`unplugin-auto-import/components/icons/vue-define-options`、`husky 8`、`@commitlint/* 19`、`cz-git`、`vite-plugin-svg-icons`。

### 1.2 vite.config.ts（`vite.config.ts`）

- **别名**：`@` → `src`。
- **SCSS**：`additionalData` 全局注入 `@/styles/mixins.scss`、`@/styles/variables.scss`。
- **server**：`host: 0.0.0.0`，`port: VITE_APP_PORT`（9527），`open: false`。
- **proxy**：`[VITE_APP_BASE_API]` → `target: VITE_APP_API_URL`，`rewrite` 去掉前缀 `/dev-api`。注释里写的 8989 是历史残留，实际指向 `.env` 的 8848。
- **插件**：`vue()`、`DefineOptions()`、`UnoCSS()`、`AutoImport`（Vue / Pinia / Router / i18n / EP 函数自动导入 + `IconsResolver`）、`Components`（`ElementPlusResolver` + `IconsResolver`，生成 `src/typings/components.d.ts`）、`Icons`、`createSvgIconsPlugin`（`src/assets/icons`，symbolId `icon-[dir]-[name]`）。
- **optimizeDeps**：硬编码了一长串 EP 组件 css（约 60 项，冗余但无害）。
- **build**：`chunkSizeWarningLimit: 2000`，`minify: terser`，`drop_console: true` / `drop_debugger: true`，输出命名 `js/[name].[hash].js`，资源分类 `media/img/fonts`。
- **define**：注入 `__APP_INFO__`（pkg 元信息 + buildTimestamp）。

### 1.3 src/ 目录树（关键）

```
src/
├─ api/            13 个：category/comment/commentRepay/dish/employee/knowledgeBase/
│                  order/sensitive/setmeal/shop/statistics/upload/workspace
├─ assets/         icons(约40个svg)/images/logo/avatar/backtop
├─ components/     AppLink、Breadcrumb、Hamburger、LangSelect、Pagination、
│                  SelectImage、SizeSelect、SvgIcon、
│                  Base/{ButtonGroup,Card,Dialog,Echarts,Empty,Form,FormDialog,Popconfirm,Table}
├─ directive/      index/resize/slideIn/stagger
├─ enums/          CacheEnum/DeviceEnum/EmployeeEnum/LanguageEnum/LayoutEnum/
│                  MessageTypeEnum/OrderEnum/PayEnum/SidebarStatusEnum/SizeEnum/
│                  StatusEnum/ThemeEnum
├─ hooks/          useEcharts/useOptions/useRequest/useSocket
├─ lang/           index.ts + package/{en,zh-cn}.ts
├─ layout/         index.vue + components/{AppMain,NavBar,Sidebar,TagsView,Settings}
├─ plugins/        index/echarts/icons/permission
├─ router/         index.ts + router.config.ts
├─ store/          index.ts + modules/{user,app,settings,tagsView}
├─ styles/         dark/element/index/mixins/reset/stagger-animation/transition/variables*
├─ typings/        auto-imports.d.ts/base-components/components/env/form/global/
│                  resize/router/table/websocket
├─ utils/          audio/echarts-utils/i18n/index/nprogress/request/table/websocket
└─ views/          workbench/statistics/order/dish/setmeal/category/comment/
                   sensitive/knowledge-base/customer-service/employee/
                   login/error-page/{401,404}/redirect
```

---

## 2. 路由与页面盘点

### 2.1 router 配置

- `src/router/index.ts`：`createWebHashHistory()`（**hash 模式** ✓），`scrollBehavior` 置顶；导出 `resetRouter()`（`replace('/login')` + `location.reload()`）。
- `src/router/router.config.ts`：全部路由为 `constantRoutes`，**无动态路由 / 角色过滤 / 异步权限菜单**。`Layout = () => import('@/layout/index.vue')` 懒加载；每个子页面 `() => import(...)` 懒加载；`meta` 含 `title/icon/keepAlive/affix/hidden`。
- **无 404 通配路由**（`/:pathMatch(.*)` 不存在），未匹配路由在守卫里兜底。
- **守卫** `src/plugins/permission.ts`：
  - 白名单仅 `['/login']`；
  - 仅按 `localStorage.getItem(TOKEN_KEY)` 存在与否放行，**无细粒度角色 / 权限校验**；
  - `to.matched.length === 0` → 跳 404；`title` 可由 `params.title` / `query.title` 覆盖；
  - `NProgress` 进度条在 before / afterEach 控制。

### 2.2 视图职责一览

| 视图 | 路径 | 职责（一句话） | Element Plus 组件 | ECharts |
|---|---|---|---|---|
| workbench | `views/workbench/index.vue` | 今日数据 + 订单概览 + 菜品/套餐总览 + 待处理订单 | `el-statistic`/`el-card`/`el-row`/`el-radio-button` | ❌ 无（用 `el-statistic`） |
| statistics | `views/statistics/index.vue` | 时间范围切换 + 4 张报表 + xlsx 导出 | `el-radio-group`/`el-button`/`el-row` | ✅ 4 张（折线/折线/折线+graphic/柱状） |
| order | `views/order/index.vue` | 订单状态 tab 流转 + 搜索 + 分页 + 接单/拒单/派送/完成/取消/出餐 | `el-tabs`/`el-table`/`el-form`/`el-dialog`/`el-drawer`/`el-date-picker` | ❌ |
| dish | `views/dish/index.vue` | 菜品 CRUD + 口味配置 + 图片上传 | `el-table`/`el-dialog`/`el-select`/`el-upload` | ❌ |
| setmeal | `views/setmeal/index.vue` | 套餐 CRUD + 套餐菜品选择 | `BaseTable`/`BaseForm`/`BaseDialog`/`BaseButtonGroup` | ❌ |
| category | `views/category/index.vue` | 分类 CRUD + 启停 | `el-table`/`el-tag`/`el-dialog` | ❌ |
| employee | `views/employee/index.vue` | 员工 CRUD + 启停 + 头像上传 | `BaseTable`/`el-dialog`/`el-upload` | ❌ |
| comment | `views/comment/index.vue` | 评论分页 + 批量删除 + 详情回复树 | `el-table`/`el-tooltip`/`el-dialog` | ❌ |
| sensitive | `views/sensitive/index.vue` | 敏感词 CRUD | `el-table`/`el-dialog` | ❌ |
| knowledge-base | `views/knowledge-base/index.vue` | 知识库上传(embed)/下载/删除 | `el-upload`/`el-table` | ❌ |
| customer-service | `views/customer-service/index.vue` | 会话列表 + WebSocket 人工客服 | `el-avatar`/`el-input`/`el-empty`/`el-button` | ❌ |
| login | `views/login/index.vue` | 登录（预填 admin/123456） | `el-form`/`el-input` | ❌ |
| 401/404 | `views/error-page/*.vue` | 错误页 | `el-button` | ❌ |
| redirect | `views/redirect/index.vue` | 重定向中转 | — | ❌ |

---

## 3. API 层盘点

### 3.1 模式

统一 `const Xxx_BASE_URL = "..."` + `class XxxAPI { static ... }` + `export default`。请求统一走 `src/utils/request.ts`（axios 实例）。BASE_URL 拼接规则：`import.meta.env.VITE_APP_BASE_API`（`/dev-api`）作为 axios `baseURL`，API 类里的 URL 是相对路径，开发时经 vite proxy 转发到 `http://localhost:8848`（去掉 `/dev-api` 前缀）。

### 3.2 request.ts 拦截器逻辑（`src/utils/request.ts`）

- **baseURL**：`VITE_APP_BASE_API`；**timeout**：`50000`（50s）；`Content-Type: application/json;charset=utf-8`；`paramsSerializer` 用 `qs.stringify`。
- **请求拦截**：读 `localStorage.getItem(TOKEN_KEY)`（key = `token`），存在则 `config.headers.token = accessToken`。
- **响应拦截（成功）**：
  - `responseType === 'blob'` 时先判 content-type，若为 `application/json` 说明是错误响应，用 `FileReader` 读出 msg 并 `ElMessage.error` 后 reject；
  - 否则解构 `{ code, msg }`，`code === 200` → **返回 `response.data`（整个 body）**，否则 `ElMessage.error` + `reject(new Error(msg))`。
- **响应拦截（错误）**：`if (error.response.data)` 解构 `{ code, msg }`，`code === 401` → `debounceFn()`（500ms 防抖：`ElNotification` 提示「登录失效」→ `resetToken()` → `location.reload()`）；其它 → `ElMessage.error`；最后 `reject(error.message)`。
- ⚠️ **三个缺陷**（详见 §7）：
  1. 错误分支 `error.response.data` 无 `error.response` 判空，网络错误 / 超时 / CORS 会直接抛 `TypeError`；
  2. `reject(error.message)` 丢失结构化信息，调用方 `.catch(err => err.msg)` 恒为 `undefined`；
  3. `ElMessage` 靠 AutoImport 未显式 import（`ElNotification` 才显式 import）。

### 3.3 API 类与端点

| 文件 | BASE_URL | 方法 → 端点 |
|---|---|---|
| employee.ts | `/store/employee` | `getInfo`→`/me`、`login`→`/login`(POST)、`add`→``(POST)、`getPage`→`/page`、`startOrStop`→`/status`(POST, params id/status)、`update`→``(PUT)、`deleteById`→``(DELETE, params id)、`logout`→`/logout`(POST) |
| order.ts | `/store/order` | `conditionSearch`→`/conditionSearch`(GET)、`details`→`/details`(GET, params id)、`confirm`→`/confirm/{id}`(PUT)、`rejection`→`/rejection`(PUT, body)、`cancel`→`/cancel`(PUT, body)、`delivery`→`/delivery/{id}`(PUT)、`complete`→`/complete/{id}`(PUT) |
| dish.ts | `/store/dish` | `add`→``(POST)、`getPage`→`/page`、`batchDelete`→``(DELETE, params ids)、`getByIdWithFlavor`→``(GET, params id)、`update`→``(PUT)、`startOrStop`→`/status/{status}`(POST, params id) |
| setmeal.ts | `/store/setmeal` | `add`/`getPage`(`/page`)/`batchDelete`/`getById`/`update`/`startOrStop`(`/status/{status}`) |
| category.ts | `/store/category` | `add`/`update`/`getPage`(`/page`)/`startOrStop`(`/status`)/`getByType`(`/list`)/`deleteById` |
| comment.ts | `/store/comment` | `pageQuery`(`/page`)、`getById`(`/{id}`)、`deleteByIds`(DELETE, params ids) |
| commentRepay.ts | `/store/comment-repay` | `deleteCommentRepay`(DELETE, params ids)、`addCommentRepay`(`/save`, POST) |
| knowledgeBase.ts | `/store/knowledgeBase` | `pageQuery`(`/page`)、`addKnowledgeBase`(`/embed`, POST multipart)、`deleteByIds`(`/delete`, params ids) |
| sensitive.ts | `/sensitive`（**无 /store 前缀**） | `addSensitive`(POST, params)、`updateSensitive`(`/{id}`, PUT, params)、`page`(GET)、`delete`(DELETE, params ids) |
| shop.ts | `/store/shop` | `status`(`/status/{status}`, PUT)、`getStatus`(`/status`, GET) |
| statistics.ts | `/store/report` | `turnoverStatistics`/`userStatistics`/`ordersStatistics`/`top10`(GET, params begin/end)、`exportReport`(`/export`, GET, blob) |
| upload.ts | `/upload` | `upload`(POST multipart) |
| workspace.ts | `/store/workspace` | `businessData`(`/businessData`)、`orderOverView`(`/overviewOrders`)、`dishOverView`(`/overviewDishes`)、`setmealOverView`(`/overviewSetmeals`) |

---

## 4. 状态管理（Pinia，`src/store/modules/`）

| Store | state | actions / 说明 |
|---|---|---|
| `user` | 无持久 state（userInfo 被注释） | `login`(存 token 到 localStorage `token`)、`logout`(清 token + employeeName + reload)、`resetToken`(置空 token + `resetRouter`)、`getInfo`(存 name 到 `employeeName`) |
| `app` | `device/size/language/sidebarStatus/shopStatus/activeTopMenuPath`（均 `useStorage` 持久化）；`sidebar` reactive | `getShopStatus`、`setShopStatus`(调 ShopAPI，营业状态切换)、`toggleSidebar/closeSideBar/openSideBar`、`toggleDevice`、`changeSize/changeLanguage`、`activeTopMenu` |
| `settings` | `settingsVisible/tagsView/sidebarLogo/fixedHeader/layout/themeColor/theme/themeAnimation/watermarkEnabled/transitionType`（`useStorage`） | `changeSetting`、`setTheme`(切 dark class)、`changeTheme/changeThemeColor/changeLayout`、View Transition 动画 |
| `tagsView` | `visitedViews/cachedViews` | 多标签页增删 / 缓存管理（addView / delView / delOtherViews / delLeft/Right/AllViews / closeCurrentView 等） |

> 注：token 实际存 **localStorage**（非 Pinia），key 为 `token`（`enums/CacheEnum.ts`）；员工姓名存 `employeeName`。Pinia 用于 UI 状态，认证态本质上是 localStorage 直读。

---

## 5. 关键业务页面深挖

### 5.1 订单管理（`views/order/`）

- **流转**：`OrderEnum`（`enums/OrderEnum.ts`）定义 8 态：`ALL=""`、待付款 1、待接单 2、已接单 3、派送中 4、已完成 5、已取消 6、退款 7、**待出餐 8（堂食新增）**。tab 用 `el-tabs` + 7 个 `views/order/modules/*.vue` 动态组件（`markRaw` 防过度响应式），每态渲染不同操作列（接单 / 拒单 / 派送 / 完成 / 取消 / 出餐完成 / 查看）。
- **分页**：`pageSize: 6`，`OrderTable.vue` + 通用 `Pagination` 组件，`useVModel` 双向绑定 `total/table/queryParams`。
- **搜索**：订单号 / 手机号关键词 + 下单时间范围（`el-date-picker` 响应式适配移动端）。`keyword` computed 用 `Object.keys(queryParams).find(k => k === keywordSelect)` 定位字段（写法绕但可工作）。
- **导出**：**无**（导出在 statistics）。
- **详情 / 拒单 / 取消**：`OrderModel.vue`（el-dialog 订单信息折叠面板）+ `ReasonModel.vue`（el-drawer 填写原因，校验 `cancelReason/rejectionReason`）。
- **工作台联动**：工作台 `DataTable.vue` 复用 `OrderAPI.baseUrls.conditionSearch`，跳转带 `?id=&status=` 实现行高亮定位。

### 5.2 菜品 / 套餐 / 分类 CRUD

- **菜品** `views/dish/`：`AddForm.vue`/`EditForm.vue` 内嵌 `BaseTable` 做口味（甜味 / 温度 / 忌口 / 辣度）多选，提交时 `JSON.stringify` 口味值；图片用 `SelectImage`（自定义 `el-upload` + `http-request` 走 `UploadAPI`，也支持「使用链接」）。
- **套餐** `views/setmeal/`：`DialogForm.vue` 用**配置驱动表单**（`BaseFormDialog` + `fields` 数组），套餐菜品选择 `AddDishDialog.vue`（左侧分类 tab + 菜品卡片，右「已选菜品」过渡动画）。
- **分类** `views/category/`：标准 CRUD 弹窗，新增默认禁用。
- ⚠️ 启停逻辑普遍用 `code.toString().startsWith("2")` 判断成功（见 §7）。

### 5.3 工作台 / 数据看板

- **工作台**（`views/workbench/index.vue`）：`Promise.all` 拉 4 个 workspace 接口；今日数据用 `el-statistic` 卡片（营业额 / 有效订单 / 完成率 / 客单价 / 新增用户），订单 / 菜品 / 套餐总览是可点击 `router-link` 卡片，订单信息用 `BaseTable`。**无 ECharts**。
- **数据统计**（`views/statistics/`）：`useStatisticsConfig` 管理时间范围（近 7 日 / 近 30 日 / 昨日 / 本周 / 本月），`transformData` 生成 4 组 ECharts option（营业额折线、新增用户折线、订单折线 + graphic 文案「订单完成率 / 有效订单 / 订单总数」、TOP10 横向柱状图）。导出 `exportReport` 返回 blob → `URL.createObjectURL` + `<a download>` 下载 `.xlsx`。

### 5.4 敏感词管理（`views/sensitive/`）

- CRUD + 批量删除。⚠️ 字段名不统一：列表实体字段是 `sensitives`（复数），新增 / 搜索 / 表单用 `sensitive`（单数）；`SensitiveForm.sensitives` vs `addSensitive(sensitive)`。`delete` 传 `ids`（`as number[]` 但实际是逗号拼接字符串，靠 `qs` 序列化）。
- 后端路径 `/sensitive`（无 `/store` 前缀），与其它资源前缀不一致。

### 5.5 知识库管理（`views/knowledge-base/`）

- `AddForm.vue`：`el-upload`（`auto-upload: false`，`accept` 限 pdf/doc/xls/ppt/txt/md，单文件）→ `formData.append("file", raw)` → `KnowledgeBaseAPI.addKnowledgeBase` → **`POST /store/knowledgeBase/embed`**（multipart）做向量化入库。全程 `ElLoading.service` 全屏 loading。
- 列表分页 + 批量删除（`deleteByIds` 传逗号拼接字符串 cast `as number`）+ 下载（`fetch(row.url)` → blob 下载）。
- ⚠️ URL 大小写 `knowledgeBase`（大写 B），与后端其它资源命名风格不一致。

### 5.6 人工客服（WebSocket 页面，重点）

`views/customer-service/index.vue` 是本次分析**风险最高**的页面：

- **连接地址写死**（与 CLAUDE.md / AGENTS.md 警示一致）：
  - `第 258 行`：`const wsUrl = \`ws://localhost:8848/ws/customer-service/${sessionId}/staff\`;` —— **写死 ws 协议 + localhost:8848**，未读 `.env` 的 `VITE_APP_WS_URL`。
  - `第 178/222/339/446 行`：`fetch("http://localhost:8848/...")` —— 4 处 **http 写死**，绕过 `/dev-api` 代理。
- **协议**：`ws://`（非 `wss://`），无 TLS。
- **实现方式**：**手写原生 `new WebSocket`**，未复用项目已有的 `src/utils/websocket.ts`（`Socket` 类，含心跳 / 重连 / 事件分发）与 `src/hooks/useSocket.ts`。后果：**无心跳、无自动重连、无 token 注入、无 onclose 兜底**（仅有 `onUnmounted` 手动 `close`）。
- **鉴权缺失**：`loadSessions`/`loadSessionMessages`/`endCurrentSession` 的 `fetch` **均不带 `token` 头**；只有 `loadAdminInfo`（`第 446 行`）带了头，但 key 用的是 **`accessToken`**（错误，实际存的是 `token`，见 `CacheEnum.ts`），导致管理员信息**永远取不到、恒回退「管理员」**。
- **`messageList` 大量 `any`**：`sessionList/messageList` 均为 `ref<any[]>`；`第 306 行` `senderId: 1 // TODO: 获取当前客服ID`（硬编码客服 ID）。
- 其它：`第 136 行` `import { log } from "console"` 未使用；`ElMessage/ElMessageBox` 靠 AutoImport。

### 5.7 员工权限

- `views/employee/`：CRUD + 启停 + 头像上传（仅 `admin` 且 `id===1` 显示头像上传项）。
- **权限模型缺失**：前端路由守卫仅判 token 有无，`UserInfo.roles/perms` 字段在 `employee.ts` 里定义了但**前端从未使用**——无按钮级 / 路由级权限控制。菜单是全量 `constantRoutes`，无角色过滤。
- ⚠️ **编辑员工 bug**：`EditForm.vue` 的 `handleSubmit` 无条件 `if (!formData.has("file")) { error("请选择一个图片文件"); return; }`，而头像上传项仅 admin 可见——**非 admin 员工编辑必然失败**；admin 编辑也必须重新选图才能提交。

---

## 6. 类型与质量

### 6.1 tsconfig（`tsconfig.json`）

- `strict: true` ✓、`noUnusedLocals/Parameters: true` ✓、`noFallthroughCasesInSwitch: true`、`skipLibCheck: true`、`allowJs: true`、`moduleResolution: Node`（非 bundler，配合 `allowImportingTsExtensions`）。
- `vue-tsc --noEmit` 已挂进 `build:prod`（生产构建前做类型检查）。
- ⚠️ `include` 里塞了 `commitlint.config.cjs`，并留了历史 TODO 注释（Parsing error 处理）。

### 6.2 ESLint（`.eslintrc.cjs`）

- `plugin:vue/vue3-recommended` + `@typescript-eslint/recommended` + `prettier`。
- **已大幅放宽**：`@typescript-eslint/no-explicit-any`、`no-unused-vars`、`no-non-null-assertion`、`no-empty-function`、`ban-ts-comment`、`explicit-module-boundary-types` 等**全部 off**。
- 大量 Vue 规则 off（`attributes-order`、`require-default-prop`、`multiline-html-element-content-newline` 等）。

### 6.3 Prettier / Stylelint / Husky / Commitlint

- **Prettier**（`.prettierrc.cjs`）：双引号 + 分号 + `printWidth: 80` + `tabWidth: 2`。
- **Stylelint**（`.stylelintrc.cjs`）：`standard` + `recommended-scss` + `recommended-vue/scss` + `recess-order`，`customSyntax` 按 vue/html、css/scss 区分。
- **Husky**：`package.json` 有 `prepare: husky install`，**但 `.husky/` 目录不存在**（实际未生成 pre-commit 等钩子）→ **commitlint / lint-staged 名义上配置、实际未在提交时强制执行**。
- **Commitlint**（`commitlint.config.cjs`）：`@commitlint/config-conventional` + cz-git 交互式中文 prompt，`useEmoji: true`。

### 6.4 TODO/FIXME / any / console

- **TODO / FIXME**：共约 11 处，实际待办类：`customer-service:306`（客服 ID 硬编码）、`BaseTable.vue:28/291`（类型 / 待完善）、`setmeal/DataTable.vue:118`（状态查询注释掉的 workaround）、`layout/index.vue:77`（混合布局未实现）、`dish/AddForm.vue:224`（dialog 内 ref 警告）。
- **`any` 泛滥程度**：`src/**/*.ts` 命中 **102 处**。分布：
  - API 层：几乎每个 `request<any, ...>`（第一泛型恒 `any`，约 60 处）；
  - 全局类型默认泛型 `T = any`（global.d.ts / table.d.ts / form.d.ts 约 20 处）；
  - 业务真 `as any`：`user.ts:14`、`app.ts:55`、`useOptions.ts:60/61`、`audio.ts:19`、`order/index.vue` 等多处 `(queryParams as any)`。
- **console 调用**：约 84 处（含大量注释掉的），集中在 customer-service、dish、order、comment、sensitive；生产 `drop_console` 会剥离，但代码可维护性一般。

---

## 7. 问题与风险

### 7.1 硬编码 URL 清单（精确到行）

| # | 文件:行 | 内容 | 严重度 |
|---|---|---|---|
| 1 | `src/views/customer-service/index.vue:178` | `http://localhost:8848/store/customer-service/sessions/active` | 🔴 高 |
| 2 | `src/views/customer-service/index.vue:222` | `http://localhost:8848/store/customer-service/messages/{sessionId}?type=1` | 🔴 高 |
| 3 | `src/views/customer-service/index.vue:258` | `ws://localhost:8848/ws/customer-service/{sessionId}/staff` | 🔴 高 |
| 4 | `src/views/customer-service/index.vue:339` | `http://localhost:8848/store/customer-service/session/{id}/end` | 🔴 高 |
| 5 | `src/views/customer-service/index.vue:446` | `http://localhost:8848/store/employee/me` | 🔴 高 |
| 6 | `src/views/login/index.vue:190` | `@import "https://fonts.googleapis.com/...Poppins..."` | 🟡 中（外部字体 CDN，离线/CSP 风险） |
| 7 | `.env.production:9/11` | `http://118.195.133.79` / `ws://118.195.133.79/ws/` | 🟡 中（生产明文 IP，且仍是 ws 非 wss） |
| 8 | `.env.development:12/14` | `http://localhost:8848` / `ws://localhost:8848/ws/` | 🟢 低（env 驱动，可接受） |
| 9 | `src/views/error-page/401.vue:13/32/41`、`404.vue:41` | 外部 gif/airbnb/taobao/wallstreetcn 链接 | 🟢 低（演示残留） |

> 补充：`.env` 里虽定义了 `VITE_APP_WS_URL`，但客服页**没用它**，而是写死 `localhost:8848`；生产切后端主机时客服页必然失效。

### 7.2 安全

1. **token 存 localStorage**（`token`），易受 XSS 窃取；且 `logout` 用 `setItem(TOKEN_KEY, "")` 而非 `removeItem`（`user.ts:29`）。
2. **客服页 token key 错误**：`customer-service/index.vue:441` 读 `accessToken`，实际是 `token` → 管理员信息失效；同时 `sessions/messages/end` 三个请求**完全不带 token**，后端 JWT 拦截器（拦 `/store/**`）会拒 401，客服页大概率与真实后端对不上。
3. **WebSocket 无鉴权、无 TLS**：`ws://` 明文，连接不带 token，任何人拿到 sessionId 可连。
4. **登录演示账号硬编码**：`login/index.vue:17-18` 预填 `admin/123456`，生产仍会预填。
5. **`dangerouslyUseHTMLString: true`**：`Settings/index.vue:45` 用 HTML 模板注入后端 `result.content` 到 `ElNotification`——若 `content` 含用户可控文本存在 **XSS 面**。
6. **外部字体 @import**：登录页依赖 Google Fonts（隐私 / 合规 / 离线风险）。
7. 敏感信息：`.env.production` 明文生产 IP（非密钥，但暴露拓扑）。

### 7.3 状态不同步 / 一致性问题

- `NavbarAction.vue:28` `loginName` 在 setup 时读一次 localStorage，**非响应式**（`getInfo` 是异步的，登录后 name 可能不同步，需刷新）。
- `Settings/index.vue` 里的 `useSocket()` 连接的 `VITE_APP_WS_URL + clientId(uuid)` 拼成 `ws://localhost:8848/ws/{uuid}`，与后端客服 WS 端点 `/ws/customer-service/{sessionId}/{userType}` 结构不符——**订单提醒 WebSocket 疑似错接 / 失效**（新订单 / 催单提醒实际不可用）。
- `user.ts` 登录后 `getInfo()` 未 `await`（`login/index.vue:56`），页面跳转时员工名可能未就绪。
- `app.ts` 营业状态文案「打样中」应为「打烊中」（`app.ts:71`、`NavbarAction.vue:69`）。

### 7.4 错误处理缺口

- `request.ts:82` `error.response.data` 缺空判 → 网络错误 / 超时 / CORS 时二次抛异常。
- `request.ts:90` `reject(error.message)` 丢弃结构 → 各页 `.catch(err => err.msg || ...)` 永远走 fallback，真实错误信息丢失。
- `knowledge-base/index.vue` `handleQuery` **无 try/catch、无 loading**；`handleDownload` 直接 `fetch(row.url)` 无鉴权。
- `dish/AddForm.vue` `handleSubmit` 无 try/catch（`DishAPI.add` 抛错时按钮 loading 不恢复）。
- `sensitive`/`comment` 删除、`DetailTree` 回复等多数 `.catch` 仅 `console.error`。
- `DetailTree.vue:258-260` catch 里 `ElMessage.success("回复失败！")`——**失败用了 success 类型**。

### 7.5 与后端接口不一致（字段 / 路径）

- **敏感词**：实体 `sensitives`（复数） vs 新增 / 搜索 `sensitive`（单数）；`SensitiveForm` 用 `sensitives`，`addSensitive` 用 `sensitive`；`delete` 参数 `ids` 类型声明 `number[]` 实为逗号字符串。
- **knowledgeBase** URL 用 camelCase 大写 B（`/store/knowledgeBase`），与 `customer-service`（kebab-case）等不一致。
- **comment.ts / commentRepay.ts / knowledgeBase.ts / sensitive.ts / useRequest.ts** 使用**单引号 + 无分号**，违反仓库 Prettier「双引号 + 分号」约定（新文件未跑 prettier）。
- **`order.ts` `details()` 返回类型写成 `PageResult<OrderPageVO[]>`**（分页类型），实际是单条详情，应为 `ReqResultType<OrderPageVO>`；`OrderPageVO.orderDetailList: any[]`。
- **`ResponseData<T>` 全局类型把 `data` 固定为 `{records, total}`**（`global.d.ts:167-179`），但 `login/getInfo/startOrStop` 等接口 `data` 并非分页结构 → 类型模型与实际响应不一致（这些方法普遍依赖 `as any` 绕过）。
- **启停成功判断** `code.toString().startsWith("2")`（dish/category/employee 三处）：把 201/204 等一切 2xx 都当成功，且与 `request.ts` 的 `code===200` 约定不一致。
- **comment/index.vue:319** `row.content.moreDishName <= 9`：`row.content` 是字符串，应为 `row.moreDishName.length`，逻辑 bug（tooltip 禁用条件失效）。

### 7.6 可维护性

- **三套表格实现并存**：手写 `el-table`（dish/category/order/comment/sensitive/knowledge-base）vs `BaseTable`（employee/setmeal/workbench）vs `OrderTable`，风格割裂。
- **两套表单实现并存**：手写 `el-form` 弹窗（dish/category/employee/sensitive）vs 配置驱动 `BaseFormDialog`（setmeal/employee DialogForm 残留）。
- **WebSocket 三套并存**：`utils/websocket.ts`（Socket 类）+ `hooks/useSocket.ts`（几乎只有 Settings 用）+ 客服页手写原生 `new WebSocket`，能力不统一、客服页未受益于重连 / 心跳。
- **死代码 / 残留**：`employee/modules/SearchForm.vue` 空组件、`employee/modules/DialogForm.vue` 未接入（注释掉）、`MessageTypeEnum`/`NavbarAction` 的 `messages` 是硬编码 mock（未接后端）、`order/hooks` 与 `order/model` 重复接单 / 拒单逻辑。
- **`* as` 全量导入**：`workbench/DataTable.vue:4`、`setmeal/*`、`AddDishDialog.vue:3` 用 `import * as`（作者注释自认「懒」，增打包体积）。
- **`vue-router` 版本过旧**（4.0.13），与 Vue 3.4 存在版本 gap 风险。
- **无单元测试脚本**，质量门禁仅 build(vue-tsc) + lint 三件套（且 husky 未落地）。

---

## 8. 总结

| 维度 | 评价 |
|---|---|
| 工程化 | 中上：Vite 插件链完整（自动导入 / 图标 / UnoCSS），Prettier / ESLint / Stylelint / commitlint 齐全；但 husky 钩子未落地、`vue-router` 偏旧 |
| 类型质量 | 中：`strict` + vue-tsc 到位，但 `no-explicit-any` 关闭、`any` 102 处、`ResponseData` 类型模型与真实响应脱节 |
| API 层 | 良好且统一（BASE_URL + class + static + default），但 request.ts 错误分支两处硬伤 + 类型双重包裹混乱 |
| 业务功能 | 覆盖完整（订单流转含堂食、菜品口味、套餐、评论回复树、知识库 embed、敏感词、客服） |
| 客服 / WS | **差**：地址 5 处写死、token key 错误、无鉴权 / 无重连 / 无心跳、ws 明文，是最大风险点 |
| 安全 | 需整改：token 明文 localStorage、XSS 面（dangerouslyUseHTMLString）、演示账号硬编码、外部字体 |
| 一致性 | 多套表格 / 表单 / WS 实现并存，敏感词字段名、knowledgeBase 大小写、注释掉的 workaround 等需统一 |

**最优先修复建议**（按严重度）：

1. 客服页改走 `request` + `.env` 的 `VITE_APP_WS_URL` 并注入 token、复用 `Socket` 类；
2. 修复 `accessToken` → `token` key；
3. 修复 `request.ts` 错误分支空判与 `reject` 结构；
4. 修复员工编辑「必须传图」bug；
5. 移除登录页预填账号与 Google Fonts；
6. 统一启停成功判断与敏感词字段命名。
