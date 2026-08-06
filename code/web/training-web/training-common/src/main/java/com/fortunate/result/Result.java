package com.fortunate.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.fortunate.constant.MessageConstant.OPERATE_FAILED;

/**
 * @description 响应结果
 */
@Data
@ApiModel(description = "响应结果")
public class Result<T> implements Serializable {
  /** 结果编码：200-成功，0-和其他数字均表示失败 */
  @ApiModelProperty("响应状态码")
  private Integer code;
  
  @ApiModelProperty("结果信息")
  private String msg;
  
  @ApiModelProperty("返回的数据")
  private T data;
  
  public static <T> Result<T> error(String msg) {
    Result<T> result = new Result<>();
    result.setMsg(msg);
    result.setCode(0);
    return result;
  }
  
  public static <T> Result<T> error(int code, String msg) {
    Result<T> result = new Result<>();
    result.setMsg(msg);
    result.setCode(code);
    return result;
  }

  public static <T> Result<T> error() {
      return error(OPERATE_FAILED);
  }
  
  public static <T> Result<T> success() {
    Result<T> result = new Result<>();
    result.setCode(200);
    result.setMsg("操作成功");
    return result;
  }
  
  public static <T> Result<T> success(T object) {
    Result<T> result = new Result<>();
    result.setData(object);
    result.setCode(200);
    return result;
  }
  
  public static <T> Result<T> success(T object, String msg) {
    Result<T> result = new Result<>();
    result.setData(object);
    result.setMsg(msg);
    result.setCode(200);
    return result;
  }
}
