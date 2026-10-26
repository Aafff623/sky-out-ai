# Domain Docs

How the engineering skills should consume this repo's domain documentation when exploring the codebase.

## Before exploring, read these

- **`GLOSSARY.md`** at the repo root — the domain glossary. Single-context repo.
- **`docs/project-facts.md`** — verified engineering facts (三端结构、主链路、AI 模块、基础设施、已知问题)。实现细节与端口/密钥等以它为准，词汇以 `GLOSSARY.md` 为准。
- **`docs/adr/`** — read ADRs that touch the area you're about to work in.

If any of these files don't exist, **proceed silently**. Don't flag their absence; don't suggest creating them upfront. The `/domain-modeling` skill (reached via `/grill-with-docs` and `/improve-codebase-architecture`) creates them lazily when terms or decisions actually get resolved.

## File structure

Single-context repo:

```
/
├── GLOSSARY.md                    ← glossary only
├── docs/
│   ├── project-facts.md           ← verified engineering facts
│   └── adr/
│       ├── 0000-use-adr.md
│       └── 0001-image-assets-minio.md
└── code/                          ← 三端实现（web / vue / uniapp）
```

## Use the glossary's vocabulary

When your output names a domain concept (in an issue title, a refactor proposal, a hypothesis, a test name), use the term as defined in `GLOSSARY.md`. Don't drift to synonyms the glossary explicitly avoids.

If the concept you need isn't in the glossary yet, that's a signal — either you're inventing language the project doesn't use (reconsider) or there's a real gap (note it for `/domain-modeling`).

## Flag ADR conflicts

If your output contradicts an existing ADR, surface it explicitly rather than silently overriding:

> _Contradicts ADR-0001 (图片资产 MinIO 权威存储) — but worth reopening because…_
