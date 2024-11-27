<template>
  <div class="container">
    <div class="sidebar">
      <el-input
        v-model="filterText"
        style="width: 240px"
        placeholder="请输入部门名称"
      />
      <el-tree
        ref="treeRef"
        style="max-width: 600px"
        class="filter-tree"
        :data="data"
        :props="defaultProps"
        default-expand-all
        :filter-node-method="filterNode"
      />
    </div>
    <div class="main-content">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="用户名称">
            <el-input v-model="searchQuery.username" placeholder="请输入用户名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="手机号码">
            <el-input v-model="searchQuery.phone" placeholder="请输入手机号码"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="状态">
            <el-select v-model="searchQuery.status" placeholder="用户状态">
              <el-option label="正常" value="正常"></el-option>
              <el-option label="禁用" value="禁用"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="searchQuery.dateRange"
              type="daterange"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
        <el-button type="info" :icon="Refresh" @click="reset">重置</el-button>
        <el-col :span="14">
          <el-button type="primary" :icon="Plus" plain @click="showAddUserForm">新增</el-button>
          <el-button type="success" :icon="Edit" plain>修改</el-button>
          <el-button type="info" :icon="Delete" plain @click="confirmDeleteAll">一键删除</el-button>
          <el-button type="warning" :icon="Upload" plain>导入</el-button>
          <el-button type="danger" :icon="Download" plain @click="confirmExport">导出</el-button>
        </el-col>
      </el-row>

      <el-table :data="filteredUsers" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="user_id" label="用户编号"></el-table-column>
        <el-table-column prop="username" label="用户名称"></el-table-column>
        <el-table-column prop="nick_name" label="用户昵称"></el-table-column>
        <el-table-column prop="enterprise" label="公司"></el-table-column>
        <el-table-column prop="department" label="部门"></el-table-column>
        <el-table-column prop="phone" label="手机号码"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template v-slot="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="'正常'"
              :inactive-value="'禁用'"
              @change="updateUserStatus(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="userCreate" label="创建时间">
          <template v-slot="scope">
            {{ formatDate(scope.row.userCreate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="mini" @click="showEditUserForm(scope.row)">修改</el-button>
            <el-button size="mini" type="danger" @click="confirmDelete(scope.row.user_id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="showDeleteDialog" title="系统提示" width="30%">
        <span>是否确认删除用户编号为“{{ deleteUser_id }}”的数据项？</span>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="deleteUser(deleteUser_id)">确定</el-button>
        </span>
      </el-dialog>

      <el-pagination
        class="pagination-fixed"
        layout="prev, pager, next, jumper"
        :total="totalItems"
        :page-size="pageSize"
        :current-page.sync="currentPage"
        @current-change="fetchData">
      </el-pagination>

      <el-dialog v-model="showEditUser" title="修改用户">
        <el-form :model="currentUser" ref="currentUserForm" label-width="100px">
          <el-form-item label="用户名" required>
            <el-input v-model="currentUser.username"></el-input>
          </el-form-item>
          <el-form-item label="用户昵称" required>
            <el-input v-model="currentUser.nick_name"></el-input>
          </el-form-item>
          <el-form-item label="密码" required>
            <el-input type="password" v-model="currentUser.password"></el-input>
          </el-form-item>
          <el-form-item label="手机号码">
            <el-input v-model="currentUser.phone"></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="currentUser.email"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="currentUser.sex">
              <el-option label="男" value="male"></el-option>
              <el-option label="女" value="female"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="currentUser.status" :active-value="'正常'" :inactive-value="'禁用'"></el-switch>
          </el-form-item>
          <el-form-item label="企业">
            <el-input v-if="isTenantAdmin" v-model="currentUser.enterprise" disabled></el-input>
            <el-input v-else v-model="currentUser.enterprise" placeholder="请输入企业"></el-input>
          </el-form-item>
          <el-form-item label="部门">
            <el-input v-model="currentUser.department"></el-input>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker v-model="currentUser.userCreate" type="datetime"></el-date-picker>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showEditUser = false">取消</el-button>
          <el-button type="primary" @click="editUser">确定</el-button>
        </span>
      </el-dialog>

      <el-dialog v-model="showAddUser" title="创建用户">
        <el-form :model="newUser" ref="addUserForm" label-width="100px">
          <el-form-item label="用户名" prop="username" required>
            <el-input v-model="newUser.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="用户名称" prop="nick_name" required>
            <el-input v-model="newUser.nick_name" placeholder="请输入用户名称"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password" required>
            <el-input type="password" v-model="newUser.password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item label="手机号码">
            <el-input v-model="newUser.phone" placeholder="请输入手机号码"></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="newUser.email" placeholder="请输入邮箱"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="newUser.sex" placeholder="请选择性别">
              <el-option label="男" value="male"></el-option>
              <el-option label="女" value="female"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="newUser.status" :active-value="'正常'" :inactive-value="'禁用'"></el-switch>
          </el-form-item>
          <el-form-item label="企业">
            <el-input v-if="isTenantAdmin" v-model="newUser.enterprise" disabled></el-input>
            <el-input v-else v-model="newUser.enterprise" placeholder="请输入企业"></el-input>
          </el-form-item>
          <el-form-item label="部门">
            <el-input v-model="newUser.department" placeholder="请输入部门"></el-input>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker v-model="newUser.userCreate" type="datetime" placeholder="请选择创建时间"></el-date-picker>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showAddUser = false">取消</el-button>
          <el-button type="primary" @click="addUser">创建</el-button>
        </span>
      </el-dialog>

      <el-dialog v-model="EditWrongDialogVisible" title="错误" width="500" align-center>
        <span>修改失败：用户名称、昵称、密码不能为空</span>
      </el-dialog>

      <el-dialog v-model="centerDialogVisible" title="错误" width="500" align-center>
        <span>创建失败：用户名称、昵称、密码不能为空</span>
      </el-dialog>
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted, reactive, watch, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ElTree } from 'element-plus';
import { Delete, Edit, Search, Share, Upload, Refresh, Plus, Download } from '@element-plus/icons-vue';
import moment from 'moment'; // 导入 moment 库
import router from '../../../router';

interface Tree {
  [key: string]: any;
}

interface User {
  user_id: number;
  username: string;
  password: string;
  phone: string;
  email: string;
  sex: string;
  enterprise: string;
  department: string;
  userCreate: string;
}
const users = ref<User[]>([]);
const selectedUsers = ref<User[]>([]);
const searchQuery = reactive({
  username: '',
  phone: '',
  status: '',
  dateRange: []
});

const currentPage = ref(1);
const totalItems = ref(0);
const pageSize = ref(10);
const showAddUser = ref(false);
const showEditUser = ref(false);
const showDeleteDialog = ref(false);
const deleteUser_id = ref(1);
const EditWrongDialogVisible = ref(false);
const centerDialogVisible = ref(false);

const confirmDelete = (user_id: number) => {
  deleteUser_id.value = user_id;
  showDeleteDialog.value = true;
};

const showEditUserForm = (user: any) => {
  Object.assign(currentUser, user);
  showEditUser.value = true;
};

const newUser = reactive({
  username: '',
  password: '',
  phone: '',
  email: '',
  sex: '',
  is_admin: false,
  status: '正常', // 默认值为正常
  nick_name: '',
  enterprise: localStorage.getItem('enterprise'),
  department: '',
  userCreate: ''
});

const currentUser = reactive({
  id: '',
  username: '',
  password: '',
  phone: '',
  email: '',
  sex: '',
  is_admin: false,
  status: '正常', // 默认值为正常
  nick_name: '',
  enterprise: '',
  department: '',
  userCreate: ''
});

const filterText = ref('');
const treeRef = ref<InstanceType<typeof ElTree>>();

const defaultProps = {
  children: 'children',
  label: 'label'
};

watch(filterText, (val) => {
  treeRef.value!.filter(val);
});

const filterNode = (value: string, data: Tree) => {
  if (!value) return true;
  return data.label.includes(value);
};

const data: Tree[] = [
  {
    id: 0,
    label: '测盟汇',
    children: [
      {
        id: 1,
        label: '深圳总公司',
        children: [
          {
            id: 4,
            label: '人力部门'
          },
          {
            id: 9,
            label: '管理部门' // 新增管理部门
          }
        ]
      },
      {
        id: 2,
        label: '上海分公司',
        children: [
          {
            id: 5,
            label: '人力部门'
          },
          {
            id: 6,
            label: '财务部门'
          },
          {
            id: 10,
            label: '管理部门' // 新增管理部门
          }
        ]
      },
      {
        id: 3,
        label: '北京分公司',
        children: [
          {
            id: 7,
            label: '人力部门'
          },
          {
            id: 8,
            label: '财务部门'
          },
          {
            id: 11,
            label: '管理部门' // 新增管理部门
          }
        ]
      }
    ]
  }
];

// 计算属性，判断用户是否为租户管理员
const isTenantAdmin = computed(() => {
  return localStorage.getItem('role') === '租户管理员';
});

const fetchData = async (page = 1) => {
  try {
    const params: any = { page, pageSize: pageSize.value };
    const role = localStorage.getItem('role');
    
    if (role !== '超级管理员') {
      params.enterprise = localStorage.getItem('enterprise');
    }

    const response = await axios.get('http://localhost:1111/user/userlist', { params });
    console.log(response.data);

    if (response.data.users && response.data.users.length > 0) {
      users.value = response.data.users;
      currentPage.value = response.data.currentPage;
      totalItems.value = response.data.totalItems;
    } else {
      users.value = [];
      totalItems.value = 0;
    }
  } catch (error) {
    console.error('获取数据时出错!', error);
  }
};

const search = async () => {
  if (
    searchQuery.username.trim() === '' &&
    searchQuery.phone.trim() === '' &&
    searchQuery.status.trim() === '' &&
    searchQuery.dateRange.length === 0
  ) {
    ElMessage({
      message: '请输入查询条件',
      type: 'warning'
    });
  } else {
    try {
      const response = await axios.get('http://localhost:1111/user/search', {
        params: {
          username: searchQuery.username || '',
          phone: searchQuery.phone || '',
          status: searchQuery.status || '',
          startDate: searchQuery.dateRange[0] ? moment(searchQuery.dateRange[0]).format('YYYY-MM-DD') : '',
          endDate: searchQuery.dateRange[1] ? moment(searchQuery.dateRange[1]).format('YYYY-MM-DD') : '',
          page: currentPage.value,
          pageSize: pageSize.value
        }
      });

      if (response.data.users && response.data.users.length > 0) {
        users.value = response.data.users;
        totalItems.value = response.data.totalItems;
        console.log(users.value);
      } else {
        ElMessage({
          message: '未查询到相关用户',
          type: 'warning'
        });
        users.value = [];
        totalItems.value = 0;
      }
    } catch (error) {
      console.error('查询用户时出错!', error);
    }
  }
};

const reset = () => {
  searchQuery.username = '';
  searchQuery.phone = '';
  searchQuery.status = '';
  searchQuery.dateRange = [];
  fetchData();
};

const editUser = async () => {
  if (!currentUser.username || !currentUser.password || !currentUser.nick_name) {
    EditWrongDialogVisible.value = true;
    return;
  }
  try {
    await axios.put(`http://localhost:1111/user/update`, currentUser);
    ElMessage({
      message: '用户更新成功',
      type: 'success'
    });
    showEditUser.value = false;
    fetchData(currentPage.value);
  } catch (error) {
    console.error('更新用户时出错!', error);
    ElMessage({
      message: '用户更新失败',
      type: 'error'
    });
  }
};

const showAddUserForm = () => {
  showAddUser.value = true;
  console.log(showAddUser.value);
};

const addUser = async () => {
  if (!newUser.username || !newUser.nick_name || !newUser.password) {
    centerDialogVisible.value = true;
    return;
  }
  try {
    await axios.post(`http://localhost:1111/user/add`, newUser);
    ElMessage({
      message: '用户添加成功',
      type: 'success'
    });
    showAddUser.value = false;
    fetchData(currentPage.value);
  } catch (error) {
    console.error('添加用户时出错!', error);
    ElMessage({
      message: '用户添加失败',
      type: 'error'
    });
  }
};

const deleteUser = async (user_id: number) => {
  try {
    await axios.delete(`http://localhost:1111/user/delete/${user_id}`);
    ElMessage({
      message: '用户删除成功',
      type: 'success'
    });
    showDeleteDialog.value = false;
    fetchData(currentPage.value);
  } catch (error) {
    console.error('删除用户时出错!', error);
    ElMessage({
      message: '用户删除失败',
      type: 'error'
    });
  }
};

const confirmDeleteAll = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage({
      type: 'warning',
      message: '请选择需要删除的用户!',
    });
    return;
  }
  ElMessageBox.confirm(`此操作将永久删除选中的用户, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const deleteRequests = selectedUsers.value.map((user: any) => {
        return axios.delete(`http://localhost:1111/user/del`, {
                    params: { user_id: user.user_id }
                });
      });
      await Promise.all(deleteRequests);
      fetchData();
      ElMessage({
        type: 'success',
        message: '删除成功!',
      });
    } catch (error) {
      console.error("删除失败", error);
    }
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除',
    });
  });
};

