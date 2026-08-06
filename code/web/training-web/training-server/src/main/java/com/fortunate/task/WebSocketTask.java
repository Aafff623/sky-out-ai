package com.fortunate.task;

import com.fortunate.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @description WebSocket定时任务
 */
@Component
@RequiredArgsConstructor
public class WebSocketTask {
  private final WebSocketServer webSocketServer;
  
  /**
   * 通过WebSocket每隔5秒向客户端发送消息
   */
  @Scheduled(cron = "0/5 * * * * ?")
  public void sendMessageToClient() {
    webSocketServer.sendToAllClient("这是来自服务端的消息：" + DateTimeFormatter.ofPattern("HH:mm:ss")
            .format(LocalDateTime.now()));
  }
}
