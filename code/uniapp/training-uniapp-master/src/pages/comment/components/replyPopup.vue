<template>
	<view
		class="reply-popup"
		v-if="visible">
		<view
			class="popup-mask"
			@click="onClose"></view>
		<view class="popup-content">
			<view class="popup-header">
				<text class="header-title">回复评论{{ totalCount }}条</text>
				<text
					class="close-btn"
					@click="onClose"
					>×</text
				>
			</view>

			<!-- 回复的原始评论 -->
			<view class="original-comment">
				<view class="original-comment-header">
					<image
						class="user-avatar"
						:src="comment.image || defaultAvatar"
						mode="aspectFill" />
					<view class="user-info">
						<text class="user-name">{{ comment.authorName }}</text>
					</view>
				</view>
				<view class="more-dish-name">
					<text class="more-dish-name-text"
						>关联商品：<text class="item-name">
							{{ comment.moreDishName }}
						</text></text
					>
				</view>
				<view class="comment-text">
					<text>{{ comment.content }}</text>
				</view>
				<view class="comment-time">
					<text>{{ formatTime(comment.createdTime) }}</text>
				</view>
			</view>

			<!-- 回复列表 -->
			<scroll-view
				class="reply-list"
				@scrolltolower="onScrollToLower"
				scroll-y
				:scroll-top="scrollTop"
				scroll-with-animation>
				<view
					class="reply-item"
					v-for="reply in replyList"
					:class="{ highlighted: highlightedReplyId === reply.id }"
					:key="reply.id">
					<image
						class="user-avatar"
						:src="reply.authorAvatar || defaultAvatar"
						mode="aspectFill" />
					<view class="reply-content">
						<view class="user-info">
							<text class="user-name"
								><text class="user-name">{{ reply.authorName }}</text>
								<text
									v-if="reply.authorId === 4"
									class="ai-reply-tag">
									AI自动回复
								</text>
								<text
									v-if="reply.authorId === 5"
									class="merchant-reply-tag">
									商家
								</text>
							</text>
						</view>
						<view class="reply-text">
							<!-- 回复当前主评论 -->
							<text
								v-if="comment.authorId === reply.replyAuthorId"
								class="reply-text-content">
								{{ reply.content }}</text
							>
							<!-- 回复其他评论 -->
							<text
								v-else
								class="reply-text-content"
								>回复@{{ reply.replyAuthorName }}：{{ reply.content }}</text
							>
						</view>
						<view class="comment-time">
							<text>{{ formatTime(reply.createdTime) }}</text>
						</view>
					</view>
					<view class="action-buttons">
						<view
							class="like-container"
							@click.stop="onLikeReply(reply)">
							<image
								:src="reply.operation === 0 ? like : noLike"
								class="like-icon" />
							<text class="like-count">{{ reply.likes || 0 }}</text>
						</view>
						<view
							class="reply-btn"
							@click="onReply(reply)">
							<text>回复</text>
						</view>
						<view
							v-if="reply.authorId === userInfo.id"
							class="delete-btn"
							@click="onDeleteReply(reply)">
							<text>删除</text>
						</view>
					</view>
				</view>

				<!-- 加载状态 -->
				<view
					v-if="loading"
					class="loading-state">
					<text>加载中...</text>
				</view>

				<!-- 没有更多数据 -->
				<view
					v-if="noMoreData"
					class="no-more-state">
					<text>没有更多回复了~</text>
				</view>
			</scroll-view>

			<!-- 回复输入框 -->
			<view class="reply-input-container">
				<view class="input-actions">
					<!-- AI功能按钮组 -->
					<button
						class="ai-expand-btn"
						:class="{ 'ai-expand-btn-disabled': !replyContent }"
						:disabled="!replyContent"
						@click="onExpandClick(1)">
						AI帮写
					</button>
					<button
						class="ai-expand-btn"
						:class="{ 'ai-expand-btn-disabled': !replyContent }"
						:disabled="!replyContent"
						@click="onExpandClick(2)">
						AI续写
					</button>
					<button
						class="ai-expand-btn"
						:class="{ 'ai-expand-btn-disabled': !replyContent }"
						:disabled="!replyContent"
						@click="onExpandClick(3)">
						AI润色
					</button>
					<button
						class="ai-expand-btn"
						:class="{ 'ai-expand-btn-disabled': !replyContent }"
						:disabled="!replyContent"
						@click="onExpandClick(4)">
						AI精简
					</button>
					<!-- 表情按钮 -->
					<button
						class="emoji-btn"
						@click="toggleEmojiPanel">
						😊
					</button>
				</view>

				<!-- 表情选择面板 -->
				<view
					ref="emojiPanelRef"
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

				<view class="input-area">
					<textarea
						ref="replyInput"
						class="reply-input"
						v-model="replyContent"
						:placeholder="replyPlaceholder"
						:maxlength="500"
						@input="onInput"
						@confirm="sendReply"
						auto-height />
					<view class="char-count">
						<text :class="{ 'over-limit': replyContent.length > 500 }">
							{{ replyContent.length }}/500
						</text>
					</view>
					<button
						class="send-button"
						:disabled="!replyContent || replyContent.length > 500"
						@click="sendReply">
						发送
					</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref, watch, onMounted, onUnmounted } from "vue"
	import { formatTime } from "@/utils/util"
	import like from "@/static/like.png"
	import noLike from "@/static/no-like.png"
	import { CommentRepayVO } from "@/api/commentRepay"
	import emojis from "@/static/emojis.json"
	import CommentRepayAPI from "@/api/commentRepay"
	import AiCommentAPI, { AiCommentDto } from "@/api/ai-comment"
	import { UserVO } from "@/api/user"
	// 定义组件属性
	interface Props {
		visible: boolean
		comment: CommentRepayVO
		replyList: CommentRepayVO[]
		repayCommentId: string // 当前需要定位的回复评论ID
		totalCount: number // 总回复数
		userInfo: UserVO
	}
	const replyPlaceholder = ref<string>("请输入回复内容")
	const replyAuthorId = ref<number>()
	const replyCommentId = ref<string>()
	const replyInput = ref()
	// 回复内容
	const replyContent = ref("")
	//当前回复的评论内容 默认为当前主评论 当用户点击回复其他人时 改为其他人的回复
	const currentReplyCommentContent = ref<string>("")
	// 默认头像
	const defaultAvatar = "@/static/default-avatar.png"
	// 添加加载状态
	const loading = ref(false)
	const noMoreData = computed(() => {
		return props.replyList.length >= props.totalCount
	})
	//默认评论主评论
	const isMainComment = ref<boolean>(true)
	//
	const emojiPanelRef = ref<HTMLElement | null>(null)
	//表情列表
	const emojiList = ref([])
	//表情框是否展示
	const emojiShow = ref(false)
	//表情文本
	const getBrowString = ref("")
	// 定义事件
	const emit = defineEmits<{
		(e: "close"): void
		(
			e: "send",
			content: string,
			replyAuthorId: number,
			replyCommentId: string
		): void
		(e: "likeReply", reply: CommentRepayVO): void
		(e: "loadMore"): void
		(e: "replyCommentIdClear"): void
	}>()

	// 接收属性
	const props = withDefaults(defineProps<Props>(), {
		visible: false,
		comment: () => ({} as CommentRepayVO),
		replyList: () => [],
		repayCommentId: "",
		userInfo: () => ({} as UserVO),
		totalCount: 0,
	})

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
				replyContent.value += getBrowString.value
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
		// 使用 ref 替代 document.querySelector
		if (
			emojiPanelRef.value &&
			!emojiPanelRef.value.contains(event.target as Node) &&
			!(event.target as Element).closest(".emoji-btn")
		) {
			emojiShow.value = false
		}
	}

	// 添加 scrollTop 控制滚动位置
	const scrollTop = ref(0)
	// 在响应式变量声明区域添加
	const highlightedReplyId = ref<string | null>(null)
	// 滚动定位方法
	const scrollToTargetComment = () => {
		// 避免重复滚动到同一目标
		if (
			!props.repayCommentId ||
			highlightedReplyId.value === props.repayCommentId
		) {
			return
		}
		if (props.repayCommentId) {
			nextTick(() => {
				setTimeout(() => {
					// 直接计算索引位置进行滚动
					const targetIndex = props.replyList.findIndex(
						reply => reply.id === props.repayCommentId
					)
					if (targetIndex !== -1) {
						const itemHeightPx = uni.upx2px(180)
						const scrollPosition = targetIndex * itemHeightPx - 30 // 留出一些间距

						// 获取实际屏幕宽度
						const screenWidth = uni.getSystemInfoSync().screenWidth
						const scrollPositionPx =
							(scrollPosition * uni.getSystemInfoSync().windowWidth) /
							screenWidth
						scrollTop.value = scrollPositionPx

						// 设置高亮状态
						highlightedReplyId.value = props.repayCommentId

						// 3秒后取消高亮
						setTimeout(() => {
							highlightedReplyId.value = null
							emit("replyCommentIdClear")
						}, 3000)
					} else {
						console.log("未找到目标回复项")
						emit("replyCommentIdClear")
					}
				}, 800)
			})
		}
	}

	// AI扩写按钮点击事件（可扩展为调用AI接口）
	const onExpandClick = async (type: number) => {
		// console.log("AI扩写:", props.comment)
		let params: AiCommentDto = {
			//回复评论不需要传递菜品和套餐id
			content: replyContent.value,
			type: type,
			originalComment: currentReplyCommentContent.value,
		}
		AiCommentAPI.aiCommentExpandList(params).then(res => {
			// console.log("params:", res)
			replyContent.value = res.data.msg
		})
	}
	// 滚动事件处理
	const onScrollToLower = () => {
		// console.log("滚动到底部")
		loadMore()
	}

	// 加载更多回复
	const loadMore = () => {
		// 如果正在加载或没有更多数据，则不执行
		if (loading.value || noMoreData.value) {
			return
		}

		loading.value = true
		emit("loadMore")
	}

	// 暴露方法给父组件
	defineExpose({
		finishLoading: () => {
			loading.value = false
		},
	})
	// 关闭弹窗
	const onClose = () => {
		emit("close")
		replyContent.value = ""
		replyPlaceholder.value = "请输入回复内容"
		// 清除高亮状态
		highlightedReplyId.value = null
		// 重置滚动位置
		scrollTop.value = 0
	}
	// 输入监听：控制字数
	const onInput = () => {
		if (replyContent.value.length > 500) {
			replyContent.value = replyContent.value.slice(0, 500)
		}
	}

	// 发送回复
	const sendReply = () => {
		// console.log("comment:", props.comment)
		if (!replyContent.value.trim()) return
		emit("send", replyContent.value, replyAuthorId.value, replyCommentId.value)
		replyContent.value = ""
		replyPlaceholder.value = "请输入回复内容"
		replyAuthorId.value = undefined
		replyCommentId.value = undefined
	}

	// 监听 comment 属性变化，确保能获取到最新的值
	watch(
		() => props.comment,
		newComment => {
			if (newComment && newComment.content) {
				currentReplyCommentContent.value = newComment.content
				// console.log(currentReplyCommentContent.value)
			}
		},
		{ immediate: true }
	)

	//回复当前评论列表里的其他评论
	const onReply = (reply: CommentRepayVO) => {
		isMainComment.value = false
		// console.log("回复当前评论列表里的其他评论:", reply)
		// 当前回复主评论下的其他的评论内容
		currentReplyCommentContent.value = reply.content
		try {
			replyInput.value?.focus()
		} catch (error) {
			console.warn("无法聚焦输入框:", error)
		}
		replyPlaceholder.value = "回复" + reply.authorName + "："
		replyAuthorId.value = reply.authorId
		replyCommentId.value = reply.id
	}

	// 添加点赞回复的方法
	const onLikeReply = (reply: CommentRepayVO) => {
		emit("likeReply", reply)
	}

	// 监听visible变化，重置输入框
	watch(
		() => props.visible,
		newVal => {
			if (!newVal) {
				replyContent.value = ""
			}
		}
	)

	//监听repayCommentId变化，定位到对应评论
	watch(
		() => props.repayCommentId,
		newVal => {
			if (newVal !== "") {
				console.log("定位回复ID:", props.repayCommentId)
				scrollToTargetComment()
			}
		}
	)

	// 删除回复的方法
	const onDeleteReply = async (reply: CommentRepayVO) => {
		// 提示删除
		uni.showModal({
			title: "友情提示",
			content: "确定要删除该回复吗？",
			showCancel: true,
			success: async ({ confirm }) => {
				const ids = reply.id
				if (confirm) {
					try {
						// 显示加载提示
						uni.showLoading({ title: "删除中..." })
						// 等待删除操作完成
						await CommentRepayAPI.deleteCommentRepay([ids])
						uni.showToast({ title: "删除成功", icon: "success" })
						// 重新获取评论（这里可能需要根据实际业务逻辑调整）
						// 可以考虑通过事件通知父组件刷新数据
						emit("loadMore")
					} catch (error) {
						uni.showToast({ title: "删除失败", icon: "none" })
						console.error("删除回复失败:", error)
					} finally {
						uni.hideLoading()
					}
				}
			},
		})
	}

	// 挂载时添加点击事件监听
	onMounted(() => {
		document.addEventListener("click", handleClickOutside)
	})

	// 卸载时移除事件监听
	onUnmounted(() => {
		currentReplyCommentContent.value = ""
		document.removeEventListener("click", handleClickOutside)
	})
