## 场景一：订单详情页使用

``` ts
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useOrderCountdownStore, orderCountdownEvents } from '@/stores/order-countdown'

const props = defineProps<{
  orderId: string
}>()

const orderInfo = ref<OrderVO>()
const orderCountdownStore = useOrderCountdownStore()

// 方法一：注册特定订单回调
let unregisterCallback: () => void | undefined

// 方法二：订阅全局事件
const unsubscribe = orderCountdownEvents.subscribe((cancelledOrderId) => {
  if (cancelledOrderId === props.orderId) {
    handleOrderCancelled()
  }
})

// 初始化加载订单数据
async function loadOrderDetail() {
  const res = await OrderAPI.getDetail(props.orderId)
  orderInfo.value = res.data
  
  // 如果订单是待支付状态，注册倒计时
  if (orderInfo.value.status === OrderStatus.PENDING_PAYMENT) {
    // 添加订单到倒计时管理并注册回调
    orderCountdownStore.addOrder(orderInfo.value, handleOrderCancelled)
    
    // 或者使用单独的注册方法
    unregisterCallback = orderCountdownStore.registerCallback(props.orderId, handleOrderCancelled)
  }
}

function handleOrderCancelled() {
  // 更新本地状态
  if (orderInfo.value) {
    orderInfo.value.status = OrderStatus.CANCELED
  }
  
  // 更新页面标题
  uni.setNavigationBarTitle({ title: '订单已取消' })
  
  // 显示提示
  uni.showToast({
    title: '订单已超时取消',
    icon: 'none'
  })
}

onMounted(() => {
  loadOrderDetail()
})

onUnmounted(() => {
  // 清理监听
  unsubscribe?.()
  unregisterCallback?.()
})
</script>
```
## 场景二：订单列表页使用

``` ts
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { orderCountdownEvents } from '@/stores/order-countdown'

const orderList = ref<OrderVO[]>([])

// 订阅全局取消事件
const unsubscribe = orderCountdownEvents.subscribe((cancelledOrderId) => {
  const index = orderList.value.findIndex(item => item.id === cancelledOrderId)
  if (index > -1) {
    // 直接修改本地状态
    orderList.value[index].status = OrderStatus.CANCELED
    
    // 可选：触发界面更新
    orderList.value = [...orderList.value]
  }
})

// 加载订单列表
async function loadOrderList() {
  const res = await OrderAPI.getList()
  orderList.value = res.data
  
  // 初始化倒计时（根据业务需求）
  const pendingOrders = orderList.value.filter(
    item => item.status === OrderStatus.PENDING_PAYMENT
  )
  useOrderCountdownStore().initialize(pendingOrders)
}

onMounted(() => {
  loadOrderList()
})

onUnmounted(() => {
  unsubscribe?.()
})
</script>
```

## 场景三：创建订单成功后处理

```ts
// 下单成功后的处理逻辑
async function handleCreateOrderSuccess(order: OrderVO) {
  // 添加订单到倒计时管理
  const store = useOrderCountdownStore()
  
  // 方法一：添加订单时直接注册回调
  store.addOrder(order, (orderId) => {
    // 处理本地状态更新
    if (currentPage === 'orderDetail' && orderId === order.id) {
      updateLocalOrderStatus(orderId)
    }
  })

  // 方法二：使用全局事件订阅
  const unsubscribe = orderCountdownEvents.subscribe((orderId) => {
    if (orderId === order.id) {
      // 跳转到订单详情时可能需要处理
    }
  })

  // 方法三：在跳转到的订单详情页自动处理（推荐）
}
```
