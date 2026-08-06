package com.fortunate.service.impl;

import com.fortunate.entity.Sensitive;
import com.fortunate.exception.DeletionNotAllowedException;
import com.fortunate.mapper.SensitiveMapper;
import com.fortunate.query.SensitivePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.CommentService;
import com.fortunate.service.SensitiveService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.fortunate.constant.MessageConstant.INVALID_PARAM;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensitiveServiceImpl implements SensitiveService {
    private final SensitiveMapper sensitiveMapper;

    @Lazy
    @Autowired
    private CommentService commentService;

    /**
     * 新增敏感词
     *
     * @param sensitive
     */
    @Override
    public Result addSensitive(String sensitive) {
        int i = sensitiveMapper.insert(sensitive);
        if (i > 0) {
            //重新初始化敏感词
            commentService.initSensitive();
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 修改敏感词
     *
     * @param id
     * @param sensitive
     * @return
     */
    @Override
    public Result updateSensitive(Integer id, String sensitive) {
        Sensitive sensitiveBuilder = Sensitive.builder()
                .id(id)
                .sensitives(sensitive)
                .updatedTime(LocalDateTime.now())
                .build();

        int update = sensitiveMapper.update(sensitiveBuilder);
        if (update > 0) {
            //重新初始化敏感词
            commentService.initSensitive();
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 分页查询敏感词
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult page(SensitivePageQueryDTO dto) {
        //开启分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        //查询
        Page<Sensitive> sensitives = sensitiveMapper.pageQuery(dto);
        //封装返回数据
        PageResult pageResult = new PageResult();
        pageResult.setTotal(sensitives.getTotal());
        pageResult.setRecords(sensitives.getResult());
        return pageResult;
    }

    /**
     * 批量删除敏感词
     *
     * @param ids
     * @return
     */
    @Override
    public Result deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new DeletionNotAllowedException(INVALID_PARAM);
        }
        int i = sensitiveMapper.batchDeleteByIds(ids);
        if (i > 0) {
            //重新初始化敏感词
            commentService.initSensitive();
            return Result.success();
        }
        return Result.error();
    }
}
