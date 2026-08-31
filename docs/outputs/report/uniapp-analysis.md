# 「锦食坊」用户微信小程序深度源码分析报告

> 分析对象：`D:\OneDrive\Desktop\sky\sky-ai\code\uniapp\training-uniapp-master`
> 技术栈：uni-app（Vue 3 `<script setup>` + TypeScript）+ uview-plus + Pinia（pinia-plugin-unistorage）+ UnoCSS + Sass，编译目标微信小程序。

---

## 1. 工程结构

### 1.1 `package.json`

**脚本**（`package.json`）

| 脚本 | 命令 | 说明 |
|---|---|---|
| `dev` | `npm run dev:mp-weixin --mode development` | 开发入口 |
| `dev:mp-weixin` | `uni -p mp-weixin` | 编译到 `dist/dev/mp-weixin` |
| `build:mp-weixin` | `uni build -p mp-weixin` | 生产构建 |
| `type-check` | `vue-tsc --noEmit` | 静态类型检查 |
| `unocss` | `unocss pages/**/*.wxml -c unocss.config.js --watch -o unocss.wxss` | ⚠️ 脚本已失效：配置文件名与目标路径都不对（见 §6） |
| 其余 | `dev:app / dev:h5 / dev:mp-alipay / ...` | uni-app 标准多端脚本 |

**关键依赖版本**（节选）

| 依赖 | 版本 | 备注 |
|---|---|---|
| `@dcloudio/uni-app` 等全套 | `3.0.0-4020420240722002` | 锁定 2024-07 快照 |
| `vue` | `^3.5.6` | |
| `pinia` | `2.0.33` | 非 3.x |
| `pinia-plugin-unistorage` | `^0.1.2` | 本地持久化插件 |
| `uview-plus` | `^3.3.32` | UI 库 |
| `mitt` | `^3.0.1` | 事件总线（订单倒计时） |
| `lodash` / `dayjs` / `vue-i18n` / `address-parse` / `clipboard` | — | `clipboard` 未在源码中使用 |

**devDependencies 关注点**：`typescript ^5.9.2` 与 `vue-tsc ^1.8.27` **版本严重不匹配**（vue-tsc 1.x 只支持到 TS 5.2 左右，`type-check` 大概率报错或不稳定，见 §6）；`unplugin-transform-we-class`、`unplugin-unocss-attributify-wechat`、`vite-plugin-vue-setup-extend` 已安装但 **未在 `vite.config.ts` 中启用**。

### 1.2 `src/manifest.json`

- 根 `appid`: `__UNI__635FCFE`（DCloud 占位符）。
- `mp-weixin.appid`: `wxb68ba3fc2d35105f`（⚠️ 与 `CLAUDE.md` 中写的 `wxaf070971c0e6240c` 不一致，**以 manifest 为准**，CLAUDE.md 已过时）。
- `mp-weixin.setting.urlCheck: true`（开启安全域名校验）、`es6: true`、`lazyCodeLoading: "requiredComponents"`。
- `mp-weixin.permission.scope.userLocation`（定位）、`requiredPrivateInfos: ["chooseAddress"]`（通讯地址）。
- `uniStatistics.enable: false`、`vueVersion: "3"`、`transformPx: false`。

### 1.3 `src/pages.json`

- **tabBar**（3 项）：点餐 `pages/index/index`、消息 `pages/message/index`、我的 `pages/user/index`；`selectedColor: #d81e06`。
- **页面路由**：点餐首页、菜品详情 `pages/index/components/DishDetail`、所有评论、写评价 `pages/comment/components/comment`、消息中心、人工客服、AI 助手、互动消息、支付成功、创建订单、订单详情、订单列表、地址（index/create/update）、登录、我的、账号设置等 18 个页面。
- **easycom** 配置了 uview-plus 三种前缀 `u--*` / `up-*` / `u-*`（注意代码里混用了 `up-*` 与 `u-*`，实际 UI 组件大量用 `up-*`）。
- `pages/message/ai-assistant` 的 `usingComponents` 单独注册了 `towxml`（markdown 渲染）。
- ⚠️ `condition.list` 内包含 `//` 注释（jsonc 写法，uni-app 允许但非标准 JSON）。

