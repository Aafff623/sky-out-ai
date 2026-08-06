package com.fortunate.service.impl;

import com.fortunate.constant.MessageConstant;
import com.fortunate.constant.StatusConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.DishDTO;
import com.fortunate.dto.SetmealDTO;
import com.fortunate.entity.Comment;
import com.fortunate.entity.Dish;
import com.fortunate.entity.Setmeal;
import com.fortunate.entity.SetmealDish;
import com.fortunate.exception.DeletionNotAllowedException;
import com.fortunate.exception.SetmealEnableFailedException;
import com.fortunate.mapper.CategoryMapper;
import com.fortunate.mapper.DishMapper;
import com.fortunate.mapper.SetmealDishMapper;
import com.fortunate.mapper.SetmealMapper;
import com.fortunate.query.SetmealPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.service.SetmealService;
import com.fortunate.service.ViewService;
import com.fortunate.vo.DishItemVO;
import com.fortunate.vo.SetmealVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fortunate.constant.Constants.SETMEAL;

/**
 * @description 套餐服务实现类
 */
@Service
@RequiredArgsConstructor
public class SetmealServiceImpl implements SetmealService {
  
  private final SetmealMapper setmealMapper;
  private final SetmealDishMapper setmealDishMapper;
  private final DishMapper dishMapper;
  private final CategoryMapper categoryManager;
  private final ViewService viewService;
  private final MongoTemplate mongoTemplate;

  /**
   * 查询套餐
   * @param setmeal 套餐实体类
   * @return
   */
  @Override
  public List<Setmeal> list(Setmeal setmeal) {
    String categoryNameById = categoryManager.getCategoryNameById(setmeal.getCategoryId());
    List<Setmeal> list = setmealMapper.list(setmeal);
    for (Setmeal s : list) {
      s.setCategoryName(categoryNameById);
    }
    return list;
  }

  @Override
  public List<DishItemVO> getDishItemBySetmealId(Long id) {
    Long userId = BaseContext.getUserId();
    //更新浏览次数
    viewService.incrementViewCount(id, SETMEAL, userId);
    return setmealMapper.getDishItemBySetmealId(id);
  }

  /**
   * 新增套餐
   * @param setmealDTO 套餐dto类
   */
  @Transactional
  @Override
  public void saveWithDish(SetmealDTO setmealDTO) {
    Setmeal setmeal = new Setmeal();
    BeanUtils.copyProperties(setmealDTO, setmeal);
    
    // 1、先将套餐数据保存到`setmeal`套餐表中
    setmealMapper.insert(setmeal);
    
    // 2、获取生成的套餐id，并向`setmeal_dish`表中插入这个新增的套餐id和关联到的菜品数据
    Long setmealId = setmeal.getId();
    List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
    // 设置关联的套餐id
    setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
    
    // 3、批量保存套餐和菜品的关联关系到`setmeal_dish`表中
    setmealDishMapper.insertBatch(setmealDishes);
  }

  /**
   * 套餐分页查询
   * @param setmealPageQueryDTO 套餐分页查询dto类
   * @return
   */
  @Override
  public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
    int pageNum = setmealPageQueryDTO.getPage();
    int pageSize = setmealPageQueryDTO.getPageSize();
    
    PageHelper.startPage(pageNum, pageSize);
    
    Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
    
