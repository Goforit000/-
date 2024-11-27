<template>
    <div>
      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索标准名称"
          clearable
          @clear="filterStates"
          @input="filterStates"
        >
          <template #append>
            <el-button icon="el-icon-search" @click="filterStates">搜索</el-button>
          </template>
        </el-input>
        <el-button @click="resetFilters" icon="el-icon-refresh-right" type="primary">重置</el-button>
        <!-- 跳转按钮 -->
      <el-button type="primary" @click="viewPDF">查看 PDF</el-button>
      </div>
  
      <!-- 状态列表 -->
      <el-row :gutter="20" class="state-list">
        <!-- 新增标准卡片 -->
        <el-col :span="8">
          <el-card class="state-card add-state-card" @click="openAddDialog">
            <div class="add-state-content">
              <el-icon><i class="el-icon-plus"></i></el-icon>
              <span>+ 增加新标准</span>
            </div>
          </el-card>
        </el-col>
  
        <!-- 状态卡片 -->
        <el-col :span="8" v-for="state in paginatedStates" :key="state.state_id">
  <el-card class="state-card">
    <!-- 左上角官方/非官方 -->
    <div class="state-header">
      <div class="official-badge">
        {{ state.isOfficial === 1 ? "官方" : "非官方" }}
      </div>
      <!-- 右上角删除按钮 -->
      <div class="delete-icon" v-if="state.isOfficial === 0" @click="deleteState(state)">
        删除
      </div>
      <div class="state-actions">
        <el-switch
          class="state-switch"
          v-model="state.isAvailable"
          :active-value="1"
          :inactive-value="0"
          @change="toggleState(state)"
        ></el-switch>
      </div>
    </div>

    <!-- 状态详情 -->
    <div class="state-content">
      <h3>名称：{{ state.stateName }}</h3>
      <p>ID: {{ state.stateId }}</p>
      <p>描述：{{ state.stateDetail }}</p>
    </div>

    <!-- 按钮组 -->
    <div class="action-buttons">
      <!-- 当没有修改按钮时，查看按钮占满宽度 -->
      <el-button
        v-if="state.isOfficial === 0"
        size="mini"
        type="primary"
        class="action-btn edit-btn"
        @click="editState(state)"
        plain
      >
        修改
      </el-button>
      <el-button
        size="mini"
        type="primary"
        :class="{'view-btn-full': state.isOfficial === 1}"
        @click="viewDetails(state.stateId)"
        plain
      >
        查看具体参数
      </el-button>
    </div>
  </el-card>
</el-col>


      </el-row>
 
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
            layout="sizes, prev, pager, next, jumper"
            :total="filteredStates.length"
            :page-size.sync="pageSize"
           :page-sizes="[5, 8, 11, 14]" 
           @size-change="handleSizeChange"
              @current-change="handlePageChange"
             :current-page.sync="currentPage"
          ></el-pagination>
      </div>
    </div>
  

    <el-dialog v-model="showDetails" width="90%" top="5%">
  <div class="details-header">
    <h2>参数设置</h2>
    <el-button icon="el-icon-close" @click="showDetails = false" class="close-btn"></el-button>
  </div>

  <div class="details-container">
    <!-- 左侧部分 -->
    <div class="details-left">
  <el-table :data="parameterList" class="custom-table" border>
    <el-table-column label="参数">
      <template #default="{ row }">
        <span>{{ row.name }}</span>
      </template>
    </el-table-column>
    <el-table-column label="状态">
      <template #default="{ row }">
        <div class="parameter-status">
          <el-switch 
            v-model="row.enabled" 
            :disabled="true" 
            :active-value="true" 
            :inactive-value="false" 
            class="switch"/>
          <span class="arrow" @click="showParameterDetails(row)">></span>
        </div>
      </template>
    </el-table-column>
  </el-table>
