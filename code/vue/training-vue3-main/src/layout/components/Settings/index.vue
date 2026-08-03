<script lang="ts" setup>
import { useSettingsStore } from "@/store";
import { ThemeEnum } from "@/enums/ThemeEnum";
import { useSocket } from "@/hooks/useSocket";
import { OrderSocketMessage } from "@/api/order";
import { audioPlayer } from "@/utils/audio";

const router = useRouter();
const settingsStore = useSettingsStore();
const { socket } = useSocket();
// 主题暗亮模式
const isDark = ref(settingsStore.theme === ThemeEnum.DARK);
const transitionOptions = ref([
  { label: "fade", value: "fade" },
  { label: "slide", value: "fade-slide" },
  { label: "scale", value: "fade-scale" },
  { label: "rotate", value: "fade-rotate" },
]);
const mute = ref(true); // 不播放声音

const settingsVisible = computed({
  get() {
    return settingsStore.settingsVisible;
  },
  set() {
    settingsStore.settingsVisible = false;
  },
});

// 收到消息
socket.on("message", (data) => {
  const result = JSON.parse(data || "{}") as OrderSocketMessage;
  if (!result) return;

  const messageContent = `
    <div>
      <span>你有一个</span>
      <span class="text-[var(--el-color-primary)]">新的订单</span>
      <span>，${result.content}</span>
    </div>
  `;

  ElNotification({
    title: result.type === 1 ? "新订单提醒" : "催单提醒",
    dangerouslyUseHTMLString: true,
    message: messageContent,
    duration: 5000,
    customClass: "cursor-pointer",
    offset: 40,
    onClick: () => {
      router.push({ path: "/order/index", query: { id: result.orderId } });
    },
  });

  // 播放对应语音
  const audioMap: Record<string, string> = {
    1: "/media/new_order.mp3",
    2: "/media/reminder_order.mp3",
  };

  if (!mute && result.type && audioMap[result.type]) {
    audioPlayer.play(audioMap[result.type]);
  }
});

watch(isDark, (newVal) => changeTheme(newVal));

const handleSwitchClick = (e: MouseEvent) => {
  settingsStore.setClickEvent(e);
};

/** 切换主题颜色 */
function changeThemeColor(color: string) {
  settingsStore.changeThemeColor(color);
}

/** 切换主题暗亮 */
const changeTheme = (val: boolean) => {
  isDark.value = val;
  settingsStore.changeTheme(isDark.value ? ThemeEnum.DARK : ThemeEnum.LIGHT);
};

onBeforeUpdate(socket.close);

defineOptions({
  name: "Settings",
});
</script>

<template>
  <el-drawer
    v-model="settingsVisible"
    size="300"
    :title="$t('settings.project')"
  >
    <el-divider>{{ $t("settings.theme") }}</el-divider>

    <!-- @change="changeTheme" -->
    <div class="flex-center">
      <el-switch
        v-model="isDark"
        active-icon="MoonNight"
        inactive-icon="Sunny"
        @click="handleSwitchClick"
      />
    </div>

    <el-divider>{{ $t("settings.interface") }}</el-divider>

    <div class="setting-item">
      <span class="text-xs">{{ $t("settings.themeColor") }}</span>
      <ThemeColorPicker
        v-model="settingsStore.themeColor"
        @update:model-value="changeThemeColor"
      />
    </div>

    <div class="setting-item">
      <span class="text-xs">{{ $t("settings.themeAnimation") }}</span>
      <el-switch v-model="settingsStore.themeAnimation" />
    </div>

    <div class="setting-item">
      <span class="text-xs">{{ $t("settings.tagsView") }}</span>
      <el-switch v-model="settingsStore.tagsView" />
    </div>

    <div class="setting-item">
      <span class="text-xs">{{ $t("settings.fixedHeader") }}</span>
      <el-switch v-model="settingsStore.fixedHeader" />
    </div>

    <div class="setting-item">
      <span class="text-xs">{{ $t("settings.sidebarLogo") }}</span>
      <el-switch v-model="settingsStore.sidebarLogo" />
    </div>

    <div class="setting-item">
      <span class="text-xs">{{ $t("settings.watermark") }}</span>
      <el-switch v-model="settingsStore.watermarkEnabled" />
    </div>

    <el-divider>{{ $t("settings.transition") }}</el-divider>
    <div class="w-full flex justify-center my-30px">
      <el-select
        clearable
        class="w-full"
        v-model="settingsStore.transitionType"
        placeholder="选择页面切换动画"
      >
        <el-option
          v-for="item in transitionOptions"
          :key="item.value"
          :value="item.value"
          :label="$t(`settings.transition_type.${item.label}`)"
        />
      </el-select>
    </div>
  </el-drawer>
</template>

<style lang="scss" scoped>
.setting-item {
  @apply py-1 flex-x-between;
}
</style>
