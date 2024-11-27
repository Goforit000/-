<template>
  <div class="project-details">
    <div class="main-content">
      <!-- 左侧文档区域 -->
        <!-- 左侧文档区域 -->
      <div class="left-panel">
        <vue-office-docx
        :src="docxSrc"
        style="height: 100vh;"
        @rendered="renderedHandler"
        @error="errorHandler"
    />
      </div>

      <!-- 右侧选择区域 -->
      <div class="right-panel">
        <el-row gutter={20}>
          <!-- 步骤一: 展示前五个选择框 -->
          <template v-if="!stepTwo">
            <el-col :span="24">
              <div class="select-container-1">
                <!-- 业务领域选择框 -->
                <el-select
                  v-model="businessDomain"
                  placeholder="请选择业务领域"
                  class="select-box"
                  @change="updateBusinessDomainValues"
                >
                  <el-option
                    v-for="item in businessDomains"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
                
                <!-- 值显示区域 -->
                <div class="value-display-1">
                  <p>下限值：{{ businessDomainValues.lower }}</p>
                  <p>中值：{{ businessDomainValues.middle }}</p>
                  <p>上限值：{{ businessDomainValues.upper }}</p>
                </div>
              </div>
            </el-col>

            <el-col :span="24">
              <div class="select-container">
                <el-select v-model="applicationType" placeholder="请选择应用类型" class="select-box" @change="updateApplicationTypeValue">
                  <el-option v-for="item in applicationTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
                <span class="value-display">{{ applicationTypeValue }}</span>
              </div>
            </el-col>

            <el-col :span="24">
              <div class="select-container">
                <el-select v-model="qualityCharacteristic" placeholder="请选择质量特性" class="select-box" @change="updateQualityCharacteristicValue">
                  <el-option v-for="item in qualityCharacteristics" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
                <span class="value-display">{{ qualityCharacteristicValue }}</span>
              </div>
            </el-col>

            <el-col :span="24">
              <div class="select-container">
                <el-select v-model="developLanguage" placeholder="请选择开发语言" class="select-box" @change="updateDevelopLanguageValue">
                  <el-option v-for="item in developLanguages" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
                <span class="value-display">{{ developLanguageValue }}</span>
              </div>
            </el-col>

            <el-col :span="24">
              <div class="select-container">
                <el-select v-model="teamBackground" placeholder="请选择开发团队背景" class="select-box" @change="updateTeamBackgroundValue">
                  <el-option v-for="item in teamBackgrounds" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
                <span class="value-display">{{ teamBackgroundValue }}</span>
              </div>
            </el-col>


          <!-- 下一步按钮 -->
          <el-col :span="24">
            <el-button type="primary" @click="nextStep">下一步</el-button>
          </el-col>

          </template>

          <!-- 步骤二: 展示后四个选择框 -->
          <template v-else>
            <el-col :span="24">
              <div class="select-container">
                <el-select v-model="risk" placeholder="请选择风险调整因子" class="select-box" @change="updateRiskValue">
                  <el-option v-for="item in risks" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
                <span class="value-display">{{ riskValue }}</span>
              </div>
            </el-col>

            <el-col :span="24">
              <div class="select-container">
                <el-select v-model="labor_cost_rate" placeholder="请选择人力成本费率" class="select-box" @change="updateLabor_cost_rateValue">
                  <el-option v-for="item in labor_cost_rates" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
                <span class="value-display">{{ labor_cost_rateValue }}</span>
              </div>
            </el-col>

            <el-col :span="24">
              <div class="select-container">
                <el-input 
                  v-model="direct_nonhuman_costValue" 
                  placeholder="请输入直接非人力成本" 
                  @input="updateDirectNonHumanCostValue"
                  class="custom-input">
                  <template #suffix>
                    万元
                  </template>
                </el-input>
              </div>
            </el-col>


            <el-col :span="24">
              <div class="select-container">
                <el-select v-model="straight_quality_grade" placeholder="请选择直质量等级因子" class="select-box" @change="updateStraight_quality_gradeValue">
                  <el-option v-for="item in straight_quality_grades" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
                <span class="value-display">{{ straight_quality_gradeValue }}</span>
              </div>
            </el-col>

            <!-- 计算按钮 -->
          <el-col :span="24">
            <el-button type="primary" @click="fetchWorkloadData">计算</el-button>
          </el-col>
          <!-- 计算结果显示 -->
          <el-col :span="24">
            <div class="result-display">
              <p>调整后的估算工作量上限值: {{ upperWorkload }}</p>
              <p>调整后的估算工作量中值: {{ midWorkload }}</p>
              <p>调整后的估算工作量下限值: {{ floorWorkload }}</p>
            </div>
          </el-col>

          <!-- 计算结果显示 -->
          <el-col :span="24">
            <div class="result-display">
              <p>调整前的软件开发成本上限值: {{ upperCost }}</p>
              <p>调整前的软件开发成本中值: {{ midCost }}</p>
              <p>调整前的软件开发成本下限值: {{ floorCost }}</p>
            </div>
          </el-col>

          <el-col :span="24">
            <div class="result-display">
              <p>调整后的软件开发成本上限值: {{ upperAdjustedCost }}</p>
              <p>调整后的软件开发成本中值: {{ midAdjustedCost }}</p>
              <p>调整后的软件开发成本下限值: {{floorAdjustedCost}}</p>
            </div>
          </el-col>
      
          <!-- 下一步按钮 -->
          <button @click="nextStepPage">下一步</button>
      
          </template>
        </el-row>
      </div>
    </div>
  </div>
