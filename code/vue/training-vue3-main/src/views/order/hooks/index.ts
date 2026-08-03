import OrderAPI, { type OrdersRejectionDTO } from "@/api/order";

/**
 * 商家接单
 * @param id 订单id
 */
export function onConfirm(id: number) {
  return new Promise(async (resolve, reject) => {
    const res = await OrderAPI.confirm(id);
    if (res.code === 200) {
      ElMessage.success("操作成功");
      resolve(res);
    } else {
      ElMessage.error(res.msg || "操作失败");
      reject(res);
    }
  });
}

/**
 * 商家拒单
 * @param data 商家拒单DTO
 */
export function onReject(data: OrdersRejectionDTO) {
  return new Promise(async (resolve, reject) => {
    const res = await OrderAPI.rejection(data);
    if (res.code === 200) {
      ElMessage.success("操作成功");
      resolve(res);
    } else {
      ElMessage.error(res.msg || "操作失败");
      reject(res);
    }
  });
}

/**
 * 商家派送订单
 * @param id 订单id
 */
export function onDelivery(id: number) {
  return new Promise(async (resolve, reject) => {
    const res = await OrderAPI.delivery(id);
    if (res.code === 200) {
      ElMessage.success("操作成功");
      resolve(res);
    } else {
      ElMessage.error(res.msg || "操作失败");
      reject(res);
    }
  });
}

/**
 * 商家完成订单
 * @param id 订单id
 */
export function onComplete(id: number) {
  return new Promise(async (resolve, reject) => {
    const res = await OrderAPI.complete(id);
    if (res.code === 200) {
      ElMessage.success("操作成功");
      resolve(res);
    } else {
      ElMessage.error(res.msg || "操作失败");
      reject(res);
    }
  });
}
