package com.fortunate.service.impl;

import com.fortunate.entity.KnowledgeBase;
import com.fortunate.entity.KnowledgeBaseRedis;
import com.fortunate.exception.BaseException;
import com.fortunate.exception.DishExecption;
import com.fortunate.mapper.KnowledgeBaseMapper;
import com.fortunate.mapper.KnowledgeBaseRedisMapper;
import com.fortunate.query.KnowledgeBasePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.service.FileService;
import com.fortunate.service.KnowledgeBaseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.MyEmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.MyIngestionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fortunate.constant.MessageConstant.FILE_EXISTS;
import static com.fortunate.constant.MessageConstant.INVALID_PARAM;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    private final FileService fileService;
    private final EmbeddingModel embeddingModel;
    /** do=Redis；local=InMemory */
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBaseRedisMapper knowledgeBaseRedisMapper;
    private static final String OK = "ok";
    private static final String ERROR = "error";

    /**
     * 新增知识库
     *
     * @param file
     * @return
     */
    @Override
    @Transactional
    public String addKnowledgeBase(MultipartFile file) throws Exception {
        //获取原始名称
        String originalFilename = file.getOriginalFilename();
        KnowledgeBase dbKnowledgeBase = knowledgeBaseMapper.selectByFileName(originalFilename);
        //判断文件是否存在
        if (dbKnowledgeBase != null) {
            throw new DishExecption(FILE_EXISTS);
        }
        //先把文件上传到minio
        String url = fileService.uploadMinio(file);
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        Document document = null;
        //判断文件类型 使用不同的解析器
        if (".pdf".equals(suffix)) {
            document = UrlDocumentLoader.load(url, new ApachePdfBoxDocumentParser());
        } else if (".txt".equals(suffix)) {
            document = UrlDocumentLoader.load(url, new TextDocumentParser());
        } else {
            //使用通用类型解析
            document = UrlDocumentLoader.load(url, new ApacheTikaDocumentParser());
        }


        document.metadata().put("file_name", originalFilename);
        //构建通用文本分割器
        DocumentSplitter recursive = DocumentSplitters.recursive(300, 100);

        //构建文本向量存储器
        MyEmbeddingStoreIngestor ingestor = MyEmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(recursive)
                .build();
        //向量存储
        MyIngestionResult ingest = null;
        try {
            ingest = ingestor.ingest(document);
            //获取存储在redis中的id
            List<String> ids = ingest.getIds();

            //构建知识库对象
            KnowledgeBase knowledgeBase = KnowledgeBase.builder()
                    .fileName(originalFilename)
                    .url(url)
                    .createdTime(new Date())
                    .build();
            //插入知识库
            knowledgeBaseMapper.insert(knowledgeBase);
            //获取返回的id
            Integer id = knowledgeBase.getId();
            if (id != null) {
                log.info("新增知识库成功");
                //往知识库向量关联表插入关联的redis_id
                List<KnowledgeBaseRedis> list = new ArrayList<>(ids.size());
                for (String redisId : ids) {
                    //构建存储对象
                    KnowledgeBaseRedis baseRedis = KnowledgeBaseRedis.builder()
                            .knowledgeBaseId(id)
                            .redisId(redisId)
                            .createdTime(new Date())
                            .build();
                    //添加到集合
                    list.add(baseRedis);
                }
                //批量插入关联数据
                if (!list.isEmpty()) {
                    knowledgeBaseRedisMapper.insertBatch(list);
                    log.info("向量关联表插入成功");
                    return OK;
                }
                //清理minio 文件
                fileService.deleteFileMinio(url);
                throw new BaseException("向量存储失败");
            }
        } catch (Exception e) {
            log.error("向量存储失败");
            e.printStackTrace();
            //清理minio 文件
            fileService.deleteFileMinio(url);
            throw new BaseException(e.getMessage());
        }
        log.error("新增知识库失败");
        return ERROR;
    }

    /**
     * 分页查询知识库
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult page(KnowledgeBasePageQueryDTO dto) {
        if (dto == null) {
            throw new BaseException(INVALID_PARAM);
        }

        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<KnowledgeBase> knowledgeBases = knowledgeBaseMapper.pageQuery(dto);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(knowledgeBases.getTotal());
        pageResult.setRecords(knowledgeBases);
        return pageResult;
    }

    /**
     * 批量除知识库
     *
     * @param ids
     */
    @Override
    @Transactional
    public int deleteByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new DishExecption(INVALID_PARAM);
        }
        //根据ids查询对应的知识库信息
        List<KnowledgeBase> knowledgeBaseList = knowledgeBaseMapper.selectByIdList(ids);

        //删除知识库
        try {
            int delete = knowledgeBaseMapper.deleteByIds(ids);
            //遍历知识库集合
            for (KnowledgeBase knowledgeBase : knowledgeBaseList) {
                //根据知识库id查询关联的redisIds
                List<String> redisIds = knowledgeBaseRedisMapper.selectRedisIdByKnowledgeBaseId(knowledgeBase.getId());
                //删除redis中的向量数据
                deleteRedisIds(redisIds);
                //删除minio中的文件
                fileService.deleteFileMinio(knowledgeBase.getUrl());
            }
            //批量删除关联表中的数据
            knowledgeBaseRedisMapper.batchDeleteRedisIdByKnowledgeBaseIds(ids);
            if (delete > 0) {
                log.info("删除知识库成功");
                return delete;
            }
        } catch (Exception e) {
            log.error("删除知识库失败");
            e.printStackTrace();
            throw new BaseException("删除知识库失败");
        }
        log.error("删除知识库失败");
        throw new BaseException("删除知识库失败");
    }

    /**
     * 删除redis中的向量数据
     *
     * @param redisIds
     */
    private void deleteRedisIds(List<String> redisIds) {
        for (String id : redisIds) {
            embeddingStore.remove(id);
        }
    }
}
