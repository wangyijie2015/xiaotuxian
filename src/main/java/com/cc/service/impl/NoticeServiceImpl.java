package com.cc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.domain.parm.IndexNoticeParm;
import com.cc.domain.parm.NoticeParm;
import com.cc.domain.pojo.Notice;
import com.cc.mapper.NoticeMapper;
import com.cc.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 公告service实现层
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper,Notice> implements NoticeService {

    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    //分页查询
    @Override
    public IPage<Notice> getList(NoticeParm noticeParm) {
        if(noticeParm == null){
            throw new IllegalArgumentException("noticeParm cannot be null");
        }

        IPage<Notice> page = new Page<>(noticeParm.getCurrentPage(), noticeParm.getPageSize());
        return baseMapper.getList(page, noticeParm.getFullName(), noticeParm.getNoticeTitle());
    }

    //添加
    @Override
    public boolean saveNotice(Notice notice) {
        if(notice == null){
            throw new IllegalArgumentException("添加公告不能为空");
        }
        //设置当前时间
        notice.setNoticeTime(new Date());
        int count = baseMapper.insert(notice);
        return count > 0;
    }

    //首页公告分页查询
    @Override
    public IPage<Notice> getIndexNoticeList(IndexNoticeParm indexNoticeParm) {
        //构造分页对象
        IPage<Notice> page = new Page<>();
        page.setCurrent(indexNoticeParm.getCurrentPage());
        page.setSize(indexNoticeParm.getPageSize());
        return baseMapper.getIndexNoticeList(page);
    }
}
