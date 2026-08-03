<template>
  <view class="select-region">
    <view class="field-body">
      <view class="field-value" @click="showPopup()">
        <up-input
          disabled
          :placeholder="placeholder"
          :border="border"
          v-model="internalValueText"
          disabledColor="#fff"
          :placeholderClass="placeholderClass"
          :placeholderStyle="placeholderStyle"
        />
      </view>
    </view>

    <up-popup
      :show="show"
      :round="20"
      mode="bottom"
      :popup="false"
      :mask="true"
      :closeable="true"
      :safe-area-inset-bottom="true"
      close-icon-color="#ffffff"
      :z-index="uZIndex"
      :maskCloseAble="maskCloseAble"
      @close="closePopup"
    >
      <view class="title">选择地区</view>

      <!-- 常用城市 -->
      <view class="common-city">
        <view class="f-28 f-w mb-20" style="color: #696b6f">常用城市</view>
        <view class="list">
          <view
            v-for="(item, index) in commonCity"
            :key="index"
            class="item"
            @click="onSelectCommonCity(item)"
          >
            <view class="label">{{ item.label }}</view>
          </view>
        </view>
      </view>

      <view class="tabs-box">
        <up-tabs
          ref="tabs"
          :list="generateTabs"
          :scrollable="true"
          :current="tabsIndex"
          @change="tabsChange"
          lineColor="#cb4544"
          lineHeight="2"
          :inactiveStyle="tabInactiveStyle"
          :itemStyle="tabItemStyle"
          :activeStyle="tabActiveStyle"
        />
      </view>

      <view class="area-box" :style="areaBoxStyle">
        <view class="u-flex" :class="{ change: tabIsChange }">
          <!-- 省份选择 -->
          <view class="area-item">
            <view class="u-padding-10 u-bg-gray" style="height: 100%">
              <scroll-view :scroll-y="true" style="height: 100%">
                <up-cell-group>
                  <up-cell
                    v-for="(p, pi) in region.provinces"
                    :title="p.label"
                    :arrow="false"
                    :index="pi"
                    :key="pi"
                    @click="provinceChange(pi)"
                  >
                    <template v-slot:right-icon>
                      <up-icon
                        v-if="region.isChooseProvince && region.province === pi"
                        size="17"
                        color="#cb4544"
                        name="checkbox-mark"
                      ></up-icon>
                    </template>
                  </up-cell>
                </up-cell-group>
              </scroll-view>
            </view>
          </view>

          <!-- 市区选择 -->
          <view class="area-item">
            <view class="u-padding-10 u-bg-gray" style="height: 100%">
              <scroll-view :scroll-y="true" style="height: 100%">
                <up-cell-group v-if="region.isChooseProvince">
                  <up-cell
                    v-for="(c, ci) in region.citys"
                    :title="c.label"
                    :arrow="false"
                    :index="ci"
                    :key="ci"
                    @click="cityChange(ci)"
                  >
                    <template v-slot:right-icon>
                      <up-icon
                        v-if="region.isChooseCity && region.city === ci"
                        size="17"
                        color="#cb4544"
                        name="checkbox-mark"
                      ></up-icon>
                    </template>
                  </up-cell>
                </up-cell-group>
              </scroll-view>
            </view>
          </view>

          <!-- 区县选择 -->
          <view class="area-item">
            <view class="u-padding-10 u-bg-gray" style="height: 100%">
              <scroll-view :scroll-y="true" style="height: 100%">
                <up-cell-group v-if="region.isChooseCity">
                  <up-cell
                    v-for="(a, ai) in region.areas"
                    :title="a.label"
                    :arrow="false"
                    :index="ai"
                    :key="ai"
                    @click="areaChange(ai)"
                  >
                    <template v-slot:right-icon>
                      <up-icon
                        v-if="region.isChooseArea && region.area === ai"
                        size="17"
                        color="#cb4544"
                        name="checkbox-mark"
                      ></up-icon>
                    </template>
                  </up-cell>
                </up-cell-group>
              </scroll-view>
            </view>
          </view>
        </view>
      </view>
    </up-popup>
  </view>
