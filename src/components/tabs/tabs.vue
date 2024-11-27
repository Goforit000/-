<template>
    <el-tabs v-model="activeTab" type="card" class="demo-tabs" closable @tab-remove="removeTab" @tab-click="clickBtn">
        <el-tab-pane v-for="item in tabList" :key="item.path" :label="item.title" :name="item.path">
   
        </el-tab-pane>
    </el-tabs>
</template>

<script lang="ts" setup>
import { computed, ref,onMounted, watch } from 'vue'
import { tabStore ,Tab} from '../../store/tabs';
import { useRoute,useRouter } from 'vue-router';
import { TabsPaneContext } from 'element-plus';



const route = useRoute()
const router = useRouter()


const activeTab = ref('2')
const store=tabStore()

const tabList= computed(()=>{
    return store.getTab
})

const clickBtn=(pane:TabsPaneContext)=>{

const{props}=pane
router.push({path:props.name as string})
}



const addTab =()=>{
    const { path ,meta}=route;
console.log("path===>"+path)
console.log("meta==>"+meta)
const tab:Tab={
    path: path,
    title: meta.title as string
}
store.addTab(tab)
}



const removeTab = (targetName: string) => {
    const tabs = tabList.value
    let activeName = activeTab.value
    if (activeName === targetName) {
        tabs.forEach((tab:Tab, index:number) => {
            if (tab.path === targetName) {
                const nextTab = tabs[index + 1] || tabs[index - 1]
                if (nextTab) {
                    activeName = nextTab.path
                }
            }
        })
    }

    activeTab.value = activeName
   store.tabList = tabs.filter((tab) => tab.path !== targetName)
   router.push({path:activeName})
}
watch(
    ()=>route.path,
    ()=>{
        setActiveTab(),
        addTab()
    }
)

const setActiveTab=()=>{
    activeTab.value=route.path
}
onMounted(()=>{
    setActiveTab(),
    addTab()
})
</script>

<style>
.demo-tabs>.el-tabs__content {
    padding: -5px;
    color: #6b778c;
    font-size: 32px;
    font-weight: 600;
}
</style>