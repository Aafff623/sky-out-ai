# Wiki 页图（锦食坊 / sky-out-ai）

> 对照 [fork-Firefly Wiki](https://github.com/Aafff623/fork-Firefly/wiki/) 与全局 skill `wiki-post`。  
> **线上**：https://github.com/Aafff623/sky-out-ai/wiki （已推送 Home / Sidebar / P0–P2）  
> 本地工作副本：仓库旁 `D:\OneDrive\Desktop\sky-ai.wiki`；草稿备份仍可在 `.scratch/wiki-draft/`。

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
