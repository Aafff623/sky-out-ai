# ADR-0001: 图片资产统一以 MinIO 为权威存储，仓库保留全量备份

## Status

Accepted

## Context

2026-09 排查发现：`dish.image` 等数据库 URL 指向本机 MinIO，但 MinIO 数据目录（`deploy/local/data/`，不入库）曾因环境重建丢失，导致 25 张菜品图全部 404；另有 `order_detail` 等表 145 条 URL 指向已清空的 OSS 对象（死链）。此前 UI 图散落在两端 `src/assets` / `static/`，小程序还有字符串别名不解析的既有 bug，且历史图片地址没有统一出口。

## Decision

1. **MinIO 为权威存储**：业务图（运行时上传）与 UI 静态图、头像素材库全部存 MinIO 桶 `training-take-out`；数据库只存 URL。
2. **仓库全量备份**：所有进入 MinIO 的静态资源在 `assets/images/ui/` 保留同名副本入库（git），MinIO 数据丢失时可用 `temp/restore-dishes.ps1` / 备份目录重灌。
3. **前端地址统一出口**：两端各建 `src/constants/minio.ts`（`MINIO_BASE` + 命名常量），禁止再硬编码存储地址；小程序真机切换局域网 IP 只改此一处。
4. **头像素材库**：从 ipaslogo.com 官方免费库（可商用）搬运 200 张、按 6 分类组织为 `ui/avatar-library/{category}/` + `index.json` 清单；前端选择器只消费清单，扩库不改代码。
5. 界面矢量图标（SVG sprite）、小程序 tabBar 图标、第三方组件资源**不迁移**（平台/构建机制硬约束）。

## Consequences

- 换存储环境只改 `MINIO_BASE`；MinIO 数据目录不再承载唯一副本，可随时重建。
- 仓库体积增加约 2MB（200 张 webp + UI 图）；换来环境可重建性。
- ~~历史快照类死链（OSS 145 条）保留不改，依赖新数据自愈；如需彻底清理另立任务。~~ **2026-10-11 更新（issue #11 拍板）**：145 条 OSS 死链已按 user.avatar 先例**置空**（走前端默认兜底），执行前已在本地 `temp/deadlink-backup-2026-10-11.sql` 留存行级备份；三表 OSS 死链清零。
- 小程序真机预览依赖局域网 IP + 「不校验合法域名」，正式发布需配置 downloadFile 合法域名。
