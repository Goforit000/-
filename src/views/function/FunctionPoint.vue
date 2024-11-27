
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
        <!-- 功能点设置 -->
        <div class="subsystem-selector">
          <label for="subsystem" class="adjustment-label">子系统：</label>
          <el-select
            v-model="selectedSubsystem"

            placeholder="选择子系统"
            class="subsystem-select"
            id="subsystem"
            @change="onSubsystemChange"
          >
            <el-option
              v-for="subsystem in subsystems"
              :key="subsystem.subsystemId"
              :label="subsystem.subsystemName"
              :value="subsystem.subsystemId"
            ></el-option>
          </el-select>

          <el-button type="primary" @click="addFunction">新增功能点</el-button>
        </div>

        <!-- 表格区域 -->
        <el-scrollbar class="table-container">
          <el-table :data="functionPoints" style="width: 100%">
            <el-table-column label="功能点名称">
              <template #default="scope">
                <el-input v-model="scope.row.featureName" placeholder="功能点名称"></el-input>
              </template>
            </el-table-column>

            <el-table-column label="重用程度">
              <template #default="scope">
                <el-select v-model="scope.row.complexity" placeholder="选择重用程度">
                  <el-option label="低" value="低"></el-option>
                  <el-option label="中" value="中"></el-option>
                  <el-option label="高" value="高"></el-option>
                </el-select>
              </template>
            </el-table-column>

            <el-table-column label="修改类型">
              <template #default="scope">
                <el-select v-model="scope.row.modifyType" placeholder="选择修改类型">
                  <el-option label="新增" value="新增"></el-option>
                  <el-option label="修改" value="修改"></el-option>
                  <el-option label="删除" value="删除"></el-option>
                </el-select>
              </template>
            </el-table-column>

            <el-table-column label="功能类型">
              <template #default="scope">
                <el-select v-model="scope.row.featureType" placeholder="选择功能类型">
                  <el-option label="EI" value="EI"></el-option>
                  <el-option label="EO" value="EO"></el-option>
                  <el-option label="EQ" value="EQ"></el-option>
                  <el-option label="ILF" value="ILF"></el-option>
                  <el-option label="EIF" value="EIF"></el-option>
                </el-select>
              </template>
            </el-table-column>

            <el-table-column label="备注">
              <template #default="scope">
                <el-input v-model="scope.row.note" placeholder="备注"></el-input>
              </template>
            </el-table-column>

            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="danger" @click="confirmRemoveFunctionPoint(scope.$index, selectedSubsystem,scope.row.featureId)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-scrollbar>

        <el-button type="success" @click="submitData(selectedSubsystem)">保存</el-button>
      </div>

      <!-- 统计信息显示 -->
      <div class="statistics-container">
        <el-card class="stat-card">
          <div class="stat-title">调整前规模</div>
          <div class="stat-value">{{ unadjustedFp }} 功能点</div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-title">调整后规模</div>
          <div class="stat-value">{{ adjustedFp }} 功能点</div>
        </el-card>
      </div>

      <!-- 下拉框选择方法 -->
      <div class="adjustment-container">
        <label for="adjustment-method" class="adjustment-label">调整方法：</label>
        <el-select
          v-model="adjustedMethod"
          placeholder="选择调整方法"
          class="method-select"
          id="adjustment-method"
        >
          <el-option label="GSC方法" value="GSC"></el-option>
          <el-option label="规模调整因子" value="规模调整因子"></el-option>
        </el-select>
        <el-button type="primary" @click="submitAdjustmentMethod(selectedSubsystem)">提交调整方法</el-button>
      </div>
      <el-button type="primary" @click="formerStep" class="next-btn">返回</el-button>
      <el-button type="primary" @click="nextStep" class="next-btn">下一步</el-button>
    </el-scrollbar>
  </el-main>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from "vue";
import axios from "axios";
import { useRouter, useRoute } from "vue-router";
import VueOfficeDocx from "@vue-office/docx";
import "@vue-office/docx/lib/index.css";
import { ElMessage, ElMessageBox } from "element-plus";

