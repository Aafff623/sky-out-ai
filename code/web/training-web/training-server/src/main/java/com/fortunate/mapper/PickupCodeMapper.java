package com.fortunate.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * @description
 */
@Mapper
public interface PickupCodeMapper {
  
  @Select("SELECT COUNT(1) FROM `pickup_code` WHERE `date` = #{date} AND `code` = #{code}")
  boolean exists(@Param("date") LocalDate date, @Param("code") String code);
  
  @Insert("INSERT INTO `pickup_code` (`date`, `code`) VALUES (#{date}, #{code})")
  int insert(@Param("date") LocalDate date, @Param("code") String code);
}
