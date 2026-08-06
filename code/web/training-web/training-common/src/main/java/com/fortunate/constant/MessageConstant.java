package com.fortunate.constant;

/**
 * @description 全局消息常量
 */
public class MessageConstant {
  public static final String OPERATE_FAILED = "操作失败";
  public static final String COMMENT_TOO_LONG = "评论内容不能超过500字";
  public static final String VIOLATION_CONTENT = "当前评论存在违规内容";
  public static final String INVALID_PARAM = "参数错误";
  public static final String ALREADY_EXISTS = "已存在";
  public static final String UNKNOWN_ERROR = "未知错误";
  public static final String ACCOUNT_NOT_FOUND = "账号不存在";
  public static final String PASSWORD_ERROR = "密码错误";
  public static final String ACCOUNT_LOCKED = "账号被锁定";
  public static final String UNAUTHORIZED = "没有权限，请先进行登录";
  public static final String USER_NOT_FOUND = "用户不存在";
  public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品，不能删除";
  public static final String DISH_ON_SALE = "起售中的菜品不能删除";
  public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";
  public static final String ORDER_REJECTION_ERROR = "订单状态错误，只有处于待接单状态的订单才能拒单";
  public static final String ORDER_NOT_FOUND = "订单不存在";
  public static final String ORDER_CANCEL_REASON = "订单超时，自动取消";
  public static final String ORDER_DELIVERY_ERROR = "订单状态错误，只有处于待接单状态的订单才能派送";
  public static final String ORDER_COMPLETE_ERROR = "订单状态错误，只有处于派送中状态的订单才能完成";
  public static final String ORDER_CANCEL_FAILED = "订单取消失败";
  public static final String ORDER_CANCEL_SUCCESS = "订单取消成功";
  public static final String UPLOAD_FAILED = "文件上传失败";
  public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";
  public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";
  public static final String ORDER_STATUS_ERROR = "订单状态错误";
  public static final String LOGIN_FAILED = "登录失败";
  public static final String GET_USER_INFO_FAILED = "获取用户信息失败，用户未登录";
  public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";
  public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐，不能删除";
  public static final String SHOP_INFO_NOT_FOUND = "商家信息未配置";
  public static final String FILE_NOT_SELECTED = "请选择文件";
  public static final String FILE_EXISTS = "该文件已存在";
  public static final String CANNOT_DELETE_ADMIN = "不能删除管理员账号";
  public static final String CANNOT_STOP_ADMIN = "不能禁用管理员账号";
  public static final String NO_DELETE_PERMISSION = "当前登录用户不具备删除权限";
  public static final String NO_STOP_PERMISSION = "当前登录用户不具备禁用权限";
  public static final String NO_UPDATE_PERMISSION = "当前登录用户不具备修改权限";

}
