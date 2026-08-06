# AGENTS.md

> **Output Style**: `humanizer-output-style` skill — 统一语气风格与去 AI 味配置。加载路径：`skills/humanizer-output-style/SKILL.md`（用户级 Claude skills）。
>
> **Windows / 回答格式**：本仓 `.cursor/rules/` 已同步 `windows-path-discipline.mdc`、`windows-shell-discipline.mdc`、`answer-format.mdc`、`AGENTS.mdc`、`commit-history.mdc`（均为 `alwaysApply: true`）。

本文件是跨工具 Agent 的**硬约束与任务流摘要**，面向对本项目零了解的编码 Agent。领域术语与技术事实以根 `CONTEXT.md` / `CONTEXT-MAP.md` 为准；共享用词以 `LANGUAGES.md` 为准；人读运行说明见根 `README.md`。

## 项目概览

「锦食坊」（sky-ai）是基于苍穹外卖重构的**培训 / 毕业设计**项目：传统外卖点餐履约 + **LangChain4j + 通义千问（Qwen）** AI 能力。前后端分离，三端代码全部在 `code/` 下：

| 端 | 路径 | 技术栈 | 默认端口 |
|---|---|---|---|
| 后端 / API | `code/web/training-web` | Spring Boot 3.4.5 · Java 17 · MyBatis · LangChain4j | **8848** |
| 商家管理端 | `code/vue/training-vue3-main` | Vue 3 · TypeScript · Vite 5 · Element Plus · Pinia · ECharts | **9527**（proxy `/dev-api` → 8848） |
| 用户小程序 | `code/uniapp/training-uniapp-master` | uni-app · Vue 3 · uview-plus · Pinia | 编译至微信开发者工具 |

业务能力：传统外卖（分类 / 菜品 / 套餐 / 购物车 / 下单支付 / 订单履约 / 员工 / 报表，支持外卖与堂食）+ AI 五大模块（智能客服「小锦」、菜品推荐、评论扩写与自动回复、知识库 RAG、敏感词过滤）。

**关键区分**：`docs/苍穹外卖文档/` 中的论文 / 开题报告描述的是 2023 年传统版系统「满香宜」，**不含现行 AI，不得当作现行 API 真相源**。实现以 `code/` + `docs/苍穹外卖文档/_conv/AI模块设计.md` + 根 `CONTEXT.md` 为准。

## 单一事实源

| 事实类型 | 唯一入口 |
|---|---|
| 领域术语与硬约束 | `CONTEXT.md` + `docs/contexts/*`（经 `CONTEXT-MAP.md`） |
| 共享用词 | `LANGUAGES.md` |
| Agent 硬约束 / 任务流 | 本文件 + `docs/agents/workflow.md` |
| 人读摘要 | `README.md` |
| 毕设/AI 论文补充 | `docs/苍穹外卖文档/_conv/`（尤其 `AI模块设计.md`） |

禁止同一事实在多处各写一版且互相漂移。禁止再维护 `docs/agents/language.md` / `docs/agents/context.md`。

## 仓库地图（路径表）

| 路径 | 含义 |
|---|---|
| `code/web/training-web` | 后端（Spring Boot，端口 8848；Maven 三模块） |
| `code/vue/training-vue3-main` | 商家管理端（Vite，端口 9527） |
| `code/uniapp/training-uniapp-master` | 用户小程序（uni-app → 微信） |
| `deploy/local/` | 本机原生中间件脚本与数据（`up-native.ps1` / `down-native.ps1`；`data/`、`logs/` 不入库） |
| `docs/` | 毕设母版、部署说明、Agent 流程、ADR、outputs |
| `assets/` | 媒体与只读备份（backup / 菜品图 / 部署图 / README 配图） |
| `.scratch/<feature>/` | 本地 Issue / PRD（不入库） |
| `scripts/gen-readme-diagrams.py` | README 说明图生成脚本 |

每个子项目根有自己的 `CLAUDE.md`（`code/{web,vue,uniapp}/**/CLAUDE.md`），管该端的命令与代码约定；在子项目内开发时与本文件同时加载。

## 构建与运行命令

### 后端 `code/web/training-web`

Maven 多模块（无 `mvnw`，用系统 Maven 或 IDEA）：`training-common`（工具 / 常量 / `Result`）、`training-pojo`（entity/dto/vo，评论类为 MongoDB `@Document`）、`training-server`（业务，启动类 `TrainingApplication`）。

```bash
mvn -pl training-server -am spring-boot:run   # 启动，端口 8848，默认 profile dev
mvn spring-boot:run -DskipTests "-Dspring-boot.run.profiles=local"  # 本机原生中间件（在 training-server 目录）
mvn package                                    # 构建
mvn test                                       # 测试（见「测试」节）
```

前置：导入 `sql/cq_take_out.sql` 到 MySQL；满足部署依赖（见下）。

