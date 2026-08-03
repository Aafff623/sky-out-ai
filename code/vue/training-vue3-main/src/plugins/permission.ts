import { NavigationGuardNext, RouteLocationNormalized } from "vue-router";
import router from "@/router";
import NProgress from "@/utils/nprogress";
import { TOKEN_KEY } from "@/enums/CacheEnum";

export function setupPermission() {
  // 白名单路由
  const whiteList = ["/login"];

  router.beforeEach(async (to, from, next) => {
    NProgress.start();
    const hasToken = localStorage.getItem(TOKEN_KEY);

    if (hasToken) {
      if (to.path === "/login") {
        // 如果已登录，跳转到首页
        next({ path: "/" });
        NProgress.done();
      } else {
        // 如果未匹配到任何路由，跳转到404页面
        if (to.matched.length === 0) {
          next(from.name ? { name: from.name } : "/404");
        } else {
          // 如果路由参数中有 title，覆盖路由元信息中的 title
          const title =
            (to.params.title as string) || (to.query.title as string);
          if (title) {
            to.meta.title = title;
          }
          next();
        }
      }
    } else {
      if (whiteList.includes(to.path)) {
        next();
      } else {
        // 不在白名单，重定向到登录页
        redirectToLogin(to, next);
        NProgress.done();
      }
    }
  });

  router.afterEach(() => {
    NProgress.done();
  });
}

/** 重定向到登录页 */
function redirectToLogin(
  to: RouteLocationNormalized,
  next: NavigationGuardNext
) {
  const params = new URLSearchParams(to.query as Record<string, string>);
  const queryString = params.toString();
  const redirect = queryString ? `${to.path}?${queryString}` : to.path;
  next(`/login?redirect=${encodeURIComponent(redirect)}`);
}