</div>


    <!-- 右侧部分 -->
    <div class="details-right">
      <div class="tab-header">
        <el-button
          :type="activeTab === 'info' ? 'primary' : 'default'"
          @click="activeTab = 'info'"
        >
          基本信息
        </el-button>
        <el-button
          :type="activeTab === 'settings' ? 'primary' : 'default'"
          @click="activeTab = 'settings'"
        >
          参数设置
        </el-button>
      </div>

      <div v-if="activeTab === 'info'" class="info-section">
    <p><strong>名称：</strong>{{ selectedParameter.name }}</p>
    <p><strong>代号：</strong>{{ selectedParameter.code }}</p>
    <p><strong>状态：</strong>
      <el-switch v-model="selectedParameter.enabled" :disabled="true" :active-value="true" :inactive-value="false" class="switch" />
    </p>
  </div>

  <div v-if="activeTab === 'settings'" class="settings-section">
  <el-table
    :data="parameterTableData[selectedParameter.code]?.data" border >
    <!-- 动态渲染表头 -->
    <el-table-column
      v-for="header in parameterTableData[selectedParameter.code]?.headers"
      :key="header.prop"
      :prop="header.prop"
      :label="header.label"
    ></el-table-column>
  </el-table>
</div>


    </div>
  </div>
</el-dialog>


    <el-dialog v-model="showEditDialog" width="400px" title="新增/修改标准" >
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="标准名称">
          <el-input v-model="editForm.stateName" />
        </el-form-item>
        <el-form-item label="标准描述">
          <el-input v-model="editForm.stateDetail" type="textarea" />
        </el-form-item>
        <el-form-item v-for="(label, key) in factorLabels" :key="key" :label="label">
          <el-switch v-model="editForm[key]" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveState">保存</el-button>
      </div>
    </el-dialog>
  
    
  </template>
  
  <script>
  import axios from "axios";
  import { ref } from 'vue';
  import { ElDialog, ElForm, ElInput, ElSwitch, ElButton } from "element-plus";

  export default {
    data() {
      return {
        searchQuery: "",
      states: [],
      filteredStates: [],
      showEditDialog: false,
      showDetails: false,
      editForm: {
        stateId: null,
        stateName: "",
        stateDetail: "",
        isOfficial: 0,
        isAvailable: 1,
        applicationTypeState: 0,
        qualityCharacteristicState: 0,
        developLanguageState: 0,
        teamBackground: 0,
        riskState: 0,
        directNonhumanCostState: 0,
        straightQualityGradeState: 0,
      },
      factorLabels: {
        applicationTypeState: "应用类型调整因子状态",
        qualityCharacteristicState: "质量特性调整因子状态",
        developLanguageState: "开发语言调整因子状态",
        teamBackground: "开发团队背景因子状态",
        riskState: "风险调整因子状态",
        directNonhumanCostState: "直接非人力成本状态",
        straightQualityGradeState: "直质量等级因子状态",
      },
      currentPage: 1,
      pageSize: 5,
      activeTab: "", // 初始化为默认值，例如空字符串或特定值
      selectedParameter: null, // 初始化为 null 或空对象
      parameterTableData: {
      ATS: {
      headers: [
          { prop: "type", label: "类型" },
          { prop: "factor", label: "因子" },
          { prop: "description", label: "描述" },
        ],
        data: [
          { type: "业务处理", factor: 1.0, description: "办公自动化系统；人事、会计、工资、销售等经营管理及业务处理用软件等" },
          { type: "软件集成", factor: 1.2, description: "软件集成等" },
          { type: "科技", factor: 1.2, description: "科学计算、模拟、统计等" },
          { type: "多媒体", factor: 1.3, description: "图形、影像、声音等多媒体应用领域；地理信息系统；教育和娱乐应用等" },
          { type: "智能信息", factor: 1.5, description: "自然语言处理、人工智能、专家系统等" },
          { type: "基础软件/支撑软件", factor: 1.7, description: "操作系统、数据库系统、集成开发环境、自动化开发/设计工具等" },
          { type: "通信控制", factor: 1.9, description: "通信协议、仿真、交换机软件、全球定位系统等" },
          { type: "流程控制", factor: 2.0, description: "实时控制、机器人控制、嵌入式软件等" },
        ],
      },
      QCS: {
        headers: [
          { prop: "type", label: "类型" },
          { prop: "factor", label: "因子" },
        ],
        data: [
          { type: "应用云化", factor: 1.2 },
          { type: "跨平台/设备", factor: 1.3 },
          { type: "分布式处理", factor: 1.5},
        ],
      },
      DLS: {
        headers: [
          { prop: "type", label: "类型" },
          { prop: "factor", label: "因子" },
        ],
        data: [
          { type: "C及其他同级别语言或平台", factor: 1.5 },
          { type: "Java、C++、C#及其它同级别语言或平台", factor: 1.0 },
          { type: "PowerBuilder，ASP及其他同级别语言/平台", factor: 1.6},
        ],
      },
      TBS: {
        headers: [
          { prop: "type", label: "类型" },
          { prop: "factor", label: "因子" },
        ],
        data: [
          { type: "为本行业开发过类似的项目", factor: 0.8 },
          { type: "为其他行业开发过类似的项目，或为本行业开发过不同但相关的项目", factor: 1.0 },
          { type: "没有同类项目的背景", factor: 1.2},
        ],
      },
      RFS: {
        headers: [
          { prop: "type", label: "类型" },
          { prop: "factor", label: "因子" },
        ],
        data: [
          { type: "低风险", factor: 0.8 },
          { type: "中风险", factor: 1.0 },
          { type: "高风险", factor: 1.2},
        ],
      },
      DCH: {
        headers: [
          { prop: "type", label: "类型" },
          { prop: "factor", label: "因子" },
        ],
        data: [
          { type: "低", factor: 1 },
          { type: "中", factor: 2 },
          { type: "高", factor: 3},
        ],
      },
      QGS: {
        headers: [
          { prop: "type", label: "类型" },
          { prop: "factor", label: "因子" },
        ],
        data: [
          { type: "优", factor: 1.0 },
          { type: "良", factor: 0.9},
          { type: "合格", factor: 0.8},
          { type: "次", factor: 0.6},
          { type: "不合格", factor: 0.0},
        ],
      },
      // 其他因子类似添加...
    },
      };
    },
    computed: {
      paginatedStates() {
        const start = (this.currentPage - 1) * this.pageSize;
        const end = start + this.pageSize;
        return this.filteredStates.slice(start, end);
      },
    },
    mounted() {
      this.fetchStates();
    },
    methods: {
      async fetchStates() {
        try {
          const response = await axios.get("http://localhost:8080/cost-evaluation/getAllStates");
          this.states = response.data.map((state) => ({
            ...state,
            enabled: true,
          }));
          this.filteredStates = [...this.states];
        } catch (error) {
          console.error("获取状态失败:", error);
        }
      },
      filterStates() {
        if (this.searchQuery.trim() === "") {
          this.filteredStates = [...this.states];
        } else {
          const query = this.searchQuery.trim().toLowerCase();
          this.filteredStates = this.states.filter((state) =>
            state.stateName.toLowerCase().includes(query)
          );
        }
        this.currentPage = 1;
      },
      resetFilters() {
        this.searchQuery = "";
        this.filteredStates = [...this.states];
        this.currentPage = 1;
      },
      handlePageChange(page) {
        this.currentPage = page;
      },
      handleSizeChange(size) {
        this.pageSize = size;
      },
      
      async saveState() {
        console.log("开始了");
      try {
        if (this.editForm.stateId) {
          // Update existing state
          console.log("开始了111");
          await axios.post("http://localhost:8080/cost-evaluation/updateState", this.editForm);
        } else {
          // Add new state
          await axios.post("http://localhost:8080/cost-evaluation/addState", this.editForm);
        }
        this.$message.success("保存成功！");
        this.fetchStates();
        this.showEditDialog = false;
      } catch (error) {
        console.error("保存标准失败:", error);
      }
    },
      addState() {},
      openAddDialog() {
        console.log("打开新增标准弹窗");
        const nextStateId = this.states.length > 0 ? Math.max(...this.states.map(state => state.stateId)) + 1 : 1;
      this.editForm = {
        stateId: null,
        stateName: "",
        stateDetail: "",
        isOfficial: 0,
        isAvailable: 1,
        applicationTypeState: 0,
        qualityCharacteristicState: 0,
        developLanguageState: 0,
        teamBackground: 0,
        riskState: 0,
        directNonhumanCostState: 0,
        straightQualityGradeState: 0,
      };

      console.log("showEditDialog before:", this.showEditDialog); // 调试输出
     this.showEditDialog = true;
      console.log("showEditDialog after:", this.showEditDialog);
    },
    openEditDialog(state) {
      this.editForm = { ...state };
      this.showEditDialog = true;
    },
  
  deleteState(state) {
  this.$confirm(`确定要删除状态 "${state.stateName}" 吗？`, "提示", {
    confirmButtonText: "删除",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        // 调用后端删除 API
        const response = await axios.delete(`http://localhost:8080/cost-evaluation/deleteState/${state.stateId}`);
        this.$message.success(response.data || "删除成功！");
        // 重新获取状态列表
        this.fetchStates();
      } catch (error) {
        console.error("删除状态失败:", error);
        this.$message.error("删除状态失败，请稍后重试！");
      }
    })
    .catch(() => {
      console.log("取消删除");
    });
},


  // 编辑状态
  editState(state) {
    console.log("编辑状态:", state.state_id);
    // 打开编辑对话框或跳转到编辑页面
    this.openEditDialog(state);
  },

  // 查看参数
  viewStateDetails(state) {
    console.log("查看参数:", state.state_id);
    this.viewDetails(state.state_id);
  },

      async toggleState(state) {
  try {
    // 1. 从后端获取完整数据
    const response = await axios.get(`http://localhost:8080/cost-evaluation/getStateById/${state.stateId}`);
    const fullStateData = response.data;

    // 2. 合并用户修改的数据
    const updatedState = {
      ...fullStateData, // 后端返回的完整数据
      isAvailable: state.isAvailable, // 用户修改的可用性
    };

    // 3. 发送更新请求
    await axios.post("http://localhost:8080/cost-evaluation/updateState", updatedState);
  } catch (error) {
    console.error("更新状态失败:", error);
    // 还原前端状态
    state.isAvailable = !state.isAvailable;
  }
},
async viewDetails(stateId) {
  try {
    console.log("OK打开了:", stateId); // 调试输出
    const response = await axios.get(
      `http://localhost:8080/cost-evaluation/getStateById/${stateId}`
    );

    const stateData = response.data;
    console.log("Fetched stateData:", stateData);

    console.log(stateData);
    // 格式化参数列表
    this.parameterList = [
          {
            name: "应用类型调整因子状态",
            code: "ATS",
            enabled: Boolean(stateData.applicationTypeState),
            settings: [],
          },
          {
            name: "质量特性调整因子状态",
            code: "QCS",
            enabled: Boolean(stateData.qualityCharacteristicState),
            settings: [],
          },
          {
            name: "开发语言调整因子状态",
            code: "DLS",
            enabled: Boolean(stateData.developLanguageState),
            settings: [],

          },
          {
            name: "开发团队背景因子状态",
            code: "TBS",
            enabled: Boolean(stateData.teamBackground),
            settings: [],
          },
          {
            name: "风险调整因子状态",
            code: "RFS",
            enabled: Boolean(stateData.riskState),
            settings: [],
          },
          {
            name: "直接非人力成本状态",
            code: "DCH",
            enabled: Boolean(stateData.directNonhumanCostState),
            settings: [],
          },
          {
            name: "直质量等级因子状态",
            code: "QGS",
            enabled: Boolean(stateData.straightQualityGradeState),
            settings: [],
          },
        ];

        console.log("Fetched parameterList:", this.parameterList); // 调试输出
        this.showDetails = true; // 显示弹窗
      } catch (error) {
        console.error("获取参数详情失败:", error);
      }
    },

    showParameterDetails(parameter) {
      this.selectedParameter = { ...parameter };
      this.activeTab = "settings";
      this.$nextTick(() => {
    console.log("Parameter details updated:", this.selectedParameter);
  });
    },
  
    },
  };
  </script>
  
  <style scoped>
  .top-actions {
    margin: 20px 0;
    text-align: center;
  }
  

