import Socket from "@/utils/websocket";

export function useSocket(opts?: SocketOptions) {
  const socket = new Socket({
    ...opts,
    clientId: generateClientId(),
  });

  onUnmounted(() => {
    socket.off("open");
    socket.off("message");
    socket.off("error");
    socket.off("close");
    socket?.close(); // 关闭WebSocket连接
  });

  // 生成随机客户端ID
  function generateClientId() {
    return crypto.randomUUID();
  }

  return {
    socket,
    send: socket.send.bind(socket),
    on: socket.on.bind(socket),
    off: socket.off.bind(socket),
  };
}
