package com.fortunate.mapper;

import com.fortunate.entity.CustomerServiceSession;
import com.fortunate.vo.CustomerServiceSessionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 客服会话Mapper
 */
@Mapper
public interface CustomerServiceSessionMapper {

    /**
     * 创建客服会话
     */
    @Insert("INSERT INTO customer_service_session (user_id, session_id, service_type, status, staff_id, create_time) " +
            "VALUES (#{userId}, #{sessionId}, #{serviceType}, #{status}, #{staffId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CustomerServiceSession session);

    /**
     * 根据会话ID查询会话
     */
    @Select("SELECT * FROM customer_service_session WHERE session_id = #{sessionId}")
    CustomerServiceSession getBySessionId(String sessionId);

    /**
     * 根据用户ID和服务类型查询进行中的会话
     */
    @Select("SELECT * FROM customer_service_session WHERE user_id = #{userId} AND service_type = #{serviceType} AND status = 1 ORDER BY create_time DESC LIMIT 1")
    CustomerServiceSession getActiveSessionByUserIdAndType(Long userId, Integer serviceType);

    /**
     * 更新会话状态
     */
    @Update("UPDATE customer_service_session SET status = #{status}, end_time = #{endTime} WHERE session_id = #{sessionId}")
    void updateStatus(String sessionId, Integer status, java.time.LocalDateTime endTime);

    /**
     * 分配客服人员
     */
    @Update("UPDATE customer_service_session SET staff_id = #{staffId} WHERE session_id = #{sessionId}")
    void assignStaff(String sessionId, Long staffId);

    /**
     * 查询客服人员的会话列表
     */
    @Select("SELECT css.*, u.name as userName, u.avatar as userAvatar, e.name as staffName " +
            "FROM customer_service_session css " +
            "LEFT JOIN user u ON css.user_id = u.id " +
            "LEFT JOIN employee e ON css.staff_id = e.id " +
            "WHERE css.staff_id = #{staffId} AND css.service_type = 1 " +
            "ORDER BY css.create_time DESC")
    List<CustomerServiceSessionVO> getSessionsByStaffId(Long staffId);

    /**
     * 查询所有进行中的人工客服会话
     */
    @Select("SELECT css.*, u.name as userName, u.avatar as userAvatar, e.name as staffName " +
            "FROM customer_service_session css " +
            "LEFT JOIN user u ON css.user_id = u.id " +
            "LEFT JOIN employee e ON css.staff_id = e.id " +
            "WHERE css.service_type = 1 AND css.status = 1 " +
            "ORDER BY css.create_time DESC")
    List<CustomerServiceSessionVO> getActiveHumanSessions();
}
