# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

「锦食坊」外卖**用户端小程序**。uni-app（Vue 3）+ Vite + uview-plus + Pinia + UnoCSS，编译目标微信小程序。工作区整体规范见项目根目录 `CLAUDE.md`。

## 命令
```bash
npm install
npm run dev:mp-weixin   # 编译到 dist/dev/mp-weixin
npm run type-check      # vue-tsc --noEmit
```
编译后**用微信开发者工具导入 `dist/dev/mp-weixin`** 预览（需对应 appid）。Node >= 18。

## 页面与导航
- tabBar 三个：`pages/index/index`（点餐）、`pages/message/message`（消息/客服）、`pages/user/user`（我的）。
- 其他页面：login、order（下单/列表/详情/支付成功）、address、comment（评价）、test。
- 路由用 uni 原生导航，全局方法 `$navTo` 跳转。

## 关键约定
- **请求封装** `src/utils/request/index.ts`（`$http`；class 封装在 `core/request.ts`）：baseURL 取 `VITE_APP_API_URL`；请求头注入 `authentication`（`uni.getStorageSync("token")`）；401 弹窗跳登录；超时 30s，AI 聊天接口 45s（stream 流式，`isFactory:false`）。
- **环境**：`.env.development` = `http://localhost:8848`（vite proxy `/dev-api`）；`.env.production` = `http://vapi.youlai.tech`。
- **登录**：`uni.login` 拿 code → `POST /client/user/login` 换 token，存 `uni.setStorageSync("token")`。
- **状态管理** `src/store/modules`：user / shopping-cart / shop / order-countdown；持久化用 `pinia-plugin-unistorage`。
- **UI**：uview-plus 全局注册（main.ts + App.vue 引入 scss），主色 `$uni-color-primary: #007aff`；UnoCSS 用 `@uni-helper/unocss-preset-uni`。markdown 渲染用 `wxcomponents/towxml`。

## 核心业务
- 点餐（index）：左分类 → 菜品/套餐列表 → 购物车 → 下单结算。
- 订单（order）：外卖/堂食切换（orderType 1 外卖 / 2 堂食，见 `enums/OrderEnum.ts`）、堂食取餐号、支付方式。
- 评价（comment）：AI 扩写 `POST /client/ai-comment/expand`（type 1 帮写 / 2 续写 / 3 润色 / 4 精简）。
- 客服（message）：AI 助手 `client/ai-assistant/chat`；人工客服 WebSocket `/ws/customer-service/{sessionId}/user`。
- AI 相关：`client/ai-assistant/recommend`（智能推荐）、`/client/view/increment/dish/{id}`（浏览计数上报）。

## 注意
- WebSocket 与个别请求地址**硬编码** `ws://localhost:8848` / `http://localhost:8848`（ai-assistant.vue、customer-service.vue），改后端地址需同步改。
- manifest.json 已配真实微信 appid `wxaf070971c0e6240c`（DCloud 占位 `__UNI__635FCFE`），`urlCheck:true`。
