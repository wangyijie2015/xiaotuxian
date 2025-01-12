package com.cc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.domain.parm.ComplaintParm;
import com.cc.domain.pojo.Complaint;
import com.cc.mapper.ComplaintMapper;
import com.cc.service.ComplaintService;
import com.tencentcloudapi.dlc.v20210125.models.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.*;
import java.util.*;
import java.sql.Date;

/**
 * 投诉serviceImpl层
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {

    @Autowired
    private ComplaintService complaintService;

//    分页查询
    @Override
    public IPage<Complaint> getComplaintList(ComplaintParm complaintParm) {
        //构造分页条件
        IPage<Complaint> page = new Page<>(complaintParm.getCurrentPage(), complaintParm.getPageSize());
        return baseMapper.selectPage(page,buildQueryWrapper(complaintParm));
//        IPage<Complaint> page = new Page<>();
//        page.setCurrent(complaintParm.getCurrentPage());
//        page.setSize(complaintParm.getPageSize());
//        return baseMapper.getList(page, complaintParm.getComplaintTitle());
    }

    //添加
    // 需要对异常记录进行捕获
    @Override
    public boolean saveComplaint(Complaint complaint) {
        try{
            //设置投诉状态 0->未处理
            complaint.setSloveStatus("0");
            //设置投诉时间
            complaint.setComplaintTime(new java.util.Date());
            //保存
            int count = baseMapper.insert(complaint);
            return count > 0;
        } catch (Exception e){
            log.error("保存投诉失败",e);
            return false;
        }
    }

    //编辑
    @Override
    public boolean updateComplaint(Complaint complaint) {
        //判断该投诉是否已被处理
        if (complaint.getSloveStatus().equals("1")) {
            return false;
        }
        int count = baseMapper.updateById(complaint);
        return count > 0;
    }

    //删除
    @Override
    public boolean removeComplaint(Integer complaintId) {
        //构造查询条件
        QueryWrapper<Complaint> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Complaint::getComplaintId, complaintId)
                .eq(Complaint::getSloveStatus, "0");
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            //投诉没被处理，才可以删除
            baseMapper.deleteById(complaintId);
            return true;
        }
        return false;
    }

    //我的投诉
    // 列表分页查询
    @Override
    public IPage<Complaint> getMyComplaintList(ComplaintParm complaintParm) {
        //构造查询条件
        IPage<Complaint> page = new Page<>(complaintParm.getCurrentPage(), complaintParm.getPageSize());
        return baseMapper.selectPage(page,buildQueryWrapper(complaintParm));
    }

    //处理投诉
    @Override
    public boolean sloveComplaint(Complaint complaint) {
        //设置投诉状态->已处理
        complaint.setSloveStatus("1");
        int count = baseMapper.updateById(complaint);
        return count > 0;
    }

    //投诉折线图
    public Map<String, Object> listforMonths() {
        Map<String, Object> resMap = new HashMap<>();
        List<String> complaintMonths = new ArrayList<>();
        List<Integer> complaintCount = new ArrayList<>();

        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            // 日期获取
            LocalDate date = now.minusMonths(i);
            String month = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            complaintMonths.add(month);

            // 获取月份的第一天和最后一天
            YearMonth yearMonth = YearMonth.from(date);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            // 确保结束日期是有效的，例如，避免 '2024-09-31'
            if (endDate.isAfter(now)) {
                endDate = now;
            }

            Integer comCount = baseMapper.findComCountByMonths(
                    Date.valueOf(startDate), Date.valueOf(endDate));
            // 投诉总数
            complaintCount.add(comCount);
        }
        resMap.put("complaintMonths", complaintMonths);
        resMap.put("complaintCount", complaintCount);
        return resMap;
    }


    private QueryWrapper<Complaint> buildQueryWrapper(ComplaintParm complaintParm){
        QueryWrapper<Complaint> wrapper = new QueryWrapper<>();
        if(complaintParm.getComplaintTitle() != null){
            wrapper.lambda().like(Complaint::getComplaintTitle,complaintParm.getComplaintTitle());
        }
        if(complaintParm.getLiverId() !=null){
            wrapper.lambda().like(Complaint::getLiverId,complaintParm.getLiverId());
        }
        return wrapper;
    }

}
