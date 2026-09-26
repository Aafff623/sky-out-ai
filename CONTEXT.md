# CONTEXT.md — 锦食坊（sky-ai）

领域事实、硬约束与已知问题的单一入口。Agent 硬约束与构建命令见 `AGENTS.md`；人读说明见 `README.md`。

## 1. 项目定位

「锦食坊」是基于苍穹外卖重构的**培训 / 毕业设计**项目：传统外卖业务 + **LangChain4j + 通义千问** AI 能力。前后端分离，三端代码在 `code/`。

| 端 | 路径 | 技术栈 | 默认端口 |
|---|---|---|---|
| 后端 API | `code/web/training-web` | Spring Boot 3.4.5 · Java 17 · MyBatis · PageHelper · LangChain4j | **8848** |
| 商家管理端 | `code/vue/training-vue3-main` | Vue 3 · TypeScript · Vite 5 · Element Plus · Pinia · ECharts | **9527**（proxy `/dev-api` → 8848） |
| 用户小程序 | `code/uniapp/training-uniapp-master` | uni-app · Vue 3 · uview-plus · UnoCSS | 编译至微信开发者工具 |

后端 Maven 三模块：`training-common`（工具 / 常量 / `Result`）· `training-pojo`（entity/dto/vo，评论类为 MongoDB `@Document`）· `training-server`（业务，启动类 `TrainingApplication`），包根 `com.fortunate`。

## 2. 主链路

**点餐履约**：浏览分类 / 菜品 / 套餐 → 购物车 → 下单 `POST /client/order/submit`（雪花单号；外卖校验地址、堂食生成取餐号）→ 模拟支付 `PUT /client/order/payment` → `paySuccess` 置状态（外卖→2 待接单，堂食→8 待取餐）→ WebSocket 来单提醒商家 → 接单 / 拒单 / 派送 / 完成；堂食完成前凭取餐号取餐。订单状态：1 待付款 2 待接单 3 已接单 4 派送中 5 已完成 6 已取消 7 退款；堂食待取餐为 8。`orderType`：1 外卖 / 2 堂食。

**AI 客服**：小程序 `POST /client/ai-assistant/chat`（SSE）→ `AiAssistantServiceImpl` → `ConsultantService`（LangChain4j `AiServices` 显式装配：流式模型 + Redis 会话记忆窗口 20 条 + RAG + Tools）→ `tools/OrderTool`（查单 / 取消）/ `DishRecommendTool` → SSE 事件码 DATA(1001) / STOP(1002) / PARAM(1003)。人设在 `resources/Consultant.txt`。

## 3. AI 五大模块

| 模块 | 实现入口 |
|---|---|
| AI 智能客服「小锦」 | `chatService/ConsultantService` + `config/LangChain4jConfiguration`（记忆 key `training:session:messages:{sessionId}`，TTL 6h；RAG maxResults=2 / minScore=0.6） |
| AI 菜品推荐 | `chatService/RecommenAssistantService`（`@AiService`）+ `POST /client/ai-assistant/recommend`（12s 超时降级传统推荐） |
| 评论 AI 扩写与自动回复 | `chatService/AiCommentService` / `AiCommentExpandServiceAgent`（帮写 / 续写 / 润色 / 精简四模式） |
| 知识库 RAG | `KnowledgeBase*` + 自定义 `dev.langchain4j.store.embedding.MyEmbeddingStoreIngestor`；向量 key 前缀 `training-embedding:` |
| 敏感词过滤 | 阿里云 Green（`GreenTextScan`）+ 本地 DFA（`SensitiveWordUtil`） |

## 4. 数据与基础设施

| 存储 | 用途 |
|---|---|
| MySQL `cq_take_out` | 18 张表（传统 11 + AI 相关 + `shop_info` / `pickup_code` 等）；SQL 在 `code/web/training-web/sql/` |
| MongoDB `training-comment` | 评论 / 回复等文档 |
| Redis | 业务缓存（前缀 `CACHE_`）、AI 会话记忆、向量库（do 环境需 Redis Stack；local 无 RediSearch 走内存，重启后端需重新 embed） |
| MinIO `training-take-out` | 菜品图、知识库文件、UI 图与头像素材库（桶内：业务图 `2026/MM/dd/`、界面图 `ui/vue` · `ui/uniapp`、预设头像 `ui/avatars`、分类素材库 `ui/avatar-library/{animals,nature,food,objects,symbols,other}` + `index.json` 清单） |

