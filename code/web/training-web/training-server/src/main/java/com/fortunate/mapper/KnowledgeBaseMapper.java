package com.fortunate.mapper;

import com.fortunate.entity.KnowledgeBase;
import com.fortunate.query.KnowledgeBasePageQueryDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {
    /**
     * 新增知识库
     *
     * @param knowledgeBase
     * @return
     */
    int insert(KnowledgeBase knowledgeBase);

    /**
     * 根据文件名进行精准查询
     * @param fileName
     * @return
     */
    @Select("select * from knowledge_base where file_name = #{fileName}")
    KnowledgeBase selectByFileName(String fileName);

    /**
     * 分页查询
     *
     * @param dto
     * @return
     */
    Page<KnowledgeBase> pageQuery(KnowledgeBasePageQueryDTO dto);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Select("select * from knowledge_base where id = #{id}")
    KnowledgeBase selectById(Integer id);

    /**
     * 根据id批量查询
     *
     * @param ids
     * @return
     */
    List<KnowledgeBase> selectByIdList(List<Integer> ids);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    int deleteByIds(List<Integer> ids);
}
