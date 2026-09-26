<template>
	<view
		class="avatar-image"
		:style="borderStyle">
		<up-avatar
			v-bind="$attrs"
			class="image"
			:size="width"
			:mode="mode"
			:randomBgColor="randomBgColor"
			:src="url ? url : IMG_DEFAULT_AVATAR"></up-avatar>
	</view>
</template>

<script lang="ts" setup>
	import { IMG_DEFAULT_AVATAR } from "@/constants/minio"

	const props = defineProps({
		url: {
			type: String,
			default: "",
		},
		width: {
			type: Number,
			default: 40,
		},
		showBorder: {
			type: Boolean,
			default: true,
		},
		mode: {
			type: String,
			default: "aspectFill",
		},
		borderWidth: {
			type: Number,
			default: 6,
		},
		borderColor: {
			type: String,
			default: "rgba(255,255,255,0.3)",
		},
		randomBgColor: {
			type: Boolean,
			default: true,
		},
	})

	const borderStyle = computed(() => {
		if (props.showBorder) {
			return {
				width: `${props.width}px`,
				height: `${props.width}px`,
				overflow: "hidden",
				borderRadius: `50%`,
				border: `${props.borderWidth}rpx solid ${props.borderColor}`,
			}
		}
	})
</script>

<style lang="scss" scoped>
	.avatar-image {
		.image {
			display: block;
			width: 80rpx;
			height: 60rpx;
			border-radius: 50%;
			border-style: solid;
		}
	}
</style>
