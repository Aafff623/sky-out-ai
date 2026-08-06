package com.fortunate.service;

import com.fortunate.entity.AddressBook;

import java.util.List;

/**
 * @description
 */
public interface AddressBookService {
  
  /**
   * 查询地址簿列表
   * @param addressBook 条件查询参数
   * @return 地址簿列表
   */
  List<AddressBook> list(AddressBook addressBook);
  
  /**
   * 新增地址
   * @param addressBook 地址簿对象
   */
  void save(AddressBook addressBook);
  
  /**
   * 根据id查询地址簿
   * @param id 地址簿id
   * @return 地址簿对象
   */
  AddressBook getById(Long id);
  
  /**
   * 更新地址簿
   * @param addressBook 地址簿对象
   */
  void update(AddressBook addressBook);
  
  /**
   * 设置默认地址簿
   * @param addressBook 地址簿对象
   */
  void setDefault(AddressBook addressBook);
  
  /**
   * 根据地址簿id删除地址簿
   * @param id 地址簿id
   */
  void deleteById(Long id);
}