### 1.4 `src/` 目录树

```
src/
├── api/                # 15 个 API 模块（address/ai-comment/ai-recommend/category/
│                       # comment/comment-message/commentRepay/customer-service/
│                       # dish/item-view/order/setmeal/shop/shopping-cart/user）
├── common/constant/order.ts   # PACK_AMOUNT / IS_REPETITION
├── common/region/             # 省市区静态数据（provinces/citys/areas）
├── components/                # 12 个业务组件（见 §2 表格）
├── core/app.ts                # 全局工具：navTo/showToast/isLogin/checkLogin
├── directive/                 # stagger 交错动画指令
├── enums/                     # Address/Category/Order/Pay/Status 枚举
├── pages/                     # index / login / message / order / comment /
│                              # address / user / test
├── plugins/                   # 全局方法挂载（$navTo/$toast/$isLogin...）
├── store/                     # index.ts + modules/{user,shopping-cart,shop,order-countdown}
├── types/                     # 全局类型（global.d.ts / globalMethods.d.ts / ...）
├── utils/request/             # $http 封装（index.ts + core/{request,types,utils}）
├── utils/                     # storage/util/verify/index + hooks/useCountdownTimer
└── wxcomponents/towxml/       # markdown 渲染组件
```

---

## 2. 页面盘点

| 页面（`src/pages/**`） | 职责一句话 | uview-plus 组件 | UnoCSS |
|---|---|---|---|
| `index/index.vue` | 点餐首页：左分类右菜品、轮播、AI 推荐、购物车浮层、去下单 | ✅ `up-tabs/sticky/swiper/number-box/badge/popup/image/tag/notice-bar/button/count-to/row/col` | ✅ `dis-flex flex-y-center w-full mb-10` 等 |
| `index/components/DishDetail.vue` | 菜品/套餐详情：评论、商家推荐、加购、立即下单 | ✅ `up-image/avatar/button/badge/icon` | ✅ |
| `index/components/DishFlavorModel.vue` | 菜品口味选择弹窗（多口味组合、加购/改数量） | ✅ `up-modal/tag/number-box/button/icon` | — |
| `login/index.vue` | 微信一键登录（uni.login → 登录接口） | ✅ `up-image` | — |
| `message/index.vue` | 消息中心：人工客服 / AI 助手 / 互动消息 / 常见问题入口 | 原生（`iconfont` 图标） | — |
| `message/ai-assistant.vue` | AI 小锦对话：SSE 流式 + 打字机 + 推荐卡片 + 购物车 | ✅ `up-image/button/badge/col` + `towxml` | ✅ |
| `message/customer-service.vue` | 人工客服 WebSocket 聊天 | 原生 + `iconfont` | — |
| `message/comment-message.vue` | 互动消息（评论被回复）列表 + 分页 | 原生 | — |
| `order/index.vue` | 创建订单：外卖/堂食切换、地址、配送、餐具、备注、支付方式 | ✅ `up-card/subsection/notice-bar/read-more/image/tag/divider/count-to/icon` | ✅ |
| `order/order-list.vue` | 订单列表（swiper + tabs，状态筛选，分页） | ✅ `up-tabs/sticky/loadmore/empty` + 自研 `OrderCard` | ✅ |
| `order/order-detail.vue` | 订单详情：倒计时、取餐码、支付/催单/取消/再来一单 | ✅ `up-card/notice-bar/image/button/modal/icon` | ✅ |
| `order/pay-success.vue` | 下单成功页（⚠️「查看订单」未实现） | ✅ `up-button` | ✅ |
| `comment/index.vue` | 评论列表：点赞/回复/删除/最新热度切换 | ✅ `up-subsection` + 自研 `ReplyPopup` | — |
| `comment/components/comment.vue` | 写评价：AI 扩写（帮写/续写/润色/精简）+ 表情 + 提交 | 原生（含 ⚠️ DOM API，见 §7） | — |
| `comment/components/replyPopup.vue` | 评论回复弹窗 + AI 扩写回复 | 原生 | — |
| `address/index.vue` | 收货地址列表（默认/复制/编辑/删除） | ✅ `up-card/tag/radio-group/radio/copy/button` | ✅ |
| `address/create.vue` | 新增地址（省市区、剪贴板识别、表单校验） | ✅ `up-form/form-item/input/textarea/tag/switch/button/read-more` + `select-region` | ✅ |
| `address/update.vue` | 编辑地址（同 create 逻辑） | ✅ 同上 | ✅ |
| `user/index.vue` | 个人中心：登录态、订单模块入口、地址/设置、退出 | ✅ `up-avatar/icon` | ✅ |
| `user/complete-user-info.vue` | 账号设置：头像选择/上传、昵称修改 | ✅ `up-form/form-item/icon` | ✅ |
| `test/scroll-test.vue` | 滚动测试页（调试遗留） | — | — |

