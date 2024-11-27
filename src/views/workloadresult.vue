<template>
  <div>
    <el-table
      :data="tableData"
      :span-method="objectSpanMethod"
      border
      style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="category" label="类别" width="180"></el-table-column>
      <el-table-column prop="feature" label="特征"></el-table-column>
      <el-table-column prop="value" label="取值"></el-table-column>
      <el-table-column prop="description" label="取值说明"></el-table-column>
    </el-table>
  </div>
  <!-- 下一步按钮 -->
      <el-col :span="24">
          <el-button type="primary" @click="goback">返回</el-button>
          <el-button type="primary" @click="goToWorkloadWord">下一步</el-button>
      </el-col>
     
</template>

<script>
export default {
  data() {
    return {
      tableData: [
        { category: "软件规模度量", feature: "未调整功能点数", value: "", description: "" },
        { category: "软件规模度量", feature: "调整后的功能点数", value: "", description: "" },
        { category: "工作量度量", feature: "功能点耗时率", value: "", description: "依据《2024年软件行业基准数据-CSBMK-202410》" },

        { category: "工作量度量", feature: "业务领域调整因子", value: "", description: "性能效率: 参考GB/T 25000.10-2016对性能效率提出的要求\n兼容性: 参考GB/T 25000.10-2016对兼容性提出的要求\n可靠性: 没有明确对可靠性的要求\n可移植性: 没有明确对可移植性的要求" },
        { category: "工作量度量", feature: "应用类型调整因子", value: "", description: "办公自动化系统；人事、会计、工资、销售等经营管理及业务处理用软件等" },
        { category: "工作量度量", feature: "质量特性调整因子", value: "", description: "依据《2024年软件行业基准数据-CSBMK-202410》" },
        { category: "工作量度量", feature: "开发语言调整因子", value: "", description: "依据《2024年软件行业基准数据-CSBMK-202410》" },
        { category: "工作量度量", feature: "开发团队背景因子", value: "", description: "依据《2024年软件行业基准数据-CSBMK-202410》" },
        { category: "工作量度量", feature: "调整后的估算工作量", value: " ", description: "" },
        { category: "成本估算", feature: "风险调整因子", value: [""], description: "依GB/T标准估算" },
        { category: "成本估算", feature: "调整前的软件开发成本", value: "", description: "" },
        { category: "成本估算", feature: "调整后的软件开发成本", value: " ", description: "" },
        { category: "成本估算", feature: "人力成本费率", value: [""], description: "" },
        { category: "成本估算", feature: "直接非人力成本", value: [""], description: "依据国标《软件工程软件开发成本度量规范》（GB∕T36964－2018），该部分费用单独测算" },
        { category: "成本估算", feature: "直质量等级因子", value: [""], description: "" },
        { category: "软件开发成本", feature: "最终费用SDC", value: "", description: "" },
        { category: "软件开发成本", feature: "功能点单价SDCH（元/功能点）", value: "", description: "" }
      ],
      API: 'http://localhost:8080', // 后端接口地址
      projectId: null, // 用来保存接收到的项目ID
    };
  },
  created() {
    // 获取传递的 projectId
    this.projectId = this.$route.params.projectId;
    console.log('Project ID we get:', this.projectId);
    if (this.projectId) {
      this.fetchData();
    } else {
      console.error("没有找到项目ID");
    }
  },
  methods: {
    fetchData() {
      // 使用 projectId 获取数据
      fetch(`${this.API}/cost-evaluation/${this.projectId}`, { // 假设后端接口通过项目ID查询数据
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        mode: 'cors', // 配置跨域请求
      })
        .then(response => response.json())
        .then(data => {
          this.updateTableData(data);
          this.bigProjectId=data.bigProjectId;
        })
        .catch(err => {
          console.error("Error loading data:", err);
        });
    },
    updateTableData(data) {
      // 手动调整后端数据与表格特征的对应关系
      const mapping = {
        "未调整功能点数": data.unadjustedFp,
        "调整后的功能点数": data.adjustedFP,
        "功能点耗时率": {
          "上限值:": data.upperPDR,
          "中值:": data.midPDR,
          "下限值:": data.floorPDR,
        },
        "业务领域调整因子": data.businessDomain,
        "应用类型调整因子": data.applicationType,
        "质量特性调整因子": data.qualityCharacteristic,
        "开发语言调整因子": data.developLanguage,
        "开发团队背景因子": data.teamBackground,
        "调整后的估算工作量": {
          "上限值:": data.upperWorkload,
          "中值:": data.midWorkload,
          "下限值:": data.floorWorkload,
        },
        "风险调整因子": data.risk,
        "调整前的软件开发成本": {
          "上限值:": data.upperCost,
          "中值:": data.midCost,
          "下限值:": data.floorCost,
        },
        "调整后的软件开发成本": {
          "上限值:": data.upperAdjustedCost,
          "中值:": data.midAdjustedCost,
          "下限值:": data.floorAdjustedCost,
        },
        "人力成本费率": data.laborCostRate,
        "直接非人力成本": data.directNonhumanCost,
        "直质量等级因子": data.straightQualityGrade,
        "最终费用SDC": "", // 计算值
        "功能点单价SDCH（元/功能点）": "", // 计算值
      };

      // 计算 finalSDC 和 unitPriceSDCH
      //const finalSDC = (data.upperAdjustedCost + data.midAdjustedCost + data.floorAdjustedCost) / 3;
      const finalSDC = parseFloat(((data.upperAdjustedCost + data.midAdjustedCost + data.floorAdjustedCost) / 3).toFixed(2));

      const unitPriceSDCH = (finalSDC / data.adjustedFP).toFixed(2);

      // 更新 mapping 中的 "最终费用SDC" 和 "功能点单价SDCH（元/功能点）"
      mapping["最终费用SDC"] = finalSDC;
      mapping["功能点单价SDCH（元/功能点）"] = unitPriceSDCH;

      // 更新 tableData 中的 value，检查是否是对象，若是对象则转化为字符串
      this.tableData.forEach(item => {
        const feature = item.feature;
        if (mapping[feature]) {
          const value = mapping[feature];
          // 如果值是对象，则提取其中的具体值
          if (typeof value === 'object') {
            item.value = Object.entries(value).map(([key, val]) => `${key} ${val}`).join(' | ');
          } else {
            item.value = value;
          }
        }
      });
    },
    goToWorkloadWord() {
      if (this.projectId) {
        console.log('Project ID hello:', this.bigProjectId);
        this.$router.push({ name: 'workloadword', params: { bigProjectId: this.bigProjectId } });
      } else {
        alert('没有找到项目ID');
      }
    },
    goback() {
  if (this.projectId) {
    // 使用 go(-1) 返回到上一个页面
    this.$router.go(-1);
  } else {
    alert('没有找到项目ID');
  }
},
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0 || columnIndex === 1) {
        let rowspan = 1;
        const key = columnIndex === 0 ? "category" : "feature";
        const currentValue = row[key];
        for (let i = rowIndex + 1; i < this.tableData.length; i++) {
          if (this.tableData[i][key] === currentValue) {
            rowspan++;
          } else {
            break;
          }
        }
        if (rowIndex === 0 || this.tableData[rowIndex - 1][key] !== currentValue) {
          return {
            rowspan: rowspan,
            colspan: 1,
          };
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          };
        }
      }
      return {
        rowspan: 1,
        colspan: 1,
      };
    },
  },
};
</script>
