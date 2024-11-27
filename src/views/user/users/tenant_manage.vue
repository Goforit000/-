<template>
  <div class="main-content">
    <el-row :gutter="6" align="middle">
      <el-col :span="6">
        <el-form-item label="租户标识">
          <el-input v-model="searchQuery.tid" placeholder="请输入租户标识"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="租户名称">
          <el-input v-model="searchQuery.tname" placeholder="请输入租户名称"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="租户联系人">
          <el-input v-model="searchQuery.tcontact" placeholder="请输入租户联系人"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="租户电话">
          <el-input v-model="searchQuery.tphone" placeholder="请输入租户电话"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
        <el-button type="info" :icon="Refresh" @click="reset">重置</el-button>
      </el-col>
      <el-col :span="14" class="button-group" style="text-align: center;">
        <el-button type="primary" :icon="Plus" plain @click="showAddTenantForm">新增</el-button>
        <el-button type="danger" :icon="Delete" plain @click="confirmDeleteAll">一键删除</el-button>
        <el-button type="warning" :icon="Download" plain @click="confirmExport">导出</el-button>
      </el-col>
    </el-row>

    <el-table :data="tenants" stripe @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="tid" label="租户标识"></el-table-column>
      <el-table-column prop="tname" label="租户名称"></el-table-column>
      <el-table-column prop="tcontact" label="租户联系人"></el-table-column>
      <el-table-column prop="tphone" label="租户电话"></el-table-column>
      <el-table-column prop="tadmin" label="租户管理员"></el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button size="mini" @click="showEditTenantForm(scope.row)">修改</el-button>
          <el-button size="mini" type="danger" @click="confirmDelete(scope.row.tid)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDeleteDialog" title="系统提示" width="30%">
      <span>是否确认删除租户编号为“{{ deleteTid }}”的数据项？</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="deleteTenant(deleteTid)">确定</el-button>
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

    <el-dialog v-model="showAddTenant" title="新增租户">
      <el-form :model="newTenant" class="add-tenant-form" label-width="100px">
        <el-form-item label="租户名称" required>
          <el-input v-model="newTenant.tname" placeholder="请输入租户名称"></el-input>
        </el-form-item>
        <el-form-item label="租户图标" prop="imageUrl" class="aligned-upload-item">
          <el-upload
            action="http://localhost:1111/tenant/upload/img"
            list-type="picture-card"
            :limit="1"
            :on-remove="handleRemove"
            :on-success="handleSuccess"
            :before-upload="beforeUpload"
            name="file"
            class="upload-box">
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                请上传大小不超过 <span style="color: red;">5MB</span> 格式为 <span style="color: red;">jpg/png/jpeg</span> 的文件
              </div>
            </template>
          </el-upload>
          <el-image v-if="newTenant.imageUrl" :src="newTenant.imageUrl" style="width: 100px; height: 100px; margin-top: 10px;" @click="viewImage(newTenant.imageUrl)"></el-image>
        </el-form-item>
        <el-form-item label="联系人" required>
          <el-input v-model="newTenant.tcontact" placeholder="请输入租户联系人"></el-input>
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="newTenant.tphone" placeholder="请输入联系人电话"></el-input>
        </el-form-item>
        <el-form-item label="管理员" required>
          <el-input v-model="newTenant.tadmin" placeholder="请输入管理员名称"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <TextEditor v-model="newTenant.remark" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetForm">重置</el-button>
        <el-button @click="showAddTenant = false">取消</el-button>
        <el-button type="primary" @click="addTenant">新增</el-button>
      </span>
    </el-dialog>

    <el-dialog v-model="showEditTenant" title="修改租户">
      <el-form :model="currentTenant" class="edit-tenant-form" label-width="100px">
        <el-form-item label="租户名称" required>
          <el-input v-model="currentTenant.tname"></el-input>
        </el-form-item>
        <el-form-item label="租户图标" prop="imageUrl" class="aligned-upload-item">
          <el-upload
            action="http://localhost:1111/tenant/upload/img"
            list-type="picture-card"
            :limit="1"
            :on-remove="handleRemove"
            :on-success="handleSuccess"
            :before-upload="beforeUpload"
            name="file"
            class="upload-box">
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                请上传大小不超过 <span style="color: red;">5MB</span> 格式为 <span style="color: red;">jpg/png/jpeg</span> 的文件
              </div>
            </template>
          </el-upload>
          <el-image v-if="currentTenant.imageUrl" :src="currentTenant.imageUrl" style="width: 100px; height: 100px; margin-top: 10px;" @click="viewImage(currentTenant.imageUrl)"></el-image>
        </el-form-item>
        <el-form-item label="联系人" required>
          <el-input v-model="currentTenant.tcontact"></el-input>
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="currentTenant.tphone"></el-input>
        </el-form-item>
        <el-form-item label="管理员" required>
          <el-input v-model="currentTenant.tadmin"></el-input>
        </el-form-item>
        <el-form-item label="备注" class="form-item-remark">
          <QuillEditor v-model="currentTenant.remark" class="quill-editor-extended" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editResetForm">重置</el-button>
        <el-button @click="showEditTenant = false">取消</el-button>
        <el-button type="primary" @click="editTenant">修改</el-button>
      </span>
    </el-dialog>

    <el-dialog v-model="EditWrongDialogVisible" title="错误" width="500" align-center>
      <span>修改失败：租户名称、联系人、电话、管理员不能为空</span>
    </el-dialog>

    <el-dialog v-model="centerDialogVisible" title="错误" width="500" align-center>
      <span>创建失败：租户名称、联系人、电话、管理员不能为空</span>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Edit, Search, Refresh, Plus, Upload, Download } from '@element-plus/icons-vue';
