package com.xiaowu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowu.common.Result;
import com.xiaowu.entity.SysRole;
import com.xiaowu.entity.SysRoleMenu;
import com.xiaowu.entity.SysUserRole;
import com.xiaowu.entity.bean.PageBean;
import com.xiaowu.service.SysRoleMenuService;
import com.xiaowu.service.SysRoleService;
import com.xiaowu.service.SysUserRoleService;
import com.xiaowu.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author 吴策
 * @Date 2024/01/01 23:13
 * @Description
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @GetMapping("/listAll")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result listAll() {
        List<SysRole> roleList = sysRoleService.list();
        return Result.success(roleList);
    }

    /**
     * 根据条件分页查询角色信息
     *
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();
        Page<SysRole> pageResult = sysRoleService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize())
                , new QueryWrapper<SysRole>().like(StringUtil.isNotEmpty(query), "name", query));
        List<SysRole> userList = pageResult.getRecords();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("roleList", userList);
        resultMap.put("total", pageResult.getTotal());
        return Result.success(resultMap);
    }

    /**
     * 添加或者修改
     *
     * @param sysRole
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:role:add')" + "||" + "hasAuthority('system:role:edit')")
    public Result save(@RequestBody SysRole sysRole) {
        if (sysRole.getId() == null || sysRole.getId() == -1) {
            sysRole.setCreateTime(new Date());
            sysRoleService.save(sysRole);
        } else {
            sysRole.setUpdateTime(new Date());
            sysRoleService.updateById(sysRole);
        }
        return Result.success();
    }
    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result findById(@PathVariable(value = "id")Integer id){
        SysRole sysRole = sysRoleService.getById(id);
        return Result.success(sysRole);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result delete(@RequestBody Long[] ids){
        sysRoleService.removeByIds(Arrays.asList(ids));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id",ids));
        return Result.success();
    }

    /**
     * 获取当前角色的权限菜单ID集合
     * @param id
     * @return
     */
    @GetMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public Result menus(@PathVariable(value = "id")Integer id){
        List<SysRoleMenu> roleMenuList = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<Long> menuIdList = roleMenuList.stream().map(p -> p.getMenuId()).collect(Collectors.toList());
        return Result.success(menuIdList);
    }


    /**
     * 更新角色权限信息
     * @param id
     * @param menuIds
     * @return
     */
    @Transactional
    @PostMapping("/updateMenus/{id}")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public Result updateMenus(@PathVariable(value = "id")Long id,@RequestBody Long[] menuIds){
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<SysRoleMenu> sysRoleMenuList=new ArrayList<>();
        //批量更新
        Arrays.stream(menuIds).forEach(menuId->{
            SysRoleMenu roleMenu=new SysRoleMenu();
            roleMenu.setRoleId(id);
            roleMenu.setMenuId(menuId);
            sysRoleMenuList.add(roleMenu);
        });
        sysRoleMenuService.saveBatch(sysRoleMenuList);
        return Result.success();
    }
}
