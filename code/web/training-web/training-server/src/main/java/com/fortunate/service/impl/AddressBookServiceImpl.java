package com.fortunate.service.impl;

import com.fortunate.constant.StatusConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.entity.AddressBook;
import com.fortunate.mapper.AddressBookMapper;
import com.fortunate.service.AddressBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @description
 */
@Service
@RequiredArgsConstructor
public class AddressBookServiceImpl implements AddressBookService {
  
  private final AddressBookMapper addressBookMapper;
  
  @Override
  public List<AddressBook> list(AddressBook addressBook) {
    // TODO: 获取地址列表未使用分页，等待完善
    return addressBookMapper.list(addressBook);
  }

  /**
   *
   * @param addressBook 地址簿对象
   */
  @Override
  public void save(AddressBook addressBook) {
    addressBook.setUserId(BaseContext.getUserId());
    
    // 当前新增地址是否设置了为默认地址
    boolean isDefault = Optional.ofNullable(addressBook.getIsDefault())
            .map(StatusConstant.ENABLE::equals)
            .orElse(false);
  
    addressBook.setIsDefault(0);
    
    // 如果当前新增地址设置为默认地址，则将当前用户的所有地址修改为非默认地址
    if(isDefault) {
      addressBookMapper.updateIsDefaultByUserId(addressBook);
      // 设置当前地址为默认地址
      addressBook.setIsDefault(1);
    }
    addressBookMapper.insert(addressBook);
  }

  /**
   * 根据id查询地址详情
   * @param id 地址簿id
   * @return
   */
  @Override
  public AddressBook getById(Long id) {
    return addressBookMapper.getById(id);
  }

  /**
   * 修改地址
   * @param addressBook 地址簿对象
   */
  @Override
  public void update(AddressBook addressBook) {
    addressBookMapper.update(addressBook);
  }
  
  @Transactional
  @Override
  public void setDefault(AddressBook addressBook) {
    /* 1、将当前用户的所有地址修改为非默认地址 */
    addressBook.setIsDefault(0);
    addressBook.setUserId(BaseContext.getUserId());
    addressBookMapper.updateIsDefaultByUserId(addressBook);
  
    /* 2、将当前地址改为默认地址 */
    addressBook.setIsDefault(1);
    addressBookMapper.update(addressBook);
  }
  
  @Override
  public void deleteById(Long id) {
    addressBookMapper.deleteById(id);
  }
}
