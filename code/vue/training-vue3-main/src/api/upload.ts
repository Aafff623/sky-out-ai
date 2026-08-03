import request from "@/utils/request";

const UPLOAD_BASE_URL = "/upload";

class UploadAPI {
  /**
   * 上传文件
   * @param data 表单数据
   */
  static upload(data: FormData) {
    return request<any, ReqResultType<string>>({
      url: `${UPLOAD_BASE_URL}`,
      method: "post",
      headers: { "Content-Type": "multipart/form-data" },
      data,
    });
  }
}

export default UploadAPI;
