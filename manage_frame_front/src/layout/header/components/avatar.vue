<!--头像-->
<template>
    <el-dropdown>
    <span class="el-dropdown-link">
      <el-avatar shape="square" :size="40" :src="squareUrl"/>
      &nbsp;&nbsp;{{ currentUser.username }}
      <el-icon class="el-icon--right">
        <arrow-down/>
      </el-icon>
    </span>
        <template #dropdown>
            <el-dropdown-menu>
                <el-dropdown-item>
                    <router-link :to="{name:'个人中心'}">个人中心</router-link>
                </el-dropdown-item>
                <el-dropdown-item @click="logout">用户退出</el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
</template>

<script setup>
import {ArrowDown} from '@element-plus/icons-vue'
import {ref} from 'vue'
import store from '@/store'
import requestUtil, {getServerUrl} from '@/util/request'
//用户信息
const currentUser = ref(store.getters.GET_USERINFO);
//头像地址
const squareUrl = ref(getServerUrl() + 'image/userAvatar/' + currentUser.value.avatar)

const logout = async () => {
    let result = await requestUtil.get("/logout")
    if (result.data.code == 200) {
        //调用store中的方法
        store.dispatch('logout')
        //重置路由
        store.commit('RESET_TABS')
        store.commit('SET_ROUTER_STATE',false)
    }
}
</script>

<style lang="scss" scoped>
.el-dropdown-link {
    cursor: pointer;
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
}
</style>
