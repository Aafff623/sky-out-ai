package com.fortunate.mapper;

import com.fortunate.entity.ItemView;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ItemViewMapper {

    /**
     * 插入浏览记录
     *
     * @param itemView
     */
    @Insert("INSERT INTO item_views(user_id,item_id, item_type, view_count) VALUES(#{userId},#{itemId}, #{itemType}, #{viewCount})")
    void insert(ItemView itemView);

    /**
     * 更新浏览次数
     *
     * @param itemView
     */
    @Update("UPDATE item_views SET view_count = view_count + 1 WHERE item_id = #{itemId} AND item_type = #{itemType} AND user_id = #{userId}")
    void updateViewCount(ItemView itemView);

    /**
     * 根据itemId和itemType查询浏览记录
     *
     * @param itemId
     * @param itemType
     * @return
     */
    @Select("SELECT * FROM item_views WHERE item_id = #{itemId} AND item_type = #{itemType} and user_id = #{userId}")
    ItemView selectByItemIdAndType(@Param("itemId") Long itemId, @Param("itemType") String itemType, @Param("userId") Long userId);

    /**
     * 根据id和用户id查询浏览记录
     *
     * @param id
     * @param userId
     * @return
     */
    @Select("SELECT * FROM item_views WHERE item_id = #{itemId} AND user_id = #{userId} AND item_type = #{type}")
    ItemView selectByItemIdAndUserIdAndType(@Param("itemId") Long itemId, @Param("userId") Long userId, @Param("type") String type);

    /**
     * 获取浏览次数最多的item
     *
     * @param itemType
     * @param limit
     * @return
     */
    @Select("SELECT * FROM item_views WHERE item_type = #{itemType} AND user_id = #{userId} ORDER BY view_count DESC LIMIT #{limit}")
    List<ItemView> selectTopItemsByViewCount(@Param("itemType") String itemType,@Param("userId") Long userId ,@Param("limit") int limit);
}