> **UnoCSS 使用**：仅点餐首页 / 订单页 / 部分组件密集使用了 `dis-flex / flex-y-center / w-full / mb-* / f-*` 等原子类；消息/评论/地址等页面以 scoped scss 为主，UnoCSS 覆盖不均衡。多数组件同时用了 `up-*`（uview-plus）与原生 `view/text` 混排。

---

## 3. 请求与状态

### 3.1 `$http` 封装细节

**入口** `src/utils/request/index.ts`

- 实例化 `new request({ baseUrl: import.meta.env.VITE_APP_API_URL, header: {"content-type":"application/json;charset=utf-8"}, timeout: 30000, config: {isPrompt:true, load:true, isFactory:true} })`。
- `VITE_APP_API_URL` 来自 `.env.development` = `http://localhost:8848`，`.env.production` = `http://vapi.youlai.tech`（⚠️ 生产域名疑似示例/占位，见 §7）。
- **请求拦截器** `$http.requestStart`：维护 `requestNum` 计数器控制全局 `uni.showLoading`；**注入 `options.header["authentication"] = uni.getStorageSync("token")`**。
- **数据工厂** `$http.dataFactory`：`whiteCodeList = [404, 401]`；`code === 200` 直接 `resolve(responseData)`；`code === 401` → 延迟 2.1s 调 `userStore.logout()` + 弹「去登录」modal；`code === 500` → toast 后 reject；字符串响应会先 `JSON.parse`。
- **错误回调** `$http.requestError` → `showRequestError`：专门识别微信「`request:fail url not in domain list`」（域名白名单错误）与「`request:fail`」网络错误。
- ⚠️ **设计隐患**：`userStore` 通过 `const userStore = ref()` + `setTimeout(() => { userStore.value = useUserStoreHook() }, 2000)` 延迟 2 秒获取（`index.ts:5-9`），2 秒内的 401 处理会拿不到 store 实例。

**核心类** `src/utils/request/core/request.ts` + `core/utils.ts`

- `mergeConfig`：`url` 若已是 `http(s)://` 则直接用，否则拼 `baseUrl + url`；`config` 合并顺序 `{timeout} ← 实例config ← 单次options`。
- `dispatchRequest`：`uni.request` + **`setTimeout` 主动 `requestTask.abort()`** 实现超时中断；`errMsg === "request:fail abort"` 时返回「请求超时」。MP-WEIXIN/MP-ALIPAY 下把 `timeout` 传给原生；H5 下传 `withCredentials`。
- 提供 `post/get/put/delete` 四个方法，泛型 `<T>`。

### 3.2 token 存取与登录

- `store/modules/user.ts` → `loginMpWx(code)`：`POST /client/user/login` → 拿 `token/id` → `uni.setStorageSync("token")` + `uni.setStorageSync("userId")`，随后并行拉取用户信息 + 商家信息。
- 额外写入的裸存储键：`userInfo`、`shopInfo`（`user.ts:49/78`）、`cart`（`shopping-cart.ts:12`）。
- `logout()`：清除 `token/cart/userId/userInfo/shopInfo` 五个键。

