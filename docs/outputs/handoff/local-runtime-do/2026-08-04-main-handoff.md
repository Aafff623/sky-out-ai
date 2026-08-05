# Handoff · 锦食坊本地运行时（DO 模式 A）

| 字段 | 值 |
|---|---|
| 日期 | 2026-08-04 |
| 分支 | `main`（相对 `origin/main` 有大量未提交变更） |
| Theme | `local-runtime-do` |
| 场景 | A · 任务交接快照（覆盖式） |
| 状态 | **后端 + 商家端已联调可跑**；小程序已编译；菜品图/AI 真机验收未完 |
| 交接目的 | 新会话可续跑环境、补验收、或开下一业务 theme（勿重复搭中间件） |

> **禁止**把本文件写入任何 AccessKey / AppSecret / 百炼 sk。明文见用户 Obsidian 私密笔记（路径见 §7）。

---

## 1. 一句话现状

预配局域网虚拟机 `192.168.64.100` **不通**，已改为 **DigitalOcean 只跑中间件 + 本机 SSH 隧道 + 本机 Spring/Vue/uni-app（模式 A）**。交接时本机探活：隧道 `13306` / 后端 `8848` / 商家端 `9527` 均为 **up**；商家登录 API 与 Vite proxy 登录均 **200**。

---

## 2. 本会话完成了什么（按时间线）

### 2.1 仓库熟悉与文档对齐（较早）

- 三端代码与 `docs/苍穹外卖文档/_conv/AI模块设计.md`、`docs/README.md` 对齐（天气无独立 Tool、MySQL 18 表、`item_views` 未参与推荐排序等）。
- 事实源：`CONTEXT.md` / `CONTEXT-MAP.md` / `LANGUAGES.md` / `AGENTS.md`（project-init Phase A）。

### 2.2 project-init Phase A / B

- 同步 `.cursor/rules/*.mdc`；根文档与 `docs/agents/*`、`docs/contexts/*`、`docs/adr/0000-use-adr.md`。
- 素材迁入 `assets/images/{dishes,deploy,readme}/`；`backup/` → `assets/backup/`（gitignore）。
- README Polish + `preview-readme.*`（端口 **8091**）+ `scripts/gen-readme-diagrams.py`。
- 报告：`docs/outputs/report/project-init/2026-08-04-phase-a.md`、`…-phase-b.md`。
- 配图契约：`docs/outputs/prd/readme-diagrams/`。

### 2.3 全局 Skill 规范（跨工具）

- **目录结构 / 仓库树 / Key docs：禁止 `<details>`，必须直接呈现**。
- 权威源：`%USERPROFILE%\.claude\skills\{project-init,readme-polish}\SKILL.md`，已同步 `.agents` / `.codex` / `.zcode` / `.opencode` / `.grok` / `.config\opencode`。
- 本仓 README 已去掉相关 `<details>`。

### 2.4 本地三端启动 → DO 模式 A（本段核心）

| Phase | 结果 |
|---|---|
| 0 工具链/密钥 | JDK17 可用（默认曾是 21）；Node/Maven OK；云密钥与微信已配（见 §7） |
| 1 中间件 | DO `129.212.238.93` compose；端口仅绑 `127.0.0.1`；**Redis 必须 Redis Stack** |
| 2 导库 | `cq_take_out.sql` 已导入；约 18 表；dish=25；admin 存在 |
| 3 后端 | `spring.profiles.active=do` → `:8848`；商家登录 200 |
| 4 商家端 | `npm run dev` → `:9527`；proxy 登录 200 |
| 5 小程序 | `dist/dev/mp-weixin` 已出；`manifest` appid=`wxb68ba3fc2d35105f` |
| 6 冒烟 | API 抽查 OK；**菜品图未重传**；AI/真机下单未闭环 |

操作包（不入库细节多在 scratch）：`.scratch/startup/`  
清单：`.scratch/startup/REMAINING-CHECKLIST.md`、`PHASE-STATUS.md`。

---

## 3. 架构与连接（接手必读）

