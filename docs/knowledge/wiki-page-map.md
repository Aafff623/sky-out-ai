# Wiki 页图（锦食坊 / sky-out-ai）

> 对照 [fork-Firefly Wiki](https://github.com/Aafff623/fork-Firefly/wiki/) 与全局 skill `wiki-post`。  
> **正文草稿**在本地 `.scratch/wiki-draft/`（gitignore）；推送到 `*.wiki.git` 须用户明确「发布 / 推送 Wiki」。  
> 远程 Wiki 需先在 GitHub 点一次 Create the first page（当前 `sky-out-ai.wiki.git` 可能尚不存在）。

## 受众

使用者 / 接手者手册（怎么跑、怎么演示、AI 边界）。治理与领域 SSOT 仍在 `CONTEXT` / `AGENTS` / `docs/`，Wiki 只外链。

## 页面树

| 优先级 | 页面 | 作用 |
|---|---|---|
| P0 | `Home` · `_Sidebar` · `Getting-Started` · `FAQ` | 导航 + 上手 + 常见误解 |
| P1 | `Local-Deploy` · `AI-Modules` · `Merchant-Admin` · `Mini-Program` · `Docs-Map` | 本机部署、AI 五大模块、两端要点、文档对照 |
| P2 | `Troubleshooting` · `Architecture` · `Contributing` | 排障；架构/贡献仅薄页外链 |

## SSOT 边界

| 层 | 内容 |
|---|---|
| Wiki | how-to / FAQ / 演示路径 |
| `CONTEXT` · `AGENTS` · ADR | 硬约束与任务流（不粘贴进 Wiki） |
| `_conv/AI模块设计.md` | 论文向 AI 补充；实现以 `code/` 为准 |
| 传统 `苍穹外卖文档/` | 满香宜历史母版，非现行 API |

## 草稿路径

```text
.scratch/wiki-draft/
  Home.md  _Sidebar.md  Getting-Started.md  FAQ.md
  Local-Deploy.md  AI-Modules.md  Merchant-Admin.md  Mini-Program.md
  Docs-Map.md  Troubleshooting.md  Architecture.md  Contributing.md
```
