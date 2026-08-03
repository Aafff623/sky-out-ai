<template>
	<view class="complete-user-info">
		<up-form
			:model="userInfo"
			ref="uForm"
			label-width="140rpx">
			<up-form-item prop="avatar">
				<view class="item mt-30">
					<button
						open-type="chooseAvatar"
						style="background-color: #fff; padding: 0"
						@click="onClickAvatar()"
						@chooseavatar="onChooseAvatar">
						<view class="profile-photo dis-center h-80">
							<text style="font-size: 30rpx">头像</text>
							<view class="dis-ali-center">
								<avatar-image
									:url="userInfo.avatar"
									:show-border="false" />
								<up-icon
									name="arrow-right"
									color="#8c8b8b"
									size="20" />
							</view>
						</view>
					</button>
				</view>
			</up-form-item>

			<up-form-item prop="name">
				<view class="item mt-4">
					<view class="nick-name dis-ali-center h-80 flex-x-between">
						<view class="mr-40">
							<text style="font-size: 30rpx">昵称</text>
						</view>

						<view
							class="dis-ali-center"
							style="color: #8c8b8b">
							<input
								:value="userInfo.name"
								type="nickname"
								:maxlength="30"
								placeholder="请输入修改后的昵称"
								style="text-align: right"
								@change="onNicknameChange" />
						</view>
					</view>
				</view>
			</up-form-item>
		</up-form>

		<view class="item mt-30">
			<view
				class="logout"
				@click="handleSubmit">
				确认更新
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { onLoad } from "@dcloudio/uni-app"
	import UserAPI, { type UserVO } from "@/api/user"
	import { ComponentInternalInstance } from "vue"
	import AvatarImage from "@/components/avatar-image/index.vue"

	const { proxy } = getCurrentInstance() as ComponentInternalInstance
	const uForm = ref<any>(null)
	const userInfo = ref<UserVO>({
		id: undefined,
		name: "",
		avatar: "",
	})
	const uploadUrl = ref(import.meta.env.VITE_APP_API_URL)

	onLoad((query: any) => {
		userInfo.value = query
		userInfo.value.id = uni.getStorageSync("userId")
	})

	// 点击头像按钮事件（非微信小程序）
	const onClickAvatar = () => {
		// #ifdef MP-WEIXIN
		return
		// #endif
		chooseImage()
	}
	const onChooseAvatar = (info: any) => {
		// #ifdef MP-WEIXIN
		// uploadAvatar(info.detail.avatarUrl);
		userInfo.value.avatar = info.detail.avatarUrl
		// #endif
	}

	async function uploadAvatar(avatarUrl: string): Promise<void> {
		uni.showLoading({
			title: "🐸开始上传头像",
			mask: true,
		})

		return new Promise((resolve, reject) => {
			uni.uploadFile({
				url: uploadUrl.value + "/upload",
				filePath: avatarUrl,
				name: "file",
				header: {
					"Content-Type": "multipart/form-data",
					authentication: uni.getStorageSync("token"),
				},
				success(res) {
					const result = JSON.parse(res.data)
					userInfo.value!.avatar = result.data
					// 更新用户信息
					// updateUserInfo();
					resolve()
				},
				fail(err) {
					proxy?.$toast(err.errMsg || "上传头像失败")
					reject()
				},
				complete() {
					uni.hideLoading()
				},
			})
		})
	}

	function updateUserInfo() {
		UserAPI.update(userInfo.value)
			.then(_ => {
				proxy?.$toast("更新用户信息成功")
			})
			.catch(err => {
				proxy?.$toast(err.msg || "更新用户信息失败")
			})
	}

	function chooseImage() {
		uni.chooseImage({
			count: 1,
			sizeType: ["original", "compressed"],
			sourceType: ["album", "camera"],
			success({ tempFiles }) {
				userInfo.value.avatar = (tempFiles as any)[0].path
			},
			fail() {
				userInfo.value.avatar = ""
			},
		})
	}

	function validFormIsPass() {
		const tipMap = {
			name: "昵称",
			avatar: "头像",
		}
		for (const key in userInfo.value) {
			if (Object.prototype.hasOwnProperty.call(userInfo.value, key)) {
				if (userInfo.value[key as keyof UserVO] === "") {
					proxy?.$toast(`请填写${(tipMap as any)[key]}`)
					return false
				}
			}
		}

		return true
	}

	const onNicknameChange = (e: any) => {
		const nickNameInput = e.detail.value.trim()
		userInfo.value.name = nickNameInput
	}

	const handleSubmit = async () => {
		if (!validFormIsPass()) return

		// 上传头像
		if (userInfo.value.avatar?.includes("http://tmp")) {
			await uploadAvatar(userInfo.value.avatar!)
		}

		// 更新用户信息
		updateUserInfo()
	}
</script>

<style>
	page {
		background-color: rgb(247, 247, 247);
	}
</style>

<style lang="scss">
	:deep(.complete-user-info) {
		.u-input {
			border: none;
		}

		.u-form {
			.u-form-item__body {
				padding: 0;
			}
		}
	}
</style>

<style lang="scss" scoped>
	.dis-center {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.dis-ali-center {
		display: flex;
		align-items: center;
	}

	.h-80 {
		height: 80rpx;
	}

	.item {
		background-color: #fff;
		padding: 20rpx 40rpx;
	}

	.avatar-image {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
	}

	.logout {
		color: #ff0009;
		font-size: 30rpx;
		text-align: center;
		margin: 10rpx 0;
		height: 50rpx;
		line-height: 50rpx;
	}
</style>
