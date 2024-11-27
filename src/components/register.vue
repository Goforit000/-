
  <template>
    <el-container class="register-container">
      <h1 class="title">租户注册</h1>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="企业租户表" name="tenant-form">
          <el-form ref="tenantFormRef" :model="registerForm" :rules="tenantRules" label-width="100px">
            <el-form-item label="企业名称" prop="tname">
              <el-input v-model="registerForm.tname" placeholder="请输入企业名称" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="企业账号" prop="taccount">
              <el-input v-model="registerForm.taccount" placeholder="请输入企业账号" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="企业联系人" prop="tcontact">
              <el-input v-model="registerForm.tcontact" placeholder="请输入企业联系人" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="企业电话" prop="tphone">
              <el-input v-model="registerForm.tphone" placeholder="请输入企业联系电话" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="企业管理员" prop="tadmin">
              <el-input v-model="registerForm.tadmin" placeholder="请输入企业管理员" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="tpwd">
              <el-input v-model="registerForm.tpwd" type="password" placeholder="请输入密码" style="width: 240px" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmtpwd">
              <el-input v-model="registerForm.confirmtpwd" type="password" placeholder="请确认密码" style="width: 240px" show-password></el-input>
            </el-form-item>
            <el-form-item label="验证码" prop="captcha" class="captcha-container">
              <div class="captcha-wrapper">
                <el-input v-model="registerForm.captcha" placeholder="请输入验证码" class="captcha-input"></el-input>
                <Identify :identifyCode="identifyCode" @click="refreshCode" />
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
  
        <el-tab-pane label="租户管理员用户表" name="admin-form">
          <el-form ref="adminFormRef" :model="adminForm" :rules="adminRules" label-width="100px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="adminForm.username" :disabled="true" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="用户密码" prop="password">
              <el-input v-model="adminForm.password" type="password" :disabled="true" style="width: 240px" show-password></el-input>
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="adminForm.phone" placeholder="请输入电话" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="adminForm.email" placeholder="请输入邮箱" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="adminForm.sex">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="角色" prop="role">
              <el-input v-model="adminForm.role" :disabled="true" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-switch v-model="adminForm.status" active-value="正常" inactive-value="禁用"></el-switch>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="adminForm.nickname" :disabled="true" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="公司" prop="enterprice">
              <el-input v-model="adminForm.enterprice" :disabled="true" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="部门" prop="department">
              <el-input v-model="adminForm.department" placeholder="请输入部门" style="width: 240px"></el-input>
            </el-form-item>
            <el-form-item label="创建时间" prop="userCreate">
              <el-date-picker v-model="adminForm.userCreate" type="datetime" placeholder="选择日期和时间" style="width: 240px"></el-date-picker>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm">注册</el-button>
      </el-form-item>
  
      <el-button type="text" @click="goToLogin">已有账号？去登录</el-button>
    </el-container>
  </template>
  
  
  <script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { ElMessage, ElForm } from 'element-plus';
import Identify from "@/components/Identify.vue";
import router from '@/router';
import axios from 'axios';

const identifyCode = ref('');
const identifyCodes = ref("1234567890abcdefjhijklinopqrsduvwxyz");

// 重置验证码
const refreshCode = () => {
  identifyCode.value = "";
  makeCode(identifyCode, 4);
};

// 生成验证码
const makeCode = (o: any, l: number) => {
  for (let i = 0; i < l; i++) {
    identifyCode.value += identifyCodes.value[randomNum(0, identifyCodes.value.length)];
  }
};

const randomNum = (min: number, max: number) => {
  return Math.floor(Math.random() * (max - min) + min);
};

const registerForm = reactive({
  tname: '',
  taccount: '',
  tcontact: '',
  tphone: '',
  tadmin: '',
  tpwd: '',
  confirmtpwd: '',
  captcha: ''
});

const adminForm = reactive({
  username: '',
  password: '',
  phone: '',
  email: '',
  sex: '',
  role: '租户管理员',
  status: '正常',
  nickname: '',
  enterprice: '',
  department: '',
  userCreate: ''
});

