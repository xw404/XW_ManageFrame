<template>
    <div class="manager-header">
        <img class="img" src="@/assets/logo.png"/>
        <div class="title">IOT-RBAC系统</div>
    </div>
    <el-menu
        active-text-color="#ffd04b"
        background-color="#2d3a4b"
        class="el-menu-vertical-demo"
        text-color="#fff"
        router
        :default-active="activeIndex"
    >
        <el-menu-item index="/index">
            <el-icon><home-filled/></el-icon>
            <span>首页</span>
        </el-menu-item>
        <el-sub-menu :index="menu.path" v-for="menu in menuList">
            <!--<template #title> 用来声明模板，不能去除-->
            <template #title>
                <el-icon>
                    <svg-icon :icon="menu.icon"></svg-icon>
                </el-icon>
                <span>{{menu.name}}</span>
            </template>
            <el-menu-item v-for="item in menu.children" :index="item.path"  @click="openTab(item)">
                <el-icon>
                    <svg-icon :icon="item.icon"></svg-icon>
                </el-icon>
                <span>{{item.name}}</span>
            </el-menu-item>
        </el-sub-menu>
    </el-menu>
</template>

<script setup>
import {HomeFilled,User,Tickets,Goods,DocumentAdd,Management,Setting,Edit,SwitchButton, Promotion} from '@element-plus/icons-vue'
import {ref,watch} from 'vue'
import store from '@/store'

const menuList = ref(store.getters.GET_MENULIST);

const openTab=(item)=>{
    store.commit("SET_ADD_TABS",item)
}
const activeIndex=ref("/index")
//监听变化
watch(store.state,()=>{
    console.log("editableTabsValue="+store.state.editableTabsValue)
    activeIndex.value=store.state.editableTabsValue
},{deep:true,immediate:true})


</script>

<style lang="scss" scoped>
.manager-header {
    padding-left: 10px;
    display: flex;
    .img{
        width: 60px;
        height: 60px;
    }
    .title {
        margin-top: 25px; /* 添加这一行来将元素距离顶上25px */
        margin-left: 15px;
        font-size: 15px;
        font-weight: bold;
        color: white;
    }
}
</style>
