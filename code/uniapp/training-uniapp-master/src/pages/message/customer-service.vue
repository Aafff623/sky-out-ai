<template>
	<view class="customer-service-page">
		<!-- 消息列表 -->
		<scroll-view class="message-list" scroll-y :scroll-top="scrollTop"
			scroll-with-animation="false">
			<view class="message-item" v-for="(message, index) in messageList"
				:key="index">
				<!-- 系统消息 -->
				<view v-if="message && message.type === 'system'"
					class="system-message">
					<text class="system-text">{{ message.content }}</text>
				</view>

				<!-- 用户消息 -->
				<view v-else-if="message && message.senderType === 1"
					class="user-message">
					<view class="message-content user-content">
						<text>{{ message.content }}</text>
					</view>
					<view class="message-avatar user-avatar">
						<image v-if="userInfo.avatar" :src="userInfo.avatar"
							class="avatar-image" />
						<text v-else
							class="avatar-text">{{ userInfo.name?.charAt(0) || "用" }}</text>
					</view>
				</view>

				<!-- 客服消息 -->
				<view v-else-if="message" class="staff-message">
					<view class="message-avatar staff-avatar">
						<text class="avatar-text">🎧</text>
					</view>
					<view class="message-content staff-content">
						<text>{{ message.content }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 快捷操作按钮 -->
		<view class="quick-actions">
			<button class="quick-btn" @click="quickQuery('订单问题')">
				订单问题
			</button>
			<button class="quick-btn" @click="quickQuery('配送问题')">
				配送问题
			</button>
			<button class="quick-btn" @click="quickQuery('售后服务')">
				售后服务
			</button>
			<button class="quick-btn" @click="quickQuery('退款申请')">
				退款申请
			</button>
			<button class="quick-btn" @click="quickQuery('商品咨询')">
				商品咨询
			</button>
		</view>

		<!-- 输入区域 -->
		<view class="input-area">
			<view class="input-container">
				<input class="message-input" v-model="inputText"
					placeholder="请输入您的问题..." @confirm="sendMessage" confirm-type="send" />
				<button class="send-btn" @click="sendMessage"
					:disabled="!inputText.trim()">
					发送
				</button>
			</view>
		</view>

		<!-- 连接状态提示 -->
		<view v-if="!isConnected" class="connection-status">
			<text>正在连接客服...</text>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref, onMounted, onUnmounted, nextTick } from "vue"
	import CustomerServiceAPI from "@/api/customer-service"
	import { useUserStore } from "@/store/modules/user"

	// 响应式数据
	const messageList = ref<any[]>([])
	const inputText = ref("")
	const scrollTop = ref(0)
	const isConnected = ref(false)
	const sessionId = ref("")

	// 用户信息和客服信息
	const userInfo = ref({
		name: "用户",
		avatar: "",
	})
	const staffInfo = ref({
		name: "客服",
		avatar: "",
	})

	// WebSocket相关
	let websocket: UniApp.SocketTask | null = null

	// 页面加载时初始化
	onMounted(() => {
		initCustomerService()
		loadUserInfo()

		// 页面完全加载后滚动到底部
		setTimeout(() => {
			scrollToBottom()
		}, 1500)
	})

	// 页面卸载时清理
	onUnmounted(() => {
		if (websocket) {
			websocket.close({})
		}
	})

	// 初始化客服服务
	const initCustomerService = async () => {
		try {
			// 1. 创建或获取客服会话
			const sessionResult = await createCustomerServiceSession()
			// console.log('创建会话结果:', sessionResult)
			// console.log('sessionResult类型:', typeof sessionResult)
			// console.log('sessionResult的所有属性:', Object.keys(sessionResult || {}))
			// console.log('sessionResult.sessionId:', sessionResult?.sessionId)
			// console.log('完整的sessionResult对象:', JSON.stringify(sessionResult, null, 2))

			if (sessionResult && sessionResult.sessionId) {
				sessionId.value = sessionResult.sessionId

				// 2. 先加载历史消息
				await loadHistoryMessages()

				// 3. 再连接WebSocket
				connectWebSocket()

				// 5. 不显示系统消息

				// 6. 初始化完成后滚动到底部
				setTimeout(() => {
					scrollToBottom()
				}, 800)
			} else {
				console.error("会话ID为空:", sessionResult)
				uni.showToast({
					title: "获取会话失败",
					icon: "error",
				})
			}
		} catch (error) {
			console.error("初始化客服服务失败:", error)
			uni.showToast({
				title: "连接客服失败",
				icon: "error",
			})
		}
	}

	// 创建客服会话
	const createCustomerServiceSession = async () => {
		const res = await CustomerServiceAPI.createHumanSession()
		if (res.code === 200) {
			return res.data
		} else {
			uni.showToast({
				title: "创建客服会话失败",
				icon: "error",
			})
		}

	}

	// 连接WebSocket
	const connectWebSocket = () => {
		const wsUrl = `ws://localhost:8848/ws/customer-service/${sessionId.value}/user`

		websocket = uni.connectSocket({
			url: wsUrl,
			success: () => {
				console.log("WebSocket连接成功")
			},
			fail: error => {
				console.error("WebSocket连接失败:", error)
				uni.showToast({
					title: "连接失败",
					icon: "error",
				})
			},
		})

		// WebSocket事件监听
		websocket.onOpen(() => {
			console.log("WebSocket连接已打开")
			isConnected.value = true
			// 不显示连接成功消息
		})

		websocket.onMessage(res => {
			console.log("收到WebSocket消息:", res.data)
			try {
				const message = JSON.parse(res.data)
				handleWebSocketMessage(message)
			} catch (error) {
				console.error("解析WebSocket消息失败:", error)
			}
		})

		websocket.onClose(() => {
			console.log("WebSocket连接已关闭")
			isConnected.value = false
			// 不显示连接断开消息
		})

		websocket.onError(error => {
			console.error("WebSocket错误:", error)
			isConnected.value = false
			// 不显示错误消息
		})
	}

	// 处理WebSocket消息
	const handleWebSocketMessage = (message: any) => {
		if (message.type === "system") {
			// 不显示系统消息
			// console.log('收到系统消息:', message.message)
		} else if (message.type === "message") {
			// 如果是客服消息，更新客服信息
			if (message.senderType === 2 && message.staffInfo) {
				staffInfo.value = {
					name: message.staffInfo.name || "客服",
					avatar: message.staffInfo.avatar || "",
				}
			}

			const newMessage = {
				senderType: message.senderType,
				content: message.content,
				createTime: message.createTime || new Date().toISOString(),
				timestamp: message.timestamp || Date.now(),
			}

			messageList.value.push(newMessage)

			scrollToBottom()
		} else if (message.type === "error") {
			// 不显示错误消息
			console.log("收到错误消息:", message.message)
		}
	}

	// 加载历史消息
	const loadHistoryMessages = async () => {
		try {
			// console.log('开始加载历史消息，sessionId:', sessionId.value)

			// 从服务器获取消息
			const response = await CustomerServiceAPI.getSessionMessages(
				sessionId.value,
				1,// 1表示人工客服消息
			)
			// console.log('服务器返回响应:', response, '类型:', typeof response)

			// 提取实际的消息数据
			const messages = response?.data || response || []
			// console.log('提取的消息数据:', messages, '类型:', typeof messages, '长度:', messages?.length)

			if (Array.isArray(messages) && messages.length > 0) {
				// 过滤并转换消息格式
				const validMessages = messages
					.filter(msg => msg != null && msg.content && msg.senderType)
					.map(msg => ({
						id: msg.id,
						sessionId: msg.sessionId,
						senderType: msg.senderType,
						senderId: msg.senderId,
						messageType: msg.messageType || 1,
						content: msg.content,
						createTime: msg.createTime,
						timestamp: msg.createTime
							? new Date(msg.createTime).getTime()
							: Date.now(),
					}))

				// console.log('处理后的消息:', validMessages)

				// 直接设置消息列表，按时间排序
				messageList.value = validMessages.sort(
					(a, b) => (a.timestamp || 0) - (b.timestamp || 0)
				)

				// console.log('最终消息列表:', messageList.value)
			} else {
				console.log("服务器没有返回消息或消息为空")
				messageList.value = []
			}

			// 加载完成后滚动到底部
			setTimeout(() => {
				scrollToBottom()
			}, 300)
		} catch (error) {
			console.error("加载历史消息失败:", error)
			messageList.value = []
			uni.showToast({
				title: "加载消息失败",
				icon: "error",
			})
		}
	}

	// 发送消息
	const sendMessage = () => {
		const content = inputText.value.trim()
		if (!content || !isConnected.value) return

		// 创建消息对象
		const newMessage = {
			senderType: 1, // 用户
			content: content,
			createTime: new Date().toISOString(),
			timestamp: Date.now(),
		}

		// 添加到消息列表
		messageList.value.push(newMessage)

		// 通过WebSocket发送消息
		if (websocket) {
			websocket.send({
				data: JSON.stringify({
					content: content,
					messageType: 1, // 文本消息
				}),
			})
		}

		// 清空输入框
		inputText.value = ""

		// 发送消息后滚动到底部
		scrollToBottom()
		setTimeout(scrollToBottom, 100)
		setTimeout(scrollToBottom, 300)
	}

	// 添加系统消息
	const addSystemMessage = (content: string) => {
		const systemMessage = {
			type: "system",
			content: content,
			createTime: new Date().toISOString(),
			timestamp: Date.now(),
		}

		messageList.value.push(systemMessage)

		// 接收消息后滚动到底部
		scrollToBottom()
		setTimeout(scrollToBottom, 100)
		setTimeout(scrollToBottom, 300)
	}

	// 加载用户信息
	const loadUserInfo = () => {
		const userStore = useUserStore()

		// 获取真实用户信息
		if (userStore.userInfo) {
			userInfo.value = {
				name: userStore.userInfo.name || "用户",
				avatar: userStore.userInfo.avatar || "",
			}
		} else {
			// 从本地存储获取
			try {
				const storedUserInfo = uni.getStorageSync("userInfo")
				if (storedUserInfo) {
					const userData =
						typeof storedUserInfo === "string"
							? JSON.parse(storedUserInfo)
							: storedUserInfo
					userInfo.value = {
						name: userData.name || userData.nickName || "用户",
						avatar: userData.avatar || userData.avatarUrl || "",
					}
				}
			} catch (error) {
				console.log("获取用户信息失败:", error)
				userInfo.value = {
					name: "用户",
					avatar: "",
				}
			}
		}

		// 设置默认客服信息
		staffInfo.value = {
			name: "客服小助手",
			avatar: "", // 可以设置默认客服头像
		}

		// console.log("用户信息:", userInfo.value)
	}

	// 滚动到底部 - 模仿管理端的实现
	const scrollToBottom = () => {
		// 直接设置一个足够大的值，让scroll-view滚动到底部
		scrollTop.value = 99999
		// console.log("强制滚动到底部，scrollTop:", scrollTop.value)

		// 立即再次设置，确保滚动生效
		setTimeout(() => {
			scrollTop.value = scrollTop.value === 99999 ? 99998 : 99999
		}, 10)
	}

	// 快捷查询
	const quickQuery = (type: string) => {
		const queries: Record<string, string> = {
			订单问题: "我想咨询一下订单相关的问题",
			配送问题: "请问配送方面有什么问题可以帮我解决吗？",
			售后服务: "我需要售后服务，请问怎么处理？",
			退款申请: "我想申请退款，请问流程是什么？",
			商品咨询: "我想了解一下商品的详细信息",
		}

		inputText.value = queries[type] || ""
		sendMessage()
	}
