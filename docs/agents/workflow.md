# workflow.md — 任务流

```text
Issue（.scratch/<feature>/）
  →（可选）docs/outputs/report/{theme}/
  → docs/outputs/prd/{theme}/prd.md（draft）
  → 用户 approved
  → docs/outputs/handoff/{theme}/YYYY-MM-DD-{branch}-{task}.md
  → 实施
  → awaiting-review【停】
  → 用户通过 → commit → docs/outputs/commit-history/{branch}/YYYY-MM-DD.md
  → archive（见 archive.md）
```

## 门禁

| 阶段 | 规则 |
|---|---|
| PRD | 未批准前不写业务功能代码（init / 纯文档对齐除外） |
| handoff | 覆盖式更新；旧 handoff 文件直接删除 |
| Review | 先于 commit；用户未点头不提交 |
| commit-history | 按分支组织；见 `.cursor/rules/commit-history.mdc` |

## theme 命名

kebab-case，例如 `ai-docs-align`、`customer-service-hardening`。与 git 分支名尽量可对应，但不强制。

## 本仓默认

- Issue tracker：本地 `.scratch/`
- Handoff：仅场景 **A**（任务交接快照）
- 首个业务 theme：init 阶段不创建；骨架就绪后再开
