import type RequestThisType from "./request";
import { RequestInfo } from "./types";

/**
 * 获取合并的数据
 * @param _this request模块类的this
 * @param options 请求参数
 */
export const mergeConfig = (
  _this: RequestThisType,
  options: UniApp.RequestOptions
): RequestInfo => {
  //判断url是不是链接
  let urlType = /^(http|https):\/\//.test(options.url);

  let config = Object.assign(
    {
      timeout: _this.timeout,
    },
    _this.config,
    options
  );

  config.url = urlType ? options.url : _this.baseUrl + options.url;
  // 请求头
  if (options.header) {
    config.header = Object.assign({}, _this.header, options.header);
  } else {
    config.header = Object.assign({}, _this.header);
  }
  return config;
};

/**
 * 发起请求
 * @param requestInfo 请求信息
 */
export const dispatchRequest = <T = any>(
  requestInfo: UniApp.RequestOptions
): Promise<ResponseData<T>> => {
  return new Promise((resolve, reject) => {
    let requestAbort = true;

    // 处理成功回调
    const handleSuccess = (res: UniApp.RequestSuccessCallbackResult) => {
      requestAbort = false;
      // 请求成功，返回数据
      resolve(res.data as ResponseData<T>);
    };

    // 处理失败回调
    const handleFail = (err?: Partial<UniApp.GeneralCallbackResult>) => {
      requestAbort = false;
      if (err?.errMsg === "request:fail abort") {
        reject({
          msg: "请求超时，请重新尝试",
          statusCode: 0,
        });
      } else {
        reject(err);
      }
    };

    let requestData = {
      url: requestInfo.url,
      header: requestInfo.header,
      success: handleSuccess,
      fail: handleFail,
    };

    const requestDataWithAppendData = appendDataToRequest(requestInfo);
    requestData = Object.assign(requestData, requestDataWithAppendData);

    let requestTask: UniApp.RequestTask = uni.request(requestData);
    // 超时中断请求
    setTimeout(() => {
      if (requestAbort) {
        requestTask.abort();
      }
    }, requestInfo.timeout);
  });
};

/**
 * 合并请求参数
 * @param requestInfo 请求信息
 */
function appendDataToRequest<T extends UniApp.RequestOptions>(requestInfo: T) {
  let requestData: Partial<UniApp.RequestOptions> = {};

  // 请求类型
  if (requestInfo.method) {
    requestData.method = requestInfo.method;
  }
  if (requestInfo.data) {
    requestData.data = requestInfo.data;
  }
  // #ifdef MP-WEIXIN || MP-ALIPAY
  if (requestInfo.timeout) {
    requestData.timeout = requestInfo.timeout;
  }
  // #endif
  if (requestInfo.dataType) {
    requestData.dataType = requestInfo.dataType;
  }
  // #ifndef APP-PLUS || MP-ALIPAY
  if (requestInfo.responseType) {
    requestData.responseType = requestInfo.responseType;
  }
  // #endif
  // #ifdef H5
  if (requestInfo.withCredentials) {
    requestData.withCredentials = requestInfo.withCredentials;
  }
  // #endif

  return requestData;
}
