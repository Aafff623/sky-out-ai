<template>
	<view class="comment-page">
		<!-- 展示订单图片和名称 -->
		<view
			class="order-items-container"
			v-if="orderInfo.orderDetailList && orderInfo.orderDetailList.length > 0">
			<view
				class="order-item"
				v-for="(item, index) in orderInfo.orderDetailList"
				:key="index">
				<image
					:src="item.image"
					class="item-image"
					mode="aspectFill" />
				<text class="item-name oneline-hide">{{ item.name }}</text>
			</view>
		</view>

		<view class="comment-input-container">
			<view class="rests">
				<!-- AI功能按钮组 -->
				<button
					class="ai-expand-btn"
					:class="{ 'ai-expand-btn-disabled': !content }"
					:disabled="!content"
					@click="onExpandClick(1)">
					AI帮写
				</button>
				<button
					class="ai-expand-btn"
					:class="{ 'ai-expand-btn-disabled': !content }"
					:disabled="!content"
					@click="onExpandClick(2)">
					AI续写
				</button>
				<button
					class="ai-expand-btn"
					:class="{ 'ai-expand-btn-disabled': !content }"
					:disabled="!content"
					@click="onExpandClick(3)">
					AI润色
				</button>
				<button
					class="ai-expand-btn"
					:class="{ 'ai-expand-btn-disabled': !content }"
					:disabled="!content"
					@click="onExpandClick(4)">
					AI精简
				</button>
				<!-- 表情按钮 -->
				<button
					class="emoji-btn"
					@click="toggleEmojiPanel">
					😊
				</button>
				<!-- 表情选择面板 -->
				<view
					class="emoji-panel"
					v-if="emojiShow">
					<view class="emoji-list">
						<view
							class="emoji-item"
							v-for="(emoji, index) in emojiList"
							:key="index"
							@click="selectEmoji(index)">
							{{ emoji }}
						</view>
					</view>
				</view>
			</view>
			<!-- 评论输入框 -->
			<view class="input-wrapper">
				<textarea
					v-model="content"
					placeholder="欢迎描述商品和使用感受，更受欢迎哦"
					maxlength="500"
					:auto-focus="false"
					class="comment-input"
					@input="onInput" />
			</view>

			<!-- 字数统计 -->
			<view class="char-count"> {{ content.length }}/500 </view>

			<!-- 提交按钮 -->
			<button
				class="submit-btn"
				:disabled="!content.trim()"
				@click="onSubmit">
				{{ isSubmitting ? "提交中..." : "提交评价" }}
			</button>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref, onMounted } from "vue"
	import { OrderVO } from "@/api/order"
	import AiCommentAPI, { AiCommentDto } from "@/api/ai-comment"
	import emojis from "@/static/emojis.json"
	import CommentAPI, { CommentSaveVO } from "@/api/comment"

	// 输入内容
	const content = ref("")

	//订单信息
	const orderInfo = ref<OrderVO>({} as OrderVO)

	//订单的菜品id集合
	const dishIds = ref([])
	/**
	 * 套餐id
	 */
	const setmealIds = ref([])

	//表情列表
	const emojiList = ref([])
	//表情框是否展示
	const emojiShow = ref(false)
	//表情文本
	const getBrowString = ref("")

	// 添加 loading 状态
	const isSubmitting = ref(false)
	//加载表情，存放到表情列表中
	const loadEmojis = () => {
		for (let i in emojis) {
			emojiList.value.push(emojis[i].char)
		}
		// console.log(emojiList.value)
	}
	loadEmojis()
	// 获取用户点击之后的标签 ，存放到输入框内
	const selectEmoji = index => {
		for (let i in emojiList.value) {
			if (index == i) {
				getBrowString.value = emojiList.value[index]
				content.value += getBrowString.value
			}
		}
		emojiShow.value = false
	}

	// 切换表情面板显示状态
	const toggleEmojiPanel = () => {
		emojiShow.value = !emojiShow.value
	}
	// 点击其他区域关闭表情面板
	const handleClickOutside = (event: Event) => {
		const emojiPanel = document.querySelector(".emoji-panel")
		const emojiBtn = document.querySelector(".emoji-btn")

		if (
			emojiPanel &&
			!emojiPanel.contains(event.target as Node) &&
			emojiBtn &&
			!emojiBtn.contains(event.target as Node)
		) {
			emojiShow.value = false
		}
	}

	// 挂载时添加点击事件监听
	onMounted(() => {
		document.addEventListener("click", handleClickOutside)
	})

	// 卸载时移除事件监听
	onUnmounted(() => {
		document.removeEventListener("click", handleClickOutside)
	})
	// 输入监听：控制字数
	const onInput = () => {
		if (content.value.length > 500) {
			content.value = content.value.slice(0, 500)
		}
	}

	// AI扩写按钮点击事件（可扩展为调用AI接口）
	const onExpandClick = async (type: number) => {
		//调用api实现ai扩写功能
		const params: AiCommentDto = {
			dishIds: dishIds.value,
			setmealIds: setmealIds.value,
			content: content.value,
			type: type,
		}
		AiCommentAPI.aiCommentExpandList(params).then(res => {
			content.value = res.data.msg
		})
	}

	// 提交评论
	const onSubmit = async () => {
		// 防止重复提交
		if (!content.value.trim() || isSubmitting.value) return

		isSubmitting.value = true
		//构造请求体
		const params: CommentSaveVO = {
			orderId: orderInfo.value.id,
			dishIds: dishIds.value,
			content: content.value,
			orderNumber: orderInfo.value.number,
			setmealIds: setmealIds.value,
		}

		try {
			// console.log("params:", params)
			// 调用 API 提交评论
			const res = await CommentAPI.addComment(params)
			// console.log("res:", res)
			if (res.code === 200) {
				// 可以添加提示框或跳转等操作
				uni.showToast({
					title: "评论提交成功",
					icon: "success",
					duration: 2000, // 显示2秒
				})
			}
			//回到订单列表页面
			setTimeout(() => {
				uni.navigateBack()
			}, 2000)
		} catch (error) {
			console.error("评论提交失败:", error)
			// uni.showToast({
			// 	title: error.msg,
			// 	icon: "error",
			// 	duration: 2000, // 显示2秒
			// })
			uni
				.showModal({
					title: "提示",
					content: error.msg,
					showCancel: false,
					confirmText: "确定",
				})
				.then(() => {
					uni.navigateBack()
				})
			//回到订单列表页面
			// 失败时也延时返回，保持一致性
		} finally {
			isSubmitting.value = false
		}
	}

	function getDishIdsAndsetmealIds() {
		//遍历订单信息的菜品信息，获取菜品ID
		dishIds.value = orderInfo.value.orderDetailList
			.map(dish => dish.dishId)
			.filter(Boolean)
		//遍历订单信息的菜品信息，获取套餐ID
		setmealIds.value = orderInfo.value.orderDetailList
			.map(dish => dish.setmealId)
			.filter(Boolean)
	}

	onMounted(() => {
		const pages = getCurrentPages()
		const currentPage = pages[pages.length - 1]
		const options = currentPage.options

		if (options.order) {
			const order = JSON.parse(decodeURIComponent(options.order))

			// 使用 order 对象进行后续操作
			orderInfo.value = order
			getDishIdsAndsetmealIds()
			console.log("orderInfo:", orderInfo.value)
		}
	})
