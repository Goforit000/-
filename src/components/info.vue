<template>
  <div class="container">
    <!-- 左侧用户信息容器 -->
    <el-card class="user-info-card">
      <div slot="header" class="clearfix">
        <span>用户信息</span>
      </div>
      <div class="user-info-content" v-if="userInfoLoaded">
        <img src="https://oss.youlai.tech/youlai-boot/2023/05/16/811270ef31f548af9cffc026dfc3777b.gif?imageView2/1/w/80/h/80" alt="Avatar" class="avatar">
        <p><i class="el-icon-user"></i> 用户名: {{ form.username }}</p>
        <p><i class="el-icon-phone"></i> 手机号码: {{ form.phone }}</p>
        <p><i class="el-icon-mail"></i> 用户邮箱: {{ form.email }}</p>
        <p><i class="el-icon-suitcase"></i> 所属部门: {{ form.department }}</p>
        <p><i class="el-icon-user-solid"></i> 所属角色: {{ form.role }}</p>
        <!-- <p><i class="el-icon-time"></i> 创建日期: {{ userInfo.userCreate }}</p> -->
      </div>
      <div class="user-info-content" v-else>
        <p>正在加载用户信息...</p>
      </div>
    </el-card>

    <!-- 右侧基本资料和修改密码容器 -->
    <el-card class="basic-info-card">
      <div slot="header" class="clearfix">
        <span>基本资料</span>
      </div>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本资料" name="basic-info">
          <el-form :model="form" ref="formRef" label-width="80px">
            <el-form-item label="用户名" required>
              <el-input v-model="form.username" disabled ></el-input>
            </el-form-item>
            <el-form-item label="手机号" required>
              <el-input v-model="form.phone"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" required>
              <el-input v-model="form.email"></el-input>
            </el-form-item>
            <el-form-item label="性别" required>
              <el-radio-group v-model="form.sex">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="角色" required>
              <el-input v-model="form.role" disabled></el-input>
            </el-form-item>
            <el-form-item label="状态" required>
              <el-input v-model="form.status" disabled></el-input>
            </el-form-item>
            <el-form-item label="昵称" required>
              <el-input v-model="form.nick_name"></el-input>
            </el-form-item>
            <el-form-item label="公司" required>
              <el-input v-model="form.enterprise"></el-input>
            </el-form-item>
            <el-form-item label="部门" required>
              <el-input v-model="form.department"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateUserStatus(form)">保存</el-button>
              <el-button type="link">取消</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="change-password">
          <el-form :model="passwordForm" ref="passwordFormRef" label-width="80px">
            <!-- <el-form-item label="当前密码" required>
              <el-input type="password" v-model="passwordForm.currentPassword"></el-input>
            </el-form-item>
            <el-form-item label="新密码" required>
              <el-input type="password" v-model="passwordForm.newPassword"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" required>
              <el-input type="password" v-model="passwordForm.confirmNewPassword"></el-input>
            </el-form-item> -->
            <el-form-item label="当前密码" required>
              <el-input v-model="passwordForm.currentPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码" required>
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认密码" required>
              <el-input v-model="passwordForm.confirmNewPassword" type="password" placeholder="请确认密码" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateUserStatus(form)">保存</el-button>
              <el-button type="link">取消</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

const activeTab = ref('basic-info');
const router = useRouter();

const userInfo = ref({
  username: '',
  phone: '',
  email: '',
  gender: '',
  role: '',
  status: '',
  nick_name: '',
  enterprise: '',
  department: '',
  userCreate: '',
});

const form = ref({
  user_id:'',
  username: '',
  phone: '',
  email: '',
  sex: '',
  role: '',
  status: '',
  nick_name: '',
  enterprise: '',
  department: '',
  password:''
});


const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: '',
});

const formRef = ref();
const passwordFormRef = ref();
const userInfoLoaded = ref(false);
const isUserInfoLoaded = ref(false);


//显示个人资料信息
const loadUserInfo = () => {
  //调用本地仓库
  const storedUsername = localStorage.getItem('username');
  const storedPassword = localStorage.getItem('password');
  
  if (storedUsername && storedPassword) {
    axios.get('http://localhost:1111/user/info', {
      params: {
        username: storedUsername,
        password: storedPassword
      }
    })
    .then(response => {
      if (response.status === 200) {
        const user = response.data;
        console.log(user);
        // 更新页面内容，如显示用户信息
        form.value.user_id = user.user_id;
        form.value.username = user.username;
        form.value.phone = user.phone;
        form.value.email = user.email;
        form.value.sex = user.sex;
        form.value.role = user.role;
        form.value.status = user.status;
        form.value.nick_name = user.nick_name;
        form.value.enterprise= user.enterprise;
        form.value.department = user.department;

        localStorage.setItem("enterprise", user.enterprise);
        
        passwordForm.value.currentPassword = storedPassword;
        userInfoLoaded.value = true;
        isUserInfoLoaded.value = true;
      }
    })
    .catch(error => {
      if (error.response && error.response.status === 401) {
        alert('密码修改成功，请重新登录');
        router.push("/login");
      } else {
        console.error('请求出错', error);
      }
    });
  } else {
    alert('请先登录');
  }
};

const onSubmit = () => {
  formRef.value.validate((valid: any) => {
    if (valid) {
      ElMessage.success('基本资料已保存');
    } else {
      ElMessage.error('请填写完整的基本资料');
      return false;
    }
  });
};

const updateUserStatus = async (user: any) => {
  if(passwordForm.value.newPassword!=null){
          form.value.password = passwordForm.value.newPassword;
        }else{
          form.value.password=form.value.password;
        }
  try {
    await axios.put(`http://localhost:1111/user/update`, user);
    ElMessage({
      message: '用户状态更新成功',
      type: 'success'
    });
    // 不需要再触发loadUserInfo，因为已经更新了
    // isUserInfoLoaded.value = true;
  } catch (error) {
    console.error('更新用户状态时出错!', error);
    ElMessage.success('用户资料修改成功');
  }
  if(form.value.password = passwordForm.value.newPassword){
    alert('密码修改成功，请重新登录');
        router.push("/login");
  }
};

const onChangePassword = () => {
  passwordFormRef.value.validate((valid: any) => {
    if (valid) {
      if (passwordForm.value.newPassword !== passwordForm.value.confirmNewPassword) {
        ElMessage.error('新密码和确认新密码不一致');
        return;
      }
      ElMessage.success('密码已修改');
    } else {
      ElMessage.error('请填写完整的密码信息');
      return false;
    }
  });
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 100px;
  gap: 10px;
  height: 80vh; /* 高度占据视窗的 70% */
}

.user-info-card {
  width: 700px; /* 设置左侧容器宽度 */
  height: calc(100% - 10px); /* 高度占据父容器的 100% 减去间距 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.basic-info-card {
  width: 1200px; /* 设置右侧容器宽度 */
  height: calc(100% - 20px); /* 高度占据父容器的 100% 减去间距 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.user-info-card .user-info-content {
  margin-top: 60px;
}

.user-info-content {
  text-align: center;
}

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  margin-bottom: 15px;
}

.user-info-content p {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin: 30px 0;
}

.user-info-content p i {
  margin-right: 10px;
  font-size: 40px;
}
</style>
