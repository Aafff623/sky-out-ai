declare global {
  /** websocket选项 */
  interface SocketOptions {
    /** 随机客户端id*/
    clientId?: string;
    /** 地址 */
    url?: WebSocket["url"];
    /** 心跳间隔（默认：30000，单位：ms，也就是30秒） */
    heartbeatInterval?: number;
    /** 超时重传（默认：5000，单位：ms，也就是5秒） */
    reconnectInterval?: number;
    /** 最大重传次数（默认：5次） */
    maxReconnectAttempts?: number;
  }

  /** 事件类型映射 */
  interface SocketEventMap {
    /** websocket 连接成功事件回调 */
    open: Event;
    /** websocket 接收到消息事件回调 */
    message: any;
    /** websocket 连接关闭事件回调 */
    error: Event;
    /** websocket 连接关闭事件回调 */
    close: CloseEvent;
  }

  /** WebSocket事件类型 */
  type SocketEvent = keyof SocketEventMap;
}

export {};