</template>


<script>
import axios from 'axios';
import VueOfficeDocx from "@vue-office/docx";
import "@vue-office/docx/lib/index.css";
export default {
  components: {
        VueOfficeDocx
    },
  data() {
    return {
      project: {
        name: '',
        documents: [] // 用来存放文档内容
      },
      loading: true, // 是否加载中
      fileName: '',
      projectIid: '',
      projectId: '',
      stateId: '',
      stepTwo: false, // 控制步骤的状态，初始为步骤一
      docxSrc: "",
      projectId: null, // 用于接收传递的项目 ID
      stateId: null,    // 用于接收传递的状态 ID
      // 业务领域调整因子数据
      businessDomain: null,
      businessDomains: [
        { value: '1', label: '电子政务' },
        { value: '2', label: '金融' },
        { value: '3', label: '电信' },
        { value: '4', label: '制造' },
        { value: '5', label: '能源' },
        { value: '6', label: '交通' }
      ],
      businessDomainValues: { lower: '', middle: '', upper: '' }, // 对应业务领域的上下限值
      businessDomainMapping: {
        1: { lower: 2.97, middle: 6.54, upper: 11.09 },
        2: { lower: 5.25, middle: 10.67, upper: 15.93 },
        3: { lower: 4.74, middle: 10.12, upper: 16.50 },
        4: { lower: 3.47, middle: 7.88, upper: 16.29 },
        5: { lower: 3.82, middle: 7.25, upper: 17.37 },
        6: { lower: 3.18, middle: 7.03, upper: 15.77 }
      },

      // 应用类型调整因子数据
      applicationType: null,
      applicationTypes: [
        { value: '1', label: '业务处理' },
        { value: '2', label: '科技' },
        { value: '3', label: '多媒体' },
        { value: '4', label: '智能信息' },
        { value: '5', label: '基础软件/支撑软件' },
        { value: '6', label: '通信控制' },
        { value: '7', label: '流程控制' }
      ],
      applicationTypeValues: {
        1: 1.0,
        2: 1.2,
        3: 1.3,
        4: 1.5,
        5: 1.7,
        6: 1.9,
        7: 2.0
      },
      applicationTypeValue: '',

      // 质量特性调整因子数据
      qualityCharacteristic: null,
      qualityCharacteristics: [
        { value: '1', label: '应用云化' },
        { value: '2', label: '跨平台/设备' },
        { value: '3', label: '分布式处理' }
      ],
      qualityCharacteristicValues: {
        1: 1.2,
        2: 1.3,
        3: 1.5
      },
      qualityCharacteristicValue: '',

      // 开发语言调整因子数据
      developLanguage: null,
      developLanguages: [
        { value: '1', label: 'C及其他同级别语言' },
        { value: '2', label: 'JAVA、C++及其他同级别语言' },
        { value: '3', label: 'PowerBuilder、ASP及其他同级别语言' }
      ],
      developLanguageValues: {
        1: 1.2,
        2: 1.0,
        3: 0.8
      },
      developLanguageValue: '',

      // 开发团队背景因子数据
      teamBackground: null,
      teamBackgrounds: [
        { value: '1', label: '为本行业开发过类似的项目' },
        { value: '2', label: '为其他行业开发过类似的项目或为本行业开发过不同但相关的项目' },
        { value: '3', label: '没有同类项目的背景' }
      ],
      teamBackgroundValues: {
        1: 0.8,
        2: 1.0,
        3: 1.2
      },
      teamBackgroundValue: '',

      upperWorkload: null,
      midWorkload: null,
      floorWorkload: null,

    
    risk: null,
    risks: [
      { value: '1', label: '低风险' },
      { value: '2', label: '中风险' },
      { value: '3', label: '高风险' }
    ],
    riskValues: {
      1: 1.0,  
      2: 1.2, 
      3: 1.4  
    },
    riskValue: '',

    labor_cost_rate: null,
    labor_cost_rates: [
      { value: '1', label: '北京' },
      { value: '2', label: '上海' },
      { value: '3', label: '广州' },
      { value: '4', label: '成都' },
      { value: '5', label: '深圳' }
    ],
    labor_cost_rateValues: {
      1: 3.2,  
      2: 3.1, 
      3: 2.8,      
      4: 2.6, 
      5: 3.1
    },
    labor_cost_rateValue: '',

    direct_nonhuman_costValue: '',

    straight_quality_grade: null,
    straight_quality_grades: [
      { value: '1', label: '优' },
      { value: '2', label: '良' },
      { value: '3', label: '合格' },
      { value: '4', label: '次' },
      { value: '5', label: '不合格' }
    ],
    straight_quality_gradeValues: {
      1: 1.0, 
      2: 0.9,  
      3: 0.8,
      4: 0.6,  
      5: 0.0    
    },
    straight_quality_gradeValue: '',

    upperCost: null,
    midCost: null,
    floorCost: null,
    upperAdjustedCost: null,
    midAdjustedCost: null,
    floorAdjustedCost: null,

    };
  },
  methods: {
    // 获取项目数据和文档内容
    async fetchProjectData() {
      const projectId = this.projectId; // 使用接收到的 projectId
      if (!projectId) {
        console.error('Project ID is missing');
        return;
      }
      try {
        this.loading = true; // 开始加载
        const response = await axios.get(`http://localhost:8080/cost-evaluation/${projectId}`);
        if (response.data) {
          this.project = response.data; // 更新项目数据
        }
      } catch (error) {
        console.error('Error fetching project data:', error);
      } finally {
        this.loading = false; // 数据加载完成
      }
    },
    fetchFileContent() {
  console.log('Project ID2:', this.projectIid);  // 通过 this 获取 projectId
  console.log('File Name2:', this.fileName);    // 通过 this 获取 fileName

  // 使用组件内的 projectId 和 fileName 拼接文件路径
  const path = `\\${this.projectIid}\\${this.fileName}`;  
  // const path="\\8\\用例具体描述.docx"
  this.docxSrc = path;  // 更新 docxSrc 为文件路径

  console.log('File Path:', this.docxSrc);    // 打印文件路径
  console.log('File Name:', this.fileName);   // 打印文件名
},

    // 切换到步骤二
    nextStep() {
      this.stepTwo = true;
    },
    nextStepPage() {
      //if (!data) {
        //alert("请先计算");
        //return;
      //}
      console.log('Project ID:', this.projectId);
      this.$router.push({
        name: 'workloadresult',
        params: {
          projectId: this.projectId
        }
      });
    console.log('执行下一步操作');
  },
    // 更新业务领域值
    //updateBusinessDomainValues() {
      //if (this.businessDomain && this.businessDomainMapping[this.businessDomain]) {
        //this.businessDomainValues = this.businessDomainMapping[this.businessDomain];
      //} else {
        //this.businessDomainValues = { lower: '', middle: '', upper: '' };
      //}
    //},
    // 更新业务领域值
updateBusinessDomainValues() {
if (this.businessDomain && this.businessDomainMapping[this.businessDomain]) {
  // 从映射中获取对应的业务领域值
  const { lower, middle, upper } = this.businessDomainMapping[this.businessDomain];
  
  // 保存到相应的变量中
  this.upperPDR = upper;
  this.midPDR = middle;
  this.floorPDR = lower;
  
  // 同时更新 businessDomainValues
  this.businessDomainValues = { lower, middle, upper };
} else {
  // 如果没有对应的映射数据，清空值
  this.businessDomainValues = { lower: '', middle: '', upper: '' };
  this.upperPDR = null;  // 可以设置为 null 或其他默认值
  this.midPDR = null;
  this.floorPDR = null;
}
},

    // 更新应用类型值
    updateApplicationTypeValue() {
      this.applicationTypeValue = this.applicationTypeValues[this.applicationType] || '';
    },
    // 更新质量特性值
    updateQualityCharacteristicValue() {
      this.qualityCharacteristicValue = this.qualityCharacteristicValues[this.qualityCharacteristic] || '';
    },
    // 更新开发语言值
    updateDevelopLanguageValue() {
      this.developLanguageValue = this.developLanguageValues[this.developLanguage] || '';
    },
    // 更新开发团队背景值
    updateTeamBackgroundValue() {
      this.teamBackgroundValue = this.teamBackgroundValues[this.teamBackground] || '';
    },
     // 更新风险因子值
    updateRiskValue() {
      this.riskValue = this.riskValues[this.risk] || '';
    },
    // 更新人力成本费率值
    updateLabor_cost_rateValue() {
      this.labor_cost_rateValue = this.labor_cost_rateValues[this.labor_cost_rate] || '';
    },
    // 更新直接非人力成本值
      updateDirectNonHumanCostValue() {
        // 你可以根据输入的值进行任何计算或处理
        // 如果需要，你可以将它转化为数字，或进行其他逻辑
        const value = parseFloat(this.direct_nonhuman_costValue);
        if (!isNaN(value)) {
          // 处理数值，比如存储或计算
        } else {
          // 处理非数值情况
        }
      },
    // 更新直质量等级因子值
    updateStraight_quality_gradeValue() {
      this.straight_quality_gradeValue = this.straight_quality_gradeValues[this.straight_quality_grade] || '';
    },
 

    
    // 调用后端接口获取工作量数据
    fetchWorkloadData() {
      // 发送请求到后端获取计算结果
      const payload = {
        projectId: this.projectId,
        projectName: this.project.projectName,
        bigProjectId: this.project.bigProjectId,
        upperPDR: this.upperPDR,
        midPDR: this.midPDR,
        floorPDR: this.floorPDR,
        businessDomain: this.businessDomain,
        applicationType: this.applicationTypeValues[this.applicationType] || '',  // 获取实际值
        qualityCharacteristic: this.qualityCharacteristicValues[this.qualityCharacteristic] || '',  // 获取实际值
        developLanguage: this.developLanguageValues[this.developLanguage] || '',  // 获取实际值
        teamBackground: this.teamBackgroundValues[this.teamBackground] || '',  // 获取实际值
        risk: this.riskValues[this.risk] || '',  // 获取实际值
        laborCostRate: this.labor_cost_rateValues[this.labor_cost_rate] || '',  // 获取实际值
        directNonhumanCost: this.direct_nonhuman_costValue,  
        straightQualityGrade: this.straight_quality_gradeValues[this.straight_quality_grade] || '',  // 获取实际值
      };

      axios.post('http://localhost:8080/cost-evaluation/add', payload)
        .then(response => {
          const data = response.data; 
          this.projectId=data.projectId;
          this.upperWorkload = data.upperWorkload.toFixed(2);
          this.midWorkload = data.midWorkload.toFixed(2);
          this.floorWorkload = data.floorWorkload.toFixed(2);
          this.upperCost = data.upperCost.toFixed(2);
          this.midCost = data.midCost.toFixed(2);
          this.floorCost = data.floorCost.toFixed(2);
          this.upperAdjustedCost=data.upperAdjustedCost.toFixed(2);
          this.midAdjustedCost=data.midAdjustedCost.toFixed(2);
          this.floorAdjustedCost=data.floorAdjustedCost.toFixed(2);
          this.bigProjectId=payload.bigProjectId;
        })
        .catch(error => {
          console.error("Error fetching workload data:", error);
        });
    }
  },

  watch: {
   
  // 监听路由变化，当projectId变化时重新加载数据
  '$route.params.projectId': {
    handler(newProjectId) {
      if (newProjectId) {
        this.projectId = newProjectId; // 更新 projectId
        this.fetchProjectData(); // 根据新的 projectId 获取数据
      }
    },
    immediate: true, // 初始化时触发一次
  },
  '$route.params.stateId': {
    handler(newStateId) {
      if (newStateId) {
        this.stateId = newStateId; // 更新 stateId
      }
    },
    immediate: true, // 初始化时触发一次
  }
},
mounted() {
  // 获取并保存路由中的参数
  this.fileName = this.$route.query.fileName;
  console.log('File Name:', this.fileName);
  this.projectId = this.$route.params.projectId;
  this.projectIid = this.$route.params.projectIid;
  console.log('Project ID:', this.projectId);
  this.stateId = this.$route.params.stateId;
  console.log('State ID:', this.stateId);
  
  // 在组件加载时获取项目数据
  this.fetchFileContent();
  this.fetchProjectData();
  
}
};
</script>

