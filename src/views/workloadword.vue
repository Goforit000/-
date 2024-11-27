<template>
  <div id="app">
    <h1>造价综合评估结果</h1>

    <!-- 下拉框与导出按钮放在同一行 -->
    <div>
      <el-row type="flex" justify="center" align="middle">
        <el-col :span="6">
          <!-- 缩小下拉框宽度 -->
          <el-select v-model="selectedProjectId" placeholder="请选择项目" @change="fetchSubsystemData" style="width: 150px;">
            <el-option
              v-for="project in projects"
              :key="project.projectId"
              :label="project.projectName"
              :value="project.projectId"
            />
          </el-select>
        </el-col>
        <el-col :span="6" style="text-align: right; padding-left: 20px;">
          <!-- 导出 Word 按钮 -->
          <el-button @click="downloadWord" :disabled="!selectedProjectId" type="primary" style="margin-top: 20px;">
            导出 Word 文件
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 显示项目和子系统信息的表格 -->
    <div v-if="subsystems.length > 0" style="margin-top: 20px;">
      <h2>项目详细信息</h2>
      <el-table :data="subsystems" style="width: 100%" :row-class-name="tableRowClassName">
        <el-table-column prop="bigProjectId" label="项目 ID" />
        <el-table-column prop="projectId" label="子系统 ID" />
        <el-table-column prop="projectName" label="子系统名称" />
        <el-table-column prop="subsystemDescribe" label="子系统描述" />
        <el-table-column prop="upperPDR" label="上限 PDR" />
        <el-table-column prop="midPDR" label="中位 PDR" />
        <el-table-column prop="floorPDR" label="下限 PDR" />
        <el-table-column prop="businessDomain" label="业务领域" />
        <el-table-column prop="applicationType" label="应用类型" />
        <el-table-column prop="qualityCharacteristic" label="质量特性" />
        <el-table-column prop="developLanguage" label="开发语言" />
        <el-table-column prop="teamBackground" label="团队背景" />
        <el-table-column prop="upperWorkload" label="上限工作量" />
        <el-table-column prop="midWorkload" label="中位工作量" />
        <el-table-column prop="floorWorkload" label="下限工作量" />
        <el-table-column prop="upperCost" label="上限成本" />
        <el-table-column prop="midCost" label="中位成本" />
        <el-table-column prop="floorCost" label="下限成本" />
        <el-table-column prop="upperAdjustedCost" label="上限调整成本" />
        <el-table-column prop="midAdjustedCost" label="中位调整成本" />
        <el-table-column prop="floorAdjustedCost" label="下限调整成本" />
        <el-table-column prop="directNonhumanCost" label="直接非人工成本" />
        <el-table-column prop="risk" label="风险" />
        <el-table-column prop="straightQualityGrade" label="质量等级" />
        <el-table-column prop="laborCostRate" label="劳动成本率" />
      </el-table>

      <!-- 项目综合评估结果 -->
      <div style="margin-top: 20px;">
        <h3>项目综合评估结果: {{ totalEvaluation.toFixed(2) }}万元</h3>
      </div>

      <!-- 绘制条形图 -->
      <div id="costBarChart" style="width: 100%; height: 400px; margin-top: 30px;"></div>

      <!-- 绘制工作量饼状图 -->
      <div id="workloadPieChart" style="width: 100%; height: 400px; margin-top: 30px;"></div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'WorkloadWord',
  data() {
    return {
      selectedProjectId: '', // 选择的项目ID
      projects: [],  // 项目列表
      subsystems: [],  // 子系统详细信息
      totalEvaluation: 0,  // 项目综合评估结果
    };
  },
  created() {
    // 从路由参数中获取传递的 bigProjectId
    const projectIdFromRoute = this.$route.params.bigProjectId;

    if (projectIdFromRoute) {
      this.selectedProjectId = projectIdFromRoute;
      this.fetchSubsystemData();
    }

    // 调用方法加载项目列表
    this.loadProjects();
  },
  methods: {
    // 获取项目列表
    loadProjects() {
      fetch('http://localhost:8080/projects/listThird', {  
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      })
      .then(response => response.json())
      .then(data => {
        if (data.isOK && Array.isArray(data.data)) {
          this.projects = data.data; 
        } else {
          throw new Error(data.msg || '项目列表数据格式不正确');
        }
      })
      .catch(error => {
        console.error('获取项目列表时出错:', error);
        alert('获取项目列表失败，请稍后再试！');
      });
    },

    // 获取子系统数据
    fetchSubsystemData() {
      fetch(`http://localhost:8080/cost-evaluation/subsystems/${this.selectedProjectId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      })
      .then(response => response.json())
      .then(data => {
        if (Array.isArray(data)) {
          this.subsystems = data;
          this.calculateTotalEvaluation();
          this.renderCostBarChart();  // 绘制条形图
          this.renderWorkloadPieChart(); // 绘制工作量饼状图
        }
      })
      .catch(error => {
        console.error('获取子系统数据时出错:', error);
      });
    },

    // 设置表格行的样式
    tableRowClassName({ rowIndex }) {
      return rowIndex % 2 === 0 ? 'even-row' : 'odd-row';  
    },

    // 计算项目综合评估结果
    calculateTotalEvaluation() {
      let total = 0;
      this.subsystems.forEach(subsystem => {
        const upperAdjustedCost = parseFloat(subsystem.upperAdjustedCost || 0);
        const midAdjustedCost = parseFloat(subsystem.midAdjustedCost || 0);
        const floorAdjustedCost = parseFloat(subsystem.floorAdjustedCost || 0);
        const averageAdjustedCost = (upperAdjustedCost + midAdjustedCost + floorAdjustedCost) / 3;
        total += averageAdjustedCost;  
      });
      this.totalEvaluation = total;
    },

    // 绘制条形图
    renderCostBarChart() {
      const chartElement = document.getElementById('costBarChart');
      const myChart = echarts.init(chartElement);

      const categories = this.subsystems.map(subsystem => subsystem.projectName);
      const upperAdjustedCosts = this.subsystems.map(subsystem => subsystem.upperAdjustedCost || 0);
      const midAdjustedCosts = this.subsystems.map(subsystem => subsystem.midAdjustedCost || 0);
      const floorAdjustedCosts = this.subsystems.map(subsystem => subsystem.floorAdjustedCost || 0);

      const option = {
        title: {
          text: '子系统调整成本对比',
        },
        tooltip: {
          trigger: 'axis',
        },
        legend: {
          data: ['上限调整成本', '中位调整成本', '下限调整成本'],
        },
        xAxis: {
          type: 'category',
          data: categories,
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: '上限调整成本',
            type: 'bar',
            data: upperAdjustedCosts,
          },
          {
            name: '中位调整成本',
            type: 'bar',
            data: midAdjustedCosts,
          },
          {
            name: '下限调整成本',
            type: 'bar',
            data: floorAdjustedCosts,
          },
        ],
      };

      myChart.setOption(option);
    },

    // 绘制工作量饼状图
    renderWorkloadPieChart() {
      const chartElement = document.getElementById('workloadPieChart');
      const myChart = echarts.init(chartElement);

      const categories = this.subsystems.map(subsystem => subsystem.projectName);
      const averageWorkloads = this.subsystems.map(subsystem => {
        const upperWorkload = parseFloat(subsystem.upperWorkload || 0);
        const midWorkload = parseFloat(subsystem.midWorkload || 0);
        const floorWorkload = parseFloat(subsystem.floorWorkload || 0);
        return (upperWorkload + midWorkload + floorWorkload) / 3;
      });

      const option = {
        title: {
          text: '子系统工作量分布',
          subtext: '按平均工作量分布',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        series: [
          {
            name: '平均工作量',
            type: 'pie',
            radius: ['50%', '70%'],
            data: categories.map((name, index) => ({
              name,
              value: averageWorkloads[index],
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      };

      myChart.setOption(option);
    },

    // 点击按钮时发起请求下载 Word 文件
    downloadWord() {
      if (!this.selectedProjectId) {
        alert('请先选择一个项目！');
        return;
      }

      fetch(`http://localhost:8080/api/exportWord?bigProjectId=${this.selectedProjectId}`, {
        method: 'GET',
        headers: {
          'Accept': 'application/msword'
        }
      })
      .then(response => response.blob())  
      .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = 'exported_file.docx';  
        document.body.appendChild(a);
        a.click();  
        window.URL.revokeObjectURL(url);  
      })
      .catch(error => {
        console.error('导出 Word 文件时出错:', error);
        alert('导出失败，请稍后再试！');
      });
    },
  },
};
</script>

<style scoped>
#app {
  text-align: center;
  margin-top: 50px;
}

el-button {
  margin-top: 20px;
}

el-select {
  margin-top: 10px;
}

el-table {
  margin-top: 20px;
  width: 100%;
}

.even-row {
  background-color: #f4f4f4; /* 浅灰色背景 */
}

.odd-row {
  background-color: #ffffff; /* 白色背景 */
}

#costBarChart,
#workloadPieChart {
  width: 100%;
  height: 400px;
  margin-top: 30px;
}
</style>
