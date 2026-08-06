package com.fortunate.mapper;

import com.fortunate.entity.KnowledgeBaseRedis;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KnowledgeBaseRedisMapper {

    /**
     * 批量插入关联数据
     *
     * @param knowledgeBaseRedis
     * @return
     */
    int insertBatch(List<KnowledgeBaseRedis> knowledgeBaseRedis);

    /**
     * 根据知识库id集合删除关联的redis向量id
     *
     * @return
     */
    List<String> selectRedisIdByKnowledgeBaseId(Integer knowledgeBaseIds);

    /**
     * 根据知识库id集合删除关联的redis向量id
     *
     * @return
     */
    int batchDeleteRedisIdByKnowledgeBaseIds(List<Integer> knowledgeBaseIds);
}