### 3.3 Pinia store 清单与持久化策略

| Store（`store/modules/`） | 关键状态 | 持久化方式 |
|---|---|---|
| `user.ts` | `token` / `userId` / `userInfo` / `shopInfo` | **手动 `uni.setStorageSync`**（token/userId/userInfo/shopInfo），未用 unistorage 插件 |
| `shopping-cart.ts` | `list` / `totalNum` / `totalPrice` | **手动 `uni.setStorageSync("cart")`** + 服务器双向同步 |
| `shop.ts` | `shopStatus` / `startTime` / `endTime` | **唯一使用 `pinia-plugin-unistorage`**，`unistorage: {key:"shopStore", storage: getStorageSync/setStorageSync, paths:[...]}` |
| `order-countdown.ts` | `tasks`（内存定时器数组） | **无持久化**（内存态，App 重启即失） |

> ⚠️ 与 `AGENTS.md` 描述「user/shopping-cart/shop/order-countdown 持久化」有偏差：实际只有 `shop` 走 unistorage 插件；`user`/`shopping-cart` 走手动 storage；**`order-countdown` 完全没有持久化**（倒计时任务在内存里，重启后待支付订单的倒计时依赖页面重新 `addOrder`）。

**购物车一致性细节**（`shopping-cart.ts`）：`addCart/updateShoppingCartNumber/subCart/cleanCart` 都是「先调后端接口成功 → 再合并本地 `cart` 存储」；`getCartlist()` 仅在 `res.code===200 && res.data.length` 时才覆盖本地（**后端返回空列表时不会清空本地**，可能造成脏数据残留）。

---

## 4. AI 交互深挖

### 4.1 `ai-assistant.vue`（AI 小锦）SSE 流式对话

**双通道并存**：该页同时建立了 ① WebSocket（`ws://localhost:8848/ws/customer-service/{sessionId}/user`，用于收「系统/消息」广播）和 ② 一条 `uni.request` 的 SSE 流式请求（真正承载大模型 token 流）。

**初始化链路**（`onMounted` → `initAIAssistant`）：
1. `createAISession()` → `POST /client/customer-service/session/ai`（`load:false`）。
2. `connectWebSocket()`。
3. `loadMessagesFromStorage()`：读本地 `ai_assistant_messages_{sessionId}`。
4. `loadHistoryMessages()` → `GET /client/customer-service/messages/{sessionId}?type=2`。
5. 无本地消息则 `addAIMessage("您好！我是AI智能助手…")` 欢迎语。

**SSE 流式实现**（`startWeixinStream`，`ai-assistant.vue:887-914`）：
- `uni.request({ url: http://localhost:8848/client/ai-assistant/chat?sessionId=...&message=..., method: "POST", header:{authentication: token}, data: chatData, enableChunked: true, responseType: "arraybuffer" })`。
- `requestTask.onChunkReceived` 回调：`ArrayBuffer → Uint8Array → TextDecoder → 字符串` → `processSSEStream`。
- ⚠️ 这里**绕过了 `$http` 封装**，直接手写 `uni.request` + 硬编码 URL（见 §7）。

**SSE 解析**（`processSSEStream`，`917-966`）：
- 按 `\n` 拆行，找 `data:` 前缀 → `line.substring(5).trim()` → `JSON.parse` → 按 `eventType` 分发：
  - **`1001`（DATA 内容事件）** → `typewriterEffectOptimized(aiMessage, eventData.eventData)` 打字机渲染；
  - **`1003`（PARAM 参数事件）** → `aiParamsObject.value = eventData.eventData`、`aiMessage.params = eventData.eventData`（用于渲染推荐卡片）；
  - **`1002`（STOP 停止事件）** → 把 `_typewriterContent` 落盘到 `content`、`_isTypewriting=false`、`scrollToBottom()`。
- ⚠️ **关键 Bug**：`processSSEStream` 内 `let sseBuffer = ""` 是**函数局部变量**（每次回调重新初始化为空），注释写的「保存不完整片段到缓冲区」实际失效——**当一个 `data:` 行被 TCP 分块截断到两个 `onChunkReceived` 时，前半段会丢失**，导致解析失败。

