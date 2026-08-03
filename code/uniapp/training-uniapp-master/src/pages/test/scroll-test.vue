<template>
  <view class="scroll-test-page">
    <view class="header">
      <text>滚动测试页面</text>
      <button @click="addMessage">添加消息</button>
      <button @click="scrollToBottom">滚动到底部</button>
    </view>
    
    <scroll-view
      class="message-list"
      scroll-y
      :scroll-top="scrollTop"
      scroll-with-animation="true"
      :scroll-into-view="scrollIntoView"
      @scroll="onScroll"
    >
      <view class="message-container">
        <view class="message-item" v-for="(message, index) in messages" :key="index">
          <view class="message-content">
            <text>{{ message.content }}</text>
            <text class="timestamp">{{ formatTime(message.timestamp) }}</text>
          </view>
        </view>
        
        <!-- 底部锚点 -->
        <view id="bottom-anchor" class="bottom-anchor"></view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'

const messages = ref<any[]>([
  { content: '这是第一条消息', timestamp: Date.now() - 10000 },
  { content: '这是第二条消息', timestamp: Date.now() - 9000 },
  { content: '这是第三条消息', timestamp: Date.now() - 8000 },
  { content: '这是第四条消息', timestamp: Date.now() - 7000 },
  { content: '这是第五条消息', timestamp: Date.now() - 6000 },
])

const scrollTop = ref(999999)
const scrollIntoView = ref('')

// 添加消息
const addMessage = () => {
  messages.value.push({
    content: `新消息 ${messages.value.length + 1}`,
    timestamp: Date.now()
  })
  
  // 添加消息后滚动到底部
  nextTick(() => {
    scrollToBottom()
  })
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    // 方法1: 使用scroll-into-view
    scrollIntoView.value = 'bottom-anchor'
    
    // 方法2: 使用scrollTop
    scrollTop.value = Date.now() % 1000000 + 999999
    
    console.log('滚动到底部 - scrollIntoView:', scrollIntoView.value, 'scrollTop:', scrollTop.value)
    
    // 清除scroll-into-view
    setTimeout(() => {
      scrollIntoView.value = ''
    }, 100)
  })
}

// 处理滚动事件
const onScroll = (e: any) => {
  console.log('滚动事件:', e.detail)
}

// 格式化时间
const formatTime = (timestamp: number) => {
  return new Date(timestamp).toLocaleTimeString()
}

// 页面加载时滚动到底部
nextTick(() => {
  setTimeout(() => {
    scrollToBottom()
  }, 500)
})
</script>

<style lang="scss" scoped>
.scroll-test-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.header {
  padding: 20rpx;
  background-color: white;
  display: flex;
  gap: 20rpx;
  align-items: center;
  border-bottom: 1rpx solid #eee;
  
  button {
    padding: 10rpx 20rpx;
    background-color: #007aff;
    color: white;
    border: none;
    border-radius: 10rpx;
    font-size: 24rpx;
  }
}

.message-list {
  flex: 1;
  padding: 20rpx;
}

.message-container {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.message-item {
  margin-bottom: 20rpx;
}

.message-content {
  background-color: white;
  padding: 20rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  
  .timestamp {
    display: block;
    font-size: 20rpx;
    color: #999;
    margin-top: 10rpx;
  }
}

.bottom-anchor {
  height: 1px;
  width: 100%;
}
</style>
