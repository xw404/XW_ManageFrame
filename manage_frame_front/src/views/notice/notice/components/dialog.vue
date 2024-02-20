<template>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%" @close="handleClose">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="标题" prop="title">
                <el-input v-model="form.title"/>
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <el-input v-model="form.content" type="textarea" :rows="4"/>
            </el-form-item>
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
import {defineEmits, defineProps, ref, watch} from "vue"
import requestUtil, {getServerUrl} from "@/util/request";
import {ElMessage} from 'element-plus'
import store from "@/store"
const props = defineProps(
    {
        id: {
            type: Number,
            default: -1,
            required: true
        },
        dialogTitle: {
            type: String,
            default: '',
            required: true
        },
        dialogVisible: {
            type: Boolean,
            default: false,
            required: true
        }
    }
)
const form = ref({
    id: -1,
    title: "",
    content: "",
    createUser: "",
})

const currentUser = ref(store.getters.GET_USERINFO)

const rules = ref({
    title: [
        {required: true, message: '请输入公告标题'}
    ],
    content: [
        {required: true, message: '请输入公告具体内容'}
    ],
})

const formRef = ref(null)

const initFormData = async (id) => {
    const res = await requestUtil.get("notice/notice/" + id);
    form.value = res.data.data;
}
watch(
    () => props.dialogVisible,
    () => {
        let id = props.id;
        console.log("id=" + id)
        if (id != -1) {
            initFormData(id);
        }
        else {
            form.value = {
                id: -1,
                title: "",
                content: "",
                createUser: "",
            }
        }
    }
)

const emits = defineEmits(['update:modelValue', 'initNoticeList'])

const handleClose = () => {
    emits('update:modelValue', false)
}

const handleConfirm = () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            //修改公告修改人为当前用户
            form.value.createUser = currentUser.value.username;
            let result = await requestUtil.post("notice/notice/save", form.value);
            let data = result.data;
            if (data.code == 200) {
                ElMessage.success("执行成功！")
                formRef.value.resetFields();
                emits("initNoticeList")
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
<style lang="scss" scoped>

</style>
