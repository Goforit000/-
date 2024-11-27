<template>
  <el-main class="container">
    <!-- 左侧容器 -->
    <el-scrollbar class="left-panel">
      <div class="left-container">
        <!-- 使用 VueOfficeDocx 显示文件 -->
        <VueOfficeDocx :src="docxSrc" />
      </div>
    </el-scrollbar>

    <!-- 右侧容器 -->
    <el-scrollbar class="right-panel">
      <div class="right-container">
        <!-- 表格区域：子系统信息 -->
        <div class="table-container">
          <el-button type="success" @click="Ai">没有思路？试试AI</el-button>
          <el-table v-loading="loading":data="subsystemsData" style="width: 100%">
            <el-table-column label="子系统名称">
              <template #default="scope">
                <el-input
                  v-model="scope.row.subsystemName"
                  placeholder="请输入子系统名称"
                ></el-input>
              </template>
            </el-table-column>

            <el-table-column label="子系统描述">
              <template #default="scope">
                <el-input
                  v-model="scope.row.subsystemDescribe"
                  placeholder="请输入子系统描述"
                ></el-input>
              </template>
            </el-table-column>

            <el-table-column label="操作">
              <template #default="scope">
                <el-button
                  type="danger"
                  size="small"
                  @click="confirmRemoveSubsystem(scope.$index, scope.row.subsystemId)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 新增和保存按钮 -->
          <el-button type="primary" @click="addSubsystem">新增子系统</el-button>
          <el-button type="success" @click="submitSubsystems" :loading="isSubmitting">保存</el-button>
        </div>
      </div>
      <el-button type="primary" @click="formerStep" class="next-btn">返回</el-button>
       <el-button type="primary" @click="nextStep" class="next-btn">下一步</el-button>
    </el-scrollbar>
  </el-main>

  
</template>

<script lang="ts" setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter, useRoute } from "vue-router";
import VueOfficeDocx from "@vue-office/docx";
import "@vue-office/docx/lib/index.css";
import { ElMessage, ElMessageBox } from "element-plus";

// 获取路由信息
const router = useRouter();
const route = useRoute();
let loading = ref(false);

// 获取路径参数和查询参数
const projectId = Number(route.params.projectId);
const fileName = ref<string>(String(route.query.fileName));   // 获取查询参数

// 子系统数据类型
interface Subsystem {
  projectId?: number;
  subsystemId?: number;
  subsystemName: string;
  subsystemDescribe: string;
}

const docxSrc = ref<string>(""); // 动态绑定的文件地址
const subsystemsData = ref<Subsystem[]>([]); // 子系统数据
const isSubmitting = ref(false);  // 表示是否正在提交数据

// 获取文件内容
const fetchFileContent = async () => {
  // 使用路径参数和查询参数拼接文件路径
  const path = `\\${projectId}\\${fileName.value}`;
  docxSrc.value = String(path); // 设置文件地址
  console.log(docxSrc.value);   // 打印调试
  console.log("file::::",fileName.value);
};

// 获取子系统列表
const fetchSubsystems = async () => {
  if (!projectId) {
    console.error("项目ID不存在，无法获取子系统");
    return;
  }

  try {
    const response = await axios.get(`http://localhost:8080/${projectId}/subsystems/fetch`);
    if (response.status === 200) {
       
      // 确保返回的数据为数组且每项都有默认值
      subsystemsData.value = response.data.map((subsystem: any) => ({
        subsystemName: subsystem.subsystemName || '',
        subsystemDescribe: subsystem.subsystemDescribe || '',
        subsystemId: subsystem.subsystemId || '',
      }));

    } else {
      console.error("获取子系统数据失败");
    }
  } catch (error) {
    console.error("获取子系统数据时出错:", error);
  }
};

// 新增子系统
const addSubsystem = () => {
  const newSubsystem: Subsystem = { subsystemName: "", subsystemDescribe: "" };
  subsystemsData.value.push(newSubsystem);
};

// 删除子系统的确认框
const confirmRemoveSubsystem = (index: number, subsystemId: number) => {
  ElMessageBox.confirm("确定删除该子系统吗?", "删除确认", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning"
  })
  .then(() => {
    removeSubsystem(index, subsystemId);
  })
  .catch(() => {
    console.log("删除已取消");
  });
};

