<template>
  <div style="border: 1px solid #ccc">
    <Toolbar
      style="border-bottom: 1px solid #ccc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
    />
    <Editor
      style="height: 500px; overflow-y: hidden"
      v-model="valueHtml"
      :defaultConfig="editorConfig"
      @onCreated="handleCreated"
    />
  </div>
</template>

<script setup lang="ts">
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { watch } from "vue";
import { onBeforeUnmount, ref, shallowRef, watchEffect } from "vue";
import wangEditor from "@wangeditor/editor";

const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
});

// 声明事件消息
const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
}>();

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

// 内容 HTML
const valueHtml = ref("<p>hello</p>");
watchEffect(() => {
  valueHtml.value = props.modelValue; // 初始内容
});

// 去除 HTML 标签的函数
const stripHtmlTags = (html: string): string => {
  return html.replace(/<\/?[^>]+(>|$)/g, "");
};

watch(valueHtml, (newHtml: any) => {
  const cleanHtml = stripHtmlTags(newHtml);
  emit("update:modelValue", cleanHtml); // 内容变化时，触发事件
});

// 模拟 ajax 异步获取内容
// onMounted(() => {
//     setTimeout(() => {
//         valueHtml.value = '<p>模拟 Ajax 异步设置内容</p>'
//     }, 1500)
// })

const toolbarConfig = {};
const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {
    uploadImage: {
      server: "http://localhost:8010/api/upload", // 请确保这个地址是有效的
      fileFieldName: "formData",

      customInsert(res: any, insertFn: any) {
        insertFn(res.url);
      },
    },
  },
};

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

const handleCreated = (editor: any) => {
  editorRef.value = editor; // 记录 editor 实例，重要！
};
</script>