**打字机效果**（`typewriterEffectOptimized`，`970-1023`）：`_typewriterBuffer` 累积 + `setInterval(50ms)` 每次取 3 字符追加到 `content`，配合 `nextTick + scrollToBottom`；缓冲区空 → `isTyping=false`、`isStream=false`、清定时器。

**推荐卡片渲染**：模板中出现了**三段几乎重复**的推荐列表代码块（`message.params.Dish`、`aiParamsObject.Dish`、以及「参数事件渲染」分支），均支持「选择规格（有 flavors）→ `handleDishFlavors` / 加入购物车 → `handleCommentAddCart` / 点击进入详情 `handleDishClick`」，末尾挂「购物车」按钮唤起 `CardPopup`。这是明显的复制粘贴式维护负担。

**转人工流程**：`ai-assistant.vue` **没有页面内的「转人工」按钮**；转人工依赖返回 `message/index.vue` 选择「人工客服」入口进入 `customer-service.vue`，两者是并列关系而非会话内移交。AI 的 WebSocket 与人工客服共用同一路径 `/ws/customer-service/{sessionId}/user`（`userType` 固定为 `user`）。

**其他**：`getMessageQuestion`（`583-588`）会裁剪用户消息中 `"\n\nAnswer using the following information:"` 后缀——这是后端 RAG 提示词泄漏到前端的产物，前端做清洗。

### 4.2 `customer-service.vue`（人工客服）WebSocket

- **连接地址**（硬编码，`customer-service.vue:179`）：`ws://localhost:8848/ws/customer-service/${sessionId.value}/user`。
- **建立流程**：`createCustomerServiceSession()` → `POST /client/customer-service/session/human` → 拿 `sessionId` → `loadHistoryMessages()`（`GET .../messages/{sessionId}?type=1`）→ `connectWebSocket()`。
- **消息格式**（收发均为 JSON）：
  - 发送：`websocket.send({ data: JSON.stringify({ content, messageType: 1 }) })`。
  - 接收：`JSON.parse(res.data)` 后按 `message.type` 分发：`system`（忽略不显示）、`message`（按 `senderType` 1 用户 / 2 客服渲染）、`error`（仅 console）。
- ⚠️ **无重连机制、无心跳**：`onClose`/`onError` 只把 `isConnected=false`，不 `connectSocket` 重连；发送时 `if (!content || !isConnected.value) return`，连接断开后无法发送。
- WebSocket 握手 **不携带 `authentication` 头/token**，仅凭路径中的 `sessionId`（可枚举猜测，存在越权风险，见 §7）。

---

## 5. 业务流核实

### 5.1 点餐 → 加购 → 下单 → 支付 → 评价 全链路

```
点餐 index/index.vue
  ├─ getCategoryAllList()          GET /client/category/list
  ├─ getDishByCateId()             GET /client/dish/list + /client/setmeal/list（并行）
  │     └─ dishes + setmeals 合并，tabIndex=1 按 hotSpot 降序
  ├─ 加购 handleAddCart → shoppingCartStore.addCart()
  │     └─ POST /client/shoppingCart/add → 成功后再合并本地 cart
  ├─ 口味选择 DishFlavorModel.show() → addCart(dishFlavor)
  └─ 去下单 → $navTo("pages/order/index")

下单 order/index.vue
  ├─ 外卖/堂食切换 onOrderTypeChange（orderType 1/2）
  ├─ 外卖：默认地址 getDefaultAddress() + delivery-options + 打包/配送费
  ├─ 堂食：清空地址/打包费/配送状态（预留电话输入未绑定，见 §7）
  ├─ 餐具选择 select-tableware / 备注 / 支付方式 select-payment-method
  └─ onPay → createOrder() → POST /client/order/submit
        └─ 成功 → redirectTo order-detail?orderId=... → 1.5s 后 cleanCart()

支付 order-detail.vue
  ├─ onPay → payOrder() → PUT /client/order/payment {orderNumber, payMethod}
  │     ⚠️ 纯“模拟支付”：无 uni.requestPayment / wx.requestPayment 调用，
  │        直接改本地状态 PENDING_ACCEPTANCE + 弹支付成功 popup
  ├─ 支付方式仅“微信支付”（select-payment-method 中支付宝已注释）
  └─ 倒计时归零 → cancelOrder / 自动取消

评价 order-list.vue → order-card「立即评价」
  └─ comment/components/comment.vue（整单透传）
        ├─ AI扩写 onExpandClick(1帮写/2续写/3润色/4精简)
        │     → POST /client/ai-comment/expand
        └─ onSubmit → POST /client/comment/save → navigateBack
```

