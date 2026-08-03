<template>
	<view v-if="!isLoading" class="empty-content" :style="customStyle">
		<view class="empty-icon">
			<image class="image" src="/static/empty.png" mode="scaleToFill" />
		</view>
		<view class="tips">{{ tips }}</view>
		<slot name="slot"></slot>
	</view>
</template>

<script lang="ts" setup>
	defineProps({
		// 正在加载
		isLoading: {
			type: Boolean,
			default: false,
		},
		// 自定义样式
		customStyle: {
			type: Object,
			default() {
				return {};
			},
		},
		// 提示的问题
		tips: {
			type: String,
			default: "数据都跑空啦~",
		},
	});
</script>

<style lang="scss" scoped>
	.empty-content {
		box-sizing: border-box;
		width: 100%;
		padding: 140rpx 50rpx;
		text-align: center;

		.tips {
			font-size: 28rpx;
			color: gray;
			margin: 50rpx 0;
		}

		.empty-icon .image {
			width: 280rpx;
			height: 184rpx;
		}
	}
</style>