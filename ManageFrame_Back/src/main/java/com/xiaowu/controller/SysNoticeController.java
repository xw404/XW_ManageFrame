package com.xiaowu.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowu.common.Result;
import com.xiaowu.entity.SysNotice;
import com.xiaowu.entity.bean.PageBean;
import com.xiaowu.service.SysNoticeService;
import com.xiaowu.util.StringUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * (SysNotice)表控制层
 *
 * @author makejava
 * @since 2024-01-02 20:49:28
 */
@RestController
@RequestMapping("notice/notice")
public class SysNoticeController extends ApiController {

    @Resource
    private SysNoticeService sysNoticeService;

    /**
     * 查询展示的公告
     */
    @GetMapping("/selectAll")
    //@PreAuthorize("hasRole('ROLE_common')")
    public Result selectAll() {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysNotice::getUpdateTime);
        List<SysNotice> list = sysNoticeService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 添加或者修改
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('notice:notice:add')" + "||" + "hasAuthority('notice:notice :edit')")
    public Result save(@RequestBody SysNotice notice) {
        if (notice.getId() == null || notice.getId() == -1) {
            //添加
            notice.setUpdateTime(new Date());
            sysNoticeService.save(notice);
        } else {
            notice.setUpdateTime(new Date());
            sysNoticeService.updateById(notice);
        }
        return Result.success();
    }

    /**
     * 根据条件分页查询用户信息
     *
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('notice:notice:query')")
    public Result list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();
        Page<SysNotice> pageResult = sysNoticeService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()),
                new QueryWrapper<SysNotice>().like(StringUtil.isNotEmpty(query), "title", query));
        List<SysNotice> noticeList = pageResult.getRecords();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("noticeList", noticeList);
        resultMap.put("total", pageResult.getTotal());
        return Result.success(resultMap);
    }


    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('notice:notice:query')")
    public Result findById(@PathVariable(value = "id") Integer id) {
        SysNotice notice = sysNoticeService.getById(id);
        return Result.success(notice);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('notice:notice:delete')")
    public Result delete(@RequestBody Long[] ids) {
        sysNoticeService.removeByIds(Arrays.asList(ids));
        return Result.success();
    }
}

