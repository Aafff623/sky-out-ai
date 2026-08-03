export interface Options {
  baseUrl?: string;
  timeout?: number;
  header?: any;
  config?: ReqOptionsConfig;
}

export type RequestInfo = ReqOptionsConfig & UniApp.RequestOptions;

export type ChangeKeys<T = UniApp.RequestOptions> = Array<
  keyof T | keyof ReqOptionsConfig
>;

export interface RequestError {
  status?: number;
  errMsg?: string;
  error?: string;
  msg?: string;
  path?: string;
  timestamp?: number;
}

export type DataFactoryParams<T = any> = {
  response: ResponseData<T>;
} & RequestInfo;
