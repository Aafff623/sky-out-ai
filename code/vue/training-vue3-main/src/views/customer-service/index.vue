<template>
  <div class="customer-service-container">
    <!-- 左侧会话列表 -->
    <div class="session-list">
      <div class="session-header">
        <h3>客服会话</h3>
        <el-button type="primary" size="small" @click="refreshSessions">
          <el-icon>
            <Refresh />
          </el-icon>
          刷新
        </el-button>
      </div>

      <div class="session-items">
        <div
          v-for="session in sessionList"
          :key="session.sessionId"
          :class="[
            'session-item',
            { active: currentSessionId === session.sessionId },
          ]"
          @click="selectSession(session)"
        >
          <div class="session-avatar">
            <el-avatar :size="40" :src="session.userAvatar">
              {{ session.userName?.charAt(0) || "用" }}
            </el-avatar>
          </div>
          <div class="session-info">
            <div class="session-name">{{ session.userName || "匿名用户" }}</div>
            <div class="session-last-message">
              {{ session.lastMessage || "暂无消息" }}
            </div>
          </div>
          <div class="session-meta">
            <div class="session-time">
              {{ formatTime(session.lastMessageTime) }}
            </div>
            <div v-if="session.unreadCount > 0" class="unread-badge">
              {{ session.unreadCount }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <div v-if="!currentSessionId" class="empty-chat">
        <el-empty description="请选择一个会话开始聊天" />
      </div>

      <div v-else class="chat-container">
        <!-- 聊天头部 -->
        <div class="chat-header">
          <div class="user-info">
            <el-avatar :size="32" :src="currentSession?.userAvatar">
              {{ currentSession?.userName?.charAt(0) || "用" }}
            </el-avatar>
            <span class="user-name">{{
              currentSession?.userName || "匿名用户"
            }}</span>
          </div>
          <div class="chat-actions">
            <el-button size="small" @click="endCurrentSession"
              >结束会话</el-button
            >
          </div>
        </div>

        <!-- 消息列表 -->
        <div
          class="message-list"
          ref="messageListRef"
          style="height: 400px; overflow-y: auto"
        >
          <div
            v-for="(message, index) in messageList.filter(
              (msg: any) => msg != null
            )"
            :key="index"
            :class="['message-item', getMessageClass(message)]"
          >
            <div class="message-avatar">
              <el-avatar
                v-if="message.senderType === 1"
                :size="32"
                :src="getMessageAvatarUrl(message)"
              >
                {{ getMessageAvatar(message) }}
              </el-avatar>
              <el-avatar v-else :size="32" style="background-color: #909399">
                🎧
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text">{{ message?.content || "" }}</div>
              <div class="message-time">
                {{ formatTime(message?.createTime) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <div class="input-container">
            <el-input
              v-model="inputText"
              type="textarea"
              :rows="3"
              placeholder="请输入回复内容..."
              @keydown.ctrl.enter="sendMessage"
            />
            <div class="input-actions">
              <el-button
                type="primary"
                @click="sendMessage"
                :disabled="!inputText.trim()"
              >
                发送 (Ctrl+Enter)
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";

// 响应式数据
const sessionList = ref<any[]>([]);
const currentSessionId = ref("");
const messageList = ref<any[]>([]);
const inputText = ref("");
const messageListRef = ref<HTMLElement>();

// 管理员和用户信息
const adminInfo = ref({
  name: "管理员",
  avatar: "",
});

// WebSocket连接
let websocket: WebSocket | null = null;

// 计算属性
const currentSession = computed(() => {
  return sessionList.value.find(
    (session) => session.sessionId === currentSessionId.value
  );
});

// 页面加载时初始化
onMounted(() => {
  loadSessions();
  loadAdminInfo();
});

// 页面卸载时清理
onUnmounted(() => {
  if (websocket) {
    websocket.close();
  }
});

// 加载会话列表
const loadSessions = async () => {
  try {
    const response = await fetch(
      "http://localhost:8848/store/customer-service/sessions/active",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        mode: "cors",
      }
    );
    const result = await response.json();

    if (result.code === 200) {
      sessionList.value = result.data || [];
      console.log("加载会话列表:", sessionList.value);
    } else {
      ElMessage.error(result.msg || "加载会话列表失败");
    }
  } catch (error) {
    console.error("加载会话列表失败:", error);
    ElMessage.error("加载会话列表失败");
  }
};

// 刷新会话列表
const refreshSessions = () => {
  loadSessions();
};

// 选择会话
const selectSession = (session: any) => {
  currentSessionId.value = session.sessionId;
  loadSessionMessages(session.sessionId);
  connectWebSocket(session.sessionId);

  // 立即滚动到底部
  scrollToBottom();
  setTimeout(scrollToBottom, 100);
  setTimeout(scrollToBottom, 300);
};

// 加载会话消息
const loadSessionMessages = async (sessionId: string) => {
  try {
    const response = await fetch(
      `http://localhost:8848/store/customer-service/messages/${sessionId}?type=1`
    );
    const result = await response.json();

    console.log("加载消息响应:", result);

    if (result.code === 200) {
      // 过滤掉null值并确保数据结构正确
      const messages = Array.isArray(result.data)
        ? result.data.filter((msg: any) => msg != null)
        : [];
      messageList.value = messages;
      // 立即滚动到底部
      scrollToBottom();
      // 多次确保滚动
      setTimeout(scrollToBottom, 50);
      setTimeout(scrollToBottom, 200);
      setTimeout(scrollToBottom, 500);
    } else {
      ElMessage.error(result.msg || "加载消息失败");
      messageList.value = [];
    }
  } catch (error) {
    console.error("加载消息失败:", error);
    ElMessage.error("加载消息失败");
    messageList.value = [];
  }
};

// 连接WebSocket
const connectWebSocket = (sessionId: string) => {
  // 关闭之前的连接
  if (websocket) {
    websocket.close();
  }

  // 握手鉴权：携带商家端 token（localStorage key 见 enums/CacheEnum.ts）
  const token = localStorage.getItem("token");
  const wsUrl = `ws://localhost:8848/ws/customer-service/${sessionId}/staff?token=${token}`;
  websocket = new WebSocket(wsUrl);

  websocket.onopen = () => {
    console.log("客服WebSocket连接成功");
  };

  websocket.onmessage = (event) => {
    try {
      const message = JSON.parse(event.data);
      handleWebSocketMessage(message);
    } catch (error) {
      console.error("解析WebSocket消息失败:", error);
    }
  };

  websocket.onclose = () => {
    console.log("客服WebSocket连接关闭");
  };

  websocket.onerror = (error) => {
    console.error("客服WebSocket错误:", error);
  };
};

// 处理WebSocket消息
const handleWebSocketMessage = (message: any) => {
  if (message.type === "message") {
    messageList.value.push({
      senderType: message.senderType,
      senderId: message.senderId,
      content: message.content,
      createTime: new Date(message.timestamp),
    });
    scrollToBottom();
    setTimeout(scrollToBottom, 50);
    setTimeout(scrollToBottom, 100);
  }
};

// 发送消息
const sendMessage = () => {
  const content = inputText.value.trim();
  if (!content || !websocket || !currentSessionId.value) return;

  // 添加到消息列表
  messageList.value.push({
    senderType: 2, // 客服
    senderId: 1, // TODO: 获取当前客服ID
    content: content,
    createTime: new Date(),
  });

  // 通过WebSocket发送消息
  websocket.send(
    JSON.stringify({
      content: content,
      messageType: 1, // 文本消息
    })
  );

  // 清空输入框
  inputText.value = "";

  // 滚动到底部
  scrollToBottom();
  setTimeout(scrollToBottom, 50);
  setTimeout(scrollToBottom, 100);
};

// 结束当前会话
const endCurrentSession = async () => {
  if (!currentSessionId.value) return;

  ElMessageBox.confirm(`确认结束当前会话操作？，是否继续?`, "结束会话", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const response = await fetch(
        `http://localhost:8848/store/customer-service/session/${currentSessionId.value}/end`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log("结束会话响应:", result);

      if (result.code === 200) {
        ElMessage.success("会话已结束");
        currentSessionId.value = "";
        messageList.value = [];
        if (websocket) {
          websocket.close();
          websocket = null;
        }
        loadSessions(); // 刷新会话列表
      } else {
        ElMessage.error(result.msg || "结束会话失败");
      }
    } catch (error) {
      console.error("结束会话失败:", error);
      ElMessage.error("结束会话失败");
    }
  });
};

