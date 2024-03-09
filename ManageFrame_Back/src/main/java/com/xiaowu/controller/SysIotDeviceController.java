package com.xiaowu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowu.common.Result;
import com.xiaowu.entity.SysIotDevice;
import com.xiaowu.entity.SysIotDeviceData;
import com.xiaowu.entity.SysIotDeviceLinedata;
import com.xiaowu.entity.SysUser;
import com.xiaowu.entity.bean.PageBean;
import com.xiaowu.service.SysIotDeviceDataService;
import com.xiaowu.service.SysIotDeviceLinedataService;
import com.xiaowu.service.SysIotDeviceService;
import com.xiaowu.service.SysUserService;
import com.xiaowu.util.StringUtil;
import com.xiaowu.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * @Author 吴策
 * @Date 2024/01/29 21:43
 * @Description
 */

@RestController
@RequestMapping("/data/device")
public class SysIotDeviceController {
    @Resource
    private SysIotDeviceService sysIotDeviceService;

    @Resource
    private SysIotDeviceDataService sysIotDeviceDataService;

    @Resource
    private SysIotDeviceLinedataService sysIotDeviceLinedataService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 查询设备总体数据(按最近时间取8条数据)
     */
    @GetMapping("/data/{deviceName}")
    @PreAuthorize("hasAuthority('data:data:query')")
    public Result queryDeviceDataByDeviceName(@PathVariable(value = "deviceName") String deviceName) throws ParseException {
        if(deviceName==null ||deviceName.isEmpty()){
            return Result.error("500","请输设备名称查询");
        }
        LambdaQueryWrapper<SysIotDeviceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysIotDeviceData::getDeviceName,deviceName);
        wrapper.orderByDesc(SysIotDeviceData::getId);
        wrapper.last("LIMIT 10");
        List<SysIotDeviceData> list = sysIotDeviceDataService.list(wrapper);
        for (SysIotDeviceData sysIotDeviceData : list) {
            String timeNum = sysIotDeviceData.getTime();
            String time = TimeUtils.yyyyMMddHHmmssToTimeString(timeNum);
            sysIotDeviceData.setTime(time);
        }
        return Result.success(list);
    }

    /**
     * 查询设备信息
     */
    @GetMapping("/selectAll")
    @PreAuthorize("hasAuthority('data:device:query')")
    public Result selectAll() {
        List<SysIotDevice> list = sysIotDeviceService.list();
        return Result.success(list);
    }

    /**
     * 添加或者修改
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('data:device:add')" + "||" + "hasAuthority('data:device:edit')")
    public Result save(@RequestBody SysIotDevice sysIotDevice) {
        if (sysIotDevice.getId() == null || sysIotDevice.getId() == -1) {
            //添加
            sysIotDevice.setCreateTime(new Date());
            sysIotDeviceService.save(sysIotDevice);
        } else {
            sysIotDevice.setUpdateTime(new Date());
            sysIotDeviceService.updateById(sysIotDevice);
        }
        return Result.success();
    }


    /**
     * 根据条件分页查询
     *
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('data:device:query')")
    public Result list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();
        Page<SysIotDevice> pageResult = sysIotDeviceService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()),
                new QueryWrapper<SysIotDevice>().like(StringUtil.isNotEmpty(query), "deviceName", query));
        List<SysIotDevice> sysIotDeviceList = pageResult.getRecords();
        for (SysIotDevice sysIotDevice : sysIotDeviceList) {
            String userIdString = sysIotDevice.getUserList();//1_2_3_4_5_6_解析
            List<SysUser> sysUserList = new ArrayList<>();
            // 使用split方法按"_"分割字符串
            if(!StringUtil.isEmpty(userIdString)){
                String[] parts = userIdString.split("_");
                // 遍历每个部分，并将其转换为整数，然后添加到列表中
                for (String part : parts) {
                    SysUser sysUser = sysUserService.getById(Integer.parseInt(part));
                    sysUserList.add(sysUser);
                }
            }
            sysIotDevice.setSysUserList(sysUserList);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sysIotDeviceList", sysIotDeviceList);
        resultMap.put("total", pageResult.getTotal());
        return Result.success(resultMap);
    }


    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('data:device:query')")
    public Result findById(@PathVariable(value = "id") Integer id) {
        SysIotDevice sysIotDevice = sysIotDeviceService.getById(id);
        return Result.success(sysIotDevice);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('data:device:delete')")
    public Result delete(@RequestBody Long[] ids) {
        sysIotDeviceService.removeByIds(Arrays.asList(ids));
        return Result.success();
    }

    /**
     * 查询所有的用户（把设备分配给用户使用）
     */
    @GetMapping("/userListAll")
    @PreAuthorize("hasAuthority('data:device:edit')")
    public Result userListAll() {
        List<SysUser> userList = sysUserService.list();
        return Result.success(userList);
    }

    /**
     * 分配用户(用户设备授权)
     */
    @PostMapping("/grantUser/{id}")
    @PreAuthorize("hasAuthority('data:device:edit')")
    public Result grantUser(@PathVariable("id") Long id, @RequestBody Long[] userIds){
        StringBuilder userIdStrings = new StringBuilder();
        for (Long userId : userIds) {
            userIdStrings.append(userId);
            userIdStrings.append("_");
        }
        SysIotDevice sysIotDevice = sysIotDeviceService.getById(id);
        sysIotDevice.setUserList(userIdStrings.toString());
        sysIotDeviceService.updateById(sysIotDevice);
        return Result.success();
    }


    /**
     * 设置所有设备设置检测报警阈值
     */
    @PostMapping("/lineSet")
    @PreAuthorize("hasAuthority('data:data:lineSet')")
    public Result saveLineData(@RequestBody SysIotDeviceLinedata sysIotDeviceLinedata) {
        sysIotDeviceLinedata.setId(1);
        sysIotDeviceLinedataService.updateById(sysIotDeviceLinedata);
        return Result.success();
    }

}
