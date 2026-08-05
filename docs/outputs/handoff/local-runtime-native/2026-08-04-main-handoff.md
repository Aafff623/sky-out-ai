# Handoff · 锦食坊本地运行时（本机原生中间件）

| 字段 | 值 |
|---|---|
| 日期 | 2026-08-04 |
| Theme | `local-runtime-native` |
| 场景 | A · 任务交接快照（覆盖式） |
| 状态 | **已切流**：本机 MySQL80 + Scoop Redis/Mongo/MinIO；Spring `local`；无 SSH 隧道 |
| 取代 | Mode A（DO + 隧道）与「本机 Docker」方案（WSL/Docker Engine 不可用） |

> **禁止**写入 AccessKey / AppSecret / 百炼 sk。明文见用户 Obsidian 私密笔记。

## 1. 一句话现状

中间件全部在本机：Windows **MySQL80**（库 `cq_take_out`，root 密码本机自管）+ Scoop **redis / mongodb / minio**。后端 `--spring.profiles.active=local`。DigitalOcean 上 `jsf-*` 容器已 **stop**（volume 暂留备份）。

## 2. 架构

```text
[本机]
  Vue :9527 ──proxy──► Spring :8848 (profile=local)
  小程序 ─────────────► Spring
       → MySQL80 :3306
       → Redis :6379（Scoop，非 Redis Stack）
       → MongoDB :27017
       → MinIO :9005/:9006
       → InMemoryEmbeddingStore（RAG）
```

启动：`deploy/local/up-native.ps1` + `mvn … -Dspring-boot.run.profiles=local`  
说明：`deploy/local/README.md`

## 3. 验收摘录（切流当日）

| 项 | 结果 |
|---|---|
| 隧道 | 无 |
| 登录 admin/123456 | 200 |
| dish 行数 | 25 |
| MySQL RTT / Redis PING | ~10ms / ~3ms |
| workspace 单接口 | businessData ~42ms 量级 |
| DO jsf-* | stopped |

## 4. 已知限制

- Scoop Redis **无** `FT.*`；知识库向量在 `local` 下存内存，**重启后端需再 embed**。
- 菜品图已对本机 MinIO 重传（约 24/25；「广式烧鸭」缺同名本地图时可能 SKIP）。
- Mongo `training-comment` 从 DO 导出时为空库（无评论数据可迁）。
- `application-do.yml` 仍保留；勿与本机 Scoop 端口同时抢绑隧道。

## 5. 日常命令

```powershell
powershell -File deploy\local\up-native.ps1
cd code\web\training-web\training-server
mvn spring-boot:run -DskipTests "-Dspring-boot.run.profiles=local"
# 商家端
cd code\vue\training-vue3-main
npm run dev
```

店铺营业：`PUT /store/shop/status/1`（Redis 冷启动后可能需再设一次）。