import TextEditor from '@/components/TextEditor.vue'; // 确保路径正确
import QuillEditor from '@/components/QuillEditor.vue'; // 确保路径正确

const tenants = ref([]);
const selectedTenants = ref();
const searchQuery = reactive({
  tid: '',
  tname: '',
  tcontact: '',
  tphone: '',
});
const currentPage = ref(1);
const totalItems = ref(0);
const pageSize = ref(10);
const showAddTenant = ref(false);
const showEditTenant = ref(false);
const centerDialogVisible = ref(false);
const EditWrongDialogVisible = ref(false);
const showDeleteDialog = ref(false);
const deleteTid = ref(1);

const viewImage = (url: string) => {
  const img = new Image();
  img.src = url;
};

const newTenant = reactive({
  tname: '',
  tcontact: '',
  tphone: '',
  tadmin: '',
  taccount: '',
  tpwd: '',
  remark: '',
  imageUrl: '',
});

const currentTenant = reactive({
  tid: '',
  tname: '',
  tcontact: '',
  tphone: '',
  tadmin: '',
  taccount: '',
  tpwd: '',
  remark: '',
  imageUrl: '',
});

const form = ref({
  tname: '',
  tcontact: '',
  tphone: '',
  tadmin: '',
  remark: ''
});

const resetForm = () => {
  newTenant.tname = '';
  newTenant.tcontact = '';
  newTenant.tphone = '';
  newTenant.tadmin = '';
  newTenant.remark = '';
};

const editResetForm = () => {
  currentTenant.tname = '';
  currentTenant.tcontact = '';
  currentTenant.tphone = '';
  currentTenant.tadmin = '';
  currentTenant.remark = '';
};

const confirmDelete = (tid: number) => {
  deleteTid.value = tid;
  showDeleteDialog.value = true;
};

const imageUrl = ref();
const fileList = ref([]);

const fetchData = async (page = 1) => {
  try {
    const response = await axios.get(`http://localhost:1111/tenant/list`, { params: { page, pageSize: pageSize.value } });
    if (response.data.tenants && response.data.tenants.length > 0) {
      tenants.value = response.data.tenants;
      currentPage.value = response.data.currentPage;
      totalItems.value = response.data.totalItems;
    } else {
      tenants.value = [];
      totalItems.value = 0;
    }
  } catch (error) {
    console.error("获取数据时出错!", error);
  }
};

const search = async () => {
  if (searchQuery.tid.trim() === '' && searchQuery.tname.trim() === '' && searchQuery.tcontact.trim() === '' && searchQuery.tphone.trim() === '') {
    ElMessage({
      message: '请输入查询条件',
      type: 'warning'
    });
  } else {
    try {
      const response = await axios.get('http://localhost:1111/tenant/search', {
        params: {
          tid: searchQuery.tid || '',
          tname: searchQuery.tname || '',
          tcontact: searchQuery.tcontact || '',
          tphone: searchQuery.tphone || '',
          page: currentPage.value,
          pageSize: pageSize.value,
        },
      });

      if (response.data.tenants && response.data.tenants.length > 0) {
        tenants.value = response.data.tenants;
        totalItems.value = response.data.totalItems;
      } else {
        ElMessage({
          message: '未查询到相关租户',
          type: 'warning',
        });
        tenants.value = [];
        totalItems.value = 0;
      }
    } catch (error) {
      console.error("查询租户时出错!", error);
    }
  }
};

const reset = () => {
  searchQuery.tid = '';
  searchQuery.tname = '';
  searchQuery.tcontact = '';
  searchQuery.tphone = '';
  fetchData();
};

