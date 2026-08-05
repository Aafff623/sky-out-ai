# 本机原生中间件迁移（取代 Docker / Mode A 隧道）

> 状态：**已实施（2026-08-04）**  
> 日期：2026-08-04  
> 取代：`本机 Docker 中间件迁移`（Docker Desktop / WSL 不可用）  
> 交接：`docs/outputs/handoff/local-runtime-native/2026-08-04-main-handoff.md`

## 目标架构

```text
[本机]
  Vue :9527 / 小程序  →  Spring :8848 (profile=local)
       → MySQL80 :3306
       → Scoop Redis :6379          （缓存 / 会话记忆）
       → Scoop MongoDB :27017
       → Scoop MinIO :9005/:9006
       → InMemoryEmbeddingStore     （RAG；无 Redis Stack）
```

| 之前 | 之后 |
|---|---|
| DO + SSH 隧道 | 本机原生进程，**无隧道** |
| Docker Redis Stack | Scoop 普通 Redis + **InMemory 向量库** |
| 工作台数秒 | 目标约 &lt;1.5s |

## 锁定选型

| 组件 | 选型 | 端口 |
|---|---|---|
| MySQL | 已有 `MySQL80` 8.0.26 | 3306（root 密码非项目默认 `root`，实施时确认） |
| Redis | `scoop install redis` | 6379 |
| MongoDB | `scoop install mongodb` | 27017 |
| MinIO | `scoop install minio` | 9005 / 9006 |
| 向量 / RAG | `InMemoryEmbeddingStore` @Profile `local` | — |

**不做**：重装 WSL、Docker、本机 Redis Stack。  
**保留**：`application-do.yml` 作 DO 回退。

## 实施步骤

1. Scoop 安装 redis / mongodb / minio；`deploy/local/up-native.ps1` 拉起服务；确认 MySQL 账号并建库 `cq_take_out`。
2. 新增 `application-local.yml`；`local` 用 InMemory 向量库，`do` 仍用 RedisEmbeddingStore。
3. 隧道最后一次导出 MySQL/Mongo/MinIO → **停隧道** → 导入本机并校验。
4. `--spring.profiles.active=local` 重启；工作台耗时验收；DO `compose stop`。
5. 更新 handoff / PHASE-STATUS / CONTEXT。

## 验收

- 无 Docker、无隧道，后端可用；登录 200
- 工作台并行墙钟约 &lt;1.5s
- 图片走本机 MinIO；RAG 重启后需再 embed（文档写明）

## 风险

| 风险 | 处理 |
|---|---|
| MySQL 密码未知 | 实施前向你确认或建专用用户 + 环境变量 |
| 无 RediSearch | InMemory；知识库非持久向量 |
| 隧道占端口 | 切流第一步杀 ssh |
