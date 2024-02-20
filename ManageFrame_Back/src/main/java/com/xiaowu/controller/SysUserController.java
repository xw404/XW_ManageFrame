package com.xiaowu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowu.common.Result;
import com.xiaowu.common.enums.Constant;
import com.xiaowu.entity.SysRole;
import com.xiaowu.entity.SysUser;
import com.xiaowu.entity.SysUserRole;
import com.xiaowu.entity.bean.PageBean;
import com.xiaowu.service.SysRoleService;
import com.xiaowu.service.SysUserRoleService;
import com.xiaowu.service.SysUserService;
import com.xiaowu.util.StringUtil;
import com.xiaowu.util.TimeUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

/**
 * @Author 吴策
 * @Date 2024/01/01 12:49
 * @Description
 */

@RequestMapping("sys/user")
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Value("${staticImagesFilePath}")
    private String avatarImagesFilePath;

    /**
     * 添加或者修改
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:user:add')" + "||" + "hasAuthority('system:user :edit')")
    public Result save(@RequestBody SysUser sysUser) {
        if (sysUser.getId() == null || sysUser.getId() == -1) {
            //添加
            sysUser.setCreateTime(new Date());
            sysUser.setPassword(bCryptPasswordEncoder.encode(sysUser.getPassword()));
            sysUserService.save(sysUser);
        } else {
            sysUser.setUpdateTime(new Date());
            sysUserService.updateById(sysUser);
        }
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/updateUserPwd")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result updateUserPwd(@RequestBody SysUser sysUser) {
        SysUser currentUser = sysUserService.getById(sysUser.getId());
        if (bCryptPasswordEncoder.matches(sysUser.getOldPassword(), currentUser.getPassword())) {
            currentUser.setPassword(bCryptPasswordEncoder.encode(sysUser.getNewPassword()));
            currentUser.setUpdateTime(new Date());
            sysUserService.updateById(currentUser);
        } else {
            return Result.error("500", "输入旧密码错误！");
        }
        return Result.success();
    }

    /**
     * 上传用户头像图片
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadImage")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result uploadImage(MultipartFile file) throws Exception {
        Map<String, Object> data = new HashMap<>();
        if (!file.isEmpty()) {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = TimeUtils.getCurrentDateStr() + suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(avatarImagesFilePath + newFileName));
            data.put("title", newFileName);
            data.put("src", "image/userAvatar/" + newFileName);
        }
        return new Result("200", "上传成功", data);
    }

    /**
     * 修改头像
     *
     * @param sysUser
     * @return
     */
    @RequestMapping("/updateAvatar")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result updateAvatar(@RequestBody SysUser sysUser) {
        SysUser currentUser = sysUserService.getById(sysUser.getId());
        currentUser.setUpdateTime(new Date());
        currentUser.setAvatar(sysUser.getAvatar());
        sysUserService.updateById(currentUser);
        return Result.success();
    }

    /**
     * 根据条件分页查询用户信息
     *
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();

        Page<SysUser> pageResult = sysUserService.page(new Page<>(
                        pageBean.getPageNum(),
                        pageBean.getPageSize()),
                new QueryWrapper<SysUser>().like(StringUtil.isNotEmpty(query), "username", query));
        List<SysUser> userList = pageResult.getRecords();
        for (SysUser user : userList) {
            List<SysRole> roleList = sysRoleService.list(new QueryWrapper<SysRole>()
                    .inSql("id", "select role_id from sys_user_role where user_id=" + user.getId()));
            user.setSysRoleList(roleList);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userList", userList);
        resultMap.put("total", pageResult.getTotal());
        return Result.success(resultMap);
    }


    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result findById(@PathVariable(value = "id") Integer id) {
        SysUser sysUser = sysUserService.getById(id);
        return Result.success(sysUser);
    }

    /**
     * 验证用户名
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/checkUserName")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result checkUserName(@RequestBody SysUser sysUser) {
        if (sysUserService.getByUsername(sysUser.getUsername()) == null) {
            return Result.success();
        } else {
            return Result.error();
        }
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result delete(@RequestBody Long[] ids) {
        sysUserService.removeByIds(Arrays.asList(ids));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", ids));
        return Result.success();
    }


    /**
     * 重置密码
     *
     * @param id
     * @return
     */
    @GetMapping("/resetPassword/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result resetPassword(@PathVariable(value = "id") Integer id) {
        SysUser sysUser = sysUserService.getById(id);
        sysUser.setPassword(bCryptPasswordEncoder.encode(Constant.DEFAULT_PASSWORD));
        sysUser.setUpdateTime(new Date());
        sysUserService.updateById(sysUser);
        return Result.success();
    }

    /**
     * 更新状态
     */
    @GetMapping("/updateStatus/{id}/status/{status}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result updateStatus(@PathVariable(value = "id")Integer id,@PathVariable(value = "status")String status){
        SysUser sysUser = sysUserService.getById(id);
        sysUser.setStatus(status);
        sysUserService.saveOrUpdate(sysUser);
        return Result.success();
    }

    /**
     * 角色授权
     * @param userId
     * @param roleIds
     * @return
     */
    @Transactional
    @PostMapping("/grantRole/{userId}")
    @PreAuthorize("hasAuthority('system:user:role')")
    public Result grantRole(@PathVariable("userId") Long userId, @RequestBody Long[] roleIds){
        List<SysUserRole> userRoleList=new ArrayList<>();
        Arrays.stream(roleIds).forEach(r -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);
            userRoleList.add(sysUserRole);
        });
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        sysUserRoleService.saveBatch(userRoleList);
        return Result.success();
    }
}
