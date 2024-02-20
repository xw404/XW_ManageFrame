<template>
  <div class="app-wrapper">
    <el-container>
        <!--        左侧菜单-->
      <el-aside width="200px" class="sidebar-container"><Menu/></el-aside>
        <!--        右侧内容-->
      <el-container>
        <el-header><Header/></el-header>
        <el-main>
            <Tabs/>
            <!--   添加 <router-view/>  为了根据路由地址，显示路由内容。定位到组件   -->
            <router-view/>
        </el-main>
        <el-footer><Footer/></el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import Menu from '@/layout/menu'
import Header from '@/layout/header'
import Footer from '@/layout/footer'
import Tabs from '@/layout/tabs'
</script>

<style scoped>
.app-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.sidebar-container {
  background-color: #2d3a4b;
  height: 100%;
}
.el-container{
  height:100%
}
.el-header{
  padding-left: 0px;
  padding-right: 0px;
}

:deep(ul.el-menu){
  border-right-width: 0px
}
</style>
