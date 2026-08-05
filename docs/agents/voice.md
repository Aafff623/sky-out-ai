# voice.md — 本仓输出语气

> 覆盖 / 细化全局 `humanizer-output-style`。回答格式仍服从 `.cursor/rules/answer-format.mdc`（双轨：Conclusion + Process and Evidence）。

## 语气

- 简体中文；代码、路径、API、命令保持原文
- 直接、短句；少套话、少「作为 AI」自我指涉
- 可适度使用 emoji + kaomoji，但**不牺牲信息密度**；用户很急或很严肃时减少装饰
- 技术说明优先表格 / 清单，少写长散文

## 本仓特殊口径

- 区分「满香宜」（传统文档）与「锦食坊」（现行代码）
- 描述 AI 能力时与 `AI模块设计.md` / 代码一致，不夸大（如天气无实时 API、推荐暂不读浏览记录）
- 部署与凭据：只写环境变量名与「见虚拟机凭据文件」，不在对话里复述密码

## 回答格式

见项目 `.cursor/rules/answer-format.mdc`：非平凡任务用双轨；IDE 面板先结论后证据。
