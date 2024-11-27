<template>
  <div id="app" class="container">
    <h1 class="title">项目成本评估</h1>

    <!-- 子系统选择 -->
    <div v-if="selectedProjectId && subsystems.length > 0">
      <el-form>
        <el-form-item label="选择子系统" :label-width="'120px'">
          <el-select v-model="selectedSubsystemId" @change="loadSubsystemData" placeholder="请选择子系统" class="select-box">
            <el-option
              v-for="subsystem in subsystems"
              :key="subsystem.projectId"
              :label="`${subsystem.projectName} - ${subsystem.adjustedFP}`"
              :value="subsystem.projectId"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 状态选择 -->
    <div v-if="states.length > 0">
      <el-form>
        <el-form-item label="选择评估标准" :label-width="'120px'">
          <el-select v-model="selectedStateId" @change="loadStateData" placeholder="请选择评估标准" class="select-box">
            <el-option
              v-for="state in states"
              :key="state.stateId"
              :label="state.stateName"
              :value="state.stateId"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 错误消息 -->
    <div v-if="error" class="error">
      <el-alert title="{{ error }}" type="error" />
    </div>

    <!-- 子系统数据表格 -->
    <div v-if="subsystems.length > 0">
      <h2>子系统列表</h2>
      <el-table :data="subsystems" style="width: 100%">
        <el-table-column label="子系统 ID" prop="projectId" />
        <el-table-column label="子系统名称" prop="projectName" />
        <el-table-column label="调整后功能点数" prop="adjustedFP" />
      </el-table>
    </div>

    <!-- 下一步按钮 -->
    <el-button type="primary" @click="nextStep" class="next-btn">下一步</el-button>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

// 定义 State 类型
interface State {
  stateId: number;
  stateName: string;
}

// 定义 Subsystem 类型
interface Subsystem {
  projectId: number;  // 使用 projectId 作为子系统的唯一标识
  projectName: string;  // 使用 projectName 作为子系统名称
  adjustedFP: number | null;  // 调整后的功能点数
}

const router = useRouter();
const route = useRoute();

// 从路由中获取项目ID和文件名（如果需要）
const projectId = route.params.projectId;
const fileName = ref(String(route.query.fileName));
const projectIid = route.params.projectId;
// 使用 ref 创建响应式数据
const selectedProjectId = ref<number | null>(projectId ? Number(projectId) : null); // 当前选择的项目ID
const selectedStateId = ref<number | null>(null);    // 当前选择的状态ID
const selectedSubsystemId = ref<number | null>(null);  // 当前选择的子系统ID

// 将 subsystems 和 states 类型声明为数组
const subsystems = ref<Subsystem[]>([]);  // 当前项目的子系统列表
const states = ref<State[]>([]);    // 所有状态列表
const error = ref<string | null>(null);  // 错误信息

// 加载子系统列表
const loadSubsystems = async () => {
  if (!selectedProjectId.value) return;

  try {
    console.log(`加载子系统列表：请求的项目ID = ${selectedProjectId.value}`);  // 调试打印请求的ID
    const response = await fetch(`http://localhost:8080/cost-evaluation/sub/${selectedProjectId.value}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      mode: 'cors',
    });

    if (!response.ok) {
      throw new Error("请求失败，状态码：" + response.status);
    }

    const data = await response.json();
    console.log("返回的子系统数据：", data);  // 调试返回的数据

    // 检查返回数据是否是数组或对象
    if (Array.isArray(data) && data.length > 0) {
      subsystems.value = data;  // 如果是数组且不为空，更新子系统列表
    } else if (data && data.projectId) {
      // 如果返回的是一个对象，并且包含 projectId，直接将该对象处理为子系统数据
      subsystems.value = [{
        projectId: data.projectId,  // 将 projectId 作为唯一的 ID
        projectName: data.projectName,  // 将 projectName 作为子系统名称
        adjustedFP: data.adjustedFP || 0,  // 如果 adjustedFP 为 null，设置默认值
      }];
    } else {
      error.value = "没有找到对应的子系统数据。";
    }
  } catch (err) {
    console.error("Error loading subsystems:", err);
    error.value = "加载子系统列表失败，请稍后重试。";
  }
};

// 选择子系统后加载子系统数据
const loadSubsystemData = async () => {
  if (!selectedSubsystemId.value) {
    console.error('子系统ID未选择，请选择有效的子系统');
    error.value = "请选择有效的子系统。";
    return;  // 如果 subsystemId 为空或无效，直接返回
  }

  console.log('加载子系统数据，子系统ID:', selectedSubsystemId.value);  // 调试传递的 subsystemId

  try {
    const response = await fetch(`http://localhost:8080/cost-evaluation/${selectedSubsystemId.value}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      mode: 'cors',
    });
    const data = await response.json();
    console.log("选中的子系统数据：", data);
  } catch (err) {
    console.error("Error loading subsystem data:", err);
    error.value = "加载子系统数据失败，请稍后重试。";
  }
};

// 加载所有状态列表
const loadStates = async () => {
  try {
    const response = await fetch("http://localhost:8080/cost-evaluation/getAllStates", {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      mode: 'cors',
    });
    const data = await response.json();
    console.log("返回的状态数据：", data);  // 调试返回的状态数据
    states.value = data;
  } catch (err) {
    console.error("Error loading states:", err);
    error.value = "加载状态列表失败，请稍后重试。";
  }
};

// 选择状态后加载状态数据
const loadStateData = async () => {
  console.log('Loading state data for State ID:', selectedStateId.value);  // 调试传递的 stateId
  if (!selectedStateId.value) return;  // 如果 stateId 为空，直接返回

  try {
    const response = await fetch(`http://localhost:8080/cost-evaluation/getStateById/${selectedStateId.value}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      mode: 'cors',  // 配置跨域请求
    });
    const data = await response.json();
    console.log("选中的状态数据：", data);
  } catch (err) {
    console.error("Error loading state data:", err);
    error.value = "加载状态数据失败，请稍后重试。";
  }
};

// 下一步 - 跳转到下一个页面
const nextStep = () => {
  if (!selectedStateId.value || !selectedSubsystemId.value) {
    alert("请选择子系统和标准！");
    return;
  }

  router.push({
    name: 'workloadchoose',
    params: {
      projectId: selectedSubsystemId.value,
      stateId: selectedStateId.value,
      projectIid: projectIid,
    },
    query: { fileName: fileName.value }
  });
};

// 在组件挂载时加载项目数据和状态列表
onMounted(() => {
  if (selectedProjectId.value) {
    loadSubsystems(); // 加载当前项目的子系统列表
    loadStates();     // 加载状态列表
  }
});
</script>

<style scoped>
.container {
  padding: 20px;
  font-family: Arial, sans-serif;
  align-items: center;
}

.title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.error {
  margin-top: 20px;
  color: #f56c6c;
}

.el-table {
  margin-top: 20px;
}


.select-box {
  width: 50%;  /* 将选择框宽度设置为 100% */
}
/* 缩小按钮宽度并居中 */
.el-button {
  width: 200px; /* 将按钮宽度设置为 200px */
  margin: 20px auto; /* 上下间距，并居中按钮 */
  display: block; /* 设置为块级元素，方便居中 */
}
</style>
