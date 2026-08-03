<template>
	<view class="message-page">
		<!-- 页面头部 -->
		<view class="header">
			<text class="title">消息</text>
		</view>

		<!-- 服务选项 -->
		<view class="service-options">
			<!-- 人工客服 -->
			<view
				class="service-item"
				@click="goToCustomerService">
				<view class="service-icon">
					<text class="iconfont icon-kefu">👨‍💼</text>
				</view>
				<view class="service-info">
					<view class="service-name">人工客服</view>
					<view class="service-desc">专业客服为您解答问题</view>
				</view>
				<view class="service-arrow">
					<text class="iconfont">></text>
				</view>
			</view>

			<!-- AI智能助手 -->
			<view
				class="service-item"
				@click="goToAIAssistant">
				<view class="service-icon">
					<text class="iconfont icon-ai">🤖</text>
				</view>
				<view class="service-info">
					<view class="service-name">AI智能助手</view>
					<view class="service-desc">24小时在线，快速响应</view>
				</view>
				<view class="service-arrow">
					<text class="iconfont">></text>
				</view>
			</view>

			<!-- 评论消息 -->
			<view
				class="service-item"
				@click="goToCommentMessage">
				<view class="service-icon">
					<text class="iconfont icon-comment">📩</text>
				</view>
				<view class="service-info">
					<view class="service-name">互动消息</view>
					<view
						v-if="unreadCount > 0"
						class="service-desc"
						>您还有{{ unreadCount }}条未读消息</view
					>
					<view
						v-else
						class="service-desc"
						>暂无未读消息</view
					>
				</view>
				<!-- 未读消息徽章 -->
				<view
					v-if="unreadCount > 0"
					class="badge">
					{{ unreadCount }}
				</view>
				<view class="service-arrow">
					<text class="iconfont">></text>
				</view>
			</view>
		</view>

		<!-- 常见问题 -->
		<view class="faq-section">
			<view class="section-title">常见问题</view>
			<view class="faq-list">
				<view
					class="faq-item"
					v-for="(item, index) in faqList"
					:key="index"
					@click="handleFAQClick(item)">
					<view class="faq-question">{{ item.question }}</view>
					<view class="faq-arrow">></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref } from "vue"
	import { onShow } from "@dcloudio/uni-app"
	import CommentMessageAPI from "@/api/comment-message"
	// 未读消息数量
	const unreadCount = ref(0)
	// 常见问题列表
	const faqList = ref([
		{
			question: "如何查询订单状态？",
			answer: '您可以在"我的"页面中查看订单列表，或联系客服查询。',
		},
		{
			question: "配送时间是多久？",
			answer: "一般配送时间为30-45分钟，具体时间根据距离和订单量而定。",
		},
		{
			question: "如何申请退款？",
			answer: "如需退款，请联系人工客服，我们会尽快为您处理。",
		},
		{
			question: "支付失败怎么办？",
			answer: "请检查网络连接和支付方式，或尝试更换支付方式。",
		},
	])

	// 跳转到人工客服
	const goToCustomerService = () => {
		uni.navigateTo({
			url: "/pages/message/customer-service",
		})
	}

	// 跳转到AI助手
	const goToAIAssistant = () => {
		uni.navigateTo({
			url: "/pages/message/ai-assistant",
		})
	}

	// 跳转到评论消息
	const goToCommentMessage = () => {
		uni.navigateTo({
			url: "/pages/message/comment-message",
		})
	}

	/**
	 * 获取未读评论消息数量
	 */
	const getUnreadCount = async () => {
		const res = await CommentMessageAPI.getUnreadCount()
		unreadCount.value = res.data
	}

	// 处理常见问题点击
	const handleFAQClick = (item: any) => {
		uni.showModal({
			title: item.question,
			content: item.answer,
			showCancel: false,
			confirmText: "知道了",
		})
	}

	onShow(() => {
		getUnreadCount()
	})
</script>

<style lang="scss" scoped>
	.message-page {
		min-height: 100vh;
		background-color: #f5f5f5;
	}

	.header {
		background-color: #fff;
		padding: 20rpx 30rpx;
		border-bottom: 1rpx solid #eee;

		.title {
			font-size: 36rpx;
			font-weight: bold;
			color: #333;
		}
	}

	.service-options {
		margin-top: 20rpx;
		background-color: #fff;
		border-radius: 16rpx;
		margin: 20rpx;
		overflow: hidden;
	}

	.service-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;

		&:last-child {
			border-bottom: none;
		}

		.service-icon {
			width: 80rpx;
			height: 80rpx;
			background-color: #f8f8f8;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 24rpx;
			font-size: 40rpx;
		}

		.service-info {
			flex: 1;

			.service-name {
				font-size: 32rpx;
				color: #333;
				font-weight: 500;
				margin-bottom: 8rpx;
			}

			.service-desc {
				font-size: 26rpx;
				color: #999;
			}
		}

		// 添加徽章样式
		.badge {
			background-color: #ff4d4f;
			color: #fff;
			border-radius: 30rpx;
			padding: 0 12rpx;
			font-size: 24rpx;
			min-width: 36rpx;
			height: 36rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 16rpx;
		}

		.service-arrow {
			color: #ccc;
			font-size: 28rpx;
		}
	}

	.faq-section {
		margin: 20rpx;
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;

		.section-title {
			padding: 30rpx;
			font-size: 32rpx;
			font-weight: 500;
			color: #333;
			border-bottom: 1rpx solid #f0f0f0;
		}
	}

	.faq-list {
		.faq-item {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 30rpx;
			border-bottom: 1rpx solid #f0f0f0;

			&:last-child {
				border-bottom: none;
			}

			.faq-question {
				font-size: 28rpx;
				color: #333;
				flex: 1;
			}

			.faq-arrow {
				color: #ccc;
				font-size: 24rpx;
			}
		}
	}
</style>
