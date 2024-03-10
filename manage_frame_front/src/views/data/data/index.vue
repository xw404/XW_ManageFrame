<template>
    <div class="app-container">
        <el-row :gutter="20" class="header">
            <el-select v-model="selectDeviceName" class="m-2" placeholder="请选择设备---"
                       style="width: 240px;margin-left: 20px">
                <el-option v-for="item in deviceItems" :key="item.id" :label="item.deviceName"
                           :value="item.deviceName"/>
            </el-select>
            <el-button style="width: 100px;margin-left: 20px" type="primary" :icon="Search" @click="queryDeviceData">
                查询数据
            </el-button>

            <el-button style="width: 200px;margin-left: 20px" type="danger" :icon="Setting"
                       @click="drawer = true">
                为所有设备设置预警参数
            </el-button>

            <el-switch v-if="selectDeviceName != ''" style="margin-left: 60px"
                       v-model="flashSwitch"
                       size="large"
                       active-text="打开动态刷新"
                       inactive-text="关闭动态刷新"
                       @change="handleSwitchChange"
            />
        </el-row>
        <div v-if="sysIotDeviceDataList.length > 0">
            <el-row style="margin-left: 80px">
                <el-col :span="5" style="margin-left: 20px">
                    <el-card style="display: flex">
                        <el-statistic title="实时照明状态" :value="led"/>
                    </el-card>
                </el-col>
                <el-col :span="5" style="margin-left: 20px">
                    <el-card>
                        <el-statistic title="实时风扇状态" :value="fan"/>
                    </el-card>
                </el-col>
                <el-col :span="5" style="margin-left: 20px">
                    <el-card>
                        <el-statistic title="实时火源状态" :value="has_fire"/>
                    </el-card>
                </el-col>
                <el-col :span="5" style="margin-left: 20px">
                    <el-card>
                        <el-statistic title="是否有人走动" :value="has_people"/>
                    </el-card>
                </el-col>
            </el-row>

            <el-row :gutter="10" style="margin-top: 10px">
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
        <div v-else>
            <el-empty description="暂无数据！"/>
        </div>
    </div>

    <el-drawer v-model="drawer" direction="rtl">
        <template #header>
            <h4>为设备设置预警阈值</h4>
        </template>
        <el-form ref="formRef" :model="lineForm" :rules="rules"  label-width="150px">
            <el-form-item label="最高预警温度" prop="tempH">
                <el-input v-model="lineForm.tempH" />
            </el-form-item>
            <el-form-item label="最低预警温度" prop="tempL">
                <el-input v-model="lineForm.tempL" />
            </el-form-item>
            <el-form-item label="最高预警湿度" prop="humiH">
                <el-input v-model="lineForm.humiH" />
            </el-form-item>
            <el-form-item label="最低预警湿度" prop="humiL">
                <el-input v-model="lineForm.humiL"/>
            </el-form-item>
            <el-form-item label="最高预警光照强度" prop="lightH">
                <el-input v-model="lineForm.lightH"/>
            </el-form-item>
            <el-form-item label="最低预警光照强度" prop="lightL">
                <el-input v-model="lineForm.lightL"/>
            </el-form-item>
            <el-form-item label="最高预警烟雾浓度" prop="mq2H">
                <el-input v-model="lineForm.mq2H"/>
            </el-form-item>
            <el-form-item label="最低预警烟雾浓度" prop="mq2L">
                <el-input v-model="lineForm.mq2L"/>
            </el-form-item>
            <el-radio-group v-model="lineForm.fire" style="margin-left: 20px">
                <el-radio :label="0">无火预警</el-radio>
                <el-radio :label="1">火源预警</el-radio>
                <el-radio :label="2">无需预警</el-radio>
            </el-radio-group>

            <el-radio-group v-model="lineForm.people" style="margin-left: 20px">
                <el-radio :label="0">无人预警</el-radio>
                <el-radio :label="1">有人预警</el-radio>
                <el-radio :label="2">无需预警</el-radio>
            </el-radio-group>

            <el-radio-group v-model="lineForm.led" style="margin-left: 20px">
                <el-radio :label="0">关灯预警</el-radio>
                <el-radio :label="1">开灯预警</el-radio>
                <el-radio :label="2">无需预警</el-radio>
            </el-radio-group>

            <el-radio-group v-model="lineForm.fan" style="margin-left: 20px">
                <el-radio :label="0">关闭预警</el-radio>
                <el-radio :label="1">风扇预警</el-radio>
                <el-radio :label="2">无需预警</el-radio>
            </el-radio-group>

        </el-form>
        <template #footer>
            <div style="flex: auto">
                <el-button @click="cancelClick">取消</el-button>
                <el-button type="primary" @click="confirmClick">提交</el-button>
            </div>
        </template>
    </el-drawer>

</template>

<script setup>
import * as echarts from 'echarts';
import {ref, onMounted, onUnmounted} from "vue";
import {Search, Setting} from '@element-plus/icons-vue';
import {ElMessage} from 'element-plus';
import requestUtil from "@/util/request";

let intervalId = null; // 用于存储setInterval的ID
const formRef = ref(null)

const drawer = ref(false)
const lineForm = ref({
    people: 2,
    led: 2,
    fire: 2,
    fan: 2
})
const rules = {
    tempH: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }],
    tempL: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }],
    humiH: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }],
    humiL: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }],
    lightH: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }],
    lightL: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }],
    mq2H: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }],
    mq2L: [{ required: true, trigger: ["blur", "change"], message: "请填写信息" }]
};

const fan = ref("");
const led = ref("");
const has_fire = ref("");
const has_people = ref("");

const flashSwitch = ref(false)
const selectDeviceName = ref('');
const deviceItems = ref([]);
const sysIotDeviceDataList = ref([]);

