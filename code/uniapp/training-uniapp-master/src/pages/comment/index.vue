<template>
	<view class="comment-page">
		<view class="comment-header">
			<view class="comment-total"> 共{{ countCommentRepay }}条评论 </view>
			<view class="subsection">
				<up-subsection
					:list="['最新', '热度']"
					:current="currentTabIndex"
					mode="button"
					inactiveColor="#9b9b9b"
					bgColor="#f5f5f5"
					@change="onCommentHotChange" />
			</view>
		</view>

		<view class="comment-list">
			<scroll-view
				scroll-y
				class="comment-scroll-view"
				@scrolltolower="onScrollToLower">
				<view
					class="comment-item"
					v-for="comment in commentList"
					:key="comment.id">
					<!-- 用户头像 -->
					<image
						class="user-avatar"
						:src="comment.image || defaultAvatar"
						mode="aspectFill" />
					<!-- 评论列表 -->
					<view class="comment-content">
						<!-- 用户昵称 -->
						<view class="user-info">
							<text class="user-name">{{ comment.authorName }}</text>
						</view>

						<view class="more-dish-name">
							<text class="more-dish-name-text"
								>关联商品：<text class="item-name">
									{{
										comment.moreDishName?.length > 15
											? comment.moreDishName.substring(0, 15) + "..."
											: comment.moreDishName
									}}
								</text></text
							>
						</view>

						<!-- 评论内容 -->
						<view class="comment-text">
							<text>{{ comment.content }}</text>
						</view>

						<!-- 评论时间 -->
						<view class="comment-time">
							<text>{{ formatTime(comment.createdTime) }}</text>
						</view>

						<!-- 点赞数、回复数 -->
						<view class="comment-actions">
							<!-- 点赞 -->
							<view
								class="action-item"
								@click.stop="onLike(comment)">
								<image
									:src="comment.operation === 0 ? like : noLike"
									class="like-icon" />
								<text>{{ comment.likes }}</text>
							</view>
							<!-- 回复 -->
							<view
								class="action-item"
								@click.stop="onReply(comment)">
								<image
									:src="commentImg"
									class="like-icon" />
								<text>{{ comment.reply }}</text>
							</view>
							<!-- 删除按钮 -->
							<view
								v-if="comment.authorId === userInfo.id"
								class="action-item"
								@click.stop="onDelete(comment)">
								<image
									:src="deleteImg"
									class="delete-icon" />
							</view>
						</view>
					</view>
				</view>

				<!-- 加载状态 -->
				<view
					v-if="loading && commentList.length > 0"
					class="loading-more">
					<text>加载中...</text>
				</view>

				<!-- 没有更多数据 -->
				<view
					v-else-if="!pageParams.hasMore && commentList.length > 0"
					class="no-more-data">
					<text>没有更多数据了~</text>
				</view>

				<!-- 空状态 -->
				<view
					v-if="commentList.length === 0 && !loading"
					class="empty-state">
					<text>暂无评论~</text>
				</view>
			</scroll-view>

			<!-- 使用回复弹窗组件 -->
			<ReplyPopup
				ref="replyPopupRef"
				:visible="showReplyPopup"
				:comment="currentReplyComment"
				:reply-list="replyList"
				:repay-comment-id="repayCommentId"
				:total-count="replyPageParams.total"
				:userInfo="userInfo"
				@loadMore="onLoadMoreReply"
				@reply-comment-id-clear="handleReplyCommentIdClear"
				@close="closeReplyPopup"
				@send="handleSendReply"
				@likeReply="handleLikeReply" />
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref, onMounted } from "vue"
	import CommentAPI, { CommentDto, CommentVO } from "@/api/comment"
	import { formatTime } from "@/utils/util"
	import like from "@/static/like.png"
	import noLike from "@/static/no-like.png"
	import commentImg from "@/static/comment.png"
	import ReplyPopup from "./components/replyPopup.vue"
	import { UserVO } from "@/api/user"
	import { useUserStore } from "@/store"
	import deleteImg from "@/static/delete.png"
	import CommentRepayAPI, {
		CommentRepayVO,
		CommentRepayDTO,
		CommentRepaySaveDTO,
		CommentRepayLikeDTO,
	} from "@/api/commentRepay"

	// 默认头像
	const defaultAvatar = "@/static/default-avatar.png"
	const userStore = useUserStore()
	const userInfo = ref<UserVO>({} as UserVO)

	// 评论相关数据
	const commentList = ref<CommentVO[]>([])
	const loading = ref(false)
	const dishId = ref("")
	const setmealId = ref("")
	//总回复数量（含评论的其他回复）
	const countCommentRepay = ref(0)
	// 回复相关数据
	const showReplyPopup = ref(false)
	const currentReplyComment = ref<CommentRepayVO>({} as CommentRepayVO)
	const replyList = ref<CommentRepayVO[]>([])
	const replyContent = ref("")
	const replyPopupRef = ref(null)
	// 添加响应式变量来跟踪当前选中的索引
	const currentTabIndex = ref(0)

	const rootCommentId = ref("")
	const repayCommentId = ref("")
	onMounted(() => {
		// 获取当前页面实例
		const pages = getCurrentPages()
		const currentPage = pages[pages.length - 1]
		const options = currentPage.options
		dishId.value = options.dishId || ""
		setmealId.value = options.setmealId || ""
		rootCommentId.value = options.rootCommentId || ""
		repayCommentId.value = options.repayCommentId || ""
		if (rootCommentId.value) {
			// 延迟执行确保DOM已渲染
			setTimeout(() => {
				openReplyPopup(rootCommentId.value)
			}, 300)
		}
	})

	const openReplyPopup = (rootCommentId: string) => {
		// 触发展开对应根评论的回复列表
		showReplyPopup.value = true
		//根据根评论id加载根评论
		// 使用 find 方法找到匹配项后自动停止
		const foundComment = commentList.value.find(
			comment => comment.id === rootCommentId
		)
		if (foundComment) {
			currentReplyComment.value = foundComment as CommentRepayVO
		}
		//加载回复列表
		loadReplies(rootCommentId)
	}

	const handleReplyCommentIdClear = () => {
		repayCommentId.value = ""
		console.log("清空回复id:", repayCommentId.value)
	}

	async function getUserInfo() {
		const res = await userStore.getUserInfo()
		userInfo.value = res.data
	}
	getUserInfo()

	// 添加分页相关数据
	const pageParams = reactive({
		size: 10,
		total: 0,
		hasMore: true,
	})
	// 获取评论列表
	const loadComments = async (isLoadMore: boolean = false) => {
		// console.log("dishId:", dishId.value)

		// if (!dishId.value) return

		if (!isLoadMore) {
			// 首次加载或刷新
			pageParams.hasMore = true
			loading.value = true
		} else {
			// 加载更多
			if (!pageParams.hasMore || loading.value) return
			pageParams.size += 10
			loading.value = true
		}
		try {
			const params: CommentDto = {
				dishId: Number(dishId.value),
				setmealId: Number(setmealId.value),
				minDate: new Date(),
				size: pageParams.size,
			}
			const result = await CommentAPI.getCommentList(params)

			if (!isLoadMore) {
				// 首次加载或刷新
				commentList.value = result.data.records || []
			} else {
				// 加载更多
				commentList.value.push(...(result.data.records || []))
			}
			// 设置总条数和是否还有更多
			pageParams.total = result.data.total || 0
			pageParams.hasMore = commentList.value.length < pageParams.total
			// console.log("获取评论结果:", result)
			countCommentRepay.value = result.data.countCommentRepay
		} catch (error) {
			console.error("获取评论失败:", error)
		} finally {
			loading.value = false
		}
	}
	// 触底加载更多
	const onScrollToLower = () => {
		if (pageParams.hasMore && !loading.value) {
			loadComments(true)
		}
	}

	//回复数触底加载更多
	const onLoadMoreReply = () => {
		loadReplies(currentReplyComment.value.id)
	}

	const onCommentHotChange = (index: number) => {
		// 更新当前选中的索引
		currentTabIndex.value = index
		if (index === 0) {
			// 按照时间最新排序（最新评论在前）
			commentList.value.sort((a, b) => {
				return (
					new Date(b.createdTime).getTime() - new Date(a.createdTime).getTime()
				)
			})
		} else {
			// 按照点赞数排序
			commentList.value.sort((a, b) => b.likes - a.likes)
		}
	}
	// 点赞评论
	const onLike = (comment: CommentVO & { liked?: boolean }) => {
		// console.log("点赞评论:", comment.id, comment.operation)
		let operation = null
		if (comment.operation === null || comment.operation === undefined) {
			operation = 0
		} else {
			operation = 1
		}
		CommentAPI.like(comment.id, operation).then(res => {
			if (res.code === 200) {
				// 加载评论
				loadComments()
			}
		})
	}

	// 回复评论
	const onReply = (comment: CommentRepayVO) => {
		// console.log("回复评论:", comment)
		currentReplyComment.value = comment
		showReplyPopup.value = true
		// 获取回复列表
		loadReplies(comment.id)
	}

	// 添加回复分页相关数据
	const replyPageParams = reactive({
		size: 10,
		total: 0,
	})
	// 加载回复列表
	const loadReplies = async (commentId: string) => {
		if (!commentId) return (replyList.value = [])

		try {
			const params: CommentRepayDTO = {
				rootCommentId: commentId,
				minDate: new Date(),
				size: replyPageParams.size,
			}

			const res = await CommentRepayAPI.getCommentRepayLoad(params)
			// console.log(res)
			// 首次加载或刷新
			replyList.value = res.data.records || []

			// 设置总条数和是否还有更多
			replyPageParams.total = res.data.total || 0
		} catch (error) {
			console.error("获取回复失败:", error)
		} finally {
			// 通知子组件结束加载状态
			if (replyPopupRef.value) {
				replyPopupRef.value.finishLoading()
			}
		}
	}

	// 处理发送回复
	const handleSendReply = async (
		content: string,
		replyAuthorId: number,
		replyCommentId: string
	) => {
		if (!content.trim()) return

		try {
			// 添加回复
			const params: CommentRepaySaveDTO = {
				rootCommentId: currentReplyComment.value.id,
				content: content,
				replyAuthorId: replyAuthorId,
				replyCommentId: replyCommentId,
			}
			const res = await CommentRepayAPI.addCommentRepay(params)
			// console.log("添加回复结果:", res)
			if (res.code === 200) {
				uni.showToast({
					title: "回复成功",
					icon: "success",
					duration: 2000,
				})
				//加载最新回复
				loadReplies(currentReplyComment.value.id)
			}
		} catch (error) {
			console.error("发送回复失败:", error)
			uni
				.showModal({
					title: "提示",
					content: error.msg,
					showCancel: false,
					confirmText: "确定",
				})
				.then(() => {
					//加载最新回复
					loadReplies(currentReplyComment.value.id)
				})
		}
	}

	//处理回复点赞
	const handleLikeReply = async (reply: CommentRepayVO) => {
		// console.log("处理回复点赞", reply)
		let operation: number = 0
		if (reply.operation === null || reply.operation === undefined) {
			operation = 0
		} else {
			operation = 1
		}
		const params: CommentRepayLikeDTO = {
			commentRepayId: reply.id,
			operation: operation,
		}
		CommentRepayAPI.like(params).then(res => {
			if (res.code === 200) {
				// 加载最新回复
				loadReplies(currentReplyComment.value.id)
			}
		})
	}

	// 关闭回复弹窗
	const closeReplyPopup = () => {
		showReplyPopup.value = false
		replyContent.value = ""
		// 清空相关状态
		repayCommentId.value = ""
		//重新获取评论
		loadComments()
	}
	//删除评论方法
	const onDelete = async (comment: CommentVO) => {
		// console.log("删除评论", comment)
		//提示删除
		uni.showModal({
			title: "友情提示",
			content: "确定要删除该评论吗？",
			showCancel: true,
			success: async ({ confirm }) => {
				const ids = comment.id
				if (confirm) {
					try {
						// 显示加载提示
						uni.showLoading({ title: "删除中..." })
						// 等待删除操作完成
						await CommentAPI.deleteByIds([ids])
						uni.showToast({ title: "删除成功", icon: "success" })
						//重新获取评论
						loadComments()
					} catch (error) {
						uni.showToast({ title: "删除失败", icon: "none" })
						console.error("删除评论失败:", error)
					} finally {
						uni.hideLoading()
					}
				}
			},
		})
	}

	// 页面加载时获取评论
	onMounted(() => {
		loadComments()
	})
