<template>
    <div ref="quillContainer" class="quill-editor-container">
      <div ref="editor" class="quill-editor"></div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, watch } from 'vue';
  import Quill from 'quill';
  import 'quill/dist/quill.snow.css';
  
  const props = defineProps({
    modelValue: {
      type: String,
      default: '',
    },
  });
  
  const emit = defineEmits(['update:modelValue']);
  
  const editor = ref();
  const quillInstance = ref();
  
  onMounted(() => {
    quillInstance.value = new Quill(editor.value, {
      theme: 'snow',
    });
  
    quillInstance.value.on('text-change', () => {
      const html = editor.value.children[0].innerHTML;
      emit('update:modelValue', html);
    });
  
    // 设置初始内容
    if (props.modelValue) {
      quillInstance.value.root.innerHTML = props.modelValue;
    }
  });
  
  watch(() => props.modelValue, (newValue) => {
    if (quillInstance.value && quillInstance.value.root.innerHTML !== newValue) {
      quillInstance.value.root.innerHTML = newValue;
    }
  });
  </script>
  
  <style scoped>
  .quill-editor-container {
    border: 1px solid #ccc;
    padding: 10px;
    width: 100%;
  }
  
  .quill-editor {
    height: 200px;
    overflow-y: hidden;
    width: 100%;
  }
  </style>
  

   