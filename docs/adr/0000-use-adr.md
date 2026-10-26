# ADR-0000: 采用 Architecture Decision Records

## Status

Accepted

## Context

锦食坊为多端培训/毕设仓，AI 与传统外卖交织，口头决策易丢失。需要轻量、可检索的决策记录位置。

## Decision

采用 `docs/adr/000N-kebab-title.md` 记录架构与流程决策。编号递增；Status 使用 Proposed / Accepted / Superseded / Deprecated。

系统级决策放本目录；若未来某端出现大量独立决策，可在该端下增设 `docs/adr/`，并在 `GLOSSARY-MAP.md` 挂链。

## Consequences

- Agent 在改架构前应先扫相关 ADR
- 与 GLOSSARY 冲突时显式标注，不静默覆盖
- 不把日常琐碎 commit 写成 ADR
