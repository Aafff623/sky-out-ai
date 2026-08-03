<script lang="ts" setup>
import { LocationQuery, useRoute, useRouter } from "vue-router";
import { User, Lock } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import { LoginData } from "@/api/employee";
import { useUserStore } from "@/store";

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const ContainerLoginWrapRef = ref<HTMLElement>();
const loginFormRef = ref<FormInstance>();

const loading = ref(false);
const loginData = ref<LoginData>({
  username: "admin",
  password: "123456",
} as LoginData);

const loginRules = computed(() => {
  return {
    username: [
      {
        required: true,
        trigger: "blur",
        message: t("login.message.username.required"),
      },
    ],
    password: [
      {
        required: true,
        trigger: "blur",
        message: t("login.message.password.required"),
      },
      {
        min: 6,
        message: t("login.message.password.min"),
        trigger: "blur",
      },
    ],
  };
});

/** 登录表单提交 */
function handleLoginSubmit() {
  loginFormRef.value?.validate((valid: boolean) => {
    if (!valid) return;
    loading.value = true;
    userStore
      .login(loginData.value)
      .then(() => {
        const { path, queryParams } = parseRedirect();
        ElMessage.success("登录成功");
        //获取员工信息
        userStore.getInfo();
        router.push({ path: path, query: queryParams });
      })
      .catch((err) => {
        ElMessage.error(err.msg || "登录失败");
      })
      .finally(() => (loading.value = false));
  });
}

/** 解析 redirect 字符串 为 path 和  queryParams */
function parseRedirect(): {
  path: string;
  queryParams: Record<string, string>;
} {
  const query: LocationQuery = route.query;
  const redirect = (query.redirect as string) ?? "/";

  const url = new URL(redirect, window.location.origin);
  const path = url.pathname;
  const queryParams: Record<string, string> = {};

  url.searchParams.forEach((value, key) => {
    queryParams[key] = value;
  });

  return { path, queryParams };
}

/** 切换表单类型 */
// function swithFormType(type: string) {
//   type !== "sign-in"
//     ? ContainerLoginWrapRef.value?.classList.add("sign-up-mode")
//     : ContainerLoginWrapRef.value?.classList.remove("sign-up-mode");
// }
defineOptions({
  name: "Login",
});
</script>

<template>
  <div
    ref="ContainerLoginWrapRef"
    class="login w-100% bg-#fff dark:bg-#1e1e1e min-h-100vh overflow-hidden"
  >
    <!-- 品牌logo移动到这里，在登录表单上方 -->
    <div class="brand-logo flex flex-col items-center mb-30px">
      <img src="@/assets/logo/logo_3.svg" alt="" class="mb-4" />
      <span>锦食坊</span>
    </div>
    <!-- 登录和注册表单 -->
    <div class="forms-container absolute wh-full top-0 left-0">
      <div class="signin-signup absolute top-50% left-75% w-50% z-5">
        <!-- 登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginData"
          :rules="loginRules"
          class="sign-in-form flex-center flex-col z-2"
        >
          <h2 class="text-2.2rem lighr:color-#444 dark:color-#fff mb-10px">
            登录
          </h2>

          <el-form-item prop="username">
            <div class="input-field">
              <el-input
                type="text"
                name="username"
                v-model="loginData.username"
                :placeholder="$t('login.username')"
                :prefix-icon="User"
                style="width: 330px"
                @keyup.enter="handleLoginSubmit"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-field">
              <el-input
                type="password"
                name="password"
                show-password
                v-model="loginData.password"
                :placeholder="$t('login.password')"
                :prefix-icon="Lock"
                style="width: 330px"
                @keyup.enter="handleLoginSubmit"
              />
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              class="btn solid"
              :loading="loading"
              @click.prevent="handleLoginSubmit"
              >立即登录</el-button
            >
          </el-form-item>
        </el-form>

        <!-- 注册表单 -->
        <!-- <el-form class="sign-up-form flex-center flex-col z-1 opacity-0">
          <h2 class="text-2.2rem text-#444 mb-10px">注册</h2>
          <div class="input-field">
            <i class="fas fa-user"></i>
            <input type="text" placeholder="用户名" />
          </div>
          <div class="input-field">
            <i class="fas fa-envelope"></i>
            <input type="email" placeholder="邮箱" />
          </div>
          <div class="input-field">
            <i class="fas fa-lock"></i>
            <input type="password" placeholder="密码" />
          </div>
          <input type="submit" class="btn hover:bg-#4d84e2" value="立即注册" />
        </el-form> -->
      </div>
    </div>

    <div
      class="panels-container absolute wh-full top-0 left-0 grid grid-cols-2"
    >
      <div class="panel left-panel">
        <img src="@/assets/icons/login.svg" class="image" alt="" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import "https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400;500;600;700;800&display=swap";

:deep(.forms-container) {
  .el-form {
    .el-form-item:has(.input-field):not(:last-child) {
      margin-bottom: 26px;
    }
    .el-form-item__error {
      left: 15px;
      padding-top: 6px;
    }
  }
}

.login {
  position: relative;

  &::before {
    position: absolute;
    top: -10%;
    right: 48%;
    z-index: 6;
    width: 2000px;
    height: 2000px;
    content: "";
    background-image: linear-gradient(
      -45deg,
      var(--el-color-primary-light-4) 0%,
      var(--el-color-primary) 100%
    );
    border-radius: 50%;
    transition: 1.8s ease-in-out;
    transform: translateY(-50%);
  }
}

.brand-logo {
  width: 50%;
  float: right;
  margin-top: 10%;
}

