import { request } from '@/utils/request';
import { mergeConfig, dispatchRequest } from "./utils";
import type {
  Options,
  RequestInfo,
  RequestError,
  ChangeKeys,
  DataFactoryParams,
} from "./types";

export default class request {
  baseUrl: Options["baseUrl"];
  timeout: Options["timeout"];
  header: Options["header"];
  config: Options["config"];

  constructor(options: Options) {
    // 请求公共地址
    this.baseUrl = options.baseUrl || "";
    // 超时时间
    this.timeout = options.timeout || 6000;
    // 默认请求头
    this.header = options.header || {};
    // 默认配置
    this.config = options.config || {
      isPrompt: true,
      load: true,
      isFactory: true,
      resend: 0,
    };

  }

  // 接口请求方法
  async request<T = any>(data: RequestInfo): Promise<ResponseData<T>> {
    // 是否运行过请求开始钩子
    let runRequestStart = false;
    // 请求数据
    let requestInfo: RequestInfo | null = null;

    try {
      if (!data.url) {
        throw {
          msg: "【request】缺失数据url",
          statusCode: 0,
        };
      }

      // 数据合并
      requestInfo = mergeConfig(this, data);
      // 代表之前运行到这里
      runRequestStart = true;
      // 请求前回调
      if (this.requestStart) {
        let requestStart = this.requestStart(requestInfo);
        if (typeof requestStart == "object") {
          const changekeys: ChangeKeys = [
            "data",
            "header",
            "isPrompt",
            "load",
            "isFactory",
          ];
          changekeys.forEach((key) => {
            (requestInfo as any)[key] = (requestStart as any)[key];
          });
        } else {
          throw {
            msg: "【request】请求开始拦截器未通过",
            statusCode: 0,
            data: requestInfo.data,
            method: requestInfo.method,
            header: requestInfo.header,
            url: requestInfo.url,
          };
        }
      }

      // 发起请求
      const requestResult = await dispatchRequest<T>(requestInfo);

      // 是否用外部的数据处理方法
      if (requestInfo.isFactory && this.dataFactory) {
        // 数据处理
        let res = await this.dataFactory<T>({
          ...requestInfo,
          response: requestResult,
        });
        return Promise.resolve({
          code: requestResult.code,
          data: res as Awaited<T>,
          msg: requestResult.msg,
        });
      } else {
        return Promise.resolve(requestResult);
      }
    } catch (err: any) {
      this.requestError && this.requestError(err as RequestError);
      return Promise.reject(err as RequestError);
    } finally {
      // 如果请求开始未运行到，请求结束也不运行
      if (runRequestStart && requestInfo) {
        this.requestEnd && this.requestEnd(requestInfo);
      }
    }
  }

  // post请求
  post<T = any>(
    url: UniApp.RequestOptions["url"] = "",
    data: UniApp.RequestOptions["data"] = {},
    options: ReqOptionsConfig = {}
  ) {
    return this.request<T>({
      method: "POST",
      data: data,
      url: url,
      ...options,
    });
  }

  // get请求
  get<T = any>(
    url: UniApp.RequestOptions["url"] = "",
    data: UniApp.RequestOptions["data"] = {},
    options: ReqOptionsConfig = {}
  ) {
    return this.request<T>({
      method: "GET",
      data: data,
      url: url,
      ...options,
    });
  }

  // put请求
  put<T = any>(
    url: UniApp.RequestOptions["url"] = "",
    data: UniApp.RequestOptions["data"] = {},
    options: ReqOptionsConfig = {}
  ) {
    return this.request<T>({
      method: "PUT",
      data: data,
      url: url,
      ...options,
    });
  }

  // delete请求
  delete<T = any>(
    url: UniApp.RequestOptions["url"] = "",
    data: UniApp.RequestOptions["data"] = {},
    options: ReqOptionsConfig = {}
  ) {
    return this.request<T>({
      method: "DELETE",
      data: data,
      url: url,
      ...options,
    });
  }

  /** 请求开始拦截器 */
  requestStart?: (options: RequestInfo) => RequestInfo | boolean;
  requestEnd?: (options: RequestInfo) => void;
  requestError?: (err: RequestError) => void;
  dataFactory?: <R = any>(res: DataFactoryParams<R>) => Promise<R>;
}
