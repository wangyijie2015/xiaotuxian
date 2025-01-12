package com.cc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.domain.parm.FacilitiesParm;
import com.cc.domain.pojo.Facilities;
import com.cc.mapper.FacilitiesMapper;
import com.cc.service.FacilitiesService;
import org.springframework.stereotype.Service;

@Service
public class FacilitiesServiceImpl extends ServiceImpl<FacilitiesMapper, Facilities> implements FacilitiesService {

    //添加设施
    @Override
    public boolean saveFacilities(Facilities facilities) {
        return baseMapper.insert(facilities) >0;
    }

    //分页查询
    @Override
    public IPage<Facilities> getList(FacilitiesParm facilitiesParm) {
        if(facilitiesParm == null){
            // 如果参数为null，可以选择抛出异常或返回一个空的分页结果
            throw new IllegalArgumentException("facilitiesParm cannot be null");
        }

        //构造查询条件
        QueryWrapper<Facilities> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(facilitiesParm.getFaName())){
            wrapper.lambda().like(Facilities::getFaName,facilitiesParm.getFaName());
        }
        if(StringUtils.isNotEmpty(facilitiesParm.getFaPerson())){
            wrapper.lambda().like(Facilities::getFaPerson,facilitiesParm.getFaPerson());
        }
        if(facilitiesParm.getPhoneNumber() != null){
            wrapper.lambda().like(Facilities::getPhoneNumber,facilitiesParm.getPhoneNumber());
        }
        //构造分页对象
        // 获取分页参数并进行类型转换
        int current = Math.toIntExact(facilitiesParm.getCurrentPage());
        int size = Math.toIntExact(facilitiesParm.getPageSize());
        // 构造分页对象
        IPage<Facilities> page = new Page<>(current, size);
        return baseMapper.selectPage(page, wrapper);
    }
}
