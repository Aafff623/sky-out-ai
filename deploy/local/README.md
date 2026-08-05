# 本机原生中间件（取代 Docker / Mode A 隧道）

## 组件

| 服务 | 来源 | 端口 |
|---|---|---|
| MySQL | Windows 服务 `MySQL80` | 3306 |
| Redis | `scoop install redis`（Cygwin 构建） | 6379 |
| MongoDB | `scoop install mongodb` | 27017 |
| MinIO | `scoop install minio` | 9005 / 9006 |

Spring profile：`local` → [`application-local.yml`](../../code/web/training-web/training-server/src/main/resources/application-local.yml)

向量 / RAG：`InMemoryEmbeddingStore`（无 RediSearch）。进程重启后需重新 embed 知识库。

## 启动

```powershell
# 1) 确认 MySQL80 Running；无 SSH 隧道占用 6379/27017/9005
powershell -File deploy\local\up-native.ps1

# 2) 后端
cd code\web\training-web\training-server
mvn spring-boot:run -DskipTests "-Dspring-boot.run.profiles=local"
```

停止 Redis/Mongo/MinIO：`powershell -File deploy\local\down-native.ps1`（不停 MySQL80）。

## 数据目录

`deploy/local/data/`、`deploy/local/logs/` 不入库（见本目录 `.gitignore`）。
