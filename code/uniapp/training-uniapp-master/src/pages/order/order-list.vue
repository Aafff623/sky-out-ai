<template>
	<view class="order-list">
		<up-sticky bgColor="#fff">
			<up-tabs
				ref="tabs"
				:list="tabsList"
				:current="curTab"
				lineWidth="35"
				:scrollable="false"
				lineColor="#ca4545"
				:activeStyle="{
					color: '#303133',
					fontWeight: 'bold',
					transform: 'scale(1.05)',
				}"
				:inactiveStyle="{ color: '#606266', transform: 'scale(1)' }"
				@change="handleTabsChange" />
		</up-sticky>

		<!-- 订单列表 swiper -->
		<swiper
			class="order-swiper-container"
			:current="swiperCurrent"
			@change="swiperChange"
			@animationfinish="swiperAnimationFinish">
			<swiper-item
				v-for="(_, swiperIndex) in tabsList"
				:key="swiperIndex"
				class="order-swiper-item">
				<scroll-view
					scroll-y
					class="wh-full"
					@scrolltolower="reachBottom"
					v-if="newRecords.length > 0">
					<view class="page-box">
						<OrderCard
							v-for="order in newRecords"
							:key="order.id"
							:order="order"
							:status-text="orderStatusText(order.status)"
							@canceled="onOrderTimeoutCanceled" />
						<up-loadmore
							:status="loadStatus[curTab]"
							bgColor="#f2f2f2" />
					</view>
				</scroll-view>

				<view
					class="empty-box"
					v-else>
					<up-empty
						mode="order"
						text="数据都跑空啦~" />
				</view>
			</swiper-item>
		</swiper>
	</view>
</template>