</template>

<script lang="ts" setup>
import * as RegionData from "@/common/region";
import { PropType } from "vue";

type RegionDataItem = {
  label: string;
  value: string;
};

type SelectResult = {
  province?: RegionDataItem;
  city?: RegionDataItem;
  area?: RegionDataItem;
};

type CommonCityItem = {
  parent: RegionDataItem;
} & RegionDataItem;

const props = defineProps({
  // 通过双向绑定控制组件的弹出与收起
  modelValue: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  // 地区选择结果回显
  valueText: {
    type: String,
    default: "",
  },
  // 弹出的z-index值
  zIndex: {
    type: [String, Number],
    default: 0,
  },
  /** 是否允许通过点击遮罩关闭Picker */
  maskCloseAble: {
    type: Boolean,
    default: true,
  },
  /** 默认显示的地区，可传类似["广西壮族自治区", "柳州市", "鱼峰区"] */
  defaultRegion: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  // 默认显示地区的编码，defaultRegion和areaCode同时存在，areaCode优先，可传类似["13", "1303", "130304"]
  areaCode: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  areaBoxStyle: {
    type: Object,
    default: () => {
      return {
        height: "700rpx",
      };
    },
  },
  // 未选中时的提示文字
  placeholder: {
    type: String,
    default: "请选择省市区街道",
  },
  // 指定placeholder的样式类
  placeholderClass: String,
  // 指定placeholder的样式
  placeholderStyle: {
    type: [String, Object],
    default: "font-size: 25rpx",
  },
  // 边框类型，surround-四周边框，bottom-底部边框，none-无边框
  border: {
    type: String,
    default: "bottom",
  },
  // 菜单item的样式
  tabItemStyle: {
    type: [String, Object],
    default: () => {
      return {
        height: "44px",
        fontSize: "20rpx",
        fontWeight: "bold",
      };
    },
  },
  // 菜单非选中时的样式
  tabInactiveStyle: {
    type: [String, Object],
    default: () => {
      return {
        fontSize: "28rpx",
      };
    },
  },
  // tab 菜单选择中时的样式
  tabActiveStyle: {
    type: [String, Object],
    default: () => {
      return {
        color: "#cb4544",
        fontSize: "28rpx",
        fontWeight: "bold",
      };
    },
  },
  // 常用城市
  commonCity: {
    type: Array as PropType<Array<CommonCityItem>>,
    default: () => {
      return [
        {
          label: "广州市",
          value: "4401",
          parent: { label: "广东省", value: "44" },
        },
        {
          label: "柳州市",
          value: "4502",
          parent: { label: "广西壮族自治区", value: "45" },
        },
        {
          label: "上海市",
          value: "3101",
          parent: { label: "上海市", value: "31" },
        },
        {
          label: "南宁市",
          value: "4501",
          parent: { label: "广西壮族自治区", value: "45" },
        },
        {
          label: "玉林市",
          value: "4509",
          parent: { label: "广西壮族自治区", value: "45" },
        },
        {
          label: "深圳市",
          value: "4403",
          parent: { label: "广东省", value: "44" },
        },
        {
          label: "长春市",
          value: "2201",
          parent: { label: "吉林省", value: "22" },
        },
        {
          label: "北京市",
          value: "1101",
          parent: { label: "北京市", value: "11" },
        },
      ] as Array<CommonCityItem>;
    },
  },
});

const show = ref(false);
const tabsIndex = ref(0);
const region = reactive({
  isChooseProvince: false, // 是否选择了 省份
  isChooseCity: false, // 是否选择了 市
  isChooseArea: false, // 是否选择了 区
  province: 0, // 省份索引
  city: 0, // 市索引
  area: 0, // 区索引
  provinces: RegionData.provinces, // 省份列表
  citys: RegionData.citys[0], // 市列表
  areas: RegionData.areas[0][0], // 区列表
});