### 5.2 堂食 / 外卖切换逻辑

- `order/index.vue` 顶部 `up-subsection ['外卖','堂食']`，`onOrderTypeChange(index)`：`index===0 → orderType=1（外卖）`、`index===1 → orderType=2（堂食）`。
- **外卖（1）**：显示收货地址卡片 + `delivery-options`（立即/预约配送）+ 打包费 `shopInfo.balingCharges` + 配送费 `shopInfo.deliveryCharges`；`createOrder` 正常提交 `addressBookId / packAmount / deliveryStatus`。
- **堂食（2）**：隐藏地址卡片，`createOrder` 将 `addressBookId=undefined、packAmount=0、deliveryStatus=undefined`；显示「预留电话」输入。
- ⚠️ **堂食预留电话未绑定**：模板中 `<input :value="reservedPhone" ...>` 是**单向绑定且无 `@input/@change` 处理器**，用户输入不会写回 `reservedPhone`，也不会进入提交的 `order` 对象（`order/index.vue:35-40`），**堂食预留电话功能实际失效**。
- 订单详情对堂食已支付订单展示 `pickupNumber`（取餐码），状态用 `OrderStatus.DINE_IN_ORDER(8)`。

### 5.3 订单倒计时

- 核心在 `store/modules/order-countdown.ts`（`mitt` 事件总线 + 内存任务表）：
  - `TOTAL_SECONDS = shopInfo.remainingPaymentTime(5分钟) * 60 = 300s`。
  - `addOrder`：按 `order.orderTime` 与当前时间差计算剩余秒数 → `setInterval(1s)` 每秒 `remainingSeconds--`，归零 → `handleAutoCancel`。
  - `handleAutoCancel`：`PUT /client/order/cancel`（`load:false`），失败按 `retryCount` 指数退避重试（`MAX_RETRY_COUNT=2`，延迟 `2^n * 1000ms`），最终 `removeOrder` 清理定时器。
  - 通过 `orderCountdownEvents.subscribe("order:cancelled")` 广播全局取消事件，`order-list.vue` / `order-card` 订阅后本地改状态。
- `order-detail.vue` 另有一条兜底：`watch(formattedTime)` 当 `"00:00" && status===1` 时手动 `cancelOrder()`。
- `utils/hooks/useCountdownTimer.ts` 是一个**未被引用的独立 Hook**（写 `orderCountdownEndTime` 到本地存储），属于遗留/重复实现。

---

## 6. 类型与质量

### 6.1 类型检查配置

- `tsconfig.json`：`strict: true`、`noUnusedLocals/Parameters: true`、`noFallthroughCasesInSwitch: true`、`skipLibCheck: true`、`moduleResolution: node`；`include` 覆盖 `src/**/*.{ts,d.ts,tsx,vue}`，`exclude` 含 `dist/node_modules/unpackage`。
- `type-check` 脚本 = `vue-tsc --noEmit`。
- ⚠️ **版本冲突**：`typescript ^5.9.2` 配 `vue-tsc ^1.8.27`（后者不支持 TS 5.9），类型检查存在高风险。未发现 `.eslintrc`（仅 `vite.config.ts` 里 AutoImport 生成了 `.eslintrc-auto-import.json`），**无 lint 脚本、无测试脚本**，质量门禁基本只有 `type-check`。

### 6.2 TODO / FIXME

