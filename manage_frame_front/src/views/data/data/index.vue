<template>
    <div>
        <el-row :gutter="20">
            <el-select v-model="selectDeviceId" class="m-2" placeholder="请选择设备---" style="width: 240px;margin-left: 20px">
                <el-option v-for="item in deviceItems" :key="item.id" :label="item.deviceName" :value="item.id"/>
            </el-select>
            <el-button onclick="queryDeviceData()" style="width: 100px;margin-left: 20px" type="primary" :icon="Search">查询数据</el-button>
        </el-row>
        <el-row :gutter="10">
            <el-col :span="12">
                <el-card>
                    <div style="width: 100%;height: 400px" id="temperature"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card>
                    <div style="width: 100%;height: 400px" id="humidity"></div>
                </el-card>
            </el-col>
        </el-row>
        <el-row :gutter="10">
            <el-col :span="12">
                <el-card>
                    <div style="width: 100%;height: 400px" id="light"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card>
                    <div style="width: 100%;height: 400px" id="gas"></div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import * as echarts from 'echarts';
import {ref,onMounted} from "vue";
import requestUtil from "@/util/request";

const selectDeviceId = ref('');
const deviceItems = ref([
]);

const option_temperature = ref({
    title: {
        subtext: 'Dynamic temperature data',
        text: '动态温度数据',
        left: 'center'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']//时间数据
    },
    yAxis: {
        type: 'value',
        axisLabel: {
            formatter: '{value} %' // 显示百分比
        }
    },
    series: [
        {
            name: "温度",
            data: [150, 230, 224, 218, 135, 147, 260],//温度数据
            type: 'line',
            smooth: true
        }
    ]
})

const option_humidity = ref({
    title: {
        subtext: 'Dynamic humidity data',
        text: '动态湿度数据',
        left: 'center'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            axisTick: {
                alignWithLabel: true
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            axisLabel: {
                formatter: '{value} %' // 显示百分比
            }
        }
    ],
    series: [
        {
            name: '湿度',
            type: 'bar',
            barWidth: '60%',
            data: [10, 52, 200, 334, 390, 330, 220]
        }
    ]
})

const option_light = ref({
    title: {
        subtext: 'Dynamic light intensity data',
        text: '光照强度数据',
        left: 'center'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            axisTick: {
                alignWithLabel: true
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            axisLabel: {
                formatter: '{value} %' // 显示百分比
            }
        }
    ],
    series: [
        {
            name: '光照强度',
            type: 'bar',
            barWidth: '60%',
            data: [10, 90, 20, 34, 39, 70, 25],
            showBackground: true,
            backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
            }
        }
    ]
})

const option_gas = ref({
    title: {
        subtext: 'Combustible gas concentration data',
        text: '动态可燃气体浓度数据',
        left: 'center'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']//时间数据
    },
    yAxis: {
        type: 'value',
        axisLabel: {
            formatter: '{value} %' // 显示百分比
        }
    },
    series: [
        {
            name: "可燃气体浓度",
            data: [150, 230, 224, 218, 135, 147, 260],
            type: 'line',
        }
    ]
})

onMounted(() => {
    initDeviceList(); //加载所有设备

    initTemperatureDevice();
    initHumidityDevice();
    initLightDevice();
    initGasDevice();
});
const initTemperatureDevice =() => {
    let chartDom = document.getElementById('temperature');
    let chart = echarts.init(chartDom);
    chart.setOption(option_temperature.value);
}

const initHumidityDevice =() => {
    let chartDom = document.getElementById('humidity');
    let chart = echarts.init(chartDom);
    chart.setOption(option_humidity.value);
}

const initLightDevice =() => {
    let chartDom = document.getElementById('light');
    let chart = echarts.init(chartDom);
    chart.setOption(option_light.value);
}

const initGasDevice =() => {
    let chartDom = document.getElementById('gas');
    let chart = echarts.init(chartDom);
    chart.setOption(option_gas.value);
}

const queryDeviceData =()=>{
    let id = selectDeviceId.value;
    initTemperatureDevice(id);
    initHumidityDevice(id);
    initLightDevice(id);
    initGasDevice(id);
    console.log("数据查询成功");
}

const initDeviceList = async () => {
    const res = await requestUtil.get("data/device/selectAll");
    deviceItems.value = res.data.data;
    console.log(res.data.data);
}

</script>

<style lang="scss" scoped>

</style>