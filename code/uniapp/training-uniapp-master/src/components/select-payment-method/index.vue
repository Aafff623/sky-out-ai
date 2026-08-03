<template>
	<view class="select-payment-method">
		<up-popup
			:show="visible"
			closeable
			:round="15"
			:customStyle="popupStyle"
			@close="closePopup">
			<view class="dis-flex flex-x-center flex-y-center f-28 mb-20"
				>支付方式</view
			>

			<view class="payment-list">
				<view
					v-for="(item, index) in paymentList"
					:key="index"
					class="payment-item"
					@click="onSelectPaymentChange(item.value)">
					<view class="left">
						<text
							class="iconfont f-40"
							:class="`icon-${item.icon}`" />
						<text class="f-25 ml-10">{{ item.label }}</text>
					</view>

					<view class="right">
						<up-radio-group
							v-model="paymentVal"
							placement="column"
							activeColor="#a32437"
							@change="onSelectPaymentChange">
							<up-radio :name="item.value" />
						</up-radio-group>
					</view>
				</view>
			</view>
		</up-popup>
	</view>
</template>

<script lang="ts" setup>
	import { CSSProperties } from 'vue';
	import { PayMethodsEnum } from '@/enums/PayEnum';

	const props = defineProps({
		modelValue: {
			type: [Number, String] as PropType<PayMethodsEnum>,
			default: PayMethodsEnum.WECHAT,
		},
	});

	const visible = ref(false);
	const popupStyle = reactive<CSSProperties>({
		padding: '40rpx',
		minHeight: '300rpx',
		backgroundColor: '#fffff',
	});
	const paymentVal = ref(props.modelValue || PayMethodsEnum.WECHAT);
	const paymentList = ref([
		{ label: '微信支付', value: PayMethodsEnum.WECHAT, icon: 'wechat' },
		// { label: '支付宝', value: PayMethodsEnum.ALIPAY, icon: 'alipay' },
	]);

	const emits = defineEmits<{
		(e: 'updatePayStatus', val: PayMethodsEnum): void;
	}>();

	// 选择支付方式改变
	const onSelectPaymentChange = (val: PayMethodsEnum) => {
		emits('updatePayStatus', val);
		paymentVal.value = val;
		// console.log(
		// 	"选择支付方式改变===>",
		// 	val === PayMethodsEnum.WECHAT ? "微信支付" : "支付宝"
		// )
		// 关闭弹窗
		closePopup();
	};

	function show() {
		visible.value = true;
	}

	function closePopup() {
		visible.value = false;
	}

	defineExpose({
		show,
		closePopup,
	});
</script>

<style lang="scss" scoped>
	.select-payment-method {
		@mixin flex-x-center {
			display: flex;
			align-items: center;
		}
		.payment-list {
			padding: 0 10rpx;

			.payment-item {
				padding: 10rpx 0;
				@include flex-x-center;
				justify-content: space-between;

				.left {
					@include flex-x-center;
					justify-content: center;
				}

				&:last-child {
					margin-top: 10rpx;
				}
			}
		}
	}
</style>
