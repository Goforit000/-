<template>
  <div class="upload-page">
    <h2>项目 ID: {{ projectId }}</h2> <!-- 显示传递的项目 ID -->
    
    <!-- Word文件上传部分 -->
    <div class="upload-section">
      <h3>Word文件上传的注意事项</h3>
      <ul class="instructions">
        <li>仅支持.docx格式的Word文档，如遇.doc格式的文件，请先在WPS或Office中将文件另存为（选择格式为.docx）</li>
        <li>Word文档请设置文档标题，否则无法解析文件的目录结构</li>
        <li>优先推荐上传Word格式的可研方案、设计方案、建设方案、实施方案，选择方案中系统功能章节进行评估</li>
      </ul>
      <el-upload
        action=""
        :http-request="handleUploadRequest"
        :before-upload="beforeWordUpload"
        accept=".docx"
        class="upload-btn"
        :limit="1"
      >
        <el-button type="primary">📤 点击上传Word文件</el-button>
      </el-upload>
    </div>

    <!-- Excel文件上传部分 -->
    <div class="upload-section">
      <h3>Excel文件上传的注意事项</h3>
      <ul class="instructions">
        <li>模板中所有标题含“必填”的列都是必填项，否则会出现解析失败的问题。</li>
        <li>模板中的“功能描述”列的各行必填项，否则无法解析</li>
      </ul>
      <el-button type="primary" @click="downloadExcelTemplate" class="download-btn">点击下载Excel格式模板</el-button>
      <el-upload
        action=""
        :http-request="handleUploadRequest"
        :before-upload="beforeExcelUpload"
        accept=".xls,.xlsx"
        class="upload-btn"
      >
        <el-button type="primary">📤 点击上传Excel文件</el-button>
      </el-upload>
    </div>
  </div>
  <el-button 
    type="primary" 
    @click="nextStep" 
    class="next-btn" 
    :disabled="!uploadedFileName" 
    >下一步</el-button>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();

//传递项目id
const route = useRoute();

// 用于存储上传后的文件名
const uploadedFileName = ref('');
const projectId = route.params.projectId;
console.log('当前项目ID:', projectId); // 可用于调试

// 预上传校验：检查文件是否为Word格式
const beforeWordUpload = (file: File) => {
  if (!file.name.endsWith('.docx')) {
    ElMessage.error('请选择有效的Word文件 (.docx)');
    return false;
  }
  return true;
};

// 预上传校验：检查文件是否为Excel格式
const beforeExcelUpload = (file: File) => {
  if (!(file.name.endsWith('.xls') || file.name.endsWith('.xlsx'))) {
    ElMessage.error('请选择有效的Excel文件 (.xls 或 .xlsx)');
    return false;
  }
  return true;
};

// 自定义上传处理
const handleUploadRequest = async (options: any) => {
  const { file, onSuccess, onError } = options;

  const formData = new FormData();
  formData.append('file', file);
  formData.append('type', file.name.endsWith('.docx') ? 'word' : 'excel');
  formData.append('projectId', projectId.toString()); // 将 projectId 传递到后端

  try {
    const response = await axios.post('http://localhost:8080/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    console.log("aaa");
    if (response.status === 200) {
      uploadedFileName.value = file.name; // 存储文件名
      console.log("成功上传文件"+uploadedFileName.value);
      ElMessage.success('文件上传成功');
      onSuccess(response.data);
    } else {
      ElMessage.error('文件上传失败');
      onError(new Error('上传失败'));
    }
  } catch (error) {
    console.error('上传失败:', error);
    ElMessage.error('文件上传过程中发生错误');
    onError(error);
    
  }
};

// 下载Excel模板
const downloadExcelTemplate = () => {
  const link = document.createElement('a');
  link.href = 'src/views/upload/评估模板文件.xlsx'; // 请确保路径正确
  link.download = '评估模板文件.xlsx';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

const nextStep = () => {
  router.push({
    name: 'systempoint',  // 使用路由的 name
    params: { projectId: projectId },  // 通过 params 传递 projectId
    query: { fileName: uploadedFileName.value },  // 如果需要传递 fileName，可以继续使用 query
  });
};


</script>
<style scoped>
.upload-page {
  margin: 20px;
  font-family: Arial, sans-serif;
}

.upload-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  
}

h3 {
  font-size: 1.2em;
  margin-bottom: 10px;
}

.instructions {
  list-style-type: decimal;
  padding-left: 20px;
  color: #555;
}

.instructions li {
  margin: 5px 0;
}
.upload-btn, .download-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    margin: 0 10px; /* 保持一致的外边距 */
    padding: 10px 20px; /* 保持一致的内边距 */
    min-width: 120px; /* 可以设置一个最小宽度 */
    font-size: 16px; /* 保持一致的字体大小 */
    color: #fff;
    
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
    text-align: center;
}
.instructions {
    list-style-type: none; /* 移除列表项前的标记 */
    padding-left: 0; /* 移除默认的左内边距 */
    color: #555;
}

.instructions li {
    margin: 5px 0;
}
.next-btn {
    margin-top: 20px;
    display: block;
    right: 20px;
    padding: 10px 20px;
    font-size: 16px;
    color: #fff;
    background-color: #409eff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
}
</style>