</script>

<style lang="scss" scoped>
	.customer-service-page {
		height: 100vh;
		display: flex;
		flex-direction: column;
		background-color: #f5f5f5;
	}

	.message-list {
		flex: 1;
		padding: 20rpx;
		padding-bottom: 40rpx;
	}

	.message-item {
		margin-bottom: 30rpx;

		&:last-child {
			margin-bottom: 20rpx;
		}
	}

	.system-message {
		text-align: center;

		.system-text {
			background-color: rgba(0, 0, 0, 0.1);
			color: #666;
			font-size: 24rpx;
			padding: 10rpx 20rpx;
			border-radius: 20rpx;
		}
	}

	.user-message {
		display: flex;
		justify-content: flex-end;
		align-items: flex-start;
		margin-bottom: 32rpx; // 和管理端保持一致 16px = 32rpx
		margin-right: 40rpx; // 右边留出空间避开悬浮数字

		.user-content {
			background-color: #d81e06;
			color: white;
			margin-right: 16rpx; // 和管理端保持一致 8px = 16rpx
		}

		.user-avatar {
			background-color: #d81e06;
			flex-shrink: 0;
		}
	}

	.staff-message {
		display: flex;
		justify-content: flex-start;
		align-items: flex-start;
		margin-bottom: 32rpx;
		margin-left: 20rpx;

		.staff-content {
			background-color: white;
			color: #333;
			margin-left: 16rpx;
			border: 1rpx solid #e0e0e0;
		}

		.staff-avatar {
			background-color: #007aff;
			flex-shrink: 0;
		}
	}

	.message-content {
		max-width: 500rpx;
		min-width: 80rpx;
		padding: 20rpx;
		border-radius: 16rpx;
		font-size: 28rpx;
		line-height: 1.4;
		word-wrap: break-word;
		word-break: break-all;
	}

	.message-avatar {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		color: white;
		font-size: 24rpx;
		overflow: hidden;

		.avatar-image {
			width: 100%;
			height: 100%;
			border-radius: 50%;
		}

		.avatar-text {
			color: white;
			font-size: 24rpx;
			font-weight: bold;
		}
	}

	.quick-actions {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
		padding: 20rpx;
		background-color: white;
		border-top: 1rpx solid #eee;
	}

	.quick-btn {
		flex: 0 0 calc(33.333% - 10rpx);
		/* 每行显示3个按钮 */
		height: 60rpx;
		background-color: #f8f8f8;
		color: #333;
		border: none;
		border-radius: 30rpx;
		font-size: 22rpx;
	}

	.input-area {
		background-color: white;
		padding: 20rpx;
		border-top: 1rpx solid #eee;
	}

	.input-container {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.message-input {
		flex: 1;
		height: 80rpx;
		padding: 0 20rpx;
		border: 1rpx solid #ddd;
		border-radius: 40rpx;
		font-size: 28rpx;
	}

	.send-btn {
		width: 120rpx;
		height: 80rpx;
		background-color: #d81e06;
		color: white;
		border: none;
		border-radius: 40rpx;
		font-size: 28rpx;

		&:disabled {
			background-color: #ccc;
		}
	}

	.connection-status {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		background-color: rgba(0, 0, 0, 0.7);
		color: white;
		padding: 20rpx 40rpx;
		border-radius: 10rpx;
		font-size: 28rpx;
	}
</style>
