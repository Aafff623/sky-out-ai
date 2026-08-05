# Domain Docs

How the engineering skills should consume this repo's domain documentation when exploring the codebase.

## Before exploring, read these

- **`CONTEXT.md`** at the repo root
- **`CONTEXT-MAP.md`** at the repo root — points at per-end `CONTEXT.md` files under `docs/contexts/`
- **`LANGUAGES.md`** — shared vocabulary
- **`docs/adr/`** — architectural decisions
- AI narrative supplement: `docs/苍穹外卖文档/_conv/AI模块设计.md`（与代码对齐的 AI 章节；传统「满香宜」论文勿当作现行 API）

## File structure（本仓 · 多端）

```text
/
├── CONTEXT.md
├── CONTEXT-MAP.md
├── LANGUAGES.md
├── docs/adr/
└── docs/contexts/
    ├── api/CONTEXT.md
    ├── admin/CONTEXT.md
    └── weapp/CONTEXT.md
```

产品代码在 `code/{web,vue,uniapp}/`，不在 `src/`。

## Use the glossary's vocabulary

When your output names a domain concept, use the term as defined in `CONTEXT.md` / `LANGUAGES.md`. Don't drift to synonyms the glossary explicitly avoids.

## Flag ADR conflicts

If your output contradicts an existing ADR, surface it explicitly rather than silently overriding.
