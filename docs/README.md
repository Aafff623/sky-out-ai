# docs 文档资产索引

> 维护入口。本文档说明 `docs/` 结构与用途；后续「AI 板块扩写」基于 `苍穹外卖文档/_conv/` 的 Markdown 阅读版进行。治理入口见根 `AGENTS.md` / `CONTEXT.md`。

## 目录结构

| 路径 | 内容 | 用途 |
|---|---|---|
| `苍穹外卖文档/` | 4 份原始毕业设计文档（docx/doc），**只读母版** | 原始依据，勿改动 |
| `苍穹外卖文档/_conv/` | 转换出的 Markdown 阅读版 + 提取脚本 + **AI 补充稿** | 工作副本，供 Claude/Agent 读取与编辑 |
| `readme.txt` | 部署操作说明 | 环境搭建参考 |
| `虚拟机各个镜像账号密码及下载地址.txt` | 虚拟机镜像下载地址与全部凭据 | 部署依赖（gitignore） |
| `agents/` | workflow / deliver / archive / domain / issue-tracker / triage-labels / voice | Agent 任务流 |
| `adr/` | 架构决策记录 | ADR-0000 起 |
| `contexts/` | api / admin / weapp 分端 CONTEXT | 经根 `CONTEXT-MAP.md` 索引 |
| `glossary/` · `knowledge/` | 术语库 / 可迁移知识（含 `wiki-page-map.md`、`project-init-notes.md`） | 按需填充；Wiki 正文不进主仓 |
| `outputs/` | report / prd / handoff / commit-history | **有产物再建子目录** |

> 菜品图与部署截图已迁至 `assets/images/dishes/`、`assets/images/deploy/`（见 `assets/README.md`）。

## 苍穹外卖文档/ 四份文档

| 原始文件（仓库内实际文件名） | 说明 | 转换 md 位置 |
|---|---|---|
| `论文.docx` | 传统版论文（5 章结构：前言/需求/系统设计/详细设计/测试） | `_conv/论文/论文.md` |
| `论文1.docx` | 传统版论文（**完整版，推荐作扩写母版**，含后台 7 模块 + 前台 7 模块） | `_conv/论文1/论文1.md` |
| `1_1_毕业设计任务书-软件工程1.docx` | 毕业设计任务书（表格型：题目/要求/进度） | `_conv/任务书/任务书.md` |
| `2.毕业设计开题报告(2)(1).doc` | 开题报告（老 .doc 格式，已文本提取并清洗） | `_conv/开题报告/开题报告.md` |

## AI 补充稿（锦食坊）

| 文件 | 说明 |
|---|---|
| `_conv/AI模块设计.md` | 「锦食坊」AI 功能论文补充稿（需求 / 总体设计 / 详细设计专章 / 测试）。扩写以**代码实装为准**；图注「待补充」、测试结果「待验证」为预期占位 |

**并入论文1 时的章节策略**：需求与总体设计并入第 2、3 章；`AI模块设计.md` 中的「第六章」作为 AI 详细设计专章，接在传统第 4 章之后；测试章节可参考 `论文.md` 结构安排，原「总结与展望」顺延。

## 转换方式（需要重新生成时）

- **docx → md**：MinerU `mineru -p 文件.docx -o 输出目录`（保留图片/表格，图片落在 `images/`）
- **.doc 老格式**：`python _conv/_scripts/doc_extract.py 输入.doc 输出.md`（olefile 文本提取）
- 注意：MinerU 会产生 `*_content_list.json` / `*_middle.json` / `*_model.json` / `*_origin.docx` 等中间产物，属冗余，清理时只保留 `.md` + `images/`

## 重要提醒

- **文档描述的是传统版系统（「满香宜」）**，不含 AI 功能；与 `code/` 的 AI 增强版（「锦食坊」）不对应。扩写 AI 板块时以代码为准、沿用本文档格式；AI 细节以 `_conv/AI模块设计.md` 为准。
- **扩写格式规范**（源自论文1）：详细设计四段式 = `（1）功能描述` → `（2）页面效果展示`（截图 + 加粗"图X.X 名称"）→ `（3）详细设计和实现`（页面 Ajax → Controller → Service → Mapper 调用链）→ `（4）关键代码`（表格内代码）。图注 `图X-X`/`图X.X`，表 `表X.X`。