</script>

<style scoped lang="scss">
	.comment-page {
		padding: 20rpx;
		min-height: 100vh;
		background-color: #f5f5f5;
		box-sizing: border-box;
		position: relative;

		.order-items-container {
			display: flex;
			flex-wrap: wrap;
			gap: 15rpx;
			width: 100%;
			margin-bottom: 20rpx;

			.order-item {
				width: calc(50% - 10rpx);
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				padding: 20rpx;
				background-color: #fff;
				border-radius: 10rpx;
				box-sizing: border-box;

				.item-image {
					width: 250rpx;
					height: 250rpx;
					border-radius: 8rpx;
					margin-bottom: 15rpx;
				}

				.item-name {
					font-size: 26rpx;
					color: #333;
					text-align: center;
				}
			}
		}

		.comment-input-container {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background-color: #fff;
			border-top: 1rpx solid #eee;
			padding: 20rpx;
			display: flex;
			flex-direction: column;
			align-items: center;
		}

		.rests {
			width: 100%;
			height: 50rpx;
			display: flex;
			justify-content: flex-end; // 将内容放在右侧
			align-items: center;
			gap: 0rpx; // 添加按钮间的间距

			.ai-expand-btn {
				height: 30rpx;
				color: blue;
				border-radius: 10rpx;
				font-size: 24rpx;
				line-height: 30rpx;
				text-align: center;
				box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.2);
				z-index: 10;
			}
			.ai-expand-btn-disabled {
				color: #999 !important; /* 强制覆盖 */
				background-color: #f5f5f5 !important;
				border-color: #ddd !important;
				cursor: not-allowed;
			}

			.emoji-btn {
				width: 50rpx;
				height: 50rpx;
				margin-bottom: 20rpx;
				background-color: #f5f5f5;
				border-radius: 10rpx;
				font-size: 32rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				border: none;
				box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
			}

			// 保持原有的 emoji-panel 样式不变
			.emoji-panel {
				position: absolute;
				// bottom: 60rpx;
				top: -300rpx;
				right: 0;
				width: 400rpx;
				max-height: 300rpx;
				background-color: #e5dede;
				border-radius: 10rpx;
				box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
				z-index: 999;
				overflow: hidden;

				.emoji-list {
					display: flex;
					flex-wrap: wrap;
					padding: 10rpx;
					max-height: 300rpx;
					overflow-y: auto;

					.emoji-item {
						width: 40rpx;
						height: 40rpx;
						font-size: 32rpx;
						margin: 10rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						cursor: pointer;
						border-radius: 8rpx;

						&:hover {
							background-color: #f0f0f0;
						}
					}
				}
			}
		}

		.input-wrapper {
			position: relative;
			width: 100%;
			margin-bottom: 20rpx;

			.comment-input {
				width: 100%;
				height: 160rpx;
				padding: 20rpx;
				background-color: #f5f5f5;
				border-radius: 10rpx;
				font-size: 28rpx;
				line-height: 1.5;
				color: #333;
				border: none;
				outline: none;
				resize: none;
			}
		}

		.char-count {
			font-size: 24rpx;
			color: #999;
			margin-bottom: 20rpx;
			text-align: right;
			width: 100%;
		}

		.submit-btn {
			width: 100%;
			height: 80rpx;
			background-color: #ff4757;
			color: white;
			border-radius: 40rpx;
			font-size: 32rpx;
			line-height: 80rpx;
			border: none;
			outline: none;
			transition: opacity 0.3s;

			&:disabled {
				background-color: #ccc;
				cursor: not-allowed;
			}
		}

		.oneline-hide {
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
	}
</style>
