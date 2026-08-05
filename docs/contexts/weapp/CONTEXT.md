# CONTEXT — weapp（用户小程序）

路径：`code/uniapp/training-uniapp-master` · uni-app Vue3 + uview-plus + Pinia · 目标微信小程序。  
`npm run dev:mp-weixin` → 微信开发者工具打开 `dist/dev/mp-weixin`。

## 导航

TabBar：点餐 `pages/index/index` · 消息 `pages/message/index` · 我的 `pages/user/index`。另有登录、订单、地址、评价、消息子页等（见 `pages.json`）。

## 主流程

点餐 → 购物车（store）→ 创建订单 → 支付 → 评价。  
`orderType`：`1` 外卖（地址/配送费）· `2` 堂食（取餐号，状态含待取餐 `8`）。

## 鉴权与请求

- 登录：`uni.login` → `POST /client/user/login` → `uni.setStorageSync("token")`
- 请求头：**`authentication`**（不是商家端的 `token` 头）
- `baseURL`：`VITE_APP_API_URL`（开发常为 `http://localhost:8848`）
- 普通超时约 30s；AI 聊天约 **45s**，流式 `responseType: 'stream'`，`isFactory: false`
- Store：`user` / `shopping-cart` / `shop` / `order-countdown`

## AI 入口

| 能力 | 位置 / API |
|---|---|
| AI 智能助手 | `pages/message/ai-assistant`：`session/ai` + 分块流式 `/client/ai-assistant/chat` + 会话 WS |
| 人工客服 | `pages/message/customer-service`：`session/human` + WS `…/user` |
| 互动消息 | `pages/message/comment-message` |
| AI 推荐 | 点餐首页 → `POST /client/ai-assistant/recommend` |
| 浏览上报 | 详情 → `POST /client/view/increment/dish/{id}`（**推荐不读该表**） |
| 评论扩写 | 帮写/续写/润色/精简 → `/client/ai-comment/expand`（需先有输入） |

SSE：DATA 1001 / STOP 1002 / PARAM 1003。Markdown：`towxml`。  
勿把前端推荐说成「基于浏览记录的协同过滤」——现行是语义偏好 + 后端降级。

## 硬编码注意点

`ai-assistant.vue` / `customer-service.vue` 写死 `http://localhost:8848/...` 与 `ws://localhost:8848/ws/customer-service/...`，未统一走 `$http` 封装；改主机必同步。  
`manifest.json` 含真实微信 appid；`urlCheck: true` 时非法域名会被拦。

## 硬约束

- 天气无独立 Tool；超范围引导人工（能力在后端提示词/Tools）
- 子项目约定见同目录树下 `CLAUDE.md`
