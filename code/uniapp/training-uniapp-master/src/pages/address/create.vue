<template>
	<view class="container">
		<!-- 表单组件 -->
		<view class="form-wrapper mb-30">
			<up-form
				:model="form"
				ref="uForm"
				label-width="140rpx"
				:label-style="labelStyle">
				<up-form-item
					label="所在地区"
					prop="regions">
					<select-region
						ref="SelectRegionRef"
						v-model="form.regions"
						v-model:value-text="regionVal"
						@city-change="onCityChange" />
				</up-form-item>
				<up-form-item
					label="收货人"
					prop="consignee">
					<up-input
						v-model="form.consignee"
						placeholder="请填写收货人姓名"
						border="bottom"
						placeholder-style="font-size: 25rpx">
						<template #suffix>
							<up-form-item prop="sex">
								<up-radio-group
									size="14"
									labelColor="#9c9c9c"
									v-model="form.sex"
									placement="row"
									iconSize="10">
									<up-radio
										label="先生"
										labelSize="12"
										:name="AddressEnum.SexEnum.MALE"
										active-color="#ca4545" />
									<up-radio
										label="女士"
										labelSize="12"
										:name="AddressEnum.SexEnum.FEMALE"
										active-color="#ca4545" />
								</up-radio-group>
							</up-form-item>
						</template>
					</up-input>
				</up-form-item>
				<up-form-item
					label="手机号码"
					prop="phone">
					<up-input
						v-model="form.phone"
						placeholder="请填写11位手机号码"
						inputmode="tel"
						border="bottom"
						placeholder-style="font-size: 25rpx" />
				</up-form-item>
				<up-form-item
					label="地址标签"
					prop="label">
					<view class="address-label dis-flex flex-y-center">
						<up-tag
							v-for="(l, li) in labels"
							:key="li"
							:text="l.text"
							checked
							size="mini"
							:color="l.checked ? '#cb4544' : '#757678'"
							:bgColor="l.checked ? '#ffeeea' : '#f5f5f5'"
							:borderColor="l.checked ? '#ffeeea' : '#f5f5f5'"
							:plain="!l.checked"
							type="info"
							:name="l.value"
							@click="onSelectLabel" />
					</view>
				</up-form-item>
				<up-form-item
					label="详细地址"
					prop="detail"
					:border-bottom="false">
					<up-textarea
						v-model="form.detail"
						placeholder="街道门牌、楼层等信息（100字以内）"
						count
						border="bottom"
						maxlength="100"
						placeholder-style="font-size: 25rpx" />
				</up-form-item>
			</up-form>

			<!-- 粘贴自动识别地址信息 -->
			<view class="auto-recognize mt-10">
				<up-read-more
					toggle
					ref="uReadMoreRef"
					showHeight="0"
					textIndent="0"
					:fontSize="12"
					color="#7b7b7b"
					openText="收起"
					closeText="粘贴地址，帮你快速填写"
					:shadowStyle="{ backgroundImage: 'none' }">
					<view class="content">
						<up-textarea
							count
							border="none"
							maxlength="300"
							placeholderStyle="font-size: 25rpx"
							v-model="autoRecognize"
							placeholder="粘贴收货人姓名、手机号、收货地址、点击识别，将自动填充上面的内容。" />

						<view class="btn">
							<up-button
								text="清空"
								size="mini"
								:customStyle="btnCustomStyle"
								@click="autoRecognize = ''" />
							<up-button
								type="error"
								text="识别"
								:disabled="autoRecognize === ''"
								size="mini"
								:customStyle="btnCustomStyle"
								@click="handleAutoRecognize" />
						</view>
					</view>
				</up-read-more>
			</view>
		</view>

		<view class="default-address">
			<view class="dis-flex flex-dir-column">
				<text class="f-w f-25">是否为默认地址</text>
				<text
					class="f-20 mt-10"
					style="color: #9b9b9b"
					>每次下单默认使用该收货地址</text
				>
			</view>
			<up-switch
				:size="20"
				space="2"
				v-model="form.isDefault"
				:activeValue="AddressEnum.AddressTypeEnum.DEFAULT"
				:inactiveValue="AddressEnum.AddressTypeEnum.NOT_DEFAULT"
				activeColor="#ca4545"
				inactiveColor="rgb(230, 230, 230)" />
		</view>

		<!-- 重置表单 -->
		<view
			class="reset-form"
			@click="resetForm()">
			<view class="f-w f-28 wh-full t-c">清空</view>
		</view>

		<!-- #ifdef MP-WEIXIN -->
		<!-- 微信导入地址 -->
		<!-- <view class="wechat-import flex-center">
      <view class="dis-flex flex-x-center flex-y-center" @click="chooseAddress">
        <text class="iconfont icon-wechat" style="font-size: 35rpx" />
        <text class="f-25 ml-10">微信导入地址</text>
      </view>
    </view> -->
		<!-- #endif -->

		<!-- 底部保存按钮 -->
		<view class="footer">
			<up-button
				text="保存"
				:disabled="disabled"
				shape="circle"
				color="#cb4544"
				@click="handleSubmit" />
		</view>

		<up-modal
			v-if="!isEmptyObject(clipboardData)"
			:show="showModal"
			title="是否使用以下地址？"
			closeOnClickOverlay
			showCancelButton
			@cancel="showModal = false"
			@confirm="useClipboardAddress">
			<view class="modal-content">
				<view class="consignee item">
					<view class="label-title">收货人</view>
					<view class="label-value twoline-hide">{{
						clipboardData.consignee || '未识别到收货人'
					}}</view>
				</view>
				<view class="phone item">
					<view class="label-title">手机号码</view>
					<view class="label-value twoline-hide">{{
						clipboardData.phone || '未识别到手机号'
					}}</view>
				</view>
				<view class="consignee item">
					<view class="label-title">所在地区</view>
					<view class="label-value twoline-hide">{{
						clipboardData.labels?.join(' ') || '未识别到地区'
					}}</view>
				</view>
				<view class="consignee item">
					<view class="label-title">详细地址</view>
					<view class="label-value twoline-hide">{{
						clipboardData.detail || '未识别到详细地址'
					}}</view>
				</view>
			</view>
		</up-modal>
	</view>
