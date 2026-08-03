<template>
	<scroll-view
		class="message-list"
		:scroll-y="true"
		@scrolltolower="loadMore"
		:refresher-enabled="true"
		:refresher-triggered="false"
		@refresherrefresh="onRefresh">
		<view
			v-for="item in commentMessage"
			:key="item.image"
			class="message-item">
			<!-- 左侧内容 -->
			<view
				class="left-content"
				@click="goToCommentPage(item)">
				<image
					:src="item.repayAuthorAvatar"
					class="avatar"
					mode="aspectFill"></image>
				<view class="user-info">
					<text class="username">{{ item.repayAuthorName }}</text>
					<br />
					<text class="reply-content"
						>回复:
						{{
							item.repayContent.length > 10
								? item.repayContent.substring(0, 8) + "..."
								: item.repayContent
						}}</text
					>
					<text class="time">{{ formatTime(item.createdTime) }}</text>
				</view>
			</view>

			<!-- 右侧商品图片 -->
			<view class="right-content">
				<image
					:src="item.image"
					class="product-image"
					mode="aspectFill"></image>
			</view>
		</view>

		<!-- 加载更多提示 -->
		<view
			class="load-more"
			v-if="hasMore">
			<text>加载中...</text>
		</view>
		<view
			class="load-more"
			v-else-if="commentMessage.length > 0">
			<text>没有更多数据了~</text>
		</view>
	</scroll-view>
</template>

<script setup lang="ts">
	import { ref, onMounted } from "vue"
	import CommentMessageAPI from "@/api/comment-message"
	import type { CommentMessageVO } from "@/api/comment-message"
	import { formatTimestamp, formatTime } from "@/utils/util"

	const commentMessage = ref<CommentMessageVO[]>([])
	const params = ref({
		page: 1,
		pageSize: 10,
	})

	// 添加分页相关状态
	const hasMore = ref(true)
	const loading = ref(false)

	// 下拉刷新
	const onRefresh = async () => {
		if (loading.value) return
		params.value.page = 1
		await getCommentMessage()
		// 关闭刷新状态
		uni.stopPullDownRefresh()
	}

	// 获取被xxx回复的评论消息
	const getCommentMessage = async () => {
		if (loading.value) return
		loading.value = true

		try {
			const res = await CommentMessageAPI.getCommentMessage(params.value)
			commentMessage.value = res.data
			hasMore.value = res.data.length === params.value.pageSize
		} finally {
			loading.value = false
		}
	}

	// 滚动到底部加载更多
	const loadMore = async () => {
		if (loading.value || !hasMore.value) return
		params.value.page++
		await loadMoreData()
	}

	// 加载更多数据
	const loadMoreData = async () => {
		if (loading.value) return
		loading.value = true

		try {
			const res = await CommentMessageAPI.getCommentMessage(params.value)
			if (res.data.length > 0) {
				commentMessage.value = [...commentMessage.value, ...res.data]
				hasMore.value = res.data.length === params.value.pageSize
			} else {
				hasMore.value = false
			}
		} catch (error) {
			params.value.page--
		} finally {
			loading.value = false
		}
	}

	// 跳转到评论详情页面
	const goToCommentPage = (item: CommentMessageVO) => {
		uni.navigateTo({
			url: `/pages/comment/index?dishId=${item.dishId}&setmealId=${item.setmealId}&rootCommentId=${item.rootCommentId}&repayCommentId=${item.repayCommentId}`,
		})
	}
	onMounted(() => {
		getCommentMessage()
	})
</script>

<style lang="scss" scoped>
	.message-list {
		.message-item {
			display: flex;
			padding: 30rpx;

			&:last-child {
				border-bottom: none;
			}
		}

		.left-content {
			flex: 1;
			display: flex;
			align-items: center;

			.avatar {
				width: 100rpx;
				height: 100rpx;
				border-radius: 50%;
				margin-right: 24rpx;
				border: 2rpx solid #fff;
				box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
			}

			.user-info {
				.username {
					font-size: 32rpx;
					font-weight: bold;
					color: #333;
					line-height: 1.4;
				}

				.reply-content {
					font-size: 28rpx;
					color: #666;
					margin: 8rpx 0;
					line-height: 1.4;
				}

				.time {
					margin-left: 20rpx;
					font-size: 24rpx;
					color: #999;
					margin-top: 4rpx;
				}
			}
		}

		.right-content {
			width: 120rpx;
			height: 120rpx;
			overflow: hidden;
			margin-left: 20rpx;

			.product-image {
				width: 100%;
				height: 100%;
				border-radius: 8rpx;
				background-color: #f5f5f5;
			}
		}

		.load-more {
			margin: 0 auto;
			text-align: center;
		}
	}
</style>
