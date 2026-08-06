package com.fortunate.mapper;

import com.fortunate.entity.Sensitive;
import com.fortunate.query.SensitivePageQueryDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SensitiveMapper {

    /**
     * 插入
     * @param sensitive
     * @return
     */
    @Insert("insert into `sensitive`(`sensitives`) values (#{sensitive})")
    int insert(String sensitive);

    /**
     * 更新
     * @param sensitive
     * @return
     */
    int update(Sensitive sensitive);

    /**
     * 分页查询
     * @param dto
     * @return
     */
    Page<Sensitive> pageQuery(SensitivePageQueryDTO dto);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    int batchDeleteByIds(List<Long> ids);
}
