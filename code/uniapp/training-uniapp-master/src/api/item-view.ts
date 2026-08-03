import request from "@/utils/request";

const ITEM_VIEW_BASE_URL = "/client/view";

class ItemViewAPI {
  //菜品浏览次数增加
  static incrementDishViewCount(dishId: number) {
    return request.post(ITEM_VIEW_BASE_URL + `/increment/dish/${dishId}`,);
  }
}

export default ItemViewAPI;