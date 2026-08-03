<template>
	<view class="container">
		<view
			class="address-list"
			v-if="list.length">
			<!-- TODO:未完成上划加载更多 -->
			<up-card
				:border="false"
				:foot-border-top="false"
				:show-head="false"
				:box-shadow="cardBoxShadow"
				v-for="(item, index) in list"
				:key="index">
				<template #body>
					<view class="address-item">
						<!-- 收货人、手机号 -->
						<view class="contacts dis-flex flex-y-center">
							<up-tag
								v-if="item.label"
								size="mini"
								type="error"
								color="#ca4545"
								:text="item.label"
								bgColor="#ffeceb"
								borderColor="transparent" />
							<text class="name ml-10">{{ item.consignee }}</text>
							<text class="phone">{{ item.phone }}</text>
						</view>

						<!-- 收货地址 -->
						<view class="address">
							<text class="province-name">{{ item.provinceName }}</text>
							<text class="city-name ml-10 mr-10">{{ item.cityName }}</text>
							<text class="district-name mr-10">{{ item.districtName }}</text>
							<text class="detail">{{ item.detail }}</text>
						</view>
						<!-- 分割线 -->
						<view class="line"></view>

						<view class="item-option">
							<view class="_left">
								<view class="item-radio">
									<up-radio-group
										v-model="defaultId"
										@change="handleSetDefault(item)">
										<up-radio
											:name="item.id"
											active-color="#ca4545"
											:label="item.id === defaultId ? '默认' : '选择'" />
									</up-radio-group>
								</view>
							</view>
							<view class="_right">
								<view class="events">
									<up-copy
										class="event-item"
										:content="`${item.consignee}${item.phone}${item.provinceName}${item.cityName}${item.districtName}${item.detail}`">
										<text class="iconfont icon-copy" />
										<text class="title">复制</text>
									</up-copy>
									<view
										class="event-item"
										@click="handleUpdate(item.id)">
										<text class="iconfont icon-edit"></text>
										<text class="title">编辑</text>
									</view>
									<view
										class="event-item"
										@click="handleRemove(item.id)">
										<text class="iconfont icon-delete"></text>
										<text class="title">删除</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</template>
			</up-card>
		</view>

		<empty
			v-if="!list.length"
			:isLoading="isLoading"
			tips="暂无收货地址哦" />

		<!-- 底部操作按钮 -->
		<view class="footer-fixed">
			<!-- <view class="wechat-import mr-30">
				<up-button
					type="primary"
					shape="circle"
					plain
					text="微信导入地址"
					iconColor="#ca4545"
					icon="weixin-fill"
					@click="chooseWXAddress()"
					:customStyle="{ color: '#ca4545', 'border-color': '#ca4545' }" />
			</view> -->
			<view class="create-address">
				<up-button
					type="primary"
					shape="circle"
					plain
					text="新建收货地址"
					iconColor="#ca4545"
					icon="plus-circle-fill"
					@click="handleCreate()"
					:customStyle="{ color: '#ca4545', 'border-color': '#ca4545' }" />
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import Empty from '@/components/empty/index.vue';
	import AddressApi, { type AddressBook } from '@/api/address';
	import { ComponentInternalInstance } from 'vue';

	const { proxy } = getCurrentInstance() as ComponentInternalInstance;
	const isLoading = ref(false);
	const cardBoxShadow = ref('0px 0px 12px rgba(0, 0, 0, 0.12)');
	const defaultId = ref<AddressBook['id']>();
	const list = ref<Array<AddressBook>>([]);
	const option = ref<AnyObject | undefined>();

	onLoad(options => {
		option.value = options;
	});

	onShow(() => {
		getPageData();
	});

	function getPageData() {
		isLoading.value = true;
		Promise.all([getDefaultId(), getAddressList()])
			.then(() => {
				// 列表排序把默认收货地址放到最前
				onReorder();
			})
			.finally(() => (isLoading.value = false));
	}

	// 获取收货地址列表
	function getAddressList() {
		return new Promise<ResponseData<AddressBook[]>>((resolve, reject) => {
			AddressApi.list()
				.then(res => {
					list.value = res.data;
					resolve(res);
				})
				.catch(reject);
		});
	}

	// 获取默认的收货地址
	function getDefaultId() {
		return new Promise((resolve, reject) => {
			AddressApi.getDefault()
				.then(res => {
					defaultId.value = res.data.id;
					resolve(res);
				})
				.catch(reject);
		});
	}

	// 列表排序把默认收货地址放到最前
	function onReorder() {
		list.value.sort(a => {
			return a.id === defaultId.value ? -1 : 1;
		});
	}

	// 从微信导入地址
	const chooseWXAddress = () => {
		uni.chooseAddress({
			success(res: UniApp.ChooseAddressRes) {
				proxy?.$navTo('pages/address/create', res);
			},
			fail(err) {
				if (err.errMsg === 'chooseAddress:fail cancel') {
					proxy?.$toast('用户取消选择地址');
				}
				console.error('获取微信地址失败==>', err);
			},
		});
	};

	// 选择新的默认地址
	const handleSetDefault = (address: AddressBook) => {
		AddressApi.setDefault(address).then(_ => {
			option.value?.form === 'order' && uni.navigateBack();
		});
	};

	const handleCreate = () => {
		proxy?.$navTo('pages/address/create');
	};

	// 编辑收货地址
	const handleUpdate = (addressId: AddressBook['id']) => {
		proxy?.$navTo('pages/address/update', { addressId });
	};

	// 删除收货地址
	const handleRemove = (addressId: AddressBook['id']) => {
		uni.showModal({
			title: '提示',
			content: '确定删除该收货地址吗？',
			success({ confirm }) {
				if (confirm) {
					confirm && onRemove(addressId);
				}
			},
		});
	};
	// 确认删除收货地址
	function onRemove(addressId: AddressBook['id']) {
		AddressApi.delete(addressId).then(() => {
			getPageData();
		});
	}