interface FunctionPoint {
  featureId: number;      // 功能点ID
  featureName: string;    // 功能点名称
  complexity: string;     // 重用程度
  modifyType: string;     // 修改类型
  featureType: string;    // 功能类型
  note: string;           // 备注
}

const router = useRouter();
const route = useRoute();
const projectId = route.params.projectId;
const fileName = ref<string>(String(route.query.fileName));

const docxSrc = ref<string>(""); // 动态绑定的文件地址
const functionPoints = ref<FunctionPoint[]>([]); // 功能点数据
const unadjustedFp = ref<number>(0); // 调整前规模点数
const adjustedFp = ref<number>(0); // 调整后规模点数

const adjustedMethod = ref<string>("GSC"); // 调整方法（0 为 GSC 方法，1 为 规模调整因子）
const subsystems = ref<Subsystem[]>([]); // 子系统列表
const selectedSubsystem = ref<number>(0); // 当前选中的子系统 ID，默认为 null

// 监听 subsystems 的变化，设置 selectedSubsystem 为第一个元素的 ID
watch(subsystems, (newValue) => {
  selectedSubsystem.value = newValue.length > 0 ? newValue[0].subsystemId : 0;
  console.log("最初最初",selectedSubsystem.value);
});

interface Subsystem {
  projectId: number;
  subsystemId: number;
  subsystemName: string;
  
}

// 获取文件内容
const fetchFileContent = async () => {
  // 使用路径参数和查询参数拼接文件路径
  const path = `\\${projectId}\\${fileName.value}`;
  docxSrc.value = String(path); // 设置文件地址
  console.log(docxSrc.value);   // 打印调试
};

// 获取子系统列表
const fetchSubsystems = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/${projectId}/subsystems/fetch`);
    const data = response.data;
    // console.log(data);
    if (Array.isArray(data) && data.length > 0) {
      subsystems.value = data;
      selectedSubsystem.value = data[0]?.subsystemId;
      console.log("data[0]",data[0].subsystemId);
      await fetchFunctionPointsBySubsystemId(selectedSubsystem.value);
    } else {
      subsystems.value = [];
      console.warn("子系统列表为空，无法加载子系统数据");
    }
  } catch (error) {
    console.error("获取子系统列表失败：", error);
    subsystems.value = [];
  }
};


// 根据子系统ID获取功能点
const fetchFunctionPointsBySubsystemId = async (subsystemId: number) => {
  try {
    const response = await axios.get(`http://localhost:8080/${projectId}/subsystems/${subsystemId}/functions`);
    
    const data = response.data;  // 获取后端返回的数据
    console.log("data:", data);  // 打印返回的数据

    // 如果数据是一个数组且有数据
    if (Array.isArray(data) && data.length > 0) {
      // 只保留需要的字段
      functionPoints.value = data.map(item => ({
        complexity: item.complexity,
        featureId: item.featureId,
        featureName: item.featureName,
        featureType: item.featureType,
        modifyType: item.modifyType,
        note: item.note,
        adjustedFp:item.adjustedFp,
        unadjustedFp:item.unadjustedFp,
        adjustedMethod:item.adjustedMethod,
      }));
    } else {
      functionPoints.value = [];
      console.warn(`子系统 ${subsystemId} 没有功能点数据`);
    }
  } catch (error) {
    console.error(`获取子系统 ${subsystemId} 的功能点失败：`, error);
    functionPoints.value = [];
  }
};


// 监听子系统选择变化
const onSubsystemChange = async () => {
  if (selectedSubsystem.value) {
    console.log("选择的子系统："+selectedSubsystem.value);
    // 重新加载功能点数据
    await fetchFunctionPointsBySubsystemId(selectedSubsystem.value);
  }else{
    alert("未选择子系统");
  }
};

// 添加功能点
const addFunction = () => {
  functionPoints.value.push({
    featureId: 0,
    featureName: "",
    complexity: "",
    modifyType: "新增",
    featureType: "",
    note: "",
  });
};
// 删除子系统的确认框
const confirmRemoveFunctionPoint = (index: number, subsystemId: number,featureId:number) => {
  ElMessageBox.confirm("确定删除该功能点吗?", "删除确认", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning"
  })
  .then(() => {
    removeFunctionPoint(index, subsystemId,featureId);
  })
  .catch(() => {
    console.log("删除已取消");
  });
};

