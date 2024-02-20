package com.xiaowu.service;

import com.xiaowu.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86152
* @description 针对表【sys_user】的数据库操作Service
* @createDate 2023-12-28 22:21:04
*/
public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    /**
     * 根据用户ID返回权限集合
     * @param userId
     * @return
     */
    String getUserAuthorityInfo(Long userId);
}
