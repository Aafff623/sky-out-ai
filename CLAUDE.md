# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

「锦食坊」外卖平台 —— 基于苍穹外卖重构，集成 LangChain4j + 通义千问 AI 能力的培训/毕业设计项目。前后端分离，三端代码：

| 端 | 路径 | 技术栈 |
|---|---|---|
| 后端 | `code/web/training-web` | Spring Boot 3.4.5 + Java 17 + MyBatis + LangChain4j |
| 商家管理端 | `code/vue/training-vue3-main` | Vue 3 + TypeScript + Vite + Element Plus |
| 用户小程序 | `code/uniapp/training-uniapp-master` | uni-app + Vue 3 + uview-plus |

非代码资料在 `docs/`（论文/开题报告/任务书、菜品图片素材、部署说明），所有压缩包在 `backup/`。

## 子项目规范（两级 CLAUDE.md）

在某个子项目内开发时，**同时加载**本文件与该子项目的 `CLAUDE.md`：本文件管工作区整体（资产/文档位置、部署依赖、端间关系），子项目文件管该端的命令与代码约定。

| 端 | 项目 CLAUDE.md 位置 |
|---|---|
| 后端 | `code/web/training-web/CLAUDE.md` |
| 商家管理端 | `code/vue/training-vue3-main/CLAUDE.md` |
| 用户小程序 | `code/uniapp/training-uniapp-master/CLAUDE.md` |

## 核心架构

### 后端分层（Maven 三模块）
- `training-common`（公共类）、`training-pojo`（entity/DTO/VO）、`training-server`（业务，启动类 `TrainingApplication`，端口 8848）。
- 包 `com.fortunate`：`controller/client`（用户端）、`controller/store`（商家端）、`controller/upload`；`service`+`impl`、`mapper`（SQL 在 `resources/mapper/*.xml`）；`config`、`interceptor`（JWT 拦截）、`handler`（全局异常）、`aspect`（`AutoFillAspect` 自动填充公共字段）、`task`（定时任务）、`websocket`（客服消息推送）。

### AI 模块（LangChain4j + 通义千问）
- **AI 客服「小锦」** `chatService/ConsultantService`：`LangChain4jConfiguration` 中用 `AiServices.builder` 显式装配 —— 流式模型 + Redis 会话记忆（窗口 20 条）+ RAG 检索 + 工具调用。系统提示词在 `resources/Consultant.txt`（定义了 4 项技能与兜底转人工策略）。
- **工具** `tools/`：`DishRecommendTool`（随机推荐 / 按用户偏好推荐，偏好推荐调用基于 `item_views` 浏览记录的智能推荐）、`OrderTool`（查单 / 取消订单）。
- **推荐助手** `RecommenAssistantService`：`@AiService` 注解式装配，仅聊天模型。
- **评论 AI 扩写/回复** `chatService/AiComment*`：扩写用户简评、自动回复评论。
- **知识库** `KnowledgeBase*`：`knowledge_base` 表内容向量化写入 Redis 向量库（key 前缀 `training-embedding:`），检索用 `EmbeddingStoreContentRetriever`（maxResults 2 / minScore 0.6），防客服幻觉。
- 敏感词过滤走阿里云 Green。

### 数据存储分层
- **MySQL** `cq_take_out`（18 张表：基础外卖 11 张 + 客服会话/浏览记录/知识库/取餐码/敏感词/店铺信息）。
- **MongoDB** `training-comment`（评论、对话等非结构化数据）。
- **Redis**（缓存 + AI 对话记忆 + 向量库）。
- **MinIO** `training-take-out` bucket（菜品图片，商家端上传走 `controller/upload`）。

### 认证
JWT 双 token：商家端请求头 `token`、客户端请求头 `authentication`；客户端 token 支持自动刷新。

## 开发命令

### 后端 `code/web/training-web`
```bash
mvn spring-boot:run   # 启动（需先导入 sql/cq_take_out.sql，并满足下方部署依赖）
mvn package           # 构建
mvn test              # 测试（EmbeddingTest/MinioTest/testMongoDB 为依赖外部服务的集成测试）
```
无 `mvnw` wrapper，用系统 Maven 或 IDEA 直接运行。

### 商家端 `code/vue/training-vue3-main`
```bash
npm install
npm run dev           # 开发（vite serve，默认自动开浏览器）
npm run build:prod    # 生产构建 + vue-tsc 类型检查
npm run lint:eslint / lint:stylelint / lint:prettier
npm run commit        # git-cz 规范提交（配 husky + commitlint）
```
无单元测试脚本。

### 小程序 `code/uniapp/training-uniapp-master`
```bash
npm install
npm run dev:mp-weixin   # 编译微信小程序到 dist/dev/mp-weixin
# 用微信开发者工具导入 dist/dev/mp-weixin 运行
npm run type-check      # vue-tsc --noEmit
```

## 部署依赖（必须满足才能启动）

- 预配虚拟机 `192.168.64.100`（MySQL/Redis/MongoDB/MinIO 已装），镜像下载地址与全部账号密码见 `docs/虚拟机各个镜像账号密码及下载地址.txt`。
- 环境变量必填：`ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET`（OSS 与敏感词）、`ALIBABA_CLOUD_BAILIAN_ACCESS_KEY`（百炼大模型）、`Wechat_APP_ID` / `Wechat_SECRET`（小程序登录）。
- 配置：`application.yml`（通用）+ `application-dev.yml`（dev 环境，指向虚拟机）；AI 模型为 `qwen-plus-latest` / `text-embedding-v4`。
- 完整部署步骤见 `docs/readme.txt` 与 `docs/readme_img/`；部署成功后需在商家端重新上传全部菜品图片。

## 注意事项

- `docs/苍穹外卖文档/` 中的论文/开题报告/任务书写于 2023 年，描述的是**基础版外卖系统**（系统名「满香宜」），不含 AI 功能，与当前代码不对应；代码侧以 README 与源码为准。
- 商家端、小程序各自维护独立的环境配置（`settings.ts` 等）。
