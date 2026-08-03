<template>
	<view class="dish-detail-page">
		<!-- 菜品图片 -->
		<view class="dish-image-container">
			<up-image
				:src="Object.keys(dish).length > 0 ? dish.image : setmeal.image"
				width="100%"
				height="400rpx"
				radius="20rpx" />
		</view>

		<!-- 菜品信息 -->
		<view class="dish-info">
			<text class="brand">锦食坊</text>
			<text class="dish-name">{{
				Object.keys(dish).length > 0 ? dish.name : setmeal.name
			}}</text>
			<br />
			<text class="dish-description"
				>描述：{{
					Object.keys(dish).length > 0
						? dish.description
						: setmeal.description || "暂无描述"
				}}</text
			>
			<text class="price"
				>¥{{ Object.keys(dish).length > 0 ? dish.price : setmeal.price }}</text
			>

			<br />
			<text
				class="setmeal-detail"
				v-if="setmealDishList.length > 0">
				套餐详情：
				<text
					v-for="(item, index) in setmealDishList"
					:key="index"
					>{{ item.name
					}}{{ index === setmealDishList.length - 1 ? "" : "、" }}</text
				>
			</text>
			<br />
			<text class="dish-category"
				>分类：{{
					Object.keys(dish).length > 0
						? dish.categoryName
						: setmeal.categoryName
				}}</text
			>
		</view>

		<!-- 商品评价 -->
		<view class="comment-container">
			<view class="comment-header dis-flex flex-y-center flex-x-between">
				<text class="f-w f-28">商品评价</text>
				<view
					class="more-comments dis-flex flex-y-center"
					@click="goToCommentPage">
					<text
						class="f-22"
						style="color: #818181"
						>更多评价({{ commentTotal }})</text
					>
					<up-icon
						name="arrow-right"
						color="#818181"
						size="12"
						class="ml-10" />
				</view>
			</view>

			<view class="comment-list">
				<!-- 评论列表 -->
				<view
					class="comment-item mb-20"
					v-if="displayComments.length !== 0"
					v-for="(comment, index) in displayComments"
					:key="index">
					<view
						class="dis-flex flex-y-center mb-10"
						style="padding: 10rpx">
						<up-avatar
							:src="comment.image || '/static/default-avatar.png'"
							customStyle="padding-top:10rpx;width: 80rpx; height: 20rpx; margin-right: 10rpx; margin-bottom:20rpx" />
						<text class="f-24">{{ comment.authorName }}</text>
					</view>
					<view class="comment-content f-24 mb-10">
						{{
							comment.content.length > 25
								? comment.content.slice(0, 25) + "..."
								: comment.content
						}}
					</view>
					<view
						class="comment-date f-20"
						style="color: #818181">
						{{ formatTime(comment.createdTime) }}
					</view>
				</view>
				<!-- 无评论 -->
				<view
					v-else
					class="no-comments f-24"
					style="text-align: center; color: #999; padding: 40rpx 0">
					<image
						style="
							display: block;
							width: 200rpx;
							height: 200rpx;
							margin: 0 auto 20rpx;
						"
						src="/static/no-comment.png"
						mode="aspectFit" />
					<text>暂无评论，快来抢沙发吧！</text>
				</view>
			</view>
		</view>

		<!-- 商家推荐 -->
		<view class="recommend-container">
			<view class="recommend-header">
				<text class="f-w f-32">商家推荐</text>
			</view>

			<view class="recommend-list dis-flex flex-wrap">
				<!-- 推荐商品 -->
				<view
					class="recommend-item"
					v-for="(item, index) in recommendList"
					:key="index"
					style="width: 50%; box-sizing: border-box; padding: 20rpx">
					<view
						class="recommend-image-container"
						@click="handleDishClick(item)">
						<up-image
							:src="item.image"
							width="100%"
							height="200rpx"
							radius="10rpx" />
						<view class="recommend-tag">
							<text class="tag-text">推荐</text>
						</view>
					</view>

					<view class="recommend-info">
						<text class="dish-name">{{ item.name }}</text>

						<view class="price-container dis-flex flex-y-center">
							<view
								class="price-wrap"
								@click="handleDishClick(item)">
								<text class="price">¥{{ item.price }}</text>
								<text class="price-unit">起</text>
							</view>

							<view class="action-btn">
								<view
									v-if="item.flavors && item.flavors.length > 0"
									@click.stop="handleDishFlavors(item)"
									style="position: relative">
									<up-button
										type="primary"
										text="选择规格"
										size="mini"
										shape="circle"
										color="#ca4545" />
									<up-badge
										absolute
										max="99"
										:offset="[-6, -6]"
										bgColor="#ca4545"
										numberType="overflow"
										:value="shoppingCartStore.getBadgeByItemId(item.id)" />
								</view>
								<text
									class="btn-text"
									@click="handleCommentAddCart(item)"
									v-else
									>加入购物车</text
								>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 底部操作 -->
		<view class="bottom-action">
			<view class="btn-wrap dis-flex flex-y-center flex-x-between">
				<view class="left">
					<view
						class="item"
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
					<!-- 购物车 -->
					<view
						class="item"
						@click="handleShowCartPopup">
						<view style="position: relative">
							<up-icon
								name="shopping-cart-fill"
								color="#020202"
								size="15" />
							<up-badge
								absolute
								:offset="[-14, -10]"
								type="error"
								max="99"
								:value="totalNum"
								color="#fff"
								bg-color="#ca4545"
								shape="horn"
								:show="totalNum > 0"></up-badge>
						</view>
						<text class="f-18 mt-10">购物车</text>
					</view>
				</view>
				<!-- 加入购物车和立即下单按钮 -->
				<view
					v-if="!dish.flavors?.length"
					class="action-bar">
					<up-button
						type="primary"
						text="加入购物车"
						color="#FFE278"
						@click="handleAddCart"
						:custom-style="{ marginRight: '20rpx' }" />
					<up-button
						type="primary"
						:text="
							'立即下单￥' +
							(Object.keys(dish).length > 0 ? dish.price : setmeal.price)
						"
						color="#ca4545"
						@click="onOrdersSubmit"
						:custom-style="{ marginRight: '20rpx' }">
					</up-button>
				</view>
				<!-- 选择规格 -->
				<view
					v-else
					class="action-bar"
					@click.stop="handleDishFlavors(dish)"
					style="position: relative">
					<up-button
						type="primary"
						text="选择规格"
						shape="circle"
						:custom-style="{
							width: '400rpx',
							height: '80rpx',
							borderRadius: '20rpx',
							fontSize: '28rpx',
						}"
						color="#ca4545" />

					<up-badge
						absolute
						max="99"
						:offset="[-6, -6]"
						bgColor="#ca4545"
						numberType="overflow"
						:value="shoppingCartStore.getBadgeByItemId(dish.id)" />
				</view>
			</view>
		</view>

		<!-- 购物车弹出层 -->
		<CardPopup
			:show-cart-popup="showCartPopup"
			@on-close="closeCartPopup" />
		<!-- 口味弹窗 -->
		<DishFlavorModel
			ref="JDishFlavorModel"
			:title="dishFlavorModel.title" />
	</view>