</template>

<script lang="ts" setup>
	import AddressAPI, { AddressBook } from '@/api/address';
	import * as AddressEnum from '@/enums/AddressEnum';
	import SelectRegion from '@/components/select-region/index.vue';
	import { isMobile } from '@/utils/verify';
	import { onLoad, onReady, onShow } from '@dcloudio/uni-app';
	import { isEmptyObject } from '@/utils/util';
	import AddressParse from 'address-parse';

	type ClipboardData = {
		consignee?: string;
		phone?: string;
		detail?: string;
		labels?: string[];
	};

	const uForm = ref<any>(null);
	const SelectRegionRef = ref<InstanceType<typeof SelectRegion> | null>(null);
	const uReadMoreRef = ref(null);
	const labelStyle = reactive({
		'font-weight': 'bold',
		'font-size': '26rpx',
	});
	const btnCustomStyle = reactive({
		width: '80rpx',
	});
	const labels = reactive([
		{ text: '家', value: AddressEnum.AddressLabelEnum.HOME, checked: false },
		{
			text: '公司',
			value: AddressEnum.AddressLabelEnum.COMPANY,
			checked: false,
		},
		{
			text: '学校',
			value: AddressEnum.AddressLabelEnum.SCHOOL,
			checked: false,
		},
		{ text: '其他', value: AddressEnum.AddressLabelEnum.OTHER, checked: false },
	]);
	const autoRecognize = ref('');
	// 按钮禁用
	const disabled = ref(false);
	const form = reactive<AddressBook>({
		consignee: '',
		phone: '',
		detail: '',
		// 省份编码 + 城市编码 + 区编码
		provinceCode: '',
		cityCode: '',
		districtCode: '',
		// 省份名称 + 城市名称 + 区名称
		provinceName: '',
		cityName: '',
		districtName: '',
		regions: [],
		sex: AddressEnum.SexEnum.MALE,
		label: AddressEnum.AddressLabelEnum.NO_SELECT,
		isDefault: AddressEnum.AddressTypeEnum.NOT_DEFAULT,
	});
	// 表单验证规则
	const rules = reactive({
		consignee: [
			{
				required: true,
				message: '请填写收货人姓名',
				trigger: ['blur', 'change'],
			},
		],
		phone: [
			{
				required: true,
				message: '请输入手机号',
				trigger: ['blur', 'change'],
			},
			{
				// 自定义验证函数
				validator: (_: any, value: string) => {
					// 返回true表示校验通过，返回false表示不通过
					return isMobile(value);
				},
				message: '手机号码不正确',
				// 触发器可以同时用blur和change
				trigger: ['blur'],
			},
		],
		regions: [
			{
				required: true,
				message: '请选择省市区街道',
				trigger: ['blur', 'change'],
				type: 'array',
				// 自定义校验规则
				validator: (_: any, value: string[], callback: any) => {
					// 返回true表示校验通过，返回false表示不通过
					if (value.length === 3) {
						callback();
					} else {
						callback(new Error('请选择省市区'));
					}
				},
			},
		],
		detail: [
			{
				required: true,
				message: '请输入详细地址',
				trigger: ['blur', 'change'],
			},
		],
	});
	const showModal = ref(false);
	const clipboardData = reactive<ClipboardData>({});
	const skipClipboardRead = ref(false);

	onLoad(options => {
		if (isEmptyObject(options)) {
			return;
		}
		// 设置表单数据显示
		createFormData(options as UniApp.ChooseAddressRes);
	});

	// 必须要在onReady生命周期，因为onLoad生命周期组件可能尚未创建完毕
	onReady(() => {
		uForm.value?.setRules(rules);
	});

	onShow(() => {
		// 如果标志位为 true，跳过读取剪贴板
		if (skipClipboardRead.value) {
			skipClipboardRead.value = false; // 重置标志位，确保下次 onShow 正常读取
			return;
		}
		// 获取剪贴板数据
		getClipboardData();
	});

	watch(
		() => form.regions,
		newVal => {
			form.provinceCode = newVal![0] || '';
			form.cityCode = newVal![1] || '';
			form.districtCode = newVal![2] || '';
		}
	);

	const regionVal = computed({
		get() {
			if (form.provinceName && form.cityName && form.districtName) {
				return `${form.provinceName}-${form.cityName}-${form.districtName}`;
			}
			return '';
		},
		set(val: string) {
			const [provinceName, cityName, districtName] = val.split('-') || [];
			form.provinceName = provinceName || '';
			form.cityName = cityName || '';
			form.districtName = districtName || '';
		},
	});

	// 设置表单数据显示
	function createFormData(options: UniApp.ChooseAddressRes) {
		// 由于 onLoad 生命周期调用的过早，此时组件可能还未挂载，未避免通过 Ref 获取到的是 null，使用 nextTick 等到组件挂载后再设置数据
		nextTick(() => {
			form.consignee = options.userName;
			form.phone = options.telNumber;
			form.detail = options.detailInfo;
			const labels = [
				options.provinceName,
				options.cityName,
				options.countyName,
			];
			SelectRegionRef.value?.setDefaultByNames(labels);
		});
	}

	// 获取剪贴板数据
	function getClipboardData() {
		uni.getClipboardData({
			success(res) {
				if ((res as any).errMsg.includes('getClipboardData:fail')) return;
				// 检查数据格式是否正确，正确则弹出model提示用户是否使用该地址
				const data = AddressParse.parse(res.data)[0];
				if (!isAddressDataValid(data)) {
					console.info('剪切板数据格式不正确');
					return;
				}
				// 显示模态框
				showModal.value = true;
				clipboardData.consignee = data.name;
				clipboardData.phone = data.mobile;
				clipboardData.detail = data.details?.split(' ').join('') || '';
				clipboardData.labels = [data.province!, data.city!, data.area!];
			},
		});
	}

	// 模态框点击确认回调
	const useClipboardAddress = () => {
		// 设置表单数据
		form.consignee = clipboardData.consignee;
		form.phone = clipboardData.phone;
		form.detail = clipboardData.detail;
		SelectRegionRef.value?.setDefaultByNames(clipboardData.labels);
		uni.$u.toast('已使用剪切板地址');
		// 关闭模态框
		showModal.value = false;
	};

	// 选择地址标签
	const onSelectLabel = (val: AddressEnum.AddressLabelEnum) => {
		form.label = val;
		labels.forEach(label => {
			label.checked = label.value === val;
		});
	};

	// #ifdef MP-WEIXIN
	// 微信导入地址
	const chooseAddress = () => {
		uni.chooseAddress({
			success(res) {
				form.consignee = res.userName;
				form.phone = res.telNumber;
				form.detail = res.detailInfo;
				const labels = [res.provinceName, res.cityName, res.countyName];
				SelectRegionRef.value?.setDefaultByNames(labels);
				// 成功导入微信地址，设置标志位为 true
				skipClipboardRead.value = true;
				uni.$u.toast('微信地址选择成功');
			},
			fail({ errMsg }) {
				console.error('获取微信地址失败：', errMsg);
			},
		});
	};
	// #endif

	// 粘贴自动识别地址信息
	const handleAutoRecognize = () => {
		if (!autoRecognize.value) {
			return;
		}
		const result = AddressParse.parse(autoRecognize.value);
		if (result.length === 0) {
			uni.$u.toast('识别失败');
			return;
		}
		const data = result[0];
		if (!isAddressDataValid(data)) {
			uni.$u.toast('识别失败');
			return;
		}
		// 设置表单数据
		form.consignee = data.name;
		form.phone = data.mobile;
		form.detail = data.details?.split(' ').join('') || '';
		SelectRegionRef.value?.setDefaultByNames([
			data.province!,
			data.city!,
			data.area!,
		]);
		uni.$u.toast('识别成功');
	};

	// 提交表单
	const handleSubmit = () => {
		uForm.value
			?.validate()
			.then((valid: boolean) => {
				if (valid) {
					disabled.value = true;
					AddressAPI.add(form)
						.then(() => {
							uni.$u.toast('新增地址成功');

							setTimeout(() => {
								// 返回上一页
								uni.navigateBack();
								// 重置表单
								resetForm();
							}, 1800);
						})
						.catch(() => uni.$u.toast('保存失败'))
						.finally(() => (disabled.value = false));
				}
			})
			.catch(() => {
				uni.$u.toast('请填写完整表单信息');
			});
	};

	function onCityChange(_: any) {}

	// 重置表单
	function resetForm() {
		uForm.value?.resetFields();
		// 清空地区选择器
		regionVal.value = '';
		// 清空地址标签
		form.label = AddressEnum.AddressLabelEnum.NO_SELECT;
		labels.forEach(label => (label.checked = false));
		// 清空默认地址
		form.isDefault = AddressEnum.AddressTypeEnum.NOT_DEFAULT;
	}

	// 验证地址数据是否有效
	function isAddressDataValid(data: AddressParseData) {
		return !!(
			data &&
			data.area &&
			data.city &&
			data.province &&
			data.mobile &&
			data.name
		);
	}