const editTenant = async () => {
  if (!currentTenant.tname || !currentTenant.tcontact || !currentTenant.tphone || !currentTenant.tadmin) {
    EditWrongDialogVisible.value = true;
    return;
  }
  try {
    await axios.put(`http://localhost:1111/tenant/update`, currentTenant);
    ElMessage({
      message: '租户修改成功',
      type: 'success',
    });
    showEditTenant.value = false;
    fetchData(currentPage.value);
  } catch (error) {
    console.error("更新租户时出错!", error);
    ElMessage({
      message: '租户修改失败',
      type: 'error',
    });
  }
};

const showAddTenantForm = () => {
  showAddTenant.value = true;
};

const showEditTenantForm = (tenant: any) => {
  Object.assign(currentTenant, tenant);
  showEditTenant.value = true;
};

const addTenant = async () => {
  if (!newTenant.tname || !newTenant.tcontact || !newTenant.tphone || !newTenant.tadmin) {
    centerDialogVisible.value = true;
    return;
  }
  try {
    await axios.post(`http://localhost:1111/tenant/add`, newTenant);
    ElMessage({
      message: '租户创建成功',
      type: 'success',
    });
    showAddTenant.value = false;
    fetchData(currentPage.value);
  } catch (error) {
    console.error("添加租户时出错!", error);
    ElMessage({
      message: '租户添加失败',
      type: 'error',
    });
  }
};

const deleteTenant = async (tid: number) => {
  try {
    await axios.delete(`http://localhost:1111/tenant/delete/${tid}`);
    ElMessage({
      message: '租户删除成功',
      type: 'success',
    });
    showDeleteDialog.value = false;
    fetchData(currentPage.value);
  } catch (error) {
    console.error("删除租户时出错!", error);
    ElMessage({
      message: '租户删除失败',
      type: 'error',
    });
  }
};

const handleRemove = (file: any, fileList: any) => {
  console.log('File removed:', file);
  console.log('Updated file list:', fileList);
  imageUrl.value = null;
};

const handleSuccess = (response: { data: { final_fileName: string | null }; }, file: any, fileList: any) => {
  console.log('Upload success:', response);
  if (response.data && response.data.final_fileName) {
    imageUrl.value = response.data.final_fileName;
    if (showEditTenant.value) {
      currentTenant.imageUrl = imageUrl.value;
    } else {
      newTenant.imageUrl = imageUrl.value;
    }
    if (imageUrl.value) {
      sessionStorage.setItem("imgUrl", imageUrl.value);
    } else {
      console.error('Image URL is null');
    }
  } else {
    console.error('Response data is missing final_fileName');
  }
};

const beforeUpload = (file: { size: number; }) => {
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isLt2M;
};

const confirmDeleteAll = () => {
  if (selectedTenants.value.length === 0) {
    ElMessage({
      type: 'warning',
      message: '请选择需要删除的租户!',
    });
    return;
  }
  ElMessageBox.confirm(`此操作将永久删除选中的租户, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const deleteRequests = selectedTenants.value.map((tenant: any) => {
        return axios.delete(`http://localhost:1111/tenant/del`, {
          params: { tid: tenant.tid }
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

const handleSelectionChange = (val: any[]) => {
  selectedTenants.value = val;
};

const confirmExport = () => {
  ElMessageBox.confirm('是否确认导出所有租户数据项?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    exportTenants();
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消导出',
    });
  });
};

const exportTenants = async () => {
  try {
    const response = await axios.get('http://localhost:1111/tenant/export', {
      responseType: 'blob', // 指定响应数据的类型为 blob
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.style.display = 'none';
    link.href = url;
    link.setAttribute('download', 'tenants.xlsx'); // 设置下载文件的文件名
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败，请稍后重试');
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.main-content {
  padding: 20px;
}

.table-container {
  margin-top: 20px;
}

.pagination-fixed {
  margin-top: 20px;
}

.add-tenant-form,
.edit-tenant-form {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
}

.add-tenant-form .el-form-item {
  margin-bottom: 20px; /* 控制上下边距 */
}

.upload-box {
  display: inline-block; /* 不居中，左对齐 */
  margin-left: 0; /* 左边对齐 */
}

.error-dialog .el-dialog__title {
  color: red;
}

.pagination-fixed {
  position: absolute;
  bottom: 20px; /* 距离容器底部的距离 */
  left: 55%;
  transform: translateX(-50%);
  width: 100%; /* 根据需要调整 */
  display: flex;
  justify-content: center; /* 居中对齐 */
}
</style>