</script>

<style scoped lang="scss">
	.reply-popup {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 999;

		.popup-mask {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background-color: rgba(0, 0, 0, 0.5);
		}

		.popup-content {
			position: absolute;
			bottom: 0;
			left: 0;
			right: 0;
			background-color: #fff;
			border-top-left-radius: 20rpx;
			border-top-right-radius: 20rpx;
			padding: 20rpx;
			max-height: 80vh;
			display: flex;
			flex-direction: column;

			.popup-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding-bottom: 20rpx;
				border-bottom: 1rpx solid #eee;

				.close-btn {
					font-size: 40rpx;
					color: #999;
				}
			}

			.original-comment {
				padding: 20rpx;
				background-color: #f9f9f9;
				border-radius: 10rpx;
				margin: 20rpx 0;
				max-height: 300rpx;
				overflow-y: auto;

				.original-comment-header {
					display: flex;
					align-items: center;
					margin-bottom: 15rpx;
				}

				.more-dish-name {
					font-size: 24rpx;
					color: #999;
				}

				.user-avatar {
					width: 60rpx;
					height: 60rpx;
					border-radius: 50%;
					margin-right: 15rpx;
				}

				.comment-time {
					font-size: 24rpx;
					color: #999;
					margin-top: 10rpx;
				}
			}

			.reply-list {
				flex: 1;
				overflow-y: auto;
				margin: 20rpx 0;
				min-height: 300rpx;
				max-height: 600rpx;

				.loading-state,
				.no-more-state {
					text-align: center;
					padding: 20rpx 0;
					color: #999;
					font-size: 24rpx;
				}

				.reply-item {
					display: flex;
					padding: 20rpx 0;
					border-bottom: 1rpx solid #eee;

					.user-avatar {
						width: 60rpx;
						height: 60rpx;
						border-radius: 50%;
						margin-right: 15rpx;
					}

					.reply-content {
						flex: 1;

						.user-info {
							display: flex;
							align-items: center;
							margin-bottom: 15rpx;

							.user-name {
								font-size: 28rpx;
								font-weight: bold;
								color: #333;
								margin-right: 15rpx;
							}

							.ai-reply-tag {
								width: 120rpx;
								height: 30rpx;
								display: inline-block;
								margin-left: 10rpx;
								padding: 2rpx 8rpx;
								font-size: 24rpx;
								color: #fff;
								background-color: #007aff;
								border-radius: 10rpx;
								vertical-align: middle;
								line-height: 1.2;
							}
							.merchant-reply-tag {
								width: 60rpx;
								height: 30rpx;
								display: inline-block;
								text-align: center;
								margin-left: 10rpx;
								padding: 2rpx 8rpx;
								font-size: 24rpx;
								color: #fff;
								background-color: #000000;
								border-radius: 10rpx;
								vertical-align: middle;
								line-height: 1.2;
							}
						}

						.reply-text {
							font-size: 26rpx;
							color: #333;
							margin: 10rpx 0;
							line-height: 1.4;
						}

						.comment-time {
							font-size: 24rpx;
							color: #999;
							margin-bottom: 20rpx;
						}
					}

					// 添加点赞按钮样式
					.like-container {
						display: flex;
						flex-direction: column;
						align-items: center;
						justify-content: center;
						padding: 0 20rpx;
						margin-left: 20rpx;

						.like-icon {
							width: 32rpx;
							height: 32rpx;
							margin-bottom: 5rpx;
						}

						.like-count {
							font-size: 24rpx;
							color: #999;
						}
					}

					// 添加回复按钮样式
					.reply-btn {
						width: 100rpx;
						height: 70rpx;
						font-size: 28rpx;
						display: flex;
						justify-content: center;
						align-items: center;
						margin-top: 20rpx; // 下移20rpx
						text-align: center;
						line-height: 70rpx;
					}
					.delete-btn {
						width: 100rpx;
						height: 70rpx;
						font-size: 28rpx;
						display: flex;
						justify-content: center;
						align-items: center;
						margin-top: 20rpx; // 下移20rpx
						text-align: center;
						line-height: 70rpx;
					}
					// 使用 flex 布局将所有操作按钮对齐在一行
					.action-buttons {
						display: flex;
						align-items: center;
						gap: 20rpx;
						margin-top: 20rpx;
						margin-left: 20rpx;

						.like-container {
							flex-shrink: 0;
						}

						.reply-btn,
						.delete-btn {
							flex-shrink: 0;
						}
					}

					// 高亮样式
					&.highlighted {
						background-color: #fff3cd;
						// border-left: 6rpx solid #ffc107;
						animation: highlightAnimation 2s ease-in-out;
					}
				}

				@keyframes highlightAnimation {
					0% {
						background-color: #fff3cd;
						transform: scale(1);
					}
					50% {
						background-color: #fff3cd;
						transform: scale(1.02);
					}
					100% {
						background-color: #fff3cd;
						transform: scale(1);
					}
				}
			}

			.reply-input-container {
				position: relative; /* 添加相对定位 */
				display: flex;
				flex-direction: column;
				padding: 20rpx 0;
				border-top: 1rpx solid #eee;

				.input-actions {
					display: flex;
					justify-content: flex-end;
					gap: 20rpx;
					margin-bottom: 20rpx;
					position: relative;

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
						background-color: #f5f5f5;
						border-radius: 10rpx;
						font-size: 32rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						border: none;
						box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
						position: relative;
						z-index: 11;
					}
				}

				.emoji-panel {
					position: absolute;
					top: -320rpx;
					right: 0;
					width: 400rpx;
					max-height: 300rpx;
					background-color: #e5dede;
					border-radius: 10rpx;
					box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
					z-index: 10;
					overflow: hidden;
					transform: translateY(0);
					transition: transform 0.2s ease-in-out;

					.emoji-list {
						display: flex;
						flex-wrap: wrap;
						padding: 10rpx 25rpx;
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

				.input-area {
					display: flex;
					align-items: flex-end;

					.reply-input {
						flex: 1;
						min-height: 70rpx;
						max-height: 200rpx;
						padding: 15rpx 20rpx;
						background-color: #f5f5f5;
						border-radius: 35rpx;
						font-size: 28rpx;
						line-height: 1.4;
						overflow-y: auto;
					}

					.char-count {
						margin: 0 15rpx;
						font-size: 24rpx;
						color: #999;
						align-self: flex-end;
						margin-bottom: 15rpx;

						.over-limit {
							color: #ff4757;
						}
					}

					.send-button {
						width: 120rpx;
						height: 70rpx;
						margin-left: 10rpx;
						background-color: #007aff;
						color: #fff;
						border-radius: 35rpx;
						font-size: 28rpx;
						align-self: flex-end;

						&[disabled] {
							background-color: #ccc;
						}
					}
				}
			}
		}
	}

	.empty-state {
		text-align: center;
		padding: 100rpx 0;
		color: #999;
	}

	.comment-text {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 20rpx;
		line-height: 1.5;
	}

	.user-info {
		display: flex;
		align-items: center;
		margin-bottom: 15rpx;

		.user-name {
			font-size: 28rpx;
			font-weight: bold;
			color: #333;
			margin-right: 15rpx;
		}
	}

	.comment-time {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 20rpx;
	}
</style>
