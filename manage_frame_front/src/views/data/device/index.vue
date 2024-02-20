<template>
    <div class="app-container">
        <el-row :gutter="20" class="header">
            <el-col :span="7">
                <el-input placeholder="请输入设备名..." v-model="queryForm.query" clearable></el-input>
            </el-col>
            <el-button type="primary" :icon="Search" @click="initDeviceList">搜索</el-button>
            <el-button type="primary" :icon="Star" @click="reset">重置</el-button>
            <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
            <el-popconfirm title="您确定批量删除这些记录吗？" @confirm="handleDelete(null)">
                <template #reference>
                    <el-button type="danger" :disabled="delBtnStatus" :icon="Delete">批量删除</el-button>
                </template>
            </el-popconfirm>
        </el-row>
        <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"/>
            <el-table-column prop="productId" label="产品ID" width="100" align="center"/>
            <el-table-column prop="deviceName" label="设备名称" width="200" align="center"/>
            <el-table-column prop="userList" label="拥有此设备用户" align="center">
                <template v-slot="scope">
                    <el-tag size="small" type="warning" v-for="item in scope.row.sysUserList"> {{ item.username }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="200" align="center"/>
            <el-table-column prop="action" label="操作" width="400" fixed="right" align="center">
                <template v-slot="scope">
                    <el-button type="primary" :icon="Tools"
                               @click="handleUserDialogValue(scope.row.id,scope.row.sysUserList)">分配用户
                    </el-button>
                    <el-button type="primary" :icon="Edit"
                               @click="handleDialogValue(scope.row.id)"/>
                    <el-popconfirm title="您确定要删除这条记录吗？"
                                   @confirm="handleDelete(scope.row.id)">
                        <template #reference>
                            <el-button type="danger" :icon="Delete"/>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            v-model:currentPage="queryForm.pageNum"
            v-model:page-size="queryForm.pageSize"
            :page-sizes="[5,10, 20, 30, 40]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
    </div>
    <Dialog v-model="dialogVisible" :dialogVisible="dialogVisible" :id="id" :dialogTitle="dialogTitle"
            @initDeviceList="initDeviceList">
    </Dialog>
    <RoleDialog v-model="userDialogVisible" :sysUserList="sysUserList" :userDialogVisible="userDialogVisible" :id="id"
                @initDeviceList="initDeviceList">
    </RoleDialog>
</template>

<script setup>
import {ref} from 'vue';
import requestUtil, {getServerUrl} from "@/util/request";
import {Search, Delete, DocumentAdd, Edit, Tools, RefreshRight, Star} from '@element-plus/icons-vue'
import Dialog from './components/dialog'
import {ElMessage} from 'element-plus'
import RoleDialog from './components/userDialog'

const tableData = ref({})

const total = ref(0)

const queryForm = ref({
    query: '',
    pageNum: 1,
    pageSize: 10
})

const dialogVisible = ref(false)

const dialogTitle = ref("")

const id = ref(-1)

const delBtnStatus = ref(true)

const multipleSelection = ref([])

const sysUserList = ref([])

const userDialogVisible = ref(false)

//复选框选中事件
const handleSelectionChange = (selection) => {
    console.log(selection)
    multipleSelection.value = selection;
    delBtnStatus.value = selection.length == 0;
}

const handleUserDialogValue = (userId, userList) => {
    console.log("userId=" + userId)
    id.value = userId;
    sysUserList.value = userList;
    userDialogVisible.value = true
}

const initDeviceList = async () => {
    const res = await requestUtil.post("data/device/list", queryForm.value);
    tableData.value = res.data.data.sysIotDeviceList;
    total.value = res.data.data.total;
}
const reset = async () => {
    queryForm.value.query=''
    queryForm.value.pageNum=1
    queryForm.value.pageSize=10
    initDeviceList();
}

initDeviceList();

const handleSizeChange = (pageSize) => {
    queryForm.value.pageNum = 1;
    queryForm.value.pageSize = pageSize;
    initDeviceList();
}

const handleCurrentChange = (pageNum) => {
    queryForm.value.pageNum = pageNum;
    initDeviceList();
}

const handleDialogValue = (deviceId) => {
    if (deviceId) {
        id.value = deviceId;
        dialogTitle.value = "设备修改"
    } else {
        id.value = -1;
        dialogTitle.value = "设备添加"
    }
    dialogVisible.value = true
}

const handleDelete = async (id) => {
    var ids = []
    if (id) {
        ids.push(id)
    } else {
        multipleSelection.value.forEach(row => {
            ids.push(row.id)
        })
    }
    const res = await requestUtil.post("data/device/delete", ids)
    if (res.data.code == 200) {
        ElMessage({
            type: 'success',
            message: '执行成功!'
        })
        initDeviceList();
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