const emits = defineEmits<{
  (e: "update:modelValue", val: string[]): void;
  (e: "update:valueText", val: string): void;
  (e: "cityChange", val: SelectResult): void;
}>();

watch(
  () => props.valueText,
  (val) => {
    if (!val && val === "") {
      // 重置选择状态
      resetRegion();
    }
  }
);

// 处理 valueText 双向绑定
const internalValueText = computed({
  get() {
    return props.valueText;
  },
  set(val) {
    emits("update:valueText", val);
  },
});

// 如果用户有传递z-index值，优先使用
const uZIndex = computed<string | number>(() => {
  return props.zIndex ? props.zIndex : uni.$u.zIndex.popup;
});

// tab 项是否发生变化
const tabIsChange = computed(() => {
  return tabsIndex.value > 1;
});

// 动态生成 tabs 列表
const generateTabs = computed(() => {
  let tabs = [{ name: "请选择省份" }];

  // 选择了 省份
  if (region.isChooseProvince) {
    console.log(
      "选择了省份==>",
      region.province,
      region.provinces[region.province]
    );
    tabs[0]["name"] = region.provinces[region.province]["label"];
    tabs[1] = { name: "请选择市" };
  }

  // 选择了 市
  if (region.isChooseCity) {
    tabs[1]["name"] = region.citys[region.city]["label"];
    tabs[2] = { name: "请选择区" };
  }

  if (region.isChooseArea) {
    tabs[2]["name"] = region.areas[region.area]["label"];
  }

  return tabs;
});

onMounted(() => {
  initRegionData();
});

function showPopup(v?: boolean) {
  show.value = v || true;
}

// 初始化数据
function initRegionData() {
  // 如果有传递 areaCode，则优先使用
  setDefault(props.defaultRegion, props.areaCode);
}

// 设置地区选择器默认值
function setDefault(labels?: string[], values?: string[]) {
  if (values?.length === 3) {
    setProvince("", values[0]);
    setCity("", values[1]);
    setArea("", values[2]);
    return;
  }

  if (labels?.length === 3) {
    setProvince(labels[0], "");
    setCity(labels[1], "");
    setArea(labels[2], "");
  }
}

/**
 * 通过名称设置默认值
 * @example 例如：["广西壮族自治区", "柳州市", "鱼峰区"]
 */
function setDefaultByNames(labels?: string[]) {
  if (!labels?.length || labels?.length < 3) {
    uni.$u.toast("请传入正确的地区名称");
    return;
  }
  setProvince(labels[0], "");
  setCity(labels[1], "");
  setArea(labels[2], "");
  // 设置默认值之后同步双向绑定值
  areaChange(region.city);
}

/**
 * 通过编码设置默认值
 * @example 例如：["13", "1303", "130304"]
 */
function setDefaultByCodes(values?: string[]) {
  if (!values?.length || values?.length < 3) {
    uni.$u.toast("请传入正确的地区码");
    return;
  }
  setProvince("", values[0]);
  setCity("", values[1]);
  setArea("", values[2]);
  // 设置默认值之后同步双向绑定值
  areaChange(region.city);
}

function setProvince(label = "", value = "") {
  region.provinces.map((v, k) => {
    if (value ? v.value === value : v.label === label) {
      provinceChange(k);
    }
  });
}

function setCity(label = "", value = "") {
  region.citys.map((v, k) => {
    if (value ? v.value === value : v.label === label) {
      cityChange(k);
    }
  });
}

function setArea(label = "", value = "") {
  region.areas.map((v, k) => {
    if (value ? v.value === value : v.label === label) {
      region.isChooseArea = true;
      region.area = k;
    }
  });
}

