# LANGUAGES.md

Agent 输出与 Issue / PRD / handoff **必须优先使用**本文件与 `CONTEXT.md` 中的用词，避免同义漂移。

## 产品与品牌

| 规范词 | 含义 | 避免 |
|---|---|---|
| 锦食坊 | 现行 AI 增强版外卖平台（`code/`） | 把代码叫「满香宜」 |
| 满香宜 | 2023 传统毕设文档中的系统名（`docs/苍穹外卖文档/`） | 与锦食坊混称 |
| 小锦 | AI 智能客服人设名 | 「小天」「苍穹助手」 |
| 商家端 / 管理端 | Vue 商家后台 | 「后台前端」含糊指代 |
| 用户端 / 小程序 | uni-app 微信端 | 「前台」单独出现时需限定 |
| 后端 / API | `training-web` | |

## 技术栈短名

| 规范词 | 说明 |
|---|---|
| LangChain4j | Java LLM 编排框架 |
| 通义千问 / Qwen | 百炼对话与嵌入模型 |
| RAG | 知识库向量检索增强 |
| SSE | 服务端流式事件；小程序侧为分块接收 |
| JWT 双 Token | 商家头 `token`；用户头 `authentication` |

## AI 模块名（五大）

1. AI 智能客服  
2. AI 菜品推荐  
3. 评论 AI 扩写与自动回复  
4. 知识库管理  
5. 敏感词过滤  

## Issue / 任务流词汇

| 词 | 用法 |
|---|---|
| theme | 业务主题目录名（kebab-case），用于 `docs/outputs/{prd,handoff,report}/{theme}/` |
| Status | Issue 文件顶部状态行，取值见 `docs/agents/triage-labels.md` |
| handoff | 任务交接快照；本仓默认场景 A；覆盖式更新 |
| ADR | `docs/adr/000N-kebab-title.md` |
| awaiting-review | 实施完成、待用户 Review，**先于 commit** |

## 订单相关

| 规范词 | 说明 |
|---|---|
| orderType | `1` 外卖 / `2` 堂食 |
| 外卖状态 1–7 | 待付款→…→退款（客服工具主要按此描述） |
| 堂食待取餐 | 状态 `8`，含取餐号（`pickup_code`） |

## 路径别名（写作可用）

- 后端仓：`code/web/training-web`
- 商家端：`code/vue/training-vue3-main`
- 小程序：`code/uniapp/training-uniapp-master`
- AI 论文补充稿：`docs/苍穹外卖文档/_conv/AI模块设计.md`
