# assets/

媒体与只读备份。**按需建子目录**；空槽不预铺。

## 布局

| 路径 | 内容 | 说明 |
|---|---|---|
| `backup/` | 上游 zip / rar | **只读归档**；已 gitignore，不入库 |
| `images/dishes/` | 28 张菜品图 | 部署后经商家端上传 MinIO |
| `images/deploy/` | 部署步骤截图（原 `docs/readme_img/`） | 配合 `docs/readme.txt` |
| `images/readme/` | 根 README 配图 | Phase B README Polish 使用 |

## 迁移说明（project-init）

| 原路径 | 现路径 |
|---|---|
| `backup/*` | `assets/backup/` |
| `docs/图片素材/*` | `assets/images/dishes/` |
| `docs/readme_img/*` | `assets/images/deploy/` |

毕设论文母版仍在 `docs/苍穹外卖文档/`（不迁入 assets）。

## 还原

- 菜品图：商家端批量上传至 MinIO bucket `training-take-out`
- 备份包：仅作源码/文档快照，日常开发以 `code/` 与 `docs/_conv` 为准
