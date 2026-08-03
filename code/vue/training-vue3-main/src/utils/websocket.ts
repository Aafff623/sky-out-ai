export default class Socket {
  private url: WebSocket["url"];
  private ws: WebSocket | null = null;
  private opts: SocketOptions | null = null;
  // 重传次数
  private reconnectAttempts: SocketOptions["maxReconnectAttempts"] = 0;
  private listeners: {
    [K in SocketEvent]?: Array<(data: SocketEventMap[K]) => void>;
  } = {};
  // 心跳间隔
  private heartbeatInterval: SocketOptions["heartbeatInterval"] = undefined;

  constructor(opts: SocketOptions = {}) {
    this.url = opts.url || import.meta.env.VITE_APP_WS_URL + opts.clientId;
    this.opts = {
      heartbeatInterval: 30000,
      reconnectInterval: 5000,
      maxReconnectAttempts: 5,
      ...opts,
    };

    this.initSocket();
  }

  getSocketInstance() {
    return this.ws;
  }

  private initSocket() {
    this.ws = new WebSocket(this.url);
    this.ws.onopen = this.onOpen.bind(this);
    this.ws.onmessage = this.onMessage.bind(this);
    this.ws.onerror = this.onError.bind(this);
    this.ws.onclose = this.onClose.bind(this);
  }

  // WebSocket连接打开
  private onOpen(event: Event) {
    // console.log("WebSocket打开==>", event);
    this.reconnectAttempts = 0;
    this.startHeartbeat();
    this.emit("open", event);
  }

  // 收到的WebSocket消息
  private onMessage(event: MessageEvent) {
    // console.log('WebSocket message received:', event.data);
    this.emit("message", event.data);
  }

  // 连接发生错误
  private onError(event: Event) {
    // console.error("WebSocket error==>", event);
    this.emit("error", event);
  }

  // 重连逻辑中，在连接失败后自动重新连接
  private onClose(event: CloseEvent) {
    // console.log("WebSocket关闭==>", event);
    this.stopHeartbeat();
    this.emit("close", event);
    // 重连逻辑中，在连接失败后自动重新连接，但会限制重连的次数和每次重连之间的间隔时间
    if (
      this.opts?.maxReconnectAttempts !== 0 &&
      this.reconnectAttempts! < this.opts?.maxReconnectAttempts!
    ) {
      setTimeout(() => {
        this.reconnectAttempts!++;
        this.initSocket();
      }, this.opts?.reconnectInterval || 5000);
    }
  }

  // 开始心跳检测
  private startHeartbeat() {
    if (!this.opts?.heartbeatInterval) return;

    this.heartbeatInterval = window.setInterval(() => {
      if (this.ws?.readyState === WebSocket.OPEN) {
        this.ws.send("ping");
      }
    }, this.opts?.heartbeatInterval);
  }

  // 停止心跳检测
  private stopHeartbeat() {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval);
      this.heartbeatInterval = undefined;
    }
  }

  // 发送消息
  send(data: string) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(data);
    } else {
      console.error("WebSocket未打开，不能发送消息==>", data);
    }
  }

  // 事件监听器注册功能的实现
  on<K extends SocketEvent>(
    event: K,
    callback: (data: SocketEventMap[K]) => void
  ) {
    if (!this.listeners[event]) {
      this.listeners[event] = [];
    }

    this.listeners?.[event]?.push(callback);
  }

  // 从事件监听器中移除
  off<K extends SocketEvent>(event: K) {
    delete this.listeners[event];
  }

  // 关闭连接方法
  close() {
    if (this?.ws) {
      this.stopHeartbeat();
      this.ws?.close();
      this.ws = null;
    }
  }

  // 在事件监听器中触发一个指定的事件
  private emit<K extends SocketEvent>(event: K, data: SocketEventMap[K]) {
    const callbacks = this.listeners[event];
    callbacks?.forEach((callback) => callback(data));
  }
}