</script>

<style scoped lang="scss">
	.comment-page {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;

		.comment-header {
			padding: 20rpx 0;
			background-color: #fff;
			margin-bottom: 20rpx;
			border-radius: 10rpx;
			display: flex;
			justify-content: space-between;

			.comment-total {
				margin-top: 10rpx;
				margin-left: 20rpx;
			}

			.subsection {
				width: 160rpx;
				margin-right: 20rpx;
			}
		}

		.comment-list {
			margin-bottom: 120rpx;

			.comment-scroll-view {
				height: calc(100vh - 200rpx);
			}

			.comment-item {
				display: flex;
				padding: 30rpx;
				background-color: #fff;
				margin-bottom: 20rpx;
				border-radius: 10rpx;

				.user-avatar {
					width: 80rpx;
					height: 80rpx;
					border-radius: 50%;
					margin-right: 20rpx;
					background-color: #eee;
				}

				.comment-content {
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

						.user-tag {
							font-size: 20rpx;
							padding: 5rpx 10rpx;
							border-radius: 5rpx;
							color: #fff;
						}
					}

					.more-dish-name {
						font-size: 24rpx;
						color: #999;
					}

					.comment-text {
						font-size: 28rpx;
						color: #333;
						margin-bottom: 20rpx;
						line-height: 1.5;
					}

					.comment-time {
						font-size: 24rpx;
						color: #999;
						margin-bottom: 20rpx;
					}

					.comment-actions {
						display: flex;
						justify-content: right;

						.action-item {
							display: flex;
							align-items: center;
							margin-right: 30rpx;
							font-size: 24rpx;
							color: #999;

							.like-icon {
								width: 32rpx;
								height: 32rpx;
								margin-right: 10rpx;
							}

							.delete-icon {
								width: 32rpx;
								height: 32rpx;
								margin-right: 10rpx;
							}

							text:first-child {
								margin-right: 10rpx;
								font-size: 32rpx;
							}
						}
					}
				}
			}

			.loading-more,
			.no-more-data {
				text-align: center;
				padding: 20rpx 0;
				color: #999;
				font-size: 24rpx;
			}

			.empty-state {
				text-align: center;
				padding: 100rpx 0;
				color: #999;
			}
		}

		/* 回复弹窗样式 */
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

					.original-comment-header {
						display: flex;
						align-items: center;
						margin-bottom: 15rpx;
					}

					.user-avatar {
						width: 60rpx;
						height: 60rpx;
						margin-right: 15rpx;
					}
				}

				.reply-list {
					flex: 1;
					overflow-y: auto;
					margin: 20rpx 0;

					.reply-item {
						display: flex;
						padding: 20rpx 0;
						border-bottom: 1rpx solid #eee;

						.user-avatar {
							width: 60rpx;
							height: 60rpx;
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
					}
				}

				.reply-input-container {
					display: flex;
					padding: 20rpx 0;
					border-top: 1rpx solid #eee;

					.reply-input {
						flex: 1;
						height: 70rpx;
						padding: 0 20rpx;
						background-color: #f5f5f5;
						border-radius: 35rpx;
						font-size: 28rpx;
					}

					.send-button {
						width: 120rpx;
						height: 70rpx;
						margin-left: 20rpx;
						background-color: #007aff;
						color: #fff;
						border-radius: 35rpx;
						font-size: 28rpx;

						&[disabled] {
							background-color: #ccc;
						}
					}
				}
			}
		}

		.comment-input-container {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			display: flex;
			padding: 20rpx;
			background-color: #fff;
			border-top: 1rpx solid #eee;

			.comment-input {
				flex: 1;
				height: 70rpx;
				padding: 0 20rpx;
				background-color: #f5f5f5;
				border-radius: 35rpx;
				font-size: 28rpx;
			}
		}

		.liked {
			color: #ff4757;
		}

		// 标签样式
		.tag-1 {
			background-color: #ff6b6b;
		}

		.tag-2 {
			background-color: #4ecdc4;
		}

		.tag-3 {
			background-color: #ff9f1c;
		}

		.tag-4 {
			background-color: #6a0572;
		}

		.tag-5 {
			background-color: #1a535c;
		}
	}
</style>