```text
[本机]
  Vue :9527  ──proxy──►  Spring :8848 (profile=do)
  小程序编译产物 ──────►  Spring :8848
  SSH 隧道 13306/6379/27017/9005/9006
        │
        ▼
[DO 129.212.238.93 · sgp1]
  /opt/jinshifang/docker-compose.middleware.yml
  MySQL · Redis Stack · Mongo · MinIO   （仅 127.0.0.1）
```

| 组件 | 本机入口 | 远端 |
|---|---|---|
| MySQL | `127.0.0.1:13306` | 容器 `3306`（避开本机 mysqld 占 3306） |
| Redis | `127.0.0.1:6379` | Redis Stack（含 RediSearch `FT.*`） |
| Mongo | `127.0.0.1:27017` | `training-comment` |
| MinIO | `9005` / `9006` | bucket `training-take-out` |

**不要**再假设必须用 `192.168.64.100`（搁置）。  
**不要**把库端口绑 `0.0.0.0` 对公网开放。

---

## 4. 关键代码 / 配置变更（运行时相关）

| 路径 | 说明 |
|---|---|
| `code/web/training-web/training-server/src/main/resources/application-do.yml` | DO+隧道 profile；MySQL 端口 13306；百炼 base-url 可读环境变量；排除 RedisEmbedding 自动配置 |
| `code/web/training-web/training-server/src/main/java/com/fortunate/config/DoRedisEmbeddingStoreConfig.java` | `@Profile("do")` 固定 embedding 维度 1024，避免启动期调模型 403 |
| `code/uniapp/.../src/manifest.json` | `mp-weixin.appid` → `wxb68ba3fc2d35105f` |
| `.scratch/startup/docker-compose.middleware.yml` | 中间件定义；Redis=`redis/redis-stack-server` |
| `.scratch/startup/local-ssh-tunnel.ps1` | 隧道脚本 |
| `.scratch/startup/HERMES-DEPLOY.md` / `HERMES-ADD-SSH-KEY.md` | 给 DO 上 Hermes 的部署/加公钥说明 |
| `.gitignore`（sky-ai） | 忽略 apiKey/AccessKey csv、`.env`、`.scratch/` 等 |

训练库中间件默认口令（非云密钥）：MySQL `root/root`；MinIO `minioadmin/minioadmin`；商家 `admin/123456`。

---

## 5. 环境变量（只列名）

必须存在于 **Windows 用户环境变量**（改完重启 IDE/终端）：

- `ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET`（RAM；已授 Green）
- `ALIBABA_CLOUD_BAILIAN_ACCESS_KEY` / `ALIBABA_CLOUD_BAILIAN_BASE_URL`（业务空间兼容地址，非公共 dashscope 时必填）
- `Wechat_APP_ID` / `Wechat_SECRET`