- `vite.config.ts:6` —— `// TODO: css原子化`。
- `pages/index/index.vue:715` —— `// TODO: 可以选择执行一些逻辑`（店铺打样分支）。
- `pages/address/index.vue:6` —— `<!-- TODO:未完成上划加载更多 -->`。
- 其余 TODO 集中在 `wxcomponents/towxml` 第三方库内部。

### 6.3 `any` 泛滥情况

- `.ts` 文件约 **54 处**：集中在 `utils/util.ts`（约 20 处，如 `dateFormat/inArray/cloneObj`）、`utils/request/core/*`（约 15 处，泛型默认 `any`）、`utils/storage.ts`、`utils/verify.ts`、`types/*.d.ts`（`$u/$request: any`）、`api/customer-service.ts` 的 `options?: any`。
- `.vue` 文件约 **34 处**：AI 页面是重灾区——`ai-assistant.vue` 的 `messageList: ref<any[]>`、`aiParamsObject: ref<any>`、`processSSEStream(aiMessage: any, ...)`、`typewriterEffectOptimized(assistantMessage: any)`；`customer-service.vue` 的 `messageList: ref<any[]>`、`handleWebSocketMessage(message: any)`；各页面 `onLoad((options: any))`、`uForm = ref<any>`、表单校验 `validator(_: any, ...)`。
- AI 流式消息对象**完全无类型**（`aiMessage` 用运行时属性 `_typewriterBuffer/_typewriterInterval/_isTypewriting` 挂载），是类型安全的最大缺口。

### 6.4 样式方案

- **双轨**：`scss`（`uni.scss` 主题变量 + `app.scss` 基础样式 + 各页面 scoped scss）+ **UnoCSS**（`uno.config.ts` 用 `@uni-helper/unocss-preset-uni`，内置 shortcuts `flex-center/flex-x-between/wh-full/...`，theme 主色 `primary`）。
- ⚠️ **UnoCSS 配置不一致**：`package.json` 的 `unocss` 脚本指向 `unocss.config.js`（实际文件是 `uno.config.ts`），且 `content.pipeline.include` 只匹配 `/\.wxml$/`——**编译产物**文件，对源码 `.vue` 不生效；`presetWeapp` 与 attributify/weapp transformer 均被注释。实际原子类是否生效依赖 Vite 插件 `UnoCSS()` 的默认扫描，与脚本目标脱节。

---

## 7. 问题与风险

### 7.1 硬编码 URL 清单（文件 + 行号）

| 位置 | 内容 |
|---|---|
| `src/pages/message/customer-service.vue:179` | `ws://localhost:8848/ws/customer-service/${sessionId}/user` |
| `src/pages/message/ai-assistant.vue:717` | `ws://localhost:8848/ws/customer-service/${sessionId}/user` |
| `src/pages/message/ai-assistant.vue:889` | `http://localhost:8848/client/ai-assistant/chat?...` |
| `src/wxcomponents/towxml/config.js:6,11` | `http://towxml.vvadd.com/?tex` / `?yuml`（第三方 LaTeX/图） |

> 结论：**3 处业务硬编码**全部集中在 AI/客服两个页面；其余通过 `VITE_APP_API_URL` 走环境变量。生产环境 `VITE_APP_API_URL = http://vapi.youlai.tech` 疑似示例域名，若未改则生产必挂。

### 7.2 token 存储安全性

- `authentication` token 用 `uni.setStorageSync("token")` 明文存本地（微信 storage 不加密，root/越狱可读）。
- AI 流式请求虽在 header 里带 `authentication`，但 **WebSocket 握手不鉴权**，仅凭 URL 中的 `sessionId` 即可连入客服/会话通道，存在会话 ID 猜测与越权风险。
- 头像上传 `uni.uploadFile` header 手动拼 `authentication`（`complete-user-info.vue:110`），正常。

### 7.3 会话与缓存一致性问题