</script>

<style>
	page {
		--auto-width: 100rpx;
		--auto-height: 50rpx;
		--auto-right: 20rpx;
		--auto-margin-right: 15rpx;
		--auto-bottom: 20rpx;
		padding-bottom: 150rpx;
		background-color: #f8f8f8 !important;
	}
</style>

<!-- 组件样式穿透 -->
<style lang="scss">
	:deep(.form-wrapper) {
		.u-form {
			.u-form-item:nth-child(5) {
				.u-form-item__body__left {
					margin-top: 15rpx;
					align-items: flex-start !important;
				}
			}
			// 微信小程序暂时还不支持:has()选择器，以下写法无效报错
			// .u-form-item {
			//   &:has(.u-form-item__body__right .u-textarea) {
			//     .u-form-item__body__left {
			//       margin-top: 15rpx;
			//       align-items: flex-start !important;
			//     }
			//   }
			// }
		}
		.u-radio-group {
			.u-radio:first-child {
				margin-right: 15rpx !important;
			}
			.u-radio__icon-wrap {
				margin-right: 8rpx !important;
			}
		}
		.u-tag {
			padding: 6rpx 10rpx !important;
			min-width: 50rpx;
			justify-content: center;
			height: auto !important;
			margin-right: 10rpx !important;
			&:not(:last-child) {
				margin-right: 10rpx !important;
			}
		}
	}

	:deep(.auto-recognize) {
		.u-textarea {
			height: 180rpx;
			z-index: 99;
			padding: var(--auto-bottom) 12px !important;
			.u-textarea__field {
				height: 120rpx !important;
			}
			&,
			.u-textarea__count {
				background-color: #f4f3f3 !important;
			}

			.u-textarea__count {
				bottom: calc(var(--auto-height) / 2) !important;
				right: calc(
					(var(--auto-width) * 2) + var(--auto-right) + var(--auto-margin-right)
				) !important;
			}
		}
		.content {
			& .u-button {
				height: var(--auto-height);
				&:first-child {
					margin-right: var(--auto-margin-right);
				}
			}
		}
	}