<script lang="ts" setup>
	import { OrderStatus } from "@/enums/OrderEnum"
	import OrderAPI, { OrderQueryDTO, OrderVO } from "@/api/order"
	import { onHide, onLoad, onPullDownRefresh, onShow } from "@dcloudio/uni-app"
	import * as UniappType from "@uni-helper/uni-app-types/index"
	import OrderCard from "@/components/order-card/index.vue"
	import CommentAPI from "@/api/comment"

	const tabsList = ref([
		{ name: "全部订单", value: OrderStatus.ALL },
		{ name: "待付款", value: OrderStatus.PENDING_PAYMENT },
		{ name: "堂食订单", value: OrderStatus.DINE_IN_ORDER },
		{ name: "已完成", value: OrderStatus.COMPLETED },
		{ name: "已取消", value: OrderStatus.CANCELED },
		// { name: "退款", value: OrderStatus.REFUNDED },
	])
	// 当前tab索引
	const curTab = ref(0)
	// 当前swiper索引
	const swiperCurrent = ref(0)
	// 加载状态
	const loadStatus = ref(new Array(tabsList.value.length).fill("loadmore"))
	// 分页数据
	const queryParams = reactive<OrderQueryDTO>({
		page: 1,
		pageSize: 10,
		status: OrderStatus.ALL,
	})
	// 订单查询结果
	const pageResult = reactive<PageResult<OrderVO>>({
		total: 0,
		records: [],
	})
	//查询订单是否已经评论
	let newRecords = reactive<OrderVO[]>([])
	const orderIds = ref<number[]>([])
	// 是否还有下一页数据
	const hasNextPage = computed(() => {
		if (pageResult.total === 0) return false
		return pageResult.total > queryParams.page * queryParams.pageSize
	})

	onLoad((options: any) => {
		// 初始化当前选中标签
		initCurTab(options)
	})

	onShow(() => {
		getOrderList(curTab.value)
	})

	onHide(() => resetData())

	onPullDownRefresh(() => {
		resetData()
		getOrderList(
			curTab.value,
			() => {
				uni.stopPullDownRefresh()
			},
			false
		)
	})

	// 订单状态文本
	const orderStatusText = (val?: OrderStatus): string => {
		const statusMap: Record<OrderStatus, string> = {
			[OrderStatus.ALL]: "全部订单",
			[OrderStatus.PENDING_PAYMENT]: "待付款",
			[OrderStatus.PENDING_ACCEPTANCE]: "待接单",
			[OrderStatus.ACCEPTED]: "已接单",
			[OrderStatus.IN_DELIVERY]: "派送中",
			[OrderStatus.COMPLETED]: "已完成",
			[OrderStatus.CANCELED]: "已取消",
			[OrderStatus.REFUNDED]: "退款",
			[OrderStatus.DINE_IN_ORDER]: "待取餐",
		}
		return statusMap[val!] || "未知状态"
	}

	// 初始化当前选中标签
	function initCurTab(options: { dataType: OrderStatus }) {
		if (options.dataType) {
			const index = tabsList.value.findIndex(
				v => v.value === Number(options.dataType)
			)
			curTab.value = index > -1 ? index : 0
		}
	}

	function getOrderList(
		index: any,
		callback?: Function,
		isLoadMore: boolean = false
	) {
		// 正在加载中
		changeLoadStatus(index, "loading")
		// 获取历史订单列表
		OrderAPI.historyOrders(queryParams)
			.then(async res => {
				const { total, records } = res.data
				// console.log("res.data:", records)

				//获取所有已完成订单号
				pageResult.total = total || 0
				if (isLoadMore) {
					// 加载更多：追加数据而不是替换
					pageResult.records.push(...(records || []))
				} else {
					// 首次加载或刷新：清空之前的数据并添加新数据
					pageResult.records.splice(
						0,
						pageResult.records.length,
						...(records || [])
					)
				}
				// console.log("获取历史订单列表:", pageResult)

				orderIds.value = pageResult.records
					.filter(v => v.id && v.status === OrderStatus.COMPLETED)
					.map(v => v.id)
				if (orderIds.value.length > 0) {
					// console.log("获取历史订单ID:", orderIds.value)
					await orderIsComment(orderIds.value)
				}
				// console.log("获取所有已完成订单号2:", orderIds.value)

				// 同样需要根据是否加载更多来处理 newRecords
				if (isLoadMore) {
					newRecords.push(...pageResult.records.slice(newRecords.length))
				} else {
					newRecords.splice(0, newRecords.length, ...pageResult.records)
				}
				// 执行回调函数
				callback?.()
			})
			.finally(() => {
				// 加载结束，loading状态改为`加载更多`
				changeLoadStatus(index, hasNextPage.value ? "loadmore" : "nomore")
			})
	}

	// 查询订单是否已经评论
	function orderIsComment(orderIds: number[]) {
		if (orderIds.length === 0) return Promise.resolve([])
		// console.log("查询订单是否已经评论")
		return CommentAPI.isComment(orderIds)
			.then(res => {
				// console.log("查询订单是否已经评论", res.data)
				// 将result转换为以orderId为键的对象，方便查找
				const commentMap = new Map<number, number>()
				res.data.forEach(item => {
					commentMap.set(item.orderId, item.isComment)
				}) // 更新订单列表中的isComment属性
				pageResult.records.forEach(order => {
					if (order.id && commentMap.has(order.id)) {
						// 设置isComment属性
						order.isComment = commentMap.get(order.id)!
					} else if (order.id) {
						// 默认未评论
						order.isComment = 0
					}
				})

				// 使用splice保持响应性
				newRecords.splice(0, newRecords.length, ...pageResult.records)
				// console.log("newRecords:", newRecords)

				return newRecords
			})
			.catch(error => {
				console.error("查询订单评论状态失败:", error)
			})
	}

	// 触底加载更多
	function reachBottom() {
		if (!hasNextPage.value) return
		queryParams.page += 1
		getOrderList(curTab.value, undefined, true) // 添加 true 表示加载更多
	}

	// tabs 标签栏变化
	const handleTabsChange = (
		tab: {
			index: number
		} & (typeof tabsList.value)[0]
	) => {
		// 防止重复回调
		if (tab.index === curTab.value) return
		curTab.value = tab.index
		// 切换tab时，swiper索引也要跟着切换
		swiperCurrent.value = tab.index
		// 对应tab下的订单状态
		queryParams.status = tab.value
		// 重置分页数据
		resetData()
		// 刷新对应的订单列表页
		getOrderList(tab.index, undefined, false)
	}

	// 订单超时取消回调函数
	async function onOrderTimeoutCanceled(orderId: OrderVO["id"]) {
		const order = pageResult.records?.find(v => v.id === orderId)
		if (order) {
			order.status = OrderStatus.CANCELED
		}
	}

	// swiper索引变化回调函数
	function swiperChange(e: UniappType.SwiperOnChangeEvent) {
		curTab.value = e.detail?.current || 0
	}

	// swiper动画结束回调函数
	function swiperAnimationFinish(e: UniappType.SwiperOnAnimationfinishEvent) {
		const index = e.detail?.current || 0
		// 防止重复回调
		if (swiperCurrent.value === index) return
		const status = tabsList.value[index].value
		swiperCurrent.value = index
		queryParams.status = status
		// 每次切换swiper-item时，都重置数据
		resetData()
		// 刷新对应的订单列表页
		getOrderList(index, undefined, false)
	}

	// 切换加载状态
	function changeLoadStatus(
		index: number,
		status: "loading" | "loadmore" | "nomore",
		deleteCount: number = 1
	) {
		loadStatus.value.splice(index, deleteCount, status)
	}

	// 重置数据
	function resetData() {
		queryParams.page = 1
		pageResult.total = 0
		pageResult.records = []
	}
</script>

<style>
	/* #ifndef H5 */
	page {
		height: 100%;
		background-color: #f2f2f2;
	}

	/* #endif */
</style>

<style lang="scss">
	:deep(.order-list) {
		.u-tabs {
			.u-tabs__wrapper__nav {
				padding: 0 20rpx;
			}
		}

		.u-image {
			border: 5rpx solid #eee;
		}

		.u-empty {
			height: 100%;
		}
	}
</style>

<style lang="scss" scoped>
	.order-list {
		display: flex;
		flex-direction: column;
		height: calc(100vh - var(--window-top));
		width: 100%;
	}

	.order-swiper-container {
		flex: 1;
	}

	.order-swiper-item {
		height: 100%;

		.empty-box {
			height: calc(100% - 88rpx);
		}
	}
</style>