.signin-signup {
  display: grid;
  grid-template-columns: 1fr;
  transition: 1s 0.7s ease-in-out;
  transform: translate(-50%, -50%);
}

// 表单共同样式
.sign-in-form,
.sign-up-form {
  grid-row: 1 / 2;
  grid-column: 1 / 2;
  padding: 0 5rem;
  overflow: hidden;
  transition: all 0.2s 0.7s;
}

// 表单项
:deep(.input-field) {
  & .el-input__prefix {
    padding: 0 10px;
  }
  & .el-input__suffix {
    padding-right: 10px;
  }
  & .el-input__wrapper {
    font-size: 1.1rem;
    width: 100%;
    max-width: 380px;
    height: 55px;
    @apply light:bg-[rgba(240,240,240,0.5)];
    border-radius: 55px;

    &::placeholder {
      font-weight: 500;
      color: #aaa;
    }

    & .el-input__inner {
      font-size: 0.9rem;
    }
  }
}

.btn {
  width: 200px;
  height: 49px;
  margin: 10px 0;
  font-weight: 600;
  color: #fff;
  text-transform: uppercase;
  cursor: pointer;
  background-color: var(--el-color-primary);
  border: none;
  border-radius: 49px;
  outline: none;
  transition: 0.5s;
  letter-spacing: 4px;
  &:hover {
    background-color: var(--el-color-primary-light-4);
  }
}

.panels-container {
  .image {
    width: 100%;
    transition: transform 1.1s ease-in-out;
    transition-delay: 0.4s;
  }

  .transparent {
    width: 130px;
    height: 41px;
    margin: 0;
    font-size: 0.8rem;
    font-weight: 600;
    background: none;
    border: 2px solid #fff;
  }

  .panel {
    z-index: 6;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: space-around;
    text-align: center;

    .content {
      color: #fff;
      transition: transform 0.9s ease-in-out;
      transition-delay: 0.6s;
    }

    h3 {
      font-size: 1.5rem;
      font-weight: 600;
      line-height: 1;
    }

    p {
      padding: 0.7rem 0;
      font-size: 0.95rem;
    }
  }

  .left-panel {
    padding: 3rem 17% 2rem 12%;
    pointer-events: all;
  }

  .right-panel {
    padding: 3rem 12% 2rem 17%;
    pointer-events: none;

    .image,
    .content {
      transform: translateX(800px);
    }
  }
}

/* 动画 */
.login.sign-up-mode::before {
  right: 52%;
  transform: translate(100%, -50%);
}

.login.sign-up-mode .left-panel .image,
.login.sign-up-mode .left-panel .content {
  transform: translateX(-800px);
}

.login.sign-up-mode .signin-signup {
  left: 25%;
}

.login.sign-up-mode form.sign-up-form {
  z-index: 2;
  opacity: 1;
}

.login.sign-up-mode form.sign-in-form {
  z-index: 1;
  opacity: 0;
}

.login.sign-up-mode .right-panel .image,
.login.sign-up-mode .right-panel .content {
  transform: translateX(0%);
}

.login.sign-up-mode .left-panel {
  pointer-events: none;
}

.login.sign-up-mode .right-panel {
  pointer-events: all;
}

@media (width <= 870px) {
  .login {
    height: 100vh;
    min-height: 800px;
  }

  .signin-signup {
    top: 95%;
    width: 100%;
    transition: 1s 0.8s ease-in-out;
    transform: translate(-50%, -100%);
  }

  .signin-signup,
  .login.sign-up-mode .signin-signup {
    left: 50%;
  }

  .panels-container {
    grid-template-rows: 1fr 2fr 1fr;
    grid-template-columns: 1fr;
  }

  .panel {
    flex-direction: row;
    grid-column: 1 / 2;
    align-items: center;
    justify-content: space-around;
    padding: 2.5rem 8%;
  }

  .right-panel {
    grid-row: 3 / 4;
  }

  .left-panel {
    grid-row: 1 / 2;
  }

  .image {
    width: 200px;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.6s;
  }

  .panel .content {
    padding-right: 15%;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.8s;
  }

  .panel h3 {
    font-size: 1.2rem;
  }

  .panel p {
    padding: 0.5rem 0;
    font-size: 0.7rem;
  }

  .btn.transparent {
    width: 110px;
    height: 35px;
    font-size: 0.7rem;
  }

  .login::before {
    top: initial;
    right: initial;
    bottom: 68%;
    left: 30%;
    width: 1500px;
    height: 1500px;
    transition: 2s ease-in-out;
    transform: translateX(-50%);
  }

  .login.sign-up-mode::before {
    right: initial;
    bottom: 32%;
    transform: translate(-50%, 100%);
  }

  .login.sign-up-mode .left-panel .image,
  .login.sign-up-mode .left-panel .content {
    transform: translateY(-300px);
  }

  .login.sign-up-mode .right-panel .image,
  .login.sign-up-mode .right-panel .content {
    transform: translateY(0);
  }

  .right-panel .image,
  .right-panel .content {
    transform: translateY(300px);
  }

  .login.sign-up-mode .signin-signup {
    top: 5%;
    transform: translate(-50%, 0);
  }
}

@media (width <= 570px) {
  form {
    padding: 0 1.5rem;
  }

  .image {
    display: none;
  }

  .panel .content {
    padding: 0.5rem 1rem;
  }

  .login {
    padding: 1.5rem;
  }

  .login::before {
    bottom: 72%;
    left: 50%;
  }

  .login.sign-up-mode::before {
    bottom: 28%;
    left: 50%;
  }
}
</style>
