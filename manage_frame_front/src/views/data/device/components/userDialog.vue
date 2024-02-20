<template>
    <el-dialog model-value="userDialogVisible" title="分配用户" width="40%" @close="handleClose">
        <el-form ref="formRef" :model="form" label-width="100px">
            <el-checkbox-group v-model="form.checkedUsers">
                <el-checkbox v-for="user in form.userList" :id="user.id" :key="user.id" :label="user.id"
                             name="checkedRoles">{{ user.username }}
                </el-checkbox>
            </el-checkbox-group>
        </el-form>
        <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="handleConfirm">确认</el-button>
        <el-button @click="handleClose">取消</el-button>
      </span>
        </template>
    </el-dialog>
</template>

<script setup>

import {defineEmits, defineProps, ref, watch} from "vue";
import requestUtil, {getServerUrl} from "@/util/request";
import {ElMessage} from 'element-plus'

const props = defineProps(
    {
        id: {
            type: Number,
            default: -1,
            required: true
        },
        userDialogVisible: {
            type: Boolean,
            default: false,
            required: true
        },
        sysUserList: {
            type: Array,
            default: [],
            required: true
        }
    }
)


const form = ref({
    id: -1,
    userList: [],
    checkedUsers: []
})


const formRef = ref(null)

const initFormData = async (id) => {
    const res = await requestUtil.get("data/device/userListAll");
    form.value.userList = res.data.data;
    form.value.id = id;
}


watch(
    () => props.userDialogVisible,
    () => {
        let id = props.id;
        console.log("id=" + id)
        if (id != -1) {
            form.value.checkedUsers = []
            props.sysUserList.forEach(item => {
                form.value.checkedUsers.push(item.id);
            })
            initFormData(id)
        }
    }
)


const emits = defineEmits(['update:modelValue', 'initDeviceList'])

const handleClose = () => {
    emits('update:modelValue', false)
}

const handleConfirm = () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            let result = await requestUtil.post("data/device/grantUser/" + form.value.id, form.value.checkedUsers);
            let data = result.data;
            if (data.code == 200) {
                ElMessage.success("执行成功！")
                emits("initDeviceList")
                handleClose();
            } else {
                ElMessage.error(data.msg);
            }
        } else {
            console.log("fail")
        }
    })
}

</script>

<style scoped>

</style>
