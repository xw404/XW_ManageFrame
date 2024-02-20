<template>
    <el-icon><HomeFilled/></el-icon>
    <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="(item,index) in breadcrumbList">
            <span class="root" v-if="parentName && index>0">{{ parentName }}&nbsp;&nbsp;/&nbsp;&nbsp;</span>
            <span v-if="index==breadcrumbList.length-1">{{ item.name }}</span>
            <span class="root" v-else>{{ item.name }}</span>
        </el-breadcrumb-item>
    </el-breadcrumb>
</template>

<script setup>
import {HomeFilled} from '@element-plus/icons-vue'
import {useRoute} from 'vue-router'
import {ref, watch} from 'vue'

const route = useRoute();
const breadcrumbList = ref([])
//父节点
const parentName = ref("")

const initBreadcrumbList = () => {
    breadcrumbList.value = route.matched;
    parentName.value = route.meta.parentName;
}
//监听方法 路由变化就调用
watch(route, () => {
    initBreadcrumbList();
}, {deep: true, immediate: true})


</script>

<style lang="scss" scoped>

.root {
    color: #666;
    font-weight: 600;
}
</style>