// Watch the registerForm to update adminForm
watch(() => registerForm.taccount, (newVal) => { adminForm.username = newVal });
watch(() => registerForm.tpwd, (newVal) => { adminForm.password = newVal });
watch(() => registerForm.tadmin, (newVal) => { adminForm.nickname = newVal });
watch(() => registerForm.tname, (newVal) => { adminForm.enterprice = newVal });

const confirmtpwdValidator = (rule: any, value: string, callback: (error?: Error) => void) => {
  if (value !== registerForm.tpwd) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

//租户表表单验证
const tenantRules = {
  tname: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  taccount: [{ required: true, message: '请输入企业账号', trigger: 'blur' }],
  tcontact: [{ required: true, message: '请输入企业联系人', trigger: 'blur' }],
  tphone: [
    { required: true, message: "请输入企业电话", trigger: "blur" },
    {
      type: "string",
      message: "请输入有效的电话号码",
      trigger: ["blur", "change"],
    },
  ],
  tadmin: [{ required: true, message: '请输入企业管理员', trigger: 'blur' }],
  tpwd: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 8, message: "密码不能少于8位", trigger: "blur" },
  ],
  confirmtpwd: [
    { required: true, message: "请确认密码", trigger: "blur" },
    { validator: confirmtpwdValidator, trigger: "blur" },
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value != identifyCode.value) {
          callback(new Error('验证码输入错误'));
          refreshCode();
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const adminRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'blur' }],
  role: [{ required: true, message: '请输入角色', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  enterprice: [{ required: true, message: '请输入公司', trigger: 'blur' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  userCreate: [{ required: true, message: '请选择创建时间', trigger: 'blur' }],
};

const activeTab = ref('tenant-form');
const tenantFormRef = ref<InstanceType<typeof ElForm>>();
const adminFormRef = ref<InstanceType<typeof ElForm>>();

const submitForm = () => {
  if (tenantFormRef.value && adminFormRef.value) {
    tenantFormRef.value.validate((validTenant: boolean) => {
      if (validTenant) {
        adminFormRef.value.validate(async (validAdmin: boolean) => {
          if (validAdmin) {
            try {
              const fd = new FormData();
              fd.append('tname', registerForm.tname);
              fd.append('taccount', registerForm.taccount);
              fd.append('tcontact', registerForm.tcontact);
              fd.append('tphone', registerForm.tphone);
              fd.append('tadmin', registerForm.tadmin);
              fd.append('tpwd', registerForm.tpwd);
              // 添加租户管理员表中的相关变量
              fd.append('username', adminForm.username);
              fd.append('password', adminForm.password);
              fd.append('phone', adminForm.phone);
              fd.append('email', adminForm.email);
              fd.append('sex', adminForm.sex);
              fd.append('role', adminForm.role);
              fd.append('status', adminForm.status);
              fd.append('nickname', adminForm.nickname);
              fd.append('enterprice', adminForm.enterprice);
              fd.append('department', adminForm.department);
              fd.append('userCreate', adminForm.userCreate);

              const response = await axios.post('http://localhost:1111/user/register', fd);
              if (response.data.isOk) {
                ElMessage.success('注册成功，跳转到登录页');
                setTimeout(() => {
                  router.push('/login');
                }, 1000);
              } else {
                ElMessage.error(response.data.msg);
              }
            } catch (error) {
              ElMessage.error('注册失败');
              console.log(error);
            }
          } else {
            ElMessage.error('租户管理员用户表单验证失败，请检查输入项');
          }
        });
      } else {
        ElMessage.error('企业租户表单验证失败，请检查输入项');
      }
    });
  }
};

const goToLogin = () => {
  router.push('/login');
};
</script>

  
  <style scoped>
  .register-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 100px auto;
    padding: 20px;
    width: 100%;
    max-width: 700px;
    height: auto;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    background-color: #fff;
    position: relative;
  }
  
  .title {
    text-align: center;
    margin-bottom: 20px;
    color: #333;
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
    width: 200%;
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
  
  .captcha-container .captcha-wrapper {
    display: flex;
    align-items: center; /* 垂直居中 */
  }
  
  .captcha-container .captcha-input {
    margin-right: 10px; /* 设置验证码和输入框之间的间距 */
  }
  </style>
  