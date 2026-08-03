<template>
	<view class="dish-flavor-model">
		<up-modal
			:show="visible"
			:title="title"
			:negativeTop="100"
			:showConfirmButton="false"
			:showCancelButton="false"
			closeOnClickOverlay>
			<template #default>
				<view class="dish-flavor-model-content">
					<view
						class="dish-flavors"
						v-for="flavor in dish?.flavors || []"
						:key="flavor.id">
						<view class="dish-flavor-name">{{
							flavor?.name || "菜品口味名"
						}}</view>

						<!-- 菜品口味值 -->
						<view class="dish-flavor-item">
							<template
								v-for="item in JSON.parse(flavor?.value as string || '[]') as string[] || []"
								:key="item">
								<up-tag
									:text="item"
									:plain="!isSelected(flavor, item)"
									:name="item"
									:color="!isSelected(flavor, item) ? '#232323' : '#ffffff'"
									:bgColor="!isSelected(flavor, item) ? '#ffffff' : '#ca4545'"
									:borderColor="
										!isSelected(flavor, item) ? '#a5a5a5' : '#ca4545'
									"
									@click="selectDishFlavor(flavor, item)" />
							</template>
						</view>
					</view>

					<view class="dish-flavor-bottom">
						<!-- 价格 -->
						<view class="dish-price">
							<text class="f-25 f-w">￥</text>
							<text class="f-45 f-w">
								{{ precisePrice(dish?.price!, currentQuantity || 1) }}
							</text>
						</view>

						<!-- 加入购物车按钮 -->
						<view class="dish-flavor-add-cart">
							<template v-if="isInCart">
								<up-number-box
									:modelValue="currentQuantity"
									:min="0"
									:max="100"
									integer
									@change="handleQuantityChange">
									<template #minus>
										<up-icon
											name="minus-circle"
											color="#ca4545"
											size="20" />
									</template>
									<template #input>
										<text
											class="dish-number"
											style="width: 25px; text-align: center"
											>{{ currentQuantity }}</text
										>
									</template>
									<template #plus>
										<up-icon
											name="plus-circle-fill"
											color="#ca4545"
											size="20" />
									</template>
								</up-number-box>
							</template>

							<up-button
								v-else
								type="primary"
								text="加入购物车"
								size="mini"
								shape="circle"
								color="#ca4545"
								@click="handleAddToCart" />
						</view>

						<!-- 关闭按钮 -->
						<view
							class="dish-flavor-close"
							@click="onClose">
							<up-icon
								name="close"
								size="22"
								color="#000" />
						</view>
					</view>
				</view>
			</template>
		</up-modal>
	</view>
</template>