const provinceChange = (index: number) => {
  // 已选 `省份`，未选 `市` 或 `区`
  region.isChooseProvince = true;
  region.isChooseCity = false;
  region.isChooseArea = false;

  region.province = index; // 记录当前选择的省份索引
  region.citys = RegionData.citys[index]; // 根据对应的省份，更新市列表
  tabsIndex.value = 1; // 切换到 `市` 选项卡
};

const cityChange = (index: number) => {
  // 已选 `市`，未选 `区`
  region.isChooseCity = true;
  region.isChooseArea = false;

  region.city = index; // 记录当前选择的市索引
  region.areas = RegionData.areas[region.province][index]; // 根据对应的市，更新区列表
  tabsIndex.value = 2; // 切换到 `区` 选项卡
};

const areaChange = (index: number) => {
  // 最终选择结果
  const result: SelectResult = {};
  // 已选 `区`
  region.isChooseArea = true;

  region.area = index; // 记录当前选择的区索引
  result["province"] = region.provinces[region.province];
  result["city"] = region.citys[region.city];
  result["area"] = region.areas[region.area];

  emits("cityChange", result); // 将选择结果返回给父组件
  // 同步双向绑定值
  emits("update:modelValue", getValues(result));

  // 更新valueText的值
  internalValueText.value = `${result.province?.label || ""}-${
    result.city?.label || ""
  }-${result.area?.label || ""}`;

  closePopup(); // 关闭弹窗
};

function getValues(obj: { [key: string]: { value: string } }) {
  const keys = ["province", "city", "area"];
  const values: string[] = [];

  for (const key of keys) {
    if (obj[key] && obj[key].value !== undefined) {
      values.push(obj[key].value);
    }
  }
  return values;
}

// 选择常用城市
const onSelectCommonCity = (city: CommonCityItem) => {
  if (city.value === region.citys[region.city].value) {
    return;
  }
  setProvince("", city.parent.value); // 父级省份
  setCity("", city.value);
};

// tabs 切换
function tabsChange({ index }: { index: number }) {
  tabsIndex.value = index;
}

// 关闭弹窗
function closePopup() {
  show.value = false;
}

// 重置选择状态
function resetRegion() {
  // 重置选择状态
  region.isChooseProvince = false;
  region.isChooseCity = false;
  region.isChooseArea = false;

  // 重置索引
  region.province = 0;
  region.city = 0;
  region.area = 0;

  // 恢复初始的数据，默认展示第一个省、市、区
  region.citys = RegionData.citys[0];
  region.areas = RegionData.areas[0][0];

  // 重置 tabIndex，返回到省份选择的标签
  tabsIndex.value = 0;

  // 清空显示的文本
  internalValueText.value = "";

  // 发出事件通知，双向绑定的值重置为空数组
  emits("update:modelValue", []);
}

defineExpose({
  showPopup,
  setDefault,
  setDefaultByNames,
  setDefaultByCodes,
  resetRegion,
});
</script>

<style lang="scss" scoped>
.select-region .title {
  margin: 30rpx 0 20rpx;
  text-align: center;
  font-size: 35rpx;
  font-weight: bold;
  color: #808080;
}

.common-city {
  padding: 30rpx;
  .list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20rpx;
    .item {
      text-align: center;
      border: 2rpx solid #eee;
      border-radius: 8rpx;
      padding: 10rpx 20rpx;
      font-size: 25rpx;
    }
  }
}

.tabs-box {
  padding: 0 15rpx;
}

.area-box {
  $w: 150%;
  width: 100%;
  overflow: hidden;
  // height: 800rpx;

  > view {
    width: $w;
    transition: transform 0.3s ease-in-out 0s;
    transform: translateX(0);

    &.change {
      transform: translateX(-33.3333333%);
    }
  }

  .area-item {
    width: 33.3333333%;
    height: 800rpx;
  }
}
</style>