### 商家端 `code/vue/training-vue3-main`

```bash
npm install
npm run dev            # 开发，端口 9527，proxy /dev-api → http://localhost:8848
npm run build:prod     # 生产构建 + vue-tsc 类型检查
npm run lint:eslint / lint:stylelint / lint:prettier
npm run commit         # git-cz 规范提交（husky + commitlint + lint-staged）
```

Node ≥ 18。无单元测试脚本。

### 小程序 `code/uniapp/training-uniapp-master`

```bash
npm install
npm run dev:mp-weixin   # 编译到 dist/dev/mp-weixin
npm run type-check      # vue-tsc --noEmit
```

编译后用**微信开发者工具导入 `dist/dev/mp-weixin`** 运行（`manifest.json` 已配真实 appid）。Node ≥ 18。

### 本机中间件（当前默认运行时，2026-08）

本机原生中间件取代预配虚拟机：MySQL80（Windows 服务）+ Scoop 安装的 Redis / MongoDB / MinIO，Spring profile **`local`**。启动脚本：

```powershell
powershell -File deploy\local\up-native.ps1    # 起 Redis/Mongo/MinIO（MySQL80 需已运行）
powershell -File deploy\local\down-native.ps1  # 停止（不停 MySQL80）
```

详见 `deploy/local/README.md`。`local` 下向量库为 `InMemoryEmbeddingStore`（无 RediSearch），进程重启后需重新 embed 知识库。Mode A（DigitalOcean + SSH 隧道，profile `do`）仅作回退；预配虚拟机 `192.168.64.100`（profile `dev`）为更早方案，凭据见 `docs/虚拟机各个镜像账号密码及下载地址.txt`（gitignore）。

### 部署依赖（必填环境变量）

`ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET`（OSS + 敏感词 Green）、`ALIBABA_CLOUD_BAILIAN_ACCESS_KEY`（百炼大模型）、`Wechat_APP_ID` / `Wechat_SECRET`（小程序登录）。AI 模型：`qwen-plus-latest` / `text-embedding-v4`。完整部署步骤见 `docs/readme.txt` 与 `assets/images/deploy/`；部署成功后需在商家端重新上传 `assets/images/dishes/` 中的菜品图。

## 代码组织与关键机制

### 后端（包根 `com.fortunate`，模块 `training-server`）

- **Controller 分区**：`controller/client`（用户端 `/client/**`）、`controller/store`（商家端 `/store/**`）、`controller/upload`（`/upload`）；敏感词走 `/sensitive`（无 store 前缀）。
- **JWT 双拦截器**：`JwtTokenStoreInterceptor` 拦 `/store/**`（头 `token`）、`JwtTokenClientInterceptor` 拦 `/client/**`（头 `authentication`）；secret 均为配置中的 `training`，失败 401；login 与公开查询路径已排除。
- **MyBatis**：接口 + `resources/mapper/*.xml`，驼峰映射，PageHelper 分页，`useGeneratedKeys` 主键回填。
- **公共字段填充**：`AutoFillAspect` 对 `@AutoFill` 的 mapper 方法自动填 create/update 时间与操作人（取 `BaseContext`）。
- **缓存**：Spring Cache（Redis），key 前缀 `CACHE_`。
- **AI 编排**：`config/LangChain4jConfiguration` 显式装配客服「小锦」（流式 + Redis 会话记忆窗口 20 条 + RAG + Tools）；工具在 `tools/`（`DishRecommendTool`、`OrderTool`）；人设在 `resources/Consultant.txt`。记忆 key `training:session:messages:{sessionId}`（TTL 6h）；向量前缀 `training-embedding:`；RAG `maxResults=2` / `minScore=0.6`。
- **WebSocket**：客服消息 `/ws/customer-service/{sessionId}/{userType}`；SSE 聊天事件码 DATA(1001)/STOP(1002)/PARAM(1003)。
- **定时任务**：`OrderTask`（每分钟取消超 15 分钟未付款订单；每天 1 点把派送超 24h 订单置完成）、`WebSocketTask`（每 5 秒广播在线状态）。

### 数据存储

MySQL `cq_take_out`（18 表）· MongoDB `training-comment`（评论体系，`MongoTemplate`）· Redis（缓存 + AI 会话记忆 + 向量库）· MinIO `training-take-out` bucket（菜品图、知识库文件）。

### 前端两端约定

- **商家端**：API 层 `src/api/*.ts` 为 `BASE_URL` + `class XxxAPI` + default export，统一走 `utils/request.ts`（注入 `headers.token`，`code===200` 返回 data，401 清 token 刷新）；hash 路由 + Layout 懒加载；登录演示账号 `admin` / `123456`。
- **小程序**：请求封装 `src/utils/request/`（`$http`，注入 `authentication` 头；普通超时 30s、AI 聊天 45s 流式）；登录 `uni.login` → `POST /client/user/login`；状态 `user` / `shopping-cart` / `shop` / `order-countdown`（`pinia-plugin-unistorage` 持久化）。
- **硬编码警告**：商家端 `views/customer-service/` 与小程序 `ai-assistant.vue` / `customer-service.vue` 多处写死 `http://localhost:8848` / `ws://localhost:8848`，改后端主机必须同步这些页面。

