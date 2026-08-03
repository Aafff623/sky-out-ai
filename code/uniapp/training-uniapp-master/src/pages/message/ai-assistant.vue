<template>
	<view class="ai-assistant-page">
		<!-- 消息列表 -->
		<scroll-view
			class="message-list"
			scroll-y
			:scroll-top="scrollTop"
			:scroll-with-animation="false">
			<!-- 消息列表 - 直接循环，不使用容器包装 -->
			<view
				class="message-item"
				v-for="(message, index) in messageList"
				:key="index">
				<!-- 过滤null消息 -->
				<template v-if="message">
					<!-- 系统消息 -->
					<view
						v-if="message.type === 'system'"
						class="system-message">
						<text class="system-text">{{ message.content }}</text>
					</view>

					<!-- 用户消息 -->
					<view
						v-else-if="message.senderType === 1"
						class="user-message">
						<view class="message-content user-content">
							<text>{{ getMessageQuestion(message.content) }}</text>
						</view>
						<view class="message-avatar user-avatar">
							<image
								v-if="userInfo?.avatar"
								:src="userInfo.avatar"
								class="avatar-image" />
							<text
								v-else
								class="avatar-text"
								>{{ userInfo?.name?.charAt(0) || "用" }}</text
							>
						</view>
					</view>

					<!-- AI消息 -->
					<view
						v-else-if="message.senderType === 3 && !message.isTyping"
						class="ai-message">
						<view class="message-avatar ai-avatar">
							<text class="avatar-text">🤖</text>
						</view>
						<view class="ai-content">
							<!-- <text>{{ message.content }}</text> -->
							<towxml :nodes="useMarkdown(message.content)" />
							<!-- ai历史记录参数事件渲染 -->
							<view
								v-if="
									message.params &&
									message.params.Dish.length > 0 &&
									!message.isTyping &&
									!message.initMessage
								">
								<view class="recommend-list dis-flex flex-wrap">
									<!-- 推荐商品 -->
									<view
										class="recommend-item"
										v-for="(item, index) in message.params.Dish"
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
															:value="
																shoppingCartStore.getBadgeByItemId(item.id)
															" />
													</view>
													<view
														class="btn-text"
														@click="handleCommentAddCart(item)"
														v-else>
														<up-button
															type="primary"
															text="加入购物车"
															size="mini"
															shape="circle"
															color="#ca4545" />
														<up-badge
															absolute
															max="99"
															:offset="[-6, -6]"
															bgColor="#ca4545"
															numberType="overflow"
															:value="
																shoppingCartStore.getBadgeByItemId(item.id)
															" />
													</view>
												</view>
											</view>
										</view>
									</view>
								</view>
								<!-- 购物车 -->
								<view
									class="checkout-wrap"
									style="width: 250rpx; z-index: 9999; margin: 5rpx auto">
									<up-col
										span="8"
										offset="2"
										justify="end">
										<up-button
											type="error"
											text="购物车"
											color="#ca4545"
											shape="circle"
											@click="onOpenCart"
											:disabled="shoppingCartStore.totalNum <= 0"
											:custom-style="{
												'max-width': '150rpx',
												'margin-top': '20rpx',
											}" />
									</up-col>
								</view>
							</view>
							<!-- ai回答参数事件渲染 -->
							<view
								v-else-if="
									isTypewriter &&
									aiParamsObject !== null &&
									aiParamsObject.Dish.length > 0
								">
								<view class="recommend-list dis-flex flex-wrap">
									<!-- 推荐商品 -->
									<view
										class="recommend-item"
										v-for="(item, index) in aiParamsObject.Dish"
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
												<text class="tag-text">推荐2</text>
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
															:value="
																shoppingCartStore.getBadgeByItemId(item.id)
															" />
													</view>
													<view
														class="btn-text"
														@click="handleCommentAddCart(item)"
														v-else>
														<up-button
															type="primary"
															text="加入购物车"
															size="mini"
															shape="circle"
															color="#ca4545" />
														<up-badge
															absolute
															max="99"
															:offset="[-6, -6]"
															bgColor="#ca4545"
															numberType="overflow"
															:value="
																shoppingCartStore.getBadgeByItemId(item.id)
															" />
													</view>
												</view>
											</view>
										</view>
									</view>
								</view>
								<!-- 购物车 -->
								<view
									class="checkout-wrap"
									style="width: 250rpx; z-index: 9999; margin: 5rpx auto">
									<up-col
										span="8"
										offset="2"
										justify="end">
										<up-button
											type="error"
											text="购物车"
											color="#ca4545"
											shape="circle"
											@click="onOpenCart"
											:disabled="shoppingCartStore.totalNum <= 0"
											:custom-style="{
												'max-width': '150rpx',
												'margin-top': '20rpx',
											}" />
									</up-col>
								</view>
							</view>
						</view>
					</view>

					<!-- AI消息 - 正在输入的消息 -->
					<view
						v-else-if="
							message.senderType === 3 &&
							message.isTyping &&
							message.content !== ''
						"
						class="ai-message">
						<view class="message-avatar ai-avatar">
							<text class="avatar-text">🤖</text>
						</view>
						<view class="ai-content">
							<towxml :nodes="useMarkdown(message.content)" />
						</view>
					</view>

					<!-- AI消息 - 参数事件渲染（仅在打字机效果完成后显示） -->
					<view
						v-else-if="
							message.senderType === 3 &&
							!message.isTyping &&
							message.params &&
							message.params.Dish &&
							message.params.Dish.length > 0 &&
							!message.initMessage
						"
						class="ai-message ai-content-params">
						<view class="message-avatar ai-avatar">
							<text class="avatar-text">🤖</text>
						</view>
						<view class="ai-content">
							<view class="recommend-list dis-flex flex-wrap">
								<!-- 推荐商品 -->
								<view
									class="recommend-item"
									v-for="(item, index) in message.params.Dish"
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
														:value="
															shoppingCartStore.getBadgeByItemId(item.id)
														" />
												</view>
												<view
													class="btn-text"
													@click="handleCommentAddCart(item)"
													v-else>
													<up-button
														type="primary"
														text="加入购物车"
														size="mini"
														shape="circle"
														color="#ca4545" />
													<up-badge
														absolute
														max="99"
														:offset="[-6, -6]"
														bgColor="#ca4545"
														numberType="overflow"
														:value="
															shoppingCartStore.getBadgeByItemId(item.id)
														" />
												</view>
											</view>
										</view>
									</view>
								</view>
							</view>
							<!-- 购物车 -->
							<view
								class="checkout-wrap"
								style="width: 250rpx; z-index: 9999; margin: 5rpx auto">
								<up-col
									span="8"
									offset="2"
									justify="end">
									<up-button
										type="error"
										text="购物车"
										color="#ca4545"
										shape="circle"
										@click="onOpenCart"
										:disabled="shoppingCartStore.totalNum <= 0"
										:custom-style="{
											'max-width': '150rpx',
											'margin-top': '20rpx',
										}" />
								</up-col>
							</view>
						</view>
					</view>
				</template>
			</view>

			<!-- AI正在输入的加载气泡 -->
			<view
				v-if="isBubble"
				class="message-item">
				<view class="ai-message">
					<view class="message-avatar ai-avatar">
						<text class="avatar-text">🤖</text>
					</view>
					<view class="message-content ai-content typing-bubble">
						<view class="typing-indicator">
							<view class="typing-dot"></view>
							<view class="typing-dot"></view>
							<view class="typing-dot"></view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 快捷操作按钮 -->
		<view class="quick-actions">
			<button
				class="quick-btn"
				@click="quickQuery('查询订单')">
				查询订单
			</button>
			<button
				class="quick-btn"
				@click="quickQuery('推荐菜品')">
				推荐菜品
			</button>
			<button
				class="quick-btn"
				@click="quickQuery('营业时间')">
				营业时间
			</button>
			<button
				class="quick-btn"
				@click="quickQuery('联系方式')">
				联系方式
			</button>
			<button
				class="quick-btn"
				@click="quickQuery('当前天气')">
				当前天气
			</button>
			<button
				class="quick-btn"
				@click="quickQuery('配送范围')">
				配送范围
			</button>
		</view>

		<!-- 输入区域 -->
		<view class="input-area">
			<view class="input-container">
				<input
					class="message-input"
					v-model="inputText"
					placeholder="请输入您的问题..."
					@confirm="sendMessage"
					confirm-type="send" />
				<button
					class="send-btn"
					@click="sendMessage"
					:disabled="!inputText.trim()">
					发送
				</button>
			</view>
		</view>

		<!-- 连接状态提示 -->
		<view
			v-if="!isConnected"
			class="connection-status">
			<text>正在连接AI助手...</text>
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