<script lang="ts" setup>
	import { useShoppingCartStore } from "@/store/modules/shopping-cart"
	import { type DishVO, type DishFlavor } from "@/api/dish"
	import { type ShoppingCartDTO } from "@/api/shopping-cart"

	defineProps({
		title: {
			type: String,
			default: "选择菜品口味",
		},
	})

	const cartStore = useShoppingCartStore()
	const visible = ref(false)
	const dish = ref<DishVO | null>(null)
	const selected = ref<DishFlavor[]>([])
	// 当前选中的口味
	const currentFlavor = computed(() => generateFlavorString(selected.value))
	// 判断是否在购物车中
	const isInCart = computed(() => {
		return cartStore
			.getList()
			.some(
				item =>
					item.dishId === dish.value?.id &&
					item.dishFlavor === currentFlavor.value
			)
	})

	// 获取当前数量
	const currentQuantity = computed(() => {
		const item = cartStore
			.getList()
			.find(
				item =>
					item.dishId === dish.value?.id &&
					item.dishFlavor === currentFlavor.value
			)
		return item?.number || 0
	})

	// 加入购物车
	const handleAddToCart = async () => {
		if (!dish.value) return

		const params: ShoppingCartDTO = {
			name: dish.value.name,
			image: dish.value.image,
			amount: dish.value.price,
			dishId: dish.value.id!,
			number: 1,
			dishFlavor: currentFlavor.value,
		}

		// console.log("加入购物车", params);
		await cartStore.addCart(params)
		//关闭弹窗
		visible.value = false
	}

	// 数量变更
	const handleQuantityChange = async ({ value }: { value: number }) => {
		if (!dish.value) return

		await cartStore.updateShoppingCartNumber({
			name: dish.value.name,
			image: dish.value.image,
			amount: dish.value.price,
			dishId: dish.value.id!,
			number: value,
			dishFlavor: currentFlavor.value,
		})
	}

	// 判断是否已选中
	const isSelected = (flavor: DishFlavor, item: string) => {
		const selectedFlavor = selected.value.find(f => f.id === flavor.id)
		return selectedFlavor?.value?.includes(item) ?? false
	}

	// 选择菜品口味项
	const selectDishFlavor = (flavor: DishFlavor, item: string) => {
		const newArray = selected.value
			.filter(f => f.id !== flavor.id)
			.concat({ ...flavor, value: [item] })

		// 按照原始顺序重新排序
		selected.value = (dish.value?.flavors || [])
			.map(original => newArray.find(f => f.id === original.id))
			.filter(Boolean) as DishFlavor[]
	}

	// 生成口味字符串
	const generateFlavorString = (selectedFlavors: DishFlavor[]) => {
		const orderedFlavors = (dish.value?.flavors || [])
			.map(original => selectedFlavors.find(s => s.id === original.id))
			.filter(Boolean) as DishFlavor[]

		return orderedFlavors
			.map(f => {
				const values = Array.isArray(f.value) ? f.value : [f.value]
				return values.join("-")
			})
			.join("-")
			.trim()
	}

	// 精确价格计算
	const precisePrice = (price: string | number, quantity: number) => {
		if (price === undefined || price === null) {
			return "0.00"
		}

		if (typeof price === "string") {
			price = parseFloat(price || "0.00")
		}

		// 转为分计算避免精度丢失
		const priceInCents = Math.round(price * 100)
		const totalInCents = priceInCents * quantity
		return (totalInCents / 100).toFixed(2)
	}

	function show(data: DishVO) {
		visible.value = true
		// console.log("显示", data)
		dish.value = { ...data }

		// 保持原始顺序初始化选中
		selected.value = (data.flavors || [])
			.map(flavor => {
				try {
					const values = JSON.parse((flavor.value as string) || "[]")
					return {
						...flavor,
						value:
							Array.isArray(values) && values.length > 0 ? [values[0]] : [],
					}
				} catch {
					return { ...flavor, value: [] }
				}
			})
			.filter(f => f.value.length > 0)
	}

	function onClose() {
		visible.value = false
		// 防止布局坍塌
		setTimeout(() => {
			dish.value = null
			selected.value = []
		}, 500)
	}

	defineExpose({
		show,
	})
</script>

<style lang="scss" scoped>
	.dish-flavor-model {
		position: relative;

		.dish-flavors {
			margin-bottom: 20rpx;
		}

		.dish-flavor-name {
			color: #6b6b6b;
			font-size: 28rpx;
			margin-bottom: 15rpx;
		}

		.dish-flavor-item {
			display: flex;
			align-items: center;
			justify-content: flex-start;
			flex-wrap: wrap;
			gap: 20rpx;
		}

		.dish-flavor-bottom {
			margin-top: 60rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;

			.dish-price {
				color: #ca4545;
				font-family: fantasy;
			}
		}

		.dish-flavor-close {
			position: absolute;
			top: 0rpx;
			right: 0rpx;
			z-index: 10077;
			display: flex;
			align-items: center;
			justify-content: center;
			width: 80rpx;
			height: 80rpx;
			border-radius: 50%;
			// border: 2rpx solid #fff;
			// background-color: rgba(14, 19, 7, 0.7);
		}
	}
</style>