## 代码风格

- **Java**：Spring Boot 分层（controller/service/mapper），Lombok，统一返回 `Result{code,msg,data}` / `PageResult`；遵循既有模块的包结构与命名。
- **商家端 TS/Vue**：Prettier 双引号 + 分号（printWidth 80）；ESLint 已放宽（`no-explicit-any`、`no-unused-vars` 关闭）；提交走 Conventional Commit（`npm run commit` 调 git-cz）。
- **小程序**：Vue 3 `<script setup>` + TypeScript；uview-plus 组件 + UnoCSS（`@uni-helper/unocss-preset-uni`）。
- **输出语气**：见 `docs/agents/voice.md` —— 简体中文，代码 / 路径 / 命令保持原文；技术说明优先表格 / 清单。
- **用词**：必须用 `LANGUAGES.md` 与 `CONTEXT.md` 的规范词（如「锦食坊」≠「满香宜」、「小锦」、商家端 / 用户端），避免同义漂移。

## 测试

- **后端**：`src/test` 分四层——`base/`（基类，`BaseIntegrationTest` 提供 MockMvc + token 获取 + 断言工具）、`integration/`（42 个接口集成测试，真实中间件，`@ActiveProfiles("local")`）、`ai/`（5 个 AI 接口测试，真实调百炼）、`mock/`（18 个边界测试，JWT 拦截 + 参数校验）。另有 3 个历史集成测试（`EmbeddingTest` / `MinioTest` / `testMongoDB`，大部分已注释）。运行：`mvn test -pl training-server -am`，需先起本机中间件。
- **商家端**：无单元测试脚本；质量门禁为 `npm run build:prod`（含 vue-tsc 类型检查）+ lint 三件套 + husky/lint-staged 提交钩子。
- **小程序**：无测试脚本；用 `npm run type-check`（vue-tsc --noEmit）做静态校验，真机 / 微信开发者工具手动验证。
- 验证改动时：后端跑 `mvn test`；前端以 type-check / lint / 构建通过为准。

## 任务流门禁（摘要）

```text
Issue（.scratch/<feature>/）
  →（可选）docs/outputs/report/{theme}/
  → docs/outputs/prd/{theme}/prd.md（draft）
  → 用户 approved
  → docs/outputs/handoff/{theme}/…（覆盖式更新）
  → 实施 → awaiting-review【停】
  → 用户通过 → commit → docs/outputs/commit-history/{branch}/
```

硬约束：

1. **PRD 未批准不写功能代码**（project-init / 骨架与文档对齐除外）。
2. Handoff **覆盖式更新**（旧文件直接删除）；本仓默认只启用场景 **A**（任务交接快照），详见 `docs/agents/deliver.md`。
3. Review 先于 commit；未经用户明确要求不 `git commit` / 不 `push`。
4. 密钥与凭据不入库（见 `.gitignore` 与「安全」节）。
5. 修改代码遵循最小变更；业务实现以 `code/` 为准，论文母版描述「满香宜」传统版时不得当成现行 API。

Issue tracker 为本地 markdown：`.scratch/<feature-slug>/PRD.md` + `issues/<NN>-<slug>.md`，状态用 `Status:` 行（五种 canonical 标签见 `docs/agents/triage-labels.md`）。theme 用 kebab-case。

## Review 门禁

- 改动可追溯到 Issue / PRD / 用户请求。
- 不引入未请求的抽象层；不擅自「顺手重构」。
- 文档与代码冲突时：**以代码为准**，并回写 CONTEXT / AI 补充稿（若影响叙事）。
- commit 信息：个人项目可用中文 Conventional Commit（`feat|fix|docs|chore(scope): …`）。

## 安全注意事项

- **绝不提交**：`.env*`、`*AccessKey*.csv` / `*apiKey*.csv`、虚拟机凭据文件 `docs/虚拟机各个镜像账号密码及下载地址.txt`、`application-dev.yml`（含明文秘密，已 gitignore）、`*.pem` / `*.p12`、`assets/backup/`（大体积）。
- 对话与交付文档中只写**环境变量名**与「见虚拟机凭据文件」，不复述密码 / API Key。
- `application-local.yml` / `application-do.yml` 等含连接信息的配置文件，改动时留意不引入新的明文秘密。
- 描述 AI 能力时与代码一致，不夸大：天气为提示词口语能力（无实时 Weather Tool）；`item_views` 仅采集、**不参与推荐排序**；推荐候选池以启售菜品为主。
