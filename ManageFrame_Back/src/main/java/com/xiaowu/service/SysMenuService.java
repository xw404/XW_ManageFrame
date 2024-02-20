package com.xiaowu.service;

import com.xiaowu.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86152
* @description 针对表【sys_menu】的数据库操作Service
* @createDate 2023-12-29 17:53:50
*/
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> buildTreeMenu(List<SysMenu> sysMenuList);

}
