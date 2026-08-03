<template>
	<view class="order-card">
		<view
			class="top mb-10"
			@click="onTargetOrderDetail(order)">
			<view class="left f-w oneline-hide">{{
				userStore.getShopRecord.name
			}}</view>
			<!-- 订单状态 -->
			<view class="right">
				<view class="status">{{ statusText }}</view>
				<up-icon
					name="arrow-right"
					color="#c5c5c5"
					size="12" />
			</view>
		</view>

		<!-- 订单下单时间 -->
		<view class="time-info dis-flex flex-y-center flex-x-between">
			<view class="order-time">{{ order.orderTime }}</view>
			<view
				class="dis-flex flex-y-center flex-x-center"
				v-if="showCountdown">
				<text class="count-down-text">{{ formattedTime }}</text>
				<text class="ml-10">后将自动取消</text>
			</view>
		</view>

		<view class="content">
			<!-- 左侧订单商品列表 -->
			<scroll-view
				:scroll-x="true"
				class="order-detail-list">
				<view
					class="item-left"
					v-for="(item, index) in order.orderDetailList"
					:key="item?.id || index">
					<up-image
						:radius="5"
						show-loading
						:src="item.image"
						width="130rpx"
						height="130rpx" />
					<view class="item-name w-full oneline-hide">{{ item.name }}</view>
				</view>
			</scroll-view>

			<!-- 右侧商品总价以及总数量 -->
			<view class="item-right">
				<view class="price">￥{{ order.amount || 0 }}</view>
				<view class="total">共{{ order.orderDetailList?.length || 0 }}件</view>
			</view>
		</view>

		<view class="bottom">
			<view class="more">
				<up-icon
					name="more-dot-fill"
					color="rgb(203,203,203)" />
			</view>

			<view
				v-if="order.isComment === 0 && order.status === 5"
				class="evaluate btn"
				@click="handlerComment(order)"
				>立即评价</view
			>
			<view
				v-if="order.status === 1"
				class="evaluate btn"
				@click="onTargetOrderDetail(order)"
				>去支付</view
			>
			<view
				class="evaluate btn"
				@click="onRepetition(order.id)"
				>再来一单</view
			>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ComponentInternalInstance } from "vue"
	import { OrderStatus } from "@/enums/OrderEnum"
	import { useUserStore, useOrderCountdownStore } from "@/store"
	import { useShoppingCartStore } from "@/store"
	import OrderAPI, { OrderVO } from "@/api/order"
	import * as OrderConstants from "@/common/constant/order"

	const cartStore = useShoppingCartStore()

	const props = defineProps({
		order: {
			required: true,
			type: Object as PropType<OrderVO>,
			default: () => ({}),
		},
		statusText: {
			type: String,
			default: "",
		},
	})

	const userStore = useUserStore()
	const orderCountdownStore = useOrderCountdownStore()
	const { proxy } = getCurrentInstance() as ComponentInternalInstance

	const emits = defineEmits<{
		(e: "canceled", orderId: OrderVO["id"]): void
	}>()

	// 计算显示倒计时
	const showCountdown = computed(
		() =>
			props.order.status === OrderStatus.PENDING_PAYMENT &&
			orderCountdownStore.getRemainingSeconds(props.order.id) > 0
	)

	// 获取格式化时间
	const formattedTime = computed(() =>
		orderCountdownStore.getFormattedTime(props.order.id)
	)

	const onTargetOrderDetail = (order: OrderVO) => {
		proxy?.$navTo("pages/order/order-detail", { orderId: order.id })
	}

	// 初始化时添加订单
	onMounted(() => {
		// console.log("orderInfo:", props.order)

		if (props.order.status === OrderStatus.PENDING_PAYMENT) {
			orderCountdownStore.addOrder(props.order, orderId =>
				emits("canceled", orderId)
			)
		}
	})

	// 再来一单
	const onRepetition = (orderId: string) => {
		uni.showModal({
			title: "再来一单",
			content: "确定再来一单吗？",
			async success({ confirm }) {
				if (!confirm) {
					return
				}
				// 如果购物车存在商品，先清空购物车
				if (cartStore.isCartNotEmpty) {
					await cartStore.cleanCart()
				}

				OrderAPI.repetition(orderId!).then(res => {
					if (res.code === 200) {
						// tabbar 页面只能使用 switchTab(跳转到 tabBar 页面，并关闭其他所有非 tabBar 页面) 跳转，且无法携带参数
						uni.setStorageSync(OrderConstants.IS_REPETITION, true)
						proxy?.$navTo("pages/index/index")
					}
				})
			},
		})
	}

	//立即评价
	function handlerComment(order: OrderVO) {
		// console.log("立即评价:", order)
		//传递整个order
		const orderEncode = encodeURIComponent(JSON.stringify(order))
		uni.navigateTo({
			url: `/pages/comment/components/comment?order=${orderEncode}`,
		})
	}

	// 监听订单状态变化
	watch(
		() => props.order.status,
		newStatus => {
			if (newStatus !== OrderStatus.PENDING_PAYMENT) {
				orderCountdownStore.removeOrder(props.order.id)
			}
		}
	)
</script>

<style lang="scss" scoped>
	.order-card {
		width: 710rpx;
		background-color: #ffffff;
		margin: 20rpx auto;
		border-radius: 20rpx;
		box-sizing: border-box;
		padding: 30rpx;
		font-size: 28rpx;
		.top {
			display: flex;
			justify-content: space-between;
			.left {
				flex: 3;
			}
			.right {
				flex: 1;
				display: flex;
				align-items: center;
				justify-content: flex-end;
				.status {
					font-size: 25rpx;
					margin-right: 10rpx;
				}
			}
		}

		.time-info {
			.order-time {
				font-size: 25rpx;
				color: #777777;
				margin: 10rpx 0 20rpx 0;
			}

			text {
				font-size: 25rpx;
			}

			.count-down-text {
				color: #a32435;
				font-weight: bold;
			}
		}

		// 订单商品列表（横向滑动列表）
		.content {
			$item-right-width: 120rpx;
			width: 100%;
			position: relative;
			.order-detail-list {
				width: 100%;
				white-space: nowrap;

				.item-left {
					width: 130rpx;
					display: inline-flex;
					flex-direction: column;
					justify-content: center;
					align-items: center;
					&:not(:last-child) {
						margin-right: 20rpx;
					}
					&:last-child {
						padding-right: $item-right-width;
					}
					.item-name {
						margin-top: 10rpx;
						font-size: 25rpx;
						color: #777777;
						text-align: center;
					}
				}
			}

			.item-right {
				width: $item-right-width;
				height: 100%;
				position: absolute;
				top: 0;
				right: 0;
				background-color: #fff;
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;
				box-shadow: -20rpx 0 50rpx 20rpx white;

				.price {
					font-weight: bold;
					font-size: 35rpx;
					font-family: fantasy;
				}
				.total {
					margin-top: 15rpx;
					font-size: 22rpx;
					color: #999999;
				}
			}
		}

		.bottom {
			display: flex;
			margin-top: 20rpx;
			padding: 0 10rpx;
			justify-content: flex-end;
			align-items: center;
			.more {
				margin-right: auto;
			}
			.btn {
				line-height: 52rpx;
				width: 160rpx;
				border-radius: 26rpx;
				border: 2rpx solid #f1a532;
				font-size: 26rpx;
				text-align: center;
				color: #f1a532;
				&:not(:last-child) {
					margin-right: 20rpx;
				}
			}

			.evaluate {
				color: #f1a532;
				border-color: #f1a532;
			}
		}
	}
</style>
