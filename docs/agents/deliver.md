# deliver.md — 交付约定

## Handoff 场景（本仓）

默认只启用 **A**：

| 场景 | 是否启用 | 说明 |
|---|---|---|
| A · 任务交接快照 | ✅ | `docs/outputs/handoff/{theme}/YYYY-MM-DD-{branch}-{task}.md`，覆盖式更新 |
| B–E | ❌ | 未启用；需要时再在本文件打开 |

## 交付物清单（功能 theme）

1. 代码变更（最小必要集）
2. 若影响领域事实：更新 `CONTEXT.md` / 分端 CONTEXT / `LANGUAGES.md`
3. 若影响 AI 论文叙事：同步 `docs/苍穹外卖文档/_conv/AI模块设计.md`
4. handoff 快照（场景 A）
5. 用户 Review 通过后再 commit

## 不做

- 不把密钥写入交付文档
- 不以「满香宜」论文接口冒充现行 API
- 不预建空的 `docs/outputs/**` 子目录凑数
