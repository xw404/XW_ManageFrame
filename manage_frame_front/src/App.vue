<template>
    <router-view/>
</template>
<script setup>
//解决根据网址跳转不会添加tab和选中tab的问题
//（个人中心可以添加tab也是这个原因）
import { watch} from 'vue'
import { useRoute } from 'vue-router'
import store from '@/store'
const route=useRoute();
const whitePath=['/login','/index','/']
watch(route,(to,from)=>{
    console.log("to"+to.name)
    console.log(to.path)
    if (whitePath.indexOf(to.path)===-1) {
        console.log("to.path="+to.path)
        let obj = {
            name: to.name,
            path: to.path
        }
        store.commit("SET_ADD_TABS", obj)
    }
},{deep:true,immediate:true})

</script>
<style>
html,body,#app{
  height: 100%;
}
.app-container{
  padding:20px
}
</style>
