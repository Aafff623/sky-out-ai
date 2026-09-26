/**
 * MinIO 对象存储资源地址（本地开发环境 9005 端口）。
 * 图片统一存 MinIO，仓库 assets/images/ui/ 留有同名备份；
 * 更换部署地址时只需改 MINIO_BASE。
 */
export const MINIO_BASE = "http://127.0.0.1:9005/training-take-out";

export const IMG_AVATAR = `${MINIO_BASE}/ui/vue/avatar.png`;
export const IMG_401 = `${MINIO_BASE}/ui/vue/images/401.gif`;
export const IMG_404 = `${MINIO_BASE}/ui/vue/images/404.png`;
export const IMG_404_CLOUD = `${MINIO_BASE}/ui/vue/images/404_cloud.png`;

/**
 * 预设 IP 形象头像，素材来自 ipaslogo.com 免费素材库（可商用），
 * 存于 MinIO ui/avatars/，仓库备份 assets/images/ui/avatars/。
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

/** 小程序用户默认头像（微信授权用户未设置头像时的兜底） */
export const IMG_WX_DEFAULT_AVATAR = `${MINIO_BASE}/ui/uniapp/default-avatar.png`;