// 获取消息样式类
const getMessageClass = (message: any) => {
  if (!message) return "staff-message";
  return message.senderType === 1 ? "user-message" : "staff-message";
};

// 获取消息头像
const getMessageAvatar = (message: any) => {
  if (!message) return "👨‍💼";
  if (message.senderType === 1) {
    // 用户消息，显示用户名首字母
    const userName = currentSession.value?.userName || "用户";
    return userName.charAt(0);
  } else {
    // 管理员消息，显示管理员名首字母
    return adminInfo.value.name.charAt(0);
  }
};

// 获取消息头像URL
const getMessageAvatarUrl = (message: any) => {
  if (!message) return "";
  if (message.senderType === 1) {
    // 用户头像
    return currentSession.value?.userAvatar || "";
  } else {
    // 管理员头像
    return adminInfo.value.avatar || "";
  }
};

// 格式化时间
const formatTime = (time: any) => {
  if (!time) return "";

  try {
    const date = new Date(time);
    if (isNaN(date.getTime())) return "";

    const now = new Date();
    const diff = now.getTime() - date.getTime();

    if (diff < 60000) {
      // 1分钟内
      return "刚刚";
    } else if (diff < 3600000) {
      // 1小时内
      return `${Math.floor(diff / 60000)}分钟前`;
    } else if (diff < 86400000) {
      // 1天内
      return `${Math.floor(diff / 3600000)}小时前`;
    } else {
      return date.toLocaleDateString();
    }
  } catch (error) {
    console.error("时间格式化错误:", error);
    return "";
  }
};

