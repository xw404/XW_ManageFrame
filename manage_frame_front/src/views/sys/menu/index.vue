<template>
    <div class="app-container">
        <el-row class="header">
            <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
        </el-row>
        <el-table :data="tableData" row-key="id" stripe style="width: 100%; margin-bottom: 20px" border
                  default-expand-all
                  :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        >
            <el-table-column prop="name" label="菜单名称" width="200"/>
            <el-table-column prop="icon" label="图标" width="70" align="center">
                <template v-slot="scope">
                    <el-icon>
                        <svg-icon :icon="scope.row.icon"/>
                    </el-icon>
                </template>
            </el-table-column>
            <el-table-column prop="orderNum" label="排序" width="70" align="center"/>
            <el-table-column prop="perms" label="权限标识" width="200" align="center"/>
            <el-table-column prop="path" label="路由地址" width="150" align="center"/>
            <el-table-column prop="component" label="组件路径" width="150" align="center"/>
            <el-table-column prop="menuType" label="菜单类型" width="120" align="center">
                <template v-slot="scope">
                    <el-tag size="small" v-if="scope.row.menuType === 'M'" type="danger" effect="dark">目录</el-tag>
                    <el-tag size="small" v-else-if="scope.row.menuType === 'C'" type="success" effect="dark">菜单</el-tag>
                    <el-tag size="small" v-else-if="scope.row.menuType === 'F'" type="warning" effect="dark">按钮</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" align="center"/>
            <el-table-column prop="updateTime" label="修改时间" width="180" align="center"/>
            <el-table-column prop="remark" label="备注" width="160" align="center"/>
            <el-table-column prop="action" label="操作" width="200" fixed="right" align="center">
                <template v-slot="scope">
                    <el-button type="primary" :icon="Edit" @click="handleDialogValue(scope.row.id)"/>
                    <el-popconfirm title="您确定要删除这条记录吗？" @confirm="handleDelete(scope.row.id)">
                        <template #reference>
                            <el-button type="danger" :icon="Delete"/>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <Dialog v-model="dialogVisible" :tableData="tableData" :dialogVisible="dialogVisible" :id="id"
            :dialogTitle="dialogTitle" @initMenuList="initMenuList"/>
</template>

<script setup>
import {ref} from 'vue';
import requestUtil, {getServerUrl} from "@/util/request";
import {Search, Delete, DocumentAdd, Edit, Tools, RefreshRight} from '@element-plus/icons-vue'
import Dialog from './components/dialog'
import {ElMessage, ElMessageBox} from 'element-plus'

const tableData = ref({})
const dialogVisible = ref(false)
const dialogTitle = ref("")
const id = ref(-1)

const initMenuList = async () => {
    const res = await requestUtil.get("sys/menu/treeList");
    tableData.value = res.data.data;
}
initMenuList();
const handleDialogValue = (menuId) => {
    if (menuId) {
        id.value = menuId;
        dialogTitle.value = "菜单修改"
    } else {
        id.value = -1;
        dialogTitle.value = "菜单添加"
    }
    dialogVisible.value = true
}
const handleDelete = async (id) => {
    const res = await requestUtil.get("sys/menu/delete/" + id)
    if (res.data.code == 200) {
        ElMessage({
            type: 'success',
            message: '执行成功!'
        })
        initMenuList();
    } else {
        ElMessage({
            type: 'error',
            message: res.data.msg,
        })
    }
}

</script>

<style lang="scss" scoped>
.header {
    padding-bottom: 16px;
    box-sizing: border-box;
}

.el-pagination {
    float: right;
    padding: 20px;
    box-sizing: border-box;
}

::v-deep th.el-table__cell {
    word-break: break-word;
    background-color: #f8f8f9 !important;
    color: #515a6e;
    height: 40px;
    font-size: 13px;

}
.el-tag--small {
    margin-left: 5px;
}
</style>
