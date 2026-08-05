<div align="center">

# 锦食坊 · sky-ai

*AI-enhanced takeout platform with intelligent assistant XiaoJin*

基于苍穹外卖深度重构的培训 / 毕业设计项目：完整外卖点餐履约链路 + LangChain4j / 通义千问五大 AI 模块（智能客服、菜品推荐、评论扩写、知识库 RAG、敏感词过滤）。

![Banner](assets/images/readme/banner.png)

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge)](code/web/training-web)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-green?style=for-the-badge)](code/web/training-web)
[![Vue 3](https://img.shields.io/badge/Vue-3-brightgreen?style=for-the-badge)](code/vue/training-vue3-main)
[![uni-app](https://img.shields.io/badge/uni--app-WeChat-blue?style=for-the-badge)](code/uniapp/training-uniapp-master)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-Qwen-red?style=for-the-badge)](docs/苍穹外卖文档/_conv/AI模块设计.md)

[为什么](#为什么需要锦食坊) · [功能](#功能) · [Preview](#preview) · [演示](#演示--showcase) · [快速开始](#快速开始) · [架构](#架构) · [主链路](#用户主链路) · [目录](#目录结构) · [路线图](#路线图) · [文档](#文档)

</div>

---

> [!TIP]
> `docs/苍穹外卖文档/` 里的论文和开题报告是 2023 年传统版「满香宜」，没有 AI 功能。实现细节以 `code/` 源码 + [`AI模块设计.md`](docs/苍穹外卖文档/_conv/AI模块设计.md) + 根 [`CONTEXT.md`](CONTEXT.md) 为准。

## 为什么需要锦食坊

校园和培训场景里的外卖系统，往往止步于「能下单」：

- 客服只能人工值班，夜间咨询没人管
- 推荐靠静态列表，说不清「为什么推荐这道菜」
- 评论短、运营难沉淀；内容安全缺少可配置的词库
- 毕设论文和代码脱节，接手的人找不到单一事实源

锦食坊的解法：

| 组件 | 职责 |
| --- | --- |
| 传统外卖内核 | 分类 / 菜品 / 套餐 / 购物车 / 订单履约 / 堂食取餐 / 员工与报表 |
| AI 客服「小锦」 | 7×24 流式多轮对话 + Redis 会话记忆 + RAG 知识检索；推荐和查单走业务 Tools |
| 推荐与评论 AI | 自然语言偏好推荐（语义理解 + 规则降级）；评论帮写 / 续写 / 润色 / 精简 + 异步自动回复 |
| 知识库 / 敏感词 | 商家文档上传后自动向量化；阿里云 Green + 本地 DFA 双重内容审核 |
| 人工兜底 | AI 超范围时转人工客服（WebSocket 实时通道） |

```text
User (WeChat Mini Program) ──JWT authentication──┐
Merchant Admin (Vue3) ───────JWT token───────────┼──► Spring Boot :8848
                                                  ├── MySQL / Redis / MongoDB / MinIO
                                                  └── Bailian Qwen · Green · WeChat
```

---

## 功能

![Features](assets/images/readme/features.png)

| 领域 | 能力 | 包含内容 | 主要入口 |
| --- | --- | --- | --- |
| 外卖 | 点餐履约 | 分类浏览、菜品/套餐、购物车、外卖/堂食切换、支付与订单状态流转 | `code/uniapp/.../pages/index` · `.../order` |
| 外卖 | 商家运营 | 工作台数据看板、订单管理、菜品/套餐/分类管理、员工权限、统计报表导出 | `code/vue/.../src/views/*` |
| AI | 智能客服 | SSE 流式输出、Redis 记忆窗口 20 条、RAG 检索增强、Dish/Order Tools、转人工兜底 | `ConsultantService` · `pages/message/ai-assistant` |
| AI | 菜品推荐 | 偏好语义理解 + 关键词/热度双降级；浏览行为上报采集（当前不参与排序） | `/client/ai-assistant/recommend` |
| AI | 评论助手 | 帮写 / 续写 / 润色 / 精简四种模式；发评后 AI 异步自动回复 | `/client/ai-comment/expand` |
| AI | 知识库 | 文档解析（PDF/TXT/Word）、文本切块 300 字符 / 100 重叠、Redis 向量存储、`minScore=0.6` 相似度过滤 | `/store/knowledgeBase/embed` |
| 安全 | 敏感词 | 阿里云 Green 云审核 + 本地 DFA 词典双重过滤；商家端 CRUD 即时生效 | `/sensitive` · `views/sensitive` |

边界说明：天气问答是提示词口语能力，没有接实时 Weather Tool；`item_views` 只采集上报，不参与推荐排序；部分客服页面硬编码 `localhost:8848`，改后端主机要同步调整。

### 核心文档与资产

| 入口 | 用途 |
| --- | --- |
| [CONTEXT.md](CONTEXT.md) · [CONTEXT-MAP.md](CONTEXT-MAP.md) | 领域术语、硬约束与三端边界 |
| [AGENTS.md](AGENTS.md) · [LANGUAGES.md](LANGUAGES.md) | Agent 任务流门禁与共享用词规范 |
| [docs/adr/](docs/adr/) | 架构决策记录 |
| [docs/苍穹外卖文档/_conv/AI模块设计.md](docs/苍穹外卖文档/_conv/AI模块设计.md) | AI 功能论文补充稿（需求 / 设计 / 实现 / 测试） |
| [assets/images/readme/](assets/images/readme/) | README 配图与 Showcase 截图 |
| [docs/outputs/prd/readme-diagrams/](docs/outputs/prd/readme-diagrams/) | 配图设计 brief 与生成 Prompt |

推荐阅读顺序：`README` → `CONTEXT` → `AGENTS` → 分端 `docs/contexts/*` → `code/`。

---

## Preview

本仓库是可运行的三端完整产品，不单独维护资产 Gallery Preview 站。

本地预览 README 渲染效果：

```bash
# 仓库根目录执行
python -m http.server 8091
# 浏览器访问 http://127.0.0.1:8091/preview-readme.html
```

> 必须通过 HTTP 打开；`file://` 协议无法 `fetch` README 内容。

---

## 演示 / Showcase

### 推荐演示路径

1. 启动本机中间件（MySQL80 / Redis / MongoDB / MinIO）+ 后端 `:8848` → 商家端 `:9527` 登录 `admin` / `123456`
2. 商家端上传菜品图 / 知识库文档；维护敏感词库
3. 小程序点餐加购 → 下单支付 → 评价页体验 AI 四模式扩写
4. 消息页进入「小锦」→ 输入「想吃辣的」→ 查看推荐卡片 → 必要时转人工客服

### Showcase — 用户小程序

| 点餐首页 | 消息中心 | 评论 AI 扩写 |
| :---: | :---: | :---: |
| [![点餐首页](assets/images/readme/showcase-weapp-order.png)](assets/images/readme/showcase-weapp-order.png) | [![消息中心](assets/images/readme/showcase-weapp-ai.png)](assets/images/readme/showcase-weapp-ai.png) | [![评论 AI 扩写](assets/images/readme/showcase-weapp-comment.png)](assets/images/readme/showcase-weapp-comment.png) |
| 分类导航 · 菜品列表 · AI 推荐入口 · 购物车 | 人工客服 · AI 智能助手 · 互动消息 | 帮写 / 续写 / 润色 / 精简 四模式 · AI 生成结果 |

小程序截图由微信开发者工具真机截取。

### Showcase — 商家管理端

| 订单管理 | AI 知识库管理 | 人工客服 |
| :---: | :---: | :---: |
| [![订单管理](assets/images/readme/showcase-admin-order.png)](assets/images/readme/showcase-admin-order.png) | [![AI 知识库管理](assets/images/readme/showcase-admin-kb.png)](assets/images/readme/showcase-admin-kb.png) | [![人工客服](assets/images/readme/showcase-admin-cs.png)](assets/images/readme/showcase-admin-cs.png) |
| 订单状态流转 · 外卖/堂食履约 · 分页搜索 | 文档上传 · 自动向量化 · RAG 检索支持 | WebSocket 实时会话 · AI 转人工兜底 |

商家端截图由 Playwright 自动截取。截图路径：`assets/images/readme/showcase-*.png`。

---

## 快速开始

### 环境要求

| 项目 | 版本 / 说明 |
| --- | --- |
| JDK | 17 |
| Node | ≥ 18（商家端与小程序编译） |
| Maven | 系统 Maven（项目无 mvnw） |
| 中间件 | 本机原生：MySQL80 · Redis · MongoDB · MinIO（详见 `deploy/local/README.md`） |
| 云服务 | 阿里云百炼（大模型）· 内容安全 Green · 微信小程序（环境变量名见 `CONTEXT.md`） |

凭据文件：`docs/虚拟机各个镜像账号密码及下载地址.txt`（已 gitignore，勿提交明文秘密）。

### 后端启动

```bash
cd code/web/training-web
# 先导入 sql/cq_take_out.sql 到本机 MySQL
mvn -pl training-server -am spring-boot:run -DskipTests "-Dspring-boot.run.profiles=local"
# 服务地址：http://localhost:8848
```

### 商家端启动

```bash
cd code/vue/training-vue3-main
npm install
npm run dev
# 访问地址：http://localhost:9527
# 开发代理：/dev-api → http://localhost:8848
```

### 小程序编译

```bash
cd code/uniapp/training-uniapp-master
npm install
npm run dev:mp-weixin
# 编译产物：dist/dev/mp-weixin
# 微信开发者工具导入该目录即可运行
```

部署成功后要在商家端重新上传 `assets/images/dishes/` 中的 28 张菜品图。部署步骤截图见 `assets/images/deploy/`，文字说明见 `docs/readme.txt`。

---

## 架构

![Architecture](assets/images/readme/architecture.png)

| 层级 | 职责 | 主要实现 |
| --- | --- | --- |
| Clients | 用户点餐 / 商家运营 | `training-uniapp-master`（微信小程序）· `training-vue3-main`（Vue3 管理端） |
| Access | JWT 双头认证、SSE 流式、WebSocket、RESTful | `authentication` / `token` 头 · `/ws/customer-service/**` |
| Application | 外卖业务逻辑 + AI 编排 | `training-server` · `LangChain4jConfiguration` |
| Data | 关系数据 / 文档 / 缓存向量 / 对象存储 | MySQL · MongoDB · Redis · MinIO |
| External | 大模型与内容审核、微信登录 | 阿里云百炼 Qwen · Green · WeChat |

### 设计原则

1. Code over legacy thesis：「满香宜」论文仅作历史参考，不作现行 API 真相源。
2. AI with tools + RAG：推荐 / 查单走业务 Tools；知识问答靠向量检索，降低模型幻觉。
3. Degrade gracefully：大模型调用失败时自动降级为关键词规则 / 模板 / 固定文案，保证核心功能可用。
4. Human in the loop：AI 客服超范围时转人工；付款与履约仍走传统业务订单流。
5. Secrets stay out：虚拟机密码、API Key、`application-dev.yml` 等敏感信息不入库。

---

## 技术栈

![Tech stack](assets/images/readme/tech-stack.png)

| 层级 | 技术选型 |
| --- | --- |
| Backend | Java 17 · Spring Boot 3.4.5 · MyBatis · LangChain4j 1.10.0-beta18 |
| Merchant Admin | Vue 3 · TypeScript · Vite 5 · Element Plus · Pinia · ECharts |
| Mini Program | uni-app · Vue 3 · uview-plus · Pinia（`pinia-plugin-unistorage` 持久化） |
| Data | MySQL 8.0 · Redis · MongoDB · MinIO |
| AI | 通义千问 `qwen-plus-latest`（对话）· `text-embedding-v4`（向量嵌入） |

---

## 用户主链路

![Workflow](assets/images/readme/workflow.png)

```text
用户侧：
Browse 菜品浏览 → Cart 购物车 → Order/Pay 下单支付 → Comment 评价（+ AI 扩写辅助）
                ↘ Message 消息中心 → AI 小锦对话 ⇄ Tools/RAG → 转人工客服（可选）

商家侧：
Orders 订单履约 · Knowledge Base 知识库上传 · Sensitive Words 敏感词维护 · Staff CS 人工客服
```

---

## 目录结构

```text
sky-ai/
├── AGENTS.md · CONTEXT.md · CONTEXT-MAP.md · LANGUAGES.md · README.md
├── preview-readme.html · preview-readme.css · preview-readme.js
├── .cursor/rules/                 # Windows 路径 / Shell 纪律 / 输出格式 / commit-history
├── code/
│   ├── web/training-web/          # 后端 Spring Boot（端口 8848）
│   ├── vue/training-vue3-main/    # 商家管理端（端口 9527）
│   └── uniapp/training-uniapp-master/  # 用户小程序（编译至微信开发者工具）
├── docs/
│   ├── agents/ · adr/ · contexts/ · outputs/
│   ├── 苍穹外卖文档/              # 毕设母版（「满香宜」传统版）+ AI 补充稿
│   └── readme.txt
└── assets/
    ├── backup/                    # 上游源码压缩包（gitignore，不入库）
    └── images/{dishes,deploy,readme}/  # 菜品图 / 部署截图 / README 配图
```

---

## 路线图

| 状态 | 事项 |
| --- | --- |
| ✅ | 三端源码与文档资产完整入库 |
| ✅ | project-init Phase A（治理骨架 · CONTEXT 单一事实源） |
| ✅ | AI 补充稿与代码实现对齐润色 |
| ✅ | README Polish 结构 + 配图 Prompt 契约 |
| ✅ | 说明图：banner（MiniMax 生成）+ features / architecture / tech-stack / workflow（脚本精确绘制） |
| ✅ | Showcase 真机截图（小程序 3 张 + 商家端 3 张） |
| ⬜ | 首个业务 theme 开发（待 Gate 审批后启动） |

---

## 文档

| 文档 | 说明 |
| --- | --- |
| [CONTEXT.md](CONTEXT.md) | 领域事实与硬约束单一事实源 |
| [AGENTS.md](AGENTS.md) | Agent 硬约束、任务流门禁与 Review 规范 |
| [LANGUAGES.md](LANGUAGES.md) | 共享用词规范（锦食坊 / 小锦 / 商家端等） |
| [docs/README.md](docs/README.md) | docs 目录资产索引与维护说明 |
| [AI模块设计.md](docs/苍穹外卖文档/_conv/AI模块设计.md) | AI 功能论文补充稿（需求 / 总体设计 / 详细设计 / 测试） |
| [readme-diagram-brief.md](docs/outputs/prd/readme-diagrams/readme-diagram-brief.md) | README 配图设计契约 |
| [readme-image-prompts.md](docs/outputs/prd/readme-diagrams/readme-image-prompts.md) | 配图生成 Prompt 记录 |
| [assets/README.md](assets/README.md) | 媒体资产目录约定与迁移说明 |

---

## License / 说明

本项目用于培训与毕业设计用途。上游苍穹外卖与第三方依赖遵循各自许可证。请勿提交 API Key、虚拟机密码、微信 Secret 等敏感信息。