<style scoped>
.project-details {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40pt; /* 设置页面的整体边距 */
}

.main-content {
  display: flex;
  width: 100%;
}

.left-panel {
  flex: 1;
  width:50%;
  padding: 16px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加阴影 */
  border-radius: 8px; /* 圆角效果 */
  background-color: #fff; /* 背景色 */
  border-right: 1px solid #e0e0e0;
  box-sizing: border-box;
}

.middle-space {
  width: 25pt;
}

.right-panel {
  flex: 1;
  display: flex;
  width:50%;
  padding: 16px;
  flex-direction: column;
  padding: 40pt;  /* 设置右侧区域的边距 */
  box-sizing: border-box;
}

.select-container {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  width: 100%;
}

.select-box {
  width: 260px; /* 设置下拉框宽度 */
  font-size: 14px;
  margin-right: 25px;
}

.select-container-1 {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column; /* 垂直排列 */
  width: 100%;
}

.value-display-1 {
  font-size: 14px;
  color: #666; /* 字体颜色 */
  background-color: #f9f9f9; /* 背景颜色 */
  border: 1px solid #dcdcdc; /* 边框颜色 */
  border-radius: 4px; /* 圆角 */
  padding: 6px; /* 内部间距 */
  line-height: 1; /* 行距 */
  white-space: normal; /* 防止内容不换行 */
  height: 94px; /* 固定高度，确保选择框前后大小一致 */
  width:94px;
  overflow: hidden; /* 隐藏溢出的内容 */
}

.value-display {
  font-size: 14px;
  color: #666; /* 字体颜色 */
  background-color: #f9f9f9; /* 背景颜色 */
  border: 1px solid #dcdcdc; /* 边框颜色 */
  border-radius: 4px; /* 圆角 */
  padding: 6px; /* 内部间距 */
  line-height: 1.6; /* 行距 */
  white-space: normal; /* 防止内容不换行 */
  height: 22px;
  width: 20px;
  overflow: hidden; /* 隐藏溢出的内容 */
}

.value-display p {
  display: block; /* 确保每个段落独占一行 */
  margin: 0; 
  line-height: 1.6; /* 行距 */
  white-space: normal; /* 防止内容不换行 */
}

.custom-input {
  width: 260px;
}

.result-display {
  margin-top: 20px;
  padding: 10px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  color: #333;
  width: 400px;
}

.el-select {
  height: 36px; /* 设置下拉框高度 */
}

.el-select .el-input__inner {
  height: 36px !important; /* 强制设置下拉框输入框的高度 */
  padding: 0 10px; /* 设置适当的内边距 */
}

.el-select .el-input__suffix {
  height: 36px !important; /* 确保下拉框的按钮与输入框对齐 */
}

.el-option {
  font-size: 14px;
}
</style>