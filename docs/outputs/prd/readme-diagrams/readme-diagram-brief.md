# README Diagram Brief — 锦食坊 / sky-ai

日期：2026-08-04 · Phase B  
范文意图：[fork-Firefly](https://github.com/Aafff623/fork-Firefly)（结构密度 · Features 入口表 · Showcase 网格 · 治理链接）+ [agent-cfo](https://github.com/San-Y108/agent-cfo)（为什么 · 核心边界 · 主链路 · 演示分区）  
**禁止照抄业务文案**，只对齐信息架构。

## 0. 确认结论（本仓默认）

| 项 | 决定 |
|---|---|
| 受众 | 答辩 / 培训同学 / 接手 Agent |
| 范文偏向 | 产品演示向为主，吸收 agent-cfo 的「为什么 + 边界表」密度 |
| Preview 站 | **省略**资产 Gallery（单产品三端）；改用 **README 本地预览壳** `preview-readme.html` |
| Showcase | 占位槽位；环境跑通后 Playwright / 真机补图 |
| Architecture 标杆 | `bytebytego-client-server.png`（Client → API → Data/AI） |
| Structure | Markdown `<details>` 树，不出 `structure.png` |

## 1. README 章节地图

| # | 章节 | 配图 / 资产 |
|---|---|---|
| 1 | Header（标题 · 一句话 · Banner · 徽章 · 锚点） | `banner.png` |
| 2 | 为什么需要 | —（痛点 + 核心边界表 + 主链路 text） |
| 3 | 功能 | `features.png` + Features 表（含 Main entry） |
| 4 | Preview | 声明：无 Gallery；链到 README 预览壳启动 |
| 5 | 演示 / Showcase | 占位网格（小程序 / 商家端 / AI） |
| 6 | 快速开始 | 命令可复制 |
| 7 | 架构 | `architecture.png` + 分层表 |
| 8 | 技术栈 | `tech-stack.png` |
| 9 | 用户主链路 | `workflow.png` |
| 10 | 目录结构 | Markdown 树 |
| 11 | 路线图 | 状态表 |
| 12 | 文档 | CONTEXT / AGENTS / AI 补充稿 / assets |

## 2. 配图资产清单

| 文件 | 比例 | 状态 | method |
|---|---|---|---|
| `banner.png` | 3:1 | 待用户出图 | img2img |
| `features.png` | ~16:9 | 待用户出图 | img2img |
| `architecture.png` | 宽图 | 待用户出图 | img2img · client-server |
| `tech-stack.png` | 宽图 | 待用户出图 | img2img |
| `workflow.png` | 宽图 | 待用户出图 | img2img · stage-decision |
| `structure.png` | — | **不做** | markdown-tree |
| `preview-shell.png` | — | **不做**（无 Gallery） | — |
| `showcase-*.png` | — | 占位 | screenshot |

终稿目录：`assets/images/readme/`  
执行层 Prompt：`readme-image-prompts.md`

## 3. 设计语言（4–6 色）

| Token | Hex | 用途 |
|---|---|---|
| Brand red | `#D81E06` | 小程序主强调 / CTA |
| Ink | `#1A1A1A` | 标题与主文 |
| Paper | `#F7F5F2` | 浅底 |
| Fresh | `#2D6A4F` | AI / 成功态辅色 |
| Mute | `#6B6B6B` | 次要标签 |

禁止：紫雾霓虹、彩虹块、蜘蛛网连线、伪造产品 UI。

## 4. 验收

- [ ] README 章节齐全，与范文意图对齐非照抄
- [ ] 说明图契约文件名已引用或占位说明
- [ ] `readme-image-prompts.md` 可投喂 GPT img2img
- [ ] Preview：已声明省略 Gallery + README 壳可启动
- [ ] Showcase 路径/占位已写
- [ ] 快速开始可执行；未与 CONTEXT 抢事实源
