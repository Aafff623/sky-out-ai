# archive.md — 归档

## 何时归档

- theme 已合并 / 放弃，且 handoff 不再活跃
- commit-history 中特性分支已合入 `main`

## 怎么做

1. 特性分支的 `docs/outputs/commit-history/{branch}/` → 移入 `docs/outputs/commit-history/archive/{branch}/`
2. 相关 `.scratch/<feature>/` 可删除或移入 `.scratch/archive/`（本地，不入库）
3. 过期 handoff：删除（覆盖式策略下通常已是最新一份）

## 永不归档

- `main` 的 commit-history
- `docs/苍穹外卖文档/` 母版与 `_conv` 工作副本
- 根 `CONTEXT.md` / `AGENTS.md` 等治理入口