.state-card {
  position: relative; /* 确保子元素可以使用绝对定位 */
  padding-top: 30px; /* 为顶部内容留出空间 */
}

.state-header {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  box-sizing: border-box;
}

/* 按钮组样式 */
.action-buttons {
  display: flex;
  justify-content: space-between; /* 两个按钮左右对齐 */
  gap: 10px;
  margin-top: auto;
}

/* 按钮通用样式 */
.action-btn {
  text-align: center;
  background-color: #409eff;
  color: white;
  border-radius: 4px;
  flex: 1; /* 默认占据可用空间 */
  transition: background-color 0.3s ease;
}

.action-btn:hover {
  background-color: #66b1ff; /* 鼠标悬浮颜色变浅 */
}

/* 查看按钮全宽 */
.view-btn-full {
  flex: none;
  width: 100%;
}

/* 修改按钮 */
.edit-btn {
  flex: 1; /* 两个按钮各占一半空间 */
}

/* 删除按钮样式 */
.delete-icon {
  position: absolute;
  top: 20px;
  right: 100px;
  cursor: pointer;
  color: #f56c6c; /* 删除按钮红色 */
  font-size: 12px;
  font-weight: bold;
}

.delete-icon:hover {
  color: #ff8787; /* 删除按钮悬浮颜色变浅 */
}

