package com.fortunate.mapper;

import com.fortunate.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @description
 */
@Mapper
public interface AddressBookMapper {
  
  /**
   * 根据id查询地址簿
   * @param id 地址簿id
   * @return 地址簿实体类
   */
  @Select("select * from `address_book` where `id` = #{id}")
  AddressBook getById(@Param("id") Long id);
  
  /**
   * 查询地址簿列表
   * @param addressBook 地址簿实体类
   * @return 地址簿列表
   */
  List<AddressBook> list(AddressBook addressBook);
  
  /**
   * 新增地址簿
   * @param addressBook 地址簿实体类
   */
  @Insert("insert into `address_book` (`user_id`, `consignee`, `phone`, `sex`, `province_code`, `province_name`, " +
          "`city_code`, `city_name`, `district_code`, `district_name`, `detail`, `label`, `is_default`) values " +
          "(#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, " +
          "#{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
  void insert(AddressBook addressBook);
  
  /**
   * 更新地址簿
   * @param addressBook 地址簿实体类
   */
  void update(AddressBook addressBook);
  
  /**
   * 根据用户id更新默认地址簿
   * @param addressBook 地址簿实体类
   */
  @Update("update `address_book` set `is_default` = #{isDefault} where `user_id` = #{userId}")
  void updateIsDefaultByUserId(AddressBook addressBook);
  
  /**
   * 根据id删除地址簿
   * @param id 地址簿id
   */
  @Delete("delete from `address_book` where `id` = #{id}")
  void deleteById(@Param("id") Long id);
}