const temperatureDataList = ref([]);
const humidityDataList = ref([]);
const lightDataList = ref([]);
const gasDataList = ref([]);
const timeDataList = ref([]);

const option_temperature = ref({
    title: {
        subtext: 'Dynamic temperature data',
        text: '动态温度数据(℃)',
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
        // data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']//时间数据
        data: timeDataList.value//时间数据
    },
    yAxis: {
        type: 'value',
        axisLabel: {
            formatter: '{value} ℃' // 显示百分比
        }
    },
    series: [
        {
            name: "温度",
            // data: [150, 230, 224, 218, 135, 147, 260],//温度数据
            data: temperatureDataList.value,//温度数据
            type: 'line',
            smooth: true
        }
    ]
})

const option_humidity = ref({
    title: {
        subtext: 'Dynamic humidity data',
        text: '动态湿度数据(%)',
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
            // data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            data: timeDataList.value,
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
            // data: [10, 52, 200, 334, 390, 330, 220]
            data: humidityDataList.value
        }
    ]
})

const option_light = ref({
    title: {
        subtext: 'Dynamic light intensity data',
        text: '光照强度数据(%)',
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
            // data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            data: timeDataList.value,
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
            // data: [10, 90, 20, 34, 39, 70, 25],
            data: lightDataList.value,
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
        text: '动态可燃气体浓度数据(%)',
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
        // data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']//时间数据
        data: timeDataList.value//时间数据
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
            // data: [150, 230, 224, 218, 135, 147, 260],
            data: gasDataList.value,
            type: 'line',
        }
    ]
})
//组件挂载时
onMounted(() => {
    initDeviceList(); //加载所有设备
});
onUnmounted(() => {
    endInterval();
})


const initTemperatureDevice = () => {
    let chartDom = document.getElementById('temperature');
    let chart = echarts.init(chartDom);
    chart.setOption(option_temperature.value);
}

const initHumidityDevice = () => {
    let chartDom = document.getElementById('humidity');
    let chart = echarts.init(chartDom);
    chart.setOption(option_humidity.value);
}

const initLightDevice = () => {
    let chartDom = document.getElementById('light');
    let chart = echarts.init(chartDom);
    chart.setOption(option_light.value);
}

const initGasDevice = () => {
    let chartDom = document.getElementById('gas');
    let chart = echarts.init(chartDom);
    chart.setOption(option_gas.value);
}

const queryDeviceData = async () => {
    //先把表单数据置空
    resetData();
    console.log(timeDataList.value)
    let deviceName = selectDeviceName.value;
    if (deviceName == null || deviceName == '') {
        ElMessage.info("请选择设备后再查询数据！");
        return
    }
    const res = await requestUtil.get("data/device/data/" + deviceName);
    sysIotDeviceDataList.value = res.data.data;
    if (sysIotDeviceDataList.value.length > 0) {
        let flag = 1;
        sysIotDeviceDataList.value.forEach((item) => {
            timeDataList.value.push(item.time);
            temperatureDataList.value.push(item.temp);
            humidityDataList.value.push(item.humi);
            lightDataList.value.push(item.light);
            gasDataList.value.push(item.mq2);
            flag++;
            if (flag == sysIotDeviceDataList.value.length) {
                //取最后的数据作为实时设备状态
                fan.value = item.fan == 1 ? "开启" : "关闭";
                led.value = item.led == 1 ? "开启" : "关闭";
                has_fire.value = item.hasFire == 1 ? "危险:附近检测到火源" : "正常";
                has_people.value = item.hasPeople == 1 ? "设备附近有人" : "设备附近无人";
            }
        })
        initTemperatureDevice();
        initHumidityDevice();
        initLightDevice();
        initGasDevice();
    }
}

const resetData = () => {
    /*使用此方法再次填充数组时候会失败：原因未知：可能和引用丢失有关
    temperatureDataList.value = [];
    humidityDataList.value = [];
    lightDataList.value = [];
    gasDataList.value = [];
    timeDataList.value = [];
     */
    temperatureDataList.value.splice(0, temperatureDataList.value.length);
    humidityDataList.value.splice(0, humidityDataList.value.length);
    lightDataList.value.splice(0, lightDataList.value.length);
    gasDataList.value.splice(0, gasDataList.value.length);
    timeDataList.value.splice(0, timeDataList.value.length);
}


const initDeviceList = async () => {
    const res = await requestUtil.get("data/device/selectAll");
    deviceItems.value = res.data.data;
}

// 设置定时器
const startInterval = () => {
    intervalId = setInterval(queryDeviceData, 1000); // 每5秒调用一次queryDeviceData,便于测试
};

// 清除定时任务
const endInterval = () => {
    clearInterval(intervalId);
    intervalId = null;
};

const handleSwitchChange = () => {
    if (selectDeviceName.value == '') {
        flashSwitch.value = false;
        return
    }
    if (selectDeviceName.value != '' && flashSwitch.value) {
        startInterval(); // 当value有值时启动定时任务
    } else {
        endInterval(); // 当value没有值时清除定时任务
    }
}

const cancelClick = () => {
    drawer.value = false
}

const confirmClick = async () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            console.log(lineForm.value)
            let result = await requestUtil.post("data/device/lineSet", lineForm.value);
            let data = result.data;
            if (data.code == 200) {
                ElMessage.success("执行成功，立即生效！")
                cancelClick();
                //重置表单
                formRef.value.resetFields();
            } else {
                ElMessage.error(data.msg);
            }
        }else {
            console.log("fail")
        }
    })
}
</script>

<style lang="scss" scoped>

.header {
    padding-bottom: 16px;
    box-sizing: border-box;
}
</style>
