package com.fortunate;

import com.fortunate.entity.Comment;
import com.fortunate.entity.CommentRepay;
import com.fortunate.entity.Dish;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class testMongoDB {
    @Autowired
    private MongoTemplate mongoTemplate;

//    @Test
//    void testUpdate() {
//        Integer authorId = 3;
//        String image = "https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/2d77c4a928c7c2b91406fd348cebbe1.jpg";
//        //查询所有
//        Query query = Query.query(Criteria.where("authorId").is(authorId));
//        List<CommentRepay> comments = mongoTemplate.find(query, CommentRepay.class);
//        for (CommentRepay comment : comments) {
//            comment.setAuthorAvatar(image);
//            mongoTemplate.save(comment);
//        }
//    }
}
