# CONTEXT — api（后端）

路径：`code/web/training-web` · 启动类 `TrainingApplication` · 端口 **8848** · profile 默认 `dev`。  
启动：`mvn -pl training-server -am spring-boot:run`（无 mvnw）。

## 模块

| Maven 模块 | 职责 |
|---|---|
| `training-common` | JwtUtil、GreenTextScan、SensitiveWordUtil、Result、常量 |
| `training-pojo` | entity / dto / vo；评论相关为 MongoDB `@Document` |
| `training-server` | controller / service / mapper / AI / websocket / task |

包根：`com.fortunate`。Controller 分区：`client`（用户）· `store`（商家）· `upload`。  
前缀：`/client/**` · `/store/**` · `/upload` · `/sensitive`（无 store 前缀）· `/ws/customer-service/{sessionId}/{userType}`。

## 认证

- 商家：`JwtTokenStoreInterceptor` → 头 **`token`**，拦 `/store/**`
- 客户端：`JwtTokenClientInterceptor` → 头 **`authentication`**，拦 `/client/**`
- secret 配置为 `training`；失败 401
- 常见放行：两侧 login、公开分类/菜品、`/client/shop/status`、`/client/shopInfo`
- 注意：`/store/customer-service/**` 当前亦在排除列表（临时）；敏感词走 `/sensitive`

## AI（要点）

| 类 / 入口 | 职责 |
|---|---|
| `ConsultantService` | `AiServices.builder`：流式 + Redis 记忆（窗口 20）+ RAG + Tools；人设 `Consultant.txt` |
| `RecommenAssistantService` | `@AiService`；偏好推荐 JSON |
| `DishRecommendTool` / `OrderTool` | 唯一注册的业务 Tools（随机/偏好推荐；查单/取消） |
| `AiComment*` / `SystemChatServer` | 评论扩写与自动回复 |
| `POST /client/ai-assistant/chat` | SSE：`Flux<ChatEventVO>` DATA(1001)/STOP(1002)/PARAM(1003) |
| `POST /client/ai-assistant/recommend` | 点餐页偏好推荐（与 Tool 共享算法） |
| `POST /client/ai-comment/expand` | type 1~4：帮写/续写/润色/精简 |
| `POST /store/knowledgeBase/embed` | 知识库向量化 |
| `POST /client/view/increment/dish/{id}` | 写 `item_views`（仅采集） |

记忆 key：`training:session:messages:{sessionId}`，TTL **6h**。RAG：`maxResults=2`，`minScore=0.6`；向量前缀 `training-embedding:`。模型：`qwen-plus-latest` / `text-embedding-v4`。

**硬约束**：天气为提示词能力，**无 Weather Tool**；推荐候选池为启售菜品，降级为关键词→热度兜底；**不读 `item_views` 排序**。

## 数据

- MySQL `cq_take_out`：**18** 表（`sql/cq_take_out.sql`）
- MongoDB `training-comment`：评论体系（`MongoTemplate`）
- Redis：缓存 + 会话记忆 + 向量
- MinIO `training-take-out`：图片与知识库文件

## 任务

`OrderTask`：超时未付款取消；派送超时完成。`WebSocketTask`：广播在线状态。

## 测试

`src/test`（`training-server`）分四层，共 **22 个测试类 / 65 个用例**，基类用 `@SpringBootTest` + `@ActiveProfiles("local")` 起真实 Spring 上下文连真实中间件。**实测全绿（0 失败 0 错误 0 跳过）**。

| 层 | 路径 | 内容 | 外部依赖 |
|---|---|---|---|
| 基类 | `base/BaseIntegrationTest` | 提供 `MockMvc`、商家端 token（`admin`/`123456` 真实登录）、用户端 token（`JwtUtil` 造 `userId=1`）、`parseResult` / `assertSuccess` / `assertError` 工具；`@MockBean ServerEndpointExporter` 规避 MockMvc 无 Servlet 容器 | local 中间件 |
| 集成 | `integration/*ApiTest`（13 个，42 用例） | 商家端 + 用户端全接口集成：员工 / 分类 / 菜品 / 套餐 / 订单 / 评论 / 客服 / 知识库 / 敏感词 / 报表 / 工作台 / 上传 / 健康检查等，含正反面与 401 边界 | local 中间件 |
| AI | `ai/*ApiTest`（3 个，5 用例） | 真实调百炼：客服对话、菜品推荐、评论扩写 | local 中间件 + **百炼 API** |
| 边界 | `mock/*Test`（2 个，18 用例） | `JwtInterceptorTest`（store/client 双侧无 token、错 token → 401）、`ParamValidationTest`（缺参 / 非法参数） | local 中间件 |
| 历史 | 根包 `EmbeddingTest` / `MinioTest` / `testMongoDB` | 早期集成测试，大部分已注释 | — |

**运行**：`mvn test -pl training-server -am`（无 `mvnw`，需系统 Maven）。排除 AI（省百炼配额 / 提速）：`mvn test -pl training-server -am -Dtest='!com.fortunate.ai.*' -Dsurefire.failIfNoSpecifiedTests=false`。

**测试配置**：`test/resources/application-test.yml` 仅设 `spring.profiles.active=local` + 调低日志级别；实际中间件连接走 `application-local.yml`。

**硬约束**：
- 集成 / 边界层强依赖本机中间件（MySQL `cq_take_out` 含 `admin` 账号 · Redis · Mongo）；MinIO 未起时上传类用例会失败。
- AI 层真实消耗百炼配额且较慢，CI/本地提速默认排除。
- Java 版本：Maven 运行于 JDK 21 可正常编译（目标 Java 17 字节码）；不影响测试结果。
- 改后端接口时，对应 `integration/*ApiTest` 应同步更新并保持 `mvn test` 绿。

## 硬约束

- 配置含密钥的文件勿提交（见根 `.gitignore`）
- 论文「满香宜」接口叙述不得直接当现行 API
- 子项目约定见同目录树下 `CLAUDE.md`；用词见根 `LANGUAGES.md`
