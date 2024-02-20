package com.xiaowu.entity.bean;

import lombok.Data;

@Data
public class PageBean {
    private int pageNum; // 第几页
    private int pageSize; // 每页记录数MybatisPlus开启分页功能：
    private int start; // 起始页
    private String query; // 查询参数
}