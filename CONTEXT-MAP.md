# CONTEXT-MAP.md

本仓为**多端**外卖平台。探索或改代码前，先读根 `CONTEXT.md`，再读与主题相关的分端 CONTEXT；架构决策读 `docs/adr/`。

| Context | 路径 | 范围 |
|---|---|---|
| 系统总览 | [`CONTEXT.md`](./CONTEXT.md) | 产品定位、端间关系、数据与 AI 总约束、部署依赖 |
| api | [`docs/contexts/api/CONTEXT.md`](./docs/contexts/api/CONTEXT.md) | 后端 `code/web/training-web` |
| admin | [`docs/contexts/admin/CONTEXT.md`](./docs/contexts/admin/CONTEXT.md) | 商家端 `code/vue/training-vue3-main` |
| weapp | [`docs/contexts/weapp/CONTEXT.md`](./docs/contexts/weapp/CONTEXT.md) | 小程序 `code/uniapp/training-uniapp-master` |

## 消费规则

- 改单一端时：总览 + 该端 CONTEXT。
- 改跨端协议（JWT 头、WS 路径、AI 事件码）时：三端都读，并检查 `AI模块设计.md`。
- 术语冲突：以本 MAP 指向的 CONTEXT 为准，回写时同步 `LANGUAGES.md`。