// 删除子系统
const removeSubsystem = (index: number, subsystemId: number) => {
  // 从数据中删除子系统
  subsystemsData.value.splice(index, 1);
  console.log("删除后的子系统数据:", subsystemsData.value);

  // 确保传递了项目ID
  if (projectId) {
    axios.delete(`http://localhost:8080/${projectId}/subsystems/${subsystemId}`)
      .then(response => {
        console.log("删除子系统成功:", response.data);
        ElMessage.success("删除子系统成功");
      })
      .catch(error => {
        console.error("删除子系统失败:", error);
      });
  } else {
    console.error("项目ID不存在，无法删除子系统");
  }
};

// 保存所有子系统数据
const submitSubsystems = async () => {

  // 校验每个子系统是否有 subsystemName 和 subsystemDescribe
  if (subsystemsData.value.some(subsystem => !subsystem.subsystemName || !subsystem.subsystemDescribe)) {
    alert("子系统名称和描述不能为空！");
    return;
  }

  // 提交前确保每个子系统都带上 projectId
  subsystemsData.value.forEach(subsystem => {
    if (!subsystem.projectId) {
      subsystem.projectId = projectId!; // 设置项目ID
    }
  });

  // 提交前打印数据，检查是否为空
  console.log("准备提交的子系统数据:", subsystemsData.value);

  isSubmitting.value = true;
  try {
    if (projectId) {
      const response = await axios.post(`http://localhost:8080/${projectId}/subsystems/save`, subsystemsData.value);
      if (response.status === 200) {
        alert("子系统数据保存成功");
        fetchSubsystems();
      } else {
        alert("保存失败，请稍后再试");
      }
    } else {
      console.error("项目ID不存在，无法保存子系统");
      alert("无法保存子系统，项目ID不存在");
    }
  } catch (error) {
    console.error("保存失败", error);
    alert("保存失败，请稍后再试");
  } finally {
    isSubmitting.value = false;
  }
};

// AI
const Ai = async () => {
  loading.value = true;
try {
  if (projectId) {
    await axios.post("http://localhost:8080/detail/ai", null, {
        params: { projectId,fileName: fileName.value }  // projectId 作为查询参数附加到 URL
      });
    fetchSubsystems();
    loading.value = false;
  } else {
    alert("AI分析失败");
  }
} catch (error) {
  console.error("AI失败", error);
  alert("AI分析失败，请稍后再试");
}
};

const formerStep = () => { 
  router.push({
    name: 'uploadMenu',  // 使用路由的 subsystemName
    params: { projectId: projectId },  // 传递 projectId
    query: { fileName: fileName.value },     // 如果需要传递 fileName，也可以继续使用 query
  });
};
const nextStep = () => { 
  router.push({
    name: 'functionmenu',  // 使用路由的 subsystemName
    params: { projectId: projectId },  // 传递 projectId
    query: { fileName: fileName.value },     // 如果需要传递 fileName，也可以继续使用 query
  });
};

onMounted(() => {
  fetchFileContent();
  fetchSubsystems();  // 获取当前项目的所有子系统
});
</script>



<style lang="scss" scoped>
/* 主容器样式 */
.container {
  display: flex;
  flex-direction: row;
  height: 100vh;
}

/* 左侧容器样式 */
.left-panel {
  width: 50%;
  padding: 20px;
  
}

.left-container {
  padding: 10px;
}

.file-display {
  width: 100%;
  height: 100%;
  border: none;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  background-color: #fff;
  color: #333;
  white-space: pre-wrap; /* 保持文本的换行 */
  overflow-y: scroll;
}

/* 右侧容器样式 */
.right-panel {
  width: 50%;
  padding: 20px;
  background-color: #fff;
  overflow-y: auto;
}

/* 表格区域样式 */
.table-container {
  margin-top: 20px;
  margin-bottom: 20px;
}

.el-table {
  width: 100%;
  border-collapse: collapse;
}

.el-table-column {
  text-align: center;
}

.el-input,
.el-select {
  width: 100%;
}

/* 操作按钮 */
.el-button.danger {
  margin: 5px;
  color: #fff;
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.el-button.danger:hover {
  background-color: #e64c4c;
  border-color: #e64c4c;
}

.el-button.primary {
  margin-left: 10px;
}
</style>
