package com.fortunate.service;

import com.fortunate.query.KnowledgeBasePageQueryDTO;
import com.fortunate.result.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KnowledgeBaseService {

    /**
     * 新增知识库
     * @param file
     * @return
     */
    String addKnowledgeBase(MultipartFile file) throws Exception;

    /**
     * 分页查询知识库
     * @param dto
     * @return
     */
    PageResult page(KnowledgeBasePageQueryDTO dto);

    /**
     * 删除知识库
     * @param ids
     */
    int deleteByIds(List<Integer> ids);
}
