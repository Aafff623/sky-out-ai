<script lang="ts" setup>
import { MINIO_BASE } from "@/constants/minio";

const visible = defineModel<boolean>({ default: false });
const emit = defineEmits<{ (e: "select", url: string): void }>();

const CAT_LABELS: Record<string, string> = {
  animals: "动物",
  nature: "自然",
  food: "食物",
  objects: "物品",
  symbols: "符号",
  other: "其他",
};

const library = ref<Record<string, { name: string; slug: string; url: string }[]>>({});
const activeCat = ref("food");
const loading = ref(false);

watch(visible, async v => {
  if (!v || Object.keys(library.value).length) return;
  loading.value = true;
  try {
    const res = await fetch(`${MINIO_BASE}/ui/avatar-library/index.json`);
    library.value = await res.json();
  } finally {
    loading.value = false;
  }
});

function onPick(url: string) {
  emit("select", url);
  visible.value = false;
}

defineOptions({
  name: "AvatarLibraryPicker",
});
</script>

<template>
  <el-dialog v-model="visible" title="形象素材库" width="680px" destroy-on-close>
    <el-tabs v-model="activeCat">
      <el-tab-pane v-for="(_, cat) in library" :key="cat" :label="CAT_LABELS[cat] || cat" :name="cat" />
    </el-tabs>
    <div v-loading="loading" class="grid">
      <img
        v-for="it in library[activeCat] || []"
        :key="it.url"
        :src="it.url"
        :title="it.name"
        loading="lazy"
        class="cell"
        @click="onPick(it.url)"
      />
      <el-empty
        v-if="!loading && !(library[activeCat] || []).length"
        description="该分类暂无素材"
      />
    </div>
  </el-dialog>
</template>

<style lang="scss" scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(72px, 1fr));
  gap: 10px;
  max-height: 420px;
  overflow: auto;

  .cell {
    width: 72px;
    height: 72px;
    border-radius: 12px;
    object-fit: cover;
    cursor: pointer;
    transition: transform 0.15s;

    &:hover {
      transform: scale(1.08);
    }
  }
}
</style>
