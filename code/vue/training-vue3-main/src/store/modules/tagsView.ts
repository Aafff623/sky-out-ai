export const useTagsViewStore = defineStore("tagsView", () => {
  const route = useRoute();
  const router = useRouter();
  const visitedViews = ref<TagView[]>([]);
  const cachedViews = ref<string[]>([]);

  /**
   * 添加已访问页面到已访问视图列表中
   */
  function addVisitedView(view: TagView) {
    // 如果已经存在，则不再添加
    if (visitedViews.value.some((v) => v.path === view.path)) return;

    // 如果此页面被标记为固定标签页，则将该页面添加到视图列表开头
    if (view.affix) {
      visitedViews.value.unshift(view);
    } else {
      // 否则就正常添加标签页
      visitedViews.value.push(view);
    }
  }

  /**
   * 添加缓存页面到缓存列表中
   * @description 缓存页面就是在 meta: { keepAlive: true } 的页面
   */
  function addCachedView(view: TagView) {
    const viewName = view.name;
    // 已存在该缓存页面，不再添加
    if (cachedViews.value.includes(viewName)) return;

    // 仅添加 meta: { keepAlive: true } 的页面
    if (view.keepAlive) {
      cachedViews.value.push(viewName);
    }
  }

  /**
   * 从已访问视图列表中删除指定的页面
   * @returns 返回删除后的已访问视图列表
   */
  function delVisitedView(view: TagView) {
    return new Promise<TagView[]>((resolve) => {
      for (const [i, v] of visitedViews.value.entries()) {
        // 通过比较页面路径判断是否是同一个页面
        if (v.path === view.path) {
          visitedViews.value.splice(i, 1);
          break;
        }
      }
      resolve([...visitedViews.value]);
    });
  }

  /**
   * 删除指定缓存页面
   * @param view 要删除的缓存页面
   * @returns 返回删除后的缓存页面列表
   * @example 返回结果示例：['/dashboard', '/about']
   */
  function delCachedView(view: TagView) {
    const viewName = view.name;
    return new Promise<string[]>((resolve) => {
      // 查找要删除的缓存页面是否存在
      const index = cachedViews.value.indexOf(viewName);
      index > -1 && cachedViews.value.splice(index, 1);
      resolve([...cachedViews.value]);
    });
  }

  /**
   * 删除其他已访问页面
   * @description 除了当前页面和固定标签页以外的其他已访问页面都删除
   */
  function delOtherVisitedViews(view: TagView) {
    return new Promise<TagView[]>((resolve) => {
      visitedViews.value = visitedViews.value.filter((v) => {
        return v?.affix || v.path === view.path;
      });
      resolve([...visitedViews.value]);
    });
  }
  /**
   * 删除指定缓存视图之外的其他所有缓存视图
   */
  function delOtherCachedViews(view: TagView) {
    const viewName = view.name;
    return new Promise<string[]>((resolve) => {
      const index = cachedViews.value.indexOf(viewName);
      if (index > -1) {
        // 仅截取指定的缓存视图，其他缓存视图删除
        cachedViews.value = cachedViews.value.slice(index, index + 1);
      } else {
        // index < 0 证明指定的缓存视图不存在，则清空缓存视图列表
        cachedViews.value = [];
      }
      resolve([...cachedViews.value]);
    });
  }

  /**
   * 更新指定视图的状态
   * @description 使用Object.assign(target, ...sources)方法，将source中的所有可枚举属性复制到target对象中
   */
  function updateVisitedView(view: TagView) {
    for (let v of visitedViews.value) {
      if (v.path === view.path) {
        // 将 view 中的所有可枚举属性复制到 v 对象中
        v = Object.assign(v, view);
        break;
      }
    }
  }

  /**
   * 添加视图（如果该视图被标记为keepAlive缓存视图，则将其添加到缓存列表中）
   * @description
   * - addVisitedView(view);
   * - addCachedView(view);
   */
  function addView(view: TagView) {
    addVisitedView(view);
    addCachedView(view);
  }

  /**
   * 删除视图（如果该视图被标记为keepAlive缓存视图，则将缓存中的视图也删除）
   * @returns  返回删除后的视图列表（已访问视图列表和缓存视图列表）
   */
  function delView(view: TagView) {
    return new Promise<{ visitedViews: TagView[]; cachedViews: string[] }>(
      (resolve) => {
        delVisitedView(view);
        delCachedView(view);
        resolve({
          visitedViews: [...visitedViews.value],
          cachedViews: [...cachedViews.value],
        });
      }
    );
  }

  /**
   * 删除指定视图之外的其他视图以及缓存视图
   */
  function delOtherViews(view: TagView) {
    return new Promise<{ visitedViews: TagView[]; cachedViews: string[] }>(
      (resolve) => {
        delOtherVisitedViews(view);
        delOtherCachedViews(view);
        resolve({
          visitedViews: [...visitedViews.value],
          cachedViews: [...cachedViews.value],
        });
      }
    );
  }

  /**
   * 关闭当前指定标签的左侧所有非固定标签
   * @returns 返回删除后的已访问视图列表
   */
  function delLeftViews(view: TagView) {
    return new Promise<{ visitedViews: TagView[] }>((resolve) => {
      // 当前指定标签所在下标
      const curTagIndex = visitedViews.value.findIndex(
        (v) => v.path === view.path
      );
      if (curTagIndex === -1) return;

      visitedViews.value = visitedViews.value.filter((item, index) => {
        // 处于当前指定标签的右侧或者是固定标签则保留
        if (index >= curTagIndex || item?.affix) {
          return true;
        }

        // 当前指定标签是否存在于缓存标签列表中，存在则删除
        const cacheTagIndex = cachedViews.value.indexOf(item.name);
        if (cacheTagIndex > -1) {
          cachedViews.value.splice(cacheTagIndex, 1);
        }
        return false;
      });

      resolve({
        visitedViews: [...visitedViews.value],
      });
    });
  }

  /**
   * 删除当前指定标签的右侧所有非固定标签
   * @returns 返回删除后的已访问视图列表
   */
  function delRightViews(view: TagView) {
    return new Promise<{ visitedViews: TagView[] }>((resolve) => {
      // 当前指定标签所在下标
      const curTagIndex = visitedViews.value.findIndex(
        (v) => v.path === view.path
      );
      if (curTagIndex === -1) return;

      visitedViews.value = visitedViews.value.filter((item, index) => {
        if (index <= curTagIndex || item?.affix) {
          return true;
        }
      });

      resolve({
        visitedViews: [...visitedViews.value],
      });
    });
  }

  /**
   * 删除所有非固定标签
   */
  function delAllViews() {
    return new Promise<{ visitedViews: TagView[]; cachedViews: string[] }>(
      (resolve) => {
        // 保留固定标签
        const affixTags = visitedViews.value.filter((tag) => tag?.affix);
        visitedViews.value = affixTags;
        cachedViews.value = [];
        resolve({
          visitedViews: [...visitedViews.value],
          cachedViews: [...cachedViews.value],
        });
      }
    );
  }

  /**
   * 删除所有非固定标签（但不删除缓存视图）
   */
  function delAllVisitedViews() {
    return new Promise<TagView[]>((resolve) => {
      const affixTags = visitedViews.value.filter((tag) => tag?.affix);
      visitedViews.value = affixTags;
      resolve([...visitedViews.value]);
    });
  }

  /**
   * 清空所有的缓存视图
   */
  function delAllCachedViews() {
    return new Promise<string[]>((resolve) => {
      cachedViews.value = [];
      resolve([...cachedViews.value]);
    });
  }

  /**
   * 关闭当前路由的标签页
   */
  function closeCurrentView() {
    // 当前路由
    const tags: TagView = {
      name: route.name as string,
      title: route.meta.title as string,
      path: route.path,
      fullPath: route.fullPath,
      affix: route.meta?.affix,
      keepAlive: route.meta?.keepAlive,
      query: route.query,
    };

    delView(tags).then(({ visitedViews }) => {
      if (isActive(tags)) {
        toLastView(visitedViews, tags);
      }
    });
  }

  /**
   * 当前指定标签是否是激活状态
   * @description tag.path === route.path
   */
  function isActive(tag: TagView) {
    return tag.path === route.path;
  }

  /**
   * 当关闭了指定的标签时，跳转到这个指定关闭的标签的上一个标签
   * @param visitedViews 已访问视图列表
   * @param view 标签对象
   */
  function toLastView(visitedViews: TagView[], view?: TagView) {
    // 获取最后一个标签
    const latestView = visitedViews.slice(-1)[0];

    // 当指定关闭了最后一个标签时，则跳转到上一个标签
    if (latestView && latestView.fullPath) {
      router.push(latestView.fullPath);
    } else {
      if (view?.name === "Dashboard") {
        router.replace("/redirect" + view.fullPath);
      } else {
        router.push("/");
      }
    }
  }

  return {
    visitedViews,
    cachedViews,
    addVisitedView,
    addCachedView,
    delVisitedView,
    delCachedView,
    delOtherVisitedViews,
    delOtherCachedViews,
    updateVisitedView,
    addView,
    delView,
    delOtherViews,
    delLeftViews,
    delRightViews,
    delAllViews,
    delAllVisitedViews,
    delAllCachedViews,
    closeCurrentView,
    isActive,
    toLastView,
  };
});