<script setup lang="ts">
	import { ref, onMounted, onUnmounted } from "vue"
	import { onShow } from "@dcloudio/uni-app"
	import { checkLogin } from "@/core/app"
	import CardPopup from "@/components/card-popup/index.vue"
	import { DishVO } from "@/api/dish"
	import CustomerServiceAPI, {
		AiStreamChatResponseVO,
		AiChatDTO,
	} from "@/api/customer-service"
	import { useTowxml } from "@/wxcomponents/towxml/index"
	import DishFlavorModel from "@/pages/index/components/DishFlavorModel.vue"
	import { useShoppingCartStore } from "@/store"
	// 响应式数据
	const messageList = ref<any[]>([])
	const inputText = ref("")
	const scrollTop = ref(0) // 初始值设为0，与客服管理页面一致
	const isConnected = ref(false)
	const sessionId = ref("")
	//气泡显示
	const isBubble = ref(false)
	//是否正在输入
	const isStream = ref(false)
	const showCartPopup = ref(false)
	const closeCartPopup = () => {
		showCartPopup.value = false
	}
	//购物车store
	const shoppingCartStore = useShoppingCartStore() //mkdown解析
	//参数事件：1003
	const aiParamsObject = ref<any>(null)

	const onOpenCart = () => {
		showCartPopup.value = true
	}
	const useMarkdown = (value: string) => useTowxml(value || "", "markdown", {})
	// 获取真实的用户信息
	const getUserInfo = () => {
		try {
			// 从本地存储获取用户信息
			const localUserInfo = uni.getStorageSync("userInfo")
			// console.log('获取到的用户信息:', localUserInfo)

			if (localUserInfo && typeof localUserInfo === "object") {
				return {
					name: localUserInfo.name || "用户",
					avatar: localUserInfo.avatar || "",
				}
			}
		} catch (error) {
			console.error("获取本地用户信息失败:", error)
		}

		// 默认值
		return {
			name: "用户",
			avatar: "",
		}
	}

	const userInfo = ref(getUserInfo())

	// 从本地存储加载消息
	const loadMessagesFromStorage = () => {
		try {
			const storageKey = `ai_assistant_messages_${sessionId.value}`
			const storedMessages = uni.getStorageSync(storageKey)

			if (storedMessages) {
				const messages = JSON.parse(storedMessages)
				if (Array.isArray(messages) && messages.length > 0) {
					const filteredMessages = messages.filter(msg => msg != null)
					// 确保messageList始终是数组
					if (!Array.isArray(messageList.value)) {
						messageList.value = []
					}
					messageList.value = filteredMessages

					// 加载消息后滚动到底部 - 与客服页面保持一致
					scrollToBottom()
					setTimeout(scrollToBottom, 100)
					setTimeout(scrollToBottom, 300)

					return true
				}
			}
			// console.log("本地存储中没有AI助手消息")
		} catch (error) {
			console.error("从本地存储加载AI助手消息失败:", error)
		}
		return false
	}

	//处理用户的消息内容
	const getMessageQuestion = (content: string) => {
		const index = content.indexOf("\n\nAnswer using the following information:")
		let newContent = index !== -1 ? content.substring(0, index) : content
		// console.log("处理完毕的内容:", newContent)
		return newContent
	}

	// WebSocket相关
	let websocket: UniApp.SocketTask | null = null

	// 页面加载时初始化
	onMounted(async () => {
		// console.log("aiParamsObject", aiParamsObject.value)

		// 确保messageList正确初始化
		if (!messageList.value || !Array.isArray(messageList.value)) {
			messageList.value = []
		}

		// 更新用户信息
		userInfo.value = getUserInfo()
		// console.log("AI助手页面用户信息:", userInfo.value)

		// 先初始化AI助手，等消息加载完成后再滚动
		await initAIAssistant()

		// 消息加载完成后，延迟滚动到底部
		setTimeout(() => {
			scrollToBottom()
		}, 500)

		setTimeout(() => {
			scrollToBottom()
		}, 1000)

		setTimeout(() => {
			scrollToBottom()
		}, 2000)
	})

	// 页面显示时的处理
	onShow(() => {
		// console.log("AI助手页面显示 - 自动滚动到最新消息")
		// 延迟滚动，确保页面完全渲染
		setTimeout(() => {
			scrollToBottom()
		}, 300)

		setTimeout(() => {
			scrollToBottom()
		}, 800)

		setTimeout(() => {
			scrollToBottom()
		}, 1500)

		//加载购物车数据
		shoppingCartStore.getCartlist()
	})

	// 页面卸载时清理
	onUnmounted(() => {
		if (websocket) {
			websocket.close({})
		}
	})

	// 初始化AI助手
	const initAIAssistant = async () => {
		try {
			// 1. 创建或获取AI会话
			const sessionResult = await createAISession()
			// console.log("会话创建结果:", sessionResult)

			const sessionData = sessionResult?.data || sessionResult
			if (sessionData && sessionData.sessionId) {
				sessionId.value = sessionData.sessionId
				// console.log("设置sessionId:", sessionId.value)

				// 2. 连接WebSocket
				connectWebSocket()

				// 3. 先从本地存储加载消息
				const hasStoredMessages = loadMessagesFromStorage()

				// 4. 等待服务器历史消息加载完成
				await loadHistoryMessages()

				// 5. 如果没有本地消息，发送欢迎消息
				if (!hasStoredMessages) {
					// console.log(111);

					addAIMessage(
						"您好！我是AI智能助手，可以帮您查询订单、推荐菜品等。请问有什么可以帮助您的吗？"
					)
				}

				// 6. 初始化完成，消息已加载
				console.log("AI助手初始化完成，消息数量:", messageList.value)
			} else {
				console.error("会话创建失败，sessionResult:", sessionResult)
				uni.showToast({
					title: "创建AI会话失败",
					icon: "error",
				})
			}
		} catch (error) {
			console.error("初始化AI助手失败:", error)
			uni.showToast({
				title: "连接AI助手失败",
				icon: "error",
			})
		}
	}

	// 创建AI会话
	const createAISession = async () => {
		try {
			// console.log("开始创建AI会话...")
			const result = await CustomerServiceAPI.createAISession({ load: false })
			// console.log("AI会话创建成功:", result)
			return result
		} catch (error) {
			console.error("创建AI会话失败:", error)
			uni.showToast({
				title: "创建AI会话失败",
				icon: "error",
			})
			throw error
		}
	}

	// 连接WebSocket
	const connectWebSocket = () => {
		const wsUrl = `ws://localhost:8848/ws/customer-service/${sessionId.value}/user`

		websocket = uni.connectSocket({
			url: wsUrl,
			success: () => {
				console.log("AI WebSocket连接成功")
			},
			fail: error => {
				console.error("AI WebSocket连接失败:", error)
			},
		})

		// WebSocket事件监听
		websocket.onOpen(() => {
			// console.log("AI WebSocket连接已打开")
			isConnected.value = true
		})

		websocket.onMessage(res => {
			// console.log("收到AI WebSocket消息:", res.data)
			try {
				const message = JSON.parse(res.data)
				handleWebSocketMessage(message)
			} catch (error) {
				console.error("解析AI WebSocket消息失败:", error)
			}
		})

		websocket.onClose(() => {
			console.log("AI WebSocket连接已关闭")
			isConnected.value = false
		})

		websocket.onError(error => {
			console.error("AI WebSocket错误:", error)
			isConnected.value = false
		})
	}

	// 处理WebSocket消息
	const handleWebSocketMessage = (message: any) => {
		if (!messageList.value || !Array.isArray(messageList.value)) {
			console.error("messageList未正确初始化")
			messageList.value = []
		}

		if (message.type === "system") {
			addSystemMessage(message.message)
		} else if (message.type === "message") {
			messageList.value.push({
				senderType: message.senderType,
				content: message.content,
				timestamp: message.timestamp,
			})

			// 收到新消息后滚动到底部 - 与客服页面保持一致
			scrollToBottom()
			setTimeout(scrollToBottom, 100)
			setTimeout(scrollToBottom, 300)
		} else if (message.type === "error") {
			addSystemMessage(`错误: ${message.message}`)
		}
	}

	// 加载历史消息
	const loadHistoryMessages = async () => {
		try {
			const response = await CustomerServiceAPI.getSessionMessages(
				sessionId.value,
				2, // 2表示AI助手消息
				{ load: false }
			)
			const messages = response?.data || response || []
			messageList.value = Array.isArray(messages) ? messages : []
			console.log("加载历史消息完成，消息数量:", messageList.value)

			// 历史消息加载完成后滚动到底部 - 与客服页面保持一致
			if (messageList.value.length > 0) {
				scrollToBottom()
				setTimeout(scrollToBottom, 100)
				setTimeout(scrollToBottom, 300)
			}

			// console.log("历史消息加载完成并滚动到底部")
		} catch (error) {
			console.error("加载历史消息失败:", error)
		}
	}

	// 通用快捷查询
	const quickQuery = (type: string) => {
		const queries: Record<string, string> = {
			推荐菜品: "有什么好吃的菜品推荐吗？",
			查询订单: "帮我查询一下最近的订单状态",
			营业时间: "请问你们的营业时间是什么时候？",
			联系方式: "请问怎么联系你们的客服？",
			当前天气: "广州当天天气怎么样，适合吃点什么？",
			配送范围: "你们的配送范围是哪些地区？",
		}
		try {
			if (isStream.value) return
			inputText.value = queries[type] || ""
			sendMessage()
		} catch (error) {
			console.error("快捷操作失败:", error)
			isBubble.value = false
			isStream.value = false
			addAIMessage("抱歉，快捷操作时出现了问题，请稍后再试。")
		}
	}

	// 发送消息
	const sendMessage = async () => {
		const content = inputText.value.trim()
		if (!content) return
		if (isStream.value) return // 聊天中不允许发送消息

		// 检查sessionId是否存在
		if (!sessionId.value) {
			console.error("sessionId为空，无法发送消息")
			uni.showToast({
				title: "AI会话未初始化",
				icon: "error",
			})
			return
		}
		isStream.value = true

		// 添加用户消息
		addUserMessage(content)

		// 显示AI正在输入的气泡
		isBubble.value = true

		// 滚动到底部显示加载气泡
		setTimeout(() => {
			// console.log("显示AI正在输入气泡，滚动到底部")
			scrollToBottom()
		}, 150)

		// 清空输入框
		inputText.value = ""

		try {
			// 调用AI聊天API
			const chatData: AiChatDTO = {
				sessionId: sessionId.value,
				message: content,
				messageType: 1,
			}
			// 创建AI消息对象
			const aiMessage = {
				senderType: 3, // AI
				content: "",
				timestamp: Date.now(),
				isTyping: true, //表示是否正在输入
			}
			messageList.value.push(aiMessage)
			await startWeixinStream(chatData, aiMessage)
		} catch (error) {
			console.error("AI聊天失败:", error)
			// 隐藏AI正在输入的气泡
			isBubble.value = false
			isStream.value = false
			addAIMessage("抱歉，服务暂时不可用，请稍后再试。")
		}
	}

	let requestTask = null

	function startWeixinStream(chatData: AiChatDTO, aiMessage: any) {
		requestTask = uni.request({
			url: `http://localhost:8848/client/ai-assistant/chat?sessionId=${chatData.sessionId}&message=${chatData.message}`,
			method: "POST",
			header: {
				authentication: uni.getStorageSync("token"),
			},
			data: chatData,
			enableChunked: true, // 开启分块接收
			responseType: "arraybuffer",
			success: () => {
				// 整个请求结束，如果是正常结束，[DONE]信号会处理
				// console.log("请求结束");
			},
			fail: err => {
				console.log(err)
			},
		})
		// 监听接收到的分块数据
		requestTask.onChunkReceived(async res => {
			const arrayBuffer = res.data
			const uint8Array = new Uint8Array(arrayBuffer)
			// 将 ArrayBuffer 转换为字符串
			const textChunk = new TextDecoder().decode(uint8Array)
			// 处理SSE流式数据
			processSSEStream(aiMessage, textChunk as unknown as string)
		})
	}

	// 处理SSE流式数据
	const processSSEStream = async (aiMessage: any, sseData: string) => {
		// 用于存储不完整的消息片段
		let sseBuffer = ""
		// 将新数据与缓冲区中的数据合并
		sseBuffer += sseData

		// 分割成行
		const lines = sseBuffer.split("\n")
		// 保存最后一个可能不完整的行到缓冲区
		sseBuffer = lines.pop() || ""
		for (const line of lines) {
			if (line.startsWith("data:")) {
				try {
					const jsonData = line.substring(5).trim() // 移除 'data:' 前缀
					if (jsonData && jsonData !== "") {
						const eventData: AiStreamChatResponseVO = JSON.parse(jsonData)
						// 处理不同类型的消息
						if (eventData.eventType === 1001) {
							// 内容消息
							if (eventData.eventData) {
								// console.log("AI助手消息流式传输中：", eventData.eventData)
								//关闭loading
								isBubble.value = false
								//渲染数据
								// 使用打字机效果渲染数据
								await typewriterEffectOptimized(aiMessage, eventData.eventData)
							}
						} else if (eventData.eventType === 1003) {
							console.log("接收到参数事件：", eventData.eventData)
							aiParamsObject.value = eventData.eventData
							aiMessage.params = eventData.eventData
						} else if (eventData.eventType === 1002) {
							// console.log("AI助手消息流式传输完成：", eventData.eventData)
							// 结束消息 - 确保所有内容都已显示
							if (aiMessage._typewriterContent) {
								aiMessage.content = aiMessage._typewriterContent
								aiMessage._typewriterContent = ""
							}
							aiMessage._isTypewriting = false // 打字机效果结束
							await nextTick()
							scrollToBottom()
							break
						}
					}
				} catch (parseError) {
					console.error("解析SSE数据行失败:", parseError)
				}
			}
		}
	}

	// 打字机效果 - deepSeek深度思考生成
	const isTypewriter = ref(false)
	const typewriterEffectOptimized = async (
		assistantMessage: any,
		newContent: string
	) => {
		// 初始化
		if (!assistantMessage._typewriterBuffer) {
			//初始化缓存冲
			assistantMessage._typewriterBuffer = ""
			// 标记打字机正在运行
			assistantMessage._isTypewriting = true
			isTypewriter.value = true
		}

		// 将需要渲染的内容添加到缓冲区
		assistantMessage._typewriterBuffer += newContent
		// console.log("打字机新增缓冲区:", assistantMessage._typewriterBuffer);

		// 如果已经有打字机在运行，则不需要启动新的
		if (assistantMessage._typewriterInterval) {
			return
		}

		const typeSpeed = 50 // 更快的速度
		const chunkSize = 3 // 每次显示2个字符，平衡流畅性和性能

		assistantMessage._typewriterInterval = setInterval(async () => {
			if (assistantMessage._typewriterBuffer.length > 0) {
				// 每次取出一小块内容显示
				const chunk = assistantMessage._typewriterBuffer.substring(0, chunkSize)
				// console.log("打字机显示chunk:", chunk);
				//更新缓冲区，移除已取出的内容
				assistantMessage._typewriterBuffer =
					assistantMessage._typewriterBuffer.substring(chunkSize)
				// console.log("打字机剩余缓冲区:", assistantMessage._typewriterBuffer);
				// 更新显示内容
				if (!assistantMessage.content) {
					assistantMessage.content = ""
				}
				assistantMessage.content += chunk

				await nextTick()
				scrollToBottom()
			} else {
				// 缓冲区为空，停止打字机
				assistantMessage.isTyping = false // 标记为已完成
				isTypewriter.value = false
				isStream.value = false
				aiParamsObject.value = null
				clearInterval(assistantMessage._typewriterInterval)
				assistantMessage._typewriterInterval = null
				assistantMessage._isTypewriting = false
			}
		}, typeSpeed)
	}

	// 添加用户消息
	const addUserMessage = (content: string) => {
		if (!messageList.value || !Array.isArray(messageList.value)) {
			console.error("messageList未正确初始化")
			messageList.value = []
		}

		messageList.value.push({
			senderType: 1, // 用户
			content: content,
			timestamp: Date.now(),
		})

		// 发送消息后滚动到底部 - 与客服页面保持一致
		scrollToBottom()
		setTimeout(scrollToBottom, 100)
		setTimeout(scrollToBottom, 300)
	}

	// 修改 addAIMessage 函数
	const addAIMessage = (content: string) => {
		if (!messageList.value || !Array.isArray(messageList.value)) {
			console.error("messageList未正确初始化")
			messageList.value = []
		}
		messageList.value.push({
			senderType: 3, // AI
			content: content,
			timestamp: Date.now(),
			isTyping: false,
			initMessage: true,
		})
	}

	// 添加系统消息
	const addSystemMessage = (content: string) => {
		if (!messageList.value || !Array.isArray(messageList.value)) {
			console.error("messageList未正确初始化")
			messageList.value = []
		}

		messageList.value.push({
			type: "system",
			content: content,
			timestamp: Date.now(),
		})

		// 添加系统消息后滚动到底部 - 与客服页面保持一致
		scrollToBottom()
		setTimeout(scrollToBottom, 100)
		setTimeout(scrollToBottom, 300)
	}

	// 滚动到底部 - 使用多种方法确保滚动成功
	const scrollToBottom = () => {
		// 方法1: 使用scroll-view的scrollTop
		scrollTop.value = 99999

		// 方法2: 使用uni.pageScrollTo API作为备选
		uni.pageScrollTo({
			scrollTop: 999999,
			duration: 0,
		})

		// 立即再次设置，确保滚动生效
		setTimeout(() => {
			scrollTop.value = scrollTop.value === 99999 ? 99998 : 99999
			// 再次调用uni.pageScrollTo
			uni.pageScrollTo({
				scrollTop: 999999,
				duration: 0,
			})
		}, 10)
	}

	//点击菜品进入详情页
	function handleDishClick(dish: DishVO) {
		// console.log("点击菜品进入详情页", dish)
		dish.type = 1
		// 将整个dish对象序列化后传递
		const dishParam = encodeURIComponent(JSON.stringify(dish))
		uni.navigateTo({
			url: `/pages/index/components/DishDetail?dish=${dishParam}`,
		})
	}
	const dishFlavorModel = reactive({
		title: "",
	})
	const JDishFlavorModel = ref<InstanceType<typeof DishFlavorModel>>()
	//选择规格
	const handleDishFlavors = (dish: DishVO) => {
		dishFlavorModel.title = dish.name || "选择菜品口味"
		JDishFlavorModel.value?.show(dish)
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
</script>

<style lang="scss" scoped>
	.ai-assistant-page {
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
		align-items: flex-end;
		padding-right: 60rpx;
		/* 增加右边距，防止头像被右侧按钮遮住 */

		.user-content {
			background-color: #d81e06;
			color: white;
			margin-right: 16rpx;
		}

		.user-avatar {
			background-color: #d81e06;
		}
	}

	.ai-message {
		display: flex;
		justify-content: flex-start;
		align-items: flex-end;

		.ai-content {
			max-width: 600rpx;
			color: #333;
			border-radius: 16rpx;
			font-size: 28rpx;
			background-color: #ffffff;
			margin-left: 16rpx;

			.recommend-list {
				display: flex;
				// position: relative;
				flex-wrap: wrap;
				padding: 0;
				width: 100%; /* 确保容器宽度占满 */
				box-sizing: border-box;
				justify-content: center; /* 关键：让所有项水平居中 */

				.recommend-item {
					flex: 0 0 calc(50% - 10rpx);
					min-width: 200rpx;
					max-width: 250rpx;
					margin: 5rpx 25rpx;
					padding: 20rpx;
					box-sizing: border-box;
					background-color: #fff;
					border-radius: 10rpx;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);

					&:nth-child(odd) {
						margin-left: 0;
					}

					&:nth-child(even) {
						margin-right: 0;
					}

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
							font-size: 25rpx;
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
								font-size: 25rpx;
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
								position: relative;
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

		.ai-avatar {
			background-color: #007aff;
		}
	}

	.message-content {
		max-width: 500rpx;
		padding: 20rpx;
		border-radius: 16rpx;
		font-size: 28rpx;
		line-height: 1.4;
		word-wrap: break-word;
		white-space: pre-line;
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
		/* 确保头像图片不会溢出圆形边界 */
	}

	.avatar-image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		object-fit: cover;
		/* 保持图片比例并填充整个容器 */
	}

	.avatar-text {
		font-weight: bold;
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

	/* AI正在输入的气泡样式 */
	.typing-bubble {
		background: #f0f0f0 !important;
		border-radius: 18rpx !important;
		padding: 20rpx 30rpx !important;
		min-height: 60rpx !important;
		display: flex !important;
		align-items: center !important;
		justify-content: center !important;
	}

	.typing-indicator {
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.typing-dot {
		width: 12rpx;
		height: 12rpx;
		background: #999;
		border-radius: 50%;
		animation: typing-bounce 1.4s infinite ease-in-out;
	}

	.typing-dot:nth-child(1) {
		animation-delay: -0.32s;
	}

	.typing-dot:nth-child(2) {
		animation-delay: -0.16s;
	}

	.typing-dot:nth-child(3) {
		animation-delay: 0s;
	}

	@keyframes typing-bounce {
		0%,
		80%,
		100% {
			transform: scale(0.8);
			opacity: 0.5;
		}

		40% {
			transform: scale(1);
			opacity: 1;
		}
	}
</style>
