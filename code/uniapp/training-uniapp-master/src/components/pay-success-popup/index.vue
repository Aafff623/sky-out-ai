<template>
	<div class="pay-success-popup">
		<up-popup
			v-bind="$attrs"
			:show="modelValue"
			:custom-style="{ height: '100vh', overflow: 'hidden' }">
			<view class="pay-success-container">
				<view class="success-header">
					<text class="iconfont icon-wechat-pay" />
					<text class="success-text">支付成功</text>
				</view>

				<view class="amount-text">
					<text class="shop-name">{{
						userStore.shopInfo?.name || "锦食坊"
					}}</text>
					<view class="amount">
						<view class="amount-num">
							<view class="amount-symbol">￥</view>
							<text>{{ totalPrice }}</text>
						</view>
					</view>
				</view>

				<view class="bottom-button">
					<up-button
						type="info"
						:hairline="false"
						:customStyle="{
							width: '300rpx',
							height: '70rpx',
							fontWeight: 'bold',
							color: '#2eb76b',
							backgroundColor: '#f2f2f2',
						}"
						@click="onTargetDetail"
						>完 成</up-button
					>
				</view>
			</view>
		</up-popup>
	</div>
</template>

<script lang="ts" setup>
	import { ComponentInternalInstance } from "vue"
	import { useUserStore } from "@/store"

	const props = defineProps({
		modelValue: {
			type: Boolean,
			default: false,
		},
		targetUrl: {
			type: String,
			default: "pages/order/order-detail",
		},
		orderId: [String, Number],
		totalPrice: {
			type: [Number, String],
			default: "0.00",
		},
		customTarget: {
			type: Boolean,
			default: false,
		},
	})

	const userStore = useUserStore()
	const { proxy } = getCurrentInstance() as ComponentInternalInstance

	const emit = defineEmits<{
		(e: "update:modelValue", val: boolean): void
		(e: "custom-target"): void
	}>()

	// 点击完成
	const onTargetDetail = () => {
		if (props.customTarget) {
			return emit("custom-target")
		}

		// 当前页面就是订单详情页，无需跳转，隐藏popup
		const pages = getCurrentPages()
		console.log("当前页面", pages[0].route)

		if (pages.length && pages[0]?.route === props.targetUrl) {
			emit("update:modelValue", false)
			return
		}

		// 跳转到订单详情页
		proxy?.$navTo(props.targetUrl, { orderId: props.orderId }, "redirectTo")
	}
</script>

<style lang="scss" scoped>
	.pay-success-popup {
		.pay-success-container {
			display: flex;
			flex-direction: column;
			height: 100%;
			background-color: #fff;
			padding: 40rpx;
			box-sizing: border-box;
		}

		.success-header {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: flex-start;

			.icon-wechat-pay {
				font-size: 60rpx;
			}
		}

		.success-text {
			font-size: 25rpx;
			font-weight: bold;
			color: #2cbd65;
			margin-top: 10rpx;
		}

		.amount-text {
			flex: 2;
			display: flex;
			justify-content: center;
			align-items: center;
			flex-direction: column;

			.shop-name {
				font-size: 28rpx;
				margin-bottom: 10rpx;
			}

			.amount {
				font-weight: bold;
				display: flex;
				align-items: flex-start;
				justify-content: center;

				.amount-num {
					position: relative;
					font-size: 70rpx;

					.amount-symbol {
						position: absolute;
						font-size: 45rpx;
						margin: 20rpx 10rpx 0 0;
						top: -10rpx;
						left: -40rpx;
					}
				}
			}
		}

		.bottom-button {
			flex: 3;
			display: flex;
			justify-content: center;
			align-items: flex-end;
			padding-bottom: 100rpx;
		}
	}
</style>