// 加载管理员信息
const loadAdminInfo = async () => {
  try {
    // 从用户store或API获取当前登录的管理员信息
    // const userStore = useUserStore()

    // 尝试从localStorage获取用户信息
    const token = localStorage.getItem("accessToken");
    if (token) {
      try {
        // 可以调用API获取当前用户信息
        const response = await fetch(
          "http://localhost:8848/store/employee/me",
          {
            headers: {
              token: token,
              "Content-Type": "application/json",
            },
          }
        );

        if (response.ok) {
          const result = await response.json();
          if (result.code === 200 && result.data) {
            adminInfo.value = {
              name: result.data.name || result.data.username || "管理员",
              avatar: result.data.avatar || "",
            };
            console.log("从API获取管理员信息:", adminInfo.value);
            return;
          }
        }
      } catch (apiError) {
        console.log("API获取管理员信息失败，使用默认值:", apiError);
      }
    }

    // 如果API调用失败，使用默认值
    adminInfo.value = {
      name: "管理员",
      avatar: "",
    };
  } catch (error) {
    console.error("获取管理员信息失败:", error);
    adminInfo.value = {
      name: "管理员",
      avatar: "",
    };
  }

  console.log("最终管理员信息:", adminInfo.value);
};

// 滚动到底部
const scrollToBottom = () => {
  if (messageListRef.value) {
    const element = messageListRef.value;
    // 强制滚动到底部
    element.scrollTop = element.scrollHeight + 1000;
    console.log(
      "管理端强制滚动到底部，scrollTop:",
      element.scrollTop,
      "scrollHeight:",
      element.scrollHeight
    );
  }
};
</script>

<style lang="scss" scoped>
.customer-service-container {
  display: flex;
  height: calc(100vh - 120px);
  background-color: #f5f5f5;
}

.session-list {
  width: 300px;
  background-color: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;

  .session-header {
    padding: 16px;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 16px;
      color: #303133;
    }
  }

  .session-items {
    flex: 1;
    overflow-y: auto;
  }

  .session-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    cursor: pointer;
    border-bottom: 1px solid #f0f0f0;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f5f7fa;
    }

    &.active {
      background-color: #ecf5ff;
    }

    .session-avatar {
      margin-right: 12px;
    }

    .session-info {
      flex: 1;
      min-width: 0;

      .session-name {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
        margin-bottom: 4px;
      }

      .session-last-message {
        font-size: 12px;
        color: #909399;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .session-meta {
      text-align: right;

      .session-time {
        font-size: 12px;
        color: #c0c4cc;
        margin-bottom: 4px;
      }

      .unread-badge {
        background-color: #f56c6c;
        color: white;
        font-size: 12px;
        padding: 2px 6px;
        border-radius: 10px;
        min-width: 16px;
        text-align: center;
      }
    }
  }
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;

  .empty-chat {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 600px;
    max-height: 600px;
  }

  .chat-header {
    padding: 16px;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .user-name {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }
    }
  }

  .message-list {
    height: 400px !important;
    padding: 16px;
    overflow-y: auto !important;
    background-color: #f8f9fa;
    border: 1px solid #e0e0e0;
    border-radius: 8px;

    .message-item {
      display: flex;
      margin-bottom: 16px;

      &.user-message {
        flex-direction: row;

        .message-content {
          background-color: #e4e7ed;
          margin-left: 8px;
        }
      }

      &.staff-message {
        flex-direction: row-reverse;

        .message-content {
          background-color: #409eff;
          color: white;
          margin-right: 8px;
        }
      }

      .message-avatar {
        flex-shrink: 0;
      }

      .message-content {
        max-width: 60%;
        padding: 8px 12px;
        border-radius: 8px;
        font-size: 14px;
        line-height: 1.4;

        .message-text {
          margin-bottom: 4px;
        }

        .message-time {
          font-size: 12px;
          opacity: 0.7;
        }
      }
    }
  }

  .input-area {
    padding: 16px;
    border-top: 1px solid #e4e7ed;
    background-color: white;

    .input-container {
      .input-actions {
        margin-top: 8px;
        text-align: right;
      }
    }
  }
}
</style>
