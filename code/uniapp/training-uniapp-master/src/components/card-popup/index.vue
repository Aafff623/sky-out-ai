<template>
	<!-- 购物车弹出层 -->
	<view class="cart-popup">
		<up-popup
			:show="props.showCartPopup"
			mode="bottom"
			@close="close"
			@open="open"
			:customStyle="{
				'min-height': '400rpx',
				padding: '30rpx 30rpx 80px 30rpx',
			}"
			z-index="10075">
			<view class="cart-wrap h-full dis-flex flex-dir-column">
				<view class="cart-header dis-flex flex-y-center flex-x-between mb-30">
					<view class="f-w">🛒已加购物车商品</view>
					<view
						class="cart-clean dis-flex flex-y-center"
						@click="handleCleanCart">
						<up-icon
							name="trash-fill"
							color="#818181"
							size="15"></up-icon>
						<text
							class="f-22 mr-10"
							style="color: #818181"
							>清空</text
						>
					</view>
				</view>

				<!-- 购物车商品列表，80rpx为cart-header的height+margin-bottom -->
				<scroll-view
					:scroll-y="true"
					style="
						height: calc(100vh - 200rpx);
						max-height: 500rpx;
						flex-grow: 1;
					">
					<view
						class="cart-dish-item mb-20"
						v-for="(cd, di) in cartList"
						:key="di">
						<view class="cart-dish-image">
							<up-image
								show-loading
								:src="cd.image"
								width="85px"
								height="85px"
								:radius="5" />
						</view>

						<view class="cart-dish-info w-full">
							<view class="dis-flex flex-dir-column">
								<view class="dish-name">{{ cd.name }}</view>
								<view
									v-show="cd.dishFlavor"
									class="dish-desc">
									{{ cd.dishFlavor }}
								</view>
							</view>

							<view
								class="dish-price-number dis-flex flex-y-center flex-x-between">
								<view class="dish-price">
									<text class="f-24 f-w">￥</text>
									<text class="f-40 f-w">{{ cd.amount }}</text>
								</view>

								<view
									class="number-box"
									style="transform: scale(0.8)">
									<up-number-box
										v-model="cd.number"
										:buttonSize="25"
										:min="0"
										:max="100"
										@change="onChangeStepper($event, cd)" />
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
			<!-- 去下单 -->
			<view class="checkout-wrap">
				<up-col
					span="8"
					offset="2"
					justify="end">
					<up-button
						type="error"
						text="去下单"
						color="#ca4545"
						shape="circle"
						:disabled="totalNum <= 0"
						@click="onOrdersSubmit"
						:custom-style="{
							'max-width': '110px',
							'margin-top': '20rpx',
						}" />
				</up-col>
			</view>
		</up-popup>
	</view>
</template>

<script setup lang="ts">
	import { ShoppingCart } from "@/api/shopping-cart"
	import { useShoppingCartStore } from "@/store"
	import { type DishVO } from "@/api/dish"
	import { onShow } from "@dcloudio/uni-app"
	const props = defineProps<{
		showCartPopup: boolean
	}>()
	const emit = defineEmits(["onClose"])
	//购物车store
	const shoppingCartStore = useShoppingCartStore()
	const { totalNum } = storeToRefs(shoppingCartStore)
	//购物车列表
	const cartList = ref<ShoppingCart[]>([])
	// 获取当前页面实例
	const instance = getCurrentInstance()
	const proxy = instance?.proxy
	//菜品列表
	const dishList = ref<DishVO[]>([])
	//关闭购物车弹窗
	const close = () => {
		// props.showCartPopup = false
		// 重新加载购物车数据
		getCartList()
		emit("onClose")
	}
	onShow(() => {
		getCartList()
	})

	const open = () => {
		// showCartPopup.value = true
		const len = cartList.value.length
		const data = uni.getStorageSync("cart") as ShoppingCart[]
		// 使用缓存数据
		if (data.length !== 0) {
			cartList.value = data
			return
		}

		if (len === 0) {
			// 重新加载购物车数据
			getCartList()
		}
	}

	async function getCartList() {
		return shoppingCartStore.getCartlist().then(res => {
			cartList.value = res.data || []
			const ids = res.data.map(v => v.dishId || v.setmealId)
			// 更新菜品列表（菜品数量）
			updateDishNumber(ids as number[])
			return cartList.value // 返回购物车列表
		})
	}

	// 使用本地存储数据更新菜品列表中选择的菜品数量
	function updateDishNumber(ids?: number | number[]) {
		const c = (uni.getStorageSync("cart") || []) as ShoppingCart[]

		// 无本地存储，证明数据购物车中没有数据
		if (c.length === 0) {
			dishList.value.forEach(dish => (dish.number = 0))
			return
		}

		// 如果传入的是单个菜品的 ID
		if (typeof ids === "number") {
			const cartItem = c.find(v => v.dishId === ids || v.setmealId === ids)
			const index = dishList.value.findIndex(v => v.id === ids)
			// 增加边界检查
			if (index !== -1 && dishList.value[index]) {
				dishList.value[index].number = cartItem?.number ?? 0
			}
			return
		}

		if (Array.isArray(ids)) {
			// 如果传入的是多个菜品的 ID
			for (const id of ids) {
				const cartItem = c.find(v => v.dishId === id || v.setmealId === id)
				const index = dishList.value.findIndex(v => v.id === id)
				if (index !== -1 && dishList.value[index]) {
					dishList.value[index].number = cartItem?.number ?? 0
				}
			}
		} else {
			// 如果没有传入参数，将所有菜品数量重置为 0
			for (const dish of dishList.value) {
				dish.number = 0
			}
		}
	}

	// 清空购物车
	const handleCleanCart = () => {
		uni.showModal({
			title: "友情提示",
			content: "确定要删除该清空购物车吗？",
			showCancel: true,
			success({ confirm }) {
				// 确认删除
				confirm && onClearCart()
			},
		})
	}

	function onClearCart() {
		shoppingCartStore.cleanCart().then(() => {
			// 刷新菜品列表
			// getDishByCateId()
			dishList.value = []
			// 关闭购物车弹出层
			// showCartPopup.value = false
			emit("onClose")
		})
	}

	// 购物车弹出层商品增加或者减少，发生变化
	const onChangeStepper = (
		{ value }: { value: number },
		item: ShoppingCart
	) => {
		shoppingCartStore
			.updateShoppingCartNumber({
				...item,
				number: value,
			})
			.then(() => {
				const itemKey = item.dishId ? "dishId" : "setmealId"
				// 更新菜品列表数据
				updateDishNumber(item[itemKey] as number)
				if (value === 0) {
					cartList.value = cartList.value.filter(v => v.number! > 0)
					// 更新购物车列表数据
					getCartList()
				}
			})
	}

	// 创建订单，订单确认
	async function onOrdersSubmit() {
		proxy?.$navTo("pages/order/index")
	}
</script>

<style lang="scss" scoped>
	// 购物车弹出层
	.cart-header {
		flex: 1;
		max-height: 50rpx;
		height: auto;
		.f-w {
			font-weight: bold;
		}
	}

	.cart-dish-item {
		display: flex;
		max-height: 85px;

		.cart-dish-image {
			margin-right: 25rpx;
		}

		.cart-dish-info {
			display: flex;
			flex-direction: column;
			justify-content: space-between;

			.dish-name {
				font-weight: bold;
				font-size: 28rpx;
				margin-bottom: 10rpx;
			}

			.dish-desc {
				font-size: 20rpx;
				color: #ababab;
			}
		}
	}
</style>