// 删除功能点
const removeFunctionPoint = (index: number, subsystemId: number,featureId:number) => {
  // 从数据中删除功能点
  functionPoints.value.splice(index, 1);
  console.log("删除后的功能点数据:", functionPoints.value);
  console.log("删除后的功能点所在子系统:", subsystemId);
  // 确保传递了项目ID
  if (projectId) {
    axios.delete(`http://localhost:8080/${projectId}/subsystems/${subsystemId}/${featureId}`)
      .then(response => {
        console.log("删除功能点成功:", response.data);
        ElMessage.success("删除功能点成功");
        fetchFunctionPointsBySubsystemId(subsystemId);
      })
      .catch(error => {
        console.error("删除功能点失败:", error);
      });
  } else {
    console.error("项目ID不存在,无法删除功能点");
  }
};

// 提交数据
const submitData = async (subsystemId: number) => {
  try {
    console.log(functionPoints.value);
    await axios.post(`http://localhost:8080/${projectId}/subsystems/${subsystemId}/save`, functionPoints.value);
    alert("所有数据已成功提交");
    fetchFunctionPointsBySubsystemId(subsystemId);
  } catch (error) {
    console.error("数据提交失败：", error);
  }
};
// 提交调整方法
const submitAdjustmentMethod = async (subsystemId: number) => {
  try {
    const response = await axios.post(`http://localhost:8080/${projectId}/subsystems/${subsystemId}/method`, {
      method: adjustedMethod.value,
    });
    if (response.data.success) {

      await fetchPostAdjustmentPoints(subsystemId);
    }
  } catch (error) {
    console.error("提交调整方法失败：", error);
  }
};

// 获取调整后规模点数
const fetchPostAdjustmentPoints = async (subsystemId: number) => {
  try {
    const response = await axios.get(`http://localhost:8080/${projectId}/subsystems/${subsystemId}/adjustedMethod`);
   console.log(response.data);
    adjustedFp.value = response.data.adjustedFp;
    unadjustedFp.value = response.data.unadjustedFp;
  } catch (error) {
    console.error("获取调整后规模数据失败：", error);
  }
};
const formerStep = () => { 
  router.push({
    name: 'systempoint',  // 使用路由的 subsystemName
    params: { projectId: projectId },  // 传递 projectId
    query: { fileName: fileName.value },     // 如果需要传递 fileName，也可以继续使用 query
  });
};
// 跳转到下一个步骤
const nextStep = () => {
  router.push({
    name: 'detailmenu',  // 使用路由的 name
    params: { projectId: projectId },  // 通过 params 传递 projectId
    query: { fileName: fileName.value,
    },  // 如果需要传递 fileName，可以继续使用 query
  });
};

// 初始化时获取文件内容和子系统列表
onMounted(() => {
  fetchFileContent();
  fetchSubsystems();

});
</script>




<style scoped>
.container {
  display: flex;
}

.left-panel,
.right-panel {
  height: 600px;
}

.left-container,
.right-container {
  padding: 10px;
}

.left-panel {
  width: 50%;
  margin-right: 20px;
}

.right-panel {
  width: 50%;
}

.file-display {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 10px;
  margin-top: 20px;
  margin-bottom: auto;
}

.table-container {
  max-height: 400px;
  overflow-y: auto;
}

.el-button {
  margin: 10px 0;
  margin-left: 10px;
}

.statistics-container {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.stat-card {
  width: 200px;
  height: 100px;
  text-align: left;
  padding: 20px;
}

.stat-title {
  font-size: 14px;
  color: #888;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
}

.method-selection {
  margin-top: 20px;
}
.adjustment-container {
  display: flex;
  align-items: center; /* 保证 label 和 select 垂直居中对齐 */
  justify-content: flex-start; /* 左对齐 */
}

.adjustment-label {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-right: 10px; /* 设置 label 与 select 之间的间距 */
}

.method-select {
  width: 200px; /* 设置选择框的宽度 */
}
.subsystem-selector {
  margin-bottom: 20px;
}

.subsystem-select {
  width: 200px;
}
</style>
