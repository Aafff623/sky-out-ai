# CONTEXT.md — 锦食坊（sky-ai）

领域事实与硬约束的单一入口。分端细节见 `CONTEXT-MAP.md`。

## 1. 项目定位

「锦食坊」是基于苍穹外卖重构的**培训 / 毕业设计**项目：传统外卖业务 + **LangChain4j + 通义千问** AI 能力。前后端分离，三端代码在 `code/`。

| 端 | 路径 | 技术栈 | 默认端口 |
|---|---|---|---|
| 后端 | `code/web/training-web` | Spring Boot 3.4.5 · Java 17 · MyBatis · LangChain4j | **8848** |
| 商家管理端 | `code/vue/training-vue3-main` | Vue 3 · TS · Vite · Element Plus | **9527**（proxy → 8848） |
| 用户小程序 | `code/uniapp/training-uniapp-master` | uni-app · Vue 3 · uview-plus | 编译至微信开发者工具 |

文档中的「满香宜」指 2023 传统毕设叙事，**不含现行 AI**；扩写与实现对齐以代码 + `docs/苍穹外卖文档/_conv/AI模块设计.md` 为准。

## 2. 业务能力（摘要）

**传统外卖**：分类 / 菜品 / 套餐 / 购物车 / 地址 / 下单支付 / 订单履约 / 员工 / 营业状态 / 统计报表；小程序支持外卖与堂食。

**AI 五大模块**：

1. AI 智能客服「小锦」（流式对话 + Redis 记忆 + RAG + 工具）
2. AI 菜品推荐（语义偏好 + 规则/热度降级）
3. 评论 AI 扩写与自动回复
4. 知识库管理（向量化 + RAG）
5. 敏感词过滤（阿里云 Green + 本地 DFA）

## 3. 数据与基础设施

| 存储 | 用途 |
|---|---|
| MySQL `cq_take_out` | **18** 张表（传统 11 + AI 相关 5 + `shop_info` / `pickup_code` 等） |
| MongoDB `training-comment` | 评论 / 回复等文档 |
| Redis | 业务缓存、会话记忆；**do** 环境向量库用 Redis Stack（RediSearch） |
| MinIO `training-take-out` | 菜品图、知识库文件 |

**本地默认运行时（2026-08）**：本机原生中间件 — MySQL80 + Scoop Redis/Mongo/MinIO，Spring profile **`local`**，启动见 `deploy/local/README.md`。`local` 下向量库为内存实现（无 RediSearch）。Mode A（DigitalOcean + SSH 隧道，profile `do`）仅作回退。

预配虚拟机 IP：`192.168.64.100`（凭据见 `docs/虚拟机各个镜像账号密码及下载地址.txt`，**不入库明文到文档正文**；当前本机未依赖该 VM）。

必填环境变量（名称级）：`ALIBABA_CLOUD_ACCESS_KEY_ID` / `SECRET`、`ALIBABA_CLOUD_BAILIAN_ACCESS_KEY`、`Wechat_APP_ID` / `Wechat_SECRET`。

## 4. 认证

- 商家端请求头：`token`
- 用户端请求头：`authentication`
- JWT secret 均为配置中的 `training`（以代码/配置为准）

## 5. 硬约束（Agent 必守）

1. **文档 vs 代码**：传统论文 ≠ 现行实现；AI 叙事以代码与已润色的 `AI模块设计.md` 为准。
2. **推荐与浏览**：`item_views` 可上报采集；**当前推荐算法不读取浏览记录排序**。
3. **天气能力**：客服提示词中的天气问答**无独立 Weather Tool / 实时天气 API**。
4. **推荐候选池**：智能推荐当前以启售**菜品**为主，套餐未稳定入池。
5. **密钥**：不提交 `.env`、虚拟机密码文件、`application-dev.yml` 中的秘密。
6. **大体积备份**：`assets/backup/` 不入库（见 `.gitignore`）。
7. **WebSocket / SSE**：部分前端演示地址硬编码 `localhost:8848`；改后端主机时需同步前端。

## 6. 术语（节选）

完整用词见 `LANGUAGES.md`。关键实体：小锦、知识库、敏感词、堂食取餐号、SSE 事件码 DATA(1001)/STOP(1002)/PARAM(1003)。

## 7. 资产位置

| 资产 | 路径 |
|---|---|
| 毕设母版与 AI 补充稿 | `docs/苍穹外卖文档/` |
| 部署说明 | `docs/readme.txt`；截图 `assets/images/deploy/` |
| 菜品图片 | `assets/images/dishes/`（28 张） |
| 上游压缩包 | `assets/backup/` |
| Agent 流程 | `docs/agents/` |
| 任务产物 | `docs/outputs/`（有产物再建子目录） |