1. **AI 消息本地缓存 key 失效风险**：`ai_assistant_messages_{sessionId}`（`ai-assistant.vue:554`）依赖 `sessionId`，而 `createAISession` 每次可能返回新 `sessionId`，历史本地消息可能永远读不到（依赖后端是否复用会话）。
2. **购物车本地/服务器双写可能漂移**：`getCartlist()` 只在后端返回非空时才覆盖本地；后端清空购物车（返回 `[]`）时本地 `cart` 不会清空。
3. **SSE 分块截断丢失**：`processSSEStream` 的 `sseBuffer` 为局部变量，跨 chunk 的不完整行会丢失（§4.1）。
4. **订单倒计时无持久化**：与 `AGENTS.md` 声明不符，App 重启/页面销毁后倒计时依赖重新拉单重建。

### 7.4 与后端接口不一致 / 潜在 Bug

1. **评价 AI 扩写取值疑似错误**：`comment.vue:189` `content.value = res.data.msg`，但 `AiCommentDto` 无 `msg` 字段，扩写结果很可能取空；且 `onExpandClick` 构造的 `params` 未传 `originalComment` 字段。
2. **评价页使用 DOM API**：`comment.vue:150/165` `document.querySelector(".emoji-panel")` / `document.addEventListener` 在微信小程序环境**不存在**（无 DOM），表情面板的点击外部关闭逻辑在真机无效（虽不致命但属错误实现）。
3. **`pay-success.vue`「查看订单」未开发**：点击仅弹「功能未开发」。
4. **AI 推荐 `shopId` 硬编码为 1**（`index/index.vue:522`），且接口失败会降级到本地 mock 数据（`getMockRecommendations`），mock 图片为百度外链。
5. **堂食预留电话输入未绑定**（§5.2），功能失效。
6. **`OrderAPI.payment` 走 `PUT /client/order/payment` 模拟支付**，未接微信支付 SDK；`PayStatusEnum` 注释值（1/2）语义与订单 `payStatus`（0未付/1已付/2退款）冲突。
7. **`ai-comment.ts` 的 `AiCommentDto` 同时被用作请求体与响应体类型**（`aiCommentExpandList` 返回 `AiCommentDto`），类型模型不严谨。
8. **`manifest appid` 与 CLAUDE.md 记录不符**（`wxb68ba3fc2d35105f` vs `wxaf070971c0e6240c`）。

### 7.5 体验问题（加载态 / 错误态 / 边界）

- **错误处理缺失**：`index.vue` 的 `getDishByCateId`、`comment-message.vue` 的 `getCommentMessage` 首次加载、`DishDetail.vue` 的 `loadComments` 等多处未做 catch/降级，仅部分有 `console.error`。
- **`onLoad`/`onShow` 重复请求**：`order-list.vue` 的 tab 切换 + swiper 滑动会双向触发 `getOrderList`（虽有 `swiperCurrent===index` 防重，但 `handleTabsChange` 与 `swiperAnimationFinish` 逻辑叠加仍存在重复刷新风险）。
- **滚动到底部实现粗糙**：`scrollToBottom` 用「设 99999 再切 99998」的 hack，`ai-assistant.vue` 甚至叠加 `setTimeout(500/1000/2000)` 多次滚动。
- **遗留调试代码**：全仓 `console.log` 约 **124 处**（AI/客服页面最密集），生产环境未清理。
- **重复模板**：AI 助手页推荐卡片三段重复、`CardPopup` 与 `index.vue` 内联购物车弹层功能重叠。
- **依赖冗余/失效**：`clipboard`、`vue-i18n`、`vite-plugin-vue-setup-extend` 等未使用；`unocss` 脚本失效。

---

### 一句话总评

该小程序**业务骨架完整**（点餐→加购→下单→模拟支付→评价、外卖/堂食、订单倒计时均闭环），uview-plus + UnoCSS + Pinia 分层清晰、请求封装 `$http` 设计较成熟；但 **AI 两个页面是质量洼地**：硬编码 URL、SSE 分块丢失 Bug、大段 `any`、DOM API 误用、无重连的 WebSocket、三段重复模板与大量遗留日志，加上 `vue-tsc` 与 `typescript` 版本冲突导致类型门禁形同虚设，是上线前需要重点整改的区域。
