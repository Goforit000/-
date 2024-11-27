<template>
  <div class="project-evaluation">
    <el-row>
      <el-col :span="8">
        <el-input
          v-model="projectName"
          placeholder="请输入项目名称"
          :style="{ width: '300px' }"
        />
      </el-col>

      <el-col :span="8">
        <el-select
          v-model="status"
          placeholder="状态"
          :style="{ width: '300px' }"
        >
          <el-option label="全部" value="all" />
          <el-option label="待确认" value="pending" />
          <el-option label="评估完成" value="completed" />
        </el-select>
      </el-col>

      <el-col :span="8">
        <el-button type="primary" @click="searchProjects">搜索</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-col>
    </el-row>

    <el-button type="primary" @click="openDialog(false)" class="new-project-btn">新建项目</el-button>

    <el-row :gutter="20" class="project-cards">
      <el-col :span="8" v-for="project in projects" :key="project.projectId">
        <el-card>
          <div slot="header" class="clearfix">
            <span>{{ project.projectName }}</span>
            <el-tag v-if="project.status === 'completed'" type="success">评估完成</el-tag>
            <el-tag v-else type="warning">待确认</el-tag>
          </div>
          <p>评估金额: {{ project.amount }} 万元</p>
          <p>评估时间: {{ project.startTime }}</p>
          <p>完成时间: {{ project.endTime }}</p>
          <p>备注信息: {{ project.note }}</p>
          <div class="card-actions">
            <el-button type="link" @click="openDialog(true, project)">编辑</el-button>
            <el-button type="link" @click="detail(project.projectId)">详情</el-button>
            <el-button type="link" @click="uploadFileToProject(project.projectId)">上传文件</el-button>
            <el-button type="link" @click="deleteProject(project.projectId)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页 -->
    <div class="pagination-container" style="margin-top: 20px">
      <el-pagination
        background
        layout="total, prev, pager, next, jumper"
        :total="total"
        :current-page.sync="currentPage"
        :page-size.sync="pageSize"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <el-dialog :title="isEditMode ? '编辑项目' : '新建项目'" v-model="dialogVisible" width="500px">
      <el-form :model="newProjectForm" ref="newProjectFormRef" label-width="120px">
        <el-form-item label="项目名称">
          <el-input 
            v-model="newProjectForm.project_name" 
            placeholder="请输入项目名称" 
            clearable 
          />
        </el-form-item>

        <el-form-item label="备注信息">
          <el-input 
            v-model="newProjectForm.note" 
            type="textarea" 
            placeholder="请输入备注信息" 
            :autosize="{ minRows: 3, maxRows: 5 }" 
            clearable 
          />
        </el-form-item>

        <el-form-item label="选择评估师">
          <el-select v-model="newProjectForm1.evaluators" placeholder="选择评估师">
            <el-option
              v-for="evaluator in evaluators"
              :key="evaluator.value"
              :label="evaluator.label"
              :value="evaluator.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNewProject">
          {{ isEditMode ? '保存' : '确定' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import router from '../../router';

// 搜索筛选状态
const projectName = ref('');
const status = ref('all');
const currentPage = ref(1);
const pageSize = ref(6);
const total = ref(0);
const evaluators = ref([
      { label: "评估师1", value: "评估师1" },
      { label: "评估师2", value: "评估师2" },
      { label: "评估师3", value: "评估师3" },
    ]);

// 定义项目类型
interface Project {
  projectId: number;
  projectName: string;
  amount: number;
  startTime: string;
  endTime: string;
  note: string;
  status: 'pending' | 'completed';
}

// 项目列表
const projects = ref<Project[]>([]);

// 控制弹框显示和编辑模式
const dialogVisible = ref(false);
const isEditMode = ref(false);
const selectedProjectId = ref<number | null>(null);

// 弹框表单
const newProjectForm = reactive({
  project_name: '',
  note: '',
});
const newProjectForm1 = reactive({
  project_name: '',
  note: '',
  evaluators:'',
});
//详情
const detail = (projectId:number) => { 
  router.push({
    name: 'detailmenu',  // 使用路由的 subsystemName
    params: { projectId: projectId },  // 传递 projectId
    // query: { fileName: fileName.value },     // 如果需要传递 fileName，也可以继续使用 query
  });
};
// 获取项目列表
const fetchProjects = async () => {
  try {
    console.log("s",status.value);
    const response = await axios.get("http://localhost:8080/projects/list", {
      params: { page: currentPage.value, size: pageSize.value, name: projectName.value,status:status.value},
    });
    if (response.data && response.data.data) {
      projects.value = response.data.data;
      console.log("project",projects.value);
      total.value = response.data.total || 0;
    } else {
      projects.value = [];
      total.value = 0;
    }
  } catch (error) {
    ElMessage.error('加载项目失败');
    console.error(error);
  }
};

// 打开弹框（新增或编辑）
const openDialog = (editMode: boolean, project: Project | null = null) => {
  isEditMode.value = editMode;
  dialogVisible.value = true;

  if (editMode && project) {
    selectedProjectId.value = project.projectId;
    newProjectForm.project_name = project.projectName;
    newProjectForm.note = project.note;
  } else {
    selectedProjectId.value = null;
    newProjectForm.project_name = '';
    newProjectForm.note = '';
  }
};
const uploadFileToProject = (projectId: number) => {
      // 跳转到 Uploadmenu.vue 页面，并传递 projectId 作为路径参数
      router.push({ name: 'uploadMenu', params: { projectId: projectId.toString() } });
    };
// 提交项目表单
const submitNewProject = async () => {
  const payload = {
    projectName: newProjectForm.project_name,
    note: newProjectForm.note,
  };

  try {
    if (isEditMode.value) {
      await axios.put("http://localhost:8080/projects/update", {
        ...payload,
        projectId: selectedProjectId.value,
      });
      ElMessage.success('编辑项目成功');
    } else {
      await axios.post("http://localhost:8080/projects/add", payload);
      ElMessage.success('创建项目成功');
    }
    dialogVisible.value = false;
    fetchProjects();
  } catch (error) {
    ElMessage.error(isEditMode.value ? '编辑项目失败' : '创建项目失败');
    console.error(error);
  }
};

// 删除项目
const deleteProject = async (projectId: number) => {
  try {
    await axios.delete("http://localhost:8080/projects/delete", { params: { projectId } });
    ElMessage.success('删除项目成功');
    fetchProjects();
  } catch (error) {
    ElMessage.error('删除项目失败');
    console.error(error);
  }
};

// 搜索项目
const searchProjects = async () => {
  currentPage.value = 1; // 重置分页为第一页
  try {
    const response = await axios.get("http://localhost:8080/projects/search", {
      params: {
        projectName: projectName.value || null, // 项目名称，支持模糊搜索
        status: status.value || null,
      },
    });
    if (response.data.isOK === true) {
      // 更新项目列表和总条目数
      projects.value = response.data.data || [];
      ElMessage.success('搜索成功');
    } else {
      ElMessage.error('搜索项目失败');
    }
    
  } catch (error) {
    ElMessage.error('搜索项目失败');
    console.error(error);
  }
};


// 重置筛选条件
const resetFilters = () => {
  projectName.value = '';
  status.value = 'all';
  fetchProjects();
};

// 分页
const handlePageChange = async (page: number) => {
  currentPage.value = page;
  await fetchProjects();
};

const handleSizeChange = async (size: number) => {
  pageSize.value = size;
  await fetchProjects();
};

// 初始化加载
onMounted(() => {
  fetchProjects();
});
</script>


<style scoped>
.pagination-container {
    display: flex;
    justify-content: center;
    align-items: center;
}
.project-evaluation {
  padding: 20px;
}
.new-project-btn {
  margin: 20px 0;
}
.project-cards {
  margin-top: 20px;
}
.card-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
</style>