/* 开关样式 */
.state-switch {
  margin-right: 10px; /* 右边留点间距 */
}


.official-badge {
  background-color: #ff9800;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

  .state-switch {
  margin-right: 10px; /* 右边留点间距 */
}

  .search-box {
    margin: 20px 0;
    display: flex;
    justify-content: center;
    gap: 10px;
    width: 500px; /* 搜索框缩小 */
    margin-left: auto;
    margin-right: auto;
  }
  
  .state-list {
    margin-bottom: 40px; /* 与分页之间增加间距 */
    row-gap: 30px; /* 增加卡片间垂直距离 */
  }
  
  .state-card,
  .add-state-card {
    height: 240px; /* 卡片统一高度 */
  display: flex;
  flex-direction: column; /* 子元素按列排列 */
  justify-content: space-between; /* 保证 .view-details 位于底部 */
  border-radius: 10px; /* 增加圆角 */
  overflow: hidden; /* 子元素超出隐藏，保持圆角一致 */
  box-sizing: border-box; /* 确保尺寸计算准确 */
  padding: 0; /* 去除内边距以避免多余空间 */
  }
  
  .add-state-card {
  background-color: #f2f2f2; /* 灰色背景 */
  cursor: pointer;
  font-size: 30px; /* 字体更大 */
  color: #606266;
  display: flex; /* 使用 Flexbox */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  text-align: center; /* 文本居中对齐（针对多行文本） */

}
  
  .view-details {
    height: 50px; /* 调整高度 */
  background-color: #409eff; /* 蓝色背景 */
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
  /* 让背景与卡片宽度和圆角一致 */
  border-top-right-radius: 10px;
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px; /* 左下圆角 */
  border-bottom-right-radius: 10px; /* 右下圆角 */
  width: 100%; /* 填充整个卡片宽度 */
  margin-top: auto; /* 将背景推至卡片底部 */
  box-sizing: border-box; /* 确保尺寸准确 */
  }
  .state-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

  .view-details:hover {
    background-color: #66b1ff; /* 鼠标悬浮时背景变浅 */
  }
  
  .pagination {
    text-align: center;
  }

  .details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.details-header h2 {
  font-size: 24px;
  margin: 0;
}
.close-btn {
  font-size: 20px;
}
.details-container {
  display: flex;
  gap: 20px;
  height: 80vh;
}
.details-left {
  width: 40%;
  overflow-y: auto;
}
.custom-table .enabled {
  color: blue;
}
.custom-table .disabled {
  color: gray;
}
.more-btn {
  margin-left: 10px;
}
.details-right {
  width: 60%;
  overflow-y: auto;
  border-left: 1px solid #e4e4e4;
  padding: 10px;
}
.tab-header {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
.info-section p,
.settings-section p {
  margin: 10px 0;
}
.el-dialog {
  z-index: 9999; /* 确保弹窗层级高于其他元素 */
  overflow: visible;
}
.custom-dialog .el-dialog {
  border-radius: 12px; /* 圆角 */
}
.factor-switches {
  display: flex;
  flex-direction: column;
}

/* 因子项确保它们占据一行 */
.factor-item {
  display: flex;
  justify-content: space-between; /* 标签和开关两端对齐 */
  align-items: center;
  margin-bottom: 10px;
}
.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  margin-left: 10px;
}

.details-container {
  display: flex;
  margin-top: 20px;
}

.details-left {
  flex: 1;
  margin-right: 20px;
}

.details-right {
  flex: 2;
}
.custom-table {
  width: 100%;
}
.parameter-status {
  display: flex;
  align-items: center;
}

.switch {
  margin-right: 10px;
}

.arrow {
  cursor: pointer;
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.tab-header {
  margin-bottom: 20px;
}

.info-section, .settings-section {
  padding: 20px;
}

.custom-table {
  width: 100%;
}
  </style>
  