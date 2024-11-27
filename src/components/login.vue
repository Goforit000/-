<template>
  <el-container class="login-container">
    <h1 class="title">软件造价评估系统</h1>
    <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="100px">
      <h2 style="display: flex; justify-content: center; margin-top: 10px">登录</h2>
      <el-form-item label="角色" prop="role">
        <el-radio-group v-model="loginForm.role">
          <el-radio label="租户管理员">租户管理员</el-radio>
          <el-radio label="评估师">评估师</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="用户名" prop="username" v-if="loginForm.role === '租户管理员'" >
        <el-input v-model="loginForm.username" placeholder="请输入用户名/企业名称" style="width: 240px"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="username" v-else>
        <el-input v-model="loginForm.username" placeholder="请输入用户名" style="width: 240px" ></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="loginForm.password" style="width: 240px" type="password" placeholder="请输入密码" show-password/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">登录</el-button>
        <el-button type="text" @click="goToRegister">没有账号？去注册</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

interface LoginForm {
  role: string;
  username: string;
  password: string;
}
const loginFormRef = ref();
const router = useRouter();
const loginForm = ref<LoginForm>({
  role: "租户管理员", // 默认角色
  username: "",
  password: "",
});

// 表单验证规则
const rules = {
  role: [{ required: true, message: "请选择角色", trigger: "change" }],
  username: [
    { required: true, message: "请输入用户名/企业名称", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 8, message: "密码不能少于8位", trigger: "blur" },
  ],
};
// 登录按钮动作
const onSubmit = () => {
  loginFormRef.value.validate((valid: boolean) => {
    if (valid) {
      login();
    } else {
      console.log("表单验证失败");
      return false;
    }
  });
};
// 登录逻辑
// 硬编码的账号信息
const predefinedAccounts: Record<"租户管理员" | "评估师", { username: string; password: string }> = {
  "租户管理员": {
    username: "admin",
    password: "admin1234",
  },
  "评估师": {
    username: "pinggushi111",
    password: "pinggushi1234",
  },
};

// 登录逻辑
const login = () => {
  const { role, username, password } = loginForm.value;

  // 确保 role 是 "租户管理员" 或 "评估师"
  if (role in predefinedAccounts) {
    const account = predefinedAccounts[role as "租户管理员" | "评估师"];

    if (account.username === username && account.password === password) {
      // 模拟登录成功
      localStorage.setItem("username", username);
      localStorage.setItem("role", role);
      ElMessage.success("登录成功，跳转到首页");
      setTimeout(() => {
        sessionStorage.setItem("username", username);
        router.push("/home");
      }, 1000);
    } else {
      // 登录失败
      ElMessage.error("用户名或密码错误");
    }
  } else {
    ElMessage.error("无效的角色选择");
  }
};

// 跳转注册
const goToRegister = () => {
  router.push("/register");
};
</script>


<!-- //背景 -->
<style>
body {
background-image:url(../assets/background.jpg );
background-size:100%;background-position: center;background-repeat:no-repeat;background-attachment:fixed;margin: 0;
padding: 0;font-family:Arial,sans-serif;height: 100vh;display: flex;
justify-content: center;align-items: center;
}
</style>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 100px auto;
  padding: 20px;
  width: 100%;
  max-width: 750px;
  height: 450px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background-color: #fff;
  position: relative;
}

.title {
  text-align: center;
  margin-right: 20px;
  color: #333;
  margin-bottom: 20px;
  font-size: 2em;
  font-weight: bold;
}

.el-form-item {
  display: flex;
  align-items: center;
  position: relative;
  margin-bottom: 20px;
}

.el-input {
  width: 240px;
}

.el-button {
  margin-top: 20px;
  width: 100%;
  border-radius: 20px;
  padding: 10px;
  font-weight: bold;
}

.el-button--primary {
  background-color: #2193b0;
  border-color: #2193b0;
  color: white;
}

.el-button--text {
  margin-left: 10px;
  color: #6dd5ed;
}
</style>