const handleSelectionChange = (val: User[]) => {
  selectedUsers.value = val;
};

const updateUserStatus = async (user: any) => {
  try {
    await axios.put(`http://localhost:1111/user/update`, user);
    ElMessage({
      message: '用户更新成功',
      type: 'success'
    });
  } catch (error) {
    console.error('更新用户时出错!', error);
    ElMessage({
      message: '用户更新失败',
      type: 'error'
    });
  }
};

const formatDate = (date: string) => {
  return moment(date).format('YYYY-MM-DD HH:mm:ss');
};

const loadUserInfo = () => {
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
        localStorage.setItem("enterprise", user.enterprise);
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

//导出
const confirmExport = () => {
    ElMessageBox.confirm('是否确认导出所有用户数据项?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        exportUsers();
    }).catch(() => {
        ElMessage({
            type: 'info',
            message: '已取消导出',
        });
    });
};

const exportUsers = async () => {
    try {
        const response = await axios.get('http://localhost:1111/user/export', {
            responseType: 'blob', // 指定响应数据的类型为 blob
        });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.style.display = 'none';
        link.href = url;
        link.setAttribute('download', 'users.xlsx'); // 设置下载文件的文件名
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('导出失败:', error);
        ElMessage.error('导出失败，请稍后重试');
    }
};

// 计算属性，用于根据角色和公司信息过滤用户列表
const filteredUsers = computed(() => {
  const role = localStorage.getItem('role');
  const enterprise = localStorage.getItem('enterprise');
  if (role === '超级管理员') {
    return users.value;
  } else {
    if (users.value != null) {
      return users.value.filter(user => user.enterprise === enterprise);
    }
  }
});

onMounted(() => {
  loadUserInfo();
  fetchData();
});
</script>


<style scoped>
.container {
  display: flex;
}

.sidebar {
  width: 250px;
  padding: 20px;
  border-right: 1px solid #ccc;
}

.main-content {
  padding: 20px;
}

.pagination-fixed {
  margin-top: 20px;
}

.add-user-form,
.edit-user-form {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
}
.search-input >>> .el-input__icon {
  color: grey;
}

.el-tree .el-tree-node__content {
  line-height: 40px !important; /* 调整为合适的行高 */
}

.el-input {
  margin-bottom: 20px; /* 调整为合适的间距 */
}
</style>
