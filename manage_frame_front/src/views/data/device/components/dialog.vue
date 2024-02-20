<template>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%" @close="handleClose">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="产品id" prop="productId">
                <el-input v-model="form.productId"/>
            </el-form-item>
            <el-form-item label="设备名称" prop="deviceName">
                <el-input v-model="form.deviceName"/>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="form.remark" type="textarea" :rows="4"/>
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
    productId: "",
    deviceName: "",
    remark: ""
})
const rules = ref({
    productId: [
        {required: true, message: '请输入产品Id'}
    ],
    deviceName: [
        {required: true, message: '请输入设备名称'}
    ],

})

const formRef = ref(null)
const initFormData = async (id) => {
    const res = await requestUtil.get("data/device/" + id);
    form.value = res.data.data;
}
watch(
    () => props.dialogVisible,
    () => {
        let id = props.id;
        console.log("id=" + id)
        if (id != -1) {
            initFormData(id);
        } else {
            form.value = {
                id: -1,
                productId: "",
                deviceName: "",
                remark: ""
            }
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
            let result = await requestUtil.post("data/device/save", form.value);
            let data = result.data;
            if (data.code == 200) {
                ElMessage.success("执行成功！")
                formRef.value.resetFields();//重置表单字段，清除验证状态
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
<style lang="scss" scoped>

</style>
