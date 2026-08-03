import { store } from "@/store";
import ShopAPI from "@/api/shop";
import defaultSettings from "@/settings";
import { DeviceEnum } from "@/enums/DeviceEnum";
import { SidebarStatusEnum } from "@/enums/SidebarStatusEnum";
import { StatusEnum } from "@/enums/StatusEnum";


// 导入 Element Plus 中英文语言包
import zhCn from "element-plus/es/locale/lang/zh-cn";
import en from "element-plus/es/locale/lang/en";

export const useAppStore = defineStore("app", () => {
  // 设备类型
  const device = useStorage("device", DeviceEnum.DESKTOP);
  // 布局大小
  const size = useStorage("size", defaultSettings.size);
  // 语言
  const language = useStorage("language", defaultSettings.language);
  // 侧边栏状态
  const sidebarStatus = useStorage("sidebarStatus", SidebarStatusEnum.CLOSED);
  const sidebar = reactive({
    opened: sidebarStatus.value === SidebarStatusEnum.OPENED,
    withoutAnimation: false,
  });

  // 顶部菜单激活路径
  const activeTopMenuPath = useStorage("activeTopMenuPath", "");
  const isMobile = computed(() => device.value === DeviceEnum.MOBILE);
  // 店铺营业状态
  const shopStatus = useStorage("shopStatus", StatusEnum.DISABLE);

  /**
   * 根据语言标识读取对应的语言包
   */
  const locale = computed(() => {
    if (language?.value == "en") {
      return en;
    } else {
      return zhCn;
    }
  });

  /** 店铺是否处于营业状态 */
  const isOpenShop = computed(() => {
    return shopStatus.value === StatusEnum.ENABLE;
  });

  /**
   * 获取店铺营业状态
   */
  function getShopStatus() {
    ShopAPI.getStatus()
      .then((res) => {
        shopStatus.value = res.data as any;
      })
      .catch(() => {
        shopStatus.value = StatusEnum.DISABLE;
      });
  }

  /**
   * 设置店铺营业状态
   * @param value 店铺营业状态
   */
  function setShopStatus(value: StatusEnum) {
    ShopAPI.status(value)
      .then(() => {
        shopStatus.value = value;
        ElMessage.success(
          `店铺【${value === StatusEnum.ENABLE ? "营业中" : "打样中"}】`
        );
      })
      .catch(() => {
        shopStatus.value = StatusEnum.DISABLE;
      });
  }

  /**
   * 切换侧边栏
   */
  function toggleSidebar() {
    sidebar.opened = !sidebar.opened;
    sidebarStatus.value = sidebar.opened
      ? SidebarStatusEnum.OPENED
      : SidebarStatusEnum.CLOSED;
  }

  /**
   * 关闭侧边栏
   */
  function closeSideBar() {
    sidebar.opened = false;
    sidebarStatus.value = SidebarStatusEnum.CLOSED;
  }

  /**
   * 打开侧边栏
   */
  function openSideBar() {
    sidebar.opened = true;
    sidebarStatus.value = SidebarStatusEnum.OPENED;
  }

  /**
   * 切换设备
   * @param val 设备类型 desktop（宽屏） | mobile（窄屏）
   */
  function toggleDevice(val: string) {
    device.value = val;
  }

  /**
   * 改变布局大小
   * @param val 布局大小 default | small | large
   */
  function changeSize(val: string) {
    size.value = val;
  }
  /**
   * 切换语言
   * @param val
   */
  function changeLanguage(val: string) {
    language.value = val;
  }

  /**
   * 混合模式顶部切换
   */
  function activeTopMenu(val: string) {
    activeTopMenuPath.value = val;
  }

  return {
    device,
    sidebar,
    language,
    locale,
    size,
    activeTopMenuPath,
    isOpenShop,
    shopStatus,
    isMobile,
    getShopStatus,
    setShopStatus,
    activeTopMenu,
    toggleDevice,
    changeSize,
    changeLanguage,
    toggleSidebar,
    closeSideBar,
    openSideBar,
  };
});

// 在单文件中使用pinia，不这样做会报错，pinia未安装注册
export function useAppStoreHook() {
  return useAppStore(store);
}
