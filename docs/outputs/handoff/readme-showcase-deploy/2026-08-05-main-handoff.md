# Handoff · 锦食坊 README Showcase 与部署验证

| 字段 | 值 |
|---|---|
| 日期 | 2026-08-05 |
| Theme | `readme-showcase-deploy` |
| 场景 | A · 任务交接快照（覆盖式） |
| 状态 | **已完成**：README Showcase 优化 + 6 张截图 + 小程序 bug 修复 |
| 前置 | `local-runtime-native` 已切流，本机中间件运行中 |

> **禁止**写入 AccessKey / AppSecret / 百炼 sk。明文见用户 Obsidian 私密笔记。

## 1. 一句话现状

README 已完成 Showcase 区块重写（参照 agent-cfo 风格），6 张真机截图全部就位；小程序「选择规格」按钮无响应 bug 已修复；三端服务（后端 :8848 / 商家端 :9527 / 小程序）当前运行正常。

## 2. 本次变更

### 2.1 README 优化

- Showcase 区块重写为分端卡片式表格（小程序 3 张 + 商家端 3 张）
- 按 humanizer-output-style 规范去 AI 味，措辞更人性化
- 快速开始更新为本机中间件方案（`local` profile）
- 路线图 Showcase 截图标记为 ✅ 完成

### 2.2 截图资产

| 端 | 文件 | 来源 |
|---|---|---|
| 小程序 | `showcase-weapp-order.png` | 微信开发者工具真机截取 |
| 小程序 | `showcase-weapp-ai.png` | 微信开发者工具真机截取 |
| 小程序 | `showcase-weapp-comment.png` | 微信开发者工具真机截取 |
| 商家端 | `showcase-admin-order.png` | Playwright 自动截取 |
| 商家端 | `showcase-admin-kb.png` | Playwright 自动截取 |
| 商家端 | `showcase-admin-cs.png` | Playwright 自动截取 |

路径：`assets/images/readme/showcase-*.png`

### 2.3 Bug 修复

**问题**：小程序「选择规格」按钮点击无响应。

**原因**：`up-button` 组件默认拦截点击事件，外层 `view` 的 `@click.stop` 收不到。

**修复**：4 处 `up-button` 显式添加 `@click="handleDishFlavors(...)"`：
- `code/uniapp/.../pages/index/index.vue` 2 处
- `code/uniapp/.../pages/index/components/DishDetail.vue` 2 处

## 3. Commit 记录

```
e87e722 chore(project-init): 骨架与文档资产入库
94535c0 docs(readme): 优化 Showcase 区块与整体措辞
a5b992a fix(uniapp): 修复「选择规格」按钮点击无响应
```

## 4. 当前运行状态

| 服务 | 端口 | 状态 |
|---|---|---|
| MySQL80 | 3306 | ✅ 运行中 |
| Redis | 6379 | ✅ 运行中 |
| MongoDB | 27017 | ✅ 运行中 |
| MinIO | 9005/9006 | ✅ 运行中 |
| 后端 | 8848 | ✅ 运行中（`local` profile） |
| 商家端 | 9527 | ✅ 运行中 |
| 小程序 | - | ✅ 编译产物已更新 |

## 5. 已知限制

- `local` 下向量库为 `InMemoryEmbeddingStore`，重启后端需重新 embed 知识库
- 部分客服页面硬编码 `localhost:8848`，改后端主机需同步调整
- `code/web/training-web` 有未提交修改（`m` 标记），非本次任务产生

## 6. 下一步建议

1. **AI 模块设计.md 占位符填充**：截图「待补充」、测试结果「待验证」需系统跑通后补实测
2. **首个业务 theme 开发**：待 Gate 审批后启动
3. **后端 `code/web/training-web` 的未提交修改**：需确认是否为有效变更

## 7. 日常命令

```powershell
# 启动中间件
powershell -File deploy\local\up-native.ps1

# 后端
cd code\web\training-web\training-server
mvn spring-boot:run -DskipTests "-Dspring-boot.run.profiles=local"

# 商家端
cd code\vue\training-vue3-main
npm run dev

# 小程序
cd code\uniapp\training-uniapp-master
npm run dev:mp-weixin
# 微信开发者工具导入 dist/dev/mp-weixin
```

README 预览：`python -m http.server 8091` → `http://127.0.0.1:8091/preview-readme.html`
