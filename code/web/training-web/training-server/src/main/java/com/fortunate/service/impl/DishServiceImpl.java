package com.fortunate.service.impl;

import com.fortunate.constant.MessageConstant;
import com.fortunate.constant.StatusConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.DishDTO;
import com.fortunate.dto.UserDTO;
import com.fortunate.entity.Comment;
import com.fortunate.entity.CommentRepay;
import com.fortunate.entity.Dish;
import com.fortunate.entity.DishFlavor;
import com.fortunate.exception.DeletionNotAllowedException;
import com.fortunate.exception.DishExecption;
import com.fortunate.mapper.*;
import com.fortunate.query.DishPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.service.DishService;
import com.fortunate.service.ViewService;
import com.fortunate.vo.DishVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fortunate.constant.Constants.DISH;
import static com.fortunate.constant.MessageConstant.INVALID_PARAM;

/**
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;
    private final CategoryMapper categoryMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final FileServiceImpl fileService;
    private final MongoTemplate mongoTemplate;

    /**
     * 新增菜品及对应的口味数据
     * @param dishDTO 菜品数据传输对象
     */
    @Transactional
    @Override
    public void saveWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.insert(dish);
        // 获取插入之后菜品的id值
        Long dishId = dish.getId();

        // 处理多种口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            // 将口味与对应菜品进行关联
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品分页查询数据传输对象
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        if (dishPageQueryDTO == null){
            throw new DishExecption(INVALID_PARAM);
        }
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> pageInfo = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(pageInfo.getTotal(), pageInfo.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids 菜品id列表
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品是否处于起售状态，如果处于起售状态，则不能删除
        List<Dish> dishList = dishMapper.getDishByIds(ids);
        for (Dish dish : dishList) {
            if (StatusConstant.ENABLE.equals(dish.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 判断当前菜品是否被套餐关联，如果被套餐关联，则不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealDishByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除了菜品，对应的口味数据也应该被删除
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
        //删除对应的图片的文件数据
        dishList.forEach(dish -> {
            String image = dish.getImage();
            if (image != null) {
                log.info("删除菜品图片：{}", image);
                //minio版
                fileService.deleteFileMinio(image);
            }
        });
    }

    /**
     * 根据id查询菜品数据
     * @param id 菜品id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 根据菜品id查询菜品数据
        Dish dish = dishMapper.getDishById(id);
        if (dish == null) {
            return null;
        }

        // 查询该菜品所属分类名称
        String categoryName = categoryMapper.getCategoryNameById(dish.getCategoryId());

        // 根据菜品id查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getFlavorsByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        dishVO.setCategoryName(categoryName);
        return dishVO;
    }

    @Transactional
    @Override
    public void updateWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 先更新菜品表中的数据
        try {
            updateDishInfoInComment(dishDTO);
            dishMapper.update(dish);
            // 先删除原有的口味数据
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
            // 再插入新的口味数据（如果是移除了口味，则不需要插入新的口味数据）
            List<DishFlavor> flavors = dishDTO.getFlavors();
            if (flavors != null && !flavors.isEmpty()) {
                flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
                dishFlavorMapper.insertBatch(flavors);
            }
        } catch (Exception e) {
            log.error("更新菜品数据异常：{}", e.getMessage());
            //回滚更新的评论数据

            throw new RuntimeException(e);
        }

    }

    @Transactional
    @Override
    public void startOrStop(Integer status, Long id) {
        // 1、先更新菜品表，将菜品的状态更新为`status`
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        // 停售一个菜品，那么包含这个菜品的套餐也应该停售
        if (!StatusConstant.DISABLE.equals(status)) return;
        List<Long> dishIds = new ArrayList<>();
        dishIds.add(id);

        // 查询包含该菜品的套餐（返回套餐id），原型：select `setmeal_id` from `setmeal_dish` where `dish_id` in (?,?,?)
        List<Long> setmealIds = setmealDishMapper.getSetmealDishByDishIds(dishIds);
        if (setmealIds.size() == 0) return;
        setmealMapper.batchUpdateStatus(setmealIds, StatusConstant.DISABLE);
    }

    /**
     * 查询菜品和口味数据
     * @param dish 菜品数据
     * @return
     */
    @Override
    public List<DishVO> listWithFlavors(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();
        Long categoryId = null;
        //查询分类名称
        String categoryName = null;
        if (dishList != null && !dishList.isEmpty()) {
            categoryId = dishList.get(0).getCategoryId();
            categoryName = categoryMapper.getCategoryNameById(categoryId);
        }


        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);
            if (categoryId != null) {
                dishVO.setCategoryName(categoryName);
            }

            // 根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getFlavorsByDishId(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    private void updateDishInfoInComment(DishDTO dishDTO) {
        // 首先需要获取原始菜品信息以获得旧名称
        Dish originalDish = dishMapper.getDishById(dishDTO.getId());
        if (originalDish == null) {
            return; // 如果找不到原始菜品，则直接返回
        }

        String oldDishName = originalDish.getName();
        // 如果菜品名称没有变化，则无需更新MongoDB中的评论
        if (oldDishName.equals(dishDTO.getName())) {
            return;
        }
        Query query = Query.query(Criteria.where("moreDishName").regex(".*" + oldDishName + ".*"));
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
            if (moreDishName != null && moreDishName.contains(oldDishName)) {
                // 将字符串按"、"分割成数组
                String[] dishNames = moreDishName.split("、");

                // 遍历数组，替换匹配的菜品名称
                for (int i = 0; i < dishNames.length; i++) {
                    if (dishNames[i].equals(oldDishName)) {
                        dishNames[i] = dishDTO.getName(); // 替换为新名称
                    }
                }

                // 将数组重新组合成字符串
                String newMoreDishName = String.join("、", dishNames);
                comment.setMoreDishName(newMoreDishName);
                //更新评论信息
                mongoTemplate.save(comment);
            }
        });
    }

}
