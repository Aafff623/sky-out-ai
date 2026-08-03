<template>
	<view class="order-payment">
		<!-- 待支付倒计时（只有是待付款订单才会显示） -->
		<view class="countdown flex-center">
			<view
				v-if="
					remainingSeconds && orderInfo.status === OrderStatus.PENDING_PAYMENT
				"
				class="dis-flex flex-y-center flex-x-center">
				<text class="f-25 mr-10">等待支付，剩余时长</text>
				<text class="time">{{ formattedTime }}</text>
			</view>

			<view
				v-else
				class="f-25"
				>感谢您对我们的支持，欢迎再次光临</view
			>
		</view>

		<!-- 堂食取餐码 （已支付订单才会显示） -->
		<up-card
			v-if="
				orderInfo.orderType === 2 &&
				orderInfo.pickupNumber &&
				orderInfo.status === OrderStatus.DINE_IN_ORDER
			"
			:border="false"
			:foot-border-top="false"
			:show-head="false"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)">
			<template #body>
				<view class="pickup-info">
					<view class="pickuo-code-text">取餐号</view>
					<view class="pickup-number">{{ orderInfo.pickupNumber }}</view>
				</view>
			</template>
		</up-card>

		<up-card
			:border="false"
			:head-border-bottom="false"
			:foot-border-top="false"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)"
			:body-style="{
				'border-bottom': '5rpx solid #eee',
			}"
			:head-style="{
				'border-bottom': '5rpx solid #eee',
			}">
			<template #head>
				<view class="notice mb-30">
					<up-notice-bar
						:text="
							orderInfo.orderType === 1
								? `支付后预计送达时间将会更新，超过${
										countdownStore.TOTAL_SECONDS / 60
								  }分钟后未支付订单将自动取消。`
								: `订单支付成功后，商家预计10分钟左右出餐请注意留意`
						"
						style="border-radius: 10px 10px 0 0" />
				</view>
				<view class="card-header dis-flex flex-y-center flex-x-between">
					<!-- 店铺名称 -->
					<view class="shop-name f-w oneline-hide">{{
						countdownStore.shopTitle
					}}</view>
				</view>
			</template>

			<template #body>
				<!-- 订单信息 -->
				<view
					class="order-dish"
					:style="orderDishStyle">
					<view
						v-for="(dish, index) in orderInfo.orderDetailList"
						:key="index"
						class="order-dish-item dis-flex mb-15">
						<view class="dish-image mr-20">
							<up-image
								show-loading
								:src="dish.image"
								width="100rpx"
								height="100rpx"
								:radius="5" />
						</view>

						<view class="dish-info w-full dis-flex flex-x-between">
							<view class="dis-flex flex-dir-column">
								<text class="f-w f-25">{{ dish.name }}</text>
								<text
									class="f-20 mt-10"
									style="color: #8c8c8c"
									>x{{ dish.number }}</text
								>
							</view>

							<view class="price">
								<text class="f-20">￥</text>
								<text class="f-30">{{
									Number(dish.amount) * Number(dish.number) || 0
								}}</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 展开/收起菜品列表 -->
				<view
					class="flex-center"
					v-if="
						orderInfo.orderDetailList && orderInfo.orderDetailList.length > 3
					"
					@click="showMoreDish = !showMoreDish">
					<view class="f-25 mr-10">{{
						showMoreDish ? "收起" : "展开更多"
					}}</view>
					<up-icon
						:name="showMoreDish ? 'arrow-up' : 'arrow-down'"
						color="#252525"
						size="14" />
				</view>
			</template>

			<template #foot>
				<view class="card-footer">
					<view class="f-22"
						>共<text class="count">{{
							orderInfo.orderDetailList?.length || 0
						}}</text
						>件商品</view
					>
					<view class="dis-flex flex-y-center">
						<text class="f-22 mr-10"
							>合计{{
								orderInfo.orderType === 1 ? "（含打包费、配送费）" : ""
							}}</text
						>
						<view class="price">
							<text class="f-20">￥</text>
							<text class="f-30">{{ Number(orderInfo.amount) || 0 }}</text>
						</view></view
					>
				</view>
			</template>
		</up-card>

		<!-- 订单信息 -->
		<up-card
			:border="false"
			:foot-border-top="false"
			:show-head="false"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)">
			<template #body>
				<view class="order-info">
					<view class="order-info-item">
						<view class="left">订单编号</view>
						<view class="right">{{ orderInfo.number }}</view>
					</view>

					<view class="order-info-item">
						<view class="left">下单时间</view>
						<view class="right">{{ orderInfo.orderTime }}</view>
					</view>

					<view class="order-info-item">
						<view class="left">支付方式</view>
						<view
							@click="onSelectPay"
							class="right dis-flex flex-x-center flex-y-center">
							<text
								class="iconfont f-40"
								:class="`icon-${
									orderInfo.payMethod === PayMethodsEnum.WECHAT
										? 'wechat'
										: 'alipay'
								}`" />
							<text class="ml-10"
								>{{
									orderInfo.payMethod === PayMethodsEnum.WECHAT
										? "微信支付"
										: "支付宝"
								}}></text
							>
						</view>
					</view>

					<!-- 订单状态为：`已取消`时显示 -->
					<block v-if="orderInfo.status === OrderStatus.CANCELED">
						<view class="order-info-item">
							<view class="left">取消时间</view>
							<view class="right">{{ orderInfo.cancelTime || "--" }}</view>
						</view>

						<view class="order-info-item">
							<view class="left">取消原因</view>
							<view class="right">{{ orderInfo.cancelReason || "--" }}</view>
						</view>
					</block>

					<!-- 订单状态为：`已付款/待接单`时显示 -->
					<block
						v-else-if="
							orderInfo.status === OrderStatus.PENDING_ACCEPTANCE &&
							orderInfo.orderType === 1
						">
						<view class="order-info-item">
							<view class="left">收货人</view>
							<view class="right"
								>{{ orderInfo.consignee || "--" }}-{{
									orderInfo.phone || "--"
								}}</view
							>
						</view>

						<view class="order-info-item">
							<view class="left">收货地址</view>
							<view class="right">{{ orderInfo.address || "--" }}</view>
						</view>
					</block>
				</view>
			</template>
		</up-card>

		<!-- 底部操作 -->
		<view class="bottom-action">
			<view class="btn-wrap dis-flex flex-y-center flex-x-between">
				<view class="left">
					<view
						class="item"
						v-if="showMoreAction"
						@click="onMoreAction">
						<up-icon
							name="more-dot-fill"
							color="#020202"
							size="15" />
						<text class="f-18 mt-10">更多</text>
					</view>
					<view
						class="item"
						@click="onService">
						<up-icon
							name="server-fill"
							color="server-fill"
							size="15" />
						<text class="f-18 mt-10">客服</text>
					</view>
				</view>

				<!-- 订单已超时自动取消时显示 -->
				<view class="right btn-group">
					<up-button
						v-if="canCancel"
						text="取消订单"
						shape="circle"
						size="small"
						color="#f1f1f1"
						:customStyle="{
							width: '170rpx',
							color: 'black',
							'margin-right': '10rpx',
						}"
						@click="() => cancelOrderPopup?.show()" />

					<!-- 待支付 -->
					<up-button
						v-if="orderInfo.status === OrderStatus.PENDING_PAYMENT"
						type="error"
						text="去支付"
						shape="circle"
						size="small"
						color="#ca4545"
						:customStyle="{ width: '170rpx' }"
						@click="onPay" />

					<!-- 待接单可以催促商家接单 -->
					<up-button
						v-if="orderInfo.status === OrderStatus.PENDING_ACCEPTANCE"
						text="快速催单"
						shape="circle"
						size="small"
						type="error"
						color="#ca4545"
						:customStyle="{ width: '170rpx' }"
						@click="onReminderDebounced" />

					<!-- 订单已完成，可以再来一单 -->
					<up-button
						v-if="
							canRepetition || orderInfo.status === OrderStatus.DINE_IN_ORDER
						"
						type="error"
						text="再来一单"
						shape="circle"
						size="small"
						color="#ca4545"
						:customStyle="{ width: '170rpx' }"
						@click="onRepetition" />
				</view>
			</view>
		</view>

		<up-modal
			width="300px"
			:show="showReminder"
			closeOnClickOverlay
			@confirm="() => (showReminder = false)"
			@close="() => (showReminder = false)"
			content="已催促商家尽快接单，请耐心等待。" />

		<cancel-order-popup
			ref="cancelOrderPopup"
			v-model="cancelReason"
			@change="cancelOrder" />

		<!-- 支付成功弹出层 -->
		<pay-success-popup
			v-model="showPaySuccessPopup"
			:order-id="orderInfo.id"
			:total-price="totalPrice" />
	</view>

	<select-payment-method
		ref="selectPaymentMethod"
		@updatePayStatus="onPaymentMethodChange"
		v-model="orderInfo.payMethod" />
</template>

<script lang="ts" setup>
	import * as _ from "lodash"
	import { CSSProperties, ComponentInternalInstance } from "vue"
	import { useShoppingCartStore, useOrderCountdownStore } from "@/store"
	import { OrderSubmitVO } from "@/api/order"
	import { onLoad, onShow } from "@dcloudio/uni-app"
	import OrderAPI, { OrderVO } from "@/api/order"
	import { PayMethodsEnum } from "@/enums/PayEnum"
	import { OrderStatus, orderStatusMap } from "@/enums/OrderEnum"
	import CancelOrderPopup from "@/components/cancel-order-popup/index.vue"
	import * as OrderConstants from "@/common/constant/order"
	import PaySuccessPopup from "@/components/pay-success-popup/index.vue"
	import SelectPaymentMethod from "@/components/select-payment-method/index.vue"

	const cartStore = useShoppingCartStore()
	const countdownStore = useOrderCountdownStore()
	const { proxy } = getCurrentInstance() as ComponentInternalInstance
	const cancelOrderPopup = ref<InstanceType<typeof CancelOrderPopup>>()
	// 订单信息
	const orderInfo = ref<OrderVO>({})
	const showReminder = ref(false)
	const showPaySuccessPopup = ref(false)
	// 是否显示更多菜品
	const showMoreDish = ref(false)
	// 订单取消原因
	const cancelReason = ref("")
	const orderId = ref<OrderSubmitVO["id"]>("")
	const totalPrice = ref("")

	const selectPaymentMethod = ref<InstanceType<typeof SelectPaymentMethod>>()

	// 是否显示更多
	const showMoreAction = computed(() => {
		return orderInfo.value.status && orderInfo.value.status >= 5
	})
	// 是否能取消订单，只有处于`待付款(1)`和`待接单(2)`状态的订单才能取消
	const canCancel = computed(() => {
		return (
			orderInfo.value.status === OrderStatus.PENDING_PAYMENT ||
			orderInfo.value.status === OrderStatus.PENDING_ACCEPTANCE
		)
	})
	// 是否能再来一单，处于已取消（6）、已完成（5）的订单才能再来一单
	const canRepetition = computed(() => {
		return (
			orderInfo.value.status === OrderStatus.COMPLETED ||
			orderInfo.value.status === OrderStatus.CANCELED ||
			orderInfo.value.status === OrderStatus.ACCEPTED
		)
	})
	// 根据订单状态返回标题
	const navTitle = computed(
		() => orderStatusMap.find(v => v.value === orderInfo.value.status)?.name
	)

	// 剩余秒数
	const remainingSeconds = computed(() => {
		return countdownStore.getRemainingSeconds(orderInfo.value.id)
	})

	// 格式化时间为MM:SS
	const formattedTime = computed(() =>
		countdownStore.getFormattedTime(orderInfo.value.id)
	)

	const orderDishStyle = computed<CSSProperties>(() => {
		const marginBottom = 15 // 15rpx
		const itemHeight = 100 // 100rpx
		const length = orderInfo.value.orderDetailList?.length || 0
		if (length === 0 || length <= 3 || showMoreDish.value) {
			return {
				height: "auto",
				overflow: "auto",
			}
		}
		return {
			height: `${itemHeight * 3 + marginBottom * 2}rpx`,
			overflow: "hidden",
		}
	})

	// 选择支付方式，1：微信支付，2：支付宝支付
	const onSelectPay = () => {
		selectPaymentMethod.value?.show()
	}

	const onPaymentMethodChange = (val: PayMethodsEnum) => {
		OrderAPI.changePaymentMethod({
			orderNumber: orderInfo.value.number,
			payMethod: val,
		}).then(res => {
			if (res.code === 200) {
				proxy?.$toast("支付方式修改成功")
				console.log("val:", val)
				orderInfo.payMethod = val
			}
		})
	}

	watch(
		() => formattedTime.value,
		newValue => {
			if (newValue === "00:00" && orderInfo.value.status === 1) {
				//订单已超时
				cancelReason.value = "订单未支付已超时，自动取消"
				console.log("订单已超时")
				cancelOrder()
			}
		}
	)

	onLoad((options: any) => {
		if (_.isEmpty(options) || !options.orderId) {
			proxy?.$toast("订单信息获取失败")
			setTimeout(() => uni.navigateBack({ delta: 1 }), 1500)
			return
		}

		orderId.value = options.orderId
	})

	watch(
		() => orderInfo.value,
		val => {
			if (val.status === OrderStatus.PENDING_PAYMENT) {
				// 每次获取订单详情时，显示都添加订单倒计时
				countdownStore.addOrder(val, () => fakeUpdateOfCancel(val))
			}
		},
		{ deep: true, immediate: true }
	)

	watch(
		showPaySuccessPopup,
		async val => {
			uni.setNavigationBarTitle({ title: "支付成功" })
			if (!val && orderId.value) {
				await getOrderDetail()
			}
		},
		{ immediate: true }
	)

	onShow(async () => {
		await getOrderDetail()
		// 使用订单的实际金额，而不是购物车中的金额
		totalPrice.value = orderInfo.value.amount?.toString() || "0.00"
		uni.setNavigationBarTitle({ title: `${navTitle.value}` })
		if (orderInfo.value.status !== OrderStatus.PENDING_PAYMENT) {
			return
		}
		// 订单状态到这里一定是`待支付`
	})

	// 通过订单id获取订单详情
	async function getOrderDetail() {
		try {
			const response = await OrderAPI.details(orderId.value!)
			orderInfo.value = response.data
			// console.log("订单详情==>", response.data)
		} catch (error) {
			console.error("获取订单详情失败==>", error)
		}
	}

	// 支付订单
	function payOrder(orderNum: string | number) {
		return new Promise<ResponseData<null>>((resolve, reject) => {
			const paymentData = {
				orderNumber: orderNum,
				payMethod: orderInfo.value.payMethod,
			}

			OrderAPI.payment(paymentData, { load: true })
				.then(res => res.code === 200 && resolve(res))
				.catch(err => {
					console.error("支付请求失败==>", err)
					proxy?.$toast("支付异常，请稍后重试")
					reject(err)
				})
		})
	}

	// 取消订单
	async function cancelOrder() {
		console.log("取消订单")

		// 只有处于`待付款(1)`和`待接单(2)`状态的订单才能取消
		if (!canCancel.value) {
			uni.setNavigationBarTitle({ title: "订单状态异常" })
			return proxy?.$toast("订单状态异常")
		}

		try {
			const res = await OrderAPI.cancel({
				id: orderId.value,
				cancelReason: cancelReason.value,
			})

			if (res.code === 200) {
				proxy?.$toast("订单已取消")
				fakeUpdateOfCancel(orderInfo.value)
				// 关闭弹窗
				cancelOrderPopup.value?.closePopup()
			}
		} catch (error) {
			console.error("取消订单失败", error)
		}
	}

	// 取消订单数据假更新
	function fakeUpdateOfCancel(order: OrderVO) {
		order.status = OrderStatus.CANCELED
		order.cancelTime = countdownStore.getCurTime()
		order.cancelReason = countdownStore.ORDER_AUTO_CANCEL_REASON
		uni.setNavigationBarTitle({ title: "已取消" })
	}

	// 去支付
	const onPay = async () => {
		uni.showModal({
			title: "支付订单",
			content: "确定支付该订单吗嘛？",
			showCancel: true,
			async success({ confirm }) {
				if (confirm) {
					// 支付订单【根据订单号支付】
					const res = await payOrder(orderInfo.value.number as string)
					if (res.code !== 200) return

					// 订单支付成功，取消倒计时
					countdownStore.removeOrder(orderInfo.value.id)
					//临时改变订单状态，用于临时显示订单支付成功popup
					orderInfo.value.status = OrderStatus.PENDING_ACCEPTANCE
					// 显示订单支付成功popup（延迟显示）
					setTimeout(() => (showPaySuccessPopup.value = true), 500)
				}
			},
		})
	}

	// 快速催单
	const onReminderDebounced = _.debounce(() => {
		OrderAPI.reminder(orderId.value!).then(res => {
			if (res.code === 200) {
				showReminder.value = true
			}
		})
	}, 1000)

	// 再来一单
	const onRepetition = () => {
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

				OrderAPI.repetition(orderId.value!).then(res => {
					if (res.code === 200) {
						// tabbar 页面只能使用 switchTab(跳转到 tabBar 页面，并关闭其他所有非 tabBar 页面) 跳转，且无法携带参数
						uni.setStorageSync(OrderConstants.IS_REPETITION, true)
						proxy?.$navTo("pages/index/index")
					}
				})
			},
		})
	}

	// 点击更多按钮
	const onMoreAction = () => {
		proxy?.$toast("暂未开放，敬请期待")
	}

	// 客服热线
	const onService = () => {
		uni.navigateTo({
			url: "/pages/message/customer-service",
		})
	}
</script>

<style>
	page {
		background-color: #f5f5f5 !important;
	}
</style>

<style lang="scss">
	:deep(.order-payment) {
		.u-card {
			.u-card__body {
				padding: 40rpx !important;
			}

			.u-card__foot:first-child {
				padding: 40rpx !important;
			}

			.u-card__head {
				padding: 0 0 30rpx 0 !important;
			}
		}

		.u-notice {
			text {
				font-size: 22rpx !important;
			}
		}

		// 子组件内部样式穿透
		.cancel-order-popup {
			.other-reason {
				.u-textarea {
					padding: 30rpx !important;
					background-color: #f8f8f8 !important;
				}

				.u-textarea__count {
					padding: 0 18rpx 18rpx 0 !important;
					background-color: #f8f8f8 !important;
				}
			}
		}
	}
</style>

<style lang="scss" scoped>
	$offset-bottom: 140rpx;
	@mixin flex-y-center {
		display: flex;
		align-items: center;
	}
	.price {
		color: #000;
		font-family: fantasy;
		padding-right: 20rpx;
	}

	.order-payment {
		padding-bottom: $offset-bottom + 20rpx;
	}

	.countdown {
		margin: 30rpx 0;
		.time {
			color: #ca4545;
			font-weight: bold;
			font-size: 45rpx;
			font-family: fantasy;
		}
	}

	.pickup-info {
		display: flex;
		align-items: center;
		justify-content: center;
		flex-direction: column;

		.pickuo-code-text {
			margin-bottom: 10rpx;
			color: #494949;
			font-size: 22rpx;
		}

		.pickup-number {
			font-weight: bold;
			font-size: 50rpx;
			color: #ca4545;
		}
	}

	.notice {
		border-radius: 8px 8px 0 0;
		overflow: hidden;
	}

	.card-header {
		padding: 0 30rpx;
		.shop-name {
			max-width: 80%;
			font-size: 28rpx;
		}
	}

	.order-dish {
		&-item {
			height: 100rpx;
		}
	}

	.card-footer {
		@include flex-y-center;
		justify-content: space-between;

		.count {
			font-family: fantasy;
			color: #ca4545;
			font-weight: bold;
			font-size: 26rpx;
			margin: 0 10rpx;
		}

		view {
			color: #000;
		}
	}

	.order-info {
		&-item {
			@include flex-y-center;
			justify-content: space-between;

			.left {
				font-size: 25rpx;
				color: #000;
			}

			.right {
				font-size: 22rpx;
				color: #aaaaaa;
			}

			&:not(:last-child) {
				margin-bottom: 40rpx;
			}
		}
	}

	.bottom-action {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		height: $offset-bottom;
		padding-bottom: 30rpx;
		background-color: #fff;
		box-shadow: 0 -4px 8px rgba(0, 0, 0, 0.1);
		transition: all 0.3s ease-in-out;
		@include flex-y-center;

		.btn-wrap {
			width: 100%;
			padding: 0 40rpx;

			.left {
				@include flex-y-center;

				.item {
					padding: 10rpx;
					@include flex-y-center;
					justify-content: center;
					flex-direction: column;

					&:first-child {
						margin-right: 30rpx;
					}
				}
			}

			.right {
				@include flex-y-center;
			}
		}
	}
</style>
