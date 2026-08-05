# CONTEXT — admin（商家管理端）

路径：`code/vue/training-vue3-main` · Vue 3 + TS + Vite + Element Plus · 开发端口 **9527** · `VITE_APP_BASE_API=/dev-api` → proxy `http://localhost:8848`。

## 页面模块

| 模块 | 路由要点 |
|---|---|
| 工作台 / 统计 / 订单 | `/workbench` `/statistics` `/order` |
| 菜品 / 套餐 / 分类 / 员工 | `/dish` `/setmeal` `/category` `/employee` |
| 评论 | `/comment`（`authorId === 4` 标「AI自动回复」） |
| AI 知识库 | `/knowledgeBase` → `POST /store/knowledgeBase/embed` |
| 敏感词 | `/sensitive` → API 前缀 **`/sensitive`** |
| 客服管理 | `/customer-service` → 列表直连 WS `…/staff`（分配接口后端预留） |

营业状态：顶栏/侧栏（`store/modules/app.ts` + `/store/shop/status`），无独立店铺路由页。  
API 层：`src/api/*.ts` 为 `BASE_URL` + `class XxxAPI` + default export。

## 鉴权

- 登录：`POST /store/employee/login`（演示账号常见 `admin` / `123456`）
- Token：`localStorage` key `token`（`enums/CacheEnum.ts`）；请求头 **`headers.token`**（不是 `authentication`）
- 路由守卫：有 token 即放行；白名单 `/login`；无细粒度角色；`401` 清 token 并刷新

## 硬编码注意点

`views/customer-service/index.vue` 多处写死 `http://localhost:8848/store/customer-service/...`、`ws://localhost:8848/ws/customer-service/{sessionId}/staff`、以及 `employee/me`；部分 `fetch` **未走** `request.ts` token 注入。改后端主机必同步该页。  
`.env` 中的 `VITE_APP_WS_URL` 存在，但客服页当前未统一依赖它拼接。

## 硬约束

- 联调需先起后端 8848
- 勿臆造未实现的运营页（天气配置台、浏览偏好推荐后台等）
- 子项目约定见同目录树下 `CLAUDE.md`；用词：商家端 / 管理端
