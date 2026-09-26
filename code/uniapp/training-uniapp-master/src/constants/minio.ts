/**
 * MinIO 对象存储资源地址（本地开发环境 9005 端口）。
 * 图片统一存 MinIO，仓库 assets/images/ui/ 留有同名备份；
 * 真机预览需把 127.0.0.1 改为电脑局域网 IP，并在微信开发者工具中勾选
 * 「不校验合法域名」，正式发布需在小程序后台配置 downloadFile 域名。
 */
export const MINIO_BASE = "http://127.0.0.1:9005/training-take-out";

export const IMG_COMMENT = `${MINIO_BASE}/ui/uniapp/comment.png`;
export const IMG_DEFAULT_AVATAR = `${MINIO_BASE}/ui/uniapp/default-avatar.png`;
export const IMG_DELETE = `${MINIO_BASE}/ui/uniapp/delete.png`;
export const IMG_EMPTY = `${MINIO_BASE}/ui/uniapp/empty.png`;
export const IMG_LIKE = `${MINIO_BASE}/ui/uniapp/like.png`;
export const IMG_NO_COMMENT = `${MINIO_BASE}/ui/uniapp/no-comment.png`;
export const IMG_NO_LIKE = `${MINIO_BASE}/ui/uniapp/no-like.png`;

/**
 * 预设 IP 形象头像，素材来自 ipaslogo.com 免费素材库（可商用），
 * 存于 MinIO ui/avatars/，仓库备份 assets/images/ui/avatars/。
 * 游客/无微信头像的用户从这里选。
 */
export const PRESET_AVATARS: string[] = [
  `${MINIO_BASE}/ui/avatars/sushi-roll-3.webp`,
  `${MINIO_BASE}/ui/avatars/chubby-otter.webp`,
  `${MINIO_BASE}/ui/avatars/croissant.webp`,
  `${MINIO_BASE}/ui/avatars/compact-duckling.webp`,
  `${MINIO_BASE}/ui/avatars/baby-goat.webp`,
  `${MINIO_BASE}/ui/avatars/teapot-3.webp`,
  `${MINIO_BASE}/ui/avatars/penguin-gentle-small-muzzle-left.webp`,
  `${MINIO_BASE}/ui/avatars/kiwi-bird-7.webp`,
];

