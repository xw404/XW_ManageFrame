<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="12">
                <el-card>
                    <div style="width: 100%;height: 400px" id="line"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card>

                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import * as echarts from 'echarts';
import {ref,onMounted} from "vue";
const option = ref({
    xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            data: [150, 230, 224, 218, 135, 147, 260],
            type: 'line'
        }
    ]
})
onMounted(async () => {
    initDeviceList();
});
const initDeviceList = async () => {
    var chartDom = document.getElementById('line');
    var lineChart = echarts.init(chartDom);
    lineChart.setOption(option);
}
</script>

<style lang="scss" scoped>

</style>