package com.fortunate.repository;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import lombok.Data;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RedisMessage {
    private String type;
    private Map<String, Object> metadata;
    private List<Media> media ;
    //因为原本的 ToolExecutionRequest 没有 构造方法只能自己创建
    private List<MyToolExecutionRequest> myToolExecutionRequests;
    private String textContent;
    private Map<String, Object> params = Map.of();

    // 添加默认无参构造函数
    public RedisMessage() {
        this.media = new ArrayList<>();
        this.myToolExecutionRequests = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.textContent = "";
        this.type = "";
    }
}