</style>

<style lang="scss" scoped>
	.form-wrapper {
		background-color: #fff;
		padding: 30rpx 50rpx;
		border-top: 1px solid #eee;
	}

	.address-label {
		padding: 9px 0;
		// border-bottom: 0.5px solid #dadbde;
	}

	.auto-recognize {
		.content {
			position: relative;
			.btn {
				display: flex;
				align-items: center;
				position: absolute;
				bottom: var(--auto-bottom);
				right: var(--auto-right);
				z-index: 100;
			}
		}
	}

	.default-address,
	.reset-form {
		padding: 30rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
	}

	.default-address {
		justify-content: space-between;
	}

	.reset-form {
		margin-top: 30rpx;
		color: #cb4544;
		justify-content: center;
	}

	.wechat-import {
		width: 100%;
		margin-top: 30rpx;
		& view:first-child {
			opacity: 0.7;
			padding: 15rpx 35rpx;
			border-radius: 30rpx;
			background-color: #fff;
			box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.12);
		}
	}

	.footer {
		position: fixed;
		bottom: 50rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 85%;
	}

	.modal-content {
		display: flex;
		flex-direction: column;

		.item {
			display: flex;
			align-items: center;
			&:not(:last-child) {
				margin-bottom: 20rpx;
			}
			.label-title {
				min-width: 120rpx;
				color: #a6a6a6;
				font-weight: bold;
				font-size: 28rpx;
			}
			.label-value {
				font-size: 28rpx;
				color: black;
				font-weight: bold;
				margin-left: 20rpx;
			}
		}
	}
</style>