**图片资产策略（2026-09 起，ADR 0001）**：业务图片运行时上传 MinIO，数据库只存 URL；UI 静态图与素材库以 MinIO 为权威存储、仓库 `assets/images/ui/` 保留全量备份（可重灌）；前端图片地址统一出自两端 `src/constants/minio.ts`（`MINIO_BASE` + 常量），禁止再硬编码。头像素材来自 [ipaslogo.com](https://ipaslogo.com) 免费库（可商用）。

**运行时**：默认本机原生中间件（MySQL80 + Scoop Redis/Mongo/MinIO），Spring profile **`local`**，脚本 `deploy/local/up-native.ps1`；profile `do` 为 DigitalOcean 中间件 + SSH 隧道回退方案；`dev` 指向预配虚拟机 `192.168.64.100`（配置文件不入库，需自建）。

**必填环境变量**（名称级）：`ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET`（OSS + 敏感词）、`ALIBABA_CLOUD_BAILIAN_ACCESS_KEY`（百炼）、`Wechat_APP_ID` / `Wechat_SECRET`（小程序登录）、`JWT_STORE_SECRET` / `JWT_CLIENT_SECRET`（生产必须注入）。AI 模型：`qwen3.7-plus` / `text-embedding-v4`（do/local 可用变量覆盖）。

## 5. 认证与安全形态

- JWT 双拦截器：商家端头 **`token`** 拦 `/store/**`；用户端头 **`authentication`** 拦 `/client/**`；失败 401。放行：两侧 login、公开分类 / 菜品 / 套餐、`/client/shop/status`、`/client/shopInfo`。
- **临时豁免（业务决策）**：`/store/customer-service/**` 在拦截器排除列表；客服 WebSocket `/ws/customer-service/{sessionId}/{userType}` 本身无鉴权，`sessionId` / `userType` 由客户端自报，staff 落库暂用固定 ID。
- 密钥治理：入库配置一律 `${ENV:}` 占位；`application-dev.yml` / `application-local.yml` / `application-do.yml` 不入库（本地保留可跑，新人仿造）。

## 6. 领域约束（Agent 必守）

1. **文档 vs 代码**：`docs/苍穹外卖文档/`（论文 / 开题 / 任务书）描述 2023 传统版「满香宜」，**不含现行 AI**，不得当现行 API 真相源；AI 叙事以代码 + `_conv/AI模块设计.md` 为准。
2. **推荐与浏览**：`item_views` 仅采集，**当前推荐算法不读浏览记录排序**；推荐候选池以启售菜品为主，套餐未稳定入池。
3. **天气能力**：客服提示词中的天气问答无独立 Weather Tool / 实时天气 API。
4. **支付为模拟**：`/client/order/payment` 直接置支付成功，未接微信支付。
5. **前端硬编码**：商家端 `views/customer-service/` 与小程序 `ai-assistant.vue` / `customer-service.vue` 写死 `http(s)://localhost:8848` / `ws://localhost:8848`，改后端主机需同步。
6. **遮蔽类**：`training-server/src/main/java/dev/langchain4j/` 为仿官方包名的自定义入库器，搜类时勿与官方混淆，无需求不动。
7. 用词规范：锦食坊（≠满香宜）、小锦、商家端 / 用户端；技术短名 LangChain4j / Qwen / RAG / SSE / JWT 双 Token。
8. **图片地址统一出口**：前端新增/修改图片引用一律走 `src/constants/minio.ts`，不得硬编码 MinIO 地址；素材库新增只改 MinIO `index.json`，前端零改动（ADR 0001）。

## 7. 已知未修复 backlog

按优先级（均为 2026-09 治理盘点确认，未修）：

- **P1** 客服链路整体无鉴权（§5 临时豁免）：HTTP + WS 均可被任意持 sessionId 者冒充 user/staff；需统一接 JWT 与 staff 身份来源。
- **P1** `/client/order/orderDetail`（`OrderServiceImpl.details`）无订单归属校验——该方法被 store 端共用，需区分端校验后才能收口。
- **P2** `paySuccess` 无前置状态校验（已取消 / 已完成订单可被再次「支付」）；store 端 `confirm` 未校验当前状态。
- **P2** `rejection` / `cancel` 中 `Orders.PAID.equals(orderDB.getStatus())` 误比较订单状态（原版苍穹外卖遗留，恒 false 死代码）。
- **P2** `ToolContext` 为全局静态 Map，AI 并发会话的 USER_ID / REQUEST_ID 互相覆盖，存在跨用户上下文串扰。
- **P2** `AiAssistantServiceImpl.createMockRecommendations` 兜底路径在菜品少于 5 条时数组越界。
- **P2** 支付为模拟实现（见 §6.4），接真实微信支付前不可上线收费。
- **P2** 端口冲突：本机 `8848` 被 Nfturbo 项目 Nacos 容器占用，后端临时以 `8849` 运行（商家端 `.env.development` 已同步）；长期方案（换端口 / 错时 / 停容器）待拍板。
- **P3** 商家端存量 `vue-tsc` 类型错误 40+（遍布 setmeal/workbench 等 15 文件，历次遗留）。
- **P3** 小程序 `type-check` 工具链损坏（vue-tsc 与 TypeScript 版本不兼容，报 `Search string not found`），构建 `build:mp-weixin` 不受影响。
- **P3** OSS 历史死链：`order_detail` / `setmeal_dish` / `shopping_cart` 共 145 条 image URL 指向已 404 的 OSS 对象（历史快照，保留未改；新数据写入 MinIO 自愈）。

## 8. 待确认项

- 客服鉴权方案（JWT 化后 staff 身份、会话归属校验）与豁免解除时间。
- 是否接入真实微信支付；若接，`paySuccess` 需补支付回调与幂等。
- `dev` profile 的 `application-dev.yml` 需按虚拟机方案自建，是否提供模板。
- 小程序端改造（硬编码地址、真机验收）暂缓。

## 9. 资产位置

| 资产 | 路径 |
|---|---|
| 毕设母版与 AI 补充稿（只读） | `docs/苍穹外卖文档/`（含 `_conv/` 转换 md 与 `AI模块设计.md`） |
| 架构决策记录 | `docs/adr/` |
| 知识备忘 | `docs/knowledge/` |
| 菜品图片 / 部署截图 / README 配图 | `assets/images/{dishes,deploy,readme}/` |
| UI 图与头像素材库备份（MinIO 全量镜像） | `assets/images/ui/`（`vue` · `uniapp` · `avatars` · `avatars-library/`） |
| 素材搬运 / 恢复脚本（不入库） | `temp/restore-dishes.ps1` · `temp/mirror-library.mjs` |
| 上游压缩包（不入库） | `assets/backup/` |
| 本地工作区 / 临时文件（不入库） | `.scratch/` · `temp/` |
