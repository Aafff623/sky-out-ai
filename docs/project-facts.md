# project-facts.md — 锦食坊（sky-ai）已验证工程事实

TTA 治理资产：Agent 硬约束与构建命令见 `AGENTS.md`；领域词汇见 `GLOSSARY.md`；任务与 backlog 流转见 `docs/agents/issue-tracker.md`（GitHub Issues）；人读说明见 `README.md`。文档与代码冲突时**以代码为准**并回写本文件。

## 1. 项目定位与三端

「锦食坊」是基于苍穹外卖重构的**培训 / 毕业设计**项目：传统外卖业务 + **LangChain4j + 通义千问** AI 能力。前后端分离，三端代码在 `code/`。

| 端 | 路径 | 技术栈 | 默认端口 |
|---|---|---|---|
| 后端 API | `code/web/training-web` | Spring Boot 3.4.5 · Java 17 · MyBatis · PageHelper · LangChain4j | **8848** |
| 商家管理端 | `code/vue/training-vue3-main` | Vue 3 · TypeScript · Vite 5 · Element Plus · Pinia · ECharts | **9527**（proxy `/dev-api` → 8848） |
| 用户小程序 | `code/uniapp/training-uniapp-master` | uni-app · Vue 3 · uview-plus · UnoCSS | 编译至微信开发者工具 |

后端 Maven 三模块：`training-common`（工具 / 常量 / `Result`）· `training-pojo`（entity/dto/vo，评论类为 MongoDB `@Document`）· `training-server`（业务，启动类 `TrainingApplication`），包根 `com.fortunate`。

规模快照（2026-10-11 复核）：后端 REST Controller **33 个**（`client` 16 · `store` 15 · `upload` 1 · `health` 1）；`@Test` 标记 69 个；pojo 实体 24 个（4 个 MongoDB 文档）；MySQL 18 表（`sql/cq_take_out.sql`，路径在 `code/web/training-web/sql/`）。框架版本以 pom **parent `spring-boot-starter-parent` 3.4.5** 为准；注意 `training-server` 的 `spring-boot-maven-plugin` 仍钉 **2.7.3**（上游模板遗留，与 3.4.5 parent 混用，可跑，择机对齐）。

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

**运行时**：默认本机原生中间件（MySQL80 + Scoop Redis/Mongo/MinIO），Spring profile **`local`**，脚本 `deploy/local/up-native.ps1`；profile `do` 为 DigitalOcean 中间件 + SSH 隧道回退方案；`dev` 指向预配虚拟机 `192.168.64.100`（`application.yml` 缺省 `active: dev`，配置文件不入库，需自建）。

**必填环境变量**（名称级）：`ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET`（OSS + 敏感词）、`ALIBABA_CLOUD_BAILIAN_ACCESS_KEY`（百炼）、`Wechat_APP_ID` / `Wechat_SECRET`（小程序登录）、`JWT_STORE_SECRET` / `JWT_CLIENT_SECRET`（生产必须注入；yml 缺省 `training-dev-only` 仅供本地开发启动）。AI 模型：`qwen3.7-plus` / `text-embedding-v4`（do/local 可用变量覆盖）。LangChain4j 版本 `1.10.0-beta18`。

## 5. 认证与安全形态

- JWT 双拦截器：商家端头 **`token`** 拦 `/store/**`；用户端头 **`authentication`** 拦 `/client/**`；失败 401。放行：两侧 login、公开分类 / 菜品 / 套餐、`/client/shop/status`、`/client/shopInfo`。
- **豁免已解除（2026-10-11，issue #1）**：`/store/customer-service/**` 已纳入商家端 JWT 拦截；客服 WebSocket `/ws/customer-service/{sessionId}/{userType}` 握手校验 URL 携带的 `?token=`（user 校验会话归属、staff 取 empId 并用于落库），未授权连接以 1008 关闭。
- 密钥治理：入库配置一律 `${ENV:}` 占位；`application-dev.yml` / `application-local.yml` / `application-do.yml` 不入库（本地保留可跑，新人仿造）。

## 6. 领域约束（工程边界）

1. **推荐与浏览**：`item_views` 仅采集，**当前推荐算法不读浏览记录排序**；推荐候选池以启售菜品为主，套餐未稳定入池。
2. **天气能力**：客服提示词中的天气问答无独立 Weather Tool / 实时天气 API。
3. **支付为模拟**：`/client/order/payment` 直接置支付成功，未接微信支付。
4. **前端硬编码**：商家端 `views/customer-service/` 与小程序 `ai-assistant.vue` / `customer-service.vue` 写死 `http(s)://localhost:8848` / `ws://localhost:8848`，改后端主机需同步。
5. **图片地址统一出口**：前端新增/修改图片引用一律走 `src/constants/minio.ts`，不得硬编码 MinIO 地址；素材库新增只改 MinIO `index.json`，前端零改动（ADR 0001）。

## 7. 已知未修复问题（backlog）

**任务状态以 GitHub Issues 为准（`docs/agents/issue-tracker.md`）。2026-10-11 已修复并关闭：#1 客服鉴权、#2 查单越权、#3 状态机校验、#4 退款条件、#5 工具上下文隔离、#6 兜底越界、#8 端口回滚 8848、#9 商家端类型清零、#10 type-check 工具链、#11 OSS 死链置空。当前仅剩：**

- **P2** #7 支付为模拟实现，接真实微信支付前不可上线收费（需商户资质等外部资源，owner 拍板是否接入）。
- **P3** #12 小程序存量类型错误 927 个（type-check 工具链修复后暴露；修复 vs 放宽路线待拍板）。

## 8. 待确认项

- 是否接入真实微信支付（#7）；若接，需补支付回调验签与幂等。
- 小程序存量类型错误处理路线（#12）：按目录分批修复 vs 放宽部分检查。
- `dev` profile 的 `application-dev.yml` 需按虚拟机方案自建，是否提供模板。

## 9. 资产位置

| 资产 | 路径 |
|---|---|
| 领域词汇表 | `GLOSSARY.md` |
| Agent 硬约束 / 构建命令 | `AGENTS.md` |
| Matt 工程技能配置（tracker / 领域 / triage） | `docs/agents/` |
| 架构决策记录 | `docs/adr/` |
| 毕设母版与 AI 补充稿（只读） | `docs/苍穹外卖文档/`（含 `_conv/` 转换 md 与 `AI模块设计.md`） |
| 菜品图片 / 部署截图 / README 配图 | `assets/images/{dishes,deploy,readme}/` |
| UI 图与头像素材库备份（MinIO 全量镜像） | `assets/images/ui/`（`vue` · `uniapp` · `avatars` · `avatars-library/`） |
| 素材搬运 / 恢复脚本（不入库） | `temp/restore-dishes.ps1` · `temp/mirror-library.mjs` |
| 上游压缩包（不入库） | `assets/backup/` |
| 本地工作区 / 临时文件（不入库） | `.scratch/` · `temp/` |
