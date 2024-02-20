package com.xiaowu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowu.entity.SysMenu;
import com.xiaowu.service.SysMenuService;
import com.xiaowu.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 86152
* @description 针对表【sys_menu】的数据库操作Service实现
* @createDate 2023-12-29 17:53:50
*/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{

    /**
     * 构建树形菜单目录结构
     * @param sysMenuList
     * @return
     */
    @Override
    public List<SysMenu> buildTreeMenu(List<SysMenu> sysMenuList) {
        List<SysMenu> menuList = new ArrayList<>();
        for(SysMenu sysMenu:sysMenuList){
            // 寻找子节点:遍历所有菜单，找到当前父节点是当前节点的菜单，添加为当前节点的孩子
            for(SysMenu item:sysMenuList){
                if(item.getParentId() == sysMenu.getId()){
                    sysMenu.getChildren().add(item);
                }
            }
            // 判断父节点，添加到集合
            if(sysMenu.getParentId()==0L){
                menuList.add(sysMenu);
            }
        }
        return menuList;
    }
}




