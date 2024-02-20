<template>
    <div>
        <div style="padding: 20px; margin-left: 40px; font-size: 20px">
            您好，{{ currentUser.username }}！欢迎使用本系统
        </div>
        <div style="display: flex;  margin-left: 60px ">
            <div style="width: 30%;" class="card">
                <div style="margin-bottom: 30px; color: #666666;font-size: 18px; font-weight: bold">系统公告列表</div>
                <div class="card">
                    <el-timeline :reverse="true">
                        <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.updateTime"
                                          placement="top">
                            <el-popover
                                placement="right"
                                title="详情"
                                :width="400"
                                trigger="hover"
                                :content=item.content
                            >
                                <template #reference>
                                    <el-card>
                                        <div style="font-size: 16px; font-weight: inherit">{{ item.title }}</div>
                                        <br/>
                                        <p>{{ item.createUser }} 发布于 {{ item.updateTime }}</p>
                                    </el-card>
                                </template>
                            </el-popover>
                        </el-timeline-item>
                    </el-timeline>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";
import store from "@/store";
import requestUtil from "@/util/request";

const currentUser = ref(store.getters.GET_USERINFO)

const notices = ref([]);

const initNotices = async () => {
    const res = await requestUtil.get("notice/notice/selectAll");
    notices.value = res.data.data
}

initNotices();
</script>

<style lang="scss" scoped>

</style>
