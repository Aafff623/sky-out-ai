<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
onLaunch(() => {
  // console.log("点餐小程序【启动完毕！】");
});
onShow(() => {
  // console.log("点餐小程序【页面显示！】");
});
onHide(() => {
  // console.log("点餐小程序【页面隐藏！】");
});
</script>

<style lang="scss">
/* 注意要写在第一行，同时给style标签加入lang="scss"属性 */
@import "uview-plus/index.scss";
/* 项目基础样式 */
@import "./app.scss";
/* iconfont图标库 */
@import "./utils/iconfont.scss";
</style>
