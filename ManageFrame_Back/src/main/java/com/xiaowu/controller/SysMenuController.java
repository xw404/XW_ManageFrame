package com.xiaowu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaowu.common.Result;
import com.xiaowu.entity.SysMenu;
import com.xiaowu.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Author 吴策
 * @Date 2024/01/02 10:49
 * @Description
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 查询所有菜单树信息
     *
     * @return
     */
    @RequestMapping("/treeList")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result list() {
        //根据排序字段查询集合
        List<SysMenu> menuList = sysMenuService.list(new QueryWrapper<SysMenu>().orderByAsc("order_num"));
        List<SysMenu> treeMenu = sysMenuService.buildTreeMenu(menuList);
        return Result.success(treeMenu);
    }

    /**
     * 添加或者修改
     *
     * @param sysMenu
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:menu:add')" + "||" + "hasAuthority('system:menu:edit')")
    public Result save(@RequestBody SysMenu sysMenu) {
        if (sysMenu.getId() == null || sysMenu.getId() == -1) {
            sysMenu.setCreateTime(new Date());
            sysMenuService.save(sysMenu);
        } else {
            sysMenu.setUpdateTime(new Date());
            sysMenuService.updateById(sysMenu);
        }
        return Result.success();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result findById(@PathVariable(value = "id")Long id){
        SysMenu sysMenu = sysMenuService.getById(id);
        return Result.success(sysMenu);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result delete(@PathVariable(value = "id")Long id){
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if(count>0){
            return Result.error("500","请先删除子菜单！");
        }
        sysMenuService.removeById(id);
        return Result.success();
    }

}