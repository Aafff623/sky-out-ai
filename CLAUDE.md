# CLAUDE.md

事实源：`CONTEXT.md`（领域词汇表）· `docs/project-facts.md`（工程事实 / 已知问题）· `docs/adr/`（决策记录）· `AGENTS.md`（硬约束 / 构建命令）· `README.md`（人读说明）。
子项目规范与根文件同时加载：`code/web/training-web/CLAUDE.md` · `code/vue/training-vue3-main/CLAUDE.md` · `code/uniapp/training-uniapp-master/CLAUDE.md`。
文档与代码冲突时以代码为准；`docs/苍穹外卖文档/` 是传统毕设母版（「满香宜」），非现行实现。

## Agent skills

### Issue tracker

任务与 backlog 以 GitHub Issues（`Aafff623/sky-out-ai`，`gh` CLI）为准。See `docs/agents/issue-tracker.md`.

### Triage labels

默认五个规范 triage 标签（`needs-triage` / `needs-info` / `ready-for-agent` / `ready-for-human` / `wontfix`）。See `docs/agents/triage-labels.md`.

### Domain docs

Single-context：根 `CONTEXT.md` 词汇表 + `docs/adr/`；工程事实另见 `docs/project-facts.md`。See `docs/agents/domain.md`.