</template>

<script lang="ts" setup>
	import { CommentVO } from "@/api/comment"
	import CardPopup from "@/components/card-popup/index.vue"
	import CommentAPI, { CommentDto } from "@/api/comment"
	import { ref, onMounted } from "vue"
	import { onShow } from "@dcloudio/uni-app"
	import { useShoppingCartStore } from "@/store"
	import { ShoppingCart } from "@/api/shopping-cart"
	import { getCurrentInstance } from "vue"
	import DishAPI, { type DishVO } from "@/api/dish"
	import { checkLogin } from "@/core/app"
	import { CategoryTypeEnum } from "@/enums/CategoryEnum"
	import { formatTime } from "@/utils/util"
	import DishFlavorModel from "./DishFlavorModel.vue"
	import SetmealAPI, { SetmealVO } from "@/api/setmeal"
	import ItemViewAPI from "@/api/item-view"

	// 获取当前页面实例
	const instance = getCurrentInstance()
	const proxy = instance?.proxy

	//购物车store
	const shoppingCartStore = useShoppingCartStore()
	//购物车列表
	const cartList = ref<ShoppingCart[]>([])
	//菜品列表
	const dishList = ref<DishVO[]>([])
	//套餐详情包含的菜品列表
	const setmealDishList = ref<DishVO[]>([])
	const showCartPopup = ref(false)
	const { totalNum } = storeToRefs(shoppingCartStore)
	const dishFlavorModel = reactive({
		title: "",
	})
	const JDishFlavorModel = ref<InstanceType<typeof DishFlavorModel>>()
	// 接收从上一页传来的 dish 参数
	const dish = ref<DishVO>({})
	//接收从上一页传来的 setmeal 参数
	const setmeal = ref<SetmealVO>({})
	//选择规格
	const handleDishFlavors = (dish: DishVO) => {
		dishFlavorModel.title = dish.name || "选择菜品口味"
		JDishFlavorModel.value?.show(dish)
	}

	// 添加评价相关数据
	const commentTotal = ref() // 评价总数
	const comments = ref<CommentVO[]>([])

	// 添加推荐商品数据
	const recommendList = ref<DishVO[]>([])

	// 计算显示的评价（只显示前两条）
	const displayComments = computed(() => {
		if (!comments.value) return []
		return comments.value.slice(0, 2)
	})

	// 获取评论列表
	const loadComments = async () => {
		try {
			const params: CommentDto = {
				dishId: dish.value.id,
				setmealId: setmeal.value.id,
				minDate: new Date(),
			}
			const result = await CommentAPI.getCommentList(params)
			// console.log("获取评论结果:", result.data)
			comments.value = result.data.records
			commentTotal.value = result.data.countCommentRepay
			// console.log("获取评论结果:", comments.value.slice(0, 2))
		} catch (error) {
			console.error("获取评论失败:", error)
		}
	}

	// 跳转到评论页面
	const goToCommentPage = () => {
		uni.navigateTo({
			url: `/pages/comment/index?dishId=${dish.value.id}&setmealId=${setmeal.value.id}`,
		})
	}

	const closeCartPopup = () => {
		showCartPopup.value = false
	}

	// 页面加载时获取参数并请求菜品数据
	onMounted(() => {
		const pages = getCurrentPages()
		const currentPage = pages[pages.length - 1]
		const options = currentPage.options

		if (options && options.dish) {
			const dishParam = JSON.parse(decodeURIComponent(options.dish))
			if (dishParam.type === CategoryTypeEnum.DISH) {
				dish.value = dishParam
				// console.log("dish:", dish.value)
				ItemViewAPI.incrementDishViewCount(dish.value.id as number)
			} else {
				setmeal.value = dishParam
				fetchSetmealDetail()
			}
			// console.log("Received options:", dishParam)
		}

		//获取推荐菜品
		fetchRecommendDishes()
		//获取评论
		loadComments()
	})

	//获取套餐详情
	function fetchSetmealDetail() {
		SetmealAPI.getSetmealOfDishList(setmeal.value.id).then(res => {
			// console.log("获取套餐详情:", res.data)
			setmealDishList.value = res.data
		})
	}

	//获取推荐菜品
	async function fetchRecommendDishes() {
		try {
			const res = await DishAPI.getRecommendDish()
			//添加type属性
			const dishes =
				res.data?.map(v => ({ ...v, type: CategoryTypeEnum.DISH })) || []
			recommendList.value = dishes || []
		} catch (error) {
			console.error("获取推荐菜品失败:", error)
		}
	}

	// 显示购物车弹出层
	const handleShowCartPopup = () => {
		// 没有商品，不允许打开弹出层
		if (totalNum.value <= 0) {
			return
		}
		showCartPopup.value = !showCartPopup.value
	}
	//加入购物车
	const handleAddCart = async () => {
		const isContinue = checkLogin()
		if (!isContinue) {
			navToLogin()
			return
		}

		const idKey =
			dish.value.type === CategoryTypeEnum.DISH ? "dishId" : "setmealId"

		dish.value.number = (dish.value.number || 0) + 1
		await shoppingCartStore.addCart({
			name: dish.value.name,
			image: dish.value.image,
			amount: dish.value.price,
			number: dish.value.number,
			dishFlavor: "",
			[idKey]: dish.value.id,
		})
		await shoppingCartStore.getCartlist()
	}

	//推荐商品加入购物车
	function handleCommentAddCart(item: DishVO) {
		const isContinue = checkLogin()
		if (!isContinue) {
			navToLogin()
			return
		}

		const idKey = "dishId"
		item.number = (item.number || 0) + 1
		shoppingCartStore
			.addCart({
				name: item.name,
				image: item.image,
				amount: item.price,
				number: item.number,
				dishFlavor: "",
				[idKey]: item.id,
			})
			.then(() => {
				shoppingCartStore.getCartlist()
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
	async function getCartList() {
		return shoppingCartStore.getCartlist().then(res => {
			cartList.value = res.data || []
			const ids = res.data.map(v => v.dishId || v.setmealId)
			// 更新菜品列表（菜品数量）
			updateDishNumber(ids as number[])
			return cartList.value // 返回购物车列表
		})
	}

	//点击菜品进入详情页
	function handleDishClick(dish: DishVO) {
		// console.log("点击菜品进入详情页", dish)
		// 将整个dish对象序列化后传递
		const dishParam = encodeURIComponent(JSON.stringify(dish))
		uni.navigateTo({
			url: `/pages/index/components/DishDetail?dish=${dishParam}`,
		})
	}
	// 新增 onShow，在每次页面显示时调用
	onShow(() => {
		loadComments()
	})

	// 创建订单，订单确认
	async function onOrdersSubmit() {
		const currentCartList = await getCartList()
		//只有当购物车没有商品时才添加当前商品
		if (currentCartList.length === 0) {
			// console.log("购物车长度:", cartList.value.length)
			await handleAddCart() // 也等待添加完成
		}

		proxy?.$navTo("pages/order/index")
	}

	/**
	 * 提示前往登录
	 */
	function navToLogin() {
		uni.showModal({
			title: "🐱‍🐉温馨提示",
			content: "需要先登录才能点单哦~",
			confirmText: "去登录",
			cancelText: "再看看",
			success: res => {
				if (res.confirm) {
					uni.navigateTo({
						url: "/pages/login/index",
					})
				} else if (res.cancel && getCurrentPages().length > 1) {
					uni.navigateBack()
				}
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

<style lang="scss" scoped>
	$offset-bottom: 140rpx;

	@mixin flex-y-center {
		display: flex;
		align-items: center;
	}

	.dish-detail-page {
		padding: 30rpx;
		background-color: #fff;
		min-height: 100vh;

		.dish-image-container {
			margin-bottom: 30rpx;
		}

		.dish-info {
			margin-bottom: 40rpx;

			.brand {
				font-size: 32rpx;
				font-weight: bold;
				color: white;
				border-radius: 10rpx;
				padding: 5rpx 10rpx;
				background-color: #000;
				margin-right: 10rpx;
				margin-bottom: 10rpx;
			}

			.setmeal-detail {
				font-size: 24rpx;
				color: #8c8c8c;
				margin-bottom: 10rpx;
			}

			.dish-name {
				font-size: 36rpx;
				font-weight: bold;
				color: #303133;
				margin-bottom: 10rpx;
			}

			.dish-category {
				font-size: 24rpx;
				color: #8c8c8c;
				margin-bottom: 10rpx;
			}

			.dish-description {
				font-size: 26rpx;
				color: #666;
				// margin-left: 10rpx;
				line-height: 1.6;
				margin-bottom: 20rpx;
			}

			.price {
				float: right;
				font-size: 40rpx;
				font-weight: bold;
				color: #ca4545;
			}
		}

		.action-bar {
			display: flex;
			gap: 20rpx;
			justify-content: flex-end;
			margin-bottom: 20rpx;
		}

		//评论
		.comment-container {
			margin-bottom: 120rpx;

			.comment-header {
				margin-bottom: 30rpx;

				.more-comments {
					&:active {
						opacity: 0.7;
					}
				}
			}

			.comment-item {
				padding: 20rpx;
				background-color: #f8f8f8;
				border-radius: 10rpx;

				.comment-content {
					line-height: 1.5;
				}
			}
		}

		.quantity-control {
			display: flex;
			justify-content: center;
			margin-top: 20rpx;
		}

		//商家推荐
		.recommend-container {
			margin-bottom: 180rpx;

			.recommend-header {
				margin-bottom: 20rpx;

				.f-w {
					font-weight: bold;
				}
			}

			.recommend-list {
				display: flex;
				flex-wrap: wrap;
				gap: 20rpx;
				padding-left: 40rpx;
				// padding-right: 20rpx;

				.recommend-item {
					flex: 1 1 calc(50% - 10rpx);
					min-width: 280rpx;
					max-width: 300rpx;
					padding: 20rpx;
					box-sizing: border-box;
					background-color: #fff;
					border-radius: 10rpx;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);

					.recommend-image-container {
						position: relative;
						width: 100%;
						height: 200rpx;
						overflow: hidden;
						border-radius: 10rpx;

						.recommend-tag {
							position: absolute;
							top: 0rpx;
							left: 0rpx;
							background-color: #ff4d4d;
							color: white;
							padding: 4rpx 8rpx;
							border-radius: 4rpx;
							font-size: 20rpx;
						}
					}

					.recommend-info {
						margin-top: 10rpx;

						.dish-name {
							font-size: 30rpx;
							font-weight: bold;
							color: #303133;
							line-height: 1.4;
							margin-bottom: 10rpx;
						}

						.dish-meta {
							font-size: 24rpx;
							color: #8c8c8c;
							margin-bottom: 10rpx;
						}

						.price-container {
							display: flex;
							align-items: center;
							justify-content: space-between;

							.price-wrap .price {
								font-size: 32rpx;
								font-weight: bold;
								color: #ca4545;
							}

							.price-wrap .price-unit {
								font-size: 24rpx;
								color: #8c8c8c;
							}

							.action-btn {
								width: 110rpx;
								height: 40rpx;
								background-color: #ca4545;
								border-radius: 20rpx;
								display: flex;
								align-items: center;
								justify-content: center;
								color: white;
								font-size: 20rpx;

								&:active {
									opacity: 0.8;
								}
							}
						}
					}
				}
			}
		}

		// 购物车弹出层
		.cart-header {
			flex: 1;
			max-height: 50rpx;
			height: auto;
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
							margin-right: 10rpx;
						}
					}
				}

				.right {
					@include flex-y-center;
				}
			}
		}
	}
</style>