</script>

<style>
	page {
		background-color: #f5f6f8;
	}
</style>

<style lang="scss">
	:deep(.contacts) {
		.u-tag {
			height: 33rpx !important;
			line-height: 33rpx !important;
		}
	}

	:deep(.footer-fixed) {
		.u-button {
			min-height: 90rpx !important;
		}
		.wechat-import {
			.u-button {
				.u-icon {
					& text:first-child {
						font-size: 45rpx !important;
					}
				}
			}
		}
	}
</style>

<style lang="scss" scoped>
	.container {
		margin-bottom: 80rpx;
	}
	.addres-list {
		padding-top: 20rpx;
		// 设置ios刘海屏底部横线安全区域
		padding-bottom: calc(constant(safe-area-inset-bottom) + 140rpx);
		padding-bottom: calc(env(safe-area-inset-bottom) + 140rpx);
	}

	.contacts {
		font-size: 30rpx;
		margin-bottom: 16rpx;

		.name {
			margin-right: 16rpx;
		}
	}

	.address {
		font-size: 28rpx;

		.region {
			margin-right: 10rpx;
		}
	}

	.line {
		margin: 20rpx 0;
		border-bottom: 1rpx solid #f3f3f3;
	}

	.item-option {
		display: flex;
		justify-content: space-between;
		height: 48rpx;

		// 单选框
		.item-radio {
			font-size: 28rpx;

			.radio {
				vertical-align: middle;
				transform: scale(0.76);
			}

			.text {
				vertical-align: middle;
			}
		}

		// 操作
		.events {
			display: flex;
			align-items: center;
			line-height: 48rpx;

			.event-item {
				font-size: 28rpx;
				margin-right: 26rpx;
				color: #4c4c4c;

				& text:first-child {
					font-size: 30rpx;
				}

				&:last-child {
					margin-right: 0;
				}

				.title {
					margin-left: 8rpx;
				}
			}
		}
	}

	.footer-fixed {
		position: fixed;
		bottom: 80rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 90%;
		display: flex;
		justify-content: space-between;
		align-items: center;

		.wechat-import,
		.create-address {
			width: 50%;
		}
	}
</style>
