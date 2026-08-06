package com.fortunate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class KnowledgeBaseRedis {
    /**
     * 主键
     */
    private Integer id;
    
    /**
     * 知识库主键ID
     */
    private Integer knowledgeBaseId;
    
    /**
     * 关联的Redis向量ID
     */
    private String redisId;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;
}