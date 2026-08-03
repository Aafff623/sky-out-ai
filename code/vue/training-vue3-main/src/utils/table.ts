import type { CellStyle } from "element-plus";
import { useSettingsStoreHook } from "@/store/modules/settings";

const settingsStore = useSettingsStoreHook();

/**
 * 全局自定义el-table表格头部样式
 */
export function getRowClass(scope: {
  rowIndex: number;
  columnIndex: number;
}): CellStyle<any> | undefined {
  const style: CellStyle<any> = {
    height: "70px",
  };
  if (scope.rowIndex == 0) {
    Object.assign(style, {
      fontWeight: "bold",
      color: !settingsStore.isDark
        ? "rgba(0,0,0,0.5)"
        : "var(--el-text-color-primary)",
      backgroundColor: "var(--el-table-row-hover-bg-color)",
    });
    return style;
  }
}