    return new PageResult(page.getTotal(), page.getResult());
  }

  /**
   * 批量删除套餐
   * @param ids 套餐id集合，以逗号分隔，例如：1,2,3
   */
  @Transactional
  @Override
  public void deleteBatch(List<Long> ids) {
    // 1、判断要删除的套餐是否处于起售状态，如果处于起售状态，则不能删除
    List<Setmeal> setmealList = setmealMapper.getSetmealByIds(ids);
    setmealList.forEach(setmeal -> {
      if (StatusConstant.ENABLE.equals(setmeal.getStatus())) {
        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
      }
    });
    
    // 2、根据套餐ids集合批量删除套餐和菜品的关联关系
    setmealDishMapper.deleteBySetmealIds(ids);
    // 3、根据套餐ids集合批量删除套餐
    setmealMapper.deleteBatchByIds(ids);
  }
  
  @Override
  public SetmealVO getSetmealByIdWithDish(Long id) {
    return setmealMapper.getSetmealByIdWithDish(id);
  }

  /**
   * 修改套餐
   * @param setmealDTO 套餐dto类
   */
  @Transactional
  @Override
  public void update(SetmealDTO setmealDTO) {
    Setmeal setmeal = new Setmeal();
    BeanUtils.copyProperties(setmealDTO, setmeal);
    
    // 1、首先更新套餐表中的数据
    setmealMapper.update(setmeal);
    
    // 2、通过套餐id删除套餐和菜品的关联关系（先删除再插入）
    Long setmealId = setmealDTO.getId();
    setmealDishMapper.deleteBySetmealId(setmealId);
    
    // 3、重新插入新的套餐和菜品的关联关系
    List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
    setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
    setmealDishMapper.insertBatch(setmealDishes);
  }
  
  @Override
  public void startOrStop(Long id, Integer status) {
    // 1、如果不是起售状态，直接修改套餐状态
    if (StatusConstant.DISABLE.equals(status)) {
      updateSetmealStatus(id, status);
      return;
    }
    
    // 2、没有菜品与这个套餐进行关联，直接更新套餐状态
    List<Dish> dishList = dishMapper.getBySetmealId(id);
    if (dishList == null || dishList.isEmpty()) {
      updateSetmealStatus(id, status);
      return;
    }
    
    // 3、存在菜品与这个套餐关联，判断所有菜品的起售状态是否都为1
    boolean allDishesEnabled = dishList.stream()
            .allMatch(dish -> StatusConstant.ENABLE.equals(dish.getStatus()));
    if (!allDishesEnabled) {
      throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
    }
    
    // 4、所有菜品的起售状态都为1，更新套餐状态
    updateSetmealStatus(id, status);
    
    
    // // 1、起售一个套餐，需要这个套餐里面的所有菜品的起售状态都为1，否则无法起售套餐
    // if (StatusConstant.ENABLE.equals(status)) {
    //   // 1.1 通过套餐id获取它关联的所有菜品
    //   List<Dish> dishList = dishMapper.getBySetmealId(id);
    //   if (dishList != null && dishList.size() > 0) {
    //     // 1.2 判断所有菜品的起售状态是否都为1，如果有一个菜品的起售状态为0，则抛出异常
    //     dishList.forEach(dish -> {
    //       if (StatusConstant.DISABLE.equals(dish.getStatus())) {
    //         throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
    //       }
    //     });
    //   }
    // }
    // Setmeal setmeal = Setmeal.builder()
    //         .id(id)
    //         .status(status)
    //         .build();
    // setmealMapper.update(setmeal);
  }
  
  /**
   * 更新套餐状态
   * @param id 套餐id
   * @param status 状态，1：起售，0：停售
   */
  private void updateSetmealStatus(Long id, Integer status) {
    Setmeal setmeal = Setmeal.builder()
            .id(id)
            .status(status)
            .build();
    setmealMapper.update(setmeal);
  }

  private void updateDishInfoInComment(SetmealDTO setmealDTO) {
    // 首先需要获取原始菜品信息以获得旧名称
    Setmeal originalSetmeal = setmealMapper.getSetmealById(setmealDTO.getId());
    if (originalSetmeal == null) {
      return; // 如果找不到原始套餐，则直接返回
    }

    String oldSetMealName = originalSetmeal.getName();
    // 如果菜品名称没有变化，则无需更新MongoDB中的评论
    if (oldSetMealName.equals(setmealDTO.getName())) {
      return;
    }
    Query query = Query.query(Criteria.where("moreDishName").regex(".*" + oldSetMealName + ".*"));
    //获取到对应的评论信息
    List<Comment> comments = mongoTemplate.find(query, Comment.class);
    //为空则不处理
    if (comments.isEmpty()) {
      return;
    }

    //不为空才处理
    //不为空才处理
    comments.forEach(comment -> {
      String moreDishName = comment.getMoreDishName();
      if (moreDishName != null && moreDishName.contains(oldSetMealName)) {
        // 将字符串按"、"分割成数组
        String[] setMealName = moreDishName.split("、");

        // 遍历数组，替换匹配的菜品名称
        for (int i = 0; i < setMealName.length; i++) {
          if (setMealName[i].equals(oldSetMealName)) {
            setMealName[i] = setmealDTO.getName(); // 替换为新名称
          }
        }

        // 将数组重新组合成字符串
        String newMoreDishName = String.join("、", setMealName);
        comment.setMoreDishName(newMoreDishName);
        //更新评论信息
        mongoTemplate.save(comment);
      }
    });
  }
}
