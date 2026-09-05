# AGENTS.md — 锦食坊（sky-ai）Agent 硬约束

「锦食坊」是基于苍穹外卖重构的培训 / 毕业设计三端 AI 外卖平台。领域事实与 backlog 见 `CONTEXT.md`；人读说明见 `README.md`。

## 三端地图

| 端 | 路径 | 技术栈 | 端口 |
|---|---|---|---|
| 后端 API | `code/web/training-web` | Spring Boot 3.4 · Java 17 · MyBatis · LangChain4j | **8848** |
| 商家端 | `code/vue/training-vue3-main` | Vue 3 · TS · Vite · Element Plus | **9527**（proxy → 8848） |
| 小程序 | `code/uniapp/training-uniapp-master` | uni-app · Vue 3 · uview-plus | 编译至微信开发者工具 |

## 构建与验证

```bash
# 后端（Maven 三模块，无 mvnw）
mvn -f code/web/training-web/pom.xml -DskipTests compile
# 商家端
cd code/vue/training-vue3-main && npm install && npm run build:prod
# 小程序静态校验
cd code/uniapp/training-uniapp-master && npm run type-check
```

后端启动需 MySQL（`cq_take_out`，SQL 在 `code/web/training-web/sql/`）+ Redis + MongoDB + MinIO；本机中间件脚本 `deploy/local/up-native.ps1`，Spring profile 用 `local`。

## 约定

- `temp/`、`.scratch/`、`.codegraph/`、`.gitnexus/`、`logs/` 一律不入库。
- 敏感配置一律环境变量注入（`ALIBABA_CLOUD_*`、`Wechat_*`、`JWT_*`、`TRAINING_*`）；`application-dev.yml` / `application-local.yml` / `application-do.yml` 不入库；对话与文档只写变量名，**不回显值**。
- **遮蔽警告**：`training-server/src/main/java/dev/langchain4j/store/embedding/` 下有仿官方包名的自定义类（`MyEmbeddingStoreIngestor` / `MyIngestionResult`，知识库入库专用）。搜索类名时勿与 LangChain4j 官方同名概念混淆；无需求不要动它。
- 三端各有子项目 `CLAUDE.md`（该端命令与风格），在子项目内开发时与根文件同时加载。

## 改动边界

- 最小变更，匹配既有风格，不顺手重构；文档与代码冲突时**以代码为准**并回写 CONTEXT。
- `docs/苍穹外卖文档/` 是 2023 传统毕设「满香宜」母版（无 AI），只读保留，不得当现行 API 依据。
- 未经用户明确要求不 `git commit` / 不 `push`。
- 已知临时豁免：`/store/customer-service/**` 与客服 WebSocket 暂不鉴权（业务决策，见 CONTEXT backlog）。
