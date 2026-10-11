# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

「锦食坊」外卖系统后端。Spring Boot 3.4.5 + Java 17 + MyBatis + LangChain4j，Maven 多模块。工作区整体规范见项目根目录 `CLAUDE.md`。

## 启动与构建
- 无 `mvnw`，用系统 Maven 或 IDEA 直接运行启动类 `TrainingApplication`（training-server 模块）。
- 启动：`mvn -pl training-server -am spring-boot:run -DskipTests "-Dspring-boot.run.profiles=local"`，端口 **8848**（`application.yml` 缺省 `active: dev`，治理口径默认 `local` 本机中间件）。
- 构建：`mvn package`。
- 前置：导入 `sql/cq_take_out.sql` 到本机 MySQL；起 Redis / MongoDB / MinIO（`deploy/local/up-native.ps1`）。

## 模块结构
- `training-common`：公共工具（JwtUtil/HttpClientUtil/GreenTextScan/SensitiveWordUtil）、常量类（CachePrefixConstant/CustomerServiceConstant/JwtClaimsConstant/StatusConstant 等）、统一返回 `Result{code,msg,data}` 与 `PageResult`、异常体系。
- `training-pojo`：entity（24 个；其中 Comment/CommentLike/CommentRepay/CommentRepayLike 是 **MongoDB 文档**，其余对应 MySQL 18 表）、dto/vo/query（BasePageQuery + 各 PageQueryDTO）。
- `training-server`：业务，包 `com.fortunate` 下 controller（`client` 用户端 / `store` 商家端 / `upload`）、service+impl、mapper、config、interceptor、handler、aspect、task、websocket、chatService、tools、repository。

## 关键机制
- **JWT 双拦截器**：`JwtTokenStoreInterceptor` 拦 `/store/**`、`JwtTokenClientInterceptor` 拦 `/client/**`；请求头 `token`（商家端）/ `authentication`（客户端），密钥由环境变量 `JWT_STORE_SECRET` / `JWT_CLIENT_SECRET` 注入（yml 缺省 `training-dev-only` 仅限本地启动），失败返回 401；已排除 login 与公开查询路径。
- **公共字段填充**：`AutoFillAspect` 对带 `@AutoFill` 的 mapper 方法，INSERT 填 create_time/update_time/create_user/update_user，UPDATE 填 update_time/update_user，用户 ID 取 `BaseContext`。
- **MyBatis**：接口 + `resources/mapper/*.xml`，开启驼峰映射；PageHelper 分页（`startPage`+`Page`）；`useGeneratedKeys` 主键回填；自带 `MyBatisSqlInterceptor` 插件。
- **缓存**：Spring Cache（Redis），key 前缀 `CACHE_`；菜品相关缓存 key 见 `CachePrefixConstant`。
- **全局异常**：`GlobalExceptionHandler` 处理 BaseException 与 SQL 唯一键冲突（解析 Duplicate entry 字段名）。

## AI 模块（LangChain4j + 通义千问）
- **客服「小锦」** `chatService/ConsultantService`：流式输出 + Redis 会话记忆（20 条窗口）+ RAG + 工具调用，装配见 `config/LangChain4jConfiguration`，人设在 `resources/Consultant.txt`。
- 其他 AI 服务（对应各自提示词 txt）：`RecommenAssistantService`（推荐）、`SystemChatServer`（知识问答）、`AiCommentService`（评论创作）、`AiCommentExpandServiceAgent`（评论回复）。
- **工具** `tools/`：`DishRecommendTool`（随机 / 按偏好推荐）、`OrderTool`（查单 / 取消）。
- **记忆与向量均存 Redis**：`repository/RedisChatMemoryStore`（会话记忆 TTL 6h）、向量库 key 前缀 `training-embedding:`；RAG 检索 `EmbeddingStoreContentRetriever`（maxResults 2 / minScore 0.6）。
- 客服消息推送走 WebSocket：`/ws/customer-service/{sessionId}/{userType}`。
- 敏感词过滤：阿里云 Green（`GreenTextScan`）。

## 数据与任务
- MySQL `cq_take_out`（18 表）；MongoDB `training-comment`；Redis（缓存 + AI 记忆 + 向量库）；MinIO `training-take-out`（图片，走 `/upload`）。
- 定时任务 `task/OrderTask`：每分钟取消超 15 分钟未付款订单；每天 1 点把派送超 24h 订单置为已完成。`WebSocketTask` 每 5 秒广播在线状态。

## 测试
`src/test` 分四层：`base/`（`BaseIntegrationTest`，MockMvc + token 工具）、`integration/`（13 个接口集成测试，真实中间件）、`ai/`（3 个 AI 接口测试，真实调百炼）、`mock/`（2 个边界测试，JWT 拦截 + 参数校验）。另有 3 个历史集成测试留在根包（`EmbeddingTest` / `MinioTest` / `testMongoDB`，依赖外部服务与阿里云，基本已注释）。运行 `mvn test -pl training-server -am`，需先起本机中间件。

## 部署依赖
- 默认（`local` profile）：本机原生 MySQL80 / Redis / MongoDB / MinIO，脚本 `deploy/local/up-native.ps1`；`application-local.yml` 不入库需自建。
- 环境变量：`ALIBABA_CLOUD_ACCESS_KEY_ID`/`ALIBABA_CLOUD_ACCESS_KEY_SECRET`（OSS+敏感词）、`ALIBABA_CLOUD_BAILIAN_ACCESS_KEY`（百炼）、`Wechat_APP_ID`/`Wechat_SECRET`（小程序登录）、`JWT_STORE_SECRET`/`JWT_CLIENT_SECRET`（生产必须注入）。
- 回退方案：`do`（DigitalOcean 中间件 + SSH 隧道）/ `dev`（预配虚拟机 `192.168.64.100`，凭据见 gitignore 的 `docs/虚拟机各个镜像账号密码及下载地址.txt`）；对应 `application-do.yml` / `application-dev.yml` 均不入库。
