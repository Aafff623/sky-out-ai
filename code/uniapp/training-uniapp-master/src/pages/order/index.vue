<template>
	<view class="order-submit">
		<!-- 选择就餐方式，1：外卖，2：堂食 -->
		<up-card
			:border="false"
			:foot-border-top="false"
			:show-head="false"
			:body-style="{ padding: '40rpx' }"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)">
			<template #body>
				<view class="order-way">
					<view class="select-way">
						<view class="shop-name twoline-hide"
							>商家地址：{{ getShopRecord?.address }}</view
						>

						<view class="subsection">
							<up-subsection
								:list="['外卖', '堂食']"
								:current="order.orderType === 1 ? 0 : 1"
								mode="button"
								activeColor="#fff"
								inactiveColor="#9b9b9b"
								bgColor="#f5f5f5"
								@change="onOrderTypeChange" />
						</view>
					</view>

					<view
						class="cell-list"
						v-if="order.orderType === 2">
						<view class="cell-item">
							<view class="f-25 f-w c-000">预留电话</view>
							<view class="input">
								<input
									:value="reservedPhone"
									:maxlength="30"
									placeholder="请输入预留电话"
									style="text-align: right" />
							</view>
						</view>
					</view>
				</view>
			</template>
		</up-card>

		<!-- 收货地址 -->
		<up-card
			v-if="order.orderType === 1"
			:border="false"
			:foot-border-top="false"
			:show-head="false"
			:body-style="{ padding: '15px 0 0 0 !important' }"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)">
			<template #body>
				<!-- 选择收货地址 -->
				<view
					class="address dis-flex flex-x-between flex-y-center mb-20"
					@click="onSelectAddress">
					<!-- 存在默认地址 -->
					<block v-if="defaultAddress && !_.isEmpty(defaultAddress)">
						<view class="consignee dis-flex flex-dir-column">
							<view class="detail mb-10 dis-flex flex-y-center">
								<up-tag
									v-if="defaultAddress?.label"
									size="mini"
									type="error"
									color="#ca4545"
									:text="defaultAddress?.label"
									bgColor="#ffeceb"
									borderColor="transparent" />
								<text class="f-35 f-w ml-10">{{ defaultAddress?.detail }}</text>
							</view>

							<view class="dis-flex flex-y-center">
								<text class="f-22">{{ defaultAddress?.consignee }}</text>
								<text class="f-22 mr-10">{{
									defaultAddress?.sex === SexEnum.MALE ? "先生" : "女士"
								}}</text>
								<text class="f-22">{{ defaultAddress?.phone }}</text>
							</view>
						</view>
					</block>

					<block v-else>
						<view class="no-address">请选择您的收货地址</view>
					</block>

					<up-icon
						name="arrow-right"
						color="#444444"
						size="12" />
				</view>

				<view class="delivery-method mt-30">
					<delivery-options v-model="order.deliveryStatus" />
				</view>

				<view class="notice mt-30">
					<up-notice-bar
						color="#a5755a"
						bgColor="#fbfbfb"
						direction="column"
						:text="noticeText"
						style="border-radius: 0 0 10px 10px" />
				</view>
			</template>
		</up-card>

		<!-- 菜品列表card -->
		<up-card
			:border="false"
			:foot-border-top="false"
			:show-head="false"
			body-style="{ padding: '40rpx' }"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)">
			<template #body>
				<view class="dis-flex flex-y-center flex-x-between mb-20">
					<view
						class="f-22 oneline-hide"
						style="max-width: 70%"
						>{{ userStore.getShopRecord.name }}</view
					>
					<up-tag
						plain
						text="商家自配"
						size="mini"
						type="info"
						color="#b5b5b5"
						borderColor="#b5b5b5"></up-tag>
				</view>
				<up-read-more
					ref="uReadMoreRef"
					textIndent="0"
					:closeText="`展开(共${cartList.length || 0}件)`"
					openText="收起列表"
					color="#8c8c8c"
					toggle
					:font-size="13"
					showHeight="100">
					<scroll-view
						:scroll-y="true"
						:style="{ height: dishListHeight + 'rpx' }">
						<view
							class="dish-item dis-flex mb-15"
							v-for="(d, di) in cartList"
							:key="di"
							style="max-height: 50px">
							<view class="dish-image mr-20">
								<up-image
									show-loading
									:src="d.image"
									width="50px"
									height="50px"
									:radius="5" />
							</view>

							<view class="dish-info w-full dis-flex flex-x-between">
								<view class="dis-flex flex-dir-column">
									<text class="c-000 f-w f-25">{{ d.name }}</text>
									<text
										v-show="d.dishFlavor"
										class="f-20 mt-10"
										style="color: #8c8c8c"
										>{{ d.dishFlavor }}</text
									>
									<text
										class="f-20 mt-10"
										style="color: #8c8c8c"
										>x{{ d.number }}</text
									>
								</view>

								<view class="price">
									<text class="f-20">￥</text>
									<text class="f-30">{{
										Number(d.amount) * Number(d.number) || 0
									}}</text>
								</view>
							</view>
						</view>
					</scroll-view>
				</up-read-more>

				<!-- 其他费用 -->
				<view class="other-cost mt-40">
					<view
						class="cost-item"
						v-if="order.orderType === 1">
						<view class="f-25 f-w c-000">打包费</view>
						<view class="price">
							<text class="f-23">￥</text>
							<text class="f-33">{{ shopInfo.balingCharges }}</text>
						</view>
					</view>

					<view
						class="cost-item"
						v-if="order.orderType === 1">
						<view class="f-25 f-w c-000">用户配送费</view>
						<view class="price">
							<text class="f-23">￥</text>
							<text class="f-33">{{ shopInfo.deliveryCharges }}</text>
						</view>
					</view>

					<view class="cost-item">
						<view class="f-25 f-w c-000">发票凭据</view>
						<view class="price">
							<text class="f-22">请联系商家提供</text>
						</view>
					</view>
				</view>

				<up-divider class="mt-30 mb-40"></up-divider>

				<view class="total-price dis-flex flex-y-center flex-x-between">
					<view class="f-32 c-000 f-w">总计费用</view>

					<view
						class="price"
						style="color: #ca4545">
						<text class="f-23 f-w">￥</text>
						<up-count-to
							bold
							:startVal="0"
							:endVal="
								order.orderType === 1
									? totalPrice +
									  shopInfo.deliveryCharges +
									  shopInfo.balingCharges
									: totalPrice
							"
							color="#ca4545"
							font-size="25"
							:decimals="2"
							:duration="1000" />
					</view>
				</view>
			</template>
		</up-card>

		<!-- 支付方式 -->
		<up-card
			:border="false"
			:foot-border-top="false"
			:show-head="false"
			body-style="{ padding: '40rpx' }"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)"
			@body-click="onSelectPay">
			<template #body>
				<view class="pay-method dis-flex flex-y-center flex-x-between">
					<view class="pay-text f-26 f-w c-000">支付方式</view>
					<view class="pay-icon dis-flex flex-y-center">
						<text
							class="iconfont f-40"
							:class="`icon-${
								order.payMethod === PayMethodsEnum.WECHAT ? 'wechat' : 'alipay'
							}`" />
						<text class="f-22 ml-10">{{
							order.payMethod === PayMethodsEnum.WECHAT ? "微信支付" : "支付宝"
						}}</text>
						<up-icon
							name="arrow-right"
							color="#444444"
							size="12" />
					</view>
				</view>
			</template>
		</up-card>

		<!-- 订单备注 -->
		<up-card
			:border="false"
			disableTouch
			:foot-border-top="false"
			:show-head="false"
			box-shadow="0px 0px 12px rgba(0, 0, 0, 0.12)">
			<template #body>
				<view
					class="remark dis-flex flex-y-center flex-x-between"
					@click="onRemark">
					<view class="f-26 f-w c-000">订单备注</view>
					<view
						class="dis-flex flex-y-center"
						style="max-width: 50%">
						<text
							class="f-25 mr-10 oneline-hide"
							style="color: #c5c5c5"
							>{{ order.remark || "请填写你的要求" }}</text
						>
						<up-icon
							name="arrow-right"
							color="#c5c5c5"
							size="12" />
					</view>
				</view>

				<view
					class="table-ware dis-flex flex-y-center flex-x-between"
					@click="onTableware">
					<view class="f-26 f-w c-000">餐具</view>
					<view class="dis-flex flex-y-center">
						<text
							class="f-25 mr-10"
							style="color: #ca4545"
							>{{ tablewareText }}</text
						>
						<up-icon
							name="arrow-right"
							color="#c5c5c5"
							size="12" />
					</view>
				</view>
			</template>
		</up-card>

		<!-- 创建订单按钮 -->
		<view class="to-pay">
			<view
				class="pay-btn"
				@click="onPay">
				<text class="iconfont icon-pay-by-wechat f-40" />
				<view class="f-25 f-w ml-10">创建订单</view>
				<text
					class="f-30"
					style="font-family: fantasy"
					>￥{{
						order.orderType === 1 ? cartStore.exactTotalPrice : totalPrice
					}}</text
				>
			</view>
		</view>

		<select-table-ware
			ref="selectTableWare"
			v-model="order.tablewareStatus"
			:default-value="selectTableWareData"
			:default-address="defaultAddress"
			@select-compelete="onSelectTableWareCompelete"
			@update-cutlery-quantity="updateCutleryQuantity" />

		<select-payment-method
			ref="selectPaymentMethod"
			v-model="order.payMethod" />

		<order-remark-input
			ref="orderRemarkInput"
			v-model="order.remark" />
	</view>
</template>

<script setup lang="ts">
	import * as _ from "lodash"
	import { shopInfo } from "@/settings"
	import { onShow } from "@dcloudio/uni-app"
	import { useShoppingCartStore, useShopStore, useUserStore } from "@/store"
	import { ShoppingCart } from "@/api/shopping-cart"
	import OrderAPI, { OrderSubmitVO, OrdersSubmitDTO } from "@/api/order"
	import AddressApi, { AddressBook } from "@/api/address"
	import { SexEnum } from "@/enums/AddressEnum"
	import { DeliveryStatusEnum, TablewareStatusEnum } from "@/enums/OrderEnum"
	import { PayMethodsEnum } from "@/enums/PayEnum"
	import { ComponentInternalInstance } from "vue"
	import { getEstimatedDeliveryTime } from "@/utils"
	import DeliveryOptions from "@/components/delivery-options/index.vue"
	import * as OrderConstant from "@/common/constant/order"
	import SelectTableWare, {
		type DefaultValue,
	} from "@/components/select-tableware/index.vue"
	import SelectPaymentMethod from "@/components/select-payment-method/index.vue"
	import OrderRemarkInput from "@/components/order-remark-input/index.vue"

	const { proxy } = getCurrentInstance() as ComponentInternalInstance
	const userStore = useUserStore()
	const { getShopRecord } = storeToRefs(userStore)
	const shopStore = useShopStore()
	const cartStore = useShoppingCartStore()
	const uReadMoreRef = ref(null)
	const selectTableWare = ref<InstanceType<typeof SelectTableWare>>()
	const selectPaymentMethod = ref<InstanceType<typeof SelectPaymentMethod>>()
	const orderRemarkInput = ref<InstanceType<typeof OrderRemarkInput>>()
	const { startTime, isOperating, isInOperatingTime } = storeToRefs(shopStore)
	const { totalPrice } = storeToRefs(cartStore)
	const cartList = ref<ShoppingCart[]>([])
	const orderInfo = reactive<OrderSubmitVO>({})
	const order = reactive<OrdersSubmitDTO>({
		addressBookId: undefined,
		payMethod: PayMethodsEnum.WECHAT,
		remark: "",
		estimatedDeliveryTime: getEstimatedDeliveryTime(),
		deliveryStatus: DeliveryStatusEnum.IMMEDIATELY, // 配送状态 1立即送出 0选择具体时间
		tablewareNumber: 1,
		tablewareStatus: TablewareStatusEnum.BY_SIZE,
		packAmount: OrderConstant.PACK_AMOUNT,
		amount: (
			totalPrice.value +
			shopInfo.balingCharges +
			shopInfo.deliveryCharges
		).toString(),
		orderType: 1,
	})
	const reservedPhone = ref("")
	const defaultAddress = ref<AddressBook>()
	// 餐具选择器数据
	const selectTableWareData = reactive<DefaultValue>({
		// 是否需要餐具
		is_need: undefined,
		// 餐具数量
		cutlery_num: undefined,
		// 餐具数量状态 1按餐量提供 0选择具体数量
		tableware_status: order.tablewareStatus,
	})
	onMounted(() => {
		console.log("地址：", getShopRecord.value.address)
	})

	const tablewareText = computed(() => {
		const { is_need, cutlery_num, tableware_status } = selectTableWareData
		if (is_need === undefined && cutlery_num === undefined) {
			return "请选择"
		}

		if (!is_need && is_need !== undefined) {
			return "已选无需餐具"
		}

		if (tableware_status === TablewareStatusEnum.BY_SIZE) {
			return "已选按量提供餐具"
		}

		if (tableware_status === TablewareStatusEnum.BY_NUMBER && cutlery_num) {
			return `已选【${cutlery_num}】份餐具`
		}
	})

	const dishListHeight = computed(() => {
		const maxCount = 4 // 最多显示的元素数量
		const oneHeight = 115 // 一个元素的高度（rpx）
		const len = cartList.value.length || 1
		if (len >= maxCount) {
			return (maxCount - 1) * oneHeight
		}
		return len * oneHeight
	})

	const noticeText = computed(() => {
		// 营业中并且在营业时间内
		if (isOperating.value && isInOperatingTime.value) {
			return [
				"由商家自行配送，实际送达时间可联系商家确认",
				"点餐请适量，环保又健康",
			]
		} else {
			// 打样中
			return [
				`当前商家已打样，商家 ${startTime.value} 开始营业，支持预约配送`,
				"点餐请适量，环保又健康",
			]
		}
	})

	onMounted(async () => {
		// 等待 DOM 更新
		await nextTick()

		// 调用子组件的 init 方法
		if (uReadMoreRef.value) {
			;(uReadMoreRef.value as any).init()
		}
	})

	onShow(() => {
		proxy?.$isLogin().then(() => {
			// 查询默认收货地址
			getDefaultAddress()
			// 同步购物车数据
			cartStore.getCartStorage().then(list => {
				cartList.value = list
			})
		})
	})

	// 查询默认收货地址
	function getDefaultAddress() {
		AddressApi.getDefault().then(res => {
			defaultAddress.value = res.data
			reservedPhone.value = res.data?.phone || ""
			order.addressBookId = res.data?.id
		})
	}

	const onOrderTypeChange = (index: number) => {
		const newType = index === 0 ? 1 : 2
		order.orderType = newType

		// 重置地址相关信息
		if (newType === 2) {
			defaultAddress.value = undefined
			order.addressBookId = undefined
		} else {
			getDefaultAddress()
		}

		// 更新总价计算
		updateTotalPrice()
	}

	const updateTotalPrice = () => {
		const basePrice = totalPrice.value
		const additional =
			order.orderType === 1
				? shopInfo.balingCharges + shopInfo.deliveryCharges
				: 0

		order.amount = (basePrice + additional).toFixed(2)
		cartStore.updateTotalPrice(basePrice)
	}

	// 创建订单并支付
	const onPay = () => {
		// 没有选择餐具时打开餐具选择器
		if (!checkTablewareData()) {
			return selectTableWare.value?.show()
		}
		uni.showModal({
			title: "友情提示",
			content: "确定要下单嘛？",
			showCancel: true,
			async success({ confirm }) {
				if (!confirm) return
				try {
					// 创建订单
					const o = await createOrder()
					if (o.code !== 200) {
						proxy?.$toast("创建订单失败")
						return
					}

					proxy?.$toast("订单创建成功")
					// 创建订单成功，跳转到订单支付页面
					setTimeout(() => {
						proxy?.$navTo(
							"pages/order/order-detail",
							{
								orderId: o.data?.id,
							},
							"redirectTo"
						)
					}, 1000)
					// 等待页面跳转完成，同步本地购物车存储，创建订单成功之后清空购物车
					setTimeout(() => cartStore.cleanCart(), 1500)
				} catch (error) {
					console.error("订单处理出错==>", error)
					uni.showToast({
						title: "订单处理失败，请稍后重试",
						icon: "none",
					})
				}
			},
		})
	}

	// 必须选择餐具
	function checkTablewareData() {
		if (_.isEmpty(selectTableWareData)) {
			return false
		}

		// 必须有选择
		if (selectTableWareData.is_need === undefined) {
			return false
		}

		return true
	}

	// 创建订单
	function createOrder() {
		const submitData = _.cloneDeep(order)

		// 堂食订单处理
		if (submitData.orderType === 2) {
			submitData.addressBookId = undefined
			submitData.packAmount = 0
			submitData.deliveryStatus = undefined
		}

		return new Promise<ResponseData<OrderSubmitVO>>((resolve, reject) => {
			OrderAPI.submit(submitData)
				.then(res => {
					if (res.code === 200) {
						Object.assign(orderInfo, res.data)
						resolve(res)
					}
				})
				.catch(err => {
					console.error("订单创建错误==>", err)
					uni.showToast({
						title: "订单创建异常，请稍后重试",
						icon: "none",
					})
					reject(err)
				})
		})
	}

	// 选择支付方式，1：微信支付，2：支付宝支付
	const onSelectPay = () => {
		selectPaymentMethod.value?.show()
	}

	// 填写订单备注
	const onRemark = () => {
		orderRemarkInput.value?.show()
	}

	// 选择餐具
	const onTableware = () => {
		selectTableWare.value?.show(selectTableWareData)
	}

	const onSelectAddress = () => {
		proxy?.$navTo("pages/address/index", { form: "order" })
	}

	// 选择餐具完毕回调函数
	function onSelectTableWareCompelete(data: DefaultValue) {
		Object.assign(selectTableWareData, data)
	}

	// 用户选择`具体的餐具数量`回调函数
	function updateCutleryQuantity(quantity: number | string) {
		// 用户选择了无需餐具
		if (quantity === "no_cutlery") {
			order.tablewareStatus = TablewareStatusEnum.BY_NUMBER
			order.tablewareNumber = 0
			return
		}

		// 按量提供
		if (quantity === "") {
			order.tablewareStatus = TablewareStatusEnum.BY_SIZE
			order.tablewareNumber = 1
			return
		}

		// 选择具体餐具数量
		order.tablewareNumber = quantity as number
	}
</script>

<style>
	page {
		background-color: #f5f6f8 !important;
	}
</style>

<!-- 注意：微信小程序穿透组件样式时需要使用 !important 提高权重才会生效 -->
<style lang="scss">
	:deep(.order-submit) {
		.u-subsection--button__bar {
			background-color: #ca4545 !important;
		}

		// .u-card {
		// .u-card__body {
		//   padding: 40rpx !important;
		// }

		// &:nth-child(2) {
		//   .u-card__body {
		//     padding: 15px 0 0 0 !important;
		//   }
		// }
		// }

		.u-notice {
			text {
				font-size: 22rpx !important;
			}
		}
		.u-tag--mini {
			height: 28rpx !important;
			line-height: 28rpx !important;
			.u-tag__text {
				font-size: 22rpx !important;
			}
		}

		.u-number-box {
			width: 100% !important;
			justify-content: space-between !important;
		}

		// 在单组件内样式穿透效果不起效果，只能在父组件外部使用才生效
		.remark-textarea {
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
</style>

<style lang="scss" scoped>
	.order-submit {
		padding-bottom: 150rpx;
	}

	.c-000 {
		color: black;
	}

	.price {
		// color: #ca4545;
		color: #000;
		font-family: fantasy;
		padding-right: 20rpx;
	}

	.order-way {
		.select-way {
			display: flex;
			align-items: center;
			justify-content: space-between;
			flex-wrap: nowrap;

			.shop-name {
				flex: 1;
				font-weight: bold;
				font-size: 25rpx;
				color: black;
			}

			.subsection {
				width: 160rpx;
			}
		}
	}

	.address,
	.booking {
		padding: 0 40rpx 0 40rpx;
	}

	.delivery-method {
		padding: 0 30rpx 0 30rpx;
	}

	.address {
		.consignee {
			text {
				color: #8c8c8c;
			}
		}

		.detail text {
			color: black;
		}

		.no-address {
			color: #ca4545;
			font-size: 35rpx;
			font-weight: bold;
		}
	}

	.notice {
		border-radius: 0 0 8px 8px;
		overflow: hidden;
	}

	.other-cost,
	.cell-list {
		.cost-item,
		.cell-item {
			display: flex;
			align-items: center;
			justify-content: space-between;
			margin-top: 20rpx;

			.input {
				color: #8c8b8b;
			}
		}
	}

	.remark,
	.table-ware {
		height: 60rpx;
	}

	.to-pay {
		position: fixed;
		bottom: 50rpx;
		left: 50%;
		width: 80%;
		transform: translateX(-50%);

		.pay-btn {
			width: 100%;
			margin-bottom: 20rpx;
			width: 100%;
			height: 86rpx;
			background: linear-gradient(270deg, #ca4545 0%, #ca4545 100%);
			color: #fff;
			font-size: 26rpx;
			border-radius: 80rpx;
			letter-spacing: 5rpx;
			display: flex;
			justify-content: center;
			align-items: center;
		}
	}
</style>
