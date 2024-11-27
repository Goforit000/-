
<template>
  <div class="container">
    <!-- 顶部统计信息卡片 -->
    <div class="statistics-container">
      <el-card class="stat-card">
        <div class="stat-title">调整前规模</div>
        <div class="stat-value">{{ preAdjustmentPoints }} 功能点</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">调整后规模</div>
        <div class="stat-value">{{ postAdjustmentPoints }} 功能点</div>
      </el-card>
    </div>
    <br>
    <!-- 功能点表格 -->
    <el-table :data="functions":span-method="mergeCells" style="width: 100%">
      <el-table-column label="项目ID" prop="projectId" align="center"></el-table-column>
      <el-table-column label="子系统" prop="subsystemName" align="center"></el-table-column>
       <el-table-column label="子系统描述" prop="subsystemDescribe" align="center"></el-table-column>
      <el-table-column label="调前功能点数" prop="unadjustedFp" align="center"></el-table-column>
      <el-table-column label="调后功能点数" prop="adjustedFp" align="center"></el-table-column>
      <el-table-column label="功能点名称" prop="featureName" align="center"></el-table-column>
      <el-table-column label="功能类型" prop="featureType" align="center"></el-table-column>
      <el-table-column label="重用程度" align="center">
        <template #default="scope">
          <el-select v-model="scope.row.complexity"  prop="complexity" placeholder="选择重用程度">
            <el-option label="低" value="低"></el-option>
            <el-option label="中" value="中"></el-option>
            <el-option label="高" value="高"></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="修改类型" align="center">
        <template #default="scope">
          <el-select v-model="scope.row.modifyType"prop="modifyType" placeholder="选择修改类型">
            <el-option label="新增" value="新增"></el-option>
            <el-option label="修改" value="修改"></el-option>
            <el-option label="删除" value="删除"></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="notes" align="center"></el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-link type="primary" @click="goToEditPage(scope.row)">修改</el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-button type="primary" @click="returnFirst" class="next-btn">返回项目页</el-button>
     <el-button type="primary" @click="nextStep" class="next-btn">进行评估</el-button>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router'; // 导入 Vue Router
import axios from 'axios';

const router = useRouter(); // 获取路由实例
const route = useRoute();
const fileName = ref<string>(String(route.query.fileName));
const preAdjustmentPoints = ref(0); // 调整前规模点数
const postAdjustmentPoints = ref(0);
const functions = ref([]);
const projectId = route.params.projectId;

const mergeCells = ({ row, column, rowIndex, columnIndex }: any) => {
  const fieldsToMerge = ["projectId", "subsystemName", "subsystemDescribe", "unadjustedFp", "adjustedFp"];

  if (fieldsToMerge.includes(column.property)) {
    const currentField = column.property;
    let rowspan = 1;

    // 从当前行向下检查，计算需要合并的行数
    for (let i = rowIndex + 1; i < functions.value.length; i++) {
      if (functions.value[i][currentField] === row[currentField]) {
        rowspan++;
      } else {
        break;
      }
    }

    // 如果是第一行（即合并起始行），返回 rowspan；否则返回 [0, 0]
    return rowIndex === 0 || functions.value[rowIndex - 1][currentField] !== row[currentField]
      ? [rowspan, 1] // 合并行数
      : [0, 0]; // 当前单元格被上方单元格合并
  }

  return [1, 1]; // 默认显示
};




let rowSpanCache: Record<string, number> = {};

const fetchFunctions = async () => {
  console.log("projectId", projectId);
  try {
    const response = await axios.get("http://localhost:8080/detail/getall", {
      params: { projectId: projectId },
    });
    functions.value = response.data.data;
    rowSpanCache = {}; // 清空缓存
    console.log(response.data.data);
  } catch (error) {
    console.error("获取功能点数据失败：", error);
  }
};


// 获取功能点数据
// const fetchFunctions = async () => {
//   console.log("projectId", projectId);
//   try {
//     const response = await axios.get("http://localhost:8080/detail/getall", {
//       params: { projectId: projectId},
//     });
//     functions.value = response.data.data;
//     console.log(response.data.data);
//   } catch (error) {
//     console.error("获取功能点数据失败：", error);
//   }
// };

const fetchPreAdjustmentPoints = async () => {
  try {
    const response = await axios.get("http://localhost:8080/detail/getpre", {
      params: { projectId },
    });
    preAdjustmentPoints.value = response.data.data || 0;
  } catch (error) {
    console.error("获取调整前规模数据失败：", error);
  }
};

const fetchPostAdjustmentPoints = async () => {
  try {
    const response = await axios.get("http://localhost:8080/detail/getadjusted", {
      params: { projectId },
    });
    postAdjustmentPoints.value = (response.data.data || 0).toFixed(2);

  } catch (error) {
    console.error("获取调整后规模数据失败：", error);
  }
};

// 跳转到修改页面，并传递功能点数据
const goToEditPage = (row: any) => {
   router.push({
  name: 'systempoint',
     params: { projectId: projectId },
     query: {fileName: fileName.value } // 通过 params 传递 projectId  // 如果需要传递 fileName，可以继续使用 query
});
};

onMounted(() => {
  fetchFunctions();
  fetchPreAdjustmentPoints();
  fetchPostAdjustmentPoints();
});
// 下一步
const returnFirst = () => {
  console.log("projectId Detail", projectId);
  router.push({
    path: '/projectManage',  // 使用路由的 name

     // 如果需要传递 fileName，可以继续使用 query
  });
};
const nextStep = () => {
  console.log("projectId Detail", projectId);
  router.push({
    name: 'evaluation',  // 使用路由的 name
    
    params: { projectId: projectId },// 通过 params 传递 projectId
    query: { fileName: fileName.value } 
     // 如果需要传递 fileName，可以继续使用 query
  });
};
</script>

<style scoped>
.container {
  padding: 20px;
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
</style>