启动后端示例：

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.jdks\ms-17.0.18"
# 确保上述 User 环境变量已注入当前进程
cd D:\OneDrive\Desktop\sky-ai\code\web\training-web\training-server
mvn spring-boot:run "-Dspring-boot.run.profiles=do"
```

商家端：`code/vue/training-vue3-main` → `npm run dev` → http://127.0.0.1:9527/  
小程序：`code/uniapp/training-uniapp-master` → `npm run dev:mp-weixin` → 开发者工具打开 `dist/dev/mp-weixin`。

隧道：

```powershell
powershell -File "D:\OneDrive\Desktop\sky-ai\.scratch\startup\local-ssh-tunnel.ps1"
```

---

## 6. 已踩坑（勿重复）

1. **普通 `redis:7-alpine` 无 RediSearch** → `FT._LIST` 失败；必须 Redis Stack。  
2. **RedisEmbeddingStore 自动配置**会在启动时调 embedding 探维度 → 模型未开通则整进程挂；`do` profile 已排除并手写 Store。  
3. **有百炼 API Key ≠ 模型一定可调用**；若再遇 `AccessDenied.Unpurchased`，去模型广场开通 `qwen-plus-latest` / `text-embedding-v4`。  
4. **本机 3306 常被 mysqld 占用** → 隧道用 **13306**，与 `application-do.yml` 一致。  
5. **微信 AppSecret ≠ 百炼 Key**（曾混淆）；当前小程序为 `threetwoa-app`。  
6. 父模块上直接 `spring-boot:run` 会找不到 main → 在 **`training-server` 目录**运行。  
7. Vue `npm install` 的 husky prepare 可能失败 → 可用 `--ignore-scripts`；`node_modules` 齐后仍可 `npm run dev`。  
8. WS 多处硬编码 `localhost:8848` → 模式 A 本机跑后端正好匹配；若改 Mode B 必须同步改前端。

---

## 7. 密钥存放（私密，不入库）

| 位置 | 内容 |
|---|---|
| Obsidian | `D:\OneDrive\Desktop\Notes\threetwoa_ob\About Me\Info\敏感信息\锦食坊-sky-ai-敏感配置.md` |
| Obsidian gitignore | 已忽略 `**/敏感信息/`（可只提交 ignore 规则，勿 `add -f` 笔记） |
| 磁盘旁路 | `d:\cache\sky-ai-secrets\`（env 备份）；百炼 CSV 曾在 `d:\cache\chrome-cache\` |

sky-ai 仓内 **不要**提交上述明文。

---

## 8. Git / Review 状态

- 分支：`main`；**大量 init + 运行时改动未 commit**（含 CONTEXT/README/assets/handoff 等）。
- `code/web/training-web` 为嵌套 git（status 显示 `m`），注意父子仓边界。
- 门禁：用户 Review 前 **不自动 commit / push**（见 `AGENTS.md` / `docs/agents/workflow.md`）。
- 建议后续拆 commit（示例，需用户批准后再做）：
  1. `docs/chore`：project-init 骨架与 CONTEXT  
  2. `docs`：README + assets 配图迁移  
  3. `feat`：`application-do` + DoRedisEmbeddingStore + compose 操作说明  
  4. `chore`：gitignore / manifest appid  

---

## 9. 下一会话建议任务（按优先级）

1. **保活检查**：隧道 / `:8848` / `:9527`；断则按 §5 重启。  
2. **商家端重传菜品图**：素材 `assets/images/dishes/`。  
3. **微信开发者工具**：导入 `dist/dev/mp-weixin`，勾选不校验域名，验登录（小程序后台有「无法被搜索」限制，本地仍可测）。  
4. **AI 冒烟**：客服一轮；若 403 再开模型额度。  
5. **用户 Review 后**按 §8 攒批 commit + `docs/outputs/commit-history/main/YYYY-MM-DD.md`。  
6. 首个业务 theme：PRD 批准前不写功能代码（init 已完成大半）。

待办清单链接：`.scratch/startup/REMAINING-CHECKLIST.md`。

---

## 10. 建议加载的 Skills

| Skill | 用途 |
|---|---|
| `project-init` | 续 init / 核对骨架是否漂移 |
| `readme-polish` | README/配图再打磨（禁 details） |
| `verification-loop` | 联调验收清单 |
| `handoff` | 再压缩交接 |
| `springboot-patterns` / 后端 CLAUDE | 后端改动 |
| `uniapp-cityrun-dev` 或小程序 CLAUDE | 小程序联调 |
| `git-commit-helper` | 用户批准后的拆分提交 |
| `humanizer-output-style` | 对用户输出语气 |

事实源优先读：根 `CONTEXT.md`、`AGENTS.md`、本 handoff、`.scratch/startup/PHASE-STATUS.md`。

---

## 11. 验收口令（给下一 Agent）

```text
□ ping/隧道：127.0.0.1:13306 OPEN
□ curl/Invoke：POST /store/employee/login admin/123456 → code 200
□ 浏览器：http://127.0.0.1:9527 能进工作台
□ 微信工具：能打开 dist/dev/mp-weixin（登录视微信与后端配置）
□ 不在仓库与 handoff 中出现任何 sk-/LTAI/AppSecret 明文
```

---

## 12. 关联路径速查

| 类型 | 路径 |
|---|---|
| 启动计划（Cursor plan） | `~/.cursor/plans/三端本地启动_b585531a.plan.md` |
| 操作包 | `.scratch/startup/` |
| Init 报告 | `docs/outputs/report/project-init/` |
| README 配图 PRD | `docs/outputs/prd/readme-diagrams/` |
| 本 handoff | `docs/outputs/handoff/local-runtime-do/2026-08-04-main-handoff.md` |
| DO compose（服务器） | `/opt/jinshifang/docker-compose.middleware.yml` |
